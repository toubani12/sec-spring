package com.badr.secspring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String REGISTER =
            "{\"name\":\"Test\",\"email\":\"test@enset.ma\",\"password\":\"secret123\"}";

    private void register() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(REGISTER));
    }

    private String loginAndGetToken() throws Exception {
        register();
        MvcResult res = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@enset.ma\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        String body = res.getResponse().getContentAsString();
        return body.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void registerCreatesUserThenRejectsDuplicate() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Dup\",\"email\":\"dup@enset.ma\",\"password\":\"pwd\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("dup@enset.ma"));
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Dup\",\"email\":\"dup@enset.ma\",\"password\":\"pwd\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email deja utilise"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        register();
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@enset.ma\",\"password\":\"mauvais\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apiWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/hello")).andExpect(status().isUnauthorized());
    }

    @Test
    void apiWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/hello").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apiWithValidTokenReturns200() throws Exception {
        String token = loginAndGetToken();
        mockMvc.perform(get("/api/hello").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Bienvenue dans l'API securisee"));
        mockMvc.perform(get("/api/profile").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test@enset.ma")));
        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }
}
