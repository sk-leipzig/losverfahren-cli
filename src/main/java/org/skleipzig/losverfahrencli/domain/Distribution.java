package org.skleipzig.losverfahrencli.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

import java.util.*;
import java.util.stream.Collectors;

import static org.skleipzig.losverfahrencli.domain.AssignmentType.Zugelost;
import static org.skleipzig.losverfahrencli.domain.Pupil.pupilCollectionToString;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@CommonsLog
public class Distribution {
    private final List<PupilVoteResult> openVotes;
    private final List<Attendance> attendances;

    public static Distribution create(List<ProjectGroup> projectGroups, List<PupilVoteResult> voteResults) {
        List<Attendance> attendances = projectGroups.stream()
                .map(Attendance::createAttendance)
                .toList();
        return new Distribution(new ArrayList<>(voteResults), attendances);
    }

    public List<ProjectGroup> getOpenProjectGroups() {
        return attendances.stream()
                .filter(attendance -> attendance.getAvailableSlots() > 0)
                .map(Attendance::getProjectGroup)
                .toList();
    }

    public Optional<ProjectGroup> getOpenProjectGroupWithMostOpenSlots() {
        return attendances.stream()
                .filter(attendance -> attendance.getAvailableSlots() > 0)
                .max(Comparator.comparingInt(Attendance::getAvailableSlots))
                .map(Attendance::getProjectGroup);
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
        for (int i = 1; i <= PupilVoteResult.MAX_VOTES; i++) {
            AssignmentType assignmentType = AssignmentType.byOrdinal(i - 1);
            log.debug("Processing " + assignmentType + " votes for open ProjectGroups: " + ProjectGroup.projectGroupNames(result.getOpenProjectGroups()));
            for (ProjectGroup openProjectGroup : result.getOpenProjectGroups()) {
                final int priority = i;
                log.debug("Processing ProjectGroup: " + openProjectGroup.getProjectName());
                List<Pupil> pupilsToAssign = new ArrayList<>(result.getOpenVotes()
                        .stream()
                        .filter(pupilVoteResult -> pupilVoteResult.getPreference(priority)
                                .accepts(openProjectGroup))
                        .map(PupilVoteResult::getPupil)
                        .toList());
                Collections.shuffle(pupilsToAssign);
                log.debug("Pupils to assign: " + pupilCollectionToString(pupilsToAssign));
                result = result.assignPupils(openProjectGroup, pupilsToAssign, assignmentType);
            }
        }
        log.debug(result.createResultString());
        return result;
    }

    private Distribution assignRemainingPupils() {
        log.debug("assignRemainingPupils");
        Distribution result = this;
        log.debug("Open ProjectGroups: " + ProjectGroup.projectGroupNames(result.getOpenProjectGroups()));
        List<Pupil> pupils = result.openVotes.stream()
                .map(PupilVoteResult::getPupil)
                .toList();
        for (Pupil pupil : pupils) {
            log.debug("Processing pupil: " + pupil);
            Optional<ProjectGroup> openProjectGroup = result.getOpenProjectGroupWithMostOpenSlots();
            if (openProjectGroup.isPresent()) {
                log.debug("Assigning " + pupil + " to " + openProjectGroup.get());
                result = result.assignPupils(openProjectGroup.get(), List.of(pupil), Zugelost);
            } else {
                log.debug("No open ProjectGroup left");
                break;
            }
        }
        log.debug(result.createResultString());
        return result;
    }

    private Distribution assignPupils(ProjectGroup projectGroup, List<Pupil> pupils, AssignmentType assignmentType) {
        Attendance attendance = attendances.stream()
                .filter(a -> a.hasProjectGroup(projectGroup))
                .findAny()
                .orElse(null);
        if (attendance != null && pupils != null && !pupils.isEmpty()) {
            ArrayList<Attendance> updatedAttendances = new ArrayList<>(attendances);
            Attendance updatedAttendance = attendance.assignPupils(pupils, assignmentType);
            List<PupilVoteResult> remainingVotes = new ArrayList<>(openVotes)
                    .stream()
                    .filter(pupilVoteResult -> !updatedAttendance.getAttendees()
                            .containsKey(pupilVoteResult.getPupil()))
                    .toList();
            updatedAttendances.remove(attendance);
            updatedAttendances.add(updatedAttendance);
            return new Distribution(remainingVotes, updatedAttendances);
        } else {
            return this;
        }
    }

    public String createResultString() {
        return "Ergebnis:\n" + collectionToDelimitedString(attendances, "\n\n") +
                "\n\n Nicht zugewiesene Schüler:\n\t" + listUnassignedPupils() +
                "\n\n Offene Projektgruppen: " + listOpenProjectGroups();

    }

    public List<RemainingSeats> getRemainingSeats() {
        return attendances.stream()
                .filter(attendance -> attendance.getAvailableSlots() > 0)
                .map(Attendance::getRemainingSeats)
                .toList();
    }

    public long getNumberOfZugelost() {
        return attendances.stream()
                .map(Attendance::getAttendees)
                .flatMap(attendees -> attendees.entrySet()
                        .stream())
                .filter(entry -> entry.getValue() == Zugelost)
                .count();
    }

    private String listOpenProjectGroups() {
        return getRemainingSeats()
                .stream()
                .map(RemainingSeats::toString)
                .collect(Collectors.joining("\n\t"));
    }

    private String listUnassignedPupils() {
        return getOpenVotes().stream()
                .map(PupilVoteResult::getPupil)
                .map(Pupil::toString)
                .collect(Collectors.joining("\n\t"));
    }
}
