/**
 * UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.com.integration.crm.governmentuserinfo;

public class UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole  implements java.io.Serializable {
    private java.lang.String roleName;

    private java.math.BigDecimal approvalLimit;

    public UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole() {
    }

    public UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole(
           java.lang.String roleName,
           java.math.BigDecimal approvalLimit) {
           this.roleName = roleName;
           this.approvalLimit = approvalLimit;
    }


    /**
     * Gets the roleName value for this UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole.
     * 
     * @return roleName
     */
    public java.lang.String getRoleName() {
        return roleName;
    }


    /**
     * Sets the roleName value for this UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole.
     * 
     * @param roleName
     */
    public void setRoleName(java.lang.String roleName) {
        this.roleName = roleName;
    }


    /**
     * Gets the approvalLimit value for this UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole.
     * 
     * @return approvalLimit
     */
    public java.math.BigDecimal getApprovalLimit() {
        return approvalLimit;
    }


    /**
     * Sets the approvalLimit value for this UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole.
     * 
     * @param approvalLimit
     */
    public void setApprovalLimit(java.math.BigDecimal approvalLimit) {
        this.approvalLimit = approvalLimit;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole)) return false;
        UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole other = (UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.roleName==null && other.getRoleName()==null) || 
             (this.roleName!=null &&
              this.roleName.equals(other.getRoleName()))) &&
            ((this.approvalLimit==null && other.getApprovalLimit()==null) || 
             (this.approvalLimit!=null &&
              this.approvalLimit.equals(other.getApprovalLimit())));
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
        if (getRoleName() != null) {
            _hashCode += getRoleName().hashCode();
        }
        if (getApprovalLimit() != null) {
            _hashCode += getApprovalLimit().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateGovernmentUserInfoRqPTJRoleArrayPTJRole.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", ">>>UpdateGovernmentUserInfoRq>PTJRoleArray>PTJRole"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roleName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "RoleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approvalLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "ApprovalLimit"));
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
