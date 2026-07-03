package com.ahngow95.incidents.incident;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IncidentRepository incidentRepository;

    @BeforeEach
    void cleanDatabase() {
        incidentRepository.deleteAll();
    }

    @Test
    void createIncidentReturnsCreatedIncidentWithSlaDueDate() throws Exception {
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
                .andExpect(header().string("Location", startsWith("/api/incidents/")))
                .andExpect(jsonPath("$.title", is("Checkout API returning 500")))
                .andExpect(jsonPath("$.severity", is("P1")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.assignedTo", is("platform-team")))
                .andExpect(jsonPath("$.slaDueAt", notNullValue()))
                .andExpect(jsonPath("$.slaBreached", is(false)));
    }

    @Test
    void createIncidentRejectsInvalidPayloadWithStandardErrorResponse() throws Exception {
        String payload = """
                {
                  "title": "",
                  "description": "",
                  "severity": null
                }
                """;

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Request validation failed")))
                .andExpect(jsonPath("$.path", is("/api/incidents")))
                .andExpect(jsonPath("$.fieldViolations.length()", greaterThanOrEqualTo(3)));
    }

    @Test
    void findMissingIncidentReturnsStandardNotFoundError() throws Exception {
        mockMvc.perform(get("/api/incidents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Incident not found: 999")));
    }

    @Test
    void listIncidentsSupportsFilteringAndSearch() throws Exception {
        createIncident("Checkout API returning 500", "Payments are failing.", "P1", "platform-team");
        createIncident("Daily report delayed", "Batch job is running behind schedule.", "P3", "data-team");

        mockMvc.perform(get("/api/incidents")
                        .param("severity", "P1")
                        .param("assignedTo", "platform-team")
                        .param("q", "checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Checkout API returning 500")))
                .andExpect(jsonPath("$.content[0].severity", is("P1")));
    }

    @Test
    void updateStatusSetsResolvedTimestamp() throws Exception {
        String location = createIncident("Search service degraded", "Latency is above threshold.", "P2", "search-team");
        String incidentId = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(patch("/api/incidents/{id}/status", incidentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "RESOLVED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESOLVED")))
                .andExpect(jsonPath("$.resolvedAt", notNullValue()));
    }

    private String createIncident(String title, String description, String severity, String assignedTo) throws Exception {
        String payload = """
                {
                  "title": "%s",
                  "description": "%s",
                  "severity": "%s",
                  "assignedTo": "%s"
                }
                """.formatted(title, description, severity, assignedTo);

        return mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }
}
