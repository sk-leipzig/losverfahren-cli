package org.skleipzig.losverfahrencli;

import lombok.Getter;
import lombok.Setter;
import org.skleipzig.losverfahrencli.domain.ProjectGroup;
import org.skleipzig.losverfahrencli.domain.Pupil;
import org.skleipzig.losverfahrencli.domain.PupilVoteResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
public class Context {
    private List<ProjectGroup> projectGroups = new ArrayList<>();
    private List<Pupil> pupils = new ArrayList<>();
    private List<PupilVoteResult> voteResults = new ArrayList<>();
}
