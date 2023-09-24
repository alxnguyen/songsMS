package de.htw.songservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.songservice.controller.SongController;
import de.htw.songservice.dto.SongRequest;
import de.htw.songservice.repository.SongRepository;
import de.htw.songservice.service.SongService;
import de.htw.songservice.service.WebService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(locations = "/test.properties")
class SongTests {
	private MockMvc mockMvc;

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private SongService songService;

	@Mock
	private WebService webService;

	private final String token = "NWA3tET3lrkL_aNPg3VhWro9gSa5sCg5";

	@BeforeEach
	private void setupMockMvc() {
		songService = new SongService(songRepository, webService);
		mockMvc = MockMvcBuilders.standaloneSetup(new SongController(songService)).build();
		doNothing().when(webService).checkToken(anyString());
	}

	@Test
	@Transactional
	void getSongByIdShouldReturnStatusOkAndSong() throws Exception {
		mockMvc.perform(get("/rest/songs/songs/1")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("MacArthur Park"))
				.andExpect(jsonPath("$.artist").value("Richard Harris"))
				.andExpect(jsonPath("$.label").value("Dunhill Records"))
				.andExpect(jsonPath("$.released").value(1968));
	}

	@Test
	@Transactional
	void getSongByIdShouldReturnBadRequestForIdNonInteger() throws Exception {
		mockMvc.perform(get("/rest/songs/songs/song"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	void getSongByIdShouldReturn404ForNonExistingId() throws Exception {
		mockMvc.perform(get("/rest/songs/songs/15")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	void getSongByIdWrongAcceptHeaderShouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/rest/songs/songs/1")
				.accept(MediaType.TEXT_HTML)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	@Transactional
	void getSongsWrongAcceptHeaderShouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/rest/songs/songs")
				.accept(MediaType.APPLICATION_XML)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	@Transactional
	void getSongsShouldReturnOkAndAllSongs() throws Exception {
		SongRequest songRequest = getSongRequest();
		mockMvc.perform(get("/rest/songs/songs")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isOk());

		assert(songRepository.findAll().size() == 10);
	}

	@Test
	@Transactional
	void postSongShouldReturn201AndLocationHeader() throws Exception {
		SongRequest songRequest = getSongRequest2();
		String songRequestString = objectMapper.writeValueAsString(songRequest);
		Integer id = 11;
		String expectedUri = format("/rest/songs/songs/%s", id);
		mockMvc.perform(post("/rest/songs/songs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(songRequestString)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", expectedUri));
	}

	@Test
	@Transactional
	void postSongWrongContentTypeShouldReturnBadRequest() throws Exception {
		String songRequestString = getWrongContentType();
		mockMvc.perform(post("/rest/songs/songs")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(songRequestString)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	void postSongWithIncorrectFieldsShouldReturnBadRequest() throws Exception {
		String songToUpdate = incorrectField();

		mockMvc.perform(post("/rest/songs/songs")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(songToUpdate)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	void putSongWithIdShouldSaveSong() throws Exception {
		SongRequest songRequest = getSongRequest2();
		String songRequestString = objectMapper.writeValueAsString(songRequest);

		mockMvc.perform(put("/rest/songs/songs/1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(songRequestString)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isNoContent());
	}

	@Test
	@Transactional
	void putSongShouldReturn404ForNonExistingId() throws Exception {
		SongRequest songRequest = getSongRequest2();
		String songRequestString = objectMapper.writeValueAsString(songRequest);

		mockMvc.perform(put("/rest/songs/songs/15")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(songRequestString)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	void putSongWithNonIntegerPathVariableShouldReturnBadRequest() throws Exception {
		SongRequest songRequest = getSongRequest2();
		String songRequestString = objectMapper.writeValueAsString(songRequest);

		mockMvc.perform(put("/rest/songs/songs/song1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(songRequestString)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	void putSongWithWrongContentTypeShouldReturn415() throws Exception {
		SongRequest songRequest = getSongRequest2();
		String songRequestString = objectMapper.writeValueAsString(songRequest);

		mockMvc.perform(put("/rest/songs/songs/1")
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.content(songRequestString)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	@Transactional
	void putSongWithContentTypeNotMatchingPayloadShouldReturnBadRequest() throws Exception {
		String songToUpdate = "<song>" +
				"<artist>Alison Gold</artist>" +
				"<id>1</id>" +
				"<label>PMW Live</label>" +
				"<released>2013</released>" +
				"<title>Chinese Food</title>" +
				"</song>";

		mockMvc.perform(put("/rest/songs/songs/1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(songToUpdate)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	void deleteSongWithPathVariableIdNotIntegerShouldReturnBadRequest() throws Exception {
		mockMvc.perform(delete("/rest/songs/songs/song1"))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	@Transactional
	void deleteSongWithIdNotInDatabaseShouldReturnNotFound() throws Exception {
		mockMvc.perform(delete("/rest/songs/songs/15")
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	@Transactional
	void deleteSongShouldReturnNoContentAndRemoveSongFromRepository() throws Exception {
		mockMvc.perform(delete("/rest/songs/songs/1")
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isMethodNotAllowed());
	}
	private SongRequest getSongRequest() {
		return SongRequest.builder()
				.title("Title1")
				.artist("Artist1")
				.label("Label1")
				.released(1999)
				.build();
	}

	private SongRequest getSongRequest2() {
		return SongRequest.builder()
				.title("Title2")
				.artist("Artist2")
				.label("Label2")
				.released(2000)
				.build();
	}

	private String getWrongContentType() {
		return "<song>" +
				"<artist>Alison Gold</artist>" +
				"<id>1</id>" +
				"<label>PMW Live</label>" +
				"<released>2013</released>" +
				"<title>Chinese Food</title>" +
				"</song>";
	}

	private String incorrectField() {
		return "{\"id\": \"3\"," +
				"\"a\": \"New Title\"," +
				"\"b\": \"New Artist\"," +
				"\"c\": \"New Label\"," +
				"\"d\": 2023}";
	}
}
