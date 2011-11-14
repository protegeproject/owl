package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.ArgumentFactoryImpl;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLIndividualArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLVariableReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.DataValueConversionException;

public abstract class ArgumentFactory
{
	public static ArgumentFactory getFactory()
	{
		return new ArgumentFactoryImpl();
	}

	public abstract SWRLVariableReference createVariableArgument(String variableName);

	public abstract ClassArgument createClassArgument(String classURI);
	public abstract SWRLIndividualArgumentReference createIndividualArgument(String individualURI);
	public abstract ObjectPropertyArgument createObjectPropertyArgument(String propertyURI);
	public abstract DataPropertyArgument createDataPropertyArgument(String propertyURI);

	public abstract SWRLLiteralArgumentReference createDataValueArgument(DataValue dataValue);

	public abstract SWRLLiteralArgumentReference createDataValueArgument(String s);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(boolean b);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(Boolean b);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(int i);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(long l);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(float f);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(double d);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(short s);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(Byte b);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(XSDType xsd);
	public abstract SWRLLiteralArgumentReference createDataValueArgument(Object o) throws DataValueConversionException;

	public abstract MultiArgument createMultiArgument();
	public abstract MultiArgument createMultiArgument(List<BuiltInArgument> arguments);

	public abstract CollectionArgument createCollectionArgument(String collectionName, String collectionID);
}
