
package edu.stanford.smi.protegex.owl.swrl.model;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;

import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;

import java.util.*;
import java.util.logging.Level;

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
  private OWLObjectProperty bodyProperty, headProperty, argumentsProperty, builtInProperty, argument1Property, argument2Property,
                            classPredicateProperty, propertyPredicateProperty, dataRangeProperty;
  private OWLModel owlModel;

  public SWRLFactory(OWLModel owlModel) 
  {
    this.owlModel = owlModel;

    if (!(owlModel.getAllImports().contains(SWRLNames.SWRL_IMPORT) || owlModel.getAllImports().contains(SWRLNames.SWRL_ALT_IMPORT))) {
    	if (Log.getLogger().getLevel() == Level.FINE) {
    		Log.getLogger().warning("Attempt to create SWRLFactory with an OWL model that does not import SWRL.");
    	}
    }
  
    // Activate OWL-Java mappings.
    SWRLJavaFactory factory = new SWRLJavaFactory(owlModel);
    owlModel.setOWLJavaFactory(factory);
    
    initSWRLClasses();
    
    if(owlModel instanceof JenaOWLModel) OWLJavaFactoryUpdater.run((JenaOWLModel)owlModel);
  } // SWRLFactory
  
  
	private void initSWRLClasses() {
		atomListCls = getOrCreateOWLNamedClass(SWRLNames.Cls.ATOM_LIST);
		builtinAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.BUILTIN_ATOM);    
		classAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.CLASS_ATOM);
		dataRangeAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.DATA_RANGE_ATOM);
		dataValuedPropertyAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM);
		differentIndividualsAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM);
		impCls = getOrCreateOWLNamedClass(SWRLNames.Cls.IMP);
		individualPropertyAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM);
		sameIndividualAtomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM);
		builtInCls = getOrCreateOWLNamedClass(SWRLNames.Cls.BUILTIN);
		atomCls = getOrCreateOWLNamedClass(SWRLNames.Cls.ATOM);
		variableCls = getOrCreateOWLNamedClass(SWRLNames.Cls.VARIABLE);

		bodyProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.BODY);
		headProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.HEAD);
		argumentsProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.ARGUMENTS);
		builtInProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.BUILTIN);
		argument1Property = getOrCreateOWLObjectProperty(SWRLNames.Slot.ARGUMENT1);
		argument2Property = getOrCreateOWLObjectProperty(SWRLNames.Slot.ARGUMENT2);
		classPredicateProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.CLASS_PREDICATE);
		propertyPredicateProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.PROPERTY_PREDICATE);
		dataRangeProperty = getOrCreateOWLObjectProperty(SWRLNames.Slot.DATA_RANGE);
	}
	
	private OWLNamedClass getOrCreateOWLNamedClass(String resourceName) {
		RDFResource resource = owlModel.getRDFResource(resourceName);
		if (resource == null) {
			resource = owlModel.createOWLNamedClass(resourceName);
 
			if (resourceName.equals(SWRLNames.Cls.ATOM_LIST)) {
				((OWLNamedClass) resource).addSuperclass(owlModel.getRDFListClass());
			}
		} else if (!(resource instanceof OWLNamedClass)) {
			resource.setProtegeType(owlModel.getOWLNamedClassClass());
			resource = owlModel.getOWLNamedClass(resourceName);
			((OWLNamedClass) resource).addSuperclass(owlModel.getOWLThingClass());
		}

		return (OWLNamedClass) resource;
	}

	private OWLObjectProperty getOrCreateOWLObjectProperty(String propertyName) {
		RDFResource resource = owlModel.getRDFResource(propertyName);
		
		if (resource == null) {
			resource = owlModel.createOWLObjectProperty(propertyName);			
		} else if (!(resource instanceof OWLObjectProperty)) {
			resource.setProtegeType(owlModel.getOWLObjectPropertyClass());		
		}
		
		resource = owlModel.getOWLObjectProperty(propertyName);
		return (OWLObjectProperty) resource;
	}

  
  
  public SWRLImp createImp() 
  {
    String name = getNewImpName();
    return (SWRLImp) impCls.createInstance(name);
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
  
 
  public SWRLAtomList createAtomList() {
    return (SWRLAtomList) atomListCls.createAnonymousInstance();
  } // createAtomList


  public SWRLAtomList createAtomList(Collection atoms) {
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


    public SWRLClassAtom createClassAtom(RDFSNamedClass aClass,
                                         RDFResource iObject) {
        SWRLClassAtom swrlClassAtom;

        swrlClassAtom = (SWRLClassAtom) classAtomCls.createAnonymousInstance();

        swrlClassAtom.setClassPredicate(aClass);
        swrlClassAtom.setArgument1(iObject);

        return swrlClassAtom;

    } // createClassAtom


    public SWRLDataRangeAtom createDataRangeAtom(RDFResource dataRange,
                                                 RDFObject dObject) {

        SWRLDataRangeAtom swrlDataRangeAtom = (SWRLDataRangeAtom) dataRangeAtomCls.createAnonymousInstance();

        swrlDataRangeAtom.setArgument1(dObject);
        swrlDataRangeAtom.setDataRange(dataRange);

        return swrlDataRangeAtom;
    } // createDataRangeAtom


    public SWRLDatavaluedPropertyAtom createDatavaluedPropertyAtom(OWLDatatypeProperty datatypeSlot,
                                                                   RDFResource iObject,
                                                                   RDFObject dObject) {
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
    return (SWRLBuiltin) owlModel.getRDFSNamedClass(SWRLNames.Cls.BUILTIN).createInstance(name);
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
  } // createBuiltin


  public Collection getBuiltins() {
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

      if (hasImp(ruleName)) throw new SWRLFactoryException("Attempt to copy rule '" + ruleName + "' that has same name as an existing rule.");

      try { createImp(ruleName, expression); }
      catch (SWRLParseException e) { throw new SWRLFactoryException("Error copying rule '" + ruleName + "': " + e.getMessage()); }
    } // for
  } // copyImps

  public SWRLImp getImp(String name) throws SWRLFactoryException
  {
    RDFResource resource = owlModel.getRDFResource(name);
    SWRLImp result = null;

    if (resource instanceof SWRLImp) result = (SWRLImp) resource;
    else throw new SWRLFactoryException("Invalid attempt to cast " + name +
                                        " into SWRLBuiltin (real type is " + resource.getProtegeType() + ")");

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
  

} // SWRLFactory
