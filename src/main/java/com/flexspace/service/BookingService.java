package com.flexspace.service;

import com.flexspace.common.exception.BadRequestException;
import com.flexspace.common.exception.ResourceNotFoundException;
import com.flexspace.dto.BookingRequest;
import com.flexspace.dto.BookingResponse;
import com.flexspace.model.Booking;
import com.flexspace.model.BookingDesk;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.flexspace.model.Subscription;
import com.flexspace.repository.BookingDeskRepository;
import com.flexspace.repository.BookingRepository;
import com.flexspace.repository.DeskRepository;
import com.flexspace.repository.DeskUnavailabilityRepository;
import com.flexspace.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final DeskRepository deskRepository;
    private final BookingRepository bookingRepository;
    private final BookingDeskRepository bookingDeskRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DeskUnavailabilityRepository deskUnavailabilityRepository;



    public BookingService(
            DeskRepository deskRepository,
            BookingRepository bookingRepository,
            BookingDeskRepository bookingDeskRepository,
            SubscriptionRepository subscriptionRepository,
            DeskUnavailabilityRepository deskUnavailabilityRepository
    ) {
        this.deskRepository = deskRepository;
        this.bookingRepository = bookingRepository;
        this.bookingDeskRepository = bookingDeskRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.deskUnavailabilityRepository = deskUnavailabilityRepository;
    }

    private void validateDailyLimit(Long userId, BookingRequest request) {
        int usedHours = bookingRepository.getHoursForDate(
                userId,
                request.getStartTime().toLocalDate()
        );
        long requestedHours = (long) Math.ceil(
                Duration.between(request.getStartTime(), request.getEndTime())
                        .toMinutes() / 60.0
        );
        int allowedHours = subscriptionRepository.getTotalDailyHours(
                userId,
                request.getStartTime().toLocalDate()
        );
        if (usedHours + requestedHours > allowedHours) {
            throw new BadRequestException("Daily booking limit exceeded");
        }
    }

    private void validateDesksExist(List<Long> deskIds) {
        for (Long deskId : deskIds) {
            if (deskRepository.findById(deskId).isEmpty()) {
                throw new ResourceNotFoundException("Desk not found: " + deskId);
            }
        }
    }

    private void validateDeskAvailability(BookingRequest request) {
        if (request.getDeskIds() == null || request.getDeskIds().isEmpty()) {
            throw new BadRequestException("At least one desk must be selected");
        }
        validateDesksExist(request.getDeskIds());


        for (Long deskId : request.getDeskIds()) {
            boolean deskUnavailable = deskUnavailabilityRepository.exists(deskId, request.getStartTime(), request.getEndTime());

            if (deskUnavailable) {
                throw new BadRequestException("Desk is unavailable");
            }

            boolean conflict = bookingDeskRepository.existsConflict(
                    deskId,
                    request.getStartTime(),
                    request.getEndTime()
            );

            if (conflict) {
                throw new BadRequestException("Desk already booked");
            }

        }

    }

    private List<Subscription> validateSubscription(Long userId, LocalDate bookingDate) {

        List<Subscription> activeSubs = subscriptionRepository.findActiveSubscriptions(userId);
        // filter valid for booking date
        List<Subscription> validSubs = activeSubs.stream()
                .filter(s ->
                        !bookingDate.isBefore(s.getStartDate()) &&
                                !bookingDate.isAfter(s.getEndDate())
                )
                .toList();

        if (validSubs.isEmpty()) {
            throw new BadRequestException("No active subscription for selected date");
        }

        return validSubs;
    }

    private Booking saveBooking(Long userId, BookingRequest request) {
        Booking booking = new Booking();

        booking.setUserId(userId);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    private void saveBookingDesks(Long bookingId, List<Long> deskIds) {

        for (Long deskId : deskIds) {

            BookingDesk bd = new BookingDesk();

            bd.setBookingId(bookingId);
            bd.setDeskId(deskId);

            bookingDeskRepository.save(bd);

        }

    }

    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot book for past time");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("Invalid time range");
        }

        if (!request.getStartTime().toLocalDate()
                .equals(request.getEndTime().toLocalDate())) {
            throw new BadRequestException("Cross-day booking not allowed");
        }

        validateSubscription(userId, request.getStartTime().toLocalDate());
        validateDailyLimit(userId, request);
        validateDeskAvailability(request);

        Booking booking = saveBooking(userId, request);
        saveBookingDesks(booking.getId(), request.getDeskIds());

        return new BookingResponse(booking);
    }

    public void cancelBooking(Long id) {

        Booking booking = bookingRepository.findById(id);

        if (booking == null) {
            throw new ResourceNotFoundException("Booking not found");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BadRequestException("Booking already cancelled");
        }

        if (!booking.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel past booking");
        }

        bookingRepository.updateStatus(id, "CANCELLED");
    }

    public BookingResponse getBookingById(Long id) {

        Booking booking = bookingRepository.findById(id);

        if (booking == null) {
            throw new ResourceNotFoundException("Booking not found");
        }

        return new BookingResponse(booking);
    }

    public List<BookingResponse> getBookingsByUser(Long userId) {

        List<Booking> bookings = bookingRepository.findByUserId(userId);

        return bookings.stream()
                .map(BookingResponse::new)
                .toList();
    }
}