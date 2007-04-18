package edu.stanford.smi.protegex.owl.swrl.model;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A utility class that can (and should) be used to create and access
 * SWRL related objects in the ontology.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLFactory {

    private OWLNamedClass atomListCls;

    private OWLNamedClass builtinAtomCls;

    private OWLNamedClass classAtomCls;

    private OWLNamedClass dataRangeAtomCls;

    private OWLNamedClass dataValuedPropertyAtomCls;

    private OWLNamedClass differentIndividualsAtomCls;

    private OWLNamedClass impCls;

    private OWLNamedClass individualPropertyAtom;

    private OWLModel owlModel;

    private OWLNamedClass sameIndividualAtomCls;


    public SWRLFactory(OWLModel owlModel) {
        this.owlModel = owlModel;
        atomListCls = owlModel.getOWLNamedClass(SWRLNames.Cls.ATOM_LIST);
        builtinAtomCls = owlModel.getOWLNamedClass(SWRLNames.Cls.BUILTIN_ATOM);
        classAtomCls = owlModel.getOWLNamedClass(SWRLNames.Cls.CLASS_ATOM);
        dataRangeAtomCls = owlModel.getOWLNamedClass(SWRLNames.Cls.DATA_RANGE_ATOM);
        dataValuedPropertyAtomCls = owlModel.getOWLNamedClass(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM);
        differentIndividualsAtomCls = owlModel.getOWLNamedClass(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM);
        impCls = owlModel.getOWLNamedClass(SWRLNames.Cls.IMP);
        individualPropertyAtom = owlModel.getOWLNamedClass(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM);
        sameIndividualAtomCls = owlModel.getOWLNamedClass(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM);
    } // SWRLFactory


    public SWRLImp createImp() {
        String name = getNewImpName();
        return (SWRLImp) impCls.createInstance(name);
    }


    public SWRLImp createImp(String expression) throws SWRLParseException {
        SWRLParser parser = new SWRLParser(owlModel);
        parser.setParseOnly(false);
        return parser.parse(expression);
    }


    public SWRLImp createImp(SWRLAtom headAtom, Collection bodyAtoms) {
        SWRLAtomList head = createAtomList(Collections.singleton(headAtom));
        SWRLAtomList body = createAtomList(bodyAtoms);
        return createImp(head, body);
    }


    public SWRLImp createImp(SWRLAtomList head, SWRLAtomList body) {
        SWRLImp swrlImp = createImp();
        swrlImp.setHead(head);
        swrlImp.setBody(body);
        return swrlImp;
    } // SWRLImp


    public SWRLAtomList createAtomList() {
        return (SWRLAtomList) atomListCls.createAnonymousInstance();
    } // createAtomList


    public SWRLAtomList createAtomList(Collection atoms) {
        SWRLAtomList list = createAtomList();
        for (Iterator it = atoms.iterator(); it.hasNext();) {
            Object o = it.next();
            list.append(o);
        }
        return list;
    }


    public SWRLBuiltinAtom createBuiltinAtom(SWRLBuiltin swrlBuiltin,
                                             Iterator arguments) {
        RDFList li = owlModel.createRDFList(arguments);
        return createBuiltinAtom(swrlBuiltin, li);

    } // createBuiltinAtom


    public SWRLBuiltinAtom createBuiltinAtom(SWRLBuiltin swrlBuiltin,
                                             RDFList arguments) {
        SWRLBuiltinAtom swrlBuiltinAtom;

        swrlBuiltinAtom = (SWRLBuiltinAtom) builtinAtomCls.createAnonymousInstance();

        swrlBuiltinAtom.setBuiltin(swrlBuiltin);
        swrlBuiltinAtom.setArguments(arguments);

        return swrlBuiltinAtom;

    } // createBuiltinAtom


    public SWRLClassAtom createClassAtom(RDFSNamedClass aClass,
                                         RDFResource iObject) {
        SWRLClassAtom swrlClassAtom;

        swrlClassAtom = (SWRLClassAtom) classAtomCls.createAnonymousInstance();

        swrlClassAtom.setClassPredicate(aClass);
        swrlClassAtom.setArgument1(iObject);

        return swrlClassAtom;

    } // createClassAtom


    public SWRLDataRangeAtom createDataRangeAtom(RDFResource dataRange,
                                                 RDFObject dObject) {

        SWRLDataRangeAtom swrlDataRangeAtom = (SWRLDataRangeAtom) dataRangeAtomCls.createAnonymousInstance();

        swrlDataRangeAtom.setArgument1(dObject);
        swrlDataRangeAtom.setDataRange(dataRange);

        return swrlDataRangeAtom;
    } // createDataRangeAtom


    public SWRLDatavaluedPropertyAtom createDatavaluedPropertyAtom(OWLDatatypeProperty datatypeSlot,
                                                                   RDFResource iObject,
                                                                   RDFObject dObject) {
        SWRLDatavaluedPropertyAtom swrlDatavaluedPropertyAtom = (SWRLDatavaluedPropertyAtom) dataValuedPropertyAtomCls.createAnonymousInstance();

        swrlDatavaluedPropertyAtom.setPropertyPredicate(datatypeSlot);
        swrlDatavaluedPropertyAtom.setArgument1(iObject);
        swrlDatavaluedPropertyAtom.setArgument2(dObject);

        return swrlDatavaluedPropertyAtom;

    } // createDatavaluedPropertyAtom


    public SWRLIndividualPropertyAtom createIndividualPropertyAtom(OWLObjectProperty objectSlot,
                                                                   RDFResource iObject1,
                                                                   RDFResource iObject2) {
        SWRLIndividualPropertyAtom swrlIndividualPropertyAtom;

        swrlIndividualPropertyAtom = (SWRLIndividualPropertyAtom) individualPropertyAtom.createAnonymousInstance();

        swrlIndividualPropertyAtom.setPropertyPredicate(objectSlot);
        swrlIndividualPropertyAtom.setArgument1(iObject1);
        swrlIndividualPropertyAtom.setArgument2(iObject2);

        return swrlIndividualPropertyAtom;

    } // createIndividualPropertyAtom


    public SWRLDifferentIndividualsAtom createDifferentIndividualsAtom(RDFResource argument1,
                                                                       RDFResource argument2) {
        SWRLDifferentIndividualsAtom swrlDifferentIndividualsAtom;

        swrlDifferentIndividualsAtom = (SWRLDifferentIndividualsAtom) differentIndividualsAtomCls.createAnonymousInstance();
        swrlDifferentIndividualsAtom.setArgument1(argument1);
        swrlDifferentIndividualsAtom.setArgument2(argument2);

        return swrlDifferentIndividualsAtom;
    } // createDifferentIndividualsAtom


    public SWRLSameIndividualAtom createSameIndividualAtom(RDFResource argument1,
                                                           RDFResource argument2) {
        SWRLSameIndividualAtom swrlSameIndividualAtom;

        swrlSameIndividualAtom = (SWRLSameIndividualAtom) sameIndividualAtomCls.createAnonymousInstance();
        swrlSameIndividualAtom.setArgument1(argument1);
        swrlSameIndividualAtom.setArgument2(argument2);

        return swrlSameIndividualAtom;
    } // createSameIndividualAtom


    public SWRLVariable createVariable(String name) {
        return (SWRLVariable) owlModel.getRDFSNamedClass(SWRLNames.Cls.VARIABLE).createInstance(name);
    } // createVariable


    public SWRLBuiltin createBuiltin(String name) {
        return (SWRLBuiltin) owlModel.getRDFSNamedClass(SWRLNames.Cls.BUILTIN).createInstance(name);
    } // createBuiltin


    public SWRLBuiltin getBuiltin(String name) {
        RDFResource resource = owlModel.getRDFResource(name);
        if (resource instanceof SWRLBuiltin) {
            return (SWRLBuiltin) resource;
        }
        else {
            System.err.println("[SWRLFactory]  Invalid attempt to cast " + name +
                    " into SWRLBuiltin (real type is " + resource.getProtegeType() + ")");
            return null;
        }
    } // createBuiltin


    public Collection getBuiltins() {
        RDFSNamedClass builtinCls = owlModel.getRDFSNamedClass(SWRLNames.Cls.BUILTIN);
        return builtinCls.getInstances(true);
    }


    public Collection getImps() {
        RDFSClass impCls = owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP);
        return impCls.getInstances(true);
    }


    public String getNewImpName() {
        String base = "Rule-";
        int i = Math.max(1, impCls.getInstances(false).size());
        while (owlModel.getRDFResource(base + i) != null) {
            i++;
        }
        return base + i;
    }


    public SWRLVariable getVariable(String name) {
        return (SWRLVariable) owlModel.getRDFResource(name);
    }


    public Collection getVariables() {
        RDFSClass variableCls = owlModel.getRDFSNamedClass(SWRLNames.Cls.VARIABLE);
        return variableCls.getInstances(true);
    }
} // SWRLFactory
