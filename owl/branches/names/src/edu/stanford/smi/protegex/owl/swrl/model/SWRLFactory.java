
package edu.stanford.smi.protegex.owl.swrl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;

/**
 * A utility class that can (and should) be used to create and access SWRL related objects in an ontology.
 *
 * See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLFactoryFAQ">here</a> for documentation on using this class.
 */
public class SWRLFactory 
{
  private OWLNamedClass atomListCls, builtinAtomCls, classAtomCls, dataRangeAtomCls, dataValuedPropertyAtomCls,
                        differentIndividualsAtomCls, impCls, individualPropertyAtomCls, sameIndividualAtomCls,
                        atomCls, variableCls, builtInCls;
  private OWLObjectProperty bodyProperty, headProperty, argumentsProperty, builtInProperty, argument1Property, classPredicateProperty, 
                            propertyPredicateProperty, dataRangeProperty;
  private OWLDatatypeProperty argsProperty, minArgsProperty, maxArgsProperty; 
  private RDFProperty argument2Property;

  private OWLModel owlModel;
  private List<SWRLBuiltin> coreSWRLBuiltIns;

  public SWRLFactory(OWLModel owlModel) 
  {
    this.owlModel = owlModel;

    initSWRLClasses();
    
    if (!(owlModel.getOWLJavaFactory() instanceof SWRLJavaFactory)) {    
    	// Activate OWL-Java mappings.
    	SWRLJavaFactory factory = new SWRLJavaFactory(owlModel);
    	owlModel.setOWLJavaFactory(factory);
        
    	if(owlModel instanceof JenaOWLModel) OWLJavaFactoryUpdater.run((JenaOWLModel)owlModel);
    }
  } // SWRLFactory
  
