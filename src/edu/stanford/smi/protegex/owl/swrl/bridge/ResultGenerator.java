
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.Value;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.ResultException;

import java.util.List;

public interface ResultGenerator
{
    static String MinAggregateFunction = "min"; 
    static String MaxAggregateFunction = "max"; 
    static String SumAggregateFunction = "sum"; 
    static String AvgAggregateFunction = "avg"; 
    static String CountAggregateFunction = "count"; 

    void initialize();

    void addSelectedDatatypeColumn(String columnName) throws ResultException;
    void addSelectedObjectColumn(String columnName) throws ResultException;
    void addAggregateColumn(String columnName, String aggregateFunctionName) throws ResultException;
    void addOrderByColumn(String columnName) throws ResultException;

    void setIsOrdered() throws ResultException;
    void setIsDescending() throws ResultException;
    boolean isOrdered();
    boolean isDescending();

    void setColumnDisplayName(String columnName) throws ResultException;

    boolean isConfigured();
    void configured() throws ResultException;

    void openRow() throws ResultException;
    void addRowData(Value value) throws ResultException;
    void closeRow() throws ResultException;

    boolean isRowOpen();
    boolean isPrepared();
    void prepared() throws ResultException;
} // ResultGenerator
