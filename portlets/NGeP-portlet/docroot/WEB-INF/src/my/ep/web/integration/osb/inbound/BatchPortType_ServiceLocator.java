/**
 * BatchPortType_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.osb.inbound;

public class BatchPortType_ServiceLocator extends org.apache.axis.client.Service implements my.ep.web.integration.osb.inbound.BatchPortType_Service {

    public BatchPortType_ServiceLocator() {
    }


    public BatchPortType_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public BatchPortType_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BatchPortTypePort
    private java.lang.String BatchPortTypePort_address = "http://192.168.62.18:8011/Batch/Generic/Inbound/v1.2";

    public java.lang.String getBatchPortTypePortAddress() {
        return BatchPortTypePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BatchPortTypePortWSDDServiceName = "BatchPortTypePort";

    public java.lang.String getBatchPortTypePortWSDDServiceName() {
        return BatchPortTypePortWSDDServiceName;
    }

    public void setBatchPortTypePortWSDDServiceName(java.lang.String name) {
        BatchPortTypePortWSDDServiceName = name;
    }

    public my.ep.web.integration.osb.inbound.BatchPortType_PortType getBatchPortTypePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BatchPortTypePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBatchPortTypePort(endpoint);
    }

    public my.ep.web.integration.osb.inbound.BatchPortType_PortType getBatchPortTypePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            my.ep.web.integration.osb.inbound.BatchPortTypeBindingStub _stub = new my.ep.web.integration.osb.inbound.BatchPortTypeBindingStub(portAddress, this);
            _stub.setPortName(getBatchPortTypePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBatchPortTypePortEndpointAddress(java.lang.String address) {
        BatchPortTypePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (my.ep.web.integration.osb.inbound.BatchPortType_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                my.ep.web.integration.osb.inbound.BatchPortTypeBindingStub _stub = new my.ep.web.integration.osb.inbound.BatchPortTypeBindingStub(new java.net.URL(BatchPortTypePort_address), this);
                _stub.setPortName(getBatchPortTypePortWSDDServiceName());
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
        if ("BatchPortTypePort".equals(inputPortName)) {
            return getBatchPortTypePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/Batch", "BatchPortType");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ep.gov.my/Service/1-0/Batch", "BatchPortTypePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BatchPortTypePort".equals(portName)) {
            setBatchPortTypePortEndpointAddress(address);
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
