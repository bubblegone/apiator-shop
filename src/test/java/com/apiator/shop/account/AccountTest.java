package com.apiator.shop.account;

import com.apiator.shop.JwtGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    void accountUUIDFromJwtIsEqualToInitial(){
        String sub = "c3df403e-ad1a-4084-97f8-957960a8f696";
        UUID expectedUUID = UUID.fromString(sub);
        Jwt jwt = JwtGenerator.jwtFromSub(sub);
        UUID result = Account.getAccountUUIDFromJwt(jwt);
        assertEquals(expectedUUID, result);
    }
}