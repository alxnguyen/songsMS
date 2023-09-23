package de.htw.custommicroservice.controller;

import de.htw.custommicroservice.service.MyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/rest/myservice")
@RequiredArgsConstructor
public class MyController {
    private final MyService myService;

    @GetMapping(path = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLyrics(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                            @PathVariable Integer id) throws IOException {
        return myService.getSong(id, token);
    }
}
