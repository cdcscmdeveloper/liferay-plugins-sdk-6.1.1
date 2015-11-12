/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.association;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
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
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.assoc.CtAssocContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
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
@ViewScoped
public class AssociationBackingBean extends BaseBackingBean<AssociationManagedBean> {
	
	private static Logger log = Logger.getLogger(AssociationBackingBean.class);
	private static final String PROCESS_ASSOCIATE_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectCreation.xhtml?faces-redirect=true";
	private static final String PROCESS_ASSOCIATE_CANCEL_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectAssociation.xhtml?faces-redirect=true";
	private CtContract[] selectedContractList;
	private CtContract[] deleteSelectedContractList;
	private CtAssociation[] ctSelectAssociation;
	private IPagingDataModel<CtAssociation> associationDataModel = new AssociationPagingDataModel();
	private IPagingDataModel<CtContract> contractDataModel = new ContractPagingDataModel();
	
	public AssociationBackingBean() {
	   
	}
   
	@PostConstruct
	public void init() {
		if(this.getManagedBean().getCtAssociation() == null) {
			this.getManagedBean().setCtAssociation(new CtAssociation());
		}
	}	
   
	/**
	 * @return the selectedContractList
	 */
	public CtContract[] getSelectedContractList() {
		return selectedContractList;
	}

	/**
	 * @param selectedContractList the selectedContractList to set
	 */
	public void setSelectedContractList(CtContract[] selectedContractList) {
		this.selectedContractList = selectedContractList;
	}

	/**
	 * @return the deleteSelectedContractList
	 */
	public CtContract[] getDeleteSelectedContractList() {
		return deleteSelectedContractList;
	}

	/**
	 * @param deleteSelectedContractList the deleteSelectedContractList to set
	 */
	public void setDeleteSelectedContractList(CtContract[] deleteSelectedContractList) {
		this.deleteSelectedContractList = deleteSelectedContractList;
	}

	/**
	 * @return the ctSelectAssociation
	 */
	public CtAssociation[] getCtSelectAssociation() {
		return ctSelectAssociation;
	}

	/**
	 * @param ctSelectAssociation the ctSelectAssociation to set
	 */
	public void setCtSelectAssociation(CtAssociation[] ctSelectAssociation) {
		this.ctSelectAssociation = ctSelectAssociation;
	}
	
	/**
	 * @return the associationDataModel
	 */
	public IPagingDataModel<CtAssociation> getAssociationDataModel() {
		return associationDataModel;
	}

	/**
	 * @param associationDataModel the associationDataModel to set
	 */
	public void setAssociationDataModel(IPagingDataModel<CtAssociation> associationDataModel) {
		this.associationDataModel = associationDataModel;
	}
	
	/**
	 * Pagination for available association search list
	 */
	private class AssociationPagingDataModel extends PagingDataModel<CtAssociation> {
		private static final long serialVersionUID = 1L;

