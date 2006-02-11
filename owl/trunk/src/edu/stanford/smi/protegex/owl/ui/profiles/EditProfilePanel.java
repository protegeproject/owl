package edu.stanford.smi.protegex.owl.ui.profiles;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import junit.framework.Assert;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

/**
 * A JPanel that can be used to specify the language elements supported by
 * the user interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class EditProfilePanel extends JPanel {

    private static JFileChooser fileChooser;

    private JTextField fileTextField;

    private OWLModel owlModel;

    private OntModel ontModel;

    private FeatureTreeNode rootNode;

    private JTree tree;

    private final static int NODE_UNSELECTED = 0;

    private final static int NODE_SELECTED = 1;

    private final static int NODE_SOME_SELECTED = 2;


    public EditProfilePanel(OWLModel owlModel,
                            OntModel ontModel,
                            String fileName,
                            Collection selectedClasses) {
        this.owlModel = owlModel;
        this.ontModel = ontModel;
        rootNode = createRootNode(selectedClasses);
        tree = new JTree(rootNode);
        tree.setCellRenderer(new MyTreeCellRenderer());
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
        });
        final JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        LabeledComponent lc = new OWLLabeledComponent("Allowable Features (Note: Work in progress!)", scrollPane);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        fileTextField = new JTextField(fileName);
        LabeledComponent fileNamePanel = new LabeledComponent("Save profile to file:",
                fileTextField);
        Action selectFileAction = new AbstractAction("Select output file",
                OWLIcons.getAddIcon("File")) {
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        };
        fileNamePanel.addHeaderButton(selectFileAction);
        add(BorderLayout.SOUTH, fileNamePanel);
    }


    private FeatureTreeNode createNode(OntClass currentClass, Collection selectedClasses) {
        FeatureTreeNode node = new FeatureTreeNode(currentClass, selectedClasses);
        java.util.List nodes = new ArrayList();
        for (Iterator it = ProfilesManager.getSubclasses(ontModel, currentClass); it.hasNext();) {
            OntClass subClass = (OntClass) it.next();
            if (subClass.getLabel(null) != null) {
                FeatureTreeNode childNode = createNode(subClass, selectedClasses);
                nodes.add(childNode);
            }
        }
        Collections.sort(nodes);
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            FeatureTreeNode childNode = (FeatureTreeNode) it.next();
            node.add(childNode);
        }
        return node;
    }


    private OntModel createOntModel() {
        String namespace = "http://www.owl-ontologies.com/MyProfile.owl#";
        OntModel ontModel = ProfilesManager.createProfile(namespace);
        ProfilesManager.addAltEntryForOWLProfiles(ontModel);
        ontModel.getDocumentManager().loadImport(ontModel, OWLProfiles.NS);
        OntClass ontClass = ontModel.createClass(namespace + "MyProfile");
        rootNode.addSubclasses(ontModel, ontClass);
        return ontModel;
    }


    private FeatureTreeNode createRootNode(Collection selectedClasses) {
        OntClass rootClass = ontModel.getOntClass(OWLProfiles.RDF.getURI());
        Assert.assertNotNull(rootClass);
        FeatureTreeNode root = createNode(rootClass, selectedClasses);
        root.updateSelection();
        return root;
    }


    private static Collection getSelectedClasses(OntModel ontModel) {
        OntClass ontClass = ProfilesManager.getCustomProfileFeaturesClass(ontModel);
        if (ontClass != null) {
            return ProfilesManager.getSelectedClasses(ontModel, ontClass);
        }
        else {
            return null;
        }
    }


    private void handleMousePressed(MouseEvent e) {
        int row = tree.getRowForLocation(e.getX(), e.getY());
        if (row >= 0 && row < tree.getRowCount()) {
            Rectangle r = tree.getRowBounds(row);
            if (r.contains(e.getX(), e.getY()) && e.getX() - r.x < 12) {
                TreePath path = tree.getPathForRow(row);
                FeatureTreeNode node = (FeatureTreeNode) path.getLastPathComponent();
                node.toggle();
                rootNode.updateSelection();
                tree.repaint();
            }
        }
    }


    public void handleOK() {
        String fileName = fileTextField.getText();
        try {
            OntModel newOntModel = createOntModel();
            ProfilesManager.saveOntModel(newOntModel, fileName);
            ProfilesManager.clearCache(fileName);
            ProfilesManager.setProfile(owlModel, fileName);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(this,
                    "Could not write to " + fileName + ":\n" +
                    ex.toString(), "Error");
        }
    }


    private void selectFile() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
        }
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileTextField.setText(file.getAbsolutePath());
        }
    }


    public static boolean showProfilePanelDialog(OWLModel owlModel) {
        try {
            OntModel ontModel = ProfilesManager.getProfileOntModel(owlModel);
            if (ontModel == null) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                        "Could not find profile class:\n" + ProfilesManager.getProfile(owlModel),
                        "Invalid Profile");
            }
            else {
                return showProfilePanelDialog(owlModel, ontModel);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "Could not open profile:\n" + ex, "Error");
        }
        return false;
    }


    public static boolean showProfilePanelDialog(OWLModel owlModel, OntModel ontModel) {
        String fileName = null;
        String customProfileURI = ProfilesManager.getCustomProfileURI(owlModel);
        if (customProfileURI == null) {
            fileName = "MyProfile.owl";
        }
        else {
            if (customProfileURI.startsWith("http://")) {
                int index = customProfileURI.lastIndexOf('/');
                fileName = customProfileURI.substring(index + 1);
            }
            else {
                fileName = customProfileURI;
            }
        }
        return showProfilePanelDialog(owlModel, ontModel, fileName);
    }


    public static boolean showProfilePanelDialog(OWLModel owlModel, OntModel ontModel, String fileName) {
        Collection selectedClasses = getSelectedClasses(ontModel);
        EditProfilePanel panel = new EditProfilePanel(owlModel, ontModel, fileName, selectedClasses);
        if (ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(owlModel.getProject()), panel,
                "Language Profile (" + fileName + ")", ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK) {
            panel.handleOK();
            return true;
        }
        else {
            return false;
        }
    }


    private class FeatureTreeNode extends DefaultMutableTreeNode implements Comparable {

        private int selection = NODE_UNSELECTED;


        FeatureTreeNode(OntClass ontClass, Collection selectedClasses) {
            setUserObject(ontClass);
            if (selectedClasses.contains(ontClass)) {
                selection = NODE_SELECTED;
            }
        }


        public void addSubclasses(OntModel targetModel, OntClass ontClass) {
            if (selection == NODE_SELECTED) {
                OntClass c = (OntClass) getUserObject();
                OntClass targetClass = targetModel.getOntClass(c.getURI());
                targetClass.addSuperClass(ontClass);
            }
            else if (selection == NODE_SOME_SELECTED) {
                Enumeration enumeration = children();
                while (enumeration.hasMoreElements()) {
                    FeatureTreeNode childNode = (FeatureTreeNode) enumeration.nextElement();
                    childNode.addSubclasses(targetModel, ontClass);
                }
            }
        }


        public int compareTo(Object o) {
            return toString().compareTo(o.toString());
        }


        public Icon getIcon() {
            if (selection == NODE_UNSELECTED) {
                return OWLIcons.getImageIcon("CheckBoxUnselected");
            }
            else if (selection == NODE_SELECTED) {
                return OWLIcons.getImageIcon("CheckBoxSelected");
            }
            else if (selection == NODE_SOME_SELECTED) {
                return OWLIcons.getImageIcon("CheckBoxSelectedGrayed");
            }
            return null;
        }


        public void toggle() {
            if (selection == NODE_SOME_SELECTED || selection == NODE_UNSELECTED) {
                selection = NODE_SELECTED;
            }
            else {
                selection = NODE_UNSELECTED;
            }
            transmitSelectionIntoChildren();
        }


        public String toString() {
            OntClass ontClass = (OntClass) getUserObject();
            return ontClass.getLabel(null);
        }


        public void transmitSelectionIntoChildren() {
            Enumeration enumeration = children();
            while (enumeration.hasMoreElements()) {
                FeatureTreeNode childNode = (FeatureTreeNode) enumeration.nextElement();
                childNode.selection = selection;
                childNode.transmitSelectionIntoChildren();
            }
        }


        public void updateSelection() {
            Enumeration enumeration = children();
            boolean equal = true;
            int firstValue = -1;
            while (enumeration.hasMoreElements()) {
                FeatureTreeNode childNode = (FeatureTreeNode) enumeration.nextElement();
                childNode.updateSelection();
                if (firstValue < 0) {
                    firstValue = childNode.selection;
                }
                else if (childNode.selection != firstValue) {
                    equal = false;
                }
            }
            if (getChildCount() > 0) {
                if (equal) {
                    selection = firstValue;
                }
                else {
                    selection = NODE_SOME_SELECTED;
                }
            }
        }
    }


    private class MyTreeCellRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            FeatureTreeNode node = (FeatureTreeNode) value;
            setIcon(node.getIcon());
            return this;
        }
    }
}
