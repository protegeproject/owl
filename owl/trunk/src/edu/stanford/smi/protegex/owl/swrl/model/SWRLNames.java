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
  public final static String SWRLA_IMPORT = "http://swrl.stanford.edu/ontologies/3.3/swrla.owl";
  public final static String SWRLX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl";
  public final static String SWRLTBOX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/tbox.owl";
  public final static String SWRLABOX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/abox.owl";
  public final static String SWRLQUERY_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/query.owl";
  public final static String SWRLTEMPORAL_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl";
  
  public final static String SWRL_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrl.owl";
  public final static String SWRLB_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrlb.owl";
  public final static String SWRLA_ALT_IMPORT = "http://swrl.stanford.edu/ontologies/3.3/swrla.owl";
  public final static String SWRLX_ALT_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl";
  public final static String SWRLTBOX_ALT_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/tbox.owl";
  public final static String SWRLABOX_ALT_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/abox.owl";
  public final static String SWRLQUERY_ALT_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/query.owl";
  public final static String SWRLTEMPORAL_ALT_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl";
  
  public final static String SWRL_NAMESPACE = "http://www.w3.org/2003/11/swrl#";
  public final static String SWRLB_NAMESPACE = "http://www.w3.org/2003/11/swrlb#";
  public final static String SWRLA_NAMESPACE = "http://swrl.stanford.edu/ontologies/3.3/swrla.owl#";
  public final static String SWRLX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl#";
  public final static String SWRLTBOX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/tbox.owl#";
  public final static String SWRLABOX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/abox.owl#";
  public final static String SWRLQUERY_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/query.owl#";
  public final static String SWRLTEMPORAL_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl#";
  
  public final static String SWRL_PREFIX = "swrl";
  public final static String SWRLB_PREFIX = "swrlb";
  public final static String SWRLA_PREFIX = "swrla";
  public final static String SWRLX_PREFIX = "swrlx";
  public final static String SWRLTBOX_PREFIX = "tbox";
  public final static String SWRLABOX_PREFIX = "abox";
  public final static String SWRLQUERY_PREFIX = "query";
  public final static String SWRLTEMPORAL_PREFIX = "temporal";
  
  public static interface Cls {
    
    public final static String IMP = SWRL_PREFIX + ":" +"Imp";
    
    public final static String ATOM = SWRL_PREFIX + ":" + "Atom";
    
    public final static String CLASS_ATOM = SWRL_PREFIX + ":" + "ClassAtom";
    
    public final static String INDIVIDUAL_PROPERTY_ATOM = SWRL_PREFIX + ":" + "IndividualPropertyAtom";
    
    public final static String DATAVALUED_PROPERTY_ATOM = SWRL_PREFIX + ":" + "DatavaluedPropertyAtom";
    
    public final static String DIFFERENT_INDIVIDUALS_ATOM = SWRL_PREFIX + ":" + "DifferentIndividualsAtom";
    
    public final static String SAME_INDIVIDUAL_ATOM = SWRL_PREFIX + ":" + "SameIndividualAtom";
    
    public final static String BUILTIN_ATOM = SWRL_PREFIX + ":" + "BuiltinAtom";
    
    public final static String DATA_RANGE_ATOM = SWRL_PREFIX + ":" + "DataRangeAtom";
    
    public final static String BUILTIN = SWRL_PREFIX + ":" + "Builtin";
    
    public final static String VARIABLE = SWRL_PREFIX + ":" + "Variable";
    
    public final static String ATOM_LIST = SWRL_PREFIX + ":" + "AtomList";
    
  } // Cls
  
  public static interface Slot {
    
    public final static String BODY = SWRL_PREFIX + ":" + "body";
    
    public final static String HEAD = SWRL_PREFIX + ":" + "head";
    
    public final static String ARGUMENTS = SWRL_PREFIX + ":" + "arguments";
    
    public final static String BUILTIN = SWRL_PREFIX + ":" + "builtin";
    
    public final static String ARGUMENT1 = SWRL_PREFIX + ":" + "argument1";
    
    public final static String ARGUMENT2 = SWRL_PREFIX + ":" + "argument2";
    
    public final static String CLASS_PREDICATE = SWRL_PREFIX + ":" + "classPredicate";
    
    public final static String PROPERTY_PREDICATE = SWRL_PREFIX + ":" + "propertyPredicate";
    
    public final static String DATA_RANGE = SWRL_PREFIX + ":" + "dataRange";
    
    public final static String ARGS = SWRLB_PREFIX + ":" + "args";
    
    public final static String MIN_ARGS = SWRLB_PREFIX + ":" + "minArgs";
    
    public final static String MAX_ARGS = SWRLB_PREFIX + ":" + "maxArgs";    
    
  } // Slot

  public static interface Annotations {
    
    public final static String IS_RULE_ENABLED = SWRLA_PREFIX + ":" + "isRuleEnabled";
    public final static String IS_RULE_GROUP_ENABLED = SWRLA_PREFIX + ":" + "isRuleGroupEnabled";
    public final static String RULE_GROUP = SWRLA_PREFIX + ":" + "RuleGroup";
    public final static String HAS_RULE_GROUP = SWRLA_PREFIX + ":" + "hasRuleGroup";
  } // Annotations

} // SWRLNames
