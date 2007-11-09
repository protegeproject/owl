
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
import java.util.*;

public class DefaultSWRLImp extends AbstractSWRLIndividual implements SWRLImp 
{
  public static final String EMPTY_RULE_TEXT = "<EMPTY_RULE>";
  private boolean isRuleEnabled = true;
  private Map<String, Boolean> ruleGroups; // Contains rule groups and their enabled status.

  private OWLProperty ruleEnabledProperty, ruleGroupProperty, ruleGroupEnabledProperty;
  private OWLNamedClass ruleGroupClass;
 
  public DefaultSWRLImp(KnowledgeBase kb, FrameID id) 
  {
    super(kb, id);
    initialize();
  } // DefaultSWRLImp

  public DefaultSWRLImp() 
  {
    initialize();
  } // DefaultSWRLImp

  private void initialize()
  {
    ruleEnabledProperty = getOWLModel().getOWLProperty(SWRLNames.Annotations.IS_RULE_ENABLED);
    ruleGroupProperty = getOWLModel().getOWLProperty(SWRLNames.Annotations.HAS_RULE_GROUP);
    ruleGroupEnabledProperty = getOWLModel().getOWLProperty(SWRLNames.Annotations.IS_RULE_GROUP_ENABLED);
    ruleGroupClass = getOWLModel().getOWLNamedClass(SWRLNames.Annotations.RULE_GROUP);

    isRuleEnabled = getIsRuleEnabledAnnotation();
    ruleGroups = getRuleGroupAnnotations();
  } // initialize

  public SWRLImp createClone() 
  {
    String text = getBrowserText();
    OWLModel owlModel = (OWLModel)getKnowledgeBase();
    SWRLParser parser = new SWRLParser(owlModel);
    parser.setParseOnly(false);
    try {
      return parser.parse(text);
    } catch (Exception ex) {
      return null;  // Should not happen
    } // try
  } // createClone

  private boolean getIsRuleEnabledAnnotation()
  {
    if (ruleEnabledProperty == null) return true;
    Object value = getPropertyValue(ruleEnabledProperty);
    if (value == null) return true;
    if (!(value instanceof Boolean)) return true; // Should not happen
    
    return ((Boolean)value).booleanValue();
  } // getIsRuleEnabledAnnotation

  private Map<String, Boolean> getRuleGroupAnnotations()
  {
    Map<String, Boolean> result = new HashMap<String, Boolean>();

    if (ruleGroupProperty != null && ruleGroupEnabledProperty != null) {
      Collection values = getPropertyValues(ruleGroupProperty);
      if (values != null) {
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
          Boolean enabled = Boolean.TRUE;
          OWLIndividual ruleGroupIndividual = (OWLIndividual)iterator.next();
          Object isEnabled = ruleGroupIndividual.getPropertyValue(ruleGroupEnabledProperty);
          if (isEnabled != null) enabled = (Boolean)isEnabled;
          result.put(ruleGroupIndividual.getName(), enabled);
        } // while
      } // if
    } // if
    
