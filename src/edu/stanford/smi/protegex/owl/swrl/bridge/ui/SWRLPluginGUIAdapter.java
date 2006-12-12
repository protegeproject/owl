
package edu.stanford.smi.protegex.owl.swrl.bridge.ui;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import javax.swing.*;
import java.awt.*;

/*
** This interface must be implemented by a plugin to provide a mechanism to create screen real estate for its GUI. A plugin registers itself
** with the bridge using the registerPlugin method of the BridgePluginManager class. In addition to a plugin display name, a tool tip
** string, and an icon, this method is expecting an instance of a class that implements this interface. The plugin manager uses this
** implementation to get the GUI for the plugin, which will be displayed in the SWRLTab when the plugin is activated.
*/
public interface SWRLPluginGUIAdapter
{
  Container createPluginGUI(OWLModel owlModel);
  Container getPluginGUI();
} // SWRLPluginGUIAdapter
