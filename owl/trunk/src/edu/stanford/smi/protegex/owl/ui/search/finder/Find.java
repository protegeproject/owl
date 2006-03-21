package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Map;
import java.util.Set;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public interface Find {

    int STARTS_WITH = 0;
    int CONTAINS = 1;
    int ENDS_WITH = 2;
    int EXACTLY_MATCHES = 3;

    String[] searchTypeString = {"starts with", "contains", "ends with", "matches"};

    public void startSearch(String s);

    /**
     * This method should start the search.
     *
     * @param s          the string to search for
     * @param searchType
     */
    public void startSearch(String s, int searchType);

//  /**
//   * Search again within the current results
//   * Useful for when the seach becomes more specific (eg when adding characters
//   * to the search string
//   * @param s the string to seach for
//   */
//  public void refineSearch(String s);

    public void cancelSearch();

    /**
     * This method can be called to get the current results
     *
     * @return a map of Resources as keys with SearchResultItem objects as values
     */
    public Map getResults();

    public Set getResultResources();

    public int getResultCount();

    public String getLastSearch();

    public String getSummaryText();

    public String getDescription();

    OWLModel getModel();

    int getNumSearchProperties();

    int getSearchType();

    void addResultListener(SearchListener l);

    boolean removeResultListener(SearchListener l);
}
