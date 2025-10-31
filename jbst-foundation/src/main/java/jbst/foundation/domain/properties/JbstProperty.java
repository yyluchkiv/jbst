package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;
import static jbst.foundation.domain.properties.JbstPropertiesUtility.*;
import static jbst.foundation.utilities.strings.StringUtility.toKebab;

public abstract class JbstProperty {
    public enum JbstPropertyNodeType {
        ROOT, BRANCH, LEAF;

        public boolean isRoot() {
            return this == ROOT;
        }

        public boolean isBranch() {
            return this == BRANCH;
        }

        public boolean isLeaf() {
            return this == LEAF;
        }
    }

    public abstract JbstPropertyNodeType getNodeType();
    public abstract boolean isToggle();
    @JbstNonMandatoryMethod
    public abstract String getNameNonLeaf();

    public void assertProperties() {
        if (this.getNodeType().isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonLeaf()) :
                getMandatoryFields(this, this.getNameNonLeaf());
        var currentParentTreeName = toKebab(this.getNameNonLeaf());
        System.out.println("===");
        System.out.println(fields);
        System.out.println("===");
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
        if (this.getNodeType().isRoot()) {
            this.printProperties(currentParentTreeName);
        }
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected void assertPropertiesAsLeaf(String parentTreeName) {
        if (!this.getNodeType().isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonLeaf()) :
                getMandatoryFields(this, this.getNameNonLeaf());
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
        getMandatoryBasedFields(this, this.getNameNonLeaf()).forEach(field -> {
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
}
