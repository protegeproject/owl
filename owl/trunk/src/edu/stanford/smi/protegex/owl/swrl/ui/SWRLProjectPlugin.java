package edu.stanford.smi.protegex.owl.swrl.ui;

import edu.stanford.smi.protege.event.WidgetEvent;
import edu.stanford.smi.protege.event.WidgetListener;
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
 * A Project Plugin that does some initialization after a SWRL project has been loaded.  The idea is that SWRL support is activated iff the
 * ontology imports the SWRL namespace.  This triggers the installation of a specific FrameFactory so that SWRL-specific API classes are
 * used automatically.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLProjectPlugin extends ProjectPluginAdapter 
{
  public static void adjustWidgets(Project project) 
  {
    Cls impCls = project.getKnowledgeBase().getCls(SWRLNames.Cls.IMP);
    if (impCls != null) {
      ClsWidget w = project.getDesignTimeClsWidget(impCls);
      WidgetDescriptor d = w.getDescriptor();
      WidgetDescriptor headWidget = d.getPropertyList().getWidgetDescriptor(SWRLNames.Slot.HEAD);
      if (headWidget != null) {
        headWidget.setBounds(new Rectangle(5, 5, 1, 1));
        headWidget.setVisible(false);
      }
      WidgetDescriptor bodyWidget = d.getPropertyList().getWidgetDescriptor(SWRLNames.Slot.BODY);
      if (bodyWidget != null) {
        bodyWidget.setBounds(new Rectangle(5, 5, 1, 1));
        bodyWidget.setVisible(false);
      }
      WidgetDescriptor nameWidget = d.getPropertyList().getWidgetDescriptor(Model.Slot.NAME);
      if (nameWidget != null) {
        nameWidget.setWidgetClassName(RDFSNamedClassMetadataWidget.class.getName());
      }
      ((FormWidget) w).setModified(true);
    }   
  }
  
  public void afterLoad(Project p) { adjustWidgets(p); }

  public static boolean isSWRLImported(OWLModel owlModel) { return owlModel.getOWLJavaFactory() instanceof SWRLJavaFactory; }
} // SWRLProjectPlugin
