package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.AuthToken;
import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.AuthTokenRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    public AuthToken generateTokenAndSave(UserEntity user) {
        return saveToken(generateToken(user));
    }

    public AuthToken generateToken(UserEntity user) {
        var token = new AuthToken();
        token.setUser(user);
        token.setCreatedDateTime(Instant.now());
        token.setExpiresAtDateTime(Instant.now().plus(10, ChronoUnit.MINUTES));
        token.setConfirmedDateTime(Instant.now());
        token.setToken(UUID.randomUUID().toString());
        return token;
    }
    public AuthToken retrieveToken(UserEntity userEntity){
        return authTokenRepository.findAuthTokenByUser(userEntity);
    }

    public AuthToken saveToken(AuthToken token) {
        return authTokenRepository.save(token);
    }

    public Optional<AuthToken> findByToken(String token) {
        return authTokenRepository.findByToken(token);
    }

    @Transactional
    public void removeToken(UserEntity user) {
        authTokenRepository.deleteAuthTokenByUser(user);
    }
}
