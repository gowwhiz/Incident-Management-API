package com.ahngow95.incidents.incident;

import com.ahngow95.incidents.incident.IncidentDtos.CreateIncidentRequest;
import com.ahngow95.incidents.incident.IncidentDtos.IncidentResponse;
import com.ahngow95.incidents.incident.IncidentDtos.StatusUpdateRequest;
import com.ahngow95.incidents.incident.IncidentDtos.UpdateIncidentRequest;
import com.ahngow95.incidents.incident.IncidentEnums.Severity;
import com.ahngow95.incidents.incident.IncidentEnums.Status;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request) {
        IncidentResponse response = incidentService.create(request);
        return ResponseEntity.created(URI.create("/api/incidents/" + response.id())).body(response);
    }

    @GetMapping
    public Page<IncidentResponse> findAll(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false, name = "q") String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return incidentService.findAll(status, severity, assignedTo, search, pageable);
    }

    @GetMapping("/{id}")
    public IncidentResponse findById(@PathVariable Long id) {
        return incidentService.findById(id);
    }

    @PutMapping("/{id}")
    public IncidentResponse update(@PathVariable Long id, @Valid @RequestBody UpdateIncidentRequest request) {
        return incidentService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public IncidentResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return incidentService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        incidentService.delete(id);
    }
}
