package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protege.action.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.resource.Text;
import edu.stanford.smi.protege.ui.ProjectMenuBar;
import edu.stanford.smi.protege.ui.ProjectToolBar;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.StandardAction;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.javacode.JavaCodeGeneratorResourceAction;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.model.util.XSDVisibility;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionManager;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.forms.AbsoluteFormsGenerator;
import edu.stanford.smi.protegex.owl.ui.forms.AbsoluteFormsLoader;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.navigation.NavigationHistoryManager;
import edu.stanford.smi.protegex.owl.ui.navigation.TabNavigationHistorySelectable;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.subsumption.ChangedClassesPanel;
import edu.stanford.smi.protegex.owl.ui.tooltips.ClassDescriptionToolTipGenerator;
import edu.stanford.smi.protegex.owl.ui.tooltips.HomeOntologyToolTipGenerator;
import edu.stanford.smi.protegex.owl.ui.triplestore.TripleStoreSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.OWLWidgetMapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * A ProjectPlugin that makes a couple of initializing adjustments to
 * the main menu, tool bar etc, after a project has been loaded.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLMenuProjectPlugin extends ProjectPluginAdapter {

    private final static String CHANGED_WIDGETS = "ChangedWidgets";

    private static JCheckBoxMenuItem proseBox;

    private OWLModelAction recentAction = null;

    private SyntaxHelpAction syntaxHelpAction = new SyntaxHelpAction();

    private TripleStoreSelectionAction tripleStoreSelectionAction;

    public static final String MENU_NAME = AbstractOWLModelAction.OWL_MENU;

    public static final String PROSE_PROPERTY = "OWL-Prose";


    private void addToolBarButton(JToolBar toolBar, Action action) {
        ComponentFactory.addToolBarButton(toolBar, action);
    }


    private void addToolBarButton(JToolBar toolBar, Action action, Icon icon) {
        action.putValue(Action.SMALL_ICON, icon);
        action.putValue(StandardAction.DISABLED_ICON, null);
        addToolBarButton(toolBar, action);
    }


    private void adjustMenuAndToolBar(final OWLModel owlModel, final ProjectMenuBar menuBar, final ProjectToolBar toolBar) {

        JMenu owlMenu = new JMenu(AbstractOWLModelAction.OWL_MENU);
        owlMenu.setMnemonic(KeyEvent.VK_O);

        JMenu codeMenu = new JMenu(AbstractOWLModelAction.CODE_MENU);
        codeMenu.setMnemonic(KeyEvent.VK_C);

        JMenu toolsMenu = new JMenu(AbstractOWLModelAction.TOOLS_MENU);
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        JMenu helpMenu = menuBar.getMenu(menuBar.getMenuCount() - 1);
        helpMenu.addSeparator();
        helpMenu.add(new AbstractAction("Protege-OWL Tutorial...") {
            public void actionPerformed(ActionEvent e) {
                SystemUtilities.showHTML("http://www.co-ode.org/resources/tutorials/");
            }
        });
        helpMenu.add(syntaxHelpAction);

        // prose tooltips selector
        proseBox = new JCheckBoxMenuItem("Display prose as tool tip of OWL expressions", true);
        helpMenu.add(proseBox);
        proseBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                owlModel.getOWLProject().getSettingsMap().setBoolean(PROSE_PROPERTY, proseBox.isSelected());
                if (proseBox.isSelected()) {
                    OWLUI.setOWLToolTipGenerator(new ClassDescriptionToolTipGenerator());
                }
                else {
                    OWLUI.setOWLToolTipGenerator(new HomeOntologyToolTipGenerator());
                }
            }
        });
        proseBox.setSelected(Boolean.TRUE.equals(owlModel.getOWLProject().getSettingsMap().getBoolean(PROSE_PROPERTY)));
        if (proseBox.isSelected()) {
            OWLUI.setOWLToolTipGenerator(new ClassDescriptionToolTipGenerator());
        }
        else {
            OWLUI.setOWLToolTipGenerator(new HomeOntologyToolTipGenerator());
        }

        // add OWLMenu to mainMenuBar
        menuBar.add(owlMenu, 3);
        menuBar.add(codeMenu, 4);
        menuBar.add(toolsMenu, 5);

        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_MANAGE_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_CHANGE_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_MERGE_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_SHOW_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_INCLUDE);

        disableHelpMenuItem(menuBar, ResourceKey.HELP_MENU_ONTOLOGIES_101);
        disableHelpMenuItem(menuBar, ResourceKey.HELP_MENU_PLUGINS);
        disableHelpMenuItem(menuBar, ResourceKey.HELP_MENU_ICONS);
        disableHelpMenuItem(menuBar, ResourceKey.HELP_MENU_FAQ);
        disableHelpMenuItem(menuBar, ResourceKey.HELP_MENU_GETTING_STARTED);
        disableHelpMenuItem(menuBar, ResourceKey.HELP_MENU_USERS_GUIDE);
        helpMenu.remove(0);

        disableMenuItem(menuBar, ResourceKey.MENUBAR_PROJECT, ResourceKey.PROJECT_METRICS, false);

        adjustToolBar(toolBar);

        recentAction = null;
        OWLModelActionManager.addOWLModelActionsToMenubar(owlModel, new OWLModelActionManager.Adder() {
            public void addOWLModelAction(OWLModelAction action) {
                addOWLModelActionToMenuBar(action, owlModel, menuBar);
            }
        });

        recentAction = null;
        OWLModelActionManager.addOWLModelActionsToToolbar(owlModel, new OWLModelActionManager.Adder() {
            public void addOWLModelAction(OWLModelAction action) {
                addOWLModelActionToToolBar(action, owlModel, toolBar);
            }
        });

        toolBar.addSeparator();

        TabNavigationHistorySelectable selectable = new TabNavigationHistorySelectable(owlModel);
        NavigationHistoryManager manager = new NavigationHistoryManager(selectable, owlModel);
        manager.add(owlModel.getOWLThingClass());
        toolBar.add(manager.getBackAction());
        toolBar.add(manager.getForwardAction());

        // Remove separators as first entries
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getItemCount() > 0) {
                JMenuItem item = menu.getItem(0);
                if (item == null) {
                    menu.remove(0);
                }
            }
        }
    }


    private void addOWLModelActionToMenuBar(OWLModelAction action, OWLModel owlModel, ProjectMenuBar menuBar) {
        StringTokenizer tokenizer = new StringTokenizer(action.getMenubarPath(), "/", false);
        if (tokenizer.hasMoreElements()) {
            Action a = createAction(action, owlModel);
            if (a != null) {
                String menuName = tokenizer.nextToken();
                JMenu menu = null;
                for (int i = 0; i < menuBar.getMenuCount(); i++) {
                    if (menuName.equals(menuBar.getMenu(i).getText())) {
                        menu = menuBar.getMenu(i);
                    }
                }
                if (menu == null) {
                    menu = new JMenu(menuName);
                    menuBar.add(menu, menuBar.getMenuCount() - 1);
                }
                else if (recentAction != null) {
                    if (!recentAction.getMenubarPath().equals(action.getMenubarPath())) {
                        menu.addSeparator();
                    }
                }
                menu.add(a);
            }
        }
        recentAction = action;
    }


    private void addOWLModelActionToToolBar(OWLModelAction action, OWLModel owlModel, ProjectToolBar toolBar) {
        Action a = createAction(action, owlModel);
        if (a != null) {
            if (recentAction != null) {
                if (!recentAction.getToolbarPath().equals(action.getToolbarPath())) {
                    toolBar.addSeparator();
                }
            }
            addToolBarButton(toolBar, a);
        }
        recentAction = action;
    }


    private void adjustToolBar(JToolBar toolBar) {

        toolBar.removeAll();

        addToolBarButton(toolBar, new CreateProject(true), OWLIcons.getImageIcon("File"));
        addToolBarButton(toolBar, new OpenProject(true), OWLIcons.getOpenProjectIcon());
        addToolBarButton(toolBar, new SaveProject(true), OWLIcons.getSaveProjectIcon());
        toolBar.addSeparator();

        addToolBarButton(toolBar, new Cut(true), OWLIcons.getCutIcon());
        addToolBarButton(toolBar, new Copy(true), OWLIcons.getCopyIcon());
        addToolBarButton(toolBar, new Paste(true), OWLIcons.getPasteIcon());
        toolBar.addSeparator();

        addToolBarButton(toolBar, new ArchiveProject(true), OWLIcons.getArchiveProjectIcon());
        addToolBarButton(toolBar, new RevertProject(true), OWLIcons.getRevertProjectIcon());
        toolBar.addSeparator();

        addToolBarButton(toolBar, new UndoAction(true), OWLIcons.getUndoIcon());
        addToolBarButton(toolBar, new RedoAction(true), OWLIcons.getRedoIcon());
        toolBar.addSeparator();
    }


    public void afterCreate(Project p) {
        final KnowledgeBase kb = p.getKnowledgeBase();
        if (kb instanceof OWLModel) {
            //p.setWidgetMapper(new OWLWidgetMapper((OWLModel) p.getKnowledgeBase()));
            OWLModel owlModel = (OWLModel) kb;
            if (owlModel instanceof OWLDatabaseModel) {
                ((OWLDatabaseModel) p.getKnowledgeBase()).initDefaultNamespaces();
            }
        }
    }


    public void afterLoad(Project project) {
        KnowledgeBase kb = project.getKnowledgeBase();
        if (kb instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) kb;
            owlModel.getNamespaceManager().update();
            makeHiddenClsesWithSubclassesVisible(owlModel);
            project.setWidgetMapper(new OWLWidgetMapper(owlModel));
            Integer build = owlModel.getOWLProject().getSettingsMap().getInteger(JenaKnowledgeBaseFactory.OWL_BUILD_PROPERTY);
            if (build == null) {
                fix(owlModel);
                /*OWLUI.showMessageDialog("Warning: Your Protege project (pprj) file does not contain information" +
                        "\nabout which version of Protege-OWL it was created with.  Some forms" +
                        "\nor tabs may not show up correctly.  In that case, you should rebuild your" +
                        "\nproject from the .owl file." +
                        "\nIf you think your project file is ok you just need to save the project" +
                        "\nusing this version to get rid of this warning in the future.");*/
            }
            else if (build.intValue() < OWLText.getLatestCompatibleBuild()) {
                fix(owlModel);
                /*
                OWLUI.showMessageDialog("Warning: Your Protege project (pprj) file has been created with a" +
                        "\nprevious version of Protege-OWL (" + build + ").  As a result, some forms or" +
                        "\ntabs may not show up correctly.  In that case, you should rebuild your" +
                        "\nproject from the .owl file.");
                        */
            }
            if (project.getSources().getString(AbsoluteFormsGenerator.SAVE_FORMS_KEY) != null) {
                try {
                    AbsoluteFormsLoader absoluteFormsLoader = new AbsoluteFormsLoader(owlModel);
                    absoluteFormsLoader.loadAll();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    public void afterSave(Project p) {
        if (p.getKnowledgeBase() instanceof OWLModel) {
            OWLModel owlModel = ((OWLModel) p.getKnowledgeBase());
            restoreWidgetsAfterSave(owlModel);
        }
    }


    public void afterShow(ProjectView view, ProjectToolBar toolBar, ProjectMenuBar menuBar) {
        KnowledgeBase kb = view.getProject().getKnowledgeBase();
        if (kb instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) kb;

            ProtegeUI.register(view);

            if (owlModel instanceof JenaOWLModel) {
                JenaOWLModel.inUI = true;
            }

            owlModel.getProject().setInstanceDisplayClass(ResourceDisplay.class);
            adjustMenuAndToolBar(owlModel, menuBar, toolBar);

            ResourceActionManager.addResourceActionClass(JavaCodeGeneratorResourceAction.class);

            int buildNumber = Integer.parseInt(Text.getBuildNumber());
            if (buildNumber < OWLText.getRequiresProtegeBuild()) {
                ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                                                                    "Warning: This version of the OWL Plugin requires Protege 3.2 beta build " +
                                                                    OWLText.getRequiresProtegeBuild() + ",\nbut you currently have Protege " +
                                                                    Text.getVersion() + ", Build " + buildNumber + ". Please do a clean reinstall.");
            }
        }
    }


    public void beforeClose(Project p) {
        if (p.getKnowledgeBase() instanceof OWLModel) {
            ProjectView view = ProtegeUI.getProjectView(p);
            if (view != null) {
                ProtegeUI.unregister(view);
            }
            ChangedClassesPanel.dispose((OWLModel) p.getKnowledgeBase());
        }
    }


    public void beforeSave(Project p) {
        if (p.getKnowledgeBase() instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) p.getKnowledgeBase();
            String value = owlModel.getOWLProject().getSettingsMap().getString(AbsoluteFormsGenerator.SAVE_FORMS_KEY);
            if (value != null) {
                AbsoluteFormsGenerator generator = new AbsoluteFormsGenerator(owlModel);
                try {
                    generator.generateFiles(value);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Warning: Could not save .forms files");
                }

                if (AbsoluteFormsLoader.useNewFormMechanism_DontUseThisMethod()) {
                    prepareWidgetsForSave(owlModel);
                }
            }
        }
    }


    private Action createAction(final OWLModelAction owlModelAction, final OWLModel owlModel) {
        return new OWLModelActionAction(owlModelAction, owlModel);
    }


    private void disableHelpMenuItem(ProjectMenuBar menuBar, ResourceKey resourceKey) {
        disableMenuItem(menuBar, ResourceKey.MENUBAR_HELP, resourceKey, false);
    }


    private void disableProjectMenuItem(ProjectMenuBar menuBar, ResourceKey resourceKey) {
        disableMenuItem(menuBar, ResourceKey.MENUBAR_PROJECT, resourceKey, true);
    }


    private void disableMenuItem(ProjectMenuBar menuBar, ResourceKey menuKey,
                                 ResourceKey resourceKey, boolean removeTrailingSeparator) {
        String menuName = LocalizedText.getText(menuKey);
        String itemName = LocalizedText.getText(resourceKey);
        JMenu projectMenu = menuBar.getMenu(0);
        for (int i = 1; !projectMenu.getText().equals(menuName); i++) {
            projectMenu = menuBar.getMenu(i);
        }
        for (int i = 0; i < projectMenu.getItemCount(); i++) {
            JMenuItem item = projectMenu.getItem(i);
            if (item != null && itemName.equals(item.getText())) {
                projectMenu.remove(item);
                if (removeTrailingSeparator && projectMenu.getItem(i) == null) {
                    projectMenu.remove(i);
                }
            }
        }
    }


    private void fix(OWLModel owlModel) {
        if (!owlModel.getProject().isMultiUserClient()) {
            OWLBackwardsCompatibilityProjectFixups.fix(owlModel);
            XSDVisibility.updateVisibility(owlModel);
        }
    }


    public static boolean isProseActivated() {
        return proseBox != null && proseBox.isSelected();
    }


    /**
     * Checks whether a given class is used somewhere as a range of a property.
     * This method is not executed for database knowledgebase, because this would
     * be too slot.
     *
     * @param cls the Cls to test
     * @return true if cls is used at some range (VALUE_TYPE slot)
     */
    private static boolean isUsedInRange(Cls cls, Set systemFrames) {
        OWLModel owlModel = (OWLModel) cls.getKnowledgeBase();
        if (owlModel instanceof JenaOWLModel) {
            Collection refs = ((KnowledgeBase) owlModel).getReferences(cls, 1000);
            Slot rangeSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.VALUE_TYPE);
            for (Iterator it = refs.iterator(); it.hasNext();) {
                Reference reference = (Reference) it.next();
                Frame frame = reference.getFrame();
                if (reference.getSlot().equals(rangeSlot) &&
                    !systemFrames.contains(frame) &&
                    !frame.getName().startsWith("protege:")) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void makeHiddenClsesWithSubclassesVisible(OWLModel owlModel) {
        if (owlModel.getOWLNamedClassClass().getSubclassCount() > 0 ||
            owlModel.getRDFSNamedClassClass().getSubclassCount() > 2) {
            owlModel.getRDFSNamedClassClass().setVisible(true);
            owlModel.getOWLNamedClassClass().setVisible(true);
        }
        Set systemFrames = new HashSet(owlModel.getOWLSystemResources());
        if (owlModel.getOWLObjectPropertyClass().getSubclassCount() > 0 ||
            owlModel.getOWLDatatypePropertyClass().getSubclassCount() > 0 ||
            isUsedInRange(owlModel.getOWLObjectPropertyClass(), systemFrames) ||
            isUsedInRange(owlModel.getOWLDatatypePropertyClass(), systemFrames) ||
            isUsedInRange(owlModel.getRDFPropertyClass(), systemFrames)) {
            owlModel.getRDFPropertyClass().setVisible(true);
            owlModel.getOWLObjectPropertyClass().setVisible(true);
            owlModel.getOWLDatatypePropertyClass().setVisible(true);
        }
        makeVisibleIfSubclassesExist(owlModel.getRDFListClass(), systemFrames);
        makeVisibleIfSubclassesExist(owlModel.getOWLDataRangeClass(), systemFrames);
        makeVisibleIfSubclassesExist(owlModel.getRDFSNamedClass(RDFSNames.Cls.LITERAL), systemFrames);
        makeVisibleIfSubclassesExist(owlModel.getOWLNothing(), systemFrames);
        makeVisibleIfSubclassesExist(owlModel.getRDFSNamedClass(RDFNames.Cls.STATEMENT), systemFrames);
    }


    private static void makeVisibleIfSubclassesExist(Cls cls, Set systemFrames) {
        if (cls.getDirectSubclassCount() > 0 || isUsedInRange(cls, systemFrames)) {
            cls.setVisible(true);
        }
    }


    /**
     * Should be called prior to saving a Project in order to remove widgets
     * from the file.
     * This call must be followed by <CODE>restoreWidgetsAfterSave()</CODE>
     * after save completed.
     *
     * @param owlModel the OWLModel to adjust
     */
    public static void prepareWidgetsForSave(OWLModel owlModel) {
        Project p = owlModel.getProject();
        Collection widgets = new ArrayList();
        Iterator i = p.getClsesWithCustomizedForms().iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            if (!cls.isSystem()) {
                WidgetDescriptor d = p.getDesignTimeClsWidget(cls).getDescriptor();
                if (!d.isIncluded()) {
                    widgets.add(d);
                    d.setIncluded(true);
                }
            }
        }
        owlModel.getOWLProject().setSessionObject(CHANGED_WIDGETS, widgets);
    }


    public static void restoreWidgetsAfterSave(OWLModel owlModel) {
        OWLProject owlProject = owlModel.getOWLProject();
        Object widgets = owlProject.getSessionObject(CHANGED_WIDGETS);
        if (widgets instanceof Collection) {
            Iterator wit = ((Collection) widgets).iterator();
            while (wit.hasNext()) {
                WidgetDescriptor d = (WidgetDescriptor) wit.next();
                d.setIncluded(false);
            }
            owlProject.setSessionObject(CHANGED_WIDGETS, null);
        }
    }
}
