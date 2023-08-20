package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.apachecommons.CommonsLog;
import org.skleipzig.losverfahrencli.Context;
import org.skleipzig.losverfahrencli.domain.ProjectGroup;
import org.skleipzig.losverfahrencli.domain.Pupil;
import org.skleipzig.losverfahrencli.domain.PupilVoteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

@ShellComponent
@CommonsLog
public class CsvReader {

    static final String HINT_FILE_NAME = "Wenn die Datei nicht im aktuellen Ordner liegt, muss der komplette Pfad angegeben werden. Achtung: Sind Leerzeichen enthalten, muss der Parameter in \"\" eingeschlossen werden!";
    static final String HELP_TXT_PUPILS_FILE_NAME = "Name der CSV Datei mit der Schülerliste aus dem SAX. " + HINT_FILE_NAME;
    static final String HELP_TXT_PROJECTS_FILE_NAME = "Name der CSV Datei mit den Projektgruppen. " + HINT_FILE_NAME;
    static final String HELP_TXT_VOTINGS_FILE_NAME = "Name der CSV Datei mit den Abstimmungsergebnissen. " + HINT_FILE_NAME;
    static final String WARNING_NO_PROJECT_GROUPS = "Warnung: Es wurden keine Projektgruppen gefunden! Die Abstimmungsergebnisse können erst ausgewertet werden, wenn die Projektgruppen eingelesen wurden!";
    static final String WARNING_NO_PUPILS = "Warnung: Es wurden keine Schüler gefunden! Votings von Schülern, die nicht importiert wurden, werden ignoriert!";

    @Autowired
    private Context context;

    @ShellMethod(key = "readPupils")
    public String readPupils(@ShellOption(help = HELP_TXT_PUPILS_FILE_NAME) String pupilsFileName,
                             @ShellOption(defaultValue = ",", help = "Trennzeichen") String separator) {
        try (Reader reader = Files.newBufferedReader(Paths.get(pupilsFileName))) {
            CsvToBean<PupilDTO> cb = new CsvToBeanBuilder<PupilDTO>(reader)
                    .withType(PupilDTO.class)
                    .withSeparator(separator.charAt(0))
                    .build();

            List<Pupil> pupils = cb.parse()
                    .stream()
                    .map(Pupil::fromDTO)
                    .toList();
            context.setPupils(pupils);

            return "Schülerliste:\n" + collectionToDelimitedString(pupils, "\n", "\t", "");
        } catch (IOException e) {
            return errorReadingFile(pupilsFileName, e);
        }
    }

    @ShellMethod(key = "readProjects")
    public String readProjects(@ShellOption(help = HELP_TXT_PROJECTS_FILE_NAME) String projectsFileName,
                               @ShellOption(defaultValue = ",", help = "Trennzeichen") String separator) {
        try (Reader reader = Files.newBufferedReader(Paths.get(projectsFileName))) {
            CsvToBean<ProjectGroupDTO> cb = new CsvToBeanBuilder<ProjectGroupDTO>(reader)
                    .withType(ProjectGroupDTO.class)
                    .withSeparator(separator.charAt(0))
                    .build();

            List<ProjectGroup> projects = cb.parse()
                    .stream()
                    .map(ProjectGroup::fromDTO)
                    .toList();
            context.setProjectGroups(projects);

            return "Projektliste:\n" + collectionToDelimitedString(projects, "\n", "\t", "");
        } catch (IOException e) {
            return errorReadingFile(projectsFileName, e);
        }
    }

    @ShellMethod(key = "readVotings")
    public String readVotings(@ShellOption(help = HELP_TXT_VOTINGS_FILE_NAME) String votingsFileName,
                              @ShellOption(defaultValue = ";", help = "Trennzeichen") String separator) {
        try (Reader reader = Files.newBufferedReader(Paths.get(votingsFileName))) {
            CsvToBean<PupilVoteResultDTO> cb = new CsvToBeanBuilder<PupilVoteResultDTO>(reader)
                    .withType(PupilVoteResultDTO.class)
                    .withSeparator(separator.charAt(0))
                    .build();

            List<PupilVoteResult> voteResults = cb.parse()
                    .stream()
                    .flatMap(pupilVoteResultDTO -> PupilVoteResult.fromDTO(pupilVoteResultDTO, context.getPupils(), context.getProjectGroups()).stream())
                    .toList();
            context.setVoteResults(voteResults);

            if (context.getPupils().isEmpty()) {
                System.err.println(WARNING_NO_PUPILS);
            }

            if (context.getProjectGroups().isEmpty()) {
                System.err.println(WARNING_NO_PROJECT_GROUPS);
            }

            return "Schülerauswahl:\n" + collectionToDelimitedString(voteResults, "\n", "\t", "");
        } catch (IOException e) {
            return errorReadingFile(votingsFileName, e);
        }
    }

    private static String errorReadingFile(String projectsFileName, IOException e) {
        log.error("Error reading file " + projectsFileName, e);
        return "Konnte " + projectsFileName + " nicht einlesen: Datei nicht gefunden oder nicht lesbar.";
    }
}
