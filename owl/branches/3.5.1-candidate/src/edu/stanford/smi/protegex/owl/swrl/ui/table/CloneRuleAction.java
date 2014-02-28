
package edu.stanford.smi.protegex.owl.swrl.ui.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class CloneRuleAction extends AbstractAction
{
	private final SWRLTable table;

	public CloneRuleAction(SWRLTable table, @SuppressWarnings("unused") OWLModel owlModel)
	{
		super("Clone selected rule", OWLIcons.getAddIcon(SWRLIcons.IMP, SWRLIcons.class));
		this.table = table;
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e)
			{
				updateEnabled();
			}
		});
		updateEnabled();
	}

	public void actionPerformed(ActionEvent e)
	{
		SWRLImp imp = this.table.getSelectedImp();
		final SWRLImp c = imp.createClone();
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				CloneRuleAction.this.table.setSelectedRow(c);
			}
		});
	}

	private void updateEnabled()
	{
		setEnabled(this.table.getSelectedRowCount() == 1);
	}
}
