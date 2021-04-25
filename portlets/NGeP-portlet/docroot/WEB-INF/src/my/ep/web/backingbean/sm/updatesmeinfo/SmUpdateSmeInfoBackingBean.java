package my.ep.web.backingbean.sm.updatesmeinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.sme.SupplierSmeReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSmeSupplier;
import my.ep.persistence.entity.sm.SmSmeSupplierDoc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.supplieralert.SupplierAlertBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.updatesmeinfo.SmUpdateSmeInfoManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "smUpdateSmeInfoBackingBean")
@RequestScoped
public class SmUpdateSmeInfoBackingBean extends BaseBackingBean<SmUpdateSmeInfoManagedBean> {
	
	private static Logger log = EpLogger.getLogger(SupplierAlertBackingBean.class);

	private static final long serialVersionUID = 1L;

	public SmUpdateSmeInfoBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	public void preRenderSmeInfo() {
		renewSesionObject();
		SmCommonUtil.redirectToPage("smeUpdateInfoTnc.xhtml", "", null, null);
	}

	public void preRenderAlertSmeInfo() {
		//10724 START
		renewSesionObject();
		SmSupplier supp = SmApplicationUtil.getCurrentSupplier();
		if (supp != null) {
			SmMofAccount acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(
					supp.getSupplierId(), supp.getSmAppl().getSupplierType());
			if (acc != null
					&& acc.getExpDate() != null) {
				Date currDate = new Date();
				getManagedBean().setExpDate(acc.getExpDate());
				Date expDate = getManagedBean().getExpDate(); 
				
				Calendar currDateAfterThreeMonths = Calendar.getInstance();
				currDateAfterThreeMonths.setTime(currDate);
				currDateAfterThreeMonths.add(Calendar.MONTH, 3);
				
				
				if (currDateAfterThreeMonths.getTime().after(acc.getExpDate()) && acc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					int secsInADay = 24 * 60 * 60;
					long togo = (getManagedBean().getExpDate().getTime() - currDate
							.getTime()) / 1000;
					getManagedBean().setDays((togo / secsInADay) + "");
					
					getManagedBean().setMofBeforeExpiryMsg(
							SmCommonUtil.getMessage(
									"lbl.sm.MOF.alert.beforeexpiry", new String[] {getManagedBean().getDays(), DateUtils.dateToString("dd/MM/yyyy", expDate)}));
				}
			}
			else {
				SmMofAccount expiredAcc = SmCommonUtil.getExpiredMofAccountBySuppIdAndSuppType(
						supp.getSupplierId(), supp.getSmAppl().getSupplierType());
				
				Date currDate = new Date();
				getManagedBean().setExpDate(expiredAcc.getExpDate());
				Date expDate = getManagedBean().getExpDate(); 
				
				if (currDate.after(expiredAcc.getExpDate()) && expiredAcc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_EXPIRED){
					Calendar dateAfterOneYear = Calendar.getInstance();
					dateAfterOneYear.setTime(expDate);
					dateAfterOneYear.add(Calendar.MONTH, 12);
					
					int secsInADay = 24 * 60 * 60;
					long togo = ((dateAfterOneYear.getTimeInMillis()) - currDate
							.getTime()) / 1000;
					getManagedBean().setDays(((togo + secsInADay) / secsInADay) + "");
					
					getManagedBean().setMofAfterExpiryMsg(
							SmCommonUtil.getMessage(
									"lbl.sm.MOF.alert.afterexpiry", new String[] {DateUtils.dateToString("dd/MM/yyyy", expDate), getManagedBean().getDays()}));
				}
			}
		}
		//10724 END
		log.info(getManagedBean().getMofBeforeExpiryMsg());
		log.info(getManagedBean().getMofAfterExpiryMsg());
		SmCommonUtil.redirectToPage("alertSmeInfoWidget.xhtml", "", null, null);
	}
	
	public static final String NAV_TO_SME_INFO = "/xhtml/sm/updateSmeInfo/alertSmeInfoWidget.xhtml";

	public void proceedAfterAcceptTnC() {
		getManagedBean().setSmSupplier(SmApplicationUtil.getCurrentSupplier());
		getManagedBean().setCompanyBasic(
				SmCommonUtil.getLatestRevCompBasicByApplId(getManagedBean().getSmSupplier().getSmAppl().getApplId()));
		buildStatusTypeList();
		initDocumentList(getManagedBean().getSmeSupplier());
		SmCommonUtil.redirectToPage("smeUpdateInfoPage.xhtml", "", null, null);
	}

