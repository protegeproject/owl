package edu.stanford.smi.protegex.owl.jena.parser;

import com.hp.hpl.jena.rdf.model.impl.Util;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.NewNamespaceManager;

public class NamespaceUtil {

	public static String getNameSpace(String fullURI) { 	
		return fullURI.substring(0, Util.splitNamespace(fullURI) );
	}

	
	public static String getLocalName(String fullURI)	{  		
		return fullURI.substring(Util.splitNamespace(fullURI) );
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
		} else if (prefix.equals(NewNamespaceManager.DEFAULT_NAMESPACE_PREFIX)) {
			return localName;
		} else {
			return prefix + ":" + localName; 
		}
			
	}
	
}
