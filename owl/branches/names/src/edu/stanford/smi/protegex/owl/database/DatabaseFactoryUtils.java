package edu.stanford.smi.protegex.owl.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.AmalgamatedIOException;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerAdapter;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
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
public class DatabaseFactoryUtils {
    private static transient final Logger log = Log.getLogger(DatabaseFactoryUtils.class);
    
    public static boolean readOWLOntologyFromDatabase(OWLModel owlModel, TripleStore tripleStore) {
        NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        RDFProperty owlOntologyPointerProperty = systemFrames.getOwlOntologyPointerProperty();
        RDFIndividual owlOntologyPointerInstance = getOwlOntologyPointerInstance(owlModel, nfs);
        if (owlOntologyPointerInstance != null) {
            OWLOntology  ontology = (OWLOntology) owlOntologyPointerInstance.getPropertyValue(owlOntologyPointerProperty);
            tripleStore.setName(ontology.getName());
            return true;
        }
        else {
            return false;
        }
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
                String[] splitNamespaceEntry = splitEncodedNamespaceEntry(encodedNamespaceEntry);
                if (splitNamespaceEntry == null) {
                    continue;
                }
                String prefix = splitNamespaceEntry[0];
                String namespace = splitNamespaceEntry[1];
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
        Iterator it = tripleStore.listObjects(owlOntology, prefixesProperty); 
        while (it.hasNext()) {
            Object o = it.next();
            tripleStore.remove(owlOntology, prefixesProperty, o);
        }
        for (String prefix  : nm.getPrefixes()) {
            String namespace = nm.getNamespaceForPrefix(prefix);
            String value = joinEncodedNamespaceEntry(prefix, namespace);
            tripleStore.add(owlOntology, prefixesProperty, value);
        }
        
    }
    
    public static void addPrefixesToModelListener(final OWLModel owlModel, final TripleStore tripleStore) {
        NamespaceManager nm  = tripleStore.getNamespaceManager();
        nm.addNamespaceManagerListener(new NamespaceManagerAdapter() {
            @Override
            public void namespaceChanged(String prefix, String oldNamespace, String newNamespace) {
                OWLOntology owlOntology = tripleStore.getOWLOntology();
                RDFProperty prefixesProperty = owlModel.getSystemFrames().getOwlOntologyPrefixesProperty();
                if (oldNamespace != null) {
                    tripleStore.remove(owlOntology, prefixesProperty, joinEncodedNamespaceEntry(prefix, oldNamespace));
                }
                if (newNamespace != null) {
                    tripleStore.add(owlOntology, prefixesProperty, joinEncodedNamespaceEntry(prefix, newNamespace));
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
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

    public static String getOntologyFromTable(Connection connection, String table) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            ResultSet results =  stmt.executeQuery(getOntologyQuery(table));
            try {
                while (results.next()) {
                    return results.getString(1);
                }
            }
            finally {
                results.close();
            }
        }
        finally {
            stmt.close();
        }
        return null;
    }
    
    private static String getOntologyQuery(String table) {
        StringBuffer sb = new StringBuffer();
        sb.append("select getOntology.short_value ");
        sb.append("from ");
        sb.append(table);
        sb.append(" as ontInstance join ");
        sb.append(table);
        sb.append(" as getOntology ");
        sb.append("on ontInstance.frame='");
        sb.append(OWLNames.Cls.OWL_ONTOLOGY_POINTER_CLASS);
        sb.append("' and ontInstance.slot='");
        sb.append(Model.Slot.DIRECT_INSTANCES);
        sb.append("' and getOntology.frame=ontInstance.short_value and getOntology.slot = '");
        sb.append(OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY);
        sb.append("';");
        return sb.toString();
    }
    
    public static String[] splitEncodedNamespaceEntry(String encoded) {
        int index = encoded.indexOf(OWLDatabaseKnowledgeBaseFactory.NAMESPACE_PREFIX_SEPARATOR);
        if (index < 0) return null;
        String prefix = encoded.substring(0, index);
        String namespace = encoded.substring(index + 1);
        return new String[] {prefix, namespace};
    }
    
    public static String joinEncodedNamespaceEntry(String prefix, String namespace) {
        return prefix + OWLDatabaseKnowledgeBaseFactory.NAMESPACE_PREFIX_SEPARATOR + namespace;
    }
}
