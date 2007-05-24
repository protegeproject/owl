package edu.stanford.smi.protegex.owl.ui.properties.range;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.metaproject.impl.OperationImpl;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.menu.preferences.ProtegeSettingsPanel;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * A property widget to edit the range of an RDFProperty (or subclasses).
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLRangeWidget extends AbstractPropertyWidget {

    private final static String ANY = "Any";

    private UnionRangeClassesComponent classesComponent;

    private JComboBox comboBox;

    private OWLDataRangeComponent dataRangeComponent;

    private FacetsPanel facetsPanel;

    private LabeledComponent lc;

    private JPanel mainPanel;

    private JPanel northPanel;

    private final static String OBJECTS = "Objects";

    /**
     * Needed to avoid infinite recursion: If the range changes, the form may
     * have been re-created as a side-effect!
     */
    private boolean ignore = false;


    public static void addDatatypes(OWLModel owlModel, Vector values) {
        values.add(owlModel.getXSDboolean());
        values.add(owlModel.getXSDfloat());
        values.add(owlModel.getXSDint());
        values.add(owlModel.getXSDstring());
        java.util.List ds = new ArrayList(owlModel.getRDFSDatatypes());
        ds.removeAll(values);
        ds = OWLUtil.removeInvisibleResources(ds.iterator());
        Collections.sort(ds, new FrameComparator());
        values.add(" ");
        values.addAll(ds);
    }


    private FacetsPanel createFacetsPanel(RDFSDatatype datatype) {
        if (datatype != null) {
            RDFSDatatype baseDatatype = datatype.getBaseDatatype();
            if (baseDatatype != null) {
                return createFacetsPanel(baseDatatype);
            }
            else {
                if (datatype.equals(getOWLModel().getXSDstring())) {
                    return new StringFacetsPanel(this);
                }
                else if (datatype.isNumericDatatype()) {
                    return new NumericFacetsPanel(this);
                }
            }
        }
        return null;
    }


    public void initialize() {

        classesComponent = new UnionRangeClassesComponent(getOWLModel(), this);

        comboBox = new JComboBox();
        comboBox.setRenderer(new FrameRenderer());
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleComboBoxChanged();
            }
        });
        northPanel = new JPanel(new BorderLayout());
        northPanel.add(BorderLayout.NORTH, comboBox);

        dataRangeComponent = new OWLDataRangeComponent(this);

        mainPanel = new JPanel(new BorderLayout(0, 4));
        mainPanel.add(BorderLayout.NORTH, northPanel);
        mainPanel.add(BorderLayout.CENTER, dataRangeComponent);

        lc = new LabeledComponent("Range", mainPanel);
        lc.setVerticallyStretchable(true);
        add(BorderLayout.CENTER, lc);
    }


    RDFProperty getEditedProperty() {
        return (RDFProperty) getEditedResource();
    }


    private void handleComboBoxChanged() {
        Object newValue = comboBox.getSelectedItem();
        if (" ".equals(newValue)) {
            comboBox.setSelectedItem(ANY);
            newValue = ANY;
        }
        if (ANY.equals(newValue)) {
            setRange(null);
        }
        else if (OBJECTS.equals(newValue)) {
            if (getEditedProperty().getRange() != null) {
                setRange(null);
            }
            mainPanel.remove(dataRangeComponent);
            mainPanel.add(BorderLayout.CENTER, classesComponent);
            mainPanel.revalidate();
        }
        else if (newValue instanceof RDFSDatatype) {
            RDFSDatatype datatype = (RDFSDatatype) newValue;
            if (!datatype.equals(getEditedProperty().getRange())) {
                setRange(datatype);
            }
            dataRangeComponent.setDatatype(datatype);
            if (dataRangeComponent.getParent() != mainPanel) {
                mainPanel.remove(classesComponent);
                mainPanel.add(BorderLayout.CENTER, dataRangeComponent);
                mainPanel.revalidate();
            }
        }
        updateFacetsPanel();
    }


    public static boolean isSuitable(Cls cls, edu.stanford.smi.protege.model.Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(RDFSNames.Slot.RANGE);
    }


    private void refillAll() {
        refillComboBox();
        dataRangeComponent.refill();
        classesComponent.refill();
        updateFacetsPanel();
    }


    private void refillComboBox() {
        if (getEditedResource() instanceof OWLObjectProperty) {
            removeAll();
            add(BorderLayout.CENTER, classesComponent);
        }
        else if (getEditedResource() instanceof RDFProperty) {
            add(BorderLayout.CENTER, lc);
            Vector values = new Vector();
            OWLModel owlModel = getOWLModel();
            values.add(ANY);
            if (!(getEditedResource() instanceof OWLDatatypeProperty)) {
                values.add(OBJECTS);
            }
            addDatatypes(owlModel, values);
            comboBox.setModel(new DefaultComboBoxModel(values));
            selectComboBoxValue();
            dataRangeComponent.setEditable(!ANY.equals(comboBox.getSelectedItem()) && getEditedProperty().isEditable());
        }
    }


    private void selectComboBoxValue() {
        RDFProperty property = (RDFProperty) getEditedResource();
        if (property != null) {
            ignore = true;
            selectComboBoxValue(property);
            ignore = false;
        }
    }


    private void selectComboBoxValue(RDFProperty property) {
        RDFResource range = property.getRange();
        if (range instanceof RDFSDatatype) {
            RDFSDatatype datatype = (RDFSDatatype) range;
            if (datatype.getBaseDatatype() != null) {
                datatype = datatype.getBaseDatatype();
            }
            comboBox.setSelectedItem(datatype);
        }
        else if (range instanceof OWLDataRange) {
            RDFSDatatype datatype = ((OWLDataRange) range).getRDFDatatype();
            if (datatype != null && !datatype.equals(comboBox.getSelectedItem())) {
                comboBox.setSelectedItem(datatype);
            }
        }
        else if (range instanceof RDFSClass) {
            comboBox.setSelectedItem(OBJECTS);
        }
        else if (range == null && property.getSuperpropertyCount() > 0) {
            RDFProperty firstSuperproperty = property.getFirstSuperproperty();
            selectComboBoxValue(firstSuperproperty);
        }
    }


    public void setEditable(boolean b) {
        dataRangeComponent.setEditable(b);
        classesComponent.setEditable(b);
        comboBox.setEnabled(b);
        if (facetsPanel != null) {
            facetsPanel.setEditable(b);
        }
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        refillAll();
    }


    void setRange(RDFSDatatype datatype) {
        if (!ignore) {
            RDFResource range = getEditedProperty().getRange();
            if (range == null || !range.equals(datatype)) {
                if (range instanceof RDFSDatatype && range.isAnonymous()) {
                    range.delete();
                }
                getEditedProperty().setRange(datatype);
            }
        }
    }


    public void setValues(Collection collection) {
        refillAll();
    }


    private void updateFacetsPanel() {
        if (ProtegeSettingsPanel.isUserDefinedDatatypesSupported(getOWLModel())) {
            RDFResource range = getEditedProperty().getRange();
            if (facetsPanel != null) {
                northPanel.remove(facetsPanel);
                mainPanel.revalidate();
            }
            if (range instanceof RDFSDatatype) {
                RDFSDatatype datatype = (RDFSDatatype) range;
                facetsPanel = createFacetsPanel(datatype);
                if (facetsPanel != null) {
                    northPanel.add(BorderLayout.SOUTH, facetsPanel);
                    mainPanel.revalidate();
                    facetsPanel.update(datatype);
                }
            }
        }
    }
    
    public void setEnabled(boolean enabled) {
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(getOWLModel(), OperationImpl.PROPERTY_TAB_WRITE);
    	classesComponent.setEnabled(enabled);
    	dataRangeComponent.setEnabled(enabled);
    	super.setEnabled(enabled);
    };
}
