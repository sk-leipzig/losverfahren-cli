package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skleipzig.losverfahrencli.domain.AssignmentType;
import org.skleipzig.losverfahrencli.domain.Attendance;
import org.skleipzig.losverfahrencli.domain.Distribution;

import java.util.List;
import java.util.stream.Stream;

import static org.skleipzig.losverfahrencli.domain.AssignmentType.Nichts;

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

    @CsvBindByPosition(position = 5)
    private String assignmentType;

    public static PupilAssignmentDTO create(PupilDTO pupilDTO, String projectName, AssignmentType assignmentType) {
        return new PupilAssignmentDTO(pupilDTO.getForeName(), pupilDTO.getName(), pupilDTO.getForm(), pupilDTO.getEmailAddress(), projectName, assignmentType.name());
    }

    public static List<PupilAssignmentDTO> fromDistribution(Distribution distribution) {
        Stream<PupilAssignmentDTO> assignedPupils = distribution.getAttendances()
                .stream()
                .flatMap(PupilAssignmentDTO::streamDTOs);
        Stream<PupilAssignmentDTO> unassignedPupils = distribution.getOpenVotes()
                .stream()
                .map(pupilVoteResult -> pupilVoteResult.getPupil()
                        .toDTO())
                .map(pupilDTO -> create(pupilDTO, "nicht zugeordnet", Nichts));

        return Stream.concat(assignedPupils, unassignedPupils)
                .toList();
    }

    private static Stream<PupilAssignmentDTO> streamDTOs(Attendance attendance) {
        return attendance.getAttendees()
                .entrySet()
                .stream()
                .map(entry -> create(entry.getKey()
                        .toDTO(), attendance.getProjectGroup()
                        .getProjectName(), entry.getValue()));
    }

    public String toString() {
        return foreName + " " + name + ", KlassenStufe " + form + " (" + emailAddress + "), Projektgruppe: " + projectName;
    }

}
