package com.flexspace.service;

import com.flexspace.common.enums.BookingStatus;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class BookingService {

    private final DeskRepository deskRepository;
    private final BookingRepository bookingRepository;
    private final BookingDeskRepository bookingDeskRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DeskUnavailabilityRepository deskUnavailabilityRepository;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);



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
        List<Long> deskIds = request.getDeskIds();
        deskIds.sort(Long::compareTo);

//        we are prefering lock one by one
        for (Long deskId : deskIds) {
            deskRepository.lockDeskById(deskId);
        }
//        deskRepository.lockDeskById(deskIds);


            boolean deskUnavailable = deskUnavailabilityRepository.exists(
                    deskIds,
                    request.getStartTime(),
                    request.getEndTime()
            );

            if (deskUnavailable) {
                log.warn("Desk unavailable for request: {}", request.getDeskIds());
                throw new BadRequestException("Desk is unavailable");
            }

        boolean conflict = bookingDeskRepository.existsConflict(
                deskIds,
                request.getStartTime(),
                request.getEndTime()
        );

        if (conflict) {
            log.warn("Desk conflict detected ");
            throw new BadRequestException("Desk already booked");
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
            log.warn("No active subscription for userId: {}", userId);
            throw new BadRequestException("No active subscription for selected date");
        }

        return validSubs;
    }

    private Booking saveBooking(Long userId, BookingRequest request) {
        Booking booking = new Booking();

        booking.setUserId(userId);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(BookingStatus.CONFIRMED.name());

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
        log.info("Starting booking creation for userId: {}", userId);

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

        log.info("Desk validation successful for userId: {}", userId);

        Booking booking = saveBooking(userId, request);
        saveBookingDesks(booking.getId(), request.getDeskIds());

        log.info("Booking created successfully with id: {},  by user {}", booking.getId(), userId);
        return new BookingResponse(booking);
    }

    public void cancelBooking(Long id, Long userId) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot cancel this booking");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BadRequestException("Booking already cancelled");
        }

        if (!booking.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel past booking");
        }

        bookingRepository.updateStatus(id, "CANCELLED");
        log.info("Booking {} cancelled by user {}", id, userId);
    }

    public BookingResponse getBookingById(Long id,Long  userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized");
        }
        return new BookingResponse(booking);
    }

    public List<BookingResponse> getBookingsByUser(Long userId) {

        List<Booking> bookings = bookingRepository.findByUserId(userId);

        return bookings.stream()
                .map(BookingResponse::new)
                .toList();
    }

    public List<BookingResponse> getUpcomingBookings(Long userId) {

        List<Booking> bookings = bookingRepository.findUpcomingByUserId(userId);

        return bookings.stream()
                .map(BookingResponse::new)
                .toList();
    }

    public List<BookingResponse> getPastBookings(Long userId) {

        List<Booking> bookings = bookingRepository.findPastByUserId(userId);

        return bookings.stream()
                .map(BookingResponse::new)
                .toList();
    }
}