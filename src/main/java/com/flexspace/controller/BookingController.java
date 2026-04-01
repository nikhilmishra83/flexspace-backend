package com.flexspace.controller;

import com.flexspace.common.ApiResponse;
import com.flexspace.dto.BookingRequest;
import com.flexspace.dto.BookingResponse;
import com.flexspace.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final Long DUMMY_USER_ID = 1L;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // 1. Create Booking
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse bookingResponse = bookingService.createBooking(DUMMY_USER_ID, request);
        ApiResponse<BookingResponse> response =
                new ApiResponse<>(true, "Booking created", bookingResponse);
        return ResponseEntity.ok(response);
    }

    // 2. Cancel Booking
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable Long id
    ) {
        bookingService.cancelBooking(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Booking cancelled", null)
        );
    }

    // 3. Get Booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id
    ) {
        BookingResponse response = bookingService.getBookingById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Booking fetched", response)
        );
    }

    // 4. Get Bookings by User
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByUser() {
        List<BookingResponse> bookings =
                bookingService.getBookingsByUser(DUMMY_USER_ID);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Bookings fetched", bookings)
        );
    }

    // 5. (Optional later) Get upcoming bookings
    @GetMapping("/user/upcoming")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUpcomingBookings() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Upcoming bookings", null)
        );
    }

    // 6. (Optional later) Get past bookings
    @GetMapping("/user/past")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getPastBookings() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Past bookings", null)
        );
    }
}

