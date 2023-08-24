package org.skleipzig.losverfahrencli;

import lombok.extern.apachecommons.CommonsLog;
import org.skleipzig.losverfahrencli.domain.Distribution;
import org.skleipzig.losverfahrencli.domain.Pupil;
import org.skleipzig.losverfahrencli.domain.PupilVoteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ShellComponent
@CommonsLog
public class Distributor {

    @Autowired
    private Context context;

    @ShellMethod(key = "distributePupils", group = "distribute", value = "Schüler auf Projektgruppen verteilen")
    public String distributePupils() {
        Set<Pupil> pupilsWithVotes = context.getVoteResults()
                .stream()
                .map(PupilVoteResult::getPupil)
                .collect(Collectors.toSet());
        Set<PupilVoteResult> replacementVotes = context.getPupils()
                .stream()
                .filter(pupil -> !pupilsWithVotes.contains(pupil))
                .map(pupil -> new PupilVoteResult(pupil, new HashMap<>()))
                .collect(Collectors.toSet());
        log.debug(replacementVotes.size() + " pupils without votes: " + replacementVotes.stream()
                .map(PupilVoteResult::getPupil)
                .map(Pupil::toString)
                .collect(Collectors.joining(",")));
        List<PupilVoteResult> allVotes = Stream.concat(context.getVoteResults()
                        .stream(), replacementVotes.stream())
                .toList();
        Distribution distribution;
        int i = 0;
        do {
            i++;
            distribution = Distribution.create(context.getProjectGroups(), allVotes)
                    .assignAllPupils();
        } while (i < 10000 && !distribution.getOpenVotes()
                .isEmpty());

        context.setDistribution(distribution);

        return distribution.createResultString();
    }

}
