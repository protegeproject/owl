package edu.stanford.smi.protegex.owl.swrl.model.impl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

/**
 * @author Daniel Elenius
 * @author Martin O'Connor
 */
public class SWRLUtil
{
	static final String XSD_STRING = XSDDatatype.XSDstring.getURI();

	/**
	 * This replaces {@link RDFSLiteral#getBrowserText} for the SWRL editor. We need some special handling, e.g., adding
	 * quotes to strings.
	 */
	public static String getSWRLBrowserText(RDFObject o)
	{
		if (o instanceof RDFResource)
			return o.getBrowserText();
		else if (o instanceof RDFSLiteral) {
			RDFSLiteral literal = (RDFSLiteral)o;
			String literalType = literal.getDatatype().getURI();
			// Add quotes to strings
			if (literalType.equals(XSD_STRING)) {
				return "\"" + literal.toString() + "\"";
			}
			return literal.toString();
		}
		return "<unknown d-object>";
	}

	public static String getSWRLBrowserText(Object o, String resourceType)
	{
		StringBuilder sb = new StringBuilder();

		if (o == null)
			sb.append("<DELETED_" + resourceType + ">");
		else if (o instanceof RDFUntypedResource) { // Try to substitute the prefix.
			RDFUntypedResource resource = (RDFUntypedResource)o;
			OWLModel owlModel = resource.getOWLModel();
			NamespaceManager namespaceManager = owlModel.getNamespaceManager();
			String name = NamespaceUtil.getPrefixedName(namespaceManager, resource.getName());
			sb.append("<INVALID_" + resourceType);
			sb.append("[" + name + "]>");
		} else if (o instanceof RDFSLiteral)
			sb.append(SWRLUtil.getSWRLBrowserText((RDFSLiteral)o));
		else if (o instanceof SWRLAtomList)
			sb.append(((SWRLAtomList)o).getBrowserText());
		else if (o instanceof SWRLAtom)
			sb.append(((SWRLAtom)o).getBrowserText());
		else if (o instanceof SWRLVariable)
			sb.append(((SWRLVariable)o).getBrowserText());
		else if (o instanceof RDFSClass || o instanceof RDFIndividual || o instanceof RDFProperty) {
			String bt = ((RDFResource)o).getBrowserText(); // TODO: test should go away when quoteIfNeeded is fixed
			sb.append(bt.startsWith("'") ? bt : ParserUtils.quoteIfNeeded(bt));
		} else if (o instanceof OWLDataRange)
			sb.append(((OWLDataRange)o).getBrowserText());
		else if (o instanceof RDFResource)
			sb.append(((RDFResource)o).getBrowserText());
		else
			sb.append(o.toString());

		return sb.toString();
	}
}
