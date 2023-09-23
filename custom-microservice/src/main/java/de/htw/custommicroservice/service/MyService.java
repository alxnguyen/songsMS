package de.htw.custommicroservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import core.GLA;
import de.htw.custommicroservice.exception.UnauthorizedException;
import genius.SongSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyService {
    private final WebClient.Builder webClientBuilder;

    public ResponseEntity<String> getSong(Integer id, String token) throws IOException {
        checkToken(token);
        JSONObject song = getSongFromDB(id, token);
        String lyrics = fetchLyrics(song.getString("artist"), song.getString("title"));
        return ResponseEntity.ok(lyrics);
    }

    private String fetchLyrics(String artist, String title) throws IOException {
        GLA gla = new GLA();
        SongSearch search = gla.search(artist + " - " + title);
        SongSearch.Hit hit = search.getHits().get(0);
        return hit.fetchLyrics();
    }

    private void checkToken(String token) {
        boolean authorized = Boolean.TRUE.equals(webClientBuilder.build().get()
                .uri("http://auth-service/rest/auth")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
        if(!authorized) throw new UnauthorizedException();
    }

    private JSONObject getSongFromDB(Integer id, String token) {
        String response = webClientBuilder.build().get()
                .uri("http://song-service/rest/songs/songs/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(response);
    }
}
