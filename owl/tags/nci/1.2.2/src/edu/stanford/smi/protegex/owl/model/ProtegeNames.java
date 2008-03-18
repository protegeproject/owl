package edu.stanford.smi.protegex.owl.model;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.OntModel;

/**
 * The namespaces and names from the Protege meta ontology.
 * This ontology is used to represent Protege-specific metadata such as
 * whether a class is abstract or not.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeNames {

    public static String PROTEGE_PREFIX = "protege";

    public final static String PREFIX = PROTEGE_PREFIX + ":";

    public static interface Slot {

        final static String ABSTRACT = "protege:abstract";

        final static String CLASSIFICATION_STATUS = "protege:classificationStatus";

        final static String INFERRED_TYPE = "protege:inferredType";

        final static String INFERRED_SUBCLASSES = "protege:inferredSuperclassOf";

        final static String INFERRED_SUPERCLASSES = "protege:inferredSubclassOf";

	    final static String IS_COMMENTED_OUT = "protege:isCommentedOut";
    }

    public static String FILE = "http://protege.stanford.edu/plugins/owl/protege";

    public final static String NS = FILE + "#";

    public final static String ABSTRACT = "abstract";

    public final static String ALLOWED_PARENT = "allowedParent";

    public final static String DEFAULT_LANGUAGE = "defaultLanguage";

    public final static String EXCLUDED_TEST = "excludedTest";

    public final static String PROBE_CLASS = "probeClass";

    public static final String READ_ONLY = "readOnly";

    public final static String SUBCLASSES_DISJOINT = "subclassesDisjoint";

    public final static String TODO_PREFIX = "todoPrefix";

    public final static String TODO_PROPERTY = "todoProperty";

    public final static String RDFS_SUB_CLASS_OF_INVERSE = "superClassOf";

    public final static String RDFS_SUB_CLASS_OF_INVERSE_PREFIXED = PREFIX + RDFS_SUB_CLASS_OF_INVERSE;

    /**
     * Not represented as a property yet, but in the future the
     * :DIRECT-INSTANCES system slot could be mapped into this,
     * to access the inverses of rdf:type
     */
    public final static String RDF_TYPE_INVERSE = "typeOf";

    public final static String RDF_TYPE_INVERSE_PREFIXED = PREFIX + RDF_TYPE_INVERSE;

    public final static String USED_LANGUAGE = "usedLanguage";


    public static AnnotationProperty getAbstractProperty(OntModel ontModel) {
        return ontModel.getAnnotationProperty(NS + ABSTRACT);
    }


    public static AnnotationProperty getAllowedParentProperty(OntModel ontModel) {
        return ontModel.getAnnotationProperty(NS + ALLOWED_PARENT);
    }


    public static String getProbeClassSlotName() {
        return PREFIX + PROBE_CLASS;
    }


    public static String getReadOnlySlotName() {
        return PREFIX + READ_ONLY;
    }


    public static String getSubclassesDisjointSlotName() {
        return PREFIX + SUBCLASSES_DISJOINT;
    }


    public static String getTodoPrefixSlotName() {
        return PREFIX + TODO_PREFIX;
    }


    public static String getTodoPropertySlotName() {
        return PREFIX + TODO_PROPERTY;
    }


    public static String getDefaultLanguageSlotName() {
        return PREFIX + DEFAULT_LANGUAGE;
    }


    public static String getUsedLanguagesSlotName() {
        return PREFIX + USED_LANGUAGE;
    }
}
