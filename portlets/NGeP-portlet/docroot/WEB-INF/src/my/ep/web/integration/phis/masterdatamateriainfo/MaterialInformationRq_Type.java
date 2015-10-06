/**
 * MaterialInformationRq_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.phis.masterdatamateriainfo;

public class MaterialInformationRq_Type  implements java.io.Serializable {
    private java.lang.String productCode;

    private java.lang.String codeType;

    private java.lang.String kodTindakan;

    private java.lang.String oldProductCode;

    private java.lang.String itemDescription;

    private java.util.Date startEffectiveDate;

    private java.util.Date endEffectiveDate;

    private java.lang.String commodityName;

    private java.lang.String baseUnitOfMeasure;

    private java.lang.String segment;

    private java.lang.String family;

    private my.ep.web.integration.phis.masterdatamateriainfo.UomDetailsArray_Type uomDetailsArray;

    public MaterialInformationRq_Type() {
    }

    public MaterialInformationRq_Type(
           java.lang.String productCode,
           java.lang.String codeType,
           java.lang.String kodTindakan,
           java.lang.String oldProductCode,
           java.lang.String itemDescription,
           java.util.Date startEffectiveDate,
           java.util.Date endEffectiveDate,
           java.lang.String commodityName,
           java.lang.String baseUnitOfMeasure,
           java.lang.String segment,
           java.lang.String family,
           my.ep.web.integration.phis.masterdatamateriainfo.UomDetailsArray_Type uomDetailsArray) {
           this.productCode = productCode;
           this.codeType = codeType;
           this.kodTindakan = kodTindakan;
           this.oldProductCode = oldProductCode;
           this.itemDescription = itemDescription;
           this.startEffectiveDate = startEffectiveDate;
           this.endEffectiveDate = endEffectiveDate;
           this.commodityName = commodityName;
           this.baseUnitOfMeasure = baseUnitOfMeasure;
           this.segment = segment;
           this.family = family;
           this.uomDetailsArray = uomDetailsArray;
    }


    /**
     * Gets the productCode value for this MaterialInformationRq_Type.
     * 
     * @return productCode
     */
    public java.lang.String getProductCode() {
        return productCode;
    }


    /**
     * Sets the productCode value for this MaterialInformationRq_Type.
     * 
     * @param productCode
     */
    public void setProductCode(java.lang.String productCode) {
        this.productCode = productCode;
    }


    /**
     * Gets the codeType value for this MaterialInformationRq_Type.
     * 
     * @return codeType
     */
    public java.lang.String getCodeType() {
        return codeType;
    }


    /**
     * Sets the codeType value for this MaterialInformationRq_Type.
     * 
     * @param codeType
     */
    public void setCodeType(java.lang.String codeType) {
        this.codeType = codeType;
    }


    /**
     * Gets the kodTindakan value for this MaterialInformationRq_Type.
     * 
     * @return kodTindakan
     */
    public java.lang.String getKodTindakan() {
        return kodTindakan;
    }


    /**
     * Sets the kodTindakan value for this MaterialInformationRq_Type.
     * 
     * @param kodTindakan
     */
    public void setKodTindakan(java.lang.String kodTindakan) {
        this.kodTindakan = kodTindakan;
    }


    /**
     * Gets the oldProductCode value for this MaterialInformationRq_Type.
     * 
     * @return oldProductCode
     */
    public java.lang.String getOldProductCode() {
        return oldProductCode;
    }


    /**
     * Sets the oldProductCode value for this MaterialInformationRq_Type.
     * 
     * @param oldProductCode
     */
    public void setOldProductCode(java.lang.String oldProductCode) {
        this.oldProductCode = oldProductCode;
    }


    /**
     * Gets the itemDescription value for this MaterialInformationRq_Type.
     * 
     * @return itemDescription
     */
    public java.lang.String getItemDescription() {
        return itemDescription;
    }


    /**
     * Sets the itemDescription value for this MaterialInformationRq_Type.
     * 
     * @param itemDescription
     */
    public void setItemDescription(java.lang.String itemDescription) {
        this.itemDescription = itemDescription;
    }


    /**
     * Gets the startEffectiveDate value for this MaterialInformationRq_Type.
     * 
     * @return startEffectiveDate
     */
    public java.util.Date getStartEffectiveDate() {
        return startEffectiveDate;
    }


    /**
     * Sets the startEffectiveDate value for this MaterialInformationRq_Type.
     * 
     * @param startEffectiveDate
     */
    public void setStartEffectiveDate(java.util.Date startEffectiveDate) {
        this.startEffectiveDate = startEffectiveDate;
    }


    /**
     * Gets the endEffectiveDate value for this MaterialInformationRq_Type.
     * 
     * @return endEffectiveDate
     */
    public java.util.Date getEndEffectiveDate() {
        return endEffectiveDate;
    }


    /**
     * Sets the endEffectiveDate value for this MaterialInformationRq_Type.
     * 
     * @param endEffectiveDate
     */
    public void setEndEffectiveDate(java.util.Date endEffectiveDate) {
        this.endEffectiveDate = endEffectiveDate;
    }


    /**
     * Gets the commodityName value for this MaterialInformationRq_Type.
     * 
     * @return commodityName
     */
    public java.lang.String getCommodityName() {
        return commodityName;
    }


    /**
     * Sets the commodityName value for this MaterialInformationRq_Type.
     * 
     * @param commodityName
     */
    public void setCommodityName(java.lang.String commodityName) {
        this.commodityName = commodityName;
    }


    /**
     * Gets the baseUnitOfMeasure value for this MaterialInformationRq_Type.
     * 
     * @return baseUnitOfMeasure
     */
    public java.lang.String getBaseUnitOfMeasure() {
        return baseUnitOfMeasure;
    }


    /**
     * Sets the baseUnitOfMeasure value for this MaterialInformationRq_Type.
     * 
     * @param baseUnitOfMeasure
     */
    public void setBaseUnitOfMeasure(java.lang.String baseUnitOfMeasure) {
        this.baseUnitOfMeasure = baseUnitOfMeasure;
    }


    /**
     * Gets the segment value for this MaterialInformationRq_Type.
     * 
     * @return segment
     */
    public java.lang.String getSegment() {
        return segment;
    }


    /**
     * Sets the segment value for this MaterialInformationRq_Type.
     * 
     * @param segment
     */
    public void setSegment(java.lang.String segment) {
        this.segment = segment;
    }


    /**
     * Gets the family value for this MaterialInformationRq_Type.
     * 
     * @return family
     */
    public java.lang.String getFamily() {
        return family;
    }


    /**
     * Sets the family value for this MaterialInformationRq_Type.
     * 
     * @param family
     */
    public void setFamily(java.lang.String family) {
        this.family = family;
    }


    /**
     * Gets the uomDetailsArray value for this MaterialInformationRq_Type.
     * 
     * @return uomDetailsArray
     */
    public my.ep.web.integration.phis.masterdatamateriainfo.UomDetailsArray_Type getUomDetailsArray() {
        return uomDetailsArray;
    }


    /**
     * Sets the uomDetailsArray value for this MaterialInformationRq_Type.
     * 
     * @param uomDetailsArray
     */
    public void setUomDetailsArray(my.ep.web.integration.phis.masterdatamateriainfo.UomDetailsArray_Type uomDetailsArray) {
        this.uomDetailsArray = uomDetailsArray;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MaterialInformationRq_Type)) return false;
        MaterialInformationRq_Type other = (MaterialInformationRq_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.productCode==null && other.getProductCode()==null) || 
             (this.productCode!=null &&
              this.productCode.equals(other.getProductCode()))) &&
            ((this.codeType==null && other.getCodeType()==null) || 
             (this.codeType!=null &&
              this.codeType.equals(other.getCodeType()))) &&
            ((this.kodTindakan==null && other.getKodTindakan()==null) || 
             (this.kodTindakan!=null &&
              this.kodTindakan.equals(other.getKodTindakan()))) &&
            ((this.oldProductCode==null && other.getOldProductCode()==null) || 
             (this.oldProductCode!=null &&
              this.oldProductCode.equals(other.getOldProductCode()))) &&
            ((this.itemDescription==null && other.getItemDescription()==null) || 
             (this.itemDescription!=null &&
              this.itemDescription.equals(other.getItemDescription()))) &&
            ((this.startEffectiveDate==null && other.getStartEffectiveDate()==null) || 
             (this.startEffectiveDate!=null &&
              this.startEffectiveDate.equals(other.getStartEffectiveDate()))) &&
            ((this.endEffectiveDate==null && other.getEndEffectiveDate()==null) || 
             (this.endEffectiveDate!=null &&
              this.endEffectiveDate.equals(other.getEndEffectiveDate()))) &&
            ((this.commodityName==null && other.getCommodityName()==null) || 
             (this.commodityName!=null &&
              this.commodityName.equals(other.getCommodityName()))) &&
            ((this.baseUnitOfMeasure==null && other.getBaseUnitOfMeasure()==null) || 
             (this.baseUnitOfMeasure!=null &&
              this.baseUnitOfMeasure.equals(other.getBaseUnitOfMeasure()))) &&
            ((this.segment==null && other.getSegment()==null) || 
             (this.segment!=null &&
              this.segment.equals(other.getSegment()))) &&
            ((this.family==null && other.getFamily()==null) || 
             (this.family!=null &&
              this.family.equals(other.getFamily()))) &&
            ((this.uomDetailsArray==null && other.getUomDetailsArray()==null) || 
             (this.uomDetailsArray!=null &&
              this.uomDetailsArray.equals(other.getUomDetailsArray())));
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
        if (getProductCode() != null) {
            _hashCode += getProductCode().hashCode();
        }
        if (getCodeType() != null) {
            _hashCode += getCodeType().hashCode();
        }
        if (getKodTindakan() != null) {
            _hashCode += getKodTindakan().hashCode();
        }
        if (getOldProductCode() != null) {
            _hashCode += getOldProductCode().hashCode();
        }
        if (getItemDescription() != null) {
            _hashCode += getItemDescription().hashCode();
        }
        if (getStartEffectiveDate() != null) {
            _hashCode += getStartEffectiveDate().hashCode();
        }
        if (getEndEffectiveDate() != null) {
            _hashCode += getEndEffectiveDate().hashCode();
        }
        if (getCommodityName() != null) {
            _hashCode += getCommodityName().hashCode();
        }
        if (getBaseUnitOfMeasure() != null) {
            _hashCode += getBaseUnitOfMeasure().hashCode();
        }
        if (getSegment() != null) {
            _hashCode += getSegment().hashCode();
        }
        if (getFamily() != null) {
            _hashCode += getFamily().hashCode();
        }
        if (getUomDetailsArray() != null) {
            _hashCode += getUomDetailsArray().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MaterialInformationRq_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "MaterialInformationRq_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "ProductCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">ProductCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codeType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "CodeType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">CodeType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kodTindakan");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "KodTindakan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">KodTindakan"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldProductCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "OldProductCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">OldProductCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "ItemDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">ItemDescription"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startEffectiveDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "StartEffectiveDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endEffectiveDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "EndEffectiveDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commodityName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "CommodityName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">CommodityName"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseUnitOfMeasure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "BaseUnitOfMeasure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">BaseUnitOfMeasure"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("segment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "Segment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">Segment"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("family");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "Family"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", ">Family"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uomDetailsArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "UomDetailsArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ep.gov.my/Schema/1-0/PHISMasterDataMaterialInfomation", "UomDetailsArray_Type"));
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
