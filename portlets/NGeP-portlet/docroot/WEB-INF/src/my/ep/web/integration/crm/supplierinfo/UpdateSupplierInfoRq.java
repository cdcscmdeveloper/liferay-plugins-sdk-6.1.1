/**
 * UpdateSupplierInfoRq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.supplierinfo;

public class UpdateSupplierInfoRq  implements java.io.Serializable {
    private java.lang.String companyName;

    private java.math.BigDecimal supplierID;

    private my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBranchArrayBranch[] branchArray;

    private my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqApplArrayAppl[] applArray;

    private my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqMainContactArrayMainContact[] mainContactArray;

    private my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBidangArrayBidang[] bidangArray;

    public UpdateSupplierInfoRq() {
    }

    public UpdateSupplierInfoRq(
           java.lang.String companyName,
           java.math.BigDecimal supplierID,
           my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBranchArrayBranch[] branchArray,
           my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqApplArrayAppl[] applArray,
           my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqMainContactArrayMainContact[] mainContactArray,
           my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBidangArrayBidang[] bidangArray) {
           this.companyName = companyName;
           this.supplierID = supplierID;
           this.branchArray = branchArray;
           this.applArray = applArray;
           this.mainContactArray = mainContactArray;
           this.bidangArray = bidangArray;
    }


    /**
     * Gets the companyName value for this UpdateSupplierInfoRq.
     * 
     * @return companyName
     */
    public java.lang.String getCompanyName() {
        return companyName;
    }


    /**
     * Sets the companyName value for this UpdateSupplierInfoRq.
     * 
     * @param companyName
     */
    public void setCompanyName(java.lang.String companyName) {
        this.companyName = companyName;
    }


    /**
     * Gets the supplierID value for this UpdateSupplierInfoRq.
     * 
     * @return supplierID
     */
    public java.math.BigDecimal getSupplierID() {
        return supplierID;
    }


    /**
     * Sets the supplierID value for this UpdateSupplierInfoRq.
     * 
     * @param supplierID
     */
    public void setSupplierID(java.math.BigDecimal supplierID) {
        this.supplierID = supplierID;
    }


    /**
     * Gets the branchArray value for this UpdateSupplierInfoRq.
     * 
     * @return branchArray
     */
    public my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBranchArrayBranch[] getBranchArray() {
        return branchArray;
    }


    /**
     * Sets the branchArray value for this UpdateSupplierInfoRq.
     * 
     * @param branchArray
     */
    public void setBranchArray(my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBranchArrayBranch[] branchArray) {
        this.branchArray = branchArray;
    }


    /**
     * Gets the applArray value for this UpdateSupplierInfoRq.
     * 
     * @return applArray
     */
    public my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqApplArrayAppl[] getApplArray() {
        return applArray;
    }


    /**
     * Sets the applArray value for this UpdateSupplierInfoRq.
     * 
     * @param applArray
     */
    public void setApplArray(my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqApplArrayAppl[] applArray) {
        this.applArray = applArray;
    }


    /**
     * Gets the mainContactArray value for this UpdateSupplierInfoRq.
     * 
     * @return mainContactArray
     */
    public my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqMainContactArrayMainContact[] getMainContactArray() {
        return mainContactArray;
    }


    /**
     * Sets the mainContactArray value for this UpdateSupplierInfoRq.
     * 
     * @param mainContactArray
     */
    public void setMainContactArray(my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqMainContactArrayMainContact[] mainContactArray) {
        this.mainContactArray = mainContactArray;
    }


    /**
     * Gets the bidangArray value for this UpdateSupplierInfoRq.
     * 
     * @return bidangArray
     */
    public my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBidangArrayBidang[] getBidangArray() {
        return bidangArray;
    }


    /**
     * Sets the bidangArray value for this UpdateSupplierInfoRq.
     * 
     * @param bidangArray
     */
    public void setBidangArray(my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBidangArrayBidang[] bidangArray) {
        this.bidangArray = bidangArray;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateSupplierInfoRq)) return false;
        UpdateSupplierInfoRq other = (UpdateSupplierInfoRq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.companyName==null && other.getCompanyName()==null) || 
             (this.companyName!=null &&
              this.companyName.equals(other.getCompanyName()))) &&
            ((this.supplierID==null && other.getSupplierID()==null) || 
             (this.supplierID!=null &&
              this.supplierID.equals(other.getSupplierID()))) &&
            ((this.branchArray==null && other.getBranchArray()==null) || 
             (this.branchArray!=null &&
              java.util.Arrays.equals(this.branchArray, other.getBranchArray()))) &&
            ((this.applArray==null && other.getApplArray()==null) || 
             (this.applArray!=null &&
              java.util.Arrays.equals(this.applArray, other.getApplArray()))) &&
            ((this.mainContactArray==null && other.getMainContactArray()==null) || 
             (this.mainContactArray!=null &&
              java.util.Arrays.equals(this.mainContactArray, other.getMainContactArray()))) &&
            ((this.bidangArray==null && other.getBidangArray()==null) || 
             (this.bidangArray!=null &&
              java.util.Arrays.equals(this.bidangArray, other.getBidangArray())));
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
        if (getCompanyName() != null) {
            _hashCode += getCompanyName().hashCode();
        }
        if (getSupplierID() != null) {
            _hashCode += getSupplierID().hashCode();
        }
        if (getBranchArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBranchArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBranchArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getApplArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getApplArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getApplArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMainContactArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMainContactArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMainContactArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getBidangArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBidangArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBidangArray(), i);
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
        new org.apache.axis.description.TypeDesc(UpdateSupplierInfoRq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", ">UpdateSupplierInfoRq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "CompanyName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supplierID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "SupplierID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("branchArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "BranchArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", ">>>UpdateSupplierInfoRq>BranchArray>Branch"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "Branch"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "ApplArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", ">>>UpdateSupplierInfoRq>ApplArray>Appl"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "Appl"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mainContactArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "MainContactArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", ">>>UpdateSupplierInfoRq>MainContactArray>MainContact"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "MainContact"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bidangArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "BidangArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", ">>>UpdateSupplierInfoRq>BidangArray>Bidang"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "Bidang"));
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
