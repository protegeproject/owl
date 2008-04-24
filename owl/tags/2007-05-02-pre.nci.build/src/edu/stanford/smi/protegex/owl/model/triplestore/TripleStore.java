package edu.stanford.smi.protegex.owl.model.triplestore;

import java.util.Comparator;
import java.util.Iterator;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.model.NamespaceMap;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * An interface for low-level access to the single triples in an OWLModel.
 * All current implementations are better suited for querying than actually
 * writing triples, so better don't modify the triples in your own application
 * yet.  If you need to do that, make sure that you invoke the post-processor
 * as done in the ProtegeOWLParser.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface TripleStore extends NamespaceMap {
    
    FrameID generateFrameID();

    void add(Triple triple);


    void add(RDFResource subject, RDFProperty predicate, Object object);


    boolean contains(Triple triple);


    boolean contains(RDFResource subject, RDFProperty predicate, Object object);


    String getName();


    RDFResource getHomeResource(String name);


    /**
     * Provides access to the internal Protege storage for low-level access.
     * This method should only be used by experienced users.
     *
     * @return the NarrowFrameStore
     */
    NarrowFrameStore getNarrowFrameStore();


    /**
     * Gets all resources that have their "home" in this triple store.
     * The home is defined to be the TripleStore with the :NAME value of the resource.
     *
     * @return an Iterator of RDFResources
     */
    Iterator listHomeResources();


    /**
     * Gets the values of a given subject/property combination.
     *
     * @param subject
     * @param property
     * @return a Collection of Objects (e.g. RDFResources)
     */
    Iterator listObjects(RDFResource subject, RDFProperty property);


    /**
     * The the subjects of all triples where a given property has any value.
     * The Iterator does not contain duplicates.
     *
     * @param property the property to look for
     * @return an Iterator of RDFResources
     */
    Iterator listSubjects(RDFProperty property);


    /**
     * Gets the subjects of all triples with a given predicate and object.
     *
     * @param predicate the predicate to match
     * @param object    the object to match
     * @return an Iterator of RDFResources
     */
    Iterator listSubjects(RDFProperty predicate, Object object);


    Iterator listTriples();


    /**
     * Lists all Triples that have a given object.
     *
     * @param object the object to get the triples of
     * @return an Iterator of Triples
     */
    Iterator listTriplesWithObject(RDFObject object);


    /**
     * Lists all Triples that have a given subject.
     * In other words, this returns all property-value pairs of a given resource.
     * Note that this operation is currently not efficiently implemented.
     *
     * @param subject the subject in the triples
     * @return an Iterator of Triples
     */
    Iterator listTriplesWithSubject(RDFResource subject);


    void remove(Triple triple);


    void remove(RDFResource subject, RDFProperty predicate, Object object);


    void setName(String value);


    void setRDFResourceName(RDFResource resource, String name);


    void sortPropertyValues(RDFResource resource, RDFProperty property, Comparator comparator);


    /**
     * Debugging only.
     */
    void dump();
}
