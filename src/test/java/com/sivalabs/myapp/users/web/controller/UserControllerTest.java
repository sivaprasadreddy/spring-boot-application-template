package com.sivalabs.myapp.users.web.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sivalabs.myapp.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void shouldFetchBookmarksByPageNumber() throws Exception {
        this.mockMvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    void shouldCreateBookmarkSuccessfully() throws Exception {
        this.mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                                {
                                                    "email": "siva@gmail.com",
                                                    "password": "secret",
                                                    "name": "Siva"
                                                }
                                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("siva@gmail.com")))
                .andExpect(jsonPath("$.name", equalTo("Siva")));
    }
}
