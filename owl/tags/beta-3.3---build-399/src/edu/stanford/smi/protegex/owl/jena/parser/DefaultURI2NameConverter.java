package edu.stanford.smi.protegex.owl.jena.parser;

import com.hp.hpl.jena.rdf.arp.AResource;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultURI2NameConverter implements URI2NameConverter {

    private int externalResourceIndex = 1;

    private boolean firstDefaultNamespaceHandled = false;

    private ProtegeOWLParserLogger logger;

    private OWLModel owlModel;

    private Map prefix2URI = new HashMap();

    private Map uri2Prefix = new HashMap();

    private Set ontologyURIs;

    private static final String ANONYMOUS_PREFIX = AbstractOWLModel.ANONYMOUS_BASE;

    private static final String EXTERNAL_RESOURCE_PREFIX = "&";

    private static final String TEMPORARY_PREFIX = "^";

    private static final String PREFIX_BASE = "p";


    public DefaultURI2NameConverter(OWLModel owlModel, ProtegeOWLParserLogger logger, boolean incremental) {
        this.owlModel = owlModel;
        this.logger = logger;
        firstDefaultNamespaceHandled = incremental;
        updateInternalState();
        initPrefixesFromOWLModel();
    }


    public String addPrefix(String uri, String prefix) {
        String existingURI = getNamespace(prefix);
        if (existingURI != null) {
            if (prefix.length() == 0) { // New default namespace
                if (!firstDefaultNamespaceHandled) {
                    addPrefixHelper(uri, prefix);
                    firstDefaultNamespaceHandled = true;
                }
            }
            else if (!uri.equals(existingURI)) {
                logger.logWarning("Conflicting prefix found " + prefix + ":"
                        + "\n  Existing namespace: " + existingURI
                        + "\n  New namespace: " + uri);
            }
        }
        else {
            String existingPrefix = getPrefix(uri);
            if (existingPrefix != null) {
                if (!existingPrefix.equals(prefix)) {
                    if (isPreferredPrefix(prefix, uri)) { // For example, ruleml overloaded
                        addPrefixHelper(uri, prefix);
                    }
                    else {
                        if (existingPrefix.length() > 0) {
                            logger.logWarning("Ignoring namespace prefix \"" + prefix +
                                    "\" for existing prefix \"" + existingPrefix + "\"");
                        }
                        return existingPrefix;
                    }
                }
            }
            else {
                addPrefixHelper(uri, prefix);
            }
        }
        return prefix;
    }


    private void addPrefixHelper(String uri, String prefix) {
        uri2Prefix.put(uri, prefix);
        prefix2URI.put(prefix, uri);
        // System.out.println("Registered " + prefix + " for " + uri);
    }


    public String createAnonymousRDFResourceName() {
        return owlModel.getNextAnonymousResourceName();
    }


    private String createNewPrefixHelper(String namespace) {

        String prefix;
        int index = 1;
        do {
            prefix = PREFIX_BASE + index;
            index++;
        }
        while (uri2Prefix.containsValue(prefix));

        return prefix;
    }


    public String createNewPrefix(String uri) {
        //if (!uri.endsWith("/") && !uri.endsWith("#")) {
        //    uri += "#";
        //}

        int index = com.hp.hpl.jena.rdf.model.impl.Util.splitNamespace(uri);
        String namespace = uri.substring(0, index);

        String prefix = createNewPrefixHelper(namespace);
        // owlModel.getNamespaceManager().setPrefix(namespace, prefix);
        addPrefixHelper(namespace, prefix);
        return prefix;
    }


    private String getNamespace(String prefix) {
        return (String) prefix2URI.get(prefix);
    }


    private String getPrefix(String uri) {
        return (String) uri2Prefix.get(uri);
    }


    public String getRDFExternalResourceName() {
        for (; ;) {
            String name = EXTERNAL_RESOURCE_PREFIX + externalResourceIndex++;
            if (((KnowledgeBase) owlModel).getFrame(name) == null) {
                return name;
            }
        }
    }


    public String getRDFResourceName(String uri) {
        if (ontologyURIs.contains(uri)) {
            uri = Jena.getNamespaceFromURI(uri);
            String prefix = getPrefix(uri);
            if (prefix == null) {
                return null;
            }
            else {
                return prefix + ":";
            }
        }
        else {
            int lindex = uri.lastIndexOf('#');
            if (lindex > 0 && lindex < uri.length() - 1) {
                String l = uri.substring(lindex + 1);
                if (!owlModel.isValidResourceName(l, null)) {
                    l = AbstractOWLModel.getValidOWLFrameName(null, l);
                    uri = uri.substring(0, lindex + 1) + l;
                }
            }
            int index = com.hp.hpl.jena.rdf.model.impl.Util.splitNamespace(uri);
            String namespace = uri.substring(0, index);
            String localName = uri.substring(index);
            if (ProtegeNames.NS.equals(namespace) && localName.length() > 0) {
                final String name = ":" + localName; // System frame like :FROM
                if (((KnowledgeBase) owlModel).getFrame(name) != null) {
                    return name;
                }
            }
            String prefix = getPrefix(namespace);
            if (prefix == null) {
                return null;
            }
            else {
                if (prefix.length() == 0) {
                    if (localName.length() == 0) {
                        return ":";
                    }
                    else {
                        return localName;
                    }
                }
                else {
                    return prefix + ":" + localName;
                }
            }
        }
    }


    public String getResourceNamespace(String uri) {
        int index = com.hp.hpl.jena.rdf.model.impl.Util.splitNamespace(uri);
        return uri.substring(0, index);
    }


    public String getTemporaryRDFResourceName(String uri) {
        return TEMPORARY_PREFIX + uri;
    }


    public String getTemporaryRDFResourceName(AResource node) {
        if (node.isAnonymous()) {
            return ANONYMOUS_PREFIX + node.toString();
        }
        else {
            return getTemporaryRDFResourceName(node.getURI());
        }
    }


    public String getURIFromTemporaryName(String temporaryName) {
        return temporaryName.substring(TEMPORARY_PREFIX.length());
    }


    private void initPrefixesFromOWLModel() {
        NamespaceManager nsm = owlModel.getNamespaceManager();
        addPrefixHelper(nsm.getDefaultNamespace(), "");
        for (Iterator it = nsm.getPrefixes().iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            String namespace = nsm.getNamespaceForPrefix(prefix);
            addPrefixHelper(namespace, prefix);
        }
    }


    public boolean isAnonymousRDFResourceName(String name) {
        return name.startsWith(ANONYMOUS_PREFIX);
    }


    protected boolean isPreferredPrefix(String prefix, String uri) {
        return
        	prefix.equals(ProtegeNames.PROTEGE_PREFIX) ||
        	prefix.equals(OWLNames.OWL_PREFIX) ||
            prefix.equals(RDFNames.RDF_PREFIX) ||
            prefix.equals(RDFSNames.RDFS_PREFIX) ||
            prefix.equals(SWRLNames.SWRL_PREFIX); // Override ugly "ruleml" prefix                  
    }


    public boolean isTemporaryRDFResourceName(String name) {
        return name.startsWith(TEMPORARY_PREFIX);
    }


    public void updateInternalState() {

        ontologyURIs = new HashSet();
        for (Iterator it = owlModel.getOWLOntologies().iterator(); it.hasNext();) {
            Object next = it.next();
            if (!(next instanceof RDFResource)) {
                next = new DefaultOWLOntology(owlModel, ((Frame) next).getFrameID());
            }
            RDFResource ontology = (RDFResource) next;
            String name = ontology.getName();
            if (name.length() > 0) {
                if (isTemporaryRDFResourceName(name)) {
                    String uri = getURIFromTemporaryName(name);
                    ontologyURIs.add(uri);
                }
                else {
                    String uri = ontology.getURI();
                    ontologyURIs.add(uri);
                }
            }
        }

        Collection prefixes = new ArrayList(owlModel.getNamespaceManager().getPrefixes());
        prefixes.add("");
        for (Iterator it = prefixes.iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            String ns = owlModel.getNamespaceManager().getNamespaceForPrefix(prefix);
            if (ns != null) {
                if (ns.endsWith("#") || ns.endsWith(":")) {
                    ns = ns.substring(0, ns.length() - 1);
                }
                ontologyURIs.add(ns);
            }
        }
    }
}
