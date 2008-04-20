package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2006
 */
public class ThreadedFind extends BasicFind {

    Thread searchThread;
    DoFind currentfind;

    public ThreadedFind(OWLModel owlModel, int type) {
        super(owlModel, type);
    }

    public void startSearch(String s, int type) {
        string = s;

        searchType = type;

        currentfind = new DoFind();

        searchThread = new Thread(currentfind);

        searchThread.start();
    }

    public void cancelSearch() {
        if (searchThread != null && searchThread.isAlive()) {
            currentfind.stop();
        }
        super.cancelSearch();
    }

    class DoFind implements Runnable {

        private boolean allowRun;

        public void stop() {
            allowRun = false;
        }

        public void run() {

            results.clear();
            allowRun = true;

            notifySearchStarted();

            if ((string != null) && (string.length() > 0)) {

                List searchProps = getSearchProperties();

                for (Iterator i = searchProps.iterator(); i.hasNext() && allowRun;) {
                    Map res = searchOnSlot((Slot) i.next(), string, null, searchType);
                    results.putAll(res);
                    notifyResultsUpdated();
                }
                String lang = owlModel.getDefaultLanguage();
                if (lang != null) {
                    for (Iterator i = searchProps.iterator(); i.hasNext() && allowRun;) {
                        Slot slot = (Slot) i.next();
                        if (!slot.equals(owlModel.getNameSlot())) {
                            Map res = searchOnSlot(slot, string, lang, searchType);
                            results.putAll(res);
                            notifyResultsUpdated();
                        }
                    }
                }
            }

            if (allowRun) {
                notifySearchComplete();
            }
            else {
                notifySearchCancelled();
            }
        }
    }
}
