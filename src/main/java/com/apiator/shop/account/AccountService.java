package com.apiator.shop.account;

import com.apiator.shop.exception.ApiException;
import com.apiator.shop.exception.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccountByUUID(UUID uuid){
        Optional<Account> accountOptional = accountRepository.getAccountById(uuid);
        if(accountOptional.isPresent()){
            return accountOptional.get();
        }

        Account newAccount = new Account(uuid);
        accountRepository.save(newAccount);
        return accountRepository.getAccountById(uuid).orElseThrow(
                () -> new InternalException("Could not save new account")
        );
    }

    public void updateRoleByUUID(Jwt currentUserJwt, UUID targetUserUUID, Role role){
        UUID currentUserUUID = Account.getAccountUUIDFromJwt(currentUserJwt);
        Account account = getAccountByUUID(currentUserUUID);
        if(account.getRole() != Role.SUPER_ADMIN){
            throw new ApiException("Insufficient authority", HttpStatus.FORBIDDEN);
        }
        accountRepository.updateRoleById(role, targetUserUUID);
    }

    public void updateAccount(Account account){
        if(accountRepository.getAccountById(account.getId()).isPresent()){
            accountRepository.save(account);
        }
    }
}
