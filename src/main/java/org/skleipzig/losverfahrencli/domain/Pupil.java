package org.skleipzig.losverfahrencli.domain;

import lombok.Data;
import org.skleipzig.losverfahrencli.csv.PupilDTO;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Data
public class Pupil {
    private final String foreName;

    private final String name;

    private final int form;

    private final String emailAddress;

    public static Pupil fromDTO(PupilDTO pupilDTO) {
        return new Pupil(pupilDTO.getForeName(), pupilDTO.getName(), Integer.parseInt(pupilDTO.getForm()), pupilDTO.getEmailAddress());
    }

    public static Optional<Pupil> selectByEmail(List<Pupil> pupils, String emailAddress) {
        return pupils == null ? Optional.empty() : pupils.stream()
                .filter(pupil -> Objects.equals(pupil.getEmailAddress(), emailAddress))
                .findAny();
    }

    public boolean canAttend(ProjectGroup projectGroup) {
        return projectGroup.getMinForm() <= form && form <= projectGroup.getMaxForm();
    }

    public String toString() {
        return foreName + " " + name + ", Klassenstufe " + form + ", (" + emailAddress + ")";
    }

    static String pupilCollectionToString(Collection<Pupil> pupils) {
        return pupils == null || pupils.isEmpty() ? "N/A" : collectionToDelimitedString(pupils, ", ");
    }


}
