package edu.stanford.smi.protegex.owl.swrl.model.impl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

/**
 * @author Daniel Elenius
 */
public class SWRLUtil {

    static final String XSD_STRING = XSDDatatype.XSDstring.getURI();


    /**
     * This replaces RDFSLiteral.getBrowserText() for the SWRL editor.
     * We need some special handling, e.g. adding quotes to strings.
     */
    public static String getSWRLBrowserText(RDFObject o) {
        if (o instanceof RDFResource)
            return o.getBrowserText();
        else if (o instanceof RDFSLiteral) {
            RDFSLiteral literal = (RDFSLiteral) o;
            String literalType = literal.getDatatype().getURI();
            // Add quotes to strings
            if (literalType.equals(XSD_STRING)) {
                return "\"" + literal.toString() + "\"";
            }
            return literal.toString();
        }
        return "<unknown d-object>";
    }

}
	