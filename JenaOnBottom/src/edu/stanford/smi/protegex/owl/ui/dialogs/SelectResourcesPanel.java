package edu.stanford.smi.protegex.owl.ui.dialogs;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.SelectInstancesPanel;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SelectResourcesPanel extends SelectInstancesPanel {

    private final static String DIRECT_ASSERTED = "Direct Asserted Instances";

    private final static String DIRECT_INFERRED = "Direct Inferred Instances";

    private final static String ALL_ASSERTED = "All Asserted Instances";

    private final static String ALL_INFERRED = "All Inferred Instances";

    private JComboBox classComboBox;

    private JComboBox instancesComboBox;


    public SelectResourcesPanel(OWLModel owlModel, Collection classes) {
        this(owlModel, classes, false);
    }


    public SelectResourcesPanel(OWLModel owlModel, Collection classes, boolean allowsMultipleSelection) {
        super(owlModel, classes);
        if (!allowsMultipleSelection) {
            _instanceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }


    protected LabeledComponent createClsesLabeledComponent(KnowledgeBase kb, Collection clses) {
        LabeledComponent lc = super.createClsesLabeledComponent(kb, clses);
        //lc.setHeaderLabel(null);
        //classComboBox = new JComboBox();
        //classComboBox.addItem("Asserted Hierarchy");
        //classComboBox.addItem("Inferred Hierarchy");
        //lc.setHeaderComponent(classComboBox, BorderLayout.WEST);
        return lc;
    }


    protected JComponent createClsTree(Collection clses) {
        return super.createClsTree(clses);
    }


    protected JComboBox createDirectAllInstanceComboBox() {
        super.createDirectAllInstanceComboBox(); // Only not to break stuff
        instancesComboBox = ComponentFactory.createComboBox();
        instancesComboBox.addItem(DIRECT_ASSERTED);
        instancesComboBox.addItem(DIRECT_INFERRED);
        instancesComboBox.addItem(ALL_ASSERTED);
        instancesComboBox.addItem(ALL_INFERRED);
        instancesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadInstances();
            }
        });
        return instancesComboBox;
    }


    protected Collection getInstances(Cls cls) {
        Object selectedItem = instancesComboBox.getSelectedItem();
        if (DIRECT_INFERRED.equals(selectedItem)) {
            if (cls instanceof RDFSClass) {
                RDFSClass c = (RDFSClass) cls;
                return c.getInferredInstances(false);
            }
        }
        else if (ALL_ASSERTED.equals(selectedItem)) {
            return cls.getInstances();
        }
        else if (ALL_INFERRED.equals(selectedItem)) {
            if (cls instanceof RDFSClass) {
                RDFSClass c = (RDFSClass) cls;
                return c.getInferredInstances(true);
            }
        }
        return cls.getDirectInstances();
    }
}
