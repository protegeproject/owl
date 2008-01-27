package edu.stanford.smi.protegex.owl.swrl;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltin;

public class SWRLSystemFrames extends OWLSystemFrames {
    private OWLNamedClass atomListCls, builtinAtomCls, classAtomCls, dataRangeAtomCls, 
                          dataValuedPropertyAtomCls, differentIndividualsAtomCls, impCls, 
                          individualPropertyAtomCls, sameIndividualAtomCls,
                          atomCls, variableCls, builtInCls;
    private OWLObjectProperty bodyProperty, headProperty, argumentsProperty, builtInProperty, 
                              argument1Property, classPredicateProperty, 
                              propertyPredicateProperty, dataRangeProperty;
    private OWLDatatypeProperty argsProperty, minArgsProperty, maxArgsProperty; 
    
    private RDFProperty argument2Property;
    
    private List<SWRLBuiltin> coreSWRLBuiltIns;
    
    public SWRLSystemFrames(OWLModel owlModel) {
        super(owlModel);
        createSWRLMetaModel();
    }
    
    private void createSWRLMetaModel() {
        createSWRLClasses();
        createSWRLObjectProperties();
        createSWRLDatatypeProperties();
        createSWRLBuiltIns();
        argument2Property = new DefaultRDFProperty(owlModel, new FrameID(SWRLNames.Slot.ARGUMENT2));
    }
    
