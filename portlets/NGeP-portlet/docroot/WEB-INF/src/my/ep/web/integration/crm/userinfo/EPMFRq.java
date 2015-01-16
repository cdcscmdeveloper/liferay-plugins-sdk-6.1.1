/**
 * EPMFRq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.userinfo;

public class EPMFRq  implements java.io.Serializable {
    private my.ep.web.integration.crm.userinfo.RqHeader_Type rqHeader;

    private my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRq updateSupplierUserInfoRq;

    public EPMFRq() {
    }

    public EPMFRq(
           my.ep.web.integration.crm.userinfo.RqHeader_Type rqHeader,
           my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRq updateSupplierUserInfoRq) {
           this.rqHeader = rqHeader;
           this.updateSupplierUserInfoRq = updateSupplierUserInfoRq;
    }


    /**
     * Gets the rqHeader value for this EPMFRq.
     * 
     * @return rqHeader
     */
    public my.ep.web.integration.crm.userinfo.RqHeader_Type getRqHeader() {
        return rqHeader;
    }


    /**
     * Sets the rqHeader value for this EPMFRq.
     * 
     * @param rqHeader
     */
    public void setRqHeader(my.ep.web.integration.crm.userinfo.RqHeader_Type rqHeader) {
        this.rqHeader = rqHeader;
    }


    /**
     * Gets the updateSupplierUserInfoRq value for this EPMFRq.
     * 
     * @return updateSupplierUserInfoRq
     */
    public my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRq getUpdateSupplierUserInfoRq() {
        return updateSupplierUserInfoRq;
    }


    /**
     * Sets the updateSupplierUserInfoRq value for this EPMFRq.
     * 
     * @param updateSupplierUserInfoRq
     */
    public void setUpdateSupplierUserInfoRq(my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRq updateSupplierUserInfoRq) {
        this.updateSupplierUserInfoRq = updateSupplierUserInfoRq;
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
            ((this.updateSupplierUserInfoRq==null && other.getUpdateSupplierUserInfoRq()==null) || 
             (this.updateSupplierUserInfoRq!=null &&
              this.updateSupplierUserInfoRq.equals(other.getUpdateSupplierUserInfoRq())));
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
        if (getUpdateSupplierUserInfoRq() != null) {
            _hashCode += getUpdateSupplierUserInfoRq().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EPMFRq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", ">EPMFRq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rqHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader_Type"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updateSupplierUserInfoRq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "UpdateSupplierUserInfoRq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", ">UpdateSupplierUserInfoRq"));
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
