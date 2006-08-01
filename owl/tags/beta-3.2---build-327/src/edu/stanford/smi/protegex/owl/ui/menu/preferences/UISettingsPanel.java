package edu.stanford.smi.protegex.owl.ui.menu.preferences;

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplayFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JComponent that allows to specify user interface settings.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class UISettingsPanel extends JComponent {

    private JRadioButton[] classDisplayButtons;

    private JCheckBox constraintCheckingCheckBox;

    private JCheckBox dragAndDropCheckBox;

    private JCheckBox externalResourcesCheckBox;

    private JComboBox iconsComboBox;

    private String initialStyle;

    private boolean initialDDValue;

    private OWLModel owlModel;


    public UISettingsPanel(OWLModel aOWLModel) {

        this.owlModel = aOWLModel;

        Class[] classes = OWLClassDisplayFactory.getAvailableDisplayClasses();
        classDisplayButtons = new JRadioButton[classes.length];
        JPanel radioButtonsPanel = new JPanel(new GridLayout(classes.length, 1));
        radioButtonsPanel.setBorder(BorderFactory.createTitledBorder("Class Display Format"));
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < classes.length; i++) {
            final Class c = classes[i];
            String text = "";
            try {
                text = (String) c.getMethod("getUIDescription", new Class [0]).invoke(c, new Object [0]);
            }
            catch (Exception e) {
                text = c.getName();
            }

            int index = text.lastIndexOf(".");
            text = text.substring(index + 1);
            JRadioButton radioButton = new JRadioButton(text);
            classDisplayButtons[i] = radioButton;
            group.add(radioButton);
            if (OWLClassDisplayFactory.getDefaultDisplay().getClass() == c) {
                radioButton.setSelected(true);
            }
            radioButtonsPanel.add(radioButton);
            radioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    OWLClassDisplay display = OWLClassDisplayFactory.getDisplay(c);
                    OWLClassDisplayFactory.setDefaultDisplay(display);
                    owlModel.setOWLClassDisplay(display);
                }
            });
        }

        constraintCheckingCheckBox = new JCheckBox("Constraint checking (red borders) at edit time");
        constraintCheckingCheckBox.setSelected(OWLUI.isConstraintChecking(owlModel));
        constraintCheckingCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OWLUI.setConstraintChecking(owlModel, constraintCheckingCheckBox.isSelected());
            }
        });

        dragAndDropCheckBox = new JCheckBox("Drag and Drop");
        dragAndDropCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDragAndDrop();
            }
        });
        initialDDValue = OWLUI.isDragAndDropSupported(aOWLModel);
        dragAndDropCheckBox.setSelected(initialDDValue);

        externalResourcesCheckBox = new JCheckBox("Allow the creation of external resources (untyped URIs)");
        externalResourcesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateExternalResources();
            }
        });
        externalResourcesCheckBox.setSelected(OWLUI.isExternalResourcesSupported(aOWLModel));

        iconsComboBox = new JComboBox(new Object[]{
                OWLIcons.STYLE_DEFAULT,
                OWLIcons.STYLE_MULTICOLORED
        });
        initialStyle = OWLIcons.style;
        iconsComboBox.setSelectedItem(initialStyle);
        Dimension size = new Dimension(100, iconsComboBox.getPreferredSize().height);
        iconsComboBox.setMaximumSize(size);
        iconsComboBox.setPreferredSize(size);
        iconsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateIconsStyle();
            }
        });

        JPanel leftPanel = new JPanel(new GridLayout(3, 1));
        leftPanel.setBorder(BorderFactory.createTitledBorder("User Interface Features"));
        leftPanel.add(dragAndDropCheckBox);
        leftPanel.add(constraintCheckingCheckBox);
        leftPanel.add(externalResourcesCheckBox);

        Box iconsPanel = Box.createHorizontalBox();
        iconsPanel.add(new JLabel("Icon Style: "));
        iconsPanel.add(iconsComboBox);
        iconsPanel.add(Box.createHorizontalGlue());

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(BorderLayout.NORTH, radioButtonsPanel);
        rightPanel.add(BorderLayout.CENTER, iconsPanel);

        setLayout(new BorderLayout(8, 0));
        add(BorderLayout.CENTER, leftPanel);
        add(BorderLayout.EAST, rightPanel);
    }


    private void updateExternalResources() {
        boolean enabled = externalResourcesCheckBox.isSelected();
        OWLUI.setExternalResourcesSupported(owlModel, enabled);
    }


    public boolean getRequiresReloadUI() {
        return initialDDValue != dragAndDropCheckBox.isSelected() ||
                !initialStyle.equals(iconsComboBox.getSelectedItem());
    }


    private void updateDragAndDrop() {
        boolean enabled = dragAndDropCheckBox.isSelected();
        OWLUI.setDragAndDropSupported(owlModel, enabled);
    }


    private void updateIconsStyle() {
        String newStyle = (String) iconsComboBox.getSelectedItem();
        OWLIcons.setStyle(newStyle);
        ApplicationProperties.setString(OWLIcons.STYLE_VARIABLE, newStyle);
    }
}
