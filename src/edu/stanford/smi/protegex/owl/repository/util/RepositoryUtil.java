package edu.stanford.smi.protegex.owl.repository.util;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactory;
import edu.stanford.smi.protegex.owl.repository.impl.DublinCoreDLVersionRedirectRepository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

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
        if (rep != null) {
            InputStream is = rep.getInputStream(ontologyURI);
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
    
    
    /**
     * Loads the project repositories from a given URI. The file at the URI location should be in the 
     * repository file format expected by Protege.
     *  
     * @param owlModel - the owl model
     * @param uri - the uri of the repository file
     * @param removeExistingRepositories - if true, it will remove all existing repository entries. If false,
     * it will append to the old repositories entries the new ones loaded from uri.
     */
    public static void loadProjectRepositoriesFromURI(OWLModel owlModel, URI uri, boolean removeExistingRepositories) {
    	
    	RepositoryManager manager = owlModel.getRepositoryManager();
    	
    	if (removeExistingRepositories == true) {
    		manager.removeAllProjectRepositories();
    	}
    	
		try {
			URL url = new URL(uri.toString());
			InputStream fis = ProtegeOWLParser.getInputStream(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					RepositoryFactory factory = RepositoryFactory.getInstance();
					Repository rep = factory.createOntRepository(owlModel, line);
					if (rep != null) {						
						manager.addProjectRepository(rep);
					}
				}
			}			
			fis.close();
		} catch (IOException e) {
			Log.getLogger().warning("[Repository Manager] Could not find repository file: "
							+ uri.toString());
		}
	}
    
}

