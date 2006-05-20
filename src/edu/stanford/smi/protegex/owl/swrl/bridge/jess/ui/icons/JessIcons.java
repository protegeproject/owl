
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.icons;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;

public class JessIcons {

    public static ImageIcon getJessIcon() {
      return getImageIcon("Jess");
    } // getJessIcon

    public static ImageIcon getImageIcon(String name) {
      return OWLIcons.getImageIcon(name, JessIcons.class);
    }
} // JessIcons
