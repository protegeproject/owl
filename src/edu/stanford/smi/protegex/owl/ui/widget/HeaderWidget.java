package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.components.annotations.AnnotationsComponent;
import edu.stanford.smi.protegex.owl.ui.components.triples.AbstractTriplesComponent;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The SlotWidget showing up as default on the top of all forms.
 * This contains two "tabs", for annotation property values and some other triples.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class HeaderWidget extends AbstractSlotWidget {

    private AbstractTriplesComponent component;
    private AbstractAction switchToTriplesAction;
    private AbstractAction switchToAnnotationsAction;

    public void activate(AbstractTriplesComponent newComponent) {
    	boolean isEnabled = true;
    	
        if (component != null) {
        	isEnabled = component.isEnabled();
            removeAll();
            ComponentUtilities.dispose(component);
        }

        component = newComponent;
        component.setSubject((RDFResource) getInstance());
        add(BorderLayout.CENTER, component);
        
        component.setEnabled(isEnabled);
        revalidate();
    }


    public void activateAnnotationsComponent() {
        RDFProperty property = (RDFProperty) getSlot();
        activate(new AnnotationsComponent(property) {
            protected void addButtons(LabeledComponent lc) {
                super.addButtons(lc);
                lc.addHeaderSeparator();
                lc.addHeaderSeparator();
                switchToTriplesAction = new AbstractAction("Switch to Triples", OWLIcons.getImageIcon(OWLIcons.TRIPLES)) {
                    public void actionPerformed(ActionEvent e) {
                        activateTriplesComponent();
                    }
                };
                
                lc.addHeaderButton(switchToTriplesAction);
            }
        });
    }


    public void activateTriplesComponent() {    	
        RDFProperty property = (RDFProperty) getSlot();
        activate(new TriplesComponent(property) {
            protected void addButtons(LabeledComponent lc) {
                super.addButtons(lc);
                lc.addHeaderSeparator();
                lc.addHeaderSeparator();
                switchToAnnotationsAction = new AbstractAction("Switch to Annotations", OWLIcons.getImageIcon(OWLIcons.ANNOTATIONS_TABLE)) {
                    public void actionPerformed(ActionEvent e) {
                        activateAnnotationsComponent();
                    }
                };
                lc.addHeaderButton(switchToAnnotationsAction);
            }
        });
    }


    public void initialize() {
        setLayout(new BorderLayout());
        activateAnnotationsComponent();
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return OWLWidgetMapper.isSuitable(HeaderWidget.class, cls, slot);
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance instanceof RDFResource) {
            RDFResource resource = (RDFResource) newInstance;
            component.setSubject(resource);          
        }
    }
    
	@Override
	public void setEnabled(boolean enabled) {
		component.setEnabled(enabled);	
		super.setEnabled(enabled);
	}


	public AbstractTriplesComponent getComponent() {
		return component;
	}


	public AbstractAction getSwitchToTriplesAction() {
		return switchToTriplesAction;
	}


	public AbstractAction getSwitchToAnnotationsAction() {
		return switchToAnnotationsAction;
	}
}
