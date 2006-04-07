package edu.stanford.smi.protegex.owl.ui.menu.code;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.daml.kazuki.ClassCatalog;
import org.daml.kazuki.Datatypes;
import org.daml.kazuki.GenerateInterface;
import org.daml.kazuki.VocabularyCatalog;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.javacode.JavaCodeGeneratorAction;
import edu.stanford.smi.protegex.owl.jena.OntModelProvider;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.jena.JenaSchemagenAction;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class KazukiAction extends AbstractOWLModelAction {

    public final static String SCHEMA = "Schema";

    public final static String KAZUKI_JAVAC = "KAZUKI-JAVAC";

    public final static String KAZUKI_ROOT_FOLDER = "KAZUKI-ROOT-FOLDER";

    public final static String KAZUKI_PACKAGE = "KAZUKI-PACKAGE";


    public String getMenubarPath() {
        return CODE_MENU + PATH_SEPARATOR + JavaCodeGeneratorAction.GROUP;
    }


    public String getName() {
        return "Generate Kazuki Java classes...";
    }


    public void run(OWLModel owlModel) {
        OWLProject project = owlModel.getOWLProject();
        OntModelProvider ontModelProvider = (OntModelProvider) owlModel;
        KazukiPanel panel = new KazukiPanel();
        panel.setRootFolder(project.getSettingsMap().getString(KAZUKI_ROOT_FOLDER));
        panel.setPackage(project.getSettingsMap().getString(KAZUKI_PACKAGE));
        String javac = project.getSettingsMap().getString(KAZUKI_JAVAC);
        if (javac == null || javac.length() == 0) {
            javac = "javac";
        }
        panel.setJavaC(javac);
        Component comp = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        if (ProtegeUI.getModalDialogFactory().showDialog(comp, panel, getName(), ModalDialogFactory.MODE_OK_CANCEL) ==
                ModalDialogFactory.OPTION_OK) {
            project.getSettingsMap().setString(KAZUKI_PACKAGE, panel.getPackage());
            project.getSettingsMap().setString(KAZUKI_ROOT_FOLDER, panel.getRootFolder());
            project.getSettingsMap().setString(KAZUKI_JAVAC, panel.getJavaC());
            File folder = new File(panel.getRootFolder());
            for (int i = 0; i < 2; i++) {
                try {
                    generate(ontModelProvider, folder,
                            panel.getPackage(),
                            panel.getJavaC(),
                            panel.isOverwriteMode());
                    ProtegeUI.getModalDialogFactory().showMessageDialog(comp, "Schema generated to " + folder + ".");
                    return;
                }
                catch (Exception ex) {
                    if (i > 0) {
                        Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(comp, "Error: " + ex);
                    }
                }
            }
        }
    }


    public static void generate(OntModelProvider ontModelProvider,
                                File folder,
                                String rootPackage,
                                String javac,
                                boolean overwrite) throws Exception {

        Model model = ontModelProvider.getOntModel();

        File longFolder = folder;

        if (rootPackage != null && rootPackage.length() > 0) {
            String packagePath = rootPackage.replace('.', '/');
            longFolder = new File(folder, packagePath);
        }

        if (!longFolder.exists()) {
            longFolder.mkdirs();
        }

        File schemaFile = new File(folder, SCHEMA + ".java");
        System.out.println("Generating schema " + schemaFile);
        JenaSchemagenAction.generate(ontModelProvider, schemaFile, null);
        System.out.println("Compiling schema");
        Runtime.getRuntime().exec(javac + " -classpath plugins/edu.stanford.smi.protegex.owl/jena.jar " +
                schemaFile.toString());
        System.gc();

        GenerateInterface gi = new GenerateInterface();
        gi.OPT_ROOT_DIR = longFolder.getAbsolutePath();
        gi.OPT_ROOT_PACKAGE = rootPackage;
        gi.OPT_CUSTOM_P = true;
        gi.OPT_GENERATE_VOCABULARY = false;
        gi.OPT_VOCAB_DIR = folder.getAbsolutePath();
        gi.OPT_VOCAB_PACKAGE = "";//"vocab";
        gi.OPT_VOCAB_BIN = ""; // "bin";
        gi.OPT_LOAD_IMPORTS = false;
        gi.OPT_DATE_P = true;
        gi.OPT_OVERWRITE_P = overwrite;

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);

        Class klass = Class.forName("com.hp.hpl.jena.rdf.model.Resource");
        ClassLoader loader = klass.getClassLoader();
        VocabularyClassLoader vcl = new VocabularyClassLoader(loader, folder.toString());
        VocabularyCatalog vc = new VocabularyCatalog(vcl);
        vc.parseVocabulary(SCHEMA);
        gi.generateInterface(ontModel, vc, new ClassCatalog(), new Datatypes());

        schemaFile.delete();

        JenaSchemagenAction.generate(ontModelProvider, new File(longFolder, "base/" + SCHEMA + ".java"), rootPackage + ".base");
        JenaSchemagenAction.generate(ontModelProvider, new File(longFolder, "custom/" + SCHEMA + ".java"), rootPackage + ".custom");
    }


    public static class VocabularyClassLoader extends ClassLoader {

        String _sRoot;


        public VocabularyClassLoader(String sPackageRoot) {
            super(Thread.currentThread().getContextClassLoader());
            _sRoot = sPackageRoot;
        }


        public VocabularyClassLoader(ClassLoader parent, String sPackageRoot) {
            super(parent);
            _sRoot = sPackageRoot;
        }


        protected Class findClass(String sClass) throws ClassNotFoundException {
            StringTokenizer st = new StringTokenizer(sClass, ".");
            StringBuffer sPath = new StringBuffer(_sRoot);
            while (st.hasMoreTokens()) {
                sPath.append("/" + st.nextToken());
            }
            sPath.append(".class");
            File f = new File(sPath.toString());
            if (!f.exists()) {
                throw new ClassNotFoundException(f.toString());
            }
            try {
                FileInputStream fis = new FileInputStream(f);
                byte[] b = new byte[(int) f.length()];
                fis.read(b);
                return defineClass(sClass, b, 0, b.length);
            }
            catch (FileNotFoundException e) {
                throw new ClassNotFoundException();
            }
            catch (IOException e) {
                throw new ClassNotFoundException();
            }
        }
    }
}
