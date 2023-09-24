package de.htw.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.auth.controller.AuthController;
import de.htw.auth.dto.UserRequest;
import de.htw.auth.repository.TokenRepository;
import de.htw.auth.repository.UserRepository;
import de.htw.auth.service.AuthService;
import jakarta.transaction.Transactional;
import org.apache.http.HttpHeaders;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthTests {
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private AuthService authService;

	@Autowired
	ObjectMapper objectMapper;

	private final String token = "NWA3tET3lrkL_aNPg3VhWro9gSa5sCg5";

	@BeforeEach
	private void setupMockMvc() {
		mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
	}

	@Test
	@Transactional
	void postWithIncorrectContentTypeShouldReturn400() throws Exception {
		mockMvc.perform(post("/rest/auth")
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.accept(MediaType.ALL))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	@Transactional
	void postWithUserPasswordNotInUserTableShouldReturn401() throws Exception {
		UserRequest userRequest = getUserRequest2();
		String userRequestString = objectMapper.writeValueAsString(userRequest);
		mockMvc.perform(post("/rest/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.ALL)
				.content(userRequestString))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@Transactional
	void postSuccessfulShouldReturn200() throws Exception {
		UserRequest userRequest = getUserRequest();
		String userRequestString = objectMapper.writeValueAsString(userRequest);
		assert(userRepository.existsById("maxime"));
		mockMvc.perform(post("/rest/auth")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.ALL)
						.content(userRequestString))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(content().string(Matchers.not("")));
	}

	@Test
	@Transactional
	void checkTokenPositive() throws Exception {
		mockMvc.perform(get("/rest/auth")
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string("true"));
	}

	@Test
	@Transactional
	void checkTokenNegative() throws Exception {
		mockMvc.perform(get("/rest/auth")
				.header(HttpHeaders.AUTHORIZATION, "blablabla"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string("false"));
	}

	@Test
	@Transactional
	void getUserIdByTokenPositive() throws Exception {
		mockMvc.perform(get("/rest/auth/tokens")
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isOk())
				.andExpect(content().string("maxime"));
	}

	@Test
	@Transactional
	void getUserIdByTokenNegative() throws Exception {
		mockMvc.perform(get("/rest/auth/tokens")
				.header(HttpHeaders.AUTHORIZATION, "blablabal"))
				.andExpect(status().isUnauthorized());
	}

	UserRequest getUserRequest() {
		return UserRequest.builder()
				.userId("maxime")
				.password("pass1234")
				.build();
	}

	UserRequest getUserRequest2() {
		return UserRequest.builder()
				.userId("maxime")
				.password("pass4321")
				.build();
	}
}
