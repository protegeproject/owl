package edu.stanford.smi.protegex.owl.swrl.ui.tab;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.swrl.ui.SWRLProjectPlugin;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.swrl.ui.table.SWRLTablePanel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

/**
 * A tab widget displaying all SWRL rules in the current ontology.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTab extends AbstractTabWidget {

    private SWRLTablePanel panel;


    private void activateSWRL() {
        JenaOWLModel owlModel = (JenaOWLModel) getKnowledgeBase();
        try {
            owlModel.getNamespaceManager().setPrefix(new URI(SWRLNames.SWRL_NAMESPACE), SWRLNames.SWRL_PREFIX);
            owlModel.getNamespaceManager().setPrefix(new URI(SWRLNames.SWRLB_NAMESPACE), SWRLNames.SWRLB_PREFIX);

        	ProtegeOWLParser.addImport(owlModel, new URI(SWRLNames.SWRL_IMPORT));
            ProtegeOWLParser.addImport(owlModel, new URI(SWRLNames.SWRLB_IMPORT));
            owlModel.getDefaultOWLOntology().addImports(new URI(SWRLNames.SWRL_IMPORT));
            owlModel.getDefaultOWLOntology().addImports (new URI(SWRLNames.SWRLB_IMPORT));
   
            activateSWRLFactoryIfNecessary(owlModel);
            System.out.println(owlModel.getNamespaceManager().getPrefixes());
            
            SWRLProjectPlugin.adjustWidgets(getProject());
            owlModel.getTripleStoreModel().updateEditableResourceState();
            
            ProtegeUI.reloadUI(getProject());
        }
        catch (Exception ex) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "Could not activate SWRL support:\n" + ex +
                            ".\nYour project might be in an inconsistent state now.");
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
    }

    
    public void initialize() {
        setLabel("SWRL Rules");
        setIcon(SWRLIcons.getImpsIcon());
        setLayout(new GridLayout(2, 1));
        if (!(getKnowledgeBase() instanceof OWLModel)) {
        	add(BorderLayout.CENTER, new JLabel("This tab can only be used with OWL projects."));
        	return;
        }
    
        OWLModel owlModel = (OWLModel) getKnowledgeBase();

        if (!SWRLProjectPlugin.isSWRLImported(owlModel)) {
        	if (isSWRLImported(owlModel)) {
        		activateSWRLFactoryIfNecessary(owlModel);
        	} else {
                setLayout(new FlowLayout());
                add(new JLabel("Your ontology needs to import the SWRL ontology (" +
                        SWRLNames.SWRL_NAMESPACE + ")."));
                JButton activateButton = new JButton("Activate SWRL...");
                activateButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        activateSWRL();
                    }
                });
                add(activateButton);
                if (!(owlModel instanceof JenaOWLModel)) {
                    activateButton.setEnabled(false);
                }        		
        	}
        }
        
        //have to check again because the activateSWRLFactoryIfNecessary may have failed
        if (SWRLProjectPlugin.isSWRLImported(owlModel)) {
            panel = new SWRLTablePanel(owlModel, null, this);
    		add(panel);
        }        

    }
        
 

	private void activateSWRLFactoryIfNecessary(OWLModel owlModel) {	
		SWRLJavaFactory factory = new SWRLJavaFactory(owlModel);
		owlModel.setOWLJavaFactory(factory);
		if(owlModel instanceof JenaOWLModel) {
			OWLJavaFactoryUpdater.run((JenaOWLModel) owlModel);
		}	
	}

    private boolean isSWRLImported(OWLModel owlModel) {
    	boolean swrlFound = false;
    	boolean swrlbFound = false;
    	    	
    	Iterator iter = owlModel.getOWLOntologies().iterator();
    	
    	try {
			while (iter.hasNext() && !(swrlbFound && swrlFound)) {
				OWLOntology ont = (OWLOntology) iter.next();
				if (ont.getNamespace().equals(SWRLNames.SWRL_NAMESPACE))
					swrlFound = true;
				if (ont.getNamespace().equals(SWRLNames.SWRLB_NAMESPACE))
					swrlbFound = true;				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return swrlFound && swrlbFound;
	}

	public static boolean isSuitable(Project p, Collection errors) {
        if (p.getKnowledgeBase() instanceof OWLModel) {
            return true;
        }
        else {
            errors.add("This tab can only be used with OWL projects.");
            return false;
        }
    }

}
