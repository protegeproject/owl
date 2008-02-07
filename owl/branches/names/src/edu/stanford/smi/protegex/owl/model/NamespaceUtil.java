package edu.stanford.smi.protegex.owl.model;

import com.hp.hpl.jena.rdf.model.impl.Util;

import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public class NamespaceUtil {

	public static String getNameSpace(String fullURI) { 	
		return fullURI.substring(0, Util.splitNamespace(fullURI) );
	}

	
	public static String getLocalName(String fullURI)	{  		
		return fullURI.substring(Util.splitNamespace(fullURI) );
	}
	
	public static String getNamespacePrefix(OWLModel owlModel, String fullURI) {
	    String namespace = getNameSpace(fullURI);
	    return owlModel.getNamespaceManager().getPrefix(namespace);
	}
	
	public static String getPrefixedName(OWLModel owlModel, String fullURI)	{		
		String uri = getNameSpace(fullURI);
		
		if (uri == null) {
			return fullURI;
		}
		
		String prefix = owlModel.getNamespaceManager().getPrefix(uri);
		String localName = getLocalName(fullURI);
		
		if (prefix == null) {
			return fullURI; //do we want another strategy here?
		} else if (prefix.equals(OWLNamespaceManager.DEFAULT_NAMESPACE_PREFIX)) {
			return localName;
		} else {
			return prefix + ":" + localName; 
		}			
	}
	
	public static String getFullName(OWLModel owlModel, String prefixedName) {
		int ind = prefixedName.indexOf(":");
		
		NamespaceManager nsm = owlModel.getNamespaceManager();
		
		if (ind == -1) { //no ":" in the prefixed name, this is a name in the default namespace
			String defaultNamespace = nsm.getDefaultNamespace();
			
			if (defaultNamespace == null) { 
				return null;
			}
			
			return defaultNamespace + prefixedName;			
		}
						
		if (ind == 0) {//e.g. ":protege" - what to do in this case?
			//TODO: what should happen in this case?
			return null;
		}
		
		if (ind == prefixedName.length() - 1) { //e.g. "p1:"
			//TODO: what should happen in this case?
			return null;
		}
		
		String prefix = prefixedName.substring(0, ind);
		String localName = prefixedName.substring(ind + 1);
		
		String namespace = nsm.getNamespaceForPrefix(prefix);
		
		if (namespace == null) { // no namespace for prefix defined
			return null;
		}
		
		return namespace + localName;
				
	}
	
}
