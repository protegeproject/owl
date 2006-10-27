package edu.stanford.smi.protegex.owl.ui.search.finder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2006
 */
public class BasicFind implements Find {

    private static final int MAX_MATCHES = -1;

    OWLModel owlModel;

    private Map<RDFResource, FindResult> results = new HashMap<RDFResource, FindResult>();
    
    private String string;

    private int searchType;

    private List<SearchListener> listeners;

    private boolean running = false;

    private Object lock;

    public BasicFind(OWLModel owlModel, int type) {
        this.owlModel = owlModel;
        this.searchType = type;
        lock = owlModel;
    }

    public void startSearch(String s) {
        startSearch(s, searchType);
    }

    public void startSearch(String s, int type) {
      try {
        synchronized (lock) {
          if (!aborted()) { 
            string = s;
            searchType = type;
            results.clear();
            running = true;
          } else {
            return;
          }
        }
        notifySearchStarted();
        
        if ((s != null) && (s.length() > 0)) {

          List searchProps = getSearchProperties();

          for (Iterator i = searchProps.iterator(); i.hasNext() && !aborted();) {
            Map<RDFResource, FindResult> res = searchOnSlot((Slot) i.next(), s, null, type);
            synchronized (lock) {
              results.putAll(res);
            }
            notifyResultsUpdated();
          }
          String lang = owlModel.getDefaultLanguage();
          if (lang != null) {
            for (Iterator i = searchProps.iterator(); i.hasNext() && !aborted();) {
              Slot slot = (Slot) i.next();
              if (!slot.equals(((KnowledgeBase) owlModel).getNameSlot())) {
                Map<RDFResource, FindResult> res = searchOnSlot(slot, s, lang, type);
                synchronized (lock) {
                  results.putAll(res);
                }
                notifyResultsUpdated();
              }
            }
          }
        }

        if (!aborted()) {
          notifySearchComplete();
        }
        else {
          notifySearchCancelled();
        }
      } finally {
        synchronized (lock) {
          running = false;
        }
      }
    }

    public void cancelSearch() {
      throw new UnsupportedOperationException("Can't abort non-threaded search");
    }

    protected boolean aborted() {
      return false;
    }
    
    public boolean isRunning() {
      synchronized (lock) {
        return running;
      }
    }

    protected Map<RDFResource, FindResult> searchOnSlot(Slot searchProp, 
                                                        String searchStr,
                                                        String lang, 
                                                        int searchType) {

        Map<RDFResource, FindResult> slotResults = new HashMap<RDFResource, FindResult>();

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

        frames = ((KnowledgeBase) owlModel).getMatchingFrames(searchProp, null, false, actualSearchString, BasicFind.MAX_MATCHES);

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

    /*
     * If the following calls are expensive we can build thread-safe Map and Set objects
     * to use.
     */

    public Map<RDFResource, FindResult> getResults() {
      synchronized (lock) {
        return new HashMap<RDFResource, FindResult>(results);
      }
    }

    public Set getResultResources() {
      synchronized (lock) {
        return new HashSet(results.keySet());
      }
    }


    public int getResultCount() {
      synchronized (lock) {
        return results.size();
      }
    }


    public String getSummaryText() {
      synchronized (lock) {
        if (isRunning()) {
          return "Searching for \"" + string + "\" : (" + results.size() + " matches)";
        }
        else {
          return "Results for \"" + string + "\" : (" + results.size() + " matches)";
        }
      }
    }


    public String getLastSearch() {
      synchronized  (lock) {
        return string;
      }
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
      synchronized (lock) {
        return searchType;
      }
    }
    
    protected void notifySearchStarted() {
      synchronized (lock) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            SearchListener l = (SearchListener) i.next();
            l.searchStartedEvent(this);
            l.searchEvent(this);
        }
      }
    }

    protected void notifyResultsUpdated() {
      synchronized (lock) {
        for (Iterator<SearchListener> i = listeners.iterator(); i.hasNext();) {
            SearchListener l = i.next();
            l.resultsUpdatedEvent(results.size(), this);
            l.searchEvent(this);
        }
      }
    }

    protected void notifySearchComplete() {
      synchronized (lock) {
        for (Iterator<SearchListener> i = listeners.iterator(); i.hasNext();) {
            SearchListener l = i.next();
            l.searchCompleteEvent(results.size(), this);
            l.searchEvent(this);
        }
      }
    }

    protected void notifySearchCancelled() {
      synchronized (lock) {
        for (Iterator<SearchListener> i = listeners.iterator(); i.hasNext();) {
            SearchListener l = i.next();
            l.searchCancelledEvent(this);
            l.searchEvent(this);
        }
      }
    }

    public void addResultListener(SearchListener l) {
      synchronized (lock) {
        if (listeners == null) {
            listeners = new ArrayList<SearchListener>();
        }
        listeners.add(l);
      }
    }

    public boolean removeResultListener(SearchListener l) {
      synchronized (lock) {
        if (listeners != null) {
            return listeners.remove(l);
        }
        return false;
      }
    }
}
