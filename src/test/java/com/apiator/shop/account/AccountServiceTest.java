package com.apiator.shop.account;

import com.apiator.shop.JwtGenerator;
import com.apiator.shop.exception.ApiException;
import com.apiator.shop.exception.InternalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountService accountService;
    @Captor
    ArgumentCaptor<Account> accountCaptor;

    @Test
    void getAccountByUUIDWhenAccountIsPresent(){
        Account account = new Account(UUID.fromString("85a2e16b-b294-4177-8527-97431a821cc2"));
        when(accountRepository.getAccountById(account.getId())).thenReturn(Optional.of(account));
        Account returnedAccount = accountService.getAccountByUUID(account.getId());
        assertEquals(account.getId(), returnedAccount.getId());
    }

    @Test
    void createAccountOnGetByUUIDWhenAccountNotPresent(){
        Account account = new Account(UUID.fromString("24381ad8-129c-4a8e-9aaf-ab9b620bdbbf"));
        when(accountRepository.getAccountById(account.getId()))
                .thenReturn(Optional.empty()).thenReturn(Optional.of(account));
        accountService.getAccountByUUID(account.getId());
        verify(accountRepository).save(accountCaptor.capture());
        Account resultAccount = accountCaptor.getValue();
        assertEquals(resultAccount.getId(), account.getId());
    }

    @Test
    void failToCreateAccountForInternalReason(){
        Account account = new Account(UUID.fromString("24381ad8-129c-4a8e-9aaf-ab9b620bdbbf"));
        when(accountRepository.getAccountById(account.getId()))
                .thenReturn(Optional.empty());
        InternalException thrownException = Assertions.assertThrows(InternalException.class, () -> {
            accountService.getAccountByUUID(account.getId());
        });
        assertEquals(thrownException.getMessage(), "Could not save new account");
    }

    @Test
    void updateRoleByUUIDWhenDoneBySuperAdmin(){
        String sub = "5f2d4a0b-e5e6-4b2e-851a-99d6f908e068";
        Account superAdminAccount = new Account(UUID.fromString(sub));
        UUID target = UUID.fromString("16fe234d-cf10-4f21-a89b-cbf5a369bfbf");
        Jwt jwt = JwtGenerator.jwtFromSub(sub);
        superAdminAccount.setRole(Role.SUPER_ADMIN);
        when(accountRepository.getAccountById(superAdminAccount.getId()))
                .thenReturn(Optional.of(superAdminAccount));
        accountService.updateRoleByUUID(jwt, target, Role.ADMIN);

        verify(accountRepository).updateRoleById(Role.ADMIN, target);
    }

    @Test
    void failToUpdateRoleByUUIDWhenDoneSuperAdmin(){
        String sub = "7077b69c-bfae-4a97-b1d2-e9d4f8558572";
        Account superAdminAccount = new Account(UUID.fromString(sub));
        UUID target = UUID.fromString("b33b9845-5297-40b6-978d-cff0b22765df");
        Jwt jwt = JwtGenerator.jwtFromSub(sub);
        superAdminAccount.setRole(Role.ADMIN);
        when(accountRepository.getAccountById(superAdminAccount.getId()))
                .thenReturn(Optional.of(superAdminAccount));

        ApiException thrownException = Assertions.assertThrows(ApiException.class, () -> {
            accountService.updateRoleByUUID(jwt, target, Role.USER);
        });
        assertEquals(thrownException.getMessage(), "Insufficient authority");
    }

}