package com.ecom.craftbid.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.ByteBuffer;

import static javax.crypto.Cipher.SECRET_KEY;

public class TokenParser {
    public static final int SIZEOF_INT = 4;

    static public String getEmailFromToken(String token) {
        try {
            byte[] bytes = ByteBuffer.allocate(SIZEOF_INT).putInt(SECRET_KEY).array();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(bytes))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
