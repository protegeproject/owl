
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.sqwrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.Argument;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.CollectionArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.DataPropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ObjectPropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.AbstractSWRLBuiltInLibrary;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidBuiltInArgumentException;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLIndividualArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.ClassValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataPropertyValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.IndividualValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.ObjectPropertyValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLNames;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLResultValueFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.SQWRLResultImpl;

/**
 * Implementation library for SQWRL built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SQWRL">here</a> for documentation.
 * 
 * Unlike other built-in libraries, this library needs to be preprocessed by a SQWRL-aware processor.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
	private Map<String, Map<String, Collection<BuiltInArgument>>> collections; // Collection name to IDs to collection map

	private Map<String, Integer> collectionGroupElementNumbersMap; // Collection name to number of elements in group (which will be 0 for ungrouped collections)

	private Set<String> setNames, bagNames;

	private SQWRLResultValueFactory resultValueFactory;

	public SWRLBuiltInLibraryImpl()
	{
		super(SQWRLNames.SQWRLBuiltInLibraryName);

		resultValueFactory = new SQWRLResultValueFactory();
	}

	public void reset()
	{
		collections = new HashMap<String, Map<String, Collection<BuiltInArgument>>>();
		collectionGroupElementNumbersMap = new HashMap<String, Integer>();
		setNames = new HashSet<String>();
		bagNames = new HashSet<String>();
	}

	public boolean select(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();
		checkForUnboundArguments(arguments);
		checkNumberOfArgumentsAtLeastOne(arguments);
		SQWRLResultImpl result = getSQWRLUnpreparedResult(getInvokingRuleName());

		if (!result.isRowOpen())
			result.openRow();

		int argumentIndex = 0;
		for (BuiltInArgument argument : arguments) {
			if (argument instanceof SWRLLiteralArgumentReference) {
				DataValue dataValue = ((SWRLLiteralArgumentReference)argument).getLiteral();
				result.addRowData(dataValue);
			} else if (argument instanceof SWRLIndividualArgumentReference) {
				SWRLIndividualArgumentReference individualArgument = (SWRLIndividualArgumentReference)argument;
				IndividualValue individualValue = resultValueFactory.createIndividualValue(individualArgument.getURI());
				result.addRowData(individualValue);
			} else if (argument instanceof ClassArgument) {
				ClassArgument classArgument = (ClassArgument)argument;
				ClassValue classValue = resultValueFactory.createClassValue(classArgument.getURI());
				result.addRowData(classValue);
			} else if (argument instanceof ObjectPropertyArgument) {
				ObjectPropertyArgument objectPropertyArgument = (ObjectPropertyArgument)argument;
				ObjectPropertyValue objectPropertyValue = resultValueFactory.createObjectPropertyValue(objectPropertyArgument.getURI());
				result.addRowData(objectPropertyValue);
			} else if (argument instanceof DataPropertyArgument) {
				DataPropertyArgument dataPropertyArgument = (DataPropertyArgument)argument;
				DataPropertyValue dataPropertyValue = resultValueFactory.createDataPropertyValue(dataPropertyArgument.getURI());
				result.addRowData(dataPropertyValue);
			} else if (argument instanceof CollectionArgument) {
				throw new InvalidBuiltInArgumentException(argumentIndex, "collections cannot be selected");
			} else
				throw new InvalidBuiltInArgumentException(argumentIndex, "unknown type " + argument.getClass());
			argumentIndex++;
		}

		return false;
	}

	// Preprocessed to signal that duplicates should be removed from result
	public boolean selectDistinct(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();

		return select(arguments);
	}

	public boolean count(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();
		checkForUnboundArguments(arguments);
		checkNumberOfArgumentsEqualTo(1, arguments.size());

		SQWRLResultImpl result = getSQWRLUnpreparedResult(getInvokingRuleName());
		BuiltInArgument argument = arguments.get(0);

		if (!result.isRowOpen())
			result.openRow();

		if (argument instanceof SWRLLiteralArgumentReference) {
			DataValue dataValue = ((SWRLLiteralArgumentReference)argument).getLiteral();
			result.addRowData(dataValue);
		} else if (argument instanceof SWRLIndividualArgumentReference) {
			SWRLIndividualArgumentReference individualArgument = (SWRLIndividualArgumentReference)argument;
			IndividualValue individualValue = resultValueFactory.createIndividualValue(individualArgument.getURI());
			result.addRowData(individualValue);
		} else if (argument instanceof ClassArgument) {
			ClassArgument classArgument = (ClassArgument)argument;
			ClassValue classValue = resultValueFactory.createClassValue(classArgument.getURI());
			result.addRowData(classValue);
		} else if (argument instanceof ObjectPropertyArgument) {
			ObjectPropertyArgument objectPropertyArgument = (ObjectPropertyArgument)argument;
			ObjectPropertyValue objectPropertyValue = resultValueFactory.createObjectPropertyValue(objectPropertyArgument.getURI());
			result.addRowData(objectPropertyValue);
		} else if (argument instanceof DataPropertyArgument) {
			DataPropertyArgument dataPropertyArgument = (DataPropertyArgument)argument;
			DataPropertyValue dataPropertyValue = resultValueFactory.createDataPropertyValue(dataPropertyArgument.getURI());
			result.addRowData(dataPropertyValue);
		} else if (argument instanceof CollectionArgument) {
			throw new InvalidBuiltInArgumentException(0, "collections cannot be counted");
		} else
			throw new InvalidBuiltInArgumentException(0, "unknown type " + argument.getClass());

		return false;
	}

	// This built-in is preprocessed by SWRLProcessor so there is nothing to do here
	public boolean countDistinct(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();
		return count(arguments);
	}

	// These built-in is preprocessed by SWRLProcessor so there is nothing to do here
	public boolean columnNames(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();
		return true;
	}

	public boolean orderBy(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();
		return true;
	}

	public boolean orderByDescending(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();
		return true;
	}

	public boolean limit(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInConsequent();
		return true;
	}

	/*********************************************************************************************************************
	 * 
	 * SQWRL collection operators
	 * 
	 *********************************************************************************************************************/

	public boolean makeSet(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		checkForUnboundNonFirstArguments(arguments);

		final int resultCollectionArgumentNumber = 0, elementArgumentNumber = 1;
		String collectionName = getCollectionName(arguments, resultCollectionArgumentNumber);
		String collectionGroupID = getCollectionGroupIDInMake(arguments); // Get unique ID for collection group (if any); does argument checking
		BuiltInArgument element = arguments.get(elementArgumentNumber);
		Collection<BuiltInArgument> set;

		if (isCollection(collectionName, collectionGroupID))
			set = getCollection(collectionName, collectionGroupID);
		else
			set = createSet(collectionName, collectionGroupID);

		set.add(element);

		if (isUnboundArgument(resultCollectionArgumentNumber, arguments))
			arguments.get(resultCollectionArgumentNumber).setBuiltInResult(createCollectionArgument(collectionName, collectionGroupID));

		return true;
	}

	public boolean makeBag(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		checkForUnboundNonFirstArguments(arguments);

		final int resultCollectionArgumentNumber = 0, elementArgumentNumber = 1;
		String collectionName = getCollectionName(arguments, resultCollectionArgumentNumber);
		String collectionGroupID = getCollectionGroupIDInMake(arguments); // Get unique ID for bag; does argument checking
		BuiltInArgument element = arguments.get(elementArgumentNumber);
		Collection<BuiltInArgument> bag;

		if (isCollection(collectionName, collectionGroupID))
			bag = getCollection(collectionName, collectionGroupID);
		else
			bag = createBag(collectionName, collectionGroupID);

		bag.add(element);

		if (isUnboundArgument(resultCollectionArgumentNumber, arguments))
			arguments.get(resultCollectionArgumentNumber).setBuiltInResult(createCollectionArgument(collectionName, collectionGroupID));

		return true;
	}

	// Preprocesed so nothing to do here
	public boolean groupBy(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		return true;
	}

	/*********************************************************************************************************************
	 * 
	 * SQWRL operators that work with a single collection and return a value or an element or evaluate to true or false
	 * 
	 *********************************************************************************************************************/

	public boolean isEmpty(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		final int sourceCollectionArgumentNumber = 0, numberOfCoreArguments = 1;
		Collection<BuiltInArgument> collection = getCollectionInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);

		return collection.size() == 0;
	}

	public boolean notEmpty(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		return !isEmpty(arguments);
	}

	public boolean size(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreArguments = 2;
		Collection<BuiltInArgument> collection = getCollectionInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);

		return processResultArgument(arguments, resultArgumentNumber, collection.size());
	}

	public boolean element(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreArguments = 2;

		Collection<BuiltInArgument> collection = getCollectionInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);

		return processResultArgument(arguments, resultArgumentNumber, collection);
	}

	public boolean notElement(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		return !element(arguments);
	}

	public boolean min(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, numberOfConsequentArguments = 1;
		boolean result = false;

		if (getIsInConsequent()) { // Simple SQWRL aggregation operator
			checkForUnboundArguments(arguments);
			checkNumberOfArgumentsEqualTo(numberOfConsequentArguments, arguments.size());

			SQWRLResultImpl resultImpl = getSQWRLUnpreparedResult(getInvokingRuleName());
			BuiltInArgument argument = arguments.get(resultArgumentNumber);

			if (!resultImpl.isRowOpen())
				resultImpl.openRow();

			if (argument instanceof SWRLLiteralArgumentReference && ((SWRLLiteralArgumentReference)argument).getLiteral().isNumeric()) {
				DataValue dataValue = ((SWRLLiteralArgumentReference)argument).getLiteral();
				resultImpl.addRowData(dataValue);
			} else
				throw new InvalidBuiltInArgumentException(resultArgumentNumber, "expecting numeric literal, got " + argument);

			result = true;
		} else
			result = least(arguments); // Redirect to SQWRL collection operator

		return result;
	}

	public boolean max(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, numberOfConsequentArguments = 1;
		boolean result = false;

		if (getIsInConsequent()) { // Simple SQWRL aggregation operator
			checkForUnboundArguments(arguments);
			checkNumberOfArgumentsEqualTo(numberOfConsequentArguments, arguments.size());

			SQWRLResultImpl resultImpl = getSQWRLUnpreparedResult(getInvokingRuleName());
			BuiltInArgument argument = arguments.get(resultArgumentNumber);

			if (!resultImpl.isRowOpen())
				resultImpl.openRow();

			if (argument instanceof SWRLLiteralArgumentReference && ((SWRLLiteralArgumentReference)argument).getLiteral().isNumeric()) {
				DataValue dataValue = ((SWRLLiteralArgumentReference)argument).getLiteral();
				resultImpl.addRowData(dataValue);
			} else
				throw new InvalidBuiltInArgumentException(resultArgumentNumber, "expecting numeric literal, got: " + argument);

			result = true;
		} else
			result = greatest(arguments); // Redirect to SQWRL collection operator

		return result;
	}

	public boolean sum(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreAntecedentArguments = 2, numberOfConsequentArguments = 1;
		boolean result = false;

		if (getIsInConsequent()) { // Simple SQWRL aggregation operator
			checkForUnboundArguments(arguments);
			checkNumberOfArgumentsEqualTo(numberOfConsequentArguments, arguments.size());

			SQWRLResultImpl resultImpl = getSQWRLUnpreparedResult(getInvokingRuleName());
			BuiltInArgument argument = arguments.get(resultArgumentNumber);

			if (!resultImpl.isRowOpen())
				resultImpl.openRow();

			if (argument instanceof SWRLLiteralArgumentReference && ((SWRLLiteralArgumentReference)argument).getLiteral().isNumeric()) {
				DataValue dataValue = ((SWRLLiteralArgumentReference)argument).getLiteral();
				resultImpl.addRowData(dataValue);
			} else
				throw new InvalidBuiltInArgumentException(resultArgumentNumber, "expecting numeric literal, got: " + argument);

			result = true;
		} else { // SQWRL collection operator
			Collection<BuiltInArgument> collection = getCollectionInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber,
					numberOfCoreAntecedentArguments);

			if (collection.isEmpty())
				result = false;
			else {
				double sumValue = 0, value;
				for (BuiltInArgument element : collection) {
					checkThatElementIsComparable(element);
					value = getArgumentAsADouble(element);
					sumValue += value;
				}

				result = processResultArgument(arguments, resultArgumentNumber, sumValue);
			}
		}

		return result;
	}

	public boolean avg(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreAntecedentArguments = 2, numberOfConsequentArguments = 1;
		boolean result = false;

		if (getIsInConsequent()) { // Simple SQWRL aggregation operator
			checkForUnboundArguments(arguments);
			checkNumberOfArgumentsEqualTo(numberOfConsequentArguments, arguments.size());

			SQWRLResultImpl resultImpl = getSQWRLUnpreparedResult(getInvokingRuleName());
			Argument argument = arguments.get(0);

			if (!resultImpl.isRowOpen())
				resultImpl.openRow();

			if (argument instanceof SWRLLiteralArgumentReference && ((SWRLLiteralArgumentReference)argument).getLiteral().isNumeric()) {
				DataValue dataValue = ((SWRLLiteralArgumentReference)argument).getLiteral();
				resultImpl.addRowData(dataValue);
			} else
				throw new InvalidBuiltInArgumentException(resultArgumentNumber, "expecting numeric literal, got: " + argument);
		} else { // SQWRL collection operator
			Collection<BuiltInArgument> collection = getCollectionInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber,
					numberOfCoreAntecedentArguments);

			if (collection.isEmpty())
				result = false;
			else {
				double avgValue, sumValue = 0, value;
				for (BuiltInArgument element : collection) {
					checkThatElementIsComparable(element);
					value = getArgumentAsADouble(element);
					sumValue += value;
				}
				avgValue = sumValue / collection.size();

				result = processResultArgument(arguments, resultArgumentNumber, avgValue);
			}
		}

		return result;
	}

	public boolean median(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreConsequentArguments = 2;
		boolean result = false;

		if (getIsInConsequent()) { // Simple SQWRL aggregation operator
			throw new BuiltInException("not implemented");
		} else { // SQWRL collection operator
			Collection<BuiltInArgument> collection = getCollectionInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber,
					numberOfCoreConsequentArguments);

			if (collection.isEmpty())
				result = false;
			else {
				double[] valueArray = new double[collection.size()];
				int count = 0, middle = collection.size() / 2;
				double medianValue, value;

				for (BuiltInArgument element : collection) {
					checkThatElementIsComparable(element);
					value = getArgumentAsADouble(element);
					valueArray[count++] = value;
				}

				Arrays.sort(valueArray);

				if (collection.size() % 2 == 1)
					medianValue = valueArray[middle];
				else
					medianValue = (valueArray[middle - 1] + valueArray[middle]) / 2;

				result = processResultArgument(arguments, resultArgumentNumber, medianValue);
			}
		}

		return result;
	}

	public boolean nth(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;
		boolean result = false;

		if (getIsInConsequent())
			result = true; // Post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments) - 1; // 1-offset for user, 0 for processing

			if (!sortedList.isEmpty()) {
				if (n >= 0 && n < sortedList.size()) {
					BuiltInArgument nth = sortedList.get(n);
					result = processResultArgument(arguments, resultArgumentNumber, nth);
				} else
					result = false;
			}
		}

		return result;
	}

	public boolean greatest(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreArguments = 2;
		boolean result = false;

		if (getIsInConsequent())
			result = true; // Post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);

			if (!sortedList.isEmpty()) {
				BuiltInArgument greatest = sortedList.get(sortedList.size() - 1);
				result = processResultArgument(arguments, resultArgumentNumber, greatest);
			}
		}

		return result;
	}

	public boolean nthGreatest(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;
		boolean result = false;

		if (getIsInConsequent())
			result = true; // Post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments);

			if (!sortedList.isEmpty() && n > 0 && n <= sortedList.size()) {
				BuiltInArgument nthGreatest = sortedList.get(sortedList.size() - n);
				result = processResultArgument(arguments, resultArgumentNumber, nthGreatest);
			} else
				result = false;
		}

		return result;
	}

	public boolean least(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		final int resultArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreArguments = 2;
		boolean result = false;

		if (getIsInConsequent())
			result = true; // Post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);

			if (!sortedList.isEmpty()) {
				BuiltInArgument least = sortedList.get(resultArgumentNumber);
				result = processResultArgument(arguments, resultArgumentNumber, least);
			}
		}

		return result;
	}

	/*********************************************************************************************************************
	 * 
	 * SQWRL operators that work with a single collection and return a collection
	 * 
	 *********************************************************************************************************************/

	public boolean notNthGreatest(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments);

			if (!sortedList.isEmpty() && n > 0 && n <= sortedList.size())
				sortedList.remove(sortedList.size() - n);

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, sortedList);
		}
	}

	public boolean nthSlice(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, sliceSizeArgumentNumber = 3, numberOfCoreArguments = 4;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments) - 1; // 1-offset for user, 0 for processing
			int sliceSize = getArgumentAsAPositiveInteger(sliceSizeArgumentNumber, arguments);
			List<BuiltInArgument> slice = new ArrayList<BuiltInArgument>();

			if (!sortedList.isEmpty() && n >= 0) {
				int startIndex = n;
				int finishIndex = n + sliceSize - 1;
				for (int index = startIndex; index <= finishIndex && index < sortedList.size(); index++)
					slice.add(sortedList.get(index));
			}

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, slice);
		}
	}

	public boolean notNthSlice(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, sliceSizeArgumentNumber = 3, numberOfCoreArguments = 4;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments) - 1; // 1-offset for user, 0 for processing
			int sliceSize = getArgumentAsAPositiveInteger(sliceSizeArgumentNumber, arguments);
			List<BuiltInArgument> notSlice = new ArrayList<BuiltInArgument>();

			if (!sortedList.isEmpty() && n >= 0 && n < sortedList.size()) {
				int startIndex = n;
				int finishIndex = n + sliceSize - 1;
				for (int index = 0; index < sortedList.size(); index++)
					if (index < startIndex || index > finishIndex)
						notSlice.add(sortedList.get(index));
			}

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, notSlice);
		}
	}

	public boolean nthGreatestSlice(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, sliceSizeArgumentNumber = 3, numberOfCoreArguments = 4;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments);
			List<BuiltInArgument> slice = new ArrayList<BuiltInArgument>();
			int sliceSize = getArgumentAsAPositiveInteger(sliceSizeArgumentNumber, arguments);

			if (!sortedList.isEmpty() && n > 0) {
				int startIndex = sortedList.size() - n;
				int finishIndex = startIndex + sliceSize - 1;
				if (startIndex < 0)
					startIndex = 0;
				for (int index = startIndex; index <= finishIndex && index < sortedList.size(); index++)
					slice.add(sortedList.get(index));
			}

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, slice);
		}
	}

	public boolean notNthGreatestSlice(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, sliceSizeArgumentNumber = 3, numberOfCoreArguments = 4;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments);
			int sliceSize = getArgumentAsAPositiveInteger(sliceSizeArgumentNumber, arguments);
			List<BuiltInArgument> slice = new ArrayList<BuiltInArgument>();

			if (!sortedList.isEmpty() && n > 0 && n <= sortedList.size()) {
				int startIndex = sortedList.size() - n;
				int finishIndex = startIndex + sliceSize - 1;
				for (int index = 0; index < sortedList.size(); index++)
					if (index < startIndex || index > finishIndex)
						slice.add(sortedList.get(index));
			}

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, slice);
		}
	}

	public boolean notNth(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments) - 1; // 1-offset for user, 0 for processing

			if (!sortedList.isEmpty() && n >= 0 && n < sortedList.size())
				sortedList.remove(n);

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, sortedList);
		}
	}

	public boolean notGreatest(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreArguments = 2;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);

			if (!sortedList.isEmpty())
				sortedList.remove(sortedList.size() - 1);

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, sortedList);
		}
	}

	public boolean greatestN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments);
			List<BuiltInArgument> greatestN = new ArrayList<BuiltInArgument>();

			if (!sortedList.isEmpty() && n > 0) {
				int startIndex = sortedList.size() - n;
				if (startIndex < 0)
					startIndex = 0;
				for (int i = startIndex; i < sortedList.size(); i++)
					greatestN.add(sortedList.get(i));
			}

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, greatestN);
		}
	}

	public boolean notGreatestN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments);
			List<BuiltInArgument> notGreatestN = new ArrayList<BuiltInArgument>();

			if (!sortedList.isEmpty() && n > 0) {
				int startIndex = sortedList.size() - n;
				if (startIndex < 0)
					startIndex = 0;
				for (int i = 0; i < startIndex; i++)
					notGreatestN.add(sortedList.get(i));
			}

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, notGreatestN);
		}
	}

	public boolean notLeast(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, numberOfCoreArguments = 2;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);

			if (!sortedList.isEmpty())
				sortedList.remove(0); // Remove the first (least) element; if there are multiple element with same least value, they will not be removed

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, sortedList);
		}
	}

	public boolean leastN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments) - 1;
			List<BuiltInArgument> leastN = new ArrayList<BuiltInArgument>();

			for (int i = 0; i <= n && i < sortedList.size(); i++)
				leastN.add(sortedList.get(i));

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, leastN);
		}
	}

	public boolean notLeastN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		final int resultCollectionArgumentNumber = 0, sourceCollectionArgumentNumber = 1, nArgumentNumber = 2, numberOfCoreArguments = 3;

		if (getIsInConsequent())
			return true; // Non collection operator that is post processed - ignore
		else {
			List<BuiltInArgument> sortedList = getSortedListInSingleOperandCollectionOperation(arguments, sourceCollectionArgumentNumber, numberOfCoreArguments);
			int n = getArgumentAsAPositiveInteger(nArgumentNumber, arguments);
			List<BuiltInArgument> notLeastN = new ArrayList<BuiltInArgument>();

			for (int i = n; i >= 0 && i < sortedList.size(); i++)
				notLeastN.add(sortedList.get(i));

			return processSingleOperandCollectionOperationListResult(arguments, resultCollectionArgumentNumber, sourceCollectionArgumentNumber,
					numberOfCoreArguments, notLeastN);
		}
	}

	/*********************************************************************************************************************
	 * 
	 * SQWRL operators that work with two collections and return an element or evaluate to true or false
	 * 
	 *********************************************************************************************************************/

	public boolean intersects(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();

		final int collection1ArgumentNumber = 0, collection2ArgumentNumber = 1, numberOfCoreArguments = 2;
		String collection1Name = getCollectionName(arguments, collection1ArgumentNumber);
		String collection2Name = getCollectionName(arguments, collection2ArgumentNumber);
		int collection1NumberOfGroupElements = getNumberOfGroupElements(collection1Name);
		int collection2NumberOfGroupElements = getNumberOfGroupElements(collection2Name);
		String collection1GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection1ArgumentNumber, numberOfCoreArguments, 0,
				collection1NumberOfGroupElements);
		String collection2GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection2ArgumentNumber, numberOfCoreArguments,
				collection1NumberOfGroupElements, collection2NumberOfGroupElements);
		Collection<BuiltInArgument> collection1 = getCollection(collection1Name, collection1GroupID);
		Collection<BuiltInArgument> collection2 = getCollection(collection2Name, collection2GroupID);

		return !Collections.disjoint(collection1, collection2);
	}

	public boolean notIntersects(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		return !intersects(arguments);
	}

	public boolean contains(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		final int collection1ArgumentNumber = 0, collection2ArgumentNumber = 1, numberOfCoreArguments = 2;
		String collection1Name = getCollectionName(arguments, collection1ArgumentNumber);
		String collection2Name = getCollectionName(arguments, collection2ArgumentNumber);
		int collection1NumberOfGroupElements = getNumberOfGroupElements(collection1Name);
		int collection2NumberOfGroupElements = getNumberOfGroupElements(collection2Name);
		String collection1GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection1ArgumentNumber, numberOfCoreArguments, 0,
				collection1NumberOfGroupElements);
		String collection2GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection2ArgumentNumber, numberOfCoreArguments,
				collection1NumberOfGroupElements, collection2NumberOfGroupElements);
		Collection<BuiltInArgument> collection1 = getCollection(collection1Name, collection1GroupID);
		Collection<BuiltInArgument> collection2 = getCollection(collection2Name, collection2GroupID);

		return collection1.containsAll(collection2);
	}

	public boolean notContains(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		return !contains(arguments);
	}

	public boolean equal(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		final int collection1ArgumentNumber = 0, collection2ArgumentNumber = 1, numberOfCoreArguments = 2;
		String collection1Name = getCollectionName(arguments, collection1ArgumentNumber);
		String collection2Name = getCollectionName(arguments, collection2ArgumentNumber);
		int collection1NumberOfGroupElements = getNumberOfGroupElements(collection1Name);
		int collection2NumberOfGroupElements = getNumberOfGroupElements(collection2Name);
		String collection1GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection1ArgumentNumber, numberOfCoreArguments, 0,
				collection1NumberOfGroupElements);
		String collection2GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection2ArgumentNumber, numberOfCoreArguments,
				collection1NumberOfGroupElements, collection2NumberOfGroupElements);

		if (collection1GroupID.equals(collection2GroupID))
			return true; // The same collection was passed
		else { // Different collections - compare them
			Collection<BuiltInArgument> collection1 = getCollection(collection1Name, collection1GroupID);
			Collection<BuiltInArgument> collection2 = getCollection(collection2Name, collection2GroupID);
			return collection1.equals(collection2); // Remember, sets and lists will not be equal
		}
	}

	public boolean notEqual(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		return !equal(arguments);
	}

	/*********************************************************************************************************************
	 * 
	 * SQWRL operators that work with two collections and return a collection
	 * 
	 *********************************************************************************************************************/

	public boolean intersection(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		final int resultCollectionArgumentNumber = 0, collection1ArgumentNumber = 1, collection2ArgumentNumber = 2, numberOfCoreArguments = 3;
		String resultCollectionName = getCollectionName(arguments, resultCollectionArgumentNumber);
		String collection1Name = getCollectionName(arguments, collection1ArgumentNumber);
		String collection2Name = getCollectionName(arguments, collection2ArgumentNumber);
		int collection1NumberOfGroupElements = getNumberOfGroupElements(collection1Name);
		int collection2NumberOfGroupElements = getNumberOfGroupElements(collection2Name);
		int collectionResultNumberOfGroupElements = collection1NumberOfGroupElements + collection2NumberOfGroupElements;
		String resultCollectionGroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, resultCollectionArgumentNumber, numberOfCoreArguments, 0,
				collectionResultNumberOfGroupElements);
		String collection1GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection1ArgumentNumber, numberOfCoreArguments, 0,
				collection1NumberOfGroupElements);
		String collection2GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection2ArgumentNumber, numberOfCoreArguments,
				0 + collection1NumberOfGroupElements, collection2NumberOfGroupElements);
		Collection<BuiltInArgument> collection1 = getCollection(collection1Name, collection1GroupID);
		Collection<BuiltInArgument> collection2 = getCollection(collection2Name, collection2GroupID);
		Collection<BuiltInArgument> intersection = new HashSet<BuiltInArgument>(collection1);

		intersection.retainAll(collection2);

		if (!isCollection(resultCollectionName, resultCollectionGroupID))
			recordCollection(resultCollectionName, resultCollectionGroupID, intersection);

		if (isUnboundArgument(resultCollectionArgumentNumber, arguments))
			arguments.get(resultCollectionArgumentNumber).setBuiltInResult(createCollectionArgument(resultCollectionName, resultCollectionGroupID));

		return true;
	}

	public boolean append(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		final int resultCollectionArgumentNumber = 0, collection1ArgumentNumber = 1, collection2ArgumentNumber = 2, numberOfCoreArguments = 3;
		String resultCollectionName = getCollectionName(arguments, resultCollectionArgumentNumber);
		String collection1Name = getCollectionName(arguments, collection1ArgumentNumber);
		String collection2Name = getCollectionName(arguments, collection2ArgumentNumber);
		int collection1NumberOfGroupElements = getNumberOfGroupElements(collection1Name);
		int collection2NumberOfGroupElements = getNumberOfGroupElements(collection2Name);
		int resultCollectionNumberOfGroupElements = collection1NumberOfGroupElements + collection2NumberOfGroupElements;
		String collection1GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection1ArgumentNumber, numberOfCoreArguments, 0,
				collection1NumberOfGroupElements);
		String collection2GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection2ArgumentNumber, numberOfCoreArguments,
				0 + collection1NumberOfGroupElements, collection2NumberOfGroupElements);
		String resultCollectionGroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, resultCollectionArgumentNumber, numberOfCoreArguments, 0,
				resultCollectionNumberOfGroupElements);
		Collection<BuiltInArgument> collection1 = getCollection(collection1Name, collection1GroupID);
		Collection<BuiltInArgument> collection2 = getCollection(collection2Name, collection2GroupID);
		List<BuiltInArgument> resultCollection = new ArrayList<BuiltInArgument>(collection1);

		resultCollection.addAll(collection2);

		if (!isCollection(resultCollectionName, resultCollectionGroupID))
			recordCollection(resultCollectionName, resultCollectionGroupID, resultCollection);

		if (isUnboundArgument(resultCollectionArgumentNumber, arguments))
			arguments.get(resultCollectionArgumentNumber).setBuiltInResult(createCollectionArgument(resultCollectionName, resultCollectionGroupID));

		return true;
	}

	public boolean union(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		final int resultCollectionArgumentNumber = 0, collection1ArgumentNumber = 1, collection2ArgumentNumber = 2, numberOfCoreArguments = 3;
		String resultCollectionName = getCollectionName(arguments, resultCollectionArgumentNumber);
		String collection1Name = getCollectionName(arguments, collection1ArgumentNumber);
		String collection2Name = getCollectionName(arguments, collection2ArgumentNumber);
		int collection1NumberOfGroupElements = getNumberOfGroupElements(collection1Name);
		int collection2NumberOfGroupElements = getNumberOfGroupElements(collection2Name);
		int resultCollectionNumberOfGroupElements = collection1NumberOfGroupElements + collection2NumberOfGroupElements;
		String collection1GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection1ArgumentNumber, numberOfCoreArguments, 0,
				collection1NumberOfGroupElements);
		String collection2GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection2ArgumentNumber, numberOfCoreArguments,
				0 + collection1NumberOfGroupElements, collection2NumberOfGroupElements);
		String resultCollectionGroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, resultCollectionArgumentNumber, numberOfCoreArguments, 0,
				resultCollectionNumberOfGroupElements);
		Collection<BuiltInArgument> collection1 = getCollection(collection1Name, collection1GroupID);
		Collection<BuiltInArgument> collection2 = getCollection(collection2Name, collection2GroupID);
		Set<BuiltInArgument> union = new HashSet<BuiltInArgument>(collection1);

		union.addAll(collection2);

		if (!isCollection(resultCollectionName, resultCollectionGroupID))
			recordCollection(resultCollectionName, resultCollectionGroupID, union);

		if (isUnboundArgument(resultCollectionArgumentNumber, arguments))
			arguments.get(resultCollectionArgumentNumber).setBuiltInResult(createCollectionArgument(resultCollectionName, resultCollectionGroupID));

		return true;
	}

	public boolean difference(List<BuiltInArgument> arguments) throws BuiltInException
	{
		checkThatInAntecedent();
		
		final int resultCollectionArgumentNumber = 0, collection1ArgumentNumber = 1, collection2ArgumentNumber = 2, numberOfCoreArguments = 3;
		String resultCollectionName = getCollectionName(arguments, resultCollectionArgumentNumber);
		String collection1Name = getCollectionName(arguments, collection1ArgumentNumber);
		String collection2Name = getCollectionName(arguments, collection2ArgumentNumber);
		int collection1NumberOfGroupElements = getNumberOfGroupElements(collection1Name);
		int collection2NumberOfGroupElements = getNumberOfGroupElements(collection2Name);
		int collectionResultNumberOfGroupElements = collection1NumberOfGroupElements + collection2NumberOfGroupElements;
		String resultCollectionGroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, resultCollectionArgumentNumber, numberOfCoreArguments, 0,
				collectionResultNumberOfGroupElements);
		String collection1GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection1ArgumentNumber, numberOfCoreArguments, 0,
				collection1NumberOfGroupElements);
		String collection2GroupID = getCollectionGroupIDInMultiOperandCollectionOperation(arguments, collection2ArgumentNumber, numberOfCoreArguments,
				0 + collection1NumberOfGroupElements, collection2NumberOfGroupElements);
		Collection<BuiltInArgument> collection1 = getCollection(collection1Name, collection1GroupID);
		Collection<BuiltInArgument> collection2 = getCollection(collection2Name, collection2GroupID);
		Collection<BuiltInArgument> difference = new HashSet<BuiltInArgument>(collection1);

		difference.removeAll(collection2);

		if (!isCollection(resultCollectionName, resultCollectionGroupID))
			recordCollection(resultCollectionName, resultCollectionGroupID, difference);

		if (isUnboundArgument(resultCollectionArgumentNumber, arguments))
			arguments.get(resultCollectionArgumentNumber).setBuiltInResult(createCollectionArgument(resultCollectionName, resultCollectionGroupID));

		return true;
	}

	/*********************************************************************************************************************
	 * 
	 * Alias definitions for SQWRL operators
	 * 
	 *********************************************************************************************************************/

	public boolean nthLast(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return nthGreatest(arguments);
	}

	public boolean notNthLast(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return notNthGreatest(arguments);
	}

	public boolean nthLastSlice(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return nthGreatestSlice(arguments);
	}

	public boolean notNthLastSlice(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return notNthGreatestSlice(arguments);
	}

	public boolean last(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return greatest(arguments);
	}

	public boolean notLast(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return notGreatest(arguments);
	}

	public boolean lastN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return greatestN(arguments);
	}

	public boolean notLastN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return notGreatestN(arguments);
	}

	public boolean first(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return least(arguments);
	}

	public boolean notFirst(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return notLeast(arguments);
	}

	public boolean firstN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return leastN(arguments);
	}

	public boolean notFirstN(List<BuiltInArgument> arguments) throws BuiltInException
	{
		return notLeastN(arguments);
	}

	/*********************************************************************************************************************
	 * 
	 * Internal methods
	 * 
	 *********************************************************************************************************************/

	private String getCollectionGroupIDInMake(List<BuiltInArgument> arguments) throws BuiltInException
	{
		// The collection is the first argument, the element is the second; subsequent arguments (if any) are group elements
		final int collectionArgumentNumber = 0, numberOfCoreArguments = 2;
		checkNumberOfArgumentsAtLeast(numberOfCoreArguments, arguments.size());

		String queryName = getInvokingRuleName();
		String collectionName = getCollectionName(arguments, collectionArgumentNumber);
		int numberOfGroupArguments = arguments.size() - numberOfCoreArguments;
		boolean hasGroupPattern = numberOfGroupArguments != 0;
		String groupPattern = !hasGroupPattern ? "" : createInvocationPattern(getBuiltInBridge(), queryName, 0, false, arguments.subList(2, arguments.size()));

		if (isBoundArgument(collectionArgumentNumber, arguments) && !collectionGroupElementNumbersMap.containsKey(collectionName)) {
			// Collection variable already used in non collection context
			throw new BuiltInException("collection variable ?" + arguments.get(collectionArgumentNumber).getVariableName()
					+ " already used in non collection context");
		}

		if (hasGroupPattern) {
			if (!collectionGroupElementNumbersMap.containsKey(collectionName))
				collectionGroupElementNumbersMap.put(collectionName, numberOfGroupArguments);
			else if (collectionGroupElementNumbersMap.get(collectionName) != numberOfGroupArguments) {
				throw new BuiltInException("internal error: inconsistent number of group elements for collection " + collectionName);
			}
			return groupPattern;
		} else {
			if (collectionGroupElementNumbersMap.containsKey(collectionName)) {
				if (collectionGroupElementNumbersMap.get(collectionName) != 0) {
					throw new BuiltInException("internal error: inconsistent number of group elements for collection " + collectionName);
				}
			} else
				collectionGroupElementNumbersMap.put(collectionName, 0);
			return "";
		}
	}

	private String getCollectionGroupIDInSingleCollectionOperation(List<BuiltInArgument> arguments, int collectionArgumentNumber, int coreNumberOfArguments)
			throws BuiltInException
	{
		String queryName = getInvokingRuleName();

		checkThatInAntecedent();

		if ((arguments.size() > coreNumberOfArguments)) // Is grouped
			return createInvocationPattern(getBuiltInBridge(), queryName, 0, false, arguments.subList(coreNumberOfArguments, arguments.size()));
		else
			return "";
	}

	private String getCollectionGroupIDInMultiOperandCollectionOperation(List<BuiltInArgument> arguments, int collectionArgumentNumber,
																																				int coreNumberOfArguments, int groupArgumentOffset, int numberOfRelevantGroupArguments)
			throws BuiltInException
	{
		String queryName = getInvokingRuleName();
		String collectionName = getCollectionName(arguments, collectionArgumentNumber);

		checkThatInAntecedent();

		if (!collectionGroupElementNumbersMap.containsKey(collectionName))
			collectionGroupElementNumbersMap.put(collectionName, numberOfRelevantGroupArguments);

		if (numberOfRelevantGroupArguments != 0) // Is grouped
			return createInvocationPattern(getBuiltInBridge(), queryName, 0, false,
					arguments.subList(coreNumberOfArguments + groupArgumentOffset, coreNumberOfArguments + groupArgumentOffset + numberOfRelevantGroupArguments));
		else
			return "";
	}

	private boolean processSingleOperandCollectionOperationListResult(List<BuiltInArgument> arguments, int resultCollectionArgumentNumber,
																																		int sourceCollectionArgumentNumber, int numberOfCoreArguments,
																																		Collection<BuiltInArgument> resultList) throws BuiltInException
	{
		String sourceCollectionName = getCollectionName(arguments, sourceCollectionArgumentNumber);
		String resultCollectionName = getCollectionName(arguments, resultCollectionArgumentNumber);
		String resultCollectionGroupID = getCollectionGroupIDInSingleCollectionOperation(arguments, resultCollectionArgumentNumber, numberOfCoreArguments);

		if (!isCollection(resultCollectionName, resultCollectionGroupID))
			recordCollection(resultCollectionName, resultCollectionGroupID, resultList);

		if (!collectionGroupElementNumbersMap.containsKey(resultCollectionName)) // Give it the same number of group elements as the source collection
			collectionGroupElementNumbersMap.put(resultCollectionName, getNumberOfGroupElements(sourceCollectionName));

		return processListResultArgument(arguments, resultCollectionArgumentNumber, resultCollectionName, resultCollectionGroupID, resultList);
	}

	private boolean processListResultArgument(List<BuiltInArgument> arguments, int resultArgumentNumber, String resultListName, String resultListID,
																						Collection<BuiltInArgument> resultList) throws BuiltInException
	{
		checkArgumentNumber(resultArgumentNumber, arguments);

		if (isUnboundArgument(resultArgumentNumber, arguments)) {
			arguments.get(resultArgumentNumber).setBuiltInResult(createCollectionArgument(resultListName, resultListID));
			return true;
		} else {
			Collection<BuiltInArgument> collection = getCollection(resultListName, resultListID);
			return collection.equals(resultList); // Remember, sets and lists will not be equal
		}
	}

	private SQWRLResultImpl getSQWRLUnpreparedResult(String queryURI) throws BuiltInException
	{
		return getBuiltInBridge().getSQWRLUnpreparedResult(queryURI);
	}

	private void checkThatElementIsComparable(BuiltInArgument element) throws BuiltInException
	{
		if (!(element instanceof SWRLLiteralArgumentReference) || !((SWRLLiteralArgumentReference)element).getLiteral().isComparable())
			throw new BuiltInException("may only be applied to collections with comparable elements");
	}

	private Collection<BuiltInArgument> getCollectionInSingleCollectionOperation(List<BuiltInArgument> arguments, int sourceCollectionArgumentNumber,
																																								int coreNumberOfArguments) throws BuiltInException
	{
		String collectionName = getCollectionName(arguments, sourceCollectionArgumentNumber);
		String collectionGroupID = getCollectionGroupIDInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber, coreNumberOfArguments);

		return getCollection(collectionName, collectionGroupID);
	}

	private List<BuiltInArgument> getSortedListInSingleOperandCollectionOperation(List<BuiltInArgument> arguments, int sourceCollectionArgumentNumber,
																																								int coreNumberOfArguments) throws BuiltInException
	{
		String collectionName = getCollectionName(arguments, sourceCollectionArgumentNumber);
		String collectionGroupID = getCollectionGroupIDInSingleCollectionOperation(arguments, sourceCollectionArgumentNumber, coreNumberOfArguments);

		return getSortedList(collectionName, collectionGroupID);
	}

	// We do not cache because only one built-in will typically perform an operation on a particular collection per query.
	// Note: currently implementations may modify the returned collection.
	private List<BuiltInArgument> getSortedList(String collectionName, String collectionGroupID) throws BuiltInException
	{
		Collection<BuiltInArgument> collection = getCollection(collectionName, collectionGroupID);
		List<BuiltInArgument> result = new ArrayList<BuiltInArgument>(collection);
		Collections.sort(result);

		return result;
	}

	private List<BuiltInArgument> createBag(String collectionName, String collectionGroupID) throws BuiltInException
	{
		List<BuiltInArgument> bag = new ArrayList<BuiltInArgument>();
		recordCollection(collectionName, collectionGroupID, bag);
		bagNames.add(collectionName);
		return bag;
	}

	private Set<BuiltInArgument> createSet(String collectionName, String collectionGroupID) throws BuiltInException
	{
		Set<BuiltInArgument> set = new HashSet<BuiltInArgument>();
		recordCollection(collectionName, collectionGroupID, set);
		return set;
	}

	private String getCollectionName(List<BuiltInArgument> arguments, int collectionArgumentNumber) throws BuiltInException
	{
		String queryName = getInvokingRuleName();
		String collectionName = getVariableName(collectionArgumentNumber, arguments);
		return queryName + ":" + collectionName;
	}

	private int getNumberOfGroupElements(String collectionName) throws BuiltInException
	{
		if (!collectionGroupElementNumbersMap.containsKey(collectionName))
			throw new BuiltInException("internal error: invalid collection name " + collectionName + "; no group element number found");

		return collectionGroupElementNumbersMap.get(collectionName);
	}

	// An ungrouped collection will have a collectionGroupID of the empty string so will not be partitioned.
	private void recordCollection(String collectionName, String collectionGroupID, Collection<BuiltInArgument> collection) throws BuiltInException
	{
		if (!isCollection(collectionName)) {
			if (isBag(collection))
				bagNames.add(collectionName);
			else if (isSet(collection))
				setNames.add(collectionName);
			else
				throw new BuiltInException("internal error: collection " + collectionName + " with group ID " + collectionGroupID + " is neither a bag or a set");

			collections.put(collectionName, new HashMap<String, Collection<BuiltInArgument>>());
		}

		if (!isCollection(collectionName, collectionGroupID)) {
			if (isBag(collectionName) && !isBag(collection))
				throw new BuiltInException("attempt to add non bag elements to bag " + collectionName + "; group ID=" + collectionGroupID);

			if (isSet(collectionName) && !isSet(collection))
				throw new BuiltInException("attempt to add non set elements to set " + collectionName + "; group ID=" + collectionGroupID);

			collections.get(collectionName).put(collectionGroupID, collection);
		}
	}

	private Collection<BuiltInArgument> getCollection(String collectionName, String collectionGroupID) throws BuiltInException
	{
		if (!isCollection(collectionName, collectionGroupID))
			throw new BuiltInException("argument " + collectionName + " with group ID " + collectionGroupID + " does not refer to a collection");

		return collections.get(collectionName).get(collectionGroupID);
	}

	private boolean isCollection(String collectionName, String collectionGroupID)
	{
		return collections.containsKey(collectionName) && collections.get(collectionName).containsKey(collectionGroupID);
	}

	private boolean isSet(String collectionName)
	{
		return setNames.contains(collectionName);
	}

	private boolean isBag(String collectionName)
	{
		return bagNames.contains(collectionName);
	}

	private boolean isCollection(String collectionName) throws BuiltInException
	{
		return collections.containsKey(collectionName);
	}

	private boolean isBag(Collection<BuiltInArgument> collection)
	{
		return (collection instanceof List<?>);
	}

	private boolean isSet(Collection<BuiltInArgument> collection)
	{
		return (collection instanceof Set<?>);
	}

	@SuppressWarnings("unused")
	private Collection<BuiltInArgument> ungroupCollection(String collectionName) throws BuiltInException
	{
		if (!isCollection(collectionName))
			throw new BuiltInException(collectionName + "is not a collection");
		else {
			Collection<BuiltInArgument> ungroupedCollection = isSet(collectionName) ? new HashSet<BuiltInArgument>() : new ArrayList<BuiltInArgument>();

			for (String collectionGroupID : collections.get(collectionName).keySet()) {
				ungroupedCollection.addAll(collections.get(collectionName).get(collectionGroupID));
			}
			return ungroupedCollection;
		}
	}

}
