package org.skleipzig.losverfahrencli.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.skleipzig.losverfahrencli.domain.ProjectGroupPreference.forProjectGroupWithName;

public class TestObjects {
    static final String GOETHE_NIEST = "Goethe niest";
    static final String GOETHE_DENKT = "Goethe denkt";
    static final String OHNE_GOETHE = "Ohne Goethe";

    static final Pupil STEFAN = new Pupil("Stefan", "Küttner", 7, "stefan@gmx.de");
    static final Pupil FRITZ = new Pupil("Fritz", "Müller", 8, "fritz@gmx.de");
    static final Pupil MONI = new Pupil("Monika", "Wallach", 7, "moni@gmx.de");
    static final Pupil FRANZI = new Pupil("Franziska", "Katz", 8, "franzi@gmx.de");
    static final Pupil CHILLER = new Pupil("Hans", "Söllner", 8, "chiller@gmx.de");
    static final Pupil CARL = new Pupil("Carl Heinz", "Meister", 6, "carl@gmx.de");
    static final Pupil LUMPI = new Pupil("Ludmilla", "Kolloczek", 9, "lumpi@gmx.de");
    static final List<Pupil> ALL_PUPILS = List.of(STEFAN, FRITZ, MONI, FRANZI, CHILLER, CARL, LUMPI);

    static PupilVoteResult createVoteResult(Pupil pupil, String... projectPreferences) {
        Map<Integer, ProjectGroupPreference> preferenceMap = new HashMap<>();
        for (int i = 0; i < projectPreferences.length; i++) {
            final int priority = i + 1;
            preferenceMap.put(priority, forProjectGroupWithName(priority, projectPreferences[i]));
        }
        return new PupilVoteResult(pupil, preferenceMap);
    }
}
