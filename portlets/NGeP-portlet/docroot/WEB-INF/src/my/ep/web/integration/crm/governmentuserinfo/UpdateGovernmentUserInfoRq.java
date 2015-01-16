/**
 * UpdateGovernmentUserInfoRq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.governmentuserinfo;

public class UpdateGovernmentUserInfoRq  implements java.io.Serializable {
    private java.math.BigDecimal userID;

    private java.lang.String userName;

    private java.lang.String identityNo;

    private java.math.BigDecimal identityType;

    private java.math.BigDecimal mobileNo;

    private java.lang.String email;

    private java.lang.String nationality;

    private java.lang.String position;

    private java.math.BigDecimal recordStatus;

    private java.lang.String address1;

    private java.lang.String address2;

    private java.lang.String address3;

    private java.lang.String zipCode;

    private java.lang.String district;

    private java.lang.String city;

    private java.lang.String stateName;

    private java.lang.String countryName;

    private my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryArrayMinistry[] ministryArray;

    private my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryRoleArrayMinistryRole[] ministryRoleArray;

    private my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJArrayPTJ[] PTJArray;

    private my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole[] PTJRoleArray;

    public UpdateGovernmentUserInfoRq() {
    }

    public UpdateGovernmentUserInfoRq(
           java.math.BigDecimal userID,
           java.lang.String userName,
           java.lang.String identityNo,
           java.math.BigDecimal identityType,
           java.math.BigDecimal mobileNo,
           java.lang.String email,
           java.lang.String nationality,
           java.lang.String position,
           java.math.BigDecimal recordStatus,
           java.lang.String address1,
           java.lang.String address2,
           java.lang.String address3,
           java.lang.String zipCode,
           java.lang.String district,
           java.lang.String city,
           java.lang.String stateName,
           java.lang.String countryName,
           my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryArrayMinistry[] ministryArray,
           my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryRoleArrayMinistryRole[] ministryRoleArray,
           my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJArrayPTJ[] PTJArray,
           my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole[] PTJRoleArray) {
           this.userID = userID;
           this.userName = userName;
           this.identityNo = identityNo;
           this.identityType = identityType;
           this.mobileNo = mobileNo;
           this.email = email;
           this.nationality = nationality;
           this.position = position;
           this.recordStatus = recordStatus;
           this.address1 = address1;
           this.address2 = address2;
           this.address3 = address3;
           this.zipCode = zipCode;
           this.district = district;
           this.city = city;
           this.stateName = stateName;
           this.countryName = countryName;
           this.ministryArray = ministryArray;
           this.ministryRoleArray = ministryRoleArray;
           this.PTJArray = PTJArray;
           this.PTJRoleArray = PTJRoleArray;
    }


    /**
     * Gets the userID value for this UpdateGovernmentUserInfoRq.
     * 
     * @return userID
     */
    public java.math.BigDecimal getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this UpdateGovernmentUserInfoRq.
     * 
     * @param userID
     */
    public void setUserID(java.math.BigDecimal userID) {
        this.userID = userID;
    }


    /**
     * Gets the userName value for this UpdateGovernmentUserInfoRq.
     * 
     * @return userName
     */
    public java.lang.String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this UpdateGovernmentUserInfoRq.
     * 
     * @param userName
     */
    public void setUserName(java.lang.String userName) {
        this.userName = userName;
    }


    /**
     * Gets the identityNo value for this UpdateGovernmentUserInfoRq.
     * 
     * @return identityNo
     */
    public java.lang.String getIdentityNo() {
        return identityNo;
    }


    /**
     * Sets the identityNo value for this UpdateGovernmentUserInfoRq.
     * 
     * @param identityNo
     */
    public void setIdentityNo(java.lang.String identityNo) {
        this.identityNo = identityNo;
    }


    /**
     * Gets the identityType value for this UpdateGovernmentUserInfoRq.
     * 
     * @return identityType
     */
    public java.math.BigDecimal getIdentityType() {
        return identityType;
    }


    /**
     * Sets the identityType value for this UpdateGovernmentUserInfoRq.
     * 
     * @param identityType
     */
    public void setIdentityType(java.math.BigDecimal identityType) {
        this.identityType = identityType;
    }


    /**
     * Gets the mobileNo value for this UpdateGovernmentUserInfoRq.
     * 
     * @return mobileNo
     */
    public java.math.BigDecimal getMobileNo() {
        return mobileNo;
    }


    /**
     * Sets the mobileNo value for this UpdateGovernmentUserInfoRq.
     * 
     * @param mobileNo
     */
    public void setMobileNo(java.math.BigDecimal mobileNo) {
        this.mobileNo = mobileNo;
    }


    /**
     * Gets the email value for this UpdateGovernmentUserInfoRq.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this UpdateGovernmentUserInfoRq.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the nationality value for this UpdateGovernmentUserInfoRq.
     * 
     * @return nationality
     */
    public java.lang.String getNationality() {
        return nationality;
    }


    /**
     * Sets the nationality value for this UpdateGovernmentUserInfoRq.
     * 
     * @param nationality
     */
    public void setNationality(java.lang.String nationality) {
        this.nationality = nationality;
    }


    /**
     * Gets the position value for this UpdateGovernmentUserInfoRq.
     * 
     * @return position
     */
    public java.lang.String getPosition() {
        return position;
    }


    /**
     * Sets the position value for this UpdateGovernmentUserInfoRq.
     * 
     * @param position
     */
    public void setPosition(java.lang.String position) {
        this.position = position;
    }


    /**
     * Gets the recordStatus value for this UpdateGovernmentUserInfoRq.
     * 
     * @return recordStatus
     */
    public java.math.BigDecimal getRecordStatus() {
        return recordStatus;
    }


    /**
     * Sets the recordStatus value for this UpdateGovernmentUserInfoRq.
     * 
     * @param recordStatus
     */
    public void setRecordStatus(java.math.BigDecimal recordStatus) {
        this.recordStatus = recordStatus;
    }


    /**
     * Gets the address1 value for this UpdateGovernmentUserInfoRq.
     * 
     * @return address1
     */
    public java.lang.String getAddress1() {
        return address1;
    }


    /**
     * Sets the address1 value for this UpdateGovernmentUserInfoRq.
     * 
     * @param address1
     */
    public void setAddress1(java.lang.String address1) {
        this.address1 = address1;
    }


    /**
     * Gets the address2 value for this UpdateGovernmentUserInfoRq.
     * 
     * @return address2
     */
    public java.lang.String getAddress2() {
        return address2;
    }


    /**
     * Sets the address2 value for this UpdateGovernmentUserInfoRq.
     * 
     * @param address2
     */
    public void setAddress2(java.lang.String address2) {
        this.address2 = address2;
    }


    /**
     * Gets the address3 value for this UpdateGovernmentUserInfoRq.
     * 
     * @return address3
     */
    public java.lang.String getAddress3() {
        return address3;
    }


    /**
     * Sets the address3 value for this UpdateGovernmentUserInfoRq.
     * 
     * @param address3
     */
    public void setAddress3(java.lang.String address3) {
        this.address3 = address3;
    }


    /**
     * Gets the zipCode value for this UpdateGovernmentUserInfoRq.
     * 
     * @return zipCode
     */
    public java.lang.String getZipCode() {
        return zipCode;
    }


    /**
     * Sets the zipCode value for this UpdateGovernmentUserInfoRq.
     * 
     * @param zipCode
     */
    public void setZipCode(java.lang.String zipCode) {
        this.zipCode = zipCode;
    }


    /**
     * Gets the district value for this UpdateGovernmentUserInfoRq.
     * 
     * @return district
     */
    public java.lang.String getDistrict() {
        return district;
    }


    /**
     * Sets the district value for this UpdateGovernmentUserInfoRq.
     * 
     * @param district
     */
    public void setDistrict(java.lang.String district) {
        this.district = district;
    }


    /**
     * Gets the city value for this UpdateGovernmentUserInfoRq.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this UpdateGovernmentUserInfoRq.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the stateName value for this UpdateGovernmentUserInfoRq.
     * 
     * @return stateName
     */
    public java.lang.String getStateName() {
        return stateName;
    }


    /**
     * Sets the stateName value for this UpdateGovernmentUserInfoRq.
     * 
     * @param stateName
     */
    public void setStateName(java.lang.String stateName) {
        this.stateName = stateName;
    }


    /**
     * Gets the countryName value for this UpdateGovernmentUserInfoRq.
     * 
     * @return countryName
     */
    public java.lang.String getCountryName() {
        return countryName;
    }


    /**
     * Sets the countryName value for this UpdateGovernmentUserInfoRq.
     * 
     * @param countryName
     */
    public void setCountryName(java.lang.String countryName) {
        this.countryName = countryName;
    }


    /**
     * Gets the ministryArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @return ministryArray
     */
    public my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryArrayMinistry[] getMinistryArray() {
        return ministryArray;
    }


    /**
     * Sets the ministryArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @param ministryArray
     */
    public void setMinistryArray(my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryArrayMinistry[] ministryArray) {
        this.ministryArray = ministryArray;
    }


    /**
     * Gets the ministryRoleArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @return ministryRoleArray
     */
    public my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryRoleArrayMinistryRole[] getMinistryRoleArray() {
        return ministryRoleArray;
    }


    /**
     * Sets the ministryRoleArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @param ministryRoleArray
     */
    public void setMinistryRoleArray(my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqMinistryRoleArrayMinistryRole[] ministryRoleArray) {
        this.ministryRoleArray = ministryRoleArray;
    }


    /**
     * Gets the PTJArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @return PTJArray
     */
    public my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJArrayPTJ[] getPTJArray() {
        return PTJArray;
    }


    /**
     * Sets the PTJArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @param PTJArray
     */
    public void setPTJArray(my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJArrayPTJ[] PTJArray) {
        this.PTJArray = PTJArray;
    }


    /**
     * Gets the PTJRoleArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @return PTJRoleArray
     */
    public my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole[] getPTJRoleArray() {
        return PTJRoleArray;
    }


    /**
     * Sets the PTJRoleArray value for this UpdateGovernmentUserInfoRq.
     * 
     * @param PTJRoleArray
     */
    public void setPTJRoleArray(my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole[] PTJRoleArray) {
        this.PTJRoleArray = PTJRoleArray;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateGovernmentUserInfoRq)) return false;
        UpdateGovernmentUserInfoRq other = (UpdateGovernmentUserInfoRq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.userID==null && other.getUserID()==null) || 
             (this.userID!=null &&
              this.userID.equals(other.getUserID()))) &&
            ((this.userName==null && other.getUserName()==null) || 
             (this.userName!=null &&
              this.userName.equals(other.getUserName()))) &&
            ((this.identityNo==null && other.getIdentityNo()==null) || 
             (this.identityNo!=null &&
              this.identityNo.equals(other.getIdentityNo()))) &&
            ((this.identityType==null && other.getIdentityType()==null) || 
             (this.identityType!=null &&
              this.identityType.equals(other.getIdentityType()))) &&
            ((this.mobileNo==null && other.getMobileNo()==null) || 
             (this.mobileNo!=null &&
              this.mobileNo.equals(other.getMobileNo()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.nationality==null && other.getNationality()==null) || 
             (this.nationality!=null &&
              this.nationality.equals(other.getNationality()))) &&
            ((this.position==null && other.getPosition()==null) || 
             (this.position!=null &&
              this.position.equals(other.getPosition()))) &&
            ((this.recordStatus==null && other.getRecordStatus()==null) || 
             (this.recordStatus!=null &&
              this.recordStatus.equals(other.getRecordStatus()))) &&
            ((this.address1==null && other.getAddress1()==null) || 
             (this.address1!=null &&
              this.address1.equals(other.getAddress1()))) &&
            ((this.address2==null && other.getAddress2()==null) || 
             (this.address2!=null &&
              this.address2.equals(other.getAddress2()))) &&
            ((this.address3==null && other.getAddress3()==null) || 
             (this.address3!=null &&
              this.address3.equals(other.getAddress3()))) &&
            ((this.zipCode==null && other.getZipCode()==null) || 
             (this.zipCode!=null &&
              this.zipCode.equals(other.getZipCode()))) &&
            ((this.district==null && other.getDistrict()==null) || 
             (this.district!=null &&
              this.district.equals(other.getDistrict()))) &&
            ((this.city==null && other.getCity()==null) || 
             (this.city!=null &&
              this.city.equals(other.getCity()))) &&
            ((this.stateName==null && other.getStateName()==null) || 
             (this.stateName!=null &&
              this.stateName.equals(other.getStateName()))) &&
            ((this.countryName==null && other.getCountryName()==null) || 
             (this.countryName!=null &&
              this.countryName.equals(other.getCountryName()))) &&
            ((this.ministryArray==null && other.getMinistryArray()==null) || 
             (this.ministryArray!=null &&
              java.util.Arrays.equals(this.ministryArray, other.getMinistryArray()))) &&
            ((this.ministryRoleArray==null && other.getMinistryRoleArray()==null) || 
             (this.ministryRoleArray!=null &&
              java.util.Arrays.equals(this.ministryRoleArray, other.getMinistryRoleArray()))) &&
            ((this.PTJArray==null && other.getPTJArray()==null) || 
             (this.PTJArray!=null &&
              java.util.Arrays.equals(this.PTJArray, other.getPTJArray()))) &&
            ((this.PTJRoleArray==null && other.getPTJRoleArray()==null) || 
             (this.PTJRoleArray!=null &&
              java.util.Arrays.equals(this.PTJRoleArray, other.getPTJRoleArray())));
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
        if (getUserID() != null) {
            _hashCode += getUserID().hashCode();
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        if (getIdentityNo() != null) {
            _hashCode += getIdentityNo().hashCode();
        }
        if (getIdentityType() != null) {
            _hashCode += getIdentityType().hashCode();
        }
        if (getMobileNo() != null) {
            _hashCode += getMobileNo().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getNationality() != null) {
            _hashCode += getNationality().hashCode();
        }
        if (getPosition() != null) {
            _hashCode += getPosition().hashCode();
        }
        if (getRecordStatus() != null) {
            _hashCode += getRecordStatus().hashCode();
        }
        if (getAddress1() != null) {
            _hashCode += getAddress1().hashCode();
        }
        if (getAddress2() != null) {
            _hashCode += getAddress2().hashCode();
        }
        if (getAddress3() != null) {
            _hashCode += getAddress3().hashCode();
        }
        if (getZipCode() != null) {
            _hashCode += getZipCode().hashCode();
        }
        if (getDistrict() != null) {
            _hashCode += getDistrict().hashCode();
        }
        if (getCity() != null) {
            _hashCode += getCity().hashCode();
        }
        if (getStateName() != null) {
            _hashCode += getStateName().hashCode();
        }
        if (getCountryName() != null) {
            _hashCode += getCountryName().hashCode();
        }
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
        if (getMinistryRoleArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMinistryRoleArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMinistryRoleArray(), i);
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
        if (getPTJRoleArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPTJRoleArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPTJRoleArray(), i);
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
        new org.apache.axis.description.TypeDesc(UpdateGovernmentUserInfoRq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", ">UpdateGovernmentUserInfoRq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "UserID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "UserName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identityNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "IdentityNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identityType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "IdentityType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobileNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "MobileNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "Email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nationality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "Nationality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("position");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "Position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "RecordStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "Address1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "Address2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "Address3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zipCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "ZipCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("district");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "District"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("city");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "City"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stateName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "StateName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "CountryName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ministryArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "MinistryArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", ">>>UpdateGovernmentUserInfoRq>MinistryArray>Ministry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "Ministry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ministryRoleArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "MinistryRoleArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", ">>>UpdateGovernmentUserInfoRq>MinistryRoleArray>MinistryRole"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "MinistryRole"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PTJArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "PTJArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", ">>>UpdateGovernmentUserInfoRq>PTJArray>PTJ"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "PTJ"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PTJRoleArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "PTJRoleArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", ">>>UpdateGovernmentUserInfoRq>PTJRoleArray>PTJRole"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "PTJRole"));
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
