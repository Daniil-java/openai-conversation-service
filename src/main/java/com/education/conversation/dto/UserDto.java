package com.education.conversation.dto;

import com.education.conversation.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserDto {
    private Long id;
    private String name;

    public static UserDto userToDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setName(user.getName());
    }

}
