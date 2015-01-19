/**
 * EPMFRq_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.osb.inbound;

public class EPMFRq_Type  implements java.io.Serializable {
    private my.ep.web.integration.osb.inbound.RqHeader_Type rqHeader;

    private my.ep.web.integration.osb.inbound.BatchInboundRq_Type batchInboundRq;

    public EPMFRq_Type() {
    }

    public EPMFRq_Type(
           my.ep.web.integration.osb.inbound.RqHeader_Type rqHeader,
           my.ep.web.integration.osb.inbound.BatchInboundRq_Type batchInboundRq) {
           this.rqHeader = rqHeader;
           this.batchInboundRq = batchInboundRq;
    }


    /**
     * Gets the rqHeader value for this EPMFRq_Type.
     * 
     * @return rqHeader
     */
    public my.ep.web.integration.osb.inbound.RqHeader_Type getRqHeader() {
        return rqHeader;
    }


    /**
     * Sets the rqHeader value for this EPMFRq_Type.
     * 
     * @param rqHeader
     */
    public void setRqHeader(my.ep.web.integration.osb.inbound.RqHeader_Type rqHeader) {
        this.rqHeader = rqHeader;
    }


    /**
     * Gets the batchInboundRq value for this EPMFRq_Type.
     * 
     * @return batchInboundRq
     */
    public my.ep.web.integration.osb.inbound.BatchInboundRq_Type getBatchInboundRq() {
        return batchInboundRq;
    }


    /**
     * Sets the batchInboundRq value for this EPMFRq_Type.
     * 
     * @param batchInboundRq
     */
    public void setBatchInboundRq(my.ep.web.integration.osb.inbound.BatchInboundRq_Type batchInboundRq) {
        this.batchInboundRq = batchInboundRq;
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
            ((this.batchInboundRq==null && other.getBatchInboundRq()==null) || 
             (this.batchInboundRq!=null &&
              this.batchInboundRq.equals(other.getBatchInboundRq())));
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
        if (getBatchInboundRq() != null) {
            _hashCode += getBatchInboundRq().hashCode();
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
        elemField.setFieldName("batchInboundRq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/Batch", "BatchInboundRq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/Batch", "BatchInboundRq_Type"));
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
