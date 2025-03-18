package org.example.honorsparkingbe.mssql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MSSQLUserRepository extends JpaRepository<MSSQLUserEntity, Long> {
    Optional<MSSQLUserEntity> findByUsername(String username);
}
