/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.tpl.helper;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.tpl.spec.TplSpecEstimation;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 * Singleton Helper For Template Spec.
 * 
 * @author kenny
 * @since 24.08.2014
 * 
 */
public class TemplateSpecHelper {
	private static Logger log = EpLogger.getLogger(TemplateSpecHelper.class);
	
	private static TemplateSpecHelper instance;	
	
	/**
	 * Get singleton of TemplateSpecHelper
	 * 
	 * @return TemplateSpecHelper singleton
	 */
	public static TemplateSpecHelper getInstance(){
		if (instance == null){
			instance = new TemplateSpecHelper();
		}
		return instance;
	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
	
	/**
	 * Clone the Spec Master
	 * 
	 * @param specMaster
	 * 		the TplSpecMaster to be clone.
	 * @param includeQuestions
	 * 		clone including child entities.
	 * @return	the new cloned TplSpecMaster instance.
	 */
	public TplSpecMaster cloneTplSpecMaster(TplSpecMaster specMaster, boolean includeQuestions) {
		debugLogger("cloneTplSpecMaster : " + specMaster);
		
		TplSpecMaster newSpecMaster = null;

		if (specMaster != null) {
			try {
				newSpecMaster = (TplSpecMaster) BeanUtils.cloneBean(specMaster);
				
				if (newSpecMaster != null) {
					newSpecMaster.setBrandNew(true);
					newSpecMaster.setSpecMasterId(null);
					newSpecMaster.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
					newSpecMaster.setAdminRemark(null);
					newSpecMaster.setApproverRemark(null);
					newSpecMaster.setTplSpecZone(null);
					newSpecMaster.setScQtDocs(null);
					newSpecMaster.setScQuoteDocs(null);
					setUserDetails(newSpecMaster);

					if (includeQuestions) {
						// clone spec questions
						cloneTplSpecQuestions(newSpecMaster, specMaster.getTplSpecQuestions());	
					}
				}
			} catch (IllegalAccessException
					| InstantiationException
					| InvocationTargetException
					| NoSuchMethodException e) {
				log.error(e);
				log.warn("clone " + specMaster + ": " + specMaster.getSpecMasterId() + " failed.");
			}
		}
		
		return newSpecMaster;
	}
	
	/**
	 * Clone the list of Spec Questions.
	 * 
	 * @param newSpecMaster
	 * 		the new TplSpecMaster cloned.
	 * @param specQuestions
	 * 		the list of TplSpecQuestion to be clone.
	 * @return	the list of new cloned TplSpecQuestion instance.
	 */
	public List<TplSpecQuestion> cloneTplSpecQuestions(TplSpecMaster newSpecMaster, List<TplSpecQuestion> specQuestions) {
		debugLogger("cloneTplSpecQuestions : " + newSpecMaster);
		
		List<TplSpecQuestion> newSpecQuestions = null;
		
		if (newSpecMaster != null) {
			if (specQuestions != null && !specQuestions.isEmpty()) {
				boolean isTechnicalSpec = ParameterConst.SPECIFICATION_TYPE_TECHNICAL.equals(
						newSpecMaster.getSpecTypeCode().getParameterCode());
				
				newSpecQuestions = new ArrayList<TplSpecQuestion>();
				
				for (TplSpecQuestion specQuestion : specQuestions) {
					try {
						TplSpecQuestion newSpecQuestion = (TplSpecQuestion) BeanUtils.cloneBean(specQuestion);
						
						if (newSpecQuestion != null) {
							if (isTechnicalSpec || 
									(!isTechnicalSpec && TemplateSpecConst.QUESTION_TYPE_ITEM.equals(
											newSpecQuestion.getQuestionType()))) {
								
								newSpecQuestion.setSpecQuestionId(null);
								newSpecQuestion.setTplSpecMaster(newSpecMaster);
								newSpecQuestion.setTplSpecAnswers(null);
								
								if (isTechnicalSpec) {
									newSpecQuestion.setAnswerTypeCode(null);
								} else {
									IParameterSO parameterSO = null;
									try {
										parameterSO = BusinessDelegate.getInstance().getServiceObject(
												ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
									
										ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
										parameterReqDTO.setParameterType(ParameterTypeConst.ANSWER_TYPE);
										parameterReqDTO.setParameterCode(ParameterConst.ANSWER_TYPE_NUMERIC);
										ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
										newSpecQuestion.setAnswerTypeCode(parameterResDTO.getPmParameter());
									} finally {
										parameterSO = null;
									}
								}
								newSpecQuestion.setScoreTypeCode(null);
								newSpecQuestion.setMaxScore(null);
								newSpecQuestion.setWeightageReq(false);
								newSpecQuestion.setWeightage(null);
								newSpecQuestion.setTplSpecScorings(null);
								
								resetEstimations(newSpecQuestion);
								
								setUserDetails(newSpecQuestion);
								newSpecQuestions.add(newSpecQuestion);
								
								// clone spec quantities
								cloneTplSpecTierQty(newSpecQuestion, specQuestion.getTplSpecTierQties());	
							}
						}
					} catch (IllegalAccessException | InstantiationException
							| InvocationTargetException | NoSuchMethodException e) {
						log.error(e);
						log.warn("clone " + specQuestion + ": " + specQuestion.getSpecQuestionId() + " failed.");
					}
				}
				newSpecMaster.setTplSpecQuestions(newSpecQuestions);
				
				updateItemWeightageReq(newSpecMaster, newSpecQuestions);
			}
		}
		
		return newSpecQuestions;
	}
	
	/**
	 * Clone the list of Spec Tier Quantities.
	 * 
	 * @param newSpecMaster
	 * 		the new TplSpecMaster cloned.
	 * @param specTierQties
	 * 		the list of TplSpecTierQty to be clone.
	 * @return	the list of new cloned TplSpecTierQty instance.
	 */
	public List<TplSpecTierQty> cloneTplSpecTierQty(TplSpecQuestion newSpecQuestion, List<TplSpecTierQty> specTierQties) {
		debugLogger("cloneTplSpecTierQty : " + newSpecQuestion);
		
		List<TplSpecTierQty> newSpecTierQties = null;

		if (newSpecQuestion != null) {
			if (specTierQties != null && !specTierQties.isEmpty()) {	
				newSpecTierQties = new ArrayList<TplSpecTierQty>();
				
				for (TplSpecTierQty specTierQty : specTierQties) {
					try {
						TplSpecTierQty newSpecTierQty = (TplSpecTierQty) BeanUtils.cloneBean(specTierQty);
						
						if (newSpecTierQty != null) {
							newSpecTierQty.setSpecTierQtyId(null);
							newSpecTierQty.setTplSpecQuestion(newSpecQuestion);
							setUserDetails(newSpecTierQty);
							newSpecTierQties.add(newSpecTierQty);

						}
					} catch (IllegalAccessException
							| InstantiationException
							| InvocationTargetException
							| NoSuchMethodException e) {
						log.error(e);
						log.warn("clone " + specTierQty + ": " + specTierQty.getSpecTierQtyId() + " failed.");
					}
				}
			}
			newSpecQuestion.setTplSpecTierQties(newSpecTierQties);	
		}
		
		return newSpecTierQties;
	}
	
	/**
	 * Clone the list of Spec Scoring.
	 * 
	 * @param newSpecMaster
	 * 		the new TplSpecMaster cloned.
	 * @param specScorings
	 * 		the list of TplSpecScoring to be clone.
	 * @return	the list of new cloned TplSpecScoring instance.
	 */
	public List<TplSpecScoring> cloneTplSpecScoring(TplSpecQuestion newSpecQuestion, List<TplSpecScoring> specScorings) {
		debugLogger("cloneTplSpecScoring : " + newSpecQuestion);
		
		List<TplSpecScoring> newSpecScorings = null;
		
		if (newSpecQuestion != null) {
			if (specScorings != null && !specScorings.isEmpty()) {
				newSpecScorings = new ArrayList<TplSpecScoring>();
				
				for (TplSpecScoring specScoring : specScorings) {
					try {
						TplSpecScoring newSpecScoring = (TplSpecScoring) BeanUtils.cloneBean(specScoring);
						
						if (newSpecScoring != null) {

							newSpecScoring.setSpecScoringId(null);
							newSpecScoring.setTplSpecQuestion(newSpecQuestion);
							newSpecScoring.setScore(null);
							setUserDetails(newSpecScoring);
							newSpecScorings.add(newSpecScoring);
						}
					} catch (IllegalAccessException
							| InstantiationException
							| InvocationTargetException
							| NoSuchMethodException e) {
						log.error(e);
						log.warn("clone " + specScoring + ": " + specScoring.getSpecScoringId() + " failed.");
					}
					
				}
			}
			newSpecQuestion.setTplSpecScorings(newSpecScorings);	
		}
		
		return newSpecScorings;
	}
	
	/**
	 * Clone the list of Spec Estimations.
	 * 
	 * @param newSpecMaster
	 * 		the new TplSpecMaster cloned.
	 * @param specEstimations
	 * 		the list of TplSpecEstimation to be clone.
	 * @return	the list of new cloned TplSpecEstimation instance.
	 */
	public List<TplSpecEstimation> cloneTplSpecEstimation(TplSpecQuestion newSpecQuestion, List<TplSpecEstimation> specEstimations) {
		debugLogger("cloneTplSpecEstimation : " + newSpecQuestion);
		
		List<TplSpecEstimation> newSpecEstimations = null;
		
		if (newSpecQuestion != null) {
			if (specEstimations != null && !specEstimations.isEmpty()) {
				newSpecEstimations = new ArrayList<TplSpecEstimation>();
				
				for (TplSpecEstimation specEstimation : specEstimations) {
					try {
						// TODO - clone only standard price type.
						TplSpecEstimation newSpecEstimation = (TplSpecEstimation) BeanUtils.cloneBean(specEstimation);
						
						if (newSpecEstimation != null) {
							newSpecEstimation.setSpecEstimationId(null);
							newSpecEstimation.setTplSpecQuestion(newSpecQuestion);
							newSpecEstimation.setTplSpecZoneDtl(null);
							newSpecEstimation.setTplSpecTierQty(null);
							newSpecEstimation.setEstimatedCost(null);
							setUserDetails(newSpecEstimation);
							newSpecEstimations.add(newSpecEstimation);
						}
					} catch (IllegalAccessException
							| InstantiationException
							| InvocationTargetException
							| NoSuchMethodException e) {
						log.error(e);
						log.warn("clone " + specEstimation + ": " + specEstimation.getSpecEstimationId() + " failed.");
					}

				}
			}
			newSpecQuestion.setTplSpecEstimations(newSpecEstimations);	
		}
		
		return newSpecEstimations;
	}
	
	public void updateItemWeightageReq(TplSpecMaster specMaster, List<TplSpecQuestion> specQuestions) {
		boolean isFinancialSpec = ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equals(specMaster.getSpecTypeCode().getParameterCode());
		boolean isWeightageReq = specMaster.isWeightageReq();
		
		if (specQuestions != null) {
			for (TplSpecQuestion specQuestion : specQuestions) {
				boolean isQuestionTypeItem = TemplateSpecConst.QUESTION_TYPE_ITEM.equals(specQuestion.getQuestionType());
				
				if ((isFinancialSpec || (!isFinancialSpec && !isQuestionTypeItem))) {
					specQuestion.setWeightageReq(isWeightageReq);
				}
			}
		}
	}
	
	public boolean isPriceTypeStandard(String priceType) {
		return ParameterConst.PRICE_TYPE_STANDARD.equals(priceType)
				|| ParameterConst.PRICE_TYPE_PROMOTION.equals(priceType);
	}
	
	public boolean isPriceTypeZonalStandard(String priceType) {
		return ParameterConst.PRICE_TYPE_ZONAL_STANDARD.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_PROMOTION.equals(priceType);
	}
	
	public boolean isPriceTypeTier(String priceType) {
		return ParameterConst.PRICE_TYPE_TIER.equals(priceType)
				|| ParameterConst.PRICE_TYPE_PROMOTION_TIER.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_TIER.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER.equals(priceType)
				|| isPriceTypeTariff(priceType);
	}
	
	public boolean isPriceTypeTariff(String priceType) {
		return ParameterConst.PRICE_TYPE_TARIFF.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_TARIFF.equals(priceType);
	}
	
	public boolean isTierPriceMatched(TplSpecTierQty specTierQty, BigDecimal orderQuantity) {
		
		if (specTierQty.getFromQty() != null && specTierQty.getToQty() != null) {
			if (specTierQty.getFromQty().compareTo(orderQuantity) <= 0
					&& orderQuantity.compareTo(specTierQty.getToQty()) <= 0) {
				return true;
			}
		} else {
			log.warn("Invalid from and to qty.");
		}

		return false;
	}
	
	public void resetEstimations(TplSpecQuestion specQuestion) {
		List<TplSpecEstimation> specEstimations = null;
		if (specQuestion.getTplSpecMaster().isEstCostReq() && 
				isPriceTypeStandard(specQuestion.getClPriceTypeCode().getParameterCode())) {
			
			specEstimations = new ArrayList<TplSpecEstimation>();
			
			TplSpecEstimation specEstimation = new TplSpecEstimation();
			specEstimation.setTplSpecQuestion(specQuestion);
			
			specEstimations.add(specEstimation);
		}
		
		specQuestion.setTplSpecEstimations(specEstimations);
	}
	
	private void setUserDetails(BaseEntity baseObject) {
		PmUser user = new FacesScopes().getUserSessionBean();
		
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(user.getUserId());
			baseObject.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(user.getUserId());
	}
}