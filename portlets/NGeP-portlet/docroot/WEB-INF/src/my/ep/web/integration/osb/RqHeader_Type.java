/**
 * RqHeader_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.osb;

public class RqHeader_Type  implements java.io.Serializable {
    private java.lang.String consumerID;

    private java.lang.String orgUnit;

    private java.lang.String segment;

    private java.lang.String ipAddress;

    private my.ep.web.integration.osb.RqUID_Type UID;

    public RqHeader_Type() {
    }

    public RqHeader_Type(
           java.lang.String consumerID,
           java.lang.String orgUnit,
           java.lang.String segment,
           java.lang.String ipAddress,
           my.ep.web.integration.osb.RqUID_Type UID) {
           this.consumerID = consumerID;
           this.orgUnit = orgUnit;
           this.segment = segment;
           this.ipAddress = ipAddress;
           this.UID = UID;
    }


    /**
     * Gets the consumerID value for this RqHeader_Type.
     * 
     * @return consumerID
     */
    public java.lang.String getConsumerID() {
        return consumerID;
    }


    /**
     * Sets the consumerID value for this RqHeader_Type.
     * 
     * @param consumerID
     */
    public void setConsumerID(java.lang.String consumerID) {
        this.consumerID = consumerID;
    }


    /**
     * Gets the orgUnit value for this RqHeader_Type.
     * 
     * @return orgUnit
     */
    public java.lang.String getOrgUnit() {
        return orgUnit;
    }


    /**
     * Sets the orgUnit value for this RqHeader_Type.
     * 
     * @param orgUnit
     */
    public void setOrgUnit(java.lang.String orgUnit) {
        this.orgUnit = orgUnit;
    }


    /**
     * Gets the segment value for this RqHeader_Type.
     * 
     * @return segment
     */
    public java.lang.String getSegment() {
        return segment;
    }


    /**
     * Sets the segment value for this RqHeader_Type.
     * 
     * @param segment
     */
    public void setSegment(java.lang.String segment) {
        this.segment = segment;
    }


    /**
     * Gets the ipAddress value for this RqHeader_Type.
     * 
     * @return ipAddress
     */
    public java.lang.String getIpAddress() {
        return ipAddress;
    }


    /**
     * Sets the ipAddress value for this RqHeader_Type.
     * 
     * @param ipAddress
     */
    public void setIpAddress(java.lang.String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * Gets the UID value for this RqHeader_Type.
     * 
     * @return UID
     */
    public my.ep.web.integration.osb.RqUID_Type getUID() {
        return UID;
    }


    /**
     * Sets the UID value for this RqHeader_Type.
     * 
     * @param UID
     */
    public void setUID(my.ep.web.integration.osb.RqUID_Type UID) {
        this.UID = UID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RqHeader_Type)) return false;
        RqHeader_Type other = (RqHeader_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.consumerID==null && other.getConsumerID()==null) || 
             (this.consumerID!=null &&
              this.consumerID.equals(other.getConsumerID()))) &&
            ((this.orgUnit==null && other.getOrgUnit()==null) || 
             (this.orgUnit!=null &&
              this.orgUnit.equals(other.getOrgUnit()))) &&
            ((this.segment==null && other.getSegment()==null) || 
             (this.segment!=null &&
              this.segment.equals(other.getSegment()))) &&
            ((this.ipAddress==null && other.getIpAddress()==null) || 
             (this.ipAddress!=null &&
              this.ipAddress.equals(other.getIpAddress()))) &&
            ((this.UID==null && other.getUID()==null) || 
             (this.UID!=null &&
              this.UID.equals(other.getUID())));
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
        if (getConsumerID() != null) {
            _hashCode += getConsumerID().hashCode();
        }
        if (getOrgUnit() != null) {
            _hashCode += getOrgUnit().hashCode();
        }
        if (getSegment() != null) {
            _hashCode += getSegment().hashCode();
        }
        if (getIpAddress() != null) {
            _hashCode += getIpAddress().hashCode();
        }
        if (getUID() != null) {
            _hashCode += getUID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RqHeader_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqHeader_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consumerID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "ConsumerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", ">ConsumerID"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orgUnit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "OrgUnit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", ">OrgUnit"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("segment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "Segment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", ">Segment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "IpAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", ">IpAddress"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("UID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "UID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/epmf", "RqUID_Type"));
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
