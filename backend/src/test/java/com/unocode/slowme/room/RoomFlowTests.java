package com.unocode.slowme.room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class RoomFlowTests {

	@Autowired
	WebApplicationContext wac;

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	void roomLifecycle_create_list_join_leave_close() throws Exception {
		// create room (hostId=1)
		String createBody = """
				{
				  "type": "TYPING",
				  "hostId": 1,
				  "maxParticipants": 2
				}
				""";

		var create = mockMvc.perform(post("/rooms")
						.contentType(MediaType.APPLICATION_JSON)
						.content(createBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.status").value("OPEN"))
				.andExpect(jsonPath("$.participantCount").value(1))
				.andReturn();

		String json = create.getResponse().getContentAsString();
		String id = json.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1");

		// list includes room
		mockMvc.perform(get("/rooms"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id").value(hasItem(Integer.parseInt(id))));

		// join userId=2
		String joinBody = """
				{ "userId": 2 }
				""";
		mockMvc.perform(post("/rooms/" + id + "/join")
						.contentType(MediaType.APPLICATION_JSON)
						.content(joinBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.participantCount").value(2));

		// leave userId=2 (room still open because host remains)
		mockMvc.perform(post("/rooms/" + id + "/leave")
						.contentType(MediaType.APPLICATION_JSON)
						.content(joinBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("OPEN"))
				.andExpect(jsonPath("$.participantCount").value(1));

		// host leaves -> room closes
		String hostLeave = """
				{ "userId": 1 }
				""";
		mockMvc.perform(post("/rooms/" + id + "/leave")
						.contentType(MediaType.APPLICATION_JSON)
						.content(hostLeave))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CLOSED"))
				.andExpect(jsonPath("$.participantCount").value(0));

		// closed room should not appear in list
		mockMvc.perform(get("/rooms"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id").value(not(hasItem(Integer.parseInt(id)))));
	}
}


