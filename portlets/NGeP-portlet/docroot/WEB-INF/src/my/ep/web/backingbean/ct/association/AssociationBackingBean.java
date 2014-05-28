/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.association;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAssociationSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAssociationReqDTO;
import my.ep.dto.ct.maintain.ContractAssociationResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.assoc.CtAssocContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.ct.association.AssociationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean For Contract Association
 * 
 * @author Alam
 * @since 29/01/2014
 */
@ManagedBean (name = "associationBackingBean")
@RequestScoped
public class AssociationBackingBean extends BaseBackingBean<AssociationManagedBean> {

   private static Logger log = Logger.getLogger(AssociationBackingBean.class);
   private static final String PROCESS_ASSOCIATE_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectCreation.xhtml?faces-redirect=true";
   private static final String PROCESS_ASSOCIATE_CANCEL_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectAssociation.xhtml?faces-redirect=true";

   private CtContract[] selectedContractList;
   private CtContract[] deleteSelectedContractList;
   private CtAssociation[] ctSelectAssociation;
   
   public AssociationBackingBean(){
	   
   }
   
   @PostConstruct
   public void init(){
	   
	   if(this.getManagedBean().getCtAssociation() == null)
		   this.getManagedBean().setCtAssociation(new CtAssociation());
   }
   
