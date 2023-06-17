package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private ProfileDTO profile;
    private Role role;

    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getDisplayName())
                .email(user.getEmail())
                .profile(ProfileDTO.fromProfile(user.getProfile()))
                .role(user.getRole())
                .build();
    }

    public static List<UserDTO> fromUsers(List<User> users) {
        return users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }
}
