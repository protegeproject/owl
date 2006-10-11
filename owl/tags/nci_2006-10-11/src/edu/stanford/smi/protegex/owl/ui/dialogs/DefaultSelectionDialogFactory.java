package edu.stanford.smi.protegex.owl.ui.dialogs;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.ui.SelectClsesPanel;
import edu.stanford.smi.protege.ui.SelectInstanceFromCollectionPanel;
import edu.stanford.smi.protege.util.Assert;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import java.awt.*;
import java.util.*;

/**
 * The default implementation based on the core Protege dialogs.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultSelectionDialogFactory extends AbstractSelectionDialogFactory {

    private static Collection getFirstTwoClasses(OWLModel owlModel, Collection allowedClasses) {
        Collection concreteClasses = new HashSet();
        if (allowedClasses.isEmpty()) {
            allowedClasses = Collections.singleton(owlModel.getOWLThingClass());
        }
        getFirstTwoClasses(allowedClasses, concreteClasses);
        return concreteClasses;
    }


    private static void getFirstTwoClasses(Collection allowedClasses, Collection classes) {
        Iterator i = allowedClasses.iterator();
        while (i.hasNext() && classes.size() != 2) {
            Cls cls = (Cls) i.next();
            classes.add(cls);
            if (classes.size() == 2) {
                break;
            }
            getFirstTwoClasses(cls.getDirectSubclasses(), classes);
        }
    }


    private static boolean hasOneClass(Collection rootClses) {
        boolean hasOneClass;
        if (rootClses.size() == 1) {
            Cls cls = (Cls) CollectionUtilities.getFirstItem(rootClses);
            hasOneClass = cls.getDirectSubclassCount() == 0;
        }
        else {
            hasOneClass = false;
        }
        return hasOneClass;
    }


    private static Collection pickClasses(Component component, OWLModel owlModel,
                                          Collection rootClses,
                                          String label, boolean multiple) {

        boolean editable = true; //@@TODO make this a parameter

        Collection clses;
        if (rootClses.isEmpty()) {
            clses = Collections.EMPTY_LIST;
        }
        else if (hasOneClass(rootClses)) {
            clses = rootClses;
        }
        else {
            SelectClassPanel panel = new SelectClassPanel(owlModel, rootClses,
                                                          multiple, editable);

            int result = ProtegeUI.getModalDialogFactory().showDialog(component, panel, label, ModalDialogFactory.MODE_OK_CANCEL);
            if (result == ModalDialogFactory.OPTION_OK) {
                clses = panel.getSelection();
            }
            else {
                clses = Collections.EMPTY_LIST;
            }
        }
        return clses;
    }

    private static Cls pickClass(Component component, OWLModel owlModel, Collection allowedClasses, String label) {
        Cls cls;
        Collection concreteClses = getFirstTwoClasses(owlModel, allowedClasses);
        switch (concreteClses.size()) {
            case 0:
                ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, "There are no allowed classes");
                cls = null;
                break;
            case 1:
                cls = (Cls) CollectionUtilities.getFirstItem(concreteClses);
                break;
            case 2:
                cls = promptForClass(component, owlModel, allowedClasses, label);
                break;
            default:
                Assert.fail("bad size: " + concreteClses.size());
                cls = null;
                break;
        }
        return cls;
    }


    private static Instance pickInstanceFromCollection(Component component, Collection collection, int initialSelection, String label) {
        Instance instance = null;
        if (collection != null && collection.size() > 0){
            Collection modifiedVis = ensureResourcesHaveVisibility(collection, true);
            SelectInstanceFromCollectionPanel panel = new SelectInstanceFromCollectionPanel(collection, initialSelection);
            int result = ProtegeUI.getModalDialogFactory().showDialog(component, panel, label, ModalDialogFactory.MODE_OK_CANCEL);
            ensureResourcesHaveVisibility(modifiedVis, false);
            if (result == ModalDialogFactory.OPTION_OK) {
                instance = panel.getSelection();
            }
        }
        return instance;
    }


    private static Collection pickInstancesFromCollection(Component component, Collection instances, String label) {
        Collection selectedSlots = Collections.EMPTY_LIST;
        if (instances != null && instances.size() > 0){
            Collection modifiedVis = ensureResourcesHaveVisibility(instances, true);
            SelectResourcesFromCollectionPanel panel = new SelectResourcesFromCollectionPanel(instances);
            int result = ProtegeUI.getModalDialogFactory().showDialog(component, panel, label, ModalDialogFactory.MODE_OK_CANCEL);
            ensureResourcesHaveVisibility(modifiedVis, false);
            switch (result) {
                case ModalDialogFactory.OPTION_OK:
                    return panel.getSelection();
                case ModalDialogFactory.OPTION_CANCEL:
                    break;
                default:
                    Assert.fail("bad result: " + result);
                    break;
            }
        }
        return selectedSlots;
    }


    private static RDFSNamedClass promptForClass(final Component component, OWLModel owlModel, Collection clses,
                                                 final String label) {
        final SelectClsesPanel p = new SelectClsesPanel(owlModel, clses);

        ModalDialogFactory.CloseCallback callback = new ModalDialogFactory.CloseCallback() {
            public boolean canClose(int result) {
                boolean canClose;
                if (result == ModalDialogFactory.OPTION_OK) {
                    Cls cls = (Cls) CollectionUtilities.getFirstItem(p.getSelection());
                    canClose = cls != null;
                    if (!canClose) {
                        ProtegeUI.getModalDialogFactory().showMessageDialog(component, label, "Information");
                    }
                }
                else {
                    canClose = true;
                }
                return canClose;
            }
        };

        int result = ProtegeUI.getModalDialogFactory().showDialog(component, p, label, ModalDialogFactory.MODE_OK_CANCEL, callback);
        if (result == ModalDialogFactory.OPTION_OK) {
            Object first = CollectionUtilities.getFirstItem(p.getSelection());
            if (first instanceof RDFSNamedClass) {
                return (RDFSNamedClass) first;
            }
        }
        return null;
    }


    public RDFSNamedClass selectClass(Component parent, OWLModel owlModel, Collection rootClasses, String title) {
        if (parent == null) {
            parent = ProtegeUI.getProjectView(owlModel.getProject());
        }
        Cls cls = pickClass(parent, owlModel, rootClasses, title);
        if (cls instanceof RDFSNamedClass) {
            return (RDFSNamedClass) cls;
        }
        else {
            return null;
        }
    }


    public Set selectClasses(Component parent, OWLModel owlModel, Collection rootClasses, String title) {
        if (parent == null) {
            parent = ProtegeUI.getProjectView(owlModel.getProject());
        }
        return new HashSet(pickClasses(parent, owlModel, rootClasses, title, true));
    }


    public RDFProperty selectProperty(Component parent, OWLModel owlModel, Collection allowedProperties, String title) {
        if (parent == null) {
            parent = ProtegeUI.getProjectView(owlModel.getProject());
        }
        return (RDFProperty) pickInstanceFromCollection(parent, allowedProperties, allowedProperties.isEmpty() ? -1 : 0, title);
    }


    public RDFResource selectResourceFromCollection(Component parent, OWLModel owlModel, Collection resources, String title) {
        if (parent == null) {
            parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        }
        return (RDFResource) pickInstanceFromCollection(parent, resources, resources.isEmpty() ? -1 : 0, title);
    }


    public RDFResource selectResourceByType(Component parent, OWLModel owlModel, Collection allowedClasses, String title) {
        if (!allowedClasses.isEmpty()) {
            if (parent == null) {
                parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
            }
            Collection modifiedVis = ensureResourcesHaveVisibility(allowedClasses, true);
            SelectResourcesPanel panel = new SelectResourcesPanel(owlModel, allowedClasses, true);
            int result = ProtegeUI.getModalDialogFactory().showDialog(parent, panel, title, ModalDialogFactory.MODE_OK_CANCEL);
            ensureResourcesHaveVisibility(modifiedVis, false);
            if (result == ModalDialogFactory.OPTION_OK) {
                Collection instances = panel.getSelection();
                for (Iterator it = instances.iterator(); it.hasNext();) {
                    Instance instance = (Instance) it.next();
                    if (instance instanceof RDFResource) {
                        return (RDFResource) instance;
                    }
                }
            }
        }
        return null;
    }


    public Set selectResourcesByType(Component parent, OWLModel owlModel, Collection allowedClasses, String title) {
        if (!allowedClasses.isEmpty()) {
            Collection modifiedVis = ensureResourcesHaveVisibility(allowedClasses, true);
            if (parent == null) {
                parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
            }
            SelectResourcesPanel panel = new SelectResourcesPanel(owlModel, allowedClasses, true);
            int result = ProtegeUI.getModalDialogFactory().showDialog(parent, panel, title, ModalDialogFactory.MODE_OK_CANCEL);
            ensureResourcesHaveVisibility(modifiedVis, false);
            if (result == ModalDialogFactory.OPTION_OK) {
                Collection instances = panel.getSelection();
                Set set = new HashSet();
                for (Iterator it = instances.iterator(); it.hasNext();) {
                    Instance instance = (Instance) it.next();
                    if (instance instanceof RDFResource) {
                        set.add(instance);
                    }
                }
                return set;
            }
        }
        return Collections.EMPTY_SET;
    }


    public Set selectResourcesFromCollection(Component parent, OWLModel owlModel, Collection resources, String title) {
        if (parent == null) {
            parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        }
        return new HashSet(pickInstancesFromCollection(parent, resources, title));
    }

    /**
     * Makes sure that the resources in the specified collection have the specified
     * visibility.
     * @param allowedResources The resources that should have the specified visibility.
     * @param visible Specified whether or not the resources should be visible.
     * @return <code>true</code> if the resources should be visible, or <code>false</code>
     * if the resources should not be visible.
     */
    public static Collection ensureResourcesHaveVisibility(Collection allowedResources, boolean visible) {
        ArrayList modifiedVis = new ArrayList();
        for(Iterator it = allowedResources.iterator(); it.hasNext(); ) {
            Instance curClass = (Instance) it.next();
            if(curClass.isVisible() != visible) {
                curClass.setVisible(visible);
                modifiedVis.add(curClass);
            }
        }
        return modifiedVis;
    }
}
