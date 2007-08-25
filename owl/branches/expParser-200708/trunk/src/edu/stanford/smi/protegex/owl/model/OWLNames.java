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
	
	//added by TT for use of the fully qualified name for the owl metamodel
	// !check the namespace of constants again! not all of them should have the owl namespace
	
	static String OWL_NAMESPACE= "http://www.w3.org/2002/07/owl#";

    public static interface Cls {

        public final static String ALL_DIFFERENT = OWL_NAMESPACE + "AllDifferent";

        public final static String ALL_VALUES_FROM_RESTRICTION = OWL_NAMESPACE + "AllValuesFromRestriction";

        public final static String ANNOTATION_PROPERTY = OWL_NAMESPACE + "AnnotationProperty";

        public final static String ANONYMOUS_CLASS = OWL_NAMESPACE + "AnonymousClass";

        public final static String ANONYMOUS_ROOT = ":OWL-ANONYMOUS-ROOT";

        public final static String CARDINALITY_RESTRICTION = OWL_NAMESPACE + "CardinalityRestriction";

        public final static String COMPLEMENT_CLASS = OWL_NAMESPACE + "ComplementClass";

        public final static String DATATYPE_PROPERTY = OWL_NAMESPACE + "DatatypeProperty";

        public final static String DATA_RANGE = OWL_NAMESPACE + "DataRange";

        public final static String DEPRECATED_CLASS = OWL_NAMESPACE + "DeprecatedClass";

        public final static String DEPRECATED_PROPERTY = OWL_NAMESPACE + "DeprecatedProperty";

        public final static String ENUMERATED_CLASS = OWL_NAMESPACE + "EnumeratedClass";

        public final static String FUNCTIONAL_PROPERTY = OWL_NAMESPACE + "FunctionalProperty";

        public final static String HAS_VALUE_RESTRICTION = OWL_NAMESPACE + "HasValueRestriction";

        public final static String INTERSECTION_CLASS = OWL_NAMESPACE + "IntersectionClass";

        public final static String INVERSE_FUNCTIONAL_PROPERTY = OWL_NAMESPACE + "InverseFunctionalProperty";

        public final static String LOGICAL_CLASS = OWL_NAMESPACE + "LogicalClass";

        public final static String MAX_CARDINALITY_RESTRICTION = OWL_NAMESPACE + "MaxCardinalityRestriction";

        public final static String MIN_CARDINALITY_RESTRICTION = OWL_NAMESPACE + "MinCardinalityRestriction";

        public final static String NAMED_CLASS = OWL_NAMESPACE + "Class";

        public final static String NOTHING = OWL_NAMESPACE + "Nothing";

        public final static String ONTOLOGY = OWL_NAMESPACE + "Ontology";

        public final static String OBJECT_PROPERTY = OWL_NAMESPACE + "ObjectProperty";

        public final static String OWL_CLASS = ":OWL-CLASS";

        public final static String RESTRICTION = OWL_NAMESPACE + "Restriction";

        public final static String SOME_VALUES_FROM_RESTRICTION = OWL_NAMESPACE + "SomeValuesFromRestriction";

        public final static String SYMMETRIC_PROPERTY = OWL_NAMESPACE + "SymmetricProperty";

        public final static String THING = OWL_NAMESPACE + "Thing";

        public final static String TRANSITIVE_PROPERTY = OWL_NAMESPACE + "TransitiveProperty";

        public final static String UNION_CLASS = OWL_NAMESPACE + "UnionClass";
    }

    public static interface Slot {

        public final static String ALL_VALUES_FROM = OWL_NAMESPACE + "allValuesFrom";

        public final static String BACKWARD_COMPATIBLE_WITH = OWL_NAMESPACE + "backwardCompatibleWith";

        public final static String CARDINALITY = OWL_NAMESPACE + "cardinality";

        public final static String COMPLEMENT_OF = OWL_NAMESPACE + "complementOf";

        public final static String DIFFERENT_FROM = OWL_NAMESPACE + "differentFrom";

        public final static String DISJOINT_WITH = OWL_NAMESPACE + "disjointWith";

        public final static String DISTINCT_MEMBERS = OWL_NAMESPACE + "distinctMembers";

        public final static String EQUIVALENT_CLASS = OWL_NAMESPACE + "equivalentClass";

        public final static String EQUIVALENT_PROPERTY = OWL_NAMESPACE + "equivalentProperty";

        /**
         * @deprecated use EQUIVALENT_PROPERTY instead
         */
        public final static String EQUIVALENT_PROPERTIES = OWL_NAMESPACE + "equivalentProperty";

        public final static String HAS_VALUE = OWL_NAMESPACE + "hasValue";

        public final static String INCOMPATIBLE_WITH = OWL_NAMESPACE + "incompatibleWith";

        public final static String INTERSECTION_OF = OWL_NAMESPACE + "intersectionOf";

        public final static String INVERSE_OF = OWL_NAMESPACE + "inverseOf";

        public final static String MAX_CARDINALITY = OWL_NAMESPACE + "maxCardinality";

        public final static String MIN_CARDINALITY = OWL_NAMESPACE + "minCardinality";

        public final static String ON_PROPERTY = OWL_NAMESPACE + "onProperty";

        public final static String IMPORTS = OWL_NAMESPACE + "imports";

        public final static String ONTOLOGY_PREFIXES = ":OWL-ONTOLOGY-PREFIXES";

        public final static String ONE_OF = OWL_NAMESPACE + "oneOf";

        public final static String PRIOR_VERSION = OWL_NAMESPACE + "priorVersion";

        public final static String RESOURCE_URI = ":OWL-RESOURCE-URI";

        public final static String SAME_AS = OWL_NAMESPACE + "sameAs";

        public final static String SOME_VALUES_FROM = OWL_NAMESPACE + "someValuesFrom";

        public final static String SUBCLASSES_DISJOINT = ProtegeNames.PREFIX + "subclassesDisjoint";

        public final static String VALUES_FROM = OWL_NAMESPACE + "valuesFrom";

        public final static String VERSION_INFO = OWL_NAMESPACE + "versionInfo";

        public final static String UNION_OF = OWL_NAMESPACE + "unionOf";
    }


    final static int CLASSIFICATION_STATUS_UNDEFINED = 0;

    final static int CLASSIFICATION_STATUS_CONSISTENT_AND_UNCHANGED = 1;

    final static int CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED = 3;

    /**
     * @deprecated Please use the <code>isConsistent</code> method
     *             on <code>OWLNamedClass</code>.
     */
    final static int CLASSIFICATION_STATUS_INCONSISTENT = 2;

    final static String OWL_PREFIX = "owl";


    public interface ClsID {

        FrameID NAMED_CLASS = FrameID.createSystem(9004);

        FrameID THING = Model.ClsID.THING;
    }
}
