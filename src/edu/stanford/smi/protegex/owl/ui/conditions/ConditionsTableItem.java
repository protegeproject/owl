package edu.stanford.smi.protegex.owl.ui.conditions;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * An object representing an entry in the table.  One instance is kept for
 * each row.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ConditionsTableItem implements ConditionsTableConstants, Comparable {


    RDFSClass aClass;

    private OWLIntersectionClass definition;

    private boolean isNew;

    /**
     * The index of this row inside its block.  This is 0 for the first row
     * below each separator
     */
    private int localIndex;

    private OWLNamedClass originCls;

    private int type;

    public static final String INHERITED = "INHERITED ";

    public static final String NECESSARY = "NECESSARY ";

    public static final String SUFFICIENT = "NECESSARY & SUFFICIENT ";


    private ConditionsTableItem(RDFSClass aClass,
                                int type,
                                OWLNamedClass originCls,
                                OWLIntersectionClass definition,
                                boolean isNew) {
        this.aClass = aClass;
        this.definition = definition;
        this.type = type;
        this.originCls = originCls;
        this.isNew = isNew;
    }

    public void dispose() {
        this.aClass = null;
        this.definition = null;        
        this.originCls = null;        
    }
    
    /**
     * Sorts according to the following order:
     * 1) Equivalent classes > superclasses > inherited superclasses
     * 2) NamedClasses by name
     * 3) Anonymous classes > restrictions
     * 4) Restrictions by slot name
     *
     * @param o the other ClassDescriptionItem to compare to
     * @return -1 if this is smaller (higher in the list), 1 if the other is smaller
     */
    public int compareTo(Object o) {
        if (o instanceof ConditionsTableItem) {
            ConditionsTableItem other = (ConditionsTableItem) o;
            if (type > other.type) {
                return -1;
            }
            else if (type < other.type) {
                return 1;
            }
            else {   // type == other.type
                if (other.isSeparator()) {
                    return 1;
                }
                if (isSeparator()) {
                    return -1;
                }
                else {
                    return compareToWithSameType(other.aClass);
                }
            }
        }
        else {
            return 0;
        }
    }


    int compareToWithSameType(RDFSClass otherClass) {
        if (aClass instanceof OWLNamedClass) {
            if (otherClass instanceof OWLNamedClass) {
                return aClass.compareTo(otherClass);
            }
            else {
                return -1;
            }
        }
        else if (otherClass instanceof OWLNamedClass) {
            return 1;
        }
        else {
            return compareToWithAnonymousClses((OWLAnonymousClass) otherClass);
        }
    }


    private int compareToWithAnonymousClses(OWLAnonymousClass otherCls) {
        if (aClass instanceof OWLRestriction) {
            if (otherCls instanceof OWLRestriction) {
                return compareToWithRestrictions((OWLRestriction) otherCls);
            }
            else {
                return 1;
            }
        }
        else {
            return aClass.compareTo(otherCls);
        }
    }


    private int compareToWithRestrictions(OWLRestriction otherCls) {
        OWLRestriction restriction = (OWLRestriction) aClass;
        RDFProperty property = restriction.getOnProperty();
        RDFProperty otherProperty = otherCls.getOnProperty();
        if (property.equals(otherProperty)) {
            final OWLModel owlModel = aClass.getOWLModel();
            List metaClses = Arrays.asList(owlModel.getOWLRestrictionMetaclasses());
            int clsIndex = metaClses.indexOf(restriction.getProtegeType());
            int otherIndex = metaClses.indexOf(otherCls.getProtegeType());
            return new Integer(clsIndex).compareTo(new Integer(otherIndex));
        }
        else {
            return property.compareTo(otherProperty);
        }
    }


    static ConditionsTableItem create(RDFSClass aClass, int type) {
        return new ConditionsTableItem(aClass, type, null, null, false);
    }


    static ConditionsTableItem createInherited(RDFSClass aClass, OWLNamedClass originCls) {
        return new ConditionsTableItem(aClass, TYPE_INHERITED, originCls, null, false);
    }


    static ConditionsTableItem createNew(int type) {
        return new ConditionsTableItem(null, type, null, null, true);
    }


    static ConditionsTableItem createSufficient(RDFSClass aClass, int type, OWLIntersectionClass definition) {
        return new ConditionsTableItem(aClass, type, null, definition, false);
    }


    static ConditionsTableItem createSeparator(int type) {
        return new ConditionsTableItem(null, type, null, null, false);
    }


    RDFSClass getCls() {
        return aClass;
    }


    OWLIntersectionClass getDefinition() {
        return definition;
    }


    Icon getIcon(int rowHeight) {
        if (isSeparator()) {
            return null;
        }
        else {
            if (isDefinition()) {
                return null; // return new DefinitionIcon(localIndex, rowHeight);
                //OWLIcons.getImageIcon("EquivalentClass");
            }
            else if (type == TYPE_INHERITED) {
                return OWLIcons.getImageIcon("SuperclassInherited");
            }
            else if (type == TYPE_SUPERCLASS) {
                return getSuperclassIcon();
            }
        }
        return null;
    }


    OWLNamedClass getOriginCls() {
        return originCls;
    }


    static Icon getSuperclassIcon() {
        return OWLIcons.getImageIcon("Superclass");
    }


    int getType() {
        return type;
    }


    boolean isDefinition() {
        return type >= TYPE_DEFINITION_BASE;
    }


    boolean isInherited() {
        return type == TYPE_INHERITED;
    }


    boolean isNew() {
        return isNew;
    }


    boolean isSeparator() {
        return aClass == null && !isNew();
    }


    void setLocalIndex(int value) {
        this.localIndex = value;
    }


    void setType(int value) {
        this.type = value;
    }


    public String toString() {
        if (isSeparator()) {
            if (isDefinition()) {
                return SUFFICIENT;
            }
            else if (isInherited()) {
                return INHERITED;
            }
            else {
                return NECESSARY;
            }
        }
        else {
            if (isInherited()) {
                return getDisplayText() + "    [" + originCls.getBrowserText() + "]";
            }
            else if (isNew()) {
                return "";
            }
            else {
                return getDisplayText();
            }
        }
    }


    private String getDisplayText() {
        return aClass.getBrowserText();
    }
}
