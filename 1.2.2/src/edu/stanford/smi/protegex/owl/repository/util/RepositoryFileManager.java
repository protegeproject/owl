package edu.stanford.smi.protegex.owl.repository.util;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactory;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 24, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryFileManager {

    public static final String REPOSITORY_EXTENTION = ".repository";

    public static final String GLOBAL_REPOSITORY_FILE_NAME = "global.repository";

    private RepositoryManager manager;

    private OWLModel model;


    public RepositoryFileManager(OWLModel model) {
        this.model = model;
        this.manager = this.model.getRepositoryManager();
    }


    public void loadProjectRepositories() {
        File file = getProjectRepositoryFile();
        if (file != null && file.exists()) {
            manager.removeAllProjectRepositories();
            loadRepositoriesFromFile(file, false);
        }
    }


    private void loadGlobalRepositories(File file) {
        if (file.exists()) {
            loadRepositoriesFromFile(file, true);
        }
    }


    public void loadGlobalRepositories() {
        File f = ProtegeOWL.getPluginFolder();
        f = new File(f, GLOBAL_REPOSITORY_FILE_NAME);
        if (f.exists()) {
            loadGlobalRepositories(f);
        }
    }


    public void saveProjectRepositories(URI owlFileURI) {
        File file = getProjectRepositoryFile(owlFileURI);
        if (file != null) {
            saveProjectRepositories(file);
        }
    }


    public void saveProjectRepositories() {
        Project project = model.getProject();
        if (project != null) {
            if (project.getProjectURI() != null) {
                File file = getProjectRepositoryFile();
                if (file != null) {
                    saveProjectRepositories(file);
                }
            }
        }
    }


    private void saveProjectRepositories(File file) {
        ArrayList list = new ArrayList();
        for (Iterator it = manager.getProjectRepositories().iterator(); it.hasNext();) {
            Repository rep = (Repository) it.next();
            if (rep.isSystem() == false) {
                list.add(rep);
            }
        }
        if (list.size() > 0) {
            saveRepositories(list, file);
        }
        else {
            file.delete();
        }
    }


    public void saveGlobalRepositories() {
        File pluginsDirectory = PluginUtilities.getPluginsDirectory();
        if (pluginsDirectory != null && pluginsDirectory.exists()) {
            File f = ProtegeOWL.getPluginFolder();
            f = new File(f, GLOBAL_REPOSITORY_FILE_NAME);
            saveRepositories(manager.getGlobalRepositories(), f);
        }
    }


    private void saveRepositories(List repositories, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (Iterator it = repositories.iterator(); it.hasNext();) {
                Repository curRep = (Repository) it.next();
                if (curRep.isSystem() == false) {
                    String descriptor = curRep.getRepositoryDescriptor();
                    if (descriptor != null && descriptor.length() > 0) {
                        writer.write(descriptor);
                        writer.write("\n");
                    }
                }
            }
            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        }
    }


    private void loadRepositoriesFromFile(final File f,
                                          final boolean global) {
        try {
                    FileInputStream fis = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					String line;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						if (line.length() > 0) {
							RepositoryFactory factory = RepositoryFactory.getInstance();
							Repository rep = factory.createOntRepository(model, line);
							if (rep != null) {
								if (global) {
									manager.addGlobalRepository(rep);
								}
								else {
									manager.addProjectRepository(rep);
								}
							}
						}
					}

        }
        catch (IOException e) {
            System.err.println("[Repository Manager] Warning: Could not find repository file: " + f.toString());
        }
    }


    private File getProjectRepositoryFile() {
        Project project = model.getProject();
        if (project != null) {
            URI projectURI = project.getProjectURI();
            if (projectURI != null) {
                return getProjectRepositoryFile(projectURI);
            }
        }
        return null;
    }


    private File getProjectRepositoryFile(URI owlFileURI) {
        File f = new File(owlFileURI);
        String repName = f.getName();
        repName = repName.substring(0, repName.lastIndexOf(".")) + REPOSITORY_EXTENTION;
        f = new File(f.getParentFile(), repName);
        return f;
    }
}

