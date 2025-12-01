package com.sivalabs.myapp.users.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.sivalabs.myapp.TestcontainersConfig;
import com.sivalabs.myapp.users.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfig.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void shouldFetchBookmarksByPageNumber() throws Exception {
        MvcTestResult testResult = mockMvcTester.get().uri("/api/users").exchange();
        assertThat(testResult).hasStatusOk();
    }

    @Test
    void shouldCreateBookmarkSuccessfully() throws Exception {
        MvcTestResult testResult = mockMvcTester
                .post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "siva@gmail.com",
                            "password": "secret",
                            "name": "Siva"
                        }
                        """)
                .exchange();

        assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(User.class)
                .satisfies(user -> {
                    assertThat(user.getId()).isNotNull();
                    assertThat(user.getEmail()).isEqualTo("siva@gmail.com");
                    assertThat(user.getName()).isEqualTo("Siva");
                });
    }
}
