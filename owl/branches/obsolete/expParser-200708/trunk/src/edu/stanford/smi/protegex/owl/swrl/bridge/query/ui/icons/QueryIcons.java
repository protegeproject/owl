
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui.icons;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;

public class QueryIcons {

    public static ImageIcon getQueryIcon() {
      return getImageIcon("Query");
    } // getQueryIcon

    public static ImageIcon getImageIcon(String name) {
      return OWLIcons.getImageIcon(name, QueryIcons.class);
    }
} // QueryIcons
