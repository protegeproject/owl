package edu.stanford.smi.protegex.owl.ui.individuals;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.ui.ParentChildRoot;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.PopupMenuMouseListener;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protege.util.StringUtilities;
import edu.stanford.smi.protege.util.ViewAction;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTree;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTreePanel;
import edu.stanford.smi.protegex.owl.ui.search.finder.DefaultClassFind;
import edu.stanford.smi.protegex.owl.ui.search.finder.DefaultIndividualFind;
import edu.stanford.smi.protegex.owl.ui.search.finder.Find;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindAction;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindInDialogAction;
import edu.stanford.smi.protegex.owl.ui.search.finder.ResourceFinder;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * The class tree display on the individuals tab.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class IndividualsTabClassesPanel extends SelectableContainer implements ClassTreePanel {

    private ClassTree classTree;

    private OWLModel owlModel;


    public IndividualsTabClassesPanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createClsesPanel(), BorderLayout.CENTER);
        setSelectable(classTree);
    }


    private JComponent createHeaderPanel() {
        JLabel label = ComponentFactory.createLabel(owlModel.getProject().getName());
        label.setIcon(Icons.getProjectIcon());
        String classBrowserLabel = LocalizedText.getText(ResourceKey.CLASS_BROWSER_TITLE);
        String forProjectLabel = LocalizedText.getText(ResourceKey.CLASS_BROWSER_FOR_PROJECT_LABEL);
        HeaderComponent header = new HeaderComponent(classBrowserLabel, forProjectLabel, label);
        header.setColor(Colors.getClsColor());
        return header;
    }


    private JComponent createClsesPanel() {
        RDFSNamedClass root = owlModel.getOWLThingClass();
        classTree = new ClassTree(null, new ParentChildRoot(root));
        classTree.setLargeModel(true);       
        classTree.setSelectionRow(0);       
        
        classTree.setCellRenderer(new InferredInstancesCountRenderer());
        classTree.addMouseListener(new PopupMenuMouseListener(classTree) {
            protected JPopupMenu getPopupMenu() {
                Collection sel = classTree.getSelection();
                if (sel.size() == 1) {
                    JPopupMenu menu = new JPopupMenu();
                    Cls cls = (Cls) sel.iterator().next();
                    if (cls instanceof RDFResource) {
                        ResourceActionManager.addResourceActions(menu, IndividualsTabClassesPanel.this, (RDFResource) cls);
                        if (menu.getComponentCount() > 0) {
                            return menu;
                        }
                    }
                }
                return null;
            }


            protected void setSelection(JComponent c, int x, int y) {
                int row = classTree.getRowForLocation(x, y);
                if (row >= 0) {
                    classTree.setSelectionRow(row);
                }
            }
        });
        
        
        
        String classHiearchyLabel = LocalizedText.getText(ResourceKey.CLASS_BROWSER_HIERARCHY_LABEL);
        LabeledComponent c = new LabeledComponent(classHiearchyLabel, ComponentFactory.createScrollPane(classTree));
        c.setBorder(ComponentUtilities.getAlignBorder());
        c.addHeaderButton(getViewClsAction());

        FindAction fAction =
                new FindInDialogAction(new DefaultClassFind(owlModel, Find.CONTAINS),
                                       Icons.getFindClsIcon(), classTree, true);

        ResourceFinder finder = new ResourceFinder(fAction);

        FindAction findIndAction =
                new FindInDialogAction(new DefaultIndividualFind(owlModel, Find.CONTAINS),
                                       Icons.getFindIcon(), null, true);
        findIndAction.setTextBox(finder.getTextComponent());

        finder.addButton(findIndAction);
        c.setFooterComponent(finder);

        return c;
    }


    public JTree getDropComponent() {
        return classTree;
    }


    private Action getViewClsAction() {
        return new ViewAction(ResourceKey.CLASS_VIEW, this) {
            public void onView(Object o) {
                Cls cls = (Cls) o;
                owlModel.getProject().show(cls);
            }
        };
    }


    /**
     * @deprecated
     */
    public void setSelectedCls(Cls cls) {
        if (cls instanceof RDFSNamedClass) {
            setSelectedClass((RDFSNamedClass) cls);
        }
    }


    public void setSelectedClass(RDFSNamedClass cls) {
    	OWLUI.setSelectedNodeInTree(classTree, cls);
    }


	public void setSelectedClass(RDFSClass cls) {
		OWLUI.setSelectedNodeInTree(classTree, cls);		
	}


	public JTree getTree() {		
		return classTree;
	}
	
    private class InferredInstancesCountRenderer extends FrameRenderer {

        InferredInstancesCountRenderer() {
            setDisplayDirectInstanceCount(true);
        }
        
        @Override
        public void setMainText(String text) {        
        	super.setMainText(StringUtilities.unquote(text));
        }


        protected String getInstanceCountString(Cls cls) {
            if (cls instanceof RDFSNamedClass) {
                RDFSNamedClass c = (RDFSNamedClass) cls;
                int inferredInstanceCount = c.getInferredInstanceCount();
                if (inferredInstanceCount > 0) {
                    return "  (" + cls.getDirectInstanceCount() + " / " + inferredInstanceCount + ")";
                }
            }
            return super.getInstanceCountString(cls);
        }
    }
}
