package my.ep.web.integration.osb;

public class BatchPortTypeProxy implements my.ep.web.integration.osb.BatchPortType_PortType {
  private String _endpoint = null;
  private my.ep.web.integration.osb.BatchPortType_PortType batchPortType_PortType = null;
  
  public BatchPortTypeProxy() {
    _initBatchPortTypeProxy();
  }
  
  public BatchPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initBatchPortTypeProxy();
  }
  
  private void _initBatchPortTypeProxy() {
    try {
      batchPortType_PortType = (new my.ep.web.integration.osb.BatchPortType_ServiceLocator()).getBatchPortTypePort();
      if (batchPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)batchPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)batchPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (batchPortType_PortType != null)
      ((javax.xml.rpc.Stub)batchPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.web.integration.osb.BatchPortType_PortType getBatchPortType_PortType() {
    if (batchPortType_PortType == null)
      _initBatchPortTypeProxy();
    return batchPortType_PortType;
  }
  
  public my.ep.web.integration.osb.EPMFRs_Type invoke(my.ep.web.integration.osb.EPMFRq_Type part) throws java.rmi.RemoteException{
    if (batchPortType_PortType == null)
      _initBatchPortTypeProxy();
    return batchPortType_PortType.invoke(part);
  }
  
  
}