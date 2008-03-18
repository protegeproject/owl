package edu.stanford.smi.protegex.owl.model.triplestore;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The TripleStoreModel is an access layer on top of the normal OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface TripleStoreModel {


    /**
     * A low level method that creates and adds a new TripleStore to implement imports.
     *
     * @param name the name of the new TripleStore (must be unique)
     * @return the new TripleStore
     */
    TripleStore createTripleStore(String name);


    /**
     * Deletes an imported TripleStore.
     *
     * @param tripleStore the TripleStore to delete (must be neither system nor top TripleStore)
     */
    void deleteTripleStore(TripleStore tripleStore);


    /**
     * Must be called after changes were made in any TripleStore.
     * This will internally synchronize several Protege model features with
     * the OWL/RDF triples.
     */
    void endTripleStoreChanges();


    /**
     * Gets the currently "active" TripleStore, which all future set operations will be
     * operating on.
     *
     * @return the active TripleStore
     * @see #getTripleStores
     * @see #setActiveTripleStore
     */
    TripleStore getActiveTripleStore();


    /**
     * Gets the "home" triple store of a given resource.  This can be used to determine the
     * TripleStore where changes on the resource should be performed consistently.
     * In the default implementation, the home TripleStore is the one that contains a :NAME
     * slot value of the resource.
     *
     * @param resource the RDFResource to find the home TripleStore of
     * @return the home TripleStore
     */
    TripleStore getHomeTripleStore(RDFResource resource);


    /**
     * A low-level access method to access property values while the usual API methods are
     * in an invalid state.  This may be necessary during the direct manipulation of triples
     * on a TripleStore level.
     *
     * @param resource the resource to get the property values of
     * @param property the property to get the values of
     * @return a Collection of property values (may include RDFSLiterals)
     */
    Collection getPropertyValues(RDFResource resource, RDFProperty property);


    /**
     * Similar to getPropertyValues but for native Protege slots and instances.
     *
     * @param instance the instance to get the values of
     * @param slot     the slot to get the values of
     * @return a Collection of slot values (not including RDFSLiterals)
     */
    Collection getSlotValues(Instance instance, Slot slot);


    /**
     * Gets the TripleStore with a given name.
     * For additional TripleStores, the name corresponds to the import URI.
     *
     * @param name the name / import URI of the TripleStore
     * @return the TripleStore or null if none of this name exists
     */
    TripleStore getTripleStore(String name);


    /**
     * Gets the first TripleStore that uses a given default namespace.
     *
     * @param namespace the namespace
     * @return the TripleStore or null
     */
    TripleStore getTripleStoreByDefaultNamespace(String namespace);


    /**
     * Gets a TripleStore by its index.
     * This is equivalent to <CODE>getTripleStores().get(index);</CODE>.
     *
     * @param index the index of the TripleStore to get
     * @return the TripleStore
     */
    TripleStore getTripleStore(int index);


    /**
     * Gets the TripleStores which provide an RDF triple-based view onto this OWLModel.
     * <B>Warning: This is work in progress!</B>
     *
     * @return a collection of TripleStore objects
     */
    List getTripleStores();


    /**
     * Gets the "top" TripleStore, which is the first user-editable TripleStore,
     * from which all imports start.
     *
     * @return the top TripleStore (never null)
     */
    TripleStore getTopTripleStore();


    /**
     * Checks if a given triple is stored in the currently active TripleStore.
     * This is a shortcut for <CODE>getActiveTripleStore().contains(...)</CODE>.
     *
     * @param subject   the subject of the triple
     * @param predicate the predicate of the triple
     * @param object    the object of the triple
     * @return true  if the value is found in the active TripleStore
     */
    boolean isActiveTriple(RDFResource subject, RDFProperty predicate, Object object);


    /**
     * Checks if a given triple is stored in an editable TripleStore.
     * Editable TripleStores are those that could be saved to a local file, using the
     * OWLModel's URIResolver.
     *
     * @param subject   the subject of the triple
     * @param predicate the predicate of the triple
     * @param object    the object of the triple
     * @return true  if the value is found in an editable TripleStore
     */
    boolean isEditableTriple(RDFResource subject, RDFProperty predicate, Object object);


    boolean isEditableTripleStore(TripleStore ts);


    /**
     * Combines the result of the corresponding method of each TripleStore.
     *
     * @param subject the subject to get all triples of
     * @return an Iterator of Triples
     */
    Iterator listTriplesWithSubject(RDFResource subject);


    /**
     * Provides an Iterator on all user TripleStores, i.e. all results of <CODE>getTripleStores()</CODE>
     * except for the first (system) TripleStore.
     *
     * @return an Iterator of TripleStore objects
     */
    Iterator listUserTripleStores();


    void replaceJavaObject(RDFResource subject);


    /**
     * Specifies which TripleStore shall be the active one for future write operations.
     *
     * @param tripleStore one of the results of a recent call to <CODE>getTripleStores()</CODE>.
     */
    void setActiveTripleStore(TripleStore tripleStore);


    /**
     * Moves a given RDFResource into another "home" TripleStore.  This does not move any other
     * triples but only moves the low-level information about which TripleStore shall be regarded
     * as the "home" in the future.  In Protege, this is the graph where the :NAME slot value is
     * stored, i.e. moving the home means moving the :NAME value.
     *
     * @param resource    the RDFResource to move
     * @param tripleStore the new home TripleStoe
     */
    void setHomeTripleStore(RDFResource resource, TripleStore tripleStore);


    /**
     * Changes the <CODE>isIncluded()</CODE> value of all resources to reflect the
     * currently active TripleStore.  This should be called if the editable flag
     * is relevant (e.g. in a user interface) after changes of the active TripleStore.
     */
    void updateEditableResourceState();
}
