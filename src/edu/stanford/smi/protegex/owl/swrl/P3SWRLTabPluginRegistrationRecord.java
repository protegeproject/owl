package edu.stanford.smi.protegex.owl.swrl;

import javax.swing.Icon;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.ui.P3SWRLTabPlugin;

public class P3SWRLTabPluginRegistrationRecord
{
	private final String pluginName;
	private final String ruleEngineName;
	private final String toolTip;
	private final P3SWRLTabPlugin swrlTabPlugin;
	private final Icon pluginIcon;
	private final Icon ruleEngineIcon;
	private final Icon reasonerIcon;
	private OWLModel owlModel;

	public P3SWRLTabPluginRegistrationRecord(String pluginName, String ruleEngineName, String toolTip, Icon pluginIcon, Icon ruleEngineIcon, Icon reasonerIcon,
			P3SWRLTabPlugin swrlTabPlugin)
	{
		this.pluginName = pluginName;
		this.ruleEngineName = ruleEngineName;
		this.toolTip = toolTip;
		this.swrlTabPlugin = swrlTabPlugin;
		this.pluginIcon = pluginIcon;
		this.ruleEngineIcon = ruleEngineIcon;
		this.reasonerIcon = reasonerIcon;
		this.owlModel = null; // An OWL model is supplied when a GUI associated with the plugin is activated.
	}

	public void setOWLModel(OWLModel owlModel)
	{
		this.owlModel = owlModel;
	}

	public String getPluginName()
	{
		return this.pluginName;
	}

	public String getRuleEngineName()
	{
		return this.ruleEngineName;
	}

	public String getToolTip()
	{
		return this.toolTip;
	}

	public P3SWRLTabPlugin getSWRLTabPlugin()
	{
		return this.swrlTabPlugin;
	}

	public Icon getPluginIcon()
	{
		return this.pluginIcon;
	}

	public Icon getRuleEngineIcon()
	{
		return this.ruleEngineIcon;
	}

	public Icon getReasonerIcon()
	{
		return this.reasonerIcon;
	}

	public OWLModel getOWLModel()
	{
		return this.owlModel;
	}

	public boolean hasOWLModel()
	{
		return this.owlModel != null;
	}
}
