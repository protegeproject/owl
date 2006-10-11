package edu.stanford.smi.protegex.owl.ui.components.multiresource;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.PopupMenuMouseListener;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.TripleSelectable;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class MultiResourceList extends SelectableList implements TripleSelectable {

    private MultiResourceListModel listModel;

    private boolean symmetric;


    public MultiResourceList(RDFProperty predicate, boolean symmetric) {
        this.symmetric = symmetric;
        listModel = new MultiResourceListModel(predicate);
        setModel(listModel);
        setCellRenderer(createRenderer());
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick();
                }
            }
        });
        addMouseListener(new PopupMenuMouseListener(this) {

            protected JPopupMenu getPopupMenu() {
                return createPopupMenu();
            }


            protected void setSelection(JComponent c, int x, int y) {
                for (int i = 0; i < listModel.getSize(); i++) {
                    if (getCellBounds(i, i).contains(x, y)) {
                        setSelectedIndex(i);
                        return;
                    }
                }
                clearSelection();
            }
        });
    }


    protected ListCellRenderer createRenderer() {
        return new ResourceRenderer() {
            public void load(Object value) {
                int row = listModel.getRowOf(value);
                if (row >= 0 && !listModel.isEditable(row)) {
                    setGrayedText(true);
                }
                super.load(value);
            }
        };
    }


    protected JPopupMenu createPopupMenu() {
        int[] sels = getSelectedIndices();
        if (sels.length == 1) {
            Object value = listModel.getElementAt(sels[0]);
            if (value instanceof RDFResource) {
                JPopupMenu menu = new JPopupMenu();
                RDFResource resource = (RDFResource) value;
                ResourceActionManager.addResourceActions(menu, this, resource);
                if (menu.getComponentCount() > 0) {
                    return menu;
                }
            }
        }
        return null;
    }


    public MultiResourceListModel getListModel() {
        return listModel;
    }


    public List getPrototypeTriples() {
        RDFResource subject = listModel.getSubject();
        RDFProperty predicate = listModel.getPredicate();
        return Collections.singletonList(new DefaultTriple(subject, predicate, null));
    }


    public List getSelectedTriples() {
        RDFResource subject = listModel.getSubject();
        RDFProperty predicate = listModel.getPredicate();
        List results = new ArrayList();
        Iterator it = getSelection().iterator();
        while (it.hasNext()) {
            Object object = it.next();
            results.add(new DefaultTriple(subject, predicate, object));
        }
        return results;
    }


    protected void handleCreate() {
        RDFResource subject = listModel.getSubject();
        RDFProperty predicate = listModel.getPredicate();
        OWLModel owlModel = predicate.getOWLModel();
        RDFSNamedClass type = (RDFSNamedClass) subject.getRDFType();
        Collection clses = new ArrayList(type.getUnionRangeClasses(predicate));
        if (OWLUtil.containsAnonymousClass(clses) || clses.isEmpty()) {
            clses.clear();
            clses.add(owlModel.getOWLThingClass());
        }
        else if (OWLUI.isExternalResourcesSupported(owlModel)) {
            clses.add(owlModel.getRDFUntypedResourcesClass());
        }
        if (OWLUI.isExternalResourcesSupported(owlModel)) {
            owlModel.getRDFUntypedResourcesClass().setVisible(true);
        }
        RDFSNamedClass cls = ProtegeUI.getSelectionDialogFactory().selectClass(this, owlModel, clses, "Select type of new resource");
        owlModel.getRDFUntypedResourcesClass().setVisible(false);
        if (cls != null) {
            RDFResource instance = cls.createInstance(null);
            if (instance instanceof RDFUntypedResource) {
                OWLUtil.assignUniqueURI((RDFUntypedResource) instance);
            }
            else if (instance instanceof RDFSClass) {
                RDFSClass newcls = (RDFSClass) instance;
                if (newcls.getSuperclassCount() == 0) {
                    newcls.addSuperclass(owlModel.getOWLThingClass());
                }
            }
            subject.addPropertyValue(predicate, instance);
            if (symmetric) {
                instance.addPropertyValue(predicate, subject);
            }
            owlModel.getProject().show(instance);
        }
    }


    private void handleDoubleClick() {
        Project project = listModel.getPredicate().getOWLModel().getProject();
        int[] sels = getSelectedIndices();
        for (int i = 0; i < sels.length; i++) {
            int sel = sels[i];
            Object value = listModel.getElementAt(sel);
            if (value instanceof RDFResource) {
                project.show((RDFResource) value);
            }
        }
    }


    protected void handleRemove() {
        int[] sels = getSelectedIndices();
        Set valuesToRemove = new HashSet();
        for (int i = 0; i < sels.length; i++) {
            int sel = sels[i];
            valuesToRemove.add(listModel.getElementAt(sel));
        }
        RDFProperty predicate = listModel.getPredicate();
        RDFResource subject = listModel.getSubject();
        for (Iterator it = valuesToRemove.iterator(); it.hasNext();) {
            Object value = it.next();
            subject.removePropertyValue(predicate, value);
            if (symmetric && value instanceof RDFResource) {
                RDFResource other = (RDFResource) value;
                if (other.getPropertyValues(predicate).contains(subject)) {
                    other.removePropertyValue(predicate, subject);
                }
            }
        }
    }


    public boolean isRemoveEnabled() {
        int[] sels = getSelectedIndices();
        if (sels.length > 0) {
            for (int i = 0; i < sels.length; i++) {
                int sel = sels[i];
                if (!isRemoveEnabled(sel)) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isRemoveEnabled(int row) {
        return listModel.isEditable(row);
    }


    public void setSelectedTriples(Collection triples) {
        getSelectionModel().clearSelection();
        Iterator it = triples.iterator();
        while (it.hasNext()) {
            Triple triple = (Triple) it.next();
            int row = listModel.getRowOf(triple.getObject());
            if (row >= 0) {
                getSelectionModel().addSelectionInterval(row, row);
            }
        }
    }
}
