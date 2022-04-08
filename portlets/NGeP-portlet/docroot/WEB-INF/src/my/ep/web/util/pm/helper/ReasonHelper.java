/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.pm.helper;

import java.io.Serializable;
import java.util.List;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Singleton Helper For Reason.
 * 
 * @author Anjie
 * @since 18.12.2015
 * 
 */
public class ReasonHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = EpLogger.getLogger(ReasonHelper.class);

	public static final String KEY_SEPARATOR = "_";

	private static ReasonHelper instance;
//	private static Map<String, PmReasonDesc> reasonDescMap = new HashMap<String, PmReasonDesc>();
	// Map of Pmreason with reasonId
//	private static Map<String, PmReasonDesc> reasonMap = new HashMap<String, PmReasonDesc>();
//	private static Map<String, List<PmReasonDesc>> reasonListMap = new HashMap<String, List<PmReasonDesc>>();

	/**
	 * Get singleton of ReasonHelper
	 * 
	 * @return ReasonHelper singleton
	 */
	public static ReasonHelper getInstance() {
		if (instance == null) {
			instance = new ReasonHelper();
		}
//		getReasonMapsFromCache();
		return instance;
	}

	/**
	 * Get a list of reason description by category code
	 * 
	 * @param categoryCode
	 *            the category code
	 * @return list of PmReasonDesc
	 */
	public List<PmReasonDesc> getReasonDescList(String categoryCode) {
		debugLogger("getReasonDescList.categoryCode: " + categoryCode);

		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = new StringBuilder().append(categoryCode)
				.append(KEY_SEPARATOR).append(languageCode).toString();

		debugLogger("key: " + key);

		// Get the reason description list from map.
		List<PmReasonDesc> list = null;//reasonListMap.get(key);

		debugLogger("list: " + list);

//		if (list == null) {
			IReasonSO reasonSO;

			try {
				reasonSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

				ReasonReqDTO reqDTO = new ReasonReqDTO();
				reqDTO.setReasonCategory(categoryCode);
				reqDTO.setLanguage(languageCode);

				// Get reason description list from db.
				ReasonResDTO resDTO = reasonSO
						.retrieveReasonDescListByCategory(reqDTO);

				list = resDTO.getReasonDescList();

				String reasonKey;
				Long categoryId = null;
				for (PmReasonDesc pmReasonDesc : list) {
					if (categoryId == null) {
						categoryId = pmReasonDesc.getPmReason()
								.getReasonCategoryCode().getParameterId();
					}
					PmReason reason = pmReasonDesc.getPmReason();

					reasonKey = getReasonDescKey(categoryId,
							reason.getReasonCode(), languageCode);

//					reasonDescMap.put(reasonKey, pmReasonDesc);
				}

				// Put reason description list into map.
//				reasonListMap.put(key, list);
//				updateReasonMapsCache(null, reasonDescMap, reasonListMap);
				debugLogger("put into list: " + list);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				reasonSO = null;
			}
//		}

		return list;
	}

	/**
	 * Get reason description by reason category and code.
	 * 
	 * @param categoryId
	 *            the category id
	 * @param reasonCode
	 *            the reason code
	 * @return the reason description
	 */
	public String getReasonDesc(Long categoryId, String reasonCode) {
		debugLogger("getReasonDesc.categoryId: " + categoryId
				+ ", reasonCode: " + reasonCode);

		PmReasonDesc pmReasonDesc = getReasonDescByCategoryAndCode(categoryId,
				reasonCode);

		if (pmReasonDesc != null) {
			return pmReasonDesc.getReasonDesc();
		} else {
			return null;
		}
	}

	/**
	 * Get reason description by reason id.
	 * 
	 * @param reasonId
	 *            the reason id.
	 * @return PmReasonDesc instance
	 */
	public PmReasonDesc getReasonDescById(Long reasonId) {
		debugLogger("getReasonDescById.reasonId: " + reasonId);

		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = getReasonKey(reasonId, languageCode).toString();

		// Get the status description list from map.
		PmReasonDesc reasonDesc = null;//reasonMap.get(key);

//		if (reasonDesc == null) {
			IReasonSO reasonSO;

			try {
				reasonSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

				ReasonReqDTO reqDTO = new ReasonReqDTO();
				reqDTO.setReasonId(reasonId);
				reqDTO.setLanguage(languageCode);

				// Get status description from db.
				ReasonResDTO resDTO = reasonSO.retrieveReasonDescById(reqDTO);

				reasonDesc = resDTO.getReasonDesc();

				// Put status description into map.
//				reasonMap.put(key, reasonDesc);
//				updateReasonMapsCache(reasonMap, null, null);
				debugLogger("put into list: " + reasonDesc);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				reasonSO = null;
			}
//		}

		return reasonDesc;
	}

	/**
	 * Get reason description String by reason id.
	 * 
	 * @param reasonId
	 *            the reason id.
	 * @return PmReasonDesc instance
	 */
	public String getReasonDescStringById(Long reasonId) {
		debugLogger("getReasonDescStringById.reasonId: " + reasonId);

		PmReasonDesc reasonDesc = getReasonDescById(reasonId);
		if (reasonDesc != null) {
			return reasonDesc.getReasonDesc();
		}
		return null;
	}

	public PmReasonDesc getReasonDescByCategoryAndCode(Long categoryId,
			String reasonCode) {
		debugLogger("getReasonDescByCategoryAndCode.categoryId: " + categoryId
				+ ", reasonCode: " + reasonCode);

		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		String key = getReasonDescKey(categoryId, reasonCode, languageCode);

		debugLogger("key: " + key);

		// Get the reason description list from map.
		PmReasonDesc pmReasonDesc = null;//reasonDescMap.get(key);

//		if (pmReasonDesc == null) {
			IReasonSO reasonSO;

			try {
				reasonSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

				ReasonReqDTO reqDTO = new ReasonReqDTO();

				ISearchBean<PmReasonDesc> searchBean = new SearchBean<PmReasonDesc>();
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmReason.reasonCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								reasonCode));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("languageCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								languageCode));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmReason.reasonCategoryCode.paramterId",
								SearchOperator.OPERATOR_EQUALS.value(),
								categoryId));

				// Get reason description list from db.
				ReasonResDTO resDTO = reasonSO
						.retrieveReasonDescBySearchBean(reqDTO);

				List<PmReasonDesc> reasonDescs = resDTO.getReasonDescList();

				for (PmReasonDesc reasonDesc : reasonDescs) {
					pmReasonDesc = reasonDesc;
					break;
				}

				// Put reason description list into map.
