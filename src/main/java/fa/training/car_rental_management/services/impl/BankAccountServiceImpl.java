package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.BankAccount;
import fa.training.car_rental_management.repository.BankAccountRepository;
import fa.training.car_rental_management.services.BankAccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public BankAccount createBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public Optional<BankAccount> getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public List<BankAccount> getBankAccountsByUserId(Integer userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    @Override
    public BankAccount updateBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public void deleteBankAccount(Integer id) {
        bankAccountRepository.deleteById(id);
    }
}

