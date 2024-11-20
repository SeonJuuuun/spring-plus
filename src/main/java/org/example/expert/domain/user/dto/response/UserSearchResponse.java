package org.example.expert.domain.user.dto.response;

public record UserSearchResponse (
        String nickname
){

    public static UserSearchResponse from(final String nickname) {
        return new UserSearchResponse(nickname);
    }
}
