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

import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgunit.IOrgUnitSO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitReqDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitResDTO;
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
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.fl.createcontract.ContractRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
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

@ManagedBean(name = "fulfilmentRequestBackingBean")
@RequestScoped
public class ContractRequestBackingBean extends BaseBackingBean<ContractRequestManagedBean>{
	private static Logger log = Logger.getLogger(ContractRequestBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";


	public ContractRequestBackingBean(){
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
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
			IFulfilmentRequestSO fulfilmentRequestSO = null;
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			ContractResDTO resDto = fulfilmentRequestSO.retrieveContractList();
			List<CtContract> contractList = resDto.getCtContracts();
			this.getManagedBean().setContractList(contractList);
		}
	}

	public void createFulfillmentRequest() throws ParseException {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			//			FlRemark flRemark = this.getManagedBean().getFlRemark();
			//			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			String fileName = this.getManagedBean().getFlAttachment().getFileName();
			String fileDesc = this.getManagedBean().getFlAttachment().getFileDescription();
			String filePath = this.getManagedBean().getFlAttachment().getFilePath();
			log.info("fileName in save Action::: " + fileName);
			//			flAttachment.setDocType("txt");
			//			flAttachment.setDocId(Long.valueOf(44));
			//			flAttachment.setFileName(fileName);
			//			flAttachment.setFileDescription(fileDesc);
			//			flAttachment.setFilePath(filePath);
			//			log.info("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			//			flRemark.setDocId(Long.valueOf(1));
			//			flRemark.setDocType("tex");
			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(IFulfilmentRequestSO.class);
			//			reqDTO.setFlRemark(flRemark);
			//			reqDTO.setFlAttachment(flAttachment);
			reqDTO.setFlFulfilmentRequest(flRequest);
			reqDTO.setContractRequest(true);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.createFulfillmentRequest(reqDTO);

			

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
		OrgUnitResDTO resDTO = new OrgUnitResDTO();
		IOrgUnitSO orgProfileSO = (IOrgUnitSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgUnitSO.class);
		OrgUnitReqDTO orgProfileReqDTO = new OrgUnitReqDTO();

		if(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId() != null){
			orgProfileReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			orgProfileReqDTO.setTypeCodeId(1L);
			resDTO = orgProfileSO.retrieveOrgProfileByParentIdAndType(orgProfileReqDTO);
		}

		if(resDTO.getPmorgProfileByParentList() != null && !resDTO.getPmorgProfileByParentList().isEmpty()){
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getPmorgProfileByParentList();
			for(PmOrgProfile obj : listDB){
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		

		return retrievePTJList;
	}

	public List<SelectItem> getDivisionProfileList(){
		List<SelectItem> retrieveDivisionList = new ArrayList<SelectItem>();
		IOrgUnitSO orgProfileSO = (IOrgUnitSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgUnitSO.class);
		OrgUnitReqDTO orgProfileReqDTO = new OrgUnitReqDTO();
		orgProfileReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getCreatedOrgProfile().getPmOrgProfile().getPmOrgProfile().getOrgProfileId());
		OrgUnitResDTO resDTO = orgProfileSO.retrieveDivisionByMinistryId(orgProfileReqDTO);
		if(resDTO.getPmOrgProfileByMinistryList() != null && !resDTO.getPmOrgProfileByMinistryList().isEmpty()){
			retrieveDivisionList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getPmOrgProfileByMinistryList();
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

		IOrgUnitSO orgProfileSO = (IOrgUnitSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgUnitSO.class);
		OrgUnitReqDTO orgProfileReqDTO = new OrgUnitReqDTO();
		orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
		OrgUnitResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);
		if(resDTO.getPmOrgProfileById() != null){
			getManagedBean().getFlRequest().setCreatedOrgProfile(resDTO.getPmOrgProfileById());
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

	public void submitFlRequest(){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		System.out.println("taskid bb: "+getManagedBean().getBpmTaskId());
		reqDTO.setTaskId(getManagedBean().getBpmTaskId());
		
		reqDTO.setCtxToken(ctx);

		reqDTO.setOutcome("SUBMIT");

		BpmResponseDTO res = bpmSO.updateTask(reqDTO);
		System.out.println(res.isSuccessful());
		System.out.println(res.getTaskId());
	}
	
	/**
	 * execute actions when button is clicked
	 * @param action
	 * 
	 */
	public void submitAction(String action) {
		if(ProcurementPlanConst.BPM_SUBMIT.equals(action)){
			submitFlRequest();
		}else if (ProcurementPlanConst.BPM_REVISE.equals(action)){
			
		}else if (ProcurementPlanConst.BPM_APPROVE.equals(action)){
			
		}else if (ProcurementPlanConst.BPM_SAVE.equals(action)){
			try {
				createFulfillmentRequest();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}

}
