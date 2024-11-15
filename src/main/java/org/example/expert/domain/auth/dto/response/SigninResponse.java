package org.example.expert.domain.auth.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.entity.User;

public record SigninResponse(
        Long id
) {
    public SigninResponse(User user) {
        this(user.getId());
    }
}
