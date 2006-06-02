package edu.stanford.smi.protegex.owl.testing.sanity;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.testing.AbstractOWLTest;
import edu.stanford.smi.protegex.owl.testing.DefaultOWLTestResult;
import edu.stanford.smi.protegex.owl.testing.OWLTestResult;
import edu.stanford.smi.protegex.owl.testing.RDFPropertyTest;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch
 */
public class SubpropertyRangeMustNarrowTest extends AbstractOWLTest
        implements RDFPropertyTest {

    public SubpropertyRangeMustNarrowTest() {
        super(SANITY_GROUP, "Range of a subproperty can only narrow superproperty");
    }


    public static OWLObjectProperty fails(OWLObjectProperty slot) {
        final Collection superslots = slot.getSuperproperties(true);
        if (!superslots.isEmpty()) {
            Cls rootCls = slot.getOWLModel().getOWLThingClass();
            Collection range = slot.getUnionRangeClasses();
            if (range.isEmpty()) {
                range = Collections.singleton(rootCls);
            }
            for (Iterator it = superslots.iterator(); it.hasNext();) {
                Slot superSlot = (Slot) it.next();
                if (superSlot instanceof OWLObjectProperty) {
                    Collection superRange = superSlot.getAllowedClses();
                    if (superRange.isEmpty()) {
                        superRange = Collections.singleton(rootCls);
                    }
                    if (!isSubClasses(superRange, range)) {
                        return (OWLObjectProperty) superSlot;
                    }
                }
            }
        }
        return null;
    }


    // All classes from the sub range must have at least one superclass
    // in super range
    private static boolean isSubClasses(Collection superRange, Collection subRange) {
        for (Iterator it = subRange.iterator(); it.hasNext();) {
            Cls subCls = (Cls) it.next();
            if (subCls instanceof RDFSNamedClass) {
                boolean hasSuperCls = false;
                for (Iterator sit = superRange.iterator(); sit.hasNext();) {
                    Cls superCls = (Cls) sit.next();
                    if (superCls instanceof RDFSNamedClass) {
                        if (subCls.equals(superCls) || subCls.hasSuperclass(superCls)) {
                            hasSuperCls = true;
                            break;
                        }
                    }
                }
                if (!hasSuperCls) {
                    return false;
                }
            }
        }
        return true;
    }


    public List test(RDFProperty property) {
        if (property instanceof OWLObjectProperty) {
            OWLProperty failProperty = fails((OWLObjectProperty) property);
            if (failProperty != null) {
                return Collections.singletonList(new DefaultOWLTestResult("The range of " +
                        property.getBrowserText() + " is not a subset of the range of its superproperty " +
                        failProperty.getBrowserText() + ".",
                        property,
                        OWLTestResult.TYPE_WARNING,
                        this));
            }
        }
        return Collections.EMPTY_LIST;
    }
}
