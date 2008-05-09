package edu.stanford.smi.protegex.owl.jena.creator;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;

public class OwlProjectFromUriCreator extends AbstractOwlProjectCreator {

    private String ontologyUri;

    private String lang = FileUtils.langXMLAbbrev;
    
    private Project project;
    
    private JenaOWLModel owlModel;

    public  OwlProjectFromUriCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public OwlProjectFromUriCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    
    @SuppressWarnings("unchecked")
    public void create(Collection errors) throws IOException {
        project = Project.createBuildProject(factory, errors);
        owlModel = (JenaOWLModel) project.getKnowledgeBase();
        
        initializeSources(project.getSources());
        URI uri = getBuildProjectURI();
        if (uri != null) {
            project.setProjectURI(uri);
        }
        project.createDomainKnowledgeBase(factory, errors, true);
    }
    
    
    @Override
    public JenaOWLModel getOwlModel() {
        return owlModel;
    }
    
    @Override
    public Project getProject() {
        return project;
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
