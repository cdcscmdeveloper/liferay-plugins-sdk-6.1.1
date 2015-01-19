/**
 * EPMFRq_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.phis.masterdatamateriainfo;

public class EPMFRq_Type  implements java.io.Serializable {
    private my.gov.ep.www.Schema._1_0.epmf.RqHeader_Type rqHeader;

    private my.gov.ep.www.Schema._1_0.PHISMasterDataMaterialInfomation.MaterialInformationRq_Type materialInformationRq;

    public EPMFRq_Type() {
    }

    public EPMFRq_Type(
           my.gov.ep.www.Schema._1_0.epmf.RqHeader_Type rqHeader,
           my.gov.ep.www.Schema._1_0.PHISMasterDataMaterialInfomation.MaterialInformationRq_Type materialInformationRq) {
           this.rqHeader = rqHeader;
           this.materialInformationRq = materialInformationRq;
    }


    /**
     * Gets the rqHeader value for this EPMFRq_Type.
     * 
     * @return rqHeader
     */
    public my.gov.ep.www.Schema._1_0.epmf.RqHeader_Type getRqHeader() {
        return rqHeader;
    }


    /**
     * Sets the rqHeader value for this EPMFRq_Type.
     * 
     * @param rqHeader
     */
    public void setRqHeader(my.gov.ep.www.Schema._1_0.epmf.RqHeader_Type rqHeader) {
        this.rqHeader = rqHeader;
    }


    /**
     * Gets the materialInformationRq value for this EPMFRq_Type.
     * 
     * @return materialInformationRq
     */
    public my.gov.ep.www.Schema._1_0.PHISMasterDataMaterialInfomation.MaterialInformationRq_Type getMaterialInformationRq() {
        return materialInformationRq;
    }


    /**
     * Sets the materialInformationRq value for this EPMFRq_Type.
     * 
     * @param materialInformationRq
     */
    public void setMaterialInformationRq(my.gov.ep.www.Schema._1_0.PHISMasterDataMaterialInfomation.MaterialInformationRq_Type materialInformationRq) {
        this.materialInformationRq = materialInformationRq;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPMFRq_Type)) return false;
        EPMFRq_Type other = (EPMFRq_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rqHeader==null && other.getRqHeader()==null) || 
             (this.rqHeader!=null &&
              this.rqHeader.equals(other.getRqHeader()))) &&
            ((this.materialInformationRq==null && other.getMaterialInformationRq()==null) || 
             (this.materialInformationRq!=null &&
              this.materialInformationRq.equals(other.getMaterialInformationRq())));
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
        if (getRqHeader() != null) {
            _hashCode += getRqHeader().hashCode();
        }
        if (getMaterialInformationRq() != null) {
            _hashCode += getMaterialInformationRq().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EPMFRq_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "EPMFRq_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rqHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader_Type"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialInformationRq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "MaterialInformationRq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "MaterialInformationRq_Type"));
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
