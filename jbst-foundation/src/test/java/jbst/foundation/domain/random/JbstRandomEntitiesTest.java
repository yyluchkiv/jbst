package jbst.foundation.domain.random;

import jbst.foundation.domain.tests.classes.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.tests.constants.TestsJunitConstants.SMALL_ITERATIONS_COUNT;
import static jbst.foundation.domain.random.JbstRandomEntities.*;
import static jbst.foundation.domain.random.JbstRandom.one;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JbstRandomEntitiesTest {

    @Test
    void listDefaultConstructorTest() {
        // Arrange
        var size = (int) one(Integer.class);

        // Act
        var list = list(ClassDefaultConstructor.class, size);

        // Assert
        assertThat(list).hasSize(size);
    }

    @Test
    void listNoDefaultConstructorTest() {
        // Arrange
        var size = (int) one(Integer.class);

        // Act
        var list = list(ClassNoDefaultConstructor.class, size);

        // Assert
        assertThat(list).hasSize(size);
    }

    @Test
    void listPrivateConstructorTest() {
        // Arrange
        var size = (int) one(Integer.class);


        // Act
        var list = list(ClassPrivateConstructor.class, size);

        // Assert
        assertThat(list).hasSize(size);
    }

    @Test
    void setDefaultConstructorTest() {
        // Arrange
        var size = (int) one(Integer.class);

        // Act
        var set = set(ClassDefaultConstructor.class, size);

        // Assert
        assertThat(set).hasSize(size);
    }

    @Test
    void setNoDefaultConstructorTest() {
        // Arrange
        var size = (int) one(Integer.class);

        // Act
        var set = set(ClassNoDefaultConstructor.class, size);

        // Assert
        assertThat(set).hasSize(size);
    }

    @Test
    void setPrivateConstructorTest() {
        // Arrange
        var size = (int) one(Integer.class);

        // Act
        var set = set(ClassPrivateConstructor.class, size);

        // Assert
        assertThat(set).hasSize(size);
    }

    // =================================================================================================================
    // PROTECTED CLASSES
    // =================================================================================================================
    @SuppressWarnings("ConstantConditions")
    @Test
    void processSettersInstanceNullTest() {
        // Act
        var throwable = catchThrowable(() -> setObjectFields(null));

        // Assert
        assertThat(throwable)
                .isInstanceOf(ReflectiveOperationException.class)
                .hasMessageContaining("Cannot invoke setter. Instance is null");
    }

    @Test
    void processSettersClassDefaultConstructorNoSettersTest() throws ReflectiveOperationException {
        // Arrange
        var instance = new ClassDefaultConstructorNoSetters();

        // Act
        setObjectFields(instance);

        // Assert
        assertThat(instance.getString()).isNull();
    }

    @Test
    void processSettersClassDefaultConstructorUnexpectedMethodTest() throws ReflectiveOperationException {
        // Arrange
        var instance = new ClassDefaultConstructorUnexpectedMethods();

        // Act
        setObjectFields(instance);

        // Assert
        assertThat(instance.getString()).isNull();
    }

    @Test
    void processSettersUnexpectedSetterLoggerCaseTest() {
        // Arrange
        var instance = new ClassDefaultConstructorUnexpectedSetter();

        // Act
        var throwable
                = catchThrowable(() -> setObjectFields(instance));

        // Assert
        assertThat(throwable)
                .isInstanceOf(ReflectiveOperationException.class)
                .hasMessageContaining("Cannot invoke setter. Unexpected setter signature");
    }

    @Test
    void processSettersClassWithAllArgsAndDefaultConstructorsTest() throws ReflectiveOperationException {
        // Arrange
        var instance = new ClassWithAllArgsAndDefaultConstructors();

        // Act
        setObjectFields(instance);

        // Assert
        assertThat(instance.getStringValue()).isNotNull();
    }

    @Test
    void randomNestedClassesTest() {
        // Arrange
        var random = entity(ClassNestParent.class);

        // Assert
        assertThat(random).isNotNull();
        assertThat(random.getValue1()).isNotNull();
        assertThat(random.getValue2()).isNotNull();
        assertThat(random.getValue4()).isNotNull();
        assertThat(random.getChild1()).isNotNull();
        assertThat(random.getChild1().getNest1Value1()).isNotNull();
        assertThat(random.getChild1().getNest1Value2()).isNotNull();
        assertThat(random.getChild1().getNest1Value3()).isNotNull();
        assertThat(random.getChild2()).isNotNull();
        assertThat(random.getChild2().getNest2Value1()).isNotNull();
        assertThat(random.getChild2().getNest2Value2()).isNotNull();
    }

    @RepeatedTest(SMALL_ITERATIONS_COUNT)
    void list345Test() {
        // Act
        var set = list345(Long.class);

        // Assert
        assertThat(set).isNotNull();
        assertThat(set.size()).isBetween(1, 6);
    }

    @RepeatedTest(SMALL_ITERATIONS_COUNT)
    void set345Test() {
        // Act
        var set = set345(Long.class);

        // Assert
        assertThat(set).isNotNull();
        assertThat(set.size()).isBetween(1, 6);
    }
}
