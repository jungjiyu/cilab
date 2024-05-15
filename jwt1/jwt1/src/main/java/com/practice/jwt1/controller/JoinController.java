package com.practice.jwt1.controller;

import com.practice.jwt1.dto.JoinDTO;
import com.practice.jwt1.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {

    @Autowired
    private JoinService joinService;

    @PostMapping("/join")
    public ResponseEntity<JoinDTO> joinP(JoinDTO dto){
        JoinDTO saved = joinService.JoinP(dto);
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }


}
