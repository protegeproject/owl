package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.tests;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.OntModel;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.protege2jena.Protege2Jena;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

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
    private static transient final Logger log = Log.getLogger(AbstractRDFXMLWriterTestCases.class);

    protected void doCheck() {
        try {
            log.setLevel(Level.FINER);
            OntModel ontModel1 = Protege2Jena.createOntModel(owlModel);
            if (log.isLoggable(Level.FINE)) {
                log.fine("Jena writer ------------------------------------------------\n");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Jena.dumpRDF(ontModel1, out);
                log.fine(out.toString());
                log.fine("\n");
            }

            StringWriter writer = new StringWriter();
            OWLModelWriter omw = new OWLModelWriter(owlModel, owlModel.getTripleStoreModel().getActiveTripleStore(), writer);
            omw.write();
            if (log.isLoggable(Level.FINE)) {
                log.fine("Native writer ------------------------------------------------\n");
                log.fine(writer.getBuffer().toString());
                log.fine("\n");
            }

            OWLModel model = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(writer.getBuffer().toString()));
            OntModel ontModel2 = Protege2Jena.createOntModel(model);
            
            if (log.isLoggable(Level.FINE)) {
                log.fine("Jena writer ------------------------------------------------\n");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Jena.dumpRDF(ontModel1, out);
                log.fine(out.toString());
                log.fine("\n");
            }

            if (log.isLoggable(Level.FINER)) {
                if (!ontModel1.isIsomorphicWith(ontModel2) || 
                        !ontModel2.isIsomorphicWith(ontModel1)) {
                    log.finer("Ok - you are going to love this! Ouch! Ouch! Ouch! ;)");
                    log.finer("-----------------------ontModel1---------------------------------");
                    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                    ontModel1.write(out1, "N-TRIPLE");
                    log.finer(out1.toString());
                    log.finer("-----------------------ontModel1---------------------------------");
                    log.finer("-----------------------ontModel2---------------------------------");
                    ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                    ontModel1.write(out2, "N-TRIPLE");
                    log.finer(out2.toString());
                    log.finer("-----------------------ontModel2---------------------------------");
                }
            }
            assertTrue(ontModel1.isIsomorphicWith(ontModel2));
            assertTrue(ontModel2.isIsomorphicWith(ontModel1));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

