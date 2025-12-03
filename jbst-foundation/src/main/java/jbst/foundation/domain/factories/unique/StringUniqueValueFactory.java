package jbst.foundation.domain.factories.unique;

import jbst.foundation.domain.random.JbstRandom;

public class StringUniqueValueFactory implements UniqueValueFactory<String> {

    @Override
    public String createValue() {
        return JbstRandom.randomString();
    }
}
