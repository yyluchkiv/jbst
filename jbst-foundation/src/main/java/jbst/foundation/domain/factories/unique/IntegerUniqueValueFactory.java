package jbst.foundation.domain.factories.unique;

import jbst.foundation.domain.random.JbstRandom;

public class IntegerUniqueValueFactory implements UniqueValueFactory<Integer> {

    @Override
    public Integer createValue() {
        return JbstRandom.randomIntegerGreaterThanZero();
    }
}
