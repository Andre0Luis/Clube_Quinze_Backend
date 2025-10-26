package br.com.clube_quinze.api.service.user;

import br.com.clube_quinze.api.dto.user.UpdateUserRequest;
import br.com.clube_quinze.api.dto.user.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateUserRequest request);
}
