/**
 * UpdateSupplierInfoRqApplArrayAppl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.supplierinfo;

public class UpdateSupplierInfoRqApplArrayAppl  implements java.io.Serializable {
    private java.lang.String applNo;

    private java.math.BigDecimal applStatus;

    private java.lang.String appType;

    private java.lang.String mofNo;

    private java.util.Date expiryDate;

    private java.lang.String regNo;

    private java.math.BigDecimal accountStatus;

    private java.math.BigDecimal capitalPaidUp;

    public UpdateSupplierInfoRqApplArrayAppl() {
    }

    public UpdateSupplierInfoRqApplArrayAppl(
           java.lang.String applNo,
           java.math.BigDecimal applStatus,
           java.lang.String appType,
           java.lang.String mofNo,
           java.util.Date expiryDate,
           java.lang.String regNo,
           java.math.BigDecimal accountStatus,
           java.math.BigDecimal capitalPaidUp) {
           this.applNo = applNo;
           this.applStatus = applStatus;
           this.appType = appType;
           this.mofNo = mofNo;
           this.expiryDate = expiryDate;
           this.regNo = regNo;
           this.accountStatus = accountStatus;
           this.capitalPaidUp = capitalPaidUp;
    }


    /**
     * Gets the applNo value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return applNo
     */
    public java.lang.String getApplNo() {
        return applNo;
    }


    /**
     * Sets the applNo value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param applNo
     */
    public void setApplNo(java.lang.String applNo) {
        this.applNo = applNo;
    }


    /**
     * Gets the applStatus value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return applStatus
     */
    public java.math.BigDecimal getApplStatus() {
        return applStatus;
    }


    /**
     * Sets the applStatus value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param applStatus
     */
    public void setApplStatus(java.math.BigDecimal applStatus) {
        this.applStatus = applStatus;
    }


    /**
     * Gets the appType value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return appType
     */
    public java.lang.String getAppType() {
        return appType;
    }


    /**
     * Sets the appType value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param appType
     */
    public void setAppType(java.lang.String appType) {
        this.appType = appType;
    }


    /**
     * Gets the mofNo value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return mofNo
     */
    public java.lang.String getMofNo() {
        return mofNo;
    }


    /**
     * Sets the mofNo value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param mofNo
     */
    public void setMofNo(java.lang.String mofNo) {
        this.mofNo = mofNo;
    }


    /**
     * Gets the expiryDate value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return expiryDate
     */
    public java.util.Date getExpiryDate() {
        return expiryDate;
    }


    /**
     * Sets the expiryDate value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param expiryDate
     */
    public void setExpiryDate(java.util.Date expiryDate) {
        this.expiryDate = expiryDate;
    }


    /**
     * Gets the regNo value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return regNo
     */
    public java.lang.String getRegNo() {
        return regNo;
    }


    /**
     * Sets the regNo value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param regNo
     */
    public void setRegNo(java.lang.String regNo) {
        this.regNo = regNo;
    }


    /**
     * Gets the accountStatus value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return accountStatus
     */
    public java.math.BigDecimal getAccountStatus() {
        return accountStatus;
    }


    /**
     * Sets the accountStatus value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param accountStatus
     */
    public void setAccountStatus(java.math.BigDecimal accountStatus) {
        this.accountStatus = accountStatus;
    }


    /**
     * Gets the capitalPaidUp value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @return capitalPaidUp
     */
    public java.math.BigDecimal getCapitalPaidUp() {
        return capitalPaidUp;
    }


    /**
     * Sets the capitalPaidUp value for this UpdateSupplierInfoRqApplArrayAppl.
     * 
     * @param capitalPaidUp
     */
    public void setCapitalPaidUp(java.math.BigDecimal capitalPaidUp) {
        this.capitalPaidUp = capitalPaidUp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateSupplierInfoRqApplArrayAppl)) return false;
        UpdateSupplierInfoRqApplArrayAppl other = (UpdateSupplierInfoRqApplArrayAppl) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.applNo==null && other.getApplNo()==null) || 
             (this.applNo!=null &&
              this.applNo.equals(other.getApplNo()))) &&
            ((this.applStatus==null && other.getApplStatus()==null) || 
             (this.applStatus!=null &&
              this.applStatus.equals(other.getApplStatus()))) &&
            ((this.appType==null && other.getAppType()==null) || 
             (this.appType!=null &&
              this.appType.equals(other.getAppType()))) &&
            ((this.mofNo==null && other.getMofNo()==null) || 
             (this.mofNo!=null &&
              this.mofNo.equals(other.getMofNo()))) &&
            ((this.expiryDate==null && other.getExpiryDate()==null) || 
             (this.expiryDate!=null &&
              this.expiryDate.equals(other.getExpiryDate()))) &&
            ((this.regNo==null && other.getRegNo()==null) || 
             (this.regNo!=null &&
              this.regNo.equals(other.getRegNo()))) &&
            ((this.accountStatus==null && other.getAccountStatus()==null) || 
             (this.accountStatus!=null &&
              this.accountStatus.equals(other.getAccountStatus()))) &&
            ((this.capitalPaidUp==null && other.getCapitalPaidUp()==null) || 
             (this.capitalPaidUp!=null &&
              this.capitalPaidUp.equals(other.getCapitalPaidUp())));
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
        if (getApplNo() != null) {
            _hashCode += getApplNo().hashCode();
        }
        if (getApplStatus() != null) {
            _hashCode += getApplStatus().hashCode();
        }
        if (getAppType() != null) {
            _hashCode += getAppType().hashCode();
        }
        if (getMofNo() != null) {
            _hashCode += getMofNo().hashCode();
        }
        if (getExpiryDate() != null) {
            _hashCode += getExpiryDate().hashCode();
        }
        if (getRegNo() != null) {
            _hashCode += getRegNo().hashCode();
        }
        if (getAccountStatus() != null) {
            _hashCode += getAccountStatus().hashCode();
        }
        if (getCapitalPaidUp() != null) {
            _hashCode += getCapitalPaidUp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateSupplierInfoRqApplArrayAppl.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", ">>>UpdateSupplierInfoRq>ApplArray>Appl"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "ApplNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "ApplStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("appType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "AppType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mofNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "MofNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiryDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "ExpiryDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "RegNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "AccountStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capitalPaidUp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "CapitalPaidUp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
