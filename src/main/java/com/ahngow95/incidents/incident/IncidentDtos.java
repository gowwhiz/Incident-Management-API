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
            @NotBlank(message = "title is required")
            @Size(max = 160, message = "title must be 160 characters or less")
            String title,

            @NotBlank(message = "description is required")
            @Size(max = 4000, message = "description must be 4000 characters or less")
            String description,

            @NotNull(message = "severity is required")
            Severity severity,

            @Size(max = 120, message = "assignedTo must be 120 characters or less")
            String assignedTo
    ) {
    }

    public record UpdateIncidentRequest(
            @NotBlank(message = "title is required")
            @Size(max = 160, message = "title must be 160 characters or less")
            String title,

            @NotBlank(message = "description is required")
            @Size(max = 4000, message = "description must be 4000 characters or less")
            String description,

            @NotNull(message = "severity is required")
            Severity severity,

            @NotNull(message = "status is required")
            Status status,

            @Size(max = 120, message = "assignedTo must be 120 characters or less")
            String assignedTo
    ) {
    }

    public record StatusUpdateRequest(
            @NotNull(message = "status is required")
            Status status
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
            OffsetDateTime slaDueAt,
            OffsetDateTime resolvedAt,
            boolean slaBreached
    ) {
    }
}
