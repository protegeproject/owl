package edu.stanford.smi.protegex.owl.jena;


/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLFilesPlugin extends OWLCreateProjectPlugin {

    void addImport(String uri, String prefix);

	/**
	 * @deprecated This method will soon be removed.
	 */ 
    void setDublinCoreRedirectToDLVersion(boolean b);


    void setFile(String fileURI);


    void setLanguage(String lang);
}
