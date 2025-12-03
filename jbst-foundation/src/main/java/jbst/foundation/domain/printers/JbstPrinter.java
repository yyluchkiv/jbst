package jbst.foundation.domain.printers;

import lombok.experimental.UtilityClass;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.DASH;

@SuppressWarnings("unused")
@UtilityClass
public class JbstPrinter {

    public static void printTable(String[] headers, Object[][] cells) {
        var columnWidths = stream(headers).mapToInt(String::length).toArray();

        for (var row : cells) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].toString().length());
            }
        }

        var rowFormat = stream(columnWidths).mapToObj(w -> "%-" + (w + 2) + "s").collect(joining());
        var separator = stream(columnWidths).mapToObj(w -> DASH.repeat(w + 2)).collect(joining());

        System.out.println(separator);
        System.out.printf(rowFormat + "%n", (Object[]) headers);
        System.out.println(separator);

        for (var row : cells) {
            System.out.printf(rowFormat + "%n", row);
        }

        System.out.println(separator);
    }
}
