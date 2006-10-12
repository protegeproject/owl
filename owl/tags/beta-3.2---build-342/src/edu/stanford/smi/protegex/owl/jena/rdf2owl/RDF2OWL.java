package edu.stanford.smi.protegex.owl.jena.rdf2owl;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protegex.owl.jena.Jena;

import java.util.Iterator;

/**
 * A class that can convert pure RDF statements from a Model into corresponding
 * OWL statements.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDF2OWL {

    private Model model;


    public RDF2OWL(Model model) {
        this.model = model;
    }


    /**
     * Converts all rdfs:Classes into owl:Classes
     */
    private void convertRDFSClasses() {
        for (StmtIterator it = model.listStatements(null, RDF.type, RDFS.Class); it.hasNext();) {
            Statement s = it.nextStatement();
            Resource clazz = s.getSubject();
            s.getModel().add(clazz, RDF.type, OWL.Class);
            it.remove();
            log("Converted rdfs:Class " + clazz + " into owl:Class");
        }
    }


    /**
     * Converts the properties to either owl:DatatypeProperties or owl:ObjectProperties.
     */
    private void convertProperties() {
        for (StmtIterator it = model.listStatements(null, RDF.type, RDF.Property); it.hasNext();) {
            Statement s = it.nextStatement();
            Resource property = s.getSubject();
            Resource type = getPropertyType(property);
            s.getModel().add(property, RDF.type, type);
            it.remove();
            log("Converted rdf:Property " + property + " into " + type);
        }
    }


    /**
     * Converts all object occurances of rdfs:Resource with owl:Thing
     */
    private void convertRDFSResource() {
        for (StmtIterator it = model.listStatements(null, null, RDFS.Resource); it.hasNext();) {
            Statement s = it.nextStatement();
            s.getModel().add(s.getSubject(), s.getPredicate(), OWL.Thing);
            log("Replaced triple " + s + " with (x, x, owl:Thing)");
            it.remove();
        }
    }


    /**
     * Attempts to find the most plausible RDF type for a given property.
     *
     * @param property the property to get the type of
     * @return either owl:DatatypeProperty or owl:ObjectProperty
     */
    private Resource getPropertyType(Resource property) {
        StmtIterator it = model.listStatements(property, RDFS.range, (RDFNode) null);
        if (it.hasNext()) {
            while (it.hasNext()) {
                Statement s = it.nextStatement();
                RDFNode n = s.getObject();
                if (n.canAs(Resource.class) &&
                        model.contains((Resource) n.as(Resource.class), RDF.type, OWL.Class)) {
                    return OWL.ObjectProperty;
                }
            }
        }
        return OWL.DatatypeProperty;
    }


    private void log(String message) {
        System.out.println("[RDF2OWL] " + message);
    }


    public void run() {
        unifyRDFSVersion();
        convertRDFSResource();
        // convertRDFSClasses();
        // convertProperties();
    }


    /**
     * Converts any statements containing old RDFS vocabulary to the official ones.
     */
    private void unifyRDFSVersion() {
        String[] oldRDFSNSs = {
                //"http://www.w3.org/2000/01/rdf-schema#",
                "http://www.w3.org/TR/1999/PR-rdf-schema-19990303#"  // Any others???
        };
        for (int i = 0; i < oldRDFSNSs.length; i++) {
            String oldRDFSNS = oldRDFSNSs[i];
            unifyRDFSVersion(oldRDFSNS);
        }
    }


    private void unifyRDFSVersion(String ns) {
        for (Iterator it = Jena.cloneIt(model.listStatements()); it.hasNext();) {
            Statement s = (Statement) it.next();
            Resource newSubject = s.getSubject();
            Property newPredicate = s.getPredicate();
            RDFNode newObject = s.getObject();
            boolean changed = false;
            if (ns.equals(newSubject.getNameSpace())) {
                changed = true;
                newSubject = model.getResource(RDFS.getURI() + newSubject.getLocalName());
            }
            if (ns.equals(newPredicate.getNameSpace())) {
                changed = true;
                newPredicate = model.getProperty(RDFS.getURI() + newPredicate.getLocalName());
            }
            if (newObject.canAs(Resource.class)) {
                Resource oldResource = (Resource) newObject.as(Resource.class);
                if (ns.equals(oldResource.getNameSpace())) {
                    changed = true;
                    newObject = model.getResource(RDFS.getURI() + oldResource.getLocalName());
                }
            }
            if (changed) {
                model.add(newSubject, newPredicate, newObject);
                log("Replaced deprecated triple " + s + " with " + newSubject + ", " + newPredicate + ", " + newObject);
                it.remove();
            }
        }
    }
}
