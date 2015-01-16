/**
 * UpdateSupplierInfoRqBidangArrayBidang.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.supplierinfo;

public class UpdateSupplierInfoRqBidangArrayBidang  implements java.io.Serializable {
    private java.lang.String kodBidangCategoryL1;

    private java.lang.String kodBidangCategoryL2;

    private java.lang.String kodBidangCategoryL3;

    private java.lang.String bidangDescriptionCategoryL1;

    private java.lang.String bidangDescriptionCategoryL2;

    private java.lang.String bidangDescriptionCategoryL3;

    public UpdateSupplierInfoRqBidangArrayBidang() {
    }

    public UpdateSupplierInfoRqBidangArrayBidang(
           java.lang.String kodBidangCategoryL1,
           java.lang.String kodBidangCategoryL2,
           java.lang.String kodBidangCategoryL3,
           java.lang.String bidangDescriptionCategoryL1,
           java.lang.String bidangDescriptionCategoryL2,
           java.lang.String bidangDescriptionCategoryL3) {
           this.kodBidangCategoryL1 = kodBidangCategoryL1;
           this.kodBidangCategoryL2 = kodBidangCategoryL2;
           this.kodBidangCategoryL3 = kodBidangCategoryL3;
           this.bidangDescriptionCategoryL1 = bidangDescriptionCategoryL1;
           this.bidangDescriptionCategoryL2 = bidangDescriptionCategoryL2;
           this.bidangDescriptionCategoryL3 = bidangDescriptionCategoryL3;
    }


    /**
     * Gets the kodBidangCategoryL1 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @return kodBidangCategoryL1
     */
    public java.lang.String getKodBidangCategoryL1() {
        return kodBidangCategoryL1;
    }


    /**
     * Sets the kodBidangCategoryL1 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @param kodBidangCategoryL1
     */
    public void setKodBidangCategoryL1(java.lang.String kodBidangCategoryL1) {
        this.kodBidangCategoryL1 = kodBidangCategoryL1;
    }


    /**
     * Gets the kodBidangCategoryL2 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @return kodBidangCategoryL2
     */
    public java.lang.String getKodBidangCategoryL2() {
        return kodBidangCategoryL2;
    }


    /**
     * Sets the kodBidangCategoryL2 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @param kodBidangCategoryL2
     */
    public void setKodBidangCategoryL2(java.lang.String kodBidangCategoryL2) {
        this.kodBidangCategoryL2 = kodBidangCategoryL2;
    }


    /**
     * Gets the kodBidangCategoryL3 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @return kodBidangCategoryL3
     */
    public java.lang.String getKodBidangCategoryL3() {
        return kodBidangCategoryL3;
    }


    /**
     * Sets the kodBidangCategoryL3 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @param kodBidangCategoryL3
     */
    public void setKodBidangCategoryL3(java.lang.String kodBidangCategoryL3) {
        this.kodBidangCategoryL3 = kodBidangCategoryL3;
    }


    /**
     * Gets the bidangDescriptionCategoryL1 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @return bidangDescriptionCategoryL1
     */
    public java.lang.String getBidangDescriptionCategoryL1() {
        return bidangDescriptionCategoryL1;
    }


    /**
     * Sets the bidangDescriptionCategoryL1 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @param bidangDescriptionCategoryL1
     */
    public void setBidangDescriptionCategoryL1(java.lang.String bidangDescriptionCategoryL1) {
        this.bidangDescriptionCategoryL1 = bidangDescriptionCategoryL1;
    }


    /**
     * Gets the bidangDescriptionCategoryL2 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @return bidangDescriptionCategoryL2
     */
    public java.lang.String getBidangDescriptionCategoryL2() {
        return bidangDescriptionCategoryL2;
    }


    /**
     * Sets the bidangDescriptionCategoryL2 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @param bidangDescriptionCategoryL2
     */
    public void setBidangDescriptionCategoryL2(java.lang.String bidangDescriptionCategoryL2) {
        this.bidangDescriptionCategoryL2 = bidangDescriptionCategoryL2;
    }


    /**
     * Gets the bidangDescriptionCategoryL3 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @return bidangDescriptionCategoryL3
     */
    public java.lang.String getBidangDescriptionCategoryL3() {
        return bidangDescriptionCategoryL3;
    }


    /**
     * Sets the bidangDescriptionCategoryL3 value for this UpdateSupplierInfoRqBidangArrayBidang.
     * 
     * @param bidangDescriptionCategoryL3
     */
    public void setBidangDescriptionCategoryL3(java.lang.String bidangDescriptionCategoryL3) {
        this.bidangDescriptionCategoryL3 = bidangDescriptionCategoryL3;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateSupplierInfoRqBidangArrayBidang)) return false;
        UpdateSupplierInfoRqBidangArrayBidang other = (UpdateSupplierInfoRqBidangArrayBidang) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.kodBidangCategoryL1==null && other.getKodBidangCategoryL1()==null) || 
             (this.kodBidangCategoryL1!=null &&
              this.kodBidangCategoryL1.equals(other.getKodBidangCategoryL1()))) &&
            ((this.kodBidangCategoryL2==null && other.getKodBidangCategoryL2()==null) || 
             (this.kodBidangCategoryL2!=null &&
              this.kodBidangCategoryL2.equals(other.getKodBidangCategoryL2()))) &&
            ((this.kodBidangCategoryL3==null && other.getKodBidangCategoryL3()==null) || 
             (this.kodBidangCategoryL3!=null &&
              this.kodBidangCategoryL3.equals(other.getKodBidangCategoryL3()))) &&
            ((this.bidangDescriptionCategoryL1==null && other.getBidangDescriptionCategoryL1()==null) || 
             (this.bidangDescriptionCategoryL1!=null &&
              this.bidangDescriptionCategoryL1.equals(other.getBidangDescriptionCategoryL1()))) &&
            ((this.bidangDescriptionCategoryL2==null && other.getBidangDescriptionCategoryL2()==null) || 
             (this.bidangDescriptionCategoryL2!=null &&
              this.bidangDescriptionCategoryL2.equals(other.getBidangDescriptionCategoryL2()))) &&
            ((this.bidangDescriptionCategoryL3==null && other.getBidangDescriptionCategoryL3()==null) || 
             (this.bidangDescriptionCategoryL3!=null &&
              this.bidangDescriptionCategoryL3.equals(other.getBidangDescriptionCategoryL3())));
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
        if (getKodBidangCategoryL1() != null) {
            _hashCode += getKodBidangCategoryL1().hashCode();
        }
        if (getKodBidangCategoryL2() != null) {
            _hashCode += getKodBidangCategoryL2().hashCode();
        }
        if (getKodBidangCategoryL3() != null) {
            _hashCode += getKodBidangCategoryL3().hashCode();
        }
        if (getBidangDescriptionCategoryL1() != null) {
            _hashCode += getBidangDescriptionCategoryL1().hashCode();
        }
        if (getBidangDescriptionCategoryL2() != null) {
            _hashCode += getBidangDescriptionCategoryL2().hashCode();
        }
        if (getBidangDescriptionCategoryL3() != null) {
            _hashCode += getBidangDescriptionCategoryL3().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateSupplierInfoRqBidangArrayBidang.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", ">>>UpdateSupplierInfoRq>BidangArray>Bidang"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kodBidangCategoryL1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "KodBidangCategoryL1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kodBidangCategoryL2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "KodBidangCategoryL2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kodBidangCategoryL3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "KodBidangCategoryL3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bidangDescriptionCategoryL1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "BidangDescriptionCategoryL1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bidangDescriptionCategoryL2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "BidangDescriptionCategoryL2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bidangDescriptionCategoryL3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateSupplierInfo", "BidangDescriptionCategoryL3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
