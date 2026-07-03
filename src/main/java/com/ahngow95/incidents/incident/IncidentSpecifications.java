package com.ahngow95.incidents.incident;

import com.ahngow95.incidents.incident.IncidentEnums.Severity;
import com.ahngow95.incidents.incident.IncidentEnums.Status;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class IncidentSpecifications {

    private IncidentSpecifications() {
    }

    public static Specification<Incident> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> status == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Incident> hasSeverity(Severity severity) {
        return (root, query, criteriaBuilder) -> severity == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("severity"), severity);
    }

    public static Specification<Incident> assignedTo(String assignedTo) {
        return (root, query, criteriaBuilder) -> !StringUtils.hasText(assignedTo)
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(criteriaBuilder.lower(root.get("assignedTo")), assignedTo.toLowerCase());
    }

    public static Specification<Incident> search(String term) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(term)) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + term.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }
}
