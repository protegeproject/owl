package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protege.model.FrameID;


/**
 * Defines the names of the RDFS related parts of the OWL system ontology.
 * This corresponds to the Model interface in general Protege.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFSNames {

    public static interface Cls {

        final static String CONTAINER = "rdfs:Container";

        final static String DATATYPE = "rdfs:Datatype";

        final static String LITERAL = "rdfs:Literal";

        final static String NAMED_CLASS = "rdfs:Class";
    }


    public interface ClsID {

        FrameID NAMED_CLASS = FrameID.createSystem(9003);
    }

    public static interface Slot {

        final static String COMMENT = "rdfs:comment";

        final static String DOMAIN = "rdfs:domain";

        final static String IS_DEFINED_BY = "rdfs:isDefinedBy";

        final static String LABEL = "rdfs:label";

        /**
         * @deprecated use LABEL
         */
        final static String LABELS = "rdfs:label";

        final static String MEMBER = "rdfs:member";

        final static String RANGE = "rdfs:range";

        final static String SEE_ALSO = "rdfs:seeAlso";

        final static String SUB_CLASS_OF = "rdfs:subClassOf";

        final static String SUB_PROPERTY_OF = "rdfs:subPropertyOf";
    }

    final static String RDFS_PREFIX = "rdfs";
}
