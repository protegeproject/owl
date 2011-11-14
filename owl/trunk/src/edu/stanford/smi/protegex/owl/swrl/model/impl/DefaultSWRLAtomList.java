package edu.stanford.smi.protegex.owl.swrl.model.impl;

import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLNames;

public class DefaultSWRLAtomList extends DefaultRDFList implements SWRLAtomList 
{
	private boolean isInHead = false;
	
  public DefaultSWRLAtomList(KnowledgeBase kb, FrameID id) {
    super(kb, id);
  } // DefaultSWRLAtomList

  public DefaultSWRLAtomList() {}

  public void setInHead(boolean isInHead) { this.isInHead = isInHead; }
  
  public String getBrowserText() 
  {
    String s = "", atomText;
    boolean atomProcessed = false;
    boolean setBuildEncountered = false;
    boolean setOperationEncountered = false;
    int currentColumn = 0, atomTextWidth;
    final int maxColumnWidth = 120;
    
    if (isInHead) s += "\n";
    
    if (getValues() != null) {
      Iterator<?> iterator = getValues().iterator();
      while (iterator.hasNext()) {
    	  Instance instance = (Instance)iterator.next();
  			atomText = SWRLUtil.getSWRLBrowserText((RDFObject)instance, "ATOM");
  			atomTextWidth = atomText.length();

        if (instance instanceof SWRLBuiltinAtom) {
        	SWRLBuiltin builtIn = ((SWRLBuiltinAtom)instance).getBuiltin();
        
        	if (builtIn == null) { 
        		s += "\n<DELETED_BUILTIN>\n"; currentColumn = 0;  
        	} else {	
	        	String builtInName = builtIn.getName();
	    		
	    			if (!isInHead && (SQWRLNames.isSQWRLCollectionMakeBuiltIn(builtInName) || SQWRLNames.isSQWRLCollectionGroupByBuiltIn(builtInName)) && !setBuildEncountered) {
	    				setBuildEncountered = true;
	    				if (currentColumn + 2 >= maxColumnWidth) { s += "\n" + SWRLParser.RING_CHAR + "  "; currentColumn = 2; }
	    				else { s += " " + SWRLParser.RING_CHAR + " \n"; currentColumn = 0; }
	        			
	    				if ((currentColumn + atomTextWidth) >= maxColumnWidth) { s += "\n"; currentColumn = atomTextWidth; }
	    				else currentColumn += atomTextWidth;
	    				s += atomText;
	    			} else if (!isInHead && SQWRLNames.getCollectionOperationBuiltInNames().contains(builtInName) && !setOperationEncountered && atomProcessed) {
	    				setOperationEncountered = true;
	    				if (currentColumn + 2 >= maxColumnWidth) { s += "\n" + SWRLParser.RING_CHAR + "  "; currentColumn = 3; }
	    				else { s += " " + SWRLParser.RING_CHAR + " \n"; currentColumn = 0; }
	        			
	    				if ((currentColumn + atomTextWidth) >= maxColumnWidth) { s += "\n"; currentColumn = atomTextWidth; }
	    				else currentColumn += atomTextWidth;
	    				s += atomText;
	    			} else { // Non SQWRL make/group/operation built-in
	    				if (atomProcessed) {
	    				  if (currentColumn + 2 >= maxColumnWidth) { s += "\n" + SWRLParser.AND_CHAR + "  "; currentColumn = 2; }
	    				  else { s += " " + SWRLParser.AND_CHAR + " "; currentColumn += 3; }
	    				} // if
	        			
	    				if ((currentColumn + atomTextWidth) >= maxColumnWidth) { s += "\n"; currentColumn = atomTextWidth; }
	    				else currentColumn += atomTextWidth;
	    				s += atomText;
	    			} // if
        	} // if
        } else { // Not a built-in atom
        	if (atomProcessed) { 
      			if (currentColumn + 2 >= maxColumnWidth) { s += "\n" + SWRLParser.AND_CHAR + "  "; currentColumn = 2; }
      			else { s += " " + SWRLParser.AND_CHAR + " "; currentColumn += 3; }
        	} // if

  				if ((currentColumn + atomTextWidth) >= maxColumnWidth) { s += "\n"; currentColumn = atomTextWidth; }
  				else currentColumn += atomTextWidth;
  				s += atomText;
        } // if
        atomProcessed = true;
      } // while
    } else s += "<DELETED_ATOM_LIST>";
    
    return s;
  } // getBrowserText

  public void getReferencedInstances(Set<RDFResource> set) {
    final OWLModel owlModel = getOWLModel();
    RDFList li = this;
    while (li != null && !li.equals(owlModel.getRDFNil())) {
      set.add(li);
      Object value = li.getFirst();
      if (value instanceof SWRLIndividual) {
        SWRLIndividual individual = (SWRLIndividual) value;
        set.add(individual);
        individual.getReferencedInstances(set);
      }
      li = li.getRest();
    }
  }
  
  @Override
  public void accept(OWLModelVisitor visitor) {
	 visitor.visitSWRLAtomListIndividual(this);
  }
  
} // DefaultSWRLAtomList

