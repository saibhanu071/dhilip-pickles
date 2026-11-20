package com.dhilip.pickles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private String uniqueEmail() {
        return "test" + UUID.randomUUID().toString().substring(0, 8)
                + "@example.com";
    }

    private String uniqueMobile() {
        return "9" + String.format("%09d",
                Math.floorMod(UUID.randomUUID().hashCode(), 1_000_000_000)
        );
    }

    @Test
    void customerCanRegisterSuccessfully() throws Exception {
        String email = uniqueEmail();
        String mobile = uniqueMobile();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "fullName": "Test Customer",
                      "mobile": "%s",
                      "email": "%s",
                      "password": "TestPass123"
                    }
                    """.formatted(mobile, email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.customer.email").value(email))
                .andExpect(jsonPath("$.customer.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.customer.password").doesNotExist());
    }

    @Test
    void customerCanLoginSuccessfully() throws Exception {
        String email = uniqueEmail();
        String mobile = uniqueMobile();

        String registration = """
            {
              "fullName": "Login Customer",
              "mobile": "%s",
              "email": "%s",
              "password": "TestPass123"
            }
            """.formatted(mobile, email);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registration))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "TestPass123"
                    }
                    """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.customer.email").value(email));
    }

    @Test
    void duplicateEmailRegistrationIsRejected() throws Exception {
        String email = uniqueEmail();

        String firstRegistration = """
            {
              "fullName": "Test Customer",
              "mobile": "%s",
              "email": "%s",
              "password": "TestPass123"
            }
            """.formatted(uniqueMobile(), email);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstRegistration))
                .andExpect(status().isCreated());

        String secondRegistration = """
            {
              "fullName": "Another Customer",
              "mobile": "%s",
              "email": "%s",
              "password": "TestPass123"
            }
            """.formatted(uniqueMobile(), email);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondRegistration))
                .andExpect(status().isConflict());
    }

    @Test
    void incorrectPasswordIsRejected() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "fullName": "Test Customer",
                      "mobile": "%s",
                      "email": "%s",
                      "password": "TestPass123"
                    }
                    """.formatted(uniqueMobile(), email)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "WrongPass123"
                    }
                    """.formatted(email)))
                .andExpect(status().isUnauthorized());
    }
}
