package com.seatnova.paymentservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatnova.paymentservice.client.BookingClient;
import com.seatnova.paymentservice.dto.*;
import com.seatnova.paymentservice.entity.Payment;
import com.seatnova.paymentservice.entity.PaymentMethod;
import com.seatnova.paymentservice.entity.BookingPaymentStatus;
import com.seatnova.paymentservice.event.dto.PaymentFailedEvent;
import com.seatnova.paymentservice.event.dto.PaymentSucceededEvent;
import com.seatnova.paymentservice.event.dto.RefundCompletedEvent;
import com.seatnova.paymentservice.event.publisher.PaymentEventPublisher;
import com.seatnova.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingClient bookingClient;

    private final PaymentEventPublisher eventPublisher;

    private final ObjectMapper objectMapper;

    /*
     * RazorpayService bean does not exist in mock mode.
     * ObjectProvider allows application startup
     * without Razorpay configuration.
     */
    private final ObjectProvider<RazorpayService>
            razorpayServiceProvider;

    @Value("${payment.gateway.mock-enabled:true}")
    private boolean mockEnabled;


    /*
     * =========================================================
     * CREATE PAYMENT ORDER
     * =========================================================
     */

    @Override
    @Transactional
    public CreateOrderResponse createOrder(
            CreateOrderRequest request) {

        /*
         * Basic request validation.
         */
        if (request == null ||
                request.getBookingId() == null) {

            throw new PaymentException(
                    "Booking ID is required"
            );
        }


        /*
         * IDEMPOTENCY
         *
         * If a payment already exists for the booking,
         * return the existing payment.
         *
         * For the current SeatNova implementation,
         * one Booking has one Payment.
         */
        Payment existingPayment = paymentRepository
                .findByBookingId(
                        request.getBookingId()
                )
                .orElse(null);

        if (existingPayment != null) {

            return mapCreateOrderResponse(
                    existingPayment
            );
        }


        /*
         * =====================================================
         * VALIDATE BOOKING
         * =====================================================
         *
         * Booking Service is the source of truth for:
         *
         * bookingId
         * userId
         * amount
         * bookingStatus
         * paymentStatus
         */

        BookingResponse booking;

        try {

            booking = bookingClient.getBooking(
                    request.getBookingId()
            );

        } catch (Exception ex) {

            throw new PaymentException(
                    "Unable to validate booking",
                    ex
            );
        }


        /*
         * Booking must exist.
         */
        if (booking == null ||
                booking.getId() == null) {

            throw new PaymentException(
                    "Booking does not exist"
            );
        }


        /*
         * Defensive consistency check.
         *
         * Returned booking ID must match
         * requested booking ID.
         */
        if (!request.getBookingId()
                .equals(booking.getId())) {

            throw new PaymentException(
                    "Booking ID mismatch"
            );
        }


        /*
         * Booking must have an owner.
         */
        if (booking.getUserId() == null) {

            throw new PaymentException(
                    "Invalid booking user"
            );
        }


        /*
         * Booking amount must be valid.
         */
        if (booking.getTotalAmount() == null ||
                booking.getTotalAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new PaymentException(
                    "Invalid booking amount"
            );
        }


//        /*
//         * Payment can only be initiated
//         * while seats are being held.
//         */
//        if (booking.getBookingStatus().equals("HOLD")) {
//            throw new PaymentException(
//                    "Booking is not eligible for payment"
//            );
//        }


        /*
         * Payment should only be initiated when
         * Booking Service says payment is pending.
         */
        if (booking.getPaymentStatus()
                != BookingPaymentStatus.PENDING) {
            throw new PaymentException(
                    "Booking payment is not pending"
            );
        }


        /*
         * =====================================================
         * TEMPORARY MOCK PAYMENT MODE
         * =====================================================
         *
         * Current project requirement:
         *
         * Every valid payment request succeeds.
         *
         * Razorpay integration can later replace this flow.
         */

        if (mockEnabled) {

            String mockOrderId =
                    "order_mock_" + UUID.randomUUID();

            String mockPaymentId =
                    "pay_mock_" + UUID.randomUUID();


            Payment payment = Payment.builder()

                    .bookingId(
                            booking.getId()
                    )

                    .userId(
                            booking.getUserId()
                    )

                    .razorpayOrderId(
                            mockOrderId
                    )

                    .razorpayPaymentId(
                            mockPaymentId
                    )

                    .amount(
                            booking.getTotalAmount()
                    )

                    .currency("INR")

                    .status(
                            BookingPaymentStatus.SUCCESS
                    )

                    /*
                     * Mock payment method.
                     *
                     * Real payment method will come
                     * from Razorpay webhook.
                     */
                    .method(
                            PaymentMethod.UPI
                    )

                    .build();


            payment = paymentRepository.save(
                    payment
            );


            /*
             * Notify Booking Service asynchronously
             * through payment-success event.
             */

            eventPublisher.publishPaymentSucceeded(

                    new PaymentSucceededEvent(

                            UUID.randomUUID(),

                            payment.getId(),

                            payment.getBookingId(),

                            payment.getUserId(),

                            payment.getRazorpayOrderId(),

                            payment.getRazorpayPaymentId(),

                            payment.getAmount(),

                            payment.getCurrency(),

                            Instant.now()
                    )
            );


            return mapCreateOrderResponse(
                    payment
            );
        }


        /*
         * =====================================================
         * REAL RAZORPAY MODE
         * =====================================================
         */

        RazorpayService razorpayService =
                getRazorpayService();


        com.razorpay.Order razorpayOrder =
                razorpayService.createOrder(
                        booking.getTotalAmount()
                );


        Payment payment = Payment.builder()

                .bookingId(
                        booking.getId()
                )

                .userId(
                        booking.getUserId()
                )

                .razorpayOrderId(
                        razorpayOrder.get("id")
                )

                .amount(
                        booking.getTotalAmount()
                )

                .currency("INR")

                .status(
                        BookingPaymentStatus.CREATED
                )

                .build();


        payment = paymentRepository.save(
                payment
        );


        return mapCreateOrderResponse(
                payment
        );
    }


    /*
     * =========================================================
     * GET PAYMENT
     * =========================================================
     */

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(
            UUID bookingId) {

        if (bookingId == null) {

            throw new PaymentException(
                    "Booking ID is required"
            );
        }


        Payment payment = paymentRepository
                .findByBookingId(bookingId)

                .orElseThrow(() ->

                        new PaymentNotFoundException(

                                "Payment not found for booking: "
                                        + bookingId
                        )
                );


        return mapPaymentResponse(
                payment
        );
    }


    /*
     * =========================================================
     * REFUND PAYMENT
     * =========================================================
     */

    @Override
    @Transactional
    public RefundResponse refundPayment(
            RefundRequest request) {

        if (request == null ||
                request.getBookingId() == null) {

            throw new PaymentException(
                    "Booking ID is required"
            );
        }


        Payment payment = paymentRepository

                .findByBookingId(
                        request.getBookingId()
                )

                .orElseThrow(() ->

                        new PaymentNotFoundException(

                                "Payment not found for booking: "
                                        + request.getBookingId()
                        )
                );


        /*
         * IDEMPOTENT REFUND
         *
         * Retrying an already completed refund
         * returns the previous result.
         */

        if (payment.getStatus()
                == BookingPaymentStatus.REFUNDED) {

            return mapRefundResponse(
                    payment
            );
        }


        /*
         * Only successful payments
         * can be refunded.
         */

        if (payment.getStatus()
                != BookingPaymentStatus.SUCCESS) {

            throw new InvalidPaymentException(

                    "Only SUCCESS payments can be refunded"
            );
        }


        /*
         * Call Razorpay refund API
         * only in real gateway mode.
         */

        if (!mockEnabled) {

            RazorpayService razorpayService =
                    getRazorpayService();


            razorpayService.refund(

                    payment.getRazorpayPaymentId()
            );
        }


        payment.setStatus(
                BookingPaymentStatus.REFUNDED
        );


        payment = paymentRepository.save(
                payment
        );


        /*
         * Publish refund completed event.
         */

        eventPublisher.publishRefundCompleted(

                new RefundCompletedEvent(

                        UUID.randomUUID(),

                        payment.getId(),

                        payment.getBookingId(),

                        payment.getUserId(),

                        payment.getAmount(),

                        Instant.now()
                )
        );


        return mapRefundResponse(
                payment
        );
    }


    /*
     * =========================================================
     * RAZORPAY WEBHOOK
     * =========================================================
     */

    @Override
    @Transactional
    public void handleWebhook(
            String payload,
            String signature) {

        /*
         * Mock mode doesn't require webhooks.
         */

        if (mockEnabled) {
            return;
        }


        RazorpayService razorpayService =
                getRazorpayService();


        /*
         * Verify Razorpay webhook signature.
         */

        if (!razorpayService.verifyWebhook(
                payload,
                signature)) {

            throw new PaymentException(

                    "Invalid Razorpay webhook signature"
            );
        }


        try {

            JsonNode root =
                    objectMapper.readTree(payload);


            String event =
                    root.path("event")
                            .asText();


            JsonNode paymentEntity =

                    root.path("payload")

                            .path("payment")

                            .path("entity");


            String orderId =

                    paymentEntity

                            .path("order_id")

                            .asText();


            String paymentId =

                    paymentEntity

                            .path("id")

                            .asText();


            String method =

                    paymentEntity

                            .path("method")

                            .asText();


            switch (event) {


                case "payment.captured" ->

                        markPaymentSuccess(

                                orderId,

                                paymentId,

                                method
                        );


                case "payment.failed" -> {


                    String reason =

                            paymentEntity

                                    .path(
                                            "error_description"
                                    )

                                    .asText(
                                            "Payment failed"
                                    );


                    markPaymentFailed(

                            orderId,

                            reason
                    );
                }


                default -> {

                    /*
                     * Ignore Razorpay events
                     * not handled by Payment Service.
                     */

                }
            }


        } catch (PaymentException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new PaymentException(

                    "Unable to process webhook",

                    ex
            );
        }
    }


    /*
     * =========================================================
     * MARK PAYMENT SUCCESS
     * =========================================================
     */

    @Override
    @Transactional
    public void markPaymentSuccess(

            String razorpayOrderId,

            String razorpayPaymentId,

            String method) {


        Payment payment = paymentRepository

                .findByRazorpayOrderId(

                        razorpayOrderId
                )

                .orElseThrow(() ->

                        new PaymentNotFoundException(

                                "Payment not found for Razorpay order: "
                                        + razorpayOrderId
                        )
                );


        /*
         * WEBHOOK IDEMPOTENCY
         */

        if (payment.getStatus()
                == BookingPaymentStatus.SUCCESS) {

            return;
        }


        /*
         * Terminal states must not move
         * back to SUCCESS.
         */

        if (payment.getStatus()
                == BookingPaymentStatus.REFUNDED ||

                payment.getStatus()
                        == BookingPaymentStatus.FAILED) {

            throw new InvalidPaymentException(

                    "Cannot mark payment SUCCESS from state: "
                            + payment.getStatus()
            );
        }


        payment.setRazorpayPaymentId(
                razorpayPaymentId
        );


        payment.setStatus(
                BookingPaymentStatus.SUCCESS
        );


        payment.setMethod(
                parsePaymentMethod(method)
        );


        payment = paymentRepository.save(
                payment
        );


        /*
         * Publish payment success event.
         */

        eventPublisher.publishPaymentSucceeded(

                new PaymentSucceededEvent(

                        UUID.randomUUID(),

                        payment.getId(),

                        payment.getBookingId(),

                        payment.getUserId(),

                        payment.getRazorpayOrderId(),

                        payment.getRazorpayPaymentId(),

                        payment.getAmount(),

                        payment.getCurrency(),

                        Instant.now()
                )
        );
    }


    /*
     * =========================================================
     * MARK PAYMENT FAILED
     * =========================================================
     */

    @Override
    @Transactional
    public void markPaymentFailed(

            String razorpayOrderId,

            String reason) {


        Payment payment = paymentRepository

                .findByRazorpayOrderId(

                        razorpayOrderId
                )

                .orElseThrow(() ->

                        new PaymentNotFoundException(

                                "Payment not found for Razorpay order: "
                                        + razorpayOrderId
                        )
                );


        /*
         * Duplicate failure webhook.
         */

        if (payment.getStatus()
                == BookingPaymentStatus.FAILED) {

            return;
        }


        /*
         * Late failure webhook must not
         * overwrite SUCCESS or REFUNDED.
         */

        if (payment.getStatus()
                == BookingPaymentStatus.SUCCESS ||

                payment.getStatus()
                        == BookingPaymentStatus.REFUNDED) {

            return;
        }


        payment.setStatus(
                BookingPaymentStatus.FAILED
        );


        payment = paymentRepository.save(
                payment
        );


        /*
         * Publish payment failed event.
         */

        eventPublisher.publishPaymentFailed(

                new PaymentFailedEvent(

                        UUID.randomUUID(),

                        payment.getId(),

                        payment.getBookingId(),

                        payment.getUserId(),

                        payment.getRazorpayOrderId(),

                        reason,

                        Instant.now()
                )
        );
    }


    /*
     * =========================================================
     * GET RAZORPAY SERVICE
     * =========================================================
     */

    private RazorpayService getRazorpayService() {

        RazorpayService razorpayService =
                razorpayServiceProvider
                        .getIfAvailable();


        if (razorpayService == null) {

            throw new PaymentException(

                    "Razorpay gateway is not configured"
            );
        }


        return razorpayService;
    }


    /*
     * =========================================================
     * PARSE PAYMENT METHOD
     * =========================================================
     */

    private PaymentMethod parsePaymentMethod(
            String method) {


        if (method == null ||
                method.isBlank()) {

            return null;
        }


        try {

            return PaymentMethod.valueOf(

                    method.toUpperCase(
                            Locale.ROOT
                    )
            );


        } catch (IllegalArgumentException ex) {

            /*
             * Unknown Razorpay payment method.
             *
             * We don't fail payment processing
             * because of an unsupported method.
             */

            return null;
        }
    }


    /*
     * =========================================================
     * RESPONSE MAPPERS
     * =========================================================
     */

    private CreateOrderResponse
    mapCreateOrderResponse(
            Payment payment) {


        return CreateOrderResponse.builder()

                .message(

                        payment.getStatus()
                                == BookingPaymentStatus.SUCCESS

                                ? "Payment successful"

                                : "Payment order created"
                )

                .razorpayOrderId(

                        payment.getRazorpayOrderId()
                )

                .amount(
                        payment.getAmount()
                )

                .currency(
                        payment.getCurrency()
                )

                .build();
    }


    private PaymentResponse
    mapPaymentResponse(
            Payment payment) {


        return PaymentResponse.builder()

                .message(

                        payment.getStatus()
                                == BookingPaymentStatus.SUCCESS

                                ? "Payment successful"

                                : "Payment status: "
                                  + payment.getStatus()
                )

                .bookingId(
                        payment.getBookingId()
                )

                .razorpayOrderId(

                        payment.getRazorpayOrderId()
                )

                .razorpayPaymentId(

                        payment.getRazorpayPaymentId()
                )

                .amount(
                        payment.getAmount()
                )

                .currency(
                        payment.getCurrency()
                )

                .status(
                        payment.getStatus()
                )

                .method(
                        payment.getMethod()
                )

                .build();
    }


    private RefundResponse
    mapRefundResponse(
            Payment payment) {


        return RefundResponse.builder()

                .message(
                        "Refund successful"
                )

                .bookingId(
                        payment.getBookingId()
                )

                .paymentId(
                        payment.getId()
                )

                .status(
                        payment.getStatus()
                )

                .build();
    }
}