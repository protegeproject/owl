package edu.stanford.smi.protegex.owl.repository;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.impl.DublinCoreDLVersionRedirectRepository;
import edu.stanford.smi.protegex.owl.repository.impl.ForcedURLRetrievalRepository;
import edu.stanford.smi.protegex.owl.repository.impl.HTTPRepository;
import edu.stanford.smi.protegex.owl.repository.impl.ProtegeOWLPluginFolderRepository;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 18, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryManager {

    private ArrayList projectRepositories;

    private ArrayList globalRepositories;

    private OWLModel model;


    public RepositoryManager(OWLModel model) {
        this.model = model;
        projectRepositories = new ArrayList();
        globalRepositories = new ArrayList();
        loadSystemRepositories();
    }


    public void addDefaultRepositories() {
        projectRepositories.add(new DublinCoreDLVersionRedirectRepository());
    }


    private void loadSystemRepositories() {
        globalRepositories.add(new ProtegeOWLPluginFolderRepository());
    }


    public void removeAllProjectRepositories() {
        projectRepositories.clear();
    }


    public List getAllRepositories() {
        ArrayList list = new ArrayList();
        list.addAll(projectRepositories);
        list.addAll(globalRepositories);
        return list;
    }


    public void removeAllGlobalRepositories() {
        globalRepositories.clear();
    }


    public List getProjectRepositories() {
        return Collections.unmodifiableList(projectRepositories);
    }


    public List getGlobalRepositories() {
        return Collections.unmodifiableList(globalRepositories);
    }


    public void addProjectRepository(Repository repository) {
        addProjectRepository(projectRepositories.size(), repository);
    }


    public void addProjectRepository(int index, Repository repository) {
        if (projectRepositories.contains(repository) == false) {
            projectRepositories.add(index, repository);
        }
    }


    public void addGlobalRepository(Repository repository) {
        addGlobalRepository(globalRepositories.size(), repository);
    }


    public void addGlobalRepository(int index, Repository repository) {
        if (globalRepositories.contains(repository) == false) {
            globalRepositories.add(index, repository);
        }
    }


    public void moveUp(Repository repository) {
        List repositories = selectRepositories(repository);
        int index = repositories.indexOf(repository);
        if (index != -1 && index > 0) {
            repositories.remove(index);
            repositories.add(index - 1, repository);
        }
    }


    public void moveDown(Repository repository) {
        List repositories = selectRepositories(repository);
        int index = repositories.indexOf(repository);
        if (index != -1 && index < repositories.size() - 1) {
            repositories.remove(index);
            repositories.add(index + 1, repository);
        }
    }


    private List selectRepositories(Repository repository) {
        List repositories;
        if (isGlobalRepository(repository)) {
            repositories = globalRepositories;
        }
        else {
            repositories = projectRepositories;
        }
        return repositories;
    }


    public boolean isGlobalRepository(Repository repository) {
        return globalRepositories.contains(repository);
    }


    public void remove(Repository repository) {
        selectRepositories(repository).remove(repository);
    }


    public Repository getRepository(URI ontologyName) {
        // Process local projectRepositories first.
        for (Iterator it = projectRepositories.iterator(); it.hasNext();) {
            Repository curRepository = (Repository) it.next();
            if (curRepository.contains(ontologyName)) {
                return curRepository;
            }
        }
        for (Iterator it = globalRepositories.iterator(); it.hasNext();) {
            Repository curRepository = (Repository) it.next();
            if (curRepository.contains(ontologyName)) {
                return curRepository;
            }
        }
        return null;
    }


    public Repository getRepository(URI ontologyName, boolean createRep) {
        Repository rep = getRepository(ontologyName);
        if (rep == null) {
            if (createRep) {
                try {
                    rep = new HTTPRepository(ontologyName.toURL());
                    if (rep.contains(ontologyName)) {
                        addProjectRepository(rep);
                    }
                    else {
                        ForcedURLRetrievalRepository fr = new ForcedURLRetrievalRepository(ontologyName.toURL());
                        if (fr.contains(ontologyName)) {
                            addProjectRepository(fr);
                            return fr;
                        }
                        else {
                            return null;
                        }
                    }
                }
                catch (MalformedURLException e) {
                    return null;
                }
            }
        }
        return rep;
    }


}

