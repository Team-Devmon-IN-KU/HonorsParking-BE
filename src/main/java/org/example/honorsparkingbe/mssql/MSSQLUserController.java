package org.example.honorsparkingbe.mssql;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/mssql")
public class MSSQLUserController {

    private final MSSQLUserService mssqlUserService;

    public MSSQLUserController(MSSQLUserService mssqlUserService) {
        this.mssqlUserService = mssqlUserService;
    }

    @GetMapping("/user")
    public Optional<MSSQLUserEntity> getUserByUsername(@RequestParam String username) {
        return mssqlUserService.getUserByUsername(username);
    }
}
