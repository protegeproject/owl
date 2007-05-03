package edu.stanford.smi.protegex.owl.model.patcher;

import java.util.Iterator;

/**
 * A utility class that can add "missing" rdf:type triples so that
 * untyped resources are resolved into their most likely intended type.
 * <p/>
 * For example, if an untyped resource as object in an rdfs:domain triple,
 * then we can infer that the resource is an rdfs:Class (or more).
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLModelPatcher {

    /**
     * Adds "missing" triples for untyped resources.
     * This method shall create a new TripleStore for the given namespace
     * and then add rdf:type triples into it for all resources from a given Iterator.
     * Note that the Iterator may also contain resources from other namespaces, which
     * shall be ignored.
     *
     * @param resources an Iterator of untyped RDFResources
     * @param namespace the namespace to patch
     */
    void patch(Iterator resources, String namespace);
}
