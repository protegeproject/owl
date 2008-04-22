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
  public final static String SWRLM_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlm.owl";
  public final static String SWRLTBOX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/tbox.owl";
  public final static String SWRLABOX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/abox.owl";
  public final static String SWRLRDF_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.4/rdfb.owl";
  public final static String SWRLTEMPORAL_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl";
  public final static String SWRLXML_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl";
  public final static String SQWRL_IMPORT = "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl";
  
  public final static String SWRL_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrl.owl";
  public final static String SWRLB_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrlb.owl";
  
  public final static String SWRL_NAMESPACE = "http://www.w3.org/2003/11/swrl#";
  public final static String SWRLB_NAMESPACE = "http://www.w3.org/2003/11/swrlb#";
  public final static String SWRLA_NAMESPACE = "http://swrl.stanford.edu/ontologies/3.3/swrla.owl#";
  public final static String SWRLX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl#";
  public final static String SWRLM_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlm.owl#";
  public final static String SWRLTBOX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/tbox.owl#";
  public final static String SWRLABOX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/abox.owl#";
  public final static String SWRLRDF_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.4/rdfb.owl#";
  public final static String SWRLTEMPORAL_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl#";
  public final static String SWRLXML_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl#";
  public final static String SQWRL_NAMESPACE = "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#";
  
  public final static String SWRL_PREFIX = "swrl";
  public final static String SWRLB_PREFIX = "swrlb";
  public final static String SWRLA_PREFIX = "swrla";
  public final static String SWRLX_PREFIX = "swrlx";
  public final static String SWRLM_PREFIX = "swrlm";
  public final static String SWRLTBOX_PREFIX = "tbox";
  public final static String SWRLABOX_PREFIX = "abox";
  public final static String SWRLRDF = "rdfb";
  public final static String SWRLTEMPORAL_PREFIX = "temporal";
  public final static String SWRLXML_PREFIX = "swrlxml";
  public final static String SQWRL_PREFIX = "sqwrl";
  
  public static interface Cls {
    
    public final static String IMP = SWRL_NAMESPACE  +"Imp";
    public final static String ATOM = SWRL_NAMESPACE  + "Atom";
    public final static String CLASS_ATOM = SWRL_NAMESPACE  + "ClassAtom";
    public final static String INDIVIDUAL_PROPERTY_ATOM = SWRL_NAMESPACE  + "IndividualPropertyAtom";
    public final static String DATAVALUED_PROPERTY_ATOM = SWRL_NAMESPACE  + "DatavaluedPropertyAtom";
    public final static String DIFFERENT_INDIVIDUALS_ATOM = SWRL_NAMESPACE  + "DifferentIndividualsAtom";
    public final static String SAME_INDIVIDUAL_ATOM = SWRL_NAMESPACE  + "SameIndividualAtom";
    public final static String BUILTIN_ATOM = SWRL_NAMESPACE  + "BuiltinAtom";
    public final static String DATA_RANGE_ATOM = SWRL_NAMESPACE  + "DataRangeAtom";
    public final static String BUILTIN = SWRL_NAMESPACE  + "Builtin";
    public final static String VARIABLE = SWRL_NAMESPACE  + "Variable";
    public final static String ATOM_LIST = SWRL_NAMESPACE  + "AtomList";
  } // Cls
  
  public static interface Slot {
    public final static String BODY = SWRL_NAMESPACE  + "body";
    public final static String HEAD = SWRL_NAMESPACE  + "head";
    public final static String ARGUMENTS = SWRL_NAMESPACE  + "arguments";
    public final static String BUILTIN = SWRL_NAMESPACE  + "builtin";
    public final static String ARGUMENT1 = SWRL_NAMESPACE  + "argument1";
    public final static String ARGUMENT2 = SWRL_NAMESPACE  + "argument2";
    public final static String CLASS_PREDICATE = SWRL_NAMESPACE  + "classPredicate";
    public final static String PROPERTY_PREDICATE = SWRL_NAMESPACE  + "propertyPredicate";
    public final static String DATA_RANGE = SWRL_NAMESPACE  + "dataRange";
    public final static String ARGS = SWRLB_NAMESPACE  + "args";
    public final static String MIN_ARGS = SWRLB_NAMESPACE  + "minArgs";
    public final static String MAX_ARGS = SWRLB_NAMESPACE  + "maxArgs";    
  } // Slot

  public static interface CoreBuiltIns {
    public final static String EQUAL = SWRLB_NAMESPACE  + "equal";    
    public final static String NOT_EQUAL = SWRLB_NAMESPACE  + "notEqual";
    public final static String LESS_THAN = SWRLB_NAMESPACE  + "lessThan";
    public final static String LESS_THAN_OR_EQUAL = SWRLB_NAMESPACE  + "lessThanOrEqual";
    public final static String GREATER_THAN = SWRLB_NAMESPACE  + "greaterThan";
    public final static String GREATER_THAN_OR_EQUAL = SWRLB_NAMESPACE  + "greaterThanOrEqual";
    public final static String ADD = SWRLB_NAMESPACE  + "add";
    public final static String SUBTRACT = SWRLB_NAMESPACE  + "subtract";
    public final static String MULTIPLY = SWRLB_NAMESPACE  + "multiply";
    public final static String DIVIDE = SWRLB_NAMESPACE  + "divide";
    public final static String INTEGER_DIVIDE = SWRLB_NAMESPACE  + "integerDivide";
    public final static String MOD = SWRLB_NAMESPACE  + "mode";
    public final static String POW = SWRLB_NAMESPACE  + "pow";
    public final static String UNARY_PLUS = SWRLB_NAMESPACE  + "unaryPlus";
    public final static String UNARY_MINUS = SWRLB_NAMESPACE  + "unaryMinus";
    public final static String ABS = SWRLB_NAMESPACE  + "abs";
    public final static String CEILING = SWRLB_NAMESPACE  + "ceiling";
    public final static String FLOOR = SWRLB_NAMESPACE  + "floor";
    public final static String ROUND = SWRLB_NAMESPACE  + "round";
    public final static String ROUND_HALF_TO_EVEN = SWRLB_NAMESPACE  + "roundHalfToEven";
    public final static String SIN = SWRLB_NAMESPACE  + "sin";
    public final static String COS = SWRLB_NAMESPACE  + "cos";
    public final static String TAN = SWRLB_NAMESPACE  + "tan";
    public final static String BOOLEAN_NOT = SWRLB_NAMESPACE  + "booleanNot";
    public final static String STRING_EQUAL_IGNORE_CASE = SWRLB_NAMESPACE  + "stringEqualIgnoreCase";
    public final static String STRING_CONCAT = SWRLB_NAMESPACE  + "stringConcat";
    public final static String SUBSTRING = SWRLB_NAMESPACE  + "substring";
    public final static String STRING_LENGTH = SWRLB_NAMESPACE  + "stringLength";
    public final static String NORMALIZE_SPACE = SWRLB_NAMESPACE  + "normalizeSpace";
    public final static String UPPER_CASE = SWRLB_NAMESPACE  + "upperCase";
    public final static String LOWER_CASE = SWRLB_NAMESPACE  + "lowerCase";
    public final static String TRANSLATE = SWRLB_NAMESPACE  + "translate";
    public final static String CONTAINS = SWRLB_NAMESPACE  + "contains";
    public final static String CONTAINS_IGNORE_CASE = SWRLB_NAMESPACE  + "containsIgnoreCase";
    public final static String STARTS_WITH = SWRLB_NAMESPACE  + "startsWith";
    public final static String ENDS_WITH = SWRLB_NAMESPACE  + "endsWith";
    public final static String SUBSTRING_BEFORE = SWRLB_NAMESPACE  + "substringBefore";
    public final static String SUBSTRING_AFTER = SWRLB_NAMESPACE  + "substringAfter";
    public final static String MATCHES = SWRLB_NAMESPACE  + "matches";
    public final static String REPLACE = SWRLB_NAMESPACE  + "replace";
    public final static String TOKENIZE = SWRLB_NAMESPACE  + "tokenize";
    public final static String YEAR_MONTH_DURATION = SWRLB_NAMESPACE  + "yearMonthDuration";
    public final static String DAY_TIME_DURATION = SWRLB_NAMESPACE  + "dayTimeDuration";
    public final static String DATETIME = SWRLB_NAMESPACE  + "dateTime";
    public final static String DATE = SWRLB_NAMESPACE  + "date";
    public final static String TIME = SWRLB_NAMESPACE  + "time";
    public final static String ADD_YEAR_MONTH_DURATIONS = SWRLB_NAMESPACE  + "addYearMonthDurations";
    public final static String SUBTRACT_YEAR_MONTH_DURATIONS = SWRLB_NAMESPACE  + "subtractYearMonthDurations";
    public final static String MULTIPLY_YEAR_MONTH_DURATION = SWRLB_NAMESPACE  + "multiplyYearMonthDuration";
    public final static String DIVIDE_YEAR_MONTH_DURATIONS = SWRLB_NAMESPACE  + "divideYearMonthDurations";
    public final static String ADD_DAY_TIME_DURATIONS = SWRLB_NAMESPACE  + "addDayTimeDurations";
    public final static String SUBTRACT_DAY_TIME_DURATIONS = SWRLB_NAMESPACE  + "subtractDayTimeDurations";
    public final static String MULTIPLY_DAY_TIME_DURATIONS = SWRLB_NAMESPACE  + "multiplyDayTimeDurations";
    public final static String DIVIDE_DAY_TIME_DURATION = SWRLB_NAMESPACE  + "divideDayTimeDuration";
    public final static String SUBTRACT_DATES = SWRLB_NAMESPACE  + "subtractDates";
    public final static String SUBTRACT_TIMES = SWRLB_NAMESPACE  + "subtractTimes";
    public final static String ADD_YEAR_MONTH_DURATION_TO_DATETIME = SWRLB_NAMESPACE  + "addYearMonthDurationToDatetime";
    public final static String ADD_DAY_TIME_DURATION_TO_DATETIME = SWRLB_NAMESPACE  + "addDayTimeDurationToDatetime";
    public final static String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATETIME = SWRLB_NAMESPACE  + "subtractYearMonthDurationFromDatetime";
    public final static String SUBTRACT_DAY_TIME_DURATION_FROM_DATETIME = SWRLB_NAMESPACE  + "subtractDayTimeDurationFromDatetime";
    public final static String ADD_YEAR_MONTH_DURATION_TO_DATE = SWRLB_NAMESPACE  + "addYearMonthDurationToDate";
    public final static String ADD_DAY_TIME_DURATION_TO_DATE = SWRLB_NAMESPACE  + "addDayTimeDurationToDate";
    public final static String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE = SWRLB_NAMESPACE  + "subtractYearMonthDurationFromDate";
    public final static String SUBTRACT_DAY_TIME_DURATION_FROM_DATE = SWRLB_NAMESPACE  + "subtractDayTimeDurationFromDate";
    public final static String ADD_DAY_TIME_DURATION_TO_TIME = SWRLB_NAMESPACE  + "addDayTimeDurationToTime";
    public final static String SUBTRACT_DAY_TIME_DURATION_FROM_TIME = SWRLB_NAMESPACE  + "subtractDatTimeDurationFromTime";
    public final static String SUBTRACT_DATETIMES_YIELDING_YEAR_MONTH_DURATION = SWRLB_NAMESPACE  + "subtractDatetimesYieldingYearMonthDuration";
    public final static String SUBTRACT_DATETIMES_YIELDING_DAY_TIME_DURATION = SWRLB_NAMESPACE  + "subtractDatetimesYieldingDayTimeDuration";
  }

  public static interface Annotations 
  {    
    public final static String IS_RULE_ENABLED = SWRLA_NAMESPACE  + "isRuleEnabled";
    public final static String IS_RULE_GROUP_ENABLED = SWRLA_NAMESPACE  + "isRuleGroupEnabled";
    public final static String RULE_GROUP = SWRLA_NAMESPACE  + "RuleGroup";
    public final static String HAS_RULE_GROUP = SWRLA_NAMESPACE  + "hasRuleGroup";
  } // Annotations

} // SWRLNames
