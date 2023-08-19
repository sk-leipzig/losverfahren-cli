package org.skleipzig.losverfahrencli.logic;

import org.skleipzig.losverfahrencli.domain.Distribution;
import org.skleipzig.losverfahrencli.domain.ProjectGroup;
import org.skleipzig.losverfahrencli.domain.Pupil;

import java.util.List;

public class Distributor {
    Distribution distribute(List<ProjectGroup> projectGroups, List<Pupil> pupils) {
        Distribution distribution = Distribution.create(projectGroups, pupils)
                .assignByPreference();

        return distribution;
    }

}
