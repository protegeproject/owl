package edu.stanford.smi.protegex.owl.model.impl;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateTimeType;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import junit.framework.Assert;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A utility class to manage the mapping of XML datatypes and the OWL classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class XMLSchemaDatatypes {


    public final static String XML_LITERAL = "XMLLiteral";

    public final static XSDDatatype[] floatTypes = new XSDDatatype[]{
            XSDDatatype.XSDfloat,
            XSDDatatype.XSDdecimal,
            XSDDatatype.XSDdouble
    };

    public final static XSDDatatype[] integerTypes = new XSDDatatype[]{
            XSDDatatype.XSDint,
            XSDDatatype.XSDpositiveInteger,
            XSDDatatype.XSDnegativeInteger,
            XSDDatatype.XSDnonPositiveInteger,
            XSDDatatype.XSDnonNegativeInteger,
            XSDDatatype.XSDlong,
            XSDDatatype.XSDinteger,
            XSDDatatype.XSDshort,
            XSDDatatype.XSDbyte,
            XSDDatatype.XSDunsignedLong,
            XSDDatatype.XSDunsignedInt,
            XSDDatatype.XSDunsignedShort,
            XSDDatatype.XSDunsignedByte
    };

    public final static XSDDatatype[] stringTypes = new XSDDatatype[]{
            XSDDatatype.XSDstring,
            XSDDatatype.XSDanyURI,
            XSDDatatype.XSDdate,
            XSDDatatype.XSDdateTime,
            XSDDatatype.XSDduration,
            XSDDatatype.XSDgDay,
            XSDDatatype.XSDgMonth,
            XSDDatatype.XSDgMonthDay,
            XSDDatatype.XSDgYear,
            XSDDatatype.XSDgYearMonth,
            XSDDatatype.XSDtime,
            XSDDatatype.XSDnormalizedString,
            XSDDatatype.XSDtoken,
            XSDDatatype.XSDlanguage,
            XSDDatatype.XSDName,
            XSDDatatype.XSDNCName,
            XSDDatatype.XSDNMTOKEN,
            XSDDatatype.XSDID,
            XSDDatatype.XSDIDREF,
            XSDDatatype.XSDENTITY
    };

    /**
     * Short form (e.g. "string") to Jena XSDDatatype object
     */
    private final static Hashtable alias2XSDDatatype = new Hashtable();

    private final static Hashtable uri2ValueTypeHashtable = new Hashtable();

    private final static Hashtable valueType2URIHashtable = new Hashtable();


    static {
        initHashtables(ValueType.BOOLEAN, XSDDatatype.XSDboolean);
        initHashtables(ValueType.FLOAT, floatTypes);
        initHashtables(ValueType.INTEGER, integerTypes);
        initHashtables(ValueType.STRING, stringTypes);
        initHashtables(ValueType.SYMBOL, XSDDatatype.XSDstring);
        initHashtables(null, XSDDatatype.XSDbase64Binary);
        // initHashtables(ValueType.STRING,  XML_LITERAL);
    }


    public static XSDDatatype[] getAllTypes() {
        List list = new ArrayList();
        list.addAll(Arrays.asList(stringTypes));
        list.add(XSDDatatype.XSDboolean);
        list.addAll(Arrays.asList(floatTypes));
        list.addAll(Arrays.asList(integerTypes));
        return (XSDDatatype[]) list.toArray(new XSDDatatype[0]);
    }


    public static Date getDate(String value) {
        XSDDateTime dt = (XSDDateTime) new XSDDateTimeType("dateTime").parse(value);
        Calendar dateCal = dt.asCalendar();
        dateCal.setTimeZone(new GregorianCalendar().getTimeZone());
        return dateCal.getTime();
    }


    public static String getDateString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, offset);
        XSDDateTime dt = new DateXSDDateTime(cal);
        return dt.toString();
        //Calendar cal = new GregorianCalendar();
        //cal.setTime(date);
        //XSDDateTime dt = new DateXSDDateTime(cal);
        //return dt.toString();
    }


    public static String getDateTimeString(Date date) {
        return getDateString(date) + "T" + getTimeString(date);
    }


    public static String getDefaultAlias(ValueType valueType) {
        return getXSDDatatypeAlias(getValueTypeURI(valueType));
    }


    public static String getDefaultDateValue() {
        return getDateString(new Date());
    }


    public static String getDefaultDateTimeValue() {
        return getDateTimeString(new Date());
    }


    public static String getDefaultTimeValue() {
        return getTimeString(new Date());
    }


    public static XSDDatatype getDefaultXSDDatatype(ValueType valueType) {
        String alias = getDefaultAlias(valueType);
        return getXSDDatatype(alias);
    }


    public static List getSlotSymbols() {
        List values = new ArrayList();
        for (Enumeration enu = uri2ValueTypeHashtable.keys(); enu.hasMoreElements();) {
            String str = (String) enu.nextElement();
            values.add(str);
        }
        return values;
    }


    public static String getTimeString(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }


    public static String getTimeString(int hours, int minutes, int seconds) {
        String str = "";
        str += (hours < 10) ? "0" + hours : "" + hours;
        str += ":";
        str += (minutes < 10) ? "0" + minutes : "" + minutes;
        str += ":";
        str += (seconds < 10) ? "0" + seconds : "" + seconds;
        return str;
    }


    public static ValueType getValueType(String uri) {
        if (uri.equals(OWL.Class.getURI())) {
            return ValueType.CLS;
        }
        else if (uri.equals(RDFS.Literal.getURI())) {
            return ValueType.ANY;
        }
        else if (uri.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral")) {
            return ValueType.STRING;
        }
        int index = uri.lastIndexOf('#');
        if (index >= 0) {
            uri = uri.substring(index + 1);
        }
        ValueType result = (ValueType) uri2ValueTypeHashtable.get(uri); // ???.toLowerCase());
        if (result == null) {
            return ValueType.ANY;
        }
        return result;
    }


    public static String getValueTypeURI(ValueType valueType) {
        Assert.assertTrue(valueType != null && valueType != ValueType.INSTANCE && valueType != ValueType.CLS);
        if (valueType == ValueType.ANY) {
            System.err.println("Warning: Replaced illegal value type \"Any\" with \"String\"");
            valueType = ValueType.STRING;
        }
        return XSDDatatype.XSD + "#" + valueType2URIHashtable.get(valueType);
    }


    public static XSDDatatype getXSDDatatype(String alias) {
        return (XSDDatatype) alias2XSDDatatype.get(alias);
    }


    public static RDFDatatype getRDFDatatype(edu.stanford.smi.protegex.owl.model.RDFSDatatype datatype) {
        if (datatype.equals(datatype.getOWLModel().getRDFXMLLiteralType())) {
            return XMLLiteralType.theXMLLiteralType;
        }
        else {
            return getXSDDatatype(datatype);
        }
    }


    public static XSDDatatype getXSDDatatype(edu.stanford.smi.protegex.owl.model.RDFSDatatype datatype) {
        if (datatype.isSystem()) {
            return (XSDDatatype) alias2XSDDatatype.get(datatype.getLocalName());
        }
        else {
            return getXSDDatatype(datatype.getBaseDatatype());
        }
    }


    public static String getXSDDatatypeAlias(XSDDatatype datatype) {
        String uri = datatype.getURI();
        return getXSDDatatypeAlias(uri);
    }


    public static String getXSDDatatypeAlias(String uri) {
        int index = uri.lastIndexOf('#');
        return uri.substring(index + 1);
    }


    private static void initHashtables(ValueType valueType, String uri) {
        if (valueType2URIHashtable.get(valueType) == null) {
            valueType2URIHashtable.put(valueType, uri); // Only put first value into hashtable
        }
        // ??? uri = uri.toLowerCase();
        if (uri2ValueTypeHashtable.get(uri) == null) {
            uri2ValueTypeHashtable.put(uri, valueType);
        }
    }


    private static void initHashtables(ValueType valueType, XSDDatatype xsd) {
        String fullURI = xsd.getURI();
        int index = fullURI.lastIndexOf('#');
        String uri = fullURI.substring(index + 1);
        if (valueType != null) {
            initHashtables(valueType, uri);
        }
        alias2XSDDatatype.put(uri/*???.toLowerCase()*/, xsd);
    }


    private static void initHashtables(ValueType valueType, XSDDatatype[] xsds) {
        for (int i = 0; i < xsds.length; i++) {
            XSDDatatype xsd = xsds[i];
            initHashtables(valueType, xsd);
        }
    }


    public static boolean isDateSlot(RDFProperty property) {
        return isXMLSchemaSlot(property, XSDDatatype.XSDdate);
    }


    public static boolean isDateTimeSlot(RDFProperty property) {
        return isXMLSchemaSlot(property, XSDDatatype.XSDdateTime);
    }


    public static boolean isTimeSlot(RDFProperty property) {
        return isXMLSchemaSlot(property, XSDDatatype.XSDtime);
    }


    public static boolean isXMLLiteralSlot(Slot slot) {
        if (slot instanceof RDFProperty) {
            RDFProperty rdfProperty = (RDFProperty) slot;
            RDFResource range = rdfProperty.getRange();
            return rdfProperty.getOWLModel().getRDFXMLLiteralType().equals(range);
        }
        else {
            return false;
        }
    }


    public static boolean isValidAlias(String alias, ValueType valueType) {
        XSDDatatype xsd = getXSDDatatype(alias);
        if (xsd != null) {
            if (valueType == ValueType.INTEGER) {
                return Arrays.asList(integerTypes).contains(xsd);
            }
            else if (valueType == ValueType.STRING) {
                return Arrays.asList(stringTypes).contains(xsd);
            }
            else if (valueType == ValueType.FLOAT) {
                return Arrays.asList(floatTypes).contains(xsd);
            }
            else if (valueType == ValueType.BOOLEAN) {
                return xsd.equals(XSDDatatype.XSDboolean);
            }
        }
        return false;
    }


    public static boolean isStringAlias(String alias) {
        return Arrays.asList(stringTypes).contains(getXSDDatatype(alias));
    }


    public static boolean isXMLSchemaSlot(RDFProperty property, XSDDatatype datatype) {
        RDFResource range = property.getRange(true);
        return property.getOWLModel().getRDFSDatatypeByURI(datatype.getURI()).equals(range);
    }


    public static boolean isNumericDatatype(String uri) {
        for (int i = 0; i < floatTypes.length; i++) {
            XSDDatatype datatype = floatTypes[i];
            if (datatype.getURI().equals(uri)) {
                return true;
            }
        }
        for (int i = 0; i < integerTypes.length; i++) {
            XSDDatatype datatype = integerTypes[i];
            if (datatype.getURI().equals(uri)) {
                return true;
            }
        }
        return false;
    }


    static class DateXSDDateTime extends XSDDateTime {

        DateXSDDateTime(Calendar cal) {
            super(cal);
            mask = YEAR_MASK | MONTH_MASK | DAY_MASK;
        }
    }
}
