package de.htw.songservice.controller;

import de.htw.songservice.dto.SongRequest;
import de.htw.songservice.dto.SongResponse;
import de.htw.songservice.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/rest/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SongResponse> saveSong(@RequestBody SongRequest songRequest,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws URISyntaxException {
        return songService.saveSong(songRequest, token);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Iterable<SongResponse>> getSongs(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return songService.getSongs(token);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SongResponse> getSong(@PathVariable Integer id,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return songService.getSong(id, token);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SongResponse> updateSong(@PathVariable Integer id, @RequestBody SongRequest songToUpdate,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return songService.updateSong(id, songToUpdate, token);
    }
}
