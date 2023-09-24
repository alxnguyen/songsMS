package de.htw.custommicroservice.service;

import de.htw.custommicroservice.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
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

    public JSONObject getSongFromDB(Integer id, String token) {
        String response = webClientBuilder.build().get()
                .uri("http://song-service/rest/songs/songs/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(response);
    }
}
