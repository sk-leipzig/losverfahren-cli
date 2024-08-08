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

    private static final PupilVoteResult V_STEFAN = createVoteResult(STEFAN, GOETHE_DENKT, GOETHE_NIEST, OHNE_GOETHE);
    private static final PupilVoteResult V_FRITZ = createVoteResult(FRITZ, GOETHE_NIEST, GOETHE_NIEST, GOETHE_NIEST);
    private static final PupilVoteResult V_MONI = createVoteResult(MONI, GOETHE_NIEST);
    private static final PupilVoteResult V_FRANZI = createVoteResult(FRANZI, GOETHE_DENKT, GOETHE_NIEST);
    private static final PupilVoteResult V_CHILLER = createVoteResult(CHILLER);
    private static final PupilVoteResult V_CARL = createVoteResult(CARL);
    private static final PupilVoteResult V_LUMPI = createVoteResult(LUMPI, OHNE_GOETHE);

    private static final List<PupilVoteResult> ALL_VOTINGS = List.of(V_STEFAN, V_FRITZ, V_MONI, V_FRANZI, V_CHILLER, V_CARL, V_LUMPI);

    @BeforeEach
    void setUp() {
        goetheDenkt = new ProjectGroup(GOETHE_DENKT, 6, 7, 3);
        ohneGoethe = new ProjectGroup(GOETHE_NIEST, 7, 8, 3);
        goetheNiest = new ProjectGroup(OHNE_GOETHE, 9, 10, 1);
    }

    @Test
    void allPupilsAssigned() {
        List<Attendance> attendances = Distribution.create(List.of(goetheNiest, goetheDenkt, ohneGoethe), ALL_VOTINGS)
                .assignAllPupils()
                .getAttendances();
        assertThat("Testsetup error", ALL_PUPILS, containsInAnyOrder(STEFAN, FRITZ, MONI, FRANZI, CHILLER, CARL, LUMPI));
        assertAll(
                () -> assertPupilAssigned(attendances, STEFAN),
                () -> assertPupilAssigned(attendances, FRITZ),
                () -> assertPupilAssigned(attendances, MONI),
                () -> assertPupilAssigned(attendances, FRANZI),
                // () -> assertPupilAssigned(attendances, CHILLER),
                () -> assertPupilAssigned(attendances, CARL),
                () -> assertPupilAssigned(attendances, LUMPI)
        );

    }

    private void assertPupilAssigned(List<Attendance> attendances, Pupil pupil) {
        assertTrue(attendances.stream()
                        .anyMatch(attendance -> attendance.getAttendees()
                                .containsKey(pupil)),
                pupil + " not assigned!");
    }
}