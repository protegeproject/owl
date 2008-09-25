package edu.stanford.smi.protegex.owl.ui.profiles;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.testing.OWLDLTest;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OverlayIcon;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProfileSelectionPanel extends JPanel {

    private boolean changed = false;

    private Action createFileAction = new AbstractAction("Create new profile file...",
            OWLIcons.getCreateIcon("File")) {
        public void actionPerformed(ActionEvent e) {
            createFile();
        }
    };

    private JRadioButton customRadioButton;

    private JTextField customTextField;

    private OntModel defaultOntModel;

    private JRadioButton defaultRadioButton;

    public final static String[] DEFAULT_PROFILES = {
            OWLProfiles.RDF.getURI(),
            OWLProfiles.RDF_but_not_OWL.getURI(),
            OWLProfiles.OWL_Full.getURI(),
            OWLProfiles.OWL_DL.getURI(),
            OWLProfiles.OWL_Lite.getURI()
    };

    private Action editProfileAction = new AbstractAction("Edit profile...",
            OWLIcons.getImageIcon("EditProfile")) {
        public void actionPerformed(ActionEvent e) {
            editProfile();
        }
    };

    private static JFileChooser fileChooser;

    private String oldProfileURI;

    private OWLModel owlModel;

    private JComboBox predefinedProfileComboBox;

    private Action selectFileAction = new AbstractAction("Select file...",
            OWLIcons.getAddIcon("File")) {
        public void actionPerformed(ActionEvent e) {
            selectFile();
        }
    };


    public ProfileSelectionPanel(final OWLModel owlModel) {

        oldProfileURI = ProfilesManager.getProfile(owlModel);

        defaultOntModel = ProfilesManager.getDefaultProfileOntModel();
        if (defaultOntModel == null) {
            return;
        }

        this.owlModel = owlModel;

        predefinedProfileComboBox = createComboBox(owlModel);

        defaultRadioButton = new JRadioButton("Use standard profile:");
        defaultRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateComponents(true);
            }
        });
        customRadioButton = new JRadioButton("Use custom profile:");
        customRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateComponents(false);
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(customRadioButton);
        group.add(defaultRadioButton);

        setBorder(BorderFactory.createTitledBorder("Language Profile"));
        JPanel defaultPanel = new JPanel(new FlowLayout());
        defaultPanel.add(defaultRadioButton);
        defaultPanel.add(predefinedProfileComboBox);

        customTextField = new JTextField();
        String custom = ProfilesManager.getCustomProfileURI(owlModel);
        if (custom != null) {
            customTextField.setText(custom);
        }
        customTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                assignCustomTextFieldValue();
            }
        });

        JPanel customPanel = new JPanel(new FlowLayout());
        customPanel.add(customRadioButton);
        customPanel.add(customTextField);
        customTextField.setPreferredSize(new Dimension(300, customTextField.getPreferredSize().height));
        JToolBar customToolBar = ComponentFactory.createToolBar();
        addToolBarButton(customToolBar, createFileAction);
        addToolBarButton(customToolBar, selectFileAction);
        addToolBarButton(customToolBar, editProfileAction);
        customPanel.add(customToolBar);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createLeftAlignedPanel(defaultPanel));
        add(createLeftAlignedPanel(customPanel));

        updateComponents(ProfilesManager.getCustomProfileURI(owlModel) == null);
    }


    private void assignCustomTextFieldValue() {
        String value = customTextField.getText().trim();
        String newProfile = null;
        if (value.length() == 0) {
            newProfile = ProfilesManager.getPredefinedProfile(owlModel);
        }
        else {
            newProfile = value;
        }
        ProfilesManager.setProfile(owlModel, newProfile);
    }


    private void addToolBarButton(JToolBar toolBar, Action action) {
        JButton button = ComponentFactory.addToolBarButton(toolBar, action);
        Icon icon = (Icon) action.getValue(Action.SMALL_ICON);
        if (icon instanceof OverlayIcon) {
            button.setDisabledIcon(((OverlayIcon) icon).getGrayedIcon());
        }
    }


    private JComboBox createComboBox(final OWLModel owlModel) {
        final OntClass[] items = new OntClass[]{};
        for (int i = 0; i < items.length; i++) {
        	OntClass profileCls = defaultOntModel.getOntClass(DEFAULT_PROFILES[i]);
        	if (profileCls != null) {
        		items[i] = profileCls ;  
        	}
        }

        final JComboBox comboBox = new JComboBox(items);

        String predefinedURI = ProfilesManager.getPredefinedProfile(owlModel);
        
        for (int i = 0; i < items.length; i++) {
			if (items[i] != null && predefinedURI.equals(items[i].getURI())) {
				comboBox.setSelectedIndex(i);
				break;
			}
		}
                

        comboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                OntClass ontClass = (OntClass) value;
                String label = ontClass == null ? "" : ontClass.getLabel("");
                return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
            }
        });
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProfilesManager.setProfile(owlModel, items[comboBox.getSelectedIndex()].getURI());
                owlModel.setOWLTestGroupEnabled(OWLDLTest.GROUP,
                        ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.OWL_DL));
            }
        });
        return comboBox;
    }


    private void createFile() {
        String baseFileName = "MyProfile";
        String suffix = ".owl";
        String fileName = baseFileName + suffix;
        for (int i = 1; new File(fileName).exists(); i++) {
            fileName = baseFileName + "_" + i + suffix;
        }
        String predefined = ProfilesManager.getPredefinedProfile(owlModel);
        String ns = "http://www.owl-ontologies.com/" + fileName + "#";
        OntModel ontModel = ProfilesManager.createProfile(ns);
        OntClass ontClass = ontModel.createClass(ns + "MyProfile");
        Resource predefinedClass = ontModel.getResource(predefined);
        ontModel.add(predefinedClass, RDFS.subClassOf, ontClass);
        try {
            ProfilesManager.saveOntModel(ontModel, fileName);
            customTextField.setText(fileName);
            changed = true;
            ProfilesManager.setProfile(owlModel, fileName);
            ProfilesManager.clearCache(fileName);
            OntModel newOntModel = ProfilesManager.getProfileOntModel(owlModel);
            if (EditProfilePanel.showProfilePanelDialog(owlModel, newOntModel, fileName)) {
                customTextField.setText(ProfilesManager.getCustomProfileURI(owlModel));
            }
        }
        catch (Exception ex) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            OWLUI.showErrorMessageDialog("Could not create profile file " + fileName + ":\n" + ex,
                    "Error");
        }
    }


    private JPanel createLeftAlignedPanel(Component comp) {
        JPanel result = new JPanel(new BorderLayout());
        result.add(BorderLayout.WEST, comp);
        result.add(BorderLayout.CENTER, new JPanel());
        return result;
    }


    private void editProfile() {
        if (ProfilesManager.getCustomProfileURI(owlModel) != null) {
            if (EditProfilePanel.showProfilePanelDialog(owlModel)) {
                customTextField.setText(ProfilesManager.getCustomProfileURI(owlModel));
                changed = true;
            }
        }
        else {
            createFile();
        }
    }


    public boolean getRequiresReloadUI() {
        return !oldProfileURI.equals(ProfilesManager.getProfile(owlModel)) || changed;
    }


    private void selectFile() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
        }
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            customTextField.setText(file.getAbsolutePath());
        }
    }


    private void updateComponents(boolean defaultEnabled) {
        defaultRadioButton.setSelected(defaultEnabled);
        predefinedProfileComboBox.setEnabled(defaultEnabled);
        customRadioButton.setSelected(!defaultEnabled);
        customTextField.setEnabled(!defaultEnabled);
        selectFileAction.setEnabled(!defaultEnabled);
        createFileAction.setEnabled(!defaultEnabled);
        editProfileAction.setEnabled(!defaultEnabled);
    }
}

