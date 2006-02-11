package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExtractTaxonomyAction extends ResourceAction {

    private JFileChooser fileChooser;

    public static final String GROUP = "Extract/";


    public ExtractTaxonomyAction() {
        super("Extract (sub) taxonomy to text file...", Icons.getBlankIcon(), GROUP);
    }


    public void actionPerformed(ActionEvent e) {

        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
        }
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter fw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(fw);
                performAction((RDFSNamedClass) getResource(), pw);
                fw.close();
                ProtegeUI.getModalDialogFactory().showMessageDialog(getOWLModel(),
                        "Taxonomy has been exported to " + file);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(getOWLModel(),
                        "File could not be exported:\n" + ex);
            }
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof OWLSubclassPane && resource instanceof RDFSNamedClass;
    }


    public static void performAction(RDFSNamedClass rootClass, PrintWriter pw) {
        Set reached = new HashSet();
        performAction(rootClass, reached, "", pw);
    }


    public static void performAction(Cls cls, Set reached, String baseStr, PrintWriter pw) {
        reached.add(cls);
        pw.println(baseStr + cls.getBrowserText());
        baseStr += "\t";
        for (Iterator it = cls.getDirectSubclasses().iterator(); it.hasNext();) {
            Cls subCls = (Cls) it.next();
            if (subCls instanceof RDFSNamedClass && subCls.isVisible()) {
                if (reached.contains(subCls)) {
                    pw.println(baseStr + "(" + subCls.getBrowserText() + ")...");
                }
                else {
                    performAction(subCls, reached, baseStr, pw);
                }
            }
        }
    }
}
