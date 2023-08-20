package org.skleipzig.losverfahrencli.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.skleipzig.losverfahrencli.domain.Pupil.ProjectGroupPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.skleipzig.losverfahrencli.domain.Pupil.PREFERENCE_DEFINITIONS;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@CommonsLog
public class Distribution {
    private final List<Pupil> unassignedPupils;
    private final List<Attendance> attendances;

    public static Distribution create(List<ProjectGroup> projectGroups, List<Pupil> pupils) {
        List<Pupil> unassignedPupils = new ArrayList<>(pupils);
        List<Attendance> attendances = projectGroups.stream()
                .map(Attendance::createAttendance)
                .toList();
        return new Distribution(unassignedPupils, attendances);
    }

    public List<ProjectGroup> getOpenProjectGroups() {
        return attendances.stream()
                .filter(attendance -> attendance.getAvailableSlots() > 0)
                .map(Attendance::getProjectGroup)
                .toList();
    }

    /**
     * Weist alle freien Schüler nach ihrer Präferenz zu einem Projekt zu, beginnend mit dem Erstwunsch.
     * Wenn keine Zuweisung anhand Präferenz mehr möglich ist, wird ohne Berücksichtigung der Präferenzen weiter verteilt.
     */
    public Distribution assignAllPupils() {
        return assignByPreference().assignRemainingPupils();
    }

    private Distribution assignByPreference() {
        log.debug("assignByPreference");
        Distribution result = this;
        for (Function<Pupil, ProjectGroupPreference> preferenceDefinition : PREFERENCE_DEFINITIONS) {
            log.debug("Open ProjectGroups: " + ProjectGroup.projectGroupNames(result.getOpenProjectGroups()));
            for (ProjectGroup openProjectGroup : result.getOpenProjectGroups()) {
                log.debug("Assigning to ProjectGroup: " + openProjectGroup.getProjectName());
                List<Pupil> pupilsToAssign = result.getUnassignedPupils()
                        .stream()
                        .filter(pupil -> preferenceDefinition.apply(pupil).test(openProjectGroup))
                        .toList();
                log.debug("Pupils to assign: " + Pupil.pupilCollectionToString(pupilsToAssign));
                result = result.assignPupils(openProjectGroup, pupilsToAssign);
            }
        }
        log.debug(result.createResultString());
        return result;
    }

    private Distribution assignRemainingPupils() {
        log.debug("assignRemainingPupils");
        Distribution result = this;
        log.debug("Open ProjectGroups: " + ProjectGroup.projectGroupNames(result.getOpenProjectGroups()));
        for (ProjectGroup openProjectGroup : result.getOpenProjectGroups()) {
            log.debug("Assigning to ProjectGroup: " + openProjectGroup.getProjectName());
            log.debug("Pupils to assign: " + result.unassignedPupils);
            result = result.assignPupils(openProjectGroup, result.unassignedPupils);
        }
        log.debug(result.createResultString());
        return result;
    }

    private Distribution assignPupils(ProjectGroup projectGroup, List<Pupil> pupils) {
        Attendance attendance = attendances.stream()
                .filter(a -> a.hasProjectGroup(projectGroup))
                .findAny()
                .orElse(null);
        if (attendance != null && pupils != null && !pupils.isEmpty()) {
            ArrayList<Attendance> updatedAttendances = new ArrayList<>(attendances);
            Attendance updatedAttendance = attendance.assignPupils(pupils);
            List<Pupil> remainingUnassignedPupils = new ArrayList<>(unassignedPupils);
            remainingUnassignedPupils.removeAll(updatedAttendance.getAttendees());
            updatedAttendances.remove(attendance);
            updatedAttendances.add(updatedAttendance);
            return new Distribution(remainingUnassignedPupils, updatedAttendances);
        } else {
            return this;
        }
    }

    private String createResultString() {
        return "Result:\n" + collectionToDelimitedString(attendances, "\n", "\t", "") +
                "\nStill unassigned:" + Pupil.pupilCollectionToString(getUnassignedPupils()) +
                "\nOpen ProjectGroups: " + ProjectGroup.projectGroupNames(getOpenProjectGroups());

    }
}
