package org.example.honorsparkingbe.controller;

import jakarta.servlet.http.HttpSession;
import org.example.honorsparkingbe.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PutMapping("/role")
    public ResponseEntity<String> convertRole(HttpSession session) {
        roleService.convertRole(session);
        return ResponseEntity.ok("role converted successfully");
    }
}
