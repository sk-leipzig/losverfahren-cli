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

class PupilVoteResultDTOTest {
    private static final PupilVoteResultDTO CORINA = new PupilVoteResultDTO("corina@goethegym-leipzig.lernsax.de", "10", "Goethe-Time", "Wandern weitet den Blick", "Goethe blickt nach innen");
    private static final PupilVoteResultDTO STEFAN = new PupilVoteResultDTO("stefan@goethegym-leipzig.lernsax.de", "11", "Goethe als Spieler", "Goethe als Spieler", "Goethe als Spieler");
    private static final PupilVoteResultDTO ANNA = new PupilVoteResultDTO("anna@goethegym-leipzig.lernsax.de", "9", "Goethe-Time", "Goethe als Spieler", "Goethe in Leipzig");

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

}