package com.example.apiusuario.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieFactory {
    private static final String TOKEN_COOKIE = "token";
    private static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    public ResponseCookie crearCookieSesion(String token) {
        return ResponseCookie.from(TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(ONE_DAY_SECONDS)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie crearCookieLogout() {
        return ResponseCookie.from(TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
