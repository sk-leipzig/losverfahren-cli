package org.skleipzig.losverfahrencli;

import lombok.extern.apachecommons.CommonsLog;
import org.skleipzig.losverfahrencli.domain.Distribution;
import org.skleipzig.losverfahrencli.domain.ProjectGroup;
import org.skleipzig.losverfahrencli.domain.Pupil;
import org.skleipzig.losverfahrencli.domain.PupilVoteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
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

        int numThreads = 10;
        int iterationsPerThread = 10000 / numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Distribution>> futures = new ArrayList<>();

        for (int t = 0; t < numThreads; t++) {
            futures.add(executor.submit(new DistributionTask(context.getProjectGroups(), allVotes, iterationsPerThread)));
        }

        Distribution bestDistribution = null;
        try {
            for (Future<Distribution> future : futures) {
                Distribution distribution = future.get();
                if (bestDistribution == null || bestDistribution.getNumberOfZugelost() > distribution.getNumberOfZugelost()) {
                    bestDistribution = distribution;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while waiting for distribution tasks", e);
        } finally {
            executor.shutdown();
        }


        context.setDistribution(bestDistribution);
        return bestDistribution != null ? bestDistribution.createResultString() : null;
    }

    static class DistributionTask implements Callable<Distribution> {
        private final List<ProjectGroup> projectGroups;
        private final List<PupilVoteResult> allVotes;
        private final int iterations;

        public DistributionTask(List<ProjectGroup> projectGroups, List<PupilVoteResult> allVotes, int iterations) {
            this.projectGroups = projectGroups;
            this.allVotes = allVotes;
            this.iterations = iterations;
        }

        @Override
        public Distribution call() {
            Distribution bestDistribution = null;
            for (int i = 0; i < iterations && (bestDistribution == null || bestDistribution.getNumberOfZugelost() > 0); i++) {
                Distribution distribution = Distribution.create(projectGroups, allVotes)
                        .assignAllPupils();
                if (bestDistribution == null || bestDistribution.getNumberOfZugelost() > distribution.getNumberOfZugelost()) {
                    bestDistribution = distribution;
                }
                log.debug(Thread.currentThread()
                        .getName() + " - " + i + ": " + bestDistribution.getNumberOfZugelost());
            }
            return bestDistribution;
        }
    }
}
