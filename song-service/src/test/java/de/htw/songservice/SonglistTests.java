package de.htw.songservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.songservice.model.Songlist;
import de.htw.songservice.repository.SongRepository;
import de.htw.songservice.repository.SonglistRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SonglistTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    SonglistRepository songlistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String token = "NWA3tET3lrkL_aNPg3VhWro9gSa5sCg5";
    private final String userId = "maxime";

    @Test
    @Transactional
    void putSonglistExpect201andSonglistInDatabase() throws Exception {
        String songlist = """
                {
                "isPrivate": true,
                "name": "MaximesPrivate",
                "songList": [
                        {
                            "id": 6,
                            "title": "Achy Breaky Heart",
                            "artist": "Billy Ray Cyrus",
                            "label": "PolyGram Mercury",
                            "released": 1992
                        },
                        {
                            "id": 7,
                            "title": "What’s Up?",
                            "artist": "4 Non Blondes",
                            "label": "Interscope",
                            "released": 1993
                        }
                    ]
                }""";

        Integer nextId = (int) songlistRepository.count() + 2;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/rest/songlists")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(songlist);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/rest/songlists/%d", nextId)));

        Optional<Songlist> savedSonglist = songlistRepository.findById(nextId);
        assert(savedSonglist.isPresent());
        assert(savedSonglist.get().getId().equals(nextId));
        assert(savedSonglist.get().getUserId().equals(userId));
        assert(savedSonglist.get().getName().equals("MaximesPrivate"));
        assert(savedSonglist.get().isPrivate());
        assert(!savedSonglist.get().getSongs().isEmpty());

        request = MockMvcRequestBuilders.get("/rest/songlists/4")
                .header(HttpHeaders.AUTHORIZATION, token);

        String token2 = "Os8phiVel5bPvbvDkC7UB4Pro11DnLHE";
        MockHttpServletRequestBuilder request2 = MockMvcRequestBuilders.get("/rest/songlists/4")
                .header(HttpHeaders.AUTHORIZATION, token2);

        mockMvc.perform(request).andExpect(status().isOk());

        mockMvc.perform(request2).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void putSonglistWithSongsNotInDatabaseExpect400() throws Exception {
        String songlist = """
                {
                "isPrivate": false,
                "userId": "jane",
                "name": "JanesPublic",
                "songList": [
                        {
                            "id": 6,
                            "title": "Achy Breaky Heart",
                            "artist": "Billy Ray Cyrus",
                            "label": "PolyGram Mercury",
                            "released": 1992
                        },
                        {
                            "id": 7,
                            "title": "What’s Up?",
                            "artist": "4 Non Blondes",
                            "label": "Interscope",
                            "released": 1993
                        }
                    ]
                }""";

        Integer nextId = (int) songlistRepository.count() + 1;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/rest/songlists")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(songlist);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        Optional<Songlist> savedSonglist = songlistRepository.findById(nextId);
        assert(savedSonglist.isEmpty());
    }

    @Test
    @Transactional
    void get1() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/rest/songlists?userId=maxime")
                .header(HttpHeaders.AUTHORIZATION, token);
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    @Transactional
    void deleteSonglistWithSonglistIdNotInDatabaseExpect404() throws Exception {
        Integer nextId = (int) songlistRepository.count() + 1;

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(String.format("/rest/songlists/%d", nextId))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteSonglistExpect200AndSonglistRemovedFromDatabase() throws Exception {
        Integer maximesSonglistId = 1;
        Optional<Songlist> maximesSonglist = songlistRepository.findById(maximesSonglistId);
        assert (maximesSonglist.isPresent());
        assert (maximesSonglist.get().getUserId().equals(userId));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(String.format("/rest/songlists/%s", maximesSonglistId))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(builder)
                .andExpect(status().isOk());

        Optional<Songlist> deletedSonglist = songlistRepository.findById(maximesSonglistId);
        assert (deletedSonglist.isEmpty());
    }

    @Test
    @Transactional
    void deleteSonglistWrongUserExpect403AndSonglistNotDeleted() throws Exception {
        Integer janesSonglistId = 2;
        Optional<Songlist> janesSonglist = songlistRepository.findById(janesSonglistId);
        assert (janesSonglist.isPresent());
        assert (!janesSonglist.get().getUserId().equals(userId));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(String.format("/rest/songlists/%d", janesSonglistId))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(builder)
                .andExpect(status().isForbidden());

        assert (songlistRepository.findById(janesSonglistId).isPresent());
    }

    @Test
    @Transactional
    void updateIdDoesNotExistShouldReturnNotFound() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(String.format("/rest/songlists/%d", 96))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateInvalidIdShouldReturnBadRequest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(String.format("/rest/songlists/%s", "test"))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateSonglistWrongUserExpect403AndSonglistNotUpdated() throws Exception {
        Integer janesSonglistId = 2;
        Optional<Songlist> janesSonglist = songlistRepository.findById(janesSonglistId);
        assert (janesSonglist.isPresent());
        assert (!janesSonglist.get().getUserId().equals(userId));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(String.format("/rest/songlists/%d", janesSonglistId))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(builder)
                .andExpect(status().isForbidden());

        assert (songlistRepository.findById(janesSonglistId).isPresent());
    }

    @Test
    @Transactional
    void updateSongSuccessfullyShouldReturn200AndUpdateSonglist() throws Exception {
        String songlist = """
                {
                "isPrivate": true,
                "name": "MaximesPrivate2",
                "songList": [
                        {
                            "id": 6,
                            "title": "Achy Breaky Heart",
                            "artist": "Billy Ray Cyrus",
                            "label": "PolyGram Mercury",
                            "released": 1992
                        },
                        {
                            "id": 7,
                            "title": "What’s Up?",
                            "artist": "4 Non Blondes",
                            "label": "Interscope",
                            "released": 1993
                        }
                    ]
                }""";

        Integer nextId = (int) songlistRepository.count() + 1;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/rest/songlists")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(songlist);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/rest/songlists/%d", nextId)));

        Optional<Songlist> savedSonglist = songlistRepository.findById(nextId);
        assert(savedSonglist.isPresent());
        assert(savedSonglist.get().getId().equals(nextId));
        assert(savedSonglist.get().getUserId().equals(userId));
        assert(savedSonglist.get().getName().equals("MaximesPrivate2"));
        assert(savedSonglist.get().isPrivate());
        assert(!savedSonglist.get().getSongs().isEmpty());

        String new_songlist = """
                {
                "isPrivate": true,
                "name": "MaximesPrivate3",
                "songList": [
                        {
                            "id": 6,
                            "title": "Achy Breaky Heart",
                            "artist": "Billy Ray Cyrus",
                            "label": "PolyGram Mercury",
                            "released": 1992
                        },
                        {
                            "id": 7,
                            "title": "What’s Up?",
                            "artist": "4 Non Blondes",
                            "label": "Interscope",
                            "released": 1993
                        }
                    ]
                }""";

        MockHttpServletRequestBuilder put_request = MockMvcRequestBuilders.put(String.format("/rest/songlists/%d", nextId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new_songlist);

        mockMvc.perform(put_request)
                .andExpect(status().isOk());

        savedSonglist = songlistRepository.findById(nextId);
        assert(savedSonglist.isPresent());
        assert(savedSonglist.get().getId().equals(nextId));
        assert(savedSonglist.get().getUserId().equals(userId));
        assert(savedSonglist.get().getName().equals("MaximesPrivate3"));
        assert(savedSonglist.get().isPrivate());
        assert(!savedSonglist.get().getSongs().isEmpty());
    }
}
