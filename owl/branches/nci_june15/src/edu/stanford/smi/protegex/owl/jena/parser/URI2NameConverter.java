package edu.stanford.smi.protegex.owl.jena.parser;

import com.hp.hpl.jena.rdf.arp.AResource;

/**
 * An interface for objects that are capable of creating a Protege resource name
 * from an URI.  This is used in the Jena2Protege to convert (Jena) resource URIs
 * into names for the corresponding Protege objects.
 * <p/>
 * The use pattern of the Jena2Protege class is to first see if it can resolve
 * a URI with the current namespace prefix definitions.  For example, if a URI
 * "http://www.w3.org/2002/07/owl#Class" is referenced, then the system can
 * resolve this to "owl:Class", because "owl" is a defined prefix for that namespace.
 * However, during loading, a URI may be a forward reference to a namespace which is
 * prefixed in some other imported file, e.g. "http://aldi.de/products.owl#Apple"
 * where the name "aldi:Apple" cannot be created yet because the corresponding namespace
 * declaration hasn't been reached yet, or doesn't exist at all.  In the latter case, the
 * system will later create a default prefix, such as "p1".  In the normal case though,
 * the prefix will be reached later.  Anyway, the resource needs to still have a name
 * so that it can be looked up successfully in future references.  So, if no default
 * name could be resolved, the system will use a temporary name (e.g. the whole URI)
 * and later replace the temp name with the "real" resource name.
 * <p/>
 * This interface also allows to create names for anonymous resources.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface URI2NameConverter {


    /**
     * Adds a new prefix-namespace pair to this converter.
     * If the namespace is already used under a different prefix then the already
     * existing one will be used and returned.  If the prefix is already used for
     * a different namespace, then the prefix will not be used.
     *
     * @param uri    the URI/namespace
     * @param prefix the new prefix
     * @return the prefix that will be used for the namespace
     */
    String addPrefix(String uri, String prefix);


    /**
     * Creates a dummy prefix and adds it to the OWLModel
     *
     * @param uri the URI of the resource to create a new prefix for
     */
    String createNewPrefix(String uri);


    String createAnonymousRDFResourceName();


    /**
     * Gets the namespace part of a resource URI.  This typically wraps the Jena namespace
     * split algorithm.
     *
     * @param uri the URI to get the namespace of
     * @return the namespace (usually including the '#')
     */
    String getResourceNamespace(String uri);


    String getRDFExternalResourceName();


    /**
     * Gets the Protege resource name for a given URI.
     *
     * @param uri the URI to get the name of
     * @return the Protege name (not null)
     */
    String getRDFResourceName(String uri);


    String getTemporaryRDFResourceName(String uri);


    String getTemporaryRDFResourceName(AResource node);


    /**
     * The inverse of <CODE>getTemporaryRDFResourceName()</CODE>.
     *
     * @param temporaryName the temporary name
     * @return the URI encoded by the temp name
     */
    String getURIFromTemporaryName(String temporaryName);


    /**
     * Checks whether a given name describes an anonymous resource.
     *
     * @param name the name of the resource to test
     * @return true if name is an anonymous name
     */
    boolean isAnonymousRDFResourceName(String name);


    /**
     * Checks if this is a name that was created as a temporary name before.
     * Temporary names should follow a naming convention which keeps them
     * clearly separate from real names.
     *
     * @param name the name to test
     * @return true if name is a temporary test
     */
    boolean isTemporaryRDFResourceName(String name);


    /**
     * Called after the triples have been copied and new namespace prefixes assigned.
     * This method must update the internal state of the converter to reflect possible
     * changes during loading (e.g. add new prefixes).
     */
    void updateInternalState();
}