	private void buildStatusTypeList() {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		reqDTO.setParameterType(ParameterTypeConst.SME_STATUS_TYPE);
		reqDTO.setLanguageCode(languageCode);
		ParameterResDTO resDTO = SmServiceLookup.getParameterSO().retrieveParameterDescListByTypeAndParamCodes(reqDTO);
		List<SelectItem> statusType = new ArrayList<SelectItem>();
		for (PmParameterDesc list : resDTO.getParameterDescList()) {
			SelectItem type = new SelectItem();
			type.setValue(list.getPmParameter().getParameterCode());
			type.setLabel(list.getCodeDesc());
			statusType.add(type);
		}
		getManagedBean().setStatusTypeList(statusType);
	}

	private void initDocumentList(SmSmeSupplier sme) {
		List<PmDocument> suppDocList = SmServiceLookup.getDocumentSO().retrieveSmeSuppDocuments(new DocumentReqDTO())
				.getDocumentList();
		SmSmeSupplierDoc suppDoc = null;
		if (sme == null) {
			sme = new SmSmeSupplier();
		}
		if (suppDocList != null && !suppDocList.isEmpty()) {
			if (sme.getSmSmeSupplierDocs() == null || sme.getSmSmeSupplierDocs().isEmpty()) {
				sme.setSmSmeSupplierDocs(new ArrayList<SmSmeSupplierDoc>());
				for (PmDocument doc : suppDocList) {
					suppDoc = new SmSmeSupplierDoc();
					suppDoc.setPmDocument(doc);
					suppDoc.setSmSmeSupplier(sme);
					sme.getSmSmeSupplierDocs().add(suppDoc);
				}
			} else {
				for (PmDocument doc : suppDocList) {
					if (!isSmePmDocumentAlreadyAdded(doc, sme.getSmSmeSupplierDocs())) {
						suppDoc = new SmSmeSupplierDoc();
						suppDoc.setPmDocument(doc);
						suppDoc.setSmSmeSupplier(sme);
						sme.getSmSmeSupplierDocs().add(suppDoc);
					}
				}
			}
			if (!EntityHelper.isCollectionEmpty(sme.getSmSmeSupplierDocs())) {
				// sort by doc name
				Collections.sort(sme.getSmSmeSupplierDocs(), new Comparator<SmSmeSupplierDoc>() {
					@Override
					public int compare(final SmSmeSupplierDoc s1, final SmSmeSupplierDoc s2) {
						String str1 = s1.getPmDocument() == null ? StringUtils.EMPTY : s1.getPmDocument().getDocName();
						String str2 = s2.getPmDocument() == null ? StringUtils.EMPTY : s2.getPmDocument().getDocName();
						return (str1.compareTo(str2));
					}
				});
			}
		}
		getManagedBean().setSmeSupplier(sme);
	}

	private static boolean isSmePmDocumentAlreadyAdded(PmDocument smePmDoc, List<SmSmeSupplierDoc> smeDocList) {
		boolean retVal = false;
		for (SmSmeSupplierDoc smeDoc : smeDocList) {
			if (smeDoc.getPmDocument() != null
					&& smeDoc.getPmDocument().getDocumentId().compareTo(smePmDoc.getDocumentId()) == 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	public void assignSmeSupportDoc(SmSmeSupplierDoc doc) {
		getManagedBean().setSmeSupplierDoc(doc);
	}

	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSmeSupplierDoc() != null) {
				String filePath = getSmeSuppDocBasePath();
				SmSmeSupplierDoc doc = getManagedBean().getSmeSupplierDoc();
				doc.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", "-"));
				doc.setFilePath(filePath);
				setSmeSuppDocFileAttachment();
				commonUploadFile(event, filePath);
			}
		}
	}

	private String getSmeSuppDocBasePath() {
		return SmCommonUtil.getUploadFilePath(SupplierManagementConst.SME_FILE_PATH)
				+ getManagedBean().getSmSupplier().getRegNo() + "/" + System.currentTimeMillis() + "/";
	}

	private void setSmeSuppDocFileAttachment() {
		Iterator<SmSmeSupplierDoc> docVOIter = getManagedBean().getSmeSupplier().getSmSmeSupplierDocs().iterator();
		while (docVOIter.hasNext()) {
			SmSmeSupplierDoc docVO = docVOIter.next();
			if (docVO.getPmDocument().getDocumentId()
					.compareTo(getManagedBean().getSmeSupplierDoc().getPmDocument().getDocumentId()) == 0) {
				docVO = getManagedBean().getSmeSupplierDoc();
			}
		}
	}

