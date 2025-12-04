package jbst.foundation.domain.tests.classes;

import jbst.foundation.domain.base.ObjectId;
import jbst.foundation.domain.plurals.JbstPlurable;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record TestObject(
        ObjectId id,
        String name
) implements JbstPlurable<ObjectId> {

    public static TestObject random() {
        return new TestObject(ObjectId.random(), randomString());
    }

    @Override
    public ObjectId getId() {
        return this.id;
    }
}
