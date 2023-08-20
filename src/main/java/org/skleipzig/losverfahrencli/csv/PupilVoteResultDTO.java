package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PupilVoteResultDTO {

    @CsvBindByName(column = "Login")
    private String login;

    @CsvBindByName(column = "Klassenstufe")
    private String form;

    @CsvBindByName(column = "Erstwunsch")
    private String primaryChoice;

    @CsvBindByName(column = "Zweitwunsch")
    private String secondaryChoice;

    @CsvBindByName(column = "Drittwunsch")
    private String tertiaryChoice;

}
