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

    public static interface Cls {

        public final static String ALL_DIFFERENT = "owl:AllDifferent";

        public final static String ALL_VALUES_FROM_RESTRICTION = "owl:AllValuesFromRestriction";

        public final static String ANNOTATION_PROPERTY = "owl:AnnotationProperty";

        public final static String ANONYMOUS_CLASS = "owl:AnonymousClass";

        public final static String ANONYMOUS_ROOT = ":OWL-ANONYMOUS-ROOT";

        public final static String CARDINALITY_RESTRICTION = "owl:CardinalityRestriction";

        public final static String COMPLEMENT_CLASS = "owl:ComplementClass";

        public final static String DATATYPE_PROPERTY = "owl:DatatypeProperty";

        public final static String DATA_RANGE = "owl:DataRange";

        public final static String DEPRECATED_CLASS = "owl:DeprecatedClass";

        public final static String DEPRECATED_PROPERTY = "owl:DeprecatedProperty";

        public final static String ENUMERATED_CLASS = "owl:EnumeratedClass";

        public final static String FUNCTIONAL_PROPERTY = "owl:FunctionalProperty";

        public final static String HAS_VALUE_RESTRICTION = "owl:HasValueRestriction";

        public final static String INTERSECTION_CLASS = "owl:IntersectionClass";

        public final static String INVERSE_FUNCTIONAL_PROPERTY = "owl:InverseFunctionalProperty";

        public final static String LOGICAL_CLASS = "owl:LogicalClass";

        public final static String MAX_CARDINALITY_RESTRICTION = "owl:MaxCardinalityRestriction";

        public final static String MIN_CARDINALITY_RESTRICTION = "owl:MinCardinalityRestriction";

        public final static String NAMED_CLASS = "owl:Class";

        public final static String NOTHING = "owl:Nothing";

        public final static String ONTOLOGY = "owl:Ontology";

        public final static String OBJECT_PROPERTY = "owl:ObjectProperty";

        public final static String OWL_CLASS = ":OWL-CLASS";

        public final static String RESTRICTION = "owl:Restriction";

        public final static String SOME_VALUES_FROM_RESTRICTION = "owl:SomeValuesFromRestriction";

        public final static String SYMMETRIC_PROPERTY = "owl:SymmetricProperty";

        public final static String THING = "owl:Thing";

        public final static String TRANSITIVE_PROPERTY = "owl:TransitiveProperty";

        public final static String UNION_CLASS = "owl:UnionClass";
    }

    public static interface Slot {

        public final static String ALL_VALUES_FROM = "owl:allValuesFrom";

        public final static String BACKWARD_COMPATIBLE_WITH = "owl:backwardCompatibleWith";

        public final static String CARDINALITY = "owl:cardinality";

        public final static String COMPLEMENT_OF = "owl:complementOf";

        public final static String DIFFERENT_FROM = "owl:differentFrom";

        public final static String DISJOINT_WITH = "owl:disjointWith";

        public final static String DISTINCT_MEMBERS = "owl:distinctMembers";

        public final static String EQUIVALENT_CLASS = "owl:equivalentClass";

        public final static String EQUIVALENT_PROPERTY = "owl:equivalentProperty";

        /**
         * @deprecated use EQUIVALENT_PROPERTY instead
         */
        public final static String EQUIVALENT_PROPERTIES = "owl:equivalentProperty";

        public final static String HAS_VALUE = "owl:hasValue";

        public final static String INCOMPATIBLE_WITH = "owl:incompatibleWith";

        public final static String INTERSECTION_OF = "owl:intersectionOf";

        public final static String INVERSE_OF = "owl:inverseOf";

        public final static String MAX_CARDINALITY = "owl:maxCardinality";

        public final static String MIN_CARDINALITY = "owl:minCardinality";

        public final static String ON_PROPERTY = "owl:onProperty";

        public final static String IMPORTS = "owl:imports";

        public final static String ONTOLOGY_PREFIXES = ":OWL-ONTOLOGY-PREFIXES";

        public final static String ONE_OF = "owl:oneOf";

        public final static String PRIOR_VERSION = "owl:priorVersion";

        public final static String RESOURCE_URI = ":OWL-RESOURCE-URI";

        public final static String SAME_AS = "owl:sameAs";

        public final static String SOME_VALUES_FROM = "owl:someValuesFrom";

        public final static String SUBCLASSES_DISJOINT = "protege:subclassesDisjoint";

        public final static String VALUES_FROM = "owl:valuesFrom";

        public final static String VERSION_INFO = "owl:versionInfo";

        public final static String UNION_OF = "owl:unionOf";
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

        FrameID NAMED_CLASS = new FrameID(Cls.NAMED_CLASS);

        FrameID THING = new FrameID(Cls.THING);
    }
}
