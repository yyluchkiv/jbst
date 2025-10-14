package jbst.foundation.domain.tuples;

import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;

public abstract class AbstractTupleTest extends AbstractSerializationDeserializationRunner {

    @Override
    protected String getFolder() {
        return "jsons";
    }
}
