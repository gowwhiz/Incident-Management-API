package com.ahngow95.incidents.incident;

import com.ahngow95.incidents.incident.IncidentDtos.CreateIncidentRequest;
import com.ahngow95.incidents.incident.IncidentDtos.IncidentResponse;
import com.ahngow95.incidents.incident.IncidentDtos.UpdateIncidentRequest;
import com.ahngow95.incidents.incident.IncidentEnums.Status;

import java.time.OffsetDateTime;

public final class IncidentMapper {

    private IncidentMapper() {
    }

    public static Incident toEntity(CreateIncidentRequest request) {
        Incident incident = new Incident();
        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setSeverity(request.severity());
        incident.setAssignedTo(request.assignedTo());
        incident.setStatus(Status.OPEN);
        return incident;
    }

    public static void applyUpdate(Incident incident, UpdateIncidentRequest request) {
        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setSeverity(request.severity());
        incident.setStatus(request.status());
        incident.setAssignedTo(request.assignedTo());

        if (request.status() == Status.RESOLVED || request.status() == Status.CLOSED) {
            if (incident.getResolvedAt() == null) {
                incident.setResolvedAt(OffsetDateTime.now());
            }
        } else {
            incident.setResolvedAt(null);
        }
    }

    public static IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getAssignedTo(),
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getResolvedAt()
        );
    }
}
