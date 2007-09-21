
package edu.stanford.smi.protegex.owl.swrl.ui.code;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.ui.code.OWLResourceNameMatcher;
import edu.stanford.smi.protegex.owl.ui.code.ResourceNameMatcher;
import edu.stanford.smi.protegex.owl.ui.code.SymbolTextField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLResourceNameMatcher implements ResourceNameMatcher {

    public String getInsertString(RDFResource resource) {
        if (resource instanceof SWRLVariable) {
            return resource.getName();
        }
        else {
            return resource.getBrowserText();
        }
    }


    public List getMatchingResources(String prefix, String leftString, OWLModel owlModel) {
        List resources = new ArrayList();
        if (leftString.endsWith("?")) {
            for (Iterator it = owlModel.getOWLNamedClass(SWRLNames.Cls.VARIABLE).getInstances(true).iterator(); it.hasNext();) {
                SWRLVariable var = (SWRLVariable) it.next();
                if (var.getName().startsWith(prefix)) {
                    resources.add(var);
                }
            }
        }
        else if (prefix.length() > 0) {
            OWLResourceNameMatcher.getMatchingOWLNamedClasses(prefix, resources, owlModel);
            OWLResourceNameMatcher.getMatchingOWLIndividuals(prefix, resources, owlModel);
            OWLResourceNameMatcher.getMatchingOWLProperties(prefix, resources, owlModel);
        }
        return resources;
    }
    
    public boolean isIdChar(char ch) {
        return SymbolTextField.isIdChar(ch);
    }


}
