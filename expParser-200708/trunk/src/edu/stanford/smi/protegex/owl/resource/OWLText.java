package edu.stanford.smi.protegex.owl.resource;

import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Mar-2006
 */
public final class OWLText {
    private static String buildFile = "build.properties";
    private static String directory = "files";
    private static Properties props;

    static {
        try {
            props = new Properties();
            InputStream stream = FileUtilities.getResourceStream(OWLText.class, directory, buildFile);
            props.load(stream);
        } catch (IOException e) {
            Log.getLogger().severe(Log.toString(e));
        }
    }

    public static String getName() {
        return props.getProperty("name", "Prot\u00E9g\u00E9-OWL");
    }

    public static String getBuildInfo() {
        return "Build " + getBuildNumber();
    }

    public static int getBuildNumber() {
        return Integer.parseInt(props.getProperty("build.number", "?"));
    }

    public static String getStatus() {
        return props.getProperty("build.status");
    }

    public static String getVersion() {
        return props.getProperty("build.version", "?");
    }

    public static int getLatestCompatibleBuild(){
        return Integer.parseInt(props.getProperty("build.compatible.latest", "1"));
    }

    public static int getRequiresProtegeBuild(){
        return Integer.parseInt(props.getProperty("build.requires.protege.build", "1"));
    }
}
