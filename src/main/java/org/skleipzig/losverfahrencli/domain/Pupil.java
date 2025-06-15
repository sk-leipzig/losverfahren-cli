package org.skleipzig.losverfahrencli.domain;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.skleipzig.losverfahrencli.csv.PupilDTO;
import org.skleipzig.losverfahrencli.csv.PupilVoteResultDTO;

import java.text.Normalizer;
import java.util.*;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Data
public class Pupil {
    static final int VKA_FORM = -1;
    static final String GOETHEGYM_LEIPZIG_LERNSAX_DE = "@goethegym-leipzig.lernsax.de";

    private final String foreName;
    private final String name;
    private final int form;
    private final String emailAddress;

    public Pupil(String foreNames, String names, int form, String emailAddress) {
        this.foreName = foreNames;
        this.name = names;
        this.form = form;
        this.emailAddress = !Strings.isEmpty(emailAddress) ? emailAddress : generateEmail(foreNames, names);
    }

    public static Optional<Pupil> fromDTO(PupilDTO pupilDTO) {
        if (isEmpty(pupilDTO.getForm())) {
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
            return Integer.parseInt(formString.split("\\. ")[0]);
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

    private static String generateEmail(String foreName, String name) {
        String[] foreNames = foreName.split(" ");
        String[] names = name.split(" ");

        String firstForeName = foreNames[0];
        String allLastNames = String.join("", names);

        String rawEmail = (firstForeName + "." + allLastNames).toLowerCase(Locale.ROOT)
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss");
        String normalizedEmail = Normalizer.normalize(rawEmail, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // Entfernt diakritische Zeichen

        String emailAddress = normalizedEmail + GOETHEGYM_LEIPZIG_LERNSAX_DE;
        System.out.println("E-Mail-Adresse erzeugt: " + emailAddress);
        return emailAddress;
    }

}
