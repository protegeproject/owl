package edu.stanford.smi.protegex.owl.swrl.model.examples;

import com.hp.hpl.jena.util.FileUtils;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class SWRLFactoryTest {


    public static void main(String[] args) throws Exception {
        SWRLFactory swrlFactory;
        Collection error_messages = new Vector();

        final JenaKnowledgeBaseFactory factory = new JenaKnowledgeBaseFactory();
        Project project = new Project(null, error_messages);
        project.setKnowledgeBaseFactory(factory);
        project.createDomainKnowledgeBase(factory, error_messages, false);
        JenaOWLModel owlModel = (JenaOWLModel) project.getKnowledgeBase();

        owlModel.load(new URI("http://protege.stanford.edu/plugins/owl/testdata/importSWRL.owl"),
                FileUtils.langXMLAbbrev,
                error_messages);

        if (!error_messages.isEmpty()) {
            System.err.println("Error loading importSWRL.owl:" + error_messages);
            System.exit(-1);
        } // if

        swrlFactory = new SWRLFactory(owlModel);
        owlModel.setFrameFactory(new SWRLJavaFactory(owlModel));

        testRuleCreation(swrlFactory, owlModel);

    } //  main

    // Take a simple rule and use the SWRLFactory to create the OWL
    // instances representing that rule.
    //
    // Pizza(?x) /\ sameAs(?x, ?y) /\ lessThan(?x, ?z) /\ hasIngredient(?x, ?z) /\ myDatatypeProperty(?x, 'ddd') -> PizzaBase(?y)


    private static void testRuleCreation(SWRLFactory swrlFactory, JenaOWLModel owlModel) {
        SWRLImp imp;
        SWRLAtomList head, body;
        SWRLClassAtom classAtom;
        SWRLSameIndividualAtom sameIndividualAtom;
        SWRLDataRangeAtom dataRangeAtom;
        SWRLDatavaluedPropertyAtom datavaluedPropertyAtom;
        SWRLBuiltinAtom builtinAtom;
        SWRLIndividualPropertyAtom individualPropertyAtom;
        SWRLBuiltin lessThanBuiltin;
        OWLDatatypeProperty myDatatypeProperty;
        OWLObjectProperty hasIngredient;
        RDFObject literalValue;
        OWLNamedClass pizzaClass, pizzaBase;
        SWRLVariable x, y, z;
        Collection arguments;

        // OWL classes
        pizzaClass = owlModel.createOWLNamedClass("Pizza");
        pizzaBase = owlModel.createOWLNamedClass("PizzaBase");

        // OWL object properties
        hasIngredient = owlModel.createOWLObjectProperty("hasIngredient");

        // OWL datatype properties
        myDatatypeProperty = owlModel.createOWLDatatypeProperty("myDatatypeProperty");

        // x, y, z - variables
        x = swrlFactory.createVariable("x");
        y = swrlFactory.createVariable("y");
        z = swrlFactory.createVariable("z");

        // Built-ins
        lessThanBuiltin = swrlFactory.createBuiltin("lessThan");

        // head and body
        head = swrlFactory.createAtomList();
        body = swrlFactory.createAtomList();

        // Pizza(?x)
        classAtom = swrlFactory.createClassAtom(pizzaClass, x);
        body.append(classAtom);

        // PizzaBase(?z)
        classAtom = swrlFactory.createClassAtom(pizzaBase, z);
        head.append(classAtom);

        // sameAs(?x, ?y)
        sameIndividualAtom = swrlFactory.createSameIndividualAtom(x, y);
        head.append(sameIndividualAtom);

        // lessThan(?x, ?y)
        arguments = new ArrayList();
        arguments.add(x);
        arguments.add(y);
        builtinAtom = swrlFactory.createBuiltinAtom(lessThanBuiltin, arguments.iterator());
        head.append(builtinAtom);

        // hasIngredient(?x, ?y)
        individualPropertyAtom = swrlFactory.createIndividualPropertyAtom(hasIngredient, x, z);
        head.append(individualPropertyAtom);

        // myDatatypeProperty(?x, 'ddd')
        literalValue = owlModel.createRDFSLiteral("ddd", owlModel.getXSDstring());
        datavaluedPropertyAtom = swrlFactory.createDatavaluedPropertyAtom(myDatatypeProperty, x, literalValue);
        head.append(datavaluedPropertyAtom);

        // myDataRange
        OWLDataRange dataRange = owlModel.createOWLDataRange(new RDFSLiteral[]{
                owlModel.createRDFSLiteral("first"),
                owlModel.createRDFSLiteral("second")
        });
        dataRangeAtom = swrlFactory.createDataRangeAtom(dataRange, x);
        head.append(dataRangeAtom);

        RDFSDatatype datatype = owlModel.getXSDdouble();
        head.append(swrlFactory.createDataRangeAtom(datatype, x));

        imp = swrlFactory.createImp(head, body);

        System.out.println("Imp: " + imp.getBrowserText());

        //owlModel.dumpRDF();

    } // testRuleCreation

} // SWRLFactoryTest
