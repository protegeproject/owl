package edu.stanford.smi.protegex.owl.model;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface XSDNames {

    final static String PREFIX = "xsd:";


    final static String ANY_URI = PREFIX + "anyURI";

    final static String BASE_64_BINARY = PREFIX + "base64Binary";

    final static String BOOLEAN = PREFIX + "boolean";

    final static String BYTE = PREFIX + "byte";

    final static String DATE = PREFIX + "date";

    final static String DATE_TIME = PREFIX + "dateTime";

    final static String DECIMAL = PREFIX + "decimal";

    final static String DOUBLE = PREFIX + "double";

    final static String DURATION = PREFIX + "duration";

    final static String FLOAT = PREFIX + "float";

    final static String INT = PREFIX + "int";

    final static String INTEGER = PREFIX + "integer";

    final static String LONG = PREFIX + "long";

    final static String SHORT = PREFIX + "short";

    final static String STRING = PREFIX + "string";

    final static String TIME = PREFIX + "time";


    static interface Facet {

        final static String LENGTH = PREFIX + "length";

        final static String MAX_EXCLUSIVE = PREFIX + "maxExclusive";

        final static String MAX_INCLUSIVE = PREFIX + "maxInclusive";

        final static String MAX_LENGTH = PREFIX + "maxLength";

        final static String MIN_EXCLUSIVE = PREFIX + "minExclusive";

        final static String MIN_INCLUSIVE = PREFIX + "minInclusive";

        final static String MIN_LENGTH = PREFIX + "minLength";

        final static String PATTERN = PREFIX + "pattern";
    }
}
