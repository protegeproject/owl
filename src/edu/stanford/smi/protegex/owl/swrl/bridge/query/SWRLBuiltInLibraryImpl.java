
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.query;


import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.List;

/**
 ** Implementation library for SWRL query built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLQueryBuiltIns">here</a> for
 ** documentation on this built-in library.
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary
{
  private static String QueryLibraryName = "SWRLQueryBuiltIns";

  public SWRLBuiltInLibraryImpl() { super(QueryLibraryName); }

  public void reset() {}

  public boolean select(List<BuiltInArgument> arguments) throws BuiltInException 
  {
    throwSupercededException();

    return false;
  } // select

  public boolean selectDistinct(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throwSupercededException();

    return false;
  } // selectDistinct
  
  public boolean count(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throwSupercededException();

    return false;
  } // count

  public boolean min(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throwSupercededException();

    return false;
  } // count

  public boolean max(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throwSupercededException();

    return false;
  } // count

  public boolean sum(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throwSupercededException();

    return false;
  } // count

  public boolean avg(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throwSupercededException();
    
    return false;
  } // count

  public boolean columnNames(List<BuiltInArgument> arguments) throws BuiltInException
  {   
    throwSupercededException();

    return false;
  } // columnNames

  public boolean orderBy(List<BuiltInArgument> arguments) throws BuiltInException
  {   
    throwSupercededException();

    return false;
  } // orderBy

  public boolean orderByDescending(List<BuiltInArgument> arguments) throws BuiltInException
  {   
    throwSupercededException();

    return false;
  } // orderByDescending

  private void throwSupercededException() throws BuiltInException
  {
    throw new BuiltInException("\nThe query library has been superceded by the SQWRL library; to upgrade, import the SQWRL built-in ontology\n" +
                               "(http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl) from the Protege-OWL repository, give it the\n" +
                               "prefix 'sqwrl', and use this prefix instead of the 'query' prefix for the existing query built-ins");
  } // throwSupercededException

} // SWRLBuiltInLibraryImpl
