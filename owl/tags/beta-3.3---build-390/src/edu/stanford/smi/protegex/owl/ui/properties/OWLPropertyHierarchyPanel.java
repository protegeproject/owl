package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

import javax.swing.*;
import java.awt.*;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLPropertyHierarchyPanel extends JPanel {

	private OWLSubpropertyPane subpropertyPane;

	private OWLSuperpropertiesPanel superpropertiesPanel;

	public OWLPropertyHierarchyPanel(OWLSubpropertyPane subpropertyPane,
	                                     OWLSuperpropertiesPanel superpropertiesPanel) {
		this.subpropertyPane = subpropertyPane;
		this.superpropertiesPanel = superpropertiesPanel;
		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout(7, 7));
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		splitPane.setTopComponent(subpropertyPane);
		splitPane.setBottomComponent(superpropertiesPanel);
		splitPane.setBorder(null);
		add(splitPane);
		splitPane.setDividerLocation(600);
		subpropertyPane.addSelectionListener(new SelectionListener() {
			public void selectionChanged(SelectionEvent event) {
				RDFProperty property = (RDFProperty) CollectionUtilities.getFirstItem(subpropertyPane.getSelection());
				superpropertiesPanel.setProperty(property, null);
			}
		});
	}


	public OWLSubpropertyPane getSubpropertyPane() {
		return subpropertyPane;
	}


	public OWLSuperpropertiesPanel getSuperpropertiesPanel() {
		return superpropertiesPanel;
	}
}

