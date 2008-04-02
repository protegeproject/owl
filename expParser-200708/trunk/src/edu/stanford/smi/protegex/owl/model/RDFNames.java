package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protege.model.FrameID;


/**
 * Defines the names of the RDF(S) related parts of the OWL system ontology.
 * This corresponds to the Model interface in general Protege.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFNames {
	
	//added by TT for use of the fully qualified name for the owl metamodel
	
	static String RDF_NAMESPACE= "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static interface Cls {

        final static String ALT = RDF_NAMESPACE + "Alt";

        final static String BAG = RDF_NAMESPACE + "Bag";

        final static String DESCRIPTION = RDF_NAMESPACE + "Description";

        final static String EXTERNAL_RESOURCE = ProtegeNames.PREFIX +  "ExternalResource";

        final static String LIST = RDF_NAMESPACE + "List";

        final static String PROPERTY = RDF_NAMESPACE + "Property";

        final static String SEQ = RDF_NAMESPACE + "Seq";

        final static String STATEMENT = RDF_NAMESPACE + "Statement";
    }


    public interface ClsID {

        FrameID PROPERTY = FrameID.createSystem(9007);
    }

    public static interface Slot {

        final static String ABOUT = RDF_NAMESPACE + "about";

        final static String DATATYPE = RDF_NAMESPACE + "datatype";

        final static String FIRST = RDF_NAMESPACE + "first";

        final static String ID = RDF_NAMESPACE + "ID";

        final static String OBJECT = RDF_NAMESPACE + "object";

        final static String PARSE_TYPE = RDF_NAMESPACE + "parseType";

        final static String PREDICATE = RDF_NAMESPACE + "predicate";

        final static String RESOURCE = RDF_NAMESPACE + "resource";

        final static String REST = RDF_NAMESPACE + "rest";

        final static String SUBJECT = RDF_NAMESPACE + "subject";

        final static String TYPE = RDF_NAMESPACE + "type";

        final static String VALUE = RDF_NAMESPACE + "value";
    }


    public static interface Instance {

        final static String NIL = RDF_NAMESPACE + "nil";
    }

    final static String COLLECTION = "Collection";

    final static String RDF = RDF_NAMESPACE + "RDF";

    final static String RDF_PREFIX = "rdf";

    final static String XSD_PREFIX = "xsd";

    final static String XML_LITERAL = RDF_NAMESPACE + "XMLLiteral";
}
