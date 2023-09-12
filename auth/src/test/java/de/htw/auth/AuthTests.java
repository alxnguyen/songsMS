package de.htw.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.auth.dto.UserRequest;
import de.htw.auth.repository.TokenRepository;
import de.htw.auth.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void postWithIncorrectContentTypeShouldReturn400() throws Exception {
		mockMvc.perform(post("/rest/auth")
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.accept(MediaType.ALL))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
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
