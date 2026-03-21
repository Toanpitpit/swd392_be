package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.request.BookingRequestDTO;

import fa.training.car_rental_management.dto.request.ConfirmReturnRequest;
import fa.training.car_rental_management.dto.response.BookingResponse;
import fa.training.car_rental_management.dto.response.WaitingReturnResponse;
import fa.training.car_rental_management.entities.*;
import fa.training.car_rental_management.enums.*;
import fa.training.car_rental_management.repository.*;
import fa.training.car_rental_management.dto.response.BookingResponse;
import fa.training.car_rental_management.entities.Availability;

import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.entities.Users;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.enums.PaymentStatus;
import fa.training.car_rental_management.enums.PaymentType;
import fa.training.car_rental_management.enums.VehicleStatus;
import fa.training.car_rental_management.exception.ResourceNotFoundException;
import fa.training.car_rental_management.repository.AvailabilityRepository;
import fa.training.car_rental_management.repository.BookingRepository;
import fa.training.car_rental_management.repository.PaymentRepository;
import fa.training.car_rental_management.repository.UserRepository;
import fa.training.car_rental_management.repository.VehicleRepository;
import fa.training.car_rental_management.services.BookingService;
import fa.training.car_rental_management.util.BookingValidator;
import fa.training.car_rental_management.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private JwtService jwtService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

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
    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Override
    public Booking updateBooking(Booking booking) {
        log.info("Updating booking with id: {}", booking.getId());
        return bookingRepository.save(booking);
    }
    public BookingResponse approveBooking(Integer bookingId,Integer carOnnerId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

            Users customer = userRepository.findById(booking.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            if(carOnnerId != booking.getVehicle().getOwner().getId()) {
                throw new RuntimeException("You are not the owner of this vehicle");
            }

            booking.setStatus(BookingStatus.AWAITING_PAYMENT);
            bookingRepository.save(booking);

            log.info("Booking approved - ID: {}, Customer: {}", bookingId, customer.getEmail());

            sendBookingSuccessEmails(booking);
            return BookingResponse.builder()
                    .id(booking.getId())
                    .vehicleId(booking.getVehicleId())
                    .customerId(booking.getCustomerId())
                    .startTime(booking.getStartTime().toString())
                    .endTime(booking.getEndTime().toString())
                    .status(booking.getStatus().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error approving booking: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Reject booking
     * Gửi email thông báo từ chối cho customer
     */
    @Transactional
    public BookingResponse rejectBooking(Integer bookingId, String rejectionReason, Integer carOwnerId) {
        try {

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

            Users customer = userRepository.findById(booking.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            if (!carOwnerId.equals(booking.getVehicle().getOwner().getId())) {
                throw new RuntimeException("You are not the owner of this vehicle");
            }

            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(booking);

            List<Availability> availabilities = availabilityRepository.findAllByVehicleIdAndStartDateAndEndDate(
                    booking.getVehicleId(),
                    booking.getStartTime(),
                    booking.getEndTime()
            );

            if (!availabilities.isEmpty()) {
                availabilityRepository.deleteAll(availabilities);
                log.info("Deleted {} availability records for Booking ID: {}", availabilities.size(), bookingId);
            }

            sendBookingRejectedEmail(booking, rejectionReason);

            return BookingResponse.builder()
                    .id(booking.getId())
                    .vehicleId(booking.getVehicleId())
                    .customerId(booking.getCustomerId())
                    .startTime(booking.getStartTime().toString())
                    .endTime(booking.getEndTime().toString())
                    .status(booking.getStatus().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error rejecting booking: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    public void completeBooking(Integer bookingId) {
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

        } catch (Exception e) {
            log.error("Error completing booking: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendBookingRejectedEmail(Booking booking, String reason) {
        try {
            Vehicle vehicle = booking.getVehicle();
            Users customer = booking.getCustomer();

            Map<String, Object> vars = new HashMap<>();
            vars.put("bookingId", booking.getId());
            vars.put("vehicleName", vehicle.getMake() + " " + vehicle.getModel());
            vars.put("receiverName", customer.getName());
            vars.put("startTime", booking.getStartTime().format(DATE_FORMATTER));
            vars.put("endTime", booking.getEndTime().format(DATE_FORMATTER));

            vars.put("rejectReason", (reason != null && !reason.isEmpty()) ? reason : "Không phù hợp với lịch trình hiện tại của xe.");

            vars.put("actionUrl", frontendUrl);

            notificationEmailService.sendEmailWithTemplate(
                    customer,
                    "Booking Rejected - #" + booking.getId(),
                    "booking-rejected",
                    vars
            );

            log.info("Booking rejected email sent to customer: {} for Booking ID: {}", customer.getEmail(), booking.getId());

        } catch (Exception e) {
            log.error("Error sending booking rejected email: {}", e.getMessage(), e);
        }
    }

    public void sendReturnRequestEmailToOwner(Booking booking) {
        try {

            Vehicle vehicle = booking.getVehicle();
            Users owner = vehicle.getOwner();
            Users customer = booking.getCustomer();

            Map<String, Object> vars = new HashMap<>();

            // Variables for template
            vars.put("ownerName", owner.getName());
            vars.put("customerName", customer.getName());
            vars.put("bookingId", booking.getId());
            vars.put("vehicleName", vehicle.getMake() + " " + vehicle.getModel());

            vars.put("startTime", booking.getStartTime().format(DATE_FORMATTER));
            vars.put("endTime", booking.getEndTime().format(DATE_FORMATTER));

            vars.put("actionUrl", frontendUrl + "/owner/bookings/" + booking.getId());

            notificationEmailService.sendEmailWithTemplate(
                    owner,
                    "Vehicle Return Request - Booking #" + booking.getId(),
                    "return_vehicle",
                    vars
            );

            log.info(
                    "Return request email sent to owner: {} for Booking ID: {}",
                    owner.getEmail(),
                    booking.getId()
            );

        } catch (Exception e) {
            log.error("Error sending return request email: {}", e.getMessage(), e);
        }
    }
    public void sendReturnResultEmailToCustomer(
            Booking booking,
            double depositAmount,
            double fine,
            double refundAmount,
            double extraPayment
    ) {
        try {

            Vehicle vehicle = booking.getVehicle();
            Users customer = booking.getCustomer();

            Map<String, Object> vars = new HashMap<>();

            vars.put("customerName", customer.getName());
            vars.put("bookingId", booking.getId());
            vars.put("vehicleName", vehicle.getMake() + " " + vehicle.getModel());

            vars.put("depositAmount", depositAmount);
            vars.put("fineAmount", fine);
            vars.put("refundAmount", refundAmount);
            vars.put("extraPayment", extraPayment);

            vars.put("actionUrl", frontendUrl + "/bookings/" + booking.getId());

            notificationEmailService.sendEmailWithTemplate(
                    customer,
                    "Vehicle Return Result - Booking #" + booking.getId(),
                    "vehicle-return-result",
                    vars
            );

            log.info(
                    "Return result email sent to customer: {} for Booking ID: {}",
                    customer.getEmail(),
                    booking.getId()
            );

        } catch (Exception e) {
            log.error("Error sending return result email: {}", e.getMessage(), e);
        }
    }
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

            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(
                bookingDTO.getStartTime().toLocalDate(),
                bookingDTO.getEndTime().toLocalDate()
            );

            Double dailyRate = vehicle.getBasePrice() != null ? vehicle.getBasePrice() : 0.0;
            Double estimatedAmount = dailyRate * (totalDays + 1);

            String startTime = bookingDTO.getStartTime().format(DATE_FORMATTER);
            String endTime = bookingDTO.getEndTime().format(DATE_FORMATTER);

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

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.UNDER_INSPECTION) {
            throw new RuntimeException("Booking chưa ở trạng thái kiểm tra xe");
        }

        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Inspection inspection = new Inspection();
        inspection.setBookingId(bookingId);
        inspection.setInspectorId(vehicle.getOwnerId());
        inspection.setType(InspectionType.RETURN);
        inspection.setCarStatus(request.getCarStatus());
        inspection.setComments(request.getComments());
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

        if (fine < 0) {
            throw new RuntimeException("Fine không hợp lệ");
        }

        double extraPayment = 0;
        double refundAmount = 0;

        if (fine > depositAmount) {
            extraPayment = fine - depositAmount;
        } else {
            refundAmount = depositAmount - fine;
        }

        // khách trả thêm

        if (extraPayment > 0) {

            Payment finePayment = new Payment();
            finePayment.setBookingId(bookingId);
            finePayment.setPayerId(booking.getCustomerId());
            finePayment.setAmount(extraPayment);
            finePayment.setType(PaymentType.FINE);
            finePayment.setStatus(PaymentStatus.PENDING);

            paymentRepository.save(finePayment);

            booking.setStatus(BookingStatus.AWAITING_PAYMENT);
        }


        // 🟢 CASE 2: hoàn tiền

        else {

            if (refundAmount > 0) {

                Payment refund = new Payment();
                refund.setBookingId(bookingId);
                refund.setPayerId(booking.getCustomerId());
                refund.setAmount(refundAmount);
                refund.setType(PaymentType.REFUND);
                refund.setStatus(PaymentStatus.PENDING);

                paymentRepository.save(refund);
            }

            booking.setStatus(BookingStatus.COMPLETED);
        }

        bookingRepository.save(booking);
        sendReturnResultEmailToCustomer(
                booking,
                depositAmount,
                fine,
                refundAmount,
                extraPayment
        );

        if (booking.getStatus() == BookingStatus.COMPLETED) {

            List<Availability> availabilities = availabilityRepository
                    .findAllByVehicleIdAndStartDateAndEndDate(
                            booking.getVehicleId(),
                            booking.getStartTime(),
                            booking.getEndTime()
                    );

            if (!availabilities.isEmpty()) {
                availabilityRepository.deleteAll(availabilities);
                log.info("Deleted {} availability records for Booking ID: {}", availabilities.size(), bookingId);
            }
        }
    }

    private void sendBookingSuccessEmails(Booking booking) {
        try {
            Vehicle vehicle = booking.getVehicle();
            Users customer = booking.getCustomer();
            Users owner = vehicle.getOwner();

            Payment rentalFare = paymentRepository.findByBookingIdAndType(booking.getId(), PaymentType.RENTAL_FARE)
                    .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment RENTAL_FARE not found or not in PENDING status for booking: " + booking.getId()));

            Payment deposit = paymentRepository.findByBookingIdAndType(booking.getId(), PaymentType.SECURITY_DEPOSIT)
                    .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment SECURITY_DEPOSIT not found or not in PENDING status for booking: " + booking.getId()));
            double totalAmount = rentalFare.getAmount() + deposit.getAmount();

            Map<String, Object> vars = new HashMap<>();
            vars.put("bookingId", booking.getId());
            vars.put("vehicleName", vehicle.getMake() + " " + vehicle.getModel());
            vars.put("startTime", booking.getStartTime().format(DATE_FORMATTER));
            vars.put("endTime", booking.getEndTime().format(DATE_FORMATTER));
            vars.put("totalAmount", totalAmount);
            vars.put("paymentDate", java.time.LocalDateTime.now().format(DATE_FORMATTER));

            vars.put("receiverName", customer.getName());
            vars.put("message", "Thanh toán của bạn đã thành công. Xe đã được giữ chỗ cho chuyến đi của bạn.");
            vars.put("actionUrl", frontendUrl + "/my-bookings/" + booking.getId());

            notificationEmailService.sendEmailWithTemplate(
                    customer,
                    "Payment Successful - Booking #" + booking.getId(),
                    "booking-payment-success",
                    vars
            );

            if (owner != null) {
                vars.put("receiverName", owner.getName());
                vars.put("message", "Khách hàng đã thanh toán thành công cho yêu cầu đặt xe của bạn. Vui lòng chuẩn bị xe để bàn giao.");
                vars.put("actionUrl", frontendUrl + "/owner/bookings/" + booking.getId());

                notificationEmailService.sendEmailWithTemplate(
                        owner,
                        "Booking Confirmed & Paid - " + vehicle.getModel(),
                        "booking-payment-success",
                        vars
                );
            }

            log.info("Booking success emails sent for Booking ID: {}", booking.getId());

        } catch (Exception e) {
            log.error("Error sending booking success emails: {}", e.getMessage(), e);
        }
    }


    public List<BookingResponse> getMyBookings(String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Integer userId = jwtService.extractId(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        List<Object[]> results = bookingRepository.findBookingDetailsByCustomer(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return results.stream().map(r -> BookingResponse.builder()
                .id(((Number) r[0]).intValue())
                .vehicleId(((Number) r[1]).intValue())
                .customerId(((Number) r[2]).intValue())
                .status((String) r[3])
                .startTime(((LocalDateTime) r[4]).format(formatter))
                .endTime(((LocalDateTime) r[5]).format(formatter))

                // 🔥 vehicle
                .vehicleName(r[6] + " " + r[7])

                // 🔥 tiền
                .rentalFare(((Number) r[8]).doubleValue())
                .deposit(((Number) r[9]).doubleValue())
                .fine(((Number) r[10]).doubleValue())

                .build()
        ).toList();
    }
}

