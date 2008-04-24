package edu.stanford.smi.protegex.owlx.examples;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LogicalClassDefinitions {

    public static void main(String[] args) {

        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();

        OWLNamedClass personClass = owlModel.createOWLNamedClass("Person");
        OWLNamedClass manClass = owlModel.createOWLNamedSubclass("Man", personClass);
        OWLNamedClass womanClass = owlModel.createOWLNamedSubclass("Woman", personClass);

        // Create expression  (PersonClass & !(Man | Woman))
        OWLUnionClass unionClass = owlModel.createOWLUnionClass();
        unionClass.addOperand(manClass);
        unionClass.addOperand(womanClass);
        OWLComplementClass complementClass = owlModel.createOWLComplementClass(unionClass);
        OWLIntersectionClass intersectionClass = owlModel.createOWLIntersectionClass();
        intersectionClass.addOperand(personClass);
        intersectionClass.addOperand(complementClass);

        OWLNamedClass kidClass = owlModel.createOWLNamedClass("Kid");
        kidClass.addSuperclass(intersectionClass);

        String expression = "Person & !(Man | Woman)";
        OWLIntersectionClass ic = (OWLIntersectionClass) owlModel.createRDFSClassFromExpression(expression);
        System.out.println("Browser text: " + ic.getBrowserText());

        String parsable = intersectionClass.getParsableExpression();
        System.out.println("Expression: " + parsable);

        RDFSClass c = owlModel.createRDFSClassFromExpression("!(" + parsable + ")");
        System.out.println("New expression: " + c.getParsableExpression());
    }
}
