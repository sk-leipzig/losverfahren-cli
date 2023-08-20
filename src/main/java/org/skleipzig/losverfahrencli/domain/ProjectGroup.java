package org.skleipzig.losverfahrencli.domain;

import lombok.Data;
import org.skleipzig.losverfahrencli.csv.ProjectGroupDTO;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class ProjectGroup {
    private final String projectName;

    private final int minForm;

    private final int maxForm;

    private final int capacity;

    public static ProjectGroup fromDTO(ProjectGroupDTO projectGroupDTO) {
        return new ProjectGroup(projectGroupDTO.getProjectName(), projectGroupDTO.getMinForm(),
                projectGroupDTO.getMaxForm(), Integer.parseInt(projectGroupDTO.getCapacity()));
    }

    public static Optional<ProjectGroup> selectByName(List<ProjectGroup> projectGroups, String projectName) {
        return projectGroups.stream()
                .filter(projectGroup -> Objects.equals(projectGroup.getProjectName(), projectName))
                .findAny();
    }

    public boolean isSameProjectGroup(ProjectGroup other) {
        return projectName.equals(other.projectName);
    }

    public static String projectGroupNames(Collection<ProjectGroup> projectGroups) {
        return projectGroups == null || projectGroups.isEmpty() ? "N/A" :
                projectGroups.stream().map(ProjectGroup::getProjectName).collect(Collectors.joining(", "));
    }
}
