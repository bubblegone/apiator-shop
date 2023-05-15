package com.apiator.shop.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/make-admin")
    public void makeAdminByUUID(@AuthenticationPrincipal Jwt currentUserJwt, @RequestParam UUID target){
        accountService.updateRoleByUUID(currentUserJwt, target, Role.ADMIN);
    }

    @GetMapping("/make-user")
    public void makeUserByUUID(@AuthenticationPrincipal Jwt currentUserJwt, @RequestParam UUID target){
        accountService.updateRoleByUUID(currentUserJwt, target, Role.USER);
    }

    @GetMapping("/make-super-admin")
    public void makeSuperAdminByUUID(@AuthenticationPrincipal Jwt currentUserJwt, @RequestParam UUID target){
        accountService.updateRoleByUUID(currentUserJwt, target, Role.SUPER_ADMIN);
    }
}
