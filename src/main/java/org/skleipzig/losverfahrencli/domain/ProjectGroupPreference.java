package org.skleipzig.losverfahrencli.domain;

import java.util.Objects;

public record ProjectGroupPreference(int priority, String projectGroupName) {

    /**
     * Keine Präferenz bedeutet, alle Projekte werden abgelehnt.
     */
    static ProjectGroupPreference none(int priority) {
        return new ProjectGroupPreference(priority, null);
    }

    static ProjectGroupPreference forProjectGroup(int priority, ProjectGroup projectGroup) {
        return forProjectGroupWithName(priority, projectGroup.getProjectName());
    }

    static ProjectGroupPreference forProjectGroupWithName(int priority, String projectGroupName) {
        return new ProjectGroupPreference(priority, projectGroupName);
    }

    public boolean accepts(ProjectGroup projectGroup) {
        return Objects.equals(projectGroupName, projectGroup.getProjectName());
    }

    public String toString() {
        return priority + ". Wunsch: " + (projectGroupName == null ? "k.A." : projectGroupName);
    }

}
