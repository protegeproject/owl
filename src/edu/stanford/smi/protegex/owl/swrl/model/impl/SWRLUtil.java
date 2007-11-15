
package edu.stanford.smi.protegex.owl.swrl.model.impl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

/**
 * @author Daniel Elenius
 * @author Martin O'Connor
 */
public class SWRLUtil 
{
  static final String XSD_STRING = XSDDatatype.XSDstring.getURI();

  /**
   * This replaces RDFSLiteral.getBrowserText() for the SWRL editor.  We need some special handling, e.g. adding quotes to strings.
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

  public static String getSWRLBrowserText(Object o, String resourceType)
  {
    String s = "";

    if (o == null) s += "<DELETED_" + resourceType + ">";
    else if (o instanceof RDFUntypedResource) { // Try to substitute the prefix.
      RDFUntypedResource resource = (RDFUntypedResource)o;
      OWLModel owlModel = resource.getOWLModel(); 
      NamespaceManager namespaceManager = owlModel.getNamespaceManager();
      String resourceName = resource.getName();
      String namespace = resourceName.substring(0, resourceName.indexOf("#") + 1);
      String itemName = resourceName.substring(resourceName.indexOf("#") + 1);
      String prefix = namespaceManager.getPrefix(namespace);
      String name = prefix != null ? prefix + ":" + itemName : resourceName;
      s += "<INVALID_" + resourceType;
      s += "[" + name + "]>";
    } else if (o instanceof RDFSLiteral) s += SWRLUtil.getSWRLBrowserText((RDFSLiteral)o); 
    else if (o instanceof SWRLAtomList) s += ((SWRLAtomList)o).getBrowserText();
    else if (o instanceof SWRLAtom) s += ((SWRLAtom)o).getBrowserText();
    else if (o instanceof SWRLVariable) s += ((SWRLVariable)o).getBrowserText();
    else if (o instanceof RDFSClass) s += ((RDFSClass)o).getName(); 
    else if (o instanceof RDFIndividual) s += ((RDFIndividual)o).getName(); 
    else if (o instanceof RDFProperty) s += ((RDFProperty)o).getName(); 
    else if (o instanceof OWLDataRange) s += ((OWLDataRange)o).getBrowserText();
    else if (o instanceof RDFResource) s += ((RDFResource)o).getBrowserText();
    else s += o.toString();
      
    s += "";

    return s;      
  } // getSWRLBrowserText

} // SWRLUtil
	
