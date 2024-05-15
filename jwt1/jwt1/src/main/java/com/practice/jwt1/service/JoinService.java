package com.practice.jwt1.service;

import com.practice.jwt1.dto.JoinDTO;
import com.practice.jwt1.entity.UserEntity;
import com.practice.jwt1.repository.UserRepository;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    // 편의상 필드 주입 했지만 생성자 주입으로 했으면 private 으로 선언도 가능
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinDTO JoinP(JoinDTO dto){
        boolean exist = userRepository.existsByUsername(dto.getUsername());

        if(exist) return null;

        UserEntity entity = new UserEntity();
        entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword())); // 암호화를 한 후 넣어야됨
        entity.setUsername(dto.getUsername());
        entity.setRole("ROLE_ADMIN");
        userRepository.save(entity);
        return JoinDTO.toDTO(entity);

    }

}
