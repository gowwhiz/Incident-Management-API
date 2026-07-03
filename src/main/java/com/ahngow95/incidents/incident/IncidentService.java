package com.ahngow95.incidents.incident;

import com.ahngow95.incidents.incident.IncidentDtos.CreateIncidentRequest;
import com.ahngow95.incidents.incident.IncidentDtos.IncidentResponse;
import com.ahngow95.incidents.incident.IncidentDtos.UpdateIncidentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public IncidentResponse create(CreateIncidentRequest request) {
        Incident saved = incidentRepository.save(IncidentMapper.toEntity(request));
        return IncidentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponse> findAll(Pageable pageable) {
        return incidentRepository.findAll(pageable).map(IncidentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public IncidentResponse findById(Long id) {
        return IncidentMapper.toResponse(getIncident(id));
    }

    @Transactional
    public IncidentResponse update(Long id, UpdateIncidentRequest request) {
        Incident incident = getIncident(id);
        IncidentMapper.applyUpdate(incident, request);
        return IncidentMapper.toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public void delete(Long id) {
        Incident incident = getIncident(id);
        incidentRepository.delete(incident);
    }

    private Incident getIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found: " + id));
    }
}
