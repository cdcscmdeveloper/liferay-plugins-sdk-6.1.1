package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.CDProcessNameConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRecommendCode;
import my.ep.persistence.entity.cm.catalog.CmRemark;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.cm.helper.CmApplicationUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "itemCodeCreationBackingBean")
@RequestScoped
public class ItemCodeCreationBackingBean extends
BaseBackingBean<UnspscCodeManagedBean>implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ItemCodeCreationBackingBean.class);

//	private SearchFilterBean colorFilterBean = new SearchFilterBean(
//			SearchOperator.OPERATOR_EQUALS.value());
//	private SearchFilterBean typeFilterBean = new SearchFilterBean(
//			SearchOperator.OPERATOR_EQUALS.value());
//	private SearchFilterBean brandFilterBean = new SearchFilterBean(
//			SearchOperator.OPERATOR_EQUALS.value());
//	private SearchFilterBean measurementFilterBean = new SearchFilterBean(
//			SearchOperator.OPERATOR_EQUALS.value());
//	
//	
//	
//	private SearchFilterBean unspscIdFilterBean = new SearchFilterBean(
//			SearchOperator.OPERATOR_EQUALS.value());
	
	public static final String NO_69 = "69";

	public ItemCodeCreationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}


	public boolean isCodeAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_OFFICER");
	}

	public boolean isCdCommittee() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_COMMITTEE");
	}


	
	public static String getParentCode(long code) {

		String parent = null;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			parent = (String) String.valueOf(code).substring(0, 6) + "00";

			log.debug("parent:" + parent);
		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				parent = (String) String.valueOf(code).substring(0, 4) + "0000";
				log.debug("parent:" + parent);
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					parent = (String) String.valueOf(code).substring(0, 2)
							+ "000000";
					log.debug("parent:" + parent);
				} else {
					log.debug("segment");
				}

			}

		}

		return parent;
	}
	

	public Long checkUnspscLevel(long code) {

		long level = 0;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			level = 3;

		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				level = 2;
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					level = 1;
				} else {
					log.debug("segment");
					level = 0;
				}

			}

		}

		return level;
	}
	
	public boolean productCode(String unspscCode) {

		boolean flag = true;
		String segmentCode = "";

		if (unspscCode != null && unspscCode.trim().length() > 0) {
			segmentCode = unspscCode.substring(0, 2);
		}

		if (segmentCode.compareTo(NO_69) > 0) {
			flag = false;
		}

		return flag;
	}

	

	public void addNewUnspsc(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		FacesScopes face = new FacesScopes();
		//Object token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		ItemCodeCreationBackingBean bb = new ItemCodeCreationBackingBean();
		PmUser pmUser = new FacesScopes().getUserSessionBean();

	IStatusSO statusSO = (IStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("pm", IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();


		
		
			log.info("***this.getManagedBean().isNewSegment()"
					+ this.getManagedBean().isNewSegment());

			if (this.getManagedBean().getSelectedLovUnspscSegment()
					.getUnspscCode().isEmpty()
					|| this.getManagedBean().getSelectedLovUnspscfamily()
					.getUnspscCode().isEmpty()
					|| this.getManagedBean().getSelectedLovUnspscClass()
					.getUnspscCode().isEmpty()
					|| this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode().isEmpty()) {

				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715002,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));
				return;

			}





			boolean isDigitSegment = this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().matches("\\d{8}"); 
			boolean isDigitFamily = this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().matches("\\d{8}"); 
			boolean isDigitClass = this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().matches("\\d{8}"); 
			boolean isDigitCommodity = this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().matches("\\d{8}"); 

			if(!(isDigitSegment && isDigitFamily && isDigitClass && isDigitCommodity)){

				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715066,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));
				return;

			}
			
			
			if (this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitle().isEmpty() || this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitleEng().isEmpty()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				return;
				
			}else{



			if (this.getManagedBean().getSelectedLovUnspscSegment()
					.isBrandNew()) {

				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscSegment().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitleEng());
					unspsc.setUnspscLevel(0);

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());
				}else{
					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					return;


				}

			} else {
				
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscSegment().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitleEng());
					unspsc.setUnspscLevel(0);

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if(StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().substring(0,2),8,'0').equals(StringUtils.rightPad(this.getManagedBean().getRequestCode().getReviewedTempCode().substring(0,2),8,'0'))){
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspscSegment().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspscSegment().getUnspscTitleEng());
							reqDTO.getUnspscList().add(resDTO.getUnspscObj());
						}
					}
				}
			}
		}	
			// //// start family
			
			if (this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitle().isEmpty() || this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitleEng().isEmpty()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				return;
				
			}else{

			if (this.getManagedBean().getSelectedLovUnspscfamily()
					.isBrandNew()) {

				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscfamily().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					System.out
					.println("this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()"
							+ this.getManagedBean()
							.getSelectedLovUnspscfamily()
							.getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitleEng());
					unspsc.setUnspscLevel(1);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
				}else{

					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					return;
				}
			} else {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscfamily().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitleEng());
					unspsc.setUnspscLevel(1);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if( StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().substring(0,4),8,'0').equals(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,2).concat( this.getManagedBean().getRequestCode().getReviewedTempCode().substring(2,4)),8,'0'))){
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspscfamily().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspscfamily().getUnspscTitleEng());
							reqDTO.getFamilyList().add(resDTO.getUnspscObj());
						}
					}
				}
			}
		}
			// ///start class
			
			if (this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitle().isEmpty() || this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitleEng().isEmpty()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				return;
				
			}else{

			if (this.getManagedBean().getSelectedLovUnspscClass()
					.isBrandNew()) {

				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscClass().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					System.out
					.println("this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()"
							+ this.getManagedBean()
							.getSelectedLovUnspscClass()
							.getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitleEng());
					unspsc.setUnspscLevel(2);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getClassList().add(reqDTO.getCmUnspsc());
				}else{

					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					return;
				}

			} else {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscClass().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitleEng());
					unspsc.setUnspscLevel(2);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getClassList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if(StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().substring(0,6),8,'0').equals(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,4).concat(this.getManagedBean().getRequestCode().getReviewedTempCode().substring(4,6)),8,'0'))){
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspscClass().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspscClass().getUnspscTitleEng());
							reqDTO.getClassList().add(resDTO.getUnspscObj());
						}
					}
				}
			}
		}	
			// //start commodity
			
		if (this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle().isEmpty() || this.getManagedBean().getSelectedLovUnspsc().getUnspscTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
				return;
				
			}else{
			System.out
			.println(".....................this.getManagedBean().getSelectedLovUnspsc().isBrandNew()="
					+ this.getManagedBean().getSelectedLovUnspsc()
					.isBrandNew());
			if (this.getManagedBean().getSelectedLovUnspsc().isBrandNew()) {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspsc().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitleEng());
					unspsc.setUnspscLevel(3);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());
				}else{

					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspsc().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					return;
				}
			} else {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspsc().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscCode());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitleEng());
					unspsc.setUnspscLevel(3);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if( this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().equals(this.getManagedBean().getRequestCode().getTempCode().substring(0,6).concat(this.getManagedBean().getRequestCode().getReviewedTempCode().substring(6,8)))){
							
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspsc().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspsc().getUnspscTitleEng());
							reqDTO.getCommodityList().add(resDTO.getUnspscObj());
						}
					}
				}
			}
		}

			System.out
			.println("...................	reqDTO.getCommodityList().size="
					+ reqDTO.getCommodityList().size());

			String check68 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode()).substring(6, 8);
			String check46 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspscClass()
					.getUnspscCode()).substring(4, 6);
			String check24 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspscfamily()
					.getUnspscCode()).substring(2, 4);
			String check02 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspscSegment()
					.getUnspscCode()).substring(0, 2);

			String se = reqDTO.getUnspscList().size() > 0 ?  reqDTO.getUnspscList().get(0).getUnspscCode().substring(0,2) :"00";
			String fa = reqDTO.getFamilyList().size() > 0 ?  reqDTO.getFamilyList().get(0).getUnspscCode().substring(2,4) :"00";
			String cl = reqDTO.getClassList().size() > 0 ?  reqDTO.getClassList().get(0).getUnspscCode().substring(4,6) :"00";
			String co = reqDTO.getCommodityList().size() > 0 ?  reqDTO.getCommodityList().get(0).getUnspscCode().substring(6,8) :"00";

			String tempCode = check02 + check24 + check46 + check68;
			String reviewedTempCode = se+fa+cl+co;

			this.getManagedBean().getRequestCode().setTempCode(tempCode);
			this.getManagedBean().getRequestCode().setReviewedTempCode(reviewedTempCode);
			this.getManagedBean().getRequestCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());

			if (bb.productCode(tempCode)) {

				this.getManagedBean().getRequestCode().setRequestType(true);
				this.getManagedBean().getRequestCode()
				.setRequestBrand(true);
				this.getManagedBean().getRequestCode()
				.setRequestColor(true);
				this.getManagedBean().getRequestCode()
				.setRequestMeasurement(true);
			}

			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			//reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			//reqDTO.setOutCome(outcome);
			log.info("**********savetempCode************");

			//reqDTO.setCodeCommittees("user003,user051");
			reqDTO.setService(!bb.productCode(tempCode));

			if (!(this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscTitle() != null && this.getManagedBean()
					.getSelectedLovUnspsc().getUnspscTitle().trim()
					.length() > 0)
					|| !(this.getManagedBean().getSelectedLovUnspscClass()
							.getUnspscTitle() != null && this
							.getManagedBean().getSelectedLovUnspscClass()
							.getUnspscTitle().trim().length() > 0)
							|| !(this.getManagedBean().getSelectedLovUnspscfamily()
									.getUnspscTitle() != null && this
									.getManagedBean().getSelectedLovUnspscfamily()
									.getUnspscTitle().trim().length() > 0)
									|| !(this.getManagedBean()
											.getSelectedLovUnspscSegment().getUnspscTitle() != null && this
											.getManagedBean().getSelectedLovUnspscSegment()
											.getUnspscTitle().trim().length() > 0) 
							
											|| !(this.getManagedBean().getSelectedLovUnspsc()
													.getUnspscTitleEng() != null && this.getManagedBean()
													.getSelectedLovUnspsc().getUnspscTitleEng().trim()
													.length() > 0)
													|| !(this.getManagedBean().getSelectedLovUnspscClass()
															.getUnspscTitleEng() != null && this
															.getManagedBean().getSelectedLovUnspscClass()
															.getUnspscTitleEng().trim().length() > 0)
															|| !(this.getManagedBean().getSelectedLovUnspscfamily()
																	.getUnspscTitleEng() != null && this
																	.getManagedBean().getSelectedLovUnspscfamily()
																	.getUnspscTitleEng().trim().length() > 0)
																	|| !(this.getManagedBean()
																			.getSelectedLovUnspscSegment().getUnspscTitleEng() != null && this
																			.getManagedBean().getSelectedLovUnspscSegment()
																			.getUnspscTitleEng().trim().length() > 0)) {

				FacesContext facesContext = FacesContext
						.getCurrentInstance();

				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715009,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));
				return;

			}
			// checking hierarchy of segment, family, class and commodity
			if(!validateUnspscCodeHierarchy(this.getManagedBean()
					.getSelectedLovUnspscSegment().getUnspscCode(), this
					.getManagedBean().getSelectedLovUnspscfamily()
					.getUnspscCode(), this.getManagedBean()
					.getSelectedLovUnspscClass().getUnspscCode(), this
					.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode())){
				return;
			}
			
			log.info("tracking diary for Codification_Officer:savetempCode");
			reqDTO.setTrackingStatus(true);

			statusReqDTO.setStatusId(15004L);
			String actionDesc ="Kod Item "+this.getManagedBean().getRequestCode().getDocNo()+" telah diserah kepada CDC Working Committee untuk kelulusan. ";
			if(reqDTO.isService()){
				statusReqDTO.setStatusId( this.getManagedBean().isRenderV2() ? 15005L :15004L);
				actionDesc =  this.getManagedBean().isRenderV2() ? 
						"Kod Item "+this.getManagedBean().getRequestCode().getDocNo()+" telah diiklankan oleh "+pmUser.getUserName() :
						"Kod Item "+this.getManagedBean().getRequestCode().getDocNo()+" telah diserah kepada Pentadbir Kodifikasi untuk diiklankan." ;
			}
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CE_DocType));	

			UserReqDTO userReqDTO = new UserReqDTO();
			
			if(reqDTO.isService() && !this.getManagedBean().isRenderV2()){
				userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdAdminList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}
			}else{
				userReqDTO.setRoleCode(RoleCode.CD_COMMITTEE.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdcList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}
			}	
			//if(reqDTO.isService() && this.getManagedBean().isRenderV2()){
				log.info("Send for publishing.");
				reqDTO.setPojo(this.getManagedBean().getPayLoad());
				//reqDTO.setToken(token);
				resDTO = unspscSO.publishCodificationRequest(reqDTO);
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(MessageConst.INFO_710015,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return;
				
		//	}else{
			//	log.info("Send to CDC for ext endorsement.");
			//	resDTO = unspscSO.updateCodificationRequest(reqDTO);
			}
		
	//	}

	public boolean validateUnspscCodeHierarchy(String segnment, String family,
			String classC, String comodity) {
		String segmentIdentifier = segnment.substring(0, 2);
		String familyIdentifier = family.substring(0, 4);
		String classIdentifier = classC.substring(0, 6);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (!family.startsWith(segmentIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716011,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		} else if (!classC.startsWith(familyIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716011,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		} else if (!comodity.startsWith(classIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716011,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		//checking codes validity
		if(!segnment.substring(2).equals("000000")){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716013,
									new String[] { ResourceBundleUtils.getMessage("lbl.cm.segment",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		if(!family.substring(4).equals("0000")){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716013,
									new String[] { ResourceBundleUtils.getMessage("lbl.cm.family",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		if(!classC.substring(6).equals("00")){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716013,
									new String[] { ResourceBundleUtils.getMessage("lbl.cm.class",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		// checking for not redundant segment, familiy, class and commodity
		segmentIdentifier = segmentIdentifier + "00";
		if (segmentIdentifier.equals(familyIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716012,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		familyIdentifier = familyIdentifier + "00";
		if (familyIdentifier.equals(classIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716012,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		classIdentifier = classIdentifier + "00";
		if (classIdentifier.equals(comodity)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716012,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}

		return true;

	}


//	public SearchFilterBean getColorFilterBean() {
//		return colorFilterBean;
//	}
//
//	public void setColorFilterBean(SearchFilterBean colorFilterBean) {
//		this.colorFilterBean = colorFilterBean;
//	}
//
//	public SearchFilterBean getTypeFilterBean() {
//		return typeFilterBean;
//	}
//
//	public void setTypeFilterBean(SearchFilterBean typeFilterBean) {
//		this.typeFilterBean = typeFilterBean;
//	}
//
//	public SearchFilterBean getBrandFilterBean() {
//		return brandFilterBean;
//	}
//
//	public void setBrandFilterBean(SearchFilterBean brandFilterBean) {
//		this.brandFilterBean = brandFilterBean;
//	}
//
//	public SearchFilterBean getMeasurementFilterBean() {
//		return measurementFilterBean;
//	}
//
//	public void setMeasurementFilterBean(SearchFilterBean measurementFilterBean) {
//		this.measurementFilterBean = measurementFilterBean;
//	}


	public void removeRecommendCode(int index) {

		this.getManagedBean().getRequestCode().getCmRecommendCodes()
		.remove(index);

	}

	public void removeExtensionRecord() {

		this.getManagedBean().getRequestExtCodeList().remove(0);// there is only
		// one record

	}


	public List<CmType> autoCompleteType(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmType> suggestions = new ArrayList<CmType>();

		log.info("this.getManagedBean().getTypeList() = "
				+ this.getManagedBean().getTypeList().size());

		for (CmType type : this.getManagedBean().getTypeList()) {
			if (type.getTypeName() != null) {

				if (type.getTypeName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(type);
				}
			}
		}

		return suggestions;
	}

	public List<CmBrand> autoCompleteBrand(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmBrand> suggestions = new ArrayList<CmBrand>();

		for (CmBrand brand : this.getManagedBean().getBrandList()) {
			if (brand.getBrandName() != null) {
				if (brand.getBrandName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(brand);
				}
			}
		}

		return suggestions;
	}

	public List<CmMeasurement> autoCompleteMeasurement(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmMeasurement> suggestions = new ArrayList<CmMeasurement>();

		for (CmMeasurement brand : this.getManagedBean().getMeasurementList()) {
			if (brand.getMeasurementName() != null) {
				if (brand.getMeasurementName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(brand);
				}
			}
		}

		return suggestions;
	}

	public List<CmColor> autoCompleteColor(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmColor> suggestions = new ArrayList<CmColor>();

		for (CmColor color : this.getManagedBean().getColorList()) {
			if (color.getColorName() != null) {
				if (color.getColorName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(color);
				}
			}
		}

		return suggestions;
	}

	public boolean checkRemark(String param1, String param2) {

		if (!param1.equalsIgnoreCase(param2)) {
			return true;
		}
		return false;
	}

	public void requestNewColor() {

		if (this.getManagedBean().getRequestExtCode().isRequestColor()) {
			this.getManagedBean().setColorDisable(false);
		} else {
			this.getManagedBean().setColorDisable(true);
			this.getManagedBean().getRequestExtCode().setColorName("");
		}
	}

	public void requestNewType() {

		if (this.getManagedBean().getRequestExtCode().isRequestType()) {
			this.getManagedBean().setTypeDisable(false);

		} else {
			this.getManagedBean().setTypeDisable(true);
			this.getManagedBean().getRequestExtCode().setTypeName("");
		}
	}

	public void requestNewBrand() {

		if (this.getManagedBean().getRequestExtCode().isRequestBrand()) {
			this.getManagedBean().setBrandDisable(false);
		} else {
			this.getManagedBean().setBrandDisable(true);
			this.getManagedBean().getRequestExtCode().setBrandName("");
		}
	}

	public void requestNewMeasurement() {

		if (this.getManagedBean().getRequestExtCode().isRequestMeasurement()) {
			this.getManagedBean().setMeasurementDisable(false);
		} else {
			this.getManagedBean().setMeasurementDisable(true);
			this.getManagedBean().getRequestExtCode().setMeasurementName("");
		}
	}

	public void preRenderUnspscRequestView() {
		// clear requestCode object
		// this.getManagedBean().setRequestCode(new CmRequestCode());

		renewSesionObject();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "unspscRequestFormByMenu");
	}

	
	public String backToPendingCodified() {
		return "searchUnspscToAssign";
	}

	
	public String retrieveMofNo(long supplierId, String supplierType){

		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		String mofNo = "N/A";
		IMofAccountSO mofSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
		reqDTO.setSupplierId(supplierId);
		reqDTO.setSupplierType(supplierType);
		MofAccountResDTO resDTO = mofSO.retriveMOFAccountBySupplierIdAndType(reqDTO);

		if( resDTO.getMofAccount()!=null){
			mofNo = resDTO.getMofAccount().getMofNo();
		}

		return mofNo;
	}


	public void editExtensionRequest(int index){

		this.getManagedBean().setEditMode(true);
		this.getManagedBean().setEditModeIndex(index);
		if(this.getManagedBean().getRequestExtCodeList()!=null && this.getManagedBean().getRequestExtCodeList().size()>0){
			CmRequestCode code = this.getManagedBean().getRequestExtCodeList().get(index);


			ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICodificationSO.class);

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode());

			CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);

			this.getManagedBean()
			.getRequestExtCode()
			.setTempCode(
					this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode());
			this.getManagedBean()
			.getRequestExtCode()
			.setTempTitle(
					this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscTitle());
			this.getManagedBean().setTypeList(resDTO.getCommodityObj().getCmType());
			this.getManagedBean().setBrandList(
					resDTO.getCommodityObj().getCmBrand());
			this.getManagedBean().setMeasurementList(
					resDTO.getCommodityObj().getCmMeasurement());
			resDTO = codificationSO.searchColor(reqDTO);
			this.getManagedBean().setColorList(resDTO.getColorList());

			this.getManagedBean().setRequestExtCode(code);



			if(code.isRequestBrand()){
				log.info("********isRequestBrand*****"+this.getManagedBean().getRequestExtCode().isRequestBrand());
				this.getManagedBean().setSelectedBrand(null);
				this.getManagedBean().setBrandDisable(false);  //text box

			}else{
				this.getManagedBean().setSelectedBrand(new CmBrand());
				this.getManagedBean().getSelectedBrand().setBrandName(code.getBrandName());
				this.getManagedBean().getSelectedBrand().setBrandId(code.getBrandId());
				this.getManagedBean().setBrandDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setBrandName("");
			}


			if(code.isRequestType()){

				this.getManagedBean().setSelectedType(null);
				this.getManagedBean().setTypeDisable(false);  //text box

			}else{
				this.getManagedBean().setSelectedType(new CmType());
				this.getManagedBean().getSelectedType().setTypeName(code.getTypeName());
				this.getManagedBean().getSelectedType().setTypeId(code.getTypeId());
				this.getManagedBean().setTypeDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setTypeName("");

			}

			if(code.isRequestColor()){

				this.getManagedBean().setSelectedColor(null);
				this.getManagedBean().setColorDisable(false);  //text box


			}else{
				this.getManagedBean().setSelectedColor(new CmColor());
				this.getManagedBean().getSelectedColor().setColorName(code.getColorName());
				this.getManagedBean().getSelectedColor().setColorId(code.getColorId());
				this.getManagedBean().setColorDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setColorName("");

			}


			if(code.isRequestMeasurement()){

				this.getManagedBean().setSelectedMeasurement(null);
				this.getManagedBean().setMeasurementDisable(false);  //text box

			}else{
				this.getManagedBean().setSelectedMeasurement(new CmMeasurement());
				this.getManagedBean().getSelectedMeasurement().setMeasurementName(code.getMeasurementName());
				this.getManagedBean().getSelectedMeasurement().setMeasurementId(code.getMeasurementId());
				this.getManagedBean().setMeasurementDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setMeasurementName("");

			}

		}
	}

	public void deleteExtensionRequest(int index){



		this.getManagedBean().getRequestExtCodeList().remove(index);

	}

//	/**
//	 * @return the unspscIdFilterBean
//	 */
//	public SearchFilterBean getUnspscIdFilterBean() {
//		return unspscIdFilterBean;
//	}
//
//	/**
//	 * @param unspscIdFilterBean the unspscIdFilterBean to set
//	 */
//	public void setUnspscIdFilterBean(SearchFilterBean unspscIdFilterBean) {
//		this.unspscIdFilterBean = unspscIdFilterBean;
//	}
	
	public List<CmRemark> getLastRemarkBeforeCompletion(CmRequestCode requestCode){
		log.info("Passed request : " +requestCode.getDocNo());
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setRequestCode(requestCode);
		CodificationResDTO resDTO = codificationSO.getLastRemarkBeforeCompletion(reqDTO);
		return resDTO.getRemarkList();
	}
}
