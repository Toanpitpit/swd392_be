package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.BankAccount;
import fa.training.car_rental_management.services.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/bank-accounts")
@CrossOrigin(origins = "*")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<ApiResponse<BankAccount>> createBankAccount(@RequestBody BankAccount bankAccount) {
        try {
            BankAccount createdAccount = bankAccountService.createBankAccount(bankAccount);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Bank account created successfully", createdAccount));
        } catch (Exception e) {
            log.error("Error creating bank account", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create bank account: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BankAccount>> getBankAccountById(@PathVariable("id") Integer id) {
        try {
            Optional<BankAccount> account = bankAccountService.getBankAccountById(id);
            return account.map(acc -> ResponseEntity.ok(ApiResponse.success("Bank account retrieved", acc)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Bank account not found")));
        } catch (Exception e) {
            log.error("Error retrieving bank account", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving bank account: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BankAccount>>> getBankAccountsByUserId(@PathVariable("userId") Integer userId) {
        try {
            List<BankAccount> accounts = bankAccountService.getBankAccountsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Bank accounts retrieved", accounts));
        } catch (Exception e) {
            log.error("Error retrieving bank accounts", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving bank accounts: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BankAccount>> updateBankAccount(@PathVariable("id") Integer id, @RequestBody BankAccount bankAccount) {
        try {
            bankAccount.setId(id);
            BankAccount updatedAccount = bankAccountService.updateBankAccount(bankAccount);
            return ResponseEntity.ok(ApiResponse.success("Bank account updated successfully", updatedAccount));
        } catch (Exception e) {
            log.error("Error updating bank account", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update bank account: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBankAccount(@PathVariable("id") Integer id) {
        try {
            bankAccountService.deleteBankAccount(id);
            return ResponseEntity.ok(ApiResponse.success("Bank account deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting bank account", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete bank account: " + e.getMessage()));
        }
    }
}

