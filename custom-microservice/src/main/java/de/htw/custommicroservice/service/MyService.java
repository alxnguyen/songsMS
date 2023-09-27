package de.htw.custommicroservice.service;

import core.GLA;
import de.htw.custommicroservice.exception.BadRequestException;
import genius.SongSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyService {
    private final WebService webService;

    public ResponseEntity<String> getSong(Integer id, String token) throws IOException {
        webService.checkToken(token);
        JSONObject song = webService.getSongFromDB(id, token);
        String lyrics = fetchLyrics(song.getString("artist"), song.getString("title"));
        return ResponseEntity.ok(lyrics);
    }

    private String fetchLyrics(String artist, String title) throws IOException {
        GLA gla = new GLA();
        SongSearch search = gla.search(artist + " - " + title);
        if(search.getHits().isEmpty()) throw new BadRequestException("Song doesn't exist in the database.");
        SongSearch.Hit hit = search.getHits().get(0);
        return hit.fetchLyrics();
    }
}
