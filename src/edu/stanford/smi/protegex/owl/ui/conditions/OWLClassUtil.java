package edu.stanford.smi.protegex.owl.ui.conditions;

import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public class OWLClassUtil {
	
	public static String printRDFList(RDFList rdfList) {
		String rdfString = new String("(");
		RDFList list = rdfList;
		
		while (list != null && list.getFirst() != null) {			
			rdfString = rdfString +  list.getFirst() + " ,";				
			list = list.getRest();
		}
		rdfString = rdfString + ")";
		
		return rdfString;
	}
	
	   public static boolean replaceOperand(OWLIntersectionClass definitionCls, RDFSClass oldClass, RDFSClass newClass) {		   
		   boolean replaced = false;
		   RDFList rdfList = (RDFList) definitionCls.getPropertyValue(definitionCls.getOperandsProperty());
			
			if (rdfList == null)
				return false;
						
			RDFList list = rdfList;
			System.out.println("before: " + printRDFList(rdfList));
			
			while (!replaced && list != null && list.getFirst() != null) {
				if (list.getFirst().equals(oldClass)) {					
					list.setFirst(newClass);					
					replaced = true;
					break;
				}
				list = list.getRest();
			}
			System.out.println("after: " + printRDFList(rdfList));
	        return replaced;
		}
	   
	
}
