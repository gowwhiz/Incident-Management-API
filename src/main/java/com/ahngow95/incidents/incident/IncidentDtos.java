package com.ahngow95.incidents.incident;

import com.ahngow95.incidents.incident.IncidentEnums.Severity;
import com.ahngow95.incidents.incident.IncidentEnums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public final class IncidentDtos {

    private IncidentDtos() {
    }

    public record CreateIncidentRequest(
            @NotBlank @Size(max = 160) String title,
            @NotBlank String description,
            @NotNull Severity severity,
            @Size(max = 120) String assignedTo
    ) {
    }

    public record UpdateIncidentRequest(
            @NotBlank @Size(max = 160) String title,
            @NotBlank String description,
            @NotNull Severity severity,
            @NotNull Status status,
            @Size(max = 120) String assignedTo
    ) {
    }

    public record IncidentResponse(
            Long id,
            String title,
            String description,
            Severity severity,
            Status status,
            String assignedTo,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime resolvedAt
    ) {
    }
}
