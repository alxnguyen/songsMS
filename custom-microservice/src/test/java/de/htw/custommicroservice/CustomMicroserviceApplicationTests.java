package de.htw.custommicroservice;

import de.htw.custommicroservice.controller.MyController;
import de.htw.custommicroservice.service.MyService;
import de.htw.custommicroservice.service.WebService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CustomMicroserviceApplicationTests {
	private MockMvc mockMvc;

	private MyService myService;

	@Mock
	private WebService webService;

	private final String token = "NWA3tET3lrkL_aNPg3VhWro9gSa5sCg5";

	@BeforeEach
	void setupMockMvc() {
		myService = new MyService(webService);
		mockMvc = MockMvcBuilders.standaloneSetup(new MyController(myService)).build();
		doNothing().when(webService).checkToken(anyString());
	}

	@Test
	void getSuccessfulLyrics() throws Exception {
		String songJson = """
				{
					"id": 1,
					"title": "Achy Breaky Heart",
					"artist": "Billy Ray Cyrus",
					"label": "PolyGram Mercury",
					"released": 1992
				}""";

		when(webService.getSongFromDB(anyInt(), anyString())).thenReturn(new JSONObject(songJson));

		String expectedLyrics = """
				[Verse 1]
				You can tell the world you never was my girl
				You can burn my clothes when I'm gone
				Or you can tell your friends just what a fool I've been
				And laugh and joke about me on the phone
				You can tell my arms go back to the farm
				You can tell my feet to hit the floor
				Or you can tell my lips to tell my fingertips
				They won't be reaching out for you no more
				    
				[Chorus]
				But don't tell my heart, my achy breaky heart
				I just don't think he'd understand
				And if you tell my heart, my achy breaky heart
				He might blow up and kill this man, ooh
				    
				[Verse 2]
				You can tell your ma I moved to Arkansas
				You can tell your dog to bite my leg
				Or tell your brother Cliff whose fist can tell my lip
				He never really liked me anyway
				Or tell your Aunt Louise, tell anything you please
				Myself already knows I'm not okay
				Or you can tell my eyes to watch out for my mind
				It might be walking out on me today
				    
				[Chorus]
				But don't tell my heart, my achy breaky heart
				I just don't think he'd understand
				And if you tell my heart, my achy breaky heart
				He might blow up and kill this man, ooh
				    
				[Instrumental Break]
				    
				[Chorus]
				Don't tell my heart, my achy breaky heart
				I just don't think he'd understand
				And if you tell my heart, my achy breaky heart
				He might blow up and kill this man
				Don't tell my heart, my achy breaky heart
				I just don't think he'd understand
				And if you tell my heart, my achy breaky heart
				He might blow up and kill this man, ooh-ooh
				Ooh!""";

		mockMvc.perform(MockMvcRequestBuilders.get("/rest/myservice/1")
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isOk())
				.andExpect(content().string(expectedLyrics));
	}

	@Test
	void searchInvalidSong() throws Exception {
		String songJson = """
				{
					"id": 1,
					"title": "asasasasasasasassa",
					"artist": "Billy Ray Cyrus",
					"label": "PolyGram Mercury",
					"released": 1992
				}""";

		when(webService.getSongFromDB(anyInt(), anyString())).thenReturn(new JSONObject(songJson));
		mockMvc.perform(MockMvcRequestBuilders.get("/rest/myservice/1")
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isBadRequest());
	}
}