  private void initSWRLClasses() 
  {
    TripleStoreModel tsm = owlModel.getTripleStoreModel();               
    TripleStore activeTs = tsm.getActiveTripleStore();
    TripleStore systemTS = tsm.getSystemTripleStore();

    tsm.setActiveTripleStore(systemTS);

    impCls = getOrCreateOWLNamedClass(SWRLNames.Cls.IMP);
    variableCls = getOrCreateOWLNamedClass(SWRLNames.Cls.VARIABLE);
    builtInCls = getOrCreateOWLNamedClass(SWRLNames.Cls.BUILTIN);

    atomListCls = getOrCreateOWLNamedClass(SWRLNames.Cls.ATOM_LIST, owlModel.getRDFListClass());

    atomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.ATOM);
    classAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.CLASS_ATOM, atomCls);
    builtinAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.BUILTIN_ATOM,  atomCls);
    dataRangeAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.DATA_RANGE_ATOM,  atomCls);
    dataValuedPropertyAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM,  atomCls);
    differentIndividualsAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM,  atomCls);
    individualPropertyAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM,  atomCls);
    sameIndividualAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM, atomCls);
    
    bodyProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.BODY);
    headProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.HEAD);
    argumentsProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.ARGUMENTS);
    builtInProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.BUILTIN);
    argument1Property = getOrCreateOWLObjectProperty(SWRLNames.Slot.ARGUMENT1);
    argument2Property = getOrCreateOWLProperty(SWRLNames.Slot.ARGUMENT2);
    classPredicateProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.CLASS_PREDICATE);
    propertyPredicateProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.PROPERTY_PREDICATE);
    dataRangeProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.DATA_RANGE);

    argsProperty = getOrCreateOWLDatatypeProperty(SWRLNames.Slot.ARGS);
    minArgsProperty = getOrCreateOWLDatatypeProperty(SWRLNames.Slot.MIN_ARGS);
    maxArgsProperty = getOrCreateOWLDatatypeProperty(SWRLNames.Slot.MAX_ARGS);

    initCoreSWRLBuiltIns(); // Must be called after class and property creations

    tsm.setActiveTripleStore(activeTs); 
  } // initSWRLClasses
	
  private OWLNamedClass getOrCreateOWLNamedClass(String className) 
  {
    return getOrCreateOWLNamedClass(className, null);
  } // getOrCreateOWLNamedClass

  private OWLNamedClass getOrCreateOWLNamedClass(String className, RDFSNamedClass superclass)
  {
    RDFResource resource = owlModel.getRDFResource(className);
    OWLNamedClass cls;

    if (resource == null) {
      cls = new DefaultOWLNamedClass(owlModel, new FrameID(className));
      cls.assertFrameName();
      cls.setProtegeType(owlModel.getOWLNamedClassClass());
      if (superclass != null) {
        cls.addSuperclass(superclass);
        cls.removeSuperclass(owlModel.getOWLThingClass());
      } else cls.addSuperclass(owlModel.getOWLThingClass());   
    } else if (!(resource instanceof OWLNamedClass)) {
      resource.setProtegeType(owlModel.getOWLNamedClassClass());
      cls = owlModel.getOWLNamedClass(className);
      if (superclass != null) {
        cls.addSuperclass(superclass);
        cls.removeSuperclass(owlModel.getOWLThingClass());
      } else cls.addSuperclass(owlModel.getOWLThingClass());   
    } // if

    return owlModel.getOWLNamedClass(className);
  } // getOrCreateOWLNamedClass
  
  private OWLObjectProperty getOrCreateOWLObjectProperty(String propertyName) 
  {
    RDFResource resource = owlModel.getRDFResource(propertyName);
    
    if (resource == null) {
      OWLProperty property = new DefaultOWLObjectProperty(owlModel, new FrameID(propertyName));
      property.assertFrameName();
      property.setProtegeType(owlModel.getOWLObjectPropertyClass());
    } else if (!(resource instanceof OWLObjectProperty)) {
      resource.setProtegeType(owlModel.getOWLObjectPropertyClass());		
    } // if
    return owlModel.getOWLObjectProperty(propertyName);
  } // getOrCreateOWLObjectProperty

  private OWLDatatypeProperty getOrCreateOWLDatatypeProperty(String propertyName) 
  {
    RDFResource resource = owlModel.getRDFResource(propertyName);
    
    if (resource == null) {
      OWLProperty property = new DefaultOWLDatatypeProperty(owlModel, new FrameID(propertyName));
      property.assertFrameName();
      property.setProtegeType(owlModel.getOWLDatatypePropertyClass());
    } else if (!(resource instanceof OWLDatatypeProperty)) {
      resource.setProtegeType(owlModel.getOWLDatatypePropertyClass());		
    } // if
    return owlModel.getOWLDatatypeProperty(propertyName);
  } // getOrCreateOWLDatatypeProperty

  private RDFProperty getOrCreateOWLProperty(String propertyName) 
  {
    RDFResource resource = owlModel.getRDFResource(propertyName);
    
    if (resource == null) {
      RDFProperty property = new DefaultRDFProperty(owlModel, new FrameID(propertyName));
      property.assertFrameName();
      property.setProtegeType(owlModel.getRDFPropertyClass());
    } else if (!(resource instanceof RDFProperty)) {
      resource.setProtegeType(owlModel.getRDFPropertyClass());		
    } // if
    return owlModel.getRDFProperty(propertyName);
  } // getOrCreateOWLProperty

  private SWRLBuiltin getOrCreateBuiltIn(String builtInName)
  {
    RDFResource resource = owlModel.getRDFResource(builtInName);

    if (resource == null) { // See if it is an external resource
      String name = (builtInName.indexOf(":") == -1) ? builtInName : builtInName.substring(builtInName.indexOf(":") + 1);
      resource = owlModel.getRDFResource(SWRLNames.SWRLB_NAMESPACE + name);
      // if (resource != null) resource.setName(builtInName); // Found external resource - set its name to the friendly name with prefix
    } // if

    if (resource == null) {
      OWLIndividual individual = new DefaultOWLIndividual(owlModel, new FrameID(builtInName));
      individual.assertFrameName();
      individual.setProtegeType(builtInCls);
    } else {
      if (!(resource instanceof SWRLBuiltin)) resource.setProtegeType(builtInCls);
    } // if

    return (SWRLBuiltin)owlModel.getOWLIndividual(builtInName);
  } // getOrCreateBuiltIn    
  
  public SWRLImp createImp() 
  {
    String name = getNewImpName();
    return (SWRLImp)impCls.createInstance(name);
  }

  public SWRLImp createImpWithGivenName(String name)
  {
    RDFSClass impCls = owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP);
    return (SWRLImp)impCls.createInstance(name);
  }
  
  public SWRLImp createImp(String expression) throws SWRLParseException 
  {
    SWRLParser parser = new SWRLParser(owlModel);
    parser.setParseOnly(false);
    return parser.parse(expression);
  }

  public SWRLImp createImp(String name, String expression) throws SWRLParseException {
    SWRLParser parser = new SWRLParser(owlModel);
    SWRLImp imp = createImpWithGivenName(name);
    parser.setParseOnly(false);
    return parser.parse(expression, imp);
  } // createImp

  public SWRLImp createImp(SWRLAtom headAtom, Collection bodyAtoms) {
    SWRLAtomList head = createAtomList(Collections.singleton(headAtom));
    SWRLAtomList body = createAtomList(bodyAtoms);
    return createImp(head, body);
  }
  
  public SWRLImp createImp(SWRLAtomList head, SWRLAtomList body) 
  {
    SWRLImp swrlImp = createImp();
    swrlImp.setHead(head);
    swrlImp.setBody(body);
    return swrlImp;
  } // SWRLImp
 
  public SWRLAtomList createAtomList() 
  {
    return (SWRLAtomList) atomListCls.createAnonymousInstance();
  } // createAtomList

  public SWRLAtomList createAtomList(Collection atoms) 
  {
    SWRLAtomList list = createAtomList();
    for (Iterator it = atoms.iterator(); it.hasNext();) {
      Object o = it.next();
      list.append(o);
    }
    return list;
  }

  public SWRLBuiltinAtom createBuiltinAtom(SWRLBuiltin swrlBuiltin, Iterator arguments) 
  {
    RDFList li = owlModel.createRDFList(arguments);
    return createBuiltinAtom(swrlBuiltin, li);    
  } // createBuiltinAtom

  public SWRLBuiltinAtom createBuiltinAtom(SWRLBuiltin swrlBuiltin, RDFList arguments) 
  {
    SWRLBuiltinAtom swrlBuiltinAtom;
    
    swrlBuiltinAtom = (SWRLBuiltinAtom) builtinAtomCls.createAnonymousInstance();
    
    swrlBuiltinAtom.setBuiltin(swrlBuiltin);
    swrlBuiltinAtom.setArguments(arguments);
    
    return swrlBuiltinAtom;
  } // createBuiltinAtom

  public SWRLClassAtom createClassAtom(RDFSNamedClass aClass, RDFResource iObject) 
  {
    SWRLClassAtom swrlClassAtom;
    
    swrlClassAtom = (SWRLClassAtom) classAtomCls.createAnonymousInstance();
    
    swrlClassAtom.setClassPredicate(aClass);
    swrlClassAtom.setArgument1(iObject);
    
    return swrlClassAtom;
    
  } // createClassAtom

  public SWRLDataRangeAtom createDataRangeAtom(RDFResource dataRange, RDFObject dObject) 
  {
    SWRLDataRangeAtom swrlDataRangeAtom = (SWRLDataRangeAtom) dataRangeAtomCls.createAnonymousInstance();
    
    swrlDataRangeAtom.setArgument1(dObject);
    swrlDataRangeAtom.setDataRange(dataRange);
    
    return swrlDataRangeAtom;
  } // createDataRangeAtom
  
  public SWRLDatavaluedPropertyAtom createDatavaluedPropertyAtom(OWLDatatypeProperty datatypeSlot, RDFResource iObject, RDFObject dObject) 
  {
    SWRLDatavaluedPropertyAtom swrlDatavaluedPropertyAtom = (SWRLDatavaluedPropertyAtom) dataValuedPropertyAtomCls.createAnonymousInstance();
    
    swrlDatavaluedPropertyAtom.setPropertyPredicate(datatypeSlot);
    swrlDatavaluedPropertyAtom.setArgument1(iObject);
    swrlDatavaluedPropertyAtom.setArgument2(dObject);

    return swrlDatavaluedPropertyAtom;
    
  } // createDatavaluedPropertyAtom
  
  public SWRLIndividualPropertyAtom createIndividualPropertyAtom(OWLObjectProperty objectSlot, RDFResource iObject1, RDFResource iObject2) 
  {
    SWRLIndividualPropertyAtom swrlIndividualPropertyAtom;
    
    swrlIndividualPropertyAtom = (SWRLIndividualPropertyAtom) individualPropertyAtomCls.createAnonymousInstance();
    
    swrlIndividualPropertyAtom.setPropertyPredicate(objectSlot);
    swrlIndividualPropertyAtom.setArgument1(iObject1);
    swrlIndividualPropertyAtom.setArgument2(iObject2);
    
    return swrlIndividualPropertyAtom;
  } // createIndividualPropertyAtom
  
  public SWRLDifferentIndividualsAtom createDifferentIndividualsAtom(RDFResource argument1, RDFResource argument2) 
  {
    SWRLDifferentIndividualsAtom swrlDifferentIndividualsAtom;
    
    swrlDifferentIndividualsAtom = (SWRLDifferentIndividualsAtom) differentIndividualsAtomCls.createAnonymousInstance();
    swrlDifferentIndividualsAtom.setArgument1(argument1);
    swrlDifferentIndividualsAtom.setArgument2(argument2);
    
    return swrlDifferentIndividualsAtom;
  } // createDifferentIndividualsAtom

  public SWRLSameIndividualAtom createSameIndividualAtom(RDFResource argument1, RDFResource argument2) 
  {
    SWRLSameIndividualAtom swrlSameIndividualAtom;
    
    swrlSameIndividualAtom = (SWRLSameIndividualAtom) sameIndividualAtomCls.createAnonymousInstance();
    swrlSameIndividualAtom.setArgument1(argument1);
    swrlSameIndividualAtom.setArgument2(argument2);
    
    return swrlSameIndividualAtom;
  } // createSameIndividualAtom
  
  public SWRLVariable createVariable(String name) 
  {
    return (SWRLVariable) owlModel.getRDFSNamedClass(SWRLNames.Cls.VARIABLE).createInstance(name);
  } // createVariable

  public SWRLBuiltin createBuiltin(String name) 
  {
    return (SWRLBuiltin)owlModel.getRDFSNamedClass(SWRLNames.Cls.BUILTIN).createInstance(name);
  } // createBuiltin

  public SWRLBuiltin getBuiltin(String name) 
  {
    RDFResource resource = owlModel.getRDFResource(name);
    if (resource instanceof SWRLBuiltin) {
      return (SWRLBuiltin) resource;
    }
    else {
      System.err.println("[SWRLFactory]  Invalid attempt to cast " + name +
                         " into SWRLBuiltin (real type is " + resource.getProtegeType() + ")");
      return null;
    }
  } // getBuiltin

  public Collection getBuiltins() 
  {
    RDFSNamedClass builtinCls = owlModel.getRDFSNamedClass(SWRLNames.Cls.BUILTIN);
    return builtinCls.getInstances(true);
  }

  public Collection getImps() 
  {
    RDFSClass impCls = owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP);
    return impCls.getInstances(true);
  } // getImps

  public Collection getEnabledImps() { return getImps(new HashSet<String>(), true); }
  public Collection getEnabledImps(Set<String> ruleGroupNames) { return getImps(ruleGroupNames, true); }

  public Collection getEnabledImps(String ruleGroupName) 
  { 
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    return getImps(ruleGroupNames, true); 
  } // getEnabledImps

  // If the ruleGroupNames is empty, return all imps.
  private Collection getImps(Set<String> ruleGroupNames, boolean isEnabled) 
  {
    Collection result = new ArrayList();
    Collection imps = getImps();

    if (imps != null) {
      Iterator iterator = imps.iterator();
      while (iterator.hasNext()) {
        SWRLImp imp = (SWRLImp)iterator.next();
        if (ruleGroupNames.isEmpty() || imp.isInRuleGroups(ruleGroupNames)) {
          if (imp.isEnabled() == isEnabled) result.add(imp);
        } // if
      } // while
    } // if
    return result;
  } // getImps

  public void deleteImps()
  {
    for (Object o : getImps()) {
      SWRLImp imp = (SWRLImp)o;
      imp.deleteImp();
    } // for
  } // deleteImps

  public void replaceImps(OWLModel sourceOWLModel) throws SWRLFactoryException
  {
    deleteImps();
    copyImps(sourceOWLModel);
  } // replaceImps

  public void copyImps(OWLModel sourceOWLModel) throws SWRLFactoryException
  {
    SWRLFactory sourceSWRLFactory = new SWRLFactory(sourceOWLModel);

    for (Object o : sourceSWRLFactory.getImps()) {
      SWRLImp imp = (SWRLImp)o;
      String ruleName = imp.getLocalName();
      String expression = imp.getBrowserText();

      if (hasImp(ruleName)) throw new SWRLFactoryException("attempt to copy rule '" + ruleName + "' that has same name as an existing rule");

      try { createImp(ruleName, expression); }
      catch (SWRLParseException e) { throw new SWRLFactoryException("error copying rule '" + ruleName + "': " + e.getMessage()); }
    } // for
  } // copyImps

  public SWRLImp getImp(String name) throws SWRLFactoryException
  {
    RDFResource resource = owlModel.getRDFResource(name);
    SWRLImp result = null;

    if (resource instanceof SWRLImp) result = (SWRLImp) resource;
    else throw new SWRLFactoryException("invalid attempt to cast " + name + " into SWRLBuiltin (real type is " + resource.getProtegeType() + ")");

    return result;
  } // getImp

  public boolean hasImp(String name)
  {
    RDFResource resource = owlModel.getRDFResource(name);

    return (resource != null) && (resource instanceof SWRLImp);
  } // hasImp

  public String getNewImpName() {
    String base = "Rule-";
    int i = Math.max(1, impCls.getInstances(false).size());
    while (owlModel.getRDFResource(base + i) != null) {
      i++;
        }
    return base + i;
  }


    public SWRLVariable getVariable(String name) {
        return (SWRLVariable) owlModel.getRDFResource(name);
    }


    public Collection getVariables() {
        RDFSClass variableCls = owlModel.getRDFSNamedClass(SWRLNames.Cls.VARIABLE);
        return variableCls.getInstances(true);
    }

  public Collection getReferencedImps(RDFResource rdfResource)
  {
    Collection result = new ArrayList();

    if (rdfResource != null) {
      Iterator iterator = getImps().iterator();
      while (iterator.hasNext()) {
        SWRLImp imp = (SWRLImp)iterator.next();
        Set set = imp.getReferencedInstances();
        if (set.contains(rdfResource) && !result.contains(imp)) result.add(imp);
      } // while
    } // if
    return result;
  } // getReferencedImps

  public void enableAll() { enableStatusUpdate(new HashSet<String>(), true); }
  public void disableAll() { enableStatusUpdate(new HashSet<String>(), false); }

  public void enableAll(Set<String> ruleGroupNames) { enableStatusUpdate(ruleGroupNames, true); }
  public void disableAll(Set<String> ruleGroupNames) { enableStatusUpdate(ruleGroupNames, false); }

  public void enableAll(String ruleGroupName) 
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    enableStatusUpdate(ruleGroupNames, true); 
  } // enable

  public void disableAll(String ruleGroupName) 
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    enableStatusUpdate(ruleGroupNames, false); 
  } // enable

  public boolean isSWRLResource(RDFResource resource)
  {
    return  (resource instanceof SWRLAtomList) || (resource instanceof SWRLBuiltinAtom) || (resource instanceof SWRLClassAtom) || 
            (resource instanceof SWRLDataRangeAtom) || (resource instanceof SWRLDatavaluedPropertyAtom) ||
            (resource instanceof SWRLDifferentIndividualsAtom) || (resource instanceof SWRLImp) || 
            (resource instanceof SWRLIndividualPropertyAtom) || (resource instanceof SWRLSameIndividualAtom) || 
            (resource instanceof SWRLBuiltin) || (resource instanceof SWRLAtom) || (resource instanceof SWRLVariable);
  } // isSWRLResource

  private void enableStatusUpdate(Set<String> ruleGroupNames, boolean enable)
  {
    Iterator iterator = getImps().iterator();
    while (iterator.hasNext()) {
      SWRLImp imp = (SWRLImp)iterator.next();
      if (ruleGroupNames.isEmpty() || imp.isInRuleGroups(ruleGroupNames)) {
        if (enable) imp.enable(); else imp.disable();
      } // if
    } // while
  } // enableAll

  public Collection<RDFSNamedClass> getSWRLClasses() {
	  ArrayList<RDFSNamedClass> swrlClasses = new ArrayList<RDFSNamedClass>();
	  swrlClasses.add(atomListCls);		
	  swrlClasses.add(builtinAtomCls);    
	  swrlClasses.add(classAtomCls);
	  swrlClasses.add(dataRangeAtomCls);
	  swrlClasses.add(dataValuedPropertyAtomCls);
	  swrlClasses.add(differentIndividualsAtomCls);
	  swrlClasses.add(impCls);
	  swrlClasses.add(individualPropertyAtomCls);
	  swrlClasses.add(sameIndividualAtomCls);
	  swrlClasses.add(builtInCls);
	  swrlClasses.add(atomCls);
	  swrlClasses.add(variableCls);
	  
	  return swrlClasses;
  }
  
  public Collection<RDFProperty> getSWRLProperties() {
	  ArrayList<RDFProperty> swrlProperties = new ArrayList<RDFProperty>();
	  swrlProperties.add(bodyProperty);
	  swrlProperties.add(headProperty);
	  swrlProperties.add(argumentsProperty);
	  swrlProperties.add(builtInProperty);
	  swrlProperties.add(argument1Property);
	  swrlProperties.add(argument2Property);
	  swrlProperties.add(classPredicateProperty);
	  swrlProperties.add(propertyPredicateProperty);
	  swrlProperties.add(dataRangeProperty);
	  
	  return swrlProperties;
  }
  
  public Collection<RDFProperty> getSWRLBProperties(){
	  ArrayList<RDFProperty> swrlbProperties = new ArrayList<RDFProperty>();
	  	  
	  RDFProperty swrlbArgs = owlModel.getRDFProperty(SWRLNames.Slot.ARGS);
	  if (swrlbArgs != null) {
		  swrlbProperties.add(swrlbArgs);
	  }
	  
	  RDFProperty swrlbMinArgs = owlModel.getRDFProperty(SWRLNames.Slot.MIN_ARGS);
	  if (swrlbMinArgs != null) {
		  swrlbProperties.add(swrlbMinArgs);
	  }

	  RDFProperty swrlbMaxArgs = owlModel.getRDFProperty(SWRLNames.Slot.MAX_ARGS);
	  if (swrlbMaxArgs != null) {
		  swrlbProperties.add(swrlbMaxArgs);
	  }

	  return swrlbProperties;
  }

  private void initCoreSWRLBuiltIns()
  {
    coreSWRLBuiltIns = new ArrayList<SWRLBuiltin>();

    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.EQUAL));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.NOT_EQUAL));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.LESS_THAN));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.LESS_THAN_OR_EQUAL));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.GREATER_THAN));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.GREATER_THAN_OR_EQUAL));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.MULTIPLY));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.DIVIDE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.INTEGER_DIVIDE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.MOD));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.POW));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.UNARY_PLUS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.UNARY_MINUS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ABS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.CEILING));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.FLOOR));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ROUND));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ROUND_HALF_TO_EVEN));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SIN));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.COS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.TAN));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.BOOLEAN_NOT));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.STRING_EQUAL_IGNORE_CASE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.STRING_CONCAT));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBSTRING));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.STRING_LENGTH));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.NORMALIZE_SPACE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.UPPER_CASE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.LOWER_CASE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.TRANSLATE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.CONTAINS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.CONTAINS_IGNORE_CASE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.STARTS_WITH));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ENDS_WITH));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBSTRING_BEFORE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBSTRING_AFTER));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.MATCHES));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.REPLACE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.TOKENIZE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.YEAR_MONTH_DURATION));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.DAY_TIME_DURATION));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.DATETIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.DATE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.TIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATIONS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATIONS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.MULTIPLY_YEAR_MONTH_DURATION));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.DIVIDE_YEAR_MONTH_DURATIONS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATIONS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATIONS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.MULTIPLY_DAY_TIME_DURATIONS));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.DIVIDE_DAY_TIME_DURATION));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_DATES));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_TIMES));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATION_TO_DATETIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_DATETIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATION_FROM_DATETIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_DATETIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATION_TO_DATE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_DATE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_DATE));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_TIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_TIME));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_DATETIMES_YIELDING_YEAR_MONTH_DURATION));
    coreSWRLBuiltIns.add(getOrCreateBuiltIn(SWRLNames.CoreBuiltIns.SUBTRACT_DATETIMES_YIELDING_DAY_TIME_DURATION));
  } // initCoreSWRLBuiltIns

} // SWRLFactory
