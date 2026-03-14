package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Invoice;

import java.util.Optional;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    Optional<Invoice> getInvoiceById(Integer id);
    Optional<Invoice> getInvoiceByBookingId(Integer bookingId);
    Optional<Invoice> getInvoiceByInvoiceNumber(String invoiceNumber);
    Invoice updateInvoice(Invoice invoice);
    void deleteInvoice(Integer id);
}

