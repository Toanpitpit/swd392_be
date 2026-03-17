package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.request.BookingRequestDTO;
import fa.training.car_rental_management.dto.request.ConfirmReturnRequest;
import fa.training.car_rental_management.dto.response.WaitingReturnResponse;
import fa.training.car_rental_management.entities.*;
import fa.training.car_rental_management.enums.*;
import fa.training.car_rental_management.repository.*;
import fa.training.car_rental_management.services.BookingService;
import fa.training.car_rental_management.util.BookingValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingValidator bookingValidator;

//    @Value("${app.frontend.url}")
    private String frontendUrl="http://localhost:3000" ;

    @Autowired
    private UserNotificationEmailService notificationEmailService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @Override
    public Booking createBooking(BookingRequestDTO booking) {
        try {
            log.info("Creating booking for vehicle: {} by customer: {}", booking.getVehicleId(), booking.getCustomerId());

            Vehicle vehicle = bookingValidator.validateVehicleExists(booking.getVehicleId());

            if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
                throw new RuntimeException("Vehicle not available - Status: " + vehicle.getStatus());
            }

            Users customer = bookingValidator.validateCustomerExists(booking.getCustomerId());

            bookingValidator.validateBookingTimes(booking.getStartTime(), booking.getEndTime());

            bookingValidator.checkBookingConflicts(vehicle.getId(), booking.getStartTime(), booking.getEndTime());

            bookingValidator.checkAndUpdateAvailability(vehicle.getId(), booking.getStartTime(), booking.getEndTime());

            Booking savedBooking = createAndSaveBooking(booking);

            sendNewBookingRequestEmailToOwner(vehicle, customer, booking, savedBooking);
            
            log.info("Booking created successfully with ID: {}", savedBooking.getId());

            return savedBooking;

        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }



    private Booking createAndSaveBooking(BookingRequestDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setCustomerId(bookingDTO.getCustomerId());
        booking.setVehicleId(bookingDTO.getVehicleId());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setStatus(BookingStatus.PENDING_APPROVAL);
        booking.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking saved - ID: {}, Vehicle: {}, Customer: {}", 
                 savedBooking.getId(), savedBooking.getVehicleId(), savedBooking.getCustomerId());


        bookingValidator.createAvailabilityForBooking(
            bookingDTO.getVehicleId(), 
            bookingDTO.getStartTime(), 
            bookingDTO.getEndTime()
        );

        log.info("Availability record created for booking period - Vehicle ID: {}, Period: {} to {}", 
                 bookingDTO.getVehicleId(), bookingDTO.getStartTime(), bookingDTO.getEndTime());

        createPaymentRecords(bookingDTO, savedBooking);
        
        return savedBooking;
    }

    private void createPaymentRecords(BookingRequestDTO bookingDTO, Booking savedBooking) {
        try {
            Vehicle vehicle = vehicleRepository.findById(bookingDTO.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            long totalDays = ChronoUnit.DAYS.between(
                bookingDTO.getStartTime().toLocalDate(),
                bookingDTO.getEndTime().toLocalDate()
            );

            totalDays = totalDays <= 0 ? 1 : totalDays;


            Double basePrice = vehicle.getBasePrice() != null ? vehicle.getBasePrice() : 0.0;
            Double currentPrice = vehicle.getBasePrice() != null ? vehicle.getBasePrice() : basePrice;

            Double securityDepositAmount = currentPrice * 0.2;
            Double rentalFareAmount = basePrice * totalDays;

            createPayment(savedBooking.getId(), savedBooking.getCustomerId(), PaymentType.SECURITY_DEPOSIT, securityDepositAmount);
            createPayment(savedBooking.getId(), savedBooking.getCustomerId(), PaymentType.RENTAL_FARE, rentalFareAmount);

            Double totalPrice = securityDepositAmount + rentalFareAmount;

            bookingRepository.save(savedBooking);
            log.info("Booking updated with total price: ${} and deposit: ${}", totalPrice, securityDepositAmount);

        } catch (Exception e) {
            log.error("Error creating payment records for booking {}: {}", savedBooking.getId(), e.getMessage(), e);
        }
    }
    private void createPayment(Integer bookingId, Integer customerId, PaymentType type, Double amount) {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setPayerId(customerId);
        payment.setType(type);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        log.info("{} payment created - Booking ID: {}, Amount: ${}",
                type, bookingId, amount);
    }

    @Override
    public Optional<Booking> getBookingById(Integer id) {
        try {
            log.info("Fetching booking with id: {}", id);
            return bookingRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByVehicleId(Integer vehicleId) {
        try {
            log.info("Fetching bookings for vehicle: {}", vehicleId);
            return bookingRepository.findByVehicleId(vehicleId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByCustomerId(Integer customerId) {
        try {
            log.info("Fetching bookings for customer: {}", customerId);
            return bookingRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> getBookingsByVehicleIdAndCustomerId(Integer vehicleId, Integer customerId) {
        log.info("Fetching bookings for vehicle: {} and customer: {}", vehicleId, customerId);
        return bookingRepository.findByVehicleIdAndCustomerId(vehicleId, customerId);
    }

    @Override
    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Override
    public Booking updateBooking(Booking booking) {
        log.info("Updating booking with id: {}", booking.getId());
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Integer id) {
        log.info("Deleting booking with id: {}", id);
        bookingRepository.deleteById(id);
    }

    // ==================== Email Notification Methods ====================

    /**
     * Approve booking - admin chấp nhận booking
     * Gửi email xác nhận cho customer
     */
    public void approveBooking(Integer bookingId, String adminMessage, String adminBannerBase64) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

            Users customer = userRepository.findById(booking.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(booking);

            log.info("Booking approved - ID: {}, Customer: {}", bookingId, customer.getEmail());

            String startTime = booking.getStartTime().format(DATE_FORMATTER);
            String endTime = booking.getEndTime().format(DATE_FORMATTER);



        } catch (Exception e) {
            log.error("Error approving booking: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Reject booking - admin từ chối booking
     * Gửi email thông báo từ chối cho customer
     */
    public void rejectBooking(Integer bookingId, String rejectionReason, String adminMessage, String adminBannerBase64) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

            Users customer = userRepository.findById(booking.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(booking);

            log.info("Booking rejected - ID: {}, Reason: {}", bookingId, rejectionReason);


        } catch (Exception e) {
            log.error("Error rejecting booking: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Complete booking - hoàn thành booking (xe được trả)
     * Gửi email thông báo return xe với refund status
     */
    public void completeBooking(Integer bookingId, String refundStatus, String adminMessage, String adminBannerBase64) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

            Users customer = userRepository.findById(booking.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            log.info("Booking completed - ID: {}", bookingId);

            String returnDateTime = LocalDateTime.now().format(DATE_FORMATTER);

            // call thêm để lấy price từ payment
            Double depositAmount = 10.0;



        } catch (Exception e) {
            log.error("Error completing booking: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Send generic custom notification to customer
     */

    public void sendCustomNotification(
            Integer bookingId,
            String subject,
            String title,
            String message,
            String htmlContent,
            String bannerBase64,
            String actionUrl,
            String actionButtonText) {

        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            Users customer = userRepository.findById(booking.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));


            log.info("Custom notification sent to: {}", customer.getEmail());

        } catch (Exception e) {
            log.error("Error sending custom notification: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Send new booking request email to car owner
     */
    private void sendNewBookingRequestEmailToOwner(
            Vehicle vehicle,
            Users customer,
            BookingRequestDTO bookingDTO,
            Booking savedBooking) {
        try {
            // Get vehicle owner
            Users owner = vehicle.getOwner();
            if (owner == null) {
                log.warn("Vehicle {} has no owner assigned", vehicle.getId());
                return;
            }

            // Calculate total days
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(
                bookingDTO.getStartTime().toLocalDate(),
                bookingDTO.getEndTime().toLocalDate()
            );

            // Calculate estimated amount
            Double dailyRate = vehicle.getBasePrice() != null ? vehicle.getBasePrice() : 0.0;
            Double estimatedAmount = dailyRate * (totalDays + 1);

            // Format times
            String startTime = bookingDTO.getStartTime().format(DATE_FORMATTER);
            String endTime = bookingDTO.getEndTime().format(DATE_FORMATTER);

            // Build template variables
            // 1. Khởi tạo Map
            Map<String, Object> vars = new HashMap<>();

            vars.put("bookingId", savedBooking.getId());
            vars.put("vehicleName", vehicle.getModel());
            vars.put("ownerName", owner.getName());
            vars.put("startTime", startTime);
            vars.put("endTime", endTime);
            vars.put("totalDays", (int) totalDays + 1);
            vars.put("estimatedAmount", estimatedAmount);
            vars.put("customerName", customer.getName());
            vars.put("customerEmail", customer.getEmail());

            vars.put("actionUrl", frontendUrl + "/owner/bookings/" + savedBooking.getId());

            notificationEmailService.sendEmailWithTemplate(
                    owner,
                    "New Booking Request - " + vehicle.getModel(),
                    "new-booking-request",
                    vars
            );

            log.info("New booking request email sent to owner: {} for vehicle: {}", owner.getEmail(), vehicle.getId());

        } catch (Exception e) {
            log.error("Error sending new booking request email to owner: {}", e.getMessage(), e);
        }
    }
    @Override
    public List<WaitingReturnResponse> getWaitingReturnConfirm(Integer ownerId) {
        return bookingRepository.findWaitingReturnConfirm(ownerId);
    }

    @Transactional
    public void confirmReturn(Integer bookingId, ConfirmReturnRequest request) {

        // 1️⃣ tìm booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.UNDER_INSPECTION) {
            throw new RuntimeException("Booking chưa ở trạng thái kiểm tra xe");
        }

        // 2️⃣ tìm vehicle
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // 3️⃣ tạo inspection
        Inspection inspection = new Inspection();
        inspection.setBookingId(bookingId);
        inspection.setInspectorId(vehicle.getOwnerId());
        inspection.setType(InspectionType.RETURN);
        inspection.setCarStatus(request.getCarStatus());
        inspection.setDate(LocalDateTime.now());

        inspectionRepository.save(inspection);


        Payment deposit = paymentRepository
                .findByBookingIdAndTypeAndStatus(
                        bookingId,
                        PaymentType.SECURITY_DEPOSIT,
                        PaymentStatus.COMPLETED
                );

        double depositAmount = deposit != null ? deposit.getAmount() : 0;
        double fine = request.getFineAmount() != null ? request.getFineAmount() : 0;

        // 5️⃣ nếu có phí phạt
        if (fine > 0) {

            Payment finePayment = new Payment();
            finePayment.setBookingId(bookingId);
            finePayment.setPayerId(booking.getCustomerId());
            finePayment.setAmount(fine);
            finePayment.setType(PaymentType.FINE);
            finePayment.setStatus(PaymentStatus.COMPLETED);

            paymentRepository.save(finePayment);
        }

        // 6️⃣ tính tiền refund
        double refundAmount = depositAmount - fine;

        if (refundAmount < 0) {
            refundAmount = 0;
        }

        // 7️⃣ tạo refund payment
        if (refundAmount > 0) {

            Payment refund = new Payment();
            refund.setBookingId(bookingId);
            refund.setPayerId(booking.getCustomerId());
            refund.setAmount(refundAmount);
            refund.setType(PaymentType.REFUND);
            refund.setStatus(PaymentStatus.COMPLETED);

            paymentRepository.save(refund);
        }

        // 8️⃣ update booking
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
    }
}

