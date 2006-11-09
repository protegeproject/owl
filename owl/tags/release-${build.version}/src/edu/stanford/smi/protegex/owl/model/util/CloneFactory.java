package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import java.util.Collection;
import java.util.Iterator;

/**
 * A utility class to clone a named class
 * TODO: add methods for copying Properties and Individuals
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Jan-2006
 */
public class CloneFactory {

    public static OWLNamedClass cloneOWLNamedClass(OWLNamedClass source) {
        OWLModel owlModel = source.getOWLModel();

        String newName = null;
        int i = 2;
        do {
            newName = source.getName() + "_" + i;
            i++;
        }
        while (owlModel.getRDFResource(newName) != null);

        OWLNamedClass clone = owlModel.createOWLNamedClass(newName);

        ResourceCopier scopier = new ResourceCopier();

	    scopier.copyMultipleSlotValues(source, clone);

        Collection sourceSuperclasses = source.getSuperclasses(false);

        for (Iterator it = sourceSuperclasses.iterator(); it.hasNext();) {
            RDFSClass sourceSuperclass = (RDFSClass) it.next();

            sourceSuperclass.accept(scopier);
            RDFSClass clonesSuperclass = (RDFSClass) scopier.getCopy();
            clone.addSuperclass(clonesSuperclass);

            // add equivalent classes
            if (sourceSuperclass.getSuperclasses(false).contains(source)) {
                clonesSuperclass.addSuperclass(clone);
            }
        }

        // remove owl:Thing parent unless specified in original
        if (!sourceSuperclasses.contains(owlModel.getOWLThingClass())) {
            clone.removeSuperclass(owlModel.getOWLThingClass());
        }

        return clone;
    }
}
