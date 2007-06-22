package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Model;


/**
 * Defines the names of the OWL system ontology.
 * This corresponds to the Model interface in general Protege.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLNames {
	
	final static String OWL_PREFIX = "owl";

    public static interface Cls {

        public final static String ALL_DIFFERENT = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "AllDifferent";

        public final static String ALL_VALUES_FROM_RESTRICTION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "AllValuesFromRestriction";

        public final static String ANNOTATION_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "AnnotationProperty";

        public final static String ANONYMOUS_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "AnonymousClass";       

        public final static String CARDINALITY_RESTRICTION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "CardinalityRestriction";

        public final static String COMPLEMENT_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "ComplementClass";

        public final static String DATATYPE_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "DatatypeProperty";

        public final static String DATA_RANGE = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "DataRange";

        public final static String DEPRECATED_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "DeprecatedClass";

        public final static String DEPRECATED_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "DeprecatedProperty";

        public final static String ENUMERATED_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "EnumeratedClass";

        public final static String FUNCTIONAL_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "FunctionalProperty";

        public final static String HAS_VALUE_RESTRICTION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "HasValueRestriction";

        public final static String INTERSECTION_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "IntersectionClass";

        public final static String INVERSE_FUNCTIONAL_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "InverseFunctionalProperty";

        public final static String LOGICAL_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "LogicalClass";

        public final static String MAX_CARDINALITY_RESTRICTION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "MaxCardinalityRestriction";

        public final static String MIN_CARDINALITY_RESTRICTION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "MinCardinalityRestriction";

        public final static String NAMED_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Class";

        public final static String NOTHING = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Nothing";

        public final static String ONTOLOGY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Ontology";

        public final static String OBJECT_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "ObjectProperty";        

        public final static String RESTRICTION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Restriction";

        public final static String SOME_VALUES_FROM_RESTRICTION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "SomeValuesFromRestriction";

        public final static String SYMMETRIC_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "SymmetricProperty";

        public final static String THING = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "Thing";

        public final static String TRANSITIVE_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "TransitiveProperty";

        public final static String UNION_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "UnionClass";
        
        
        public final static String ANONYMOUS_ROOT = ":OWL-ANONYMOUS-ROOT";
        
        public final static String OWL_CLASS = ":OWL-CLASS";
    }

    public static interface Slot {

        public final static String ALL_VALUES_FROM = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "allValuesFrom";

        public final static String BACKWARD_COMPATIBLE_WITH = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "backwardCompatibleWith";

        public final static String CARDINALITY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "cardinality";

        public final static String COMPLEMENT_OF = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "complementOf";

        public final static String DIFFERENT_FROM = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "differentFrom";

        public final static String DISJOINT_WITH = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "disjointWith";

        public final static String DISTINCT_MEMBERS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "distinctMembers";

        public final static String EQUIVALENT_CLASS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "equivalentClass";

        public final static String EQUIVALENT_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "equivalentProperty";

        /**
         * @deprecated use EQUIVALENT_PROPERTY instead
         */
        public final static String EQUIVALENT_PROPERTIES = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "equivalentProperty";

        public final static String HAS_VALUE = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "hasValue";

        public final static String INCOMPATIBLE_WITH = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "incompatibleWith";

        public final static String INTERSECTION_OF = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "intersectionOf";

        public final static String INVERSE_OF = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "inverseOf";

        public final static String MAX_CARDINALITY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "maxCardinality";

        public final static String MIN_CARDINALITY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "minCardinality";

        public final static String ON_PROPERTY = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "onProperty";

        public final static String IMPORTS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "imports";

        public final static String ONE_OF = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "oneOf";

        public final static String PRIOR_VERSION = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "priorVersion";

        public final static String SAME_AS = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "sameAs";

        public final static String SOME_VALUES_FROM = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "someValuesFrom";

        public final static String SUBCLASSES_DISJOINT = ProtegeNames.PREFIX + "subclassesDisjoint";

        public final static String VALUES_FROM = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "valuesFrom";

        public final static String VERSION_INFO = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "versionInfo";

        public final static String UNION_OF = OWL_PREFIX + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + "unionOf";
        
        
        public final static String RESOURCE_URI = ":OWL-RESOURCE-URI";
        
        public final static String ONTOLOGY_PREFIXES = ":OWL-ONTOLOGY-PREFIXES";
    }


    final static int CLASSIFICATION_STATUS_UNDEFINED = 0;

    final static int CLASSIFICATION_STATUS_CONSISTENT_AND_UNCHANGED = 1;

    final static int CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED = 3;

    /**
     * @deprecated Please use the <code>isConsistent</code> method
     *             on <code>OWLNamedClass</code>.
     */
    final static int CLASSIFICATION_STATUS_INCONSISTENT = 2;

    

    public interface ClsID {

        FrameID NAMED_CLASS = FrameID.createSystem(9004);

        FrameID THING = Model.ClsID.THING;
    }
}