   public void searchAssociation() {IContractAssociationSO contractAssociationSO = null;
		ISearchBean<CtAssociation> searchBean = new SearchBean<CtAssociation>();

		try {
			
			if(this.getManagedBean().getProjectName()!=null && this.getManagedBean().getProjectName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("projectName",
				SearchOperator.OPERATOR_LIKE.value(), this.getManagedBean().getProjectName()));	
			}
			if(this.getManagedBean().getCtName()!=null && this.getManagedBean().getCtName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.contractName",
				SearchOperator.OPERATOR_LIKE.value(), this.getManagedBean().getCtName()));	
			}
			if(this.getManagedBean().getProjectDescription()!=null && this.getManagedBean().getProjectDescription().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("description",
				SearchOperator.OPERATOR_LIKE.value(), this.getManagedBean().getProjectDescription()));	
			}			
			if(this.getManagedBean().getCtSupplierName()!=null && this.getManagedBean().getCtSupplierName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.SmSupplier.supplierName",
				SearchOperator.OPERATOR_LIKE.value(), this.getManagedBean().getCtSupplierName()));	
			}
			if(this.getManagedBean().getCtDateStart()!=null && this.getManagedBean().getCtDateEnd()!=null) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.ctContractValue.effDate",
				SearchOperator.OPERATOR_GREATER_EQUAL.value(), this.getManagedBean().getCtDateStart()));	
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.ctContractValue.expDate",
				SearchOperator.OPERATOR_LESS_EQUAL.value(), this.getManagedBean().getCtDateEnd()));	
				
			}
			if(this.getManagedBean().getCtProductCode()!=null && this.getManagedBean().getCtProductCode().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.contractPhysicalNo",
				SearchOperator.OPERATOR_LIKE.value(), this.getManagedBean().getCtProductCode()));	
			}
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAssociationSO.class);
			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			reqDTO.setSearchBean(searchBean);
			ContractAssociationResDTO resDTO = contractAssociationSO.searchAssociation(reqDTO);
			this.getManagedBean().setAssociationList(resDTO.getResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);

		} finally {
			contractAssociationSO = null;
			searchBean = null;
		}
   }
   
   public int getAssocContractCount(Long associationId) {
		ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
		IContractAssociationSO contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAssociationSO.class);
		reqDTO.setAssociationId(associationId);
		ContractAssociationResDTO resDTO = contractAssociationSO.retrieveAssociation(reqDTO);
		
		return resDTO.getCtAssociation().getCtAssocContracts().size();
	}
   
     public void deleteAssociation() {

		IContractAssociationSO contractAssociationSO = null;
		ContractAssociationReqDTO reqDTO = null;
		
		try {
			 reqDTO = new ContractAssociationReqDTO();
			 contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAssociationSO.class);
				
			 for(int i = 0; i < ctSelectAssociation.length; i++) {
				 CtAssociation ctAssociation  = ctSelectAssociation[i];   
				 reqDTO.setCtDelAssociation(ctAssociation);
				 ContractAssociationResDTO resDTO= contractAssociationSO.deleteAssociation(reqDTO);
				 if(resDTO.isSuccessful()){
					 searchAssociation();
				 }
			 }
	 	 } catch (Exception ex) {
			log.error("Exception>>", ex); 
		} finally {
			contractAssociationSO = null;
		} 
	 }
     
     public void addAdvanceContract() {

 		for (int i = 0; i < selectedContractList.length; i++) {
 			CtContract ctContract = selectedContractList[i];
 			if (!this.getManagedBean().getAssociateSearchList().contains(ctContract))
 				this.getManagedBean().getAssociateSearchList().add(ctContract);
 		}
 		selectedContractList = null;
	}
     

 	public void deleteAdvanceContract() {
 		for (int i = 0; i < deleteSelectedContractList.length; i++) {
			CtContract ctContract = deleteSelectedContractList[i];
			this.getManagedBean().getAssociateSearchList().remove(ctContract);
		}
 		deleteSelectedContractList = null;
 	}
 	
 	public String createAssociation() {
		IContractAssociationSO contractAssociationSO = null;
		try {
			CtAssocContract ctAssocContract = new CtAssocContract();
			List<CtAssocContract> contractAssociationList = new ArrayList<CtAssocContract>();
 			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAssociationSO.class);
			for (CtContract contract : this.getManagedBean().getAssociateSearchList()) {
				ctAssocContract = new CtAssocContract();
				ctAssocContract.setCtContract(contract);
				ctAssocContract.setCtAssociation(this.getManagedBean().getCtAssociation());
				contractAssociationList.add(ctAssocContract);
			}
               
            this.getManagedBean().getCtAssociation().setCtAssocContracts(contractAssociationList);
 			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
 			reqDTO.setAssociation(this.getManagedBean().getCtAssociation());
 			ContractAssociationResDTO resDTO = contractAssociationSO.createAssociation(reqDTO);
 			if (resDTO.isSuccessful()) {
 				log.info("Record saved successfully");
 				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
 							MessageUtils.getMessage(MessageConst.INFO_700004, new String[]{"Project", getManagedBean().getCtAssociation().getProjectName()}, 
 							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()))); 				  
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
 			} 
		} finally {
			contractAssociationSO = null;
		}
		return null;
	}
 	
 	/*Implementation of search methode*/
	public void searchContract() {

		ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
		IContractSO contractSO = null;
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.CONTRACT_TYPE);
			parameterReqDTO.setParameterCode(ParameterConst.CONTRACT_TYPE_MINISTRY);
			ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
			
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType", SearchOperator.OPERATOR_EQUALS.value(), parameterResDTO.getPmParameter()));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.contractVerId",SearchOperator.OPERATOR_IS_NOT_NULL.value(), null));
			
			if(this.getManagedBean().getCtNumber()!=null && this.getManagedBean().getCtNumber().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractNo", SearchOperator.OPERATOR_LIKE.value(), this.getManagedBean().getCtNumber()));
			}
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(searchBean);
			reqDTO.setFilterByAssocContract(true);
			ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractSO = null;
			
		}
	}
	
	public CtAssociation[] getCtSelectAssociation() {
		return ctSelectAssociation;
	}

	public void setCtSelectAssociation(CtAssociation[] ctSelectAssociation) {
		this.ctSelectAssociation = ctSelectAssociation;
	}

	public CtContract[] getSelectedContractList() {
		return selectedContractList;
	}

	public void setSelectedContractList(CtContract[] selectedContractList) {
		this.selectedContractList = selectedContractList;
	}
    
	public String gotoAssociatePage() {
		if (this.getManagedBean().getCtAssociation() != null)
			this.getManagedBean().setCtAssociation(null);
		if (this.getManagedBean().getAssociateSearchList() != null && this.getManagedBean().getAssociateSearchList().size() != 0)
			this.getManagedBean().setAssociateSearchList(null);
		return PROCESS_ASSOCIATE_PAGE;
	}
   
    public String gotoCancelPage() {
		return PROCESS_ASSOCIATE_CANCEL_PAGE;
	}	
    
    public String gotoUpdateAssociatePage(){
    	
    	IContractAssociationSO contractAssociationSO = null;
		ContractAssociationReqDTO reqDTO = null;
		this.getManagedBean().setAssociateSearchList(null);
		try {
            
			 reqDTO = new ContractAssociationReqDTO();
			 contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAssociationSO.class);
			 reqDTO.setAssociationId(this.getManagedBean().getCtUpdateAssociation().getAssociationId());
			 ContractAssociationResDTO resDTO = contractAssociationSO.retrieveAssociation(reqDTO);
			 this.getManagedBean().setCtAssociation(resDTO.getCtAssociation());
			 for(CtAssocContract ctAssocContract : this.getManagedBean().getCtAssociation().getCtAssocContracts()){
				 this.getManagedBean().getAssociateSearchList().add(ctAssocContract.getCtContract());
			 }
		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractAssociationSO = null;
			
		}
    	return PROCESS_ASSOCIATE_PAGE;
    }

	public CtContract[] getDeleteSelectedContractList() {
		return deleteSelectedContractList;
	}

	public void setDeleteSelectedContractList(
			CtContract[] deleteSelectedContractList) {
		this.deleteSelectedContractList = deleteSelectedContractList;
	}
	
	public void searchReset(){
		this.getManagedBean().setProjectName(null);
		this.getManagedBean().setCtName(null);	
		this.getManagedBean().setProjectDescription(null);	
		this.getManagedBean().setCtSupplierName(null);
		this.getManagedBean().setCtProductCode(null);
		this.getManagedBean().setCtDateStart(null);	
		this.getManagedBean().setCtDateEnd(null);		
	}
}
