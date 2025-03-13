package org.example.honorsparkingbe.controller;

import org.example.honorsparkingbe.service.NonMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/non-user")
public class NonMemberController {

    private final NonMemberService nonMemberService;

    public NonMemberController(NonMemberService nonMemberService) {
        this.nonMemberService = nonMemberService;
    }

    @GetMapping("/car")
    public ResponseEntity<?> getCar(@RequestParam String carNumber) {
        return nonMemberService.getParkingTime(carNumber);


    }
}
