package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.*;

import java.math.BigDecimal;

// Lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ClassNestChild1 {
    private Integer nest1Value1;
    private BigDecimal nest1Value2;
    private JbstUnitTests.Enums.EnumUnderTests nest1Value3;
}
