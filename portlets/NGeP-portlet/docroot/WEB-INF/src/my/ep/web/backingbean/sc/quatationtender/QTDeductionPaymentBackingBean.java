/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.event.UnselectEvent;

/**
 * The Class QTDeductionPaymentBackingBean.
 *
 * @author Sudheer Kumar
 */
@ManagedBean(name = "qTDeductionPaymentBackingBean")
@RequestScoped
public class QTDeductionPaymentBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTDeductionPaymentBackingBean.class);

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		
		ScQt scQt = this.getManagedBean().getQt();
		if(scQt != null){
			message += " @qtNo = "+scQt.getQtNo();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}else{
			log.info(message);
		}
		
	}
	
	/** The flag. */
	private boolean flag;
	
	

	/**
	 * Instantiates a new QT deduction payment backing bean.
	 */
	public QTDeductionPaymentBackingBean() {
	}

	/**
	 * Deduction type change.
	 * Deduction Clause will show when deduction type changes
	 * @param scQtDeduction
	 */
	public void deductionTypeChange(int index, ScQtDeduction scQtDeduction) {
		debugLogger("deductionTypeChange");
		
		IParameterSO parameterSO = null;
		try {
			Long parameterId = scQtDeduction.getDeductionType()
					.getParameterId();
			
			if (null != parameterId) {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterId(parameterId);
				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
				scQtDeduction.setDeductionType(resDTO.getPmParameter());
				getClausesTitle(scQtDeduction);
				scQtDeduction.setScQtDeductionClauses(null);
				this.getManagedBean().setSelectedScQtDeduction(scQtDeduction);
			} else {
				scQtDeduction.setDeductionType(new PmParameter());
				scQtDeduction.setScQtDeductionClauses(null);
				getClausesTitle(scQtDeduction);
				this.getManagedBean().setSelectedScQtDeduction(scQtDeduction);
			}
			this.getManagedBean().getScQtDeductionList().remove(index);
			this.getManagedBean().getScQtDeductionList().add(index, this.getManagedBean().getSelectedScQtDeduction());
		} finally {
			parameterSO = null;
		}
	}

	/**
	 * Gets the clauses title.
	 *
	 * @param deduction the deduction
	 * @return the clauses title
	 */
	private void getClausesTitle(ScQtDeduction deduction){
		StringBuilder title  = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if(deduction != null && deduction.getDeductionType() != null){
			title.append(getParameterDesc(deduction.getDeductionType()));
			title.append(" > "); 
			title.append(" ");
		}

		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.deduction.clauses",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setClausesTitle(title.toString());
	}

	/**
	 * Adds the deduction payment in deduction payment.
	 */
	public void addDeductionPaymentAction() {
		debugLogger("addDeductionPaymentAction");
		List<ScQtDeduction> scQtDeductions = this.getManagedBean().getScQtDeductionList();
		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();
		}
		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setDeductionType(new PmParameter());
		//scQtDeduction.setScQt(this.getManagedBean().getQt());
		//scQtDeductions.add(scQtDeduction);
		this.getManagedBean().getScQtDeductionList().add(scQtDeduction);
	}

	/**
	 * Delete deduction payment deduction payment.
	 */
	public void deleteDeductionPaymentAction() {
		debugLogger("deleteDeductionPaymentAction");
		List<ScQtDeduction> deductionList = this.getManagedBean().getScQtDeductionList();
		List<ScQtDeduction> selectedList = new ArrayList<ScQtDeduction>();
		ScQtDeduction selectedDeduction = this.getManagedBean().getSelectedScQtDeduction();
		if (null == selectedDeduction) {
			throw new EpException("Please select At least one Item");
		}
		selectedList.add(selectedDeduction);
		List<ScQtDeduction> removeDeductionList = this.getManagedBean().getRemoveDeductions();
		if (null == removeDeductionList) {
			removeDeductionList = new ArrayList<ScQtDeduction>();
		}
		removeDeductionList.addAll(selectedList);
		if (null != selectedList && !selectedList.isEmpty()) {
			for (ScQtDeduction qtDeduction : selectedList){
				qtDeduction.setDeleted(true);
				deductionList.remove(qtDeduction);
			}
		}
		this.getManagedBean().setScQtDeductionList(deductionList);
		this.getManagedBean().setRemoveDeductions(removeDeductionList);
		this.getManagedBean().setSelectedScQtDeduction(null);
	}
	
	/**
	 * Adds the deduction clause in deduction payment.
	 */
	public void addDeductionClauseAction() {
		debugLogger("addDeductionClauseAction");
		ScQtDeduction selectedScQtDeduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> scQtDeductionClauses = selectedScQtDeduction
				.getScQtDeductionClauses();
		if (scQtDeductionClauses == null) {
			scQtDeductionClauses = new ArrayList<ScQtDeductionClause>();
		}
		ScQtDeductionClause scQtDeductionClause = new ScQtDeductionClause();
		scQtDeductionClause.setScQtDeduction(selectedScQtDeduction);
		scQtDeductionClauses.add(scQtDeductionClause);
		this.getManagedBean().getSelectedScQtDeduction()
				.setScQtDeductionClauses(scQtDeductionClauses);
	}
	
	/**
	 * Delete deduction clause.
	 */
	public void deleteDeductionClause() {
		debugLogger("deleteDeductionClause");
		List<ScQtDeductionClause> result = new ArrayList<ScQtDeductionClause>();
		boolean flag = true;
		List<ScQtDeductionClause> clauses = getManagedBean().getSelectedScQtDeduction().getScQtDeductionClauses();
		if(clauses != null && clauses.size() > 0){
			
			for (ScQtDeductionClause deductionClause : clauses) {
				if (!deductionClause.isSelected()) {
					result.add(deductionClause);
				} else {
					flag = false;
				}
			}
		}
		
		if (flag) {
			throw new EpException("Please select At least one Item");
		}
		getManagedBean().getSelectedScQtDeduction().setScQtDeductionClauses(result);
		/*ScQtDeduction scQtDeduction = getManagedBean().getSelectedScQtDeduction();
		List<ScQtDeductionClause> clauses = getManagedBean().getSelectedDeductionClauses();
		if(clauses != null && clauses.size()>0){
			deleteRowsAction(scQtDeduction.getScQtDeductionClauses(), clauses);
		} else {
			throw new EpException("Please select At least one Item");
		}*/
	}

	/**
	 * Delete rows action.
	 */
	public void deleteRowsAction() {
		ScQtDeduction deduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> deductionCluases = deduction
				.getScQtDeductionClauses();
		List<ScQtDeductionClause> selectedClauses = this.getManagedBean()
				.getSelectedDeductionClauses();
		if (null == selectedClauses || selectedClauses.isEmpty()) {
			throw new EpException("Please select at least one deduction clause");
		}
		for (ScQtDeductionClause clause : selectedClauses) {
			deductionCluases.remove(clause);
		}
		deduction.setScQtDeductionClauses(deductionCluases);
		this.getManagedBean().setSelectedScQtDeduction(deduction);
	}
	
	/**
	 * Row unselect event.
	 *
	 * @param selectEvent the select event
	 */
	public void rowUnselectEvent(UnselectEvent selectEvent) {
		ScQtDeduction deduction = (ScQtDeduction) selectEvent.getObject();
		if(ParameterConst.DEDUCTION_TYPE_OTHERS.equalsIgnoreCase(deduction.getDeductionType().getParameterCode())){
			
		}
		
	}
	
	/**
	 * Gets the deduction types.
	 *
	 * @return the deduction types
	 */
	public SelectItem[] getDeductionTypes(){
		SearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		
		List<String> deductionTypes = new ArrayList<String>();
		deductionTypes.add(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT);
		deductionTypes.add(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE);		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(),
						deductionTypes));

		return getDropdownValuesBySearchBean(ParameterTypeConst.DEDUCTION_TYPE,searchBean);
	}
	
	/**
	 *User Selects DeductionClause.
	 *
	 *
	 */
	public void selectAllDeductionClause(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		this.getManagedBean().getSelectedScQtDeduction().getScQtDeductionClauses();
		
		List<ScQtDeductionClause> clauses = getManagedBean().getSelectedScQtDeduction().getScQtDeductionClauses();
		if (clauses != null && clauses.size() > 0) {
			for (ScQtDeductionClause deductionClause : clauses) {
				if (isSelectAll) {
					deductionClause.setSelected(true);
				} else {
					deductionClause.setSelected(false);
				}
			}
		}
	}

	/**
	 * Get the flag.
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * Set the flag.
	 * @param flag the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
