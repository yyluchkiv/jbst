package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.base.ObjectId;
import jbst.foundation.domain.plurals.JbstPlurals;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Lombok
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class TestObjects extends JbstPlurals<TestObject, ObjectId> {

    public TestObjects(List<TestObject> values) {
        super(values);
    }

    public static TestObjects random(int size) {
        return new TestObjects(
                IntStream.range(0, size)
                        .mapToObj(i -> TestObject.random())
                        .toList()
        );
    }

    public Set<String> getNames() {
        return this.values.stream().map(TestObject::name).collect(Collectors.toSet());
    }
}
