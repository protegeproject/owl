package edu.stanford.smi.protegex.owl.repository.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.impl.AbstractStreamBasedRepositoryImpl;
import edu.stanford.smi.protegex.owl.repository.impl.DublinCoreDLVersionRedirectRepository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryUtil {
    private final static transient Logger log = Log.getLogger(RepositoryUtil.class);

    public static final String FORCE_READ_ONLY_FLAG = "forceReadOnly";

    public static boolean isForcedToBeReadOnly(String query) {
        return getBooleanProperty(FORCE_READ_ONLY_FLAG, query);
    }

	public static boolean getBooleanProperty(String propertyName, String query) {
		if (query != null && query.length() > 0) {
            StringTokenizer tok = new StringTokenizer(query, "&");
            while (tok.hasMoreTokens()) {
                String curNameValuePair = tok.nextToken();
                String curName = curNameValuePair.substring(0, curNameValuePair.indexOf('='));
                String curValue = curNameValuePair.substring(curNameValuePair.indexOf('=') + 1,
                        curNameValuePair.length());
                if (curName.equals(propertyName)) {
                    return Boolean.valueOf(curValue).booleanValue();
                }
            }
        }
        return false;
	}


    public static boolean createImportLocalCopy(OWLModel model, URI ontologyURI, File localFile) throws IOException {
        Repository rep = model.getRepositoryManager().getRepository(ontologyURI);
        if (rep != null && rep instanceof AbstractStreamBasedRepositoryImpl) {
            InputStream is = ((AbstractStreamBasedRepositoryImpl) rep).getInputStream(ontologyURI);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localFile)));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
            }
            bw.flush();
            bw.close();
            br.close();
            model.getRepositoryManager().addProjectRepository(0, new LocalFileRepository(localFile));
            return true;
        }
        else if (rep != null) {
            // for the non-stream based ontology we could also save the owl file but we will need to write this later.
            log.warning("Can't write non-stream based repositories yet (though it wouldn't be hard to fix this)");
        }
        return false;
    }


    public static boolean isDublinCoreRedirectedToDLVersion(RepositoryManager manager) {
        for (Iterator it = manager.getProjectRepositories().iterator(); it.hasNext();) {
            if (it.next() instanceof DublinCoreDLVersionRedirectRepository) {
                return true;
            }
        }
        return false;
    }


    public static void setDublinCoreRedirectedToDLVersion(RepositoryManager manager, boolean redirect) {
        if (redirect) {
            if (isDublinCoreRedirectedToDLVersion(manager) == false) {
                manager.addProjectRepository(0, new DublinCoreDLVersionRedirectRepository());
            }
        }
        else {
            for (Iterator it = manager.getProjectRepositories().iterator(); it.hasNext();) {
                Repository rep = (Repository) it.next();
                if (rep instanceof DublinCoreDLVersionRedirectRepository) {
                    manager.remove(rep);
                    break;
                }
            }
        }
    }
    
    public static URI getURI(URL baseURL, String relativeURL) throws MalformedURLException, URISyntaxException {
    	return new URI(new URL(baseURL, relativeURL).toString());
    }
    
    public static File getRepositoryFileFromRelativePath(OWLModel model, String s) {
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
            URL absoluteURL = new URL(owlFileUri.toURL(), RepositoryUtil.stripQuery(s.trim()));
            
            repositoryFile = new File(absoluteURL.toURI());
            if (repositoryFile.canRead() && repositoryFile.isFile()) {
                return repositoryFile;
            }
        }
        catch (Throwable t) {
            return null;
        }
        return null;
    }
    
    
    public static String stripQuery(String relativeURL) {
        int index = relativeURL.indexOf('?');
        if (index != -1) {
            return relativeURL.substring(0, index);
        }
        else {
            return relativeURL;
        }
    }
    
}

