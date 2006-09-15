package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class DuplicateSuperclassesPostProcessor {


    DuplicateSuperclassesPostProcessor(OWLModel owlModel) {
        if (owlModel.getOWLFrameStore() != null) {
          owlModel.getOWLFrameStore().setSuperclassSynchronizationBlocked(true);
        }
        Collection clses = owlModel.getUserDefinedOWLNamedClasses();
        Iterator it = clses.iterator();
        while (it.hasNext()) {
            OWLNamedClass cls = (OWLNamedClass) it.next();
            removeDuplicateSuperclasses(cls);
        }
        if (owlModel.getOWLFrameStore() != null) {
          owlModel.getOWLFrameStore().setSuperclassSynchronizationBlocked(false);
        }
    }


    private void removeDuplicateSuperclasses(RDFSNamedClass cls) {
        List superclasses = new ArrayList(cls.getPureSuperclasses());
        Set bs = new HashSet();
        boolean hasDuplicates = false;
        for (Iterator it = superclasses.iterator(); it.hasNext();) {
            Cls superclass = (Cls) it.next();
            if (superclass instanceof OWLAnonymousClass) {
                String bt = superclass.getBrowserText();
                if (bs.contains(bt)) {
                    System.err.println("[DuplicateSuperclassesPostProcessor]  Class " + cls.getBrowserText() +
                            " has duplicate superclass " + bt);
                    hasDuplicates = true;
                    break;
                }
                else {
                    bs.add(bt);
                }
            }
        }
        if (hasDuplicates) {
            for (int i = 0; i < superclasses.size() - 1; i++) {
                if (superclasses.get(i) instanceof OWLAnonymousClass) {
                    OWLAnonymousClass anon = (OWLAnonymousClass) superclasses.get(i);
                    String browserTextI = anon.getBrowserText();
                    for (int j = superclasses.size() - 1; j > i; j--) {
                        if (superclasses.get(j).getClass() == anon.getClass()) {
                            OWLAnonymousClass classJ = (OWLAnonymousClass) superclasses.get(j);
                            String browserTextJ = classJ.getBrowserText();
                            if (browserTextJ.equals(browserTextI)) {
                                ((Cls) cls).removeDirectSuperclass(classJ);
                                superclasses.remove(j);
                            }
                        }
                    }
                }
            }
        }
    }
}
