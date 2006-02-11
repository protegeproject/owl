package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.ProtegeOWL;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.*;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLPropertyHierarchiesPanel extends JPanel implements Selectable  {

	private OWLModel owlModel;

	private JTabbedPane tabbedPane;

	private OWLPropertyHierarchyPanel objectPropertyHierarchy;

	private OWLPropertyHierarchyPanel datatypePropertyHierarchy;

	private OWLPropertyHierarchyPanel annotationPropertyHierarchy;


	ArrayList listeners;

	public OWLPropertyHierarchiesPanel(OWLModel owlModel) {
		this.owlModel = owlModel;
		listeners = new ArrayList();
		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.add("Object", objectPropertyHierarchy = createPanel(new OWLObjectPropertySubpropertyPane(owlModel), "OWLObjectProperty"));
		tabbedPane.add("Datatype", datatypePropertyHierarchy = createPanel(new OWLDatatypePropertySubpropertyPane(owlModel), "OWLDatatypeProperty"));
		tabbedPane.add("Annotation",annotationPropertyHierarchy = createPanel(new OWLAnnotationPropertySubpropertyPane(owlModel), "OWLDatatypeProperty"));
		tabbedPane.add("All", createPanel(new OWLSubpropertyPane(owlModel), "OWLObjectProperty"));
		add(tabbedPane);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(isShowing()) {
					notifySelectionListeners();
				}
			}
		});
		JLabel label = ComponentFactory.createLabel(Icons.getProjectIcon());
        label.setText(owlModel.getProject().getName());
        String forProjectLabel = LocalizedText.getText(ResourceKey.CLASS_BROWSER_FOR_PROJECT_LABEL);
        HeaderComponent header = new HeaderComponent("Property Browser", forProjectLabel, label);
        header.setColor(Colors.getSlotColor());
		add(header, BorderLayout.NORTH);
	}
	
	private OWLPropertyHierarchyPanel createPanel(OWLSubpropertyPane subpropertyPane, String iconBase) {
		subpropertyPane.addSelectionListener(new SelectionListener() {
			public void selectionChanged(SelectionEvent event) {
				notifySelectionListeners();
			}
		});
		OWLSuperpropertiesPanel superpropertiesPanel = new OWLSuperpropertiesPanel(subpropertyPane, owlModel);
		superpropertiesPanel.setAddActionIconBase(iconBase);
		superpropertiesPanel.setRemoveActionIconBase(iconBase);
	    return new OWLPropertyHierarchyPanel(subpropertyPane, superpropertiesPanel);
	}


	public Collection getSelection() {
		OWLPropertyHierarchyPanel panel = ((OWLPropertyHierarchyPanel) tabbedPane.getSelectedComponent());
		if(panel != null) {
			return panel.getSubpropertyPane().getSelection();
		}
		else {
			return Collections.EMPTY_LIST;
		}
	}


	public void addSelectionListener(SelectionListener selectionListener) {
		listeners.add(selectionListener);
	}


	public void clearSelection() {
	}


	public void notifySelectionListeners() {
		for(Iterator it = new ArrayList(listeners).iterator(); it.hasNext(); ) {
			SelectionListener curListener = (SelectionListener) it.next();
			final SelectionEvent event = new SelectionEvent(this, SelectionEvent.SELECTION_CHANGED);
			curListener.selectionChanged(event);
		}
	}


	public void removeSelectionListener(SelectionListener selectionListener) {
		listeners.remove(selectionListener);
	}


	public static void main(String [] args) {
		try {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
			owlModel.createOWLObjectProperty("A");
			OWLPropertyHierarchiesPanel panel = new OWLPropertyHierarchiesPanel(owlModel);
			JFrame f = new JFrame();
			f.setSize(300, 700);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setContentPane(panel);
			f.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setSelectedProperty(RDFProperty property) {
		if(property.isAnnotationProperty()) {
			annotationPropertyHierarchy.getSubpropertyPane().setSelectedProperty(property);
		}
		else if(property instanceof OWLObjectProperty) {
			objectPropertyHierarchy.getSubpropertyPane().setSelectedProperty(property);
		}
		else {
			datatypePropertyHierarchy.getSubpropertyPane().setSelectedProperty(property);
		}

	}


}

