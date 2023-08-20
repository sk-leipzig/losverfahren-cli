package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PupilAssignmentDTO extends PupilDTO {
    public PupilAssignmentDTO(String foreName, String name, String form, String emailAddress, String projectName) {
        super(foreName, name, form, emailAddress);
        this.projectName = projectName;
    }

    @CsvBindByName(column = "Projektname")
    private String projectName;

    public static PupilAssignmentDTO create(PupilDTO pupilDTO, String projectName) {
        return new PupilAssignmentDTO(pupilDTO.getForeName(), pupilDTO.getName(), pupilDTO.getForm(), pupilDTO.getEmailAddress(), projectName);
    }

}
