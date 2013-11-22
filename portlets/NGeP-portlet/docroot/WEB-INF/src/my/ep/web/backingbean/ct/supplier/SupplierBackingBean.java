/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.supplier;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.bank.CtSupplierBank;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Backing Bean For Supplier
 *  * 
 * @author Alam
 * @since 7/11/2013	 
 */
@ManagedBean(name = SupplierBackingBean.FACE_BEAN_NAME)
@RequestScoped
public class SupplierBackingBean extends BaseBackingBean<SupplierManagedBean> {
	
	public static final String FACE_BEAN_NAME = "supplierBackingBean";
	private static Logger log = Logger.getLogger(SupplierBackingBean.class);

	private static final String PROCESS_BANK_PAGE="/xhtml/ct/supplier/bank/tabView.xhtml";
	private static final String PROCESS_FACTORING_PAGE="/xhtml/ct/supplier/factoring/tabView.xhtml";
	private static final String PROCESS_TERMINATION_PAGE="/xhtml/ct/supplier/termination/tabView.xhtml";
	
	private Date date;
	private ContractListItem contractListItem;
    private CheckBoxSelections checkBoxSelections;
	

	public SupplierBackingBean(){
		if(this.getManagedBean().getOutComes()==null) {
			this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
	}
	
	@PostConstruct
	public void init(){
		checkBoxSelections = new CheckBoxSelections();
		contractListItem = new ContractListItem();
		
		if(this.getManagedBean().getSupplierBank() == null){
			this.getManagedBean().setSupplierBank(new CtSupplierBank());
			this.getManagedBean().getSupplierBank().setCtContract(new CtContract());
			this.getManagedBean().getSupplierBank().setContractAdmin(new PmUser());
			this.getManagedBean().getSupplierBank().setBankAccount(new SmSupplierBank());
			this.getManagedBean().getSupplierBank().setStatus(new PmStatus());
			this.getManagedBean().getSupplierBank().setSupplier(new SmSupplier());
			
			this.getManagedBean().getSupplierBank().getCtContract().setContractId(1L);
			this.getManagedBean().getSupplierBank().getBankAccount().setSupplierBankId(2L);
			this.getManagedBean().getSupplierBank().getContractAdmin().setUserId(27L);
			this.getManagedBean().getSupplierBank().getStatus().setStatusId(1L);
			this.getManagedBean().getSupplierBank().getSupplier().setSupplierId(4L);
		
		}if(this.getManagedBean().getFactoring() == null){
			
			this.getManagedBean().setFactoring(new CtFactoring());
			this.getManagedBean().getFactoring().setCtContract(new CtContract());
			this.getManagedBean().getFactoring().setFactorOrgProfile(new PmOrgProfile());
			this.getManagedBean().getFactoring().setContractAdmin(new PmUser());
			this.getManagedBean().getFactoring().setSmSupplier(new SmSupplier());
			this.getManagedBean().getFactoring().setStatus(new PmStatus());
			
			this.getManagedBean().getFactoring().getCtContract().setContractId(1L);
			this.getManagedBean().getFactoring().getFactorOrgProfile().setOrgProfileId(1L);
			this.getManagedBean().getFactoring().getContractAdmin().setUserId(27L);
			this.getManagedBean().getFactoring().getStatus().setStatusId(1L);
			this.getManagedBean().getFactoring().getSmSupplier().setSupplierId(4L);
			
			this.getManagedBean().getFactoring().setAuthRefNo("123-09234-76453");
			this.getManagedBean().getFactoring().setEffDate(new Date());
			this.getManagedBean().getFactoring().setExpDate(new Date());
			this.getManagedBean().getFactoring().setQtLoaId(new Long(123456));
		
		}if(this.getManagedBean().getTermination() == null){
			
			this.getManagedBean().setTermination(new CtTermination());
			this.getManagedBean().getTermination().setCtContract(new CtContract());
			this.getManagedBean().getTermination().setStatus(new PmStatus());
			
			this.getManagedBean().getTermination().getCtContract().setContractId(1L);
			this.getManagedBean().getTermination().getStatus().setStatusId(1L);
			this.getManagedBean().getTermination().setMutualTerminate(true);
			this.getManagedBean().getTermination().setCaRecommendationCode(new PmCode());
			this.getManagedBean().getTermination().getCaRecommendationCode().setCodeId(1L);
			
		}
		
		if(this.getManagedBean().getCtTerminationReason() == null){
			   this.getManagedBean().setCtTerminationReason(new CtTerminationReason());
			   this.getManagedBean().getCtTerminationReason().setTypeCode(new PmCode());
			   this.getManagedBean().getTermination().setCtTerminationReasons(new ArrayList<CtTerminationReason>());
			   this.getManagedBean().getCtTerminationReason().setCtTermination(this.getManagedBean().getTermination());
			   this.getManagedBean().getCtTerminationReason().getTypeCode().setCodeId(155L);
			}
	}
	
    public void searchContract() {
		
    	log.info("searchAction starts");
    	IContractSO contractSO = null;
		ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
		SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
		
		if(this.getManagedBean().getLoaNo()!=null) {
		    equalFilterBean.setSearchField("qtLoaNo");
		    equalFilterBean.setSearchValue(this.getManagedBean().getLoaNo());
		    searchBean.getDataFilterBeans().add(equalFilterBean);
		
		}else if(this.getManagedBean().getContractName()!=null) {
		    equalFilterBean.setSearchField("contractName");
		    equalFilterBean.setSearchValue(getManagedBean().getContractName());
		    searchBean.getDataFilterBeans().add(equalFilterBean);
	    }

		try {
			
	   	    contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
		    ContractReqDTO reqDTO = new ContractReqDTO();
			// reqDTO.setContractSearchBean(super.createSearchBean(CtContract.class, "supTable")); 
		    reqDTO.setContractSearchBean(searchBean);
			ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		 }catch (Exception ex) {
			log.error("Exception>>", ex);
		
	     }finally {
			contractSO = null;
	     }
	}
    
    public String saveChangeBankRequest(String outcome){
    	
    	IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		
		try{
			
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
		   	 reqDTO.setSupplierBank(this.getManagedBean().getSupplierBank());
			
		   	 reqDTO.setOutcome(outcome);
			 reqDTO.setTaskId(this.getManagedBean().getTaskId());
			 
			 if(outcome == BpmOutcomeConst.BPM_SAVE){
			      reqDTO.setOutcome("");
			  
			 }else if(outcome == BpmOutcomeConst.BPM_REVISE)
			      reqDTO.setTaskId(this.getManagedBean().getTaskId());
			  
			 ContractSupplierResDTO resDTO = supplierSO.saveChangeBankRequest(reqDTO); 
			 if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Change Bank A/C. : "));
					if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
						renewSesionObject();
						return "/xhtml/shared/acknowledge.xhtml";
					}
				} else {
					throw new EpException("Failed.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Change Bank A/C save failed."));
			}finally{
			supplierSO = null;
			reqDTO = null;
		}	
		
