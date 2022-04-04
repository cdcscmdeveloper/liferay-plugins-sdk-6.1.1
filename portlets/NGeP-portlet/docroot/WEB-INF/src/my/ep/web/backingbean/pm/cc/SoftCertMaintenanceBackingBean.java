package my.ep.web.backingbean.pm.cc;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.util.DigiCertHelper;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.TrustGateHelper;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

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
public class SoftCertMaintenanceBackingBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
			String identificationNo = getDigiCertUserId();
			String epNo = getSupplierEpNo();
			log.info("SECURITY IDNO:"+identificationNo);
			log.info("SECURITY EPNO:"+epNo);
			KMCClient client = getClient();
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.generateToken(identificationNo,epNo));
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
	
	public void RequestChangePIN() throws IOException, PortalException,
			SystemException {
		String epNo = getSupplierEpNo();
		String idNo = getDigiCertUserId();
		//#32705 - Start
		String certIssuer = PmCommonUtil.getTypeOfCertIssuer(idNo, epNo);
		//#32705 - End
		if (certIssuer.equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			TrustGateHelper.getInstance().changePIN(idNo, epNo);
		} else {
			DigiCertHelper.getInstance().changePIN(idNo, epNo);
		}
	}
	
	public void RequestChallRespUpdate() throws IOException, PortalException,
			SystemException {
		String epNo = getSupplierEpNo();
		String idNo = getDigiCertUserId();
		//#32705 - Start
		String certIssuer = PmCommonUtil.getTypeOfCertIssuer(idNo, epNo);
		//#32705 - End
		if (certIssuer.equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			TrustGateHelper.getInstance().updateChallResp(idNo, epNo);
		} else {
			DigiCertHelper.getInstance().updateChallResp(idNo, epNo);
		}
	}

	public void RequestResetPIN() throws IOException, PortalException,
			SystemException {
		String epNo = getSupplierEpNo();
		String idNo = getDigiCertUserId();
		//#32705 - Start
		String certIssuer = PmCommonUtil.getTypeOfCertIssuer(idNo, epNo);
		//#32705 - End
		if (certIssuer.equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			TrustGateHelper.getInstance().resetPIN(idNo, epNo,
					getSupplierEmail());
		} else {
			DigiCertHelper.getInstance().resetPIN(idNo, epNo,
					getSupplierEmail());
		}
	}

	public void RequestRevokeCert(String digiCertUserId, String supplierEpNo)
			throws IOException, PortalException, SystemException {
		String certIssuer = PmCommonUtil
				.getCertIssuerByIdentificationNoAndEpNo(digiCertUserId,
						supplierEpNo);
		if (certIssuer.equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			TrustGateHelper.getInstance().revoke(digiCertUserId, supplierEpNo);
		} else {
			DigiCertHelper.getInstance().revoke(digiCertUserId, supplierEpNo);
		}
	}
	

}
