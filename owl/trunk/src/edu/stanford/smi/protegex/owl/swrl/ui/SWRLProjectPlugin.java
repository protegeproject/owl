
package edu.stanford.smi.protegex.owl.swrl.ui;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.FormWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.swrl.ui.widget.SWRLRuleSlotWidget;
import edu.stanford.smi.protegex.owl.ui.metadata.NameDocumentationWidget;

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
  @SuppressWarnings("deprecation")
  public static void adjustWidgets(Project project) 
  {
    KnowledgeBase kb = project.getKnowledgeBase();
    
    Cls impCls = kb.getCls(SWRLNames.Cls.IMP);
    
    if (impCls == null) return;
    
    try {
      ClsWidget clsWidget = project.getDesignTimeClsWidget(impCls);   
      
      Slot nameSlot = kb.getSlot(Model.Slot.NAME);						
      //clsWidget.replaceWidget(nameSlot, RDFSNamedClassMetadataWidget.class.getName());
      clsWidget.replaceWidget(nameSlot, null);
      
      Slot inferredTypeSlot = kb.getSlot(ProtegeNames.Slot.INFERRED_TYPE);		
      clsWidget.replaceWidget(inferredTypeSlot, null);			
      
      Slot headSlot = kb.getSlot(SWRLNames.Slot.HEAD);
      clsWidget.replaceWidget(headSlot, null);
      
      Slot bodySlot = kb.getSlot(SWRLNames.Slot.BODY);
      clsWidget.replaceWidget(bodySlot, null);
      
      clsWidget.replaceWidget(nameSlot, NameDocumentationWidget.class.getName());
      clsWidget.replaceWidget(bodySlot, SWRLRuleSlotWidget.class.getName());
      
      ((FormWidget)clsWidget).setVerticalStretcher(SWRLNames.Slot.BODY);
      ((FormWidget)clsWidget).setHorizontalStretcher(FormWidget.STRETCH_ALL);
      ((FormWidget)clsWidget).setModified(true);
      
    } catch (Exception e) {
      Log.getLogger().warning("Error at configuring SWRL forms: " + e.getMessage());
    } // try
  }  // adjustWidgets
  
  public void afterLoad(Project p) { adjustGUI(p); }
  
  public static void adjustGUI(Project p) 
  { 
    if (!isSWRLPresent(p)) return;
    adjustWidgets(p);
    addSWRLTab(p);
  } // adjustGUI
  
  private static void addSWRLTab(Project p) 
  {
    WidgetDescriptor swrlTabDescriptor = p.getTabWidgetDescriptor(SWRLTab.class.getName());
    swrlTabDescriptor.setVisible(true);		
  } // addSWRLTab
  
  private static boolean isSWRLPresent(Project project) 
  {
    KnowledgeBase kb = project.getKnowledgeBase();
    
    if (!(kb instanceof OWLModel)) return false;
    
    Cls impCls = kb.getCls(SWRLNames.Cls.IMP);
    
    if (impCls == null) return false;
    
    return true;
  } // isSWRLPresent
    
  public static boolean isSWRLImported(OWLModel owlModel) {  return owlModel.getOWLJavaFactory() instanceof SWRLJavaFactory; }

} // SWRLProjectPlugin
