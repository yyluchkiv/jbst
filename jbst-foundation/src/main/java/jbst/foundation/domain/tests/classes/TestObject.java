package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.base.ObjectId;
import jbst.foundation.domain.plurals.Plurable;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record TestObject(
        ObjectId id,
        String name
) implements Plurable<ObjectId> {

    public static TestObject random() {
        return new TestObject(ObjectId.random(), randomString());
    }

    @Override
    public ObjectId getId() {
        return this.id;
    }
}
