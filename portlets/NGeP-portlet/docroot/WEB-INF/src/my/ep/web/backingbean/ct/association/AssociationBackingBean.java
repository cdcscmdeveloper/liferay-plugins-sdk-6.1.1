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

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAssociationSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAssociationReqDTO;
import my.ep.dto.ct.maintain.ContractAssociationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.assoc.CtAssocContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.association.AssociationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
   private static final String PROCESS_ASSOCIATE_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectCreation.xhtml";
   private static final String PROCESS_ASSOCIATE_CANCEL_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectAssociation.xhtml";

   private CtContract[] selectedContractList;
   
   public AssociationBackingBean(){
	   
   }
   
   @PostConstruct
   public void init(){
	   
	   if(this.getManagedBean().getCtAssociation() == null)
		   this.getManagedBean().setCtAssociation(new CtAssociation());
   }
   
   public void searchAssociation() {
		
		IContractAssociationSO contractAssociationSO = null;
		ISearchBean<CtAssocContract> searchBean = new SearchBean<CtAssocContract>();

		try {
			
			if(this.getManagedBean().getProjectName()!=null && this.getManagedBean().getProjectName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssociation.projectName",	SearchOperator.OPERATOR_EQUALS.value(),
				getManagedBean().getProjectName()));	
			}
			if(this.getManagedBean().getCtName()!=null && this.getManagedBean().getCtName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.contractName",SearchOperator.OPERATOR_LIKE.value(),
				this.getManagedBean().getCtName()));	
			}
			if(this.getManagedBean().getProjectDescription()!=null && this.getManagedBean().getProjectDescription().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssociation.description",SearchOperator.OPERATOR_LIKE.value(),
				getManagedBean().getProjectDescription()));	
			}			
			if(getManagedBean().getCtSupplierName()!=null && getManagedBean().getCtSupplierName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.SmSupplier.supplierName",SearchOperator.OPERATOR_EQUALS.value(),
				getManagedBean().getCtSupplierName()));	
			}
			if(this.getManagedBean().getCtDateStart()!=null && this.getManagedBean().getCtDateStart().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.latestContractVer.ctContractValue.effDate",
				SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getCtDateStart()));	
			}			
			if(getManagedBean().getCtDateEnd()!=null && getManagedBean().getCtDateEnd().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.latestContractVer.ctContractValue.expDate",
				SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getCtDateEnd()));	
			}	
			if(getManagedBean().getCtProductCode()!=null && getManagedBean().getCtProductCode().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.latestContractVer.ctContractValue.contractPhysicalNo",
				SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getCtProductCode()));	
			}	

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setSearchBean(super.createSearchBean(CtAssocContract.class,"associateSearch"));
			reqDTO.setSearchBean(searchBean);
			ContractAssociationResDTO ResDTO = contractAssociationSO.searchAssociation(reqDTO);
			this.getManagedBean().setAssociationList(ResDTO.getResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);

		} finally {
			contractAssociationSO = null;
			searchBean = null;
		} 
	 }
   
     public void deleteAssociation() {

		IContractAssociationSO contractAssociationSO = null;

		try {

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setCtDelAssocContract(this.getManagedBean().getCtDelAssocContract());
			contractAssociationSO.deleteAssociation(reqDTO);
		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractAssociationSO = null;
		} 
	 }
     
     public void addAdvanceContract() {

 		for (int i = 0; i < selectedContractList.length; i++) {
 			CtContract ad = selectedContractList[i];
 			this.getManagedBean().getAssociateSearchList().add(ad);
 		}
 		selectedContractList = null;
 	}

 	public void deleteAdvanceContract() {
 		this.getManagedBean().getAssociateSearchList().remove(this.getManagedBean().getAssocoiateCtContract());
 	}
 	
 	public String createAssociation() {

 		IContractAssociationSO contractAssociationSO = null;
         List<CtAssocContract> contractAssociationList = new ArrayList<CtAssocContract>();
         CtAssocContract ctAssocContract = new CtAssocContract();
 		try {
               for(CtContract contract : this.getManagedBean().getAssociateSearchList()){
             	  ctAssocContract.setCtContract(contract);
             	  ctAssocContract.setCtAssociation(this.getManagedBean().getCtAssociation());
             	  contractAssociationList.add(ctAssocContract);  
               }
               
             this.getManagedBean().getCtAssociation().setCtAssocContracts(contractAssociationList);
 			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
 			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
 			reqDTO.setAssociation(this.getManagedBean().getCtAssociation());
 			ContractAssociationResDTO resDTO = contractAssociationSO.createAssociation(reqDTO);
 			if (resDTO.isSuccessful()) {
 				
 				  log.info("Record saved successfully");
 				  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Association : "+ 
 				   this.getManagedBean().getCtAssociation().getProjectName()));
 					
 			  } else {
 					throw new EpException("Failed.");
 				}

 		} catch (Exception ex) {
 			log.error("Exception>>>" + ex);
 			ex.printStackTrace();
 			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.toString(), ex.getMessage()));
 		} finally {
 			contractAssociationSO = null;
 			contractAssociationList = null;
 			ctAssocContract = null;
 		}
        return null;
 	}
 	
 	/*Implementation of search methode*/
	public void searchContract() {

		ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
		if(this.getManagedBean().getCtNumber()!=null && this.getManagedBean().getCtNumber().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.docNo",SearchOperator.OPERATOR_EQUALS.value(),
			getManagedBean().getCtNumber()));	
		}
				
		IContractSO contractSO = null;
		try {

			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(super.createSearchBean(CtContract.class, "dataTable"));
			reqDTO.setContractSearchBean(searchBean);
			ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractSO = null;
			
		}
	}
	
 	
 	public CtContract[] getSelectedContractList() {
		return selectedContractList;
	}

	public void setSelectedContractList(CtContract[] selectedContractList) {
		this.selectedContractList = selectedContractList;
	}
    
    public String gotoAssociatePage() {
		return PROCESS_ASSOCIATE_PAGE;
	}
   
    public String gotoCancelPage() {
		return PROCESS_ASSOCIATE_CANCEL_PAGE;
	}	
	
}
