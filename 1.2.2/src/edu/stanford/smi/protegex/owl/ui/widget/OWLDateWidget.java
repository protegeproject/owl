package edu.stanford.smi.protegex.owl.ui.widget;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.toedter.calendar.JDateChooser;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDateWidget extends AbstractPropertyWidget {

    private JDateChooser dateChooser;

    private Action deleteAction = new AbstractAction("Delete value", OWLIcons.getDeleteIcon()) {
        public void actionPerformed(ActionEvent e) {
            deleteValue();
        }
    };


    private LabeledComponent lc;

    private Action setAction = new AbstractAction("Set value", OWLIcons.getAddIcon()) {
        public void actionPerformed(ActionEvent e) {
            setPropertyValue(new Date());
        }
    };


    public OWLDateWidget() {
        setPreferredColumns(2);
        setPreferredRows(1);
    }


    protected RDFSLiteral createPropertyValue(Date date) {
        String value = XMLSchemaDatatypes.getDateString(date);
        RDFSDatatype datatype = getOWLModel().getRDFSDatatypeByURI(XSDDatatype.XSDdate.getURI());
        return getOWLModel().createRDFSLiteral(value, datatype);
    }


    protected void deleteValue() {
        getEditedResource().setPropertyValue(getRDFProperty(), null);
    }


    protected Component getCenterComponent() {
        return dateChooser;
    }


    protected Date getDate() {
        return dateChooser.getDate();
    }


    public static Date getDate(String s) {
        Date date = new Date();
        if (s != null) {
            int index = s.indexOf("T");
            if (index >= 0) {
                s = s.substring(0, index);
            }
            String[] ss = s.split("-");
            if (ss.length >= 3) {
                try {
                    int year = Integer.parseInt(ss[0]);
                    int month = Integer.parseInt(ss[1]) - 1;
                    int day = Integer.parseInt(ss[2]);
                    date = new Date(new GregorianCalendar(year, month, day).getTimeInMillis());
                }
                catch (Exception ex) {
                    System.err.println("[OWLDateWidget] Could not parse value " + s + ": " + ex);
                }
            }
        }
        return date;
    }


    public void initialize() {
        dateChooser = new JDateChooser();
        dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    updateValues();
                }
            }
        });
        setLayout(new BorderLayout());
        lc = new LabeledComponent(getRDFProperty().getBrowserText(), getCenterComponent());
        lc.addHeaderButton(setAction);
        lc.addHeaderButton(deleteAction);
        add(BorderLayout.CENTER, lc);
    }


    private void setDateChooserValue() {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        Object value = resource.getPropertyValue(property);
        setValue(value == null ? null : value.toString());
    }


    protected void setValue(String s) {
        Date date = getDate(s);
        dateChooser.setDate(date);
    }


    private void setPropertyValue(Date date) {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        if (resource != null && property != null) {
            Object value = createPropertyValue(date);
            resource.setPropertyValue(property, value);
        }
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateComponents();
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance != null) {
            setDateChooserValue();
        }
        updateComponents();
    }


    public void setValues(Collection values) {
        super.setValues(values);
        updateComponents();
        ignoreUpdate = true;
        setDateChooserValue();
        ignoreUpdate = false;
    }


    protected void updateComponents() {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        if (resource != null && property != null && resource.isEditable()) {
            boolean value = resource.getPropertyValue(property) != null;
            setAction.setEnabled(!value);
            deleteAction.setEnabled(value);
            dateChooser.setEnabled(value);
            lc.revalidate();
        }
        else {
            setAction.setEnabled(false);
            deleteAction.setEnabled(false);
            dateChooser.setEnabled(false);
        }
    }


    private boolean ignoreUpdate = false;


    protected void updateValues() {
        if (!ignoreUpdate) {
            Date date = getDate();
            setPropertyValue(date);
        }
    }


    /**
     * @param cls
     * @param slot
     * @param facet
     * @return
     * @deprecated
     */
    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return OWLWidgetMapper.isSuitable(OWLDateWidget.class, cls, slot);
    }
}
