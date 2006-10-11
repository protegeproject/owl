package edu.stanford.smi.protegex.owl.ui.components.singleresource;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SingleResourceComponent extends AbstractPropertyValuesComponent implements Disposable {


    private PropertyValueListener browserTextListener = new PropertyValueAdapter() {
        public void browserTextChanged(RDFResource resource) {
            list.repaint();
        }
    };

    private Action createAction = new AbstractAction("Create resource", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
        public void actionPerformed(ActionEvent e) {
            handleCreate();
        }
    };


    private JList list;

    private Action removeAction = new AbstractAction("Remove current value", OWLIcons.getRemoveIcon(OWLIcons.RDF_INDIVIDUAL)) {
        public void actionPerformed(ActionEvent e) {
            handleRemove();
        }
    };


    private RDFResource resource;


    private Action setAction = new SetResourceAction(this);


    public SingleResourceComponent(RDFProperty predicate) {
        super(predicate);

        list = ComponentFactory.createSingleItemList(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleDoubleClick();
            }
        });
        list.setCellRenderer(FrameRenderer.createInstance());
        list.addMouseListener(new PopupMenuMouseListener(list) {

            protected JPopupMenu getPopupMenu() {
                return createPopupMenu();
            }


            protected void setSelection(JComponent c, int x, int y) {
            }
        });
        OWLLabeledComponent lc = new OWLLabeledComponent(getLabel(), list);
        lc.addHeaderButton(createAction);
        lc.addHeaderButton(setAction);
        lc.addHeaderButton(removeAction);
        add(BorderLayout.CENTER, lc);
    }


    static boolean containsAnonymousClass(Collection clses) {
        for (Iterator it = clses.iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof OWLAnonymousClass) {
                return true;
            }
        }
        return false;
    }


    protected JPopupMenu createPopupMenu() {
        if (resource != null) {
            JPopupMenu menu = new JPopupMenu();
            ResourceActionManager.addResourceActions(menu, this, resource);
            if (menu.getComponentCount() > 0) {
                return menu;
            }
        }
        return null;
    }


    public void dispose() {
        removeBrowserTextListener();
    }


    public RDFResource getResource() {
        return resource;
    }


    protected void handleCreate() {
        OWLModel owlModel = getOWLModel();
        RDFSNamedClass clas = (RDFSNamedClass) getSubjectType();
        Collection clses = new ArrayList(clas.getUnionRangeClasses((RDFProperty) getPredicate()));
        if (containsAnonymousClass(clses)) {
            clses.clear();
        }
        if (clses.isEmpty()) {
            clses.add(owlModel.getOWLThingClass());
        }
        else if (OWLUI.isExternalResourcesSupported(owlModel)) {
            clses.add(owlModel.getRDFUntypedResourcesClass());
        }
        if (OWLUI.isExternalResourcesSupported(owlModel)) {
            owlModel.getRDFUntypedResourcesClass().setVisible(true);
        }
        RDFSNamedClass cls = ProtegeUI.getSelectionDialogFactory().selectClass(this, owlModel, clses,
                "Select type of new resource...");
        owlModel.getRDFUntypedResourcesClass().setVisible(false);
        if (cls != null) {
            Instance instance = ((KnowledgeBase) owlModel).createInstance(null, cls);
            if (instance instanceof RDFUntypedResource) {
                OWLUtil.assignUniqueURI((RDFUntypedResource) instance);
            }
            else if (instance instanceof RDFSClass) {
                RDFSClass newClass = (RDFSClass) instance;
                if (newClass.getSuperclassCount() == 0) {
                    newClass.addSuperclass(owlModel.getOWLThingClass());
                }
            }
            if (instance instanceof RDFResource) {
                showResource((RDFResource) instance);
            }
            getSubject().setPropertyValue(getPredicate(), instance);
        }
    }


    protected void handleDoubleClick() {
        if (resource != null) {
            getOWLModel().getProject().show(resource);
        }
    }


    protected void handleRemove() {
        getSubject().setPropertyValue(getPredicate(), null);
    }


    public boolean isCreateEnabled() {
        return !isEnumerationProperty() && isSetEnabled();
    }


    public boolean isRemoveEnabled() {
        if (!hasHasValueRestriction()) {
            return getObject() != null && hasOnlyEditableValues();
        }
        else {
            return false;
        }
    }


    public boolean isSetEnabled() {
        return !hasHasValueRestriction() && hasOnlyEditableValues();
    }


    private void removeBrowserTextListener() {
        if (resource != null) {
            resource.removePropertyValueListener(browserTextListener);
        }
    }


    private void updateActions() {
        createAction.setEnabled(isCreateEnabled());
        setAction.setEnabled(isSetEnabled());
        removeAction.setEnabled(isRemoveEnabled());
    }


    private void updateList() {
        ComponentUtilities.setListValues(list, CollectionUtilities.createCollection(resource));
    }


    public void valuesChanged() {
        removeBrowserTextListener();
        Object value = getSubject().getPropertyValue(getPredicate(), true);
        if (value == null) {
            Collection hs = getSubject().getHasValuesOnTypes(getPredicate());
            if (!hs.isEmpty()) {
                value = hs.iterator().next();
            }
        }
        if (value instanceof RDFResource) {
            resource = (RDFResource) value;
            resource.addPropertyValueListener(browserTextListener);
        }
        else {
            resource = null;
        }
        updateActions();
        updateList();
    }
}
