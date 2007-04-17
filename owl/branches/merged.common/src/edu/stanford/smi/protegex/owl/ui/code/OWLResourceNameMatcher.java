package edu.stanford.smi.protegex.owl.ui.code;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLResourceNameMatcher implements ResourceNameMatcher {

    public final static int SCALABLE_FRAME_COUNT = 1000;


    public String getInsertString(RDFResource resource) {
        return resource.getBrowserText();
    }


    public List getMatchingResources(String prefix, String leftString, OWLModel owlModel) {

        if (owlModel instanceof OWLDatabaseModel && prefix.length() < 3) {
            return Collections.EMPTY_LIST;
        }

        List frames = new ArrayList();
        OWLClassParseException ex = OWLClassParseException.getRecentInstance();
        if (ex.nextCouldBeClass || owlModel.getRDFResource(prefix) instanceof RDFSNamedClass) {
            getMatchingRDFSNamedClasses(prefix, frames, owlModel);
        }
        if (ex.nextCouldBeIndividual || owlModel.getRDFResource(prefix) != null) {
            getMatchingRDFIndividuals(prefix, frames, owlModel);
        }
        if (ex.nextCouldBeProperty || owlModel.getRDFResource(prefix) instanceof RDFProperty) {
            getMatchingRDFProperties(prefix, frames, owlModel);
        }
	    if(ex.nextCouldBeDatatypeName || owlModel.getRDFResource(prefix) instanceof RDFSDatatype) {
		    getMatchingDatatypeNames(prefix, frames, owlModel);
	    }
        return frames;
    }


    public static void getMatchingRDFSNamedClasses(String prefix, List result, OWLModel owlModel) {
        if (prefix.length() == 0) {
            int count = owlModel.getRDFSClassCount();
            if (count < SCALABLE_FRAME_COUNT) {
                for (Iterator it = OWLUtil.getSelectableNamedClses(owlModel).iterator(); it.hasNext();) {
                    RDFSNamedClass aClass = (RDFSNamedClass) it.next();
                    if (aClass.isVisibleFromOWLThing()) {
                        result.add(aClass);
                    }
                }
            }
        }
        else {
            Collection matches = owlModel.getResourceNameMatches(prefix + "*", SCALABLE_FRAME_COUNT);
            for (Iterator it = matches.iterator(); it.hasNext();) {
                Frame frame = (Frame) it.next();
                if (frame instanceof RDFSNamedClass &&
                        frame.isVisible() && (isSlowProject(owlModel) ||
                        ((RDFSNamedClass) frame).isVisibleFromOWLThing())) {
                    result.add(frame);
                }
            }
        }
    }


    private static boolean isSlowProject(OWLModel owlModel) {
        return owlModel.getProject().isMultiUserClient();
    }


    public static void getMatchingRDFProperties(String prefix, List result, OWLModel owlModel) {
        Collection matches = owlModel.getResourceNameMatches(prefix + "*", SCALABLE_FRAME_COUNT);
        for (Iterator it = matches.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (frame instanceof RDFProperty && frame.isVisible()) {
                result.add(frame);
            }
        }
    }


    public static void getMatchingRDFIndividuals(String prefix, List result, OWLModel owlModel) {
        Collection matches = owlModel.getResourceNameMatches(prefix + "*", SCALABLE_FRAME_COUNT);
        for (Iterator it = matches.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (frame instanceof RDFIndividual && frame.isVisible()) {
                result.add(frame);
            }
        }
    }

	public static void getMatchingDatatypeNames(String prefix, List result, OWLModel owlModel) {
		Collection matches = owlModel.getRDFSDatatypes();
        for (Iterator it = matches.iterator(); it.hasNext();) {
            RDFSDatatype datatype = (RDFSDatatype) it.next();
            if(datatype.isAnonymous() == false &&
               datatype.getBrowserText().startsWith(prefix)) {
	            result.add(datatype);
            }
        }
	}
}
