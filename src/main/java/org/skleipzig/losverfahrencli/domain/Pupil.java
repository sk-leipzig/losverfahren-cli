package org.skleipzig.losverfahrencli.domain;

import lombok.Data;
import org.skleipzig.losverfahrencli.csv.PupilDTO;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Data
public class Pupil {
    public static final List<Function<Pupil, ProjectGroupPreference>> PREFERENCE_DEFINITIONS =
            Arrays.asList(Pupil::getPrimaryPreference, Pupil::getSecondaryPreference, Pupil::getTertiaryPreference);

    private final String emailAddress;

    private final int form;

    private final ProjectGroupPreference primaryPreference;

    private final ProjectGroupPreference secondaryPreference;

    private final ProjectGroupPreference tertiaryPreference;

    public static Pupil fromDTO(PupilDTO pupilDTO, List<ProjectGroup> availableProjectGroups) {
        return new Pupil(pupilDTO.getEmailAddress(),
                Integer.parseInt(pupilDTO.getForm()),
                createPreference(pupilDTO.getPrimaryChoice(), availableProjectGroups),
                createPreference(pupilDTO.getSecondaryChoice(), availableProjectGroups),
                createPreference(pupilDTO.getTertiaryChoice(), availableProjectGroups)
        );
    }

    public boolean canAttend(ProjectGroup projectGroup) {
        return projectGroup.getMinForm() <= form && form <= projectGroup.getMaxForm();
    }

    private static ProjectGroupPreference createPreference(String projectName, List<ProjectGroup> availableProjectGroups) {
        return ProjectGroup.selectByName(availableProjectGroups, projectName)
                .map(ProjectGroupPreference::forProjectGroup)
                .orElse(ProjectGroupPreference.NONE);
    }

    public interface ProjectGroupPreference extends Predicate<ProjectGroup> {
        ProjectGroupPreference NONE = any -> false;

        static ProjectGroupPreference forProjectGroup(ProjectGroup projectGroup) {
            return projectGroup::equals;
        }
    }

}
