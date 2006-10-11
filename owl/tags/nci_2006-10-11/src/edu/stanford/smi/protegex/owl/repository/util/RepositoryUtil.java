package edu.stanford.smi.protegex.owl.repository.util;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.impl.DublinCoreDLVersionRedirectRepository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;

import java.io.*;
import java.net.URI;
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
}

