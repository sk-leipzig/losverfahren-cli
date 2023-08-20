package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PupilDTO {

    @CsvBindByName(column = "Vorname (Schüler)")
    private String foreName;

    @CsvBindByName(column = "Name (Schüler)")
    private String name;

    @CsvBindByName(column = "Klassenstufe")
    private String form;

    @CsvBindByName(column = "Email")
    private String emailAddress;


}
