package de.htw.auth.service;

import de.htw.auth.dto.UserRequest;
import de.htw.auth.exception.UnauthorizedException;
import de.htw.auth.model.Token;
import de.htw.auth.repository.TokenRepository;
import de.htw.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public String generateToken(String userId, String password) {
        if(!userRepository.existsByUserIdAndPassword(userId, password)) {
            throw new UnauthorizedException();
        }
        String token = getToken();
        tokenRepository.save(new Token(token, userId));
        return token;
    }

    public Boolean checkToken(String token) {
        return tokenRepository.existsById(token);
    }

    public String getUserIdByToken(String tokenString) {
        Token token = tokenRepository.findById(tokenString)
                .orElseThrow(UnauthorizedException::new);
        return token.getUserId();
    }

    public ResponseEntity<String> verifyUser(UserRequest userRequest) {
        String token = generateToken(userRequest.getUserId(), userRequest.getPassword());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(token);
    }

    private String getToken() {
        final SecureRandom secureRandom = new SecureRandom(); //threadsafe
        final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
