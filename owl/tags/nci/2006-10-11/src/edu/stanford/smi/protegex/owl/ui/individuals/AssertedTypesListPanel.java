package edu.stanford.smi.protegex.owl.ui.individuals;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ResourceAdapter;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.Iterator;

/**
 * A panel showing the asserted types of the currently selected instance.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class AssertedTypesListPanel extends SelectableContainer {

    private Action addAction;

    private SelectableList list;

    private OWLModel owlModel;

    private RDFResource resource;

    private ResourceListener resourceListener = new ResourceAdapter() {
        public void typeAdded(RDFResource resource, RDFSClass type) {
            ComponentUtilities.addListValue(list, type);
        }


        public void typeRemoved(RDFResource resource, RDFSClass type) {
            ComponentUtilities.removeListValue(list, type);
        }
    };


    public AssertedTypesListPanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        list = ComponentFactory.createSelectableList(null);
        list.setCellRenderer(new ResourceRenderer());
        setSelectable(list);

        OWLLabeledComponent lc = new OWLLabeledComponent("Asserted Types", new JScrollPane(list));
        lc.addHeaderButton(createAddTypeAction());
        lc.addHeaderButton(createRemoveTypeAction());
        setLayout(new BorderLayout());
        add(lc);
        setPreferredSize(new Dimension(0, 100));

        list.setDragEnabled(true);
        list.setTransferHandler(new FrameTransferHandler());
    }


    private class FrameTransferHandler extends TransferHandler {

        protected Transferable createTransferable(JComponent c) {
            Collection collection = getSelection();
            return collection.isEmpty() ? null : new TransferableCollection(collection);
        }


        public boolean canImport(JComponent c, DataFlavor[] flavors) {
            return true;
        }


        public boolean importData(JComponent component, Transferable data) {
            return true;
        }


        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE) {
                Iterator i = getSelection().iterator();
                while (i.hasNext()) {
                    RDFSClass type = (RDFSClass) i.next();
                    int index = 0;
                    Log.getLogger().info("Move " + type + " to: " + index);
                    resource.moveDirectType(type, index);
                    updateModel();
                }
            }
        }


        public int getSourceActions(JComponent c) {
            return MOVE;
        }
    }


    public void setResource(RDFResource newResource) {
        if (resource != null) {
            resource.removeResourceListener(resourceListener);
        }
        resource = newResource;
        if (resource != null) {
            resource.addResourceListener(resourceListener);
        }
        updateModel();
        updateAddButton();
    }


    public void updateModel() {
        ListModel model;
        if (resource == null) {
            model = new DefaultListModel();
        }
        else {
            Collection types = resource.getRDFTypes();
            model = new SimpleListModel(types);
        }
        list.setModel(model);
    }


    public void updateAddButton() {
        addAction.setEnabled(resource != null);
    }


    private Action createAddTypeAction() {
        addAction = new AddAction("Add type...", OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS)) {
            public void onAdd() {
                Collection clses = ProtegeUI.getSelectionDialogFactory().selectClasses(AssertedTypesListPanel.this, owlModel, "Select type to add");
                Iterator i = clses.iterator();
                while (i.hasNext()) {
                    RDFSClass cls = (RDFSClass) i.next();
                    resource.addProtegeType(cls);
                }
            }
        };
        return addAction;
    }


    private Action createRemoveTypeAction() {
        return new RemoveAction("Remove selected type", list, OWLIcons.getRemoveIcon(OWLIcons.PRIMITIVE_OWL_CLASS)) {
            public void onRemove(Object o) {
                if (o instanceof RDFSClass) {
                    if (resource.getRDFTypes().size() > 1) {
                        resource.removeProtegeType((RDFSClass) o);
                    }
                    else {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                "Resources must have at least one remaining type.");
                    }
                }
            }
        };
    }
}
