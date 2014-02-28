
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
	private final String pluginName;
	private final OWLModel owlModel;
	private final SWRLTab swrlTab;

	public ViewPluginAction(String pluginName, String tip, Icon icon, SWRLTab swrlTab, OWLModel owlModel)
	{
		super(tip, icon);

		this.pluginName = pluginName;
		this.owlModel = owlModel;
		this.swrlTab = swrlTab;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (P3SWRLTabPluginManager.isVisible(this.pluginName))
			P3SWRLTabPluginManager.hidePlugin(this.pluginName);
		else
			P3SWRLTabPluginManager.showPlugin(this.pluginName, this.swrlTab, this.owlModel);
	}
}
