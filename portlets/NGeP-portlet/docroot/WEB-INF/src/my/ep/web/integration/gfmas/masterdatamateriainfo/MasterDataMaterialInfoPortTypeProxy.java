package my.ep.web.integration.gfmas.masterdatamateriainfo;

public class MasterDataMaterialInfoPortTypeProxy implements my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_PortType {
  private String _endpoint = null;
  private my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_PortType masterDataMaterialInfoPortType_PortType = null;
  
  public MasterDataMaterialInfoPortTypeProxy() {
    _initMasterDataMaterialInfoPortTypeProxy();
  }
  
  public MasterDataMaterialInfoPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initMasterDataMaterialInfoPortTypeProxy();
  }
  
  private void _initMasterDataMaterialInfoPortTypeProxy() {
    try {
      masterDataMaterialInfoPortType_PortType = (new my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_ServiceLocator()).getMasterDataMaterialInfoPortTypePort();
      if (masterDataMaterialInfoPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)masterDataMaterialInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)masterDataMaterialInfoPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (masterDataMaterialInfoPortType_PortType != null)
      ((javax.xml.rpc.Stub)masterDataMaterialInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortType_PortType getMasterDataMaterialInfoPortType_PortType() {
    if (masterDataMaterialInfoPortType_PortType == null)
      _initMasterDataMaterialInfoPortTypeProxy();
    return masterDataMaterialInfoPortType_PortType;
  }
  
  public my.ep.web.integration.gfmas.masterdatamateriainfo.EPMFRs_Type inquire(my.ep.web.integration.gfmas.masterdatamateriainfo.EPMFRq_Type part) throws java.rmi.RemoteException{
    if (masterDataMaterialInfoPortType_PortType == null)
      _initMasterDataMaterialInfoPortTypeProxy();
    return masterDataMaterialInfoPortType_PortType.inquire(part);
  }
  
  
}