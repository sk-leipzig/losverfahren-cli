package org.skleipzig.losverfahrencli.domain;

import org.skleipzig.losverfahrencli.domain.Pupil.ProjectGroupPreference;

import java.util.List;

import static org.skleipzig.losverfahrencli.domain.Pupil.ProjectGroupPreference.NONE;
import static org.skleipzig.losverfahrencli.domain.Pupil.ProjectGroupPreference.forProjectGroupWithName;

public class TestObjects {
    static final String GOETHE_NIEST = "Goethe niest";
    static final String GOETHE_DENKT = "Goethe denkt";
    static final String OHNE_GOETHE = "Ohne Goethe";

    static final Pupil STEFAN = createPupil("stefan@gmx.de", 7, GOETHE_DENKT, GOETHE_NIEST, OHNE_GOETHE);
    static final Pupil FRITZ = createPupil("fritz@gmx.de", 8, GOETHE_NIEST, GOETHE_NIEST, GOETHE_NIEST);
    static final Pupil MONI = createPupil("moni@gmx.de", 7, GOETHE_NIEST);
    static final Pupil FRANZI = createPupil("franzi@gmx.de", 8, GOETHE_DENKT, GOETHE_NIEST);
    static final Pupil CHILLER = createPupil("chiller@gmx.de", 8);
    static final Pupil CARL = createPupil("carl@gmx.de", 6);
    static final Pupil LUMPI = createPupil("lumpi@gmx.de", 9, OHNE_GOETHE);
    static final List<Pupil> ALL_PUPILS = List.of(STEFAN, FRITZ, MONI, FRANZI, CHILLER, CARL, LUMPI);

    private static Pupil createPupil(String mail, int form, String... projectPreferences) {
        ProjectGroupPreference firstPref = projectPreferences.length > 0 ? forProjectGroupWithName(projectPreferences[0]) : NONE;
        ProjectGroupPreference secondPref = projectPreferences.length > 1 ? forProjectGroupWithName(projectPreferences[1]) : NONE;
        ProjectGroupPreference thirdPref = projectPreferences.length > 2 ? forProjectGroupWithName(projectPreferences[2]) : NONE;
        return new Pupil(mail, form, firstPref, secondPref, thirdPref);
    }
}
