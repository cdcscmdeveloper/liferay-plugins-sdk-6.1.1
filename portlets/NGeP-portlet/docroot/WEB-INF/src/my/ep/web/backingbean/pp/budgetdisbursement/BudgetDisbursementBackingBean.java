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
import javax.faces.event.ValueChangeEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetDisbursementSO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetTran;
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
//		log.info("BudgetDisbursementBackingBean - Start");
//		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){			
//			//set current user profile - TODO
//			if(getFacesScopes().getUserSessionBean()!=null){
////				for(PmUserOrg pmUserOrg:getFacesScopes().getUserSessionBean()getPmUserOrgs()){
////					pmUserOrg.get
////				}
//				List <PmOrgProfile> ministryProfiles = CommonMethod.getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
//				this.getManagedBean().setCurrentUser(ministryProfiles!=null ? ministryProfiles.get(0) : new PmOrgProfile());
//			}
//		}
//		log.info("BudgetDisbursementBackingBean - End");
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
		this.getManagedBean().setVerify(false);
		this.getManagedBean().setVerifyRequired(false);
		this.getManagedBean().setConfirm(false);
		log.info("cleanBudgetDisburse - End");
	}
	
	
	/**
	 * add new PpBudgetDtl to the GOSO list
	 */
	public void addBudgetDtls(){
		log.info("addBudgetDtls - Start");
		boolean isValid = true;
		String invalidMsg = "";
		if(this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls()==null){
			this.getManagedBean().getSelectedPpBudget().setPpBudgetDtls(new ArrayList<PpBudgetDtl>());
		}
		
		//ensure the previous entry is not null
		if(!this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().isEmpty()){
			PpBudgetDtl tempPpBudgetDtl = new PpBudgetDtl();
			tempPpBudgetDtl = this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().get(
					this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().size()-1);
			

			//SO must be selected
			if(tempPpBudgetDtl.getPmSo().getSoId()==null){
				isValid = false;
				invalidMsg = "SO is not selected.";
			}else{
				//TODO this can be commented away if user prefers to add multiple summary 1st then only transaction, before submit we will validate this logic again.
				if(this.getManagedBean().isVerifyRequired() && !this.getManagedBean().isVerify()){
					//all transaction must not be empty or invalid 
					if(tempPpBudgetDtl.getPpBudgetTrans()==null 
							|| (tempPpBudgetDtl.getPpBudgetTrans()!=null && tempPpBudgetDtl.getPpBudgetTrans().isEmpty())){
						isValid = false;
						invalidMsg = "GOSO Disbursement Transaction is empty";
					}
//					else{
//						if(this.getManagedBean().isVerifyRequired() && !this.getManagedBean().isVerify()){
//							isValid = false;
//							invalidMsg = "GOSO Disbursement Transaction is invalid or yet to verify.";
//						}
//					}
				}
			}
		}
		//only allowed new entry if valid
		if(isValid){
			this.getManagedBean().getSelectedPpBudget().addPpBudgetDtls(new PpBudgetDtl());
			this.getManagedBean().setSelectedPpBudgetDtl(
					this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().get(
							this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().size()-1));
			this.getManagedBean().getSelectedPpBudgetDtl().setUsedAmt(new BigDecimal(0));
			this.getManagedBean().setVerify(false);
			this.getManagedBean().setVerifyRequired(true);
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,"GOSO Disbursement Summary.", invalidMsg));
		}
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
		boolean isValid = true;
		String invalidMsg = "";
		if(this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans()==null){
			this.getManagedBean().getSelectedPpBudgetDtl().setPpBudgetTrans(new ArrayList<PpBudgetTran>());
		}
