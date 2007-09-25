package edu.stanford.smi.protegex.owl.jena.parser.tests;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.net.URI;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LoadSWRLFailedTestCase extends AbstractJenaTestCase {


    public void testAddSWRLImport() throws Exception {
        owlModel.getNamespaceManager().setPrefix(SWRLNames.SWRL_IMPORT, "swrlImport");
        owlModel.getNamespaceManager().setPrefix(SWRLNames.SWRLB_IMPORT, "swrlbImport");
        ProtegeOWLParser.addImport(owlModel, new URI(SWRLNames.SWRL_IMPORT));
        ProtegeOWLParser.addImport(owlModel, new URI(SWRLNames.SWRLB_IMPORT));
        RDFResource atomListClass = owlModel.getRDFResource(SWRLNames.Cls.ATOM_LIST);
        assertTrue(atomListClass instanceof OWLNamedClass);
    }


    public void testImportSWRL() throws Exception {
        JenaOWLModel newModel = reload(owlModel);
        newModel.copyFacetValuesIntoNamedClses();
        newModel.getOntModel();
    }


    public void testLoadSWRL() throws Exception {
        loadTestOntology(new URI("http://www.daml.org/2004/04/swrl/swrl.owl"));
        RDFProperty argument2Property = (RDFProperty) owlModel.getSlot("swrl:argument2");
        assertNotNull(argument2Property);
        assertNull(argument2Property.getRange());
        OWLNamedClass datavaluedPropertyAtomCls = (OWLNamedClass) owlModel.getCls("swrl:DatavaluedPropertyAtom");
        assertNotNull(datavaluedPropertyAtomCls);
        //assertEquals(ValueType.ANY, datavaluedPropertyAtomCls.getTemplateSlotValueType(argument2Property));

        OWLNamedClass dataRangeAtomCls = (OWLNamedClass) owlModel.getCls("swrl:DataRangeAtom");
        assertNotNull(dataRangeAtomCls);

        OWLObjectProperty dataRangeSlot = owlModel.getOWLObjectProperty("swrl:dataRange");
        assertNotNull(dataRangeSlot);

        //Collection clses = dataRangeAtomCls.getTemplateSlotAllowedClses(dataRangeSlot);
        //assertSize(1, clses);
        //assertContains(okb.getCls(OWLNames.Cls.DATA_RANGE), clses);
    }


    public void testLoadSWRLDataRangeAtom() throws Exception {

        loadRemoteOntology("SWRLDataRange.owl");

        Instance dataRange = owlModel.getInstance("MyDataRange");
        Slot oneOfSlot = owlModel.getSlot(OWLNames.Slot.ONE_OF);
        final List values = dataRange.getDirectOwnSlotValues(oneOfSlot);
        assertSize(2, values);
        assertContains("10", values);
        assertContains("15", values);
    }


    public void testLoadAtomList() throws Exception {
        loadRemoteOntology("swrlRules.owl");
        RDFResource imp = owlModel.getRDFResource("Rule-1");
        assertTrue(imp instanceof SWRLImp);
    }
}
