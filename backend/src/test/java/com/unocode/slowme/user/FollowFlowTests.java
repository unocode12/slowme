package com.unocode.slowme.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class FollowFlowTests {

	@Autowired
	WebApplicationContext wac;

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	void follow_unfollow_and_counts() throws Exception {
		// create user A
		String u1 = """
				{ "nickname": "a" }
				""";
		var r1 = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(u1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andReturn();
		String body1 = r1.getResponse().getContentAsString();
		long userA = Long.parseLong(body1.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1"));

		// create user B
		String u2 = """
				{ "nickname": "b" }
				""";
		var r2 = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(u2))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andReturn();
		String body2 = r2.getResponse().getContentAsString();
		long userB = Long.parseLong(body2.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1"));

		// follow A -> B
		mockMvc.perform(post("/users/" + userA + "/follow/" + userB))
				.andExpect(status().isNoContent());

		// counts
		mockMvc.perform(get("/users/" + userB + "/followers/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1));
		mockMvc.perform(get("/users/" + userA + "/following/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1));

		// idempotent follow
		mockMvc.perform(post("/users/" + userA + "/follow/" + userB))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/users/" + userB + "/followers/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1));

		// unfollow
		mockMvc.perform(delete("/users/" + userA + "/follow/" + userB))
				.andExpect(status().isNoContent());

		// counts become 0
		mockMvc.perform(get("/users/" + userB + "/followers/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0));
		mockMvc.perform(get("/users/" + userA + "/following/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0));

		// idempotent unfollow
		mockMvc.perform(delete("/users/" + userA + "/follow/" + userB))
				.andExpect(status().isNoContent());
	}
}