//		//ensure the previous entry is not null
//		if(!this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans().isEmpty()){
//			PpBudgetTran tempPpBudgetTran = new PpBudgetTran();
//			tempPpBudgetTran = this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans().get(
//					this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans().size()-1);
//			//allocation amount must be entered
//			if(tempPpBudgetTran.getAllocAmt()==null || new BigDecimal(1).compareTo(tempPpBudgetTran.getAllocAmt())==1){
//				isValid = false;
//				invalidMsg = "Alloc Amount is empty or 0.";
//			}
//		}
		//only allowed new entry if valid
		if(isValid){
			this.getManagedBean().getSelectedPpBudgetDtl().addPpBudgetTrans(new PpBudgetTran());
			this.getManagedBean().setVerify(false);
			this.getManagedBean().setVerifyRequired(true);
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,"GOSO Disbursement Transaction.", invalidMsg));
		}

		log.info("addBudgetTrans - End");
	}
	
	/**
	 * delete budget trans from list
	 * @param ppBudgetTran
	 */
	public void deleteBudgetTran(PpBudgetTran ppBudgetTran){
		log.info("deleteBudgetTran - Start");
		boolean isValid = false;
		isValid = verifyBudgetTran(
				ppBudgetTran.getAllocAmt(), 
				null,
				this.getManagedBean().getSelectedPpBudgetDtl().getUsedAmt(),
				true);
		if(isValid){
			if(ppBudgetTran!=null){
				if(ppBudgetTran.isBrandNew()){
					this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans().remove(ppBudgetTran);
					this.getManagedBean().setVerify(false);
					this.getManagedBean().setVerifyRequired(true);
				}
			}
		}
		
		log.info("deleteBudgetTran - End");
	}
	
	/**
	 * validate alloc amount when value change
	 * @param event
	 */
	public void budgetChangeListener(ValueChangeEvent event){
		log.info("budgetChangeListener - Start");
		Object oldValueObj = event.getOldValue();
	    Object newValueObj = event.getNewValue();
	    BigDecimal oldValue = oldValueObj!=null ? (BigDecimal)oldValueObj : new BigDecimal(0);
		BigDecimal newValue = newValueObj!=null ? (BigDecimal)newValueObj : new BigDecimal(0);
		BigDecimal diffValue = new BigDecimal(0);
		diffValue =  CommonMethod.substractBigDecimalNullable(newValue, oldValue);
		System.out.println(oldValue);
		System.out.println(newValue);
		boolean isValid = false;
		isValid = verifyBudgetTran(newValue, oldValue, this.getManagedBean().getSelectedPpBudgetDtl().getUsedAmt(), false);
		if(isValid){
			//update floating budget
			claimBudget(diffValue);
		}
		log.info("budgetChangeListener - End");
	}
	
	/**
	 * verify budget transaction
	 * @param newTranAllocAmt
	 * @param oldTranAllocAmt
	 * @param budgetDtlUsedAmt
	 * @param isRemove
	 * @return boolean
	 */
	public boolean verifyBudgetTran(BigDecimal newTranAllocAmt, BigDecimal oldTranAllocAmt,
			BigDecimal budgetDtlUsedAmt, boolean isRemove){
		log.info("verifyBudgetTran - Start");
		if(this.getManagedBean().isConfirm()){
			confirmUpdateBudgetDisburse();
		}
		PpBudget ppBudget = this.getManagedBean().getSelectedPpBudget();
		BigDecimal bMapperFloatAmt = new BigDecimal(0);
		BigDecimal btAllocAmt = new BigDecimal(0);
		BigDecimal bFloatAllocAmt = new BigDecimal(0);
		BigDecimal bBalanceAmt = new BigDecimal(0);
		
		boolean isValid = false;
		
		
		btAllocAmt = newTranAllocAmt;
		
		if(isRemove){
			//find the difference (0-oriAllocAmt)
			btAllocAmt = CommonMethod.substractBigDecimalNullable(
					newTranAllocAmt,
					newTranAllocAmt);
//			
//			//remove the old amt from floating
//			bMapperFloatAmt = CommonMethod.substractBigDecimalNullable(
//					ppBudget.getFloatingAllocAmt(),
//					newTranAllocAmt);
		}
		System.out.println("btAllocAmt " + btAllocAmt);
		
		//remove the old amt from floating
		bMapperFloatAmt = CommonMethod.substractBigDecimalNullable(
				ppBudget.getFloatingAllocAmt(),
				oldTranAllocAmt);
		
		//actual balance amt (alloc amt - used amt)
		bBalanceAmt = CommonMethod.substractBigDecimalNullable(
				ppBudget.getBudgetAmt(),
				ppBudget.getUsedAmt());
		System.out.println("bBalanceAmt " + bBalanceAmt);

		//actual balance amt - floating amt
		bFloatAllocAmt =  CommonMethod.substractBigDecimalNullable(
				bBalanceAmt,
				bMapperFloatAmt);
		System.out.println("bFloatAllocAmt " + bFloatAllocAmt);

		if(btAllocAmt.compareTo(bFloatAllocAmt) == 1){
			isValid = false;
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "GOSO Disbursement Transaction invalid." ," Insufficient budget amount."));
		}else if(budgetDtlUsedAmt.compareTo(btAllocAmt) == 1){
			isValid = false;
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "GOSO Disbursement Transaction invalid." ,"Allocate Amount had been used."));
		}else{
			isValid = true;
		}
		this.getManagedBean().setVerify(isValid);
