package edu.stanford.smi.protegex.owl.ui.resourcedisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JPanel;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * The manager of all InstanceDisplayPlugins.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceDisplayPluginManager {

    private static Map map = new HashMap();

    private static Set userDefinedClasses = new HashSet();


    /**
     * A work-around method for InstanceDisplayPlugins from outside the OWL Plugin.
     * Since due to a bug in the Java virtual machine these Classes
     * can not be found through their manifest, they need to be added
     * manually, preferably through a ProjectPlugin.
     *
     * @param clazz the Class of a ResourceDisplayPlugin to add
     */
    public static void addInstanceDisplayPluginClass(Class clazz) {
        userDefinedClasses.add(clazz);
    }


    public static ResourceDisplayPlugin getInstanceDisplayPlugin(Class clazz) {
        ResourceDisplayPlugin plugin = (ResourceDisplayPlugin) map.get(clazz);
        if (plugin == null) {
            try {
                plugin = (ResourceDisplayPlugin) clazz.newInstance();
                map.put(clazz, plugin);
            }
            catch (Exception ex) {
                System.err.println("[ResourceDisplayPluginManager] Fatal Error: Could not create Plugin for " + clazz);
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
        }
        return plugin;
    }


    public static Class[] getInstanceDisplayPluginClasses() {
        Collection clses = new ArrayList(PluginUtilities.getClassesWithAttribute("ResourceDisplayPlugin", "True"));
        clses.addAll(userDefinedClasses);
        Class[] classes = (Class[]) clses.toArray(new Class[0]);
        Arrays.sort(classes, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 instanceof Class && o2 instanceof Class) {
                    return ((Class) o1).getName().compareTo(((Class) o2).getName());
                }
                return 0;
            }
        });
        return classes;
    }


    /**
     * Calls the corresponding method in all currently installed InstanceDisplayPlugins.
     *
     * @param frame     the currently selected Frame
     * @param hostPanel the JPanel to add stuff into (if requested by the plugins)
     */
    public static void initInstanceDisplay(RDFResource frame, JPanel hostPanel) {
        Class[] classes = getInstanceDisplayPluginClasses();
        // System.out.println("Installed InstanceDisplayPlugins:");
        for (int i = 0; i < classes.length; i++) {
            Class c = classes[i];
            ResourceDisplayPlugin plugin = getInstanceDisplayPlugin(c);
            // System.out.println("- " + c + ": " + plugin);
            if (plugin != null) {
                plugin.initResourceDisplay(frame, hostPanel);
            }
        }
    }
}
