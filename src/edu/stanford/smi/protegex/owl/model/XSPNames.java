package edu.stanford.smi.protegex.owl.model;

/**
 * Constants for the Protege XML Schema datatype extensions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface XSPNames {

    final static String PREFIX = "xsp";

    final static String URI = "http://www.owl-ontologies.com/2005/08/07/xsp.owl";

    final static String NS = URI + "#";


    final static String BASE = PREFIX + ":base";

    final static String FRACTION_DIGITS = PREFIX + ":fractionDigits";

    final static String LENGTH = PREFIX + ":length";

    final static String MIN_EXCLUSIVE = PREFIX + ":minExclusive";

    final static String MIN_INCLUSIVE = PREFIX + ":minInclusive";

    final static String MAX_EXCLUSIVE = PREFIX + ":maxExclusive";

    final static String MAX_INCLUSIVE = PREFIX + ":maxInclusive";

    final static String MIN_LENGTH = PREFIX + ":minLength";

    final static String MAX_LENGTH = PREFIX + ":maxLength";

    final static String PATTERN = PREFIX + ":pattern";

    final static String TOTAL_DIGITS = PREFIX + ":totalDigits";
}
