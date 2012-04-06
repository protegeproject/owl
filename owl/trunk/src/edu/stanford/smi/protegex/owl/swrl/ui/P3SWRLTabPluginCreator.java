package edu.stanford.smi.protegex.owl.swrl.ui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.Icon;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.P3SWRLTabPluginManager;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

/**
 * This interface must thus be implemented by a SWRLTab plugin to create a Java Swing {@link Container} that represents the plugin's GUI. Thus GUI will be
 * activated in the SWRLTab the provide a means of interacting with the plugin.
 * <p>
 * Each plugin should register itself with the bridge using the
 * {@link P3SWRLTabPluginManager#registerPlugin(String, String, Icon, Icon, Icon, P3SWRLTabPluginCreator)} method of the {@link P3SWRLTabPluginManager} class.
 * In addition to a plugin display name, a tool tip string, and a set of icons, this method is expecting an instance of a class that implements this interface.
 * The plugin manager uses this implementation to get the GUI for the plugin, which will be displayed in the {@link SWRLTab} when the plugin is activated.
 * <p>
 * The {@link #createSWRLPluginGUI(OWLModel, String, String, Icon, Icon)} method is called once when the plugin is registered and is expecting a Java Swing
 * {@link Component} that is a subclass of a Java AWT {@link Container} class. The {@link #getSWRLPluginGUI()} method may be called repeatedly after
 * registration and should return the {@link Container} instance created on initialization.
 * <p>
 * See <a href= "http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ#nid6QJ" >here</a> for a discussion on using this interface.
 */
public interface P3SWRLTabPluginCreator {
	Container createSWRLPluginGUI(OWLModel owlModel, String pluginName, String ruleEngineName, Icon ruleEngineIcon, Icon reasonerIcon);

	Container getSWRLPluginGUI();
}
