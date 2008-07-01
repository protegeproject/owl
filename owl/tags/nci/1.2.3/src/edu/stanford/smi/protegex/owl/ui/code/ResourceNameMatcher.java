package edu.stanford.smi.protegex.owl.ui.code;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.List;

/**
 * An interface for objects that determine which resources could be inserted
 * when the user presses tab/CTRL+Space in a SymbolTextField.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ResourceNameMatcher {

    String getInsertString(RDFResource resource);


    List getMatchingResources(String prefix, String leftString, OWLModel owlModel);
}
