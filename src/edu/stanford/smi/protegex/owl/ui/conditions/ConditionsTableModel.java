package edu.stanford.smi.protegex.owl.ui.conditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLCardinalityBase;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.model.impl.AbstractRDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIntersectionClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * The TableModel used by the AssertedConditionsWidget.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ConditionsTableModel extends AbstractTableModel
        implements ConditionsTableConstants, OWLTableModel {
    private static transient Logger log = Log.getLogger(ConditionsTableModel.class);

    private ClassListener classListener = new ClassAdapter() {
        public void subclassAdded(RDFSClass cls, RDFSClass subclass) {
            refill();
        }


        public void subclassRemoved(RDFSClass cls, RDFSClass subclass) {
            refill();
        }


        public void superclassAdded(RDFSClass cls, RDFSClass superclass) {
            refill();
        }


        public void superclassRemoved(RDFSClass cls, RDFSClass superclass) {
            refill();
        }
    };


    private FrameListener frameListener = new FrameAdapter() {
        public void ownSlotValueChanged(FrameEvent event) {
            if (event.getSlot().equals(superclassesSlot)) {
                refill();
            }
        }
    };

    /**
     * The edited class
     */
    private OWLNamedClass hostClass;

    /**
     * One Item object for each row
     */
    private List items = new ArrayList();

    private Collection listenedToClses = new ArrayList();

    private OWLModel owlModel;

    /**
     * Needed to select the most recently edited row after closing the expression editor
     */
    public Cls previouslyEditedCls;

    private Slot superclassesSlot;


    /**
     * Constructs a new ConditionsTableModel with default superslot.
     *
     * @param owlModel the OWLModel
     */
    public ConditionsTableModel(OWLModel owlModel) {
        this(((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES));
    }


    /**
     * Constructs a new ConditionsTableModel with customized superslot.
     *
     * @param superclassesSlot the Slot (either inferred or asserted superclasses)
     */
    public ConditionsTableModel(Slot superclassesSlot) {
        this.superclassesSlot = superclassesSlot;
        this.owlModel = (OWLModel) superclassesSlot.getKnowledgeBase();
    }


    /**
     * Constructs a new ConditionsTableModel for a given Cls.
     *
     * @param hostCls          the initially displayed class
     * @param superclassesSlot the Slot (either inferred or asserted superclasses)
     */
    public ConditionsTableModel(OWLNamedClass hostCls, Slot superclassesSlot) {
        this(superclassesSlot);
        setCls(hostCls);
    }


    public int addEmptyDefinitionBlock() {
        int type = getType(0) + 1;
        final ConditionsTableItem separator = ConditionsTableItem.createSeparator(type);
        int row = 0;
        items.add(row, separator);
        fireTableRowsInserted(row, row);
        return row;
    }


    public int addEmptyRow(int selectedRow) {
        /*if (selectedRow == 0 && !isSeparator(1)) {   // Add new Definition block
            int newType = getType(0) + 1;
            addItem(0, ConditionsTableItem.createSeparator(newType));
            addItem(1, ConditionsTableItem.createNew(newType));
            fireTableRowsInserted(0, 1);
            return 1;
        }
        else {  */
        ConditionsTableItem item = ConditionsTableItem.createNew(getType(selectedRow));
        int index = selectedRow + 1;
        addItem(index, item);
        fireTableRowsInserted(index, index);
        return index;
        // }
    }


    private void addItem(int index, ConditionsTableItem item) {
        items.add(index, item);
        updateLocalIndices();
    }


    /**
     * Adds a given Item, unless it represents a restriction that has been overloaded
     * in the existing entries.  A restriction is overloaded iff a restriction of the
     * same type (OWLAllValuesFrom, or a cardinality restriction), and restricted slot
     * already exists in the items list.  With AllRestrictions the filler must be
     * a subclass of the filler above.
     *
     * @param aClass    the aClassass to add as an item
     * @param originCls the class where aClassass has been defined
     */
    private void addItemUnlessOverloaded(RDFSClass aClass, OWLNamedClass originCls) {
        if (aClass instanceof OWLRestriction) {
            RDFSClass directType = aClass.getProtegeType();
            RDFProperty property = ((OWLRestriction) aClass).getOnProperty();
            if (aClass instanceof OWLHasValue) {
                String browserText = aClass.getBrowserText();
                for (Iterator it = items.iterator(); it.hasNext();) {
                    ConditionsTableItem existing = (ConditionsTableItem) it.next();
                    if (!existing.isSeparator() && browserText.equals(existing.aClass.getBrowserText())) {
                        return;  // Don't add if entry with same browser text exists
                    }
                }
            }
            else if (aClass instanceof OWLSomeValuesFrom) {
                final OWLSomeValuesFrom someRestriction = ((OWLSomeValuesFrom) aClass);
                if (someRestriction.getFiller() instanceof RDFSClass) {
                    RDFSClass someClass = (RDFSClass) someRestriction.getFiller();
                    String browserText = aClass.getBrowserText();
                    for (Iterator it = items.iterator(); it.hasNext();) {
                        ConditionsTableItem existing = (ConditionsTableItem) it.next();
                        if (!existing.isSeparator() && existing.aClass instanceof OWLSomeValuesFrom) {
                            if (browserText.equals(existing.aClass.getBrowserText())) {
                                return;  // Don't add if entry with same browser text exists
                            }
                            OWLSomeValuesFrom other = (OWLSomeValuesFrom) existing.aClass;
                            if (other.getOnProperty().equals(property)) {
                                if (other.getFiller() instanceof RDFSClass) {
                                    RDFSClass otherSomeClass = (RDFSClass) other.getFiller();
                                    if (otherSomeClass.isSubclassOf(someClass)) {
                                        return;  // Don't add if OWLSomeValuesFrom with a subclass exists
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (aClass instanceof OWLAllValuesFrom && ((OWLAllValuesFrom) aClass).getFiller() instanceof RDFSClass) {
                OWLAllValuesFrom newRestriction = (OWLAllValuesFrom) aClass;
                OWLNamedClass newSourceClass = newRestriction.getOwner();
                for (Iterator it = items.iterator(); it.hasNext();) {
                    ConditionsTableItem existing = (ConditionsTableItem) it.next();
                    if (!existing.isSeparator() && directType.equals(existing.aClass.getProtegeType()) &&
                            property.equals(((OWLRestriction) existing.aClass).getOnProperty())) {
                        OWLAllValuesFrom existingRestriction = (OWLAllValuesFrom) existing.aClass;
                        OWLNamedClass existingSourceCls = existingRestriction.getOwner();
                        if (((RDFSClass) existingRestriction.getFiller()).isSubclassOf((RDFSClass) newRestriction.getFiller()) &&
                                !existingSourceCls.equals(newSourceClass)) {
                            return;
                        }
                    }
                }
            }
            else {
                boolean qcr = false;
                if (aClass instanceof OWLCardinalityBase) {
                    OWLCardinalityBase base = (OWLCardinalityBase) aClass;
                    qcr = base.isQualified();
                }
                if (!qcr) {
                    for (Iterator it = items.iterator(); it.hasNext();) {
                        ConditionsTableItem existing = (ConditionsTableItem) it.next();
                        if (!existing.isSeparator() && directType.equals(existing.aClass.getProtegeType()) &&
                                property.equals(((OWLRestriction) existing.aClass).getOnProperty())) {
                            return;  // Don't add if entry with same type exists
                        }
                    }
                }
            }
        }
        addInheritedSeparator();
        items.add(ConditionsTableItem.createInherited(aClass, originCls));
    }


    private void addInheritedSeparator() {
        for (Iterator it = items.iterator(); it.hasNext();) {
            ConditionsTableItem item = (ConditionsTableItem) it.next();
            if (item.isSeparator() && item.getType() == TYPE_INHERITED) {
                return;
            }
        }
        items.add(ConditionsTableItem.createSeparator(TYPE_INHERITED));
    }


    /**
     * Adds a given Cls to the class conditions specified by a given row.
     *
     * @param aClass      the aClassass to add
     * @param selectedRow the row to add to
     * @return true if the row has been added
     */
    public boolean addRow(RDFSClass aClass, int selectedRow) {
        String browserText = aClass.getBrowserText();
        if (isDefinition(selectedRow) ||
                !((AbstractRDFSClass) hostClass).hasPropertyValueWithBrowserText(((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES), browserText)) {
            return addRowAllowMove(aClass, selectedRow);
        }
        else {
            return false;
        }
    }


    public boolean addRowAllowMove(RDFSClass aClass, int selectedRow) {
        if (aClass.equals(hostClass) ||
                (!isCreateEnabledAt(selectedRow) && aClass instanceof OWLAnonymousClass) ||
                (!isAddEnabledAt(selectedRow) && aClass instanceof OWLNamedClass)) {
            return false;
        }

        int type = getType(selectedRow);
        if (type == TYPE_SUPERCLASS) {
            hostClass.addSuperclass(aClass);
        }
        else if (isDefinition(selectedRow)) { // Definition
            if (aClass.equals(owlModel.getOWLThingClass())) {
                return false;  // Don't allow to add owl:Thing to any definition
            }
            if (isSeparator(selectedRow)) {
                selectedRow++;
            }
            DefaultOWLIntersectionClass definition = (DefaultOWLIntersectionClass) getDefinition(selectedRow);
            if (definition != null) {
                String browserText = aClass.getBrowserText();
                if (definition.hasOperandWithBrowserText(browserText)) {
                    return false;
                }
                OWLIntersectionClass copy = (OWLIntersectionClass) definition.createClone();
                copy.addOperand(aClass);
                hostClass.addEquivalentClass(copy);  // Was swapped with line below
                definition.delete();                 // Was swapped with line above
            }
            else {
                RDFSClass oldEquivalentClass = (RDFSClass) getClass(selectedRow);
                if (oldEquivalentClass != null) {
                    if (oldEquivalentClass.getBrowserText().equals(aClass.getBrowserText())) {
                        return false;
                    }
                    OWLModel owlModel = aClass.getOWLModel();
                    OWLIntersectionClass newDefinition = owlModel.createOWLIntersectionClass();
                    newDefinition.addOperand(oldEquivalentClass.createClone());
                    newDefinition.addOperand(aClass);
                    hostClass.removeSuperclass(oldEquivalentClass);
                    if (oldEquivalentClass instanceof OWLNamedClass) {
                        oldEquivalentClass.removeSuperclass(hostClass);
                    }
                    hostClass.addEquivalentClass(newDefinition);
                }
                else {
                    hostClass.addEquivalentClass(aClass);
                }
            }
        }
        return true;
    }


    private boolean deleteDefinitionRow(int index) {
        RDFSClass rowClass = (RDFSClass) getClass(index);
        if (isDefinition(index)) {
            OWLIntersectionClass definition = getDefinition(index);
            if (definition != null) {
                Collection operands = new ArrayList(definition.getOperands());
                if (operands.size() == 2) {
                    operands.remove(rowClass);
                    RDFSClass remainder = (RDFSClass) operands.iterator().next();
                    RDFSClass copy = remainder.createClone();
                    hostClass.addEquivalentClass(copy);
                    definition.delete();
                }
                else {
                    OWLModel owlModel = rowClass.getOWLModel();
                    operands.remove(rowClass);
                    OWLIntersectionClass newDefinition = owlModel.createOWLIntersectionClass();
                    for (Iterator it = operands.iterator(); it.hasNext();) {
                        RDFSClass oldOperand = (RDFSClass) it.next();
                        newDefinition.addOperand(oldOperand.createClone());
                    }
                    hostClass.addEquivalentClass(newDefinition);
                    definition.delete();
                }
                if (!hostClass.hasNamedSuperclass()) {
                    hostClass.addSuperclass(owlModel.getOWLThingClass());
                }
                return true;
            }
        }
        return false;
    }


    public void deleteRow(int index) {
        try {
            owlModel.beginTransaction("Delete condition " + getClass(index).getBrowserText() +
                    " from " + getEditedCls().getBrowserText());
            deleteRow(index, false);
        }
        catch (Exception ex) {
            OWLUI.handleError(owlModel, ex);
        }
        finally {
            owlModel.endTransaction();
        }
    }


    public void deleteRow(int index, boolean forceDelete) {
        RDFSClass rowClass = (RDFSClass) getClass(index);
        boolean definition = getDefinition(index) != null;
        boolean isDefinition = isDefinition(index);
        boolean deleted = deleteDefinitionRow(index);
        if (!deleted && isDefinition && rowClass instanceof OWLNamedClass && forceDelete) {
            rowClass.removeSuperclass(hostClass);
            rowClass.removeSuperclass(hostClass);
        }
        else if (hostClass.isSubclassOf(rowClass)) {
            if (!getNamedDefinitionClses(definition).contains(rowClass) || forceDelete) {
                if (rowClass instanceof RDFSNamedClass) {
                    int namedSuperclassCount = hostClass.getNamedSuperclasses().size();
                    if (namedSuperclassCount == 1) {
                        hostClass.addSuperclass(owlModel.getOWLThingClass());
                    }
                    if (!rowClass.equals(owlModel.getOWLThingClass())) {
                        hostClass.removeSuperclass(rowClass);
                    }
                    else {  // owl:Thing deleted as superclass
                        Collection nss = new HashSet(hostClass.getNamedSuperclasses());
                        nss.remove(rowClass);
                        if (nss.size() > 0) {
                            hostClass.removeSuperclass(rowClass);
                        }
                    }
                }
                else {
                    hostClass.removeSuperclass(rowClass);
                }
            }
        }
    }


    public void displaySemanticError(String message) {
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel, message);
    }


    public void dispose() {
        if (hostClass != null) {
            hostClass.removeClassListener(classListener);
            ((Cls) hostClass).removeFrameListener(frameListener);
        }
        removeListeners();
    }


    // Debugging support only
    public void dumpItems() {
        System.err.println("Items:");
        for (Iterator it = items.iterator(); it.hasNext();) {
            ConditionsTableItem tableItem = (ConditionsTableItem) it.next();
            System.err.println(" - " + tableItem + " (" + tableItem.getType() + ")");
        }
    }


    private void fillItems() {
        Collection coveredClses = new HashSet();
        final int classificationStatus = getEditedCls().getClassificationStatus();
        if (superclassesSlot.getName().equals(Model.Slot.DIRECT_SUPERCLASSES) ||
                classificationStatus != OWLNames.CLASSIFICATION_STATUS_UNDEFINED) {
            fillDefinitionItems(coveredClses);
            fillDirectSuperclassItems(coveredClses);
            fillInheritedItems(coveredClses);
            sortItems();
        }
    }


    private void fillDefinitionItems(Collection coveredClses) {
        Slot slot = ((KnowledgeBase) hostClass.getOWLModel()).getSlot(Model.Slot.DIRECT_SUPERCLASSES);
        int index = TYPE_DEFINITION_BASE;
        final ConditionsTableItem firstSeparator = ConditionsTableItem.createSeparator(index);
        items.add(firstSeparator);
        List separators = new ArrayList();
        separators.add(firstSeparator);
        boolean first = true;
        for (Iterator it = ((Cls) hostClass).getDirectOwnSlotValues(slot).iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof RDFSClass && superCls.getDirectOwnSlotValues(slot).contains(hostClass)) {
                if (!first) {
                    final ConditionsTableItem separator = ConditionsTableItem.createSeparator(index);
                    items.add(separator);
                    separators.add(separator);
                }
                first = false;
                RDFSClass equivalentClass = (RDFSClass) superCls;
                coveredClses.add(equivalentClass);
                if (equivalentClass instanceof OWLIntersectionClass) {
                    OWLIntersectionClass intersectionCls = (OWLIntersectionClass) equivalentClass;
                    Collection operands = ((OWLIntersectionClass) equivalentClass).getOperands();
                    for (Iterator oit = operands.iterator(); oit.hasNext();) {
                        RDFSClass operand = (RDFSClass) oit.next();
                        coveredClses.add(operand);
                        items.add(ConditionsTableItem.createSufficient(operand, index, intersectionCls));
                    }
                }
                else {
                    items.add(ConditionsTableItem.create(equivalentClass, index));
                }
                index++;
            }
        }
        if (separators.size() > 1) {
            sortItems();
            sortSufficientBlocks(separators);
        }
    }


    private void fillDirectSuperclassItems(Collection coveredClses) {
        items.add(ConditionsTableItem.createSeparator(TYPE_SUPERCLASS));
        for (Iterator it = ((Cls) hostClass).getDirectOwnSlotValues(superclassesSlot).iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof RDFSNamedClass && !coveredClses.contains(superCls)) {
                RDFSClass aClass = (RDFSClass) superCls;
                coveredClses.add(aClass);
                items.add(ConditionsTableItem.create(aClass, TYPE_SUPERCLASS));
            }
        }
        for (Iterator it = hostClass.getSuperclasses(false).iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof OWLAnonymousClass && !coveredClses.contains(superCls)) {
                RDFSClass aClass = (RDFSClass) superCls;
                coveredClses.add(aClass);
                items.add(ConditionsTableItem.create(aClass, TYPE_SUPERCLASS));
            }
        }
    }


    private void fillInheritedItems(Collection coveredClses) {
        coveredClses.removeAll(getNamedDefinitionClses(false));
        for (Iterator it = ((Cls) hostClass).getDirectOwnSlotValues(superclassesSlot).iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof OWLNamedClass && !superCls.equals(hostClass)) {
                OWLNamedClass namedSuperclass = (OWLNamedClass) superCls;
                fillInheritedItems(namedSuperclass, coveredClses);
                listenedToClses.add(superCls);
                namedSuperclass.addClassListener(classListener);
                ((Cls) namedSuperclass).addFrameListener(frameListener);
            }
        }
    }


    private void fillInheritedItems(OWLNamedClass originCls, Collection coveredClses) {
        fillInheritedAnonymousClses(originCls, coveredClses);
        for (Iterator it = ((Cls) originCls).getDirectOwnSlotValues(superclassesSlot).iterator(); it.hasNext();) {
            Cls ss = (Cls) it.next();
            if (ss instanceof OWLNamedClass && !coveredClses.contains(ss)) {
                coveredClses.add(ss);
                fillInheritedItems((OWLNamedClass) ss, coveredClses);
            }
        }
    }


    private void fillInheritedAnonymousClses(OWLNamedClass originCls, Collection coveredClses) {
        for (Iterator it = originCls.getSuperclasses(false).iterator(); it.hasNext();) {
            Cls ss = (Cls) it.next();
            if (ss instanceof OWLAnonymousClass) {
                if (ss instanceof OWLIntersectionClass) {
                    Collection operands = ((OWLIntersectionClass) ss).getOperands();
                    for (Iterator oit = operands.iterator(); oit.hasNext();) {
                        RDFSClass operand = (RDFSClass) oit.next();
                        if (operand instanceof OWLAnonymousClass) {
                            addItemUnlessOverloaded(operand, originCls);
                        }
                    }
                }
                else if (!coveredClses.contains(ss)) {
                    addItemUnlessOverloaded((OWLAnonymousClass) ss, originCls);
                }
            }
        }
    }


    public int getBlockSize(int blockStartRow) {
        int count = 0;
        int type = getType(blockStartRow - 1);
        for (int i = blockStartRow; i < items.size() && getItem(i).getType() == type; i++) {
            count++;
        }
        return count;
    }


    /**
     * Gets a string of the form <browserText> [& <recursion>] from all
     * rows of a given block.
     *
     * @param type the type
     * @return the string
     */
    public String getBlockText(int type) {
        int i = 0;
        while (isSeparator(i) || getType(i) != type) {
            i++;
        }
        String str = getClass(i++).getBrowserText();
        while (i < getRowCount() && getType(i) == type) {
            str += " & " + getClass(i++).getBrowserText();
        }
        return str;
    }


    public int getClassRow(RDFSClass cls) {
        if (cls != null) {
            int row = 0;
            for (Iterator it = items.iterator(); it.hasNext(); row++) {
                ConditionsTableItem item = (ConditionsTableItem) it.next();
                if (item != null && cls.equals(item.aClass)) {
                    return row;
                }
            }
        }
        return -1;
    }


    /**
     * Gets the RDFSClass displayed at a given row.
     *
     * @param row the row to get the Class from
     * @return the RDFSClass there
     */
    public RDFSClass getClass(int row) {
        ConditionsTableItem item = getItem(row);
        if (item != null) {
            return item.aClass;
        }
        else {
            return null;
        }
    }


    // Implements TableModel
    public int getColumnCount() {
        return COL_COUNT;
    }


    // Implements TableModel
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == COL_EXPRESSION) {
            return String.class;
        }
        return null;
    }


    /**
     * If a given row displays an operand from an equivalent class intersection,
     * then this method returns the OWLIntersectionClass hosting it.
     *
     * @param rowIndex the index of the row to get the OWLIntersectionClass from
     * @return an OWLIntersectionClass or null
     */
    public OWLIntersectionClass getDefinition(int rowIndex) {
        return getItem(rowIndex).getDefinition();
    }


    public OWLNamedClass getEditedCls() {
        return hostClass;
    }


    public Icon getIcon(RDFResource resource) {
        return ProtegeUI.getIcon(resource);
    }


    public Icon getIcon(int rowIndex, int rowHeight) {
        return getItem(rowIndex).getIcon(rowHeight);
    }


    private ConditionsTableItem getItem(int rowIndex) {
        return (ConditionsTableItem) items.get(rowIndex);
    }


    private Collection getNamedDefinitionClses(boolean allowDirectSuperclasses) {
        Collection result = new HashSet();
        for (Iterator it = hostClass.getEquivalentClasses().iterator(); it.hasNext();) {
            RDFSClass aClass = (RDFSClass) it.next();
            if (aClass instanceof OWLIntersectionClass) {
                for (Iterator oit = ((OWLIntersectionClass) aClass).getOperands().iterator(); oit.hasNext();) {
                    Cls operand = (Cls) oit.next();
                    if (operand instanceof OWLNamedClass) {
                        result.add(operand);
                    }
                }
            }
            else if (aClass instanceof OWLNamedClass && allowDirectSuperclasses) {
                result.add(aClass);
            }
        }
        return result;
    }


    /**
     * Gets the class where the entry from a given row has been inherited from.
     * This only makes sense for rows which have the type TYPE_INHERITED.
     *
     * @param rowIndex the index of the row to query
     * @return the origin class
     */
    public OWLNamedClass getOriginClass(int rowIndex) {
        return getItem(rowIndex).getOriginCls();
    }


    public RDFProperty getPredicate(int row) {
        if (row == 0 || (row >= 0 && isDefinition(row))) {
            return owlModel.getOWLEquivalentClassProperty();
        }
        return owlModel.getRDFSSubClassOfProperty();
    }


    public RDFResource getRDFResource(int row) {
        return getClass(row);
    }


    public RDFResource getSubject() {
        return hostClass;
    }


    public int getSymbolColumnIndex() {
        return COL_EXPRESSION;
    }


    // Implements TableModel
    public int getRowCount() {
        return items.size();
    }


    /**
     * Gets the type of the entry at a certain row.
     *
     * @param rowIndex the index of the row to get the type of
     * @return one of ConditionsTableColumns.TYPE_xxx
     */
    public int getType(int rowIndex) {
        ConditionsTableItem item = getItem(rowIndex);
        if (item != null) {
            return item.getType();
        }
        else {
            return TYPE_SUPERCLASS;
        }
    }


    // Implements TableModel
    public Object getValueAt(int rowIndex, int columnIndex) {
        ConditionsTableItem item = getItem(rowIndex);
        if (columnIndex == COL_EXPRESSION) {
            return item.toString();
        }
        else {
            return null;
        }
    }


    private void handleDuplicateEntry(RDFSClass newClass) {
        displaySemanticError("The class " + newClass.getBrowserText() + " is already in the list.");
        if (newClass instanceof OWLAnonymousClass) {
            newClass.delete();
        }
    }


    /**
     * Checks whether the "add named class" function is enabled for a given row.
     *
     * @param rowIndex the index of the row where a named class shall be added
     * @return true if a named class could be added
     */
    public boolean isAddEnabledAt(int rowIndex) {
        if (!owlModel.getOWLThingClass().equals(getEditedCls())) {
            int type = getType(rowIndex);
            if (type == TYPE_SUPERCLASS) {
                return true;
            }
            else if (type != TYPE_INHERITED) {  // Other defined class
                OWLIntersectionClass ic = getDefinition(rowIndex);
                if (ic != null) {
                    return ic.isEditable();
                }
                else {
                    RDFSClass c = getClass(rowIndex);
                    if (c == null) {
                        return isAddEnabledAt(rowIndex + 1);
                    }
                    else {
                        return owlModel.getTripleStoreModel().isActiveTriple(getEditedCls(),
                                owlModel.getOWLEquivalentClassProperty(),
                                c);
                    }
                }
            }
        }
        return false;
    }


    // Implements TableModel
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == COL_EXPRESSION &&
                !isSeparator(rowIndex) &&
                !getItem(rowIndex).isInherited() &&
                superclassesSlot.getName().equals(Model.Slot.DIRECT_SUPERCLASSES)) {
            Cls cls = getClass(rowIndex);
            if (cls == null) {
                return true;
            }
            if (isCreateEnabledAt(rowIndex)) { //getEditedCls().isEditable()) {
                if (cls instanceof OWLAnonymousClass) {
                    return cls.isEditable();
                }
                else {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks whether an anonymous class could be added/created at a given row.
     *
     * @param row the index of the row to add a new anonymous class
     * @return true if yes
     */
    public boolean isCreateEnabledAt(int row) {
        if (isDefinition(row)) {
            OWLIntersectionClass ic = getDefinition(row);
            if (ic != null) {
                return ic.isEditable();
            }
            else {
                RDFSClass cls = getClass(row);
                if (cls == null) {
                    return isCreateEnabledAt(row + 1);
                }
                else {
                    return owlModel.getTripleStoreModel().isActiveTriple(getEditedCls(),
                            owlModel.getOWLEquivalentClassProperty(),
                            cls);
                }
            }
        }
        else {
            return getType(row) == TYPE_SUPERCLASS;
        }
    }


    public boolean isDeleteEnabledFor(RDFSClass cls) {
        int rowIndex = getClassRow(cls);
        if (cls instanceof OWLAnonymousClass) {
            if (!isSeparator(rowIndex)) {
                return isCellEditable(rowIndex, 0);
            }
            else {
                return false;
            }
        }
        else {
            return isRemoveEnabledFor(rowIndex);
        }
    }


    public boolean isDefinition(int rowIndex) {
        return getItem(rowIndex).isDefinition();
    }


    public boolean isEditable() {
        return hostClass.isEditable();
    }


    public boolean isRemoveEnabledFor(int rowIndex) {
        if (!isSeparator(rowIndex)) {
            RDFSClass superClass = getClass(rowIndex);
            if (superClass instanceof RDFSNamedClass) {
                if (getItem(rowIndex).isDefinition()) {
                    OWLIntersectionClass ic = getDefinition(rowIndex);
                    if (ic != null) {
                        return ic.isEditable();
                    }
                }
                OWLNamedClass editedClass = getEditedCls();
                if (owlModel.getTripleStoreModel().isEditableTriple(editedClass,
                        owlModel.getRDFSSubClassOfProperty(),
                        superClass) ||
                        owlModel.getTripleStoreModel().isEditableTriple(editedClass,
                                owlModel.getOWLEquivalentClassProperty(),
                                superClass)) {
                    Collection nss = new HashSet(hostClass.getNamedSuperclasses());
                    if (nss.contains(superClass)) {
                        nss.remove(superClass);
                        return nss.size() > 0 ||
                                !owlModel.getOWLThingClass().equals(superClass);
                    }
                }
            }
        }
        return false;
    }


    public boolean isSeparator(int rowIndex) {
        return getItem(rowIndex).isSeparator();
    }


    private void refill() {
        items.clear();
        removeListeners();
        fillItems();
        fireTableDataChanged();
    }


    public void refresh() {
        items.clear();
        if (hostClass != null) {
            fillItems();
            fireTableDataChanged();
        }
    }


    public void removeEmptyRow() {
        for (int i = getRowCount() - 1; i >= 0; i--) {
            if (getItem(i).isNew()) {
                items.remove(i);
                fireTableRowsDeleted(i, i);
            }
        }
        updateLocalIndices();
    }


    private void removeListeners() {
        for (Iterator it = listenedToClses.iterator(); it.hasNext();) {
            RDFSClass cls = (RDFSClass) it.next();
            cls.removeClassListener(classListener);
            ((Cls) cls).removeFrameListener(frameListener);
        }
        listenedToClses.clear();
    }


    private void replaceItemType(int oldType, int newType) {
        for (Iterator it = items.iterator(); it.hasNext();) {
            ConditionsTableItem item = (ConditionsTableItem) it.next();
            if (item.getType() == oldType) {
                item.setType(newType);
            }
        }
    }


    public void setCls(OWLNamedClass cls) {
        if (hostClass == null || !hostClass.equals(cls)) {
            if (hostClass != null) {
                hostClass.removeClassListener(classListener);
                ((Cls) hostClass).removeFrameListener(frameListener);
            }
            hostClass = cls;
            if (hostClass != null) {
                hostClass.addClassListener(classListener);
                ((Cls) hostClass).addFrameListener(frameListener);
            }
            refresh();
        }
    }


    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount()) {
            return;
        }
        if (columnIndex == COL_EXPRESSION) {
            String text = (String) value;
            OWLModel owlModel = hostClass.getOWLModel();
            try {
                setValueAt(rowIndex, owlModel, text);
            }
            catch (Exception ex) {
              Log.emptyCatchBlock(ex);
            }
        }
    }


    public void setValueAt(int rowIndex, OWLModel owlModel, String parsableText)
            throws Exception {

        try {
            owlModel.beginTransaction("Set condition at " + getEditedCls().getBrowserText() + " to " + parsableText);
            OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
            RDFSClass newClass = parser.parseClass(owlModel, parsableText);
            if (getEditedCls().equals(newClass)) {
                return;
            }
            String newBrowserText = newClass.getBrowserText();
            RDFSClass oldClass = (RDFSClass) getClass(rowIndex);
            Slot directSuperclassesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES);
            if (oldClass == null) {
                if (((AbstractRDFSClass) hostClass).hasPropertyValueWithBrowserText(directSuperclassesSlot, newBrowserText)) {
                    handleDuplicateEntry(newClass);
                    return;
                }
                items.remove(rowIndex);
                previouslyEditedCls = newClass;
                if (!addRow(newClass, rowIndex - 1) && newClass instanceof OWLAnonymousClass) {
                    newClass.delete();
                }
            }
            else if (!oldClass.getBrowserText().equals(newBrowserText)) {
                if (((AbstractRDFSClass) hostClass).hasPropertyValueWithBrowserText(directSuperclassesSlot, newBrowserText)) {
                    handleDuplicateEntry(newClass);
                    return;
                }
                int type = getType(rowIndex);
                if (type == TYPE_SUPERCLASS) {
                    hostClass.addSuperclass(newClass);
                    if (oldClass != null) {
                        hostClass.removeSuperclass(oldClass);
                    }
                }
                else { // Definition
                    DefaultOWLIntersectionClass definition = (DefaultOWLIntersectionClass) getDefinition(rowIndex);
                    if (definition != null) {
                        if (definition.hasOperandWithBrowserText(newBrowserText)) {
                            handleDuplicateEntry(newClass);
                            return;
                        }
                        OWLIntersectionClass copy = owlModel.createOWLIntersectionClass();
                        for (Iterator it = definition.getOperands().iterator(); it.hasNext();) {
                            RDFSClass operand = (RDFSClass) it.next();
                            if (operand.equals(oldClass)) {
                                copy.addOperand(newClass);
                            }
                            else {
                                copy.addOperand(operand.createClone());
                            }
                        }
                        if (oldClass == null) {
                            copy.addOperand(newClass);
                        }
                        else if (oldClass instanceof RDFSNamedClass) {
                            hostClass.removeSuperclass(oldClass);
                        }
                        hostClass.addEquivalentClass(copy); // Changed 1
                        definition.delete();              // Changed 2
                        if (newClass instanceof RDFSNamedClass) {
                            hostClass.addSuperclass(newClass);
                        }
                    }
                    else {
                        hostClass.addEquivalentClass(newClass);
                        if (oldClass != null) {
                            hostClass.removeSuperclass(oldClass);
                            if (oldClass instanceof RDFSNamedClass && oldClass.isSubclassOf(hostClass)) {
                                oldClass.removeSuperclass(hostClass);
                                if (oldClass.getNamedSuperclasses().isEmpty()) {
                                    oldClass.addSuperclass(owlModel.getOWLThingClass());
                                }
                            }
                        }
                        if (newClass instanceof OWLIntersectionClass) {
                            for (Iterator it = ((OWLIntersectionClass) newClass).getOperands().iterator(); it.hasNext();) {
                                RDFSClass operand = (RDFSClass) it.next();
                                if (operand instanceof RDFSNamedClass && !hostClass.isSubclassOf(operand)) {
                                    hostClass.addSuperclass(operand);
                                }
                            }
                        }
                    }
                }
                if (!hostClass.hasNamedSuperclass()) {
                    hostClass.addSuperclass(owlModel.getOWLThingClass());
                }
                previouslyEditedCls = newClass;
            }
            else if (newClass instanceof OWLAnonymousClass) {
                newClass.delete();
            }
        }
        catch (Exception ex) {
            OWLUI.handleError(owlModel, ex);
        }
        finally {
            owlModel.endTransaction();
        }
    }


    /**
     * Sorts the items according to their <CODE>compareTo</CODE> method.
     *
     * @see ConditionsTableItem#compareTo
     */
    private void sortItems() {
        Collections.sort(items);
        updateLocalIndices();
    }


    /**
     * Assumes that the items list contains nothing but at least two non-
     * empty definition blocks.
     */
    private void sortSufficientBlocks(List separators) {
        boolean changed = false;
        do {
            changed = false;
            for (int i = 0; i < separators.size() - 1; i++) {
                ConditionsTableItem separatorA = (ConditionsTableItem) separators.get(i);
                int indexA = items.indexOf(separatorA);
                ConditionsTableItem a = getItem(indexA + 1);
                ConditionsTableItem separatorB = (ConditionsTableItem) separators.get(i + 1);
                int indexB = items.indexOf(separatorB);
                ConditionsTableItem b = getItem(indexB + 1);
                if (a.compareToWithSameType(b.getCls()) < 0) {
                    changed = true;
                    swapBlocks(separatorA, separatorB);
                    separators.remove(separatorB);
                    separators.add(i, separatorB);
                }
            }
        }
        while (changed);
    }


    private void swapBlocks(ConditionsTableItem separatorA, ConditionsTableItem separatorB) {
        int typeA = separatorA.getType();
        int typeB = separatorB.getType();
        replaceItemType(typeA, -10);
        replaceItemType(typeB, typeA);
        replaceItemType(-10, typeB);
        sortItems();
    }


    private void updateLocalIndices() {
        int row = 0;
        for (Iterator it = items.iterator(); it.hasNext(); row++) {
            ConditionsTableItem item = (ConditionsTableItem) it.next();
            if (item.isSeparator()) {
                updateLocalIndices(row + 1);
            }
        }
    }


    private void updateLocalIndices(int startRow) {
        for (int i = startRow; i < items.size() && !getItem(i).isSeparator(); i++) {
            getItem(i).setLocalIndex(i - startRow);
        }
    }
}
