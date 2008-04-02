package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.tests;

import com.hp.hpl.jena.ontology.OntModel;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.protege2jena.Protege2Jena;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 9, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractRDFXMLWriterTestCases extends AbstractJenaTestCase {

    protected void doCheck() {
        try {
            OntModel ontModel1 = Protege2Jena.createOntModel(owlModel);
            //           Log.getLogger().info("Jena writer ------------------------------------------------\n");
            //           Jena.dumpRDF(ontModel1);
            //           Log.getLogger().info("\n");

            StringWriter writer = new StringWriter();
            OWLModelWriter omw = new OWLModelWriter(owlModel, owlModel.getTripleStoreModel().getActiveTripleStore(), writer);
            omw.write();
//            Log.getLogger().info("Native writer ----------------------------------------------\n");
//            Log.getLogger().info(writer.getBuffer().toString());
//            Log.getLogger().info("\n");

            OWLModel model = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(writer.getBuffer().toString()));
            OntModel ontModel2 = Protege2Jena.createOntModel(model);
//            Log.getLogger().info("Jena writer (from reload) ----------------------------------------------\n");
//          Jena.dumpRDF(ontModel2);

            assertTrue(ontModel1.isIsomorphicWith(ontModel2));
            assertTrue(ontModel2.isIsomorphicWith(ontModel1));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

