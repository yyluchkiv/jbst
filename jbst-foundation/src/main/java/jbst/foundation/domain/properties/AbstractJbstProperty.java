package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;

public abstract class AbstractJbstProperty {
    public abstract boolean isParent();
    public abstract boolean isLeaf();
    public abstract boolean isToggle();
    @JbstNonMandatoryMethod
    public abstract String getNameNonMandatory();

    // TODO [YYL] fixme?
    public void assertProperties() {

    }
}
