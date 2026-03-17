package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.request.BookingRequestDTO;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.entities.Users;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.enums.PaymentStatus;
import fa.training.car_rental_management.enums.PaymentType;
import fa.training.car_rental_management.enums.VehicleStatus;
import fa.training.car_rental_management.exception.ResourceNotFoundException;
import fa.training.car_rental_management.repository.BookingRepository;
import fa.training.car_rental_management.repository.PaymentRepository;
import fa.training.car_rental_management.repository.UserRepository;
import fa.training.car_rental_management.repository.VehicleRepository;
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


    public Booking approveBooking(Integer bookingId,Integer carOnnerId) {
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
            return  booking;

        } catch (Exception e) {
            log.error("Error approving booking: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Reject booking
     * Gửi email thông báo từ chối cho customer
     */
    public Booking rejectBooking(Integer bookingId, String rejectionReason,Integer carOnnerId) {
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

            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(booking);
            sendBookingRejectedEmail(booking, rejectionReason);

            log.info("Booking rejected - ID: {}, Reason: {}", bookingId, rejectionReason);
            return booking;

        } catch (Exception e) {
            log.error("Error rejecting booking: {}", e.getMessage(), e);
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
}

