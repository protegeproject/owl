package edu.stanford.smi.protegex.owl.model.query;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SPARQLQueryResults implements QueryResults {

    private OWLModel owlModel;

    private Query query;

    private ResultSet results;


    public SPARQLQueryResults(OWLModel owlModel, Query query, ResultSet results) {
        this.owlModel = owlModel;
        this.query = query;
        this.results = results;
    }


    /**
     * Creates QueryResults from a given query string (which should already include prefix declarations).
     *
     * @param owlModel    the OWLModel to operate on
     * @param queryString a string in the SPARQL query language syntax (SELECT...)
     * @return a new QueryResults object or an Exception if the query could not be handled
     */
    public static SPARQLQueryResults create(OWLModel owlModel, String queryString) throws Exception {
        Model model = owlModel.getJenaModel();
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();
        return new SPARQLQueryResults(owlModel, query, results);
    }


    /**
     * Creates the SPARQL PREFIX and BASE declarations for a given OWLModel.
     * This string can be used to start a new query.
     *
     * @param owlModel the OWLModel to get the prefixes of
     * @return a prefix declaration string
     */
    public static String createPrefixDeclarations(OWLModel owlModel) {
        String queryString = "";
        String defaultNamespace = owlModel.getNamespaceManager().getDefaultNamespace();
        if (defaultNamespace.endsWith("#")) {
            queryString += "BASE <" + defaultNamespace.substring(0, defaultNamespace.length() - 1) + ">\n";
        }
        else {
            queryString += "BASE <" + defaultNamespace + ">\n";
        }
        queryString += "PREFIX :   <" + defaultNamespace + ">\n";
        Iterator prefixes = owlModel.getNamespaceManager().getPrefixes().iterator();
        while (prefixes.hasNext()) {
            String prefix = (String) prefixes.next();
            String namespace = owlModel.getNamespaceManager().getNamespaceForPrefix(prefix);
            queryString += "PREFIX " + prefix + ": <" + namespace + ">\n";
        }
        return queryString;
    }


    public List getVariables() {
        return query.getResultVars();
    }


    public boolean hasNext() {
        return results.hasNext();
    }


    public Map next() {
        Map map = new HashMap();
        List vars = query.getResultVars();
        QuerySolution soln = results.nextSolution();
        for (int i = 0; i < vars.size(); i++) {
            String varName = (String) vars.get(i);
            RDFNode varNode = soln.get(varName);
            if (varNode instanceof Literal) {
                Literal literal = (Literal) varNode;
                RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(literal.getDatatypeURI());
                String lexical = literal.getLexicalForm();
                RDFSLiteral protegeLiteral = owlModel.createRDFSLiteral(lexical, datatype);
                map.put(varName, protegeLiteral);
            }
            else {
                String str = varNode.toString();
                String name = null;
                if (owlModel.isAnonymousResourceName(str)) {
                    name = str;
                }
                else {
                    name = ((AbstractOWLModel) owlModel).getFrameNameForURI(str, false);
                }
                if (name != null) {
                    RDFResource resource = owlModel.getRDFResource(name);
                    if (resource != null) {
                        map.put(varName, resource);
                    }
                    else {
                        map.put(varName, owlModel.createRDFSLiteral(str));
                    }
                }
                else {
                    map.put(varName, owlModel.createRDFSLiteral(str));
                }
            }
        }
        return map;
    }
}
