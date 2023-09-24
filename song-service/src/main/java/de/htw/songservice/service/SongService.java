package de.htw.songservice.service;

import de.htw.songservice.dto.SongRequest;
import de.htw.songservice.dto.SongResponse;
import de.htw.songservice.exception.BadRequestException;
import de.htw.songservice.exception.ResourceNotFoundException;
import de.htw.songservice.model.Song;
import de.htw.songservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final WebService webService;

    public ResponseEntity<Iterable<SongResponse>> getSongs(String token) {
        webService.checkToken(token);
        Iterable<SongResponse> songs = songRepository.findAll().stream().map(this::mapToSongResponse).toList();
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    public ResponseEntity<SongResponse> getSong(Integer id, String token) {
        webService.checkToken(token);
        Optional<Song> song = songRepository.findById(id);
        if(song.isPresent()) {
            SongResponse response = mapToSongResponse(song.get());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        else {
            throw new ResourceNotFoundException("Song", "id", id);
        }
    }

    public ResponseEntity<SongResponse> saveSong(SongRequest songRequest, String token) throws URISyntaxException {
        webService.checkToken(token);
        if(songRequest.getTitle() == null || songRequest.getArtist() == null || songRequest.getLabel() == null) {
            throw new BadRequestException("Song needs to have a title, artist, and label.");
        }
        Song songToSave = songBuilder(songRequest);
        Song savedSong = songRepository.save(songToSave);

        URI songURI = new URI(String.format("/rest/songs/songs/%d", savedSong.getId()));
        return ResponseEntity.created(songURI).build();
    }

    public ResponseEntity<SongResponse> updateSong(Integer id, SongRequest songRequest, String token) {
        webService.checkToken(token);
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", id));
        Song songToSave = songBuilder(songRequest);
        if(songRequest.getTitle() != null) songToSave.setTitle(song.getTitle());
        if(songRequest.getArtist() != null) songToSave.setTitle(song.getArtist());
        if(songRequest.getLabel() != null) songToSave.setTitle(song.getLabel());
        songToSave.setId(id);
        songRepository.save(songToSave);
        return ResponseEntity.noContent().build();
    }

    private SongResponse mapToSongResponse(Song song) {
        return SongResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artist(song.getArtist())
                .label(song.getLabel())
                .released(song.getReleased())
                .build();
    }

    private Song songBuilder(SongRequest songRequest) {
        return Song.builder()
                .title(songRequest.getTitle())
                .artist(songRequest.getArtist())
                .label(songRequest.getLabel())
                .released(songRequest.getReleased())
                .build();
    }
}
