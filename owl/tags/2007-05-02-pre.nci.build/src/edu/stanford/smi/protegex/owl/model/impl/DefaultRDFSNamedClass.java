package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultRDFSNamedClass extends AbstractRDFSClass implements RDFSNamedClass {


    public DefaultRDFSNamedClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultRDFSNamedClass() {
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof RDFSNamedClass) {
            return getName().equals(((RDFSNamedClass) object).getName());
        }
        return false;
    }


    public Collection getDependingClasses() {
        Collection result = new ArrayList();
        addAnonymousClses(result, getDisjointClasses());
        addAnonymousClses(result, getDirectSuperclasses());
        return null;
    }


    public Icon getIcon() {
        if (isMetaCls()) {
            return isEditable() ? OWLIcons.getMetaclassIcon() : OWLIcons.getReadOnlyClsIcon(OWLIcons.getImageIcon(OWLIcons.RDFS_METACLASS));
        }
        else {
            if (isEditable()) {
                return OWLIcons.getImageIcon(OWLIcons.RDFS_NAMED_CLASS);
            }
            else {
                return OWLIcons.getReadOnlyClsIcon(OWLIcons.getImageIcon(OWLIcons.RDFS_NAMED_CLASS));
            }
        }
    }


    public String getIconName() {
        return OWLIcons.RDFS_NAMED_CLASS;
    }


    public RDFResource createAnonymousInstance() {
        String name = getOWLModel().getNextAnonymousResourceName();
        return (RDFResource) createDirectInstance(name);
    }


    public RDFIndividual createRDFIndividual(String name) {
        return (RDFIndividual) createInstance(name);
    }


    public Set getAssociatedProperties() {
        Set set = new HashSet();
        Set domainlessProperties = new HashSet(getOWLModel().getDomainlessProperties());
        Collection unionDomainProperties = getUnionDomainProperties(true);
        Iterator ps = unionDomainProperties.iterator();
        while (ps.hasNext()) {
            RDFProperty property = (RDFProperty) ps.next();
            if (!domainlessProperties.contains(property) && property.isDomainDefined()) {
                set.add(property);
                Iterator subs = property.getSubproperties(true).iterator();
                while (subs.hasNext()) {
                    RDFProperty subproperty = (RDFProperty) subs.next();
                    if (unionDomainProperties.contains(subproperty)) {
                        set.add(subproperty);
                    }
                }
            }
        }
        return set;
    }


    public RDFSClass getFirstSuperclass() {
        Collection superclasses = getSuperclasses(false);
        if (superclasses.isEmpty()) {
            return null;
        }
        else {
            return (RDFSClass) superclasses.iterator().next();
        }
    }


    public boolean isFunctionalProperty(RDFProperty property) {
        return property.isFunctional();
    }


    public ImageIcon getImageIcon() {
        if (isMetaCls()) {
            return OWLIcons.getMetaclassIcon();
        }
        else {
            return OWLIcons.getImageIcon(OWLIcons.RDFS_NAMED_CLASS);
        }
    }


    public String getNestedBrowserText() {
        return getBrowserText();
    }


    public void getNestedNamedClasses(Set set) {
        set.add(this);
    }


    public Collection getUnionRangeClasses(RDFProperty property) {
        Set reached = new HashSet();
        List queue = new ArrayList();
        queue.add(this);
        while (!queue.isEmpty()) {
            RDFSNamedClass cls = (RDFSNamedClass) queue.get(0);
            queue.remove(0);
            reached.add(cls);
            for (Iterator it = cls.getSuperclasses(false).iterator(); it.hasNext();) {
                RDFSClass superclass = (RDFSClass) it.next();
                if (superclass instanceof OWLAllValuesFrom) {
                    Collection result = getUnionRangeClassesHelper((OWLAllValuesFrom) superclass, property);
                    if (result != null) {
                        return result;
                    }
                }
                else if (superclass instanceof OWLIntersectionClass) {
                    for (Iterator oit = ((OWLIntersectionClass) superclass).getOperands().iterator(); oit.hasNext();) {
                        RDFSClass operand = (AbstractRDFSClass) oit.next();
                        if (operand instanceof OWLAllValuesFrom) {
                            Collection result = getUnionRangeClassesHelper((OWLAllValuesFrom) operand, property);
                            if (result != null) {
                                return result;
                            }
                        }
                    }
                }
                else if (superclass instanceof RDFSNamedClass && !reached.contains(superclass)) {
                    queue.add(superclass);
                }
            }
        }
        return property.getUnionRangeClasses();
    }


    private Collection getUnionRangeClassesHelper(OWLAllValuesFrom allValuesFrom, RDFProperty property) {
        if (allValuesFrom.getOnProperty().equals(property)) {
            RDFResource a = allValuesFrom.getAllValuesFrom();
            if (a instanceof OWLUnionClass) {
                return ((OWLUnionClass) a).getOperands();
            }
            else if (a instanceof RDFSClass) {
                return Collections.singleton(a);
            }
        }
        return null;
    }


    public boolean isAnonymous() {
        return false;
    }


    public boolean isVisibleFromOWLThing() {
        if (!isVisible()) {
            return false;
        }
        Set reached = new HashSet();
        return isVisibleFromRootCls(reached);
    }


    private boolean isVisibleFromRootCls(Set reached) {
        if (isVisible() && !reached.contains(this)) {
            reached.add(this);
            for (Iterator it = getDirectSuperclasses().iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                if (superCls.equals(getKnowledgeBase().getRootCls())) {
                    return true;
                }
                else if (superCls instanceof DefaultRDFSNamedClass) {
                    DefaultRDFSNamedClass rdfsSuperCls = (DefaultRDFSNamedClass) superCls;
                    if (rdfsSuperCls.isVisibleFromRootCls(reached)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Deprecatable -----------------------------------------------------------


    public boolean isDeprecated() {
        RDFSClass c = getOWLModel().getOWLDeprecatedClassClass();
        return getProtegeTypes().contains(c);
    }


    public void setDeprecated(boolean value) {
        if (isDeprecated() != value) {
            RDFSClass c = getOWLModel().getRDFSNamedClass(OWLNames.Cls.DEPRECATED_CLASS);
            if (value) {
                addProtegeType(c);
            }
            else {
                removeProtegeType(c);
            }
        }
    }

    // Visitor ---------------------------------------------------------------


    public void accept(OWLModelVisitor visitor) {
        visitor.visitRDFSNamedClass(this);
    }
}