	public void onDeleteAttachment(SmSmeSupplierDoc doc) {
		getManagedBean().setSmeSupplierDoc(doc);
		RequestContext.getCurrentInstance().execute("confirmUploadDeleteDialog.show();");
	}

	public void deleteAttachment() {
		SmSmeSupplierDoc mainDoc = (SmSmeSupplierDoc) EntityHelper.getEntityBySinglePropertyFromList(getManagedBean()
				.getSmeSupplierDoc().getPmDocument().getDocumentId(), getManagedBean().getSmeSupplier()
				.getSmSmeSupplierDocs(), "pmDocument");
		mainDoc.setFileName(StringUtils.EMPTY);
		mainDoc.setFilePath(StringUtils.EMPTY);
	}

	public void onClickTooltips(String dialogWidget) {
		RequestContext.getCurrentInstance().execute(dialogWidget + ".show()");
	}

	public StreamedContent getMedia(String mediaName) {
		StreamedContent retVal = null;
		String path = "/sm/updateSmeInfo//" + mediaName + ".pdf";
		try {
			retVal = SmCommonUtil.getFileStreamContentByTypeAndPath(path, "application/pdf");
		} catch (IOException e) {
			throw new EpException(e.getMessage());
		}
		return retVal;
	}

	public void redirectToHistoryPage() {
		initSmeInfoHistoryList();
		SmCommonUtil.redirectToPage("smeUpdateHistoryPage.xhtml", "", null, null);
	}

	public void onDeclareSme() {
		if ("1".equalsIgnoreCase(getManagedBean().getSmeCorpRegisteredTrans())) {
			if (getManagedBean().getSmeSupplierDoc() == null
					|| getManagedBean().getSmeSupplierDoc().getFileName().isEmpty()) {
				throw new EpException(MessageConst.ERROR_296171, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sm.sme.regCert", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()) });
			}
		}
		RequestContext.getCurrentInstance().execute("confirmDeclareDialog.show();");
	}

	public void removeNotUploadedSuppDoc(SmSmeSupplier sme) {
		if (sme != null && sme.getSmSmeSupplierDocs() != null) {
			Iterator<SmSmeSupplierDoc> iter = sme.getSmSmeSupplierDocs().iterator();
			while (iter.hasNext()) {
				SmSmeSupplierDoc doc = iter.next();
				if (StringUtils.isBlank(doc.getFileName()) || StringUtils.isBlank(doc.getFilePath())) {
					iter.remove();
				}
			}
		}
	}

	public void declareSme() {
		if (getManagedBean().getSmeCorpRegisteredTrans().equalsIgnoreCase("1")) {
			getManagedBean().getSmeSupplier().setSmeCorpRegistered(true);
		} else if (getManagedBean().getSmeCorpRegisteredTrans().equalsIgnoreCase("0")) {
			getManagedBean().getSmeSupplier().setSmeCorpRegistered(false);
			removeNotUploadedSuppDoc(getManagedBean().getSmeSupplier());
		}
		SupplierSmeReqDTO reqDTO = new SupplierSmeReqDTO();
		reqDTO.setSmSupplier(getManagedBean().getSmSupplier());
		reqDTO.setSmSmeSupplier(getManagedBean().getSmeSupplier());
		SmServiceLookup.getSupplierSmeSO().declareSupplierSme(reqDTO);
		renewSesionObject();
		SmCommonUtil.redirectToPage(SmApplicationUtil.getApplicationErrorPage(), MessageConst.INFO_296172, null, null);
	}

	public void initSmeInfoHistoryList() {
		SupplierSmeReqDTO reqDTO = new SupplierSmeReqDTO();
		reqDTO.setSmSupplier(getManagedBean().getSmSupplier());
		List<SmSmeSupplier> declaredSmeList = SmServiceLookup.getSupplierSmeSO()
				.retrieveSmeHistoryListBySupplierId(reqDTO).getSupplierSmeList();
		getManagedBean().setSupplierSmeList(declaredSmeList);
	}

	public void onDeleteSme() {
		RequestContext.getCurrentInstance().execute("confirmDeleteDialog.show();");
	}

	public void deleteSme() {
		SupplierSmeReqDTO reqDTO = new SupplierSmeReqDTO();
		reqDTO.setSmSupplier(getManagedBean().getSmSupplier());
		SmServiceLookup.getSupplierSmeSO().deleteSupplierSme(reqDTO);
		renewSesionObject();
		SmCommonUtil.redirectToPage(SmApplicationUtil.getApplicationErrorPage(), MessageConst.INFO_296173, null, null);
	}

}
