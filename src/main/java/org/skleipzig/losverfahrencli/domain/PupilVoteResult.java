package org.skleipzig.losverfahrencli.domain;

import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.skleipzig.losverfahrencli.csv.PupilVoteResultDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@CommonsLog
public class PupilVoteResult {
    public static final int MAX_VOTES = 3;

    private final Pupil pupil;

    private final Map<Integer, ProjectGroupPreference> preferenceMap;

    public ProjectGroupPreference getPreference(int priority) {
        ProjectGroupPreference preference = preferenceMap.getOrDefault(priority, ProjectGroupPreference.none(priority));
        log.trace(getPupil().getForeName() + " has: " + preference);
        return preference;
    }

    /**
     * Liefert nur dann ein Ergebnis, wenn der Schüler in der Schülerliste ist. Abstimmung für ein unbekanntes Projekt führt zu {@link ProjectGroupPreference#none(int)}.
     */
    public static Optional<PupilVoteResult> fromDTO(PupilVoteResultDTO pupilVoteResultDTO, List<Pupil> pupils, List<ProjectGroup> availableProjectGroups) {
        return Pupil.selectByEmail(pupils, pupilVoteResultDTO.getLogin())
                .map(pupil -> new PupilVoteResult(pupil, Map.of(1, createPreference(1, pupilVoteResultDTO.getPrimaryChoice(), availableProjectGroups), 2, createPreference(2, pupilVoteResultDTO.getSecondaryChoice(), availableProjectGroups), 3, createPreference(3, pupilVoteResultDTO.getTertiaryChoice(), availableProjectGroups))));
    }

    private static ProjectGroupPreference createPreference(int priority, String projectName, List<ProjectGroup> availableProjectGroups) {
        return ProjectGroup.selectByName(availableProjectGroups, projectName)
                .map(projectGroup -> ProjectGroupPreference.forProjectGroup(priority, projectGroup))
                .orElse(ProjectGroupPreference.none(priority));
    }

    public String toString() {
        return pupil + ", " + getPreference(1) + ", " + getPreference(2) + ", " + getPreference(3);
    }

}
