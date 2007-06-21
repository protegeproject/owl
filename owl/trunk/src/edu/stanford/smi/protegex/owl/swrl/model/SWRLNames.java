package edu.stanford.smi.protegex.owl.swrl.model;

import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;

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
    
    public final static String IMP = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR +"Imp";
    
    public final static String ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Atom";
    
    public final static String CLASS_ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "ClassAtom";
    
    public final static String INDIVIDUAL_PROPERTY_ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "IndividualPropertyAtom";
    
    public final static String DATAVALUED_PROPERTY_ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "DatavaluedPropertyAtom";
    
    public final static String DIFFERENT_INDIVIDUALS_ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "DifferentIndividualsAtom";
    
    public final static String SAME_INDIVIDUAL_ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "SameIndividualAtom";
    
    public final static String BUILTIN_ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "BuiltinAtom";
    
    public final static String DATA_RANGE_ATOM = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "DataRangeAtom";
    
    public final static String BUILTIN = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Builtin";
    
    public final static String VARIABLE = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Variable";
    
    public final static String ATOM_LIST = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "AtomList";
    
  } // Cls
  
  public static interface Slot {
    
    public final static String BODY = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "body";
    
    public final static String HEAD = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "head";
    
    public final static String ARGUMENTS = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "arguments";
    
    public final static String BUILTIN = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "builtin";
    
    public final static String ARGUMENT1 = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "argument1";
    
    public final static String ARGUMENT2 = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "argument2";
    
    public final static String CLASS_PREDICATE = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "classPredicate";
    
    public final static String PROPERTY_PREDICATE = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "propertyPredicate";
    
    public final static String DATA_RANGE = SWRL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "dataRange";
    
    public final static String ARGS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "args";
    
    public final static String MIN_ARGS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "minArgs";
    
    public final static String MAX_ARGS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "maxArgs";    
    
  } // Slot

  public static interface Annotations {
    
    public final static String IS_RULE_ENABLED = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "isRuleEnabled";
    public final static String IS_RULE_GROUP_ENABLED = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "isRuleGroupEnabled";
    public final static String RULE_GROUP = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "RuleGroup";
    public final static String HAS_RULE_GROUP = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "hasRuleGroup";
  } // Annotations

} // SWRLNames
