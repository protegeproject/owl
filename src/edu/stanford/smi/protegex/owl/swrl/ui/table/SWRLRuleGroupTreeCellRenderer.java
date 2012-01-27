
package org.protege.swrltab.ui.table;

import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.protege.swrlapi.adapters.axioms.SWRLRuleAdapter;

public class SWRLRuleGroupTreeCellRenderer extends DefaultTreeCellRenderer
{
	public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel,
				                                                 boolean expanded, boolean leaf, int row, boolean focus) 
	{	
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
        
		if (value instanceof DefaultMutableTreeTableNode) {
			DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)value;
			if (node.getUserObject() instanceof SWRLRuleAdapter) {
				SWRLRuleAdapter rule = (SWRLRuleAdapter)node.getUserObject();
				setText(rule.getRuleText());
			} else if (node.getUserObject() instanceof SWRLRuleGroup) {
				SWRLRuleGroup ruleGroup = (SWRLRuleGroup)node.getUserObject();
				setText(ruleGroup.getGroupName());
			} 
			
			if (node.isLeaf()) {
				if (node.getParent() == tree.getModel().getRoot()) setIcon(getDefaultClosedIcon());
				else setIcon(getDefaultLeafIcon());
			} 
		}
        
		return this;
	} 
} 
