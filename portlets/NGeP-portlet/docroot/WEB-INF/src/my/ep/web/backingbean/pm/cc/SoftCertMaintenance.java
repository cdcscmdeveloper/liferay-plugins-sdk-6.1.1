package my.ep.web.backingbean.pm.cc;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import com.augtech.kmc.client.IKMCAPIResponse;
import com.augtech.kmc.client.IKMCAPIResponse.GenSecToken;
import com.augtech.kmc.client.KMCClient;
import com.augtech.kmc.client.KMCClientFactory;
import com.augtech.kmc.client.KMCRequestBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Alvin
 * 
 */
@ManagedBean(name = "spkiBackingBean")
@SessionScoped
public class SoftCertMaintenance {
	
    FacesScopes facesScope = new FacesScopes();

	public SoftCertMaintenance() {}
	
	private static final Logger logger = EpLogger
			.getLogger(SoftCertMaintenance.class);

	public String generateSecurityToken(){
		File f = new File("kmc.properties");
		String tok = null;
		try{
			KMCClient client = KMCClientFactory.getInstance(f);
			
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.generateToken(getDigiCertUserId(),getSupplierEpNo()));
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				tok = (String) res.get(GenSecToken.TOKEN);
				System.out.println("Token is "+tok);
				break;

			default:
				System.err.println("API return error. Message was : "+res.getStatusMessage());
			}
//			assertEquals(IKMCAPIResponse.OPERATION_SUCCESSFUL,res.getStatusCode());
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
//			fail(ex.getMessage());
		}
		
		return tok;
	}
	
	public String getDigiCertUserId() {
				
		return facesScope.getUserSessionBean().getIdentificationNo();	
	}

	public String getSupplierEmail() {
		
		return facesScope.getUserSessionBean().getEmail();
	}

	public String getSupplierEpNo() {
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
	
	public void RequestRevokeCert() throws IOException, PortalException, SystemException {
	    String token = generateSecurityToken(); 
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url4 = "https://kmcep-dev.digicert.com.my/kmc/RequestRevokeCert.aspx?userID=" + getDigiCertUserId() + "&orgid=" + getSupplierEpNo() + "&token=" + encodedtoken;
		externalContext.redirect(url4);
		System.out.println("Encoded encodedtoken " + encodedtoken);
		System.out.println("Encoded URL " + url4);	 
	}
	

}
