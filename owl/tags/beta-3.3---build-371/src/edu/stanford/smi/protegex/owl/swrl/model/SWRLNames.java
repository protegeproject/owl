package edu.stanford.smi.protegex.owl.swrl.model;

import edu.stanford.smi.protegex.owl.model.OWLNames;

/**
 * Defines the names of the SWRL system ontology classes and properties.  This corresponds to the Model interface in general Protege, and
 * the OWLNames interface in the OWL Plugin.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface SWRLNames extends OWLNames {

  public final static String SWRL_IMPORT = "http://www.w3.org/2003/11/swrl";
  public final static String SWRLB_IMPORT = "http://www.w3.org/2003/11/swrlb";
  public final static String SWRLX_IMPORT = "http://swrl.stanford.edu/swrlx.owl";
  
  public final static String SWRL_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrl.owl";
  public final static String SWRLB_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrlb.owl";
  public final static String SWRLX_ALT_IMPORT = "http://swrl.stanford.edu/swrlx.owl";
  
  public final static String SWRL_NAMESPACE = "http://www.w3.org/2003/11/swrl#";
  public final static String SWRLB_NAMESPACE = "http://www.w3.org/2003/11/swrlb#";
  public final static String SWRLX_NAMESPACE = "http://swrl.stanford.edu/swrlx.owl#";
  
  public final static String SWRL_PREFIX = "swrl";
  public final static String SWRLB_PREFIX = "swrlb";
  public final static String SWRLX_PREFIX = "swrlx";
  
  public static interface Cls {
    
    public final static String IMP = "swrl:Imp";
    
    public final static String ATOM = "swrl:Atom";
    
    public final static String CLASS_ATOM = "swrl:ClassAtom";
    
    public final static String INDIVIDUAL_PROPERTY_ATOM = "swrl:IndividualPropertyAtom";
    
    public final static String DATAVALUED_PROPERTY_ATOM = "swrl:DatavaluedPropertyAtom";
    
    public final static String DIFFERENT_INDIVIDUALS_ATOM = "swrl:DifferentIndividualsAtom";
    
    public final static String SAME_INDIVIDUAL_ATOM = "swrl:SameIndividualAtom";
    
    public final static String BUILTIN_ATOM = "swrl:BuiltinAtom";
    
    public final static String DATA_RANGE_ATOM = "swrl:DataRangeAtom";
    
    public final static String BUILTIN = "swrl:Builtin";
    
    public final static String VARIABLE = "swrl:Variable";
    
    public final static String ATOM_LIST = "swrl:AtomList";
    
  } // Cls
  
  public static interface Slot {
    
    public final static String BODY = "swrl:body";
    
    public final static String HEAD = "swrl:head";
    
    public final static String ARGUMENTS = "swrl:arguments";
    
    public final static String BUILTIN = "swrl:builtin";
    
    public final static String ARGUMENT1 = "swrl:argument1";
    
    public final static String ARGUMENT2 = "swrl:argument2";
    
    public final static String CLASS_PREDICATE = "swrl:classPredicate";
    
    public final static String PROPERTY_PREDICATE = "swrl:propertyPredicate";
    
    public final static String DATA_RANGE = "swrl:dataRange";
    
  } // Slot
} // SWRLNames
