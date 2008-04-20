package edu.stanford.smi.protegex.owl.jena;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.AbstractCreateProjectPlugin;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.XSPNames;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.model.util.XSDVisibility;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.ui.SWRLProjectPlugin;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchClassDefinitionResourceDisplayPlugin;
import edu.stanford.smi.protegex.owl.ui.jena.OWLFilesWizardPage;
import edu.stanford.smi.protegex.owl.ui.menu.OWLMenuProjectPlugin;
import edu.stanford.smi.protegex.owl.ui.metadatatab.OntologyURIWizardPage;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfileSelectionWizardPage;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFilesCreateProjectPlugin
        extends AbstractCreateProjectPlugin
        implements OWLFilesPlugin {
    private static transient Logger log = Log.getLogger(OWLFilesCreateProjectPlugin.class);

    private Class defaultClassView;

    private String defaultNamespace;

    private Collection imports = new ArrayList();

    private Map importPrefixes = new HashMap();

    private String fileURI;

    private String lang;

    private String profileURI;


    public OWLFilesCreateProjectPlugin() {
        super("OWL Files");
        JenaKnowledgeBaseFactory.useStandalone = false;
    }


    public void addImport(String uri, String prefix) {
        imports.add(uri);
        importPrefixes.put(uri, prefix);
    }


    protected void addImports(Project project) {
        JenaOWLModel owlModel = (JenaOWLModel) project.getKnowledgeBase();

        if (imports.contains(SWRLNames.SWRL_IMPORT)) {
            Collection tabWidgetDescriptors = project.getTabWidgetDescriptors();
            WidgetDescriptor w = project.getTabWidgetDescriptor(SWRLTab.class.getName());
            w.setVisible(true);
            project.setTabWidgetDescriptorOrder(tabWidgetDescriptors);
            owlModel.getNamespaceManager().setPrefix(SWRLNames.SWRL_NAMESPACE, SWRLNames.SWRL_PREFIX);
            owlModel.getNamespaceManager().setPrefix(SWRLNames.SWRLB_NAMESPACE, SWRLNames.SWRLB_PREFIX);
        }

        ImportHelper importHelper = new ImportHelper(owlModel);
        for (Iterator it = imports.iterator(); it.hasNext();) {
            String uri = (String) it.next();
            String prefix = (String) importPrefixes.get(uri);
            String namespace = uri;
            if (!namespace.endsWith("#") && !namespace.endsWith("/")) {
                namespace += "#";
            }
            owlModel.getNamespaceManager().setPrefix(namespace, prefix);
            if (namespace.equals(ProtegeNames.NS)) {
                owlModel.getNamespaceManager().setPrefix(XSPNames.NS, XSPNames.DEFAULT_PREFIX);
            }
            try {
                URI u = new URI(uri);
                importHelper.addImport(u);
            }
            catch (URISyntaxException e) {
                log.log(Level.SEVERE, "Exception caught", e);
            }
        }
        try {
            importHelper.importOntologies();
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, "Exception caught", ex);
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                                                     "Could not load import:\n" + ex);
        }
        if (imports.contains(SWRLNames.SWRL_IMPORT)) {
            SWRLProjectPlugin.adjustWidgets(project);
        }
        if (!imports.isEmpty()) {
            owlModel.getTripleStoreModel().updateEditableResourceState();
        }
    }


    private void addViewSettings(PropertyList sources) {
        String typeName = null;
        if (defaultClassView == null) {
            typeName = SwitchClassDefinitionResourceDisplayPlugin.getDefaultClassView();
        }
        else {
            typeName = defaultClassView.getName();
        }
        SwitchClassDefinitionResourceDisplayPlugin.setClassesView(sources, typeName);
        SwitchClassDefinitionResourceDisplayPlugin.setDefaultClassesView(typeName);
        if (profileURI != null) {
            ProfilesManager.setProfile(sources, profileURI);
        }
    }


    protected Project buildNewProject(KnowledgeBaseFactory factory) {
        ProtegeOWLParser.inUI = true;
        Project project = buildNewProject2(factory);
        if (project != null) {
            OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
            OWLMenuProjectPlugin.makeHiddenClsesWithSubclassesVisible(owlModel);
            if (SWRLProjectPlugin.isSWRLImported(owlModel)) {
                SWRLProjectPlugin.adjustWidgets(project);
            }
            XSDVisibility.updateVisibility(owlModel);
        }
        ProtegeOWLParser.inUI = true;
        return project;
    }


    private Project buildNewProject2(KnowledgeBaseFactory factory) {
        Collection errors = new ArrayList();
        Project project = Project.createNewProject(factory, errors);
        initialize(project);
        URI uri = getBuildProjectURI();
        if (uri != null) {
            project.setProjectURI(uri);
        }
        project.createDomainKnowledgeBase(factory, errors, true);
        handleErrors(errors);
        return project;
    }


    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return factory.getClass() == JenaKnowledgeBaseFactory.class;
    }


    public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard, boolean useExistingSources) {
        ProfileSelectionWizardPage.isBuild = useExistingSources;
        if (useExistingSources) {
            return new OWLFilesWizardPage(wizard, this);
        }
        else {
            return new OntologyURIWizardPage(wizard, this);
        }
    }


    protected Project createNewProject(KnowledgeBaseFactory factory) {
        Collection errors = new ArrayList();
        Project project = Project.createNewProject(factory, errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        if (defaultNamespace != null) {
            owlModel.getNamespaceManager().setDefaultNamespace(defaultNamespace);
        }
        addViewSettings(project.getSources());
        addImports(project);
        OWLMenuProjectPlugin.makeHiddenClsesWithSubclassesVisible(owlModel);
        return project;
    }


    protected URI getBuildProjectURI() {
        if (fileURI != null) {
            if (fileURI.startsWith("file:")) {
                int index = fileURI.lastIndexOf('.');
                if (index > 0) {
                    String uri = FileUtilities.replaceExtension(fileURI, ".pprj");
                    try {
                        return new URI(uri);
                    }
                    catch (Exception ex) {
                      Log.emptyCatchBlock(ex);
                    }
                }
            }
        }
        return super.getBuildProjectURI();
    }


    protected void initializeSources(PropertyList sources) {
        JenaKnowledgeBaseFactory.setOWLFileName(sources, fileURI);
        JenaKnowledgeBaseFactory.setOWLFileLanguage(sources, lang);
        addViewSettings(sources);
    }

    /**
     * @deprecated This method will soon be removed
     */
    public void setDublinCoreRedirectToDLVersion(boolean b) {
        // Do nothing
    }

    public void setFile(String fileURI) {
        this.fileURI = fileURI;
    }


    public void setLanguage(String lang) {
        this.lang = lang;
    }


    public void setDefaultClassView(Class typeClass) {
        this.defaultClassView = typeClass;
        SwitchClassDefinitionResourceDisplayPlugin.setDefaultClassesView(typeClass.getName());
    }


    public void setDefaultNamespace(String namespace) {
        this.defaultNamespace = namespace;
    }


    public void setProfile(String profileURI) {
        this.profileURI = profileURI;
    }
}
