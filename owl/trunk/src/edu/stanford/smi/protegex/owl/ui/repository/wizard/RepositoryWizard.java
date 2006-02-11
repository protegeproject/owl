package edu.stanford.smi.protegex.owl.ui.repository.wizard;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.HTTPRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.LocalFileRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.LocalFolderRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.RelativeFolderRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizard;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryWizard extends OWLWizard {

    private ArrayList plugins;

    private RepositoryCreatorWizardPlugin selectedPlugin;

	private PluginPanelHolder pluginPanelHolder;

    public RepositoryWizard(JComponent component, OWLModel owlModel) {
        super(component, "Create Ontology Repository");
        plugins = new ArrayList();
        plugins.add(new LocalFolderRepositoryCreatorWizardPlugin());
        plugins.add(new RelativeFolderRepositoryCreatorWizardPlugin());
        plugins.add(new LocalFileRepositoryCreatorWizardPlugin());
        plugins.add(new HTTPRepositoryCreatorWizardPlugin());
        Collection pluginClses = PluginUtilities.getClassesWithAttribute(RepositoryCreatorWizardPlugin.PLUGIN_TYPE,
                "True");
        for (Iterator it = pluginClses.iterator(); it.hasNext();) {
            Class cls = (Class) it.next();
            try {
                plugins.add(cls.newInstance());
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        addPage(new SelectRepositoryTypeWizardPage(this, owlModel));
	    addPage(pluginPanelHolder = new PluginPanelHolder(this, owlModel));
    }

    public Repository getRepository() {
        return pluginPanelHolder.getRepository();
    }

    public ArrayList getPlugins() {
        return plugins;
    }

    public void setSelectedPlugin(RepositoryCreatorWizardPlugin selectedPlugin) {
        this.selectedPlugin = selectedPlugin;
    }

	public RepositoryCreatorWizardPlugin getSelectedPlugin() {
		return selectedPlugin;
	}

	public static void main(String [] args) {
		OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
		RepositoryWizard wiz = new RepositoryWizard(null, owlModel);
		if(wiz.execute() == Wizard.RESULT_FINISH) {
			Repository rep = wiz.getRepository();
		}
	}
}

