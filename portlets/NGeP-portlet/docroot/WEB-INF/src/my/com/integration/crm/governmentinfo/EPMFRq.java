/**
 * EPMFRq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.com.integration.crm.governmentinfo;

public class EPMFRq  implements java.io.Serializable {
    private my.com.integration.crm.governmentinfo.RqHeader_Type rqHeader;

    private my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRq updateGovernmentInfoRq;

    public EPMFRq() {
    }

    public EPMFRq(
           my.com.integration.crm.governmentinfo.RqHeader_Type rqHeader,
           my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRq updateGovernmentInfoRq) {
           this.rqHeader = rqHeader;
           this.updateGovernmentInfoRq = updateGovernmentInfoRq;
    }


    /**
     * Gets the rqHeader value for this EPMFRq.
     * 
     * @return rqHeader
     */
    public my.com.integration.crm.governmentinfo.RqHeader_Type getRqHeader() {
        return rqHeader;
    }


    /**
     * Sets the rqHeader value for this EPMFRq.
     * 
     * @param rqHeader
     */
    public void setRqHeader(my.com.integration.crm.governmentinfo.RqHeader_Type rqHeader) {
        this.rqHeader = rqHeader;
    }


    /**
     * Gets the updateGovernmentInfoRq value for this EPMFRq.
     * 
     * @return updateGovernmentInfoRq
     */
    public my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRq getUpdateGovernmentInfoRq() {
        return updateGovernmentInfoRq;
    }


    /**
     * Sets the updateGovernmentInfoRq value for this EPMFRq.
     * 
     * @param updateGovernmentInfoRq
     */
    public void setUpdateGovernmentInfoRq(my.com.integration.crm.governmentinfo.UpdateGovernmentInfoRq updateGovernmentInfoRq) {
        this.updateGovernmentInfoRq = updateGovernmentInfoRq;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPMFRq)) return false;
        EPMFRq other = (EPMFRq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rqHeader==null && other.getRqHeader()==null) || 
             (this.rqHeader!=null &&
              this.rqHeader.equals(other.getRqHeader()))) &&
            ((this.updateGovernmentInfoRq==null && other.getUpdateGovernmentInfoRq()==null) || 
             (this.updateGovernmentInfoRq!=null &&
              this.updateGovernmentInfoRq.equals(other.getUpdateGovernmentInfoRq())));
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
        if (getRqHeader() != null) {
            _hashCode += getRqHeader().hashCode();
        }
        if (getUpdateGovernmentInfoRq() != null) {
            _hashCode += getUpdateGovernmentInfoRq().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EPMFRq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", ">EPMFRq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rqHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader_Type"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updateGovernmentInfoRq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", "UpdateGovernmentInfoRq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentInfo", ">UpdateGovernmentInfoRq"));
        elemField.setNillable(false);
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
