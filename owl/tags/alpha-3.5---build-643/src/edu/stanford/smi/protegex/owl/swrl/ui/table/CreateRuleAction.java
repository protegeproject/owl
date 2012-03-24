
package edu.stanford.smi.protegex.owl.swrl.ui.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLTextAreaPanel;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class CreateRuleAction extends AbstractAction
{
	private final OWLModel owlModel;
	private final SWRLTable table;
	private final SWRLFactory factory;

	public CreateRuleAction(SWRLTable table, OWLModel owlModel)
	{
		super("Create new rule...", OWLIcons.getCreateIcon(SWRLIcons.IMP, SWRLIcons.class));
		this.owlModel = owlModel;
		this.table = table;
		this.factory = new SWRLFactory(owlModel);
	}

	public void actionPerformed(ActionEvent e)
	{
		final SWRLImp newImp = this.factory.createImp();
		if (SWRLTextAreaPanel.showEditDialog(this.table, this.owlModel, newImp)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					CreateRuleAction.this.table.setSelectedRow(newImp);
				}
			});
		} else
			newImp.delete();
	}
}
