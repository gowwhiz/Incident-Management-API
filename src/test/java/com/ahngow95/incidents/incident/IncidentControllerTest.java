package com.ahngow95.incidents.incident;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createIncidentReturnsCreatedIncident() throws Exception {
        String payload = """
                {
                  "title": "Checkout API returning 500",
                  "description": "Customers are unable to complete checkout.",
                  "severity": "P1",
                  "assignedTo": "platform-team"
                }
                """;

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/incidents/1"))
                .andExpect(jsonPath("$.title", is("Checkout API returning 500")))
                .andExpect(jsonPath("$.severity", is("P1")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.assignedTo", is("platform-team")));
    }

    @Test
    void listIncidentsReturnsPagedResponse() throws Exception {
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
