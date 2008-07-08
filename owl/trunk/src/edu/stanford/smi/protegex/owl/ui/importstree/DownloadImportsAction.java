package edu.stanford.smi.protegex.owl.ui.importstree;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DownloadImportsAction extends AbstractAction {

    private ImportsTree tree;


    public DownloadImportsAction(ImportsTree tree) {
        super("Download ontologies to folder...", OWLIcons.getImageIcon(OWLIcons.SAVE_INFERRED));
        this.tree = tree;
    }


    public void actionPerformed(ActionEvent e) {
    	JFileChooser fileChooser = ComponentFactory.createFileChooser("Choose download folder", "", null);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showDialog(tree, "Download") == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            Set sels = tree.getSelectedResources();
            String message = "";
            int downloadedCount = 0;
            for (Iterator it = sels.iterator(); it.hasNext();) {
                RDFResource resource = (RDFResource) it.next();
                String uri = resource.getURI();
                try {
                    String fileName = download(uri, folder);
                    message += "Downloaded " + uri + "  to " + fileName + "\n";
                    downloadedCount++;
                }
                catch (Exception ex) {
                    message += "Failed " + uri + "\n  " + ex.toString();
                }
            }
            OWLModel owlModel = tree.getRootOntology().getOWLModel();
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, message);
            if (downloadedCount > 0) {
                message = "Would you like to add a project repository to redirect imports " +
                        "to these local copies?";

                if (ProtegeUI.getModalDialogFactory().showConfirmDialog(tree, message, "Add project repository?")) {
                    Repository rep = new LocalFolderRepository(folder);
                    owlModel.getRepositoryManager().addProjectRepository(0, rep);
                    ProtegeUI.getModalDialogFactory().showMessageDialog(tree, "You should save and reload your " +
                            "project for the imports redirection " +
                            "to take effect.");
                }

            }
        }
    }


    public static String download(String uri, File targetFolder) throws Exception {
        InputStream is = null;
        try {
            URL url = new URL(uri);
            int index = uri.lastIndexOf('/');
            if (index == uri.length() - 1) {
                index = uri.lastIndexOf('/', uri.length() - 2);
            }
            String fileName = uri.substring(index + 1);
            File file = new File(targetFolder, fileName);
            is = url.openStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(file);
            for (; ;) {
                int next = bis.read();
                if (next < 0) {
                    break;
                }
                fos.write(next);
            }
            fos.close();
            return file.toString();
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
