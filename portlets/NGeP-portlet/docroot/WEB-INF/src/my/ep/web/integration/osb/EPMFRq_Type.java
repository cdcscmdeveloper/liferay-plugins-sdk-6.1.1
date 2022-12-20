/**
 * EPMFRq_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.osb;

public class EPMFRq_Type  implements java.io.Serializable {
    private my.ep.web.integration.osb.RqHeader_Type rqHeader;

    private my.ep.web.integration.osb.BatchOutboundRq_Type batchOutboundRq;

    public EPMFRq_Type() {
    }

    public EPMFRq_Type(
           my.ep.web.integration.osb.RqHeader_Type rqHeader,
           my.ep.web.integration.osb.BatchOutboundRq_Type batchOutboundRq) {
           this.rqHeader = rqHeader;
           this.batchOutboundRq = batchOutboundRq;
    }


    /**
     * Gets the rqHeader value for this EPMFRq_Type.
     * 
     * @return rqHeader
     */
    public my.ep.web.integration.osb.RqHeader_Type getRqHeader() {
        return rqHeader;
    }


    /**
     * Sets the rqHeader value for this EPMFRq_Type.
     * 
     * @param rqHeader
     */
    public void setRqHeader(my.ep.web.integration.osb.RqHeader_Type rqHeader) {
        this.rqHeader = rqHeader;
    }


    /**
     * Gets the batchOutboundRq value for this EPMFRq_Type.
     * 
     * @return batchOutboundRq
     */
    public my.ep.web.integration.osb.BatchOutboundRq_Type getBatchOutboundRq() {
        return batchOutboundRq;
    }


    /**
     * Sets the batchOutboundRq value for this EPMFRq_Type.
     * 
     * @param batchOutboundRq
     */
    public void setBatchOutboundRq(my.ep.web.integration.osb.BatchOutboundRq_Type batchOutboundRq) {
        this.batchOutboundRq = batchOutboundRq;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPMFRq_Type)) return false;
        EPMFRq_Type other = (EPMFRq_Type) obj;
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
            ((this.batchOutboundRq==null && other.getBatchOutboundRq()==null) || 
             (this.batchOutboundRq!=null &&
              this.batchOutboundRq.equals(other.getBatchOutboundRq())));
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
        if (getBatchOutboundRq() != null) {
            _hashCode += getBatchOutboundRq().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EPMFRq_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/Batch", "EPMFRq_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rqHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader_Type"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchOutboundRq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/Batch", "BatchOutboundRq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/Batch", "BatchOutboundRq_Type"));
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
