package edu.stanford.smi.protegex.owl.swrl.ui.table;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.ui.actions.EditRuleAction;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;

import javax.swing.*;
import java.awt.*;

/**
 * A JPanel consisting of a SWRLTable and buttons to create
 * and delete rules.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTablePanel extends JPanel implements Disposable {

    private EditRuleAction editRuleAction;

    private SWRLTable table;

    private SWRLTableModel tableModel;


    public SWRLTablePanel(OWLModel owlModel, RDFResource RDFResource) {

        tableModel = RDFResource == null ?
                new SWRLTableModel(owlModel) :
                new SWRLTableModel(RDFResource);
        table = new SWRLTable(tableModel, owlModel);

        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());

        LabeledComponent lc = new OWLLabeledComponent("SWRL Rules", scrollPane);
        lc.addHeaderButton(new ViewRuleAction(table));
        lc.addHeaderButton(new CreateRuleAction(table, owlModel));
        lc.addHeaderButton(new CloneRuleAction(table, owlModel));
        lc.addHeaderButton(new DeleteRuleAction(table));

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    public void dispose() {
        table.dispose();
    }
}
