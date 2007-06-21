package edu.stanford.smi.protegex.owl.repository.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryUtil;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RelativeFileRepositoryFactoryPlugin implements RepositoryFactoryPlugin {

    public final static String RELATIVE_FILE_PREFIX = LocalFileRepositoryFactoryPlugin.FILE_PREFIX + "./";

    public boolean isSuitable(OWLModel model, String s) {
        return s.trim().startsWith(RelativeFileRepositoryFactoryPlugin.RELATIVE_FILE_PREFIX) &&
                    getRepositoryFile(model, s) != null;
    }


    public Repository createRepository(OWLModel model, String s) {
        try {
            URI uri = new URI(s);
            return new LocalFileRepository(getRepositoryFile(model, s), 
                                           RepositoryUtil.isForcedToBeReadOnly(uri.getQuery()));
        }
        catch (URISyntaxException e) {
            return null;
        }
    }
    
    private File getRepositoryFile(OWLModel model, String s) {
        File repositoryFile = null;
        try {
            URI owlFileUri = model.getProject().getProjectURI();

            if (owlFileUri == null && model.getKnowledgeBaseFactory() instanceof JenaKnowledgeBaseFactory) {
                String uriString = JenaKnowledgeBaseFactory.getOWLFilePath(model.getProject().getSources());
                owlFileUri = URIUtilities.createURI(uriString);
            } 

            if (owlFileUri == null) {
                return null;
            }
            URL absoluteURL = new URL(owlFileUri.toURL(), s.trim());
            
            repositoryFile = new File(absoluteURL.getPath());
            if (repositoryFile.canRead() && repositoryFile.isFile()) {
                return repositoryFile;
            }
        }
        catch (Throwable t) {
            return null;
        }
        return null;
    }
}

