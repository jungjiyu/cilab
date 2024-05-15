package com.practice.jwt1.dto;

import com.practice.jwt1.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Setter
@Getter
public class JoinDTO {

    private String username;
    private String password;

    public static JoinDTO toDTO(UserEntity entity){
        return new JoinDTO(entity.getUsername(),entity.getPassword());
    }

}