
package edu.stanford.smi.protegex.owl.swrl.ui.icons;

import javax.swing.ImageIcon;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class JessIcons
{

	public static ImageIcon getJessIcon()
	{
		return getImageIcon("Jess");
	}

	public static ImageIcon getReasonerIcon()
	{
		return getImageIcon("OWL2RL");
	}

	public static ImageIcon getImageIcon(String name)
	{
		return OWLIcons.getImageIcon(name, JessIcons.class);
	}
}
