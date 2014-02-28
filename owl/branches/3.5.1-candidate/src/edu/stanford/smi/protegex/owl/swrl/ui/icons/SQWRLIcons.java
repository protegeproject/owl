
package edu.stanford.smi.protegex.owl.swrl.ui.icons;

import javax.swing.ImageIcon;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class SQWRLIcons
{
	public static ImageIcon getQueryIcon()
	{
		return getImageIcon("SQWRL");
	}

	public static ImageIcon getImageIcon(String name)
	{
		return OWLIcons.getImageIcon(name, SQWRLIcons.class);
	}
}