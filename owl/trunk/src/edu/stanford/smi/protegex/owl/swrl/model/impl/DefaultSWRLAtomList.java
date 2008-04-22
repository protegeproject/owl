package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.impl.SWRLUtil;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;

import java.util.Iterator;
import java.util.Set;

public class DefaultSWRLAtomList extends DefaultRDFList implements SWRLAtomList 
{
  public DefaultSWRLAtomList(KnowledgeBase kb, FrameID id) {
    super(kb, id);
  } // DefaultSWRLAtomList

  public DefaultSWRLAtomList() {}

  public String getBrowserText() 
  {
    String s = "";

    if (getValues() != null) {
      Iterator iterator = getValues().iterator();
      while (iterator.hasNext()) {
        Instance instance = (Instance)iterator.next();
        s += SWRLUtil.getSWRLBrowserText((RDFObject)instance, "ATOM");
        if (iterator.hasNext()) s += "  " + SWRLParser.AND_CHAR + "  ";
      } // while
    } else s += "<DELETED_ATOM_LIST>";
    
    return s;
  } // getBrowserText

  public void getReferencedInstances(Set set) {
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

