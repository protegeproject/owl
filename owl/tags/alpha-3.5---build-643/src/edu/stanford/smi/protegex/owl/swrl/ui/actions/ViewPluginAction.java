
package edu.stanford.smi.protegex.owl.swrl.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.P3SWRLTabPluginManager;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

public class ViewPluginAction extends AbstractAction
{
	private static final long serialVersionUID = 2115603073256549503L;
	private String pluginName;
	private OWLModel owlModel;
	private SWRLTab swrlTab;

	public ViewPluginAction(String pluginName, String tip, Icon icon, SWRLTab swrlTab, OWLModel owlModel)
	{
		super(tip, icon);

		this.pluginName = pluginName;
		this.owlModel = owlModel;
		this.swrlTab = swrlTab;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (P3SWRLTabPluginManager.isVisible(pluginName))
			P3SWRLTabPluginManager.hidePlugin(pluginName);
		else
			P3SWRLTabPluginManager.showPlugin(pluginName, swrlTab, owlModel);
	}
}
