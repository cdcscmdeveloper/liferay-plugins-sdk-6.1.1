package my.ep.web.backingbean.pm.cc;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.tls.Certificate;

import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.pm.userprofile.UserprofileBackingBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import com.augtech.kmc.client.AccountInfo;
import com.augtech.kmc.client.ContactDetail;
import com.augtech.kmc.client.IKMCAPIRequest;
import com.augtech.kmc.client.IKMCAPIResponse;
import com.augtech.kmc.client.IKMCAPIResponse.GenSecToken;
import com.augtech.kmc.client.IUploadBundle;
import com.augtech.kmc.client.KMCClient;
import com.augtech.kmc.client.KMCClientFactory;
import com.augtech.kmc.client.KMCRequestBuilder;
import com.augtech.kmc.client.OrganizationDetail;
import com.augtech.kmc.client.PersonalDetail;
import com.augtech.kmc.client.UploadBundle;

/**
 * @author Alvin
 * 
 */
@ManagedBean(name = "spkiBackingBean")
@RequestScoped
public class spkiBackingBean {
	
	private static List<String> challs = null;
	
	private String name = null;
	
	public spkiBackingBean() {}
	
	private static final Logger logger = EpLogger
			.getLogger(UserprofileBackingBean.class);

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
	public void getChallengeQuestions(){ 
	try{
			// Create property file
			File f = new File("kmc.properties");
			// Create KMCClient with the property file
			KMCClient client = KMCClientFactory.getInstance(f);

			// Request service for user selected question for authentication
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.getChallengeQuestions("781212141111", 1));
//			List<String> challs = null;
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				challs =  (List<String>) res.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);
				// show to the user to allow user answer the challenge
				for(int i = 0 ; i < challs.size() ; i ++)
				{
					System.out.println("Challange of "+(i+1)+" is : "+challs.get(i));
				}
				
				break;

				default:
				System.err.println("API return error. Code was "+res.getStatusCode()+". Message was : "+res.getStatusMessage());
				return;
			}

			if(challs.size() == 0)
			{
				System.err.println("No challanges found for the user! Data signing failed!");
				return;
			}
				
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}


}
