package com.apiator.shop;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtGenerator {
    public static Jwt jwtFromSub(String sub){
        return Jwt.withTokenValue("test token")
                .header("alg", "HS256")
                .header("typ", "JWT")
                .subject(sub)
                .build();
    }
}
