package jbst.foundation.domain.hardware.memories;

import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;

public abstract class AbstractMemoriesTest extends AbstractFolderSerializationRunner {

    @Override
    protected String getFolder() {
        return "jsons";
    }
}
