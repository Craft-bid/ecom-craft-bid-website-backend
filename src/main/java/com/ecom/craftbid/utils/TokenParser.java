package com.ecom.craftbid.utils;

import com.ecom.craftbid.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class TokenParser {
    public static final int SIZEOF_INT = 4;

    static public String getEmailFromToken(String token, String secretKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("email", String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static public Role getRoleFromToken(String token, String secretKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            return Role.valueOf(role);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
