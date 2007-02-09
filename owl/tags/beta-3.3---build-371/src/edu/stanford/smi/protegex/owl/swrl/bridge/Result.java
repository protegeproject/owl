
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.Value;
import edu.stanford.smi.protegex.owl.swrl.bridge.ObjectValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.DatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.ResultException;

import java.util.List;

public interface Result
{
    List<String> getColumnNames() throws ResultException;
    int getNumberOfColumns() throws ResultException;
    String getColumnName(int columnIndex) throws ResultException;
    boolean hasObjectValue(String columnName) throws ResultException;
    boolean hasObjectValue(int columnIndex) throws ResultException;
    boolean hasDatatypeValue(String columnName) throws ResultException;
    boolean hasDatatypeValue(int columnIndex) throws ResultException;

    public int getNumberOfRows() throws ResultException;
    void reset() throws ResultException;
    void next() throws ResultException;
    boolean hasNext() throws ResultException;

    List<Value> getRow() throws ResultException;
    Value getValue(String columnName) throws ResultException;
    Value getValue(int columnIndex) throws ResultException;
    Value getValue(int columnIndex, int rowIndex) throws ResultException;
    ObjectValue getObjectValue(String columnName) throws ResultException;
    ObjectValue getObjectValue(int columnIndex) throws ResultException;
    DatatypeValue getDatatypeValue(String columnName) throws ResultException;
    DatatypeValue getDatatypeValue(int columnIndex) throws ResultException;
} // Result
