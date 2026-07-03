package com.ahngow95.incidents.incident;

public final class IncidentEnums {

    private IncidentEnums() {
    }

    public enum Severity {
        P1,
        P2,
        P3,
        P4
    }

    public enum Status {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        CLOSED
    }
}
