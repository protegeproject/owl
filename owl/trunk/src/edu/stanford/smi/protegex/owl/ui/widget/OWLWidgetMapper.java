package edu.stanford.smi.protegex.owl.ui.widget;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.BooleanListWidget;
import edu.stanford.smi.protege.widget.CheckBoxWidget;
import edu.stanford.smi.protege.widget.DefaultWidgetMapper;
import edu.stanford.smi.protege.widget.DirectInstancesWidget;
import edu.stanford.smi.protege.widget.FloatFieldWidget;
import edu.stanford.smi.protege.widget.FloatListWidget;
import edu.stanford.smi.protege.widget.InstanceFieldWidget;
import edu.stanford.smi.protege.widget.InstanceListWidget;
import edu.stanford.smi.protege.widget.IntegerFieldWidget;
import edu.stanford.smi.protege.widget.IntegerListWidget;
import edu.stanford.smi.protege.widget.StringListWidget;
import edu.stanford.smi.protege.widget.TextAreaWidget;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protege.widget.UniqueStringWidget;
import edu.stanford.smi.protege.widget.YellowStickyWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLWidgetMapper extends DefaultWidgetMapper {
  private static Logger log = Log.getLogger(OWLWidgetMapper.class);
  
  // The empty catch blocks suggest that exceptions are very common in this 
  // class.  How common?  Is this mechanism appropriate?
  private static boolean warnedAboutExceptions = false;

    private static Map className2Metadata;


    private final static String[] incompatibleWidgetClassNames = {
            CheckBoxWidget.class.getName(),
            DirectInstancesWidget.class.getName(),
            FloatFieldWidget.class.getName(),
            FloatListWidget.class.getName(),
            InstanceFieldWidget.class.getName(),
            InstanceListWidget.class.getName(),
            IntegerFieldWidget.class.getName(),
            IntegerListWidget.class.getName(),
            ResourceListWidget.class.getName(),
            StringListWidget.class.getName(),
            TextAreaWidget.class.getName(),
            TextFieldWidget.class.getName(),
            UniqueStringWidget.class.getName(),
            YellowStickyWidget.class.getName(),
            "edu.stanford.smi.protegex.widget.contains.ContainsWidget",
            "edu.stanford.smi.protegex.widget.instancetable.InstanceRowWidget"
    };


    public OWLWidgetMapper(OWLModel owlModel) {
        super(owlModel.getProject().getInternalProjectKnowledgeBase());
    }


    public String getDefaultWidgetClassName(Cls cls, Slot slot, Facet facet) {
        return getDefaultWidgetClassName(cls, slot, facet, false);
    }


    public String getDefaultWidgetClassName(Cls cls, Slot slot, Facet facet, boolean forceNotNull) {
        if (slot instanceof RDFProperty && cls instanceof RDFSNamedClass) {

            if (!cls.hasTemplateSlot(slot)) {
                return null;
            }

            RDFSNamedClass namedClass = (RDFSNamedClass) cls;
            RDFProperty rdfProperty = (RDFProperty) slot;

            if (rdfProperty.isAnnotationProperty()) {
                return null;
            }
            else if (!rdfProperty.isDomainDefined(true)) {
                if (!OWLWidgetUtil.isRestrictedProperty(namedClass, rdfProperty) && !forceNotNull) {
                    return null;
                }
            }

            String defaultClassName = PluginUtilities.getDefaultWidgetClassName(rdfProperty);
            if (defaultClassName != null) {
                return defaultClassName;
            }

            // Suppress properties with maximum cardinality of 0
            if (namedClass instanceof OWLNamedClass) {
                OWLNamedClass owlNamedClass = (OWLNamedClass) namedClass;
                Collection ps = new ArrayList();
                ps.add(rdfProperty);
                ps.addAll(rdfProperty.getSuperproperties(true));
                for (Iterator it = ps.iterator(); it.hasNext();) {
                    RDFProperty property = (RDFProperty) it.next();
                    if (owlNamedClass.getMaxCardinality(property) == 0) {
                        return null;
                    }
                }
            }

            initWidgetMetadata();
            int bestSuitability = OWLWidgetMetadata.NOT_SUITABLE;
            String bestClassName = null;
            for (Iterator it = className2Metadata.keySet().iterator(); it.hasNext();) {
                String className = (String) it.next();
                OWLWidgetMetadata metadata = (OWLWidgetMetadata) className2Metadata.get(className);
                if (metadata != null) {
                    int suitability = metadata.getSuitability(namedClass, rdfProperty);
                    if (suitability > bestSuitability) {
                        bestClassName = className;
                        bestSuitability = suitability;
                    }
                }
            }
            if (bestClassName != null && bestSuitability > OWLWidgetMetadata.SUITABLE) {
                return bestClassName;
            }

            Class[] widgetClasses = new Class[]{
                    DataRangeFieldWidget.class,
                    OWLDateWidget.class,
                    OWLTimeWidget.class,
                    OWLDateTimeWidget.class,
                    RDFListWidget.class,
                    MultiResourceWidget.class,
                    SingleResourceWidget.class,
                    BooleanListWidget.class,
                    MultiLiteralWidget.class,
                    SingleLiteralWidget.class
            };
            Object[] args = new Object[]{
                    cls, slot, facet
            };
            for (int i = 0; i < widgetClasses.length; i++) {
                Class widgetClass = widgetClasses[i];
                if (isSuitable(widgetClass, cls, slot)) {
                    return widgetClass.getName();
                }
                try {
                    Method method = widgetClass.getMethod("isSuitable", new Class[]{
                            Cls.class, Slot.class, Facet.class
                    });
                    if (Boolean.TRUE.equals(method.invoke(null, args))) {
                        return widgetClass.getName();
                    }
                }
                catch (Exception ex) {
                  // Empty catch blocks are really dangerous!
                  if (!warnedAboutExceptions) {
                    log.info("Exceptions caught in OWLWidgetMapper including " + ex);
                    warnedAboutExceptions = true;
                  }
                  if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE, "Exception caught in OWLWidgetMapper", ex);
                  }
                }
            }
        }
        return super.getDefaultWidgetClassName(cls, slot, facet);
    }


    public static OWLWidgetMetadata getOWLWidgetMetadata(String className) {
        initWidgetMetadata();
        return (OWLWidgetMetadata) className2Metadata.get(className);
    }


    public Collection getSuitableWidgetClassNames(Cls cls, Slot slot, Facet facet) {
        Collection ss = new ArrayList(super.getSuitableWidgetClassNames(cls, slot, facet));
        for (int i = 0; i < incompatibleWidgetClassNames.length; i++) {
            String className = incompatibleWidgetClassNames[i];
            ss.remove(className);
        }
        return ss;
    }


    private static void initWidgetMetadata() {
        if (className2Metadata == null) {
            className2Metadata = new HashMap();
            Collection classNames = PluginUtilities.getAvailableSlotWidgetClassNames();
            for (Iterator it = classNames.iterator(); it.hasNext();) {
                String clsName = (String) it.next();
                String metadataClassName = clsName + "Metadata";
                try {
                    Class metadataClass = PluginUtilities.forName(metadataClassName, true);
                    if (metadataClass == null) {
                      return;
                    }
                    Object metadata = metadataClass.newInstance();
                    className2Metadata.put(clsName, metadata);
                }
                catch (Exception ex) {
                  // Empty catch blocks are really dangerous!
                  if (!warnedAboutExceptions) {
                    log.info("Exceptions found in OWLWidgetMapper including " + ex);
                    warnedAboutExceptions = true;
                  }
                  if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE, "Exception caught", ex);
                  }
                }
            }
        }
    }


    public static boolean isIncompatibleWidgetName(String className) {
        for (int i = 0; i < incompatibleWidgetClassNames.length; i++) {
            String incompatiblewidgetclassname = incompatibleWidgetClassNames[i];
            if (className.equals(incompatiblewidgetclassname)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isSuitable(Class clazz, Cls cls, Slot slot) {
        if (slot instanceof RDFProperty && cls instanceof RDFSNamedClass) {
            RDFProperty property = (RDFProperty) slot;
            String className = clazz.getName();
            OWLWidgetMetadata metadata = OWLWidgetMapper.getOWLWidgetMetadata(className);
            if (metadata != null) {
                RDFSNamedClass namedClass = (RDFSNamedClass) cls;
                return metadata.getSuitability(namedClass, property) > OWLWidgetMetadata.NOT_SUITABLE;
            }
        }
        return false;
    }
}
