package edu.stanford.smi.protegex.owl.model;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface XSDNames {

    final static String PREFIX = "xsd:";
    
    //TT: replace PREFIX with XSD_NAMESPACE
    final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";


    final static String ANY_URI = XSD_NAMESPACE + "anyURI";

    final static String BASE_64_BINARY = XSD_NAMESPACE + "base64Binary";

    final static String BOOLEAN = XSD_NAMESPACE + "boolean";

    final static String BYTE = XSD_NAMESPACE + "byte";

    final static String DATE = XSD_NAMESPACE + "date";

    final static String DATE_TIME = XSD_NAMESPACE + "dateTime";

    final static String DECIMAL = XSD_NAMESPACE + "decimal";

    final static String DOUBLE = XSD_NAMESPACE + "double";

    final static String DURATION = XSD_NAMESPACE + "duration";

    final static String FLOAT = XSD_NAMESPACE + "float";

    final static String INT = XSD_NAMESPACE + "int";

    final static String INTEGER = XSD_NAMESPACE + "integer";

    final static String LONG = XSD_NAMESPACE + "long";

    final static String SHORT = XSD_NAMESPACE + "short";

    final static String STRING = XSD_NAMESPACE + "string";

    final static String TIME = XSD_NAMESPACE + "time";


    static interface Facet {

        final static String LENGTH = XSD_NAMESPACE + "length";

        final static String MAX_EXCLUSIVE = XSD_NAMESPACE + "maxExclusive";

        final static String MAX_INCLUSIVE = XSD_NAMESPACE + "maxInclusive";

        final static String MAX_LENGTH = XSD_NAMESPACE + "maxLength";

        final static String MIN_EXCLUSIVE = XSD_NAMESPACE + "minExclusive";

        final static String MIN_INCLUSIVE = XSD_NAMESPACE + "minInclusive";

        final static String MIN_LENGTH = XSD_NAMESPACE + "minLength";

        final static String PATTERN = XSD_NAMESPACE + "pattern";
    }
}
