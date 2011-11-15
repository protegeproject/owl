
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLDataPropertyAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataValue;

/**
 * Class representing a SWRL data valued property atom
 */
public class P3SWRLDataPropertyAtomReference extends P3SWRLBinaryAtomReference implements SWRLDataPropertyAtomReference
{
	private OWLDataPropertyReference property;

	public P3SWRLDataPropertyAtomReference(OWLDataPropertyReference property, SWRLArgumentReference argument1, SWRLArgumentReference argument2)
	{
		super(argument1, argument2);
		this.property = property;
	}

	public P3SWRLDataPropertyAtomReference(OWLDataPropertyReference property)
	{
		this.property = property;
	}

	public OWLDataPropertyReference getProperty()
	{
		return property;
	}

	public String toString()
	{
		String result = "" + getProperty() + "(" + getFirstArgument() + ", ";

		if (getSecondArgument() instanceof SWRLLiteralArgumentReference) {
			SWRLLiteralArgumentReference dataValueArgument = (SWRLLiteralArgumentReference)getSecondArgument();
			DataValue dataValue = dataValueArgument.getLiteral();
			if (dataValue.isString())
				result += "\"" + dataValue + "\"";
			else
				result += "" + dataValue;
		} else
			result += "" + getSecondArgument();

		result += ")";

		return result;
	}
}
