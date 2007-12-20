package edu.stanford.smi.protegex.owl.model.classparser.compact.tests;

import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.classparser.compact.CompactOWLClassParser;
import edu.stanford.smi.protegex.owl.tests.AbstractOWLModelTestCase;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ParseEnumeratedClassesTestCase extends AbstractOWLModelTestCase {

    private OWLClassParser parser = new CompactOWLClassParser();


    public void testParseEnumerationOfIndividuals() throws Exception {
        ProfilesManager.setProfile(owlModel, OWLProfiles.OWL_DL.getURI());
        OWLNamedClass genderCls = owlModel.createOWLNamedClass("Gender");
        genderCls.createInstance("male");
        genderCls.createInstance("female");
        String expression = "{male female}";
        RDFSClass aClass = parser.parseClass(owlModel, expression);
        assertTrue(aClass instanceof OWLEnumeratedClass);
        OWLEnumeratedClass enumeratedClass = (OWLEnumeratedClass) aClass;
        Collection resources = enumeratedClass.getOneOf();
        assertEquals(2, resources.size());
        assertTrue(resources.contains(owlModel.getRDFResourceByBrowserText("male")));
        assertTrue(resources.contains(owlModel.getRDFResourceByBrowserText("female")));
    }


    public void testParseEnumerationOfClassesAndSlots() throws Exception {
        ProfilesManager.setProfile(owlModel, OWLProfiles.OWL_Full.getURI());
        OWLNamedClass maleCls = owlModel.createOWLNamedClass("Male");
        OWLObjectProperty slot = owlModel.createOWLObjectProperty("hasChildren");
        String expression = "{Male hasChildren}";
        RDFSClass aClass = parser.parseClass(owlModel, expression);
        assertTrue(aClass instanceof OWLEnumeratedClass);
        OWLEnumeratedClass enumeratedClass = (OWLEnumeratedClass) aClass;
        Collection resources = enumeratedClass.getOneOf();
        assertEquals(2, resources.size());
        assertTrue(resources.contains(maleCls));
        assertTrue(resources.contains(slot));
    }


    public void testParseEnumerationOfClassesAndSlotsInOWLDL() throws Exception {
        ProfilesManager.setProfile(owlModel, OWLProfiles.OWL_DL.getURI());
        owlModel.createOWLNamedClass("Male");
        owlModel.createOWLObjectProperty("hasChildren");
        String expression = "{Male hasChildren}";
        try {
            parser.parseClass(owlModel, expression);
            assertFalse("Expected to fail parsing!", true);
        }
        catch (Exception ex) {
        }
    }
}