//				reasonDescMap.put(key, pmReasonDesc);
//				updateReasonMapsCache(null, reasonDescMap, null);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				reasonSO = null;
			}
//		}

		return pmReasonDesc;
	}

	public PmReason getReasonByCategoryAndCode(Long categoryId,
			String reasonCode) {
		debugLogger("getReasonByCategoryAndCode.categoryId: " + categoryId
				+ ", reasonCode: " + reasonCode);

		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = getReasonDescKey(categoryId, reasonCode, languageCode);

		debugLogger("key: " + key);

		// Get the reason description list from map.
		PmReason pmReason = null;//reasonDescMap.get(key);

		//if (pmReasonDesc == null) {
			IReasonSO reasonSO;

			try {
				reasonSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

				ReasonReqDTO reqDTO = new ReasonReqDTO();
				reqDTO.setReasonCategoryId(categoryId);
				reqDTO.setReasonCode(reasonCode);
//				reqDTO.setLanguage(languageCode);

				// Get reason description list from db.

				pmReason = reasonSO.retrieveReasonByReasonCode(reqDTO)
						.getPmReason();

				// Put reason description list into map.
				//reasonDescMap.put(key, pmReasonDesc);
//				updateReasonMapsCache(null, reasonDescMap, null);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				reasonSO = null;
			}
		//}

		return pmReason;
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	private String getReasonKey(Long reasonId, String languageCode) {
		return new StringBuilder().append(reasonId).append(KEY_SEPARATOR)
				.append(languageCode).toString();
	}

	private String getReasonDescKey(Long categoryId, String reasonCode,
			String languageCode) {
		return new StringBuilder().append(categoryId).append(KEY_SEPARATOR)
				.append(reasonCode).append(KEY_SEPARATOR).append(languageCode)
				.toString();
	}

	/*private static void getReasonMapsFromCache() {
		log.info("getReasonMapsFromCache");
		IReasonSO reasonSO;

		try {
			reasonSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

			ReasonResDTO resDTO = reasonSO.getReasonMapFromCache();
			reasonMap = resDTO.getReasonMap();
			reasonDescMap = resDTO.getReasonDescMap();
			reasonListMap =resDTO.getReasonListMap();
			
			if(reasonMap != null){
				log.info("reasonMap : "+reasonMap.toString());
				log.info("reasonMap size : "+reasonMap.size());
				log.info("reasonMap values <<<<<<<< : ");
				
				for (Map.Entry<String, PmReasonDesc> entry : reasonMap.entrySet()) {
				    String key = entry.getKey().toString();
				    PmReasonDesc value = entry.getValue();
				    log.info("key, " + key + " value " + value.getReasonDesc());
				    
				}
			}else{
				log.info("reasonMap null");
			}
			
		
			if(reasonDescMap != null){
				log.info("reasonDescMap : "+reasonDescMap.toString());
				log.info("reasonDescMap size : "+reasonDescMap.size());
				log.info("reasonDescMap values <<<<<<<< : ");
				
				for (Map.Entry<String, PmReasonDesc> entry : reasonDescMap.entrySet()) {
				    String key = entry.getKey().toString();
				    PmReasonDesc value = entry.getValue();
				    log.info("key, " + key + " value " + value.getReasonDesc());
				    
				}
			}else{
				log.info("reasonDescMap null");
			}
			
			
			if(reasonListMap != null){
				log.info("reasonListMap : "+reasonListMap.toString());
				log.info("reasonListMap size : "+reasonListMap.size());
				log.info("reasonListMap values <<<<<<<< : ");
				
				for (Map.Entry<String, List<PmReasonDesc>> entry : reasonListMap.entrySet()) {
				    String key = entry.getKey().toString();
				    List<PmReasonDesc> value = entry.getValue();
				    for(PmReasonDesc v: value){
				    	  log.info("key, " + key + " value " + v.getReasonDesc());
				    }
				}
			}else{
				log.info("reasonListMap null");
			}
			
			if(reasonMap == null){
				reasonMap = new HashMap<String, PmReasonDesc>();
			}

			if(reasonDescMap == null){
				reasonDescMap = new HashMap<String, PmReasonDesc>();
			}
			
			if(reasonListMap == null){
				reasonListMap = new HashMap<String, List<PmReasonDesc>>();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			reasonSO = null;
		}
	}
	
	private void updateReasonMapsCache(Map<String, PmReasonDesc> reasonMap,
			Map<String, PmReasonDesc> reasonDescMap,
			Map<String, List<PmReasonDesc>> reasonListMap) {
		log.info("updateReasonMapsCache");
		IReasonSO reasonSO;

		try {
			reasonSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setReasonMap(reasonMap);
			reqDTO.setReasonDescMap(reasonDescMap);
			reqDTO.setReasonListMap(reasonListMap);
			reasonSO.updateReasonCache(reqDTO);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			reasonSO = null;
		}
	}*/

}
