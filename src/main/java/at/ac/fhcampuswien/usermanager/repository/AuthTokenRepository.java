package at.ac.fhcampuswien.usermanager.repository;

import at.ac.fhcampuswien.usermanager.entity.AuthToken;
import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByToken(String token);
    AuthToken findAuthTokenByUser(UserEntity user);
    void deleteAuthTokenByUser(UserEntity user);
}
