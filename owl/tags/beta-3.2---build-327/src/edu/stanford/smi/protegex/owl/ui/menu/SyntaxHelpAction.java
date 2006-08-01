package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This action is required for showing the Protege OWL
 * restriction syntax within a JDialog.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class SyntaxHelpAction extends AbstractAction {

    public SyntaxHelpAction() {
        super("Protege OWL Syntax...", OWLIcons.getImageIcon("Help"));
    }


    public void actionPerformed(ActionEvent arg0) {
        JDialog dialog = createHelpDialog();
        dialog.setModal(false);
    }


    private JDialog createHelpDialog() {

        JFrame parent = new JFrame();
        JDialog dialog = new JDialog(parent);
        dialog.setTitle("Protege OWL Syntax");
        JLabel label = new JLabel(OWLIcons.getImageIcon("CompactSyntax.png"));
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(BorderLayout.CENTER, label);
        dialog.show();
        int w = dialog.getWidth() - contentPane.getWidth();
        int h = dialog.getHeight() - contentPane.getHeight();
        Dimension pref = contentPane.getPreferredSize();
        dialog.setSize(pref.width + w, pref.height + h);
        contentPane.doLayout();
        contentPane.repaint();
        dialog.setVisible(false);
        dialog.setVisible(true);

        return dialog;
    }
}
