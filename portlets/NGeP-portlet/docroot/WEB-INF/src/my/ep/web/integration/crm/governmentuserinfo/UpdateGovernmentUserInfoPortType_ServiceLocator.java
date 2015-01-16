/**
 * UpdateGovernmentUserInfoPortType_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.crm.governmentuserinfo;

public class UpdateGovernmentUserInfoPortType_ServiceLocator extends org.apache.axis.client.Service implements my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_Service {

    public UpdateGovernmentUserInfoPortType_ServiceLocator() {
    }


    public UpdateGovernmentUserInfoPortType_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public UpdateGovernmentUserInfoPortType_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for UpdateGovernmentUserInfoPortTypePort
    private java.lang.String UpdateGovernmentUserInfoPortTypePort_address = "http://NGEPDevINS01.ngep.com:8011/UpdateGovernmentUserInfo/v1.0";

    public java.lang.String getUpdateGovernmentUserInfoPortTypePortAddress() {
        return UpdateGovernmentUserInfoPortTypePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String UpdateGovernmentUserInfoPortTypePortWSDDServiceName = "UpdateGovernmentUserInfoPortTypePort";

    public java.lang.String getUpdateGovernmentUserInfoPortTypePortWSDDServiceName() {
        return UpdateGovernmentUserInfoPortTypePortWSDDServiceName;
    }

    public void setUpdateGovernmentUserInfoPortTypePortWSDDServiceName(java.lang.String name) {
        UpdateGovernmentUserInfoPortTypePortWSDDServiceName = name;
    }

    public my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_PortType getUpdateGovernmentUserInfoPortTypePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(UpdateGovernmentUserInfoPortTypePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getUpdateGovernmentUserInfoPortTypePort(endpoint);
    }

    public my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_PortType getUpdateGovernmentUserInfoPortTypePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortTypeBindingStub _stub = new my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortTypeBindingStub(portAddress, this);
            _stub.setPortName(getUpdateGovernmentUserInfoPortTypePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setUpdateGovernmentUserInfoPortTypePortEndpointAddress(java.lang.String address) {
        UpdateGovernmentUserInfoPortTypePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortTypeBindingStub _stub = new my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortTypeBindingStub(new java.net.URL(UpdateGovernmentUserInfoPortTypePort_address), this);
                _stub.setPortName(getUpdateGovernmentUserInfoPortTypePortWSDDServiceName());
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
        if ("UpdateGovernmentUserInfoPortTypePort".equals(inputPortName)) {
            return getUpdateGovernmentUserInfoPortTypePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/UpdateGovernmentUserInfo", "UpdateGovernmentUserInfoPortType");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/UpdateGovernmentUserInfo", "UpdateGovernmentUserInfoPortTypePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("UpdateGovernmentUserInfoPortTypePort".equals(portName)) {
            setUpdateGovernmentUserInfoPortTypePortEndpointAddress(address);
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
