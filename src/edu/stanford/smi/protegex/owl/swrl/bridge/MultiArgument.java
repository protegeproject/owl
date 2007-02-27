
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;
import java.util.ArrayList;

public class MultiArgument implements Argument
{
  private List<Argument> arguments;

  public MultiArgument()
  {
    arguments = new ArrayList();
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
