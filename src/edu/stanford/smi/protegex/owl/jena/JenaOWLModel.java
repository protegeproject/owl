package edu.stanford.smi.protegex.owl.jena;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.dig.DIGReasoner;
import com.hp.hpl.jena.reasoner.dig.DIGReasonerFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protegex.owl.jena.creator.JenaCreator;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.jena.protege2jena.Protege2Jena;
import edu.stanford.smi.protegex.owl.jena.triplestore.JenaTripleStoreModel;
import edu.stanford.smi.protegex.owl.jena.writersettings.JenaWriterSettings;
import edu.stanford.smi.protegex.owl.jena.writersettings.WriterSettings;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelAllTripleStoresWriter;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.ProtegeWriterSettings;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriterPreferences;

/**
 * An OWLModel that can be synchronized with a Jena OntModel.
 *
 * @author Holger Knublauch  <holger@smi.stanford.edu>
 */
public class JenaOWLModel extends AbstractOWLModel implements OntModelProvider {

    public final static String COPYRIGHT =
            "<!-- Created with Protege (with OWL Plugin " +
                    OWLText.getVersion() + ", Build " +
                    OWLText.getBuildNumber() + ")  http://protege.stanford.edu -->";


    public static boolean inUI = false;

    private TripleStoreModel tripleStoreModel;


    public static final String TEMPLATE_FILE_NAME = "plugins/owl/template.owl";

    public static final String DEFAULT_PREFIX = "default";

    public final static String WRITER_SETTINGS_PROPERTY = JenaOWLModel.class.getName() + ".writer";

    public final static String WRITER_PROTEGE = "protege";


    protected JenaOWLModel(KnowledgeBaseFactory factory, NamespaceManager namespaceManager) {
        super(factory, namespaceManager);
        OWLJavaFactoryUpdater.run(this);
        MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(this);
        mnfs.setTopFrameStore(mnfs.getActiveFrameStore().getName());
    }


    private void closeRDFLists() {
        for (Iterator it = getCls(RDFNames.Cls.LIST).getInstances().iterator(); it.hasNext();) {
            Instance instance = (Instance) it.next();
            if (instance instanceof RDFList &&
                    instance.isEditable()) {
                RDFList li = (RDFList) instance;
                if (li.getRest() == null) {
                    li.setRest(getRDFNil());
                }
            }
        }
    }


    // Implements NamespaceManagerListener
    public void defaultNamespaceChanged(String oldValue, String newValue) {
        super.defaultNamespaceChanged(oldValue, newValue);
    }


    public RDFResource getRDFResource(Resource resource) {
        final String uri = resource.getURI();
        final String frameName = getResourceNameForURI(uri);
        if (frameName != null) {
            return (RDFResource) getFrame(frameName);
        }
        else {
            return null;
        }
    }


    // Implements OntModelProvider
    public OntModel getOntModel() {
        // TODO buffer recent OntModel until changed
        return Protege2Jena.createOntModel(this);

        //JenaCreator creator = new JenaCreator(this, false, null,
        //        inUI ? new ModalProgressBarManager("Preparing Ontology") : null);
        //return creator.createOntModel();
    }


    // Implements OntModelProvider
    public OntModel getOWLDLOntModel() {
        JenaCreator creator = new JenaCreator(this, true, null,
                inUI ? new ModalProgressBarManager("Preparing Ontology") : null);
        return creator.createOntModelWithoutOWLFullModel();
    }


    /**
     * Gets the currently assigned file name for the top-level file of this OWLModel.
     * Note that this only returns a meaningful value after the user has assigned a name
     * (using save as...)
     *
     * @return the file name
     */
    public String getOWLFilePath() {
        return getOWLProject().getSettingsMap().getString(JenaKnowledgeBaseFactory.OWL_FILE_URI_PROPERTY);
    }


    public int getOWLSpecies() {
        OntModel ontModel = Protege2Jena.createOntModel(this);
        //JenaCreator creator = new JenaCreator(this, false, null,
        //        inUI ? new ModalProgressBarManager("Preparing Ontology") : null);
        //OntModel ontModel = creator.createOntModelWithoutOWLFullModel();
        return Jena.getOWLSpecies(ontModel);
    }


    // Implements OntModelProvider
    public OntModel getReasonerOntModel(String classifierURL) {
        com.hp.hpl.jena.rdf.model.Model newModel = ModelFactory.createDefaultModel();
        Resource resource = newModel.createResource("http://foo.de#foo");
        newModel.add(resource, ReasonerVocabulary.EXT_REASONER_URL, classifierURL);

        DIGReasoner reasoner = (DIGReasoner) ReasonerRegistry.theRegistry().
                create(DIGReasonerFactory.URI, resource);

        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
        spec.setReasoner(reasoner);
        return Jena.cloneOntModel(getOntModel(), spec);
    }

    public void setTripleStoreModel(TripleStoreModel tsm) {
      tripleStoreModel = tsm;
    }

    public TripleStoreModel getTripleStoreModel() {
        if (tripleStoreModel == null) {
            tripleStoreModel = new JenaTripleStoreModel(this);
        }
        return tripleStoreModel;
    }


