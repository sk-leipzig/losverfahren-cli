package org.skleipzig.losverfahrencli.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static org.skleipzig.losverfahrencli.domain.Pupil.pupilCollectionToString;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@CommonsLog
public class Attendance {
    @NonNull
    private final ProjectGroup projectGroup;
    @NonNull
    private final Map<Pupil, AssignmentType> attendees;

    public boolean hasProjectGroup(ProjectGroup projectGroup) {
        return this.projectGroup.equals(projectGroup);
    }

    public int getAvailableSlots() {
        return projectGroup.getCapacity() - attendees.size();
    }

    /**
     * Weist dem Projekt so viele Schüler aus der Liste zu, wie möglich. Sind es zu viele Schüler, dann erfolgt die Auswahl zufällig.
     */
    public Attendance assignPupils(Collection<Pupil> pupils, AssignmentType assignmentType) {
        log.debug("trying to assign " + Pupil.pupilCollectionToString(pupils) + " to project group " + projectGroup.getProjectName());
        List<Pupil> newPupils = new ArrayList<>(new HashSet<>(pupils));
        Collections.shuffle(newPupils);
        Map<Pupil, AssignmentType> updatedAttendees = newPupils.stream()
                .filter(pupil -> !attendees.containsKey(pupil))
                .filter(pupil -> pupil.canAttend(projectGroup))
                .limit(getAvailableSlots())
                .collect(Collectors.toMap(identity(), pupil -> assignmentType));
        String newAttendeeNames = pupilCollectionToString(updatedAttendees.keySet());
        updatedAttendees.putAll(attendees);
        Attendance result = new Attendance(projectGroup, updatedAttendees);
        if (log.isDebugEnabled()) {
            HashSet<Pupil> unAssignedPupils = new HashSet<>(pupils);
            unAssignedPupils.removeAll(updatedAttendees.keySet());
            log.debug(String.format("Assigned %s to project %s with assignment type %s. Remaining capacity: %d. Pupils not assigned: %s", newAttendeeNames, projectGroup.getProjectName(), assignmentType, result.getAvailableSlots(), pupilCollectionToString(unAssignedPupils)));
        }
        return result;
    }

    public static Attendance createAttendance(ProjectGroup projectGroup) {
        return new Attendance(projectGroup, new HashMap<>());
    }

    public RemainingSeats getRemainingSeats() {
        return new RemainingSeats(projectGroup, getAvailableSlots());
    }

    public String toString() {
        String attendeeString = attendees
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + " [" + entry.getValue() + "]")
                .collect(Collectors.joining("\n\t"));
        return "Teilnehmer der Projektgruppe " + projectGroup.getProjectName() + ":\n\t" + attendeeString;
    }
}
