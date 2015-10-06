/**
 * UomDetails_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.phis.masterdatamateriainfo;

public class UomDetails_Type  implements java.io.Serializable {
    private java.lang.String altUnitOfMeasurement;

    private java.lang.Integer conversion;

    public UomDetails_Type() {
    }

    public UomDetails_Type(
           java.lang.String altUnitOfMeasurement,
           java.lang.Integer conversion) {
           this.altUnitOfMeasurement = altUnitOfMeasurement;
           this.conversion = conversion;
    }


    /**
     * Gets the altUnitOfMeasurement value for this UomDetails_Type.
     * 
     * @return altUnitOfMeasurement
     */
    public java.lang.String getAltUnitOfMeasurement() {
        return altUnitOfMeasurement;
    }


    /**
     * Sets the altUnitOfMeasurement value for this UomDetails_Type.
     * 
     * @param altUnitOfMeasurement
     */
    public void setAltUnitOfMeasurement(java.lang.String altUnitOfMeasurement) {
        this.altUnitOfMeasurement = altUnitOfMeasurement;
    }


    /**
     * Gets the conversion value for this UomDetails_Type.
     * 
     * @return conversion
     */
    public java.lang.Integer getConversion() {
        return conversion;
    }


    /**
     * Sets the conversion value for this UomDetails_Type.
     * 
     * @param conversion
     */
    public void setConversion(java.lang.Integer conversion) {
        this.conversion = conversion;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UomDetails_Type)) return false;
        UomDetails_Type other = (UomDetails_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.altUnitOfMeasurement==null && other.getAltUnitOfMeasurement()==null) || 
             (this.altUnitOfMeasurement!=null &&
              this.altUnitOfMeasurement.equals(other.getAltUnitOfMeasurement()))) &&
            ((this.conversion==null && other.getConversion()==null) || 
             (this.conversion!=null &&
              this.conversion.equals(other.getConversion())));
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
        if (getAltUnitOfMeasurement() != null) {
            _hashCode += getAltUnitOfMeasurement().hashCode();
        }
        if (getConversion() != null) {
            _hashCode += getConversion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UomDetails_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "UomDetails_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("altUnitOfMeasurement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "AltUnitOfMeasurement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">AltUnitOfMeasurement"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conversion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "Conversion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">Conversion"));
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
