package edu.stanford.smi.protegex.owl.database;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import java.util.Collection;
import java.util.Iterator;

/**
 * A DatabaseKnowledgeBaseFactory with an even longer name.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseKnowledgeBaseFactory extends DatabaseKnowledgeBaseFactory
        implements OWLKnowledgeBaseFactory {

    public KnowledgeBase createKnowledgeBase(Collection errors) {
        ResourceSelectionAction.setActivated(false);
        return new OWLDatabaseModel(this);
    }


    private void dump(Cls cls, String tabs) {
        System.out.println(tabs + cls);
        for (Iterator it = cls.getDirectSubclasses().iterator(); it.hasNext();) {
            Cls subCls = (Cls) it.next();
            if (!subCls.isEditable()) {
                try {
                    dump(subCls, tabs + "  ");
                }
                catch (Exception ex) {
                    System.err.println("ERROR at " + cls + " / " + subCls);
                    for (Iterator sit = subCls.getDirectSubclasses().iterator(); sit.hasNext();) {
                        Object o = (Object) sit.next();
                        System.out.println("- " + o + " = " + (o instanceof Slot) + " " +
                                ((Instance) o).getDirectType());
                    }
                    ex.printStackTrace();
                }
            }
        }
    }


    public String getProjectFilePath() {
        return "OWL.pprj";
    }


    public String getDescription() {
        return "OWL / RDF Database";
    }


    public void loadKnowledgeBase(KnowledgeBase kb,
                                  String driver,
                                  String table,
                                  String url,
                                  String user,
                                  String password,
                                  Collection errors) {

        OWLDatabaseModel owlModel = (OWLDatabaseModel) kb;

        super.loadKnowledgeBase(kb, driver, table, url, user, password, errors);

        owlModel.initialize();
    }


    public void saveKnowledgeBase(KnowledgeBase kb, PropertyList sources, Collection errors) {
        if (kb instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) kb;
            sources.setInteger(JenaKnowledgeBaseFactory.OWL_BUILD_PROPERTY, OWLText.getBuildNumber());
            if (owlModel instanceof JenaOWLModel) {
                kb.removeFrameStore(owlModel.getOWLFrameStore());
            }
            super.saveKnowledgeBase(kb, sources, errors);
            if (owlModel instanceof JenaOWLModel) {
                kb.insertFrameStore(owlModel.getOWLFrameStore(), 0);
            }
        }
        else {
            errors.add("You can only save OWL projects to OWL Database format.");
        }
    }


    protected void updateKnowledgeBase(DefaultKnowledgeBase kb) {
        // Overloaded to suppress super call
    }
}