		return null;
    }
	
    public String saveFactoringRequest(String outcome){
    	

    	IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try{
			
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
		   	 reqDTO.setFactoring(this.getManagedBean().getFactoring());
			
		   	 reqDTO.setOutcome(outcome);
			 reqDTO.setTaskId(this.getManagedBean().getTaskId());
			 
			 if(outcome == BpmOutcomeConst.BPM_SAVE){
			    reqDTO.setOutcome("");
			  
			 }else if(outcome == BpmOutcomeConst.BPM_REVISE)
			   reqDTO.setTaskId(this.getManagedBean().getTaskId());
			  
			 ContractSupplierResDTO resDTO = supplierSO.saveFactoringRequest(reqDTO);
			 if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Factoring Request. : "));
					if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
						renewSesionObject();
						return "/xhtml/shared/acknowledge.xhtml";
					}
				} else {
					throw new EpException("Failed.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Factoring Request save failed."));
			}finally{
			supplierSO = null;
			reqDTO = null;
		}
		
		return null;
	}
    
    public String saveTerminationRequest(String outcome){
    	
    	System.out.println("inside supplier termination Log >>>>>>>>>>>>>>>>>>");
    	IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try{
			
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
			 this.getManagedBean().getTermination().getCtTerminationReasons().add(this.getManagedBean().getCtTerminationReason());
			 this.getManagedBean().getTermination().getCaRecommendationCode().setCodeId(155L);
			 reqDTO.setTerminationRequest(this.getManagedBean().getTermination());
			
		   	 reqDTO.setOutcome(outcome);
			 reqDTO.setTaskId(this.getManagedBean().getTaskId());
			 
			 if(outcome == BpmOutcomeConst.BPM_SAVE)
			   reqDTO.setOutcome("");
			 else if(outcome == BpmOutcomeConst.BPM_REVISE)
			   reqDTO.setTaskId(this.getManagedBean().getTaskId());
			  
			 ContractSupplierResDTO resDTO = supplierSO.saveTerminationRequest(reqDTO);
			 if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Contract Termination. : "));
					if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
						renewSesionObject();
						return "/xhtml/shared/acknowledge.xhtml";
					}
				} else {
					throw new EpException("Failed.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Termination save failed."));
			}finally{
	 
		   supplierSO = null;
		   reqDTO = null;
		}
		
		return null;
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public ContractListItem getContractListItem() {
		return contractListItem;
	}

	public void setContractListItem(ContractListItem contractListItem) {
		this.contractListItem = contractListItem;
	}

	public CheckBoxSelections getCheckBoxSelections() {
		return checkBoxSelections;
	}

	public void setCheckBoxSelections(CheckBoxSelections checkBoxSelections) {
		this.checkBoxSelections = checkBoxSelections;
	}
	
	public String gotoBankPage() {	
		return PROCESS_BANK_PAGE;
	}
	
	public String gotoFactoringPage() {	
		return PROCESS_FACTORING_PAGE;
	}
	
	public String gotoTerminationPage() {	
		return PROCESS_TERMINATION_PAGE;
	}

}