		public AssociationPagingDataModel() {
			super("associateSearch");
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<CtAssociation> pageChangeListener(ISearchBean<CtAssociation> searchBean) {
			IContractAssociationSO contractAssociationSO = null;
			Calendar cal = Calendar.getInstance();
		    log.info("BackingBean: Performance Testing SearchAssociation Function: Time taken to start >>>>" + cal.getTime());
			
			try {
				if (getManagedBean().getProjectName() != null && getManagedBean().getProjectName().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("projectName",
							SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getProjectName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getManagedBean().getCtName() != null && getManagedBean().getCtName().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctAssocContracts.ctContract.contractName",
							SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getCtName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if (getManagedBean().getProjectDescription() != null && getManagedBean().getProjectDescription().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getProjectDescription());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}			
				if (getManagedBean().getCtSupplierName() != null && getManagedBean().getCtSupplierName().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.ctContractValue.supplierName",
							SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getCtSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getManagedBean().getCtDateStart() != null && getManagedBean().getCtDateEnd() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.ctContractValue.effDate",
							SearchOperator.OPERATOR_GREATER_EQUAL.value(), getManagedBean().getCtDateStart()));
					searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.ctContractValue.expDate",
							SearchOperator.OPERATOR_LESS_EQUAL.value(), getManagedBean().getCtDateEnd()));			
				}
				if (getManagedBean().getCtDateStart() != null && getManagedBean().getCtDateEnd() == null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.ctContractValue.effDate",
							SearchOperator.OPERATOR_GREATER_EQUAL.value(), getManagedBean().getCtDateStart()));
			    
				}
				if (getManagedBean().getCtDateStart() == null && getManagedBean().getCtDateEnd() != null) {
			    	searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.ctContractValue.expDate",
							SearchOperator.OPERATOR_LESS_EQUAL.value(), getManagedBean().getCtDateEnd()));
		        }
				if (getManagedBean().getCtProductCode() != null && getManagedBean().getCtProductCode().length() > 0) {
		        	SearchFilterBean filterBean = new SearchFilterBean("ctAssocContracts.ctContract.latestContractVer.contractPhysicalNo",
		        			SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getCtProductCode());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAssociationSO.class);
				ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
				reqDTO.setSearchBean(searchBean);
				ContractAssociationResDTO resDTO = contractAssociationSO.searchAssociation(reqDTO);
				getManagedBean().setAssociationList(resDTO.getResultBean().getResultList());
				
				log.info("BackingBean: Performance Testing  SearchAssociation Function: Time taken to End >>>>" + Calendar.getInstance().getTime());
				log.info("BackingBean: Performance Testing  SearchAssociation Function: Total Time taken (Second) >>>>" + (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
				
				if (resDTO.isSuccessful()) {
					return resDTO.getResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * @return the contractDataModel
	 */
	public IPagingDataModel<CtContract> getContractDataModel() {
		return contractDataModel;
	}
	
	/**
	 * @param contractDataModel the contractDataModel to set
	 */
	public void setContractDataModel(IPagingDataModel<CtContract> contractDataModel) {
		this.contractDataModel = contractDataModel;
	}
	
	/**
	 * Pagination for contract search list that can be used
	 * for association.
	 */
	private class ContractPagingDataModel extends PagingDataModel<CtContract> {
		private static final long serialVersionUID = 1L;

		public ContractPagingDataModel() {
			super("advanceCtSearch");
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<CtContract> pageChangeListener(ISearchBean<CtContract> searchBean) {
			IContractSO contractSO = null;
			IParameterSO parameterSO = null;
			Calendar cal = Calendar.getInstance();
			log.info("BackingBean: Performance Testing Search Function: Time taken to start >>>>" + cal.getTime());
			
			try {				
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterType(ParameterTypeConst.CONTRACT_TYPE);
				parameterReqDTO.setParameterCode(ParameterConst.CONTRACT_TYPE_MINISTRY);
				ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType", SearchOperator.OPERATOR_EQUALS.value(), parameterResDTO.getPmParameter()));
				searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.contractVerId",SearchOperator.OPERATOR_IS_NOT_NULL.value(), null));
				
				if(getManagedBean().getCtNumber() != null && getManagedBean().getCtNumber().length()> 0) {
					SearchFilterBean filterBean = new SearchFilterBean("contractNo",
							SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getCtNumber());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setContractSearchBean(searchBean);
				reqDTO.setFilterByAssocContract(true);
				ContractResDTO resDTO = contractSO.searchContract(reqDTO);
				
				log.info("BackingBean: Performance Testing  Search Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
				log.info("BackingBean: Performance Testing  Search Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
				
				if(resDTO.isSuccessful()) {
					return resDTO.getContractResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * The method used for searching the associated contract.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchAssociation() throws Exception {
		if ((getManagedBean().getProjectName() == null || getManagedBean().getProjectName().length() == 0)
				&& (getManagedBean().getCtName() == null || getManagedBean().getCtName().length() == 0)
				&& (getManagedBean().getProjectDescription() == null || getManagedBean().getProjectDescription().length() == 0)
				&& (getManagedBean().getCtSupplierName() == null || getManagedBean().getCtSupplierName().length() == 0)
				&& getManagedBean().getCtDateStart() == null
				&& getManagedBean().getCtDateEnd() == null
				&&(getManagedBean().getCtProductCode() == null || getManagedBean().getCtProductCode().length() == 0)) {
			throw new EpException(MessageConst.ERROR_100060);
		} else {
			associationDataModel.reloadData();
		}
	}
	
	/**
	 * The method used to reset the search criteria.
	 */
	public void searchReset(){
		this.getManagedBean().setProjectName(null);
		this.getManagedBean().setCtName(null);	
		this.getManagedBean().setProjectDescription(null);	
		this.getManagedBean().setCtSupplierName(null);
		this.getManagedBean().setCtProductCode(null);
		this.getManagedBean().setCtDateStart(null);	
		this.getManagedBean().setCtDateEnd(null);		
	}
	
	/**
	 * The method used to create the association.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully.  
	 * 
	 * @return the navigate URL 
	 */
	public String createAssociation() {
		IContractAssociationSO contractAssociationSO = null;
		Calendar cal = Calendar.getInstance();
		log.info("BackingBean: Performance Testing  CreateAssociation Function: Time taken to start >>>>"+ cal.getTime());
		
		try {
			CtAssocContract ctAssocContract = new CtAssocContract();
			List<CtAssocContract> contractAssociationList = new ArrayList<CtAssocContract>();
 			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(
 					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAssociationSO.class);
			
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
 				FacesContext.getCurrentInstance().addMessage(null,
 						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
 								MessageUtils.getMessage(MessageConst.INFO_700004,
 										new String[] {"Project", getManagedBean().getCtAssociation().getProjectName()},
 										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()))); 				  
				renewSesionObject();
			    log.info("BackingBean: Performance Testing  createAssociation Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
				log.info("BackingBean: Performance Testing  createAssociation Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
				return CommonUIConst.ACKNOWLEDGE_PAGE;
 			} 
		} finally {
			contractAssociationSO = null;
		}
		return null;
	}
	
	/**
	 * The method used to delete the selected association list.
	 * This method will refresh the pagination once delete successfully.
	 */
	public void deleteAssociation() {
		IContractAssociationSO contractAssociationSO = null;
		ContractAssociationReqDTO reqDTO = null;
		
		try {
			reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAssociationSO.class);
			
			for (int i = 0; i < ctSelectAssociation.length; i++) {
				CtAssociation ctAssociation  = ctSelectAssociation[i];   
				reqDTO.setCtDelAssociation(ctAssociation);
				ContractAssociationResDTO resDTO = contractAssociationSO.deleteAssociation(reqDTO);
				if(resDTO.isSuccessful()){
					associationDataModel.reloadData();
				}
			}
		} catch (Exception ex) {
			log.error("Exception>>", ex); 
		} finally {
			contractAssociationSO = null;
		} 
	}
	
	/**
	 * The method used for searching the contract that can be
	 * used for association.
	 */
	public void searchContract() {
		try {
			contractDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
		}
	}
	
	/**
	 * The method used to add the selected contract that want to
	 * be associated.
	 */
	public void addAdvanceContract() {
		for (int i = 0; i < selectedContractList.length; i++) {
			CtContract ctContract = selectedContractList[i];
 			if (!this.getManagedBean().getAssociateSearchList().contains(ctContract)) {
 				this.getManagedBean().getAssociateSearchList().add(ctContract);
 			}
		}
 		selectedContractList = null;
	}
	
	/**
	 * The method used to delete the selected contract from
	 * the association.
	 */
 	public void deleteAdvanceContract() {
 		for (int i = 0; i < deleteSelectedContractList.length; i++) {
			CtContract ctContract = deleteSelectedContractList[i];
			this.getManagedBean().getAssociateSearchList().remove(ctContract);
		}
 		deleteSelectedContractList = null;
 	}

 	/**
	 * The method used to navigate to association details screen
	 * for creation or view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String goToAssociatePage() {
		if (this.getManagedBean().getCtAssociation() != null) {
			this.getManagedBean().setCtAssociation(null);
		}
		if (this.getManagedBean().getAssociateSearchList() != null
				&& this.getManagedBean().getAssociateSearchList().size() != 0) {
			this.getManagedBean().setAssociateSearchList(null);
		}
		return PROCESS_ASSOCIATE_PAGE;
	}
   
	/**
	 * The method used to navigate to association details screen
	 * for cancellation purpose.
	 * 
	 * @return the navigate URL 
	 */
    public String goToCancelPage() {
		return PROCESS_ASSOCIATE_CANCEL_PAGE;
	}	
    
    /**
	 * The method used to navigate to association details screen
	 * for update purpose.
	 * 
	 * @return the navigate URL 
	 */
    public String goToUpdateAssociatePage() {    	
    	IContractAssociationSO contractAssociationSO = null;
		ContractAssociationReqDTO reqDTO = null;
		this.getManagedBean().setAssociateSearchList(null);
		
		try {
			reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAssociationSO.class);
			reqDTO.setAssociationId(this.getManagedBean().getCtUpdateAssociation().getAssociationId());
			
			ContractAssociationResDTO resDTO = contractAssociationSO.retrieveAssociation(reqDTO);
			this.getManagedBean().setCtAssociation(resDTO.getCtAssociation());
			
			for(CtAssocContract ctAssocContract : this.getManagedBean().getCtAssociation().getCtAssocContracts()) {
				this.getManagedBean().getAssociateSearchList().add(ctAssocContract.getCtContract());
			}
		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractAssociationSO = null;
		}
    	return PROCESS_ASSOCIATE_PAGE;
    }
}
