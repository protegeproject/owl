package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A SelectableContainer showing the superproperties of given RDFProperty.
 * This component is used in conjunction with an OWLSubpropertyPane.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLSuperpropertiesPanel extends SelectableContainer {

    private AbstractAction addAction;

    private SelectableList list;

    private OWLModel owlModel;

    private RDFProperty property;

    private PropertyListener propertyListener = new PropertyAdapter() {

        public void superpropertyAdded(RDFProperty property, RDFProperty superproperty) {
            ComponentUtilities.addListValue(list, superproperty);
        }


        public void superpropertyRemoved(RDFProperty property, RDFProperty superproperty) {
            boolean wasEnabled = setNotificationsEnabled(false);
            ComponentUtilities.removeListValue(list, superproperty);
            setNotificationsEnabled(wasEnabled);
        }
    };

    private AbstractAction removeAction;

    private OWLSubpropertyPane subpropertyPane;


    /**
     * @deprecated
     */
    public OWLSuperpropertiesPanel(OWLSubpropertyPane subpropertyPane, Project project) {
        this(subpropertyPane, (OWLModel) project.getKnowledgeBase());
    }


    public OWLSuperpropertiesPanel(OWLSubpropertyPane subpropertyPane, OWLModel owlModel) {

        this.owlModel = owlModel;
        createComponents();
        layoutComponents();
        setSelectable(list);

        setPreferredSize(new Dimension(0, 100));

        this.subpropertyPane = subpropertyPane;
    }


    private void addProperties() {
        Collection allowedProperties = new ArrayList(owlModel.getRDFProperties());
        allowedProperties.remove(property);
        allowedProperties.removeAll(property.getSubproperties(true));
        allowedProperties.removeAll(property.getSuperproperties(true));
        Iterator it = selectProperties(allowedProperties).iterator();
        while (it.hasNext()) {
            RDFProperty superproperty = (RDFProperty) it.next();
            property.addSuperproperty(superproperty);
        }
        updateModel();
    }


    private void createComponents() {
        list = ComponentFactory.createSelectableList(null);
        list.setCellRenderer(new ResourceRenderer());
        addAction = createAddAction();
        removeAction = createRemoveAction();
    }


    private AbstractAction createAddAction() {
        return new AddAction("Add super properties...", OWLIcons.getAddIcon(OWLIcons.RDF_PROPERTY)) {
            public void onAdd() {
                if (property != null) {
                    addProperties();
                }
            }
        };
    }


    private AbstractAction createRemoveAction() {
        return new RemoveAction("Remove super property", list, OWLIcons.getRemoveIcon(OWLIcons.RDF_PROPERTY)) {
            public void onRemove(Collection superproperties) {
                removeProperties(superproperties);
            }
        };
    }

	public void setAddActionIconBase(String baseName) {
		addAction.putValue(Action.SMALL_ICON, OWLIcons.getAddIcon(baseName));
	}

	public void setRemoveActionIconBase(String baseName) {
		removeAction.putValue(Action.SMALL_ICON, OWLIcons.getRemoveIcon(baseName));
	}

    private void layoutComponents() {
        JScrollPane pane = ComponentFactory.createScrollPane(list);
        OWLLabeledComponent c = new OWLLabeledComponent("Super Properties", pane);
        c.addHeaderButton(addAction);
        c.addHeaderButton(removeAction);
        setLayout(new BorderLayout());
        add(c, BorderLayout.CENTER);
    }


    private Collection selectProperties(Collection allowedProperties) {
        for (Iterator it = allowedProperties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (subpropertyPane.contains(property)) {
                if (property.isAnnotationProperty()) {
                    it.remove();
                }
            }
            else {
                it.remove();
            }
        }
        return ProtegeUI.getSelectionDialogFactory().selectResourcesFromCollection(this, owlModel, allowedProperties, "Select Properties");
    }


    private void removeProperties(Collection superproperties) {
        //try {
        owlModel.beginTransaction("Remove superproperties from " + property);
        Iterator i = superproperties.iterator();
        while (i.hasNext()) {
            RDFProperty superslot = (RDFProperty) i.next();
            property.removeSuperproperty(superslot);
        }
        updateModel();
        //}
        //finally {
        //    owlModel.endTransaction();
        //}
    }


    public void setProperty(RDFProperty property, RDFProperty parent) {
        if (this.property != null) {
            this.property.removePropertyListener(propertyListener);
        }
        this.property = property;
        if (this.property != null) {
            this.property.addPropertyListener(propertyListener);
        }
        updateModel();
        addAction.setEnabled(property != null && property.isEditable());
        list.setSelectedValue(parent, true);
    }


    public void setDisplayParent(RDFProperty parent) {
        list.setSelectedValue(parent, true);
    }


    private void updateModel() {
        Collection properties = (property == null) ? Collections.EMPTY_LIST : property.getSuperproperties(false);
        ListModel model = new SimpleListModel(properties);
        list.setModel(model);
        repaint();
    }
}
