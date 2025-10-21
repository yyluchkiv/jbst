package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;

import static java.util.Objects.isNull;
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
        var currentParentTreeName = this.getParentTreeName();
        fields.forEach(field -> {
            var edge = new JbstPropertyEdge(currentParentTreeName,this, field);
            assertNonNullOrThrow(edge);
            if (edge.isChildBranch()) {
                var property = edge.getChildAsJbstProperty();
                property.assertProperties();
            } else if (edge.isChildLeaf()) {
                var property = edge.getChildAsJbstProperty();
                property.assertPropertiesAsLeaf(currentParentTreeName + "." + toKebab(field.getName()));
            } else {
                edge.assertOrThrow();
            }
        });
        if (this.isRoot()) {
            this.printProperties(currentParentTreeName);
        }
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
    private void printProperties(String parentTreeName) {
        getMandatoryBasedFields(this, this.getNameNonMandatory()).forEach(field -> {
            var edge = new JbstPropertyEdge(parentTreeName, this, field);
            if (isNull(edge.getValueRAW())) {
                edge.print();
            } else {
                if (edge.isChildBranch()) {
                    edge.getChildAsJbstProperty().printProperties(edge.getName());
                } else if (edge.isChildLeaf()) {
                    edge.printChildProperty(edge.getName());
                } else {
                    edge.print();
                }
            }
        });
    }

    private String getParentTreeName() {
        return toKebab(this.getNameNonMandatory());
    }
}
