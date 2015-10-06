/**
 * PHISMasterDataMaterialInfomationPortType_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.phis.masterdatamateriainfo;

public class PHISMasterDataMaterialInfomationPortType_ServiceLocator extends org.apache.axis.client.Service implements my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortType_Service {

    public PHISMasterDataMaterialInfomationPortType_ServiceLocator() {
    }


    public PHISMasterDataMaterialInfomationPortType_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PHISMasterDataMaterialInfomationPortType_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PHISMasterDataMaterialInfomationPortTypePort
    private java.lang.String PHISMasterDataMaterialInfomationPortTypePort_address = "http://192.168.62.18:8011/PHISMasterDataMaterialInformation/v1.1";

    public java.lang.String getPHISMasterDataMaterialInfomationPortTypePortAddress() {
        return PHISMasterDataMaterialInfomationPortTypePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PHISMasterDataMaterialInfomationPortTypePortWSDDServiceName = "PHISMasterDataMaterialInfomationPortTypePort";

    public java.lang.String getPHISMasterDataMaterialInfomationPortTypePortWSDDServiceName() {
        return PHISMasterDataMaterialInfomationPortTypePortWSDDServiceName;
    }

    public void setPHISMasterDataMaterialInfomationPortTypePortWSDDServiceName(java.lang.String name) {
        PHISMasterDataMaterialInfomationPortTypePortWSDDServiceName = name;
    }

    public my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortType_PortType getPHISMasterDataMaterialInfomationPortTypePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PHISMasterDataMaterialInfomationPortTypePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPHISMasterDataMaterialInfomationPortTypePort(endpoint);
    }

    public my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortType_PortType getPHISMasterDataMaterialInfomationPortTypePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortTypeBindingStub _stub = new my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortTypeBindingStub(portAddress, this);
            _stub.setPortName(getPHISMasterDataMaterialInfomationPortTypePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPHISMasterDataMaterialInfomationPortTypePortEndpointAddress(java.lang.String address) {
        PHISMasterDataMaterialInfomationPortTypePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortType_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortTypeBindingStub _stub = new my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortTypeBindingStub(new java.net.URL(PHISMasterDataMaterialInfomationPortTypePort_address), this);
                _stub.setPortName(getPHISMasterDataMaterialInfomationPortTypePortWSDDServiceName());
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
        if ("PHISMasterDataMaterialInfomationPortTypePort".equals(inputPortName)) {
            return getPHISMasterDataMaterialInfomationPortTypePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/PHISMasterDataMaterialInfomation", "PHISMasterDataMaterialInfomationPortType");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/PHISMasterDataMaterialInfomation", "PHISMasterDataMaterialInfomationPortTypePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PHISMasterDataMaterialInfomationPortTypePort".equals(portName)) {
            setPHISMasterDataMaterialInfomationPortTypePortEndpointAddress(address);
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