    private void createSWRLClasses() {
        impCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.IMP));
        variableCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.VARIABLE));
        builtInCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.BUILTIN));

        atomListCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.ATOM_LIST));

        atomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.ATOM));
        classAtomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.CLASS_ATOM));
        builtinAtomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.BUILTIN_ATOM));
        dataRangeAtomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.DATA_RANGE_ATOM));
        dataValuedPropertyAtomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM));
        differentIndividualsAtomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM));
        individualPropertyAtomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM));
        sameIndividualAtomCls = new DefaultOWLNamedClass(owlModel, new FrameID(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM));
    }
    
    private void createSWRLObjectProperties() {
        bodyProperty = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.BODY));
        headProperty = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.HEAD));
        argumentsProperty = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.ARGUMENTS));
        builtInProperty = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.BUILTIN));
        argument1Property = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.ARGUMENT1));

        classPredicateProperty = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.CLASS_PREDICATE));
        propertyPredicateProperty = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.PROPERTY_PREDICATE));
        dataRangeProperty = new DefaultOWLObjectProperty(owlModel, new FrameID(SWRLNames.Slot.DATA_RANGE));
    }
    
    private void createSWRLDatatypeProperties() {
        argsProperty = new DefaultOWLDatatypeProperty(owlModel, new FrameID(SWRLNames.Slot.ARGS));
        minArgsProperty = new DefaultOWLDatatypeProperty(owlModel, new FrameID(SWRLNames.Slot.MIN_ARGS));
        maxArgsProperty = new DefaultOWLDatatypeProperty(owlModel, new FrameID(SWRLNames.Slot.MAX_ARGS));
    }
    
    private void createSWRLBuiltIns() {
        coreSWRLBuiltIns = new ArrayList<SWRLBuiltin>();

        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.EQUAL)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.NOT_EQUAL)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.LESS_THAN)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.LESS_THAN_OR_EQUAL)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.GREATER_THAN)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.GREATER_THAN_OR_EQUAL)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.MULTIPLY)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.DIVIDE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.INTEGER_DIVIDE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.MOD)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.POW)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.UNARY_PLUS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.UNARY_MINUS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ABS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.CEILING)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.FLOOR)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ROUND)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ROUND_HALF_TO_EVEN)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SIN)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.COS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.TAN)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.BOOLEAN_NOT)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.STRING_EQUAL_IGNORE_CASE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.STRING_CONCAT)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBSTRING)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.STRING_LENGTH)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.NORMALIZE_SPACE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.UPPER_CASE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.LOWER_CASE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.TRANSLATE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.CONTAINS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.CONTAINS_IGNORE_CASE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.STARTS_WITH)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ENDS_WITH)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBSTRING_BEFORE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBSTRING_AFTER)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.MATCHES)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.REPLACE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.TOKENIZE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.YEAR_MONTH_DURATION)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.DAY_TIME_DURATION)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.DATETIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.DATE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.TIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATIONS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATIONS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.MULTIPLY_YEAR_MONTH_DURATION)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.DIVIDE_YEAR_MONTH_DURATIONS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATIONS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATIONS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.MULTIPLY_DAY_TIME_DURATIONS)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.DIVIDE_DAY_TIME_DURATION)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_DATES)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_TIMES)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATION_TO_DATETIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_DATETIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATION_FROM_DATETIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_DATETIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATION_TO_DATE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_DATE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_DATE)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_TIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_TIME)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_DATETIMES_YIELDING_YEAR_MONTH_DURATION)));
        coreSWRLBuiltIns.add(new DefaultSWRLBuiltin(owlModel, new FrameID(SWRLNames.CoreBuiltIns.SUBTRACT_DATETIMES_YIELDING_DAY_TIME_DURATION)));
    }
    
    
    @Override
    public void addSystemFrames(FrameStore fs) {
        super.addSystemFrames(fs);
        SWRLSystemFramesAssertions assertions = new SWRLSystemFramesAssertions(fs);
        assertions.addSWRLHierarchy();
        assertions.addPropertyTypes();
        assertions.addBuiltInTypes();
    }
    
    protected class SWRLSystemFramesAssertions extends SystemFramesAsserter {
        
        public SWRLSystemFramesAssertions(FrameStore fs) {
            super(fs);
        }
        
        public void addSWRLHierarchy() {
            fs.addDirectSuperclass(impCls, getRootCls());
            assertTypeAndSubclasses(impCls, getOwlNamedClassClass(), new Cls[] {});
            fs.addDirectSuperclass(variableCls, getRootCls());
            assertTypeAndSubclasses(variableCls, getOwlNamedClassClass(), new Cls[] {});
            fs.addDirectSuperclass(builtInCls, getRootCls());
            assertTypeAndSubclasses(builtInCls, getOwlNamedClassClass(), new Cls[] {}); 
            fs.addDirectSuperclass(atomListCls, getRdfListClass());
            assertTypeAndSubclasses(atomListCls, getOwlNamedClassClass(), new Cls[] {});
            
            fs.addDirectSuperclass(atomCls, getRootCls());
            assertTypeAndSubclasses(atomCls, getOwlNamedClassClass(), new Cls[] {
                assertTypeAndSubclasses(classAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(builtinAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(dataRangeAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(dataValuedPropertyAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(differentIndividualsAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(individualPropertyAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(sameIndividualAtomCls, getOwlNamedClassClass(), new Cls[] {})
            });
        }

        public void addPropertyTypes() {
            assertTypeAndName(bodyProperty, getOwlObjectPropertyClass());
            assertTypeAndName(headProperty, getOwlObjectPropertyClass());
            assertTypeAndName(argumentsProperty, getOwlObjectPropertyClass());
            assertTypeAndName(builtInProperty, getOwlObjectPropertyClass());
            assertTypeAndName(argument1Property, getOwlObjectPropertyClass());
            assertTypeAndName(classPredicateProperty, getOwlObjectPropertyClass());
            assertTypeAndName(propertyPredicateProperty, getOwlObjectPropertyClass());
            assertTypeAndName(dataRangeProperty, getOwlObjectPropertyClass());
            
            assertTypeAndName(argsProperty, getOwlDatatypePropertyClass());
            assertTypeAndName(minArgsProperty, getOwlDatatypePropertyClass());
            assertTypeAndName(maxArgsProperty, getOwlDatatypePropertyClass());
            
            assertTypeAndName(argument2Property, getRdfPropertyClass());
        }
        
        public void addBuiltInTypes() {
            for (SWRLBuiltin builtIn : coreSWRLBuiltIns) {
                assertTypeAndName(builtIn, getBuiltInCls());
            }
        }
    }
    
    
    /*
     * ----------------------------------------------------------------------------
     * The obvious getters.
     */
    /**
     * @return the atomListCls
     */
    public OWLNamedClass getAtomListCls() {
        return atomListCls;
    }

    /**
     * @return the builtinAtomCls
     */
    public OWLNamedClass getBuiltinAtomCls() {
        return builtinAtomCls;
    }

    /**
     * @return the classAtomCls
     */
    public OWLNamedClass getClassAtomCls() {
        return classAtomCls;
    }

    /**
     * @return the dataRangeAtomCls
     */
    public OWLNamedClass getDataRangeAtomCls() {
        return dataRangeAtomCls;
    }

    /**
     * @return the dataValuedPropertyAtomCls
     */
    public OWLNamedClass getDataValuedPropertyAtomCls() {
        return dataValuedPropertyAtomCls;
    }

    /**
     * @return the differentIndividualsAtomCls
     */
    public OWLNamedClass getDifferentIndividualsAtomCls() {
        return differentIndividualsAtomCls;
    }

    /**
     * @return the impCls
     */
    public OWLNamedClass getImpCls() {
        return impCls;
    }

    /**
     * @return the individualPropertyAtomCls
     */
    public OWLNamedClass getIndividualPropertyAtomCls() {
        return individualPropertyAtomCls;
    }

    /**
     * @return the sameIndividualAtomCls
     */
    public OWLNamedClass getSameIndividualAtomCls() {
        return sameIndividualAtomCls;
    }

    /**
     * @return the atomCls
     */
    public OWLNamedClass getAtomCls() {
        return atomCls;
    }

    /**
     * @return the variableCls
     */
    public OWLNamedClass getVariableCls() {
        return variableCls;
    }

    /**
     * @return the builtInCls
     */
    public OWLNamedClass getBuiltInCls() {
        return builtInCls;
    }

    /**
     * @return the bodyProperty
     */
    public OWLObjectProperty getBodyProperty() {
        return bodyProperty;
    }

    /**
     * @return the headProperty
     */
    public OWLObjectProperty getHeadProperty() {
        return headProperty;
    }

    /**
     * @return the argumentsProperty
     */
    public OWLObjectProperty getArgumentsProperty() {
        return argumentsProperty;
    }

    /**
     * @return the builtInProperty
     */
    public OWLObjectProperty getBuiltInProperty() {
        return builtInProperty;
    }

    /**
     * @return the argument1Property
     */
    public OWLObjectProperty getArgument1Property() {
        return argument1Property;
    }

    /**
     * @return the classPredicateProperty
     */
    public OWLObjectProperty getClassPredicateProperty() {
        return classPredicateProperty;
    }

    /**
     * @return the propertyPredicateProperty
     */
    public OWLObjectProperty getPropertyPredicateProperty() {
        return propertyPredicateProperty;
    }

    /**
     * @return the dataRangeProperty
     */
    public OWLObjectProperty getDataRangeProperty() {
        return dataRangeProperty;
    }

    /**
     * @return the argsProperty
     */
    public OWLDatatypeProperty getArgsProperty() {
        return argsProperty;
    }

    /**
     * @return the minArgsProperty
     */
    public OWLDatatypeProperty getMinArgsProperty() {
        return minArgsProperty;
    }

    /**
     * @return the maxArgsProperty
     */
    public OWLDatatypeProperty getMaxArgsProperty() {
        return maxArgsProperty;
    }

    /**
     * @return the coreSWRLBuiltIns
     */
    public List<SWRLBuiltin> getCoreSWRLBuiltIns() {
        return coreSWRLBuiltIns;
    }

    /**
     * @return the argument2Property
     */
    public RDFProperty getArgument2Property() {
        return argument2Property;
    }
    
    
    

}
