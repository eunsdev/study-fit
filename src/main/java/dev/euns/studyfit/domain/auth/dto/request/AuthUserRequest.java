package dev.euns.studyfit.domain.auth.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUserRequest {
    private String username;
    private String password;
}
