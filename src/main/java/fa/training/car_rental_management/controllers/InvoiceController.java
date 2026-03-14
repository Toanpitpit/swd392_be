package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Invoice;
import fa.training.car_rental_management.services.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<Invoice>> createInvoice(@RequestBody Invoice invoice) {
        try {
            Invoice createdInvoice = invoiceService.createInvoice(invoice);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Invoice created successfully", createdInvoice));
        } catch (Exception e) {
            log.error("Error creating invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create invoice: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Invoice>> getInvoiceById(@PathVariable Integer id) {
        try {
            Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
            return invoice.map(inv -> ResponseEntity.ok(ApiResponse.success("Invoice retrieved", inv)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Invoice not found")));
        } catch (Exception e) {
            log.error("Error retrieving invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving invoice: " + e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<Invoice>> getInvoiceByBookingId(@PathVariable Integer bookingId) {
        try {
            Optional<Invoice> invoice = invoiceService.getInvoiceByBookingId(bookingId);
            return invoice.map(inv -> ResponseEntity.ok(ApiResponse.success("Invoice retrieved", inv)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Invoice not found")));
        } catch (Exception e) {
            log.error("Error retrieving invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving invoice: " + e.getMessage()));
        }
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<ApiResponse<Invoice>> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        try {
            Optional<Invoice> invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
            return invoice.map(inv -> ResponseEntity.ok(ApiResponse.success("Invoice retrieved", inv)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Invoice not found")));
        } catch (Exception e) {
            log.error("Error retrieving invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving invoice: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Invoice>> updateInvoice(@PathVariable Integer id, @RequestBody Invoice invoice) {
        try {
            invoice.setId(id);
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
            return ResponseEntity.ok(ApiResponse.success("Invoice updated successfully", updatedInvoice));
        } catch (Exception e) {
            log.error("Error updating invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update invoice: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable Integer id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok(ApiResponse.success("Invoice deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete invoice: " + e.getMessage()));
        }
    }
}

