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


    public void activate(AbstractTriplesComponent newComponent) {

        if (component != null) {
            removeAll();
            ComponentUtilities.dispose(component);
        }

        component = newComponent;
        component.setSubject((RDFResource) getInstance());
        add(BorderLayout.CENTER, component);
        revalidate();
    }


    public void activateAnnotationsComponent() {
        RDFProperty property = (RDFProperty) getSlot();
        activate(new AnnotationsComponent(property) {
            protected void addButtons(LabeledComponent lc) {
                super.addButtons(lc);
                lc.addHeaderSeparator();
                lc.addHeaderSeparator();
                lc.addHeaderButton(new AbstractAction("Switch to Triples", OWLIcons.getImageIcon(OWLIcons.TRIPLES)) {
                    public void actionPerformed(ActionEvent e) {
                        activateTriplesComponent();
                    }
                });
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
                lc.addHeaderButton(new AbstractAction("Switch to Annotations", OWLIcons.getImageIcon(OWLIcons.ANNOTATIONS_TABLE)) {
                    public void actionPerformed(ActionEvent e) {
                        activateAnnotationsComponent();
                    }
                });
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
}
