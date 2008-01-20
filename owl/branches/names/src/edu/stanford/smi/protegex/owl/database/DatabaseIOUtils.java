package edu.stanford.smi.protegex.owl.database;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.AmalgamatedIOException;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

/**
 * This class is for internal use only.  It is used during the loading, saving and importing
 * of database models.
 * 
 * @author tredmond
 *
 */
public class DatabaseIOUtils {
    private static transient final Logger log = Log.getLogger(DatabaseIOUtils.class);
    
    public static void readOWLOntologyFromDatabase(OWLModel owlModel, TripleStore tripleStore) {
        NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        RDFProperty owlOntologyPointerProperty = systemFrames.getOwlOntologyPointerProperty();
        RDFIndividual owlOntologyPointerInstance = getOwlOntologyPointerInstance(owlModel, nfs);
        OWLOntology  ontology = (OWLOntology) owlOntologyPointerInstance.getPropertyValue(owlOntologyPointerProperty);
        tripleStore.setName(ontology.getName());
    }
    
    public static void writeOWLOntologyToDatabase(OWLModel owlModel, TripleStore tripleStore) {
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        try {
            tripleStoreModel.setActiveTripleStore(tripleStore);
            NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
            OWLSystemFrames systemFrames = owlModel.getSystemFrames();
            RDFProperty owlOntologyPointerProperty = systemFrames.getOwlOntologyPointerProperty();
            RDFIndividual owlOntologyPointerInstance = getOwlOntologyPointerInstance(owlModel, nfs);
            if (owlOntologyPointerInstance == null) {
                owlOntologyPointerInstance = (RDFIndividual) systemFrames.getOwlOntologyPointerClass().createInstance(null);
            }
            owlOntologyPointerInstance.setPropertyValue(owlOntologyPointerProperty, tripleStore.getOWLOntology());
        }
        finally {
            tripleStoreModel.setActiveTripleStore(activeTripleStore);
        }
    }
    
    private static  RDFIndividual getOwlOntologyPointerInstance(OWLModel owlModel, NarrowFrameStore nfs) {
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        RDFSNamedClass owlOntologyPointerClass = systemFrames.getOwlOntologyPointerClass();
        for (Object o : nfs.getValues(owlOntologyPointerClass, systemFrames.getDirectInstancesSlot(), null, false)) {
            return (RDFIndividual) o;
        }
        return null;
    }

    public static void loadPrefixesFromDB(OWLModel owlModel, TripleStore tripleStore, Collection errors) {
        OWLOntology ontology = tripleStore.getOWLOntology();
        RDFProperty prefixesProperty = owlModel.getSystemFrames().getOwlOntologyPrefixesProperty();
        NamespaceManager nm = tripleStore.getNamespaceManager();
        for (Object o : ontology.getPropertyValues(prefixesProperty)) {
            if (o instanceof String) {
                String encodedNamespaceEntry = (String) o;
                int index = encodedNamespaceEntry.indexOf(OWLDatabaseKnowledgeBaseFactory.NAMESPACE_PREFIX_SEPARATOR);
                if (index < 0) continue;
                String prefix = encodedNamespaceEntry.substring(0, index);
                String namespace = encodedNamespaceEntry.substring(index + 1);
                nm.setPrefix(namespace, prefix);
            }
        }
    }

    public static void writePrefixesToModel(OWLModel owlModel, TripleStore tripleStore) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Saving Prefixes to database, owl ontology = " + tripleStore.getOWLOntology());
            log.fine("prefixes = " + tripleStore.getNamespaceManager().getPrefixes());
        }    
        OWLOntology owlOntology = tripleStore.getOWLOntology();  
        RDFProperty prefixesProperty = owlModel.getSystemFrames().getOwlOntologyPrefixesProperty();
        NamespaceManager nm = tripleStore.getNamespaceManager();
        for (String prefix  : nm.getPrefixes()) {
            String namespace = nm.getNamespaceForPrefix(prefix);
            String value = prefix + OWLDatabaseKnowledgeBaseFactory.NAMESPACE_PREFIX_SEPARATOR + namespace;
            owlOntology.addPropertyValue(prefixesProperty, value);
        }
        
    }

    public static void loadImports(OWLModel owlModel, Collection errors) {
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        OWLOntology ontology = activeTripleStore.getOWLOntology();
        for (String imprt : ontology.getImports()) {
            try {
                 owlModel.addImport(URIUtilities.createURI(imprt));
            }
            catch (AmalgamatedIOException e) {
                errors.addAll(e.getErrorList());
            }
            catch (IOException e) {
                errors.add(e);
            }
        }
    }
}
