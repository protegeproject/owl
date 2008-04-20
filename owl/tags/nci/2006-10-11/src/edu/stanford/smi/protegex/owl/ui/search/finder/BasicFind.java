package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;

import java.util.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2006
 */
public class BasicFind implements Find {

    private static final int MAX_MATCHES = -1;

    protected OWLModel owlModel;

    protected Map results = new HashMap();

    protected String string;

    protected int searchType;

    private List listeners;

    private boolean running = false;

    public BasicFind(OWLModel owlModel, int type) {
        this.owlModel = owlModel;
        this.searchType = type;
    }

    public void startSearch(String s) {
        startSearch(s, this.searchType);
    }

    public void startSearch(String s, int type) {

        notifySearchStarted();

        string = s;
        searchType = type;

        results.clear();

        if ((string != null) && (string.length() > 0)) {
            List searchProps = getSearchProperties();

            for (Iterator i = searchProps.iterator(); i.hasNext();) {
                results.putAll(searchOnSlot((Slot) i.next(), string, null, searchType));
            }
            String lang = owlModel.getDefaultLanguage();
            if (lang != null) {
                for (Iterator i = searchProps.iterator(); i.hasNext();) {
                    Slot slot = (Slot) i.next();
                    if (!slot.equals(owlModel.getNameSlot())) {
                        results.putAll(searchOnSlot(slot, string, lang, searchType));
                    }
                }
            }
        }

        notifySearchComplete();
    }

    public void cancelSearch() {
    }

    protected Map searchOnSlot(Slot searchProp, String searchStr,
                               String lang, int searchType) {

        Map slotResults = new HashMap();

        Collection frames = null;

        String actualSearchString = searchStr;

        switch (searchType) {
            case STARTS_WITH:
                actualSearchString += "*"; // no break
            case EXACTLY_MATCHES:
                if (lang != null) {
                    actualSearchString = "~#" + lang + " " + actualSearchString;
                }
                break;

            case CONTAINS:
                actualSearchString += "*"; // no break
            case ENDS_WITH:
                actualSearchString = "*" + actualSearchString;
                break;
        }

        frames = owlModel.getMatchingFrames(searchProp, null, false, actualSearchString, BasicFind.MAX_MATCHES);

        if (frames != null) {
            for (Iterator j = frames.iterator(); j.hasNext();) {
                Frame f = (Frame) j.next();
                if (isValidFrameToSearch(f)) {
                    RDFResource res = (RDFResource) f;
                    FindResult item = FindResult.createFindResult(res, searchProp, searchStr);
                    if (item != null) {
                        slotResults.put(res, item);
                    }
                }
            }
        }

        return slotResults;
    }

    /**
     * by default automatically search the name and the current browser slot
     * as well as the search synonym slots (if set)
     */
    protected List getSearchProperties() {
        List searchProps = new ArrayList();

        Collection synonymProps = owlModel.getSearchSynonymProperties();
        searchProps.addAll(synonymProps);

        searchProps.add(owlModel.getNameSlot());

        Slot bs = owlModel.getOWLNamedClassClass().getBrowserSlotPattern().getFirstSlot();
        if ((!bs.equals(owlModel.getNameSlot())) && (!synonymProps.contains(bs))) {
            searchProps.add(bs);
        }

        return searchProps;
    }


    public Map<RDFResource, FindResult> getResults() {
        return results;
    }

    public Set getResultResources() {
        return results.keySet();
    }


    public int getResultCount() {
        return results.size();
    }


    public String getSummaryText() {
        if (running) {
            return "Searching for \"" + string + "\" : (" + results.size() + " matches)";
        }
        else {

            return "Results for \"" + string + "\" : (" + results.size() + " matches)";
        }
    }


    public String getLastSearch() {
        return string;
    }


    public String getDescription() {
        return "Find Resource";
    }


    public OWLModel getModel() {
        return owlModel;
    }


    public int getNumSearchProperties() {
        return getSearchProperties().size();
    }


    /**
     * Only look at classes, properties and individuals
     *
     * @param f a frame
     * @return true if the given frame should be included in the results
     */
    protected boolean isValidFrameToSearch(Frame f) {
        Class fclass = f.getClass();
        return (OWLNamedClass.class.isAssignableFrom(fclass)) ||
               (OWLProperty.class.isAssignableFrom(fclass)) ||
               (OWLIndividual.class.isAssignableFrom(fclass));
    }

    public int getSearchType() {
        return searchType;
    }

    protected void notifySearchStarted() {
        running = true;
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            SearchListener l = (SearchListener) i.next();
            l.searchStartedEvent(this);
            l.searchEvent(this);
        }
        Thread.yield();
    }

    protected void notifyResultsUpdated() {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            SearchListener l = (SearchListener) i.next();
            l.resultsUpdatedEvent(results.size(), this);
            l.searchEvent(this);
        }
        Thread.yield();
    }

    protected void notifySearchComplete() {
        running = false;
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            SearchListener l = (SearchListener) i.next();
            l.searchCompleteEvent(results.size(), this);
            l.searchEvent(this);
        }
        Thread.yield();
    }

    protected void notifySearchCancelled() {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            SearchListener l = (SearchListener) i.next();
            l.searchCancelledEvent(this);
            l.searchEvent(this);
        }
        running = false;
        Thread.yield();
    }

    public void addResultListener(SearchListener l) {
        if (listeners == null) {
            listeners = new ArrayList();
        }
        listeners.add(l);
    }

    public boolean removeResultListener(SearchListener l) {
        if (listeners != null) {
            return listeners.remove(l);
        }
        return false;
    }
}
