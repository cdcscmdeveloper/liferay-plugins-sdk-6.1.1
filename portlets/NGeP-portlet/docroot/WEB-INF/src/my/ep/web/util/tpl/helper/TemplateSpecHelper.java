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
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecEstimation;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.entity.tpl.spec.TplSpecSuppAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;
import my.ep.persistence.entity.tpl.spec.TplSpecZone;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
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
	public TplSpecMaster cloneTplSpecMaster(TplSpecMaster specMaster, boolean includeQuestions, boolean isScoringReq, boolean isQtyByZone) {
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
					newSpecMaster.setScQtDoc(null);
					newSpecMaster.setScQuoteDocs(null);
					newSpecMaster.setScoringReq(isScoringReq);
					newSpecMaster.setQtyByZone(isQtyByZone);
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
				TplSpecQuestion prevNewSpecQuestion = null;
				
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
								newSpecQuestion.setTplSpecEstimations(null);
								newSpecQuestion.setTplSpecScorings(null);
								
								if (isTechnicalSpec) {
									if(TemplateSpecConst.QUESTION_TYPE_ITEM.equals(newSpecQuestion.getQuestionType())){
										newSpecQuestion.setAnswerTypeCode(null);
									} else{
										if (newSpecQuestion.getAnswerTypeCode() == null) {
											newSpecQuestion.setAnswerTypeCode(new PmParameter());
										}
									}
									
									// clone spec scoring
									if (newSpecMaster.isScoringReq()) {
										cloneTplSpecScoring(newSpecQuestion, specQuestion.getTplSpecScorings());	
									} else {
										newSpecQuestion.setScoreTypeCode(null);
										newSpecQuestion.setMaxScore(null);
									}
									
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
									
									resetEstimations(newSpecQuestion);
									resetPriceType(newSpecMaster, newSpecQuestion);
									
									newSpecQuestion.setScoreTypeCode(null);
									newSpecQuestion.setMaxScore(null);
								}
								
								newSpecQuestion.setWeightageReq(false);
								newSpecQuestion.setWeightage(null);
								
								if (prevNewSpecQuestion != null && TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC.equals(newSpecQuestion.getQuestionType())) {
									String prevQuestionType = prevNewSpecQuestion.getQuestionType();
									
									if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(prevQuestionType)) {
										newSpecQuestion.setParentSpecQuestion(prevNewSpecQuestion);
									} else if (TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC.equals(prevQuestionType)) {
										newSpecQuestion.setParentSpecQuestion(prevNewSpecQuestion.getParentSpecQuestion());
									} else {
										newSpecQuestion.setParentSpecQuestion(null);
									}
								} else {
									newSpecQuestion.setParentSpecQuestion(null);
								}
								
								// clone spec quantities
								cloneTplSpecTierQty(newSpecQuestion, specQuestion.getTplSpecTierQties());	
								
								// clone spec zone quantities.
								cloneTplSpecZoneQty(newSpecQuestion, specQuestion.getTplSpecZoneQties());
								
								setUserDetails(newSpecQuestion);
								newSpecQuestions.add(newSpecQuestion);
								
								prevNewSpecQuestion = newSpecQuestion;
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
							//newSpecScoring.setScore(null);
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
							newSpecEstimation.setBrandNew(true);
							newSpecEstimation.setTplSpecQuestion(newSpecQuestion);
							//newSpecEstimation.setTplSpecZoneDtl(null);
							//newSpecEstimation.setTplSpecTierQty(null);
							//newSpecEstimation.setEstimatedCost(null);
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
	
	/**
	 * Clone the list of Spec Zone Quantities.
	 * 
	 * @param newSpecMaster
	 * 		the new TplSpecMaster cloned.
	 * @param specZoneQties
	 * 		the list of TplSpecZoneQty to be clone.
	 * @return	the list of new cloned TplSpecZoneQty instance.
	 */
	public List<TplSpecZoneQty> cloneTplSpecZoneQty(TplSpecQuestion newSpecQuestion, List<TplSpecZoneQty> specZoneQties) {
		debugLogger("cloneTplSpecZoneQty : " + newSpecQuestion);
		
		List<TplSpecZoneQty> newSpecZoneQties = null;

		if (newSpecQuestion != null) {
			if (specZoneQties != null && !specZoneQties.isEmpty()) {	
				newSpecZoneQties = new ArrayList<TplSpecZoneQty>();
				
				for (TplSpecZoneQty specZoneQty : specZoneQties) {
					try {
						TplSpecZoneQty newSpecZoneQty = (TplSpecZoneQty) BeanUtils.cloneBean(specZoneQty);
						
						if (newSpecZoneQty != null) {
							newSpecZoneQty.setSpecZoneQtyId(null);
							newSpecZoneQty.setBrandNew(true);
							newSpecZoneQty.setTplSpecQuestion(newSpecQuestion);
							setUserDetails(newSpecZoneQty);
							newSpecZoneQties.add(newSpecZoneQty);

						}
					} catch (IllegalAccessException
							| InstantiationException
							| InvocationTargetException
							| NoSuchMethodException e) {
						log.error(e);
						log.warn("clone " + specZoneQty + ": " + specZoneQty.getSpecZoneQtyId() + " failed.");
					}
				}
			}
			newSpecQuestion.setTplSpecZoneQties(newSpecZoneQties);	
		}
		
		return newSpecZoneQties;
	}
	
	/**
	 * Update Item Weightage Required
	 * 
	 * @param specMaster
	 * 		the TplSpecMaster instance
	 * @param specQuestions
	 * 		the list of TplSpecQuestion instance
	 */
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
	
	/**
	 * Check price type is standard
	 * 
	 * @param priceType
	 * 		the price type
	 * @return	the indicator price type is standard
	 */
	public boolean isPriceTypeStandard(String priceType) {
		return ParameterConst.PRICE_TYPE_STANDARD.equals(priceType)
				|| ParameterConst.PRICE_TYPE_PROMOTION.equals(priceType);
	}
	
	/**
	 * Check price type is zonal standard
	 * 
	 * @param priceType
	 * 		the price type
	 * @return	the indicator price type is zonal standard
	 */
	public boolean isPriceTypeZonalStandard(String priceType) {
		return ParameterConst.PRICE_TYPE_ZONAL_STANDARD.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_PROMOTION.equals(priceType);
	}
	
	/**
	 * Check price type is tier
	 * 
	 * @param priceType
	 * 		the price type
	 * @return	the indicator price type is tier
	 */
	public boolean isPriceTypeTier(String priceType) {
		return ParameterConst.PRICE_TYPE_TIER.equals(priceType)
				|| ParameterConst.PRICE_TYPE_PROMOTION_TIER.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_TIER.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER.equals(priceType)
				|| isPriceTypeTariff(priceType);
	}
	
	/**
	 * Check price type is tariff
	 * 
	 * @param priceType
	 * 		the price type
	 * @return	the indicator price type is tariff
	 */
	public boolean isPriceTypeTariff(String priceType) {
		return ParameterConst.PRICE_TYPE_TARIFF.equals(priceType)
				|| ParameterConst.PRICE_TYPE_ZONAL_TARIFF.equals(priceType);
	}
	
	/**
	 * Reset Estimation Details
	 * 
	 * @param specQuestion
	 * 		the TplSpecQuestion instance 
	 */
	public void resetEstimations(TplSpecQuestion specQuestion) {
		TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
		List<TplSpecEstimation> specEstimations = null;
		
		if (specMaster.isEstCostReq() && specMaster.getTplSpecZone() == null) {
			
			specEstimations = new ArrayList<TplSpecEstimation>();
			
			TplSpecEstimation specEstimation = new TplSpecEstimation();
			specEstimation.setTplSpecQuestion(specQuestion);
			
			specEstimations.add(specEstimation);
		}
		
		specQuestion.setTplSpecEstimations(specEstimations);
	}
	
	/**
	 * Reset Spec Zone Quantities
	 * 
	 * @param specQuestion
	 * 		the TplSpecQuestion instance 
	 */
	public void resetSpecZoneQuanties(TplSpecQuestion specQuestion) {
		TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
		
		List<TplSpecZoneQty> specZoneQtiesToBeDeleted 
					= specMaster.getSpecZoneQtiesToDelete();
		if (CollectionUtils.isNotEmpty(
				specQuestion.getTplSpecZoneQties())) {
			if (specZoneQtiesToBeDeleted == null) {
				specZoneQtiesToBeDeleted = new ArrayList<TplSpecZoneQty>();
				specMaster.setSpecZoneQtiesToDelete(specZoneQtiesToBeDeleted);
			}
			for (TplSpecZoneQty tplSpecZoneQty :
						specQuestion.getTplSpecZoneQties()) {
				if (!tplSpecZoneQty.isBrandNew()) {
					tplSpecZoneQty.setDeleted(true);
					specZoneQtiesToBeDeleted.add(tplSpecZoneQty);
				}
			}
			specQuestion.setTplSpecZoneQties(null);
		}
	}	
	
	/**
	 * Update Zone Details
	 * 
	 * @param specMaster
	 * 		the TplSpecMaster instance
	 * @param specZone
	 * 		the TplSpecZone instance
	 */
	public void updateZoneDtl(TplSpecMaster specMaster, TplSpecZone specZone) {
		specMaster.setTplSpecZone(specZone);
		if(CollectionUtils.isNotEmpty(specMaster.getTplSpecQuestions())){
		for (TplSpecQuestion specQuestion : specMaster.getTplSpecQuestions()) {
			if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(specQuestion.getQuestionType())) {
				resetPriceType(specMaster, specQuestion);
			}
		}
		}
	}
	
	/**
	 * Reset Price Type Details
	 * 
	 * @param specMaster
	 * 		the TplSpecMaster instance
	 * @param specQuestion
	 * 		the TplSpecQuestion instance 
	 */
	public void resetPriceType(TplSpecMaster specMaster, TplSpecQuestion specQuestion) {
		boolean isZonal = specMaster.getTplSpecZone() != null;
		PmParameter clPriceTypeCode = specQuestion.getClPriceTypeCode();
		PmParameter newPriceTypeCode = null;
		
		if (ParameterConst.ITEM_TYPE_SERVICE.equals(specMaster.getItemTypeCode().getParameterCode())) {			
			newPriceTypeCode = getStandardPriceType(isZonal);		
			
		} else {
			if (clPriceTypeCode != null && clPriceTypeCode.getParameterId() != null) {
				if (isPriceTypeTier(clPriceTypeCode.getParameterCode())) {
					newPriceTypeCode = getTierPriceType(isZonal);
				} else {
					newPriceTypeCode = getStandardPriceType(isZonal);
				}
			} 
			else {
				newPriceTypeCode = new PmParameter();
			}
		}
		specQuestion.setClPriceTypeCode(newPriceTypeCode);	
		
		if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equals(specMaster.getSpecTypeCode().getParameterCode())) {
			resetEstimations(specQuestion);	
		}
	}
	
	private PmParameter getStandardPriceType(boolean isZonal) {		
		PmParameter priceTypeCode = null;
		String priceType = null;
		
		if (isZonal) {
			priceType = ParameterConst.PRICE_TYPE_ZONAL_STANDARD;
		} else {
			priceType = ParameterConst.PRICE_TYPE_STANDARD;
		}
		
		priceTypeCode = ParameterHelper.getInstance().getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, priceType);
		
		return priceTypeCode;
	}
	
	private PmParameter getTierPriceType(boolean isZonal) {		
		PmParameter priceTypeCode = null;
		String priceType = null;
		
		if (isZonal) {
			priceType = ParameterConst.PRICE_TYPE_ZONAL_TIER;
		} else {
			priceType = ParameterConst.PRICE_TYPE_TIER;
		}
		
		priceTypeCode = ParameterHelper.getInstance().getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, priceType);
		
		return priceTypeCode;
	}
	
	private void setUserDetails(BaseEntity baseObject) {
		PmUser user = new FacesScopes().getUserSessionBean();
		
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(user.getUserId());
			baseObject.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(user.getUserId());
		baseObject.setChangedDate(null);
	}
	
	/**
	 * Calculate total amount per item
	 * 
	 * @param specQuestion
	 * 		the TplSpecQuestion instance 
	 * @param unitPrice
	 * 		the unit price
	 * @param quantity
	 * 		the quantity
	 * @return the total amount per item
	 */
	public BigDecimal calcTotalAmount(TplSpecQuestion specQuestion, BigDecimal unitPrice, BigDecimal quantity) {
		BigDecimal totalPrice = null;
		boolean isPriceTypeTier = isPriceTypeTier(
				specQuestion.getClPriceTypeCode().getParameterCode());
		
		if (!isPriceTypeTier) {
			totalPrice = calulateTotalAmount(specQuestion, unitPrice, quantity);
		}
		
		return totalPrice;
	}
	
	/**
	 * Calculate total amount per item including price type tier
	 * 
	 * @param specQuestion
	 * 		the TplSpecQuestion instance 
	 * @param unitPrice
	 * 		the unit price
	 * @param quantity
	 * 		the quantity
	 * @return the total amount per item
	 * 
	 */	
	public BigDecimal calculateTotalAmount(TplSpecQuestion specQuestion, BigDecimal unitPrice, BigDecimal quantity) {
		BigDecimal totalPrice = null;
		
		totalPrice = calulateTotalAmount(specQuestion, unitPrice, quantity);
		
		return totalPrice;
	}	
	
	private BigDecimal calulateTotalAmount(TplSpecQuestion specQuestion, BigDecimal unitPrice, 
			BigDecimal quantity) {
		BigDecimal totalPrice = null;
		TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
		boolean isItemTypeService = ParameterConst.ITEM_TYPE_SERVICE.equals(
										specMaster.getItemTypeCode().getParameterCode());
		if (unitPrice != null && quantity != null) {
			totalPrice = unitPrice.multiply(quantity);
			
			if (isItemTypeService) {
				BigDecimal uomFrequency = specQuestion.getClUomFrequency();
				BigDecimal uomPerDay = specQuestion.getClUomPerDay();
				BigDecimal uomPerMonth = specQuestion.getClUomPerMonth();
				BigDecimal itemDurationMonth = specQuestion.getClDurationMonth();
				
				if (uomFrequency != null) {
					totalPrice = totalPrice.multiply(uomFrequency);
				}
				
				if (uomPerDay != null) {
					totalPrice = totalPrice.multiply(uomPerDay);
				}
				
				if (uomPerMonth != null) {
					totalPrice = totalPrice.multiply(uomPerMonth);
				}
				
				if (itemDurationMonth != null) {
					totalPrice = totalPrice.multiply(itemDurationMonth);
				} else {
					BigDecimal durationMonth = specQuestion.getTplSpecMaster().getDurationMonth();
					
					if (durationMonth != null) {
						totalPrice = totalPrice.multiply(durationMonth);
					}
				}
			}	
		}		
		
		return totalPrice;
	}
	
	/**
	 * Reset Price Type Details
	 * 
	 * @param specQuestion
	 * 		the TplSpecQuestion instance 
	 */
	public void resetPriceTypeDtl(TplSpecQuestion specQuestion) {
		specQuestion.setTplSpecTierQties(null);

		resetEstimations(specQuestion);
	}
	
	/**
	 * Reset Specification Answers
	 * 
	 * @param templateSpecBean
	 * 		the template specification bean
	 * @param specQuestion
	 * 		the template specification question
	 */
	public void resetSpecAnswers(TemplateSpecBean templateSpecBean,
			TplSpecQuestion specQuestion) {
		
		if (TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER
				.equals(templateSpecBean.getContentType())) {
			List<TplSpecAnswer> specAnswers = null;

			if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(specQuestion.getQuestionType()) && isPriceTypeStandard(specQuestion
					.getClPriceTypeCode().getParameterCode())) {
				TplSpecAnswer specAnswer = createNewSpecAnswer(
						templateSpecBean, specQuestion, null, null);

				specAnswers = new ArrayList<TplSpecAnswer>();
				specAnswers.add(specAnswer);
			}

			addToBeRemovedSpecAnswers(templateSpecBean,
					specQuestion.getTplSpecAnswers());

			specQuestion.setTplSpecAnswers(specAnswers);
		}
	}
	
	/**
	 * Create new specification answer
	 * 
	 * @param templateSpecBean
	 * 		the template specification bean
	 * @param specQuestion
	 * 		the template specification question
	 * @param specTierQty
	 * 		the template specification tier quantity
	 * @return	the TplSpecAnswer instance
	 */
	public TplSpecAnswer createNewSpecAnswer(TemplateSpecBean templateSpecBean, TplSpecQuestion specQuestion, TplSpecZoneDtl specZoneDtl,
			TplSpecTierQty specTierQty) {
		
		TplSpecZoneQty tplSpecZoneQty = null;
		TplSpecAnswer newSpecAnswer = new TplSpecAnswer();
		
		newSpecAnswer.setTplSpecQuestion(specQuestion);
		newSpecAnswer.setTplSpecTierQty(specTierQty);
		
		if (specZoneDtl != null && templateSpecBean.getSpecMaster().isQtyByZone()) {
			// Get TplSpecZoneQty by zone.
			List<TplSpecZoneQty> specZoneQties = specQuestion.getTplSpecZoneQties();
			if (specZoneQties != null) {
				for (TplSpecZoneQty specZoneQty : specZoneQties) {
					if (specZoneQty != null && 
							(specZoneQty.getTplSpecZoneDtl().equals(specZoneDtl) ||
									(specZoneQty.getTplSpecZoneDtl().getSpecZoneId() != null && specZoneQty.getTplSpecZoneDtl().getSpecZoneId().equals(specZoneDtl.getSpecZoneId())))) {
						tplSpecZoneQty = specZoneQty;
						break;
					}
				}	
			}
			newSpecAnswer.setTplSpecZoneQty(tplSpecZoneQty);	
		}
		
		setUserDetails(newSpecAnswer);
		
		TplSpecSuppAnswer newSpecSuppAnswer = new TplSpecSuppAnswer();
		newSpecSuppAnswer.setSmSupplier(templateSpecBean.getSupplier());
		newSpecSuppAnswer.setTplSpecZoneDtl(specZoneDtl);
		newSpecAnswer.setTplSpecSuppAnswer(newSpecSuppAnswer);
		setUserDetails(newSpecSuppAnswer);
		
		return newSpecAnswer;
	}
	
	/**
	 * Add Spec Answer into to be removed list.
	 * 
	 * @param templateSpecBean
	 * 		the template specification bean
	 * @param specAnswers
	 * 		the list of TplSpecAnswer instance
	 */
	public void addToBeRemovedSpecAnswers(TemplateSpecBean templateSpecBean,
			List<TplSpecAnswer> specAnswers) {
		if (specAnswers != null && specAnswers.size() > 0) {
			List<TplSpecAnswer> specAnswersToDelete = templateSpecBean
					.getSpecAnswersToDelete();
			if (specAnswersToDelete == null) {
				specAnswersToDelete = new ArrayList<TplSpecAnswer>();
				templateSpecBean.setSpecAnswersToDelete(specAnswersToDelete);
			}

			for (TplSpecAnswer specAnswer : specAnswers) {
				specAnswer.setDeleted(true);

				if (!specAnswer.isBrandNew()) {
					specAnswersToDelete.add(specAnswer);
				}
			}
		}
	}
	
	/**
	 * copy scoring and estimated cost during technical specification complete action.
	 * 
	 * @param newSpecMaster
	 * 		the new specification master instance
	 * @param oldSpecMaster
	 * 		the old specification master instance
	 */
	public void copySpecDetails(TplSpecMaster newSpecMaster, TplSpecMaster oldSpecMaster) {
		if (newSpecMaster != null && oldSpecMaster != null && 
				newSpecMaster.getTplSpecQuestions() != null && oldSpecMaster.getTplSpecQuestions() != null &&
				newSpecMaster.getTplSpecQuestions().size() > 0 && oldSpecMaster.getTplSpecQuestions().size() > 0) {
			
			List<TplSpecQuestion> oldSpecQuestions = new ArrayList<TplSpecQuestion>(oldSpecMaster.getTplSpecQuestions());
			for (TplSpecQuestion newSpecQuestion : newSpecMaster.getTplSpecQuestions()) {
				for (TplSpecQuestion oldSpecQuestion : oldSpecQuestions) {
					if (newSpecQuestion.getClItem() != null && newSpecQuestion.getClItem().equals(oldSpecQuestion.getClItem())) {
						copySpecQuestionDetails(newSpecQuestion, oldSpecQuestion);
						oldSpecQuestions.remove(oldSpecQuestion);
						break;
					}
				}
			}
		}
	}
	
	private void copySpecQuestionDetails(TplSpecQuestion newSpecQuestion, TplSpecQuestion oldSpecQuestion) {
		PmParameter scoreTypeCode = oldSpecQuestion.getScoreTypeCode();
		
		newSpecQuestion.setAnswerTypeCode(oldSpecQuestion.getAnswerTypeCode());
		newSpecQuestion.setScoreTypeCode(scoreTypeCode);
		newSpecQuestion.setMaxScore(oldSpecQuestion.getMaxScore());
		newSpecQuestion.setWeightageReq(oldSpecQuestion.isWeightageReq());
		newSpecQuestion.setWeightage(oldSpecQuestion.getWeightage());
		
		// copy tplSpecScorings
		if (scoreTypeCode != null && ParameterConst.SCORE_TYPE_AUTO.equals(scoreTypeCode.getParameterCode())) {
			cloneTplSpecScoring(newSpecQuestion, oldSpecQuestion.getTplSpecScorings());
		}
		
		// copy tplSpecEstimations
		cloneTplSpecEstimation(newSpecQuestion, oldSpecQuestion.getTplSpecEstimations());
	}
}