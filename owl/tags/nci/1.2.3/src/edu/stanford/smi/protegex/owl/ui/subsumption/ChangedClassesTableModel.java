package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * A TableModel containing all classes that are either inconsistent or
 * where the asserted subclasses differ from the computed subclasses.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ChangedClassesTableModel extends AbstractTableModel implements Disposable {

    public final static int COL_CLS = 0;

    public final static int COL_TEXT = 1;

    public final static int COL_COUNT = 2;

    private List items = new ArrayList();

    private Hashtable itemsByCls = new Hashtable();

    private ModelListener modelListener = new ModelAdapter() {
        public void classDeleted(RDFSClass cls) {
            refill();
        }
    };

    private OWLModel owlModel;


    public ChangedClassesTableModel(OWLModel owlModel) {
        this.owlModel = owlModel;
        refill();
        owlModel.addModelListener(modelListener);
    }


    void assertChange(OWLNamedClass cls) {
        ChangedClassItem changedClassItem = getOrCreateItem(cls);
        assertChange(changedClassItem);
    }


    private void assertChange(ChangedClassItem item) {
        item.assertChange();
        int index = this.items.indexOf(item);
        this.items.remove(index);
        itemsByCls.remove(item.getCls());
        fireTableRowsDeleted(index, index);
    }


    void assertChanges(int[] rows) {
        Collection is = new ArrayList();
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            ChangedClassItem item = (ChangedClassItem) items.get(row);
            OWLNamedClass cls = item.getCls();
            if (!OWLUtil.isInconsistent(cls) && cls.isEditable()) {
                is.add(item);
            }
        }
        assertChanges(is);
    }


    private void assertChanges(Collection items) {
    	try {
            owlModel.beginTransaction("Assert classification changes");
            for (Iterator it = items.iterator(); it.hasNext();) {
                ChangedClassItem item = (ChangedClassItem) it.next();
                assertChange(item);
            }
            owlModel.commitTransaction();			
		} catch (Exception e) {
			owlModel.rollbackTransaction();
			// TODO: check whether you need to rethrow exception
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw(re);
		}
    }


    public boolean contains(Cls cls) {
        return itemsByCls.containsKey(cls);
    }


    public void dispose() {
        owlModel.removeModelListener(modelListener);
    }


    private void fillItems(Collection assertedSubClses, Collection computedSubClses, Cls cls) {
        removeDuplicates(assertedSubClses, computedSubClses);
        for (Iterator ait = assertedSubClses.iterator(); ait.hasNext();) {
            OWLNamedClass namedCls = (OWLNamedClass) ait.next();
            if (!namedCls.isMetaclass()) {
                ChangedClassItem item = getOrCreateItem(namedCls);
                item.addRemovedSuperCls(cls);
            }
        }
        for (Iterator ait = computedSubClses.iterator(); ait.hasNext();) {
            OWLNamedClass namedCls = (OWLNamedClass) ait.next();
            if (!namedCls.isMetaclass()) {
                ChangedClassItem item = getOrCreateItem(namedCls);
                item.addAddedSuperCls(cls);
            }
        }
    }


    public String getChangeText(Cls cls) {
        ChangedClassItem item = (ChangedClassItem) itemsByCls.get(cls);
        if (item != null) {
            return item.toString();
        }
        else {
            return null;
        }
    }


    public Cls getCls(int row) {
        return (Cls) getValueAt(row, COL_CLS);
    }


    public Class getColumnClass(int columnIndex) {
        if (columnIndex == COL_CLS) {
            return Frame.class;
        }
        else {
            return String.class;
        }
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public String getColumnName(int column) {
        if (column == COL_CLS) {
            return "Class";
        }
        else {
            return "Changed direct superclasses";
        }
    }


    private ChangedClassItem getOrCreateItem(OWLNamedClass cls) {
        ChangedClassItem item = (ChangedClassItem) itemsByCls.get(cls);
        if (item == null) {
            item = new ChangedClassItem(cls);
            items.add(item);
            itemsByCls.put(cls, item);
        }
        return item;
    }


    public int getRowCount() {
        return items.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        ChangedClassItem item = (ChangedClassItem) items.get(rowIndex);
        if (columnIndex == COL_CLS) {
            return item.getCls();
        }
        else {
            return item.toString();
        }
    }


    public void refill() {

        items = new ArrayList();
        itemsByCls = new Hashtable();

        for (Iterator it = owlModel.getChangedInferredClasses().iterator(); it.hasNext();) {
            OWLNamedClass cls = (OWLNamedClass) it.next();
            ChangedClassItem item = getOrCreateItem(cls);
            Collection asserted = new ArrayList(cls.getNamedSuperclasses());
            Collection inferred = new ArrayList(cls.getInferredSuperclasses());
            removeDuplicates(asserted, inferred);
            for (Iterator ait = asserted.iterator(); ait.hasNext();) {
                RDFSNamedClass superCls = (RDFSNamedClass) ait.next();
                if (!superCls.isMetaclass()) {
                    item.addRemovedSuperCls(superCls);
                }
            }
            for (Iterator ait = inferred.iterator(); ait.hasNext();) {
                RDFSNamedClass superCls = (RDFSNamedClass) ait.next();
                if (!superCls.isMetaclass()) {
                    item.addAddedSuperCls(superCls);
                }
            }
        }

        for (Iterator it = owlModel.getInconsistentClasses().iterator(); it.hasNext();) {
            OWLNamedClass cls = (OWLNamedClass) it.next();
            getOrCreateItem(cls);
        }

        Collections.sort(items);
        fireTableDataChanged();
    }


    private void removeDuplicates(Collection a, Collection b) {
        Collection copyA = new ArrayList(a);
        a.removeAll(b);
        b.removeAll(copyA);
    }
}
