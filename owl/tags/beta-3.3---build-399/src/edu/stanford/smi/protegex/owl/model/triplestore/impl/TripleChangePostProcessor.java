package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

/**
 * A class that can be used to perform various post processing steps on an OWLModel
 * after triples have been changed.  This only delegates to other classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TripleChangePostProcessor {

    //private static void aldi(OWLModel owlModel, TripleStore tripleStore) {
    //    for (Iterator it = tripleStore.listSubjects(owlModel.getRDFTypeProperty(), owlModel.getOWLOntologyClass()); it.hasNext();) {
    //        RDFResource subject = (RDFResource) it.next();
    //        System.out.println(" - " + subject.getBrowserText());
    //    }
    //}


    public static void postProcess(OWLModel owlModel) {
        TripleStore activeTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();

        {
            long startTime = System.currentTimeMillis();
            new RDFListPostProcessor(owlModel);
            log("Completed lists after " + (System.currentTimeMillis() - startTime) + " ms");
        }

        {
            long startTime = System.currentTimeMillis();
            new OWLAnonymousClassPostProcessor(owlModel);
            log("Completed anonymous classes after " + (System.currentTimeMillis() - startTime) + " ms");
        }

        {
            long startTime = System.currentTimeMillis();
            new OWLDeprecatedClassPostProcessor(owlModel);
            log("Completed deprecated classes after " + (System.currentTimeMillis() - startTime) + " ms");
        }

        {
            long startTime = System.currentTimeMillis();
            new RDFPropertyPostProcessor(owlModel);
            log("Completed properties after " + (System.currentTimeMillis() - startTime) + " ms");
        }

        {
            long startTime = System.currentTimeMillis();
            new RDFSNamedClassPostProcessor(owlModel);
            new DuplicateSuperclassesPostProcessor(owlModel);
            log("Completed named classes after " + (System.currentTimeMillis() - startTime) + " ms");
        }
        owlModel.getTripleStoreModel().setActiveTripleStore(activeTripleStore);
    }


    private static void log(String str) {
        Log.getLogger().info("[TripleChangePostProcessor] " + str);
    }
}
