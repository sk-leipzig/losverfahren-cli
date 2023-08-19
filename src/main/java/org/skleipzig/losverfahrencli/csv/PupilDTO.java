package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class PupilDTO {

    @CsvBindByName(column = "E-Mail")
    private final String emailAddress;

    @CsvBindByName(column = "Klassenstufe")
    private final String form;

    @CsvBindByName(column = "Erstwunsch")
    private final String primaryChoice;

    @CsvBindByName(column = "Zweitwunsch")
    private final String secondaryChoice;

    @CsvBindByName(column = "Drittwunsch")
    private final String tertiaryChoice;

}
