
package edu.stanford.smi.protegex.owl.swrl.ui.icons;

import javax.swing.ImageIcon;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class DroolsIcons {

    public static ImageIcon getDroolsIcon() {
      return getImageIcon("Drools");
    } 

  	public static ImageIcon getReasonerIcon()
  	{
  		return getImageIcon("OWL2RL");
  	}

    public static ImageIcon getImageIcon(String name) {
      return OWLIcons.getImageIcon(name, DroolsIcons.class);
    }
} 
