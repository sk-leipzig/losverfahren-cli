package org.skleipzig.losverfahrencli.domain;

public record RemainingSeats(ProjectGroup projectGroup, int capacity) {

    public String toString() {
        return projectGroup.getProjectName() + "(Restplätze: " + capacity + ")";
    }
}
