package org.skleipzig.losverfahrencli.domain;

import lombok.Data;
import org.skleipzig.losverfahrencli.csv.PupilDTO;
import org.skleipzig.losverfahrencli.csv.PupilVoteResultDTO;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Data
public class Pupil {
    static final int VKA_FORM = -1;

    private final String foreName;

    private final String name;

    private final int form;

    private final String emailAddress;

    public static Optional<Pupil> fromDTO(PupilDTO pupilDTO) {
        if (isEmpty(pupilDTO.getForm()) || isEmpty(pupilDTO.getEmailAddress())) {
            System.err.println("Ungültige Schülerdaten (ignoriert): " + pupilDTO);
            return Optional.empty();
        }
        return Optional.of(new Pupil(pupilDTO.getForeName(), pupilDTO.getName(), parseForm(pupilDTO.getForm()), pupilDTO.getEmailAddress()));
    }

    public static Optional<Pupil> fromPupilVoteResultDTO(PupilVoteResultDTO pupilVoteResultDTO) {
        if (isEmpty(pupilVoteResultDTO.getForm()) || isEmpty(pupilVoteResultDTO.getLogin())) {
            System.err.println("Ungültige Schülerdaten (ignoriert): " + pupilVoteResultDTO);
            return Optional.empty();
        }
        return Optional.of(new Pupil("N/A", "N/A", parseForm(pupilVoteResultDTO.getForm()), pupilVoteResultDTO.getLogin()));
    }

    private static int parseForm(String formString) {
        if (isEmpty(formString)) {
            return 0;
        } else if (formString.startsWith("KL") || formString.startsWith("JG")) {
            return Integer.parseInt(formString.substring(2));
        } else if (formString.equalsIgnoreCase("VKA")) {
            return VKA_FORM;
        } else
            return Integer.parseInt(formString.trim());
    }

    public static Optional<Pupil> selectByEmail(List<Pupil> pupils, String emailAddress) {
        return pupils == null ? Optional.empty() : pupils.stream()
                .filter(pupil -> Objects.equals(pupil.getEmailAddress(), emailAddress))
                .findAny();
    }

    public boolean canAttend(ProjectGroup projectGroup) {
        return form == VKA_FORM || projectGroup.getMinForm() <= form && form <= projectGroup.getMaxForm();
    }

    public PupilDTO toDTO() {
        return new PupilDTO(foreName, name, formToString(), emailAddress);
    }

    public String toString() {
        return foreName + " " + name + ", Klassenstufe " + formToString() + ", (" + emailAddress + ")";
    }

    private String formToString() {
        return form == 0 ? "N/A" : form == VKA_FORM ? "VKA" : Integer.toString(form);
    }

    static String pupilCollectionToString(Collection<Pupil> pupils) {
        return pupils == null || pupils.isEmpty() ? "N/A" : collectionToDelimitedString(pupils, ", ");
    }


}
