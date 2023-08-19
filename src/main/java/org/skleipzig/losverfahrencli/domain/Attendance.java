package org.skleipzig.losverfahrencli.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
    public Attendance assignPupils(Collection<Pupil> pupils) {
        List<Pupil> newPupils = new ArrayList<>(pupils);
        Collections.shuffle(newPupils);
        Set<Pupil> updatedAttendees = newPupils.stream()
                .filter(pupil -> !attendees.contains(pupil))
                .filter(pupil -> pupil.canAttend(projectGroup))
                .limit(getAvailableSlots())
                .collect(Collectors.toSet());
        updatedAttendees.addAll(attendees);
        return new Attendance(projectGroup, updatedAttendees);
    }

    public static Attendance createAttendance(ProjectGroup projectGroup) {
        return new Attendance(projectGroup, new HashSet<>());
    }
}
