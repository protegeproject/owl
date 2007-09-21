package edu.stanford.smi.protegex.owl.ui.code;

import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * An interface for objects that determine which resources could be inserted
 * when the user presses tab/CTRL+Space in a SymbolTextField.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ResourceNameMatcher {

    String getInsertString(RDFResource resource);


    List<RDFResource> getMatchingResources(String prefix, String leftString, OWLModel owlModel);
    
    public boolean isIdChar(char ch);
}
