package de.htw.songservice.controller;

import de.htw.songservice.model.Songlist;
import de.htw.songservice.service.SonglistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/songlists")
@RequiredArgsConstructor
public class SonglistController {
    private final SonglistService songlistService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Songlist>> getSonglists(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                                   @RequestParam("userId") String songlistUserId) {
       return songlistService.getSonglists(token, songlistUserId);
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Songlist> getSonglistsFromId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                                       @PathVariable Integer id) {
        return songlistService.getSonglistsFromId(authToken, id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Songlist> putSonglist(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                @RequestBody Songlist songlistRequest) {
        return songlistService.putSonglist(token, songlistRequest);
    }

    @DeleteMapping(path = "{songlistId}")
    public ResponseEntity<Songlist> deleteSonglist(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable Integer songlistId) {
        return songlistService.deleteSonglist(token, songlistId);
    }

    @PutMapping(path = "{songlistId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Songlist> updateSonglist(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable Integer songlistId,
                                         @RequestBody Songlist newSonglist) {
        return songlistService.updateSonglist(token, songlistId, newSonglist);
    }
}
