package edu.stanford.smi.protegex.owl.ui.menu;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.tidy.Checker;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.OntModelProvider;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionConstants;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * An Action that allows to validate the OWL sublanguage of the current Jena ontology.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class ValidatorAction extends AbstractOWLModelAction {
		
    public String getMenubarPath() {
        return TOOLS_MENU;
    }


    public String getName() {
        return "Determine OWL Sublanguage...";
    }


    public void run(OWLModel owlModel) {
        Set<String> imports = owlModel.getAllImports();
        Map<URI, String> map = new HashMap<URI, String>();
        for (Iterator<String> it = imports.iterator(); it.hasNext();) {
            String uriString = it.next();
            try {
                URI uri = new URI(uriString);
                Repository rep = owlModel.getRepositoryManager().getRepository(uri);
                if (rep != null) {
                    map.put(uri, rep.getOntologyLocationDescription(uri));
                }
            }
            catch (URISyntaxException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
        }

        if (!map.isEmpty()) {
            String str = "Your project uses redirected imports.  " +
                    "The species validation does not use these imports\n" +
                    "and therefore the following result may be wrong.  The following\n" +
                    "URI aliases are used:\n";
            for (Iterator<URI> it = map.keySet().iterator(); it.hasNext();) {
                URI key = it.next();
                String alias = map.get(key);
                str += "- " + key + "\n   has alias " + alias + "\n";
            }
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, str, "Warning");
        }

        if (!OWLUI.isConfirmationNeeded(owlModel) ||
                OWLUI.isConfirmed(owlModel, owlModel.getRDFResourceCount() > OWLUI.getConfirmationThreshold(owlModel))) {
            performAction(owlModel);
        }
    }


    private void performAction(OWLModel owlModel) {
        OntModelProvider ontModelProvider = (OntModelProvider) owlModel;
        String msg = Jena.getOWLSpeciesString(ontModelProvider.getOWLSpecies());
        ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                "The OWL sublanguage of this ontology is OWL " + msg,
                "OWL Sublanguage");
    }


    public static String getSubLanguage(OntModel ontModel) {
        Checker checker = new Checker(false);
        checker.addGraphAndImports(ontModel.getGraph());
        String sublanguage = checker.getSubLanguage();
        return sublanguage;
    }
}
