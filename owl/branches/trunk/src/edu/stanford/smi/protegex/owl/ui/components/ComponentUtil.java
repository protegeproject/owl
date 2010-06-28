package edu.stanford.smi.protegex.owl.ui.components;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ResourceComparator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ComponentUtil {


    public static JComboBox createDatatypeComboBox(final OWLModel owlModel) {
        List types = new ArrayList(owlModel.getRDFSDatatypes());
        types.remove(owlModel.getRDFXMLLiteralType());
        Collections.sort(types, new ResourceComparator());
        JComboBox datatypeComboBox = new JComboBox(types.toArray());
        datatypeComboBox.setRenderer(new DatatypeCellRenderer());
        datatypeComboBox.setSelectedItem(owlModel.getXSDstring());
        return datatypeComboBox;
    }


    public static JComboBox createLanguageComboBox(OWLModel owlModel) {
        return createLanguageComboBox(owlModel, null);
    }


    public static JComboBox createLanguageComboBox(OWLModel owlModel, String initialSelection) {
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("");
        comboBox.setEditable(true);
        String[] languages = owlModel.getUsedLanguages();
        for (int i = 0; i < languages.length; i++) {
            String language = languages[i];
            comboBox.addItem(language);
        }
        if (initialSelection != null) {
            comboBox.setSelectedItem(initialSelection);
        }
        return comboBox;
    }


    public static boolean isRangeDefined(final RDFResource resource, final RDFProperty property) {
        if (property.getRange() != null) {
            return true;
        }
        Collection types = resource.getRDFTypes();
        for (Iterator it = types.iterator(); it.hasNext();) {
            RDFSClass type = (RDFSClass) it.next();
            if (type instanceof OWLNamedClass) {
                OWLNamedClass namedClass = (OWLNamedClass) type;
                RDFResource allValuesFrom = namedClass.getAllValuesFrom(property);
                if (allValuesFrom != null) {
                    return true;
                }
            }
        }
        return false;
    }


    public static JComboBox createLangCellEditor(OWLModel owlModel, JTable table) {
        JComboBox comboBox = createLanguageComboBox(owlModel, null);
        comboBox.setFont(table.getFont());
        comboBox.setBackground(table.getBackground());
        return comboBox;
    }


    public static class DatatypeCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof RDFSDatatype) {
                value = ((RDFSDatatype) value).getLocalName();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
