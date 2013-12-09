/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.supplier;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean For Supplier
 *  * 
 * @author Alam
 * @since 7/11/2013	 
 */
@ManagedBean(name = "supplierBackingBean")
@RequestScoped
public class SupplierBackingBean extends BaseBackingBean<SupplierManagedBean> {
	
	private static Logger log = Logger.getLogger(SupplierBackingBean.class);

	private static final String PROCESS_BANK_PAGE="/xhtml/ct/supplier/bank/tabView.xhtml";
	private static final String PROCESS_FACTORING_PAGE="/xhtml/ct/supplier/factoring/tabView.xhtml";
	private static final String PROCESS_TERMINATION_PAGE="/xhtml/ct/supplier/termination/tabView.xhtml";
	
	
	private Date date;
	private ContractListItem contractListItem;
    private CheckBoxSelections checkBoxSelections;
	

	public SupplierBackingBean(){
		
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());

		if(this.getManagedBean().getOutComes()==null) {
			this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
	}
	
	@PostConstruct
	public void init(){
			
		if(this.getManagedBean().getFactoring() == null){
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
			this.getManagedBean().setFactoring(new CtFactoring());
			this.getManagedBean().getFactoring().setCtContract(new CtContract());
			this.getManagedBean().getFactoring().setFinancialOrg(new PmFinancialOrg());
			this.getManagedBean().getFactoring().setContractAdmin(new PmUser());
			this.getManagedBean().getFactoring().setSmSupplier(new SmSupplier());
			this.getManagedBean().getFactoring().setStatus(new PmStatus());
			
			this.getManagedBean().getFactoring().getCtContract().setContractId(1L);
			this.getManagedBean().getFactoring().getFinancialOrg().setFinancialOrgId(1L);
			this.getManagedBean().getFactoring().getContractAdmin().setUserId(27L);
			this.getManagedBean().getFactoring().getStatus().setStatusId(1L);
			this.getManagedBean().getFactoring().getSmSupplier().setSupplierId(4L);
			
			this.getManagedBean().getFactoring().setAuthRefNo("123-09234-76453");
			this.getManagedBean().getFactoring().setEffDate(new Date());
			this.getManagedBean().getFactoring().setExpDate(new Date());
		}
		
		if(this.getManagedBean().getCtTerminationReason() == null){
			
			this.getManagedBean().setCtTerminationReason(new CtTerminationReason());
			this.getManagedBean().getCtTerminationReason().setReasonType(new PmCode());
			this.getManagedBean().getCtTerminationReason().getReasonType().setCodeId(1L);
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
	
    public String saveFactoringRequest(String action){
    	IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try{
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
		   	 reqDTO.setFactoring(this.getManagedBean().getFactoring());
			
		   	String token = this.getManagedBean().getToken();
			if (token == null) {
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
				token = bpmSO.authenticate("khai", "welcome1");
				if (token == null) {
					throw new EpException("BPM token not found");
				}
			}

			reqDTO.setOutcome(action);
			if (action == BpmOutcomeConst.BPM_SAVE) {
				reqDTO.setOutcome("");
			}
			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());

			  
			 ContractSupplierResDTO resDTO = supplierSO.saveFactoringRequest(reqDTO);
			 if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Factoring Request. : "));
					if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
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
