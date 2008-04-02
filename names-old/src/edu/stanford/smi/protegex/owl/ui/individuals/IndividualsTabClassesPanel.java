package edu.stanford.smi.protegex.owl.ui.individuals;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.ui.ParentChildRoot;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTree;
import edu.stanford.smi.protegex.owl.ui.search.finder.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * The class tree display on the individuals tab.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class IndividualsTabClassesPanel extends SelectableContainer {

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

        FrameRenderer renderer = FrameRenderer.createInstance();
        renderer.setDisplayDirectInstanceCount(true);
        classTree.setCellRenderer(renderer);
        classTree.setSelectionRow(0);
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
        Collection path = ModelUtilities.getPathToRoot(cls);
        ComponentUtilities.setSelectedObjectPath(classTree, path);
    }
}
