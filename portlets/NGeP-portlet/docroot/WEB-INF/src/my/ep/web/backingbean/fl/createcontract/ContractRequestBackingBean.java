/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.createcontract;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.maksud.MaksudReqDTO;
import my.ep.dto.pm.common.maksud.MaksudResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.createcontract.ContractRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * Backing Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */

@ManagedBean(name = "contractRequestBackingBean")
@RequestScoped
public class ContractRequestBackingBean extends BaseBackingBean<ContractRequestManagedBean>{
	private static Logger log = Logger.getLogger(ContractRequestBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";


	public ContractRequestBackingBean(){
	}

	@PostConstruct
	public void init(){
		if(getManagedBean().getFlRequest() == null){
			System.out.println("==post contruct==");
			getManagedBean().setFlRequest(new FlFulfilmentRequest());
			getManagedBean().getFlRequest().setPmDocStatus(new PmStatus());
			getManagedBean().getFlRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setReceivedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCreatedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCtContract(new CtContract());
			getManagedBean().getFlRequest().getCtContract().setContractTypeCode(new PmCode());
			getManagedBean().getFlRequest().getCtContract().setFulfillmentTypeCode(new PmCode());
			getManagedBean().getFlRequest().getCtContract().setSmSupplier(new SmSupplier());
			getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
		}

		//onclick or trigger from the liferay menu page
		FacesContext fCtx = FacesContext.getCurrentInstance();
		PortletRequest req = (PortletRequest) fCtx.getExternalContext()
				.getRequest();
		ThemeDisplay themeDisplay = (ThemeDisplay) req
				.getAttribute(WebKeys.THEME_DISPLAY);
		Layout layout = themeDisplay.getLayout();

		if (layout.getFriendlyURL().equals("/view-contract-list")) {
			System.out.println("---view-contract-list---");
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO contractReq = new ContractReqDTO();
			contractReq.setOrgProfileId(4L);
			ContractResDTO resDTO = contractSO.retrieveActiveContractList(contractReq);
			List<CtContract> contractList = resDTO.getCtContracts();
			this.getManagedBean().setContractList(contractList);
		}
	}

	public void submitAction(String action) {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		IFulfilmentOrderSO flOrderSO = null;
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			//			FlRemark flRemark = this.getManagedBean().getFlRemark();//temporary comment for attachment n remark
			//			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
//			String fileName = this.getManagedBean().getFlAttachment().getFileName();
//			String fileDesc = this.getManagedBean().getFlAttachment().getFileDescription();
//			String filePath = this.getManagedBean().getFlAttachment().getFilePath();
			//			flAttachment.setDocType("txt");
			//			flAttachment.setDocId(Long.valueOf(44));
			//			flAttachment.setFileName(fileName);
			//			flAttachment.setFileDescription(fileDesc);
			//			flAttachment.setFilePath(filePath);
			//			log.info("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			//			flRemark.setDocId(Long.valueOf(1));
			//			flRemark.setDocType("tex");
			//			reqDTO.setFlRemark(flRemark);
			//			reqDTO.setFlAttachment(flAttachment);
			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentRequestSO.class);
			reqDTO.setFlFulfilmentRequest(flRequest);
			reqDTO.setContractRequest(Boolean.TRUE);
			System.out.println("templogin: "+ getManagedBean().getTempLogin());
			if(getManagedBean().getTempLogin() == null){
				reqDTO.setTempLogin("roslan");
			}else{
				reqDTO.setTempLogin(getManagedBean().getTempLogin());
			}
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getBpmTaskId());
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)){
				flOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
				flOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			}else{
				iFulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			}
			

		}catch(Exception e){
			log.error("Exception>>", e);
		}finally {
			iFulfilmentRequestSO = null;
		}
	}

	public void fileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		log.info("Processed uploaded file " + fileName + " of size:"+ fileBytes.length);
		this.getManagedBean().getFlAttachment().setFileName(fileName);
		this.getManagedBean().getFlAttachment().setFileDescription("File uploded for Contract Req Order");
		this.getManagedBean().getFlAttachment().setFilePath("D:\\upload");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"The file was uploaded successfully.", null);
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
		FacesContext.getCurrentInstance().addMessage(null, msg);  
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("D:\\upload\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<SelectItem> getPTJList(){
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

		if(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId() != null){
			orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		}

		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		

		return retrievePTJList;
	}

	public List<SelectItem> getDivisionProfileList(){
		List<SelectItem> retrieveDivisionList = new ArrayList<SelectItem>();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFlRequest().getCreatedOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId());
		orgProfileReqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			retrieveDivisionList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrieveDivisionList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		

		return retrieveDivisionList;
	}

	public void removeAddRow(){
		getManagedBean().getFlRequest().getFlDeliveryAddresses().remove(getManagedBean().getSelectedAddrRow());
	}

	public void addDeliveryAddr(){
		for(PmOrgAddress pmOrgAddr : getManagedBean().getSelectedPmDelAddrList()){
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
			deliveryAddr.setOrgAddress(pmOrgAddr);
			deliveryAddr.setDeliveryAddressId(pmOrgAddr.getOrgAddressId());
			getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
		}
	}

	public void getActivityCode(){

	}

	public void flDeliveryAddrList(){
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setOrgProfileId(1L);
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);	
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		getManagedBean().setDelAddrList(resDTO.getOrgAddresses());

	}

	public String procureContract(){
		getManagedBean().getFlRequest().setCtContract(getManagedBean().getSelectedContract());

		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);
		if(resDTO.getPmOrgProfile() != null){
			getManagedBean().getFlRequest().setCreatedOrgProfile(resDTO.getPmOrgProfile());
		}
		this.getManagedBean().setActions(new String[]{"SAVE", "SUBMIT"}); 
		

		return NAV_TO_CREATE_CONTRACT_REQUEST;
	}

	public void selectedAddrRow(){
		getManagedBean().setSelectedRowFlg(true);
	}


	public void flDeliveryItemList(){
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setOrgProfileId(1L);
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);	
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		getManagedBean().setDelAddrList(resDTO.getOrgAddresses());

	}

	public List<SelectItem> getMaksudCodeList(){
		List<SelectItem> maksudList = new ArrayList<SelectItem>();
		MaksudReqDTO maksudDTO = new MaksudReqDTO();
		maksudDTO.setOrgProfileId(1L);

		IMaksudSO maksudSO = (IMaksudSO)BusinessDelegate.getInstance().getServiceObject("pm",IMaksudSO.class);	
		MaksudResDTO resDTO = maksudSO.retrieveMaksudCodeList(maksudDTO);
		if(resDTO.getMaksudCodeList() != null && !resDTO.getMaksudCodeList().isEmpty()){
			maksudList.add(new SelectItem("", "- Select One -"));
			List<PmMaksud> maksudListDB = resDTO.getMaksudCodeList();
			for(PmMaksud obj : maksudListDB){
				maksudList.add(new SelectItem(obj.getMaksudId(),obj.getMaksudCode()));
			}
		}		
		return maksudList;

	}

	public void checkPtjFlg(){
		getManagedBean().setCheckPtjFlg(false);
	}
	
	
}
