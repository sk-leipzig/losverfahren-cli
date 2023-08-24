package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.Test;
import org.skleipzig.losverfahrencli.domain.ProjectGroup;
import org.skleipzig.losverfahrencli.domain.Pupil;
import org.skleipzig.losverfahrencli.domain.PupilVoteResult;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PupilVoteResultDTOTest {
    private static final PupilVoteResultDTO CORINA = new PupilVoteResultDTO("corina@goethegym-leipzig.lernsax.de", "10", "Goethe-Time", "Wandern weitet den Blick", "Goethe blickt nach innen");
    private static final PupilVoteResultDTO STEFAN = new PupilVoteResultDTO("stefan@goethegym-leipzig.lernsax.de", "11", "Goethe als Spieler", "Goethe als Spieler", "Goethe als Spieler");
    private static final PupilVoteResultDTO ANNA = new PupilVoteResultDTO("anna@goethegym-leipzig.lernsax.de", "9", "Goethe-Time", "Goethe als Spieler", "Goethe in Leipzig");
    private static final List<ProjectGroup> PROJECT_GROUPS = createProjectGroups("Goethe-Time", "Wandern weitet den Blick", "Goethe blickt nach innen");

    @Test
    void canReadSample() throws URISyntaxException, IOException {
        Path path = Paths.get(
                ClassLoader.getSystemResource("Einschreibung Projektwoche 2023.csv")
                        .toURI());
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<PupilVoteResultDTO> cb = new CsvToBeanBuilder<PupilVoteResultDTO>(reader)
                    .withType(PupilVoteResultDTO.class)
                    .withSeparator(';')
                    .build();

            List<PupilVoteResultDTO> actualVoteResults = cb.parse();
            assertEquals(List.of(CORINA, STEFAN, ANNA), actualVoteResults);
        }
    }

    @Test
    void createPupilFromVoteResultIfMissing() {
        PupilVoteResult pupilVoteResult = PupilVoteResult.fromDTO(CORINA, List.of(), PROJECT_GROUPS)
                .get();
        assertAll(() -> assertEquals(10, pupilVoteResult.getPupil()
                        .getForm()),
                () -> assertEquals(CORINA.getLogin(), pupilVoteResult.getPupil()
                        .getEmailAddress()));
    }

    @Test
    void doNotDuplicatePupils() {
        Pupil corina = new Pupil("Corina", "Küttner", 10, CORINA.getLogin());

        PupilVoteResult pupilVoteResult = PupilVoteResult.fromDTO
                        (CORINA, List.of(corina), createProjectGroups("Goethe-Time", "Wandern weitet den Blick", "Goethe blickt nach innen"))
                .get();
        assertEquals(corina, pupilVoteResult.getPupil());
    }

    private static List<ProjectGroup> createProjectGroups(String... projectNames) {
        return Arrays.stream(projectNames)
                .map(projectName -> new ProjectGroup(projectName, 0, 99, 1))
                .toList();
    }

}