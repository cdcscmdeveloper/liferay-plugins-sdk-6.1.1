/**
 * RsUID_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.osb;

public class RsUID_Type  implements java.io.Serializable {
    private java.lang.String rqUID;

    private java.lang.String asyncRqUID;

    private java.lang.String hostTransRefUID;

    public RsUID_Type() {
    }

    public RsUID_Type(
           java.lang.String rqUID,
           java.lang.String asyncRqUID,
           java.lang.String hostTransRefUID) {
           this.rqUID = rqUID;
           this.asyncRqUID = asyncRqUID;
           this.hostTransRefUID = hostTransRefUID;
    }


    /**
     * Gets the rqUID value for this RsUID_Type.
     * 
     * @return rqUID
     */
    public java.lang.String getRqUID() {
        return rqUID;
    }


    /**
     * Sets the rqUID value for this RsUID_Type.
     * 
     * @param rqUID
     */
    public void setRqUID(java.lang.String rqUID) {
        this.rqUID = rqUID;
    }


    /**
     * Gets the asyncRqUID value for this RsUID_Type.
     * 
     * @return asyncRqUID
     */
    public java.lang.String getAsyncRqUID() {
        return asyncRqUID;
    }


    /**
     * Sets the asyncRqUID value for this RsUID_Type.
     * 
     * @param asyncRqUID
     */
    public void setAsyncRqUID(java.lang.String asyncRqUID) {
        this.asyncRqUID = asyncRqUID;
    }


    /**
     * Gets the hostTransRefUID value for this RsUID_Type.
     * 
     * @return hostTransRefUID
     */
    public java.lang.String getHostTransRefUID() {
        return hostTransRefUID;
    }


    /**
     * Sets the hostTransRefUID value for this RsUID_Type.
     * 
     * @param hostTransRefUID
     */
    public void setHostTransRefUID(java.lang.String hostTransRefUID) {
        this.hostTransRefUID = hostTransRefUID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RsUID_Type)) return false;
        RsUID_Type other = (RsUID_Type) obj;
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
              this.asyncRqUID.equals(other.getAsyncRqUID()))) &&
            ((this.hostTransRefUID==null && other.getHostTransRefUID()==null) || 
             (this.hostTransRefUID!=null &&
              this.hostTransRefUID.equals(other.getHostTransRefUID())));
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
        if (getHostTransRefUID() != null) {
            _hashCode += getHostTransRefUID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RsUID_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RsUID_Type"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostTransRefUID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "HostTransRefUID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", ">HostTransRefUID"));
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
