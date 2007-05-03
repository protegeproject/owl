
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;
import java.util.ArrayList;

/**
 ** A class used to bind multiple arguments to a built-in argument. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge#nid8LH">here</a> for details.
 */
public class MultiArgument implements Argument
{
  private List<Argument> arguments;

  public MultiArgument()
  {
    arguments = new ArrayList<Argument>();
  } // MultiArgument

  public MultiArgument(List<Argument> arguments)
  {
    this.arguments = arguments;
  } // MultiArgument

  public void addArgument(Argument argument) { arguments.add(argument); }
  public void setArguments(List<Argument> arguments) { this.arguments = arguments; }
  public List<Argument> getArguments() { return arguments; }
  public int getNumberOfArguments() { return arguments.size(); }
  public boolean hasNoArguments() { return arguments.size() == 0; }
} // MultiArgument
