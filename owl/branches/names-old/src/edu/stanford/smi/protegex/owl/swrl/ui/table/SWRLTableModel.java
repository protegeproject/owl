package edu.stanford.smi.protegex.owl.swrl.ui.table;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.owltable.SymbolTableModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTableModel extends AbstractTableModel implements Disposable, SymbolTableModel {

    public final static int COL_NAME = 0;

    public final static int COL_EXPRESSION = 1;

    public final static int COL_COUNT = 2;

    private ClassListener clsListener = new ClassAdapter() {

        public void instanceAdded(RDFSClass cls, RDFResource instance) {
            final SWRLImp newImp = (SWRLImp) instance;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    perhapsAdd(newImp);
                }
            });
        }


        public void instanceRemoved(RDFSClass cls, RDFResource instance) {
            perhapsRemove((SWRLImp) instance);
        }
    };


    private List imps = new ArrayList();

    private RDFResource rdfResource;

    private OWLModel owlModel;


    public SWRLTableModel(OWLModel owlModel) {
        this.owlModel = owlModel;
        SWRLFactory factory = new SWRLFactory(owlModel);
        imps.addAll(factory.getImps());
        sortImps();
        initClsListener();
    }


    public SWRLTableModel(RDFResource resource) {
        this.rdfResource = resource;
        owlModel = resource.getOWLModel();
        addReferencingImps(resource);
        sortImps();
        initClsListener();
    }


    private void addReferencingImps(RDFResource rdfResource) {
        OWLModel owlModel = rdfResource.getOWLModel();
        SWRLFactory factory = new SWRLFactory(owlModel);
        Collection allImps = factory.getImps();
        for (Iterator it = allImps.iterator(); it.hasNext();) {
            SWRLImp imp = (SWRLImp) it.next();
            if (isSuitable(imp)) {
                imps.add(imp);
            }
        }
    }


    public void dispose() {
        owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP).removeClassListener(clsListener);
    }


    public Class getColumnClass(int columnIndex) {
        return String.class;
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public String getColumnName(int column) {
        if (column == COL_NAME) {
            return "Name";
        }
        else if (column == COL_EXPRESSION) {
            return "Expression";
        }
        else {
            return null;
        }
    }


    public Icon getIcon(RDFResource resource) {
        return ProtegeUI.getIcon(resource);
    }


    public SWRLImp getImp(int row) {
        return (SWRLImp) imps.get(row);
    }


    public RDFProperty getPredicate(int row) {
        return null;
    }


    public RDFResource getRDFResource(int row) {
        return getImp(row);
    }


    public RDFResource getSubject() {
        return null;
    }


    private int getRowFor(SWRLImp imp) {
        final String impName = imp.getName();
        int i = 0;
        while (i < imps.size() && impName.compareToIgnoreCase(getImp(i).getName()) >= 0) {
            i++;
        }
        return i;
    }


    public int getSymbolColumnIndex() {
        return COL_EXPRESSION;
    }


    public int getRowCount() {
        return imps.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == getSymbolColumnIndex()) {
            return getImp(rowIndex).getBrowserText();
        }
        else if (columnIndex == COL_NAME) {
            return getImp(rowIndex).getName();
        }
        else {
            return null;
        }
    }


    public int indexOf(SWRLImp imp) {
        return imps.indexOf(imp);
    }


    private void initClsListener() {
        owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP).addClassListener(clsListener);
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == COL_NAME || columnIndex == COL_EXPRESSION) {
            SWRLImp imp = getImp(rowIndex);
            return imp.isEditable();
        }
        else {
            return false;
        }
    }


    private boolean isSuitable(SWRLImp imp) {
        if (rdfResource == null) {
            return true;
        }
        else {
            Set set = imp.getReferencedInstances();
            return set.contains(rdfResource);
        }
    }


    private void perhapsAdd(SWRLImp imp) {
        if (isSuitable(imp)) {
            int row = getRowFor(imp);
            imps.add(row, imp);
            fireTableRowsInserted(row, row);
        }
    }


    private void perhapsRemove(SWRLImp imp) {
        int row = imps.indexOf(imp);
        if (row >= 0) {
            imps.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SWRLImp imp = getImp(rowIndex);
        if (columnIndex == COL_EXPRESSION) {
            String text = (String) aValue;
            SWRLParser parser = new SWRLParser(owlModel);
            try {
                imp.setExpression(text);
                if (!isSuitable(imp)) {
                    ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, "The replacing rule no longer fits the selection\n" +
                            "criteria of this rules list, and will therefore no\n" +
                            "longer be visible here.  But no reason to panic: It\n" +
                            "should still show up on the SWRL tab.");
                }
            }
            catch (Exception ex) {
            }
        }
        else if (columnIndex == COL_NAME) {
            String newName = (String) aValue;
            if (owlModel.isValidResourceName(newName, imp)) {
                RDFResource resource = owlModel.getRDFResource(newName);
                if (resource != null) {
                    if (!imp.equals(resource)) {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                "The name " + newName + " is already used in this ontology.");
                    }
                }
                else {
                    throw new UnsupportedOperationException("imp.setName(newName)");
                }
            }
            else {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                        newName + " is not a valid rule name.");
            }
        }
    }


    public void setRowOf(SWRLImp imp, int index) {
        int oldIndex = imps.indexOf(imp);
        imps.remove(oldIndex);
        fireTableRowsDeleted(oldIndex, oldIndex);
        imps.add(index, imp);
        fireTableRowsInserted(index, index);
    }


    private void sortImps() {
        Collections.sort(imps, new Comparator() {
            public int compare(Object o1, Object o2) {
                SWRLImp a = (SWRLImp) o1;
                SWRLImp b = (SWRLImp) o2;
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }
}
