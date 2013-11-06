/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.suspend;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;

import my.ep.biz.service.ct.suspend.ISuspendActiveSO;
import my.ep.dto.ct.suspend.ContractSuspendReqDTO;
import my.ep.dto.ct.suspend.ContractSuspendResDTO;
import my.ep.persistence.entity.ct.suspend.CtSuspendActive;
import my.ep.web.backingbean.BaseBackingBean;
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
		
		if(this.getManagedBean().getOutComes()==null) {
			this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
		
		log.info("===================="+this.getManagedBean().getContract().getContractId());
	}
	
	@PostConstruct
	public void initializeSuspendValue(){
		
		CtSuspendActive suspendActive = new CtSuspendActive();
		suspendActive.setSuspendActiveDesc("This contract is suspended");
		suspendActive.setSuspendFrom(new Date());
		suspendActive.setSuspendTo(new Date());
		this.getManagedBean().setSuspendActive(suspendActive);
		
	}
	
	public void createSuspend(String outcome){
		
		ISuspendActiveSO suspendActiveSO = null;
		try{
			
		  ContractSuspendReqDTO reqDTO = new ContractSuspendReqDTO();
		  suspendActiveSO = (ISuspendActiveSO)BusinessDelegate.getInstance().getServiceObject("ct", ISuspendActiveSO.class);
		  reqDTO.setSuspendActive(this.getManagedBean().getSuspendActive());
		  reqDTO.setOutcome(outcome);
		  reqDTO.setTaskId(this.getManagedBean().getTaskId());
		 
		  if(outcome == BpmOutcomeConst.BPM_SAVE){
		  reqDTO.setOutcome("");
		  
		  }else if(outcome == BpmOutcomeConst.BPM_REVISE)
		  reqDTO.setTaskId(this.getManagedBean().getTaskId());
		  System.out.println("Contract Id>>>:: "+ this.getManagedBean().getContract().getContractId()); 
		  this.getManagedBean().getSuspendActive().setCtContract(this.getManagedBean().getContract());
		  ContractSuspendResDTO resDTO = suspendActiveSO.createSuspend(reqDTO);
		  System.out.println("Save Successful>>>:: "+ resDTO.isSuccessful()); 
	
		}catch(Exception ex){
		   log.error("Exception>>>" +ex);
		}finally{
			suspendActiveSO = null;
		}
	}
	
	public String gotoSuspendPage() {	
	    return PROCESS_SUSPEND_PAGE;
    }
	
	public void onChange(TabChangeEvent event) { 
	 
		 System.out.println("tab id =============== " + event.getTab().getId());
		}
}
