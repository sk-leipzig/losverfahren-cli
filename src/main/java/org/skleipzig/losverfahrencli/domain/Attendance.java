package org.skleipzig.losverfahrencli.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

import java.util.*;
import java.util.stream.Collectors;

import static org.skleipzig.losverfahrencli.domain.Pupil.pupilCollectionToString;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@CommonsLog
public class Attendance {
    @NonNull
    private final ProjectGroup projectGroup;
    @NonNull
    private final Set<Pupil> attendees;

    public boolean hasProjectGroup(ProjectGroup projectGroup) {
        return this.projectGroup.equals(projectGroup);
    }

    public int getAvailableSlots() {
        return projectGroup.getCapacity() - attendees.size();
    }

    /**
     * Weist dem Projekt so viele Schüler aus der Liste zu, wie möglich. Sind es zu viele Schüler, dann erfolgt die Auswahl zufällig.
     */
    public Attendance assignPupilsByVoteResult(Collection<Pupil> pupils) {
        log.debug("trying to assign " + Pupil.pupilCollectionToString(pupils) + " to project group " + projectGroup.getProjectName());
        List<Pupil> newPupils = new ArrayList<>(pupils);
        Collections.shuffle(newPupils);
        Set<Pupil> updatedAttendees = newPupils.stream()
                .filter(pupil -> !attendees.contains(pupil))
                .filter(pupil -> pupil.canAttend(projectGroup))
                .limit(getAvailableSlots())
                .collect(Collectors.toSet());
        String newAttendeeNames = pupilCollectionToString(updatedAttendees);
        updatedAttendees.addAll(attendees);
        Attendance result = new Attendance(projectGroup, updatedAttendees);
        if (log.isDebugEnabled()) {
            HashSet<Pupil> unAssignedPupils = new HashSet<>(pupils);
            unAssignedPupils.removeAll(updatedAttendees);
            log.debug(String.format("Assigned %s to project %s. Remaining capacity: %d. Pupils not assigned: %s", newAttendeeNames, projectGroup.getProjectName(), result.getAvailableSlots(), pupilCollectionToString(unAssignedPupils)));
        }
        return result;
    }

    public static Attendance createAttendance(ProjectGroup projectGroup) {
        return new Attendance(projectGroup, new HashSet<>());
    }

    public String toString() {
        String attendeeString = attendees.stream()
                .map(Pupil::toString)
                .collect(Collectors.joining("\n\t"));
        return "Teilnehmer der Projektgruppe " + projectGroup.getProjectName() + ":\n\t" + attendeeString;
    }
}
