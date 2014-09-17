package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constants.CDProcessNameConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRecommendCode;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmRequestRemark;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
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

@ManagedBean(name = "unspscCodeBackingBean")
@RequestScoped
public class UnspscCodeBackingBean extends
		BaseBackingBean<UnspscCodeManagedBean> {

	private static Logger log = Logger.getLogger(UnspscCodeBackingBean.class);

	private SearchFilterBean colorFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());

	public UnspscCodeBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public boolean isCatalogueAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CM_ADMIN");
	}

	public boolean isSupplierUser() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER");
	}

	public boolean isSupplierAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN");
	}

	public boolean isCodeAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_OFFICER");
	}

	public boolean isRequisitioner() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("REQUISITIONER");
	}

	public boolean isCdCommittee() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_COMMITTEE");
	}

	public boolean isSupplierUserOrTempSupplierAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER")
				|| fs.hasRoleUser("SUPPLIER_TEMP")
				|| fs.hasRoleUser("SUPPLIER_ADMIN");
	}

	public boolean isAllSupplier() {

		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN")
				|| fs.hasRoleUser("SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("BASIC_SUPPLIER_USER")
				|| fs.hasRoleUser("BASIC_SUPPLIER_ADMIN");
	}

	public String submitForRequest(String action) throws Exception {

		try {
			System.out.println("action:" + action);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);

			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			log.info("new:"
					+ this.getManagedBean().getRequestCode().isBrandNew());

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());

			// this block is to identify the request from Catalogue Admin or
			// Supplier
			if (isAllSupplier()) {
				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				if (smResDTO.getResultBean().getResultList().isEmpty()) {
					throw new EpException(MessageConst.ERROR_721017);
				}
				log.info("getSmSupplier: "
						+ smResDTO.getResultBean().getResultList().get(0));
				this.getManagedBean()
						.getRequestCode()
						.setSmSupplier(
								smResDTO.getResultBean().getResultList().get(0));

				reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);

				log.info("normal unspsc request");
			} else {
				reqDTO.setProcessId(CDProcessNameConst.CA_UNSPSC_CODE_REQUEST_PROCESS_ID);
				log.info("skip CA unspsc request");
			}

			// if(smResDTO.getResultBean().getResultList() == null){
			// FacesContext facesContext = FacesContext.getCurrentInstance();
			// facesContext.addMessage(null, new
			// FacesMessage(FacesMessage.SEVERITY_ERROR,
			// "user's profile not found!"," "));
			// return "unspscRequestForm";
			// }

			if (this.getManagedBean().getRequestCode().getItemType()
					.equals("S")) {// for services clear extension fileds
				this.getManagedBean().getRequestCode().setTypeName(null);
				this.getManagedBean().getRequestCode().setBrandName(null);
				this.getManagedBean().getRequestCode().setMeasurementName(null);
				this.getManagedBean().getRequestCode().setColorName(null);
			}

			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);

			reqDTO.setToken(token);
			CodificationResDTO resDTO = null;

			if (action.equalsIgnoreCase("SUBMIT")) {

				if (this.getManagedBean().getRequestCode().isBrandNew()) {

					resDTO = unspscSO.addCodificationRequest(reqDTO);

					FacesContext facesContext = FacesContext
							.getCurrentInstance();
					facesContext
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_710001,
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					log.debug("***************BB end*****************");
					// return "unspscRequestForm";
					return "BPMtesting";

				} else {
					log.info("***update***");
					resDTO = unspscSO.updateCodificationRequest(reqDTO);
				}

			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new EpException(MessageConst.EP_MAJ_00001,
					new String[] { ResourceBundleUtils.getMessage(null,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });

			// return "unspscRequestForm";
		}

		// return "taskListView";
		return "BPMtesting";

	}

	public String bpmTesting(String action) throws Exception {

		try {
			System.out.println("action:" + action);
			CodificationBackingBean codeBB = new CodificationBackingBean();
			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			log.info("new:"
					+ this.getManagedBean().getRequestCode().isBrandNew());

			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);

			if (action.equalsIgnoreCase("RECOMMEND TO SUPPLIER")) {

				if (!(this.getManagedBean().getRequestCode()
						.getCmRecommendCodes() != null && this.getManagedBean()
						.getRequestCode().getCmRecommendCodes().size() > 0)) {
					System.out.println("**********warning***************");

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
															MessageConst.ERROR_715005,
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

					return "reviewUnspscCode";
				}

				for (CmRecommendCode unspsc : this.getManagedBean()
						.getRequestCode().getCmRecommendCodes()) {

					unspsc.setCmRequestCode(this.getManagedBean()
							.getRequestCode());

				}

			}

			System.out.println("size:"
					+ this.getManagedBean().getRequestCode()
							.getCmRecommendCodes().size());
			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());

			CodificationResDTO resDTO = null;

			if (this.getManagedBean().getRequestCode().isBrandNew()) {

				resDTO = unspscSO.addCodificationRequest(reqDTO);

				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

				return "unspscRequestForm";

			} else {
				log.info("***update***");
				System.out.println("size:"
						+ reqDTO.getRequestCode().getCmRecommendCodes().size());
				resDTO = unspscSO.updateCodificationRequest(reqDTO);
			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} catch (Exception ex) {
			System.out.println("***************Exception*****************");
			ex.printStackTrace();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext
					.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"BPM  ERROR", ex.getMessage()));

			return "reviewUnspscCode";
		}

		return "BPMtesting";

	}

	public String selectSubmitRecommend(String action) throws Exception {

		try {

			System.out
					.println("this.getManagedBean().getRequestCode().getCmRecommendCodes().size()"
							+ this.getManagedBean().getRequestCode()
									.getCmRecommendCodes().size());
			if (this.getManagedBean().getRequestCode().getCmRecommendCodes()
					.size() > 1) {
				System.out
						.println("***************more than 1*****************");
				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715006,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

				return "selectRecommendUnspscCode";
			}

			CodificationBackingBean codeBB = new CodificationBackingBean();
			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			log.info("new:"
					+ this.getManagedBean().getRequestCode().isBrandNew());

			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);

			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());

			CodificationResDTO resDTO = null;

			log.info("***update***");
			System.out.println("size:"
					+ reqDTO.getRequestCode().getCmRecommendCodes().size());
			resDTO = unspscSO.updateCodificationRequest(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} catch (Exception ex) {
			System.out.println("***************Exception*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext
					.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"BPM  ERROR", ex.getMessage()));

			return "selectRecommendUnspscCode";
		}

		return "BPMtesting";

	}

	public String submitForMaintenanceRequest(String action) throws Exception {

		try {
			System.out.println("action:" + action);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);

			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			CodificationReqDTO reqDTO = new CodificationReqDTO();

			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			CodificationResDTO resDTO = null;

			resDTO = unspscSO.updateCodificationRequest(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} catch (Exception ex) {
			System.out.println("***************Exception*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext
					.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"BPM  ERROR", ex.getMessage()));

			return "reviewDeleteExtension";
		}

		// return "taskListView";
		return "BPMtesting";

	}

	public void uploadUNSPSC(FileUploadEvent event) throws Exception {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		UploadedFile item = event.getFile();

		FileInputStream file = (FileInputStream) item.getInputstream();

		HSSFWorkbook workbook = new HSSFWorkbook(file);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		HSSFSheet segment = workbook.getSheetAt(2);

		log.info("*******segment***********");

		long key = 0;
		long code = 0;
		String title = null;
		String desc = null;

		int lastRowNum = segment.getLastRowNum() + 1;
		for (int i = 1; i < lastRowNum; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = segment.getRow(i);

			if (row.getCell(0) != null) {
				key = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(key);

			}

			if (row.getCell(1) != null) {
				code = (long) row.getCell(1).getNumericCellValue();

				unspsc.setUnspscCode(String.valueOf(code));
			}

			if (row.getCell(2) != null) {
				title = row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(title);

			}

			if (row.getCell(3) != null) {
				desc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(desc);

			}

			unspsc.setUnspscLevel(0);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

		}

		CodificationResDTO resDTO = unspscSO.saveUnspsc(reqDTO);
		log.info("*******end Family***********");

		// /////////////////////////////////////////////////////////////////////

		HSSFSheet family = workbook.getSheetAt(3);

		long familyKey = 0;
		long familyCode = 0;
		String familyTitle = null;
		String familyDesc = null;

		int lastRowNumFamily = family.getLastRowNum() + 1;
		for (int i = 1; i < lastRowNumFamily; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = family.getRow(i);

			if (row.getCell(0) != null) {
				familyKey = (long) row.getCell(0).getNumericCellValue();

				unspsc.setUnspscKey(familyKey);
			}

			if (row.getCell(1) != null) {
				familyCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(familyCode);

				unspsc.setUnspscCode(String.valueOf(familyCode));
				unspsc.setUnspscParentCode(parentCode);

			}

			if (row.getCell(2) != null) {
				familyTitle = row.getCell(2).getStringCellValue();

				unspsc.setUnspscTitle(familyTitle);
			}

			if (row.getCell(3) != null) {
				familyDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(familyDesc);
			}

			unspsc.setUnspscLevel(1);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());

		}
		resDTO = unspscSO.saveFamilyList(reqDTO);

		// /////////////////////////////////////////////////////////////////////

		HSSFSheet classes = workbook.getSheetAt(4);

		long classKey = 0;
		long classCode = 0;
		String classTitle = null;
		String classDesc = null;

		int lastRowNumClass = classes.getLastRowNum() + 1;
		for (int i = 1; i < lastRowNumClass; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = classes.getRow(i);

			if (row.getCell(0) != null) {
				classKey = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(classKey);

			}

			if (row.getCell(1) != null) {

				classCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(classCode);

				unspsc.setUnspscCode(String.valueOf(classCode));
				unspsc.setUnspscParentCode(parentCode);
			}

			if (row.getCell(2) != null) {
				classTitle = row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(classTitle);
			}

			if (row.getCell(3) != null) {
				classDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(classDesc);
			}

			unspsc.setUnspscLevel(2);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getClassList().add(reqDTO.getCmUnspsc());

		}

		resDTO = unspscSO.saveClassList(reqDTO);

		HSSFSheet commodity = workbook.getSheetAt(5);

		long commodityKey = 0;
		long commodityCode = 0;
		String commodityTitle = null;
		String commodityDesc = null;

		int lastRowNumCommodity = commodity.getLastRowNum() + 1;

		for (int i = 1; i < lastRowNumCommodity; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = commodity.getRow(i);

			if (row.getCell(6) != null
					&& row.getCell(6).toString().trim().length() > 0) {

				commodityKey = (long) row.getCell(6).getNumericCellValue();

				unspsc.setUnspscKey(commodityKey);

			}

			if (row.getCell(7) != null
					&& row.getCell(7).toString().trim().length() > 0) {

				commodityCode = (long) row.getCell(7).getNumericCellValue();

				String parentCode = getParentCode(commodityCode);

				unspsc.setUnspscCode(String.valueOf(commodityCode));
				unspsc.setUnspscParentCode(parentCode);
			}

			if (row.getCell(8) != null
					&& row.getCell(8).toString().trim().length() > 0) {
				commodityTitle = row.getCell(8).getStringCellValue();

				unspsc.setUnspscTitle(commodityTitle);

			}

			if (row.getCell(9) != null
					&& row.getCell(9).toString().trim().length() > 0) {
				commodityDesc = row.getCell(9).getStringCellValue();

				unspsc.setUnspscDescription(commodityDesc);

			}

			unspsc.setUnspscLevel(3);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());

		}

		resDTO = unspscSO.saveCommodityList(reqDTO);

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

	public String navigateToSearch() {
		log.info("*****navigateToSearch******");

		if (this.getManagedBean().getRequestCode().getCmRecommendCodes() != null
				&& this.getManagedBean().getRequestCode().getCmRecommendCodes()
						.size() > 0) {

			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_715001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			return "reviewUnspscCode?faces-redirect=true";
		}

		// clear search fileds and result
		CodificationBackingBean bb = new CodificationBackingBean();
		bb.getManagedBean().setAutoCompleteUnspscCode(new CmUnspsc());
		bb.getTitleFilterBean().setSearchValue(null);
		bb.setSearchDataModel(null);

		return "verifyUnspscSearch";
	}

	public String navigateCodeAdminToSearch() {
		log.info("*****navigateCodeAdminToSearch******");

		return "verifyUnspscSearch?faces-redirect=true";
	}

	public String createTempCode() {

		log.info("ID:" + this.getManagedBean().getRequestCode().getRequestId());

		return "unspscSingleUpload?faces-redirect=true";
	}

	public String savetempCode(String outcome) {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		CodificationBackingBean bb = new CodificationBackingBean();

		try {

			if (outcome.equalsIgnoreCase("SUBMIT")) {
				System.out.println("***this.getManagedBean().isNewSegment()"
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

					return "reviewBeforeCreateTempCode";

				}

				if (this.getManagedBean().getSelectedLovUnspscSegment()
						.isBrandNew()) {
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitle());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscSegment()
							.getUnspscDescription());
					unspsc.setUnspscLevel(0);

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

					// resDTO = unspscSO.saveUnspsc(reqDTO);
				} else {
					reqDTO.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());
					resDTO = unspscSO.searchUnspscObj(reqDTO);
					if (resDTO.getUnspscObj() == null) {// so its new unspsc,
														// save it
						CmUnspsc unspsc = new CmUnspsc();
						unspsc.setUnspscCode(this.getManagedBean()
								.getSelectedLovUnspscSegment().getUnspscCode());
						unspsc.setUnspscKey(this.getManagedBean()
								.getSelectedLovUnspscSegment().getUnspscKey());
						unspsc.setUnspscTitle(this.getManagedBean()
								.getSelectedLovUnspscSegment().getUnspscTitle());
						unspsc.setUnspscDescription(this.getManagedBean()
								.getSelectedLovUnspscSegment()
								.getUnspscDescription());
						unspsc.setUnspscLevel(0);

						reqDTO.setCmUnspsc(unspsc);
						reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());
					}
				}
				// //// start family

				if (this.getManagedBean().getSelectedLovUnspscfamily()
						.isBrandNew()) {
					CmUnspsc unspsc = new CmUnspsc();
					System.out
							.println("this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()"
									+ this.getManagedBean()
											.getSelectedLovUnspscfamily()
											.getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitle());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscfamily()
							.getUnspscDescription());
					unspsc.setUnspscLevel(1);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
					// resDTO = unspscSO.saveFamilyList(reqDTO);
				} else {
					reqDTO.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());
					resDTO = unspscSO.searchUnspscObj(reqDTO);
					if (resDTO.getUnspscObj() == null) {// so its new unspsc,
														// save it
						CmUnspsc unspsc = new CmUnspsc();
						unspsc.setUnspscCode(this.getManagedBean()
								.getSelectedLovUnspscfamily().getUnspscCode());
						unspsc.setUnspscKey(this.getManagedBean()
								.getSelectedLovUnspscfamily().getUnspscKey());
						unspsc.setUnspscTitle(this.getManagedBean()
								.getSelectedLovUnspscfamily().getUnspscTitle());
						unspsc.setUnspscDescription(this.getManagedBean()
								.getSelectedLovUnspscfamily()
								.getUnspscDescription());
						unspsc.setUnspscLevel(1);
						unspsc.setUnspscParentCode(this.getManagedBean()
								.getSelectedLovUnspscSegment().getUnspscCode());

						reqDTO.setCmUnspsc(unspsc);
						reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
					}
				}
				// ///start class

				if (this.getManagedBean().getSelectedLovUnspscClass()
						.isBrandNew()) {
					CmUnspsc unspsc = new CmUnspsc();
					System.out
							.println("this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()"
									+ this.getManagedBean()
											.getSelectedLovUnspscClass()
											.getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitle());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscDescription());
					unspsc.setUnspscLevel(2);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getClassList().add(reqDTO.getCmUnspsc());
					// resDTO = unspscSO.saveClassList(reqDTO);

				} else {
					reqDTO.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());
					resDTO = unspscSO.searchUnspscObj(reqDTO);
					if (resDTO.getUnspscObj() == null) {// so its new unspsc,
														// save it
						CmUnspsc unspsc = new CmUnspsc();
						unspsc.setUnspscCode(this.getManagedBean()
								.getSelectedLovUnspscClass().getUnspscCode());
						unspsc.setUnspscKey(this.getManagedBean()
								.getSelectedLovUnspscClass().getUnspscKey());
						unspsc.setUnspscTitle(this.getManagedBean()
								.getSelectedLovUnspscClass().getUnspscTitle());
						unspsc.setUnspscDescription(this.getManagedBean()
								.getSelectedLovUnspscClass()
								.getUnspscDescription());
						unspsc.setUnspscLevel(2);
						unspsc.setUnspscParentCode(this.getManagedBean()
								.getSelectedLovUnspscfamily().getUnspscCode());

						reqDTO.setCmUnspsc(unspsc);
						reqDTO.getClassList().add(reqDTO.getCmUnspsc());
					}
				}
				// //start commodity
				System.out
						.println(".....................this.getManagedBean().getSelectedLovUnspsc().isBrandNew()="
								+ this.getManagedBean().getSelectedLovUnspsc()
										.isBrandNew());
				if (this.getManagedBean().getSelectedLovUnspsc().isBrandNew()) {
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitle());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscDescription());
					unspsc.setUnspscLevel(3);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());

					// resDTO = unspscSO.saveCommodityList(reqDTO);
				} else {
					reqDTO.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscCode());
					resDTO = unspscSO.searchUnspscObj(reqDTO);
					if (resDTO.getUnspscObj() == null) {// so its new unspsc,
														// save it
						CmUnspsc unspsc = new CmUnspsc();
						unspsc.setUnspscCode(this.getManagedBean()
								.getSelectedLovUnspsc().getUnspscCode());
						unspsc.setUnspscKey(this.getManagedBean()
								.getSelectedLovUnspsc().getUnspscKey());
						unspsc.setUnspscTitle(this.getManagedBean()
								.getSelectedLovUnspsc().getUnspscTitle());
						unspsc.setUnspscDescription(this.getManagedBean()
								.getSelectedLovUnspsc().getUnspscDescription());
						unspsc.setUnspscLevel(3);
						unspsc.setUnspscParentCode(this.getManagedBean()
								.getSelectedLovUnspscClass().getUnspscCode());

						reqDTO.setCmUnspsc(unspsc);
						reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());
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
				System.out.println("tempCode:" + tempCode);
				this.getManagedBean().getRequestCode().setTempCode(tempCode);
				this.getManagedBean()
						.getRequestCode()
						.setTempTitle(
								this.getManagedBean().getSelectedLovUnspsc()
										.getUnspscTitle());

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
				reqDTO.setToken(token);
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setOutCome(outcome);
				System.out.println("**********savetempCode************");

				reqDTO.setCodeCommittees("user003,user051");
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
								.getUnspscTitle().trim().length() > 0)) {

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
															MessageConst.ERROR_715008,
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

					return "reviewBeforeCreateTempCode";

				}
				// checking hierarchy of segment, family, class and commodity
				if(!validateUnspscCodeHierarchy(this.getManagedBean()
						.getSelectedLovUnspscSegment().getUnspscCode(), this
						.getManagedBean().getSelectedLovUnspscfamily()
						.getUnspscCode(), this.getManagedBean()
						.getSelectedLovUnspscClass().getUnspscCode(), this
						.getManagedBean().getSelectedLovUnspsc()
						.getUnspscCode())){
					return "reviewBeforeCreateTempCode";
				}

				resDTO = unspscSO.updateCodificationRequest(reqDTO);

			} else if (outcome.equalsIgnoreCase("RECOMMEND TO SUPPLIER")) {

				if (!(this.getManagedBean().getRequestCode()
						.getCmRecommendCodes() != null && this.getManagedBean()
						.getRequestCode().getCmRecommendCodes().size() > 0)) {
					System.out.println("**********warning***************");

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
															MessageConst.ERROR_715003,
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

					return "reviewBeforeCreateTempCode";
				}

				for (CmRecommendCode code : this.getManagedBean()
						.getRequestCode().getCmRecommendCodes()) {

					code.setCmRequestCode(this.getManagedBean()
							.getRequestCode());

				}

				reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
				reqDTO.setToken(token);
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setOutCome(outcome);

				resDTO = unspscSO.updateCodificationRequest(reqDTO);

			}
		} catch (Exception ex) {

			System.out.println("***************Exception*****************");

			throw new EpException(MessageConst.EP_MAJ_00001);

			// return "reviewBeforeCreateTempCode";
		}

		// BpmResponseDTO responseDTO =
		// updateTask(resDTO.getRequestCode(),taskListBB.getManagedBean().getTaskId());
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		// return "taskListView";
		return "BPMtesting";

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

	public String createExtCode() {

		log.info("**************************");
		// this.getManagedBean().getRequestCode()

		return "extensionCodeSingleCreation.xhtml";
	}

	/*
	 * public String saveExtCode(){
	 * 
	 * ICodificationSO unspscSO = (ICodificationSO)
	 * BusinessDelegate.getInstance(
	 * ).getServiceObject("cm",ICodificationSO.class);
	 * 
	 * CodificationReqDTO reqDTO = new CodificationReqDTO(); CodificationResDTO
	 * resDTO = null; FacesScopes face = new FacesScopes(); String token
	 * =face.getBPMAuthenticationCtxToken(); CmItem item = new CmItem();
	 * 
	 * System.out.println("this.getManagedBean().getRequestCode().getTempCode():"
	 * +this.getManagedBean().getRequestCode().getTempCode());
	 * reqDTO.setUnspscCode
	 * (this.getManagedBean().getRequestCode().getTempCode()); resDTO =
	 * unspscSO.searchCommodityObj(reqDTO);
	 * 
	 * 
	 * 
	 * AlphanumericGenerator brandCode = new AlphanumericGenerator(false,
	 * "000"); AlphanumericGenerator typeCode = new AlphanumericGenerator(false,
	 * "00"); AlphanumericGenerator measurementCode = new
	 * AlphanumericGenerator(false, "000"); AlphanumericGenerator colorCode =
	 * new AlphanumericGenerator(false, "00");
	 * 
	 * String brand = brandCode.nextStringIdentifier(); String type =
	 * typeCode.nextStringIdentifier(); String measurement =
	 * measurementCode.nextStringIdentifier(); String color =
	 * colorCode.nextStringIdentifier(); log.info("brand:"+brand);
	 * 
	 * //////////brand//////////
	 * System.out.println("getUnspscCode:"+resDTO.getCommodityObj
	 * ().getUnspscCode()); CmBrand cmBrand = new CmBrand();
	 * 
	 * log.info("****************************************************");
	 * cmBrand.
	 * setBrandName(this.getManagedBean().getRequestCode().getBrandName());
	 * cmBrand.setBrandCode(brand);
	 * cmBrand.setUnspscId(resDTO.getCommodityObj());
	 * cmBrand.setRecordStatus(true); reqDTO.setCmBrand(cmBrand);
	 * 
	 * //CodificationResDTO resBrandDTO = unspscSO.addNewBrand(reqDTO);
	 * //item.setCmBrand(resBrandDTO.getCmBrand());
	 * /////////////type////////////////// CmType cmType = new CmType();
	 * 
	 * cmType.setTypeName(this.getManagedBean().getRequestCode().getTypeName());
	 * cmType.setTypeCode(type); cmType.setUnspscId(resDTO.getCommodityObj());
	 * cmType.setRecordStatus(true);
	 * 
	 * reqDTO.setCmType(cmType); //CodificationResDTO resTypeDTO =
	 * unspscSO.addNewType(reqDTO); //item.setCmType(resTypeDTO.getCmType());
	 * /////////////measurement///////////////////////////
	 * 
	 * CmMeasurement cmMeasurement = new CmMeasurement();
	 * 
	 * cmMeasurement.setMeasurementName(this.getManagedBean().getRequestCode().
	 * getMeasurementName()); cmMeasurement.setMeasurementCode(measurement);
	 * cmMeasurement.setUnspscId(resDTO.getCommodityObj());
	 * cmMeasurement.setRecordStatus(true);
	 * 
	 * reqDTO.setCmMeasurement(cmMeasurement); //CodificationResDTO
	 * resMeasurementDTO = unspscSO.addNewMeasurement(reqDTO);
	 * //item.setCmMeasurement(resMeasurementDTO.getCmMeasurement());
	 * //////////////////////////////////////
	 * 
	 * CmColor cmColor = new CmColor();
	 * cmColor.setColorName(this.getManagedBean(
	 * ).getRequestCode().getColorName()); cmColor.setColorCode(color);
	 * cmColor.setRecordStatus(true); reqDTO.setCmColor(cmColor);
	 * 
	 * //CodificationResDTO resColorDTO = unspscSO.addNewColor(reqDTO);
	 * //item.setCmColor(resColorDTO.getCmColor());
	 * System.out.println("************New Extension End***************");
	 * 
	 * String extensionCode = type+brand+measurement+color;
	 * 
	 * log.info("extensionCode:"+extensionCode);
	 * 
	 * 
	 * item.setRecordStatus(true); item.setCmUnspsc(resDTO.getCommodityObj());
	 * item.setExtensionCode(extensionCode); reqDTO.setCmItem(item);
	 * reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
	 * reqDTO.setTaskId(this.getManagedBean().getTaskId());
	 * reqDTO.setToken(token); reqDTO.setOutCome("CREATE"); resDTO =
	 * unspscSO.addNewItem(reqDTO);
	 * 
	 * System.out.println("****************save Item End******************");
	 * 
	 * 
	 * FacesContext facesContext = FacesContext.getCurrentInstance();
	 * facesContext.addMessage(null, new
	 * FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));
	 * 
	 * //return "taskListView"; return "BPMtesting";
	 * 
	 * }
	 */

	public void addMoreRequest() {
		log.info(".................addMoreRequest...................");
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		// reqDTO.setParentCode(this.getManagedBean().getUnspscCode());
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

		this.getManagedBean().setRequestExtCode(new CmRequestCode());
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
		clearForm();
		this.getManagedBean().setTypeDisable(true);
		this.getManagedBean().setBrandDisable(true);
		this.getManagedBean().setMeasurementDisable(true);
		this.getManagedBean().setColorDisable(true);

	}

	public String submitToBPM() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (this.getManagedBean().getRequestExtCodeList() == null
				|| this.getManagedBean().getRequestExtCodeList().isEmpty()) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_715004,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return null;
		}

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		supplierReqDTO.setUserId(pmUser.getUserId());

		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierReqDTO);

		System.out.println("getSmSupplier"
				+ smResDTO.getResultBean().getResultList().get(0));

		for (CmRequestCode code : this.getManagedBean().getRequestExtCodeList()) {
			code.setSmSupplier(smResDTO.getResultBean().getResultList().get(0));
		}

		reqDTO.setRequestExtList(this.getManagedBean().getRequestExtCodeList());
		reqDTO.setToken(token);

		CodificationResDTO resDTO = unspscSO.addExtensionRequest(reqDTO);

		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return "BPMtesting";
	}

	public void addNewRequest() {
		log.info("...............addNewRequest...................");
		/*
		 * ICodificationSO unspscSO = (ICodificationSO)
		 * BusinessDelegate.getInstance
		 * ().getServiceObject("cm",ICodificationSO.class); CodificationReqDTO
		 * reqDTO = new CodificationReqDTO(); CodificationResDTO resDTO = null;
		 */

		// ///check that there be at least one new request
		if (!this.getManagedBean().getRequestExtCode().isRequestType()
				&& !this.getManagedBean().getRequestExtCode().isRequestBrand()
				&& !this.getManagedBean().getRequestExtCode()
						.isRequestMeasurement()
				&& !this.getManagedBean().getRequestExtCode().isRequestColor()) {
			throw new EpException(MessageConst.ERROR_716010);

		}
		// //////////checking for redundancy
		if (this.getManagedBean().getRequestExtCode().isRequestType()
				&& !this.getManagedBean().getRequestExtCode().getTypeName()
						.isEmpty()) {
			checkingTypeRedundency(this.getManagedBean().getRequestExtCode()
					.getTypeName());
		}
		if (this.getManagedBean().getRequestExtCode().isRequestBrand()
				&& !this.getManagedBean().getRequestExtCode().getBrandName()
						.isEmpty()) {
			checkingBrandRedundency(this.getManagedBean().getRequestExtCode()
					.getBrandName());
		}
		if (this.getManagedBean().getRequestExtCode().isRequestMeasurement()
				&& !this.getManagedBean().getRequestExtCode()
						.getMeasurementName().isEmpty()) {
			checkingMeasurmentRedundency(this.getManagedBean()
					.getRequestExtCode().getMeasurementName());
		}
		if (this.getManagedBean().getRequestExtCode().isRequestColor()
				&& !this.getManagedBean().getRequestExtCode().getColorName()
						.isEmpty()) {
			checkingColorRedundency(this.getManagedBean().getRequestExtCode()
					.getColorName());
		}

		/*
		 * if (this.getManagedBean().isTypeRedundancy()) { throw new
		 * EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.type",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); } if
		 * (this.getManagedBean().isBarndRedundancy()) { throw new
		 * EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.brand",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); } if
		 * (this.getManagedBean().isMeasurRedundancy()) { FacesContext
		 * facesContext = FacesContext.getCurrentInstance();
		 * 
		 * throw new EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.measurement",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); } if
		 * (this.getManagedBean().isColorRedundancy()) { FacesContext
		 * facesContext = FacesContext.getCurrentInstance();
		 * 
		 * throw new EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.color",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); }
		 */
		// type
		if ((!this.getManagedBean().getRequestExtCode().isRequestType() && this
				.getManagedBean().getSelectedType() == null)
				|| (this.getManagedBean().getRequestExtCode().isRequestType() && this
						.getManagedBean().getRequestExtCode().getTypeName()
						.isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {

			if (!this.getManagedBean().getRequestExtCode().isRequestType()) {
				this.getManagedBean()
						.getRequestExtCode()
						.setTypeName(
								this.getManagedBean().getSelectedType()
										.getTypeName());
				this.getManagedBean()
						.getRequestExtCode()
						.setTypeId(
								this.getManagedBean().getSelectedType()
										.getTypeId());
			}
		}
		// brand
		if ((!this.getManagedBean().getRequestExtCode().isRequestBrand() && this
				.getManagedBean().getSelectedBrand() == null)
				|| (this.getManagedBean().getRequestExtCode().isRequestBrand() && this
						.getManagedBean().getRequestExtCode().getBrandName()
						.isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.brand",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {

			if (!this.getManagedBean().getRequestExtCode().isRequestBrand()) {
				this.getManagedBean()
						.getRequestExtCode()
						.setBrandName(
								this.getManagedBean().getSelectedBrand()
										.getBrandName());
				this.getManagedBean()
						.getRequestExtCode()
						.setBrandId(
								this.getManagedBean().getSelectedBrand()
										.getBrandId());
			}
		}
		// color
		if ((!this.getManagedBean().getRequestExtCode().isRequestColor() && this
				.getManagedBean().getSelectedColor() == null)
				|| (this.getManagedBean().getRequestExtCode().isRequestColor() && this
						.getManagedBean().getRequestExtCode().getColorName()
						.isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.color",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
			if (!this.getManagedBean().getRequestExtCode().isRequestColor()) {
				this.getManagedBean()
						.getRequestExtCode()
						.setColorName(
								this.getManagedBean().getSelectedColor()
										.getColorName());
				this.getManagedBean()
						.getRequestExtCode()
						.setColorId(
								this.getManagedBean().getSelectedColor()
										.getColorId());
			}
		}
		// measurement
		if ((!this.getManagedBean().getRequestExtCode().isRequestMeasurement() && this
				.getManagedBean().getSelectedMeasurement() == null)
				|| (this.getManagedBean().getRequestExtCode()
						.isRequestMeasurement() && this.getManagedBean()
						.getRequestExtCode().getMeasurementName().isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.measurement",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
			if (!this.getManagedBean().getRequestExtCode()
					.isRequestMeasurement()) {
				this.getManagedBean()
						.getRequestExtCode()
						.setMeasurementId(
								this.getManagedBean().getSelectedMeasurement()
										.getMeasurementId());
				this.getManagedBean()
						.getRequestExtCode()
						.setMeasurementName(
								this.getManagedBean().getSelectedMeasurement()
										.getMeasurementName());
			}
		}

		this.getManagedBean().getRequestExtCodeList()
				.add(this.getManagedBean().getRequestExtCode());
		clearForm();

		RequestContext.getCurrentInstance().execute("extensionPopUp.hide();");
	}

	public void clearForm() {
		log.info("...............clearForm...................");
		this.getManagedBean().setSelectedType(null);
		this.getManagedBean().setSelectedBrand(null);
		this.getManagedBean().setSelectedColor(null);
		this.getManagedBean().setSelectedMeasurement(null);
	}

	public String submitToReviewExt(String action) {
		System.out.println("***action***" + action);
		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		// try {

		System.out.println("face.getUserScreenName()"
				+ face.getUserScreenName());

		Map temp = new HashMap();

		temp.put("user003", "user003");
		temp.put("user051", "user051");

		if (face.hasRoleUser("CM_ADMIN")) {
			System.out.println("*******CM ADMIN*********");
			reqDTO.setCodeCommittees("user003,user051");
		} else if (face.hasRoleUser("CD_COMMITTEE")) {
			System.out.println("*******CD_COMMITTEE*********:" + temp.size());
			if (!temp.isEmpty() && temp.size() > 0) {

				Iterator itMap = temp.entrySet().iterator();

				while (itMap.hasNext()) {
					Map.Entry entry = (Map.Entry) itMap.next();
					String key = (String) entry.getKey();
					log.info("mapKey:" + key);
					if (!key.equalsIgnoreCase(face.getUserScreenName())) {
						reqDTO.setCodeCommittees((String) entry.getValue());
					}
				}

			}
		}

		// /////////////////////////////////////

		if (this.getManagedBean().getReviewExtCodeList() != null
				&& this.getManagedBean().getReviewExtCodeList().size() > 0) {

			for (CmRequestCode code : this.getManagedBean()
					.getReviewExtCodeList()) {

				if (code.getColorNameDD() != null) {
					ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
					colorFilterBean.setSearchField("colorId");
					colorFilterBean.setSearchValue(code.getColorNameDD());
					searchBeanColor.getDataFilterBeans().add(colorFilterBean);

					reqDTO.setColorSearchBean(searchBeanColor);

					resDTO = unspscSO.searchColorObj(reqDTO);
					code.setColorName(resDTO.getColorList().get(0)
							.getColorName());
					code.setColorId(code.getColorNameDD());
					code.setRequestColor(false);

				}

				if (code.getBrandNameDD() != null) {

					ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
					brandFilterBean.setSearchField("brandId");
					brandFilterBean.setSearchValue(code.getBrandNameDD());
					searchBeanColor.getDataFilterBeans().add(brandFilterBean);

					reqDTO.setBrandSearchBean(searchBeanColor);

					resDTO = unspscSO.searchBrandObj(reqDTO);

					code.setBrandName(resDTO.getBrandList().get(0)
							.getBrandName());
					code.setBrandId(code.getBrandNameDD());
					code.setRequestBrand(false);

				}

				if (code.getTypeNameDD() != null) {

					ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
					typeFilterBean.setSearchField("typeId");
					typeFilterBean.setSearchValue(code.getTypeNameDD());
					searchBeanColor.getDataFilterBeans().add(typeFilterBean);

					reqDTO.setTypeSearchBean(searchBeanColor);

					resDTO = unspscSO.searchTypeObj(reqDTO);

					code.setTypeName(resDTO.getTypeList().get(0).getTypeName());
					code.setTypeId(code.getTypeNameDD());
					code.setRequestType(false);
				}

				if (code.getMeasurementNameDD() != null) {

					ISearchBean<CmMeasurement> searchBeanColor = new SearchBean<CmMeasurement>();
					measurementFilterBean.setSearchField("measurementId");
					measurementFilterBean.setSearchValue(code
							.getMeasurementNameDD());
					searchBeanColor.getDataFilterBeans().add(
							measurementFilterBean);

					reqDTO.setMeasurementSearchBean(searchBeanColor);

					resDTO = unspscSO.searchMeasurementObj(reqDTO);

					code.setMeasurementName(resDTO.getMeasurementList().get(0)
							.getMeasurementName());
					code.setMeasurementId(code.getMeasurementNameDD());
					code.setRequestMeasurement(false);
				}

				reqDTO.getRequestExtList().add(code);

			}
		}

		/*
		 * for(CmRequestRemark remark:
		 * this.getManagedBean().getReviewExtCodeList
		 * ().get(0).getCmRequestRemarks()){
		 * remark.setCmRequestCode(this.getManagedBean
		 * ().getReviewExtCodeList().get(0)); }
		 */

		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setOutCome(action);
		reqDTO.setToken(token);

		System.out.println("getCodeCommittees" + reqDTO.getCodeCommittees());
		System.out.println("consensus" + reqDTO.isConsensus());

		System.out.println("action" + action);

		if (action.equalsIgnoreCase("PUBLISH")) {

			reqDTO.setUnspscCode(this.getManagedBean().getReviewExtCodeList()
					.get(0).getTempCode());
			resDTO = unspscSO.searchCommodityObj(reqDTO);

			reqDTO.setCmUnspsc(resDTO.getCommodityObj());
			reqDTO.setUnspscId(resDTO.getCommodityObj().getUnspscId());

			resDTO = unspscSO.publishExtensionRequest(reqDTO);

		}

		if (!action.equalsIgnoreCase("PUBLISH")) {

			for (CmRequestCode code : this.getManagedBean()
					.getReviewExtCodeList()) {

				System.out.println("code.isRemarkRequired"
						+ code.isRemarkRequired());
				System.out.println("code.isRemarkRequiredII"
						+ code.isRemarkRequiredII());
				System.out.println("******getReviewedBrandNameII********"
						+ code.getReviewedBrandNameII());
				if (code.getReviewedBrandNameII() != null
						&& code.getReviewedBrandNameII().trim().length() > 0) {
					// //////checking for redundant values
					if (!isCodeAdmin()) {
						if (code.isRequestType()
								&& (code.getTypeRemarkII() != null && code
										.getTypeRemarkII().trim().length() > 0)) {
							checkingTypeRedundency(code.getReviewedTypeNameII());
						}
						if (code.isRequestBrand()
								&& (code.getBrandRemarkII() != null && code
										.getBrandRemarkII().trim().length() > 0)) {
							checkingBrandRedundency(code
									.getReviewedBrandNameII());
						}
						if (code.isRequestMeasurement()
								&& (code.getMeasurementRemarkII() != null && code
										.getMeasurementRemarkII().trim()
										.length() > 0)) {
							checkingMeasurmentRedundency(code
									.getReviewedMeasurementNameII());
						}
						if (code.isRequestColor()
								&& (code.getColorRemarkII() != null && code
										.getColorRemarkII().trim().length() > 0)) {
							checkingColorRedundency(code
									.getReviewedColorNameII());
						}
					}
					// //////////////////////////////////
					if ((code.getTypeRemarkII() != null && code
							.getTypeRemarkII().trim().length() > 0)
							|| (code.getBrandRemarkII() != null && code
									.getBrandRemarkII().trim().length() > 0)
							|| (code.getMeasurementRemarkII() != null && code
									.getMeasurementRemarkII().trim().length() > 0)
							|| (code.getColorRemarkII() != null && code
									.getColorRemarkII().trim().length() > 0)) {
						System.out.println("remark added!!");
						code.setRemarkRequiredII(false);

					}

					if (code.isRemarkRequiredII()) {

						FacesContext facesContext = FacesContext
								.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils
								.getMessage(MessageConst.ERROR_716007,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
						return "reviewApproveExtensionCodeII";
					}
				} else {
					System.out.println("******else********");
					// //////checking for redundant values
					if (code.isRequestType()
							&& (code.getTypeRemark() != null && code
									.getTypeRemark().trim().length() > 0)) {
						checkingTypeRedundency(code.getReviewedTypeName());
					}
					if (code.isRequestBrand()
							&& (code.getBrandRemark() != null && code
									.getBrandRemark().trim().length() > 0)) {
						checkingBrandRedundency(code.getReviewedBrandName());
					}
					if (code.isRequestMeasurement()
							&& (code.getMeasurementRemark() != null && code
									.getMeasurementRemark().trim().length() > 0)) {
						checkingMeasurmentRedundency(code
								.getReviewedMeasurementName());
					}
					if (code.isRequestColor()
							&& (code.getColorRemark() != null && code
									.getColorRemark().trim().length() > 0)) {
						checkingColorRedundency(code.getReviewedColorName());
					}
					// //////////////////////////////////
					if ((code.getTypeRemark() != null && code.getTypeRemark()
							.trim().length() > 0)
							|| (code.getBrandRemark() != null && code
									.getBrandRemark().trim().length() > 0)
							|| (code.getMeasurementRemark() != null && code
									.getMeasurementRemark().trim().length() > 0)
							|| (code.getColorRemark() != null && code
									.getColorRemark().trim().length() > 0)) {
						System.out.println("remark added!!");
						code.setRemarkRequired(false);

					}

					if (code.isRemarkRequired()) {
						System.out
								.println("................test...........code.isRemarkRequired()="
										+ code.isRemarkRequired());
						throw new EpException(
								MessageConst.ERROR_716007,
								new String[] { ResourceBundleUtils
										.getMessage(
												null,
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
														.getUserLocale()) });

						// return "reviewApproveExtensionCode";
					}
				}

			}

			resDTO = unspscSO.updateExtensionRequest(reqDTO);

		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		// } catch (Exception ex) {
		//
		// }

		// return "taskListView";
		return "BPMtesting";
	}

	public void preRenderExtensionMaintenanceView() {

		System.out.println("**********preRenderExtensionView**************");
		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		CDRequestDataType unspsc = (CDRequestDataType) obj;

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IRequestCodeSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
		codeDTO.setRequestId(new Long(unspsc.getRequestId()));
		RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);

		UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
		unspscBB.getManagedBean().setRequestCode(resDTO.getRequestCode());

		this.getManagedBean().setAction(res.getAction());
		this.getManagedBean().setTaskId(taskId);

	}

	public void preRenderExtensionView() {

		System.out.println("**********preRenderExtensionView**************");

		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();

		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);

		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		CDRequestDataType unspsc = (CDRequestDataType) obj;

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IRequestCodeSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();

		CodificationReqDTO reqCodeDTO = new CodificationReqDTO();
		RequestCodeResDTO resDTO = null;
		this.getManagedBean().setReviewExtCodeList(null);

		if (unspsc.getItemId() != null && unspsc.getItemId().size() > 0) {
			for (String itemId : unspsc.getItemId()) {
				System.out.println("itemId:" + itemId);
				codeDTO.setRequestId(new Long(itemId));
				resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
				this.getManagedBean().getReviewExtCodeList()
						.add(resDTO.getRequestCode());

				this.getManagedBean().setRequestCode(resDTO.getRequestCode());
			}
		} else {
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
			this.getManagedBean().getReviewExtCodeList()
					.add(resDTO.getRequestCode());
			this.getManagedBean().setRequestCode(resDTO.getRequestCode());
		}

		for (CmRequestCode code : this.getManagedBean().getReviewExtCodeList()) {

			// if(!code.isRequestColor()){
			if (res.getTaskName().equalsIgnoreCase("Approve Extension Code")) {
				code.setReviewedColorName(code.getColorName());
			}
			if (res.getTaskName().equalsIgnoreCase("Endorse Extension Code")) {
				code.setReviewedColorNameII(code.getReviewedColorName());
			}
			// }

			// if(!code.isRequestBrand()){
			if (res.getTaskName().equalsIgnoreCase("Approve Extension Code")) {
				code.setReviewedBrandName(code.getBrandName());
			}
			if (res.getTaskName().equalsIgnoreCase("Endorse Extension Code")) {
				code.setReviewedBrandNameII(code.getReviewedBrandName());
			}
			// }

			// if(!code.isRequestType()){
			if (res.getTaskName().equalsIgnoreCase("Approve Extension Code")) {
				code.setReviewedTypeName(code.getTypeName());
			}
			if (res.getTaskName().equalsIgnoreCase("Endorse Extension Code")) {
				code.setReviewedTypeNameII(code.getReviewedTypeName());
			}
			// }

			// if(!code.isRequestMeasurement()){
			if (res.getTaskName().equalsIgnoreCase("Approve Extension Code")) {
				code.setReviewedMeasurementName(code.getMeasurementName());
			}
			if (res.getTaskName().equalsIgnoreCase("Endorse Extension Code")) {
				code.setReviewedMeasurementNameII(code
						.getReviewedMeasurementName());
			}
			// }

			code.setRemarkRequired(false);
			code.setRemarkRequiredII(false);
			code.setAddedRemark(false);
			for (CmRequestRemark remark : code.getCmRequestRemarks()) {

				userReqDTO.setUserId(remark.getChangedBy());
				System.out.println("remark.getChangedBy()"
						+ remark.getChangedBy());
				UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
				System.out.println("userResDTO.getPmUser().getUserName():"
						+ userResDTO.getPmUser().getUserName());
				remark.setCreatedUser(userResDTO.getPmUser().getUserName());
			}

		}

		reqCodeDTO.setUnspscCode(resDTO.getRequestCode().getTempCode());
		CodificationResDTO resCodeDTO = codificationSO
				.searchCommodityObj(reqCodeDTO);

		this.getManagedBean().setTypeList(
				resCodeDTO.getCommodityObj().getCmType());
		this.getManagedBean().setBrandList(
				resCodeDTO.getCommodityObj().getCmBrand());
		this.getManagedBean().setMeasurementList(
				resCodeDTO.getCommodityObj().getCmMeasurement());
		resCodeDTO = codificationSO.searchColor(reqCodeDTO);
		this.getManagedBean().setColorList(resCodeDTO.getColorList());

		this.getManagedBean().setAction(res.getAction());
		this.getManagedBean().setTaskId(taskId);

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqStatusDTO = new StatusReqDTO();

		reqStatusDTO.setLanguageCode(languageCode);
		reqStatusDTO.setStatusId(res.getDocStatus().longValue());
		StatusResDTO resStatusDTO = statusSO
				.retrieveStatusDescById(reqStatusDTO);

		this.getManagedBean().setDocStatus(
				resStatusDTO.getPmStatusDesc() != null ? resStatusDTO
						.getPmStatusDesc().getStatusName() : "Review");

		System.out.println("********res.getTaskName()********"
				+ res.getTaskName());
		if (res.getTaskName().equalsIgnoreCase("Approve Extension Code")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"reviewApproveExtensionCode.xhtml");
		} else if (res.getTaskName().equalsIgnoreCase("Endorse Extension Code")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"reviewApproveExtensionCodeII.xhtml");
		} else if (res.getTaskName().equalsIgnoreCase("Publish Extension Code")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"reviewApproveExtensionCodeII.xhtml");
		} else if (res.getTaskName().equalsIgnoreCase("Result Notification")) {
			FacesContext fc = FacesContext.getCurrentInstance();

			// boolean isPublished;
			// if(unspsc.isPublished() == null) {
			// isPublished = true;
			// } else {
			// isPublished = unspsc.isPublished().booleanValue();
			// }

			if (unspsc.isPublished()) {
				fc.getApplication().getNavigationHandler()
						.handleNavigation(fc, null, "resultNotification.xhtml");
			} else {
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"reviewExtensionCodeFlow2.xhtml");
			}
		} else {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null, "reviewExtensionCodeFlow.xhtml");
		}

		//

	}

	public void preRenderView() {
		log.debug("**********preRenderView**************");
		this.getManagedBean().setSelectedLovUnspscSegment(null);
		this.getManagedBean().setSelectedLovUnspscfamily(null);
		this.getManagedBean().setSelectedLovUnspscClass(null);
		this.getManagedBean().setSelectedLovUnspsc(null);

		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		CodificationBackingBean codeBB = new CodificationBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		log.info("taskId:" + taskId);

		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);

		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		CDRequestDataType unspsc = (CDRequestDataType) obj;

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IRequestCodeSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();

		this.getManagedBean().setReviewExtCodeList(null);

		if (unspsc.getItemId() != null && unspsc.getItemId().size() > 0) {
			for (String itemId : unspsc.getItemId()) {
				log.info("itemId:" + itemId);

				codeDTO.setRequestId(new Long(itemId));
				RequestCodeResDTO resDTO = requestSO
						.retrieveUnspscCodeReview(codeDTO);
				this.getManagedBean().getReviewExtCodeList()
						.add(resDTO.getRequestCode());
				this.getManagedBean().setRequestCode(resDTO.getRequestCode());
			}
		} else {
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			RequestCodeResDTO resDTO = requestSO
					.retrieveUnspscCodeReview(codeDTO);
			this.getManagedBean().getReviewExtCodeList()
					.add(resDTO.getRequestCode());
			this.getManagedBean().setRequestCode(resDTO.getRequestCode());
		}

		this.getManagedBean().setTaskName(res.getTaskName());

		/*
		 * if(resDTO.getRequestCode().getCmRecommendCodes()!=null &&
		 * resDTO.getRequestCode().getCmRecommendCodes().size()>0){
		 * //codeBB.getManagedBean().setRecommendUnspscList(recommendUnspscList)
		 * }
		 */

		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setAction(res.getAction());

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqStatusDTO = new StatusReqDTO();
		reqStatusDTO.setLanguageCode(languageCode);
		reqStatusDTO.setStatusId(res.getDocStatus().longValue());
		StatusResDTO resStatusDTO = statusSO
				.retrieveStatusDescById(reqStatusDTO);

		this.getManagedBean().setDocStatus(
				resStatusDTO.getPmStatusDesc() != null ? resStatusDTO
						.getPmStatusDesc().getStatusName() : "Review");

		if (res.getTaskName().equalsIgnoreCase("Create Temporary Code")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "reviewBeforeCreateTempCode");
		} else if (res.getTaskName().equalsIgnoreCase("Create Extension Code")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "reviewBeforeCreateExtCode");
		} else if (res.getTaskName().equalsIgnoreCase("Select Recommended")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"selectRecommendUnspscCode.xhtml");
		} else if (res.getTaskName().equalsIgnoreCase("Publish Extension Code")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"reviewApproveExtensionCodeII.xhtml");
		} else if (res.getTaskName().equalsIgnoreCase("Result Notification")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"unspscResultNotification.xhtml");
		} else {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "reviewUnspscCode");

		}

	}

	public String deleteExtensionCode() {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CmRequestCode code = new CmRequestCode();
		CodificationBackingBean cdBB = new CodificationBackingBean();
		try {

			code.setItemId(cdBB.getManagedBean().getDeleteItem().getItemId());
			code.setTempCode(cdBB.getManagedBean().getDeleteItem()
					.getCmUnspsc().getUnspscCode());
			code.setTempTitle(cdBB.getManagedBean().getDeleteItem()
					.getCmUnspsc().getUnspscTitle());
			code.setBrandName(cdBB.getManagedBean().getDeleteItem()
					.getCmBrand().getBrandName());
			code.setItemName(cdBB.getManagedBean().getDeleteItem()
					.getItemName());
			code.setTypeName(cdBB.getManagedBean().getDeleteItem().getCmType()
					.getTypeName());
			code.setMeasurementName(cdBB.getManagedBean().getDeleteItem()
					.getCmMeasurement().getMeasurementName());
			code.setColorName(cdBB.getManagedBean().getDeleteItem()
					.getCmColor().getColorName());
			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome("SUBMIT");
			reqDTO.setRequestCode(code);
			reqDTO.setProcessId(CDProcessNameConst.EXTENSION_CODE_MAINTENANCE_PROCESS_ID);

			CodificationResDTO resDTO = unspscSO.addCodificationRequest(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "extensionSearch";
	}

	public SearchFilterBean getColorFilterBean() {
		return colorFilterBean;
	}

	public void setColorFilterBean(SearchFilterBean colorFilterBean) {
		this.colorFilterBean = colorFilterBean;
	}

	public SearchFilterBean getTypeFilterBean() {
		return typeFilterBean;
	}

	public void setTypeFilterBean(SearchFilterBean typeFilterBean) {
		this.typeFilterBean = typeFilterBean;
	}

	public SearchFilterBean getBrandFilterBean() {
		return brandFilterBean;
	}

	public void setBrandFilterBean(SearchFilterBean brandFilterBean) {
		this.brandFilterBean = brandFilterBean;
	}

	public SearchFilterBean getMeasurementFilterBean() {
		return measurementFilterBean;
	}

	public void setMeasurementFilterBean(SearchFilterBean measurementFilterBean) {
		this.measurementFilterBean = measurementFilterBean;
	}

	public String backToReviewUnspsc() {

		return "reviewBeforeCreateTempCode?faces-redirect=true";
	}

	public String backToCodificationSearch() {

		return "codificationView?faces-redirect=true";
	}

	public String requestExtensionCode() {

		if (this.getManagedBean().getRequestExtCodeList() != null) {
			this.getManagedBean().getRequestExtCodeList().clear();
		}

		CodificationBackingBean codeBB = new CodificationBackingBean();

		this.getManagedBean()
				.getSelectedLovUnspsc()
				.setUnspscCode(
						codeBB.getManagedBean().getSelectedUNSPSCObj()
								.getUnspscCode());
		this.getManagedBean()
				.getSelectedLovUnspsc()
				.setUnspscTitle(
						codeBB.getManagedBean().getSelectedUNSPSCObj()
								.getUnspscTitle());
		return "extensionCodeRequestForm?faces-redirect=true";
	}

	public String backToExtensionSearch() {

		return "extensionSearch?faces-redirect=true";
	}

	public void removeRecommendCode(int index) {

		this.getManagedBean().getRequestCode().getCmRecommendCodes()
				.remove(index);

	}

	public void removeExtensionRecord() {

		this.getManagedBean().getRequestExtCodeList().remove(0);// there is only
																// one record

	}

	public void addNewBpmRemark() {

		CmRequestRemark remark = new CmRequestRemark();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		System.out.println("pmUser:" + pmUser);
		if (pmUser != null) {
			System.out.println("pmUser.getUserName():" + pmUser.getUserName());
			remark.setCreatedUser(pmUser.getUserName());
		}

		System.out
				.println("remark.getCreatedUser():" + remark.getCreatedUser());

		this.getManagedBean().getReviewExtCodeList().get(0)
				.getCmRequestRemarks().add(remark);

		for (CmRequestCode code : this.getManagedBean().getReviewExtCodeList()) {
			code.setRemarkRequired(false);
			code.setRemarkRequiredII(false);
			code.setAddedRemark(true);
		}

	}

	public void selectRecommendCode() {

		this.getManagedBean().getRequestCode().setCmRecommendCodes(null);
		this.getManagedBean().getRequestCode().getCmRecommendCodes()
				.add(this.getManagedBean().getSelectedRecommendCode());
	}

	public String requestUnspscCode() {
		renewSesionObject();

		if (this.getManagedBean().getRequestExtCodeList() != null) {
			this.getManagedBean().getRequestExtCodeList().clear();
		}
		this.getManagedBean().getRequestCode().setItemType("P");
		return "unspscRequestForm";
	}

	public List<CmType> autoCompleteType(String query) {

		System.out.println("query:::::::::::::::::::::::::::::: " + query);
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

		System.out.println("query:::::::::::::::::::::::::::::: " + query);
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

		System.out.println("query:::::::::::::::::::::::::::::: " + query);
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

		System.out.println("query:::::::::::::::::::::::::::::: " + query);
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
				.handleNavigation(fc, null, "unspscRequestForm");
	}

	public int productOrServiceOperator() {
		if (this.getManagedBean().getRequestCode() != null) {
			if (this.getManagedBean().getRequestCode().getItemType()
					.equalsIgnoreCase("P")) {
				return 5; // means less than (76000000)
			} else {
				return 4; // means greater than (76000000)
			}
		}
		return -1;
	}

	public void checkingTypeRedundency(String typeInput) {
		log.info(".....checking redundancy for typeInput=" + typeInput);
		this.getManagedBean().setTypeRedundancy(false);
		if (typeInput != null && !typeInput.equals("")) {
			for (CmType type : this.getManagedBean().getTypeList()) {
				if (!StringUtils.isBlank(type.getTypeName())) {
					if (type.getTypeName().toLowerCase()
							.equals(typeInput.toLowerCase())) {
						this.getManagedBean().setTypeRedundancy(true);

						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.type",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
														.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingBrandRedundency(String brandInput) {
		log.info(".....checking redundancy for brandInput=" + brandInput);
		this.getManagedBean().setBarndRedundancy(false);
		if (brandInput != null && !brandInput.equals("")) {
			for (CmBrand brand : this.getManagedBean().getBrandList()) {
				if (!StringUtils.isBlank(brand.getBrandName())) {
					if (brand.getBrandName().toLowerCase()
							.equals(brandInput.toLowerCase())) {

						this.getManagedBean().setBarndRedundancy(true);

						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.brand",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
														.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingMeasurmentRedundency(String MeasurmentInput) {
		log.info(".....checking redundancy for MeasurmentInput="
				+ MeasurmentInput);
		this.getManagedBean().setMeasurRedundancy(false);
		if (MeasurmentInput != null && !MeasurmentInput.equals("")) {
			for (CmMeasurement measur : this.getManagedBean()
					.getMeasurementList()) {

				if (!StringUtils.isBlank(measur.getMeasurementName())) {
					if (measur.getMeasurementName().toLowerCase()
							.equals(MeasurmentInput.toLowerCase())) {
						this.getManagedBean().setMeasurRedundancy(true);

						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.measurement",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
														.getUserLocale()) });
					}
				}
			}
		}
	}

	public String backToPendingCodified() {
		return "searchUnspscToAssign";
	}

	public void checkingColorRedundency(String colorInput) {
		log.info(".....checking redundancy for colorInput=" + colorInput);
		log.info("colorList " + this.getManagedBean().getColorList());
		this.getManagedBean().setColorRedundancy(false);
		if (colorInput != null && !colorInput.equals("")) {
			for (CmColor color : this.getManagedBean().getColorList()) {
				if (!StringUtils.isBlank(color.getColorName())) {
					if (color.getColorName().toLowerCase()
							.equals(colorInput.toLowerCase())) {
						this.getManagedBean().setColorRedundancy(true);
						log.info("this.getManagedBean().isColorRedundancy()="
								+ this.getManagedBean().isColorRedundancy());
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.color",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
														.getUserLocale()) });

					}
				}
			}
			log.info("end no exception");
		}
	}

}
