package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skleipzig.losverfahrencli.domain.Attendance;
import org.skleipzig.losverfahrencli.domain.Distribution;
import org.skleipzig.losverfahrencli.domain.Pupil;

import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PupilAssignmentDTO {
    @CsvBindByPosition(position = 0)
    private String foreName;

    @CsvBindByPosition(position = 1)
    private String name;

    @CsvBindByPosition(position = 2)
    private String form;

    @CsvBindByPosition(position = 3)
    private String emailAddress;

    @CsvBindByPosition(position = 4)
    private String projectName;

    public static PupilAssignmentDTO create(PupilDTO pupilDTO, String projectName) {
        return new PupilAssignmentDTO(pupilDTO.getForeName(), pupilDTO.getName(), pupilDTO.getForm(), pupilDTO.getEmailAddress(), projectName);
    }

    public static List<PupilAssignmentDTO> fromDistribution(Distribution distribution) {
        Stream<PupilAssignmentDTO> assignedPupils = distribution.getAttendances()
                .stream()
                .flatMap(PupilAssignmentDTO::streamDTOs);
        Stream<PupilAssignmentDTO> unassignedPupils = distribution.getOpenVotes()
                .stream()
                .map(pupilVoteResult -> pupilVoteResult.getPupil()
                        .toDTO())
                .map(pupilDTO -> create(pupilDTO, "nicht zugeordnet"));

        return Stream.concat(assignedPupils, unassignedPupils)
                .toList();
    }

    private static Stream<PupilAssignmentDTO> streamDTOs(Attendance attendance) {
        return attendance.getAttendees()
                .stream()
                .map(Pupil::toDTO)
                .map(pupilDTO -> create(pupilDTO, attendance.getProjectGroup()
                        .getProjectName()));
    }

    public String toString() {
        return foreName + " " + name + ", KlassenStufe " + form + " (" + emailAddress + "), Projektgruppe: " + projectName;
    }

}
