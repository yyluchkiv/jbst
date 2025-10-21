package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;

import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;
import static jbst.foundation.domain.properties.JbstPropertiesUtility.*;
import static jbst.foundation.utilities.strings.StringUtility.toKebab;

public abstract class JbstProperty {
    public abstract boolean isRoot();
    public abstract boolean isLeaf();
    public abstract boolean isToggle();
    @JbstNonMandatoryMethod
    public abstract String getNameNonMandatory();

    public void assertProperties() {
        if (this.isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonMandatory()) :
                getMandatoryFields(this, this.getNameNonMandatory());
        fields.forEach(field -> {
            var edge = new JbstPropertyEdge(this.getParentTreeName(),this, field);
            assertNonNullOrThrow(edge);
            if (edge.isChildBranch()) {
                edge.getChildAsJbstProperty().assertProperties();
            } else if (edge.isChildLeaf()) {
                edge.getChildAsJbstProperty().assertPropertiesAsLeaf(this.getParentTreeName());
                edge.printChildProperty(this.getParentTreeName());
            } else {
                edge.assertOrThrow();
                edge.print();
            }
        });
//        if (this.isRoot()) {
//            this.printProperties();
//        }
    }

    public void printPropertiesV1() {
        if (!this.isRoot()) {
            return;
        }
        getMandatoryBasedFields(this, this.getNameNonMandatory()).forEach(field -> {
//            var edge = new JbstPropertyEdge(this, field);
//            if (isNull(edge.getValueRAW())) {
//                edge.print();
//            } else {
//                if (edge.isChildBranch()) {
//                    edge.getChildAsJbstProperty().printProperties();
//                } else if (edge.isChildLeaf()) {
//                    edge.printChildProperty();
//                } else {
//                    edge.print();
//                }
//            }
        });
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected void assertPropertiesAsLeaf(String parentTreeName) {
        if (!this.isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonMandatory()) :
                getMandatoryFields(this, this.getNameNonMandatory());
        fields.forEach(field -> {
            var edge = new JbstPropertyEdge(parentTreeName, this, field);
            assertNonNullOrThrow(edge);
            edge.assertOrThrow();
        });
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private String getParentTreeName() {
        return toKebab(this.getNameNonMandatory());
    }
}
