package edu.stanford.smi.protegex.owl.model;

import com.hp.hpl.jena.rdf.model.impl.Util;

import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;

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
			return fullURI;
		} else if (prefix.equals(OWLNamespaceManager.DEFAULT_NAMESPACE_PREFIX)) {
			return localName;
		} else {
			return prefix + ":" + localName; 
		}
			
	}
	
}
