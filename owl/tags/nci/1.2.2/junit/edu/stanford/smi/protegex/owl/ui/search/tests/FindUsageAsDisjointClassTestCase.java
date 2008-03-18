package edu.stanford.smi.protegex.owl.ui.search.tests;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.search.FindUsage;
import edu.stanford.smi.protegex.owl.ui.search.AbstractFindUsageTableItem;
import edu.stanford.smi.protegex.owl.ui.search.FindUsageTableItem;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FindUsageAsDisjointClassTestCase extends AbstractJenaTestCase {

    public void testFindSimpleDisjointClass() {
        OWLNamedClass hostCls = owlModel.createOWLNamedClass("Host");
        OWLNamedClass otherCls = owlModel.createOWLNamedClass("Other");
        hostCls.addDisjointClass(otherCls);

        Collection items = FindUsage.getItems(otherCls);
        assertSize(1, items);
        AbstractFindUsageTableItem item = (AbstractFindUsageTableItem) items.iterator().next();
        assertEquals(hostCls, item.getHost());
        assertEquals(otherCls, item.getUsage());
        assertEquals(FindUsageTableItem.DISJOINT_CLASS, item.getType());
    }


    public void testFindComplexDisjointClass() {
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        OWLNamedClass hostCls = owlModel.createOWLNamedClass("Host");
        OWLNamedClass otherCls = owlModel.createOWLNamedClass("Other");
        OWLUnionClass unionCls = owlModel.createOWLUnionClass();
        unionCls.addOperand(owlModel.createOWLMinCardinality(property, 1));
        unionCls.addOperand(owlModel.createOWLComplementClass(otherCls));
        hostCls.addDisjointClass(unionCls);

        Collection items = FindUsage.getItems(otherCls);
        assertSize(1, items);
        AbstractFindUsageTableItem item = (AbstractFindUsageTableItem) items.iterator().next();
        assertEquals(hostCls, item.getHost());
        assertEquals(unionCls, item.getUsage());
        assertEquals(FindUsageTableItem.DISJOINT_CLASS, item.getType());
    }
}
