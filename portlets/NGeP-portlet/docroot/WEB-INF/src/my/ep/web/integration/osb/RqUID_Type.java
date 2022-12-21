/**
 * RqUID_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.osb;

public class RqUID_Type  implements java.io.Serializable {
    private java.lang.String rqUID;

    private java.lang.String asyncRqUID;

    public RqUID_Type() {
    }

    public RqUID_Type(
           java.lang.String rqUID,
           java.lang.String asyncRqUID) {
           this.rqUID = rqUID;
           this.asyncRqUID = asyncRqUID;
    }


    /**
     * Gets the rqUID value for this RqUID_Type.
     * 
     * @return rqUID
     */
    public java.lang.String getRqUID() {
        return rqUID;
    }


    /**
     * Sets the rqUID value for this RqUID_Type.
     * 
     * @param rqUID
     */
    public void setRqUID(java.lang.String rqUID) {
        this.rqUID = rqUID;
    }


    /**
     * Gets the asyncRqUID value for this RqUID_Type.
     * 
     * @return asyncRqUID
     */
    public java.lang.String getAsyncRqUID() {
        return asyncRqUID;
    }


    /**
     * Sets the asyncRqUID value for this RqUID_Type.
     * 
     * @param asyncRqUID
     */
    public void setAsyncRqUID(java.lang.String asyncRqUID) {
        this.asyncRqUID = asyncRqUID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RqUID_Type)) return false;
        RqUID_Type other = (RqUID_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rqUID==null && other.getRqUID()==null) || 
             (this.rqUID!=null &&
              this.rqUID.equals(other.getRqUID()))) &&
            ((this.asyncRqUID==null && other.getAsyncRqUID()==null) || 
             (this.asyncRqUID!=null &&
              this.asyncRqUID.equals(other.getAsyncRqUID())));
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
        if (getRqUID() != null) {
            _hashCode += getRqUID().hashCode();
        }
        if (getAsyncRqUID() != null) {
            _hashCode += getAsyncRqUID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RqUID_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqUID_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rqUID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqUID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", ">RqUID"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("asyncRqUID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "AsyncRqUID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", ">AsyncRqUID"));
        elemField.setMinOccurs(0);
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
