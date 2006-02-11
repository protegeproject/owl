package edu.stanford.smi.protegex.owl.repository.impl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactoryPlugin;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RelativeFolderRepositoryFactoryPlugin implements RepositoryFactoryPlugin {

    public boolean isSuitable(OWLModel model,
                              String s) {
        if (model.getProject() != null) {
            if (model.getProject().getProjectURI() != null) {
                try {
                    URI uri = new URI(s.trim());
                    if (uri.isAbsolute() == false) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                catch (URISyntaxException e) {
                    return false;
                }
            }
        }
        return false;
    }


    public Repository createRepository(OWLModel model,
                                       String s) {
        try {
            return new RelativeFolderRepository(model.getProject().getProjectURI().toURL(),
                    s.trim());

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}

