package edu.stanford.smi.protegex.owl.ui.clsproperties.tests;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.clsproperties.PropertyRestrictionsTree;
import edu.stanford.smi.protegex.owl.ui.clsproperties.PropertyTreeNode;

public class PropertyRestrictionsTreeTestCase extends AbstractJenaTestCase {

    private void assertTree(PropertyRestrictionsTree tree, Object[] objects) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Enumeration enumeration = root.preorderEnumeration();
        enumeration.nextElement();
        int i = 0;
        for (i = 0; i < objects.length && enumeration.hasMoreElements(); i++) {
            Object object = objects[i];
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (!object.equals(node.getUserObject())) {
                printErrorTree(root);
            }
            assertEquals(object, node.getUserObject());
        }
        if (enumeration.hasMoreElements()) {
            printErrorTree(root);
        }
        if (i < objects.length) {
            printErrorTree(root);
        }
        assertFalse(enumeration.hasMoreElements() || i < objects.length);
    }


    private void printErrorTree(DefaultMutableTreeNode root) {
        Log.getLogger().info("Structure of failed tree:");
        Enumeration e = root.preorderEnumeration();
        e.nextElement();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode next = (DefaultMutableTreeNode) e.nextElement();
            Object obj = next.getUserObject();
            Log.getLogger().info(" - " + (obj instanceof Frame ? ((Frame) obj).getBrowserText() : obj.toString()));
        }
    }


    public void testSimpleBuild() {

        OWLNamedClass cls = owlModel.createOWLNamedClass("Parent");
        OWLObjectProperty hasChildrenProperty = owlModel.createOWLObjectProperty("hasChildren");
        hasChildrenProperty.addUnionDomainClass(cls);
        OWLRestriction restriction = owlModel.createOWLMinCardinality(hasChildrenProperty, 1);
        cls.addSuperclass(restriction);

        PropertyRestrictionsTree tree = new PropertyRestrictionsTree(owlModel, cls);
        assertTree(tree, new Object[]{
                hasChildrenProperty,
                restriction
        });
        PropertyTreeNode[] propertyTreeNodes = tree.getPropertyTreeNodes();
        assertEquals(1, propertyTreeNodes.length);
        assertEquals(hasChildrenProperty, propertyTreeNodes[0].getRDFProperty());
        assertFalse(propertyTreeNodes[0].isInherited());
        assertEquals(1, propertyTreeNodes[0].getChildCount());
        assertEquals(restriction, propertyTreeNodes[0].getRestrictionTreeNode(0).getUserObject());
    }


    public void testRecurseIntoSuperclassesAndEquivalentClasses() {

        OWLNamedClass superClass = owlModel.createOWLNamedClass("Super");
        OWLObjectProperty superproperty = owlModel.createOWLObjectProperty("super");
        superproperty.setDomain(superClass);

        OWLNamedClass middleClass = owlModel.createOWLNamedSubclass("Middle", superClass);
        OWLObjectProperty middleProperty = owlModel.createOWLObjectProperty("middle");
        middleProperty.setDomain(middleClass);

        OWLNamedClass subClass = owlModel.createOWLNamedSubclass("Sub", middleClass);
        OWLObjectProperty subProperty = owlModel.createOWLObjectProperty("sub");
        subProperty.setDomain(subClass);

        OWLNamedClass otherClass = owlModel.createOWLNamedClass("Other");
        OWLObjectProperty otherProperty = owlModel.createOWLObjectProperty("other");
        otherProperty.setDomain(otherClass);

        OWLIntersectionClass intersectionClass = owlModel.createOWLIntersectionClass();
        OWLRestriction restriction = owlModel.createOWLMinCardinality(otherProperty, 1);
        intersectionClass.addOperand(otherClass);
        intersectionClass.addOperand(restriction);

        subClass.addSuperclass(otherClass);
        subClass.addEquivalentClass(intersectionClass);

        assertTree(new PropertyRestrictionsTree(owlModel, subClass), new Object[]{
                subProperty,
                otherProperty,
                restriction,
                middleProperty,
                superproperty
        });
    }


    public void testDuplicateProperty() {

        OWLNamedClass superCls = owlModel.createOWLNamedClass("Super");
        OWLObjectProperty superproperty = owlModel.createOWLObjectProperty("super");
        superproperty.setDomain(superCls);

        OWLNamedClass subCls = owlModel.createOWLNamedSubclass("Sub", superCls);
        superproperty.addUnionDomainClass(subCls);

        assertTree(new PropertyRestrictionsTree(owlModel, subCls), new Object[]{
                superproperty
        });
    }


    public void testOverloadedMinCardiRestrictionWithMinCardiRestriction() {
        OWLNamedClass superclass = owlModel.createOWLNamedClass("Super");
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        property.addUnionDomainClass(superclass);
        OWLMinCardinality superRestriction = owlModel.createOWLMinCardinality(property, 1);
        superclass.addSuperclass(superRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, superclass), new Object[]{
                property,
                superRestriction
        });

        OWLNamedClass subclass = owlModel.createOWLNamedSubclass("Sub", superclass);
        OWLMinCardinality subRestriction = owlModel.createOWLMinCardinality(property, 2);
        subclass.addSuperclass(subRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, subclass), new Object[]{
                property,
                subRestriction
        });
    }


    public void testOverloadedCardiRestrictionWithMinCardiRestriction() {
        OWLNamedClass superclass = owlModel.createOWLNamedClass("Super");
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        property.addUnionDomainClass(superclass);
        OWLCardinality superRestriction = owlModel.createOWLCardinality(property, 1);
        superclass.addSuperclass(superRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, superclass), new Object[]{
                property,
                superRestriction
        });

        OWLNamedClass subclass = owlModel.createOWLNamedSubclass("Sub", superclass);
        OWLMinCardinality subRestriction = owlModel.createOWLMinCardinality(property, 2);
        subclass.addSuperclass(subRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, subclass), new Object[]{
                property,
                subRestriction,
                superRestriction
        });
    }


    public void testAllRestrictionOverloaded() {
        OWLNamedClass superclass = owlModel.createOWLNamedClass("Super");
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        property.addUnionDomainClass(superclass);
        OWLRestriction superRestriction = owlModel.createOWLAllValuesFrom(property, superclass);
        superclass.addSuperclass(superRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, superclass), new Object[]{
                property,
                superRestriction
        });

        OWLNamedClass subclass = owlModel.createOWLNamedSubclass("Sub", superclass);
        OWLRestriction subRestriction = owlModel.createOWLAllValuesFrom(property, subclass);
        subclass.addSuperclass(subRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, subclass), new Object[]{
                property,
                subRestriction
        });
    }


    public void testAllRestrictionMultiInherited() {
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        property.setDomainDefined(false);

        OWLNamedClass superCls1 = owlModel.createOWLNamedClass("Super1");
        OWLRestriction superRestriction1 = owlModel.createOWLAllValuesFrom(property, superCls1);
        superCls1.addSuperclass(superRestriction1);

        OWLNamedClass superCls2 = owlModel.createOWLNamedClass("Super2");
        OWLRestriction superRestriction2 = owlModel.createOWLAllValuesFrom(property, superCls2);
        superCls2.addSuperclass(superRestriction2);

        OWLNamedClass subCls = owlModel.createOWLNamedSubclass("Sub", superCls1);
        assertTree(new PropertyRestrictionsTree(owlModel, subCls), new Object[]{
                property,
                superRestriction1
        });

        subCls.addSuperclass(superCls2);
        assertTree(new PropertyRestrictionsTree(owlModel, subCls), new Object[]{
                property,
                superRestriction1,
                superRestriction2
        });
    }


    public void testOverloadedHasRestriction() {
        OWLNamedClass superclass = owlModel.createOWLNamedClass("Super");
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        property.addUnionDomainClass(superclass);
        OWLRestriction superRestriction = owlModel.createOWLHasValue(property, superclass);
        superclass.addSuperclass(superRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, superclass), new Object[]{
                property,
                superRestriction
        });

        OWLNamedClass subclass = owlModel.createOWLNamedSubclass("Sub", superclass);
        OWLRestriction subRestriction = owlModel.createOWLHasValue(property, subclass);
        subclass.addSuperclass(subRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, subclass), new Object[]{
                property,
                subRestriction,
                superRestriction
        });
    }


    public void testOverloadedSomeRestriction() {
        OWLNamedClass superclass = owlModel.createOWLNamedClass("Super");
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        property.setDomain(superclass);
        OWLRestriction superRestriction = owlModel.createOWLSomeValuesFrom(property, superclass);
        superclass.addSuperclass(superRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, superclass), new Object[]{
                property,
                superRestriction
        });

        OWLNamedClass subCls = owlModel.createOWLNamedSubclass("Sub", superclass);
        OWLRestriction subRestriction = owlModel.createOWLSomeValuesFrom(property, subCls);
        subCls.addSuperclass(subRestriction);
        assertTree(new PropertyRestrictionsTree(owlModel, subCls), new Object[]{
                property,
                subRestriction
        });
    }


    public void testShowDomainlessPropertiesOnlyIfRestricted() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Cls");
        OWLObjectProperty localProperty = owlModel.createOWLObjectProperty("local");
        localProperty.setDomain(cls);
        OWLObjectProperty globalProperty = owlModel.createOWLObjectProperty("global");
        globalProperty.setDomainDefined(false);
        assertTree(new PropertyRestrictionsTree(owlModel, cls), new Object[]{
                localProperty
        });
        final OWLMinCardinality restriction = owlModel.createOWLMinCardinality(globalProperty, 1);
        cls.addSuperclass(restriction);
        assertTree(new PropertyRestrictionsTree(owlModel, cls), new Object[]{
                localProperty,
                globalProperty,
                restriction
        });
    }


    public void testSubproperties() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Cls");
        OWLObjectProperty superproperty = owlModel.createOWLObjectProperty("superproperty");
        superproperty.setDomain(cls);
        OWLObjectProperty subproperty = (OWLObjectProperty) owlModel.createSubproperty("subproperty", superproperty);
        assertFalse(subproperty.isDomainDefined());
        assertTree(new PropertyRestrictionsTree(owlModel, cls), new Object[]{
                superproperty,
                subproperty
        });
    }


    public void testDomainlessSubproperties() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Cls");
        OWLObjectProperty superproperty = owlModel.createOWLObjectProperty("superproperty");
        superproperty.setDomainDefined(false);
        OWLObjectProperty subPropertyWithout = (OWLObjectProperty) owlModel.createSubproperty("subPropertyWithout", superproperty);
        OWLObjectProperty subPropertyWith = (OWLObjectProperty) owlModel.createSubproperty("subPropertyWith", superproperty);
        OWLMinCardinality minCardiRestriction = owlModel.createOWLMinCardinality(subPropertyWith, 1);
        cls.addSuperclass(minCardiRestriction);
        assertFalse(subPropertyWithout.isDomainDefined());
        assertFalse(subPropertyWith.isDomainDefined());
        assertTree(new PropertyRestrictionsTree(owlModel, cls), new Object[]{
                subPropertyWith,
                minCardiRestriction
        });
    }
}
