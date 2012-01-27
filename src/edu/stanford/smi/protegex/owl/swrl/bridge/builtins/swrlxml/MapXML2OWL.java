
package org.protege.swrltab.bridge.builtins.swrlxml;

import java.net.URI;
import java.net.URISyntaxException;

import org.jdom.Document;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtil;

public class MapXML2OWL
{
  public static void main(String args[])
  {
    String xmlURI = "", owlFilename = "";
    Document doc;
    JenaOWLModel owlModel;
    org.protege.swrltab.bridge.builtins.swrlxml.XMLMapper  mapper;
    XMLProcessor processor = new XMLProcessor();

    if (args.length == 2) {
      xmlURI = args[0];
      owlFilename = args[1];
    } else Usage();

    try {
      doc = processor.processXMLStream(xmlURI);
      owlModel = P3OWLUtil.createJenaOWLModel();
      owlModel.setGenerateEventsEnabled(false);
      addSWRLXMLImport(owlModel);
      mapper = new XMLMapper(owlModel);
      mapper.document2OWLDocument(doc);
      P3OWLUtil.writeJenaOWLModel2File(owlModel, owlFilename);
    } catch (Exception e) {
      System.err.println("error mapping XML document with URI '" + xmlURI + "': " + e.getMessage());
      e.printStackTrace();
    } 
  } 

  private static void Usage()
  {
    System.err.println("Usage: MapXML2OWL <xmlURI> <owlFilename>");
    System.exit(1);
  } 

  private static void addSWRLXMLImport(OWLModel owlModel) 
  {
    ImportHelper importHelper = new ImportHelper(owlModel);

    try {
      owlModel.getNamespaceManager().setPrefix(new URI(SWRLNames.SWRLXML_NAMESPACE), SWRLNames.SWRLXML_PREFIX);
      
      if  (owlModel.getTripleStoreModel().getTripleStore(SWRLNames.SWRLXML_IMPORT) == null) 
        importHelper.addImport(new URI(SWRLNames.SWRLXML_IMPORT));

      importHelper.importOntologies(false);
    } catch (URISyntaxException e) {
      System.err.println("error importing SWRLXML ontology: " + e.getMessage());
      System.exit(-1);
    } catch (OntologyLoadException e) {
      System.err.println("error loading SWRLXML ontology: " + e.getMessage());
      System.exit(-1);
    }
  } 
} 
