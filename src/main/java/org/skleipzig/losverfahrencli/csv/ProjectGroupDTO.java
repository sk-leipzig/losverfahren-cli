package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectGroupDTO {

    @CsvBindByName(column = "Projektname")
    private String projectName;

    @CsvBindByName(column = "Klassenstufen")
    private String forms;

    @CsvBindByName(column = "Teilnehmerzahl")
    private String capacity;

    public int getMinForm() {
        return formsToken(0).orElse(0);
    }

    public int getMaxForm() {
        return formsToken(1).orElse(0);
    }

    private Optional<Integer> formsToken(int index) {
        return Optional.ofNullable(forms)
                .map(formsString -> formsString.split("-"))
                .filter(tokens -> tokens.length > index)
                .map(tokens -> tokens[index])
                .map(String::trim)
                .filter(Strings::isNotEmpty)
                .map(Integer::parseInt);
    }

}
