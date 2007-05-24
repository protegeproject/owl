package edu.stanford.smi.protegex.owl.repository.impl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LocalFileRepositoryFactoryPlugin implements RepositoryFactoryPlugin {

    public boolean isSuitable(OWLModel model, String s) {
        if (s.trim().startsWith("file:")) {
            try {
                File f = new File(new URI(s).getPath());
                return f.isFile();
            }
            catch (URISyntaxException e) {
                return false;
            }
        }
        return false;
    }


    public Repository createRepository(OWLModel model, String s) {
        try {
            URI uri = new URI(s);
            File f = new File(uri.getPath());
            return new LocalFileRepository(f, RepositoryUtil.isForcedToBeReadOnly(uri.getQuery()));
        }
        catch (URISyntaxException e) {
            return null;
        }
    }
}

