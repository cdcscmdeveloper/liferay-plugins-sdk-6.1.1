/**
 * DeliveryAddressRq_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.com.integration.phis.ptj.deliveryaddress;

public class DeliveryAddressRq_Type  implements java.io.Serializable {
    private java.lang.String action;

    private java.lang.String PTJCode;

    private int noOfDeliveryAddress;

    private DeliveryAddress_Type[] deliveryAddressArray;

    public DeliveryAddressRq_Type() {
    }

    public DeliveryAddressRq_Type(
           java.lang.String action,
           java.lang.String PTJCode,
           int noOfDeliveryAddress,
           DeliveryAddress_Type[] deliveryAddressArray) {
           this.action = action;
           this.PTJCode = PTJCode;
           this.noOfDeliveryAddress = noOfDeliveryAddress;
           this.deliveryAddressArray = deliveryAddressArray;
    }


    /**
     * Gets the action value for this DeliveryAddressRq_Type.
     * 
     * @return action
     */
    public java.lang.String getAction() {
        return action;
    }


    /**
     * Sets the action value for this DeliveryAddressRq_Type.
     * 
     * @param action
     */
    public void setAction(java.lang.String action) {
        this.action = action;
    }


    /**
     * Gets the PTJCode value for this DeliveryAddressRq_Type.
     * 
     * @return PTJCode
     */
    public java.lang.String getPTJCode() {
        return PTJCode;
    }


    /**
     * Sets the PTJCode value for this DeliveryAddressRq_Type.
     * 
     * @param PTJCode
     */
    public void setPTJCode(java.lang.String PTJCode) {
        this.PTJCode = PTJCode;
    }


    /**
     * Gets the noOfDeliveryAddress value for this DeliveryAddressRq_Type.
     * 
     * @return noOfDeliveryAddress
     */
    public int getNoOfDeliveryAddress() {
        return noOfDeliveryAddress;
    }


    /**
     * Sets the noOfDeliveryAddress value for this DeliveryAddressRq_Type.
     * 
     * @param noOfDeliveryAddress
     */
    public void setNoOfDeliveryAddress(int noOfDeliveryAddress) {
        this.noOfDeliveryAddress = noOfDeliveryAddress;
    }


    /**
     * Gets the deliveryAddressArray value for this DeliveryAddressRq_Type.
     * 
     * @return deliveryAddressArray
     */
    public DeliveryAddress_Type[] getDeliveryAddressArray() {
        return deliveryAddressArray;
    }


    /**
     * Sets the deliveryAddressArray value for this DeliveryAddressRq_Type.
     * 
     * @param deliveryAddressArray
     */
    public void setDeliveryAddressArray(DeliveryAddress_Type[] deliveryAddressArray) {
        this.deliveryAddressArray = deliveryAddressArray;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeliveryAddressRq_Type)) return false;
        DeliveryAddressRq_Type other = (DeliveryAddressRq_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.action==null && other.getAction()==null) || 
             (this.action!=null &&
              this.action.equals(other.getAction()))) &&
            ((this.PTJCode==null && other.getPTJCode()==null) || 
             (this.PTJCode!=null &&
              this.PTJCode.equals(other.getPTJCode()))) &&
            this.noOfDeliveryAddress == other.getNoOfDeliveryAddress() &&
            ((this.deliveryAddressArray==null && other.getDeliveryAddressArray()==null) || 
             (this.deliveryAddressArray!=null &&
              java.util.Arrays.equals(this.deliveryAddressArray, other.getDeliveryAddressArray())));
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
        if (getAction() != null) {
            _hashCode += getAction().hashCode();
        }
        if (getPTJCode() != null) {
            _hashCode += getPTJCode().hashCode();
        }
        _hashCode += getNoOfDeliveryAddress();
        if (getDeliveryAddressArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDeliveryAddressArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDeliveryAddressArray(), i);
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
        new org.apache.axis.description.TypeDesc(DeliveryAddressRq_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", "DeliveryAddressRq_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("action");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", "Action"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", ">Action"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PTJCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", "PTJCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", ">PTJCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("noOfDeliveryAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", "NoOfDeliveryAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", ">NoOfDeliveryAddress"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deliveryAddressArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", "DeliveryAddressArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataDeliveryAddress", "DeliveryAddressArray_Type"));
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
