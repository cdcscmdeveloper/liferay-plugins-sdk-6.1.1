/**
 * Copyright © 2013 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.budgetdisbursement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetDisbursementSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetTran;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.budgetdisbursement.BudgetDisbursementManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 * Backing Bean For Budget Disbursement
 * 
 * @author RainPoh
 * @since 19/12/2013
 */
@ManagedBean(name="budgetDisburseBackingBean")
@ViewScoped
public class BudgetDisbursementBackingBean extends BaseBackingBean<BudgetDisbursementManagedBean>{
	private static Logger log = Logger.getLogger(BudgetDisbursementBackingBean.class);
	private IPagingDataModel<PpBudget> budgetDataModel = new BudgetPagingDataModel();
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * constructor of the budget allocation program
	 */
	public BudgetDisbursementBackingBean(){
		log.info("BudgetDisbursementBackingBean - Start");
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){			
			//set current user profile
			if(getFacesScopes().getUserSessionBean()!=null){
				List <PmOrgProfile> ministryProfiles = getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
				this.getManagedBean().setCurrentUser(ministryProfiles!=null ? ministryProfiles.get(0) : new PmOrgProfile());
			}
		}
		log.info("BudgetDisbursementBackingBean - End");
	}
	

	/**
	 * redirect to budget disbursement details view
	 * @param budgetId
	 * @return xhtml
	 */
	public String redirectToBudgetDisbursement(Long budgetId){
		log.info("redirectToBudgetDisbursement - Start");
		try{
			PpBudget budgetObj = retrieveBudget(budgetId);
			cleanBudgetDisburse(budgetObj);
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("redirectToBudgetDisbursement - End");
		return ProcurementPlanConst.NAV_TO_BUDGET_DISBURSEMENT;
	
	}
	
	/**
	 * redirect to budget list view
	 * @return xhtml
	 */
	public String redirectToBudgetList(){
		log.info("redirectToBudgetList - Start");
		try{

		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToBudgetList - End");
		return ProcurementPlanConst.NAV_TO_LIST_BUDGET;
	
	}
	
	/**
	 * initial budget disbursement clean view
	 * @param ppBudget
	 */
	private void cleanBudgetDisburse(PpBudget ppBudget){
		log.info("cleanBudgetDisburse - End");
		this.getManagedBean().getSelectedGoMapper().clear();
		this.getManagedBean().setSelectedPpBudget(ppBudget); //populate selected GO
		this.getManagedBean().setSelectedPpBudgetDtl(null);
		log.info("cleanBudgetDisburse - End");
	}
	
	
	/**
	 * add new PpBudgetDtl to the GOSO list
	 */
	public void addBudgetDtls(){
		log.info("addBudgetDtls - Start");
		if(this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls()==null){
			this.getManagedBean().getSelectedPpBudget().setPpBudgetDtls(new ArrayList<PpBudgetDtl>());
		}
		this.getManagedBean().getSelectedPpBudget().addPpBudgetDtls(new PpBudgetDtl());
		this.getManagedBean().setSelectedPpBudgetDtl(
				this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().get(
						this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().size()-1));
		log.info("addBudgetDtls - End");
	}
	
	/**
	 * delete budget dtl from list
	 * @param ppBudgetDtl
	 */
	public void deleteBudgetDtl(PpBudgetDtl ppBudgetDtl){
		log.info("deleteBudgetDtl - Start");
		if(ppBudgetDtl!=null){
			if(!ppBudgetDtl.isBrandNew()){
				ppBudgetDtl.setDeleted(true);
				ppBudgetDtl.setRecordStatus(false);
			}else{
				this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().remove(ppBudgetDtl);
			}
			//set null plan for childs relationship
			this.getManagedBean().setSelectedPpBudgetDtl(null);
		}
		log.info("deleteBudgetDtl - End");
	}
	
	/**
	 * add new PpBudgetTran to the transaction list
	 */
	public void addBudgetTrans(){
		log.info("addBudgetTrans - Start");
		if(this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans()==null){
			this.getManagedBean().getSelectedPpBudgetDtl().setPpBudgetTrans(new ArrayList<PpBudgetTran>());
		}
		this.getManagedBean().getSelectedPpBudgetDtl().addPpBudgetTrans(new PpBudgetTran());
		this.getManagedBean().getSelectedPpBudgetDtl().setVerify(false);
		this.getManagedBean().getSelectedPpBudgetDtl().setVerifyRequired(true);
		log.info("addBudgetTrans - End");
	}
	
	/**
	 * delete budget trans from list
	 * @param ppBudgetTran
	 */
	public void deleteBudgetTran(PpBudgetTran ppBudgetTran){
		log.info("deleteBudgetTran - Start");
		if(ppBudgetTran!=null){
			if(ppBudgetTran.isBrandNew()){
				this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans().remove(ppBudgetTran);
				this.getManagedBean().getSelectedPpBudgetDtl().setVerify(false);
				this.getManagedBean().getSelectedPpBudgetDtl().setVerifyRequired(true);
			}
		}
		log.info("deleteBudgetTran - End");
	}
	
	/**
	 * confirm the list of new PpBudgetTran 
	 */
	public void confirmBudgetTrans(){
		log.info("confirmBudgetTrans - Start");
		boolean isValid = false;
		if(this.getManagedBean().getSelectedPpBudgetDtl()!=null && this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans()!=null){
			//calculate and add new entry to summary
			BigDecimal newAllocAmt = new BigDecimal(0);
			BigDecimal totalAllocAmt = new BigDecimal(0);
			BigDecimal claimBudgetAmt = new BigDecimal(0);
			for(PpBudgetTran budgetTran: this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans()){
				newAllocAmt = CommonMethod.addBigDecimalNullable(newAllocAmt,budgetTran.getAllocAmt());
				budgetTran.setAccumAmt(newAllocAmt);
				
				if(budgetTran.isBrandNew()){
					claimBudgetAmt = CommonMethod.addBigDecimalNullable(claimBudgetAmt,budgetTran.getAllocAmt());
				}
			}
			totalAllocAmt = CommonMethod.addBigDecimalNullable(this.getManagedBean().getSelectedPpBudget().getUsedAmt(),newAllocAmt);
			isValid = verifyBudgetTrans(totalAllocAmt, this.getManagedBean().getSelectedPpBudget());
			this.getManagedBean().getSelectedPpBudgetDtl().setVerify(isValid);
			if(isValid){
				this.getManagedBean().getSelectedPpBudget().setUsedAmt(totalAllocAmt); //update budget amount
				this.getManagedBean().getSelectedPpBudgetDtl().setAllocAmt(newAllocAmt); //update budget dtl amount
				claimBudget(claimBudgetAmt); //claim budget
			}
		}
		log.info("confirmBudgetTrans - End");
	}
	
	/**
	 * claim budget
	 * @param claimAmt
	 */
	private void claimBudget(BigDecimal claimAmt){
		log.info("claimBudget - Start");
		log.info("claimAmt " + claimAmt);
		PpBudget budget = this.getManagedBean().getSelectedPpBudget();

		this.getManagedBean().getSelectedGoMapper().put(
				budget.getBudgetId(),
				CommonMethod.addBigDecimalNullable(
					this.getManagedBean().getSelectedGoMapper().get(budget.getBudgetId()),
					claimAmt)
		);
		
		
		log.info("claimBudget - End");
	}

	/**
	 * verify budget trans amount
	 * @param totalAllocAmt
	 * @param ppBudget
	 * @return isValid
	 */
	private boolean verifyBudgetTrans(BigDecimal totalAllocAmt, PpBudget ppBudget){
		log.info("verifyBudgetTrans - Start");
		//totalAllocAmt > budgetAmt = false
		//totalAllocAmt < useAmt = false
		boolean isValid = false;
		
		if(totalAllocAmt.compareTo(ppBudget.getBudgetAmt()) == 1){
			isValid = false;
		}else if(ppBudget.getUsedAmt().compareTo(totalAllocAmt) == 1){
			isValid = false;
		}else{
			isValid = true;
		}
		log.info("isValid " + isValid);
		log.info("verifyBudgetTrans - Start");
		return isValid;
	}
	
//	private boolean verifyBudgetDtl(PpBudget ppBudget){
//		log.info("verifyBudgetDtl - Start");
//		boolean isValid = false;
//		BigDecimal newAllocAmt = new BigDecimal(0);
//		for(PpBudgetDtl budgetDtl: ppBudget.getPpBudgetDtls()){
//			newAllocAmt = CommonMethod.addBigDecimalNullable(newAllocAmt, budgetDtl.getAllocAmt());
//		}
//		totalAllocAmt = CommonMethod.addBigDecimalNullable(this.getManagedBean().getSelectedPpBudget().getUsedAmt(),newAllocAmt);
//		//newAllocAmt
//		log.info("verifyBudgetDtl - Start");
//		return isValid;
//	}
//	
	/**
	 * submit/save budget disbursement
	 * @return xhtml
	 */
	public String submitBudgetDisbursement(){
		log.info("submitBudgetDisbursement - Start");
		boolean isSuccess = false;
		try{
			isSuccess = saveBudgetDisbursement();
		}catch (Exception e){
			e.printStackTrace();	
		}
		log.info("isSuccess " + isSuccess);
		log.info("submitBudgetDisbursement - End");
		if(isSuccess){
			return ProcurementPlanConst.NAV_TO_LIST_BUDGET;
		}else{
			return "";
		}
	}
	
	/**
	 * retrieve org profile by login user and type
	 * @param userId
	 * @param type
	 * @return org profile
	 */
	private List <PmOrgProfile> getOrgProfiles(Long userId, String orgType){
		log.info("getOrgProfiles - Start");
		log.info("userId " + userId);
		List <PmOrgProfile> profileList =  new ArrayList<PmOrgProfile>();
		try{
			if(CommonMethod.isSelected(userId) && CommonMethod.isSelected(orgType)){
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setOrgType(orgType);
				
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
//				if(resDTO.isSuccessful()){
					profileList = resDTO.getOrgProfileList();
				
					System.out.println("profile size" + profileList.size());
					for(PmOrgProfile o :profileList){
						System.out.println("org id" + o.getOrgProfileId());
					}
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("getOrgProfiles - End");
		return profileList;
	}

	/**
	 * populate SO by GO
	 * @return SO list
	 */
	public List<PmSo> soOptions(Long goId) {
		log.info("soOptions - Start");
		log.info("id " + goId);
		List<PmSo> pmSos = new ArrayList<PmSo>();
		try{
			if(goId!=null){
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				reqDTO.setGoId(goId);
				IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
				ObjectResDTO resDTO = objectSO.retrieveSOListByGOCode(reqDTO);
		//		if(resDTO.isSuccessful()){
					pmSos = resDTO.getPmSoList();
		//		}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("soOptions - End");
		return pmSos;
	}
	
	/**
	 * retrieve and set selected SO obj
	 */
	public void selectedSo(Long soId){
		log.info("selectedSo - Start");
		log.info("id " + soId);
		PmSo pmSo = new PmSo();
		try{
			if(soId!=null){
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				reqDTO.setSoId(soId);
				IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
				ObjectResDTO resDTO = objectSO.retrieveSOBYSOId(reqDTO);
	//			if(resDTO.isSuccessful()){
					pmSo = resDTO.getPmSo();
					this.getManagedBean().getSelectedPpBudgetDtl().setPmSo(pmSo);
	//			}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("selectedSo - End");
	}
	
	/**
	 * calculate balance amount
	 * @param budgetAmount
	 * @param usedAmount
	 * @return
	 */
	public BigDecimal balanceAmount(BigDecimal budgetAmount, BigDecimal usedAmount){
		return CommonMethod.substractBigDecimalNullable(budgetAmount,usedAmount);
	}

	/**
	 * retrieve budget by id
	 * @param budgetId
	 * @return PpBudget
	 */
	public PpBudget retrieveBudget(Long budgetId){
		log.info("retrieveBudget - Start");
		log.info("id " + budgetId);
		PpBudget budget = new PpBudget();
		
		try{
			if(budgetId!=null){
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setBudgetId(budgetId);
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);
				BudgetDisbursementResDTO resDTO = reqSO.retrieveBudgetById(reqDTO);
				if(resDTO.isSuccessful()){
					budget = resDTO.getBudget();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		log.info("retrieveBudget - End");
		return budget;
	}
//	
//	/**
//	 * retrieve budget trans by budget dtl id
//	 * @param budgetDtlId
//	 * @return PpBudgetDtl
//	 */
//	public List <PpBudgetTran> retrieveBudgetTrans(Long budgetDtlId){
//		log.info("retrieveBudgetTrans - Start");
//		log.info("id " + budgetDtlId);
//		List <PpBudgetTran> budgetTrans = new ArrayList<PpBudgetTran>();
//		
//		try{
//			if(budgetDtlId!=null){
//				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
//				reqDTO.setBudgetDtlId(budgetDtlId);
//				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);
//				BudgetDisbursementResDTO resDTO = reqSO.retrieveBudgetTransByBudgetDtl(reqDTO);
//				if(resDTO.isSuccessful()){
//					budgetTrans = resDTO.getBudgetTranList();
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		log.info("retrieveBudgetTrans - End");
//		return budgetTrans;
//	}
//	
	/**
	 * save budget disbursement
	 * @return isSuccess
	 */
	public boolean saveBudgetDisbursement()throws Exception{
		log.info("saveBudgetDisbursement - Start");
		boolean isSuccess = false;
		PpBudget budget = this.getManagedBean().getSelectedPpBudget();
		try{
			if(budget!=null && budget.getPpBudgetDtls()!=null && !budget.getPpBudgetDtls().isEmpty()){
//				budget.setUsedAmt(budget.getUsedAmt().add(this.getManagedBean().getSelectedGoMapper().get(budget.getBudgetId())));
//				System.out.println("budget used amount  " + budget.getUsedAmt());
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setBudget(budget);
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);
				BudgetDisbursementResDTO resDTO = reqSO.saveBudgetDisbursement(reqDTO);
				isSuccess = resDTO.isSuccessful();
				if(isSuccess){
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_INFO,"Budget Disbursement submit successfully.","GO : "+ budget.getPmGo().getGoCode()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),"Budget Disbursement failed."));
			throw e;
		}
		log.info("saveBudgetDisbursement - End");
		return isSuccess;
	}
	
	
	/**
	 * paginate for budget list
	 * @author RainPoh
	 *
	 */
	private class BudgetPagingDataModel extends PagingDataModel<PpBudget> {
		private static final long serialVersionUID = 1L;

		public BudgetPagingDataModel() {
			super("dtBudget");
		}
		/**
		 * @author rainpoh
		 * @since 19.12.2013
		 * retrieve default budget by ministry id
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpBudget> pageChangeListener(ISearchBean<PpBudget> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {				
				
				List <PmOrgProfile> ministryProfiles = getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
				Long ministryId = ministryProfiles!=null ? ministryProfiles.get(0).getOrgProfileId() : 0L;

				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"orgProfileId", 
						SearchOperator.OPERATOR_EQUALS.value(), 
						ministryId));
				reqDTO.setBudgetSearchBean(searchBean);	
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);	
				BudgetDisbursementResDTO resDTO = reqSO.retrieveBudgetsByMinistry(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getBudgetResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * @return the budgetDataModel
	 */
	public IPagingDataModel<PpBudget> getBudgetDataModel() {
		return budgetDataModel;
	}

	/**
	 * @param budgetDataModel the budgetDataModel to set
	 */
	public void setBudgetDataModel(IPagingDataModel<PpBudget> budgetDataModel) {
		this.budgetDataModel = budgetDataModel;
	}

}
