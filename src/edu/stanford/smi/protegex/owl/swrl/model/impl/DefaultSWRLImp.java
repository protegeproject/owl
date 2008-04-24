
package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.swrl.model.impl.SWRLUtil;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DefaultSWRLImp extends DefaultOWLIndividual implements SWRLImp 
{
  public static final String EMPTY_RULE_TEXT = "<EMPTY_RULE>";
  private boolean isEnabled;

  public DefaultSWRLImp(KnowledgeBase kb, FrameID id) 
  {
    super(kb, id);
    isEnabled = getIsEnabledAnnotation();
  } // DefaultSWRLImp

  public DefaultSWRLImp() 
  {
    isEnabled = getIsEnabledAnnotation();
  } // DefaultSWRLImp

  public SWRLImp createClone() {
    String text = getBrowserText();
    OWLModel owlModel = (OWLModel) getKnowledgeBase();
    SWRLParser parser = new SWRLParser(owlModel);
    parser.setParseOnly(false);
    try {
      return parser.parse(text);
    }
    catch (Exception ex) {
      return null;  // Cannot happen
    }
  }

  private boolean getIsEnabledAnnotation()
  {
    OWLProperty property = getOWLModel().getOWLProperty(SWRLNames.Annotations.IS_ENABLED);
    if (property == null) return true;
    Object value = getPropertyValue(property);
    if (value == null) return true;
    if (!(value instanceof Boolean)) return true; // Should not happen
    
    return ((Boolean)value).booleanValue();
  } // getIsEnabledAnnotation

    private void deleteHeadAndBody() {
        Slot directInstancesSlot = getKnowledgeBase().getSlot(Model.Slot.DIRECT_INSTANCES);
        Collection instances = getReferencedInstances();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof SWRLIndividual && !(o instanceof SWRLBuiltin)) {
                SWRLIndividual swrlIndividual = (SWRLIndividual) o;
                if (!swrlIndividual.isDeleted()) {
                    Collection references = getKnowledgeBase().getReferences(swrlIndividual, -1);
                    boolean hasExternalRef = false;
                    for (Iterator rit = references.iterator(); rit.hasNext();) {
                        Reference reference = (Reference) rit.next();
                        if (!directInstancesSlot.equals(reference.getSlot())) {
                            Frame frame = reference.getFrame();
                            if (!instances.contains(frame) && !equals(frame)) {
                                hasExternalRef = true;
                            }
                        }
                    }
                    if (!hasExternalRef) {
                        swrlIndividual.delete();
                    }
                }
            }
        }
    }


    public void deleteImp() {
        deleteHeadAndBody();
        delete();
    }


    public SWRLAtomList getHead() {
        return (SWRLAtomList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.HEAD));
    } // getHead


    public Icon getIcon() {
        return isEditable() ? SWRLIcons.getImpIcon() :
                OWLIcons.getReadOnlyIcon(SWRLIcons.getImpIcon(), "RoundedBoxFrame");
    }


  public Set getReferencedInstances() 
  {
    Set set = new HashSet();
    getReferencedInstances(set);
    return set;
  }

  public void getReferencedInstances(Set set) 
  {
    SWRLAtomList head = getHead();
    if (head != null) {
      set.add(head);
      head.getReferencedInstances(set);
    }
    SWRLAtomList body = getBody();
    if (body != null) {
      set.add(body);
      body.getReferencedInstances(set);
    }
  }

  public SWRLAtomList getBody() 
  {
    return (SWRLAtomList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BODY));
  } // getBody

  public void setBody(SWRLAtomList swrlAtomList) 
  {
    setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BODY), swrlAtomList);
  } // setBody
  
  public String getBrowserText() 
  {
    SWRLAtomList body = getBody();
    SWRLAtomList head = getHead();
    String s = "";

    if (head == null && body == null) {
      s += EMPTY_RULE_TEXT;
    } else {
      s += SWRLUtil.getSWRLBrowserText(body, "BODY");
      s += " " + SWRLParser.IMP_CHAR + " ";
      s += SWRLUtil.getSWRLBrowserText(head, "HEAD");
    } // if

    return s;
  } // toString

  public void setHead(SWRLAtomList swrlAtomList) 
  {
    setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.HEAD), swrlAtomList);
  } // setHead

  public void setExpression(String parsableText) throws SWRLParseException 
  {
    SWRLParser parser = new SWRLParser(getOWLModel());
    parser.parse(parsableText);
    deleteHeadAndBody();
    parser.setParseOnly(false);
    parser.parse(parsableText, this);
  }

  public boolean isEnabled() 
  { 
    return isEnabled;
  } // isEnabled

  public void enable() 
  { 
    OWLProperty property = getOWLModel().getOWLProperty(SWRLNames.Annotations.IS_ENABLED);

    if (property != null) setPropertyValue(property, Boolean.TRUE);
    
    isEnabled = true;
  } // enable

  public void disable() 
  { 
    OWLProperty property = getOWLModel().getOWLProperty(SWRLNames.Annotations.IS_ENABLED);

    if (property != null) setPropertyValue(property, Boolean.FALSE);

    isEnabled = false;
  } // disable

} // DefaultSWRLImp
