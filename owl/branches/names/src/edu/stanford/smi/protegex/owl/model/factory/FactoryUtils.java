package edu.stanford.smi.protegex.owl.model.factory;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerAdapter;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public class FactoryUtils {

    public static void loadEncodedNamespaceFromModel(OWLModel owlModel, TripleStore tripleStore, Collection errors) {
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

    public static void encodeNamespaceIntoModel(OWLModel owlModel, TripleStore tripleStore) {
        if (DatabaseFactoryUtils.log.isLoggable(Level.FINE)) {
            DatabaseFactoryUtils.log.fine("Saving Prefixes to database, owl ontology = " + tripleStore.getOWLOntology());
            DatabaseFactoryUtils.log.fine("prefixes = " + tripleStore.getNamespaceManager().getPrefixes());
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