    public WriterSettings getWriterSettings() {
        String value = getOWLProject().getSettingsMap().getString(WRITER_SETTINGS_PROPERTY);
        if (WRITER_PROTEGE.equals(value)) {
            return new ProtegeWriterSettings(this);
        }
        else {
            return new JenaWriterSettings(this);
        }
    }


    public void initOWLFrameFactoryInvocationHandler() {
        setFrameFactory(new OWLJavaFactory(this));
    }


    public void initPrefixes(OntModel ontModel) {
        NamespaceManager nsm = getNamespaceManager();
        initPrefixes(nsm, ontModel);
        String defaultNamespace = ontModel.getNsPrefixURI("");
        if (defaultNamespace != null) {
            nsm.setDefaultNamespace(defaultNamespace);
        }

        for (Iterator it = ontModel.getSubGraphs().iterator(); it.hasNext();) {
            Graph graph = (Graph) it.next();
            PrefixMapping mapping = graph.getPrefixMapping();
            initPrefixes(nsm, mapping);
        }
    }


    private void initPrefixes(NamespaceManager nsm, PrefixMapping mapping) {
        Map map = mapping.getNsPrefixMap();
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            if (prefix.length() > 0) {
                String uri = (String) map.get(prefix);
                if (nsm.getPrefix(uri) == null && nsm.getNamespaceForPrefix(prefix) == null) {
                    nsm.setPrefix(uri, prefix);
                }
                else {
                    String oldNS = nsm.getNamespaceForPrefix(prefix);
                    if (oldNS != null && !oldNS.equals(uri)) {
                        System.err.println("[JenaOWLModel] Error: Conflicting prefix " +
                                prefix + " (was: " + oldNS + ") was redefined as " + uri);
                    }
                }
            }
        }
    }


    public void load(URI uri, String language) throws Exception {
        ProtegeOWLParser parser = new ProtegeOWLParser(this, false);
        parser.run(uri);
        TripleStoreUtil.sortSubclasses(this);
        copyFacetValuesIntoNamedClses();
    }


    public void load(InputStream is, String language) throws Exception {
        ProtegeOWLParser parser = new ProtegeOWLParser(this, false);
        parser.run(is, "http://dummy-ontologies.com/dummy.owl");
        TripleStoreUtil.sortSubclasses(this);
        copyFacetValuesIntoNamedClses();
    }


    public void load(Reader reader, String language) throws Exception {
        ProtegeOWLParser parser = new ProtegeOWLParser(this, false);
        parser.run(reader, "http://dummy-ontologies.com/dummy.owl");
        TripleStoreUtil.sortSubclasses(this);
        copyFacetValuesIntoNamedClses();
    }


    public void load(URI uri, String language, Collection errors) {
        try {
            load(uri, language);
        }
        catch (Throwable t) {
            Log.getLogger().log(Level.SEVERE, "Error at loading file "+uri, t);
            
            Collection parseErrors = ProtegeOWLParser.getErrors(); 
            if (parseErrors != null && parseErrors.size() > 0) {
            	errors.addAll(parseErrors);
            }
            
            errors.add(t);
            
            String message = "Errors at loading OWL file from " + uri + "\n";
            message = message + "\nPlease consider running the file through an RDF or OWL validation service such as:";
            message = message + "\n  - RDF Validator: http://www.w3.org/RDF/Validator";
            message = message + "\n  - OWL Validator: http://phoebus.cs.man.ac.uk:9999/OWL/Validator";
            if (getNamespaceManager().getPrefix("http://protege.stanford.edu/system#") != null ||
                    getNamespaceManager().getPrefix("http://protege.stanford.edu/kb#") != null) {
                message = message + "\nThis file seems to have been created with the frame-based Protege RDF Backend. " +
                		"Please try to use the RDF Backend of Protege to open this file and then export it to OWL " +
                		"using Export to Format...";
            }
   
         	errors.add(new MessageError(message));
        }
    }


    // Implements NamespaceManagerListener
    public void namespaceChanged(String prefix, String oldValue, String newValue) {
        super.namespaceChanged(prefix, oldValue, newValue);
    }


    // Implements NamespaceManagerListener
    public void prefixAdded(String prefix) {
        super.prefixAdded(prefix);
    }


    // Implements NamespaceManagerListener
    public void prefixChanged(String namespace, String oldPrefix, String newPrefix) {
        super.prefixChanged(namespace, oldPrefix, newPrefix);
    }


    // Implements NamespaceManagerListener
    public void prefixRemoved(String prefix) {
        super.prefixRemoved(prefix);
    }


    /**
     * Saves the current OWLModel in the standard format.
     *
     * @param fileURI the URI to write into
     * @throws Exception if something went wrong
     */
    public void save(URI fileURI) throws Exception {
        Protege2Jena.saveAll(this, fileURI, FileUtils.langXMLAbbrev);
    }


    /**
     * Writes the base model of this into a given file.
     *
     * @param fileURI  the URI of the target file
     * @param language the Jena output language (typically FileUtils.langXMLAbbrev)
     * @param errors   an initially empty collection of errors
     */
    public void save(URI fileURI, String language, Collection errors) {
        if (getWriterSettings() instanceof JenaWriterSettings) {
            try {
                Protege2Jena.saveAll(this, fileURI, language);
            }
            catch (Exception ex) {
            	String message = "Failed to save file " + fileURI + " using Protege2Jena."; 
            	Log.getLogger().log(Level.SEVERE, message, ex);
                errors.add(new MessageError(ex, message));                
            }
        }
        else if (getWriterSettings() instanceof ProtegeWriterSettings) {
            ProtegeWriterSettings ws = (ProtegeWriterSettings) getWriterSettings();
            try {
                boolean useEntities = ws.getUseXMLEntities();
                XMLWriterPreferences.getInstance().setUseNamespaceEntities(useEntities);
                OWLModelAllTripleStoresWriter writer = new OWLModelAllTripleStoresWriter(this, fileURI,
                        ws.isSortAlphabetically());
                writer.write();
            }
            catch (Exception ex) {
               	String message = "Failed to save file " + fileURI; 
            	Log.getLogger().log(Level.SEVERE, message, ex);
                errors.add(new MessageError(ex, message));
            }
        }
    }


    /**
     * @deprecated please use the version with the URIs or access the OntModel directly
     */
    public void save(OutputStream os, String language, Collection errors) {
        closeRDFLists();
        save(os, language, errors, getOntModel());
    }


    public void save(URI fileURI, String language, Collection errors, OntModel ontModel) {
        try {
            File file = new File(fileURI);
            String namespace = getNamespaceManager().getDefaultNamespace();
            save(file, ontModel, language, namespace);
        }
        catch (Throwable t) {
           	String message = "Failed to save file " + fileURI; 
        	Log.getLogger().log(Level.SEVERE, message, t);
            errors.add(new MessageError(new Exception(t), message));
        }
    }


    public static void save(File file, OntModel ontModel, String language, String namespace) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        save(outputStream, ontModel, language, namespace);
    }


    /**
     * @deprecated please use the version with the URIs or access the OntModel directly
     */
    public void save(OutputStream os, String language, Collection errors, OntModel ontModel) {
        try {
            String namespace = getNamespaceManager().getDefaultNamespace();
            save(os, ontModel, language, namespace);
        }
        catch (Throwable t) {
           	String message = "Failed to save file to output stream"; 
        	Log.getLogger().log(Level.SEVERE, message, t);
            errors.add(new MessageError(new Exception(t), message));
        }
    }


    private static void save(OutputStream outputStream, OntModel ontModel, String language, String namespace) throws IOException {
        saveModel(outputStream, ontModel.getBaseModel(), language, namespace);
    }


    public static void saveModel(OutputStream outputStream, Model model, String language, String namespace) throws IOException {
        PrintStream ps = new PrintStream(outputStream);
        RDFWriter writer = model.getWriter(language);
        Jena.prepareWriter(writer, language, namespace);
        boolean xml = Jena.isXMLLanguage(language);
        if (xml) {
            String encoding = SystemUtilities.getFileEncoding();
            Collection charsets = Charset.availableCharsets().keySet();
            if (!charsets.contains(encoding)) {
                encoding = "UTF-8";
            }
            //ps.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>");
            // Jena will save in <encoding>, and add xml-declaration
            writer.write(model, new OutputStreamWriter(ps, encoding), namespace);
        }
        else {
            // Jena will save in UTF-8
            model.removeNsPrefix("");
            model.setNsPrefix(DEFAULT_PREFIX, namespace);
            try {
                writer.write(model, ps, namespace);
            }
            finally {
                model.removeNsPrefix(DEFAULT_PREFIX);
                model.setNsPrefix("", namespace);
            }
        }
        //writer.write(model.getBaseModel(), ps, namespace);
        if (xml) {
            ps.println();
            ps.println(COPYRIGHT);
        }
        outputStream.close();
    }


    /**
     * Sets the name of the file in the associated Project.
     * This should be called before a newly created Project is saved.
     *
     * @param filePath the new (typically relative) path to the .owl file
     * @see edu.stanford.smi.protege.model.Project#save
     */
    public void setProjectFileName(String filePath) {
        // Ensure the path is a URI
        try {
            URI uri = new URI(filePath);
            getOWLProject().getSettingsMap().setString(JenaKnowledgeBaseFactory.OWL_FILE_LANGUAGE_PROPERTY, FileUtils.langXMLAbbrev);
            getOWLProject().getSettingsMap().setString(JenaKnowledgeBaseFactory.OWL_FILE_URI_PROPERTY, uri.toString());
        } catch (URISyntaxException e) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void setWriterSettings(WriterSettings writerSettings) {
        if (writerSettings instanceof ProtegeWriterSettings) {
            getOWLProject().getSettingsMap().setString(WRITER_SETTINGS_PROPERTY, WRITER_PROTEGE);
        }
        else {
            getOWLProject().getSettingsMap().remove(WRITER_SETTINGS_PROPERTY);
        }
    }
}
