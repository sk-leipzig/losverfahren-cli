package org.skleipzig.losverfahrencli;

import lombok.extern.apachecommons.CommonsLog;
import org.skleipzig.losverfahrencli.domain.Distribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@CommonsLog
public class Distributor {

    @Autowired
    private Context context;

    @ShellMethod(key = "distributePupils", group = "distribute", value = "Schüler auf Projektgruppen verteilen")
    public String distributePupils() {
        Distribution distribution = Distribution.create(context.getProjectGroups(), context.getVoteResults())
                .assignAllPupils();
        context.setDistribution(distribution);

        return distribution.createResultString();
    }

}
