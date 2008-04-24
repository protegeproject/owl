package edu.stanford.smi.protegex.owl.ui.clsdesc;

import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.ui.owltable.AbstractOWLTableModel;

/**
 * A ClassDescriptionTableModel that displays all direct superclasses that are
 * not restrictions.  This implementation assumes that the edited class has
 * no equivalent classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertiesSuperclassesTableModel extends AbstractOWLTableModel implements ClassDescriptionTableModel {

    public PropertiesSuperclassesTableModel() {
        super(0);
    }


    public boolean addRow(RDFSClass aClass, int selectedRow) {
        getEditedCls().addSuperclass(aClass);
        return true;
    }


    protected int getInsertRowIndex(Cls cls) {
        if (cls instanceof RDFSNamedClass) {
            return 0;
        }
        else {
            return getRowCount();
        }
    }


    public RDFProperty getPredicate(int row) {
        return null;
    }


    public boolean isDeleteEnabledFor(RDFSClass cls) {
        return cls instanceof OWLAnonymousClass;
    }


    public boolean isRemoveEnabledFor(Cls cls) {
        return cls instanceof RDFSNamedClass &&
                getRowCount() > 1 &&
                getEditedCls().getNamedSuperclasses().size() > 1;
    }


    protected boolean isSuitable(Cls cls) {
        if (cls instanceof RDFSClass && !getEditedCls().equals(cls) && getEditedCls().isSubclassOf((RDFSClass) cls)) {
            if (!(cls instanceof OWLRestriction)) {
                if (cls instanceof OWLAnonymousClass || cls.isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }


    protected void setValueAt(int rowIndex, OWLModel owlModel, String parsableText) throws Exception {
        if (rowIndex >= getRowCount()) {
            return;
        }
        try {
            DefaultOWLNamedClass cls = (DefaultOWLNamedClass) getEditedCls();
            OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
            RDFSClass newClass = parser.parseClass(owlModel, parsableText);
            RDFSClass oldClass = getClass(rowIndex);
            final String newBrowserCls = newClass.getBrowserText();
            if (oldClass == null) {
                Slot directSuperclassesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES);
                if (cls.hasPropertyValueWithBrowserText(directSuperclassesSlot,
                        newBrowserCls)) {
                    displaySemanticError("The class " + newBrowserCls +
                            " is already a superclass of " + cls.getBrowserText() + ".");
                    if (newClass instanceof OWLAnonymousClass) {
                        newClass.delete();
                    }
                }
                else {
                    cls.addSuperclass(newClass);
                }
            }
            else if (oldClass.getBrowserText().equals(newBrowserCls)) {
                if (newClass instanceof OWLAnonymousClass) {
                    newClass.delete();
                }
            }
            else {
                cls.addSuperclass(newClass);
                cls.removeSuperclass(oldClass);
                if (!cls.hasNamedSuperclass()) {
                    cls.addSuperclass(owlModel.getOWLThingClass());
                }
            }
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
    }
}
