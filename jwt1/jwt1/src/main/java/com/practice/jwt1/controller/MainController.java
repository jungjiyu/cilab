package com.practice.jwt1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/main")
    public ResponseEntity adminP(){
        return ResponseEntity.status(HttpStatus.OK).body("main controller");
    }

}
