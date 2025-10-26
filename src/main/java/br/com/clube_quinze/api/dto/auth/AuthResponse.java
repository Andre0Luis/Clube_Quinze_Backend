package br.com.clube_quinze.api.dto.auth;

public record AuthResponse(String accessToken, String refreshToken, String tokenType) {

    public static AuthResponse bearer(String accessToken, String refreshToken) {
        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }
}
