package my.ep.web.backingbean.cm.codification;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.logging.LogKey;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

//35503 Start
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.cm.AuditCMConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.backingbean.cm.audit.AuditCodificationManagementBackingBean;
import my.ep.web.backingbean.cm.audit.AuditCodificationManagementBackingBeanTemplate;
import my.ep.dto.cm.audit.AuditCodificationManagementDTO;
//35503 End

@ManagedBean(name = "itemCodeCreationBackingBean")
@RequestScoped
public class ItemCodeCreationBackingBean extends BaseBackingBean<UnspscCodeManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ItemCodeCreationBackingBean.class);

	public static final String NO_69 = "69";

	public ItemCodeCreationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}
	
	public String addNewUnspsc() {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		AuditCodificationManagementDTO auditDTO = new AuditCodificationManagementDTO(); //35503

		log.info("***this.getManagedBean().isNewSegment()" + this.getManagedBean().isNewSegment());

		if (this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().isEmpty()
				|| this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().isEmpty()
				|| this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().isEmpty()
				|| this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715008, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return null;

		}

		boolean isDigitSegment = this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().matches("\\d{8}");
		boolean isDigitFamily = this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().matches("\\d{8}");
		boolean isDigitClass = this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().matches("\\d{8}");
		boolean isDigitCommodity = this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().matches("\\d{8}");

		if (!(isDigitSegment && isDigitFamily && isDigitClass && isDigitCommodity)) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715066, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return null;

		}

		reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		reqDTO.setUnspscTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		reqDTO.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		resDTO = unspscSO.searchUnspscObj(reqDTO);
		if (resDTO.getUnspscObj() != null) {

			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716014, new String[] { this.getManagedBean().getSelectedLovUnspsc()
									.getUnspscCode() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return null;
		}

		if (this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitle().isEmpty()
				|| this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715008, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return null;

		} else if

		(this.getManagedBean().getSelectedLovUnspscSegment().isBrandNew()) {

			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitleEng());
				unspsc.setUnspscLevel(0);

				reqDTO.setCmUnspsc(unspsc);
				reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());
				
				//35503 Start - segment for audit trail required				
				for (CmUnspsc auSegObj : reqDTO.getUnspscList()) {
					auditDTO.getSegmentUnspscCode().add(auSegObj.getUnspscCode()); 
					auditDTO.getSegmentUnspscCode().add(Integer.toString(AuditCMConst.SEG_CODE_ID)); 
					auditDTO.getSegmentUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_SEG)); 
					auditDTO.getSegmentUnspscTitle().add(auSegObj.getUnspscTitle()); 
					auditDTO.getSegmentUnspscTitle().add(Integer.toString(AuditCMConst.SEG_LABEL_BM_ID)); 
					auditDTO.getSegmentUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_SEG)); 
					auditDTO.getSegmentUnspscTitleEng().add(auSegObj.getUnspscTitleEng()); 
					auditDTO.getSegmentUnspscTitleEng().add(Integer.toString(AuditCMConst.SEG_LABEL_BI_ID)); 
					auditDTO.getSegmentUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_SEG)); 
				}
				
				auditDTO.getAuditSegmentList().add(auditDTO.getSegmentUnspscCode());
				auditDTO.getAuditSegmentList().add(auditDTO.getSegmentUnspscTitle()); 
				auditDTO.getAuditSegmentList().add(auditDTO.getSegmentUnspscTitleEng()); 
				this.getManagedBean().setAuditSegmentList(auditDTO.getAuditSegmentList()); 
				this.getManagedBean().setAuditSegmentCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode()); 
				//35503 End - segment for audit trail required
			} else {
				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_716014, new String[] { this.getManagedBean()
										.getSelectedLovUnspscSegment().getUnspscCode() },
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				return null;

			}
		} else {
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());
				unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscKey());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitleEng());
				unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscDescription());
				unspsc.setUnspscLevel(0);

				reqDTO.setCmUnspsc(unspsc);
				reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());
				
				// 35503 Start - segment for audit trail required
				for (CmUnspsc auSegObj : reqDTO.getUnspscList()) {
					auditDTO.getSegmentUnspscCode().add(auSegObj.getUnspscCode());
					auditDTO.getSegmentUnspscCode().add(Integer.toString(AuditCMConst.SEG_CODE_ID));
					auditDTO.getSegmentUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_SEG));
					auditDTO.getSegmentUnspscTitle().add(auSegObj.getUnspscTitle());
					auditDTO.getSegmentUnspscTitle().add(Integer.toString(AuditCMConst.SEG_LABEL_BM_ID));
					auditDTO.getSegmentUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_SEG));
					auditDTO.getSegmentUnspscTitleEng().add(auSegObj.getUnspscTitleEng());
					auditDTO.getSegmentUnspscTitleEng().add(Integer.toString(AuditCMConst.SEG_LABEL_BI_ID));
					auditDTO.getSegmentUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_SEG));
				}

				auditDTO.getAuditSegmentList().add(auditDTO.getSegmentUnspscCode());
				auditDTO.getAuditSegmentList().add(auditDTO.getSegmentUnspscTitle());
				auditDTO.getAuditSegmentList().add(auditDTO.getSegmentUnspscTitleEng());
				this.getManagedBean().setAuditSegmentList(auditDTO.getAuditSegmentList());
				this.getManagedBean().setAuditSegmentCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());
				// 35503 End - segment for audit trail required
			}
		}

		// //// start family

		if (this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitle().isEmpty()
				|| this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715008, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return null;

		} else

		if (this.getManagedBean().getSelectedLovUnspscfamily().isBrandNew()) {

			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				System.out.println("this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()"
						+ this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitleEng());
				unspsc.setUnspscLevel(1);
				unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());

				reqDTO.setCmUnspsc(unspsc);
				reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
				
				//35503 Start - family for audit trail required
				for (CmUnspsc auFamObj : reqDTO.getFamilyList()) {
					auditDTO.getFamilyUnspscCode().add(auFamObj.getUnspscCode());
					auditDTO.getFamilyUnspscCode().add(Integer.toString(AuditCMConst.FAM_CODE_ID));
					auditDTO.getFamilyUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_FAM));
					auditDTO.getFamilyUnspscTitle().add(auFamObj.getUnspscTitle());
					auditDTO.getFamilyUnspscTitle().add(Integer.toString(AuditCMConst.FAM_LABEL_BM_ID));
					auditDTO.getFamilyUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_FAM));
					auditDTO.getFamilyUnspscTitleEng().add(auFamObj.getUnspscTitleEng());
					auditDTO.getFamilyUnspscTitleEng().add(Integer.toString(AuditCMConst.FAM_LABEL_BI_ID));
					auditDTO.getFamilyUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_FAM));
				}

				auditDTO.getAuditFamilyList().add(auditDTO.getFamilyUnspscCode());
				auditDTO.getAuditFamilyList().add(auditDTO.getFamilyUnspscTitle());
				auditDTO.getAuditFamilyList().add(auditDTO.getFamilyUnspscTitleEng());
				this.getManagedBean().setAuditFamilyList(auditDTO.getAuditFamilyList());
				this.getManagedBean().setAuditFamilyCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
				//35503 End - family for audit trail required
			} else {

				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_716014, new String[] { this.getManagedBean()
										.getSelectedLovUnspscfamily().getUnspscCode() },
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				return null;
			}
		} else {
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
				unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscKey());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitleEng());
				unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscDescription());
				unspsc.setUnspscLevel(1);
				unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());

				reqDTO.setCmUnspsc(unspsc);
				reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
				
				// 35503 Start - family for audit trail required
				for (CmUnspsc auFamObj : reqDTO.getFamilyList()) {
					auditDTO.getFamilyUnspscCode().add(auFamObj.getUnspscCode());
					auditDTO.getFamilyUnspscCode().add(Integer.toString(AuditCMConst.FAM_CODE_ID));
					auditDTO.getFamilyUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_FAM));
					auditDTO.getFamilyUnspscTitle().add(auFamObj.getUnspscTitle());
					auditDTO.getFamilyUnspscTitle().add(Integer.toString(AuditCMConst.FAM_LABEL_BM_ID));
					auditDTO.getFamilyUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_FAM));
					auditDTO.getFamilyUnspscTitleEng().add(auFamObj.getUnspscTitleEng());
					auditDTO.getFamilyUnspscTitleEng().add(Integer.toString(AuditCMConst.FAM_LABEL_BI_ID));
					auditDTO.getFamilyUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_FAM));
				}

				auditDTO.getAuditFamilyList().add(auditDTO.getFamilyUnspscCode());
				auditDTO.getAuditFamilyList().add(auditDTO.getFamilyUnspscTitle());
				auditDTO.getAuditFamilyList().add(auditDTO.getFamilyUnspscTitleEng());
				this.getManagedBean().setAuditFamilyList(auditDTO.getAuditFamilyList());
				this.getManagedBean().setAuditFamilyCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
				// 35503 End - family for audit trail required
			}
		}
		// ///start class

		if (this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitle().isEmpty()
				|| this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715008, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return null;

		} else

		if (this.getManagedBean().getSelectedLovUnspscClass().isBrandNew()) {

			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				System.out.println("this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()"
						+ this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitleEng());
				unspsc.setUnspscLevel(2);
				unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());

				reqDTO.setCmUnspsc(unspsc);
				reqDTO.getClassList().add(reqDTO.getCmUnspsc());
				
				// 35503 Start - class for audit trail required
				for (CmUnspsc auClsObj : reqDTO.getClassList()) {
					auditDTO.getClassUnspscCode().add(auClsObj.getUnspscCode());
					auditDTO.getClassUnspscCode().add(Integer.toString(AuditCMConst.CLS_CODE_ID));
					auditDTO.getClassUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_CLS));
					auditDTO.getClassUnspscTitle().add(auClsObj.getUnspscTitle());
					auditDTO.getClassUnspscTitle().add(Integer.toString(AuditCMConst.CLS_LABEL_BM_ID));
					auditDTO.getClassUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_CLS));
					auditDTO.getClassUnspscTitleEng().add(auClsObj.getUnspscTitleEng());
					auditDTO.getClassUnspscTitleEng().add(Integer.toString(AuditCMConst.CLS_LABEL_BI_ID));
					auditDTO.getClassUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_CLS));
				}

				auditDTO.getAuditClassList().add(auditDTO.getClassUnspscCode());
				auditDTO.getAuditClassList().add(auditDTO.getClassUnspscTitle());
				auditDTO.getAuditClassList().add(auditDTO.getClassUnspscTitleEng());
				this.getManagedBean().setAuditClassList(auditDTO.getAuditClassList());
				this.getManagedBean().setAuditClassCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
				// 35503 End - class for audit trail required
			} else {

				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_716014, new String[] { this.getManagedBean()
										.getSelectedLovUnspscClass().getUnspscCode() },
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				return null;

			}

		} else {
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
				unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspscClass().getUnspscKey());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitleEng());
				unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspscClass().getUnspscDescription());
				unspsc.setUnspscLevel(2);
				unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());

				reqDTO.setCmUnspsc(unspsc);
				reqDTO.getClassList().add(reqDTO.getCmUnspsc());
				
				// 35503 Start - class for audit trail required
				for (CmUnspsc auClsObj : reqDTO.getClassList()) {
					auditDTO.getClassUnspscCode().add(auClsObj.getUnspscCode());
					auditDTO.getClassUnspscCode().add(Integer.toString(AuditCMConst.CLS_CODE_ID));
					auditDTO.getClassUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_CLS));
					auditDTO.getClassUnspscTitle().add(auClsObj.getUnspscTitle());
					auditDTO.getClassUnspscTitle().add(Integer.toString(AuditCMConst.CLS_LABEL_BM_ID));
					auditDTO.getClassUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_CLS));
					auditDTO.getClassUnspscTitleEng().add(auClsObj.getUnspscTitleEng());
					auditDTO.getClassUnspscTitleEng().add(Integer.toString(AuditCMConst.CLS_LABEL_BI_ID));
					auditDTO.getClassUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_CLS));
				}

				auditDTO.getAuditClassList().add(auditDTO.getClassUnspscCode());
				auditDTO.getAuditClassList().add(auditDTO.getClassUnspscTitle());
				auditDTO.getAuditClassList().add(auditDTO.getClassUnspscTitleEng());
				this.getManagedBean().setAuditClassList(auditDTO.getAuditClassList());
				this.getManagedBean().setAuditClassCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
				// 35503 End - class for audit trail required
			}
		}

		// //start commodity

		if (this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle().isEmpty()
				|| this.getManagedBean().getSelectedLovUnspsc().getUnspscTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715008, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return null;

		} else if (this.getManagedBean().getSelectedLovUnspsc().isBrandNew()) {

			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitleEng());
				unspsc.setUnspscLevel(3);
				unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());

				reqDTO.setCmUnspsc(unspsc);
     			reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());
     			
				// 35503 Start - commodity for audit trail required
				for (CmUnspsc auComObj : reqDTO.getCommodityList()) {
					auditDTO.getCommodityUnspscCode().add(auComObj.getUnspscCode());
					auditDTO.getCommodityUnspscCode().add(Integer.toString(AuditCMConst.COM_CODE_ID));
					auditDTO.getCommodityUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_COM));
					auditDTO.getCommodityUnspscTitle().add(auComObj.getUnspscTitle());
					auditDTO.getCommodityUnspscTitle().add(Integer.toString(AuditCMConst.COM_LABEL_BM_ID));
					auditDTO.getCommodityUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_COM));
					auditDTO.getCommodityUnspscTitleEng().add(auComObj.getUnspscTitleEng());
					auditDTO.getCommodityUnspscTitleEng().add(Integer.toString(AuditCMConst.COM_LABEL_BI_ID));
					auditDTO.getCommodityUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_COM));
				}

				auditDTO.getAuditCommodityList().add(auditDTO.getCommodityUnspscCode());
				auditDTO.getAuditCommodityList().add(auditDTO.getCommodityUnspscTitle());
				auditDTO.getAuditCommodityList().add(auditDTO.getCommodityUnspscTitleEng());
				this.getManagedBean().setAuditCommodityList(auditDTO.getAuditCommodityList());
				this.getManagedBean().setAuditCommodityCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
				// 35503 End - commodity for audit trail required
			} else {

				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_716014, new String[] { this.getManagedBean().getSelectedLovUnspsc()
										.getUnspscCode() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				return null;
			}

		} else {
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			if (resDTO.getUnspscObj() == null) {// so its new unspsc,
				// save it
				CmUnspsc unspsc = new CmUnspsc();
				unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
				unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspsc().getUnspscKey());
				unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
				unspsc.setUnspscTitleEng(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitleEng());
				unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspsc().getUnspscDescription());
				unspsc.setUnspscLevel(3);
				unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());

				reqDTO.setCmUnspsc(unspsc);
				reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());
				
				// 35503 Start - commodity for audit trail required
				for (CmUnspsc auComObj : reqDTO.getCommodityList()) {
					auditDTO.getCommodityUnspscCode().add(auComObj.getUnspscCode());
					auditDTO.getCommodityUnspscCode().add(Integer.toString(AuditCMConst.COM_CODE_ID));
					auditDTO.getCommodityUnspscCode().add(Integer.toString(AuditCMConst.FIELD_LABEL_CODE_ID_COM));
					auditDTO.getCommodityUnspscTitle().add(auComObj.getUnspscTitle());
					auditDTO.getCommodityUnspscTitle().add(Integer.toString(AuditCMConst.COM_LABEL_BM_ID));
					auditDTO.getCommodityUnspscTitle().add(Integer.toString(AuditCMConst.FIELD_LABEL_BM_ID_COM));
					auditDTO.getCommodityUnspscTitleEng().add(auComObj.getUnspscTitleEng());
					auditDTO.getCommodityUnspscTitleEng().add(Integer.toString(AuditCMConst.COM_LABEL_BI_ID));
					auditDTO.getCommodityUnspscTitleEng().add(Integer.toString(AuditCMConst.FIELD_LABEL_BI_ID_COM));
				}

				auditDTO.getAuditCommodityList().add(auditDTO.getCommodityUnspscCode());
				auditDTO.getAuditCommodityList().add(auditDTO.getCommodityUnspscTitle());
				auditDTO.getAuditCommodityList().add(auditDTO.getCommodityUnspscTitleEng());
				this.getManagedBean().setAuditCommodityList(auditDTO.getAuditCommodityList());
				this.getManagedBean().setAuditCommodityCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
				// 35503 End - commodity for audit trail required
			}
		}

		System.out.println("...................	reqDTO.getCommodityList().size=" + reqDTO.getCommodityList().size());

		reqDTO.setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());

		// checking hierarchy of segment, family, class and commodity
		if (!validateUnspscCodeHierarchy(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode(), this
				.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode(), this.getManagedBean()
				.getSelectedLovUnspscClass().getUnspscCode(), this.getManagedBean().getSelectedLovUnspsc()
				.getUnspscCode())) {
			return null;
		}

		resDTO = unspscSO.publishCodification(reqDTO);
		
		// 35503 Start - for audit trail required
		if (resDTO.isSuccessful()) {
			IAuditTrailSO auditCodificationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT,
					IAuditTrailSO.class);

			if (auditCodificationSO.isAuditConfig(AuditCMConst.CM_AU_CONF)) {
				AuditCodificationManagementBackingBeanTemplate auTemplate = new AuditCodificationManagementBackingBean();
				auTemplate.saveAuditItemCodeCreation();
			}
		}
		// 35503 End - for audit trail required

		renewSesionObject();
		
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
				MessageConst.INFO_710022, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		context.addMessage(null, fm);

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
				.handleNavigation(FacesContext.getCurrentInstance(), null, CommonUIConst.ACKNOWLEDGE_PAGE);

		log.info("End Publish");
		return null;
	}

	public boolean validateUnspscCodeHierarchy(String segnment, String family, String classC, String comodity) {
		String segmentIdentifier = segnment.substring(0, 2);
		String familyIdentifier = family.substring(0, 4);
		String classIdentifier = classC.substring(0, 6);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (!family.startsWith(segmentIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716011, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return false;
		} else if (!classC.startsWith(familyIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716011, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return false;
		} else if (!comodity.startsWith(classIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716011, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return false;
		}
		// checking codes validity
		if (!segnment.substring(2).equals("000000")) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716013, new String[] { ResourceBundleUtils.getMessage("lbl.cm.segment",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) },
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return false;
		}
		if (!family.substring(4).equals("0000")) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716013, new String[] { ResourceBundleUtils.getMessage("lbl.cm.family",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) },
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return false;
		}
		if (!classC.substring(6).equals("00")) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716013, new String[] { ResourceBundleUtils.getMessage("lbl.cm.class",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) },
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return false;
		}
		// checking for not redundant segment, familiy, class and commodity
		segmentIdentifier = segmentIdentifier + "00";
		if (segmentIdentifier.equals(familyIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716012, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return false;
		}
		familyIdentifier = familyIdentifier + "00";
		if (familyIdentifier.equals(classIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716012, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return false;
		}
		classIdentifier = classIdentifier + "00";
		if (classIdentifier.equals(comodity)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716012, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return false;
		}

		return true;

	}

	public void backToCodificationPortal() {
		log.info(".................codification................");

		renewSesionObject();

		FacesContext context = FacesContext.getCurrentInstance();
		javax.faces.context.ExternalContext externalContext = context.getExternalContext();
		try {
			externalContext.redirect("codification");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
