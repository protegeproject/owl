package edu.stanford.smi.protegex.owl.swrl.ui.table;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLSymbolPanel;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.owltable.SymbolTable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTable extends SymbolTable implements Disposable {


    public SWRLTable(SWRLTableModel tableModel, OWLModel owlModel) {
        super(tableModel, owlModel, true, new SWRLSymbolPanel(owlModel, true, true));
        TableColumn nameColumn = getColumnModel().getColumn(SWRLTableModel.COL_NAME);
        TableColumn expressionColumn = getColumnModel().getColumn(SWRLTableModel.COL_EXPRESSION);
        nameColumn.setPreferredWidth(150);
        expressionColumn.setPreferredWidth(700);
        setDefaultRenderer(SWRLImp.class, new ResourceRenderer());
    }


    protected SymbolEditorComponent createSymbolEditorComponent(OWLModel model,
                                                                SymbolErrorDisplay errorDisplay) {
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
            Instance instance = (Instance) it.next();
            if (!(instance instanceof SWRLIndividual)) {
                result.add(instance);
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
            return (String) rdfResource.getPropertyValue(commentSlot);
        }
        else {
            return null;
        }
    }


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
}
