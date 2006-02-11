package edu.stanford.smi.protegex.owl.model.impl;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

import java.util.*;

/**
 * A NamespaceManager that maintains the prefix information by means of the values
 * of the corresponding slot in the default OWLOntology of the knowledge base.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLNamespaceManager extends AbstractNamespaceManager {

    public final static String DEFAULT_DEFAULT_BASE = "http://www.owl-ontologies.com/unnamed.owl";

    public final static String DEFAULT_DEFAULT_NAMESPACE = DEFAULT_DEFAULT_BASE + "#";

    private String defaultNamespace;

    private Slot prefixesSlot;

    private Map namespace2Prefix = new HashMap();

    private Map prefix2Namespace = new HashMap();

    private Collection systemPrefixes = new ArrayList();


    public OWLNamespaceManager() {
    }


    private void addPrefixes(Instance ontology, boolean isDefaultOntology) {
        Collection values = ontology.getDirectOwnSlotValues(prefixesSlot);
        for (Iterator vit = values.iterator(); vit.hasNext();) {
            String value = (String) vit.next();
            int index = value.indexOf(':');
            if (index > 0 || isDefaultOntology) {
                String prefix = value.substring(0, index);
                String namespace = value.substring(index + 1);
                addPrefix(prefix, namespace);
            }
        }
    }


    private void addPrefix(String prefix, String namespace) {
        if (!prefix2Namespace.containsKey(prefix)) { // && !namespace2Prefix.containsKey(namespace)) {
            prefix2Namespace.put(prefix, namespace);
        }
        namespace2Prefix.put(namespace, prefix);
    }


    public String getDefaultNamespace() {
        return defaultNamespace;
    }


    public String getNamespaceForPrefix(String prefix) {
        return (String) prefix2Namespace.get(prefix);
    }


    public String getPrefix(String namespace) {
        return (String) namespace2Prefix.get(namespace);
    }


    public Collection getPrefixes() {
        Collection results = new HashSet(prefix2Namespace.keySet());
        results.remove("");
        return results;
    }


    public void init(OWLModel owlModel) {
        super.init(owlModel);
        prefixesSlot = ((KnowledgeBase) owlModel).getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
        update();
    }


    public boolean isModifiable(String prefix) {
        return !systemPrefixes.contains(prefix);
    }


    public static boolean isValidPrefix(String prefix) {
        if (prefix.length() == 0) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(prefix.charAt(0))) {
            return false;
        }
        for (int i = 1; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!Character.isJavaIdentifierPart(c) && c != '.' && c != '-') {
                return false;
            }
        }
        return true;
    }


    public void removePrefix(String prefix) {
        removePrefixHelper(prefix);
        update();
        for (Iterator it = new ArrayList(getListeners()).iterator(); it.hasNext();) {
            NamespaceManagerListener listener = (NamespaceManagerListener) it.next();
            listener.prefixRemoved(prefix);
        }
    }


    private void removePrefixHelper(String prefix) {
	    String value = prefix + ":" + getNamespaceForPrefix(prefix);
	    for(Iterator it = owlModel.getOWLOntologies().iterator(); it.hasNext(); ) {
		    Instance curOntology = (Instance) it.next();
		    curOntology.removeOwnSlotValue(prefixesSlot, value);
	    }
    }


    public void setDefaultNamespace(String value) {
        String oldValue = getDefaultNamespace();
        defaultNamespace = value;
        OWLOntology oi = owlModel.getDefaultOWLOntology();
        setDefaultNamespace(oi, value);
        for (Iterator it = new ArrayList(getListeners()).iterator(); it.hasNext();) {
            NamespaceManagerListener listener = (NamespaceManagerListener) it.next();
            listener.defaultNamespaceChanged(oldValue, value);
        }
    }


    private void setDefaultNamespace(OWLOntology oi, String value) {
        Collection values = ((Instance) oi).getDirectOwnSlotValues(prefixesSlot);
        for (Iterator it = values.iterator(); it.hasNext();) {
            String str = (String) it.next();
            if (str.startsWith(":")) {
                ((Instance) oi).removeOwnSlotValue(prefixesSlot, str);
                break;
            }
        }
        ((Instance) oi).addOwnSlotValue(prefixesSlot, ":" + value);

        update();
    }


    public void setModifiable(String prefix, boolean value) {
        if (value) {
            systemPrefixes.remove(prefix);
        }
        else {
            systemPrefixes.add(prefix);
        }
    }


    public void setPrefix(String namespace, String prefix) {
        final TripleStoreModel tsm = owlModel.getTripleStoreModel();
        TripleStore oldActiveTripleStore = tsm.getActiveTripleStore();
        tsm.setActiveTripleStore(tsm.getTopTripleStore());
        String oldPrefix = getPrefix(namespace);
        String oldNamespace = getNamespaceForPrefix(prefix);
        String value = prefix + ":" + namespace;
        OWLOntology oi = owlModel.getDefaultOWLOntology();
        if (oldNamespace != null) {
            removePrefixHelper(prefix);
            ((Instance) oi).addOwnSlotValue(prefixesSlot, value);
            tsm.setActiveTripleStore(oldActiveTripleStore);
            update();
            for (Iterator it = new ArrayList(getListeners()).iterator(); it.hasNext();) {
                NamespaceManagerListener listener = (NamespaceManagerListener) it.next();
                listener.namespaceChanged(prefix, oldNamespace, namespace);
            }
        }
        else if (oldPrefix != null) {
            removePrefixHelper(oldPrefix);
            ((Instance) oi).addOwnSlotValue(prefixesSlot, value);
            tsm.setActiveTripleStore(oldActiveTripleStore);
            update();
            for (Iterator it = new ArrayList(getListeners()).iterator(); it.hasNext();) {
                NamespaceManagerListener listener = (NamespaceManagerListener) it.next();
                listener.prefixChanged(namespace, oldPrefix, prefix);
            }
        }
        else {
            ((Instance) oi).addOwnSlotValue(prefixesSlot, value);
            tsm.setActiveTripleStore(oldActiveTripleStore);
            update();
            for (Iterator it = new ArrayList(getListeners()).iterator(); it.hasNext();) {
                NamespaceManagerListener listener = (NamespaceManagerListener) it.next();
                listener.prefixAdded(prefix);
            }
        }
    }


    public void update() {
        prefix2Namespace.clear();
        namespace2Prefix.clear();
        installDefaultNamespaces();
        Collection ontologies = owlModel.getOWLOntologies();
        Instance defaultOntology = null;
        TripleStoreModel tsm = owlModel.getTripleStoreModel();
        if (!ontologies.isEmpty()) {
            if (tsm != null && owlModel instanceof JenaOWLModel) {
                TripleStore tripleStore = tsm.getTopTripleStore();
                defaultOntology = TripleStoreUtil.getFirstOntology(owlModel, tripleStore);
            }
            else {
                defaultOntology = (Instance) ontologies.iterator().next();
            }
        }

        // Ensure that default ontology is handled first in case prefixes contradict below
        if (defaultOntology != null) {
            addPrefixes(defaultOntology, true);
            ontologies = new ArrayList(ontologies);
            ontologies.remove(defaultOntology);
        }
        for (Iterator it = ontologies.iterator(); it.hasNext();) {
            Instance ontology = (Instance) it.next();
            addPrefixes(ontology, false);
        }
        defaultNamespace = getNamespaceForPrefix("");
        if (defaultNamespace == null) {
            defaultNamespace = OWLNamespaceManager.DEFAULT_DEFAULT_NAMESPACE;
        }
    }


    private void installDefaultNamespaces() {
        addPrefix("owl", OWL.getURI());
        addPrefix("rdf", RDF.getURI());
        addPrefix("rdfs", RDFS.getURI());
        addPrefix("xsd", XSD.anyURI.getNameSpace());
    }
}
