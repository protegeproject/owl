package edu.stanford.smi.protegex.owl.model.factory;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerAdapter;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.metadatatab.OntologyURIPanel;

public class FactoryUtils {
    
    public static void addOntologyToTripleStore(OWLModel owlModel, TripleStore tripleStore, String ontologyName) {
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        try {
            tripleStoreModel.setActiveTripleStore(tripleStore);
            owlModel.getOWLOntologyClass().createInstance(ontologyName);
            tripleStore.setName(ontologyName);
            NamespaceManager names = tripleStore.getNamespaceManager();
            if (names.getDefaultNamespace() == null) {
                names.setDefaultNamespace(ontologyName + "#");
            }
            if (tripleStore.getOriginalXMLBase() == null) {
                tripleStore.setOriginalXMLBase(ontologyName);
            }
        }
        finally {
            tripleStoreModel.setActiveTripleStore(activeTripleStore);
        }
        owlModel.resetOntologyCache();
    }

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

    public static String getOntologyURIBase(String defaultBase,
                                            boolean appendYear,
                                            boolean appendMonth,
                                            boolean appendDay) {
        if (defaultBase != null && defaultBase.trim().length() > 0) {
            if (defaultBase.endsWith("/") == false) {
                defaultBase += "/";
            }
            if (appendYear) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                defaultBase += year + "/";
                if (appendMonth) {
                    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                    defaultBase += month + "/";
                    if (appendDay) {
                        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        defaultBase += day + "/";
                    }
                }
            }
        }
        return defaultBase;
    }

    public static String generateOntologyURIBase() {
        String defaultBase = ApplicationProperties.getString(OntologyURIPanel.URI_BASE_PROPERTY);
        if (defaultBase == null) {
            defaultBase = OntologyURIPanel.DEFAULT_BASE;
            ApplicationProperties.setString(OntologyURIPanel.URI_BASE_PROPERTY, OntologyURIPanel.DEFAULT_BASE);
        }
        defaultBase = getOntologyURIBase(defaultBase,
                                         ApplicationProperties.getBooleanProperty(OntologyURIPanel.URI_BASE_APPEND_YEAR_PROPERTY, false),
                                         ApplicationProperties.getBooleanProperty(OntologyURIPanel.URI_BASE_APPEND_MONTH_PROPERTY, false),
                                         ApplicationProperties.getBooleanProperty(OntologyURIPanel.URI_BASE_APPEND_DAY_PROPERTY, false));
        String fileName = "Ontology";
        fileName += System.currentTimeMillis() / 1000;
        fileName += ".owl";
        return defaultBase + fileName;
    }

}
