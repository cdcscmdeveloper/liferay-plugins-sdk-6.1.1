package my.ep.web.backingbean.pm.cc;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.augtech.kmc.client.IKMCAPIResponse;
import com.augtech.kmc.client.IKMCAPIResponse.GenSecToken;
import com.augtech.kmc.client.KMCAPIException;
import com.augtech.kmc.client.KMCClient;
import com.augtech.kmc.client.KMCClientFactory;
import com.augtech.kmc.client.KMCRequestBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Alvin
 * 
 */
@ManagedBean(name = "softCertMaintenanceBackingBean")
@SessionScoped
public class SoftCertMaintenanceBackingBean {
	public static final String PATH = "NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties";

	public SoftCertMaintenanceBackingBean() {}
	
	private static final Logger log = EpLogger
			.getLogger(SoftCertMaintenanceBackingBean.class);
	
	private KMCClient getClient() throws KMCAPIException {
		URL resource = getClass().getResource(".");
		String path = resource.getPath();
		System.out.println("path value with (.) :"+path);
		
		String substring = path.substring(0,path.indexOf("NGeP-portlet.war"));
		String filePath = substring+PATH;
		System.out.println("filePath :"+filePath);
		
		// Create property file
		File f = new File(filePath);
//		File f = new File("C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties");
		// Create KMCClient with the property file
		KMCClient client = KMCClientFactory.getInstance(f);
		return client;
		
	}
	public String generateSecurityToken(){
		String tok = null;
		try{
			KMCClient client = getClient();
			
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.generateToken(getDigiCertUserId(),getSupplierEpNo()));
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				tok = (String) res.get(GenSecToken.TOKEN);
				log.info(">>>>>>>TOKEN:"+tok+"<<<<<<<<<");
				break;

			default:
				log.info(">>>>>>>>>>API return error. Message was : "+res.getStatusMessage());
			}
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw new EpException(ex.getMessage());
		}
		
		return tok;
	}
	
	public String getDigiCertUserId() {
		FacesScopes facesScope = new FacesScopes();
		PmUser user  = facesScope.getUserSessionBean(); 		
		return user.getIdentificationNo();	
	}

	public String getSupplierEmail() {
		FacesScopes facesScope = new FacesScopes();
		PmUser user  = facesScope.getUserSessionBean();
		return user.getEmail();
	}

	public String getSupplierEpNo() {
		FacesScopes facesScope = new FacesScopes();
		ISupplierSearchSO smSupplierSO = null;
		System.out.println("ISearchResultBean<SmSupplier> pageChangeListener");
		smSupplierSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISupplierSearchSO.class);
	    Long userId = facesScope.getUserSessionBean().getUserId();

		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setUserId(userId);
		SupplierSearchResDTO res = smSupplierSO.retriveSupplierProfiles(reqDTO);

		return res.getResultBean().getResultList().get(0).getEpNo();	
	}
	
	public void RequestChangePIN() throws IOException, PortalException, SystemException {
	    String token = generateSecurityToken(); 
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url = "https://kmcep-dev.digicert.com.my/kmc/RequestChangePIN.aspx?userID=" + getDigiCertUserId() + "&token=" + encodedtoken;
		externalContext.redirect(url);
		System.out.println("Encoded encodedtoken " + encodedtoken);
		System.out.println("Encoded URL " + url);	 
	}
	
	public void RequestChallRespUpdate() throws IOException, PortalException, SystemException {
	    String token = generateSecurityToken(); 
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url2 = "https://kmcep-dev.digicert.com.my/kmc/RequestChallRespUpdate.aspx?userID=" + getDigiCertUserId() + "&token=" + encodedtoken;
		externalContext.redirect(url2);
		System.out.println("Encoded encodedtoken " + encodedtoken);
		System.out.println("Encoded URL " + url2);	 
	}
	
	public void RequestResetPIN() throws IOException, PortalException, SystemException {
	    String token = generateSecurityToken(); 
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url3 = "https://kmcep-dev.digicert.com.my/kmc/RequestResetPIN.aspx?userID=" + getDigiCertUserId() + "&email=" + getSupplierEmail() + "&token=" + encodedtoken;
		externalContext.redirect(url3);
		System.out.println("Encoded encodedtoken " + encodedtoken);
		System.out.println("Encoded URL " + url3);	 
	}
	
	public void RequestRevokeCert(String digiCertUserId, String supplierEpNo) throws IOException, PortalException, SystemException {
	    String token = generateSecurityToken(); 
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url4 = "https://kmcep-dev.digicert.com.my/kmc/RequestRevokeCert.aspx?userID=" + digiCertUserId + "&orgid=" + supplierEpNo + "&token=" + encodedtoken;
		externalContext.redirect(url4);
		log.info(">>>>>>>>>>>>>>>>>DigicertUserId:"+digiCertUserId);
		log.info(">>>>>>>>>>>>>>>>>OrgId:"+supplierEpNo);
		log.info(">>>>>>>>>>>>>>>>>Token:"+encodedtoken);
		log.info(">>>>>>>>>>>>>>>>>Encoded URL " + url4);	 
	}
	

}