//		if(isValid){
//			ppBudget.setFloatingAllocAmt(bMapperFloatAmt)
//		}
		log.info("isValid " + isValid);
		log.info("verifyBudgetTran - Start");
		return isValid;
	}
	
	public boolean uniqueSO(Long soId){
		log.info("uniqueSO - Start");
		boolean isValid = true;
		int size = this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().size();
		int index = 0;
		//validate the last record with previous entered records
		if(soId!=null && size>1){
			for(PpBudgetDtl ppBudgetDtl:this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls()){	
				index++;
	
				if(soId.equals(ppBudgetDtl.getPmSo().getSoId()) && (index!=size)){
					System.out.println("3");
					this.getManagedBean().setVerify(false);
					this.getManagedBean().setVerifyRequired(true);
					isValid = false;
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "GOSO Disbursement Summary." ,"Duplicate SO Code is selected."));
					break;
				}
				
			}
		}
		if(isValid){
			this.getManagedBean().setVerify(true);
			this.getManagedBean().setVerifyRequired(true);
		}
		log.info("isValid " + isValid);
		log.info("uniqueSO - End ");
		return isValid;
	}
	
//	
//	/**
//	 * confirm the list of new PpBudgetTran 
//	 */
//	public void confirmBudgetTrans(){
//		log.info("confirmBudgetTrans - Start");
//		boolean isValid = false;
//		if(this.getManagedBean().getSelectedPpBudgetDtl()!=null 
//				&& this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans()!=null
//				&& ! this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans().isEmpty()){
//			//calculate and add new entry to summary
//			BigDecimal newAllocAmt = new BigDecimal(0);
//			BigDecimal totalAllocAmt = new BigDecimal(0);
//			BigDecimal claimBudgetAmt = new BigDecimal(0);
//			for(PpBudgetTran budgetTran: this.getManagedBean().getSelectedPpBudgetDtl().getPpBudgetTrans()){
//				newAllocAmt = CommonMethod.addBigDecimalNullable(newAllocAmt,budgetTran.getAllocAmt());
//				budgetTran.setAccumAmt(newAllocAmt);
//				
//				if(budgetTran.isBrandNew()){
//					claimBudgetAmt = CommonMethod.addBigDecimalNullable(claimBudgetAmt,budgetTran.getAllocAmt());
//				}
//			}
//			totalAllocAmt = CommonMethod.addBigDecimalNullable(this.getManagedBean().getSelectedPpBudget().getUsedAmt(),newAllocAmt);
//			isValid = verifyBudgetTrans(totalAllocAmt, this.getManagedBean().getSelectedPpBudget());
//			this.getManagedBean().setVerify(isValid);
//			if(isValid){
//				this.getManagedBean().getSelectedPpBudget().setUsedAmt(totalAllocAmt); //update budget amount
//				this.getManagedBean().getSelectedPpBudgetDtl().setAllocAmt(newAllocAmt); //update budget dtl amount
//				claimBudget(claimBudgetAmt); //claim budget
//			}
//		}
//		log.info("confirmBudgetTrans - End");
//	}
	
	/**
	 * claim budget
	 * @param claimAmt
	 */
	private void claimBudget(BigDecimal claimAmt){
		log.info("claimBudget - Start");
		log.info("claimAmt " + claimAmt);
		PpBudget budget = this.getManagedBean().getSelectedPpBudget();
		budget.setFloatingAllocAmt(CommonMethod.addBigDecimalNullable(budget.getFloatingAllocAmt(), claimAmt));

//		this.getManagedBean().getSelectedGoMapper().put(
//				budget.getBudgetId(),
//				CommonMethod.addBigDecimalNullable(
//					this.getManagedBean().getSelectedGoMapper().get(budget.getBudgetId()),
//					claimAmt)
//		);
//		
		
		log.info("claimBudget - End");
	}

