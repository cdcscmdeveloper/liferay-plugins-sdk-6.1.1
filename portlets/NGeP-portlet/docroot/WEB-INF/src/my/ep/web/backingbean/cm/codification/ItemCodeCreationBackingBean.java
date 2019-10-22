package my.ep.web.backingbean.cm.codification;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


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
	
	

	public void addNewUnspsc(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		ItemCodeCreationBackingBean bb = new ItemCodeCreationBackingBean();
	

		
		
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

			String tempCode = check02 + check24 + check46 + check68;
			
			
			reqDTO.setTempCode(tempCode);
					
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
			
			
	       UserReqDTO userReqDTO = new UserReqDTO();
			
			if(isCodeAdmin()){
				log.info("**isCodeAdmin***");
				userReqDTO.setRoleCode(RoleCode.CD_COMMITTEE.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdAdminList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}
			}
				
	            resDTO = unspscSO.publishCodification(reqDTO);
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(MessageConst.INFO_710022,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return;	
				
				
	
		}

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
	
}
