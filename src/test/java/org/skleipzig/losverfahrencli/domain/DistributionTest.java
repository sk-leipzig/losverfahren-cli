package org.skleipzig.losverfahrencli.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.skleipzig.losverfahrencli.domain.TestObjects.*;

class DistributionTest {

    private ProjectGroup goetheDenkt;
    private ProjectGroup goetheNiest;
    private ProjectGroup ohneGoethe;

    @BeforeEach
    void setUp() {
        goetheDenkt = new ProjectGroup(GOETHE_DENKT, 6, 7, 3);
        ohneGoethe = new ProjectGroup(GOETHE_NIEST, 7, 8, 3);
        goetheNiest = new ProjectGroup(OHNE_GOETHE, 9, 10, 1);
    }

    @Test
    void allPupilsAssigned() {
        List<Attendance> attendances = Distribution.create(List.of(goetheNiest, goetheDenkt, ohneGoethe), ALL_PUPILS)
                .assignAllPupils()
                .getAttendances();
        assertThat("Testsetup error", ALL_PUPILS, containsInAnyOrder(STEFAN, FRITZ, MONI, FRANZI, CHILLER, CARL, LUMPI));
        assertAll(
                () -> assertPupilAssigned(attendances, STEFAN),
                () -> assertPupilAssigned(attendances, FRITZ),
                () -> assertPupilAssigned(attendances, MONI),
                () -> assertPupilAssigned(attendances, FRANZI),
                () -> assertPupilAssigned(attendances, CHILLER),
                () -> assertPupilAssigned(attendances, CARL),
                () -> assertPupilAssigned(attendances, LUMPI)
        );

    }

    private void assertPupilAssigned(List<Attendance> attendances, Pupil pupil) {
        assertTrue(attendances.stream()
                        .anyMatch(attendance -> attendance.getAttendees().contains(pupil)),
                pupil + " not assigned!");
    }
}