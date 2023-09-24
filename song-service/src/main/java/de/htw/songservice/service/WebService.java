package de.htw.songservice.service;

import de.htw.songservice.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WebService {
    private final WebClient.Builder webClientBuilder;

    public void checkToken(String token) {
        boolean authorized = Boolean.TRUE.equals(webClientBuilder.build().get()
                .uri("http://auth-service/rest/auth")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
        if(!authorized) throw new UnauthorizedException();
    }

    public String getUserIdByToken(String token) {
        return webClientBuilder.build().get()
                .uri("http://auth-service/rest/auth/tokens")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
