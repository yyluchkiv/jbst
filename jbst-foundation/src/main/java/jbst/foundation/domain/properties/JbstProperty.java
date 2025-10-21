package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;
import jbst.foundation.domain.reflections.JbstPropertyEdge;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;
import static jbst.foundation.domain.reflections.JbstPropertiesUtility.*;

// TODO [YYL] merge AbstractPropertyConfigs + AbstractPropertiesConfigs: [parent, leaf, name]?
public abstract class JbstProperty {
    public abstract boolean isRoot();
    public abstract boolean isLeaf();
    public abstract boolean isToggle();
    @JbstNonMandatoryMethod
    public abstract String getNameNonMandatory();

    // TODO [YYL] fixme?
    public void assertProperty() {
        if (this.isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonMandatory()) :
                getMandatoryFields(this, this.getNameNonMandatory());
        fields.forEach(field -> {
            var edge = new JbstPropertyEdge(this, field);
            assertNonNullOrThrow(edge);
            if (edge.isChildBranch()) {
                edge.getChildAsJbstProperty().assertProperty();
            } else if (edge.isChildLeaf()) {
                edge.getChildAsJbstProperty().assertPropertyLeaf();
            } else {
                edge.assertOrThrow();
            }
        });
        if (this.isRoot()) {
            this.printProperties();
        }
    }

    private void assertPropertyLeaf() {
        if (!this.isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonMandatory()) :
                getMandatoryFields(this, this.getNameNonMandatory());
        fields.forEach(field -> {
            var edge = new JbstPropertyEdge(this, field);
            assertNonNullOrThrow(edge);
            edge.assertOrThrow();
        });
    }

    private void printProperties() {
        if (!this.isRoot()) {
            return;
        }
        getMandatoryBasedFields(this, this.getNameNonMandatory()).forEach(field -> {
            var edge = new JbstPropertyEdge(this, field);
            if (isNull(edge.getValueRAW())) {
                edge.print();
            } else {
                if (edge.isChildBranch()) {
                    // edge.getChildAsJbstProperty().assertProperty();
                } else if (edge.isChildLeaf()) {
                    edge.printAbstractPropertyConfigs();
                } else {
                    edge.print();
                }
//                    var nestedPropertyClass = jbstProperty.getPropertyValue().getClass();
//                    if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                        ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).printProperties();
//                    } /* else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                        jbstProperty.printAbstractPropertyConfigs();
//                    } */ else {
//                        jbstProperty.print();
//                    }
            }
        });
    }
}
