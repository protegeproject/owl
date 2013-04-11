/*
 * Created on Sep 16, 2003
 */
package edu.stanford.smi.protegex.owl.ui.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;

/**
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class ModalProgressBarDialog extends JDialog implements Runnable {

    private JProgressBar progressBar;

    private LabeledComponent labeledComponent;


    public ModalProgressBarDialog(int min, int max, Frame parentFrame, String title) {
        super(parentFrame);
        // setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(title);
        progressBar = new JProgressBar(min, max);
        progressBar.setIndeterminate(false);
        init();
    }


    private void init() {

        progressBar.setValue(0);

        setModal(true);
        setSize(200, 100);
        JComponent contentPane = (JComponent) getContentPane();
        contentPane.setLayout(new BorderLayout());
        labeledComponent = new LabeledComponent("-------------------------------------------", progressBar);
        contentPane.add(BorderLayout.CENTER, labeledComponent);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ComponentUtilities.centerInMainWindow(this);
    }


    public void run() {
        setVisible(true);
    }


    public void setLabel(String text) {
        labeledComponent.setHeaderLabel(text);
    }


    public boolean setValue(final int value) {
        if (value != progressBar.getValue()) {
            progressBar.setValue(value);
            JComponent repaintComponent = (JComponent) getContentPane();
            Dimension size = repaintComponent.getSize();
            repaintComponent.paintImmediately(new Rectangle(0, 0, size.width, size.height));
            return true;
        }
        else {
            return false;
        }
    }


    public boolean setValueRelative(double value) {
        int v = (int) ((progressBar.getMaximum() - progressBar.getMinimum()) * value);
        return setValue(v);
    }
}
