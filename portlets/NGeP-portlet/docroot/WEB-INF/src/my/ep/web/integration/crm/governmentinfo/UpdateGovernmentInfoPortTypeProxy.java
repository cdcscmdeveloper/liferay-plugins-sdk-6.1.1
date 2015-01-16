package my.ep.web.integration.crm.governmentinfo;

public class UpdateGovernmentInfoPortTypeProxy implements my.ep.web.integration.crm.governmentinfo.UpdateGovernmentInfoPortType_PortType {
  private String _endpoint = null;
  private my.ep.web.integration.crm.governmentinfo.UpdateGovernmentInfoPortType_PortType updateGovernmentInfoPortType_PortType = null;
  
  public UpdateGovernmentInfoPortTypeProxy() {
    _initUpdateGovernmentInfoPortTypeProxy();
  }
  
  public UpdateGovernmentInfoPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initUpdateGovernmentInfoPortTypeProxy();
  }
  
  private void _initUpdateGovernmentInfoPortTypeProxy() {
    try {
      updateGovernmentInfoPortType_PortType = (new my.ep.web.integration.crm.governmentinfo.UpdateGovernmentInfoPortType_ServiceLocator()).getUpdateGovernmentInfoPortTypePort();
      if (updateGovernmentInfoPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)updateGovernmentInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)updateGovernmentInfoPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (updateGovernmentInfoPortType_PortType != null)
      ((javax.xml.rpc.Stub)updateGovernmentInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.web.integration.crm.governmentinfo.UpdateGovernmentInfoPortType_PortType getUpdateGovernmentInfoPortType_PortType() {
    if (updateGovernmentInfoPortType_PortType == null)
      _initUpdateGovernmentInfoPortTypeProxy();
    return updateGovernmentInfoPortType_PortType;
  }
  
  public my.ep.web.integration.crm.governmentinfo.EPMFRs update(my.ep.web.integration.crm.governmentinfo.EPMFRq part) throws java.rmi.RemoteException{
    if (updateGovernmentInfoPortType_PortType == null)
      _initUpdateGovernmentInfoPortTypeProxy();
    return updateGovernmentInfoPortType_PortType.update(part);
  }
  
  
}