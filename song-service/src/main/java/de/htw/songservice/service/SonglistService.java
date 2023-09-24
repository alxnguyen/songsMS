package de.htw.songservice.service;

import de.htw.songservice.exception.BadRequestException;
import de.htw.songservice.model.Song;
import de.htw.songservice.model.Songlist;
import de.htw.songservice.repository.SongRepository;
import de.htw.songservice.repository.SonglistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SonglistService {
    private final SonglistRepository songlistRepository;
    private final SongRepository songRepository;
    private final WebService webService;

    public ResponseEntity<Iterable<Songlist>> getSonglists(String token, String userId) {
        String tokenUserId = webService.getUserIdByToken(token);

        Iterable<Songlist> songlists;
        if(!userId.equals(tokenUserId)) {
            songlists = songlistRepository.findAllByUserIdAndIsPrivate(userId, false);
        }
        else {
            songlists = songlistRepository.findAllByUserId(userId);
        }
        return ResponseEntity.ok().body(songlists);
    }

    public ResponseEntity<Songlist> getSonglistsFromId(String token, Integer id) {
        Optional<Songlist> songList = songlistRepository.findById(id);
        if(songList.isEmpty()) return ResponseEntity.notFound().build();

        String songListUserId = songList.get().getUserId();
        String userId = webService.getUserIdByToken(token);
        if(!songListUserId.equals(userId)) {
            if(songList.get().isPrivate()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok().body(songList.get());
    }

    public ResponseEntity<Songlist> putSonglist(String token, Songlist songlist) {
        String tokenUserId = webService.getUserIdByToken(token);
        if(songlist.getUserId() != null) {
            if(!songlist.getUserId().equals(tokenUserId)) {
                throw new BadRequestException("Cannot post a song list for another user.");
            }
        }

        List<Song> songs = songlist.getSongs();
        for(Song s : songs) {
            Optional<Song> song = songRepository.findById(s.getId());
            if(song.isEmpty()) {
                throw new BadRequestException("A song in the songlist does not exist.");
            }
            if(!song.get().equals(s)) {
                throw new BadRequestException("Song in songlist does not match song in database with same id.");
            }
        }

        songlist.setUserId(tokenUserId);
        Songlist savedSonglist = songlistRepository.save(songlist);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, String.format("/rest/songs/songlists/%s", savedSonglist.getId()))
                .build();
    }

    public ResponseEntity<Songlist> deleteSonglist(String token, Integer songlistId) {
        if(songlistId <= 0) return ResponseEntity.badRequest().build();
        Optional<Songlist> songlist = songlistRepository.findById(songlistId);
        if(songlist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String tokenUserId = webService.getUserIdByToken(token);
        if(!songlist.get().getUserId().equals(tokenUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        songlistRepository.delete(songlist.get());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Songlist> updateSonglist(String token, Integer songlistId, Songlist newSonglist) {
        String tokenUserId = webService.getUserIdByToken(token);
        if(songlistId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<Songlist> songlist = songlistRepository.findById(songlistId);
        if(songlist.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if(!songlist.get().getUserId().equals(tokenUserId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        newSonglist.setUserId(tokenUserId);
        newSonglist.setId(songlistId);
        songlistRepository.save(newSonglist);

        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, String.format("/rest/songs/songlists/%d", songlistId))
                .build();
    }
}
