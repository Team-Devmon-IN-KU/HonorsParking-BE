package org.example.honorsparkingbe.mssql;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MSSQLUserService {
    private final MSSQLUserRepository mssqlUserRepository;

    public MSSQLUserService(MSSQLUserRepository mssqlUserRepository) {
        this.mssqlUserRepository = mssqlUserRepository;
    }

    public Optional<MSSQLUserEntity> getUserByUsername(String username) {
        return mssqlUserRepository.findByUsername(username);
    }
}
