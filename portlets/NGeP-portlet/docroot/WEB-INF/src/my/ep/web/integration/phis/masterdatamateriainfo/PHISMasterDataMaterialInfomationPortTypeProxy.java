package my.ep.web.integration.phis.masterdatamateriainfo;

public class PHISMasterDataMaterialInfomationPortTypeProxy implements my.gov.ep.www.Service._1_0.PHISMasterDataMaterialInfomation.PHISMasterDataMaterialInfomationPortType_PortType {
  private String _endpoint = null;
  private my.gov.ep.www.Service._1_0.PHISMasterDataMaterialInfomation.PHISMasterDataMaterialInfomationPortType_PortType pHISMasterDataMaterialInfomationPortType_PortType = null;
  
  public PHISMasterDataMaterialInfomationPortTypeProxy() {
    _initPHISMasterDataMaterialInfomationPortTypeProxy();
  }
  
  public PHISMasterDataMaterialInfomationPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initPHISMasterDataMaterialInfomationPortTypeProxy();
  }
  
  private void _initPHISMasterDataMaterialInfomationPortTypeProxy() {
    try {
      pHISMasterDataMaterialInfomationPortType_PortType = (new my.gov.ep.www.Service._1_0.PHISMasterDataMaterialInfomation.PHISMasterDataMaterialInfomationPortType_ServiceLocator()).getPHISMasterDataMaterialInfomationPortTypePort();
      if (pHISMasterDataMaterialInfomationPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)pHISMasterDataMaterialInfomationPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)pHISMasterDataMaterialInfomationPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (pHISMasterDataMaterialInfomationPortType_PortType != null)
      ((javax.xml.rpc.Stub)pHISMasterDataMaterialInfomationPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.gov.ep.www.Service._1_0.PHISMasterDataMaterialInfomation.PHISMasterDataMaterialInfomationPortType_PortType getPHISMasterDataMaterialInfomationPortType_PortType() {
    if (pHISMasterDataMaterialInfomationPortType_PortType == null)
      _initPHISMasterDataMaterialInfomationPortTypeProxy();
    return pHISMasterDataMaterialInfomationPortType_PortType;
  }
  
  public my.gov.ep.www.Schema._1_0.PHISMasterDataMaterialInfomation.EPMFRs_Type inquire(my.gov.ep.www.Schema._1_0.PHISMasterDataMaterialInfomation.EPMFRq_Type part) throws java.rmi.RemoteException{
    if (pHISMasterDataMaterialInfomationPortType_PortType == null)
      _initPHISMasterDataMaterialInfomationPortTypeProxy();
    return pHISMasterDataMaterialInfomationPortType_PortType.inquire(part);
  }
  
  
}