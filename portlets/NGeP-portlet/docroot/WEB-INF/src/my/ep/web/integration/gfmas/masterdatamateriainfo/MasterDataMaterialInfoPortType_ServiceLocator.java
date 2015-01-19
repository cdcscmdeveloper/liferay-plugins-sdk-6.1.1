/**
 * MasterDataMaterialInfoPortType_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.gfmas.masterdatamateriainfo;

public class MasterDataMaterialInfoPortType_ServiceLocator extends org.apache.axis.client.Service implements my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_Service {

    public MasterDataMaterialInfoPortType_ServiceLocator() {
    }


    public MasterDataMaterialInfoPortType_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MasterDataMaterialInfoPortType_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MasterDataMaterialInfoPortTypePort
    private java.lang.String MasterDataMaterialInfoPortTypePort_address = "http://NGEPDevINS01.ngep.com:8011/MasterDataMaterialInfo/v1.3";

    public java.lang.String getMasterDataMaterialInfoPortTypePortAddress() {
        return MasterDataMaterialInfoPortTypePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MasterDataMaterialInfoPortTypePortWSDDServiceName = "MasterDataMaterialInfoPortTypePort";

    public java.lang.String getMasterDataMaterialInfoPortTypePortWSDDServiceName() {
        return MasterDataMaterialInfoPortTypePortWSDDServiceName;
    }

    public void setMasterDataMaterialInfoPortTypePortWSDDServiceName(java.lang.String name) {
        MasterDataMaterialInfoPortTypePortWSDDServiceName = name;
    }

    public my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_PortType getMasterDataMaterialInfoPortTypePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MasterDataMaterialInfoPortTypePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMasterDataMaterialInfoPortTypePort(endpoint);
    }

    public my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_PortType getMasterDataMaterialInfoPortTypePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortTypeBindingStub _stub = new my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortTypeBindingStub(portAddress, this);
            _stub.setPortName(getMasterDataMaterialInfoPortTypePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMasterDataMaterialInfoPortTypePortEndpointAddress(java.lang.String address) {
        MasterDataMaterialInfoPortTypePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortTypeBindingStub _stub = new my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortTypeBindingStub(new java.net.URL(MasterDataMaterialInfoPortTypePort_address), this);
                _stub.setPortName(getMasterDataMaterialInfoPortTypePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MasterDataMaterialInfoPortTypePort".equals(inputPortName)) {
            return getMasterDataMaterialInfoPortTypePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:MasterDataMaterialInfo", "MasterDataMaterialInfoPortType");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:MasterDataMaterialInfo", "MasterDataMaterialInfoPortTypePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MasterDataMaterialInfoPortTypePort".equals(portName)) {
            setMasterDataMaterialInfoPortTypePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
