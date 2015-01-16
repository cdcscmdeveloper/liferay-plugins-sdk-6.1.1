/**
 * UpdateSupplierUserInfoRqBelongsToArrayBelongsTo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.userinfo;

public class UpdateSupplierUserInfoRqBelongsToArrayBelongsTo  implements java.io.Serializable {
    private java.math.BigDecimal branchID;

    private java.math.BigDecimal supplierID;

    public UpdateSupplierUserInfoRqBelongsToArrayBelongsTo() {
    }

    public UpdateSupplierUserInfoRqBelongsToArrayBelongsTo(
           java.math.BigDecimal branchID,
           java.math.BigDecimal supplierID) {
           this.branchID = branchID;
           this.supplierID = supplierID;
    }


    /**
     * Gets the branchID value for this UpdateSupplierUserInfoRqBelongsToArrayBelongsTo.
     * 
     * @return branchID
     */
    public java.math.BigDecimal getBranchID() {
        return branchID;
    }


    /**
     * Sets the branchID value for this UpdateSupplierUserInfoRqBelongsToArrayBelongsTo.
     * 
     * @param branchID
     */
    public void setBranchID(java.math.BigDecimal branchID) {
        this.branchID = branchID;
    }


    /**
     * Gets the supplierID value for this UpdateSupplierUserInfoRqBelongsToArrayBelongsTo.
     * 
     * @return supplierID
     */
    public java.math.BigDecimal getSupplierID() {
        return supplierID;
    }


    /**
     * Sets the supplierID value for this UpdateSupplierUserInfoRqBelongsToArrayBelongsTo.
     * 
     * @param supplierID
     */
    public void setSupplierID(java.math.BigDecimal supplierID) {
        this.supplierID = supplierID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateSupplierUserInfoRqBelongsToArrayBelongsTo)) return false;
        UpdateSupplierUserInfoRqBelongsToArrayBelongsTo other = (UpdateSupplierUserInfoRqBelongsToArrayBelongsTo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.branchID==null && other.getBranchID()==null) || 
             (this.branchID!=null &&
              this.branchID.equals(other.getBranchID()))) &&
            ((this.supplierID==null && other.getSupplierID()==null) || 
             (this.supplierID!=null &&
              this.supplierID.equals(other.getSupplierID())));
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
        if (getBranchID() != null) {
            _hashCode += getBranchID().hashCode();
        }
        if (getSupplierID() != null) {
            _hashCode += getSupplierID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateSupplierUserInfoRqBelongsToArrayBelongsTo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", ">>>UpdateSupplierUserInfoRq>BelongsToArray>BelongsTo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("branchID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "BranchID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supplierID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierUserInfo", "SupplierID"));
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
