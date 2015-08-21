/**
 * PHISMasterDataDeliveryAddressPortType_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.phis.ptj.deliveryaddress;

public class PHISMasterDataDeliveryAddressPortType_ServiceLocator extends org.apache.axis.client.Service implements my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortType_Service {

    public PHISMasterDataDeliveryAddressPortType_ServiceLocator() {
    }


    public PHISMasterDataDeliveryAddressPortType_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PHISMasterDataDeliveryAddressPortType_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PHISMasterDataDeliveryAddressPortTypePort
    private java.lang.String PHISMasterDataDeliveryAddressPortTypePort_address = "http://192.168.62.18:8011/PHISMasterDataDeliveryAddress/v1.1";

    public java.lang.String getPHISMasterDataDeliveryAddressPortTypePortAddress() {
        return PHISMasterDataDeliveryAddressPortTypePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PHISMasterDataDeliveryAddressPortTypePortWSDDServiceName = "PHISMasterDataDeliveryAddressPortTypePort";

    public java.lang.String getPHISMasterDataDeliveryAddressPortTypePortWSDDServiceName() {
        return PHISMasterDataDeliveryAddressPortTypePortWSDDServiceName;
    }

    public void setPHISMasterDataDeliveryAddressPortTypePortWSDDServiceName(java.lang.String name) {
        PHISMasterDataDeliveryAddressPortTypePortWSDDServiceName = name;
    }

    public my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortType_PortType getPHISMasterDataDeliveryAddressPortTypePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PHISMasterDataDeliveryAddressPortTypePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPHISMasterDataDeliveryAddressPortTypePort(endpoint);
    }

    public my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortType_PortType getPHISMasterDataDeliveryAddressPortTypePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortTypeBindingStub _stub = new my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortTypeBindingStub(portAddress, this);
            _stub.setPortName(getPHISMasterDataDeliveryAddressPortTypePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPHISMasterDataDeliveryAddressPortTypePortEndpointAddress(java.lang.String address) {
        PHISMasterDataDeliveryAddressPortTypePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortType_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortTypeBindingStub _stub = new my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortTypeBindingStub(new java.net.URL(PHISMasterDataDeliveryAddressPortTypePort_address), this);
                _stub.setPortName(getPHISMasterDataDeliveryAddressPortTypePortWSDDServiceName());
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
        if ("PHISMasterDataDeliveryAddressPortTypePort".equals(inputPortName)) {
            return getPHISMasterDataDeliveryAddressPortTypePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/PHISMasterDataDeliveryAddress", "PHISMasterDataDeliveryAddressPortType");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/PHISMasterDataDeliveryAddress", "PHISMasterDataDeliveryAddressPortTypePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PHISMasterDataDeliveryAddressPortTypePort".equals(portName)) {
            setPHISMasterDataDeliveryAddressPortTypePortEndpointAddress(address);
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
