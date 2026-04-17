package com.flexspace.controller;

import com.flexspace.common.ApiResponse;
import com.flexspace.dto.BookingRequest;
import com.flexspace.dto.BookingResponse;
import com.flexspace.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // 1. Create Booking
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        BookingResponse bookingResponse =
                bookingService.createBooking(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Booking created", bookingResponse)
        );
    }

    // 2. Cancel Booking
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        bookingService.cancelBooking(id,  userId );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Booking cancelled", null)
        );
    }

    // 3. Get Booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        BookingResponse response = bookingService.getBookingById(id, userId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Booking fetched", response)
        );
    }

    // 4. Get Bookings by User
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByUser(
            @AuthenticationPrincipal Long userId
    ) {

        List<BookingResponse> bookings =
                bookingService.getBookingsByUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Bookings fetched", bookings)
        );
    }

    // 5. (Optional later) Get upcoming bookings
    @GetMapping("/user/upcoming")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUpcomingBookings(
            @AuthenticationPrincipal Long userId
    ) {

        List<BookingResponse> upcomingBookings = bookingService.getUpcomingBookings(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Upcoming bookings", upcomingBookings)
        );
    }

    // 6. (Optional later) Get past bookings
    @GetMapping("/user/past")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getPastBookings(
            @AuthenticationPrincipal Long userId
    ) {
        List<BookingResponse> pastBookings = bookingService.getPastBookings(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Past bookings", pastBookings)
        );
    }
}

