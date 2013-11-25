/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.suspend;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;

import my.ep.biz.service.ct.suspend.ISuspendActiveSO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ct.suspend.ContractSuspendReqDTO;
import my.ep.dto.ct.suspend.ContractSuspendResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.suspend.CtSuspendActive;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.ct.suspend.ContractSuspendManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;


/**
 * Backing Bean For Contract Suspension
 * 
 * @author Alam
 * @since 22/10/2013	 
 */
@ManagedBean(name = "contractSuspendBackingBean")
@RequestScoped
public class ContractSuspendBackingBean extends BaseBackingBean<ContractSuspendManagedBean>{

	private static Logger log = Logger.getLogger(ContractSuspendBackingBean.class);
	private static final String PROCESS_SUSPEND_PAGE="/xhtml/ct/fulfilmentdetails/suspension/suspend.xhtml";
	
	public ContractSuspendBackingBean(){
		
		/* this.getManagedBean().setSuspendLocTN(new DefaultTreeNode("root","", null));
		 TreeNode zone1 = new DefaultTreeNode(new ZoneDetails("Zone Tengah"), this.getManagedBean().getSuspendLocTN());  
	     TreeNode zone2 = new DefaultTreeNode(new ZoneDetails("Zone Selatan"), this.getManagedBean().getSuspendLocTN());  
	     new DefaultTreeNode(new ZoneDetails("PUTRAJAYA", " "), zone1);  
	     new DefaultTreeNode(new ZoneDetails("PERAK", " "), zone1);  
	     new DefaultTreeNode(new ZoneDetails("MELAKA", " "), zone1);  
	
	     new DefaultTreeNode(new ZoneDetails("SELANGOR", " "), zone2);  
	     new DefaultTreeNode(new ZoneDetails("JOHOR", " "), zone2);  
	     new DefaultTreeNode(new ZoneDetails("PAHANG", " "), zone2);  
	     new DefaultTreeNode(new ZoneDetails("MELAKA", " "), zone2);  */
		
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		
		if(this.getManagedBean().getOutComes()==null) {
			this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
		
		log.info("===================="+this.getManagedBean().getContract().getContractId());
	}
	
	@PostConstruct
	public void initializeSuspendValue(){
		
		if(this.getManagedBean().getSuspendActive() == null){
			this.getManagedBean().setSuspendActive(new CtSuspendActive());
			this.getManagedBean().getSuspendActive().setSuspendActiveDesc("This contract is suspended");
			this.getManagedBean().getSuspendActive().setSuspendFrom(new Date());
			this.getManagedBean().getSuspendActive().setSuspendTo(new Date());
			
			this.getManagedBean().getSuspendActive().setCtContract(new CtContract());
			this.getManagedBean().getSuspendActive().setApprover(new PmUser());
			this.getManagedBean().getSuspendActive().setStatus(new PmStatus());
			this.getManagedBean().getSuspendActive().getApprover().setUserId(new Long(1));
			this.getManagedBean().getSuspendActive().getStatus().setStatusId(new Long(1));
			this.getManagedBean().getSuspendActive().getCtContract().setContractId(1L);
		}
	}
	
	public String createSuspend(String action){
		
		ISuspendActiveSO suspendActiveSO = null;
		try{
			
		  ContractSuspendReqDTO reqDTO = new ContractSuspendReqDTO();
		  suspendActiveSO = (ISuspendActiveSO)BusinessDelegate.getInstance().getServiceObject("ct", ISuspendActiveSO.class);
		  reqDTO.setSuspendActive(this.getManagedBean().getSuspendActive());
		 
		  String token = getManagedBean().getToken();
			if (token == null) {
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
				token = bpmSO.authenticate("roslan", "welcome1");
				if (token == null) {
					throw new EpException("BPM token not found");
				}
			}

			reqDTO.setOutcome(action);
			if (action == BpmOutcomeConst.BPM_SAVE) {
				reqDTO.setOutcome("");
			}
			reqDTO.setToken(token);
			reqDTO.setTaskId(getManagedBean().getTaskId());
		  
		  ContractSuspendResDTO resDTO = suspendActiveSO.createSuspend(reqDTO);
		  if (resDTO.isSuccessful()) {
				
			  log.info("Record saved successfully");
			  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Name. : "+ this.getManagedBean().getSuspendActive().getSuspendActiveId()));
			  if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
			   }
		  } else {
				throw new EpException("Failed.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Contract save failed."));
		}finally{
			suspendActiveSO = null;
		}
		
		return null;
	}
	
	public String gotoSuspendPage() {	
	    return PROCESS_SUSPEND_PAGE;
    }
	
	public void onChange(TabChangeEvent event) { 
	 
		 System.out.println("tab id =============== " + event.getTab().getId());
		}
}
