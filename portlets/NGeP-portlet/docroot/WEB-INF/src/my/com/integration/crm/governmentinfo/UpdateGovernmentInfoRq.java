/**
 * UpdateGovernmentInfoRq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.com.integration.crm.governmentinfo;

public class UpdateGovernmentInfoRq  implements java.io.Serializable {
    private my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqMinistryArrayMinistry[] ministryArray;

    private my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqPTJArrayPTJ[] PTJArray;

    public UpdateGovernmentInfoRq() {
    }

    public UpdateGovernmentInfoRq(
           my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqMinistryArrayMinistry[] ministryArray,
           my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqPTJArrayPTJ[] PTJArray) {
           this.ministryArray = ministryArray;
           this.PTJArray = PTJArray;
    }


    /**
     * Gets the ministryArray value for this UpdateGovernmentInfoRq.
     * 
     * @return ministryArray
     */
    public my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqMinistryArrayMinistry[] getMinistryArray() {
        return ministryArray;
    }


    /**
     * Sets the ministryArray value for this UpdateGovernmentInfoRq.
     * 
     * @param ministryArray
     */
    public void setMinistryArray(my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqMinistryArrayMinistry[] ministryArray) {
        this.ministryArray = ministryArray;
    }


    /**
     * Gets the PTJArray value for this UpdateGovernmentInfoRq.
     * 
     * @return PTJArray
     */
    public my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqPTJArrayPTJ[] getPTJArray() {
        return PTJArray;
    }


    /**
     * Sets the PTJArray value for this UpdateGovernmentInfoRq.
     * 
     * @param PTJArray
     */
    public void setPTJArray(my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRqPTJArrayPTJ[] PTJArray) {
        this.PTJArray = PTJArray;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateGovernmentInfoRq)) return false;
        UpdateGovernmentInfoRq other = (UpdateGovernmentInfoRq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ministryArray==null && other.getMinistryArray()==null) || 
             (this.ministryArray!=null &&
              java.util.Arrays.equals(this.ministryArray, other.getMinistryArray()))) &&
            ((this.PTJArray==null && other.getPTJArray()==null) || 
             (this.PTJArray!=null &&
              java.util.Arrays.equals(this.PTJArray, other.getPTJArray())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMinistryArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMinistryArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMinistryArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPTJArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPTJArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPTJArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateGovernmentInfoRq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", ">UpdateGovernmentInfoRq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ministryArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", "MinistryArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", ">>>UpdateGovernmentInfoRq>MinistryArray>Ministry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", "Ministry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PTJArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", "PTJArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", ">>>UpdateGovernmentInfoRq>PTJArray>PTJ"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", "PTJ"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
