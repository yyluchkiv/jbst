package jbst.foundation.domain.properties.annotations;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;

public abstract class AbstractProperty {
    public abstract boolean isParent();
    public abstract boolean isLeaf();
    public abstract boolean isToggle();
    @JbstNonMandatoryMethod
    public abstract String getNameNonMandatory();
}
