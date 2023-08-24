package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.skleipzig.losverfahrencli.csv.PupilDTOTest.*;
import static org.skleipzig.losverfahrencli.domain.AssignmentType.*;

class PupilAssignmentDTOTest {

    static final PupilAssignmentDTO CORINA_ASSIGNMENT = PupilAssignmentDTO.create(CORINA, "Goethe-Time", Erstwunsch);
    static final PupilAssignmentDTO STEFAN_ASSIGNMENT = PupilAssignmentDTO.create(STEFAN, "Goethe als Spieler", Zweitwunsch);
    static final PupilAssignmentDTO ANNA_ASSIGNMENT = PupilAssignmentDTO.create(ANNA, "Goethe-Time", Zugelost);

    @Test
    void canReadSample() throws URISyntaxException, IOException {
        Path path = Paths.get(
                ClassLoader.getSystemResource("Zuteilungsliste Projektwoche 2023.csv")
                        .toURI());
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<PupilAssignmentDTO> cb = new CsvToBeanBuilder<PupilAssignmentDTO>(reader)
                    .withType(PupilAssignmentDTO.class)
                    .build();

            List<PupilAssignmentDTO> actualAssignments = cb.parse();
            assertEquals(List.of(CORINA_ASSIGNMENT, STEFAN_ASSIGNMENT, ANNA_ASSIGNMENT), actualAssignments);
        }
    }
}