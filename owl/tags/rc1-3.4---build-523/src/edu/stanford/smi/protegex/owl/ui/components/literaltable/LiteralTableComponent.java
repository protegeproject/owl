package edu.stanford.smi.protegex.owl.ui.components.literaltable;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.components.AddResourceAction;
import edu.stanford.smi.protegex.owl.ui.components.AddablePropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditor;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditorManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LiteralTableComponent extends AddablePropertyValuesComponent {


    private Action addAction = new AbstractAction("Add new value", OWLIcons.getAddIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleAddAction();
        }
    };


    private Action deleteAction = new AbstractAction("Delete selected value", OWLIcons.getDeleteIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleDeleteAction();
        }
    };

    private LiteralTable table;


    private Action viewAction = new AbstractAction("View/edit selected value...", OWLIcons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleViewAction();
        }
    };


    public LiteralTableComponent(RDFProperty predicate) {
    	this(predicate, null);
    }

    public LiteralTableComponent(RDFProperty predicate, String label) {
        super(predicate, label, false);
    }
    
    public LiteralTableComponent(RDFProperty predicate, String label, boolean isReadOnly) {
        super(predicate, label, isReadOnly);
        table = new LiteralTable(predicate);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActionsState();
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.white);
        OWLLabeledComponent lc = new OWLLabeledComponent((label == null ? getLabel():label), scrollPane);
        lc.addHeaderButton(viewAction);
        lc.addHeaderButton(addAction);
        if (!(predicate instanceof OWLProperty) && predicate.getRange() == null) {
            lc.addHeaderButton(new AddResourceAction(this, false));
        }
        lc.addHeaderButton(deleteAction);
        add(lc);
        updateActionsState();
    }


    private Object createDefaultValue() {
        RDFProperty property = getPredicate();
        Iterator it = PropertyValueEditorManager.listEditors();
        while (it.hasNext()) {
            PropertyValueEditor plugin = (PropertyValueEditor) it.next();
            if (plugin.canEdit(getSubject(), property, null)) {
                Object defaultValue = plugin.createDefaultValue(getSubject(), property);
                if (defaultValue != null) {
                    return defaultValue;
                }
            }
        }
        RDFResource range = property.getRange();
        if (range instanceof RDFSDatatype) {
            return ((RDFSDatatype) range).getDefaultValue();
        }
        else if (range instanceof OWLDataRange) {
            OWLDataRange dataRange = (OWLDataRange) range;
            RDFList oneOf = dataRange.getOneOf();
            Object defaultValue = null;
            if (oneOf != null) {
                RDFResource subj = getSubject();
                RDFProperty pred = getPredicate();
                Iterator values = oneOf.getValues().iterator();
                while (values.hasNext() && defaultValue == null) {
                    Object value = values.next();
                    if (!subj.hasPropertyValue(pred, value)){
                        defaultValue = value;
                    }
                }
                if (defaultValue == null){
                    ProtegeUI.getModalDialogFactory().showMessageDialog(getOWLModel(),
                                                                        "You have already assigned all allowed values");
                }
            }
            return defaultValue;
        }
        else if (property instanceof OWLObjectProperty) {
            return null;
        }
        else {
            return "";
        }
    }


    private void handleAddAction() {
        Object defaultValue = createDefaultValue();
        if (defaultValue != null && !getObjects().contains(defaultValue)) {
            getSubject().addPropertyValue(getPredicate(), defaultValue);
            table.setSelectedRow(defaultValue);
            
            if (defaultValue instanceof DefaultRDFSLiteral && ((DefaultRDFSLiteral)defaultValue).getDatatype().equals(getOWLModel().getXSDstring())) {
            	table.editCell(defaultValue);
            	return;
            }
                        
            if (!defaultValue.equals("")) {
                final Iterator it = PropertyValueEditorManager.listEditors();
                while (it.hasNext()) {
                    PropertyValueEditor editor = (PropertyValueEditor) it.next();
                    if (editor.canEdit(getSubject(), getPredicate(), null)) {
                        Object newValue = editor.editValue(this, getSubject(), getPredicate(), defaultValue);
                        if (newValue != null) {
                            getSubject().removePropertyValue(getPredicate(), defaultValue);
                            getSubject().addPropertyValue(getPredicate(), newValue);
                            table.setSelectedRow(newValue);
                        }
                        return;
                    }
                }
            }
            table.editCell(defaultValue);
        }
    }


    private void handleDeleteAction() {
        Collection values = getOWLModel().asRDFSLiterals(getObjects());
        int[] sels = table.getSelectedRows();
        for (int i = 0; i < sels.length; i++) {
            int row = sels[i];
            if (row >= 0 && row < table.getTableModel().getRowCount()) {
                Object value = table.getTableModel().getObject(row);
                values.remove(value);
            }
        }
        getSubject().setPropertyValues(getPredicate(), values);
    }


    protected void handleDoubleClick() {
        int[] sel = table.getSelectedRows();
        if (sel.length == 1 && viewAction.isEnabled()) {
            if (!table.getTableModel().isCellEditable(sel[0], LiteralTableModel.COL_VALUE)) {
                handleViewAction();
            }
        }
    }


    protected void handleViewAction() {
        int[] sel = table.getSelectedRows();
        if (sel.length > 0) {
            table.stopEditing();
            Object oldValue = table.getTableModel().getObject(sel[0]);
            if (oldValue instanceof RDFResource) {
                RDFResource resource = (RDFResource) oldValue;
                resource.getProject().show(resource);
            }
            else {
                PropertyValueEditor editor = getEditor(oldValue);
                if (editor != null) {
                    Object newValue = editor.editValue(null, getSubject(), getPredicate(), oldValue);
                    if (newValue != null) {
                        if (oldValue instanceof RDFSLiteral) {
                            Object neo = ((RDFSLiteral) oldValue).getPlainValue();
                            if (neo != null) {
                                oldValue = neo;
                            }
                        }
                        getSubject().removePropertyValue(getPredicate(), oldValue);
                        getSubject().addPropertyValue(getPredicate(), newValue);
                        table.setSelectedRow(newValue);
                    }
                }
            }
        }
    }


    public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        table.setSubject(subject);
    }


    private void updateActionsState() {
        int[] sel = table.getSelectedRows();
        boolean deleteEnabled = !isReadOnly() && table.getTableModel().isDeleteEnabled(sel);
        deleteAction.setEnabled(deleteEnabled);

        if (isReadOnly()) {
        	viewAction.setEnabled(false);
        } else {        
	        boolean viewEnabled = false;
	        if (sel.length == 1) {
	            Object object = table.getTableModel().getObject(sel[0]);
	            if (object instanceof RDFResource) {
	                viewEnabled = true;
	            }
	            else {
	                if (getSubject().getAllValuesFromOnTypes(getPredicate()) instanceof OWLDataRange) {
	                    viewEnabled = false;
	                }
	                else {
	                    PropertyValueEditor editor = getEditor(object);
	                    if (editor != null) {
	                    	TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
	                        viewEnabled = tsm.isActiveTriple(getSubject(), getPredicate(), object);
	                    }
	                }
	            }
	        }
	        viewAction.setEnabled(viewEnabled);
        }
        
        addAction.setEnabled(!isReadOnly());        
    }


    public void valuesChanged() {
        table.getTableModel().updateValues();
    }
}
