package jbst.foundation.domain.tests.runners;

import jbst.foundation.domain.tests.JbstUnitTests;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;

public abstract class AbstractSerializationDeserializationRunner extends JbstUnitTests.Runners.BaseFolder {

    protected abstract String getFileName();

    protected final String readFile() {
        return read(this.getFolder(), this.getFileName());
    }
}
