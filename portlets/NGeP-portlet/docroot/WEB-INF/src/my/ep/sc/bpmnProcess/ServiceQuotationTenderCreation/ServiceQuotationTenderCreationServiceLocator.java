/**
 * ServiceQuotationTenderCreationServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation;

public class ServiceQuotationTenderCreationServiceLocator extends org.apache.axis.client.Service implements my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationService {

    public ServiceQuotationTenderCreationServiceLocator() {
    }


    public ServiceQuotationTenderCreationServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServiceQuotationTenderCreationServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ServiceQuotationTenderCreationPort
    private java.lang.String ServiceQuotationTenderCreationPort_address = "http://192.168.120.17:8001/soa-infra/services/default/Sourcing/ServiceQuotationTenderCreation.service";

    public java.lang.String getServiceQuotationTenderCreationPortAddress() {
        return ServiceQuotationTenderCreationPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ServiceQuotationTenderCreationPortWSDDServiceName = "ServiceQuotationTenderCreationPort";

    public java.lang.String getServiceQuotationTenderCreationPortWSDDServiceName() {
        return ServiceQuotationTenderCreationPortWSDDServiceName;
    }

    public void setServiceQuotationTenderCreationPortWSDDServiceName(java.lang.String name) {
        ServiceQuotationTenderCreationPortWSDDServiceName = name;
    }

    public my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationPortType getServiceQuotationTenderCreationPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ServiceQuotationTenderCreationPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getServiceQuotationTenderCreationPort(endpoint);
    }

    public my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationPortType getServiceQuotationTenderCreationPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationBindingStub _stub = new my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationBindingStub(portAddress, this);
            _stub.setPortName(getServiceQuotationTenderCreationPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setServiceQuotationTenderCreationPortEndpointAddress(java.lang.String address) {
        ServiceQuotationTenderCreationPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationBindingStub _stub = new my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationBindingStub(new java.net.URL(ServiceQuotationTenderCreationPort_address), this);
                _stub.setPortName(getServiceQuotationTenderCreationPortWSDDServiceName());
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
        if ("ServiceQuotationTenderCreationPort".equals(inputPortName)) {
            return getServiceQuotationTenderCreationPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://xmlns.oracle.com/bpmn/bpmnProcess/ServiceQuotationTenderCreation", "ServiceQuotationTenderCreation.service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://xmlns.oracle.com/bpmn/bpmnProcess/ServiceQuotationTenderCreation", "ServiceQuotationTenderCreationPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ServiceQuotationTenderCreationPort".equals(portName)) {
            setServiceQuotationTenderCreationPortEndpointAddress(address);
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
