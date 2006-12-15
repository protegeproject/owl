package edu.stanford.smi.protegex.owl.ui.components.singleliteral;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.components.ComponentUtil;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractSingleLiteralComponent extends AbstractPropertyValuesComponent {

    private JComboBox booleanComboBox;

    private JComboBox datatypeComboBox;

    private Action deleteAction = new AbstractAction("Delete value", OWLIcons.getDeleteIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleDeleteAction();
        }
    };

    private JPanel mainPanel;

    private JTextComponent textComponent;

    private Component textComponentHolder;

    private final static String UNDEFINED = "undefined";


    private Action viewAction = new AbstractAction("View/edit value...", OWLIcons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleViewAction();
        }
    };


    public AbstractSingleLiteralComponent(RDFProperty predicate) {
    	this(predicate, null);
    }
    
    public AbstractSingleLiteralComponent(RDFProperty predicate, String label) {
        super(predicate, label);

        final OWLModel owlModel = getOWLModel();
        this.datatypeComboBox = ComponentUtil.createDatatypeComboBox(owlModel);
        int height = datatypeComboBox.getPreferredSize().height;
        datatypeComboBox.setPreferredSize(new Dimension(80, height));
        datatypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignTextFieldValue();
                updateTextFieldAlignment((RDFSDatatype) datatypeComboBox.getSelectedItem());
            }
        });

        booleanComboBox = new JComboBox(new Object[]{
                UNDEFINED, Boolean.FALSE, Boolean.TRUE
        });
        booleanComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignBooleanComboBoxValue();
            }
        });

        textComponent = createTextComponent();
        textComponent.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                assignTextFieldValue();
            }
        });
        textComponent.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    assignTextFieldValue();
                }
            }
        });

        OWLUI.addCopyPastePopup(textComponent);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.EAST, datatypeComboBox);
        textComponentHolder = createTextComponentHolder(textComponent);
        mainPanel.add(BorderLayout.CENTER, textComponentHolder);
        OWLLabeledComponent lc = new OWLLabeledComponent((label == null ? getLabel():label), mainPanel);
        lc.addHeaderButton(viewAction);
        lc.addHeaderButton(deleteAction);
        if (textComponentHolder instanceof JScrollPane) {
            lc.setVerticallyStretchable(true);
        }
        add(lc);
    }


    protected abstract JTextComponent createTextComponent();


    protected abstract Component createTextComponentHolder(JTextComponent textComponent);


    private void assignBooleanComboBoxValue() {
        Object sel = booleanComboBox.getSelectedItem();
        if (UNDEFINED.equals(sel)) {
            getSubject().setPropertyValue(getPredicate(), null);
        }
        else {
            getSubject().setPropertyValue(getPredicate(), sel);
        }
    }


    private void assignTextFieldValue() {
        Object oldValue = getSubject().getPropertyValue(getPredicate());
        String text = textComponent.getText().trim();
        Object newValue = null;
        if (text.length() > 0) {
            RDFSDatatype datatype = getDatatype();
            if (getOWLModel().getXSDstring().equals(datatype)) {
                String language = null;
                if (oldValue instanceof RDFSLiteral) {
                    RDFSLiteral oldLiteral = (RDFSLiteral) oldValue;
                    if (oldLiteral.getLanguage() != null) {
                        language = oldLiteral.getLanguage();
                        newValue = getOWLModel().createRDFSLiteral(text, language);
                    }
                    else {
                        newValue = text;
                    }
                }
                else {
                    newValue = text;
                }
            }
            else {
                newValue = getOWLModel().createRDFSLiteral(text, datatype);
            }
        }
        if (newValue == null) {
            getSubject().setPropertyValue(getPredicate(), null);
        }
        else {
            newValue = DefaultRDFSLiteral.getPlainValueIfPossible(newValue);
            Collection oldValues = getSubject().getPropertyValues(getPredicate(), true);
            if (!oldValues.contains(newValue)) {
                getSubject().setPropertyValue(getPredicate(), newValue);
            }
        }
    }


    private RDFSDatatype getDatatype() {
        return (RDFSDatatype) datatypeComboBox.getSelectedItem();
    }


    protected JTextComponent getTextComponent() {
        return textComponent;
    }


    private void handleDeleteAction() {
        textComponent.setText("");
        assignTextFieldValue();
        resetDatatypeComboBox();
    }


    private void handleViewAction() {
        Object object = getObject();
        PropertyValueEditor editor = getEditor(object);
        if (editor != null) {
            if (getObject() == null) {
                object = editor.createDefaultValue(getSubject(), getPredicate());
            }
            Object newValue = editor.editValue(null, getSubject(), getPredicate(), object);
            if (newValue != null) {
                getSubject().setPropertyValue(getPredicate(), newValue);
            }
        }
    }


    private boolean hasOnlyActiveValues() {
        if (getSubject() != null) {
            Collection values = getSubject().getPropertyValues(getPredicate());
            TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object value = it.next();
                if (!tsm.isActiveTriple(getSubject(), getPredicate(), value)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    private boolean isRangeDefined() {
        final RDFResource resource = getSubject();
        final RDFProperty property = getPredicate();
        return ComponentUtil.isRangeDefined(resource, property);
    }


    private void resetDatatypeComboBox() {
        RDFResource range = getPredicate().getRange();
        if (range instanceof RDFSDatatype) {
            setDatatypeComboBoxItem(range);
            updateTextFieldAlignment((RDFSDatatype) range);
        }
        else {
            Collection types = getSubject().getRDFTypes();
            for (Iterator it = types.iterator(); it.hasNext();) {
                RDFSClass type = (RDFSClass) it.next();
                if (type instanceof OWLNamedClass) {
                    OWLNamedClass namedClass = (OWLNamedClass) type;
                    RDFResource allValuesFrom = namedClass.getAllValuesFrom(getPredicate());
                    if (allValuesFrom instanceof RDFSDatatype) {
                        setDatatypeComboBoxItem(allValuesFrom);
                        updateTextFieldAlignment((RDFSDatatype) allValuesFrom);
                        return;
                    }
                    RDFResource someValuesFrom = namedClass.getSomeValuesFrom(getPredicate());
                    if (someValuesFrom instanceof RDFSDatatype) {
                        setDatatypeComboBoxItem(someValuesFrom);
                        updateTextFieldAlignment((RDFSDatatype) someValuesFrom);
                        return;
                    }
                }
            }
        }
    }


    private void setDatatypeComboBoxItem(RDFResource range) {
        if (!datatypeComboBox.getSelectedItem().equals(range)) { // To suppress event
            datatypeComboBox.setSelectedItem(range);
        }
    }


    public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        updateComboBoxVisibility();
        boolean editable = hasOnlyEditableValues();
        boolean b = editable && hasOnlyActiveValues() && subject.getHasValuesOnTypes(getPredicate()).isEmpty();
        textComponent.setEditable(b);
        booleanComboBox.setEnabled(b);
        datatypeComboBox.setEnabled(b && !isRangeDefined());
    }


    private void updateActionStatus() {
        boolean editable = hasOnlyEditableValues();
        deleteAction.setEnabled(getSubject() != null &&
                getSubject().getPropertyValue(getPredicate()) != null &&
                editable);
        final Object object = getObject();
        PropertyValueEditor editor = getEditor(object);
        viewAction.setEnabled(editable && editor != null);
    }


    private void updateComboBoxVisibility() {
        final RDFProperty property = getPredicate();
        RDFSDatatype datatype = null;
        Collection types = getSubject().getRDFTypes();
        for (Iterator it = types.iterator(); it.hasNext();) {
            RDFSClass type = (RDFSClass) it.next();
            if (type instanceof OWLNamedClass) {
                OWLNamedClass namedClass = (OWLNamedClass) type;
                RDFResource allValuesFrom = namedClass.getAllValuesFrom(property);
                if (allValuesFrom instanceof RDFSDatatype) {
                    datatype = (RDFSDatatype) allValuesFrom;
                }
            }
        }
        if (datatype == null) {
            RDFResource range = property.getRange();
            if (range instanceof RDFSDatatype) {
                datatype = (RDFSDatatype) range;
            }
        }
        else {
            if (datatype.getBaseDatatype() != null) {
                datatype = datatype.getBaseDatatype();
            }
        }
        OWLModel owlModel = getOWLModel();
        boolean defaultDatatype =
                owlModel.getXSDboolean().equals(datatype) ||
                        owlModel.getXSDstring().equals(datatype) ||
                        owlModel.getXSDint().equals(datatype) ||
                        owlModel.getXSDfloat().equals(datatype);
        if (defaultDatatype) {
            mainPanel.remove(datatypeComboBox);
        }
        else {
            mainPanel.add(BorderLayout.EAST, datatypeComboBox);
        }
    }


    private void updateDatatypeComboBox(Object value) {
        RDFSDatatype type = getOWLModel().getRDFSDatatypeOfValue(value);
        if (type != null) {
            setDatatypeComboBoxItem(type);
            updateTextFieldAlignment(type);
        }
        else {
            updateDatatypeComboBox(getOWLModel().getXSDstring());
        }
    }


    private void updateTextFieldAlignment(RDFSDatatype type) {
        if (getOWLModel().getXSDboolean().equals(type)) {
            if (booleanComboBox.getParent() != mainPanel) {
                mainPanel.remove(textComponentHolder);
                mainPanel.add(BorderLayout.CENTER, booleanComboBox);
            }
        }
        else {
            final boolean numericDatatype = type.isNumericDatatype();
            updateTextFieldAlignment(numericDatatype);
            if (textComponentHolder.getParent() != mainPanel) {
                mainPanel.remove(booleanComboBox);
                mainPanel.add(BorderLayout.CENTER, textComponentHolder);
            }
        }
    }


    protected void updateTextFieldAlignment(final boolean numericDatatype) {
    }


    public void valuesChanged() {
        Collection values = new ArrayList(getObjects(true));
        Collection hasValues = getSubject().getHasValuesOnTypes(getPredicate());
        for (Iterator it = hasValues.iterator(); it.hasNext();) {
            Object hasValue = it.next();
            if (!values.contains(hasValue)) {
                values.add(hasValue);
            }
        }
        Iterator it = values.iterator();
        if (it.hasNext()) {
            Object value = it.next();
            textComponent.setText("" + value);
            if (value instanceof Boolean) {
                booleanComboBox.setSelectedItem(value);
            }
            updateDatatypeComboBox(value);
        }
        else {
            textComponent.setText("");
            booleanComboBox.setSelectedItem(UNDEFINED);
            resetDatatypeComboBox();
        }
        updateActionStatus();
    }
}
