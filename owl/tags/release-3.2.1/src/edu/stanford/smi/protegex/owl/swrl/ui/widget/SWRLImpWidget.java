package edu.stanford.smi.protegex.owl.swrl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

/**
 * A SlotWidget to edit the expression of a SWRLImp.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLImpWidget extends AbstractSlotWidget {

    public void initialize() {
        // TODO...
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        Cls impCls = cls.getKnowledgeBase().getCls(SWRLNames.Cls.IMP);
        return slot instanceof RDFProperty &&
                slot.getValueType() == ValueType.INSTANCE &&
                impCls != null &&
                slot.getAllowedValues().contains(impCls) &&
                !slot.getAllowsMultipleValues();
    }
}
