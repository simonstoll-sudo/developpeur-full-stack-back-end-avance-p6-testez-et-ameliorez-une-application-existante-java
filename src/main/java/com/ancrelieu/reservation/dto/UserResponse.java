package com.ancrelieu.reservation.dto;

import com.ancrelieu.reservation.entity.User;

public record UserResponse(Long id, String email, String fullName) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName());
    }
}
