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

  public static interface CoreBuiltIns {
    public final static String EQUAL = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "equal";    
    public final static String NOT_EQUAL = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "notEqual";
    public final static String LESS_THAN = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "lessThan";
    public final static String LESS_THAN_OR_EQUAL = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "lessThanOrEqual";
    public final static String GREATER_THAN = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "greaterThan";
    public final static String GREATER_THAN_OR_EQUAL = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "greaterThanOrEqual";
    public final static String ADD = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "add";
    public final static String SUBTRACT = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtract";
    public final static String MULTIPLY = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "multiply";
    public final static String DIVIDE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "divide";
    public final static String INTEGER_DIVIDE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "integerDivide";
    public final static String MOD = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "mode";
    public final static String POW = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "pow";
    public final static String UNARY_PLUS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "unaryPlus";
    public final static String UNARY_MINUS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "unaryMinus";
    public final static String ABS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "abs";
    public final static String CEILING = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "ceiling";
    public final static String FLOOR = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "floor";
    public final static String ROUND = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "round";
    public final static String ROUND_HALF_TO_EVEN = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "roundHalfToEven";
    public final static String SIN = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "sin";
    public final static String COS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "cos";
    public final static String TAN = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "tan";
    public final static String BOOLEAN_NOT = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "booleanNot";
    public final static String STRING_EQUAL_IGNORE_CASE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "stringEqualIgnoreCase";
    public final static String STRING_CONCAT = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "stringConcat";
    public final static String SUBSTRING = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "substring";
    public final static String STRING_LENGTH = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "stringLength";
    public final static String NORMALIZE_SPACE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "normalizeSpace";
    public final static String UPPER_CASE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "upperCase";
    public final static String LOWER_CASE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "lowerCase";
    public final static String TRANSLATE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "translate";
    public final static String CONTAINS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "contains";
    public final static String CONTAINS_IGNORE_CASE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "containsIgnoreCase";
    public final static String STARTS_WITH = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "startsWith";
    public final static String ENDS_WITH = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "endsWith";
    public final static String SUBSTRING_BEFORE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "substringBefore";
    public final static String SUBSTRING_AFTER = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "substringAfter";
    public final static String MATCHES = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "matches";
    public final static String REPLACE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "replace";
    public final static String TOKENIZE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "tokenize";
    public final static String YEAR_MONTH_DURATION = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "yearMonthDuration";
    public final static String DAY_TIME_DURATION = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "dayTimeDuration";
    public final static String DATETIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "dateTime";
    public final static String DATE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "date";
    public final static String TIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "time";
    public final static String ADD_YEAR_MONTH_DURATIONS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "addYearMonthDurations";
    public final static String SUBTRACT_YEAR_MONTH_DURATIONS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractYearMonthDurations";
    public final static String MULTIPLY_YEAR_MONTH_DURATION = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "multiplyYearMonthDuration";
    public final static String DIVIDE_YEAR_MONTH_DURATIONS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "divideYearMonthDurations";
    public final static String ADD_DAY_TIME_DURATIONS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "addDayTimeDurations";
    public final static String SUBTRACT_DAY_TIME_DURATIONS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractDayTimeDurations";
    public final static String MULTIPLY_DAY_TIME_DURATIONS = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "multiplyDayTimeDurations";
    public final static String DIVIDE_DAY_TIME_DURATION = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "divideDayTimeDuration";
    public final static String SUBTRACT_DATES = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractDates";
    public final static String SUBTRACT_TIMES = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractTimes";
    public final static String ADD_YEAR_MONTH_DURATION_TO_DATETIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "addYearMonthDurationToDatetime";
    public final static String ADD_DAY_TIME_DURATION_TO_DATETIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "addDayTimeDurationToDatetime";
    public final static String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATETIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractYearMonthDurationFromDatetime";
    public final static String SUBTRACT_DAY_TIME_DURATION_FROM_DATETIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractDayTimeDurationFromDatetime";
    public final static String ADD_YEAR_MONTH_DURATION_TO_DATE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "addYearMonthDurationToDate";
    public final static String ADD_DAY_TIME_DURATION_TO_DATE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "addDayTimeDurationToDate";
    public final static String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractYearMonthDurationFromDate";
    public final static String SUBTRACT_DAY_TIME_DURATION_FROM_DATE = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractDayTimeDurationFromDate";
    public final static String ADD_DAY_TIME_DURATION_TO_TIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "addDayTimeDurationToTime";
    public final static String SUBTRACT_DAY_TIME_DURATION_FROM_TIME = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractDatTimeDurationFromTime";
    public final static String SUBTRACT_DATETIMES_YIELDING_YEAR_MONTH_DURATION = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractDatetimesYieldingYearMonthDuration";
    public final static String SUBTRACT_DATETIMES_YIELDING_DAY_TIME_DURATION = SWRLB_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "subtractDatetimesYieldingDayTimeDuration";
  }

  public static interface Annotations 
  {    
    public final static String IS_RULE_ENABLED = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "isRuleEnabled";
    public final static String IS_RULE_GROUP_ENABLED = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "isRuleGroupEnabled";
    public final static String RULE_GROUP = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "RuleGroup";
    public final static String HAS_RULE_GROUP = SWRLA_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "hasRuleGroup";
  } // Annotations

} // SWRLNames
