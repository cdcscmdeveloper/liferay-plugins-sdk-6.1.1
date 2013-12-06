package my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation;

public class ServiceQuotationTenderCreationPortTypeProxy implements my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationPortType {
  private String _endpoint = null;
  private my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationPortType serviceQuotationTenderCreationPortType = null;
  
  public ServiceQuotationTenderCreationPortTypeProxy() {
    _initServiceQuotationTenderCreationPortTypeProxy();
  }
  
  public ServiceQuotationTenderCreationPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiceQuotationTenderCreationPortTypeProxy();
  }
  
  private void _initServiceQuotationTenderCreationPortTypeProxy() {
    try {
      serviceQuotationTenderCreationPortType = (new my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationServiceLocator()).getServiceQuotationTenderCreationPort();
      if (serviceQuotationTenderCreationPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)serviceQuotationTenderCreationPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)serviceQuotationTenderCreationPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (serviceQuotationTenderCreationPortType != null)
      ((javax.xml.rpc.Stub)serviceQuotationTenderCreationPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationPortType getServiceQuotationTenderCreationPortType() {
    if (serviceQuotationTenderCreationPortType == null)
      _initServiceQuotationTenderCreationPortTypeProxy();
    return serviceQuotationTenderCreationPortType;
  }
  
  public void start(my.ep.sc.bpm.bpmobject.Data.SC_QuotationTender_Data.SC_QuotationTender_DataType SC_QuotationTender_Data) throws java.rmi.RemoteException{
    if (serviceQuotationTenderCreationPortType == null)
      _initServiceQuotationTenderCreationPortTypeProxy();
    serviceQuotationTenderCreationPortType.start(SC_QuotationTender_Data);
  }
  
  
}