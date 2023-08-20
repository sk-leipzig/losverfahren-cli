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

class ProjectGroupDTOTest {

    private static final ProjectGroupDTO GOETHE_TIME = new ProjectGroupDTO("Goethe-Time", "8-11", "15");
    private static final ProjectGroupDTO GOETHE_IN_LEIPZIG = new ProjectGroupDTO("Goethe in Leipzig", "8-11", "40");
    private static final ProjectGroupDTO GOETHE_ALS_SPIELER = new ProjectGroupDTO("Goethe als Spieler", "6-11", "75");
    private static final ProjectGroupDTO WANDERN_WEITET = new ProjectGroupDTO("Wandern weitet den Blick", "7-9", "25");
    private static final ProjectGroupDTO GOETHE_NACH_INNEN = new ProjectGroupDTO("Goethe blickt nach innen", "10", "15");

    @Test
    void canReadSample() throws URISyntaxException, IOException {
        Path path = Paths.get(
                ClassLoader.getSystemResource("Projektgruppen Projektwoche 2023.csv")
                        .toURI());
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<ProjectGroupDTO> cb = new CsvToBeanBuilder<ProjectGroupDTO>(reader)
                    .withType(ProjectGroupDTO.class)
                    .build();

            List<ProjectGroupDTO> actualProjects = cb.parse();
            assertEquals(List.of(GOETHE_TIME, GOETHE_IN_LEIPZIG, GOETHE_ALS_SPIELER, WANDERN_WEITET, GOETHE_NACH_INNEN), actualProjects);
        }
    }

    @Test
    void getMinForm() {
        assertEquals(8, GOETHE_IN_LEIPZIG.getMinForm());
    }

    @Test
    void getMaxForm() {
        assertEquals(11, GOETHE_IN_LEIPZIG.getMaxForm());
    }

}