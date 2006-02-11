package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protegex.owl.jena.creator.ProgressDisplay;

import javax.swing.*;

/**
 * An object managing a ModalProgressBarDialog serving as ProgressDisplay.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ModalProgressBarManager implements ProgressDisplay {

    private ModalProgressBarDialog dialog;


    public ModalProgressBarManager(String title) {
        JFrame frame = (JFrame) Application.getMainWindow();
        dialog = new ModalProgressBarDialog(0, 100, frame, title);
        Thread thread = new Thread(dialog, "ModalProgressBar");
        thread.start();
        try {
            Thread.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void setProgressText(String str) {
        dialog.setLabel(str);
        try {
            Thread.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }


    public void setProgressValue(double value) {
        if (dialog.setValueRelative(value)) {
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
    }


    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.show();
            }
        });
    }


    public void stop() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ComponentUtilities.dispose(dialog);
            }
        });
    }
}
