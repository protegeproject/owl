package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protege.model.FrameID;


/**
 * Defines the names of the RDF(S) related parts of the OWL system ontology.
 * This corresponds to the Model interface in general Protege.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFNames {

    public static interface Cls {

        final static String ALT = "rdf:Alt";

        final static String BAG = "rdf:Bag";

        final static String DESCRIPTION = "rdf:Description";

        final static String EXTERNAL_RESOURCE = "protege:ExternalResource";

        final static String LIST = "rdf:List";

        final static String PROPERTY = "rdf:Property";

        final static String SEQ = "rdf:Seq";

        final static String STATEMENT = "rdf:Statement";
    }


    public interface ClsID {

        FrameID PROPERTY = FrameID.createSystem(9007);
    }

    public static interface Slot {

        final static String ABOUT = "rdf:about";

        final static String DATATYPE = "rdf:datatype";

        final static String FIRST = "rdf:first";

        final static String ID = "rdf:ID";

        final static String OBJECT = "rdf:object";

        final static String PARSE_TYPE = "rdf:parseType";

        final static String PREDICATE = "rdf:predicate";

        final static String RESOURCE = "rdf:resource";

        final static String REST = "rdf:rest";

        final static String SUBJECT = "rdf:subject";

        final static String TYPE = "rdf:type";

        final static String VALUE = "rdf:value";
    }


    public static interface Instance {

        final static String NIL = "rdf:nil";
    }

    final static String COLLECTION = "Collection";

    final static String RDF = "rdf:RDF";

    final static String RDF_PREFIX = "rdf";

    final static String XSD_PREFIX = "xsd";

    final static String XML_LITERAL = "rdf:XMLLiteral";
}
