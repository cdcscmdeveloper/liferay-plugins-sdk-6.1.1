/**
 * SC_SiteVisitProposal_DataType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.sc.Data.SC_SiteVisitProposal_Data;

public class SC_SiteVisitProposal_DataType  implements java.io.Serializable {
    private java.lang.Integer qt_id;

    private java.lang.String supplier;

    private java.lang.Boolean approval;

    private java.util.Calendar closing_time;

    private java.lang.String[] recommended_suppliers;

    public SC_SiteVisitProposal_DataType() {
    }

    public SC_SiteVisitProposal_DataType(
           java.lang.Integer qt_id,
           java.lang.String supplier,
           java.lang.Boolean approval,
           java.util.Calendar closing_time,
           java.lang.String[] recommended_suppliers) {
           this.qt_id = qt_id;
           this.supplier = supplier;
           this.approval = approval;
           this.closing_time = closing_time;
           this.recommended_suppliers = recommended_suppliers;
    }


    /**
     * Gets the qt_id value for this SC_SiteVisitProposal_DataType.
     * 
     * @return qt_id
     */
    public java.lang.Integer getQt_id() {
        return qt_id;
    }


    /**
     * Sets the qt_id value for this SC_SiteVisitProposal_DataType.
     * 
     * @param qt_id
     */
    public void setQt_id(java.lang.Integer qt_id) {
        this.qt_id = qt_id;
    }


    /**
     * Gets the supplier value for this SC_SiteVisitProposal_DataType.
     * 
     * @return supplier
     */
    public java.lang.String getSupplier() {
        return supplier;
    }


    /**
     * Sets the supplier value for this SC_SiteVisitProposal_DataType.
     * 
     * @param supplier
     */
    public void setSupplier(java.lang.String supplier) {
        this.supplier = supplier;
    }


    /**
     * Gets the approval value for this SC_SiteVisitProposal_DataType.
     * 
     * @return approval
     */
    public java.lang.Boolean getApproval() {
        return approval;
    }


    /**
     * Sets the approval value for this SC_SiteVisitProposal_DataType.
     * 
     * @param approval
     */
    public void setApproval(java.lang.Boolean approval) {
        this.approval = approval;
    }


    /**
     * Gets the closing_time value for this SC_SiteVisitProposal_DataType.
     * 
     * @return closing_time
     */
    public java.util.Calendar getClosing_time() {
        return closing_time;
    }


    /**
     * Sets the closing_time value for this SC_SiteVisitProposal_DataType.
     * 
     * @param closing_time
     */
    public void setClosing_time(java.util.Calendar closing_time) {
        this.closing_time = closing_time;
    }


    /**
     * Gets the recommended_suppliers value for this SC_SiteVisitProposal_DataType.
     * 
     * @return recommended_suppliers
     */
    public java.lang.String[] getRecommended_suppliers() {
        return recommended_suppliers;
    }


    /**
     * Sets the recommended_suppliers value for this SC_SiteVisitProposal_DataType.
     * 
     * @param recommended_suppliers
     */
    public void setRecommended_suppliers(java.lang.String[] recommended_suppliers) {
        this.recommended_suppliers = recommended_suppliers;
    }

    public java.lang.String getRecommended_suppliers(int i) {
        return this.recommended_suppliers[i];
    }

    public void setRecommended_suppliers(int i, java.lang.String _value) {
        this.recommended_suppliers[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SC_SiteVisitProposal_DataType)) return false;
        SC_SiteVisitProposal_DataType other = (SC_SiteVisitProposal_DataType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.qt_id==null && other.getQt_id()==null) || 
             (this.qt_id!=null &&
              this.qt_id.equals(other.getQt_id()))) &&
            ((this.supplier==null && other.getSupplier()==null) || 
             (this.supplier!=null &&
              this.supplier.equals(other.getSupplier()))) &&
            ((this.approval==null && other.getApproval()==null) || 
             (this.approval!=null &&
              this.approval.equals(other.getApproval()))) &&
            ((this.closing_time==null && other.getClosing_time()==null) || 
             (this.closing_time!=null &&
              this.closing_time.equals(other.getClosing_time()))) &&
            ((this.recommended_suppliers==null && other.getRecommended_suppliers()==null) || 
             (this.recommended_suppliers!=null &&
              java.util.Arrays.equals(this.recommended_suppliers, other.getRecommended_suppliers())));
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
        if (getQt_id() != null) {
            _hashCode += getQt_id().hashCode();
        }
        if (getSupplier() != null) {
            _hashCode += getSupplier().hashCode();
        }
        if (getApproval() != null) {
            _hashCode += getApproval().hashCode();
        }
        if (getClosing_time() != null) {
            _hashCode += getClosing_time().hashCode();
        }
        if (getRecommended_suppliers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRecommended_suppliers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRecommended_suppliers(), i);
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
        new org.apache.axis.description.TypeDesc(SC_SiteVisitProposal_DataType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://xmlns.oracle.com/bpm/bpmobject/Data/SC_SiteVisitProposal_Data", "SC_SiteVisitProposal_DataType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qt_id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://xmlns.oracle.com/bpm/bpmobject/Data/SC_SiteVisitProposal_Data", "qt_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supplier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://xmlns.oracle.com/bpm/bpmobject/Data/SC_SiteVisitProposal_Data", "supplier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approval");
        elemField.setXmlName(new javax.xml.namespace.QName("http://xmlns.oracle.com/bpm/bpmobject/Data/SC_SiteVisitProposal_Data", "approval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("closing_time");
        elemField.setXmlName(new javax.xml.namespace.QName("http://xmlns.oracle.com/bpm/bpmobject/Data/SC_SiteVisitProposal_Data", "closing_time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recommended_suppliers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://xmlns.oracle.com/bpm/bpmobject/Data/SC_SiteVisitProposal_Data", "recommended_suppliers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
