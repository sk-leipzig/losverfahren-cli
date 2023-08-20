package org.skleipzig.losverfahrencli.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skleipzig.losverfahrencli.domain.Distribution;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemainingSeatsDTO {

    @CsvBindByName(column = "Projektname")
    private String projectName;

    @CsvBindByName(column = "Restplätze")
    private int capacity;

    public static List<RemainingSeatsDTO> fromDistribution(Distribution distribution) {
        return distribution.getRemainingSeats()
                .stream()
                .map(remainingSeats -> new RemainingSeatsDTO(remainingSeats.projectGroup()
                        .getProjectName(), remainingSeats.capacity()))
                .toList();
    }

    public String toString() {
        return projectName + "(Restplätze: " + capacity + ")";

    }

}
