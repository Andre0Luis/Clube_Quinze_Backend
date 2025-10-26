package br.com.clube_quinze.api.service.auth;

import br.com.clube_quinze.api.dto.auth.AuthResponse;
import br.com.clube_quinze.api.dto.auth.LoginRequest;
import br.com.clube_quinze.api.dto.auth.RefreshTokenRequest;
import br.com.clube_quinze.api.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
