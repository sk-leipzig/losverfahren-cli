package org.skleipzig.losverfahrencli.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.skleipzig.losverfahrencli.domain.Attendance.createAttendance;
import static org.skleipzig.losverfahrencli.domain.TestObjects.*;

class AttendanceTest {

    private ProjectGroup testProject;

    @BeforeEach
    void setUp() {
        testProject = new ProjectGroup("Testprojekt", 6, 8, 3);
    }

    @Test
    void doNotAssignLumpi() {
        Attendance result = createAttendance(testProject).assignPupilsByVoteResult(List.of(STEFAN, LUMPI));
        assertThat(result.getAttendees(), contains(STEFAN));
    }

    @Test
    void doNotAssignMoreThanCapacity() {
        Attendance result = createAttendance(testProject).assignPupilsByVoteResult(ALL_PUPILS);
        assertEquals(3, result.getAttendees()
                .size());
    }

    @Test
    void doNotDoubleAssignPupil() {
        Attendance result = createAttendance(testProject).assignPupilsByVoteResult(List.of(STEFAN, STEFAN, STEFAN));
        assertEquals(2, result.getAvailableSlots());
    }

    @Test
    void assignDifferentPupils() {
        long numberOfDistinctAttendees = createAttendance(testProject).assignPupilsByVoteResult(ALL_PUPILS)
                .getAttendees()
                .stream()
                .distinct()
                .count();
        assertEquals(3, numberOfDistinctAttendees);
    }

    @Test
    void noAvailableSlotsAfterAssignment() {
        Attendance result = createAttendance(testProject).assignPupilsByVoteResult(ALL_PUPILS);
        assertEquals(0, result.getAvailableSlots());
    }


}