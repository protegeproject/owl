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

	public final static String PREFIX_LOCALNAME_SEPARATOR = ":";

	//maybe move this later to a different class 
	public final static String DEFAULT_DEFAULT_BASE = "http://www.owl-ontologies.com/unnamed.owl";
	
	//maybe move this later to a different class
	public final static String DEFAULT_DEFAULT_NAMESPACE = DEFAULT_DEFAULT_BASE + "#";
	
	//should be used only in initializations
	public final static String DEFAULT_ONTOLOGY = DEFAULT_DEFAULT_BASE;

    public static String PROTEGE_PREFIX = "protege";

    public final static String PREFIX = PROTEGE_PREFIX + ":";


    public static interface Slot {

        final static String ABSTRACT = PROTEGE_OWL_NAMESPACE + "abstract";

        final static String CLASSIFICATION_STATUS = PROTEGE_OWL_NAMESPACE + "classificationStatus";

        final static String INFERRED_TYPE = PROTEGE_OWL_NAMESPACE + "inferredType";

        final static String INFERRED_SUBCLASSES = PROTEGE_OWL_NAMESPACE + "inferredSuperclassOf";

        final static String INFERRED_SUPERCLASSES = PROTEGE_OWL_NAMESPACE + "inferredSubclassOf";

	    final static String IS_COMMENTED_OUT = PROTEGE_OWL_NAMESPACE + "isCommentedOut";
    }

    public static String PROTEGE_OWL_ONTOLOGY = "http://protege.stanford.edu/plugins/owl/protege";
    
    /**
     * @deprecated use {@link #PROTEGE_OWL_ONTOLOGY}
     */
    @Deprecated
    public static String FILE = PROTEGE_OWL_ONTOLOGY;

    public final static String PROTEGE_OWL_NAMESPACE = PROTEGE_OWL_ONTOLOGY + "#";
 
    /**
     * @deprecated use {@link #PROTEGE_OWL_NAMESPACE}
     */
    @Deprecated
    public final static String NS = PROTEGE_OWL_NAMESPACE;

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
        return ontModel.getAnnotationProperty(PROTEGE_OWL_NAMESPACE + ABSTRACT);
    }


    public static AnnotationProperty getAllowedParentProperty(OntModel ontModel) {
        return ontModel.getAnnotationProperty(PROTEGE_OWL_NAMESPACE + ALLOWED_PARENT);
    }


    public static String getProbeClassSlotName() {
        return PROTEGE_OWL_NAMESPACE + PROBE_CLASS;
    }


    public static String getReadOnlySlotName() {
        return PROTEGE_OWL_NAMESPACE + READ_ONLY;
    }


    public static String getSubclassesDisjointSlotName() {
        return PROTEGE_OWL_NAMESPACE + SUBCLASSES_DISJOINT;
    }


    public static String getTodoPrefixSlotName() {
        return PROTEGE_OWL_NAMESPACE + TODO_PREFIX;
    }


    public static String getTodoPropertySlotName() {
        return PROTEGE_OWL_NAMESPACE + TODO_PROPERTY;
    }


    public static String getDefaultLanguageSlotName() {
        return PROTEGE_OWL_NAMESPACE + DEFAULT_LANGUAGE;
    }


    public static String getUsedLanguagesSlotName() {
        return PROTEGE_OWL_NAMESPACE + USED_LANGUAGE;
    }


}
