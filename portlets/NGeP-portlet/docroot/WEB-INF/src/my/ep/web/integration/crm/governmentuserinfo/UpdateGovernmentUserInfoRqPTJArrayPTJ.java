/**
 * UpdateGovernmentUserInfoRqPTJArrayPTJ.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.governmentuserinfo;

public class UpdateGovernmentUserInfoRqPTJArrayPTJ  implements java.io.Serializable {
    private java.lang.String PTJCode;

    private java.util.Date effectiveFrom;

    private java.util.Date effectiveTo;

    public UpdateGovernmentUserInfoRqPTJArrayPTJ() {
    }

    public UpdateGovernmentUserInfoRqPTJArrayPTJ(
           java.lang.String PTJCode,
           java.util.Date effectiveFrom,
           java.util.Date effectiveTo) {
           this.PTJCode = PTJCode;
           this.effectiveFrom = effectiveFrom;
           this.effectiveTo = effectiveTo;
    }


    /**
     * Gets the PTJCode value for this UpdateGovernmentUserInfoRqPTJArrayPTJ.
     * 
     * @return PTJCode
     */
    public java.lang.String getPTJCode() {
        return PTJCode;
    }


    /**
     * Sets the PTJCode value for this UpdateGovernmentUserInfoRqPTJArrayPTJ.
     * 
     * @param PTJCode
     */
    public void setPTJCode(java.lang.String PTJCode) {
        this.PTJCode = PTJCode;
    }


    /**
     * Gets the effectiveFrom value for this UpdateGovernmentUserInfoRqPTJArrayPTJ.
     * 
     * @return effectiveFrom
     */
    public java.util.Date getEffectiveFrom() {
        return effectiveFrom;
    }


    /**
     * Sets the effectiveFrom value for this UpdateGovernmentUserInfoRqPTJArrayPTJ.
     * 
     * @param effectiveFrom
     */
    public void setEffectiveFrom(java.util.Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }


    /**
     * Gets the effectiveTo value for this UpdateGovernmentUserInfoRqPTJArrayPTJ.
     * 
     * @return effectiveTo
     */
    public java.util.Date getEffectiveTo() {
        return effectiveTo;
    }


    /**
     * Sets the effectiveTo value for this UpdateGovernmentUserInfoRqPTJArrayPTJ.
     * 
     * @param effectiveTo
     */
    public void setEffectiveTo(java.util.Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateGovernmentUserInfoRqPTJArrayPTJ)) return false;
        UpdateGovernmentUserInfoRqPTJArrayPTJ other = (UpdateGovernmentUserInfoRqPTJArrayPTJ) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.PTJCode==null && other.getPTJCode()==null) || 
             (this.PTJCode!=null &&
              this.PTJCode.equals(other.getPTJCode()))) &&
            ((this.effectiveFrom==null && other.getEffectiveFrom()==null) || 
             (this.effectiveFrom!=null &&
              this.effectiveFrom.equals(other.getEffectiveFrom()))) &&
            ((this.effectiveTo==null && other.getEffectiveTo()==null) || 
             (this.effectiveTo!=null &&
              this.effectiveTo.equals(other.getEffectiveTo())));
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
        if (getPTJCode() != null) {
            _hashCode += getPTJCode().hashCode();
        }
        if (getEffectiveFrom() != null) {
            _hashCode += getEffectiveFrom().hashCode();
        }
        if (getEffectiveTo() != null) {
            _hashCode += getEffectiveTo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateGovernmentUserInfoRqPTJArrayPTJ.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", ">>>UpdateGovernmentUserInfoRq>PTJArray>PTJ"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PTJCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "PTJCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("effectiveFrom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "EffectiveFrom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("effectiveTo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/UpdateGovernmentUserInfo", "EffectiveTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
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
