package edu.stanford.smi.protegex.owl.model;

/**
 * An interface for listeners of NamespaceKnowledgeBases.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface NamespaceManagerListener {

    void defaultNamespaceChanged(String oldValue, String newValue);


    void namespaceChanged(String prefix, String oldValue, String newValue);


    void prefixAdded(String prefix);


    void prefixChanged(String namespace, String oldPrefix, String newPrefix);


    void prefixRemoved(String prefix);
}
