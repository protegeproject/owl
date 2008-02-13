package edu.stanford.smi.protegex.owl.jena.creator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;
import edu.stanford.smi.protegex.owl.model.util.XSDVisibility;
import edu.stanford.smi.protegex.owl.swrl.ui.SWRLProjectPlugin;
import edu.stanford.smi.protegex.owl.ui.menu.OWLMenuProjectPlugin;

public class OwlProjectFromUriCreator extends AbstractOwlProjectCreator {

    private String ontologyUri;

    private String lang = FileUtils.langXMLAbbrev;

    public  OwlProjectFromUriCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public OwlProjectFromUriCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    
    @SuppressWarnings("unchecked")
    public Project create()  {
        Collection errors = new ArrayList();
        Project project = Project.createBuildProject(factory, errors);
        load(project, errors);

        handleErrors(errors);
        return project;
    }
    
    protected void load(Project project, Collection errors) {
        initializeSources(project.getSources());
        URI uri = getBuildProjectURI();
        if (uri != null) {
            project.setProjectURI(uri);
        }
        project.createDomainKnowledgeBase(factory, errors, true);
    }
    
    
    protected void initializeSources(PropertyList sources) {
        JenaKnowledgeBaseFactory.setOWLFileName(sources, ontologyUri);
        JenaKnowledgeBaseFactory.setOWLFileLanguage(sources, lang);
        addViewSettings(sources);
    }
    
    protected URI getBuildProjectURI() {
        if (ontologyUri != null) {
            if (ontologyUri.startsWith("file:")) {
                int index = ontologyUri.lastIndexOf('.');
                if (index > 0) {
                    String uri = FileUtilities.replaceExtension(ontologyUri, ".pprj");
                    try {
                        return new URI(uri);
                    }
                    catch (Exception ex) {
                      Log.emptyCatchBlock(ex);
                    }
                }
            }
        }
        return null;
    }
    
    /*
     * ------------------------------------------------------
     * setters and getters
     */

    public void setOntologyUri(String fileURI) {
        this.ontologyUri = fileURI;
    }
   

    public void setLang(String lang) {
        this.lang = lang;
    }



}
