package com.practice.jwt1.service;

import com.practice.jwt1.dto.CustomUserDetails;
import com.practice.jwt1.entity.UserEntity;
import com.practice.jwt1.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 로그인 검증 용 . db의 정보와 일치하는가
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsername(username);


        if(entity!=null){
            log.info("userDetialsService: "+entity.getUsername()+","+entity.getPassword());
            return new CustomUserDetails(entity);
        }

        return null;
    }
}
