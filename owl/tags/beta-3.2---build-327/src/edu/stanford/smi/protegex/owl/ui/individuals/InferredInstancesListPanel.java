package edu.stanford.smi.protegex.owl.ui.individuals;

import edu.stanford.smi.protege.action.DeleteInstancesAction;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * A JPanel to display the inferred instances of a collection of classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InferredInstancesListPanel extends SelectableContainer {

    private AllowableAction deleteAction;

    private OWLLabeledComponent lc;

    private InstancesList list;

    private Collection types;


    public InferredInstancesListPanel() {
        list = new InstancesList(null);
        lc = new OWLLabeledComponent("Inferred Instances", new JScrollPane(list));
        deleteAction = new DeleteInstancesAction(this) {
            protected void onAfterDelete(Object o) {
                refill();
            }
        };
        deleteAction.putValue(Action.SMALL_ICON, OWLIcons.getDeleteIcon(OWLIcons.RDF_INDIVIDUAL));
        lc.addHeaderButton(deleteAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        setSelectable(list);
    }


    public void dispose() {
    }


    private void refill() {
        Set set = new HashSet();
        for (Iterator it = types.iterator(); it.hasNext();) {
            RDFSClass type = (RDFSClass) it.next();
            set.addAll(type.getInferredInstances(false));
        }
        ArrayList instances = new ArrayList(set);
        Collections.sort(instances, new FrameComparator());
        list.setListData(instances.toArray());
    }


    public void setTypes(Collection types) {
        this.types = types;
        refill();
    }
}
