package edu.stanford.smi.protegex.owl.swrl.ui;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.FormWidget;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.ui.metadata.RDFSNamedClassMetadataWidget;

import java.awt.*;

/**
 * A Project Plugin that does some initialization after a SWRL project
 * has been loaded.  The idea is that SWRL support is activated iff
 * the ontology imports the SWRL namespace.  This triggers the installation
 * of a specific FrameFactory so that SWRL-specific API classes are used
 * automatically.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLProjectPlugin extends ProjectPluginAdapter {

    public static void adjustWidgets(Project project) {
        Cls impCls = project.getKnowledgeBase().getCls(SWRLNames.Cls.IMP);
        if (impCls != null) {
            ClsWidget w = project.getDesignTimeClsWidget(impCls);
            
            if (w == null)
            	return;
            
            Slot s1 = project.getKnowledgeBase().getSlot(SWRLNames.Slot.HEAD);
            if (s1 != null) {
            	w.replaceWidget(s1, null);
            }

            Slot s2 = project.getKnowledgeBase().getSlot(SWRLNames.Slot.BODY);
            if (s2 != null) {
            	w.replaceWidget(s2, null);
            }
            
            Slot s3 = project.getKnowledgeBase().getSlot(Model.Slot.NAME);
            if (s3 != null) {
            	w.replaceWidget(s3, RDFSNamedClassMetadataWidget.class.getName());           
            }
            
            w.relayout();
        }
    }


    public void afterLoad(Project p) {
        adjustWidgets(p);
    }


    public static boolean isSWRLImported(OWLModel owlModel) {
        return owlModel.getOWLJavaFactory() instanceof SWRLJavaFactory;
    }
    
    
}
