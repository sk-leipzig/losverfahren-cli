package org.skleipzig.losverfahrencli;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.skleipzig.losverfahrencli.domain.Distribution;
import org.skleipzig.losverfahrencli.domain.ProjectGroup;
import org.skleipzig.losverfahrencli.domain.Pupil;
import org.skleipzig.losverfahrencli.domain.PupilVoteResult;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Component
@Getter
@Setter
@ShellComponent
@CommonsLog
public class Context {
    private List<ProjectGroup> projectGroups = new ArrayList<>();
    private List<Pupil> pupils = new ArrayList<>();
    private List<PupilVoteResult> voteResults = new ArrayList<>();
    private Distribution distribution = Distribution.create(emptyList(), emptyList());

    @ShellMethod(key = "listPupils", group = "context", value = "Schülerliste anzeigen")
    public String listPupils() {
        return "Schülerliste:\n" + collectionToDelimitedString(pupils, "\n", "\t", "") + "\n" + pupils.size() + " Schüler\n";
    }

    @ShellMethod(key = "listProjectGroups", group = "context", value = "Projektgruppen anzeigen")
    public String listProjectGroups() {
        return "Projektgruppen:\n" + collectionToDelimitedString(projectGroups, "\n", "\t", "") + "\n" + projectGroups.size() + " Projektgruppen\n";
    }

    @ShellMethod(key = "listVotings", group = "context", value = "Umfrageergebisse anzeigen")
    public String listVotings() {
        return "Schülerauswahl:\n" + collectionToDelimitedString(voteResults, "\n", "\t", "") + "\n" + voteResults.size() + " Abstimmungsergebnisse\n";
    }

    @ShellMethod(key = "listAll", group = "context", value = "Alle gespeicherten Daten anzeigen")
    public String listAll() {
        return listPupils() + "\n\n" + listProjectGroups() + "\n\n" + listVotings();
    }
}
