package jbst.foundation.utilities.printer;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

import static java.lang.Math.max;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.DASH;

@SuppressWarnings("unused")
@UtilityClass
public class Printer {

    public static void printTable(String[] headers, Object[][] cells) {
        var columnWidths = Arrays.stream(headers).mapToInt(String::length).toArray();
        var rowFormat = Arrays.stream(columnWidths).mapToObj(width -> "%-" + (width + 2) + "s").collect(joining());

        Arrays.stream(cells)
                .forEach(row -> range(0, row.length)
                        .forEach(i -> columnWidths[i] = max(columnWidths[i], row[i].toString().length())));

        Arrays.stream(columnWidths).mapToObj(width -> DASH.repeat(width + 2)).forEach(System.out::print);
        System.out.println();

        System.out.printf(rowFormat + "%n", (Object[]) headers);

        Arrays.stream(columnWidths).mapToObj(width -> DASH.repeat(width + 2)).forEach(System.out::print);
        System.out.println();

        Arrays.stream(cells).forEach(row -> System.out.printf(rowFormat + "%n", row));
        System.out.println();
    }
}
