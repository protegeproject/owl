package edu.stanford.smi.protegex.owl.ui.repository.action;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.FTPRepository;
import edu.stanford.smi.protegex.owl.ui.repository.AbstractRepositoriesPanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 3, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryActionManager {

    private static RepositoryActionManager instance;

    private HashSet actions;


    private RepositoryActionManager() {
        actions = new HashSet();
        actions.add(new RepositoryAction() {
            public String getName() {
                return "Refresh repository";
            }


            public boolean isSuitable(Repository repository,
                                      OWLModel owlModel) {
                return true;
            }


            public void actionPerformed(Repository repository,
                                        AbstractRepositoriesPanel repositoriesPanel,
                                        OWLModel model) {
                repository.refresh();
                repositoriesPanel.reloadUI();
            }
        });
        actions.add(new RepositoryAction() {
            public String getName() {
                return "Upload using FTP";
            }


            public boolean isSuitable(Repository repository,
                                      OWLModel owlModel) {
                return repository instanceof FTPRepository;
            }


            public void actionPerformed(Repository repository,
                                        AbstractRepositoriesPanel repositoriesPanel,
                                        OWLModel model) {
                FTPRepository rep = (FTPRepository) repository;
                rep.ftpPut();
            }
        });
    }


    public static synchronized RepositoryActionManager getInstance() {
        if (instance == null) {
            instance = new RepositoryActionManager();
        }
        return instance;
    }


    public Collection getActions(Repository repository, OWLModel owlModel) {
        ArrayList suitableActions = new ArrayList();
        for (Iterator it = actions.iterator(); it.hasNext();) {
            RepositoryAction curAction = (RepositoryAction) it.next();
            if (curAction.isSuitable(repository, owlModel)) {
                suitableActions.add(curAction);
            }
        }
        return suitableActions;
    }
}

