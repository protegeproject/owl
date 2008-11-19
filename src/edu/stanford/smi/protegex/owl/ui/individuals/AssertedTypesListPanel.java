package edu.stanford.smi.protegex.owl.ui.individuals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.TransferHandler;

import edu.stanford.smi.protege.util.AddAction;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.RemoveAction;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.util.SimpleListModel;
import edu.stanford.smi.protege.util.TransferableCollection;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ResourceAdapter;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

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
        @Override
		public void typeAdded(RDFResource resource, RDFSClass type) {
            ComponentUtilities.addListValue(list, type);
        }


        @Override
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
        updateAddButton();

        list.setDragEnabled(true);
        list.setTransferHandler(new FrameTransferHandler());
    }


    private class FrameTransferHandler extends TransferHandler {

        @Override
		protected Transferable createTransferable(JComponent c) {
            Collection collection = getSelection();
            return collection.isEmpty() ? null : new TransferableCollection(collection);
        }


        @Override
		public boolean canImport(JComponent c, DataFlavor[] flavors) {
            return true;
        }


        @Override
		public boolean importData(JComponent component, Transferable data) {
            return true;
        }


        @Override
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


        @Override
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
            @Override
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
            @Override
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
