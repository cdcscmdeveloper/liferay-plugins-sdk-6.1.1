/**
 * UpdateSupplierUserInfoRq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.userinfo;

public class UpdateSupplierUserInfoRq  implements java.io.Serializable {
    private java.lang.String userName;

    private java.lang.String identityNo;

    private java.math.BigDecimal identityType;

    private java.lang.String nationality;

    private java.lang.String recordStatus;

    private java.lang.String email;

    private java.math.BigDecimal mobileNo;

    private java.lang.String position;

    private java.math.BigDecimal accessStatus;

    private my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqBelongsToArrayBelongsTo[] belongsToArray;

    private my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqSoftCertArraySoftCert[] softCertArray;

    public UpdateSupplierUserInfoRq() {
    }

    public UpdateSupplierUserInfoRq(
           java.lang.String userName,
           java.lang.String identityNo,
           java.math.BigDecimal identityType,
           java.lang.String nationality,
           java.lang.String recordStatus,
           java.lang.String email,
           java.math.BigDecimal mobileNo,
           java.lang.String position,
           java.math.BigDecimal accessStatus,
           my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqBelongsToArrayBelongsTo[] belongsToArray,
           my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqSoftCertArraySoftCert[] softCertArray) {
           this.userName = userName;
           this.identityNo = identityNo;
           this.identityType = identityType;
           this.nationality = nationality;
           this.recordStatus = recordStatus;
           this.email = email;
           this.mobileNo = mobileNo;
           this.position = position;
           this.accessStatus = accessStatus;
           this.belongsToArray = belongsToArray;
           this.softCertArray = softCertArray;
    }


    /**
     * Gets the userName value for this UpdateSupplierUserInfoRq.
     * 
     * @return userName
     */
    public java.lang.String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this UpdateSupplierUserInfoRq.
     * 
     * @param userName
     */
    public void setUserName(java.lang.String userName) {
        this.userName = userName;
    }


    /**
     * Gets the identityNo value for this UpdateSupplierUserInfoRq.
     * 
     * @return identityNo
     */
    public java.lang.String getIdentityNo() {
        return identityNo;
    }


    /**
     * Sets the identityNo value for this UpdateSupplierUserInfoRq.
     * 
     * @param identityNo
     */
    public void setIdentityNo(java.lang.String identityNo) {
        this.identityNo = identityNo;
    }


    /**
     * Gets the identityType value for this UpdateSupplierUserInfoRq.
     * 
     * @return identityType
     */
    public java.math.BigDecimal getIdentityType() {
        return identityType;
    }


    /**
     * Sets the identityType value for this UpdateSupplierUserInfoRq.
     * 
     * @param identityType
     */
    public void setIdentityType(java.math.BigDecimal identityType) {
        this.identityType = identityType;
    }


    /**
     * Gets the nationality value for this UpdateSupplierUserInfoRq.
     * 
     * @return nationality
     */
    public java.lang.String getNationality() {
        return nationality;
    }


    /**
     * Sets the nationality value for this UpdateSupplierUserInfoRq.
     * 
     * @param nationality
     */
    public void setNationality(java.lang.String nationality) {
        this.nationality = nationality;
    }


    /**
     * Gets the recordStatus value for this UpdateSupplierUserInfoRq.
     * 
     * @return recordStatus
     */
    public java.lang.String getRecordStatus() {
        return recordStatus;
    }


    /**
     * Sets the recordStatus value for this UpdateSupplierUserInfoRq.
     * 
     * @param recordStatus
     */
    public void setRecordStatus(java.lang.String recordStatus) {
        this.recordStatus = recordStatus;
    }


    /**
     * Gets the email value for this UpdateSupplierUserInfoRq.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this UpdateSupplierUserInfoRq.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the mobileNo value for this UpdateSupplierUserInfoRq.
     * 
     * @return mobileNo
     */
    public java.math.BigDecimal getMobileNo() {
        return mobileNo;
    }


    /**
     * Sets the mobileNo value for this UpdateSupplierUserInfoRq.
     * 
     * @param mobileNo
     */
    public void setMobileNo(java.math.BigDecimal mobileNo) {
        this.mobileNo = mobileNo;
    }


    /**
     * Gets the position value for this UpdateSupplierUserInfoRq.
     * 
     * @return position
     */
    public java.lang.String getPosition() {
        return position;
    }


    /**
     * Sets the position value for this UpdateSupplierUserInfoRq.
     * 
     * @param position
     */
    public void setPosition(java.lang.String position) {
        this.position = position;
    }


    /**
     * Gets the accessStatus value for this UpdateSupplierUserInfoRq.
     * 
     * @return accessStatus
     */
    public java.math.BigDecimal getAccessStatus() {
        return accessStatus;
    }


    /**
     * Sets the accessStatus value for this UpdateSupplierUserInfoRq.
     * 
     * @param accessStatus
     */
    public void setAccessStatus(java.math.BigDecimal accessStatus) {
        this.accessStatus = accessStatus;
    }


    /**
     * Gets the belongsToArray value for this UpdateSupplierUserInfoRq.
     * 
     * @return belongsToArray
     */
    public my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqBelongsToArrayBelongsTo[] getBelongsToArray() {
        return belongsToArray;
    }


    /**
     * Sets the belongsToArray value for this UpdateSupplierUserInfoRq.
     * 
     * @param belongsToArray
     */
    public void setBelongsToArray(my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqBelongsToArrayBelongsTo[] belongsToArray) {
        this.belongsToArray = belongsToArray;
    }


    /**
     * Gets the softCertArray value for this UpdateSupplierUserInfoRq.
     * 
     * @return softCertArray
     */
    public my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqSoftCertArraySoftCert[] getSoftCertArray() {
        return softCertArray;
    }


    /**
     * Sets the softCertArray value for this UpdateSupplierUserInfoRq.
     * 
     * @param softCertArray
     */
    public void setSoftCertArray(my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqSoftCertArraySoftCert[] softCertArray) {
        this.softCertArray = softCertArray;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateSupplierUserInfoRq)) return false;
        UpdateSupplierUserInfoRq other = (UpdateSupplierUserInfoRq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.userName==null && other.getUserName()==null) || 
             (this.userName!=null &&
              this.userName.equals(other.getUserName()))) &&
            ((this.identityNo==null && other.getIdentityNo()==null) || 
             (this.identityNo!=null &&
              this.identityNo.equals(other.getIdentityNo()))) &&
            ((this.identityType==null && other.getIdentityType()==null) || 
             (this.identityType!=null &&
              this.identityType.equals(other.getIdentityType()))) &&
            ((this.nationality==null && other.getNationality()==null) || 
             (this.nationality!=null &&
              this.nationality.equals(other.getNationality()))) &&
            ((this.recordStatus==null && other.getRecordStatus()==null) || 
             (this.recordStatus!=null &&
              this.recordStatus.equals(other.getRecordStatus()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.mobileNo==null && other.getMobileNo()==null) || 
             (this.mobileNo!=null &&
              this.mobileNo.equals(other.getMobileNo()))) &&
            ((this.position==null && other.getPosition()==null) || 
             (this.position!=null &&
              this.position.equals(other.getPosition()))) &&
            ((this.accessStatus==null && other.getAccessStatus()==null) || 
             (this.accessStatus!=null &&
              this.accessStatus.equals(other.getAccessStatus()))) &&
            ((this.belongsToArray==null && other.getBelongsToArray()==null) || 
             (this.belongsToArray!=null &&
              java.util.Arrays.equals(this.belongsToArray, other.getBelongsToArray()))) &&
            ((this.softCertArray==null && other.getSoftCertArray()==null) || 
             (this.softCertArray!=null &&
              java.util.Arrays.equals(this.softCertArray, other.getSoftCertArray())));
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
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        if (getIdentityNo() != null) {
            _hashCode += getIdentityNo().hashCode();
        }
        if (getIdentityType() != null) {
            _hashCode += getIdentityType().hashCode();
        }
        if (getNationality() != null) {
            _hashCode += getNationality().hashCode();
        }
        if (getRecordStatus() != null) {
            _hashCode += getRecordStatus().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getMobileNo() != null) {
            _hashCode += getMobileNo().hashCode();
        }
        if (getPosition() != null) {
            _hashCode += getPosition().hashCode();
        }
        if (getAccessStatus() != null) {
            _hashCode += getAccessStatus().hashCode();
        }
        if (getBelongsToArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBelongsToArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBelongsToArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSoftCertArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSoftCertArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSoftCertArray(), i);
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
        new org.apache.axis.description.TypeDesc(UpdateSupplierUserInfoRq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", ">UpdateSupplierUserInfoRq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "UserName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identityNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "IdentityNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identityType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "IdentityType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nationality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "Nationality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "RecordStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "Email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobileNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "MobileNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("position");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "Position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accessStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "AccessStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("belongsToArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "BelongsToArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", ">>>UpdateSupplierUserInfoRq>BelongsToArray>BelongsTo"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "BelongsTo"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("softCertArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "SoftCertArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", ">>>UpdateSupplierUserInfoRq>SoftCertArray>SoftCert"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "SoftCert"));
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
