package edu.stanford.smi.protegex.owl.ui.menu.preferences;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.smi.protege.model.BrowserSlotPattern;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.util.OWLBrowserSlotPattern;

public class RenderingPanel extends JPanel {
    private static final long serialVersionUID = -2021694698732430578L;
    public static String RENDERING_PANEL_TITLE = "Rendering";
    public static String DEFAULT_BROWSER_SLOT_PROP = "owl.default.browser.slot";
    public static String[] META_SLOT_NAMES = {
        OWLNames.Cls.NAMED_CLASS, RDFSNames.Cls.NAMED_CLASS, RDFNames.Cls.PROPERTY, OWLNames.Cls.THING
    };
    private OWLModel owlModel;
    private JComboBox renderingPropertyBox;
    private JComboBox defaultRenderingPropertyBox;
    private boolean requiresReloadUI = false;
    
    public RenderingPanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(RENDERING_PANEL_TITLE));
        add(createCenterPanel(), BorderLayout.CENTER);
    }
    
    
    public boolean getRequiresReloadUI() {
        return requiresReloadUI;
    }
    
    /* ****************************************************************
     * Beans - made static with owlModel argument so that they can be used elsewhere.
     */
    
    @SuppressWarnings("deprecation")
    public static void setCommonBrowserSlot(OWLModel owlModel, Slot slot) {
        OWLBrowserSlotPattern pattern = new OWLBrowserSlotPattern(slot);
        for (String metaClsName : META_SLOT_NAMES) {
            owlModel.setDirectBrowserSlotPattern(owlModel.getCls(metaClsName), pattern);
        }
    }
    
    @SuppressWarnings("deprecation")
    public static Slot getCommonBrowserSlot(OWLModel owlModel) {
        Slot candidateSlot = null;
        for (String metaClsName : META_SLOT_NAMES) {
            Cls cls = owlModel.getCls(metaClsName);
            BrowserSlotPattern pattern  = cls.getBrowserSlotPattern();
            if (pattern == null) { 
                return null;
            }
            List<Slot> slots = pattern.getSlots();
            if (slots == null || slots.size() != 1) {
                return null;
            }
            Slot slot = slots.iterator().next();
            
            if (candidateSlot == null) {
                candidateSlot = slot;
            }
            else if (!candidateSlot.equals(slot)) {
                return null;
            }
        }
        return candidateSlot;
    }
    
    @SuppressWarnings("deprecation")
    public static Slot getDefaultBrowserSlot(OWLModel  owlModel) {
        String slotName = ApplicationProperties.getString(DEFAULT_BROWSER_SLOT_PROP);
        if (slotName == null) {
            return null;
        }
        return owlModel.getSlot(slotName);
    }
    
    public static void setDefaultBrowserSlot(OWLModel owlModel, Slot defaultSlot) {
        if (defaultSlot == null) {
            ApplicationProperties.setString(DEFAULT_BROWSER_SLOT_PROP, null);
        }
        else {
            ApplicationProperties.setString(DEFAULT_BROWSER_SLOT_PROP, defaultSlot.getName());
        }
    }
    
    /* ****************************************************************
     * Internal Methods
     */
    
    private JComponent createCenterPanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
    	
    	JLabel lbl0 = new JLabel("Render owl entities with: ");
    	panel.add(lbl0);
    	lbl0.setAlignmentX(Component.LEFT_ALIGNMENT);

    	panel.add(Box.createRigidArea(new Dimension(0, 5)));

    	renderingPropertyBox = makePropertyComboBox(getCommonBrowserSlot(owlModel));
    	renderingPropertyBox.setPreferredSize(new Dimension(250, ComponentFactory.STANDARD_FIELD_HEIGHT));
    	renderingPropertyBox.setMaximumSize(new Dimension(250, ComponentFactory.STANDARD_FIELD_HEIGHT));
    	panel.add(renderingPropertyBox);
    	renderingPropertyBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        renderingPropertyBox.addActionListener(new SetRendererActionListener());
    	
    	panel.add(Box.createRigidArea(new Dimension(0, 10)));
    	
    	JLabel lbl1 = new JLabel("For new OWL files, render with: ");
    	panel.add(lbl1);
    	lbl1.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	panel.add(Box.createRigidArea(new Dimension(0, 5)));

    	defaultRenderingPropertyBox = makePropertyComboBox(getDefaultBrowserSlot(owlModel));
    	defaultRenderingPropertyBox.setPreferredSize(new Dimension(250, ComponentFactory.STANDARD_FIELD_HEIGHT));
    	defaultRenderingPropertyBox.setMaximumSize(new Dimension(250, ComponentFactory.STANDARD_FIELD_HEIGHT));
    	panel.add(defaultRenderingPropertyBox);
    	defaultRenderingPropertyBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        defaultRenderingPropertyBox.addActionListener(new SetDefaultRendererActionListener());
    	
        return panel;
    }
    
    @SuppressWarnings("deprecation")
    private JComboBox makePropertyComboBox(Slot defaultSlot) {
        TreeSet<RDFProperty> properties = new TreeSet(owlModel.getOWLAnnotationProperties());
        properties.remove(owlModel.getRDFSLabelProperty());
        Slot[] propertyArray = new Slot[properties.size() + 2];
        int counter = 0;
        propertyArray[counter++] = owlModel.getNameSlot();
        propertyArray[counter++] = owlModel.getRDFSLabelProperty();
        for (Slot p : properties)  {
            propertyArray[counter++] = p;
        }

        JComboBox combo = new JComboBox(propertyArray);
        combo.setRenderer(new ComboBoxRenderer());
        if (defaultSlot != null && 
                (properties.contains(defaultSlot) ||
                        defaultSlot.equals(owlModel.getNameSlot()) ||
                        defaultSlot.equals(owlModel.getRDFSLabelProperty()))) {
            combo.setSelectedItem(defaultSlot);
        }
        else {
            combo.setSelectedIndex(-1);
        }
        return combo;
    }
    
    /* ****************************************************************
     * Inner Classes
     */
    private class ComboBoxRenderer extends ResourceRenderer {
        private static final long serialVersionUID = -964343376627678218L;

        @Override
        protected void loadSlot(Slot slot) {
            if (slot.equals(owlModel.getSystemFrames().getNameSlot())) {
                addText("rdf:id");
            }
            else {
                super.loadSlot(slot);
            }
        }  
    }
    
    private class SetRendererActionListener implements ActionListener {
        @SuppressWarnings("deprecation")
        public void actionPerformed(ActionEvent e) {
            Slot slot = (Slot) renderingPropertyBox.getSelectedItem();
            setCommonBrowserSlot(owlModel, slot);
            requiresReloadUI = true;
        }
    }
    
    private class SetDefaultRendererActionListener implements ActionListener  {
        public void actionPerformed(ActionEvent e) {
            Slot slot = (Slot) defaultRenderingPropertyBox.getSelectedItem();
            setDefaultBrowserSlot(owlModel, slot);
        }
    }
}
