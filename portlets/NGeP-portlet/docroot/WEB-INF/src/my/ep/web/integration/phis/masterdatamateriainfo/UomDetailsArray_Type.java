/**
 * UomDetailsArray_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.phis.masterdatamateriainfo;

public class UomDetailsArray_Type  implements java.io.Serializable {
    private my.ep.web.integration.phis.masterdatamateriainfo.UomDetails_Type[] uomDetails;

    public UomDetailsArray_Type() {
    }

    public UomDetailsArray_Type(
           my.ep.web.integration.phis.masterdatamateriainfo.UomDetails_Type[] uomDetails) {
           this.uomDetails = uomDetails;
    }


    /**
     * Gets the uomDetails value for this UomDetailsArray_Type.
     * 
     * @return uomDetails
     */
    public my.ep.web.integration.phis.masterdatamateriainfo.UomDetails_Type[] getUomDetails() {
        return uomDetails;
    }


    /**
     * Sets the uomDetails value for this UomDetailsArray_Type.
     * 
     * @param uomDetails
     */
    public void setUomDetails(my.ep.web.integration.phis.masterdatamateriainfo.UomDetails_Type[] uomDetails) {
        this.uomDetails = uomDetails;
    }

    public my.ep.web.integration.phis.masterdatamateriainfo.UomDetails_Type getUomDetails(int i) {
        return this.uomDetails[i];
    }

    public void setUomDetails(int i, my.ep.web.integration.phis.masterdatamateriainfo.UomDetails_Type _value) {
        this.uomDetails[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UomDetailsArray_Type)) return false;
        UomDetailsArray_Type other = (UomDetailsArray_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.uomDetails==null && other.getUomDetails()==null) || 
             (this.uomDetails!=null &&
              java.util.Arrays.equals(this.uomDetails, other.getUomDetails())));
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
        if (getUomDetails() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUomDetails());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUomDetails(), i);
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
        new org.apache.axis.description.TypeDesc(UomDetailsArray_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "UomDetailsArray_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uomDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "UomDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "UomDetails"));
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
