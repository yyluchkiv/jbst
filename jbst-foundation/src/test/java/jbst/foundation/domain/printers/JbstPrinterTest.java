package jbst.foundation.domain.printers;

import org.junit.jupiter.api.Test;

class JbstPrinterTest {

    @Test
    void printTable() {
        // Act
        JbstPrinter.printTable(
                new String[] {
                        "Currency",
                        "Amount",
                        "Rate, $",
                        "Total, $"
                },
                new Object[][] {
                    {"EUR", 100, 1.10, 110.00},
                    {"UAH", 1000, 0.027, 27.00},
                    {"JPY", 10000, 0.0068, 68.00}
                }
        );

        // Assert
        // ignore
    }
}
