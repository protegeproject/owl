package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A class that manages the (in) visibility of RDFSDatatypes, depending
 * on their usage.  Default datatypes (xsd:int etc) are always visible
 * unless the user has explicitly made them invisible, and other datatypes
 * are visible if they are used (have References to them).
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class XSDVisibility {

    public static Set getDefaultDatatypes(OWLModel owlModel) {
        Set set = new HashSet();
        set.add(owlModel.getXSDboolean());
        set.add(owlModel.getXSDfloat());
        set.add(owlModel.getXSDint());
        set.add(owlModel.getXSDstring());
        set.add(owlModel.getXSDdate());
        set.add(owlModel.getXSDdateTime());
        set.add(owlModel.getXSDtime());
        return set;
    }


    public static void updateVisibility(OWLModel owlModel) {
        Set defaultDatatypes = getDefaultDatatypes(owlModel);
        Iterator types = owlModel.getRDFSDatatypes().iterator();
        while (types.hasNext()) {
            RDFSDatatype datatype = (RDFSDatatype) types.next();
            if (datatype.isSystem() && !defaultDatatypes.contains(datatype)) {
                Collection refs = ((KnowledgeBase) owlModel).getReferences(datatype, 10);
                boolean visible = refs.size() > 1;
                datatype.setVisible(visible);
            }
        }
    }
}
