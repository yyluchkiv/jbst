package jbst.foundation.domain.plurals;

import jbst.foundation.domain.base.ObjectId;
import jbst.foundation.domain.tests.classes.TestObject;
import jbst.foundation.domain.tests.classes.TestObjects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class JbstPluralsTest {

    @Test
    void randomTest() {
        // Arrange
        int size = 3;

        // Act
        var testObjects = TestObjects.random(size);

        // Assert
        Assertions.assertThat(testObjects.getValues()).hasSize(size);
        Assertions.assertThat(testObjects.getMappedValues()).hasSize(size);
        assertThat(testObjects.getNames()).hasSize(size);
    }

    @Test
    void unmodifiableValuesTest() {
        // Arrange
        int size = 3;
        var testObjects = TestObjects.random(size);

        // Act
        var throwable = catchThrowable(() -> testObjects.getValues().add(TestObject.random()));

        // Arrange
        assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void unmodifiableMappedValuesTest() {
        // Arrange
        int size = 3;
        var testObjects = TestObjects.random(size);

        // Act
        var throwable = catchThrowable(() -> testObjects.getMappedValues().put(ObjectId.random(), TestObject.random()));

        // Arrange
        assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
    }
}
