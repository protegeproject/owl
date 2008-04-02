package edu.stanford.smi.protegex.owl.model.event.tests;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyListener;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyListenerTestCase extends AbstractJenaTestCase {

    private int eventCount = 0;

    private PropertyListener listener = new PropertyAdapter() {
        public void subpropertyAdded(RDFProperty property, RDFProperty subproperty) {
            eventCount++;
        }


        public void subpropertyRemoved(RDFProperty property, RDFProperty subproperty) {
            eventCount++;
        }


        public void superpropertyAdded(RDFProperty property, RDFProperty superproperty) {
            eventCount++;
        }


        public void superpropertyRemoved(RDFProperty property, RDFProperty superproperty) {
            eventCount++;
        }


        public void unionDomainClassAdded(RDFProperty property, RDFSClass rdfsClass) {
            eventCount++;
        }


        public void unionDomainClassRemoved(RDFProperty property, RDFSClass rdfsClass) {
            eventCount++;
        }
    };


    public void testEvents() {
        RDFSClass domainClass = owlModel.createRDFSNamedClass("Class");
        RDFProperty property = owlModel.createRDFProperty("property");
        RDFProperty subProperty = owlModel.createRDFProperty("sub");
        property.addPropertyListener(listener);
        subProperty.addPropertyListener(listener);
        subProperty.addSuperproperty(property);
        assertEquals(3, eventCount);
        subProperty.removeSuperproperty(property);
        assertEquals(6, eventCount);
        property.addUnionDomainClass(domainClass);
        assertEquals(7, eventCount);
        property.removeUnionDomainClass(domainClass);
        assertEquals(8, eventCount);
    }
}