//	/**
//	 * verify budget trans amount
//	 * @param totalAllocAmt
//	 * @param ppBudget
//	 * @return isValid
//	 */
//	private boolean verifyBudgetTrans(BigDecimal totalAllocAmt, PpBudget ppBudget){
//		log.info("verifyBudgetTrans - Start");
//		//totalAllocAmt > budgetAmt = false
//		//totalAllocAmt < useAmt = false
//		boolean isValid = false;
//		
//		if(totalAllocAmt.compareTo(ppBudget.getBudgetAmt()) == 1){
//			isValid = false;
//		}else if(ppBudget.getUsedAmt().compareTo(totalAllocAmt) == 1){
//			isValid = false;
//		}else{
//			isValid = true;
//		}
//		log.info("isValid " + isValid);
//		log.info("verifyBudgetTrans - Start");
//		return isValid;
//	}
	
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
				if(uniqueSO(soId)){
					ObjectReqDTO reqDTO = new ObjectReqDTO();
					reqDTO.setSoId(soId);
					IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
					ObjectResDTO resDTO = objectSO.retrieveSOBYSOId(reqDTO);
		//			if(resDTO.isSuccessful()){
						pmSo = resDTO.getPmSo();
		//			}
				}
			}
			this.getManagedBean().getSelectedPpBudgetDtl().setPmSo(pmSo);
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
	 * calculate balance amount included floating amt
	 * @param budgetAmount
	 * @param usedAmount
	 * @param floatingAmount
	 * @return
	 */
	public BigDecimal balanceAmount(BigDecimal budgetAmount, BigDecimal usedAmount, BigDecimal floatingAmount){
		BigDecimal usedFloatAmt = CommonMethod.addBigDecimalNullable(floatingAmount,usedAmount);
		System.out.println("budgetAmount " + budgetAmount);
		System.out.println("usedAmount " + usedAmount);
		System.out.println("floatingAmount " + floatingAmount);
		return CommonMethod.substractBigDecimalNullable(budgetAmount,usedFloatAmt);
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
	 * submit/save budget disbursement
	 * @return xhtml
	 */
	public String submitBudgetDisbursement(){
		log.info("submitBudgetDisbursement - Start");
		boolean isSuccess = false;
		boolean isValid = true;
		String invalidMsg = "";
		try{
			//summary is empty
			if(this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls()==null
					|| (this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls()!=null && this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls().isEmpty()) ){
				
				isValid = false;
				invalidMsg = "GOSO Disbursement Summary is empty.";
				
			}else{
				//transaction is empty
				for(PpBudgetDtl ppBudgetDtl:this.getManagedBean().getSelectedPpBudget().getPpBudgetDtls()){
					if(ppBudgetDtl.getPpBudgetTrans()==null || (ppBudgetDtl.getPpBudgetTrans()!=null && ppBudgetDtl.getPpBudgetTrans().isEmpty())){
						isValid = false;
						System.out.println("2");
						invalidMsg = "GOSO Disbursement Transaction for SO: " + ppBudgetDtl.getPmSo().getSoCode() + " is empty.";
						break;
						
					}
				}
				if(isValid){
					//verify is required and invalid
					if(this.getManagedBean().isVerifyRequired() && !this.getManagedBean().isVerify()){
						isValid = false;
						invalidMsg = "GOSO Disbursement Transaction is invalid or yet to verify.";
						System.out.println("3");
						
					}
				}
			}

			if(isValid){
				if(!this.getManagedBean().isConfirm()){
					confirmUpdateBudgetDisburse();
				}
				isSuccess = saveBudgetDisbursement();
			}else{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_ERROR,"Budget Disbursement submit failed.",invalidMsg));
			}	
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
	 * calculate and update budget dtl
	 */
	private void confirmUpdateBudgetDisburse(){
		log.info("confirmUpdateBudgetAlloc - Start");
		if(this.getManagedBean().isConfirm()){
			this.getManagedBean().getSelectedPpBudget().setUsedAmt(
					CommonMethod.substractBigDecimalNullable(
							this.getManagedBean().getSelectedPpBudget().getUsedAmt(), 
							this.getManagedBean().getSelectedPpBudget().getFloatingAllocAmt())
							);
			this.getManagedBean().setConfirm(false);
		}else{
			this.getManagedBean().getSelectedPpBudget().setUsedAmt(
					CommonMethod.addBigDecimalNullable(
							this.getManagedBean().getSelectedPpBudget().getUsedAmt(), 
							this.getManagedBean().getSelectedPpBudget().getFloatingAllocAmt())
							);
			this.getManagedBean().setConfirm(true);
		}
		
		log.info("confirmUpdateBudgetAlloc - End");
	}
	
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
				
				List <PmOrgProfile> ministryProfiles = CommonMethod.getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
//				Long ministryId = ministryProfiles!=null ? ministryProfiles.get(0).getOrgProfileId() : 0L;
//				for(PmOrgProfile pmOrgProfile:ministryProfiles){
//					System.out.println("pmOrgProfile "  + pmOrgProfile.getOrgName());
//				}
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"pmOrgProfile", 
						SearchOperator.OPERATOR_IN.value(), 
						ministryProfiles));
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
