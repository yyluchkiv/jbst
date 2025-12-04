package jbst.foundation.domain.tests.runners;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;

public abstract class AbstractSerializationDeserializationRunner extends AbstractFolderSerializationRunner {

    protected abstract String getFileName();

    protected final String readFile() {
        return read(this.getFolder(), this.getFileName());
    }
}
