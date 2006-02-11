package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A ResourceAction for deleted the selected named classes inside a ClassTreePanel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeleteClassAction extends ResourceAction {

    public final static String TEXT = "Delete class";


    public DeleteClassAction() {
        super(TEXT, OWLIcons.getDeleteClsIcon());
    }


    public void actionPerformed(ActionEvent e) {
        ClassTreePanel classTreePanel = (ClassTreePanel) getComponent();
        RDFSNamedClass cls = (RDFSNamedClass) getResource();
        performAction(cls, classTreePanel);
    }


    private static boolean confirmDelete(Component parent) {
        String text = LocalizedText.getText(ResourceKey.DIALOG_CONFIRM_DELETE_TEXT);
        return ProtegeUI.getModalDialogFactory().showConfirmDialog(parent, text, "Confirm Delete");
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof ClassTreePanel &&
                resource instanceof RDFSNamedClass &&
                resource.isEditable();
    }


    public static void performAction(RDFSNamedClass cls, ClassTreePanel classTreePanel) {
        int instanceCount = cls.getInstanceCount(true);
        if (instanceCount > 0) {
            String text = LocalizedText.getText(ResourceKey.DELETE_CLASS_FAILED_DIALOG_TEXT);
            ProtegeUI.getModalDialogFactory().showMessageDialog(cls.getOWLModel(), text);
        }
        else if (confirmDelete((Component)classTreePanel)) {
            JTree tree = classTreePanel.getTree();
            ComponentUtilities.removeSelection(tree);
            cls.delete();
        }
    }
}
