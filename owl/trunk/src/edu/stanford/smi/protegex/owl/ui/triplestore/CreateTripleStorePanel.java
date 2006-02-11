package edu.stanford.smi.protegex.owl.ui.triplestore;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class CreateTripleStorePanel extends AddTripleStorePanel {


    protected CreateTripleStorePanel(OWLModel owlModel) {
        super(owlModel);
    }


    protected TripleStore performAction() {
//        URI uri = getURI();
//        URL url = null;
//        try {
//            url = file.toURL();
//            String prefix = getPrefix();
//            String namespace = Jena.getNamespaceFromURI(uri.toString());
//            OWLModel owlModel = getOWLModel();
//            owlModel.getNamespaceManager().setPrefix(namespace, prefix);
//            final TripleStoreModel tsm = owlModel.getTripleStoreModel();
//            TripleStore tripleStore = tsm.createTripleStore(uri.toString());
//            tsm.setActiveTripleStore(tripleStore);
//            OWLOntology owlOntology = owlModel.createOWLOntology(prefix);
//            RDFProperty property = owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
//            owlOntology.addPropertyValue(property, ":" + namespace);
//            URIResolver uriResolver = owlModel.getURIResolver();
//            uriResolver.setPhysicalURL(uri, url);
//            tsm.setActiveTripleStore(tsm.getTopTripleStore());
//            owlModel.getDefaultOWLOntology().addImports(uri.toString());
//            return tripleStore;
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
        return null;
    }


    public boolean validateContents() {
//        if (super.validateContents()) {
//            File file = getFile();
//            if (file != null) {
//                return true;
//            }
//            else {
//                OWLUI.showErrorMessageDialog("Invalid file.");
//            }
//        }
        return false;
    }
}
