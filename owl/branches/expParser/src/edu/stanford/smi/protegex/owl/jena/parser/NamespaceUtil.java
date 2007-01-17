package edu.stanford.smi.protegex.owl.jena.parser;

import com.hp.hpl.jena.rdf.model.impl.Util;

public class NamespaceUtil {

	public static String getNameSpace(String fullURI) { 	
		return fullURI.substring(0, Util.splitNamespace(fullURI) );
	}

	
	public static String getLocalName(String fullURI)	{  		
		return fullURI.substring(Util.splitNamespace(fullURI) );
	}
	
}
