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

class PupilDTOTest {

    static final PupilDTO CORINA = new PupilDTO("Corina", "Küttner", "KL10", "corina@goethegym-leipzig.lernsax.de");
    static final PupilDTO STEFAN = new PupilDTO("Stefan", "Küttner", "KL11", "stefan@goethegym-leipzig.lernsax.de");
    static final PupilDTO ANNA = new PupilDTO("Anna", "Küttner", "KL9", "anna@goethegym-leipzig.lernsax.de");

    @Test
    void canReadSample() throws URISyntaxException, IOException {
        Path path = Paths.get(
                ClassLoader.getSystemResource("Schülerliste Projektwoche 2023.csv")
                        .toURI());
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<PupilDTO> cb = new CsvToBeanBuilder<PupilDTO>(reader)
                    .withType(PupilDTO.class)
                    .build();

            List<PupilDTO> actualPupils = cb.parse();
            assertEquals(List.of(CORINA, STEFAN, ANNA), actualPupils);
        }
    }

}