    return result;
  } // getRuleGroupNamesAnnotation

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

  public SWRLAtomList getHead() { return (SWRLAtomList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.HEAD)); }
  public SWRLAtomList getBody() { return (SWRLAtomList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BODY)); }
  public void setBody(SWRLAtomList swrlAtomList) { setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BODY), swrlAtomList); } 
  public void setHead(SWRLAtomList swrlAtomList) { setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.HEAD), swrlAtomList); }

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
    return isRuleEnabled;
  } // isEnabled

  public void enable() { enable(new HashSet<String>()); }

  public void enable(String ruleGroupName)
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    enable(ruleGroupNames);
  } // enable

  public void enable(Set<String> ruleGroupNames) 
  { 
    if (ruleGroupNames.isEmpty()) {
      if (ruleEnabledProperty != null) setPropertyValue(ruleEnabledProperty, Boolean.TRUE);
      isRuleEnabled = true;
    } else {
      for (String ruleGroupName : ruleGroupNames) {
        OWLIndividual ruleGroupIndividual = getOWLModel().getOWLIndividual(ruleGroupName);
        if (ruleGroupIndividual != null && ruleGroupProperty != null) {
          addPropertyValue(ruleGroupProperty, ruleGroupIndividual);
          ruleGroupIndividual.addPropertyValue(ruleGroupEnabledProperty, Boolean.TRUE);
        }  // if
        ruleGroups.put(ruleGroupName, Boolean.TRUE);
      } // for
    } // if  
  } // enable

  public void disable() { disable(new HashSet<String>()); }

  public void disable(String ruleGroupName)
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    disable(ruleGroupNames);
  } // disable

  public void disable(Set<String> ruleGroupNames) 
  { 
    if (ruleGroupNames.isEmpty()) {
      if (ruleEnabledProperty != null) setPropertyValue(ruleEnabledProperty, Boolean.FALSE);
      isRuleEnabled = false;
    } else {
      for (String ruleGroupName : ruleGroupNames) {
        OWLIndividual ruleGroupIndividual = getOWLModel().getOWLIndividual(ruleGroupName);
        if (ruleGroupIndividual != null && ruleGroupProperty != null) {
          addPropertyValue(ruleGroupProperty, ruleGroupIndividual);
          ruleGroupIndividual.addPropertyValue(ruleGroupEnabledProperty, Boolean.FALSE);
        }  // if
        ruleGroups.put(ruleGroupName, Boolean.FALSE);
      } // for
    } // if  
  } // disable

  public Set<String> getRuleGroupNames() { return ruleGroups.keySet(); }
  public boolean isInRuleGroup(String name) { return ruleGroups.containsKey(name); }

  public boolean isInRuleGroups(Set<String> names) 
  { 
    for (String name : names) if (!ruleGroups.containsKey(name)) return false;
    return true;
  }  // isInRuleGroups

  /**
   ** Add a rule group name to a rule. Returns true on success. The name must the name of an OWL individual of class RuleGroup defined in
   ** the ontology http://swrl.stanford.edu/ontologies/3.3/swrla.owl. Make the group enabled by default.
   */
  public boolean addRuleGroup(String name) 
  { 
    OWLNamedClass ruleGroupClass = getOWLModel().getOWLNamedClass(SWRLNames.Annotations.RULE_GROUP);
    OWLProperty ruleGroupProperty = getOWLModel().getOWLProperty(SWRLNames.Annotations.HAS_RULE_GROUP);
    OWLProperty ruleGroupEnabledProperty = getOWLModel().getOWLProperty(SWRLNames.Annotations.IS_RULE_GROUP_ENABLED);
    boolean result = false;

    if (isInRuleGroup(name)) return true;

    if (ruleGroupClass != null) {
      OWLIndividual ruleGroupIndividual = getOWLModel().getOWLIndividual(name);
      if (ruleGroupIndividual != null) {
        if (ruleGroupProperty != null) {
          addPropertyValue(ruleGroupProperty, ruleGroupIndividual);
          ruleGroupIndividual.addPropertyValue(ruleGroupEnabledProperty, Boolean.TRUE);
          ruleGroups.put(name, Boolean.TRUE);
          result = true;
        } // if
      } // if
    } // if

    return result;
  } // addRuleGroupName

  /**
   ** Remove a rule group name from a rule. Returns true on success. 
   */
  public boolean removeRuleGroup(String name) 
  { 
    OWLNamedClass ruleGroupClass = getOWLModel().getOWLNamedClass(SWRLNames.Annotations.RULE_GROUP);
    boolean result = false;

    if (!isInRuleGroup(name)) return true;

    if (ruleGroupClass != null) {
      OWLIndividual individual = getOWLModel().getOWLIndividual(name);
      if (individual != null) {
        OWLProperty property = getOWLModel().getOWLProperty(SWRLNames.Annotations.HAS_RULE_GROUP);
        if (property != null) {
          removePropertyValue(property, individual);
          ruleGroups.remove(name);
          result = true;
        } // if
      } // if
    } // if

    return result;
  } // removeRuleGroupName

} // DefaultSWRLImp
