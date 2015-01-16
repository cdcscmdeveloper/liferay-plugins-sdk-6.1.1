/**
 * UpdateSupplierUserInfoRqSoftCertArraySoftCert.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.userinfo;

public class UpdateSupplierUserInfoRqSoftCertArraySoftCert  implements java.io.Serializable {
    private java.util.Date dateFrom;

    private java.util.Date dateTo;

    private java.math.BigDecimal serialNo;

    public UpdateSupplierUserInfoRqSoftCertArraySoftCert() {
    }

    public UpdateSupplierUserInfoRqSoftCertArraySoftCert(
           java.util.Date dateFrom,
           java.util.Date dateTo,
           java.math.BigDecimal serialNo) {
           this.dateFrom = dateFrom;
           this.dateTo = dateTo;
           this.serialNo = serialNo;
    }


    /**
     * Gets the dateFrom value for this UpdateSupplierUserInfoRqSoftCertArraySoftCert.
     * 
     * @return dateFrom
     */
    public java.util.Date getDateFrom() {
        return dateFrom;
    }


    /**
     * Sets the dateFrom value for this UpdateSupplierUserInfoRqSoftCertArraySoftCert.
     * 
     * @param dateFrom
     */
    public void setDateFrom(java.util.Date dateFrom) {
        this.dateFrom = dateFrom;
    }


    /**
     * Gets the dateTo value for this UpdateSupplierUserInfoRqSoftCertArraySoftCert.
     * 
     * @return dateTo
     */
    public java.util.Date getDateTo() {
        return dateTo;
    }


    /**
     * Sets the dateTo value for this UpdateSupplierUserInfoRqSoftCertArraySoftCert.
     * 
     * @param dateTo
     */
    public void setDateTo(java.util.Date dateTo) {
        this.dateTo = dateTo;
    }


    /**
     * Gets the serialNo value for this UpdateSupplierUserInfoRqSoftCertArraySoftCert.
     * 
     * @return serialNo
     */
    public java.math.BigDecimal getSerialNo() {
        return serialNo;
    }


    /**
     * Sets the serialNo value for this UpdateSupplierUserInfoRqSoftCertArraySoftCert.
     * 
     * @param serialNo
     */
    public void setSerialNo(java.math.BigDecimal serialNo) {
        this.serialNo = serialNo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateSupplierUserInfoRqSoftCertArraySoftCert)) return false;
        UpdateSupplierUserInfoRqSoftCertArraySoftCert other = (UpdateSupplierUserInfoRqSoftCertArraySoftCert) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dateFrom==null && other.getDateFrom()==null) || 
             (this.dateFrom!=null &&
              this.dateFrom.equals(other.getDateFrom()))) &&
            ((this.dateTo==null && other.getDateTo()==null) || 
             (this.dateTo!=null &&
              this.dateTo.equals(other.getDateTo()))) &&
            ((this.serialNo==null && other.getSerialNo()==null) || 
             (this.serialNo!=null &&
              this.serialNo.equals(other.getSerialNo())));
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
        if (getDateFrom() != null) {
            _hashCode += getDateFrom().hashCode();
        }
        if (getDateTo() != null) {
            _hashCode += getDateTo().hashCode();
        }
        if (getSerialNo() != null) {
            _hashCode += getSerialNo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateSupplierUserInfoRqSoftCertArraySoftCert.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", ">>>UpdateSupplierUserInfoRq>SoftCertArray>SoftCert"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateFrom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "DateFrom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateTo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "DateTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serialNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "SerialNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
