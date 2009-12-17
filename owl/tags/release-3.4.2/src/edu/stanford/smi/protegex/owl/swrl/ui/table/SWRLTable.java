
package edu.stanford.smi.protegex.owl.swrl.ui.table;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;

import edu.stanford.smi.protege.event.KnowledgeBaseAdapter;
import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgePluginManager;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLSymbolPanel;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.owltable.SymbolTable;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTable extends SymbolTable implements Disposable {

  public SWRLTable(SWRLTableModel tableModel, OWLModel owlModel) {
    super(tableModel, owlModel, true, new SWRLSymbolPanel(owlModel, true, true));
    TableColumn enabledColumn = getColumnModel().getColumn(SWRLTableModel.COL_ENABLED);
    TableColumn nameColumn = getColumnModel().getColumn(SWRLTableModel.COL_NAME);
    TableColumn expressionColumn = getColumnModel().getColumn(SWRLTableModel.COL_EXPRESSION);
    enabledColumn.setMaxWidth(60);
    nameColumn.setPreferredWidth(100);
    expressionColumn.setPreferredWidth(700);
  }

  protected SymbolEditorComponent createSymbolEditorComponent(OWLModel model, SymbolErrorDisplay errorDisplay) 
  {
    return new SWRLSymbolEditor(model, errorDisplay);
    }
    public void dispose() {
        SWRLTableModel tableModel = (SWRLTableModel) getSymbolTableModel();
        tableModel.dispose();
    }

    protected String editMultiLine(RDFResource input) {
        return null;  // TODO
    }

    protected Icon getDefaultCellEditorIcon(RDFResource RDFResource) {
        return SWRLIcons.getImpIcon();
    }


    protected Collection getNavigationMenuItems(RDFResource RDFResource) {
        SWRLImp imp = (SWRLImp) RDFResource;
        Set set = imp.getReferencedInstances();
        Collection result = new ArrayList();
        for (Iterator it = set.iterator(); it.hasNext();) {
          RDFResource resource = (RDFResource)it.next();
          if (!(resource instanceof SWRLIndividual)) {
            result.add(resource);
          }
        }
        return result;
    }


    public SWRLImp getSelectedImp() {
        int row = getSelectedRow();
        if (row >= 0 && row < getModel().getRowCount()) {
            return (SWRLImp) getSymbolTableModel().getRDFResource(row);
        }
        else {
            return null;
        }
    }


    protected String getToolTipText(RDFResource rdfResource) {
        if (rdfResource instanceof SWRLImp) {
            RDFProperty commentSlot = getOWLModel().getRDFSCommentProperty();
	    Object value = rdfResource.getPropertyValue(commentSlot);
	    // A comment is stored as a String if no language is specified, as an RDFSLiteral otherwise.
	    if (value instanceof String) return (String)value;
	    else if (value instanceof RDFSLiteral) return ((RDFSLiteral)value).toString();
	    else return null; // Should not happen
        }
        else {
            return null;
        }
    }

    public void valueChanged(ListSelectionEvent e)
    {
      int selectedRow = getSelectedRow();
      int selectedColumn = getSelectedColumn();
      SWRLTableModel tableModel = (SWRLTableModel)getSymbolTableModel();

      super.valueChanged(e);

      if (selectedRow != -1) {
        String selectedRuleName = (String)tableModel.getValueAt(selectedRow, SWRLTableModel.COL_NAME);
        BridgePluginManager.setSelectedRuleName(selectedRuleName);
      } // if

    } // valueChanged

      public void replaceImp(SWRLImp oldImp, SWRLImp newImp) {
        SWRLTableModel tableModel = (SWRLTableModel) getSymbolTableModel();
        int index = tableModel.indexOf(oldImp);
        if (tableModel.indexOf(newImp) >= 0) {
            tableModel.setRowOf(newImp, index);
            setSelectedRow(index);
        }
        oldImp.deleteImp();
    }

  public void setSelectedRow(RDFResource RDFResource) {
    SWRLTableModel tableModel = (SWRLTableModel) getSymbolTableModel();
    int index = tableModel.indexOf((SWRLImp) RDFResource);
    setSelectedRow(index);
  }

  protected JPopupMenu createPopupMenu() 
  {
    JPopupMenu popup = super.createPopupMenu();
    
    if (popup == null) popup = new JPopupMenu();
    popup.add(new EnableAllRulesAction());
    popup.add(new DisableAllRulesAction());

    return popup;
  } // createPopupMenu

  private class EnableAllRulesAction extends AbstractAction
  {
    public EnableAllRulesAction() { super("Enable all rules"); }

    public void actionPerformed(ActionEvent e)
    {
      ((SWRLTableModel)getSymbolTableModel()).enableAll();
    } // actionPerformed
  } // EnableAllRulesAction

  private class DisableAllRulesAction extends AbstractAction
  {
    public DisableAllRulesAction() { super("Disable all rules"); }

    public void actionPerformed(ActionEvent e)
    {
      ((SWRLTableModel)getSymbolTableModel()).disableAll();
    } // actionPerformed
  } // DisableAllRulesAction

} // SWRLTable
