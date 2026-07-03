package com.ahngow95.incidents.incident;

import com.ahngow95.incidents.common.NotFoundException;
import com.ahngow95.incidents.incident.IncidentDtos.CreateIncidentRequest;
import com.ahngow95.incidents.incident.IncidentDtos.IncidentResponse;
import com.ahngow95.incidents.incident.IncidentDtos.StatusUpdateRequest;
import com.ahngow95.incidents.incident.IncidentDtos.UpdateIncidentRequest;
import com.ahngow95.incidents.incident.IncidentEnums.Severity;
import com.ahngow95.incidents.incident.IncidentEnums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public IncidentResponse create(CreateIncidentRequest request) {
        Incident incident = IncidentMapper.toEntity(request);
        incident.setSlaDueAt(calculateSlaDueAt(request.severity(), OffsetDateTime.now()));
        Incident saved = incidentRepository.save(incident);
        return IncidentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponse> findAll(Status status, Severity severity, String assignedTo, String search, Pageable pageable) {
        Specification<Incident> specification = Specification
                .where(IncidentSpecifications.hasStatus(status))
                .and(IncidentSpecifications.hasSeverity(severity))
                .and(IncidentSpecifications.assignedTo(assignedTo))
                .and(IncidentSpecifications.search(search));

        return incidentRepository.findAll(specification, pageable).map(IncidentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public IncidentResponse findById(Long id) {
        return IncidentMapper.toResponse(getIncident(id));
    }

    @Transactional
    public IncidentResponse update(Long id, UpdateIncidentRequest request) {
        Incident incident = getIncident(id);
        boolean severityChanged = incident.getSeverity() != request.severity();

        IncidentMapper.applyUpdate(incident, request);
        if (severityChanged && incident.getStatus() != Status.RESOLVED && incident.getStatus() != Status.CLOSED) {
            incident.setSlaDueAt(calculateSlaDueAt(request.severity(), incident.getCreatedAt()));
        }

        return IncidentMapper.toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public IncidentResponse updateStatus(Long id, StatusUpdateRequest request) {
        Incident incident = getIncident(id);
        IncidentMapper.applyStatus(incident, request.status());
        return IncidentMapper.toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public void delete(Long id) {
        Incident incident = getIncident(id);
        incidentRepository.delete(incident);
    }

    private Incident getIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incident not found: " + id));
    }

    private OffsetDateTime calculateSlaDueAt(Severity severity, OffsetDateTime start) {
        return switch (severity) {
            case P1 -> start.plusHours(4);
            case P2 -> start.plusHours(8);
            case P3 -> start.plusHours(24);
            case P4 -> start.plusHours(72);
        };
    }
}
