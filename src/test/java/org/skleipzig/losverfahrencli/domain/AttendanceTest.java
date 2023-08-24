package org.skleipzig.losverfahrencli.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.skleipzig.losverfahrencli.domain.AssignmentType.Erstwunsch;
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
        Attendance result = createAttendance(testProject).assignPupils(List.of(STEFAN, LUMPI), Erstwunsch);
        assertThat(result.getAttendees()
                .keySet(), contains(STEFAN));
    }

    @Test
    void doNotAssignMoreThanCapacity() {
        Attendance result = createAttendance(testProject).assignPupils(ALL_PUPILS, Erstwunsch);
        assertEquals(3, result.getAttendees()
                .size());
    }

    @Test
    void doNotDoubleAssignPupil() {
        Attendance result = createAttendance(testProject).assignPupils(List.of(STEFAN, STEFAN, STEFAN), Erstwunsch);
        assertEquals(2, result.getAvailableSlots());
    }

    @Test
    void assignDifferentPupils() {
        long numberOfDistinctAttendees = createAttendance(testProject).assignPupils(ALL_PUPILS, Erstwunsch)
                .getAttendees()
                .keySet()
                .stream()
                .distinct()
                .count();
        assertEquals(3, numberOfDistinctAttendees);
    }

    @Test
    void noAvailableSlotsAfterAssignment() {
        Attendance result = createAttendance(testProject).assignPupils(ALL_PUPILS, Erstwunsch);
        assertEquals(0, result.getAvailableSlots());
    }


}