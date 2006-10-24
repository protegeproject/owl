package edu.stanford.smi.protegex.owl.ui.individuals;

import edu.stanford.smi.protege.action.DeleteInstancesAction;
import edu.stanford.smi.protege.action.MakeCopiesAction;
import edu.stanford.smi.protege.action.ReferencersAction;
import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.ConfigureAction;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.search.finder.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * The panel that holds the list of direct instances of one or more classes. If
 * only one class is chosen then you can also create new instances of this
 * class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class AssertedInstancesListPanel extends SelectableContainer implements Disposable {

    private Collection classes = Collections.EMPTY_LIST;

    private AllowableAction createAction;

    private AllowableAction createAnonymousAction;
    
    private AllowableAction copyAction;

    private AllowableAction deleteAction;

    private HeaderComponent header;

    private OWLLabeledComponent lc;

    private InstancesList list;

    private Collection listenedToInstances = new ArrayList();

    private OWLModel owlModel;

    private static final int SORT_LIMIT;

    private boolean showSubclassInstances;


    static {
        SORT_LIMIT = ApplicationProperties.getIntegerProperty("ui.DirectInstancesList.sort_limit", 1000);
    }


    private ClsListener _clsListener = new ClsAdapter() {
        public void directInstanceAdded(ClsEvent event) {
            Instance instance = event.getInstance();
            if (!getModel().contains(instance)) {
                ComponentUtilities.addListValue(list, instance);
                instance.addFrameListener(_instanceFrameListener);
            }
        }


        public void directInstanceRemoved(ClsEvent event) {
            removeInstance(event.getInstance());
        }
    };

    private FrameListener _clsFrameListener = new FrameAdapter() {
        public void ownSlotValueChanged(FrameEvent event) {
            super.ownSlotValueChanged(event);
            updateButtons();
        }
    };

    private FrameListener _instanceFrameListener = new FrameAdapter() {
        public void browserTextChanged(FrameEvent event) {
            super.browserTextChanged(event);
            sort();
            repaint();
        }
    };


    public AssertedInstancesListPanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        Action viewAction = createViewAction();

        list = new InstancesList(viewAction);

        lc = new OWLLabeledComponent(null, ComponentFactory.createScrollPane(list));
        addButtons(viewAction, lc);

        ResultsViewModelFind findAlg = new DefaultIndividualFind(owlModel, Find.CONTAINS) {
            protected boolean isValidFrameToSearch(Frame f) {
                return (((SimpleListModel) list.getModel()).getValues()).contains(f) &&
                       super.isValidFrameToSearch(f);
            }

            public String getDescription() {
                return "Find Individual Of Selected Class";
            }
        };
        FindAction fAction = new FindInDialogAction(findAlg,
                                                    Icons.getFindInstanceIcon(),
                                                    list, true);

        ResourceFinder finder = new ResourceFinder(fAction);
        lc.setFooterComponent(finder);

        lc.setBorder(ComponentUtilities.getAlignBorder());
        add(lc, BorderLayout.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeader(), BorderLayout.NORTH);
        add(panel, BorderLayout.NORTH);

        setSelectable(list);
        // initializeShowSubclassInstances();
        lc.setHeaderLabel("Asserted Instances");
    }


    private void updateLabel() {
        String text;
        Cls cls = getSoleAllowedCls();
        BrowserSlotPattern pattern = (cls == null) ? null : cls.getBrowserSlotPattern();
        if (pattern == null) {
            text = null;
        }
        else {
            // text = "Instances by ";
            if (pattern.isSimple()) {
                text = pattern.getFirstSlot().getBrowserText();
                if (Model.Slot.NAME.equals(text)) {
                    text = "Asserted Instances";
                }
            }
            else {
                text = "multiple properties";
            }
        }
        lc.setHeaderLabel(text);
    }


    private HeaderComponent createHeader() {
        JLabel label = ComponentFactory.createLabel();
        String instanceBrowserLabel = LocalizedText.getText(ResourceKey.INSTANCE_BROWSER_TITLE);
        String forClassLabel = LocalizedText.getText(ResourceKey.CLASS_EDITOR_FOR_CLASS_LABEL);
        header = new HeaderComponent(instanceBrowserLabel, forClassLabel, label);
        header.setColor(Colors.getInstanceColor());
        return header;
    }


    private void fixRenderer() {
        FrameRenderer frameRenderer = (FrameRenderer) list.getCellRenderer();
        frameRenderer.setDisplayType(showSubclassInstances);
    }


    protected void addButtons(Action viewAction, LabeledComponent c) {
        // c.addHeaderButton(createReferencersAction());
        c.addHeaderButton(createConfigureAction());
        c.addHeaderButton(createCreateAction());
        c.addHeaderButton(createCopyAction());
        c.addHeaderButton(createDeleteAction());
        c.addHeaderButton(createCreateAnonymousAction());
    }


    private void addClsListeners() {
        Iterator i = classes.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            cls.addClsListener(_clsListener);
            cls.addFrameListener(_clsFrameListener);
        }
    }


    private void addInstanceListeners() {
        ListModel model = list.getModel();
        int start = list.getFirstVisibleIndex();
        int stop = list.getLastVisibleIndex();
        for (int i = start; i < stop; ++i) {
            Instance instance = (Instance) model.getElementAt(i);
            addInstanceListener(instance);

        }
    }


    private void removeInstanceListeners() {
        Iterator i = listenedToInstances.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            instance.removeFrameListener(_instanceFrameListener);
        }
        listenedToInstances.clear();
    }


    private void addInstanceListener(Instance instance) {
        instance.addFrameListener(_instanceFrameListener);
        listenedToInstances.add(instance);
    }


    protected Action createCreateAction() {
        createAction = new CreateAction("Create instance", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
            public void onCreate() {
                if (!classes.isEmpty()) {
                    Instance instance = owlModel.createInstance(null, classes);
                    if (instance instanceof Cls) {
                        Cls newCls = (Cls) instance;
                        if (newCls.getDirectSuperclassCount() == 0) {
                            newCls.addDirectSuperclass(owlModel.getOWLThingClass());
                        }
                    }
                    list.setSelectedValue(instance, true);
                }
            }
        };
        return createAction;
    }

    
    protected Action createCreateAnonymousAction() {
        createAnonymousAction = new CreateAction("Create anonymous instance", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_ANON_INDIVIDUAL)) {
            public void onCreate() {
                if (!classes.isEmpty()) {
                	String name = owlModel.getNextAnonymousResourceName();
                    Instance instance = owlModel.createInstance(name, classes);
                    if (instance instanceof Cls) {
                        Cls newCls = (Cls) instance;
                        if (newCls.getDirectSuperclassCount() == 0) {
                            newCls.addDirectSuperclass(owlModel.getOWLThingClass());
                        }
                    }
                    list.setSelectedValue(instance, true);
                }
            }
        };
        return createAnonymousAction;
    }
    

    protected Action createConfigureAction() {
        return new ConfigureAction() {
            public void loadPopupMenu(JPopupMenu menu) {
                menu.add(createSetDisplaySlotAction());
                menu.add(createShowAllInstancesAction());
            }
        };
    }


    protected JMenuItem createShowAllInstancesAction() {
        Action action = new AbstractAction("Show Subclass Instances") {
            public void actionPerformed(ActionEvent event) {
                setShowAllInstances(!showSubclassInstances);
            }
        };
        JMenuItem item = new JCheckBoxMenuItem(action);
        item.setSelected(showSubclassInstances);
        return item;
    }

    //    private void initializeShowSubclassInstances() {
    //        showSubclassInstances = ApplicationProperties.getBooleanProperty(SHOW_SUBCLASS_INSTANCES, false);
    //        reload();
    //        fixRenderer();
    //    }


    private void setShowAllInstances(boolean b) {
        showSubclassInstances = b;
        // ApplicationProperties.setBoolean(SHOW_SUBCLASS_INSTANCES, b);
        reload();
        fixRenderer();
    }


    protected Cls getSoleAllowedCls() {
        Cls cls;
        if (classes.size() == 1) {
            cls = (Cls) CollectionUtilities.getFirstItem(classes);
        }
        else {
            cls = null;
        }
        return cls;
    }


    protected JMenu createSetDisplaySlotAction() {
        JMenu menu = ComponentFactory.createMenu("Set Display Slot");
        boolean enabled = false;
        Cls cls = getSoleAllowedCls();
        if (cls != null) {
            BrowserSlotPattern pattern = cls.getBrowserSlotPattern();
            Slot browserSlot = (pattern != null && pattern.isSimple()) ? pattern.getFirstSlot() : null;
            Iterator i = cls.getVisibleTemplateSlots().iterator();
            while (i.hasNext()) {
                Slot slot = (Slot) i.next();
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(createSetDisplaySlotAction(slot));
                if (slot.equals(browserSlot)) {
                    item.setSelected(true);
                }
                menu.add(item);
                enabled = true;
            }
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(createSetDisplaySlotMultipleAction());
            if (browserSlot == null) {
                item.setSelected(true);
            }
            menu.add(item);
        }
        menu.setEnabled(enabled);
        return menu;
    }


    protected Action createSetDisplaySlotAction(final Slot slot) {
        return new AbstractAction(slot.getBrowserText(), slot.getIcon()) {
            public void actionPerformed(ActionEvent event) {
                getSoleAllowedCls().setDirectBrowserSlot(slot);
                updateLabel();
                repaint();
            }
        };
    }


    protected Action createSetDisplaySlotMultipleAction() {
        return new AbstractAction("Multple Slots...") {
            public void actionPerformed(ActionEvent event) {
                Cls cls = getSoleAllowedCls();
                BrowserSlotPattern currentPattern = getSoleAllowedCls().getBrowserSlotPattern();
                MultiSlotPanel panel = new MultiSlotPanel(currentPattern, cls);
                int rval = ModalDialog.showDialog(AssertedInstancesListPanel.this, panel, "Multislot Display Pattern",
                                                  ModalDialog.MODE_OK_CANCEL);
                if (rval == ModalDialog.OPTION_OK) {
                    BrowserSlotPattern pattern = panel.getBrowserTextPattern();
                    if (pattern != null) {
                        cls.setDirectBrowserSlotPattern(pattern);
                    }
                }
                updateLabel();
                repaint();
            }
        };
    }


    protected Action createDeleteAction() {
        deleteAction = new DeleteInstancesAction(this);
        return deleteAction;
    }


    protected Action createCopyAction() {
        copyAction = new MakeCopiesAction(ResourceKey.INSTANCE_COPY, this) {
            protected Instance copy(Instance instance, boolean isDeep) {
                Instance copy = super.copy(instance, isDeep);
                setSelectedInstance(copy);
                return copy;
            }
        };
        return copyAction;
    }


    protected Action createReferencersAction() {
        return new ReferencersAction(ResourceKey.INSTANCE_VIEW_REFERENCES, this);
    }


    protected Action createViewAction() {
        return new ViewAction(ResourceKey.INSTANCE_VIEW, this) {
            public void onView(Object o) {
                owlModel.getProject().show((Instance) o);
            }
        };
    }


    public void dispose() {
        removeClsListeners();
        removeInstanceListeners();
    }


    public JComponent getDragComponent() {
        return list;
    }


    private SimpleListModel getModel() {
        return (SimpleListModel) list.getModel();
    }


    private boolean isSelectionEditable() {
        boolean isEditable = true;
        Iterator i = getSelection().iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (!instance.isEditable()) {
                isEditable = false;
                break;
            }
        }
        return isEditable;
    }


    public void onSelectionChange() {
        // Log.enter(this, "onSelectionChange");
        boolean editable = isSelectionEditable();
        ComponentUtilities.setDragAndDropEnabled(list, editable);
        updateButtons();
    }


    private void removeInstance(Instance instance) {
        ComponentUtilities.removeListValue(list, instance);
        instance.removeFrameListener(_instanceFrameListener);
    }


    private void removeClsListeners() {
        Iterator i = classes.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            cls.removeClsListener(_clsListener);
            cls.removeFrameListener(_clsFrameListener);
        }
    }


    public void setClses(Collection newClses) {
        removeClsListeners();
        classes = new ArrayList(newClses);
        list.setClasses(newClses);
        reload();
        updateButtons();
        addClsListeners();
    }


    public void reload() {
        removeInstanceListeners();
        Object selectedValue = list.getSelectedValue();
        Set instanceSet = new LinkedHashSet();
        Iterator i = classes.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            instanceSet.addAll(getInstances(cls));
        }
        List instances = new ArrayList(instanceSet);
        if (instances.size() <= SORT_LIMIT) {
            Collections.sort(instances, new FrameComparator());
        }
        getModel().setValues(instances);
        if (instances.contains(selectedValue)) {
            list.setSelectedValue(selectedValue, true);
        }
        else if (!instances.isEmpty()) {
            list.setSelectedIndex(0);
        }
        addInstanceListeners();
        reloadHeader(classes);
        updateLabel();
    }


    private void reloadHeader(Collection clses) {
        StringBuffer text = new StringBuffer();
        Icon icon = null;
        Iterator i = clses.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            if (icon == null) {
                icon = cls.getIcon();
            }
            if (text.length() != 0) {
                text.append(", ");
            }
            text.append(cls.getName());
        }
        JLabel label = (JLabel) header.getComponent();
        label.setText(text.toString());
        label.setIcon(icon);
    }


    private Collection getInstances(Cls cls) {
        Collection instances;
        if (showSubclassInstances) {
            instances = cls.getInstances();
        }
        else {
            instances = cls.getDirectInstances();
        }
        if (!owlModel.getProject().getDisplayHiddenFrames()) {
            instances = removeHiddenInstances(instances);
        }
        return instances;
    }


    private static Collection removeHiddenInstances(Collection instances) {
        Collection visibleInstances = new ArrayList(instances);
        Iterator i = visibleInstances.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (!instance.isVisible()) {
                i.remove();
            }
        }
        return visibleInstances;
    }


    public void sort() {
        list.setListenerNotificationEnabled(false);
        Object selectedValue = list.getSelectedValue();
        List instances = new ArrayList(getModel().getValues());
        if (instances.size() <= SORT_LIMIT) {
            Collections.sort(instances, new FrameComparator());
        }
        getModel().setValues(instances);
        list.setSelectedValue(selectedValue);
        list.setListenerNotificationEnabled(true);
    }


    public void setSelectedInstance(Instance instance) {
        list.setSelectedValue(instance, true);
        updateButtons();
    }


    private void updateButtons() {
        Cls cls = (Cls) CollectionUtilities.getFirstItem(classes);        
        createAction.setEnabled(cls == null ? false : cls.isConcrete());
        createAnonymousAction.setEnabled(cls == null ? false : cls.isConcrete());
        
        Instance instance = (Instance) getSoleSelection();
        boolean allowed = instance != null && instance instanceof SimpleInstance;
        copyAction.setAllowed(allowed);
    }


    /**
     * Does nothing anymore. This functionality moved to the menu button.
     *
     * @deprecated
     */
    public void setShowDisplaySlotPanel(boolean b) {

    }
}