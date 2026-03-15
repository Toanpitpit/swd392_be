package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountService {
    BankAccount createBankAccount(BankAccount bankAccount);
    Optional<BankAccount> getBankAccountById(Integer id);
    List<BankAccount> getBankAccountsByUserId(Integer userId);
    BankAccount updateBankAccount(BankAccount bankAccount);
    void deleteBankAccount(Integer id);
}

