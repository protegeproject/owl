package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.Files;
import edu.stanford.smi.protege.storage.clips.ClipsKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.Assert;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protege.widget.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.individuals.OWLIndividualsTab;
import edu.stanford.smi.protegex.owl.ui.widget.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Repairs invalid forms from old pprj files.
 *
 * @author Ray Fergerson  <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */

public class OWLBackwardsCompatibilityProjectFixups {


    private static void changeInstanceValue(KnowledgeBase kb,
                                            String className,
                                            String slotNameToCheck,
                                            String slotValue,
                                            String slotNameToChange,
                                            Object oldValue,
                                            Object newValue) {
        Cls cls = kb.getCls(className);
        Slot slotToCheck = kb.getSlot(slotNameToCheck);
        Slot slotToChange = kb.getSlot(slotNameToChange);
        Iterator i = cls.getInstances().iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (slotValue.equals(instance.getOwnSlotValue(slotToCheck))
                    && oldValue.equals(instance.getOwnSlotValue(slotToChange))) {
                instance.setOwnSlotValue(slotToChange, newValue);
                // Log.trace("set " + slotNameToChange + " to " + newValue, Project.class, "changeInstanceValue");
            }
        }
    }


    public static void fix(OWLModel owlModel) {
        KnowledgeBase systemKB = owlModel.getProject().getInternalProjectKnowledgeBase();
        updateStandardForms(systemKB);
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.cls.OWLClsesTab", OWLClassesTab.class.getName());
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.OWLIndividualsTab", OWLIndividualsTab.class.getName());
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.widget.OWLInstanceListWidget", MultiResourceWidget.class.getName());
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.widget.OWLInstanceFieldWidget", SingleResourceWidget.class.getName());
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.widget.ListInstanceWidget", RDFListWidget.class.getName());
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.widget.LiteralFieldWidget", SingleLiteralWidget.class.getName());
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.widget.LiteralListWidget", MultiLiteralWidget.class.getName());
        renameWidget(systemKB, "edu.stanford.smi.protegex.owl.ui.widget.MultiLanguageStringValueWidget", MultiLiteralWidget.class.getName());
        renameWidget(systemKB, TextFieldWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(systemKB, TextAreaWidget.class.getName(), SingleLiteralAreaWidget.class.getName());
        renameWidget(systemKB, CheckBoxWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(systemKB, ComboBoxWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(systemKB, FloatFieldWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(systemKB, FloatListWidget.class.getName(), MultiLiteralWidget.class.getName());
        renameWidget(systemKB, IntegerFieldWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(systemKB, IntegerListWidget.class.getName(), MultiLiteralWidget.class.getName());
        renameWidget(systemKB, StringListWidget.class.getName(), MultiLiteralWidget.class.getName());
        renameWidget(systemKB, SymbolListWidget.class.getName(), MultiLiteralWidget.class.getName());
        String[] invisibles = new String[]{
                OWLNames.Cls.ANNOTATION_PROPERTY,
                OWLNames.Cls.DEPRECATED_CLASS,
                OWLNames.Cls.DEPRECATED_PROPERTY,
                OWLNames.Cls.FUNCTIONAL_PROPERTY,
                OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY,
                OWLNames.Cls.SYMMETRIC_PROPERTY,
                OWLNames.Cls.TRANSITIVE_PROPERTY,
                RDFSNames.Cls.DATATYPE,
                RDFNames.Cls.ALT,
                RDFNames.Cls.BAG,
                RDFNames.Cls.SEQ,
                OWLNames.Cls.ALL_DIFFERENT,
                OWLNames.Slot.DISJOINT_WITH,
                OWLNames.Slot.DISTINCT_MEMBERS,
                RDFNames.Cls.EXTERNAL_RESOURCE
        };
        for (int i = 0; i < invisibles.length; i++) {
            String invisible = invisibles[i];
            RDFResource resource = owlModel.getRDFResource(invisible);
            resource.setVisible(false);
        }
    }


    private static Instance getClsWidgetInstance(String name, KnowledgeBase kb) {
        Instance result = null;
        Collection values = ModelUtilities
                .getDirectOwnSlotValues(getProjectInstance(kb), "customized_instance_widgets");
        Iterator i = values.iterator();
        while (i.hasNext()) {
            Instance widgetInstance = (Instance) i.next();
            String widgetSlotName = (String) ModelUtilities.getDirectOwnSlotValue(widgetInstance, "name");
            if (name.equals(widgetSlotName)) {
                result = widgetInstance;
                break;
            }
        }
        return result;
    }


    private static KnowledgeBaseFactory getFactory(KnowledgeBase kb) {
        KnowledgeBaseFactory result = null;
        Instance projectInstance = getProjectInstance(kb);
        Instance sources = (Instance) ModelUtilities.getDirectOwnSlotValue(projectInstance, "sources");
        Iterator i = ModelUtilities.getDirectOwnSlotValues(sources, "properties").iterator();
        while (i.hasNext()) {
            Instance property = (Instance) i.next();
            String name = (String) ModelUtilities.getDirectOwnSlotValue(property, "name");
            if (name.equals("factory_class_name")) {
                String factoryName = (String) ModelUtilities.getDirectOwnSlotValue(property, "string_value");
                result = (KnowledgeBaseFactory) SystemUtilities.newInstance(factoryName);
                break;
            }
        }
        return result;
    }


    private static Instance getProjectInstance(KnowledgeBase kb) {
        Instance instance = kb.getInstance("PROJECT");
        Assert.assertNotNull("instance", instance);
        return instance;
    }


    private static KnowledgeBase getTemplateKnowledgeBase(KnowledgeBase projectKB) {
        Collection errors = new ArrayList();
        Reader clsesReader = Files.getSystemClsesReader();
        KnowledgeBaseFactory factory = getFactory(projectKB);
        String path = (factory == null) ? (String) null : factory.getProjectFilePath();
        Reader instancesReader;
        if (path == null) {
            instancesReader = Files.getSystemInstancesReader();
        }
        else {
            instancesReader = FileUtilities.getResourceReader(factory.getClass(), path);
        }
        return new ClipsKnowledgeBaseFactory().loadKnowledgeBase(clsesReader, instancesReader, errors);
    }


    private static void renameWidget(KnowledgeBase kb, String oldWidgetName, String newWidgetName) {
        changeInstanceValue(kb,
                "Widget",
                "widget_class_name",
                oldWidgetName,
                "widget_class_name",
                oldWidgetName,
                newWidgetName);
    }


    private static boolean replaceFormWidget(String name, KnowledgeBase projectKB, KnowledgeBase templateProjectKB) {
        Instance templateClsWidget = getClsWidgetInstance(name, templateProjectKB);
        Instance projectClsWidget = getClsWidgetInstance(name, projectKB);
        if (projectClsWidget == null) {
            Cls widgetCls = projectKB.getCls("Widget");
            projectClsWidget = projectKB.createInstance(null, widgetCls);
            ModelUtilities.setOwnSlotValue(projectClsWidget, "name", name);
            Instance projectInstance = getProjectInstance(projectKB);
            ModelUtilities.addOwnSlotValue(projectInstance, "customized_instance_widgets", projectClsWidget);
            // Log.trace("added customization for new class " + name, BackwardsCompatibilityProjectFixups.class, "replaceFormWidget");
        }
        Instance templatePropertyList = (Instance) ModelUtilities.getDirectOwnSlotValue(templateClsWidget,
                "property_list");
        Instance newPropertyList = (Instance) templatePropertyList.deepCopy(projectKB, null);
        ModelUtilities.setOwnSlotValue(projectClsWidget, "property_list", newPropertyList);
        return templateClsWidget != null;
    }


    private static void updateStandardForms(KnowledgeBase projectKB) {
        KnowledgeBase templateProjectKB = getTemplateKnowledgeBase(projectKB);
        Instance templateProjectInstance = getProjectInstance(templateProjectKB);
        // Instance projectInstance = getProjectInstance(projectKB);
        Iterator i = ModelUtilities.getDirectOwnSlotValues(templateProjectInstance, "customized_instance_widgets")
                .iterator();
        while (i.hasNext()) {
            Instance widgetInstance = (Instance) i.next();
            String widgetClsName = (String) ModelUtilities.getDirectOwnSlotValue(widgetInstance, "name");
            boolean changed = replaceFormWidget(widgetClsName, projectKB, templateProjectKB);
            // Log.trace(widgetClsName + " form changed= " + changed, BackwardsCompatibilityProjectFixups.class, "updateStandardForms");
        }
    }
}
