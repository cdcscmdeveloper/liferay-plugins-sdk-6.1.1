package my.ep.web.backingbean.sm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.pm.general.settings.bpm.ICompositeValiditySO;
import my.ep.biz.service.sm.attachment.ISmAttachmentSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.supportingdocinquiry.ISupportingDocInquirySO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.certbody.CertBodyReqDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.pm.general.settings.bpm.CompositeValidityReqDTO;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.common.ssm.SsmReqDTO;
import my.ep.dto.sm.common.ssm.SsmResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.revertApplication.RevertApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryResDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sm.common.notification.SMNotificationCommonDataType;
import my.ep.jaxb.sm.mofappcandata.SMMOFAppCanDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmRegStatus;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplAuditors;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplInquiryDocFile;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplRevise;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmApplSupportDocInquiry;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmCategoryOverlapDoc;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmPersonnelSecretaries;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCategoryFacility;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierSystem;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvWorker;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewApplHistoryBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewSmApplBackingBean;
import my.ep.web.backingbean.sm.outputmsg.SmOutputMsgBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pp.common.PpCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationNotificationUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmFieldChangeUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmSideMenuIconUtil;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;
import my.ep.web.vo.SmLicenseInfoVO;
import my.ep.web.vo.SmMachineCategoryVO;
import my.ep.web.vo.SmPaymentDetailsVO;
import my.ep.web.vo.SmPersonnleCalcVO;
import my.ep.web.vo.SmSiteVisitFacilityVO;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "backingBean")
@RequestScoped
public class SmApplicationBackingBean extends BaseBackingBean<SmApplicationManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7293382095687532399L;

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private int currentTab;
	private int outputTab;
	private String buttonType = "";
	private StreamedContent file;
	private boolean alreadyExecute = false;
	NumberFormat formatter = new DecimalFormat("#0.00");
	SmRuleHelper ruleHelper = new SmRuleHelper();
	ViewApplHistoryBackingBean viewApplHistoryBackingBean;

	//#11641 - START
	private int mandatoryCounter = 1;
	private int nonMandatoryCounter = 1;
	//#11641 - END
	
	/**
	 * Instantiates a new sm application backing bean.
	 */
	public SmApplicationBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * Custom validation for required field
	 * 
	 * @param FacesContext
	 *            facesContext
	 * @param UIComponent
	 *            uiComponent
	 * @param Object
	 *            value
	 */
	public void validateRequiredField(FacesContext facesContext,
			UIComponent uiComponent, Object value) {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		if (!params.containsKey("validate")) {
			return;
		}
		String labelNameVal = (String) uiComponent.getAttributes().get(
				"labelName");
		UIInput component = (UIInput) uiComponent;

		if (!StringUtils.isBlank(labelNameVal)
				&& (component == null || component.getValue() == null || StringUtils
						.isBlank(component.getValue().toString()))) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Required field error",
					SmCommonUtil.getMessage("lbl.sm.required.msg",
							new String[] { labelNameVal })));
		}
	}

	public void validateEmail(FacesContext context, UIComponent component,
			Object value) {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		if (params.containsKey("disableValidate")) {
			return;
		}
		UIInput emailInput = (UIInput) component.getParent().findComponent(
				"equiEmail");
		String emailVal = (emailInput != null && emailInput.getValue() != null) ? emailInput
				.getValue().toString() : "";

		if (!StringUtils.isBlank(emailVal) && !isValidEmail(emailVal)) {
			String msg = SmCommonUtil.getMessage("lbl.sm.person.email", null)
					+ " - "
					+ SmCommonUtil.getMessage("lbl.sm.invalidEmail", null);
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Email Error", msg));
		}
	}

	/**
	 * Renew session object by application type.
	 * 
	 * @param applicationType
	 *            - the application type
	 */
	public void renewSesionObjectByApplicationType(String applicationType) {
		renewSesionObject();
		//log.info("*** renewSesionObjectByApplicationType *** " + applicationType);
		String errMsg = StringUtils.EMPTY;
		if (applicationType.equals("DPA")) {
			errMsg = getDeletePendingApplicationErrorMsg();
		}
		if (StringUtils.isBlank(errMsg)) {
			SmApplicationUtil.setNavigationForApplTnCPage(applicationType);
		} else {
			String[] params = null;
			FacesContext fc = FacesContext.getCurrentInstance();
			if (errMsg.equals(MessageConst.ERROR_722582)) {
				params = new String[] { getManagedBean().getSmAppl()
						.getApplNo() };
			}
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(errMsg, params,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())
									+ " (" + errMsg + ")"));
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							SmApplicationUtil.getApplicationErrorPage());
		}
	}

	/**
	 * Claim bpm task
	 */
	public void onClickClaimButton() {
		claimBpmTask();
		getManagedBean().setClaimTask(true);
	}

	/**
	 * On click delete HQ GST doc
	 * 
	 * @param SmGstSupplierDoc
	 *            doc
	 */
	public void onDeleteHqAttachment(SmGstSupplierDoc doc) {
		getManagedBean().setSelectedGstSuppDoc(doc);
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteHqDialog.show();");
	}

	// harris
	//#28807 - Start
	public void onDeleteSupDocFileAttachment(SmApplSupportDoc doc, int index, SmApplDocFile file) {
		this.getManagedBean().setSupportDoc(doc);
		this.getManagedBean().setAttachedFileIndex(index);
		this.getManagedBean().setSmApplDocFile(file);
		//#28807 - End
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteSupDocDialog.show();");
	}

	/**
	 * On click delete overlap category supporting doc
	 * 
	 * @param SmGstSupplierDoc
	 *            doc
	 */
	public void onDeleteOverlapCatAttachment(SmCategoryOverlapDoc doc) {
		getManagedBean().setSelectedOverlapDoc(doc);
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteOverlapCatDialog.show();");
	}

	public void onViewOverlapCategory() {
		RequestContext.getCurrentInstance().execute(
				"overlapCategoryDialog.show();");
	}

	//13362 START
	public CellStyle columnStyle(Workbook wb, 
			CreationHelper createHelper,
			Boolean isCellListHeader,
			Boolean isBold, 
			Boolean isHAlignCenter,
			Boolean isFormatDate,
			Boolean isWrapTxt
			){
		
		Font contentFont = wb.createFont();
		contentFont.setFontHeightInPoints((short)10);  
		contentFont.setFontName("Arial"); 
		if(isBold)contentFont.setBold(true);
		else contentFont.setBold(false);
		
		CellStyle cellStyle = wb.createCellStyle();
		if(isCellListHeader){
			cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());  
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		if(isHAlignCenter)cellStyle.setAlignment(HorizontalAlignment.CENTER);
		if(isFormatDate)cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		if(isWrapTxt)cellStyle.setWrapText(true);
		cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
	
		cellStyle.setFont(contentFont);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		
		return cellStyle;
}

	public void generateAndDownloadOverlapCategoryDetailsExcel()throws FileNotFoundException, IOException,
		EncryptedDocumentException, InvalidFormatException {
		
		//START: Generate Report In Excel
		String[] colHeaderReportList = {
					SmCommonUtil.getMessageMalay("lbl.sm.userprofile.num", null), 
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.name", null), 
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.idno", null), 
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.rolename", null),
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.compname", null), 
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.mofno", null),  
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.expdate", null),
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.regno", null),
					SmCommonUtil.getMessageMalay("lbl.sm.overlapped.category.personnel.applno", null),
					SmCommonUtil.getMessageMalay("lbl.sm.application.status", null)
		};
		
		//CREATE WORKBOOK
		Workbook wb = new XSSFWorkbook();
	
		//OPTIONAL - Set font
		Font contentFont = wb.createFont();
		contentFont.setBold(true);
		contentFont.setFontHeightInPoints((short)10); 
		contentFont.setFontName("Arial");
			
		//OPTIONAL - Set cell style (cell alignment/Cell format/etc ...)
		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle titleStyle = wb.createCellStyle(); 
		titleStyle.setFont(contentFont);
		
		CellStyle columnHeaderStyle = columnStyle(wb,createHelper,true,true,true,false,false);
		CellStyle colStyleDate = columnStyle(wb,createHelper,false,false,true,true,false);
		CellStyle colStyleString = columnStyle(wb,createHelper,false,false,false,false,true);
		CellStyle colStyleBil = columnStyle(wb,createHelper,false,false,true,false,true);
		//END CELL STYLE
		
		//CREATE SHEET
		Sheet sheetLaporan = wb.createSheet("Sheet1");	
	
		//CREATE ROW & CELL(COLUMN) - MAKLUMAT PERTINDIHAN BIDANG
		Row titleRow = sheetLaporan.createRow(0); //Create Row
		Cell cellTitleRow = titleRow.createCell(0);//Create Cell (Column)
		cellTitleRow.setCellValue(StringUtils.upperCase(SmCommonUtil.getMessageMalay("lbl.sm.overlapping.category", null))); // Set Column Value
		cellTitleRow.setCellStyle(titleStyle);  
		sheetLaporan.addMergedRegionUnsafe(new CellRangeAddress(0,0,0,9)); //to merge cell in selected row
		
		List<SmBulkApplOverlapCatVO> overlapCategoryList = SmApplicationUtil.checkOverLappingCategoriesMalay(getManagedBean());
		
		if(overlapCategoryList != null){
			log.info("OVERLAP CATEGORY SIZE ---->"+ overlapCategoryList.size());
			for(int x=0; x<overlapCategoryList.size(); x++){
				//Create Group Title Row & Cell by Category Code
				int addRowIndexGroupTitle = sheetLaporan.getLastRowNum()+2;
				Row subTitleRow = sheetLaporan.createRow(addRowIndexGroupTitle);
				Cell cellSubTitleRow = subTitleRow.createCell(0); 
				cellSubTitleRow.setCellValue(overlapCategoryList.get(x).getCategoryCode()+" "+overlapCategoryList.get(x).getCategoryName());
				cellSubTitleRow.setCellStyle(titleStyle); 	
				sheetLaporan.addMergedRegionUnsafe(new CellRangeAddress(addRowIndexGroupTitle,addRowIndexGroupTitle,0,9));//to merge cell in selected row
			
				if(overlapCategoryList.get(x).getOverlapInfoList() != null){
					//Create Row & Cell Title Header
					Row headerRowList = sheetLaporan.createRow(addRowIndexGroupTitle+2);
					for(int i = 0; i < colHeaderReportList.length; i++) {
						Cell cell = headerRowList.createCell(i);
						cell.setCellValue(colHeaderReportList[i]);
						cell.setCellStyle(columnHeaderStyle);
					}
					
					//Create Row - Populate Data from resultSet List
					int rowid = headerRowList.getRowNum()+1; //To get Header Row Number/Index In Excel
					int countRow = 1; //Initialize to count number of record (populate Cell No. value)
					for(int y=0; y<overlapCategoryList.get(x).getOverlapInfoList().size(); y++){
						//Start Populate data for each cell.
						Row rowDtl = sheetLaporan.createRow(rowid++);
						Cell cellBil = rowDtl.createCell(0); //CELL : No
						cellBil.setCellValue(countRow);
						cellBil.setCellStyle(colStyleBil);
						
						Cell cellPersonnelName = rowDtl.createCell(1); //CELL : Nama Personnel
						cellPersonnelName.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getPersonnelName());
						cellPersonnelName.setCellStyle(colStyleString);
						
						Cell cellIdNo = rowDtl.createCell(2); //CELL : Identification No
						cellIdNo.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getIdentificationNo());
						cellIdNo.setCellStyle(colStyleString);
						
						Cell cellRole = rowDtl.createCell(3); //CELL : Role In Company
						cellRole.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getRole());
						cellRole.setCellStyle(colStyleString);
						
						Cell cellCompanyName = rowDtl.createCell(4); //CELL : Company Name
						cellCompanyName.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getCompanyName());
						cellCompanyName.setCellStyle(colStyleString);
						
						Cell cellMofNo = rowDtl.createCell(5); //CELL : MOF NO.
						cellMofNo.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getMofNo());
						cellMofNo.setCellStyle(colStyleString);
						
						Cell cellExpDate = rowDtl.createCell(6); //CELL : Tarikh Luput
						cellExpDate.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getExpDate());
						cellExpDate.setCellStyle(colStyleString);
						
						Cell cellRegNo = rowDtl.createCell(7); //CELL : Business Registration No
						cellRegNo.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getRegNo());
						cellRegNo.setCellStyle(colStyleString);
						
						Cell cellApplNo = rowDtl.createCell(8); //CELL : Application No.
						cellApplNo.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getApplNo());
						cellApplNo.setCellStyle(colStyleString);
						
						Cell cellStatus = rowDtl.createCell(9); //CELL : Application Status
						cellStatus.setCellValue(overlapCategoryList.get(x).getOverlapInfoList().get(y).getApplStatus());
						cellStatus.setCellStyle(colStyleString);
						
						countRow++;
					}
				}
			}
		}else{
			log.info("Overlap is null !!!! ");
		}
		
		// Resize all columns to fit the content size
		for(int i = 0; i < colHeaderReportList.length; i++) {   
			if(i==0)sheetLaporan.setColumnWidth(i, 8*256);//Col : Bil
			else if(i==1)sheetLaporan.setColumnWidth(i, 52*256); //Col : Personnel Name
			else if(i==2)sheetLaporan.setColumnWidth(i, 21*256); //Col : No. Pengenalan
			else if(i==3)sheetLaporan.setColumnWidth(i, 52*256); //Col : Role
			else if(i==4)sheetLaporan.setColumnWidth(i, 52*256); //Col : Company Name
			else if(i==5)sheetLaporan.setColumnWidth(i, 20*256); //Col : Mof. No			
			else if(i==6)sheetLaporan.setColumnWidth(i, 13*256); //Col : Tarikh
			else if(i==7)sheetLaporan.setColumnWidth(i, 27*256); //Col : Reg. No
			else if(i==8)sheetLaporan.setColumnWidth(i, 20*256); //Col : Application No.
			else if(i==9)sheetLaporan.setColumnWidth(i, 25*256); //Col : Status
		}
				
		//END - Generate Report In EXCEL
	
		//START: Download 
		PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);
		
		response.setContentType("application/ms-excel");
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=\"SM_OverlappingDetails.xlsx\"");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.flushBuffer();
		
		OutputStream outStream = response.getOutputStream();
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		wb.write(outByteStream);//Write into file
		byte [] outArray = outByteStream.toByteArray();					
		
		outStream.write(outArray);
		outStream.flush();
		outStream.close();
		wb.close();//Close Workbook
		
		//END: Download
	}

	//13362 END
	/**
	 * Change business type & display comfirmation dialog
	 */
	public void onChangeBussType() {
		assignSupplierBusinessType(getManagedBean().getSmSupplier());
		RequestContext.getCurrentInstance().execute(
				"changeBussTypeConfirmDialog.show();");
	}

	/**
	 * Delete HQ GST attachment
	 */
	public void deleteHqAttachment() {
		SmGstSupplierDoc mainDoc = (SmGstSupplierDoc) EntityHelper
				.getEntityBySinglePropertyFromList(getManagedBean()
						.getSelectedGstSuppDoc().getPmDocument()
						.getDocumentId(), getManagedBean().getCompanyBasicGst()
						.getSmGstSupplierDocs(), "pmDocument");
		mainDoc.setFileName(StringUtils.EMPTY);
		mainDoc.setFilePath(StringUtils.EMPTY);
	}

	/**
	 * Delete overlap category attachment
	 */
	public void deleteOverlapCategoryAttachment() {
		SmCategoryOverlapDoc doc = (SmCategoryOverlapDoc) EntityHelper
				.getEntityBySinglePropertyFromList(getManagedBean()
						.getSelectedOverlapDoc().getPmDocument()
						.getDocumentId(), getManagedBean().getOverlapDocs(),
						"pmDocument");
		doc.setFileName(StringUtils.EMPTY);
		doc.setFilePath(StringUtils.EMPTY);
		doc.setOverlapReason(null);
		if(doc.getCategoryOverlapDocId() != null){ //#31018
			SmServiceLookup.getMofRegistrationSO().deleteCategoryCodeAttachmentDoc(doc);
		}
	}

	public void deleteBrAttachment(SmGstSupplierDoc doc) {
		doc.setFileName(StringUtils.EMPTY);
		doc.setFilePath(StringUtils.EMPTY);
	}

	/**
	 * Pre-render re-submission questionnaire page.
	 */
	public void prerenderResubmissionQuestionaire() {
		SmSupplier currLoginSupplier = SmApplicationUtil.getCurrentSupplier();
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			log.info("**** Prerender Questions *************");
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			HttpServletRequest originalServletRequest = new FacesScopes()
					.getOriginalServletRequest();
			String applId = originalServletRequest.getParameter("apId");

			log.info("application id : " + applId);
			SmAppl rejAppl = null;
			SmSupplier supplierProfile = null;
			String xhtml = "/xhtml/sm/resubmitApplication/prerenderResubmitApplication.xhtml";
			String resubmitErrorKey = StringUtils.EMPTY;
			if (applId != null) {
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = SmApplicationUtil.retriveApplicationById(Integer
						.parseInt(applId));
				rejAppl = mofResDto.getSmAppl();
				supplierProfile = mofResDto.getSmSupplier();
			}

			this.getManagedBean().setValidResubmit(true);
			if (rejAppl != null
					&& supplierProfile != null
					&& currLoginSupplier != null
					&& currLoginSupplier.getSupplierId().compareTo(
							supplierProfile.getSupplierId()) == 0) {
				boolean activeExist = false;
				activeExist = SmApplicationUtil
						.checkActiveSmApplForSupp(supplierProfile);
				if (!SmApplicationUtil
						.isRejectedApplValidForResubmission(rejAppl)) {
					if (rejAppl.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
						resubmitErrorKey = "lbl.sm.resubmitExpired.inprogress";
					} else {
						resubmitErrorKey = "lbl.sm.resubmitExpired";
					}
					this.getManagedBean().setValidResubmit(false);
				}
				if (!activeExist && this.getManagedBean().isValidResubmit()) {
					MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
					reqDTO.setCreatedDate(rejAppl.getCreatedDate());
					reqDTO.setSupplierId(supplierProfile.getSupplierId());
					reqDTO.setAppType(rejAppl.getApplType());

					MofRegistrationResDTO resDto = null;
					// 1.1. Check for resubmitted and rejected
					StringBuilder sb = new StringBuilder();
					sb.append("(")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)
							.append(",")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)
							.append(",")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM)
							//#32627 START
							.append(",")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_SDO_INQUIRY)
							.append(",")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PO_INQUIRY)
							.append(",")
							.append(SupplierManagementConst.PM_STATUS_AUTOREJECT_UNSUBMIT_OFFLINE_DOCUMENT)
							//#32627 END
							.append(")");
					reqDTO.setWorkflowStatus(sb.toString());
					resDto = mofRegistrationSO
							.checkForApplicationDuplicateResubmission(reqDTO);
					if (resDto != null && resDto.getResubmitApplnCount() > 0) {
						resubmitErrorKey = "lbl.sm.resubmitAndRejectApplicationCantProceed";
						this.getManagedBean().setValidResubmit(false);
					}
					// 1.2 Check for resubmitted and approved
					if (this.getManagedBean().isValidResubmit()) {
						sb = new StringBuilder();
						sb.append("(")
								.append(SupplierManagementConst.PM_STATUS_MOF_ACCT_REG_APPROVED)
								.append(",")
								.append(SupplierManagementConst.PM_STATUS_MOF_ACCT_MAINTAIN_APPROVED)
								.append(")");
						reqDTO.setWorkflowStatus(sb.toString());
						resDto = mofRegistrationSO
								.checkForApplicationDuplicateResubmission(reqDTO);
						if (resDto != null
								&& resDto.getResubmitApplnCount() > 0) {
							resubmitErrorKey = "lbl.sm.approvedApplicationCantProceed";
							this.getManagedBean().setValidResubmit(false);
						}
					}
					// 1.3 Check for resubmitted and pending final payment
					if (this.getManagedBean().isValidResubmit()) {
						sb = new StringBuilder();
						sb.append("(")
								.append(SupplierManagementConst.PM_STATUS_PENDING_PAYMENT)
								.append(")");
						reqDTO.setWorkflowStatus(sb.toString());
						resDto = mofRegistrationSO
								.checkForApplicationDuplicateResubmission(reqDTO);
						if (resDto != null
								&& resDto.getResubmitApplnCount() > 0) {
							resubmitErrorKey = "lbl.sm.approvedApplicationPendingFinalPay";
							this.getManagedBean().setValidResubmit(false);
						}
					}
					//#32002-start
					if (this.getManagedBean().isValidResubmit()) {
						sb = new StringBuilder();
						sb.append("(")
								.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)
								.append(",")
								.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)
								.append(")");
						reqDTO.setWorkflowStatus(sb.toString());;
						try {
						SmMofAccount resultList = SmServiceLookup.getMofRegistrationSO().getLatestMofAccountBySupplierId(supplierProfile.getSupplierId());
							if (reqDTO.getAppType().equals("R")) {
								if (resultList != null) {
									if ((supplierProfile.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED && resultList
											.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED)) {
										resubmitErrorKey = "lbl.sm.resubmitExpired.mofExpired";
										this.getManagedBean().setValidResubmit(false);
									}
									if ((supplierProfile.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE && resultList
											.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED)) {
										resubmitErrorKey = "lbl.sm.resubmitExpired.mofExpired";
										this.getManagedBean().setValidResubmit(false);
									}
								}
							}
						} catch (EpException ex) {
							log.info(">>>>>>>>>>ERROR RENEW RESUBMIT APPLICATION:"+ ex.getMessage() + "<<<<<<<<<<<");
						}
					}
					//#32002-end
					
					if (this.getManagedBean().isValidResubmit()
							&& rejAppl.getCreatedDate().compareTo(
									supplierProfile.getSmAppl()
											.getCreatedDate()) < 0) {
						resubmitErrorKey = "lbl.sm.anotherApplApproved";
						this.getManagedBean().setValidResubmit(false);
					}
					if (this.getManagedBean().isValidResubmit()) {
						SmAppl latestResubmitAppl = null;
						SmAppl rejectedAppl = SmApplicationUtil
								.retriveApplicationById(
										Integer.parseInt(applId)).getSmAppl();
						MofRegistrationReqDTO mofReqDto = new MofRegistrationReqDTO();

						mofReqDto = new MofRegistrationReqDTO();
						mofReqDto.setApplId(Long.parseLong(applId));
						mofReqDto.setSmSupplier(supplierProfile);
						try {
							MofRegistrationResDTO mofRespDto = mofRegistrationSO
									.cloneSmApplicationForResubmissionDraft(mofReqDto);
							latestResubmitAppl = mofRespDto.getSmAppl();
						} catch (EpException ex) {
							resubmitErrorKey = "lbl.sm.resubmission.invalid";
							this.getManagedBean().setValidResubmit(false);
							log.info(">>>>>>>>>>ERROR CLONE NEW RESUBMIT APPLICATION:"
									+ ex.getMessage() + "<<<<<<<<<<<");
						}

						if (getManagedBean().isValidResubmit()) {
							getManagedBean().setSmAppl(latestResubmitAppl);
							getManagedBean().setSmSupplier(
									latestResubmitAppl.getSmSupplier());
							//#29086
							if (!this.getManagedBean().getSmAppl().getApplType()
									.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
									&& !this.getManagedBean().getSmAppl().getApplType()
											.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
									&& !this.getManagedBean().getSmAppl().getApplType()
											.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
								SmApplicationUtil.checkEquityOwnerUpdateAllowance(getManagedBean());
							}
							//#27232 - START SSM CHANGES FOR RESUBMIT
							if(SmCommonUtil.getInstance().ssmActive() && businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupplier().getRegNo(),this.getManagedBean().getSmSupplier().getBusinessType())){
								ssmIntegrationResubmit();
							}							
							//#27232 - END SSM CHANGES FOR RESUBMIT
							// already answer questionnaire, redirect to
							// application page
							if (rejectedAppl.isQuestionnaire()
									|| rejectedAppl
											.getApplType()
											.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {

								FacesContext
										.getCurrentInstance()
										.addMessage(
												null,
												new FacesMessage(
														FacesMessage.SEVERITY_INFO,
														null,
														SmCommonUtil
																.getMessage(
																		"lbl.sm.submit.questionaire.msg",
																		null)));

								// automatically initiate new BPM task
								SmAppl smAppl = SmApplicationUtil
										.initiateMofApplNewBpmTask(getManagedBean()
												.getSmAppl());

								if (smAppl.isSuccess()
										&& smAppl.getInstanceId() != null) {
									// update back instance id
									mofReqDto = new MofRegistrationReqDTO();
									mofReqDto.setSmAppl(smAppl);
									getManagedBean().setSmAppl(
											SmServiceLookup
													.getMofRegistrationSO()
													.updateSmAppl(mofReqDto)
													.getSmAppl());
								}

								prerenderApplicationForResubmission();
							}

							// not yet answer questionnaire, redirect to
							// questionnaire page
							else {
								FacesContext fc = FacesContext
										.getCurrentInstance();
								QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
								quesBackingBean.getManagedBean().setTargetUrl(
										xhtml);
								quesBackingBean.getManagedBean().setSmSupp(
										supplierProfile);
								quesBackingBean.getManagedBean().setSmAppl(
										latestResubmitAppl);
								quesBackingBean.getManagedBean()
										.setRejectedAppl(rejectedAppl);
								quesBackingBean.retrieveQuestionnaire();
								fc.getApplication()
										.getNavigationHandler()
										.handleNavigation(fc, null,
												"/xhtml/sm/questionnaire/questionnaireView.xhtml");
							}
						} else {
							redirectToResubmitErrorPage(resubmitErrorKey);
						}

					} else {
						redirectToResubmitErrorPage(resubmitErrorKey);
					}
				} else {
					if (StringUtils.isBlank(resubmitErrorKey)) {
						redirectToResubmitErrorPage("lbl.sm.activeDrftExist");
					} else {
						redirectToResubmitErrorPage(resubmitErrorKey);
					}
				}

			} else {
				redirectToResubmitErrorPage("lbl.sm.resubmission.invalid");
			}
		} finally {
			mofRegistrationSO = null;
		}

	}

	private void redirectToResubmitErrorPage(String labelKey) {
		getManagedBean().setResubmitInValidMsg(
				SmCommonUtil.getMessage(labelKey, null));
		this.getManagedBean().setValidResubmit(false);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
				getManagedBean().getResubmitInValidMsg()));
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						SmApplicationUtil.getApplicationErrorPage());
	}

	/**
	 * Pre-render application for re-submission after finishing the answer
	 * questionnaire
	 */
	public void prerenderApplicationForResubmission() {
		log.info("**** Prerender Application *************");
		SmAppl appl = getManagedBean().getSmAppl();
		SmSupplier supplierProfile = getManagedBean().getSmSupplier();

		try {
			if (appl != null && appl.getApplId() != null) {
				MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
				reqDTO.setApplId(appl.getApplId());
				MofRegistrationResDTO resDTO = SmServiceLookup
						.getMofRegistrationSO()
						.retrieveCurrentDraftApplication(reqDTO);
				appl = resDTO.getSmAppl();
				supplierProfile = appl.getSmSupplier();
			}
		} finally {
		}

		if (appl != null && supplierProfile != null) {

			appl.setSdoReceivedDate(null);
			proceedToApplication(appl.getApplType(), Boolean.TRUE, appl,
					supplierProfile);
			getManagedBean().getSmAppl().setResubmit(true);
			Calendar c = new GregorianCalendar();
			c.add(Calendar.DATE, ruleHelper.getApplicationSubmissionDays());
			Date d = c.getTime();
			getManagedBean().getSmAppl().setDraftExpiryDate(d);
			if (getManagedBean().getSmAppl().getSmApplSectionReviews() != null
					&& !getManagedBean().getSmAppl().getSmApplSectionReviews()
							.isEmpty()) {
				getManagedBean().getSmAppl().getSmApplSectionReviews().clear();

			}
			if (getManagedBean().getSmAppl().getRecordStatusFlg()
					.compareTo(DbValueConst.RECORD_STATUS_DELETED) == 0) {
				getManagedBean().getSmAppl().setRecordStatusFlg(
						DbValueConst.RECORD_STATUS_ACTIVE);
			}
			getManagedBean().getSmAppl().setQuestionnaire(false);
			if (getManagedBean().getSmAppl().getSmSvTasks() != null
					&& !getManagedBean().getSmAppl().getSmSvTasks().isEmpty()) {
				for (SmSvTask task : getManagedBean().getSmAppl()
						.getSmSvTasks()) {
					task.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}
			}
			
			//Bug 27022 - start 
			//#30766 & #34209 - START
			/*SmAppl latestAppl = supplierProfile.getSmAppl();
			if(latestAppl != null 
					 && latestAppl.getSmSupplierCategories() != null
					 	&& getManagedBean().getSmAppl().getSmSupplierCategories() != null){
				 for(SmSupplierCategory categoryRejectedAppl : latestAppl.getSmSupplierCategories()){
					 if(categoryRejectedAppl.getRecordStatusFlg() == SupplierManagementConst.DELETED_RECORD_STATUS){
						 for(SmSupplierCategory categoryResubmitAppl: getManagedBean().getSmAppl().getSmSupplierCategories()){
							 if(categoryRejectedAppl.getPmCategoryL3().getCategoryL3Id()
									 .equals(categoryResubmitAppl.getPmCategoryL3().getCategoryL3Id())){
								 categoryResubmitAppl.setRecordStatusFlg(SupplierManagementConst.DELETED_RECORD_STATUS);
							 }
						 }
					 }
				 }
			 }*/
			//#30766 & #34209 - END
			//Bug 27022 - end
			 
			 //3460 - Start
			SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
			//to resubmit application for applying new MOF registration
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
				SmAppl appli = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(
						currSupp.getSupplierId(), ParameterConst.NEW_SM_APPLN_FOR_MOF);
				SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
				reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
				reqDTO.setDocId(appli.getApplId());

				 SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);
				if (resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
					SmRemark remark = resDTO.getRemarksList().get(0);
					getManagedBean().setSdoRemark(remark.getRemark());
				}
			}
			//To resubmit application for applying to update profile
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)) {
				SmAppl appli = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(
						currSupp.getSupplierId(), ParameterConst.NEW_SM_APPLN_FOR_UPDATE);
				SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
				reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
				reqDTO.setDocId(appli.getApplId());

				 SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);
				if (resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
					SmRemark remark = resDTO.getRemarksList().get(0);
					getManagedBean().setSdoRemark(remark.getRemark());
				}
			}
			//To resubmit application for applying to renew profile
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
				SmAppl appli = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(
						currSupp.getSupplierId(), ParameterConst.NEW_SM_APPLN_FOR_RENEW);
				SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
				reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
				reqDTO.setDocId(appli.getApplId());

				SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);
				if (resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
					SmRemark remark = resDTO.getRemarksList().get(0);
					getManagedBean().setSdoRemark(remark.getRemark());
				}
			}
			//to resubmit application for applying new category
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
				SmAppl appli = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(
						currSupp.getSupplierId(), ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES);
				SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
				reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
				reqDTO.setDocId(appli.getApplId());

				 SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);
				if (resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
					SmRemark remark = resDTO.getRemarksList().get(0);
					getManagedBean().setSdoRemark(remark.getRemark());
				}
			}
			//to resubmit application for Bumiputera Status
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
				SmAppl appli = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(
						currSupp.getSupplierId(), ParameterConst.NEW_SM_APPLN_FOR_BUMI);
				SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
				reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
				reqDTO.setDocId(appli.getApplId());

				 SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);
				if (resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
					SmRemark remark = resDTO.getRemarksList().get(0);
					getManagedBean().setSdoRemark(remark.getRemark());
				}
			}
			//3460 - End
			if (getManagedBean().getSmAppl().getSmSupplierCategories() != null
					&& !getManagedBean().getSmAppl().getSmSupplierCategories()
							.isEmpty()) {
				for (SmSupplierCategory cat : getManagedBean().getSmAppl()
						.getSmSupplierCategories()) {
					if (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						if (cat.getPoDecFlg() != null) {
							cat.setPrevApprovedByPo(cat.isApprovedByPo());
							cat.setPrevPoDecFlg(cat.getPoDecFlg());
							cat.setPoDecFlg(null);
						}
						if (!StringUtils.isEmpty(cat.getPoRemarks())) {
							cat.setPrevPoRemarks(cat.getPoRemarks());
							cat.setPoRemarks(null);
						}
						if (cat.getApprvrDecFlg() != null) {
							cat.setPrevApprovedByAp(cat.isApprovedByAp());
							cat.setPrevApprDecFlg(cat.getApprvrDecFlg());
							cat.setApprvrDecFlg(null);
						}
						if (!StringUtils.isEmpty(cat.getApproverRemarks())) {
							cat.setPrevApproverRemarks(cat.getApproverRemarks());
							cat.setApproverRemarks(null);
						}

					}

				}
			}

			setNavigationForResubmitAppln(appl.getApplType());
		} else {
			// redirect & display resubmit expired
			this.getManagedBean().setValidResubmit(false);
		}

	}

	/**
	 * Process draft application from BPM.
	 * 
	 * @param applicationType
	 *            - the application type
	 * @param fromBpm
	 *            - boolean true ,if from task list and false , if application
	 *            from screen
	 * @param appl
	 *            - SmAppl object
	 * @param supp
	 *            - SmSupplier object
	 * 
	 */
	public void processDraftApplication(String applicationType,
			boolean fromBpm, SmAppl appl, SmSupplier supp) {
		proceedToApplication(applicationType, fromBpm, appl, supp);

	}

	/**
	 * Proceed to application UI , click on proceed.
	 * 
	 * @param applicationType
	 *            - the application type
	 * @return the the redirect page
	 */
	public String proceedToApplication(String applicationType) {
		return proceedToApplication(applicationType, Boolean.FALSE, null, null);
	}
	
	/**
	 * Gets the max date.
	 * 
	 * @return the max date
	 */
	public String getMaxDate() {
		DateFormat df = new SimpleDateFormat();
		log.info("*** Date String *** " + df.format(new Date()));
		return df.format(new Date());
	}

	/**
	 * Proceed to application from UI and also BPM task list.
	 * 
	 * @param applicationType
	 *            - the application type
	 * @param fromBpm
	 *            - boolean true ,if from task list and false , if application
	 *            from screen
	 * @param appl
	 *            - SmAppl object
	 * @param supp
	 *            - SmSupplier object
	 * @return the redirect page
	 */
	public String proceedToApplication(String applicationType, boolean fromBpm,
			SmAppl appl, SmSupplier supp) {
		String sessionId = ((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestedSessionId(); 
		long subStartTime = 0L;
		// #16052019 - SSM INTEGRATION CR
		getManagedBean().setSsmIntegrationActive(SmCommonUtil.getInstance().ssmActive());
		//#Enhancement #11765
		alreadyExecute = false;//avoid duplicate execute function setDefaultApproverReviewSection() && setDefaultPoReviewSection()
		getManagedBean().setRedirectToPersonPage(false);
		assignValReqdToFalse();
		if (!applicationType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {

			log.info("*** Today ****" + getManagedBean().getToday());
			getManagedBean().setCountryLs(
					SmApplicationUtil.retrievePmCountryList());
			getManagedBean().setCountryItems(SmCommonUtil.getCountryList());
			getManagedBean().setProfBodyLs(
					SmApplicationUtil.retrievePmProfBodyList());
			getManagedBean().setProfBodyItems(
					SmApplicationUtil.getProfBodyItems());

			getManagedBean().setEduFieldList(
					SmApplicationUtil.retrieveEduFieldList());
			getManagedBean().setRacesList(
					SmApplicationUtil.retrievePmRaceList());
			getManagedBean().setGlpCert(
					SmApplicationUtil.retrievePmParameterByTypeAndCode(
							SupplierManagementConst.SM_SUPPLIER_PARAM_TYPE_CRT,
							SupplierManagementConst.SM_SUPPLIER_CERT_TYPE_GLP));
			getManagedBean().setAcadQualifications(
					SmApplicationUtil
							.getAcademicQualifications(getManagedBean()));
		}

		getManagedBean()
				.setDefPmCountry(
						SmCommonUtil
								.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		SmAppl smAppl = new SmAppl();
		SmSupplier smApplSupplier = new SmSupplier();

		MofRegistrationResDTO resDto = new MofRegistrationResDTO();
		
		log.info("[proceedToApplication] #31754 fromBpm: " + fromBpm);
		if (!fromBpm) {
			subStartTime = System.currentTimeMillis();
			SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "getCurrentSupplier-1", 
					subStartTime); //20211005@fadhli - #31754
			boolean cannotProceedAlertMsg = false;

			if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {

				// Not allowed on boarding supplier to proceed with any
				// application if still not yet active (#24106)
				if (currSupp != null && currSupp.getRecordStatusFlg() == 7) {
					throw new EpException(MessageConst.ERROR_722672);
				}

				cannotProceedAlertMsg = SmApplicationUtil
						.checkActiveCancelSmApplForSupp(currSupp);
				if (cannotProceedAlertMsg) {
					throw new EpException(MessageConst.ERROR_722542);
				}
//#17906
				subStartTime = System.currentTimeMillis();
				boolean hasActiveTransactions = SmApplicationUtil
						.hasActiveTransactions(currSupp);
				SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
						"SmApplicationBackingBean.proceedToApplication", "hasActiveTransactions-4", 
						subStartTime); //20211005@fadhli - #31754
				subStartTime = System.currentTimeMillis();
				boolean hasOtherActiveTransactions = SmApplicationUtil
						.hasOtherActiveTransactions(currSupp);
				SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
						"SmApplicationBackingBean.proceedToApplication", "hasOtherActiveTransactions-5", 
						subStartTime); //20211005@fadhli - #31754
				if (hasActiveTransactions || hasOtherActiveTransactions) {
					throw new EpException(MessageConst.ERROR_722565);
				}
			}

			// Disabled blocking if has active transaction for cancel MOF
			if (!applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {

				// Not allowed on boarding supplier to proceed with any
				// application if still not yet active (#24106)
				if (currSupp != null && currSupp.getRecordStatusFlg() == 7) {
					throw new EpException(MessageConst.ERROR_722672);
				}

				cannotProceedAlertMsg = SmApplicationUtil
						.checkActiveSmApplForSupp(currSupp);
				if (cannotProceedAlertMsg) {
					throw new EpException(MessageConst.ERROR_722542);
				}
			}

			if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {

				// individual registration type cant proceed to upgrade to
				// MOF
				if (currSupp != null
						&& currSupp
								.getBusinessType()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)) {
					throw new EpException(MessageConst.ERROR_722607);
				}
				// government seller type cant proceed to upgrade to
				// MOF
				if (currSupp != null
						&& currSupp
								.getBusinessType()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER)) {
					throw new EpException(MessageConst.ERROR_722628);
				}
				
				//#31549 - START
				if(currSupp != null && currSupp.getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)){
					
					IPersonnelSO personnelSO = null;
					try {
						personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm", IPersonnelSO.class);
						PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
						personnelReqDTO.setApplId(currSupp.getSmAppl().getApplId());
						currSupp.getSmAppl().setSmPersonnels(personnelSO.retrievePersonnelListByApplId(personnelReqDTO).getSmPersonnelList());
						
					} finally {
						personnelSO = null;
					}
					
					for(SmPersonnel smPersonnel : currSupp.getSmAppl().getSmPersonnels()){
						if(smPersonnel.getEpRole() != null && smPersonnel.getEpRole().equals(SupplierManagementConst.BASIC_SUPPLIER_ADMIN)){
							throw new EpException(MessageConst.ERROR_722704);
						}
					}
				}
				//#31549 - END
				
				subStartTime = System.currentTimeMillis();
				// 25157
				SmAppl rejectedAppl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppType(
						currSupp.getSupplierId(), applicationType);
				//#36474 - Start
				//Add checking check expired for KN
				SmWorkflowStatus rejectedWf = rejectedApplExist(currSupp.getSupplierId());
				SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
						"SmApplicationBackingBean.proceedToApplication", "getLatesRejectedApplicationBySuppIdAndAppType-6", 
						subStartTime); //20211005@fadhli - #31754
				if (rejectedAppl != null && my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_MOF.equalsIgnoreCase(currSupp.getSmAppl().getApplType())) {
					Timestamp currentDate = new Timestamp(System.currentTimeMillis());
					long diff = currentDate.getTime()
							- rejectedWf.getCreatedDate().getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);
					//If expired set null
					if (diffDays >= SmRuleHelper.getInstance().getApplicationResubmissionDays()) {
						rejectedAppl = null;
					}
				}
				//#36474 - End
				String labelNameVal = "";
				//27951 - If from resubmit appl history/status no need display message
				if (rejectedAppl != null && !this.getManagedBean().isPreRegistration()) {
					FacesContext.getCurrentInstance().addMessage("rejectedExist", new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							SmCommonUtil.getMessage("lbl.sm.supplier.promo.rejected",
									new String[] { labelNameVal })));
					SmCommonUtil.redirectToPage(
							"/xhtml/sm/payment/portletViewMode.xhtml", "", null, null);
				}

				String currSuppType = currSupp.getSmAppl()
						.getSupplierType();
				
				if(StringUtils.isBlank(currSuppType) || currSuppType.equals("B")){
					currSuppType = "K";
				}

				if (getManagedBean().getNewSupplierType() == null
						|| StringUtils.isBlank(getManagedBean()
								.getNewSupplierType())) {
					getManagedBean().setNewSupplierType(currSuppType);
				}
				smAppl.setSupplierType(getManagedBean().getNewSupplierType());
			}
			// else if (applicationType.equalsIgnoreCase("B")) {}
			else if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
					|| applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
					|| applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
					|| applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)) {
				subStartTime = System.currentTimeMillis();
				cannotProceedAlertMsg = SmApplicationUtil
						.validateBeforeProceed(currSupp.getSupplierId(),
								applicationType);
				SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
						"SmApplicationBackingBean.proceedToApplication", "validateBeforeProceed-10", 
						subStartTime); //20211005@fadhli - #31754
			}
			if (cannotProceedAlertMsg) {
				return SmApplicationUtil
						.setExceptionNavigationForAppl(applicationType);
			} else {
				log.info("****** Before Clone supp id *****"
						+ currSupp.getSupplierId());
				subStartTime = System.currentTimeMillis();
				resDto = cloneSmApplicationDraft(currSupp.getSupplierId(),
						applicationType, smAppl.getSupplierType());
				SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
						"SmApplicationBackingBean.proceedToApplication", "cloneSmApplicationDraft-11", 
						subStartTime); //20211005@fadhli - #31754
				if (resDto.isErrorCaued()) {
					return SmApplicationUtil
							.setExceptionNavigationForAppl(applicationType);

				}
				smApplSupplier = resDto.getSmSupplier();
				log.info(" *** After clone Supplier Id ******* "
						+ smApplSupplier.getSupplierId());
				log.info("**** After clone appl id *** "
						+ resDto.getSmAppl().getApplId() + " -- "
						+ resDto.getSmAppl().getApplNo());
				if (resDto.isErrorCaued()) {
					return SmApplicationUtil
							.setExceptionNavigationForAppl(applicationType);
				}
				smAppl = resDto.getSmAppl();
				smAppl.setResubmit(false);
				if (smAppl != null
						&& StringUtils.isBlank(smAppl.getSupportingDocMode())) {
					//28771
					smAppl.setSupportingDocMode(StringUtils.EMPTY);
				}

				log.info("****** After retrieve the appl is ********"
						+ smAppl.getApplId() + " *** Num ***"
						+ smAppl.getApplNo() + "*** Bank Size *** "
						+ smAppl.getSmSupplierBanks().size());
			}
		} else {
			log.info("******* This is PO or Approver from BPM ************");
			smApplSupplier = supp;
			smAppl = appl;
		}
		getManagedBean().setSmSupplier(smApplSupplier);
		if (applicationType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			getManagedBean()
					.setDisableWithFedralTransaction(
							SmApplicationUtil
									.isTransactWithBasicAccountApprovedSoftCert(getManagedBean()
											.getSmSupplier().getSmAppl()));
		}
		if (!applicationType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				&& !applicationType
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
				&& !applicationType
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			subStartTime = System.currentTimeMillis();
			SmApplicationUtil.checkEquityOwnerUpdateAllowance(getManagedBean());
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "checkEquityOwnerUpdateAllowance-12", 
					subStartTime); //20211005@fadhli - #31754
		}
		subStartTime = System.currentTimeMillis();
		retrieveBusinessTypeList();
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "retrieveBusinessTypeList-13", 
				subStartTime); //20211005@fadhli - #31754
		assignSupplierBusinessType(getManagedBean().getSmSupplier());
		//#15629 && 37896 - START	
		String suppType = getManagedBean().getSmAppl().getSupplierType();
		if((suppType == null || "".equalsIgnoreCase(suppType)) && appl!= null){
			suppType = appl.getSupplierType();
		}
		subStartTime = System.currentTimeMillis();
		getManagedBean().setReasonDescInitList(
				SmApplicationUtil.retrieveMofRejectReason(suppType, getManagedBean()
						.getSmSupplier().getBusinessType(), applicationType));
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "retrieveMofRejectReason-14", 
				subStartTime); //20211005@fadhli - #31754
		//#15629 && 37896 - END	
		getManagedBean().setReasonDescList(
				getManagedBean().getReasonDescInitList());
		subStartTime = System.currentTimeMillis();
		getManagedBean().setRejectionOtherReasonId(
				SmApplicationUtil
						.retrieveOtherRejectReasonIdByParamCodeAndType(
								ReasonCategory.SUPPLIER_REJECTION_REASON
										.value(),
								SupplierManagementConst.SM_REASON_CODE_OTHERS));
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "retrieveOtherRejectReasonIdByParamCodeAndType-15", 
				subStartTime); //20211005@fadhli - #31754
		
		//#15629 && 37896 - START		
		if(getManagedBean().isApproverUser()){
			if(!EntityHelper.isCollectionEmpty(appl.getSmApplRejectReasons())) {
				
				getManagedBean().setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
				List<Long> reasonIdList = new ArrayList<Long>();
				for(SmApplRejectReason smApplRejectReason : appl.getSmApplRejectReasons()){
						for(PmReasonDesc pmReasonDesc : getManagedBean().getReasonDescList()){
							if(smApplRejectReason.getPmReason().getReasonId().equals(pmReasonDesc.getPmReason().getReasonId())){
								getManagedBean().getSelectedReasonDescs().add(pmReasonDesc);
								reasonIdList.add(pmReasonDesc.getPmReason().getReasonId());
							}
						}			
				}
				getManagedBean().setReasonId(
						reasonIdList.toArray(new Long[reasonIdList.size()]));
				verifySelectedRejectReasons();
			}
		}
		
		if(getManagedBean().isPoUser()){
		String suppTypePo = getManagedBean().getSmAppl().getSupplierType();
		if((suppTypePo == null || "".equalsIgnoreCase(suppTypePo)) && appl != null){
			suppTypePo = appl.getSupplierType();
		}
		subStartTime = System.currentTimeMillis();
		getManagedBean().setRecommendReasonDescInitList(
				SmApplicationUtil.retrieveMofRejectReason(suppTypePo, getManagedBean()
						.getSmSupplier().getBusinessType(), applicationType));
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "retrieveMofRejectReason-16", 
				subStartTime); //20211005@fadhli - #31754
		getManagedBean().setRecommendReasonDescList(
				getManagedBean().getRecommendReasonDescInitList());
		}
		//#15629 && 37896 - END

		if (getManagedBean().isApproverUser() || getManagedBean().isPoUser()) {
			if (smAppl.getSmCompanyBasic() == null) {
				smAppl.setSmCompanyBasic(new SmCompanyBasic());
			}
		}

		if (smAppl.getSmCompanyBasic() != null) {
			if (smAppl.getSmCompanyBasic().getSmCompanyAddresses() != null
					&& !smAppl.getSmCompanyBasic().getSmCompanyAddresses()
							.isEmpty()) {
				SmCompanyAddress comAdd = smAppl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0);
				SmAddress addr = comAdd.getSmAddress();
				SmApplicationUtil.setPmStateDropdownValuesForBasicInfo(this
						.getManagedBean(), addr.getPmCountry().getCountryId());
				this.getManagedBean().setBasicCompanyDistricts(
						SmCommonUtil.getDistrictList(addr.getPmState()
								.getStateId()));
				//13320-Start
				if(addr.getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
					this.getManagedBean().setBasicCompanyDivisions(
							SmCommonUtil.getDivisionList(addr.getPmState()
									.getStateId()));
				}//13320-End
				getManagedBean().setPrevBasicStateId(
						addr.getPmState().getStateId());
				this.getManagedBean().setBasicCompanyCities(
						SmCommonUtil.getCityList(addr.getPmDistrict()
								.getDistrictId()));
			} else {
				SmCompanyAddress comAdd = new SmCompanyAddress();
				SmAddress addr = SmApplicationUtil
						.setDefaultAddr(getManagedBean().getDefPmCountry());
				comAdd.setSmAddress(addr);
				List<SmCompanyAddress> addrs = new ArrayList<SmCompanyAddress>();
				addrs.add(comAdd);
				smAppl.getSmCompanyBasic().setSmCompanyAddresses(addrs);
			}
		} else {
			log.info("******** My Company Basic is null ********");
		}

		SmCommonUtil.getInstance().setUserDetails(smAppl);
		//28771 - start 
		getManagedBean().setSmAppl(smAppl);
		if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) 
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW) 
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)){
			subStartTime = System.currentTimeMillis();
			SmWorkflowStatus sts = SmCommonUtil.getCurrentActiveWokFlowStatus(getManagedBean().getSmAppl().getApplId());
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "getCurrentActiveWokFlowStatus-17", 
					subStartTime); //20211005@fadhli - #31754
			if (sts != null && sts.getPmStatus() != null){
				if (sts.getPmStatus().getStatusId().compareTo(SupplierManagementConst.PM_STATUS_PENDING_APPL_DECLARATION) == 0) {
					if(getManagedBean().getSmAppl().getSupportingDocMode() == null){
						smAppl.setSupportingDocMode(StringUtils.EMPTY);
						
					}else{
					smAppl.setSupportingDocMode(getManagedBean().getSmAppl().getSupportingDocMode());
					}
				}
				
			}else{
				smAppl.setSupportingDocMode(StringUtils.EMPTY);
				}
				
			}
		//28771 - end
		if (fromBpm) {
			SmApplicationUtil.getInstance().setApplAddressTransientValue(
					getManagedBean().getSmAppl(), false);
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
		
			//Bug #25979 - to hide message/note for Company that has Overlapping Category
			if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL))
			{
				log.info("#25979 From BPM - Cancel");
				getManagedBean().setOverLappedCategoryExists(false);
			}
			
			if (!(getManagedBean().isApproverUser() || getManagedBean()
					.isPoUser())) {
				boolean updateNeeded = SmApplicationUtil
						.resetDeactivatedPersonnel(getManagedBean());
				if (updateNeeded)
					saveOrUpdateAppl();
			}
		}

		if (getManagedBean().getSmAppl().getSmStaffTotals().isEmpty()) {
			log.info("*** Empty list of staff *****" + smAppl.getSupplierType());
			getManagedBean().getSmAppl().setSmStaffTotals(
					SmApplicationUtil.getStaffList(smAppl));
		}

		getManagedBean().setTabActiveIndex(0);
		if (!fromBpm) {
			setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_DECLARATION);

			// if (getManagedBean().getSmAppl().getSupplierType()
			// .equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			// getManagedBean().getSmAppl().getSmCompanyBasic()
			// .setWithFederal(true);
			// }

			// SmApplicationUtil.getInstance().setApplAddressTransientValue(
			// getManagedBean().getSmAppl(), false);

		} else {
			if (!StringUtils.isEmpty(getManagedBean().getCurrentStatus())) {
				log.info("*** Current Status Value Retrieved from BPM (set in common appl backing bean)****** "
						+ getManagedBean().getCurrentStatus());
			} else {
				subStartTime = System.currentTimeMillis();
				SmWorkflowStatus sts = SmCommonUtil
						.getCurrentActiveWokFlowStatus(smAppl.getApplId());
				SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
						"SmApplicationBackingBean.proceedToApplication", "getCurrentActiveWokFlowStatus-18", 
						subStartTime); //20211005@fadhli - #31754
				if (sts != null && sts.getPmStatus() != null
						&& sts.getPmStatus().getStatusId() != null) {
					log.info("** PM Status retrived from work flow *******");
					setPmStatusDesc(sts.getPmStatus().getStatusId().toString());
					log.info("*** Status ******* "
							+ getManagedBean().getCurrentStatus());
				} else {
					log.info("** PM Status retrived from SM appl only *******");
					setPmStatusDesc(smAppl.getPmStatus().getStatusId()
							.toString());
					log.info("*** SM appl  Status ******* "
							+ getManagedBean().getCurrentStatus());
				}
			}
		}
		
		//#34624 & #35204 start
		log.info("Application Type: " + applicationType);
		log.info("Application Category Type: " + getManagedBean().getSmAppl().getApplCategoryType());
		if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW) 
				&& getManagedBean().getSmAppl().getApplCategoryType().equals("S")){
			getManagedBean().getSmAppl().setApplCategoryType("N");
		}
		log.info("Application Category Type: " + getManagedBean().getSmAppl().getApplCategoryType());
		//#34624 & #35204 end
		
		// 26671 - Site Visit Grouping Enhancement -28775 - START
		getManagedBean().setSpecialCategoryRechecked(
				applicationType.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW) 
				&& SmApplicationUtil.hasSpecialCategory(getManagedBean().getSmAppl()));
		// 26671 - Site Visit Grouping Enhancement -28775 - END
		//32008 to initialize set PO & Approver cannot edit field key in by supplier
		getManagedBean().setUpsCannotEdit(false);
		if (fromBpm
				&& (getManagedBean().isPoUser() || getManagedBean()
						.isApproverUser())) {
			//32008 to set PO & Approver cannot edit field key in by supplier
			getManagedBean().setUpsCannotEdit(true);
			setRevUrl();

			// set whether to show or not approve and recommend button because
			// of site visit
			getManagedBean().setShowPoApprovedOrRecommendButton(
					showPoApprovedOrRecommendButton());

			getManagedBean().setMaxReviewCount(
					assignMaxReviewCount(applicationType, getManagedBean()
							.getSmAppl().getApplCategoryType(),
							getManagedBean().getSmAppl().getSupplierType(),
							getManagedBean().getBusinessTypeVal()));
			//31043 START
			initNonMandatoryAndMandatoryDocs(); 
			SmApplicationUtil.assignActiveAndApprovedCategoryList(getManagedBean()); 
			//31043 END
			if (getManagedBean().getSmAppl().getSmApplSectionReviews() == null) {
				smAppl.setSmApplSectionReviews(new ArrayList<SmApplSectionReview>());
			}

			if (getManagedBean().isPoUser()) {
				setDefaultPoReviewSection();
				/*setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_VERIFICATION);*/ //#32617
			}

			if (getManagedBean().isApproverUser()) {
				setDefaultApproverReviewSection();

				// set category as verified (copy from is_approved_by_po)
				setVerifiedSupplierCategory();

				// set supporting document as verified (copy from is_verified)
				setVerifiedSupportDoc();
			}

			if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				subStartTime = System.currentTimeMillis();
				getManagedBean().setSupplierRemarks(
						SmApplicationUtil.getSmRemarksByDocTypeAndId(
								applicationType, getManagedBean().getSmAppl()
										.getApplId()));
				SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
						"SmApplicationBackingBean.proceedToApplication", "getSmRemarksByDocTypeAndId-18", 
						subStartTime); //20211005@fadhli - #31754
			}

			// currently is display for ApplType C/DPA
			subStartTime = System.currentTimeMillis();
			getManagedBean().setBasicInfo(
					SmApplicationUtil.getSmSupplierBasicInfoVO(getManagedBean()
							.getBasicInfo(), getManagedBean().getSmSupplier(),
							getManagedBean().getSmAppl()));
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "getSmSupplierBasicInfoVO-19", 
					subStartTime); //20211005@fadhli - #31754
		}

		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			getManagedBean().getSmAppl().setApplCategoryType("N");
		} else if (StringUtils.isEmpty(getManagedBean().getSmAppl()
				.getApplCategoryType())) {
			getManagedBean().getSmAppl().setApplCategoryType("N");
		}
		getManagedBean().setCurrentPageType(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
		if (SmCommonUtil.isCollectionEmpty(smAppl.getSmFacilities())
				&& !smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
			SmFacility fac = new SmFacility();
			if (smAppl.getApplType().equalsIgnoreCase(
					ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				fac.setFacilityType("H");
			}
			if (fac.getSmAddress() == null
					|| StringUtils.isBlank(fac.getSmAddress().getAddress1())) {
				fac.setSmAddress(new SmAddress());
				subStartTime = System.currentTimeMillis();
				SmApplicationUtil.getInstance().cloneAddress(
						fac.getSmAddress(),
						getManagedBean().getSmAppl().getSmCompanyBasic()
								.getSmCompanyAddresses().get(0).getSmAddress());				
			}
			fac.setFacilityName(getManagedBean().getSmAppl()
					.getSmCompanyBasic().getCompanyName());
			fac.setSmAppl(smAppl);
			fac.setPurchasePrice(new BigDecimal(0.00));
			fac.setRentalRate(new BigDecimal(0.00));
			fac.setRevNo(1);
			smAppl.getSmFacilities().add(fac);
			this.getManagedBean().setBasicCompanyFacility(fac);
			subStartTime = System.currentTimeMillis();
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(), fac.getSmAddress(),
					SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL);
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "assignStateDistAndCityForSelVal-21", 
					subStartTime); //20211005@fadhli - #31754
		} else {
			subStartTime = System.currentTimeMillis();
			// #34303 - start
			setValueForFacilityHeadquarters();
			// #34303 - end
			setApplicationFailities(smAppl.getSmFacilities());
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "assignStateDistAndCityForSelVal-22", 
					subStartTime); //20211005@fadhli - #31754
		}
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			SmApplicationUtil.setCancellationSmAttachment(getManagedBean());
			// set basic info for cancel mof
		}

		subStartTime = System.currentTimeMillis();
		// set previous approved personnels
		getManagedBean()
				.setPrevApprovedPersonnels(
						SmApplicationUtil
								.getPrevApprovedPersonnelDirectorOwner(smAppl));

		// set application header info
		getManagedBean().setApplStatusInfo(
				SmApplicationUtil.getSmApplStatusInfoVO(smAppl));

		// set whether allowed to edit company name
		getManagedBean().setDisabledCompanyName(
				SmApplicationUtil.isDisableCompanyNameInAppl(getManagedBean()
						.getSmSupplier(), getManagedBean().getSmAppl()));

		// set whether allowed to edit business type
		getManagedBean().setDisabledBusinessType(
				SmApplicationUtil.isDisableBusinessTypeInAppl(getManagedBean()
						.getSmSupplier(), getManagedBean().getSmAppl()));

		// set whether allowed to change business type from office to
		// business
		setBusinessAndHadApprovedSpecialCatcode();
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "setBusinessAndHadApprovedSpecialCatcode-23", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// set travelling software select items
		getManagedBean().setTravellingSoftwareItems(
				SmCommonUtil.getInstance().getMachineItemsByCatTypeCode(
						ParameterConst.CATEGORY_TYPE_TRAVEL));
		SmApplicationUtil.initializeTravellingSoftwareTicked(getManagedBean());

		// set registration status list exclude current registration status only
		// if registration list size more than one
		getManagedBean().setRegStatusDescList(
				SmApplicationUtil.getInstance().retrieveRegistrationStatusList(
						getManagedBean().getSmAppl().getSupplierType(),
						applicationType));
		getManagedBean().setRecommendRegStatusDescList(
				SmApplicationUtil.getInstance().retrieveRegistrationStatusList(
						getManagedBean().getSmAppl().getSupplierType(),
						applicationType));
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "setBusinessAndHadApprovedSpecialCatcode-23", 
				subStartTime); //20211005@fadhli - #31754
		if (!getManagedBean().getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				&& getManagedBean().getSmAppl().getSmSupplier().getSmAppl()
						.getPmRegStatus() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean()
						.getRegStatusDescList())
				&& getManagedBean().getRegStatusDescList().size() > 1
				&& getManagedBean().getSmAppl().getSmSupplier().getSmAppl()
						.getPmRegStatus().getPmStatus() != null) {
			
			subStartTime = System.currentTimeMillis();
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "getCurrentSupplier-24", 
					subStartTime); //20211005@fadhli - #31754
			PmStatusDesc regStatusToExclude = (PmStatusDesc) EntityHelper
					.getEntityBySinglePropertyFromList(getManagedBean()
							.getSmAppl().getSmSupplier().getSmAppl()
							.getPmRegStatus().getPmStatus().getStatusId(),
							getManagedBean().getRegStatusDescList(), "pmStatus");
			// 27877 - start - if it is pending site review after approval, registration status can follow what was recommended
			BPMTaskListBackingBean bpmBean = new BPMTaskListBackingBean();
			String pendingSiteVisitReviewAfterApprovedStatus = bpmBean.applicationStatus((int) SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED);
			if (getManagedBean().getCurrentStatus() != null && !getManagedBean().getCurrentStatus().equals(pendingSiteVisitReviewAfterApprovedStatus)) {
				getManagedBean().getRegStatusDescList().remove(regStatusToExclude);
			}
			// 27877 - end
		}

		subStartTime = System.currentTimeMillis();
		getManagedBean()
				.setSupplierParentCatList(
						SmApplicationUtil
								.retrieveParentCategoryListForExperiencePage(getManagedBean()));
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "retrieveParentCategoryListForExperiencePage-25", 
				subStartTime); //20211005@fadhli - #31754
		if (getManagedBean().getSmAppl().getSmAssociationBodies() != null) {
			SmApplicationUtil.assignNonPersonnelProfBodyList(getManagedBean());
		}
		List<SmMofAccount> accounts = getManagedBean().getSmSupplier()
				.getSmMofAccounts();
		if (!EntityHelper.isCollectionEmpty(accounts)) {
			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						getManagedBean().getSmSupplier().getSmAppl()
								.getSupplierType())
						&& account.getRecordStatusFlg().compareTo(
								DbValueConst.RECORD_STATUS_ACTIVE) == 0) {
					getManagedBean().setMofNumber(account.getMofNo());
				}
			}
		}
		
		// 32064 - Start
		initBusinessPremise();
		// 32064 - End

		// init downgradeMofToBasic
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		SmMofAccount downgradeMofAcc = SmServiceLookup.getMofAccountSO()
				.retrieveDowngradeMofAcc(reqDTO).getMofAccount();
		getManagedBean().setDowngradeMofAcc(downgradeMofAcc);

		// init mof account
		subStartTime = System.currentTimeMillis();
		getManagedBean().setMofAccount(
				SmCommonUtil.getMofAccountBySuppIdAndSuppType(getManagedBean()
						.getSmSupplier().getSupplierId(), getManagedBean()
						.getSmAppl().getSupplierType()));
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "getMofAccountBySuppIdAndSuppType-26", 
				subStartTime); //20211005@fadhli - #31754

		// init GST information
		subStartTime = System.currentTimeMillis();
		initGst();
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "initGst-27", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// init to show change value compare to previous application
		SmFieldChangeUtil.initLatestAppl(getManagedBean());
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "initLatestAppl-28A", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// set softcert reject reason
		setPersonnelSoftcertRejectReason();
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "setPersonnelSoftcertRejectReason-28B", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// populate and set default local authority
		populateLocalAuthority(getManagedBean().getSmSupplier(),
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress());
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "populateLocalAuthority-28C", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// check whether its site visit after approval
		getManagedBean().setSiteVisitAfterApproval(
				SmCommonUtil.isSiteVisitAfterApproved(getManagedBean()
						.getSmAppl()));
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "isSiteVisitAfterApproved-28D", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// init current registration status description
		PmStatusDesc currentStatusDesc = SmApplicationUtil
				.getCurrRegStatusDesc(getManagedBean().getSmAppl());
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "isSiteVisitAfterApproved-28E", 
				subStartTime); //20211005@fadhli - #31754
		if (currentStatusDesc != null
				&& !getManagedBean().getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			getManagedBean().setApprovedStatusId(
					currentStatusDesc.getPmStatus().getStatusId());
			getManagedBean().setPoRecommendStatusId(
					getManagedBean().getApprovedStatusId());
			getManagedBean()
					.setCurrRegStatus(currentStatusDesc.getStatusName());
		}

		// set business type parameter type
		getManagedBean()
				.setBusinessTypeParamCode(
						getManagedBean().getSmAppl().getSupplierType()
								.equals("K") ? ParameterTypeConst.SUPPLIER_BUSINESS_TYPE
								: ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT);

		// set supporting document mode if empty
		//28771 - start
		if (getManagedBean().isPoUser()|| getManagedBean().isApproverUser()){
		if (StringUtils.isBlank(getManagedBean().getSmAppl()
				.getSupportingDocMode())) {
			getManagedBean().getSmAppl().setSupportingDocMode(
					SupplierManagementConst.SUPPORTING_DOC_ONLINE);
		}}
		//28771 - end
		// set latest mofAccount
		if (getManagedBean().getSmAppl().getSmSupplier() != null) {
			getManagedBean().setExpiryDate(StringUtils.EMPTY);
			subStartTime = System.currentTimeMillis();
			SmMofAccount acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(
					getManagedBean().getSmAppl().getSmSupplier()
							.getSupplierId(), getManagedBean().getSmAppl()
							.getSupplierType());
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "getMofAccountBySuppIdAndSuppType-28F", 
					subStartTime); //20211005@fadhli - #31754
			if (acc != null) {
				getManagedBean().setExpiryDate(
						SmCommonUtil.getDisplayDateFormat(acc.getExpDate()));
			}
		}

		subStartTime = System.currentTimeMillis();
		// sort staff info
		SmApplicationUtil.sortStaffInfo(getManagedBean().getSmAppl()
				.getSmStaffTotals());
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "sortStaffInfo-28G", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// set previous application to compare based on role
		SmSideMenuIconUtil.setPrevApplByRole(getManagedBean());
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "setPrevApplByRole-28H", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// set section editor VO
		SmSideMenuIconUtil.setAllSectionEditorVO(getManagedBean(), true,
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "setAllSectionEditorVO-29", 
				subStartTime); //20211005@fadhli - #31754

		subStartTime = System.currentTimeMillis();
		// show or hide travel software panel
		SmApplicationUtil.showOrHideTravelSoftwarePanel(getManagedBean());

		// show or hide equipment panel
		SmApplicationUtil.getInstance().removeEquipmentIfNotApplicable(
				getManagedBean());

		// show or hide facility panel
		SmApplicationUtil.handleShowOrHideFacilityPanels(getManagedBean());

		// PO inquiry initialization
		initMissingDocs(false, null);
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "post-Disciplinary-Action-32", 
				subStartTime); //20211005@fadhli - #31754

		// show po remarks and rejection reason for resubmit application
		if (getManagedBean().getSmAppl().isResubmit()
				&& getManagedBean().getSmAppl().getOriginalAppl() != null) {
			subStartTime = System.currentTimeMillis();
			applSectionRevExist();

			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setSmAppl(getManagedBean().getSmAppl());
			SmAppl latestRejectedAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveLatestRejectedApplication(mofReqDTO).getSmAppl();
			if (latestRejectedAppl != null) {
				getManagedBean()
						.setRejectReasonsMsg(
								SmCommonUtil
										.getHtmlApplicationRejectReasons(latestRejectedAppl
												.getSmApplRejectReasons()));
			}
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "poRemarksRejectionReason-33", 
					subStartTime); //20211005@fadhli - #31754
		}

		// set supplier disciplinary action status for PO/Approver review page
		if (getManagedBean().isPoUser() || getManagedBean().isApproverUser()) {
			String disciplinaryActionStatus = SmCommonUtil
					.getDisciplinaryActionStatus(getManagedBean()
							.getSmSupplier());
			if (!StringUtils.isBlank(disciplinaryActionStatus)) {
				getManagedBean()
						.setDiscplinaryActionMsg(
								SmCommonUtil
										.getMessage(
												"lbl.sm.supplier.disciplinary.action.msg",
												null));
			}
		}

		// set payment details section only for supplier and application type is
		// NEW or RENEW
		if (getManagedBean().isSupplierUser()
				&& (getManagedBean().getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF) || getManagedBean()
						.getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW))) {
			SmPaymentDetailsVO paymentVO = new SmPaymentDetailsVO();
			paymentVO.setDetails(getParameterDesc(
					ParameterTypeConst.SUPP_PAYMENT_ITEM,
					SupplierManagementConst.MOF_PAYMENT));
			paymentVO.setPrice(SmRuleHelper.getInstance().getRegistrationFee());
			paymentVO.setTotalAmount(paymentVO.getPrice());
			getManagedBean().setPaymentDetailsVOList(
					new ArrayList<SmPaymentDetailsVO>());
			getManagedBean().getPaymentDetailsVOList().add(paymentVO);

			paymentVO = new SmPaymentDetailsVO();
			paymentVO.setDetails(getParameterDesc(
					ParameterTypeConst.SUPP_PAYMENT_ITEM,
					SupplierManagementConst.PROCESSING_PAYMENT));
			paymentVO.setPrice(SmRuleHelper.getInstance().getProcessingFee());
			paymentVO.setTotalAmount(paymentVO.getPrice());
			getManagedBean().getPaymentDetailsVOList().add(paymentVO);

			getManagedBean().setTotalPaymentAmount(BigDecimal.ZERO);
			getManagedBean().setGstAmount(SmCommonUtil.getGstCurrentRate());
			getManagedBean().setGstPercentageLabels(
					SmCommonUtil.getMessage("lbl.sm.gst.percent",
							new String[] { getManagedBean().getGstAmount()
									.toString() }));

			for (SmPaymentDetailsVO paymentDetailsVO : getManagedBean()
					.getPaymentDetailsVOList()) {
				getManagedBean().setTotalPaymentAmount(
						getManagedBean().getTotalPaymentAmount().add(
								paymentDetailsVO.getPrice()));
			}
			getManagedBean().setTotalPaymentAmount(
					getManagedBean().getTotalPaymentAmount().setScale(2,
							BigDecimal.ROUND_UP));
			getManagedBean().setApplDeclarationAlertMsg(
					SmCommonUtil.getMessage(
							"lbl.sm.payment.declaration.appl.alert.msg",
							new String[] { getManagedBean().getApplStatusInfo()
									.getDaysLeft() }));
		}
		
		//16052019 - SSM INTEGRATION CR - Fahmi
		if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) 
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW) 
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)
				|| applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)){	
			long ssmIntegrationStartTime = System.currentTimeMillis();
			if(!fromBpm && getManagedBean().isSsmIntegrationActive() && this.getManagedBean().getSmSupplier()!= null 
					//&& !this.getManagedBean().getSmAppl().isApplValidWithSsm() disable cz problem when upgarde to MOF
					&& businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupplier().getRegNo(),this.getManagedBean().getSmSupplier().getBusinessType())){
				//SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
				if(businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupplier().getRegNo(),this.getManagedBean().getSmSupplier().getBusinessType())){
					//#27165
					boolean dontGetSsmData = this.getManagedBean().isDisAllowOwnerEdit();
					//SSM CHANGES #27214
					SsmReqDTO ssmReqDTO = new SsmReqDTO();
					ssmReqDTO.setRegNo(this.getManagedBean().getSmSupplier().getRegNo());
					ssmReqDTO.setBusinessType(this.getManagedBean().getSmSupplier().getBusinessType());
					//29582
					ssmReqDTO.setSmSupp(this.getManagedBean().getSmSupplier());
					if(SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())){
						ssmReqDTO.setBasicAcc(true);
					}else{
						ssmReqDTO.setBasicAcc(false);
					}
					// #30660
					ssmReqDTO.setDisAllowOwnerEdit(this.getManagedBean().isDisAllowOwnerEdit());
					subStartTime = System.currentTimeMillis();
					SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
					SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
							"SmApplicationBackingBean.proceedToApplication", "validateWithSSM-34A", 
							subStartTime); //20211005@fadhli - #31754
					this.getManagedBean().setCompanyValidWithSSM(false);
					this.getManagedBean().setValidateWithSSM(true);
					if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
						this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
						if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
							//27382 - start
							if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("L")){
								throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
							}//27382 - Terminated 
							if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("T")){
								throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
							}//27382 - Dissolved Conversion to LLP
							if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("B")){
								if(businessTypeIsROC(this.getManagedBean().getSmSupplier().getBusinessType())){
									throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null) });
								}
								if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
									throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null) });
								}
							} else { 
								throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});}
							}
						//27382 - end
						this.getManagedBean().setCompanyValidWithSSM(true);
						SmCommonUtil.ssmIdentificationTypeError(ssmResDTO); //#29582
						// #24819
						this.getManagedBean().getSmSupplier().setSsmNewRegNo(ssmResDTO.getSsmNewRegNo());
						//SET COMPANY NAME
						//if rob no need to copy capital.
						//long tempAnnualRevenue = this.getManagedBean().getSmAppl().getSmCompanyBasic().getAnnualRevenue();
						if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
							String[] copyProp = {"companyName","robExpiryDate","businessDesc","annualRevenue","revenueSsmNull","robExpirySsmNull","authorizedCapitalSsmNull","companySsmStatus","annualRevenueSSM"};
							EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(),this.getManagedBean().getSmAppl().getSmCompanyBasic(),copyProp);							
						}else{
							// 28466 - add paid up capital, 28767 add registerAdressSSM
							// #30660 start 
							if (businessTypeIsROC(this.getManagedBean().getSmSupplier().getBusinessType()) && getManagedBean().isDisAllowOwnerEdit()) {
								String[] copyProp = { "companyName", "robExpiryDate", "annualRevenue", "businessDesc", "revenueSsmNull", "robExpirySsmNull",
										"authorizedCapitalSsmNull", "companySsmStatus", "annualRevenueSSM", "paidUpCapitalSSM", "registerAdressSSM" };
								EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(), this.getManagedBean().getSmAppl().getSmCompanyBasic(), copyProp);
							} else { 
								String[] copyProp = { "companyName", "robExpiryDate", "authorizedCapital", "nonBumiEquity", "annualRevenue", "bumiEquity",
										"localCompanyEquity", "foreignCompanyEquity", "foreignerEquity", "govtEquity", "govtUnitrustEquity", "businessDesc",
										"revenueSsmNull", "robExpirySsmNull", "authorizedCapitalSsmNull", "companySsmStatus", "annualRevenueSSM", "paidUpCapitalSSM",
										"paidUpCapital", "registerAdressSSM" };
								EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(), this.getManagedBean().getSmAppl().getSmCompanyBasic(), copyProp);
							}
							// #30660 end
							
							//START 28767	- ni set untuk save ke dalam DB		- perlu buat dalam ni sebab data tu firsttime je dr SSM			
							if(ssmResDTO.getSmApplIndebtedness() != null){
								ssmResDTO.getSmApplIndebtedness().setSmAppl(this.getManagedBean().getSmAppl());
								this.getManagedBean().getSmAppl().setSmApplIndebtedness(ssmResDTO.getSmApplIndebtedness());
							}
							
							if(ssmResDTO.getSmPersonnelSecretaries() != null){
								this.getManagedBean().getSmAppl().setSmPersonnelSecretaries(new ArrayList<SmPersonnelSecretaries>());
								for(SmPersonnelSecretaries personnelSecretaries: ssmResDTO.getSmPersonnelSecretaries()){
									personnelSecretaries.setSmAppl(this.getManagedBean().getSmAppl());
									this.getManagedBean().getSmAppl().getSmPersonnelSecretaries().add(personnelSecretaries);
								}
							}
							
							for(SmPersonnelSecretaries personnelSec : this.getManagedBean().getSmAppl().getSmPersonnelSecretaries()){
								if(personnelSec.getSecDesignation() != null){
									String designation = populateDesignationSSM(personnelSec.getSecDesignation());
									personnelSec.setSecDesignation(designation);
								}
							}
							
							if(ssmResDTO.getSmApplAuditors()!= null){
								this.getManagedBean().getSmAppl().setSmApplAuditors(new ArrayList<SmApplAuditors>());
								for(SmApplAuditors auditors: ssmResDTO.getSmApplAuditors()){
									auditors.setSmAppl(this.getManagedBean().getSmAppl());
									this.getManagedBean().getSmAppl().getSmApplAuditors().add(auditors);
								}
							}
							//END 28767 - end set untuk save dalam DB
						}
						//#27165
						if(dontGetSsmData){	
							String[] copyProp = {"companyName","robExpiryDate","businessDesc","annualRevenue","revenueSsmNull","robExpirySsmNull","authorizedCapitalSsmNull","companySsmStatus","annualRevenueSSM"};
							EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(),this.getManagedBean().getSmAppl().getSmCompanyBasic(),copyProp);														
						}
						/*if((ssmResDTO.getSmCompanyBasic().getAnnualRevenue() == null || ssmResDTO.getSmCompanyBasic().getAnnualRevenue()==0) && tempAnnualRevenue > 0){
							this.getManagedBean().getSmAppl().getSmCompanyBasic().setAnnualRevenue(tempAnnualRevenue);
						}*/
						//START #27256
						//27495
						//#34138 - start
						String[] copyPropAddress = {"address1","address2","address3","addressSsmNull","postcodeSsmNull","stateSsmNull","countrySsmNull","ssmData","postcode","pmState","pmCountry","stateId",
												"countryId"};
						//#34138 - end
						EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmAddress(),this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress(),copyPropAddress);
						//#27256
						SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
								"SmApplicationBackingBean.proceedToApplication", "copyEntityPropertiesOnly-34B", 
								subStartTime); //20211005@fadhli - #31754
						//this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPostcode(ssmResDTO.getSmAddress().getPostcode());						
						if(ssmResDTO.getSmAddress().getPmCountry() != null && ssmResDTO.getSmAddress().getPmState() != null){
							PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
							this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setCountryId(defaultCountry.getCountryId());

						}
						if(ssmResDTO.getSmAddress().getPmState() != null){
							this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setStateId(ssmResDTO.getSmAddress().getPmState().getStateId());
							this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setCityId(null);
							this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setDistrictId(null);
							this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmDistrict(null);
							this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmCity(null);
							if(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCountry() == null || this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCountry().getCountryId() == null){
								PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
								this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmCountry(defaultCountry);
							}
							this.getManagedBean().setBasicCompanyStates(SmCommonUtil.getStateList(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCountry().getCountryId()));
							this.getManagedBean().setBasicCompanyDistricts(SmCommonUtil.getDistrictList(ssmResDTO.getSmAddress().getPmState().getStateId()));
							
						}
						//END #27256
						this.getManagedBean().getSmAppl().setApplValidWithSsm(true);
						//start #29956
						if(ssmResDTO.getSmCompanyBasic()!=null){
							//set establish date
							this.getManagedBean().getSmAppl().getSmCompanyBasic().setEstablishDate(ssmResDTO.getSmCompanyBasic().getEstablishDate());
						}
						//end #29956
						//START #27239
						//POPUP USER NOT SSM OWNER #26734 //ANY CHANGES HERE PLEASE CHANGES IN ViewApplHistoryBackingBean.redirectToReapplyNewAppl & viewOnlineAccount.RegistrationBackingBean & SmApplicationBackingBean.proceedToApplication
						subStartTime = System.currentTimeMillis();
						//FacesScopes facesScope = new FacesScopes();
						SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
								"SmApplicationBackingBean.proceedToApplication", "facesScope-34C", 
								subStartTime); //20211005@fadhli - #31754
						//START 28454
						//if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) || applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW) || applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)){
							
						//START NEW CHANGES POPUP OWNER UPDATE ROLE
						List<SmPersonnel> personnelsRole = this.getManagedBean().getPersonnelsRole();
						//start #29956
						boolean displayPopupDowngradeSSMOwner = displayPopupDowngradeSSMOwner(getManagedBean().getSmAppl(),ssmResDTO); //#37598
						if (displayPopupDowngradeSSMOwner) {
							// end #29956
							getManagedBean().setSmSupplierSsm(this.getManagedBean().getSmSupplier());	
							subStartTime = System.currentTimeMillis();
							
							SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
									"SmApplicationBackingBean.proceedToApplication", "ownerCheckingWidgetVar.show-34D", 
									subStartTime); //20211005@fadhli - #31754
							return SmApplicationUtil.setStringNavigationForApplTnCPage(applicationType);
						}else{
							//set latest role to personnel
							for(SmPersonnel personnel :  personnelsRole){
								SmPersonnel personnelActive = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), this.getManagedBean().getSmAppl().getSmPersonnels(), "identificationNo");
								if(personnelActive != null){
									//copy latest data to supplier admin
									String[] copyEquityOwner = {"equityOwner","director","clerical","higherManagement","management","downgradeRoleSsm"};
									EntityHelper.copyEntityPropertiesOnly(personnel,personnelActive,copyEquityOwner);
								}
							}
						}
							
						//}
						//END 28454
						// #26734 END
						//END #27239
						//CHECK EQUITY OWNER EXIST IN SSM TO DISABLE CHECKBOX
						int i = 0;
						for(SmPersonnel personnel : this.getManagedBean().getSmAppl().getSmPersonnels()){
							//#27165
							if(dontGetSsmData){
								break;
							}
							validateRole(personnel);
							// SET DESINGATION SSM #27201
							if(personnel.getDesignation() != null){
								String designation = populateDesignationSSM(personnel.getDesignation());
								personnel.setDesignation(designation);
							}
							
							SmPersonnel personnelSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),ssmResDTO.getSmPersonnelAll(),"identificationNo");
							if(personnelSSM != null){								
								//if rob no need to copy shareamt.
								if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
									personnel.setSsmData(true);
									personnel.setShareAmtSsmNull(true);
									//SSM CHANGE 11012020
									//#27315, #35145
									EntityHelper.copyEntityPropertiesOnly(personnelSSM,personnel,getEquityOwnerField(true));
								}else{
									personnel.setSsmData(true);
									//#27315
									//27408 - Remove Pmcountry for ROC 
									//28767 - add birthdate & designation, #35145
									EntityHelper.copyEntityPropertiesOnly(personnelSSM,personnel,getEquityOwnerField(false));
								}
								validateRole(personnel);
								//personnel.setSmPersonnelAddresses(personnelSSM.getSmPersonnelAddresses());
								//personnel.setSmAppl(this.getManagedBean().getSmAppl());
								///check ssm address exist or not
								if(personnelSSM.getSmPersonnelAddresses()!= null && personnelSSM.getSmPersonnelAddresses().size() > 0){
									//check ssm address exist type = R
									SmPersonnelAddress personnelAddressSSM = (SmPersonnelAddress)EntityHelper.getEntityBySinglePropertyFromList(my.ep.constant.sm.SupplierManagementConst.ADDRESS_TYPE_RESIDENTIAL,personnelSSM.getSmPersonnelAddresses(),"addressType");
									if(personnelAddressSSM != null){
										if(personnel.getSmPersonnelAddresses()!= null && personnel.getSmPersonnelAddresses().size() > 0){
											for(SmPersonnelAddress personnelAddress : personnel.getSmPersonnelAddresses()){
												if(my.ep.constant.sm.SupplierManagementConst.ADDRESS_TYPE_RESIDENTIAL.equalsIgnoreCase(personnelAddress.getAddressType())){
													personnelAddress.setSmPersonnel(personnel);
													EntityHelper.copyEntityPropertiesOnly(personnelAddressSSM.getSmAddress(),personnelAddress.getSmAddress(),copyPropAddress);
													break;
												}
											}
										}
									}
									
								}
							}else{
								//27239
								setDeletePersonnel(personnel);
							}
							this.getManagedBean().getSmAppl().getSmPersonnels().set(i, personnel);
							i++;
						}
						//SET EQUITY COMPANY
						//if(this.getManagedBean().getSmAppl().getSmEquityCompanies()!=null){
						// #30660
						if(!businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType()) && ssmResDTO.getSmEquityCompanys()!=null && !dontGetSsmData){
							this.getManagedBean().setSmEquityCompanyTemp(ssmResDTO.getSmEquityCompanys());
							if(this.getManagedBean().getSmAppl().getSmEquityCompanies()==null){
								this.getManagedBean().getSmAppl().setSmEquityCompanies(new ArrayList<SmEquityCompany>());
							}
							//delete all old company share #27145
							Iterator<SmEquityCompany> iterequityCompany = this.getManagedBean().getSmAppl().getSmEquityCompanies().iterator();
							while(iterequityCompany.hasNext()){
								iterequityCompany.next();
								iterequityCompany.remove();
							}
							
							//TO Set equity compny from ssm only #27145
							for(SmEquityCompany equityCompany : ssmResDTO.getSmEquityCompanys()){
									equityCompany.setSmAppl(this.getManagedBean().getSmAppl());
									this.getManagedBean().getSmAppl().getSmEquityCompanies().add(equityCompany);
							}
						}
						//SET SMPERSONNEL
						if(ssmResDTO.getSmPersonnelAll()!=null){
							if(this.getManagedBean().getSmAppl().getSmPersonnels()==null){
								this.getManagedBean().getSmAppl().setSmPersonnels(new ArrayList<SmPersonnel>());
							}
							for(SmPersonnel personnel : ssmResDTO.getSmPersonnelAll()){
								//#27165
								if(dontGetSsmData){
									break;
								}
								SmPersonnel oldPersonnel = (SmPersonnel)EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), this.getManagedBean().getSmAppl().getSmPersonnels(), "identificationNo");
								if(oldPersonnel == null){
									personnel.setBrandNew(true);
									personnel.setSmAppl(this.getManagedBean().getSmAppl());
									personnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);	
									if(personnel.getSmPersonnelAdd()!= null){
										personnel.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
										personnel.getSmPersonnelAdd().setSmPersonnel(personnel);
										personnel.getSmPersonnelAddresses().add(personnel.getSmPersonnelAdd());
									}
									if(personnel.getPmCountry() == null || personnel.getPmCountry().getCountryId() == null){
										if(personnel.getPmCountry() == null || personnel.getPmCountry().getCountryId() == null){
											//#27408
											if(personnel.getIdentificationType() != null && ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(personnel.getIdentificationType().getParameterCode())){
												PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(ParameterConst.COUNTRY_UNKNOWN_CODE);
												personnel.setPmCountry(defaultCountry);
											}else{
												PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
												personnel.setPmCountry(defaultCountry);
											}
										}
									}
									
									this.getManagedBean().getSmAppl().getSmPersonnels().add(personnel);
								}else{
									validateRole(oldPersonnel);
									if(personnel.getPmCountry() == null || personnel.getPmCountry().getCountryId() == null){
										//#27408
										if(personnel.getPmCountry() == null || personnel.getPmCountry().getCountryId() == null){
											if(personnel.getIdentificationType() != null && ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(personnel.getIdentificationType().getParameterCode())){
												PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(ParameterConst.COUNTRY_UNKNOWN_CODE);
												oldPersonnel.setPmCountry(defaultCountry);
											}else{
												PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
												oldPersonnel.setPmCountry(defaultCountry);
											}
										}
									}
								}
							}
						}
						//SET EQUITY BRANCH
						if(ssmResDTO.getSmSupplierBranches() != null){
							if(this.getManagedBean().getSmAppl().getSmSupplierBranches()==null){
								this.getManagedBean().getSmAppl().setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
							}
							//#28900
							for (SmSupplierBranch branchSSM : ssmResDTO.getSmSupplierBranches()) {
								copyBranchFromSSM(branchSSM);
							}
						}
						//SET BUSIESS NATURE
						/*
						boolean existInOld=false;
						//compare old data with ssm, if ssm has new data, add data into list
						if(getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses().size() > 0){
							for(SmSupplierBusiness smSupplierBusiness:ssmResDTO.getSmSupplierBusinessList()){
								if(smSupplierBusiness.getPmBusinessNature()!=null){
									for(SmSupplierBusiness smSuppBussOld:getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses()){
										if(smSupplierBusiness.getPmBusinessNature().getLevelCode().equalsIgnoreCase(smSuppBussOld.getPmBusinessNature().getLevelCode())){
											existInOld=true;
										}
									}
								}
								if(!existInOld){
									smSupplierBusiness.setSmSupplier(getManagedBean().getSmSupplier());
									getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses().add(smSupplierBusiness);
								}
							}
						//if old data not exits,set all data from ssm into list
						}else if(ssmResDTO.getSmSupplierBusinessList().size() > 0){
							getManagedBean().getSmAppl().getSmSupplier().setSmSupplierBusinesses(ssmResDTO.getSmSupplierBusinessList());
							for(SmSupplierBusiness business :getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses()){
								getManagedBean().getSmAppl().getSmSupplier().setSmAppl(getManagedBean().getSmAppl());
								business.setSmSupplier(getManagedBean().getSmAppl().getSmSupplier());
							}
						}*/
						// maybe here to put method to check with ssm
						//SmCommonUtil.getInstance().validateWithSSM(regNo,businessType,"");
						// 28466 - to maintain paid up capital from SSM
						if(!businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType()) && !this.getManagedBean().isSsmIntegrationActive()){
							reCalculateCapitalPersonnel();
						}
						//Remove equity/director personnel not exist in ssm
						if(this.getManagedBean().getSmAppl().getSmPersonnels()!= null){
							Iterator<SmPersonnel> iterPersonnel = this.getManagedBean().getSmAppl().getSmPersonnels().iterator();
							while(iterPersonnel.hasNext()){
								SmPersonnel personnel = iterPersonnel.next();
								//START SSM CHANGES #27201 - SET DESIGNATION
								personnel = setDesignationSSM(personnel);
								//END SSM CHANGES #27201 - SET DESIGNATION
								if(personnel.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_DELETED){
									if (this.getManagedBean().getSmAppl().getSmSupplierBanks() != null && !this.getManagedBean().getSmAppl().getSmSupplierBanks().isEmpty()) {
										
										for (SmSupplierBank smSupplierBank : this.getManagedBean().getSmAppl().getSmSupplierBanks()) {
											if (smSupplierBank.getSmChequeSignatories() != null && !smSupplierBank.getSmChequeSignatories().isEmpty()) {
												Iterator<SmChequeSignatory> itersmChequeSignatory = smSupplierBank.getSmChequeSignatories().iterator();
												while (itersmChequeSignatory.hasNext()) {
													SmChequeSignatory smChequeSignatory = itersmChequeSignatory.next();
													if (smChequeSignatory.getSmPersonnel() != null && smChequeSignatory.getSmPersonnel().getPersonnelId() != null && smChequeSignatory.getSmPersonnel().getPersonnelId().compareTo(personnel.getPersonnelId()) == 0 ) {
														itersmChequeSignatory.remove();
													}
												}
											}
										}
									}
									
									if (this.getManagedBean().getSmAppl().getSmSupplierBranches() != null && !this.getManagedBean().getSmAppl().getSmSupplierBranches().isEmpty()) {
										Iterator<SmSupplierBranch> itersmSupplierBranch = this.getManagedBean().getSmAppl().getSmSupplierBranches().iterator();
										while (itersmSupplierBranch.hasNext()) {
											SmSupplierBranch smSupplierBranch = itersmSupplierBranch.next();
											if (smSupplierBranch.getSmPersonnel() != null && smSupplierBranch.getSmPersonnel().getPersonnelId().compareTo(personnel.getPersonnelId()) == 0) {
													smSupplierBranch.setSmPersonnel(null);
											}
										}
									}
									
									iterPersonnel.remove();
								}
							}
						}
					}else{
						this.getManagedBean().setCompanyValidWithSSM(false);
					}
					
					
				
					if(!this.getManagedBean().isCompanyValidWithSSM()){
	//					throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.contractor",null), getManagedBean().getSmSupp().getRegNo()});
						throw new EpException(MessageConst.ERROR_721070);
					}
				}
			//}else if(fromBpm && this.getManagedBean().getSmSupplier() != null && getManagedBean().isSsmIntegrationActive() && businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupplier().getRegNo(),this.getManagedBean().getSmSupplier().getBusinessType())){
			}else if(fromBpm && this.getManagedBean().getSmAppl().isApplValidWithSsm()&& businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupplier().getRegNo(),this.getManagedBean().getSmSupplier().getBusinessType())){
				//this will use, when click from tasklist n only to check data is valid with ssm or not
				/*SsmReqDTO ssmReqDTO = new SsmReqDTO();
				ssmReqDTO.setRegNo(this.getManagedBean().getSmSupplier().getRegNo());
				ssmReqDTO.setBusinessType(this.getManagedBean().getSmSupplier().getBusinessType());
				ssmReqDTO.setCheckOnly(true);
				SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);*/
				getManagedBean().setCompanyValidWithSSM(true);
				getManagedBean().setValidateWithSSM(true);
			}else{
				getManagedBean().setCompanyValidWithSSM(this.getManagedBean().getSmAppl().isApplValidWithSsm());
				getManagedBean().setValidateWithSSM(this.getManagedBean().getSmAppl().isApplValidWithSsm());
			}
			SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
					"SmApplicationBackingBean.proceedToApplication", "ssm-integration-34", 
					ssmIntegrationStartTime); //20211005@fadhli - #31754
		}
		//16052019 - SSM INTEGRATION CR - Fahmi END
		//START 28767
		BigDecimal sum = new BigDecimal(0);
		this.getManagedBean().getPersonnelDirectorManager().clear();
		this.getManagedBean().getPersonnelMembers().clear();
		for(SmPersonnel personnel:this.getManagedBean().getSmAppl().getSmPersonnels()){
			BigDecimal shareAmtSsm = personnel.getShareAmtSsm();
				if(personnel.getDesignation() != null){
					String designation = populateDesignationSSM(personnel.getDesignation());
					personnel.setDesignation(designation);
				}
				if(personnel.isDirector() || personnel.isManagement()){
				this.getManagedBean().getPersonnelDirectorManager().add(personnel);	
				}
				//28767
				//#35037 - start
				if(personnel.getSmPersonnelAddresses() != null){
					for (SmPersonnelAddress address : personnel.getSmPersonnelAddresses()){
						if (address.getAddressType() != null && address.getAddressType()
								.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)){
							personnel.setSmPersonnelAdd(address);
						}
					}
				}
				//#35037 - end
				if (personnel.isSsmData()!= true){
					this.getManagedBean().getPersonnelDirectorManager().remove(personnel);
				}
				//#35130
				if(personnel.isEquityOwnerSsm() && personnel.isSsmData()){
					if(shareAmtSsm != null && !(personnel.getShareAmtSsm() == null)){
						log.info("THIS IS THE VALUE OF THE TOTAL SUM ////////////////////// " + sum);
						log.info("THIS IS THE VALUE OF THE TOTAL SHARE ////////////////////// " + shareAmtSsm);
						sum =  sum.add(shareAmtSsm);
						getManagedBean().setTotalShare(sum);
						log.info("THIS IS THE VALUE OF THE TOTAL SUM ////////////////////// " + sum);
						
					}
					this.getManagedBean().getPersonnelMembers().add(personnel);
				}
		}
	//END 28767
		
		// 35311 - Start
		if (getManagedBean().getSmAppl().getPmStatus() != null
				&& getManagedBean().getSmAppl().getPmStatus().getStatusId() == SupplierManagementConst.PM_STATUS_PENDING_APPL_DECLARATION) {
			for (SmFacility facility : getManagedBean().getSmAppl().getSmFacilities()) {
				if (facility.getFacilityId() != null) {
					assignApplicationSupportingDocs();
					break;
				}
			}
		}
		// 35311 - end
		subStartTime = System.currentTimeMillis();
		// set Personnel List Count
		calcPersonnelListCount();
		updateCategoriesRecordStatus(); //#29847
		SLAUtils.getInstance().logSLASubInfo("SM", "VA", sessionId, "null",
				"SmApplicationBackingBean.proceedToApplication", "calcPersonnelListCount-updateCategoriesRecordStatus-36", 
				subStartTime); //20211005@fadhli - #31754
		return SmApplicationUtil.setNavigationForAppl(applicationType);

	}
	
	//#3460 - Start
	/**
	 * To retrieveSdoRemark from task list for supplier
	 */
	public void retrieveSdoRemark(String applType){
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		if (applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)
				|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {			
			
			SmAppl appli = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(
					currSupp.getSupplierId(), applType);
			if(appli != null){
				SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
				reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
				reqDTO.setDocId(appli.getApplId());
				
				SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);
				if (resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
					SmRemark remark = resDTO.getRemarksList().get(0);
					//log.info("The SDO Remark for the Application is*******" + remark.getRemark());
					getManagedBean().setSdoRemark(remark.getRemark());
				}	
			}
		}	
	}
	//#3460 - End
	
	//#28900- start
	//#31549 start - refactor this method
	private void copyBranchFromSSM(SmSupplierBranch ssmBranch) {
		//boolean isExist = false;
		ssmBranch.setSmAppl(getManagedBean().getSmAppl());
		List<SmSupplierBranch> branches = getManagedBean().getSmAppl().getSmSupplierBranches();
		if (branches == null) {
			branches = new ArrayList<>();
			getManagedBean().getSmAppl().setSmSupplierBranches(branches);
		}

		SmSupplierBranch existingBranch = findBranchByName(branches, ssmBranch.getBranchName().trim());
		if (existingBranch != null) {
			updateExistingBranch(existingBranch, ssmBranch);
		} else {
			addNewBranch(branches, ssmBranch);
		}
	}
	//#28900- end

	private SmSupplierBranch findBranchByName(List<SmSupplierBranch> branches,String branchName) {
		for (SmSupplierBranch branch : branches) {
			if (branch.getBranchName().trim().equalsIgnoreCase(branchName)
					&& !branch.isCopy()) {
				return branch;
			}
		}
		return null;
	}
	
	private void updateExistingBranch(SmSupplierBranch existingBranch,SmSupplierBranch ssmBranch) {
		existingBranch.setCopy(true);
		existingBranch.setSmAddress(SmCommonUtil.cloneAddress(ssmBranch
				.getSmAddress()));
		// #32725 - start
		existingBranch.setBranchName(ssmBranch.getBranchName().trim());
		existingBranch.setSsmData(true);
		// #32725 - end
	}
	
	private void addNewBranch(List<SmSupplierBranch> branches,SmSupplierBranch ssmBranch) {
		ssmBranch.setCopy(true);
		ssmBranch.setRevNo(SupplierManagementConst.DEFAULT_REV_NO); // #32725
		branches.add(ssmBranch);
	}

	// #31549 end
	
	/**
	 * Delete pending mof application.
	 * 
	 * @return String - output page to redirect
	 */
	public String deletePendingMofApplication() {
		try {
			String errMsg = getDeletePendingApplicationErrorMsg();
			if (!StringUtils.isBlank(errMsg)) {
				if (errMsg.equals(MessageConst.ERROR_722582)) {
					throw new EpException(errMsg,
							new String[] { getManagedBean().getSmAppl()
									.getApplNo() });
				} else {
					throw new EpException(errMsg);
				}
			}
			getManagedBean()
					.setReasonDescList(
							SmApplicationUtil
									.retrieveRejectReasonListByParameterCode(ReasonCategory.DELETE_PENDING_APPLICATION_REASON
											.value()));
			getManagedBean()
					.setRejectionOtherReasonId(
							SmApplicationUtil
									.retrieveOtherRejectReasonIdByParamCodeAndType(
											ReasonCategory.DELETE_PENDING_APPLICATION_REASON
													.value(),
											SupplierManagementConst.SM_REASON_CODE_OTHER));
			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;

		} finally {

		}
	}

	private String getDeletePendingApplicationErrorMsg() {
		String errMsg = StringUtils.EMPTY;
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		log.info("****** Testing the delete pending *********"
				+ smSupplier.getSupplierId());
		getManagedBean().setSmSupplier(smSupplier);
		MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
		reqDto.setSmSupplier(smSupplier);
		MofRegistrationResDTO resDTO = SmServiceLookup.getMofRegistrationSO()
				.retrievePendingApplicationDetails(reqDto);
		if (resDTO.getSmAppl() == null) {
			errMsg = MessageConst.ERROR_722528;
		} else {
			SmAppl smAppl = SmApplicationUtil.defaultSmAppl(resDTO.getSmAppl());
			if (smAppl.getApplType() != null
					&& smAppl.getApplType().equalsIgnoreCase(
							ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				log.info("*** This is cancellation application *****");
				errMsg = MessageConst.ERROR_722581;
			}
			getManagedBean().setSmAppl(smAppl);
			SmWorkflowStatus sts = SmCommonUtil
					.getCurrentActiveWokFlowStatus(getManagedBean().getSmAppl()
							.getApplId());
			if (sts != null && sts.getPmStatus() != null) {
				if (sts.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_APPL_DECLARATION) == 0) {
					log.info("*** This is draft application *****");
					errMsg = MessageConst.ERROR_722569;
				} else if (sts
						.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_PAYMENT) == 0) {
					log.info("*** This is application with pending payment *****");
					errMsg = MessageConst.ERROR_722577;
				} else if (sts
						.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_REVIEW_CANCELLATION) == 0) {
					log.info("*** This is duplicate request for the delete pending *****");
					errMsg = MessageConst.ERROR_722582;
				}
				setPmStatusDesc(sts.getPmStatus().getStatusId().toString());
			}
		}
		return errMsg;
	}

	/**
	 * To open the confirmation dialog box for cancel registration with MOF
	 */
	public String onDisplayConfirmCanceMofRegistration() {
		String page = null;
		Locale locale = new FacesScopes().getUserLocale();
		StringBuilder sb = new StringBuilder();
		boolean valid = true;
		if (getManagedBean().getSupplierRemarks() == null
				|| getManagedBean().getSupplierRemarks().trim() == "") {
			sb.append(ResourceBundleUtils.getMessage(
					"lbl.sm.mof.reg.reason.delete",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			// #35619 Start
			sb.append(" ");
			sb.append(ResourceBundleUtils.getMessage("lbl.sm.mandatory",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			sb.append("</br>");
			// #35619 End
			valid = false;
		}
		if (getManagedBean().getCancellationAttachment().getFileName() == null
				|| getManagedBean().getCancellationAttachment().getFileName()
						.trim() == "") {
			sb.append(ResourceBundleUtils.getMessage("lbl.sm.suppdoc.attach",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			// #35619 Start
			sb.append(" "); 
			sb.append(ResourceBundleUtils.getMessage("lbl.sm.mandatory",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			// #35619 End
			valid = false;
		}
		if (!valid) {
			throw new EpException(sb.toString());
		} else {
			page = submitCancelMofApplToBpm();
		}
		return page;
	}

	public void onDisplaySpkiPopForSubmitAppl() {
		boolean valid = false;
		if (getManagedBean().getSmAppl().getSupportingDocMode() != null
				&& getManagedBean().getSmAppl().getSupportingDocMode()
						.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			valid = validateSupportingDocs();
		} else {
			valid = true;
		}
		if (valid) {
			getManagedBean().getSpkiBackingBean().getChallengeQuestions();
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
		} else {
			log.info("*** Invalid Online supporting docs validataion **** ");
		}
	}

	/**
	 * To open the confirmation dialog box for deleting pending application
	 */
	public void onDisplayConfirmDelPending() {
		RequestContext.getCurrentInstance().execute("delPenRegConfirm.show()");
	}

	/**
	 * Update sm appl.To set the navigation tab counts and values.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void updateSmAppl() throws Exception {
		try {
			log.info("**** ENTER ACTION METHOD BEAN *************" + currentTab
					+ "  " + outputTab);

			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				log.info(" ****** REMAIN PREV VALUE ********");
			}
			getManagedBean().setTabActiveIndex(outputTab);
			log.info("Value ----"
					+ getManagedBean().getSmAppl().getSupportingDocMode());

		} catch (Exception e) {
			getManagedBean().setTabActiveIndex(currentTab);
			throw new EpException(MessageConst.ERROR_722505);
		}

	}

	/**
	 * Validate and Update sm application.Mainly called when navigate from one
	 * page to other or save as draft i.e. triggred from task list
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @param isBpmSubmit
	 *            - is from task list
	 * @param isBpmSubmit
	 *            the is bpm submit
	 */
	public void updateSmApplBpmSubmit(String applType, String currentPageType,
			String outputPageType, boolean isBpmSubmit) {
		validateUpdateSmAppl(applType, currentPageType, outputPageType,
				isBpmSubmit);
	}

	/**
	 * Validate and Update sm application.Mainly called when navigate from one
	 * page to other or save as draft
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * 
	 */
	public void updateSmAppl(String applType, String currentPageType,
			String outputPageType) {
		
		//#36483 - START
		if(currentPageType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC) 
				&& getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)){
			for(SmFacility fac : getManagedBean().getSmAppl().getSmFacilities()){
				if(fac.getFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE) && fac.isHeadquarters()){
					fac.getSmAddress().setMapLatitude(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLatitude());
					fac.getSmAddress().setMapLongitude(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLongitude());
					getManagedBean().getBasicCompanyFacility().getSmAddress().setMapLatitude(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLatitude());
					getManagedBean().getBasicCompanyFacility().getSmAddress().setMapLongitude(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLongitude());
					
					getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress1(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddress1());
					getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress2(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddress2());
					getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress3(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddress3());
					getManagedBean().getBasicCompanyFacility().getSmAddress().getPmCountry().setCountryId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCountry().getCountryId());
					getManagedBean().getBasicCompanyFacility().getSmAddress().getPmState().setStateId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState().getStateId());
					if(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision() != null && getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision().getDivisionId() != null){
						getManagedBean().getBasicCompanyFacility().getSmAddress().getPmDivision().setDivisionId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision().getDivisionId());
					}					
					getManagedBean().getBasicCompanyFacility().getSmAddress().getPmDistrict().setDistrictId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDistrict().getDistrictId());
					getManagedBean().getBasicCompanyFacility().getSmAddress().getPmCity().setCityId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCity().getCityId());
					getManagedBean().getBasicCompanyFacility().getSmAddress().setPostcode(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPostcode());
				}
			}
		}
		//#36483 - END
				
		// #34166 - Start
		if (currentPageType.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK)) {
			validateText();
			// #37779 - Start
			if (getManagedBean().isSupplierUser()) {
				SmApplicationUtil.checkBankAccountPurpose(getManagedBean());
			}
			// #37779 - End
		}
		// #34166 - End

		// show confirm popup to save draft before proceed to next section if
		// still not save draft
		if (!StringUtils.isBlank(applType)
				&& !getManagedBean().getSmAppl().isActiveAppl()
				&& getManagedBean().isSupplierUser()) {
			if (getManagedBean().getCompanyBasicGst() != null) {
				SmCommonUtil.validateGstRegNoUniqueness(getManagedBean()
						.getSmSupplier().getRegNo(), getManagedBean()
						.getCompanyBasicGst().getGstRegNo());
				SmApplicationUtil.validateGSt(getManagedBean(), currentTab,
						ruleHelper);
			}
			if (SmApplicationUtil.checkActiveSmApplForSupp(getManagedBean()
					.getSmSupplier())) {
				throw new EpException(MessageConst.ERROR_722542);
			}
			//#27356
			SmApplicationUtil.validateCoordinate(getManagedBean());
			// #34303 - start
			setValueForFacilityHeadquarters();
			// #34303 - end
			RequestContext.getCurrentInstance().execute(
					"saveDraftConfirmDialog.show();");
		} else {
			if ((getManagedBean().isApproverUser() || getManagedBean()
					.isPoUser())
					&& currentPageType
							.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				//#31043 start
				SmApplicationUtil.validateCategoriesBeforeSave(true,getManagedBean(), currentTab);
				//#31043 end
				SmApplicationUtil
						.validateOverlapCategoryJustificationAndSuppDocs(
								getManagedBean(), currentPageType);

				// set back newly added and approved categories to main appl
				// categories for appl type other than renew
				for (SmSupplierCategory cat : getManagedBean()
						.getActiveCategoryList()) {
					for (SmSupplierCategory parentCat : getManagedBean()
							.getSmAppl().getSmSupplierCategories()) {
						if (parentCat.getSupplierCategoryId().equals(
								cat.getSupplierCategoryId())) {
							getManagedBean()
									.getSmAppl()
									.getSmSupplierCategories()
									.set(getManagedBean().getSmAppl()
											.getSmSupplierCategories()
											.indexOf(parentCat), cat);
							break;
						}
					}

				}

				// set back newly added and approved categories to main appl
				// categories for appl type renew
				if (getManagedBean().getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
						&& !EntityHelper.isCollectionEmpty(getManagedBean()
								.getApprovedCategoryList())) {
					for (SmSupplierCategory cat : getManagedBean()
							.getApprovedCategoryList()) {
						for (SmSupplierCategory parentCat : getManagedBean()
								.getSmAppl().getSmSupplierCategories()) {
							if (parentCat.getSupplierCategoryId().equals(
									cat.getSupplierCategoryId())) {
								getManagedBean()
										.getSmAppl()
										.getSmSupplierCategories()
										.set(getManagedBean().getSmAppl()
												.getSmSupplierCategories()
												.indexOf(parentCat), cat);
								break;
							}
						}
					}
				}
				//#32236 start
				setBusinessAndHadApprovedSpecialCatcode();
				//#32236 end
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				//#32236 start
				getManagedBean().setOutputPage(outputPageType);
				//#32236 end
				SmApplicationUtil.validateCategoriesBeforeSave(true,
						getManagedBean(), currentTab);
				SmApplicationUtil
						.validateOverlapCategoryJustificationAndSuppDocs(
								getManagedBean(), currentPageType);
				SmApplicationUtil.updateOverlapDocsToSmAppl(getManagedBean());
				SmApplicationUtil.getInstance()
						.removeNotApplicableInformationBeforeSave(
								getManagedBean());
				//Bug #26703 set whether allowed to change business type from home office to business
				setBusinessAndHadApprovedSpecialCatcode();
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP)) {
					//Bug #26616
//					|| currentPageType
//							.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)
				SmApplicationUtil.updateTravellingSoftware(getManagedBean());
				nullifiedPmMachineWithNoId();
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)
					&& applType.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
				SmApplicationUtil.validateEquityOwnerCount(getManagedBean(),
						currentTab, currentPageType);
				SmApplicationUtil.validateEquityOwnerAmount(getManagedBean(),
						currentTab, ruleHelper);
				SmApplicationUtil.validateEquityCompanyAmount(getManagedBean(),
						currentTab);
			//#34277 --START
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)
					&& !applType.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
				SmApplicationUtil.validateEquityOwnerAmount(getManagedBean(),
						currentTab, ruleHelper);
				SmApplicationUtil.validateEquityCompanyAmount(getManagedBean(),
						currentTab); 
			//#34277 --END
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)
					|| currentPageType
							.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {

				updatePersonnelRecordStatus(getManagedBean().getSmAppl()
						.getSmPersonnels(), getManagedBean().getSmSupplier());

				SmApplicationUtil.updateApplBranchCodeIfEmpty(getManagedBean()
						.getSmAppl());
				//START SSM AUTO CALCULATE SHARE
				// #28466 - fix reopen
				if (getManagedBean().getSmAppl().isApplValidWithSsm()) {
					calcPersonnelShareAmt(getManagedBean().getSmAppl().getSmPersonnels(),getManagedBean().getSmAppl().getSmCompanyBasic());
					setApplicationShareHolders(getManagedBean().getSmAppl().getSmPersonnels());
					calculateCapital();
				}
				//END SSM AUTO CALCULATE SHARE

			}
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_PODEC)
					|| currentPageType
							.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_APPRDEC)) {
				retriveCurrentAppl();
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS)) {
				updateMandatoryAndNonMandatorySuppDocToApplDoc();

				if (getManagedBean().isSupplierUser()) {
					if (getManagedBean().getSmAppl().getSupportingDocMode() != null
							&& getManagedBean()
									.getSmAppl()
									.getSupportingDocMode()
									.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)
							&& (currentTab < outputTab)) {
						validateSupportingDocs();
					}
					if (getManagedBean().getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses() != null
							&& !getManagedBean().getSmAppl()
									.getSmCompanyBasic()
									.getSmCompanyAddresses().isEmpty()) {
						SmCompanyAddress comAdd = getManagedBean().getSmAppl()
								.getSmCompanyBasic().getSmCompanyAddresses()
								.get(0);
						getManagedBean()
								.setPrevBasicStateId(
										comAdd.getSmAddress().getPmState()
												.getStateId());
					}
				}
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)
					&& getManagedBean().isSupplierUser()) {
					if (getManagedBean().getCompanyBasicGst() != null) {
						SmCommonUtil.validateGstRegNoUniqueness(getManagedBean()
								.getSmSupplier().getRegNo(), getManagedBean()
								.getCompanyBasicGst().getGstRegNo());
					}
					SmApplicationUtil.validateGSt(getManagedBean(), currentTab,
							ruleHelper);
					//28767
					log.info(" **** Initialize list for ANNUAL INFO Page **********");
					getManagedBean().getSmAppl().getSmApplIndebtedness();
					getManagedBean().getSmAppl().getSmPersonnelSecretaries();
					if(getManagedBean().getSmAppl().getSmApplAuditors()!=null){
						getManagedBean().getSmAppl().getSmApplAuditors().size();
					}
					// #34303 - start
					setValueForFacilityHeadquarters();
					// #34303 - end
					//#27356
					SmApplicationUtil.validateCoordinate(getManagedBean());
			//#32236 - start
			} else if(outputPageType.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)){
				setBusinessAndHadApprovedSpecialCatcode();
			}
			//#32236 - End
			
			//#34303 - Start
			if ((outputPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)
					|| outputPageType
							.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_DECLARATION)) && (getManagedBean().isSupplierUser())) {
				SmApplicationUtil.getInstance()
				.removeNotApplicableInformationBeforeSave(
						getManagedBean());
			}
			//#34303 - End
			
			setSupplierChangeValues(currentPageType);
			setBranchDisplayTable(); //29727 , #34135
			validateUpdateSmAppl(applType, currentPageType, outputPageType,
					false);
			log.info("******* Current page AFTER in managed bean ********"
					+ getManagedBean().getCurrentPageType());
		}
	}

	/**
	 * Update application and set spki value
	 * 
	 * @param PmDigiSign
	 *            pmDigiSign
	 * @param String
	 *            applType
	 * @param String
	 *            currentPageType
	 * @param String
	 *            outputPageType
	 */
	public void updateSmAppl(PmDigiSign pmDigiSign, String applType,
			String currentPageType, String outputPageType) {

		if (pmDigiSign != null) {
			updateSpkiDigiSignData(pmDigiSign, "SR", this.getManagedBean()
					.getSmAppl().getApplId(), this.getManagedBean().getSmAppl()
					.getApplNo(), prepareOriginalText());
			SpkiBackingBean spkiBackingBean = new SpkiBackingBean();
			byte[] signedData = spkiBackingBean
					.spkiSignTransaction(prepareOriginalText());
			log.info("signedData: " + signedData);

			pmDigiSign.setSpkiSignedData(signedData);
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		updateSmAppl(applType, currentPageType, outputPageType);
	}
	
	// #34303 - start
	public void setValueForFacilityHeadquarters() {

		boolean isHq = false;
		SmAppl appl = getManagedBean().getSmAppl();

		for (int i = 0; i < appl.getSmFacilities().size(); i++) {
			if ((appl.getSmFacilities().get(i).getFacilityType()
					.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE) || 
					appl.getSmFacilities().get(i).getFacilityType()
					.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE))
					&& appl.getSmFacilities().get(i).isHeadquarters()) {
				isHq = true;
			}
		}

		if (!isHq) {
			for (int i = 0; i < appl.getSmFacilities().size(); i++) {
				if ((appl.getSmFacilities().get(i).getFacilityType()
						.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE) || 
						appl.getSmFacilities().get(i).getFacilityType()
						.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE))) {
					appl.getSmFacilities().get(i).setHeadquarters(true);
				}
			}
		}
	}
	// #34303 - end	

	/**
	 * Get cert type name
	 * 
	 * @param Long
	 *            certTypeId
	 * @return String
	 */
	public String getCertTypeLabelById(Long certTypeId) {
		String retVal = "";
		if (certTypeId != null) {
			for (SelectItem item : Arrays.asList(getDropdownValues("CRT"))) {
				if (item.getValue() != null
						&& item.getValue().toString()
								.equals(certTypeId.toString())) {
					retVal = item.getLabel();
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Get country label by country Id
	 * 
	 * @param Long
	 *            countryId
	 * @return String
	 */
	public String getCountryLabelById(Long countryId) {
		return SmCommonUtil.getCountryLabelById(countryId);
	}

	/**
	 * Get state label by state Id
	 * 
	 * @param Long
	 *            stateId
	 * @return String
	 */
	public String getStateLabelById(Long stateId) {
		return SmCommonUtil.getStateLabelById(stateId);
	}
	//13320
	public String getDivisionLabelById(Long divisionId) {
		return SmCommonUtil.getDivisionLabelById(divisionId);
	}

	/**
	 * Show side menu icon verification icon
	 * 
	 * @param String
	 *            sectionType
	 * @return String
	 */
	public String getStepIcon(String sectionType) {
		return SmSideMenuIconUtil.getSideMenuVerificationIcon(getManagedBean(),
				sectionType);
	}

	/**
	 * Get district label by district Id
	 * 
	 * @param Long
	 *            districtId
	 * @return String
	 */
	public String getDistrictLabelById(Long districtId) {
		return SmCommonUtil.getDistrictLabelById(districtId);
	}

	/**
	 * Get city label by city Id
	 * 
	 * @param Long
	 *            cityId
	 * @return String
	 */
	public String getCityLabelById(Long cityId) {
		return SmCommonUtil.getCityLabelById(cityId);
	}

	/**
	 * Get annual revenue desciption by id
	 * 
	 * @param Long
	 *            annualRevenueId
	 * @return String
	 */
	public String getAnnualRevById(Long annualRevenueId) {
		String name = "";
		if (annualRevenueId != null) {
			for (SelectItem item : Arrays.asList(getDropdownValues("SAR"))) {
				if (item.getValue() != null
						&& item.getValue().toString()
								.equals(annualRevenueId.toString())) {
					name = item.getLabel();
					break;
				}
			}
		}
		return name;
	}

	/**
	 * Get staff total by category
	 * 
	 * @param String
	 *            staffCategory
	 * @return SmStaffTotal
	 */
	public SmStaffTotal getStaffTotalByCategory(String staffCategory) {
		return SmFieldChangeUtil.getStaffTotalByCategoryFromList(
				getManagedBean().getLatestAppl(), staffCategory);
	}

	/**
	 * Get ownership type label by type
	 * 
	 * @param String
	 *            ownershipType
	 * @return String
	 */
	public String getOwnershipTypeLabel(String ownershipType) {
		return SmCommonUtil.getOwnershipTypeLabel(ownershipType);
	}

	/**
	 * Get year list for work experience
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getYearsWorkExp() {
		List<SelectItem> initYears = Arrays.asList(getYearDropDown());
		return SmApplicationUtil.getYearsWorkExp(getManagedBean()
				.getSelectedJobFunction(), initYears);
	}

	/**
	 * Get factory type label by type
	 * 
	 * @param String
	 *            facType
	 * @return String
	 */
	public String getFacTypeLabel(String facType) {
		return SmCommonUtil.getFacTypeLabel(facType);
	}

	/**
	 * Get race by Id
	 * 
	 * @param Long
	 *            raceId
	 * @return String
	 */
	public String getRaceLabel(Long raceId) {
		return SmCommonUtil.getRaceByIdFromSelectItems(raceId);
	}

	/**
	 * Get professional body from session list by profBodyId
	 * 
	 * @param Long
	 *            profBodyId
	 * @return String
	 */
	public String getProfBodyByIdFromList(Long profBodyId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getProfBodyLs())
				&& profBodyId != null) {
			for (PmCertBody body : getManagedBean().getProfBodyLs()) {
				if (body.getCertBodyId().compareTo(profBodyId) == 0) {
					retVal = body.getBodyName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get education level from session list by eduLvlId
	 * 
	 * @param Long
	 *            eduLvlId
	 * @return String
	 */
	public String getEduLvlByIdFromList(Long eduLvlId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getEduLevelList())
				&& eduLvlId != null) {
			for (PmParameterDesc eduLvl : getManagedBean().getEduLevelList()) {
				if (eduLvl.getParameterDescId().compareTo(eduLvlId) == 0) {
					retVal = eduLvl.getCodeName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get education field from session list by eduFieldId
	 * 
	 * @param Long
	 *            eduFieldId
	 * @return String
	 */
	public String getEduFieldByIdFromList(Long eduFieldId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getEduFieldList())
				&& eduFieldId != null) {
			for (PmEduField eduField : getManagedBean().getEduFieldList()) {
				if (eduField.getEduFieldId().compareTo(eduFieldId) == 0) {
					retVal = eduField.getFieldName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get financial organization from session list by finOrgId
	 * 
	 * @param Long
	 *            finOrgId
	 * @return String
	 */
	public String getFinOrgByIdFromList(Long finOrgId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getFinOrgLs())
				&& finOrgId != null) {
			for (PmFinancialOrg finOrg : getManagedBean().getFinOrgLs()) {
				if (finOrg.getFinancialOrgId().compareTo(finOrgId) == 0) {
					retVal = finOrg.getFinOrgName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get bank account purpose label by accPurpose
	 * 
	 * @param String
	 *            accPurpose
	 * @return String
	 */
	public String getBankAccPurposeLabel(String accPurpose) {
		String retVal = "";
		if (accPurpose.equals("R")) {
			retVal = SmCommonUtil.getMessage("lbl.sm.bankDtls.registration",
					null);
		} else if (accPurpose.equals("P")) {
			retVal = SmCommonUtil.getMessage("lbl.sm.paymentLbl", null);
		} else {
			retVal = SmCommonUtil.getMessage("lbl.sm.bankDtls.both.regandpay",
					null);
		}
		return retVal;
	}

	/**
	 * Get price amount format to display from BigDecimal object type
	 * 
	 * @param BigDecimal
	 *            priceAmount
	 * @return BigDecimal
	 */
	public String getPriceAmountFormat(BigDecimal priceAmount) {
		return SmCommonUtil.getPriceAmountFormat(priceAmount);
	}

	/**
	 * Get date format to display from Date object type
	 * 
	 * @param Date
	 *            date
	 * @return String
	 */
	public String getDateFormat(Date date) {
		return SmCommonUtil.getDisplayDateFormat(date);
	}

	/**
	 * Get select item label from select items by select items value
	 * 
	 * @param Object
	 *            objId
	 * @param String
	 *            paramType
	 * @return String
	 */
	public String getLabelFromSelectItems(Object objId, String paramType) {
		String retVal = "";
		List<SelectItem> items = Arrays.asList(getDropdownValues(paramType));
		if (!SmCommonUtil.isCollectionEmpty(items) && objId != null) {
			for (SelectItem item : items) {
				if (item.getValue() != null
						&& item.getValue().toString().equals(objId.toString())) {
					retVal = item.getLabel();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get select item label from select items by value
	 * 
	 * @param Object
	 *            objId
	 * @return String
	 */
	public String getLabelFromSelectItems(Object objId, List<SelectItem> items) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(items) && objId != null) {
			for (SelectItem item : items) {
				if (item.getValue() != null
						&& item.getValue().toString().equals(objId.toString())) {
					retVal = item.getLabel();
				}
			}
		}
		return retVal;
	}

	/**
	 * Update spki related data
	 * 
	 * @param PmDigiSign
	 *            pmDigiSign
	 * @param String
	 *            docType
	 * @param Long
	 *            docId
	 * @param String
	 *            docNumber
	 * @param String
	 *            textBeforeSign
	 */
	private void updateSpkiDigiSignData(PmDigiSign pmDigiSign, String docType,
			Long docId, String docNumber, String textBeforeSign) {
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(userId);
		pmDigiSign.setCreatedDate(date);
		pmDigiSign.setOriginalData(textBeforeSign);
	}

	/**
	 * text to be signed for SM: applNo + userId (IC or password) + epNo
	 * 
	 * @return
	 */
	private String prepareOriginalText() {

		String applNo = this.getManagedBean().getSmAppl().getApplNo();
		String userIdentificationNo = new FacesScopes().getUserSessionBean()
				.getIdentificationNo();
		String epNo = this.getManagedBean().getSmSupplier().getEpNo();

		String originalText = applNo + "|" + userIdentificationNo + "|" + epNo;
		log.info("originalText: " + originalText);
		return originalText;
	}

	/**
	 * Validate and Update sm application.Mainly called when navigate from one
	 * page to other or save as draft
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @param isBpmSubmit
	 *            - is from task list
	 * @return true, if successful
	 */
	public boolean validateUpdateSmAppl(String applType,
			String currentPageType, String outputPageType, Boolean isBpmSubmit) {
		boolean valid = false;
		//30444--START--31072021--
		int k = 1; //#30444--02082021-
		boolean validPersonnels = true; 
		boolean displayError = false; 
		StringBuilder sbd = new StringBuilder(); 
		sbd.append("<br/>"); //30444
		//30444--END--31072021--
		log.info(" ***** Business  Type After hard code *********"
				+ getManagedBean().getSmAppl().getSmSupplier()
						.getBusinessType());
		getManagedBean().setTabActiveIndex(outputTab);
		try {

			log.info("******* IS PO *******" + getManagedBean().isPoUser());
			if (getManagedBean().isPoUser()
					|| getManagedBean().isApproverUser()) {
				setPoOrApprvrSectionReview();
			}

			log.info("****** Button type value ******** " + buttonType);
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)
					&& (buttonType
							.equalsIgnoreCase(SupplierManagementConst.SM_APPL_BUTTON_TYPE_PREV)
							|| buttonType
									.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)
							|| currentTab > outputTab || currentTab == outputTab)) {
				valid = SmApplicationUtil.validateCategoryOtherDesc(true,
						getManagedBean(), currentTab);
			} else {
				valid = validateBeforeSave(applType, currentPageType);
			}

			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				log.info(" ****** REMAIN PREV VALUE ********");
			}
			if (valid) {
				getManagedBean().setCurrentPageType(outputPageType); //#36576
				SmCommonUtil.getInstance().setUserDetails(
						getManagedBean().getSmAppl());
				saveOrUpdateAppl();
				getManagedBean().setCurrentPageType(outputPageType); //#36576 & #36622
			} else {
				getManagedBean().setTabActiveIndex(currentTab);
				getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
				getManagedBean().setCurrentPageType(currentPageType);
			}
			//#35086 & 36622 - start
			SmFieldChangeUtil.setApplTableListChangeStatus(getManagedBean().getLatestAppl(), 
					getManagedBean().getSmAppl(), getManagedBean().getCurrentPageType());
			//#35086 & 36622 - end
		}

		finally {
			if (getManagedBean().getCurrentPageType() != null) {
				switch (getManagedBean().getCurrentPageType()) {
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC:
					if (getManagedBean().getSmAppl().getSmAssociationBodies() != null) {
						SmApplicationUtil
								.assignNonPersonnelProfBodyList(getManagedBean());
					}
					SmApplicationUtil.sortStaffInfo(getManagedBean()
							.getSmAppl().getSmStaffTotals());
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS:
					//28771
					if(getManagedBean().getSmAppl().getSupportingDocMode() ==null){
						getManagedBean().getSmAppl().setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
					}
					if (getManagedBean().isSupplierUser()) {
						assignApplicationSupportingDocs();
					}
					//Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen * avoid double message if already execute saveOrUpdateAppl() previously
					if ((getManagedBean().isPoUser()
							|| getManagedBean().isApproverUser()) && !valid) {
						saveOrUpdateAppl();
					}
					//3460 - Start
					initSDORemarkInfo();
					//3460 - End
					initNonMandatoryAndMandatoryDocs();
					displayBumiRecommendNote();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL:
					calcPersonnelListCount();
					SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
							getManagedBean().getSmAppl(), getManagedBean()
							.getPrevApprovedPersonnels(),
							getManagedBean().isDisAllowOwnerEdit());
					/*#28458 - Start*/
					if (getManagedBean().isDisAllowOwnerEdit()){
						throw new EpException(MessageConst.ERROR_722683);
					}
					/*#28458 - End*/
					SmApplicationUtil.setPersonnelSoftcertRenewalStatus(this
							.getManagedBean().getSmSupplier(), this
							.getManagedBean().getSmAppl().getSmPersonnels());
					SmApplicationUtil
							.sortPersonnelByRoleInCompany(getManagedBean()
									.getSmAppl());
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY:
					if (valid) {
						SmApplicationUtil
								.assignActiveAndApprovedCategoryList(getManagedBean());
					}
					if (getManagedBean().getSmAppl().getApplCategoryType()
							.equalsIgnoreCase("N")) {
						getManagedBean()
								.setCatTypeConfirm(
										SmCommonUtil
												.getMessage(
														"lbl.sm.categorycode.note.normal",
														null));
					} else {
						getManagedBean().setCatTypeConfirm(
								SmCommonUtil.getMessage(
										"lbl.sm.categorycode.note.special",
										null));
					}
					SmCommonUtil
							.calculateSmApplicationPaidUpCapitalPercent(getManagedBean()
									.getSmAppl().getSmCompanyBasic());

					// to init page whether normal or special category type is
					// allowed
					//#32236 - start
					if(getManagedBean().isSupplierUser()){
						initApplCategoryType();
					}
					//#32236 - end

					// API FOR LIST OF OVERLAPPING CATEGORIES
					SmApplicationUtil
							.checkOverLappingCategories(getManagedBean());

					SmApplicationUtil.initOverlapCategoryDocs(getManagedBean()
							.isOverLappedCategoryExists(), getManagedBean());

					if ((getManagedBean().isPoUser() || getManagedBean()
							.isApproverUser())
							&& getManagedBean().isOverLappedCategoryExists()
							&& EntityHelper.isCollectionEmpty(getManagedBean()
									.getSmAppl().getSmCategoryOverlapDocs())) {
						FacesContext fc = FacesContext.getCurrentInstance();
						fc.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										null,
										SmCommonUtil
												.getMessage(
														"lbl.sm.category.overlap.pleaseReject",
														null)));
					}

					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY:
					if (valid) {
						log.info("*********OUPUT IS CAPITAL PAGE Values is capital *******");
						// 28466 - remove skip condition
						calculateCapital();
						SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
								getManagedBean().getSmAppl(), getManagedBean()
										.getPrevApprovedPersonnels(),
								getManagedBean().isDisAllowOwnerEdit());
						/*#28458 - Start*/
						if (getManagedBean().isDisAllowOwnerEdit()){
							throw new EpException(MessageConst.ERROR_722683);
						}
						/*#28458 - End*/
						SmApplicationUtil.setPersonnelSoftcertRenewalStatus(
								this.getManagedBean().getSmSupplier(), this
										.getManagedBean().getSmAppl()
										.getSmPersonnels());
						break;
					}
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_POINQUIRY:
					log.info("******* This is PO inquiry page ********");
					SmApplicationUtil.initPoInquiry(getManagedBean(), false);
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_PODEC:
					//#30444 --START--  --31072021
					for (SmPersonnel personnel : getManagedBean().getSmAppl().getSmPersonnels()){ 
						validPersonnels = true;					
						//#30444 --START--25082021--
						if ((personnel.isClerical() && personnel.isManagement() 
								&& !getManagedBean().getBusinessTypeVal().equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT))
										|| (personnel.isClerical() && personnel.isManagement() && !personnel.isEquityOwner()
												&& getManagedBean().getBusinessTypeVal().equalsIgnoreCase(
														SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT))){
							validPersonnels = false;
						}
						//#30444 --END--25082021--
						if(!validPersonnels){
							sbd.append(k + ". ");//#30444--02082021-
							sbd.append(personnel.getName());
							sbd.append("<br/>");
							displayError = true;
							k++;//#30444--02082021-
						}	
					}	
						
					if (displayError && (getManagedBean().isPoUser() || getManagedBean().isApproverUser())) {
						if (getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONTRACTOR)){
						throw new EpException(MessageConst.ERROR_722689, new String[]
								{sbd.toString() });
						}
						if (getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONSULTANT)){
						throw new EpException(MessageConst.ERROR_722690, new String[]
								{sbd.toString() });
						}	
					}
				//#30444 --END--  --31072021
					//#31461-Start
					SmApplicationUtil.validateEquityOwnerCount(getManagedBean(), -1,
							SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);
					//#30691 - START - 26112021
					if (getManagedBean().isPoApproverChgBusiness()) {
						throw new EpException(getManagedBean().getMsgChgBusiness());
					}//#30691 - END - 26112021
					if (getManagedBean().isPoApproverEquityErr()) {
						throw new EpException(getManagedBean().getMsgEquityErr());
					}//#31461-end
					SmApplicationUtil.checkAllowedToApprove(getManagedBean());
					displayBumiRecommendNote();
					getHqFacility();
					
					//#31130-start
					SmApplicationUtil.validatePersonnelBeforeSave(true, -1,getManagedBean());
					SmApplicationUtil.validatePersonnelAcademics(true, getManagedBean(), -1);
					//#31130 -end
					
					//#32236 start
					SmApplicationUtil.setBusinessAndHadApprovedSpecialCatcode(getManagedBean());
					SmApplicationUtil.validateSpecialAndNormalCategoryCapital(getManagedBean());
					//#32236 end
					
					// #37779 - Start
					SmApplicationUtil.checkBankAccountPurpose(getManagedBean());
					// #37779 - End
					
					setDefaultPoReviewSection(); //#36622
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_APPRDEC:
					//#30444 --START--  --31072021
					for (SmPersonnel personnel : getManagedBean().getSmAppl().getSmPersonnels()){
						validPersonnels = true;
						//#30444 --START--25082021--
						if ((personnel.isClerical() && personnel.isManagement() 
								&& !getManagedBean().getBusinessTypeVal().equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT))
										|| (personnel.isClerical() && personnel.isManagement() && !personnel.isEquityOwner()
												&& getManagedBean().getBusinessTypeVal().equalsIgnoreCase(
														SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT))){
							validPersonnels = false;
						}
						//#30444 --END--25082021--
						if(!validPersonnels){
							sbd.append(k + ". ");//#30444--02082021-
							sbd.append(personnel.getName());
							sbd.append("<br/>");
							displayError = true;	
							k++;//#30444--02082021-
						}	
					}	
					
					if (displayError && (getManagedBean().isPoUser() || getManagedBean().isApproverUser())) {
						if (getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONTRACTOR)){
						throw new EpException(MessageConst.ERROR_722689, new String[]
								{sbd.toString() });
						}
						if (getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONSULTANT)){
						throw new EpException(MessageConst.ERROR_722690, new String[]
								{sbd.toString() });
						}	
					}
				//#30444 --END--  --31072021
					//#31461-Start
					SmApplicationUtil.validateEquityOwnerCount(getManagedBean(), -1,
							SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);
					//#30691 - START - 26112021
					if (getManagedBean().isPoApproverChgBusiness()) {
						throw new EpException(getManagedBean().getMsgChgBusiness());
					}//#30691 - END - 26112021
					if (getManagedBean().isPoApproverEquityErr()) {
						throw new EpException(getManagedBean().getMsgEquityErr());
					}//#31461-End
					SmApplicationUtil.checkAllowedToApprove(getManagedBean());
					displayBumiRecommendNote();
					//#31130-start
					SmApplicationUtil.validatePersonnelBeforeSave(true, -1,getManagedBean());
					SmApplicationUtil.validatePersonnelAcademics(true, getManagedBean(), -1);
					//#31130 -end
					
					//#32236 - start
					SmApplicationUtil.setBusinessAndHadApprovedSpecialCatcode(getManagedBean());
					SmApplicationUtil.validateSpecialAndNormalCategoryCapital(getManagedBean());
					//#32236 - end
					
					// #37779 - Start
					SmApplicationUtil.checkBankAccountPurpose(getManagedBean());
					// #37779 - End
					
					setDefaultApproverReviewSection(); //#36622
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_SITEVISITREV:
					log.info("******* This is site visit review page ********");
					assignViewSiteVisitReport();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY:
					log.info("******* This is facility for special category page ********");
					SmApplicationUtil
							.handleShowOrHideFacilityPanels(getManagedBean());
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP:
					getManagedBean().setMachineCategoryVOList(
							SmApplicationUtil
									.getMachineCategoryVO(getManagedBean()
											.getSmAppl()));
					SmApplicationUtil
							.showOrHideTravelSoftwarePanel(getManagedBean());
					SmApplicationUtil
							.initializeTravellingSoftwareTicked(getManagedBean());
					break;
				default:
					break;
				}

				// init business premise whether show or hide home office
				initBusinessPremise();

			}
			assignValReqdToFalse();
		}
		return valid;
	}

	/**
	 * Retrieve latest application after update.
	 * 
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @return true, if successful
	 */
	private boolean retriveLatestApplAfterUpdate(String currentPageType,
			String outputPageType) {
		//#Enhancement #11765
		//alreadyExecute = false;//avoid duplicate execute function setDefaultApproverReviewSection() && setDefaultPoReviewSection()
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			//SSM CHANGES 11012020
			if(getManagedBean().isPoUser() || getManagedBean().isApproverUser()){
				finReqDto.setPoSdoUser(true);
			}else{
				finReqDto.setPoSdoUser(false);
			}
			finReqDto.setApplId(getManagedBean().getSmAppl().getApplId());
			finReqDto.setCurrentPage(getManagedBean().getCurrentPageType());
			log.info("********* Final Block Id *****" + finReqDto.getApplId());
			log.info("***** Its a valid ************");
			MofRegistrationResDTO resDTO = SmServiceLookup
					.getMofRegistrationSO().retrieveCurrentDraftApplication(
							finReqDto);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmSupplier(
					getManagedBean().getSmAppl().getSmSupplier());
			// #34303 - start
			setValueForFacilityHeadquarters();
			// #34303 - end
			setApplicationFailities(getManagedBean().getSmAppl()
					.getSmFacilities());

			if (getManagedBean().getSmAppl().getSmPersonnels() != null
					&& !getManagedBean().getSmAppl().getSmPersonnels()
							.isEmpty()) {
				setApplicationShareHolders(getManagedBean().getSmAppl()
						.getSmPersonnels());
			}
			retrieveBusinessTypeList();
			assignSupplierBusinessType(getManagedBean().getSmAppl()
					.getSmSupplier());
			if (getManagedBean().isPoUser()) {
				setDefaultPoReviewSection();
			}
			if (getManagedBean().isApproverUser()) {
				setDefaultApproverReviewSection();
			}
			return true;
		} finally {
			SmApplicationUtil.getInstance().setApplAddressTransientValue(
					getManagedBean().getSmAppl(), false);

			// set application table field change status
			SmFieldChangeUtil.setApplTableListChangeStatus(getManagedBean()
					.getLatestAppl(), getManagedBean().getSmAppl(),
					getManagedBean().getCurrentPageType());
			
			// 35208 START
			if(getManagedBean().getSmSupplier().getSmAppl() != null){
				SmServiceLookup.getPersonnelSO().updatePersonnelByLatestApplId(
						getManagedBean().getSmSupplier().getSmAppl());
			}
			if(getManagedBean().getSmAppl() != null){
				SmServiceLookup.getPersonnelSO().updatePersonnelByLatestApplId(
						getManagedBean().getSmAppl());
			}
			// 35208 END

			// init GST information
			initGst();
		}

	}

	/**
	 * Retrieve current application.
	 */
	private void retriveCurrentAppl() {
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			finReqDto.setApplId(getManagedBean().getSmAppl().getApplId());
			MofRegistrationResDTO resDTO = SmServiceLookup
					.getMofRegistrationSO().retrieveCurrentDraftApplication(
							finReqDto);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
		} finally {

		}
	}

	/**
	 * Gets the personnel info by id.
	 * 
	 * @param personnelId
	 *            - Long ( personnel id)
	 * @return SmPersonnel
	 */
	public SmPersonnel getPersonnelInfoById(Long personnelId) {
		try {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setPersonnelId(Long.valueOf(personnelId));
			PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
					.retrievePersonnelById(reqDTO);
			return resDTO.getSmPersonnel();
		} finally {

		}
	}

	/**
	 * Verify selected rejection reason
	 */
	public void verifySelectedRejectReasons() {
		getManagedBean().setRejectReasonReqd(isDpaOthersSelected());
		if (!getManagedBean().isRejectReasonReqd()) {
			getManagedBean().setApplnRejectionRemarks(StringUtils.EMPTY);
		}
	}

	/**
	 * Return true or false whether there is Others selected in Delete Pending
	 * Application Reason
	 * 
	 * @return boolean
	 */
	private boolean isDpaOthersSelected() {
		boolean retVal = false;
		if (getManagedBean().getReasonId() != null
				&& getManagedBean().getReasonId().length > 0) {
			for (Long reasonId : getManagedBean().getReasonId()) {
				ReasonReqDTO reqDTO = new ReasonReqDTO();
				reqDTO.setReasonId(reasonId);
				PmReason reason = SmServiceLookup.getReasonSO()
						.retrieveReasonById(reqDTO).getPmReason();
				if (reason != null
						&& !StringUtils.isBlank(reason.getReasonCode())
						&& SupplierManagementConst.DPA_REASON_OTHERS_LIST
								.contains(reason.getReasonCode())) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Sets the SM application rejection reasons selected.
	 */
	private void setSmApplRejectionReasons() {
		StringBuilder sb = new StringBuilder();
		// int count = 0;
		List<SmApplRejectReason> resList = new ArrayList<SmApplRejectReason>();
		if(getManagedBean().getReasonId()!=null && getManagedBean().getReasonId().length>0){
			for (Long resId : getManagedBean().getReasonId()) {
				for (PmReasonDesc desc : getManagedBean().getReasonDescList()) {
					if (desc.getPmReason().getReasonId().equals(resId)) {				
						if(resId.equals(new Long(345L))){//safe check, not necessarily needed, just in case, reason(others) must be accompanied by a remark
							if (StringUtils.isBlank(getManagedBean()
									.getApplnRejectionRemarks())) {
								//#37896, 15629 - Start
								throw new EpException(SmCommonUtil.getMessage("lbl.sm.error.rejection.reasons",
										null));
								//#37896,15629 - End
							}
						}
						
						SmApplRejectReason rej = new SmApplRejectReason();
						rej.setPmReason(desc.getPmReason());
						rej.setSmAppl(getManagedBean().getSmAppl());
						resList.add(rej);
						log.info("Reason Id === " + resId);
						// sb.append(++count).append(". ");
						// sb.append(desc.getReasonDesc());
						// sb.append("\n");
						sb.append(desc.getPmReason().getReasonId());
						sb.append("|");
					}
				}
			}
		}else{
			//Just in case
			//#37896, 15629 - Start
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.error.rejection.reasons",
					null)); 
			//#37896, 15629 - End
		}

		if (getManagedBean().getSmAppl().getSmApplRejectReasons() != null
				&& !getManagedBean().getSmAppl().getSmApplRejectReasons()
						.isEmpty()) {
			log.info(" ***** Already exisiting records ******* "
					+ getManagedBean().getSmAppl().getSmApplRejectReasons()
							.size());
			getManagedBean().getSmAppl().getSmApplRejectReasons().clear();
			log.info(" *****Size after reset******* "
					+ getManagedBean().getSmAppl().getSmApplRejectReasons()
							.size());
		}
		getManagedBean().getSmAppl().getSmApplRejectReasons().addAll(resList);
		getManagedBean().setReasonDesc(sb.toString());
	}

	/**
	 * Sets the PO or Approver section review.
	 */
	private void setPoOrApprvrSectionReview() {
		if (getManagedBean().isPoUser()
				&& getManagedBean().getPoSecReview().getApplSectionReviewId() != null) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getApplSectionReviewId().equals(
						getManagedBean().getPoSecReview()
								.getApplSectionReviewId())) {
					// Bug #21123
					if (getManagedBean().getPoSecReview().getRemarkToApprover() != null) {
						getManagedBean().getPoSecReview().setRemarkDate(
								new Timestamp(System.currentTimeMillis()));
					}
					getManagedBean()
							.getSmAppl()
							.getSmApplSectionReviews()
							.set(getManagedBean().getSmAppl()
									.getSmApplSectionReviews().indexOf(rev),
									getManagedBean().getPoSecReview());
				}
			}

		}
		if (getManagedBean().isApproverUser()
				&& getManagedBean().getApprvrSecReview()
						.getApplSectionReviewId() != null) {
			log.info("**** Existing Apprvr remarks *******");
			for (SmApplSectionReview revs : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (revs.getApplSectionReviewId().equals(
						getManagedBean().getApprvrSecReview()
								.getApplSectionReviewId())) {
					getManagedBean()
							.getSmAppl()
							.getSmApplSectionReviews()
							.set(getManagedBean().getSmAppl()
									.getSmApplSectionReviews().indexOf(revs),
									getManagedBean().getApprvrSecReview());
				}
			}
		}
	}

	/**
	 * Validate for each time navigate from one section to another section.
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @return true, if valid
	 */
	private Boolean validateBeforeSave(String applType, String currentPageType) {
		log.info("******* Page Type ********" + currentPageType);
		boolean validSave = true;
		//#32968 START
		getManagedBean().setCheckBranch(false);
		//#32968 END
		switch (currentPageType) {
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC:
			if (!getManagedBean().getSmAppl().getApplType()
					.equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				validSave = SmApplicationUtil.validateBasicInfoBeforeSave(
						validSave, getManagedBean(), currentTab);
				//#27853
				if (!getManagedBean().isApproverUser() && !getManagedBean().isPoUser()) {
					SmApplicationUtil.validateCoordinate(getManagedBean());
				}
			}
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL:

			if (getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
					&& !getManagedBean().isPoUser() && !getManagedBean().isApproverUser()) { //#31130 	
				validSave = SmApplicationUtil.validatePersonnelAcademics(
						validSave, getManagedBean(), currentTab);
			}
			//#30762 --START
			if(!getManagedBean().isDisAllowOwnerEdit() 
					&& !getManagedBean().isPoUser() && !getManagedBean().isApproverUser()){	//#30444 --31072021--
				validSave = SmApplicationUtil.validatePersonnelBeforeSave(
						validSave, currentTab, getManagedBean());
			}
			//#30762 --END
			calculateBumiPercentageTotals();
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK:
			// #37779 - Start
			if (getManagedBean().isSupplierUser()) {
				SmApplicationUtil.checkBankAccountPurpose(getManagedBean());
				validSave = SmApplicationUtil.validateBankBeforeSave(validSave,
						getManagedBean(), currentTab);
			}
			// #37779 - End
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY:
			validSave = SmApplicationUtil.validateCategoriesBeforeSave(
					validSave, getManagedBean(), currentTab);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY:
			validSave = SmApplicationUtil.validateFacilityPanel(
					getManagedBean(), currentTab, validSave);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP:
			validSave = SmApplicationUtil.validateMachineDetails(validSave,
					currentTab, getManagedBean());
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS:

			if ((getManagedBean().isPoUser() || getManagedBean()
					.isApproverUser()) && currentTab < outputTab) {
				validSave = SmApplicationUtil.checkSupportDocsValidity(
						validSave, getManagedBean(), currentTab);
				copyPoVerifiedDocsToApprover();
			}

		default:
			log.info("****** Not a defined page ***********");

		}
		return validSave;
	}

	/**
	 * Final validation before declare or save draft application
	 * 
	 * @param String
	 *            applType
	 * @param String
	 *            currentPageType
	 * @return Boolean
	 */
	private Boolean validateFinal(String applType, String currentPageType) {
		boolean validSave = validateBeforeSave(applType, currentPageType);
		// #28466 - Reopen fix
		currentTab = getManagedBean().getTabActiveIndex(); 

		if (validSave && !StringUtils.isBlank(currentPageType)) {
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY) && !getManagedBean().isDisAllowOwnerEdit() ) {//31461
				validSave = SmApplicationUtil.validateEquityOwnerCount(
						getManagedBean(), currentTab, currentPageType);
				if (validSave) {
					validSave = SmApplicationUtil.validateEquityOwnerAmount(
							getManagedBean(), currentTab, ruleHelper);
				} 
				//25929 - start
				if (validSave) {
					validSave = SmApplicationUtil.validateEquityCompanyAmount(
							getManagedBean(), currentTab);
				} else if (validSave
						&& getManagedBean()
								.getSmAppl()
								.getSupplierType()
								.equalsIgnoreCase(
										SupplierManagementConst.STYPE_CONSULTANT)) {
					validSave = SmApplicationUtil.validatePersonnelAcademics(
							validSave, getManagedBean(), currentTab);
				}
				//25929 - end
				validatePaidCaptialAmt(true);
			}
		}
		return validSave;
	}

	private void validateBeforeSubmit() {

		// validate dont have facility
		SmApplicationUtil.validateDontHavePremise(getManagedBean().getSmAppl());

		// validate overlap category supporting docs
		SmApplicationUtil.validateOverlapCategoryJustificationAndSuppDocs(
				getManagedBean(), null);

		// validate at least one category is added
		if (getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| getManagedBean()
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			// exclude site visit after approval because the catcode already
			// approved
			if (!getManagedBean().isSiteVisitAfterApproval()) {
				boolean validNewCatAdded = SmApplicationUtil
						.valiateNewCategoryAdded(getManagedBean());
				if (!validNewCatAdded) {
					//Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen
					getManagedBean().setShowParentMsg(true);
					throw new EpException(MessageConst.ERROR_722570);
				}
			}
		}

		// validate personnel
		SmApplicationUtil.validatePersonnelBeforeSave(true, -1,
				getManagedBean());

		// validate branch
		SmApplicationUtil.validateBranchWithoutBankAcc(getManagedBean(),
				currentTab, true);

		// validate facility
		SmApplicationUtil.validateFacilityPanel(getManagedBean(), -1, true);

		// validate machine
		SmApplicationUtil.validateMachineDetails(true, -1, getManagedBean());

		// validate equity owner count
		SmApplicationUtil.validateEquityOwnerCount(getManagedBean(), -1,
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);

		// validate equity owner amount
		SmApplicationUtil.validateEquityOwnerAmount(getManagedBean(), -1,
				ruleHelper);

		// validate equity company amount
		SmApplicationUtil.validateEquityCompanyAmount(getManagedBean(), -1);

		// validate company basic info
		SmApplicationUtil
				.validateCompanyInformation(getManagedBean(), -1, true);

		// validate paid up capital amount
		SmApplicationUtil.validatePaidCaptialAmt(false, getManagedBean(),
				currentTab);

		SmApplicationUtil
				.validateSpecialCategoryAssignedToFacility(getManagedBean());
		//#30508
		//#32236 - Start
		if(getManagedBean().isSupplierUser()){
			SmApplicationUtil.setBusinessAndHadApprovedSpecialCatcode(getManagedBean());
			SmApplicationUtil.validateSpecialAndNormalCategoryCapital(getManagedBean());	
		}
		//#32236 - End
		
		//#31130- start
		if (getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
			SmApplicationUtil.validatePersonnelAcademics(true, getManagedBean(), currentTab);
		}
		//#31130- end
		
		// #37779 - Start
		if (getManagedBean().isSupplierUser()) {
			SmApplicationUtil.checkBankAccountPurpose(getManagedBean());
			SmApplicationUtil.validateBankBeforeSave(true, getManagedBean(),
					currentTab);
		}
		// #37779 - End
	}

	/**
	 * Retrieve and Assign supporting documents list required for application.
	 */
	private void assignApplicationSupportingDocs() {
		log.info(" ** For Suporting Docs ** ");

		// default to false has suppdoc to download
		getManagedBean().setHasSuppDocToDownload(false);

		if (getManagedBean().getSmAppl().getSmApplSupportDocs() == null) {
			getManagedBean().getSmAppl().setSmApplSupportDocs(
					new ArrayList<SmApplSupportDoc>());
		}
		DocumentReqDTO rqDto = new DocumentReqDTO();
		rqDto.setStateCode(getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState()
				.getStateCode());
		rqDto.setAppTypeCode(getManagedBean().getSmAppl().getApplType());
		//28775 - Site Visit Grouping Enhancement - 28775 & 32726 & 35419 - start
		List<SmSupplierCategory> suppCats = getManagedBean().getSmAppl().getSmSupplierCategories();
		boolean isRenew = getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		if (!suppCats.isEmpty()){
			List<Long> catL3Ids = new ArrayList<>();
			for (SmSupplierCategory cat : suppCats) {
				int status = cat.getRecordStatusFlg();
				if (status == SupplierManagementConst.PENDING_ACT_STATUS
						|| (isRenew && status == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)) {
					catL3Ids.add(cat.getPmCategoryL3().getCategoryL3Id());
					}
				}
			// temp fix to reduce the number of supporting documents retrieve
			// for update & bumi.
			if (catL3Ids.isEmpty()) {
				catL3Ids.add(0L);
				}
		//28775 - Site Visit Grouping Enhancement - 28775 & 32726 & 35419 - end

			rqDto.setCategoryL3Ids(catL3Ids);
			log.info(" **** Total categories sent to Doc So ***"
					+ rqDto.getCategoryL3Ids().size());
		}
		if (!getManagedBean().getSmAppl().getSmSupplierCerts().isEmpty()) {
			List<Long> certIds = new ArrayList<>();
			for (SmSupplierCert cert : getManagedBean().getSmAppl()
					.getSmSupplierCerts()) {
				if (getManagedBean().getGlpCert() != null
						&& cert.getCertTypeId() != null
						&& cert.getCertTypeId().compareTo(
								new BigDecimal(getManagedBean().getGlpCert()
										.getParameterId())) == 0) {
					if (cert.getPmCertBody() != null
							&& cert.getPmCertBody().getCertBodyId() != null)
						certIds.add(cert.getPmCertBody().getCertBodyId());
				}
			}
			rqDto.setCertBodyIds(certIds);
		}

		log.info(">>>>>>>>START SET SECTION EDITOR<<<<<<<<<<");
		SmSideMenuIconUtil.setAllSectionEditorVO(getManagedBean(), false,
				SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS);
		rqDto.setSupplierTypeCode(getManagedBean().getSmAppl()
				.getSupplierType());
		rqDto.setCompanyTypeCode(getManagedBean().getSmAppl().getSmSupplier()
				.getBusinessType());
		rqDto.setCompanyInfoChanged(getManagedBean().getSupplierEditVO()
				.isSuppEditCompBasic());
		rqDto.setAsscProjChanged(getManagedBean().getSupplierEditVO()
				.isSuppEditAssocProj());
		rqDto.setCapitalEquityChanged(getManagedBean().getSupplierEditVO()
				.isSuppEditEquity());
		rqDto.setPersonnelBranchChanged(getManagedBean().getSupplierEditVO()
				.isSuppEditPersonnel());
		rqDto.setBankInfoChanged(getManagedBean().getSupplierEditVO()
				.isSuppEditBank());
		rqDto.setForeigner(getManagedBean().getSupplierEditVO().isHasForeign());
		log.info("SUPP TYPE:" + getManagedBean().getSmAppl().getSupplierType());
		log.info("BUSS TYPE:"
				+ getManagedBean().getSmAppl().getSmSupplier()
						.getBusinessType());
		log.info("EDIT COMP BASIC:" + rqDto.isCompanyInfoChanged());
		log.info("EDIT ASSOC:" + rqDto.isAsscProjChanged());
		log.info("EDIT EQUITY:" + rqDto.isCapitalEquityChanged());
		log.info("EDIT PERSONNEL BRANCH:" + rqDto.isPersonnelBranchChanged());
		log.info("EDIT BANK:" + rqDto.isBankInfoChanged());
		log.info("EDIT FOREIGNER:" + rqDto.isForeigner());
		log.info(">>>>>>>>END SET SECTION EDITOR<<<<<<<<<<");
		SmFacility hq = SmApplicationUtil.getHqFacilityByAppl(getManagedBean()
				.getSmAppl());
		if (hq != null) {
			//29480 - Start
			if (hq.getFacilityType().equalsIgnoreCase("H")) {
				rqDto.setForHomeOffice(true);
				rqDto.setForBusinessOffice(false);
				//rqDto.setForHomeOffice(hq.getFacilityType().equals("H") ? true : false);
			}
			else if(hq.getFacilityType().equalsIgnoreCase("B")){
				rqDto.setForBusinessOffice(true);
				rqDto.setForHomeOffice(false);
				//rqDto.setForHomeOffice(hq.getFacilityType().equals("B") ? true : false);
			}
			//29480 - End
		}
		DocumentResDTO rsDTO = null;
		rsDTO = retrieveSupportingDocumentList(rqDto);
		//SSM INTEGRATION
		boolean changesInCapital = false;
		if(businessTypeIsROC(getManagedBean().getSmAppl().getSmSupplier().getBusinessType()) && getManagedBean().getSmAppl().isApplValidWithSsm() && getManagedBean().getSmAppl().getSmCompanyBasic().getPaidUpCapitalSSM() != null){
			BigDecimal paidCapital = getManagedBean().getSmAppl().getSmCompanyBasic().getPaidUpCapital();
			BigDecimal paidCapitalSSM = getManagedBean().getSmAppl().getSmCompanyBasic().getPaidUpCapitalSSM();
			if(paidCapital.compareTo(paidCapitalSSM) < 0 || paidCapital.compareTo(paidCapitalSSM) >0){
				changesInCapital = true;
			}else if(paidCapital.compareTo(paidCapitalSSM) == 0){
				changesInCapital = false;
			}
			if(getManagedBean().getSmAppl().getSmPersonnels() !=null && !changesInCapital){
				for(SmPersonnel personnel : getManagedBean().getSmAppl().getSmPersonnels()){
					if(personnel.isEquityOwner() && !personnel.isSsmData()){
						changesInCapital = true;
						break;
					}
					if(personnel.getShareAmt() != null && personnel.getShareAmtSsm() != null && personnel.getShareAmt().compareTo(personnel.getShareAmtSsm()) != 0){
						changesInCapital = true;
						break;
					}
				}
			}
		}
		if (rsDTO != null && rsDTO.getDocumentList() != null
				&& !rsDTO.getDocumentList().isEmpty()) {
			log.info("*** Not Empty list ****" + rsDTO.getDocumentList().size());
			List<SmApplSupportDoc> docs = new ArrayList<SmApplSupportDoc>();

			if (getManagedBean().getSmAppl().getSmApplSupportDocs().isEmpty()) {
				log.info(" ******* Empty Doc List *****");
				for (PmDocument doc : rsDTO.getDocumentList()) {					
					if(businessTypeIsROC(getManagedBean().getSmAppl().getSmSupplier().getBusinessType()) && getManagedBean().getSmAppl().isApplValidWithSsm()){						
						if(showDoc(doc,changesInCapital)){
							docs.add(SmApplicationUtil.getDefaultSmApplSupportingDoc(
									doc, getManagedBean().getSmAppl()));
							// to display or hide download column
							if (doc.isDownload()) {
								getManagedBean().setHasSuppDocToDownload(true);
							}
						}
					}else{
						//no ssm changes
						docs.add(SmApplicationUtil.getDefaultSmApplSupportingDoc(
								doc, getManagedBean().getSmAppl()));

						// to display or hide download column
						if (doc.isDownload()) {
							getManagedBean().setHasSuppDocToDownload(true);
						}
					}
					
				}
			} else {
				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress()
						.getPmState().getStateId()
						.equals(getManagedBean().getPrevBasicStateId())) {
					log.info(" ******* Existing doc list without change in state id  *****");
					for (PmDocument doc : rsDTO.getDocumentList()) {
						boolean recordExists = false;
						boolean showDoc = true;
						for (SmApplSupportDoc smDocu : getManagedBean()
								.getSmAppl().getSmApplSupportDocs()) {
							if (smDocu.getPmDocument().getDocumentId()
									.equals(doc.getDocumentId())) {

								if (EntityHelper.isCollectionEmpty(smDocu
										.getSmApplDocFiles())) {
									SmApplicationUtil
											.setDefaultSmApplDocFiles(smDocu);
								}
								//SSM INTEGRATION
								if(businessTypeIsROC(getManagedBean().getSmAppl().getSmSupplier().getBusinessType()) && getManagedBean().getSmAppl().isApplValidWithSsm()){
									showDoc = showDoc(smDocu.getPmDocument(),changesInCapital);
									if(showDoc){
										docs.add(smDocu);
										recordExists = true;										
									}else{
										smDocu.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
										recordExists = true;
									}
								}else{
									docs.add(smDocu);
									recordExists = true;
								}
								
							}
						}
						if (!recordExists) {
							SmApplSupportDoc suppDoc = SmApplicationUtil
									.getDefaultSmApplSupportingDoc(doc,
											getManagedBean().getSmAppl());
							if(businessTypeIsROC(getManagedBean().getSmAppl().getSmSupplier().getBusinessType()) && getManagedBean().getSmAppl().isApplValidWithSsm()){
								if(showDoc(suppDoc.getPmDocument(),changesInCapital)){
									docs.add(suppDoc);
								}else{
									suppDoc.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
								}
							}else{
								docs.add(suppDoc);
							}
							
						}
						if(showDoc){
							// to display or hide download column
							if (doc.isDownload()) {
								getManagedBean().setHasSuppDocToDownload(true);
							}
						}						
					}
					//SSM Changes 11012020
					//only SSM appl will reset doc with PM - Azida
					if(getManagedBean().getSmAppl().isApplValidWithSsm() || (!businessTypeIsROB(getManagedBean().getSmAppl().getSmSupplier().getBusinessType()) && !businessTypeIsROC(getManagedBean().getSmAppl().getSmSupplier().getBusinessType()))){
						//If resubmit no need to reset
						if(!getManagedBean().getSmAppl().isResubmit()){
							EntityHelper
							.compareAndRemoveEntityFromListIfNotExist(docs,
									getManagedBean().getSmAppl()
											.getSmApplSupportDocs(),
									"applSupportDocId");
						}						
					}
					
				} else {
					log.info(" ******* Existing doc list without CHANGE in state id  *****");
					for (PmDocument doc : rsDTO.getDocumentList()) {
						getManagedBean().getSmAppl().getSmApplSupportDocs()
								.clear();
						docs.add(SmApplicationUtil
								.getDefaultSmApplSupportingDoc(doc,
										getManagedBean().getSmAppl()));
						if (doc.isDownload()) {
							getManagedBean().setHasSuppDocToDownload(true);
						}
					}
				}
			}
//			if(!getManagedBean().getSmAppl().isResubmit()){ //#30556
				getManagedBean().getSmAppl().setSmApplSupportDocs(docs);
//			}

			// sort by document is mandatory or not
			Collections.sort(getManagedBean().getSmAppl()
					.getSmApplSupportDocs(),
					new Comparator<SmApplSupportDoc>() {
						@Override
						public int compare(final SmApplSupportDoc s1,
								final SmApplSupportDoc s2) {
							String str1 = "0";
							String str2 = "0";

							if (s1.getPmDocument() != null
									&& s1.getPmDocument().isMandatory()) {
								str1 = "1";
							}
							if (s2.getPmDocument() != null
									&& s2.getPmDocument().isMandatory()) {
								str2 = "1";
							}
							return (str2.compareTo(str1));
						}
					});

		} else {
			log.info("*** Empty Supporting doc list **** ");
		}
		log.info(" ** For Suporting Docs END ** ");
	}

	/**
	 * Sets the tab value for the navigation i.e. count of the tab to navigate.
	 * 
	 * @param currentTab
	 *            - int current tab value
	 * @param outputTab
	 *            - int output tab value
	 * @param buttonType
	 *            - String button type prev,next or same
	 * 
	 */
	public void setTabValue(int currentTab, int outputTab, String buttonType) {
		log.info(" ***** Button type ***** " + buttonType);
		this.buttonType = buttonType;
		setTabValue(currentTab, outputTab);
	}

	/**
	 * Sets the tab value for the navigation i.e. count of the tab to navigate.
	 * 
	 * @param currentTab
	 *            - int current tab value
	 * @param outputTab
	 *            - int output tab value
	 */
	public void setTabValue(int currentTab, int outputTab) {
		log.info("**** SET TAB CALLED *******");
		this.currentTab = currentTab;
		this.outputTab = outputTab;
	}

	/**
	 * Assign review application page navigation counts for PO and Approver
	 * final steps.
	 * 
	 * @param navType
	 *            - the nav type i.e. next or prev
	 */
	public void assignReviewSupportingNav(String navType) {
		int currentPage = getManagedBean().getTabActiveIndex();
		try {
			this.currentTab = currentPage;
			// getManagedBean().getSmAppl().setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
			if (navType.equalsIgnoreCase("next")) {
				this.outputTab = currentPage + 1;
			} else if (navType.equalsIgnoreCase("prev")) {
				this.outputTab = currentPage - 1;
			} else {
				this.outputTab = currentPage;
			}
		} catch (Exception e) {
			this.currentTab = currentPage;
			this.outputTab = currentPage;
		}
	}

	/**
	 * Ajax call to set the base or office facility selected i.e. owned or
	 * rented.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent object
	 */
	public void officeFacilitySelected(AjaxBehaviorEvent e) {
		if (getManagedBean().getBasicCompanyFacility().getSmAddress() == null
				|| StringUtils
						.isBlank(getManagedBean().getBasicCompanyFacility()
								.getSmAddress().getAddress1())) {
			getManagedBean().getBasicCompanyFacility().setSmAddress(
					new SmAddress());
			SmApplicationUtil.getInstance().cloneAddress(
					getManagedBean().getBasicCompanyFacility().getSmAddress(),
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses().get(0).getSmAddress());
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(),
					getManagedBean().getBasicCompanyFacility().getSmAddress(),
					SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL);
		}
		
		//#36483
		if(getManagedBean().getBasicCompanyFacility().getOwnershipType().equals(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)){
			SmApplicationUtil.getInstance().cloneAddress(
					getManagedBean().getBasicCompanyFacility().getSmAddress(),
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses().get(0).getSmAddress());
		}
		//#36483
				
		setFacilityDefaultPrices(getManagedBean().getBasicCompanyFacility());
		
		// 32064 - Start
		//initBusinessPremise(); 
		// 32064 - End
	}

	/**
	 * Sets the facility default prices.
	 * 
	 * @param fac
	 *            - SmFacility object
	 */
	private void setFacilityDefaultPrices(SmFacility fac) {
		if (fac.getRentalRate() == null) {
			fac.setRentalRate(new BigDecimal(0.00));
		}
		if (fac.getPurchasePrice() == null) {
			fac.setPurchasePrice(new BigDecimal(0.00));
		}
	}

	/**
	 * Ajax call to assign selected personnel privilege
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignShareHolderPrivilege(AjaxBehaviorEvent e) {
		SmApplicationUtil.assignShareHolderPrivilege(getManagedBean());
	}

	private int getCurrAuthPersonnelCount() {
		return SmApplicationUtil.getCurrAuthPersonnelCount(getManagedBean());
	}

	/**
	 * Ajax call to assign selected personnel privilege by ep role.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignShareHolderPrivilegeByEpRole(AjaxBehaviorEvent e) {
		SmApplicationUtil.setPersonnelRoleChange(getManagedBean().getSmAppl(),
				SmApplicationUtil.getPrevApprovedPersonnel(getManagedBean()
						.getSelectedShareHolder(), getManagedBean()
						.getPrevApprovedPersonnels()), getManagedBean()
						.getSelectedShareHolder());
		getManagedBean()
				.setDisableRoleSelection(
						disableEpRoleDropDown(getManagedBean()
								.getSelectedShareHolder()));

		getManagedBean().setDisableAuthorizedRadio(
				disableAuthorizedRadio(getManagedBean()
						.getSelectedShareHolder()));
	}

	/**
	 * Ajax call to set approve or reject date for the selected category.
	 * 
	 * @param category
	 *            - SmSupplierCategory
	 */
	public void assignCategoryApproveOrRejectDate(SmSupplierCategory category) {
		if (getManagedBean().isPoUser()) {
			log.info("***** PO DECISION SELECTED **** "
					+ category.getPoDecFlg());
		} else if (getManagedBean().isApproverUser()) {
			log.info("***** APPROVER DECISION SELECTED **** "
					+ category.getApprvrDecFlg());
			//#36587
			if(category.getPoDecFlg().equals(0)
					&& category.getApprvrDecFlg().equals(0)){
				category.setApproverRemarks(category.getPoRemarks());
			}else{
				category.setApproverRemarks(null);
			}
		}
		category.setApprovedDate(new Date());
		//#31043 start
		SmSideMenuIconUtil.getSideMenuVerificationIcon(getManagedBean(), 
				getManagedBean().getCurrentPageType());
		//#31043 end
	}

	public void assignCategoryReApprovalOnOtherDescChange(SmSupplierCategory cat) {
		if (getManagedBean().isSupplierUser()) {
			if (StringUtils.isNotEmpty(cat.getOtherDesc())
					&& cat.getApprovedDate() != null) {
				if (!cat.getOtherDesc().trim()
						.equalsIgnoreCase(cat.getPrevOtherDesc().trim())) {
					cat.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
					cat.setPoDecFlg(null);
					cat.setApprvrDecFlg(null);
				} else {
					cat.setRecordStatusFlg(SupplierManagementConst.APPROVED_RECORD_STATUS);
					cat.setPoDecFlg(cat.getPrevPoDecFlg());
					cat.setApprvrDecFlg(cat.getPrevApprDecFlg());
				}

			}
		}
	}

	/**
	 * Ajax call to set the application category selected.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void applicationCategorySelected(AjaxBehaviorEvent e) {
		SmApplicationUtil.setApplicationCategorySelected(getManagedBean());
	}

	/**
	 * Ajax call to Assign documents verified value.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignDocVerified(AjaxBehaviorEvent e) {
		log.info(" *******Docs verified******88");
	}

	/**
	 * Assign application category type based on selection on screen.
	 */
	public void assignApplnCategoryType() {
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		// Remove from active list
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getActiveCategoryList().removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (!cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getActiveCategoryList().removeAll(catList);
		}
		// Remove from main list
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (!cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		}

		// assign application category type for consultant
		assignApplnConsultantCategoryType();

		StringBuilder sb = new StringBuilder();
		String desc = null;
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			desc = SmCommonUtil.getMessage("lbl.sm.categorycode.note.normal",
					null);
		} else {
			desc = SmCommonUtil.getMessage("lbl.sm.categorycode.note.special",
					null);
		}
		sb.append(desc);
		getManagedBean().setCatTypeConfirm(sb.toString());
		log.info("***** After confirm +++++++"
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode(
					//28771
					StringUtils.EMPTY);
		}
		saveOrUpdateAppl();
	}

	private void assignApplnConsultantCategoryType() {
		List<SmSupplierCategory> toRemoveCategoryList = SmApplicationUtil
				.getToRemoveConsultantCategoriesByChangeCategorType(
						getManagedBean().getSmAppl(), getManagedBean()
								.getActiveCategoryList(), getManagedBean()
								.getPhysicalConsulting());
		getManagedBean().getActiveCategoryList()
				.removeAll(toRemoveCategoryList);
		getManagedBean().getSmAppl().getSmSupplierCategories()
				.removeAll(toRemoveCategoryList);
	}

	/**
	 * Reset application category type based on the selection to Normal or
	 * Special.
	 */
	public void resetApplnCategoryType() {
		log.info("********* Reset the Values ********");
		if (getManagedBean().getSmAppl().getApplCategoryType() != null
				&& getManagedBean().getSmAppl().getApplCategoryType()
						.equals("S")) {
			getManagedBean().getSmAppl().setApplCategoryType("N");
		} else {
			getManagedBean().getSmAppl().setApplCategoryType("S");
		}
		// getManagedBean().setCatTypeConfirm(null);
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode(
				//28771
				StringUtils.EMPTY);
		}
		RequestContext.getCurrentInstance().execute("typeConfirmation.hide()");
	}

	// -- Process Branches Start

	/**
	 * Copy hq gst info to branch
	 * 
	 * @param SmSupplierBranch
	 *            branch
	 */
	public void copyHqGstToBranch(SmSupplierBranch branch) {
		if (branch.getGst() == null) {
			branch.setGst(new SmGstSupplier());
			SmApplicationUtil.initGstSuppDocs(branch.getGst());
		}
		SmCommonUtil.copyHqGstToBranch(getManagedBean().getCompanyBasicGst(),
				branch.getGst(), branch.isSameAsHq(), branch.getBranchCode());
	}

	/**
	 * Delete GST
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public void deleteGst(SmGstSupplier gst) {
		gst.setGstRegNo(null);
		gst.setGstEffDate(null);
		gst.setGstEndDate(null);
		gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
		SmApplicationUtil.initGstSuppDocs(gst);
	}

	/**
	 * Add or update selected branch info to SmSupplierBranch list of the
	 * application.
	 */
	public void addOrUpdateBranchToSmAppl() {
		//#34135 START - set stateid from pmstate
		if(getManagedBean().getSelectedBranch().getSmAddress().getStateId() != null &&
				getManagedBean().getSelectedBranch().getSmAddress().getPmState() != null &&
				!getManagedBean().getSelectedBranch().getSmAddress().getStateId().equals(getManagedBean().getSelectedBranch().getSmAddress().getPmState().getStateId())){
			getManagedBean().getSelectedBranch().getSmAddress().setStateId(getManagedBean().getSelectedBranch().getSmAddress().getPmState().getStateId());
		}
		//#34135 END
		// validate branch GST only for New Application
		SmApplicationUtil.validateBranchGstInformation(getManagedBean()
				.getSelectedBranch(), getManagedBean().getSelectedBranch()
				.getGst(), getManagedBean().getCompanyBasicGst());
		
		SmCommonUtil.getInstance().setUserDetails(getManagedBean()
				.getSelectedBranch());
		//#34135 START
		if(getManagedBean().getSelectedBranch().getSmAddress().getPmDivision() != null &&
				!getManagedBean().getSelectedBranch().getSmAddress().getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
			getManagedBean().getSelectedBranch().getSmAddress().setPmDivision(null);
			this.getManagedBean().setBranchCompanyDivisions(new ArrayList<SelectItem>());
		}
		//#34135 END
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedBranch(), getManagedBean().getSmAppl()
				.getSmSupplierBranches());
		if (getManagedBean().getSelectedBranch().getSupplierBranchId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppBrnachDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		assignValReqdToFalse();
		setBranchDisplayTable(); //29727 , #34135
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
		calcPersonnelListCount();
	}

	/**
	 * Assign selected branch object to managed bean for editing the
	 * information.
	 * 
	 * @param branchBean
	 *            - SmSupplierBranch object
	 * @param index
	 *            - int
	 */
	public void assignSelBranch(SmSupplierBranch branchBean, int index) {

		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), branchBean.getSmAddress(),
				SupplierManagementConst.SM_BRANCH_PANEL);
		if (branchBean.getSmPersonnel() == null) {
			branchBean.setSmPersonnel(new SmPersonnel());
		}
		if (branchBean.getGst() == null) {
			branchBean.setGst(new SmGstSupplier());
			branchBean.getGst().setSmGstSupplierDocs(
					new ArrayList<SmGstSupplierDoc>());
		}
		SmApplicationUtil.initGstSuppDocs(branchBean.getGst());
		getManagedBean().setSelectedBranch(
				(SmSupplierBranch) SerializationUtils.clone(branchBean));
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestBranch(
					(SmSupplierBranch) EntityHelper
							.getEntityByMultiplePropertiesFromList(branchBean,
									getManagedBean().getLatestAppl()
											.getSmSupplierBranches(),
									new String[] { "branchCode" }, null, null));
		}
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");

	}

	/**
	 * Add or create a new SmSupplierBranch object
	 */
	public void assignNewBranchBean() {
		SmSupplierBranch br = new SmSupplierBranch();
		br.setRevNo(1);
		br.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		br.setSmAppl(getManagedBean().getSmAppl());
		br.setSmPersonnel(new SmPersonnel());
		getManagedBean().setBranchCompanyStates(
				SmCommonUtil.getStateList(getManagedBean().getSelectedBranch()
						.getSmAddress().getPmCountry().getCountryId()));
		getManagedBean().setBranchCompanyDistricts(new ArrayList<SelectItem>());
		getManagedBean().setBranchCompanyCities(new ArrayList<SelectItem>());
		getManagedBean().setSelectedBranch(br);
		copyHqGstToBranch(getManagedBean().getSelectedBranch());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");
	}

	/**
	 * Cancel add or update functionality and close the supplier branch
	 * information dialog
	 */
	public void assignCancelBranchBean() {
		if (getManagedBean().getSelectedBranch() != null
				&& getManagedBean().getSelectedBranch().getGst() != null) {
			getManagedBean().getSelectedBranch().setGst(null);
		}
		assignValReqdToFalse();
		initGst();
		RequestContext.getCurrentInstance().reset(":form:tt:banchDialog");
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
	}

	/**
	 * Delete or remove selected branches from the SmSupplierBranch list of the
	 * application.
	 */
	public void deleteSelectedBranches() {
		// validate if deleted branch been used in bank section
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean()
				.getSelectedBranches())) {
			for (SmSupplierBranch brTmp : getManagedBean()
					.getSelectedBranches()) {
				if (SmApplicationUtil.isBranchBankAssoc(brTmp, getManagedBean()
						.getSmAppl().getSmSupplierBanks())) {
					throw new EpException(
							MessageConst.ERROR_722617,
							new String[] {
									brTmp.getBranchName(),
									SmApplicationUtil
											.getRelatedBankNameByBranch(
													getManagedBean()
															.getSmAppl()
															.getSmSupplierBanks(),
													brTmp) });
				}
			}
		}
		if (getManagedBean().getSelectedBranches().size() > 0) {
			for (SmSupplierBranch brn : getManagedBean().getSelectedBranches()) {
				getManagedBean().getSmAppl().getSmSupplierBranches()
						.remove(brn);
			}
			getManagedBean().getSelectedBranches().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage("lbl.sm.branch",
							null) });
		}
		saveOrUpdateAppl();
	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	/**
	 * Add or update selected associated company info to SmAssociateCompany list
	 * of the application.
	 */
	public void addOrUpdateAssocToSmAppl() {
		SmCommonUtil.getInstance().setUserDetails(getManagedBean()
				.getSelectedAssocCompany());
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedAssocCompany(), getManagedBean().getSmAppl()
				.getSmAssociateCompanies());
		if (getManagedBean().getSelectedAssocCompany().getAssociateCompanyId() == null) {
			RequestContext.getCurrentInstance().execute(
					"assocDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("assocDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the associated company
	 * information dialog
	 */
	public void cancelAssocCompanyFuntion() {
		assignValReqdToFalse();
		if (getManagedBean().getSelectedAssocCompany() != null
				&& getManagedBean().getSelectedAssocCompany().getSmAddress() != null
				&& getManagedBean().getSelectedAssocCompany().getSmAddress()
						.getAddressId() != null) {
			getManagedBean().getSelectedAssocCompany().setSmAddress(
					SmCommonUtil.getInstance().getSmAddressById(
							getManagedBean().getSelectedAssocCompany()
									.getSmAddress().getAddressId()));
		}
		RequestContext.getCurrentInstance().reset(":form:tt:asDisp");
		RequestContext.getCurrentInstance().execute("assocDialog.hide()");
	}

	/**
	 * Assign selected associated company object to managed bean for editing the
	 * information.
	 * 
	 * @param assocBean
	 *            -SmAssociateCompany object
	 */
	public void assignSelAssoc(SmAssociateCompany assocBean) {
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), assocBean.getSmAddress(),
				SupplierManagementConst.SM_ASSOC_COMPANY_PANEL);
		getManagedBean().setSelectedAssocCompany(
				(SmAssociateCompany) SerializationUtils.clone(assocBean));
		getManagedBean().getSelectedAssocCompany().setIsShareholding(
				new BigDecimal(0));
		assignValReqdToTrue();
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestAssocCompany(
					(SmAssociateCompany) EntityHelper
							.getEntityByMultiplePropertiesFromList(assocBean,
									getManagedBean().getLatestAppl()
											.getSmAssociateCompanies(),
									new String[] { "regNo" }, null, null));
		}
		RequestContext.getCurrentInstance().execute("assocDialog.show()");
	}

	/**
	 * Add or create a new SmAssociateCompany object
	 */
	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmAssociateCompany());
		getManagedBean().getSelectedAssocCompany().setRevNo(1);
		getManagedBean().getSelectedAssocCompany().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(null)));
		getManagedBean().getSelectedAssocCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setAssocCompanyStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedAssocCompany().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setAssocCompanyDistricts(new ArrayList<SelectItem>());
		getManagedBean().setAssocCompanyCities(new ArrayList<SelectItem>());
		getManagedBean().getSelectedAssocCompany().setIsShareholding(
				new BigDecimal(0));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("assocDialog.show()");
	}

	/**
	 * Delete or remove selected assocaiated companies from the
	 * SmAssociateCompany list of the application.
	 */
	public void deleteSelectedAssocCompanies() {
		log.info("*** Delete Assoc Companies ******* "
				+ getManagedBean().getSelectedAssocCompanies().size());
		if (getManagedBean().getSelectedAssocCompanies().size() > 0) {
			for (SmAssociateCompany assoc : getManagedBean()
					.getSelectedAssocCompanies()) {
				getManagedBean().getSmAppl().getSmAssociateCompanies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocCompanies().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Assocatied Company" });
		}
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	/**
	 * Add or update selected certificate info to SmSupplierCert list of the
	 * application.
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		getManagedBean().getSelectedCert().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSelectedCert().getCertTypeId() == null) {
			getManagedBean().getSelectedCert().setCertTypeId(
					new BigDecimal(getManagedBean().getGlp().getPmParameter()
							.getParameterId()));
		}
		if (getManagedBean().getSelectedCert().getCertEndDate() != null
				&& getManagedBean()
						.getSelectedCert()
						.getCertStartDate()
						.compareTo(
								getManagedBean().getSelectedCert()
										.getCertEndDate()) >= 0) {
			throw new EpException(MessageConst.ERROR_722530);
		}
		SmCommonUtil.getInstance().setUserDetails(getManagedBean()
				.getSelectedCert());
		EntityHelper.addOrUpdateEntityToList(
				getManagedBean().getSelectedCert(), getManagedBean()
						.getSmAppl().getSmSupplierCerts());
		if (getManagedBean().getSelectedCert().getSupplierCertId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppCertDtWidVar.paginator.endLink.click()");
		}
		if (getHq() != null) {
			saveOrUpdateAppl();
		}
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("certDialog.hide()");
	}

	/**
	 * Assign selected supplier cert object to managed bean for editing the
	 * information.
	 * 
	 * @param certBean
	 *            - SmSupplierCert object
	 */
	public void assignSelCert(SmSupplierCert certBean) {
		if (getHq() != null) {
			getManagedBean().setSelectedCert(
					(SmSupplierCert) SerializationUtils.clone(certBean));
		} else {
			getManagedBean().setSelectedCert(certBean);
		}
		assignValReqdToTrue();
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestCert(
					(SmSupplierCert) EntityHelper
							.getEntityByMultiplePropertiesFromList(certBean,
									getManagedBean().getLatestAppl()
											.getSmSupplierCerts(),
									new String[] { "certTypeId", "certName" },
									new String[] { "pmCertBody" }, null));
		}
		RequestContext.getCurrentInstance().execute("certDialog.show()");
	}

	/**
	 * set cert name when on change selection cert body
	 */
	public void onChangeCertBody() {
		getManagedBean().getSelectedCert().setCertName(
				SmCommonUtil.getMessage("lbl.txt.na", null));
		if (getManagedBean().getSelectedCert() != null
				&& getManagedBean().getSelectedCert().getPmCertBody() != null) {
			PmCertBody certBody = (PmCertBody) EntityHelper
					.getEntityBySinglePropertyFromList(getManagedBean()
							.getSelectedCert().getPmCertBody().getCertBodyId(),
							getManagedBean().getCertBodyLs(), "certBodyId");
			if (certBody != null) {
				getManagedBean().getSelectedCert().setCertName(
						certBody.getDescription());
			}
		}

	}

	/**
	 * Cancel add or update functionality and close the supplier cert
	 * information dialog
	 */
	public void cancelSupplierCertAction() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:certPop");
		RequestContext.getCurrentInstance().execute("certDialog.hide()");
	}

	/**
	 * Add or create a new SmSupplierCert object
	 */
	public void assignNewCert() {
		SmSupplierCert cert = new SmSupplierCert();
		cert.setPmCertBody(new PmCertBody());
		cert.setRevNo(1);
		getManagedBean().setSelectedCert(cert);
		cert.setSmAppl(getManagedBean().getSmAppl());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("certDialog.show()");
	}

	/**
	 * Delete or remove selected supplier certificates from the SmSupplierCert
	 * list of the application.
	 */
	public void deleteSelectedCerts() {
		if (getManagedBean().getSelectedCertList().size() > 0) {
			for (SmSupplierCert cert : getManagedBean().getSelectedCertList()) {
				getManagedBean().getSmAppl().getSmSupplierCerts().remove(cert);
			}
			getManagedBean().getSelectedCertList().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Supplier Certificate" });
		}
	}

	// -- Process Certss End
	// -- Process Assoc Bodies Start
	/**
	 * Add or update selected professional body info to SmAssociationBody List
	 * of the application.
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateAssocBodiesToSmAppl() throws ParseException {
		getManagedBean().getSelectedAssocBody().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSelectedAssocBody().getPmCertBody()
				.getCertBodyId() != null) {
			for (PmCertBody cert : getManagedBean().getProfBodyLs()) {
				if (getManagedBean().getSelectedAssocBody().getPmCertBody()
						.getCertBodyId().equals(cert.getCertBodyId())) {
					getManagedBean().getSelectedAssocBody().setPmCertBody(cert);
					break;
				}
			}

		}
		if (getManagedBean().getSmAppl().getSmAssociationBodies().contains(getManagedBean().getSelectedAssocBody())) {

		} else {
			getManagedBean().getSmAppl().getSmAssociationBodies().add(getManagedBean().getSelectedAssocBody());
			RequestContext.getCurrentInstance().execute("suppProfBodyDtWidVar.paginator.endLink.click()");
		}
		SmApplicationUtil.assignNonPersonnelProfBodyList(getManagedBean());
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("profDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the professional body
	 * information dialog
	 */
	public void cancelSelProfBody() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:profPop");
		RequestContext.getCurrentInstance().execute("profDialog.hide()");
	}

	/**
	 * Assign selected professional body object to managed bean for editing the
	 * information.
	 * 
	 * @param assocBean
	 *            - SmAssociationBody object
	 */
	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("profDialog.show()");
	}

	/**
	 * Add or create a new SmAssociationBody object
	 */
	public void assignNewAssocBody() {
		SmAssociationBody assocBean = new SmAssociationBody();
		assocBean.setPmCertBody(new PmCertBody());
		assocBean.setRevNo(1);
		getManagedBean().setSelectedAssocBody(assocBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("profDialog.show()");
	}

	/**
	 * Delete or remove selected professional bodies from the SmAssociationBody
	 * list of the application.
	 */
	public void deleteSelectedAssocBodies() {

		log.info("*** Delete AssocBodies ******* "
				+ getManagedBean().getSelectedAssocBodyList().size());
		if (getManagedBean().getSelectedAssocBodyList().size() > 0) {
			if (getManagedBean().getSelectedAssocBodyList().size() == getManagedBean()
					.getSmAppl().getSmAssociationBodies().size()) {
				throw new EpException(MessageConst.ERROR_722597);
			}
			for (SmAssociationBody assoc : getManagedBean()
					.getSelectedAssocBodyList()) {
				getManagedBean().getSmAppl().getSmAssociationBodies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocBodyList().clear();
			SmApplicationUtil.assignNonPersonnelProfBodyList(getManagedBean());
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Professional Body" });
		}

	}

	// -- Process Assoc Bodies End

	// -- Process Consultant or Contractor Company Experience Start
	/**
	 * Add or update selected project info to SmCompanyExperience list of the
	 * application.
	 */
	public void addOrUpdateCompanyExperienceToSmAppl() {
		if (EntityHelper.isCollectionEmpty(getManagedBean()
				.getSelectedCompanyExperience().getSmCompanyExperienceCats())
				&& (getManagedBean().getSelectedCompanyExperience()
						.getContractValue() != null && getManagedBean()
						.getSelectedCompanyExperience().getContractValue()
						.compareTo(new BigDecimal(20000)) >= 1)) {
			throw new EpException(MessageConst.ERROR_722550);
		}
		SmCommonUtil.getInstance().setUserDetails(getManagedBean()
				.getSelectedCompanyExperience());
		//#28175
		if(getManagedBean().getSmAppl().isActiveAppl()){
			updateCompanyExpCatNames(getManagedBean().getSelectedCompanyExperience());
		}
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedCompanyExperience(), getManagedBean().getSmAppl()
				.getSmCompanyExperiences());
		if (getManagedBean().getSelectedCompanyExperience()
				.getCompanyExperienceId() == null) {
			if (getManagedBean().getSmAppl().getSupplierType()
					.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
				RequestContext.getCurrentInstance().execute(
						"suppCnstlExpDtWidVar.paginator.endLink.click()");
			} else {
				RequestContext.getCurrentInstance().execute(
						"suppContrExpDtWidVar.paginator.endLink.click()");
			}
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.hide()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.hide()");
		}
	}

	/**
	 * Cancel add or update functionality and close the company experience
	 * information dialog
	 */
	public void cancelCompExpFunctions() {
		assignValReqdToFalse();
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().reset(":form:tt:consDialog");
			RequestContext.getCurrentInstance().execute("consDialog.hide()");
		} else {

			RequestContext.getCurrentInstance().reset(":form:tt:contDialog");
			RequestContext.getCurrentInstance().execute("contDialog.hide()");

		}
	}

	/**
	 * Assign selected company experience or project object to managed bean for
	 * editing the information.
	 * 
	 * @param consultantBean
	 *            - SmCompanyExperience object
	 */
	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		assignValReqdToTrue();
		if (consultantBean.getConsultantFee() == null) {
			consultantBean.setConsultantFee(new BigDecimal(0));
		}
		if (consultantBean.getContractValue() == null) {
			consultantBean.setContractValue(new BigDecimal(0));
		}
		getManagedBean()
				.setSupplierParentCatList(
						SmApplicationUtil
								.retrieveParentCategoryListForExperiencePage(getManagedBean()));
		if (consultantBean.getSmCompanyExperienceCats() != null
				&& !consultantBean.getSmCompanyExperienceCats().isEmpty()) {
			for (SmCompanyExperienceCat cat : consultantBean
					.getSmCompanyExperienceCats()) {
				populateExperienceChildCategoryByParent(cat.getPmCategoryL1()
						.getCategoryL1Id(), cat.getPmCategoryL2(),
						cat.getPmCategoryL3());
				populateExperienceSubChildCategoryByChild(cat.getPmCategoryL2()
						.getCategoryL2Id(), cat.getPmCategoryL3());
			}

		}
		getManagedBean().setSelectedCompanyExperience(
				(SmCompanyExperience) SerializationUtils.clone(consultantBean));
		SmFieldChangeUtil.initCompanyExperienceChangeField(getManagedBean());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.show()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.show()");
		}
	}

	/**
	 * Assign new category object for select company experience or project.
	 * 
	 * @param exp
	 *            - SmCompanyExperience object
	 */
	public void assignCategoryForCompExperience(SmCompanyExperience exp) {
		getManagedBean().setSelectedCompanyExperience(exp);
		SmCompanyExperienceCat cat = new SmCompanyExperienceCat();
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		cat.setSmCompanyExperience(getManagedBean()
				.getSelectedCompanyExperience());
		if (EntityHelper.isCollectionEmpty(getManagedBean()
				.getSelectedCompanyExperience().getSmCompanyExperienceCats())) {
			getManagedBean().getSelectedCompanyExperience()
					.setSmCompanyExperienceCats(
							new ArrayList<SmCompanyExperienceCat>());
		}
		getManagedBean().getSelectedCompanyExperience()
				.getSmCompanyExperienceCats().add(cat);
		getManagedBean().setSelected(false);
		getManagedBean().setSelectedAll(false);

	}

	/**
	 * Check for duplicate category in the same project.
	 * 
	 * @param expCat
	 *            - SmCompanyExperienceCat object
	 * @param exp
	 *            - SmCompanyExperience object
	 */
	public void checkDuplicateCategoryForProject(SmCompanyExperienceCat expCat,
			SmCompanyExperience exp) {
		log.info("**** Validate the category Codes ********** ");
		for (SmCompanyExperienceCat cat : exp.getSmCompanyExperienceCats()) {
			if (expCat.equals(cat)) {
				log.info(" *** Same Obj ****"
						+ cat.getPmCategoryL1().getCategoryL1Code() + " -- --"
						+ expCat.getPmCategoryL1().getCategoryL1Code());

			} else if (cat.getPmCategoryL1().getCategoryL1Id()
					.equals(expCat.getPmCategoryL1().getCategoryL1Id())
					&& cat.getPmCategoryL2().getCategoryL2Id()
							.equals(expCat.getPmCategoryL2().getCategoryL2Id())
					&& cat.getPmCategoryL3().getCategoryL3Id()
							.equals(expCat.getPmCategoryL3().getCategoryL3Id())) {
				expCat.getPmCategoryL3().setCategoryL3Id(null);
				throw new EpException(MessageConst.ERROR_722533);
			}
		}

	}

	/**
	 * Delete selected category from company experience object.
	 * 
	 * @param exp
	 *            -SmCompanyExperience object
	 */
	public void deleteSelectedCategoryFromCompanyExperience(
			SmCompanyExperience exp) {
		if (getManagedBean().getSelectedCompExpCats().size() == exp
				.getSmCompanyExperienceCats().size()
				&& (getManagedBean().getSelectedCompanyExperience()
						.getContractValue() != null && getManagedBean()
						.getSelectedCompanyExperience().getContractValue()
						.compareTo(new BigDecimal(20000)) >= 0)) {
			throw new EpException(MessageConst.ERROR_722549);
		}
		if (getManagedBean().getSelectedCompExpCats().size() > 0) {
			for (SmCompanyExperienceCat cat : getManagedBean()
					.getSelectedCompExpCats()) {
				exp.getSmCompanyExperienceCats().remove(cat);
			}
			getManagedBean().getSelectedCompExpCats().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Category" });
		}
		getManagedBean().setSelected(false);
		getManagedBean().setSelectedAll(false);
	}

	/**
	 * Add or create a new SmCompanyExperience object
	 */
	public void assignNewCompanyExperience() {
		log.info("***** Assign New Company Exp ***********");
		SmCompanyExperience exp = new SmCompanyExperience();
		exp.setRevNo(1);
		exp.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		exp.setSmAppl(getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			exp.setProcurementModeId(new Integer(0));
		}
		exp.setConsultantFee(new BigDecimal(0));
		exp.setContractValue(new BigDecimal(0));

		// List<SmCompanyExperienceCat> expCatList = new
		// ArrayList<SmCompanyExperienceCat>();
		// SmCompanyExperienceCat cat = new SmCompanyExperienceCat();
		// cat.setPmCategoryL1(new PmCategoryL1());
		// PmCategoryL2 catL2 = new PmCategoryL2();
		// catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		// cat.setPmCategoryL2(catL2);
		// PmCategoryL3 catL3 = new PmCategoryL3();
		// catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		// cat.setPmCategoryL3(catL3);
		// cat.setSmCompanyExperience(exp);
		// expCatList.add(cat);
		// exp.setSmCompanyExperienceCats(expCatList);

		getManagedBean().setSelectedCompanyExperience(exp);
		getManagedBean()
				.setSupplierParentCatList(
						SmApplicationUtil
								.retrieveParentCategoryListForExperiencePage(getManagedBean()));
		resetChildCatList();
		assignValReqdToTrue();
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.show()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.show()");
		}

	}

	/**
	 * Delete or remove selected company projects from the SmCompanyExperience
	 * list of the application.
	 */
	public void deleteSelectedCompanyExperiences() {
		if (getManagedBean().getSelectedCompanyExperiences().size() > 0) {
			for (SmCompanyExperience exp : getManagedBean()
					.getSelectedCompanyExperiences()) {
				getManagedBean().getSmAppl().getSmCompanyExperiences()
						.remove(exp);
			}
			getManagedBean().getSelectedCompanyExperiences().clear();
		} else {
			log.info(" ******** NO Record selected please select atleast one ");
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Company Project" });
		}

	}

	// ---- Process Consultant or Contractor Company Experience End

	// -- Process Equity Companies Start
	/**
	 * Add or update selected share holding company info to SmEquityCompany list
	 * of the application.
	 */
	public void addOrUpdateEquityCompanyToSmAppl() {
		SmCommonUtil.getInstance().setUserDetails(getManagedBean()
				.getSelectedEquityCompany());
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedEquityCompany(), getManagedBean().getSmAppl()
				.getSmEquityCompanies());
		if (getManagedBean().getSelectedEquityCompany().getEquityCompanyId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppEquiCompDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		calculateCapital();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.hide()");
	}

	/**
	 * Assign selected smEquityCompany object to managed bean for editing the
	 * information.
	 * 
	 * @param equiCompBean
	 *            -SmEquityCompany object
	 */
	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		if (equiCompBean.getShareAmt() == null) {
			equiCompBean.setShareAmt(new BigDecimal(0));
		}
		validatePaidCaptialAmt(false);
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), equiCompBean.getSmAddress(),
				SupplierManagementConst.SM_EQUI_COMPANY_PANEL);
		getManagedBean().setSelectedEquityCompany(
				(SmEquityCompany) SerializationUtils.clone(equiCompBean));
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestEquityCompany(
					(SmEquityCompany) EntityHelper
							.getEntityByMultiplePropertiesFromList(
									equiCompBean, getManagedBean()
											.getLatestAppl()
											.getSmEquityCompanies(),
									new String[] { "regNo" }, null, null));
		}
		assignValReqdToTrue();
		// #30660 start - fix reopen
		if (getManagedBean().isDisAllowOwnerEdit()) {
			RequestContext.getCurrentInstance().execute("viewEqiCompDialog.show()");
		} else {
			RequestContext.getCurrentInstance().execute("eqiCompDialog.show()");
		}
		// #30660 end
		
		//#27308 - 13/03/2020 START
		// #28466 start - remove #27308 showstopper
//		if(getManagedBean().getSelectedEquityCompany().isSsmData()  
//				&& (businessTypeIsROC(getManagedBean().getSmSupplier().getBusinessType()) 
//				|| getManagedBean().getSelectedEquityCompany().getShareAmt() != null 
//				|| getManagedBean().getSelectedEquityCompany().getShareAmt().compareTo(BigDecimal.ZERO) == 0)){
//			throw new EpException(MessageConst.ERROR_722680);
//		}
		// #28466 end - remove #27308 showstopper
		//#27308 - 13/03/2020 END
	}

	/**
	 * Add or create a new SmEquityCompany object
	 */
	public void assignNewEquiCompBean() {
		validatePaidCaptialAmt(false);
		getManagedBean().setSelectedEquityCompany(new SmEquityCompany());
		getManagedBean().getSelectedEquityCompany().setRevNo(1);
		getManagedBean().getSelectedEquityCompany().setShareAmt(
				new BigDecimal(0));
		getManagedBean().getSelectedEquityCompany().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedEquityCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setEquiCompanyStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedEquityCompany().getSmAddress()
						.getPmCountry().getCountryId()));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.show()");
	}

	/**
	 * Delete or remove selected share holding or equity companies from the
	 * SmEquityCompany list of the application.
	 */
	public void deleteSelectedEquiCompanies() {

		if (getManagedBean().getSelectedEquityCompanies().size() > 0) {
			validatePaidCaptialAmt(false);
			for (SmEquityCompany equi : getManagedBean()
					.getSelectedEquityCompanies()) {
				getManagedBean().getSmAppl().getSmEquityCompanies()
						.remove(equi);
			}
			getManagedBean().getSelectedEquityCompanies().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Share Holding Company" });
		}

	}

	/**
	 * Cancel add or update functionality and close the share holding company
	 * information dialog
	 */
	public void cancelEquiCompFuntionc() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:eqiCompDialog");
		RequestContext.getCurrentInstance().execute("eqiCompDialog.hide()");
	}

	// -- Process Equity Companies End

	// -- Process Category Codes Start

	/**
	 * Ajax action performed when parent is changed on the screen i.e. reset
	 * child and sub child category values
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent object
	 */
	public void changeParentCategory(AjaxBehaviorEvent e) {
		String selPmCatL1 = getManagedBean().getSelectedParentCat();
		if (selPmCatL1 == null) {
			PmCategoryL1 p = new PmCategoryL1();
			p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
			getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		} else {
			for (PmCategoryL1 p : getManagedBean().getDefaultParentCatList()) {
				if (p.getCategoryL1Code().equalsIgnoreCase(selPmCatL1)) {
					if (p.getPmCategoryL2s() == null) {
						p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL1(p);

				}
			}
		}
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(
				new PmCategoryL2());
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedChildCat(null);
		getManagedBean().setSelectedSubChildCat(null);

	}

	/**
	 * Ajax action performed when child category is changed on the screen i.e.
	 * reset sub child category values
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent object
	 */
	public void changeChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL2 = getManagedBean().getSelectedChildCat();
		if (selPmCatL2 == null) {
			PmCategoryL2 p2 = new PmCategoryL2();
			p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
			getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		} else {
			for (PmCategoryL2 p2 : getManagedBean().getSelectedCategory()
					.getPmCategoryL1().getPmCategoryL2s()) {
				if (p2.getCategoryL2Code().equalsIgnoreCase(selPmCatL2)) {
					if (p2.getPmCategoryL3s() == null) {
						p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL2(p2);

				}
			}
		}
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedSubChildCat(null);

	}

	/**
	 * Resetting all the existing values.
	 */
	private void resetSelectedCategoryParams() {
		PmCategoryL1 p = new PmCategoryL1();
		p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	}

	/**
	 * Add or create a new SmSupplierCategory object.
	 */
	public void assignNewCategoryCode() {
		boolean validCatCode = true;
		if (getManagedBean().getSmAppl().getSmSupplierCategories().size() == ruleHelper
				.getMaxCategoryAllowed()) {
			validCatCode = false;
			throw new EpException(MessageConst.ERROR_722517,
					new String[] { ruleHelper.getMaxCategoryAllowed() + "" });
		}

		if (validCatCode) {
			SmSupplierCategory categ = new SmSupplierCategory();
			categ.setRevNo(1);
			categ.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
			if (getManagedBean().getSmAppl().getApplCategoryType()
					.equalsIgnoreCase("S")) {
				categ.setSpecialCategory(true);
			}
			categ.setSmAppl(getManagedBean().getSmAppl());
			categ.setSupplierType(getManagedBean().getSmAppl()
					.getSupplierType());
			getManagedBean().setSelectedCategory(categ);
			getManagedBean()
					.setSupplierParentCatList(
							SmApplicationUtil
									.retrieveParentCategoryListForCategoryCodesPage(getManagedBean()));
			getManagedBean().setSupplierChildCatList(
					new ArrayList<PmCategoryL2>());
			this.getManagedBean().setSupplierSubChildCatList(
					new ArrayList<PmCategoryL3>());
			resetSelectedCategoryParams();
			assignValReqdToTrue();
			RequestContext.getCurrentInstance().execute("regCatDialog.show()");
		}
	}

	/**
	 * Cancel save or update functionality and close the category codes
	 * information dialog
	 */
	public void cancelCategoryCodesSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:regCatDialog");
		RequestContext.getCurrentInstance().execute("regCatDialog.hide()");
	}

	/**
	 * Action to Approve all category codes by PO or Approver.
	 */
	public void approveAllCategoryCodesByPOorApprover() {
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getActiveCategoryList())) {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (getManagedBean().isPoUser()) {
					cat.setPoDecFlg(1);
				} else if (getManagedBean().isApproverUser()) {
					cat.setApprvrDecFlg(1);
					cat.setApproverRemarks(null); //36587
				}
			}
		}
		if (getManagedBean().getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				&& !EntityHelper.isCollectionEmpty(getManagedBean()
						.getApprovedCategoryList())) {
			for (SmSupplierCategory cat : getManagedBean()
					.getApprovedCategoryList()) {
				if (getManagedBean().isPoUser()) {
					cat.setPoDecFlg(1);
				} else if (getManagedBean().isApproverUser()) {
					cat.setApprvrDecFlg(1);
					cat.setApproverRemarks(null); //36587
				}
			}
		}
	}

	/**
	 * Action to Approve all supporting document by PO or Approver.
	 */
	public void approveAllSuppDocByPOorApprover() {
		if (!SmCommonUtil
				.isCollectionEmpty(getManagedBean().getMandatoryDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getMandatoryDocs()) {
				//#31043 start
				if(doc.isReceived()){
					if (getManagedBean().isPoUser()) {
						doc.setVerifiedFlg(1);
					}
					if (getManagedBean().isApproverUser()) {
						doc.setApproverVerifiedFlg(1);
					}
				}
				//#31043 end
			}

			if (!EntityHelper.isCollectionEmpty(getManagedBean()
					.getMandatoryLicenseDocs())) {
				for (SmLicenseInfoVO vo : getManagedBean()
						.getMandatoryLicenseDocs()) {
					if (!EntityHelper
							.isCollectionEmpty(vo.getLicenseSuppDocs())) {
						for (SmApplSupportDoc doc : vo.getLicenseSuppDocs()) {
							//#31043 start
							if(doc.isReceived()){
								/*if (getManagedBean().isPoUser()) {*/
									doc.setVerifiedFlg(1);
								/*}
								if (getManagedBean().isApproverUser()) {
									doc.setApproverVerifiedFlg(1);
								}*/
							}
							//#31043 end
						}
					}
				}
			}
		}
		
		//#31043 start
		if (!SmCommonUtil
				.isCollectionEmpty(getManagedBean().getNonMandatoryDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getNonMandatoryDocs()) {
				//#31043 start
				if(doc.isReceived()){
					if (getManagedBean().isPoUser()) {
						doc.setVerifiedFlg(1);
					}
					if (getManagedBean().isApproverUser()) {
						doc.setApproverVerifiedFlg(1);
					}
				}//#31043 end
			}
		}
		//#31043 end
	}

	public void deleteSelectedExistingCatCodes() {
		if (EntityHelper.isCollectionEmpty(getManagedBean()
				.getDeleteExistingCategoryList())) {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.catCodes.catcode", null) });
		} else {
			getManagedBean().setDeleteCategoryList(
					new ArrayList<SmSupplierCategory>());
			for (SmSupplierCategory cat : getManagedBean()
					.getDeleteExistingCategoryList()) {
				getManagedBean().getDeleteCategoryList().add(cat);
			}
			deleteSelectedCategoryCodes();
		}
	}

	/**
	 * Delete selected category codes from the SmSupplierCategory list of the
	 * application.
	 */
	public void deleteSelectedCategoryCodes() {
		SmApplicationUtil.checkCategoryPendingTransaction(getManagedBean()
				.getDeleteCategoryList(), getManagedBean().getSmAppl()
				.getSmSupplier().getSupplierId());
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getDeleteCategoryList())) {
			List<SmSupplierCategory> categories = getManagedBean()
					.getDeleteCategoryList();
			deleteCatCodes(categories);
			// API FOR LIST OF OVERLAPPING CATEGORIES
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.catCodes.catcode", null) });
		}
	}

	/**
	 * Delete category codes.
	 * 
	 * @param categories
	 *            - List of categories
	 */
	private void deleteCatCodes(List<SmSupplierCategory> categories) {
		for (SmSupplierCategory category : categories) {
			if (category.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0
				//#29541 --START
					&& getManagedBean().isSupplierUser()) {
				//#29541 --END
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.remove(category);
			} else if (category.getRecordStatusFlg().compareTo(        
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0
					|| category.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL
				//#29541 --START
					|| (category.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_ACT_STATUS) == 0)
					&& !getManagedBean().isSupplierUser()) {
				//#29541 --END
				category.setRecordStatusFlg(SupplierManagementConst.RECORD_STATUS_DELETED);
			}
		}
		// #31018 check overlap category
		int overlapCount = 0;
		for (SmSupplierCategory smSupplierCategory : getManagedBean().getSmAppl().getSmSupplierCategories()) {
			if (smSupplierCategory.isOverLapCategory()) {
				overlapCount++;
			}
		}

		if (overlapCount == 0) {
			getManagedBean().getSmAppl().setSmCategoryOverlapDocs(null);
		}
		// #31018 END
		saveOrUpdateAppl();
		SmApplicationUtil.assignActiveAndApprovedCategoryList(getManagedBean());
	}

	/**
	 * Delete selected category code(s) that are still pending approval from the
	 * SmSupplierCategory list of the application.
	 */
	public void deleteSelectedPendingApprovalCategoryCodes() {
		log.info("Start deleteSelectedPendingApprovalCategoryCodes");
		if (getManagedBean().getDeleteNewApplCategoryList() != null
				&& getManagedBean().getDeleteNewApplCategoryList().size() > 0) {
			List<SmSupplierCategory> categories = getManagedBean()
					.getDeleteNewApplCategoryList();
			deleteCatCodes(categories);
			// API FOR LIST OF OVERLAPPING CATEGORIES
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());

			initPhysicalConsultingCatcode();
		} else {
			throw new EpException(MessageConst.ERROR_722534, new String[] { "Category Code" });
		}
	}

	/**
	 * Save selected category information into SmSupplierCategory list of the
	 * application.
	 */
	public void saveSelectedCategory() {
		boolean validCat = true;
		if (getManagedBean().getSelectedCategory().getPmCategoryL1() != null
				&& getManagedBean().getSelectedCategory().getPmCategoryL1()
						.getCategoryL1Id() != null) {
			if (getManagedBean().getSelectedCategory().getPmCategoryL1()
					.getCategoryL1Code() != null
					&& getManagedBean()
							.getSelectedCategory()
							.getPmCategoryL1()
							.getCategoryL1Code()
							.equalsIgnoreCase(
									SupplierManagementConst.ICT_CATEGORY_L1_CODE)) {
				validCat = SmApplicationUtil.validateIctCatCode(
						getManagedBean().getBusinessTypeVal(), getManagedBean()
								.getSmAppl(), getManagedBean()
								.getSelectedCategory());
			}
		}
		boolean overLapCategory = false;
		overLapCategory = SmApplicationUtil.validL3CategoryCodeForOverLapping(
				getManagedBean().getSelectedCategory().getPmCategoryL3()
						.getCategoryL3Id(), getManagedBean().getSmSupplier()
						.getSupplierId(), getManagedBean().getSmAppl()
						.getApplId());
		getManagedBean().getSelectedCategory().setOverLapCategory(
				overLapCategory);

		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (getManagedBean().getSelectedCategory().getPmCategoryL3()
					.getCategoryL3Code().equals("99")) {
				validCat = true;
			}
			
			if (cat.getPmCategoryL1()
					.getCategoryL1Id()
					.equals(getManagedBean().getSelectedCategory()
							.getPmCategoryL1().getCategoryL1Id())
					&& cat.getPmCategoryL2()
							.getCategoryL2Id()
							.equals(getManagedBean().getSelectedCategory()
									.getPmCategoryL2().getCategoryL2Id())
					&& cat.getPmCategoryL3()
							.getCategoryL3Id()
							.equals(getManagedBean().getSelectedCategory()
									.getPmCategoryL3().getCategoryL3Id())) {
				validCat = false;
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.DELETED_RECORD_STATUS) == 0 || cat.getCancelledDate() != null) {
					if (cat.getCancelledDate() != null
							&& !isValidToAddAfterDeleted(cat.getCancelledDate())) {
						throw new EpException(MessageConst.ERROR_722563);
					} else {
						validCat = true;
					}
				} else {
					throw new EpException(MessageConst.ERROR_722533);
				}
			}
		}

		if (validCat) {
			getManagedBean().getSelectedCategory().setCategoryL1Name(
					getManagedBean().getSelectedCategory().getPmCategoryL1()
							.getCategoryName());
			getManagedBean().getSelectedCategory().setCategoryL2Name(
					getManagedBean().getSelectedCategory().getPmCategoryL2()
							.getCategoryName());
			getManagedBean().getSelectedCategory().setCategoryL3Name(
					getManagedBean().getSelectedCategory().getPmCategoryL3()
							.getCategoryName());
			SmCommonUtil.getInstance().setUserDetails(
					getManagedBean().getSelectedCategory());

			// check if added category already inside in main list, just set
			// back to pending activation status
			SmSupplierCategory cat = (SmSupplierCategory) EntityHelper
					.getEntityBySinglePropertyFromList(getManagedBean()
							.getSelectedCategory().getPmCategoryL3()
							.getCategoryL3Id(), getManagedBean().getSmAppl()
							.getSmSupplierCategories(), "pmCategoryL3");
			if (cat != null) {
				// #25794 & #26031 - start
				// update applRefId to NULL if previous data is deleted (9)
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED) {
					cat.setApplRefId(null);
				}
				// #25794 & #26031 - end
				cat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
				cat.setPoDecFlg(null);
				cat.setApprvrDecFlg(null);
				cat.setApprovedDate(null);
				cat.setCategoryEffDate(null);
				cat.setPrevPoRemarks(null);
				cat.setPoRemarks(null);
				cat.setPrevApproverRemarks(null);
				cat.setApproverRemarks(null);
				cat.setCancelledDate(null);
			} else {
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.add(getManagedBean().getSelectedCategory());
			}

			saveOrUpdateAppl();
			SmApplicationUtil
					.assignActiveAndApprovedCategoryList(getManagedBean());

			assignValReqdToFalse();
			// API FOR LIST OF OVERLAPPING CATEGORIES
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
			SmApplicationUtil.initOverlapCategoryDocs(getManagedBean()
					.isOverLappedCategoryExists(), getManagedBean());
			RequestContext.getCurrentInstance().execute("regCatDialog.hide()");

		}
	}

	/**
	 * Check whether is valid to add again previously deleted category
	 * 
	 * @param Date
	 *            cancelDate
	 * @return boolean
	 */
	private boolean isValidToAddAfterDeleted(Date cancelDate) {
		boolean retVal = false;
		Calendar cancelCalDate = Calendar.getInstance();
		cancelCalDate.setTime(cancelDate);
		cancelCalDate.add(Calendar.YEAR, SmRuleHelper.getInstance()
				.getYearsAllowedToAddDeletedCatCode());

		if (new Date().after(cancelCalDate.getTime())) {
			retVal = true;
		} else {
			if (getManagedBean().getMofAccount() != null
					&& getManagedBean().getMofAccount().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
					&& getManagedBean().getMofAccount().getEffDate() != null
					&& getManagedBean().getMofAccount().getEffDate()
							.after(cancelDate)) {
				retVal = true;
			}
			// #26031 - start
			if (getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
				retVal = true;
			}
			// #26031 - end
		}
		return retVal;
	}

	// -- Process Category Codes End
	// factory start
	/**
	 * Add or update selected factory info to SmFacility list of the
	 * application.
	 */
	public void addOrUpdateFactoryToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedFactoryFacility();
		SmCommonUtil.getInstance().setUserDetails(smFac);
		SmApplicationUtil.addOrUpdateFacilityCategory(getManagedBean(), smFac);
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getApplFactoryFacilities());
		//#27356
		SmApplicationUtil.validateCoordinate(getManagedBean());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smFacilityFactorDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		getManagedBean().setFactoryFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_FACTORY));
		RequestContext.getCurrentInstance().execute("factoryDialog.hide()");

	}

	/**
	 * Cancel add or update functionality and close the factory information
	 * dialog
	 */
	public void cancelFactorySaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:factoryDialog");
		RequestContext.getCurrentInstance().execute("factoryDialog.hide()");
	}

	/**
	 * Assign selected factory - facility object to managed bean for editing the
	 * information.
	 * 
	 * @param facilityBean
	 *            - SmFacility object
	 */
	public void assignSelFactory(SmFacility facilityBean) {
		SmApplicationUtil.assignSupplierCategoryFacility(getManagedBean(),
				facilityBean);
		setFacilityAddressSameAs(facilityBean);
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_FACTORY_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedFactoryFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.factory", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("factoryDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Factory
	 */
	public void assignNewFactoryBean() {
		getManagedBean().setSelSuppCategoryFacilityId(null);
		getManagedBean().setSelectedSuppCategoryFacilityIds(null);
		getManagedBean().setFacAddresSameAs(StringUtils.EMPTY);
		getManagedBean().setSelectedFactoryFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedFactoryFacility());
		getManagedBean().getSelectedFactoryFacility().setRevNo(1);
		getManagedBean().getSelectedFactoryFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedFactoryFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedFactoryFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_FACTORY);
		getManagedBean().setFactoryFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.factory", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("factoryDialog.show()");
	}

	/**
	 * Factory owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void factoryOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedFactoryFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected factories from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedFactories() {
		deleteSelectedFacilities(getManagedBean().getFactoryFacilitiesVOList());
		getManagedBean().setFactoryFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_FACTORY));
	}

	// factory end

	// Dockyard start
	/**
	 * Add or update selected dockyard info to SmFacility list of the
	 * application.
	 */
	public void addOrUpdateDockYardToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedDockyardFacility();
		SmCommonUtil.getInstance().setUserDetails(smFac);
		SmApplicationUtil.addOrUpdateFacilityCategory(getManagedBean(), smFac);
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getApplDockyardFacilities());
		//#27853
		SmApplicationUtil.validateCoordinate(getManagedBean());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smFacilityDockDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		getManagedBean().setDockYardFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD));
		RequestContext.getCurrentInstance().execute("dockYardDialog.hide()");

	}

	/**
	 * Cancel add or update functionality and close the dockyard information
	 * dialog
	 */
	public void cancelDockyardSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:dockYardDialog");
		RequestContext.getCurrentInstance().execute("dockYardDialog.hide()");
	}

	/**
	 * Assign selected dockyard - facility object to managed bean for editing
	 * the information.
	 * 
	 * @param facilityBean
	 *            - SmFacility object
	 */
	public void assignSelDockyard(SmFacility facilityBean) {
		setFacilityAddressSameAs(facilityBean);
		SmApplicationUtil.assignSupplierCategoryFacility(getManagedBean(),
				facilityBean);
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedDockyardFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.dockyard", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("dockYardDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Dockyard
	 */
	public void assignNewDockyardBean() {
		getManagedBean().setSelSuppCategoryFacilityId(null);
		getManagedBean().setSelectedSuppCategoryFacilityIds(null);
		getManagedBean().setFacAddresSameAs(StringUtils.EMPTY);
		getManagedBean().setSelectedDockyardFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedDockyardFacility());
		getManagedBean().getSelectedDockyardFacility().setRevNo(1);
		getManagedBean().getSelectedDockyardFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedDockyardFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedDockyardFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD);
		getManagedBean().setDockFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.dockyard", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("dockYardDialog.show()");
	}

	/**
	 * Dockyard owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void dockOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedDockyardFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected dockyards from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedDocks() {
		deleteSelectedFacilities(getManagedBean().getDockYardFacilitiesVOList());
		getManagedBean().setDockYardFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD));
	}

	// Dockyard end
	// Workshop start
	/**
	* Add or update selected workshop info to SmFacility list of the
	* application.
	*/
	public void addOrUpdateWorkshopToSmAppl() {
		// 29379 - Site Visit Grouping Enhancement -28775 - start
		final int minEmployee = 2;
		SmFacility smFac = getManagedBean().getSelectedWorkShopFacility();
		
		if(smFac.getSmSvWorkers().size() < minEmployee){
			throw new EpException(
					SmCommonUtil.getMessage("lbl.sm.insertemployee.instruction",null), null);
		}
		//validate for no duplicate IC/passport
		for(SmSvWorker worker1 : smFac.getSmSvWorkers()){
			for(SmSvWorker worker2 : smFac.getSmSvWorkers()){
				if(!worker1.equals(worker2) 
						&& worker1.getIdentificationNo().equals(worker2.getIdentificationNo())){
					throw new EpException(MessageConst.ERROR_722524,
							new String[] { worker1.getIdentificationNo() });
				}
			}
			//#28775 - 38366
			worker1.setOldExpertise(worker1.getExpertise());
			worker1.setOldIdentificationNo(worker1.getIdentificationNo());
			worker1.setOldIdentificationTypeId(worker1.getIdentificationTypeId());
			worker1.setOldName(worker1.getName());
			worker1.setOldWorkExpYear(worker1.getWorkExpYear());
		}
		// 29379 - Site Visit Grouping Enhancement -28775 - end
		
		SmCommonUtil.getInstance().setUserDetails(smFac);
		SmApplicationUtil.addOrUpdateFacilityCategory(getManagedBean(), smFac);
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getApplWorkShopFacilities());
		//#27853
		SmApplicationUtil.validateCoordinate(getManagedBean());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smWorkShopDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		// 29379 - Site Visit Grouping Enhancement -28775
		validateWorkshopWorkers();
		
		saveOrUpdateAppl();
		getManagedBean().setWorkshopFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP));
		RequestContext.getCurrentInstance().execute("workShopDialog.hide()");
	}
	
	// 29379 - Site Visit Grouping Enhancement -28775
	private void validateWorkshopWorkers(){
		ArrayList<SmSvWorker> workers = new ArrayList<>();
		for(SmSvWorker worker : getManagedBean().getSelectedWorkShopFacility().getSmSvWorkers()){
			worker.setIdentificationType(ParameterHelper.getInstance()
					.getPmParameterById(worker.getIdentificationTypeId()));
			if(worker.getIdentificationType().getParameterCode().equals(
					SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
				if(worker.getIdentificationNo().length() != SupplierManagementConst.IC_NO_LENGTH){
					throw new EpException(MessageConst.ERROR_722532,
							new String[] { worker.getIdentificationNo() });
				}
				SmCommonUtil.getInstance().verifyWithMyIdentityICOnly(
					worker.getName(), worker.getIdentificationNo());
			}
			SmSvWorker work = (SmSvWorker) EntityHelper.getEntityBySinglePropertyFromList(
				worker.getIdentificationNo(), getManagedBean().getDeletedWorkers(), "identificationNo");
			if(work != null){
				workers.add(worker);
			}
		}
		for(SmSvWorker worker : workers){
			SmSvWorker work1 = (SmSvWorker) EntityHelper.getEntityBySinglePropertyFromList(
					worker.getIdentificationNo(), getManagedBean().getSelectedWorkShopFacility().getSmSvWorkers(), "identificationNo");
			getManagedBean().getSelectedWorkShopFacility().getSmSvWorkers().remove(work1);
			SmSvWorker work2 = (SmSvWorker) EntityHelper.getEntityBySinglePropertyFromList(
					worker.getIdentificationNo(), getManagedBean().getDeletedWorkers(), "identificationNo");
			if(work1 != null && work2 != null){
				work2.setName(work1.getName());
				work2.setIdentificationType(work1.getIdentificationType());
				work2.setIdentificationTypeId(work1.getIdentificationTypeId());
				work2.setExpertise(work1.getExpertise());
				work2.setWorkExpYear(work1.getWorkExpYear());
				getManagedBean().getSelectedWorkShopFacility().getSmSvWorkers().add(work2);
			}
		}
	}
	
	// 29379 - Site Visit Grouping Enhancement -28775
	private void validateAllWorkshops(){
		if (getManagedBean().getSmAppl() == null || getManagedBean().getSmAppl().getPmStatus() == null
				|| getManagedBean().getSmAppl().getPmStatus()
					.getStatusId() != SupplierManagementConst.PM_STATUS_PENDING_APPL_DECLARATION
				|| EntityHelper.isCollectionEmpty(getManagedBean().getWorkshopFacilitiesVOList())){
			return;
		}
		final int minEmployee = 2;
		for (PmCategoryL3 cat : getManagedBean().getWorkshopFacilitiesVOList()){
			if (EntityHelper.isCollectionEmpty(cat.getFacilityList())){
				continue;
			}
			for (SmFacility fac : cat.getFacilityList()){
				if (EntityHelper.isCollectionEmpty(fac.getSmSvWorkers()) 
						|| fac.getSmSvWorkers().size() < minEmployee){
					throw new EpException(
							SmCommonUtil.getMessage("lbl.sm.insertemployee.instruction",null), null);
				}
			}
		}
	}

	/**
	 * Cancel add or update functionality and close the workshop information
	 * dialog
	 */
	public void cancelWorkShopSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:workShopDialog");
		RequestContext.getCurrentInstance().execute("workShopDialog.hide()");
	}

	/**
	 * Assign selected workshop - facility object to managed bean for editing
	 * the information.
	 * 
	 * @param facilityBean
	 *            - SmFacility object
	 */
	public void assignSelWorkshop(SmFacility facilityBean) {
		// 29379 - Site Visit Grouping Enhancement -28775 - start
		getManagedBean().setDeletedWorkers(new ArrayList<SmSvWorker>());
		if (getManagedBean().getSelectedWorkShopFacility().getSmSvWorkers() == null) {
			getManagedBean().getSelectedWorkShopFacility().setSmSvWorkers(new ArrayList<SmSvWorker>());
		}
		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		// 29379 - Site Visit Grouping Enhancement -28775 - end
		SmApplicationUtil.assignSupplierCategoryFacility(getManagedBean(),
				facilityBean);
		setFacilityAddressSameAs(facilityBean);
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedWorkShopFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.workshop", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("workShopDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Workshop
	 */
	public void assignNewWorkshopBean() {
		getManagedBean().setSelSuppCategoryFacilityId(null);
		getManagedBean().setSelectedSuppCategoryFacilityIds(null);
		getManagedBean().setFacAddresSameAs(StringUtils.EMPTY);
		getManagedBean().setSelectedWorkShopFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedWorkShopFacility());
		getManagedBean().getSelectedWorkShopFacility().setRevNo(1);
		getManagedBean().getSelectedWorkShopFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean().getDefPmCountry())));
		getManagedBean().getSelectedWorkShopFacility().setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().getSelectedWorkShopFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP);
		getManagedBean().setWorkShopFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.workshop", null)));
		// 29379 - Site Visit Grouping Enhancement -28775 - start
		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		getManagedBean().getSelectedWorkShopFacility().setSmSvWorkers(new ArrayList<SmSvWorker>());
		// 29379 - Site Visit Grouping Enhancement -28775 - end
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("workShopDialog.show()");
	}

	/**
	 * work shop owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void workShopOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedWorkShopFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected work shops from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedWorkShops() {
		deleteSelectedFacilities(getManagedBean().getWorkshopFacilitiesVOList());
		getManagedBean().setWorkshopFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP));
	}

	// 29379 - Site Visit Grouping Enhancement -28775 START
	/**
	 * Add employee
	 */
	public void addEmployee() {
		SmSvWorker worker = new SmSvWorker();
		worker.setIdentificationType(new PmParameter());
		worker.setSmFacility(getManagedBean().getSelectedWorkShopFacility());
		getManagedBean().getSelectedWorkShopFacility().getSmSvWorkers().add(worker);
	}

	/**
	 * Remove employee
	 */
	public void removeEmployee() {
		SmSvWorker[] selectedWorkers = getManagedBean().getSelectedWorkers();
		if(selectedWorkers == null || selectedWorkers.length < 1){
			return;
		}
		for(SmSvWorker worker : selectedWorkers){
			getManagedBean().getSelectedWorkShopFacility().getSmSvWorkers().remove(worker);
			if(worker.getSvWorkerId() != null){
				SmSvWorker deletedWorker = (SmSvWorker) EntityHelper.getEntityBySinglePropertyFromList(
						worker.getIdentificationNo(), getManagedBean().getDeletedWorkers(), "identificationNo");
				if(deletedWorker == null){
					getManagedBean().getDeletedWorkers().add(worker);
				}
			}
		}
		getManagedBean().setSelectedWorkers(null);
	}
	// 29379 - Site Visit Grouping Enhancement -28775 END

	// Workshop end
	// Store start
	// #34303 - start
	public void addOrUpdateStoreToSmApplNew() {

		List<SmSupplierCategory> categoryTypeB = new ArrayList<SmSupplierCategory>();
		List<SmSupplierCategory> categoryTypeS = new ArrayList<SmSupplierCategory>();

		Long[] cloneFacilityIds = getManagedBean()
				.getSelectedSuppCategoryFacilityIds().clone();

		for (Long catId : getManagedBean().getSelectedSuppCategoryFacilityIds()) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {

				// insert new selected category
				if (cat.getPmCategoryL3().getCategoryL3Id().equals(catId)) {
					if (cat.getPmCategoryL3().getFacilityType()
							.equalsIgnoreCase("B")) {
						categoryTypeB.add(cat);
					} else {
						categoryTypeS.add(cat);
					}
				}
			}
		}

		if (categoryTypeB.size() > 0) {

			List<Long> facilityIdList = new ArrayList<Long>();
			for (SmSupplierCategory SmSupplierCategory : categoryTypeB) {
				for (Long catId : cloneFacilityIds) {
					if (SmSupplierCategory.getPmCategoryL3().getCategoryL3Id()
							.equals(catId)) {
						facilityIdList.add(catId);
					}
				}
			}
			getManagedBean().setSelectedSuppCategoryFacilityIds(
					facilityIdList.toArray(new Long[facilityIdList.size()]));
			SmFacility smFac = getManagedBean().getSelectedStoreFacility();
			smFac.setFacilityType("B");
			addOrUpdateStoreToSmAppl(smFac);
		}

		if (categoryTypeS.size() > 0) {

			List<Long> facilityIdList = new ArrayList<Long>();
			for (SmSupplierCategory SmSupplierCategory : categoryTypeS) {
				for (Long catId : cloneFacilityIds) {
					if (SmSupplierCategory.getPmCategoryL3().getCategoryL3Id()
							.equals(catId)) {
						facilityIdList.add(catId);
					}
				}
			}
			getManagedBean().setSelectedSuppCategoryFacilityIds(
					facilityIdList.toArray(new Long[facilityIdList.size()]));
			SmFacility smFac = getManagedBean().getSelectedStoreFacility();
			smFac.setFacilityType("S");
			addOrUpdateStoreToSmAppl(smFac);
		}

		RequestContext.getCurrentInstance().execute("storeDialog.hide()");
	}
	/**
	 * Add or update selected store info to SmFacility list of the application.
	 * @param smFac2 
	 */
	public void addOrUpdateStoreToSmAppl(SmFacility smFac) {
//		SmFacility smFac = getManagedBean().getSelectedStoreFacility();
		// #34303 - end
		SmCommonUtil.getInstance().setUserDetails(smFac);
		SmApplicationUtil.addOrUpdateFacilityCategory(getManagedBean(), smFac);
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		/*EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getApplStoreFacilities());*/ //commented for #34711
		//#27853
		SmApplicationUtil.validateCoordinate(getManagedBean());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smFacStoreDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		getManagedBean().setStoreFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_STORE));
		//start #30043
		if(getManagedBean().getStoreFacilitiesVOList().isEmpty()){
			getManagedBean().setStoreFacilitiesVOList((SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
					.getSmAppl(),
					SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)));
		}else{
			getManagedBean().getStoreFacilitiesVOList().addAll((SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
					.getSmAppl(),
					SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)));
		}
		//end #30043
		RequestContext.getCurrentInstance().execute("storeDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the store information dialog
	 */
	public void cancelStoreSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:storeDialog");
		RequestContext.getCurrentInstance().execute("storeDialog.hide()");
	}

	/**
	 * Assign selected store - facility object to managed bean for editing the
	 * information.
	 * 
	 * @param facilityBean
	 *            the facility bean
	 */
	public void assignSelStore(SmFacility facilityBean) {
		SmApplicationUtil.assignSupplierCategoryFacility(getManagedBean(),
				facilityBean);
		setFacilityAddressSameAs(facilityBean);
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_STORE_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedStoreFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.store", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("storeDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Store
	 */
	public void assignNewStoreBean() {
		getManagedBean().setSelSuppCategoryFacilityId(null);
		getManagedBean().setSelectedSuppCategoryFacilityIds(null);
		getManagedBean().setFacAddresSameAs(StringUtils.EMPTY);
		getManagedBean().setSelectedStoreFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedStoreFacility());
		getManagedBean().getSelectedStoreFacility().setRevNo(1);
		getManagedBean().getSelectedStoreFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedStoreFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedStoreFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_STORE);
		getManagedBean().setStoreFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.store", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("storeDialog.show()");
	}

	/**
	 * Store owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void storeOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedStoreFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected stores from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedStores() {
		deleteSelectedFacilities(getManagedBean().getStoreFacilitiesVOList());
		getManagedBean().setStoreFacilitiesVOList(
				SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
						.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_STORE));
		//start #30043
				if(getManagedBean().getStoreFacilitiesVOList().isEmpty()){
					getManagedBean().setStoreFacilitiesVOList((SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
							.getSmAppl(),
							SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)));
				}else{
					getManagedBean().getStoreFacilitiesVOList().addAll((SmApplicationUtil.getCategoryFacilityVO(getManagedBean()
							.getSmAppl(),
							SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)));
				}
				//end #30043
	}

	// Store end
	// manufacturer start
	/**
	 * Add or update selected bank info to SmManufacturingDtl list of the
	 * application.
	 */
	public void addOrUpdateManufacturerToSmAppl() {
		/*
		 * if (getManagedBean().getSelectedManufacturer().getPmUom().getUomId()
		 * != null) { for (PmUom uom : getManagedBean().getUomLs()) { if
		 * (uom.getUomId().equals(
		 * getManagedBean().getSelectedManufacturer().getPmUom() .getUomId())) {
		 * getManagedBean().getSelectedManufacturer().setPmUom(uom); break; } }
		 * }
		 */
		SmCommonUtil.getInstance().setUserDetails(
				getManagedBean().getSelectedManufacturer());
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedManufacturer(), getManagedBean().getSmAppl()
				.getSmManufacturingDtls());
		// if
		// (getManagedBean().getSelectedManufacturer().getManufacturingDtlId()
		// == null) {
		// RequestContext.getCurrentInstance().execute(
		// "smManuDtWidVar.paginator.endLink.click()");
		// }
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("manufactorDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the manufacturer information
	 * dialog
	 */
	public void cancelManuSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:manufactorDialog");
		RequestContext.getCurrentInstance().execute("manufactorDialog.hide()");
	}

	/**
	 * Assign selected manufacturer object to managed bean for editing the
	 * information.
	 * 
	 * @param manBean
	 *            - SmManufacturingDtl object
	 */
	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("manufactorDialog.show()");
	}

	/**
	 * Add or create a new SmManufacturingDtl object
	 */
	public void assignNewManufacturerBean() {
		log.info("**** Add new Manufacturer *********");
		SmManufacturingDtl manu = new SmManufacturingDtl();
		// manu.setPmUom(new PmUom());
		manu.setRevNo(1);
		manu.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().setSelectedManufacturer(manu);
		// getManagedBean().setUomSelectItems(
		// SmApplicationUtil.getUomSelectItems());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("manufactorDialog.show()");
		// log.info("****** After **********"
		// + getManagedBean().getSelectedManufacturer().getPmUom()
		// .getUomName());

	}

	/**
	 * Delete or remove selected manufactures from the SmManufacturingDtl list
	 * of the application.
	 */
	public void deleteSelectedManufacturers() {
		if (getManagedBean().getSelectedManufacturingList().size() > 0) {
			EntityHelper
					.compareAndRemoveEntityFromList(getManagedBean()
							.getSelectedManufacturingList(), getManagedBean()
							.getSmAppl().getSmManufacturingDtls(),
							"manufacturingDtlId");
			getManagedBean().getSelectedManufacturingList().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Manufacturer" });
		}
	}

	// manufacturer end

	// machinery start
	/**
	 * Add or update selected machinery info to SmMachineDtls list of the
	 * application.
	 */
	public void addOrUpdateMachineToSmAppl() {
		SmApplicationUtil.addOrUpdateMachineDtlCategory(getManagedBean(),
				getManagedBean().getSelectedMachine());
		if (getManagedBean().getSelectedMachine().getCategoryType() != null
				&& getManagedBean().getSelectedMachine().getCategoryType()
						.getParameterId() == null) {
			getManagedBean().getSelectedMachine().setCategoryType(null);
		}
		if (getManagedBean().getSelectedMachine().getMachineDtlId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smMachDtWidVar.paginator.endLink.click()");
		}
		if (getManagedBean().getSelectedMachine().getMachine() != null
				&& getManagedBean().getSelectedMachine().getMachine()
						.getMachineId().compareTo(-1L) == 0) {
			getManagedBean().getSelectedMachine().setMachine(null);
		}

		// remove from main list for non selected machine that is still not yet
		// save to avoid error
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmMachineDtls())) {
			List<SmMachineDtl> needToDeleteList = new ArrayList<SmMachineDtl>();
			for (SmMachineDtl mac : getManagedBean().getSmAppl()
					.getSmMachineDtls()) {
				if (mac.getMachineDtlId() == null
						&& !mac.equals(getManagedBean().getSelectedMachine())) {
					needToDeleteList.add(mac);
				}
			}
			if (!EntityHelper.isCollectionEmpty(needToDeleteList)) {
				getManagedBean().getSmAppl().getSmMachineDtls()
						.removeAll(needToDeleteList);
			}
		}

		// add for new machine
		if (getManagedBean().getSelectedMachine().isBrandNew()) {
			if (getManagedBean().getSmAppl().getSmMachineDtls() == null) {
				getManagedBean().getSmAppl().setSmMachineDtls(
						new ArrayList<SmMachineDtl>());
			}
			getManagedBean().getSmAppl().getSmMachineDtls()
					.add(getManagedBean().getSelectedMachine());
		}
		saveOrUpdateAppl();
		assignValReqdToFalse();

		// set default machine if null to avoid null in screen dialog after save
		if (getManagedBean().getSelectedMachine().getMachine() == null) {
			getManagedBean().getSelectedMachine().setMachine(new PmMachine());
		}

		// set machine category vo list
		getManagedBean().setMachineCategoryVOList(
				SmApplicationUtil.getMachineCategoryVO(getManagedBean()
						.getSmAppl()));

		RequestContext.getCurrentInstance().execute("equipmentDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the machine detail
	 * information dialog
	 */
	public void cancelMachineSaveorUpdate() {
		assignValReqdToFalse();
		// set default machine if null to avoid null in screen dialog after save
		if (getManagedBean().getSelectedMachine().getMachine() == null) {
			getManagedBean().getSelectedMachine().setMachine(new PmMachine());
		}
		if (getManagedBean().getSelectedMachine().getMachineDtlId() == null) {
			getManagedBean().getSelectedMachine().setValue(
					new BigDecimal(SupplierManagementConst.MIN_AMT_RANGE));
		} else {
			getManagedBean().getSelectedMachine().setValue(
					getManagedBean().getRentPrevVal());
		}

		// roll back main list to clone object if cancel for update action
		if (getManagedBean().getPopupAction().equals("U")) {
			if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
					.getSmMachineDtls())) {

				EntityHelper.addOrUpdateEntityToList(
						(SmMachineDtl) getManagedBean().getCloneObject(),
						getManagedBean().getSmAppl().getSmMachineDtls());
			}
		}
		RequestContext.getCurrentInstance().reset(":form:tt:equipmentDialog");
		RequestContext.getCurrentInstance().execute("equipmentDialog.hide()");
		RequestContext.getCurrentInstance().update(":form:tt:macDt");
	}

	/**
	 * Assign selected machine object to managed bean for editing the
	 * information.
	 * 
	 * @param macBean
	 *            - SmMachineDtl object
	 */
	public void assignSelMachinery(SmMachineDtl macBean) {
		if (macBean.getMachine() == null) {
			macBean.setMachine(new PmMachine());
			macBean.getMachine().setMachineId(-1L);
		}
		getManagedBean().setPopupAction("U");
		SmApplicationUtil.assignMachineCategoryIds(getManagedBean(), macBean);
		getManagedBean().setRentPrevVal(macBean.getValue());
		getManagedBean().setCategoryTypeItems(
				SmApplicationUtil.getSelectedCategoryTypeItems(getManagedBean()
						.getSmAppl()));
		getManagedBean().setCategoryCodeItems(
				SmApplicationUtil.getCategoryCodesItemsByCatTypeId(macBean
						.getCategoryType().getParameterId(), getManagedBean()
						.getSmAppl()));
		getManagedBean().setMachineItems(
				SmApplicationUtil.getMachineItemsByMachineDetails(macBean,
						getManagedBean().getSelectedMachineCategoryIds()));
		getManagedBean().setSelectedMachine(macBean);
		getManagedBean().getSelectedMachine().setBrandNew(false);
		getManagedBean().setCloneObject(
				(SmMachineDtl) SerializationUtils.clone(macBean));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equipmentDialog.show()");
	}

	/**
	 * Add or create a new SmMachineDtl object
	 */
	public void assignNewMachineryBean(Long categoryTypeId) {
		getManagedBean().setSelectedMachineCategoryIds(null);
		getManagedBean().setPopupAction("N");
		getManagedBean().setOthersMachine(false);
		getManagedBean().setCategoryTypeItems(
				SmApplicationUtil.getSelectedCategoryTypeItems(getManagedBean()
						.getSmAppl()));
		getManagedBean().setCategoryCodeItems(new ArrayList<SelectItem>());
		getManagedBean().setSelectedMachine(new SmMachineDtl());
		getManagedBean().getSelectedMachine().setMachine(new PmMachine());
		getManagedBean().getSelectedMachine()
				.setCategoryType(new PmParameter());
		getManagedBean().getSelectedMachine().setRevNo(1);
		getManagedBean().getSelectedMachine().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedMachine().setValue(new BigDecimal(0.00));
		getManagedBean().getSelectedMachine().setBrandNew(true);
		getManagedBean().getSelectedMachine().setCategoryType(
				SmApplicationUtil.retrievePmParameterById(categoryTypeId));
		getManagedBean()
				.setCategoryCodeItems(
						SmApplicationUtil.getCategoryCodesItemsByCatTypeId(
								getManagedBean().getSelectedMachine()
										.getCategoryType().getParameterId(),
								getManagedBean().getSmAppl()));
		getManagedBean().setMachineItems(
				SmApplicationUtil.getMachineItemsByMachineDetails(
						getManagedBean().getSelectedMachine(), getManagedBean()
								.getSelectedMachineCategoryIds()));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equipmentDialog.show()");
	}

	/**
	 * Delete or remove selected machinery from the SmMachineDtl list of the
	 * application.
	 */
	public void deleteSelectedMachinery() {
		String errMsg = StringUtils.EMPTY;
		getManagedBean().setSelectedMachineList(new ArrayList<SmMachineDtl>());
		for (SmMachineCategoryVO macVO : getManagedBean()
				.getMachineCategoryVOList()) {
			for (SmMachineDtl mac : macVO.getSmMachineDtls()) {
				if (mac.isSelected()) {
					getManagedBean().getSelectedMachineList().add(mac);
				}
			}
		}
		SmAppl cloneAppl = (SmAppl) SerializationUtils.clone(getManagedBean()
				.getSmAppl());
		errMsg = SmApplicationUtil.getMachineDeleteErrMsgKey(getManagedBean()
				.getSmAppl(), getManagedBean().getSelectedMachineList(),
				getManagedBean().isShowMachineryEquipmentPanel());
		if (!StringUtils.isBlank(errMsg)) {
			getManagedBean().setSmAppl(cloneAppl);
			FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									errMsg));
		}
		getManagedBean().setMachineCategoryVOList(
				SmApplicationUtil.getMachineCategoryVO(getManagedBean()
						.getSmAppl()));
	}

	/**
	 * Populate category codes when on change category type selected
	 */
	public void onChangeCategoryType() {
		// set selected category type
		getManagedBean().getSelectedMachine().setCategoryType(
				SmApplicationUtil.retrievePmParameterById(getManagedBean()
						.getSelectedMachine().getCategoryType()
						.getParameterId()));

		// set dropdown category codes
		if (!StringUtils.isBlank(getManagedBean().getSelectedMachine()
				.getCategoryType().getParameterCode())
				&& getManagedBean().getSelectedMachine().getCategoryType()
						.getParameterCode()
						.equals(ParameterConst.CATEGORY_TYPE_PRINTING)) {
			getManagedBean().setCategoryCodeItems(
					SmApplicationUtil.getCategoryCodesItemsByCatTypeId(
							getManagedBean().getSelectedMachine()
									.getCategoryType().getParameterId(),
							getManagedBean().getSmAppl()));
		} else {
			getManagedBean().setCategoryCodeItems(null);
		}

		// set dropdown machines
		getManagedBean().getSelectedMachine().setMachine(new PmMachine());
		getManagedBean().getSelectedMachine().setMachineType(StringUtils.EMPTY);
		getManagedBean().setMachineItems(
				SmApplicationUtil.getMachineItemsByMachineDetails(
						getManagedBean().getSelectedMachine(), getManagedBean()
								.getSelectedMachineCategoryIds()));
	}

	/**
	 * Populate machine when on change category code selected
	 */
	public void onChangeCategoryCode() {
		// set dropdown machines
		getManagedBean().setMachineItems(
				SmApplicationUtil.getMachineItemsByMachineDetails(
						getManagedBean().getSelectedMachine(), getManagedBean()
								.getSelectedMachineCategoryIds()));
	}

	public void onChangeMachine() {
		if (getManagedBean().getSelectedMachine().getMachine() != null) {
			if ((getManagedBean().getSelectedMachine().getMachine()
					.getMachineId().compareTo(-1L) == 0)) {
				log.info(">>>OTHERS EQUIPMENT TYPE IS SELECTED<<<<");
			} else {
				getManagedBean().getSelectedMachine().setMachine(
						SmCommonUtil.getInstance().getMachineById(
								getManagedBean().getSelectedMachine()
										.getMachine().getMachineId()));
				getManagedBean().getSelectedMachine().setMachineType(
						StringUtils.EMPTY);
			}
		} else {
			getManagedBean().getSelectedMachine().setMachine(new PmMachine());
			getManagedBean().getSelectedMachine().setMachineType(
					StringUtils.EMPTY);
		}
	}

	/**
	 * Populate category codes when on change category type selected
	 */
	public void onChangeCategoryType(SmMachineDtl mac) {
		// set selected category type
		mac.setCategoryType(SmApplicationUtil.retrievePmParameterById(mac
				.getCategoryType().getParameterId()));

		// set dropdown machines
		mac.setMachine(new PmMachine());
		mac.setMachineType(StringUtils.EMPTY);
	}

	/**
	 * Process machine value when changing drop down value
	 * 
	 * @param SmMachineDtl
	 *            mac
	 */
	public void onChangeMachine(SmMachineDtl mac) {
		if (mac.getMachine() != null) {
			if (mac.getMachine().getMachineId().compareTo(Long.parseLong("-1")) != 0) {
				mac.setMachineType(StringUtils.EMPTY);
			}
		}
	}

	/**
	 * Populate machine when on change category code selected
	 */
	public void onChangeCategoryCode(SmMachineDtl mac) {
		// set dropdown machines
		mac.setMachine(new PmMachine());
		mac.setMachineType(StringUtils.EMPTY);
	}

	// manufacturer end

	// START:Supplier System

	/**
	 * Add or create a new SmSupplierSystem object
	 */
	public void assignNewSystemBean() {
		PmMachine mac = new PmMachine();
		getManagedBean().getTravellingSoftwareList().add(mac);
	}

	/**
	 * Assign selected system object to managed bean for editing the
	 * information.
	 * 
	 * @param systemBean
	 *            - SmSupplierSystem object
	 */
	public void assignSelSystem(SmSupplierSystem systemBean) {
		if (StringUtils.isBlank(systemBean.getSystemName())) {
			getManagedBean().setOthersSystem(false);
		} else {
			getManagedBean().setOthersSystem(true);
		}
		getManagedBean().setSelectedSystem(
				(SmSupplierSystem) SerializationUtils.clone(systemBean));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("systemDialog.show()");
	}

	/**
	 * Cancel add or update functionality and close the system information
	 * dialog
	 */
	public void cancelSystemSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:systemDialog");
		RequestContext.getCurrentInstance().execute("systemDialog.hide()");
	}

	/**
	 * Add or update selected travelling system info to SmSupplierSystem list of
	 * the application.
	 */
	public void addOrUpdateSystemToSmAppl() {
		PmMachine mac = new PmMachine();
		mac.setMachineDesc(getManagedBean().getTravellingSoftwareOthers());
		getManagedBean().getTravellingSoftwareList().add(mac);
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("systemDialog.hide()");
	}

	public void onDeleteSystem(PmMachine mach) {
		getManagedBean().setSelectedTravellingSoftware(mach);
	}

	/**
	 * Delete or remove selected travelling system from the SmSupplierSystem
	 * list of the application.
	 */
	public void deleteSelectedSystem() {

		Iterator<PmMachine> mainTravelSoftwareIt = getManagedBean()
				.getTravellingSoftwareList().iterator();
		while (mainTravelSoftwareIt.hasNext()) {
			PmMachine mainTravelSoftware = mainTravelSoftwareIt.next();

			if (mainTravelSoftware.equals(getManagedBean()
					.getSelectedTravellingSoftware())) {
				mainTravelSoftwareIt.remove();
			}
		}
	}

	/**
	 * Get Machine description by id
	 * 
	 * @param Long
	 *            machineId
	 * @return String
	 */
	public String getMachineDescById(Long machineId) {
		PmMachine machine = SmCommonUtil.getInstance()
				.getMachineById(machineId);
		if (machine != null) {
			return machine.getMachineDesc();
		} else {
			return "";
		}
	}

	/**
	 * Reset selected software name when click other software checkbox
	 */
	public void onTickOtherSystem() {
		SmSupplierSystem system = (SmSupplierSystem) EntityHelper
				.getEntityByMultiplePropertiesFromList(getManagedBean()
						.getSelectedSystem(), getManagedBean().getSmAppl()
						.getSmSupplierSystems(),
						new String[] { "supplierSystemId" }, null, null);
		if (system != null) {
			if (getManagedBean().isOthersSystem()) {
				getManagedBean().getSelectedSystem().setSystem(null);
			} else {
				getManagedBean().getSelectedSystem().setSystemName("");
				if (system.getSystem() != null) {
					getManagedBean().getSelectedSystem().setSystem(
							system.getSystem());
				} else {
					getManagedBean().getSelectedSystem().setSystem(
							new PmMachine());
				}
			}
		} else {
			if (getManagedBean().isOthersSystem()) {
				getManagedBean().getSelectedSystem().setSystem(null);
			} else {
				getManagedBean().getSelectedSystem().setSystemName("");
				getManagedBean().getSelectedSystem().setSystem(new PmMachine());
			}
		}
	}

	// END:Supplier Sysem

	// -- Process Share Holder Personnel Start
	/**
	 * Add or update selected personnel info to SmPersonnel of the application.
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateShareHolderToSmAppl() throws ParseException {
		boolean validShrHldr = true;
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			calculateShareHolderPercent();
		}

		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean()
						.getSelectedShareHolder()
						.getWorkExperienceYear()
						.compareTo(
								new BigDecimal(ruleHelper
										.getConsutantPersonnelMinExp())) < 0
				&& getManagedBean().getSelectedShareHolder().isEquityOwner()) {

			validShrHldr = false;
			throw new EpException(MessageConst.ERROR_722518);
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean().getSelectedShareHolder().isEquityOwner()
				&& (getManagedBean().getSelectedShareHolder()
						.getSmStaffExperiences() == null || getManagedBean()
						.getSelectedShareHolder().getSmStaffExperiences()
						.isEmpty())) {

			validShrHldr = false;
			throw new EpException(SmCommonUtil.getMessage(
					"lbl.sm.jobexperience.required.atleast.one", null));
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean().getSelectedShareHolder().isEquityOwner()
				&& (EntityHelper.isCollectionEmpty(getManagedBean()
						.getSelectedShareHolder().getSmAssociationBodies()))) {

			validShrHldr = false;
			throw new EpException(SmCommonUtil.getMessage(
					"lbl.sm.profbody.required.atleast.one", null));
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& EntityHelper.isCollectionEmpty(getManagedBean()
						.getSelectedShareHolder().getSmStaffAcademics())) {

			validShrHldr = false;
			throw new EpException(MessageConst.ERROR_722575);
		}
		validateOwnerIndividualAcc(getManagedBean().getSelectedShareHolder());
		validateAuthorizedPersonnel(getManagedBean().getSelectedShareHolder());
		addOrUpdateShareHolderAddress(getManagedBean().getSelectedShareHolder());
		validShrHldr = SmApplicationUtil.checkValidUser(getManagedBean()
				.getSelectedShareHolder(), getManagedBean().getSmAppl());

		// validate email domain
		if (getManagedBean().getSelectedShareHolder() != null) {
			String email = getManagedBean().getSelectedShareHolder()
					.getPmUser() != null
					&& StringUtils.isNotBlank(getManagedBean()
							.getSelectedShareHolder().getPmUser().getEmail()) ? getManagedBean()
					.getSelectedShareHolder().getPmUser().getEmail()
					: getManagedBean().getSelectedShareHolder().getEmail();
			SmCommonUtil.checkEmailDomainWithExceptionMsg(getManagedBean()
					.getSelectedShareHolder().getName(), email);
		}

		// if not authorized, clear justification
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
			getManagedBean().getSelectedShareHolder()
					.setAddnlPersonnelJustification("");
		}

		getManagedBean().getSelectedShareHolder().setSmAppl(
				getManagedBean().getSmAppl());

		// set bumi flag
		if (getManagedBean().getSelectedShareHolder().getBumiFlgTrans() == null) {
			getManagedBean().getSelectedShareHolder().setBumiFlg(0);
		} else {
			getManagedBean().getSelectedShareHolder()
					.setBumiFlg(
							getManagedBean().getSelectedShareHolder()
									.getBumiFlgTrans());
		}
		//#31130 --START 050625 -set bumi flg to 0 when country other than Malaysia
		if(!getManagedBean().getSelectedShareHolder().getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)){
			getManagedBean().getSelectedShareHolder().setBumiFlg(0);
		} //#31130 --END 050625	

		// set muslim flg
		//#29856 - start
		/*if(ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase
				(getManagedBean().getSelectedShareHolder().getIdentificationType().getParameterCode())){
			getManagedBean().getSelectedShareHolder().setMuslimFlg(null);*/ //Commented for #31130 -050625
		//#29856 - end
		
		//#31130 --START 050625 -set muslim flg to null when country other than Malaysia
		if(!getManagedBean().getSelectedShareHolder().getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)){
			getManagedBean().getSelectedShareHolder().setMuslimFlg(null);
	    	//#31130 --END 050625		
		} else {
			if(getManagedBean().getSelectedShareHolder().getMuslimFlgTrans()== null){
				getManagedBean().getSelectedShareHolder().setMuslimFlg(0);
			}else{
				getManagedBean().getSelectedShareHolder().setMuslimFlg(
						getManagedBean().getSelectedShareHolder()
								.getMuslimFlgTrans());
			}	
		}
		
		//31749 - start
		/*
		 * Bug where symbol 'P' will show eventough 
		 * there is no changed value at sm personnel
		 * 
		 * Need to set value ..Flg before .ser file creation
		 * 
		 * */
		//set flag value
		if(getManagedBean().getSelectedShareHolder().isManagement()){
			getManagedBean().getSelectedShareHolder().setManagementFlg(1);
		}
		
		if(getManagedBean().getSelectedShareHolder().isClerical()){
			getManagedBean().getSelectedShareHolder().setClericalFlg(1);
		}
		
		//34197 START 
		/*if(getManagedBean().getSelectedShareHolder().isContractSigner()){
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(1);
		}*/
		if(getManagedBean().getSelectedShareHolder().getContractSignerFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
			getManagedBean().getSelectedShareHolder().setContractSigner(true);
		}
		
		if(getManagedBean().getSelectedShareHolder().getContractSignerFlg() == DbValueConst.RECORD_STATUS_SUSPENDED){
			getManagedBean().getSelectedShareHolder().setContractSigner(false);
		}
		//34197 END
		if(getManagedBean().getSelectedShareHolder().isSemiProf()){
			getManagedBean().getSelectedShareHolder().setSemiProfFlg(1);
		}
		
		if(getManagedBean().getSelectedShareHolder().isProf()){
			getManagedBean().getSelectedShareHolder().setProfFlg(1);
		}
		//31749 - end
		
		
		if (validShrHldr) {
			if (getManagedBean().getSelectedShareHolder().getPmRace() != null
					&& getManagedBean().getSelectedShareHolder().getPmRace()
							.getRaceId() == null) {
				getManagedBean().getSelectedShareHolder().setPmRace(null);
			}
			if (getManagedBean().getSelectedShareHolder().getTitle() != null
					&& getManagedBean().getSelectedShareHolder().getTitle()
							.getSalutationId() == null) {
				getManagedBean().getSelectedShareHolder().setTitle(null);
			}
			if (getManagedBean().getSelectedShareHolder()
					.getIdentificationType() != null
					&& getManagedBean().getSelectedShareHolder()
							.getIdentificationType().getParameterId() == null) {
				log.info(">>>>>IDENTIFICATION TYPE IS NEW");
				getManagedBean().getSelectedShareHolder()
						.setIdentificationType(null);
			}
			if (getManagedBean().getSelectedShareHolder().getSmStaffAcademics() != null) {
				for (SmStaffAcademic ac : getManagedBean()
						.getSelectedShareHolder().getSmStaffAcademics()) {
					if (ac.getQualificationId() != null
							&& ac.getQualificationId().getParameterId() == null) {
						log.info(">>>>>QUALIFICATION IS NEW");
						ac.setQualificationId(null);
					}
				}
			}

			SmCommonUtil.getInstance().setUserDetails(
					getManagedBean().getSelectedShareHolder());
			EntityHelper.addOrUpdateEntityToList(getManagedBean()
					.getSelectedShareHolder(), getManagedBean().getSmAppl()
					.getSmPersonnels());
			if (getManagedBean().getSelectedShareHolder().getPersonnelId() == null) {
				if (getManagedBean().getSelectedShareHolder().isEquityOwner()
						&& getManagedBean()
								.getCurrentPageType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
					RequestContext.getCurrentInstance().execute(
							"suppCapShrHldrDtWidVar.paginator.endLink.click()");
				} else {
					RequestContext
							.getCurrentInstance()
							.execute(
									"suppPersonnelInfoDtWidVar.paginator.endLink.click()");
				}
			}

			saveOrUpdateAppl();
			SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
					getManagedBean().getSmAppl(), getManagedBean()
							.getPrevApprovedPersonnels(), getManagedBean()
							.isDisAllowOwnerEdit());
			// sort personnel by company role
			SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
					.getSmAppl());
			if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				SmCommonUtil.getInstance().setUserDetails(
						getManagedBean().getSmAppl());
				saveOrUpdateAppl();
				calculateCapital();
				getManagedBean().setRedirectToPersonPage(false);
				SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
						getManagedBean().getSmAppl(), getManagedBean()
								.getPrevApprovedPersonnels(),
						getManagedBean().isDisAllowOwnerEdit());
				SmApplicationUtil.setPersonnelSoftcertRenewalStatus(
						this.getManagedBean().getSmSupplier(), this
								.getManagedBean().getSmAppl()
								.getSmPersonnels());
			}
			if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
				log.info("****** Calc countf ******* ");
				calcPersonnelListCount();
				getManagedBean().setRedirectToPersonPage(false);

			}

		}

	}

	public String showTravellingInstruction() {
		String retVal = StringUtils.EMPTY;
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.isSpecialCategory()
					&& cat.getPmCategoryL3() != null
					&& cat.getPmCategoryL3().getCategoryTypeCode() != null
					&& cat.getPmCategoryL3().getCategoryTypeCode()
							.getParameterId() != null
					&& cat.getRecordStatusFlg() != null
					&& cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0
					&& !SmApplicationUtil.isCategoryRejected(cat)) {
				String catType = "";
				if (cat.getPmCategoryL3().getCategoryTypeCode() != null) {
					catType = cat.getPmCategoryL3().getCategoryTypeCode()
							.getParameterCode();
				}
				if (StringUtils.isNotBlank(catType)
						&& catType
								.equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_TRAVEL)) {
					retVal = SmCommonUtil.getMessage(
							"lbl.sm.travel.software.instruction",
							new String[] { cat.getPmCategoryL3()
									.getCategoryName()
									+ " - "
									+ cat.getPmCategoryL3().getFullCatcode() });
				}
			}
		}
		return retVal;
	}

	/**
	 * Save or update the latest information of the application i.e. SmAppl .
	 */
	private void saveOrUpdateAppl() {
		//checkin if coordinate null #27853
		if(!getManagedBean().isApproverUser() && !getManagedBean().isPoUser()){
		SmApplicationUtil.validateCoordinate(getManagedBean());
		}
		
		// checking if suddenly premise information is missing (#21955)
		SmApplicationUtil.validateDontHavePremise(getManagedBean().getSmAppl());

		// make sure application reject reason is empty for this application
		// (#22444)
		getManagedBean().getSmAppl().setSmApplRejectReasons(null);

		SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
				.getCompanyBasicGst());
		SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
				.getSelectedBranch().getGst());

		// update category that record status is 1 for new mof application. This
		// happen due to resubmit application (#23683)
		updateCategoriesRecordStatus();

		reCheckVerifiedSection();
		try {
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmAppl(this.getManagedBean().getSmAppl());
			reqDto.setCompanyBasicGst(getManagedBean().getCompanyBasicGst());
			reqDto.setBranchGst(getManagedBean().getSelectedBranch().getGst());
			reqDto.setSameAsHq(getManagedBean().getSelectedBranch()
					.isSameAsHq());
			// #27802 - start
			if (getManagedBean().isSupplierUser() && getManagedBean().getSelectedShareHolder() != null && getManagedBean().getSelectedShareHolder().getPmUser() != null) {
				reqDto.setPersonnel(getManagedBean().getSelectedShareHolder());
			}
			// #27802 - end
			SmServiceLookup.getMofRegistrationSO().updateApplication(reqDto);
		} catch (Exception e) {
			log.info("error to save appl no:"
					+ getManagedBean().getSmAppl().getApplNo(), e);
		} finally {

			retriveLatestApplAfterUpdate("", "");

			// set section editor VO after update
			SmSideMenuIconUtil.setAllSectionEditorVO(getManagedBean(), false,
					getManagedBean().getCurrentPageType());
			setBranchDisplayTable(); //29727 , #34135

		}
	}

	/**
	 * Update category record status if category record status is 1 and
	 * application type is new
	 */
	private void updateCategoriesRecordStatus() {
		if (getManagedBean().isSupplierUser()
				&& getManagedBean().getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				&& getManagedBean().getSmAppl().getSmSupplierCategories() != null) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					cat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
				}
			}
		} else if (getManagedBean().isSupplierUser() // start 29847
				&& getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				&& getManagedBean().getSmAppl().getSmSupplierCategories() != null) {
			for (Iterator<SmSupplierCategory> iterator = getManagedBean().getSmAppl().getSmSupplierCategories()
					.iterator(); iterator.hasNext();) {
				SmSupplierCategory smSupplierCategory = iterator.next();
				if (smSupplierCategory.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED) {
					iterator.remove();
					//#30766 & #34209 - START
					for (Iterator<SmFacility> facilityIterator = getManagedBean().getSmAppl().getSmFacilities().iterator(); facilityIterator.hasNext();){
						SmFacility smFacility = facilityIterator.next();
						for (SmSupplierCategoryFacility smSupplierCategoryFacility : smFacility.getSmSupplierCategoryFacilities()){
							if(smSupplierCategoryFacility.getPmCategoryL3().getCategoryL3Id() == smSupplierCategory.getPmCategoryL3().getCategoryL3Id()){
								facilityIterator.remove();
							}
						}
					}
					//#30766 & #34209 - END 
				}
			}
		} // end 29847
	}

	/**
	 * Update SmPersonnel record status just in case after change epRole not
	 * reflect.
	 * 
	 * @param smPersonnels
	 *            - List of sm personnels
	 */
	private void updatePersonnelRecordStatus(List<SmPersonnel> smPersonnels,
			SmSupplier smSupplier) {
		if (smPersonnels != null && !smPersonnels.isEmpty()) {
			for (SmPersonnel smPersonnel : smPersonnels) {
				if (smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED
						&& smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					smPersonnel
							.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					if (StringUtils.isNotBlank(smPersonnel.getEpRole())
							&& (smPersonnel.getPmUser() == null || smPersonnel
									.getPmUser().getUserId() == null)) {
						smPersonnel
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
					}
				}
			}
		}
	}

	/**
	 * To check whether need to disable ep role drop down
	 * 
	 * @param smPersonnel
	 * @return boolean
	 */
	public boolean disableEpRoleDropDown(SmPersonnel smPersonnel) {
		boolean retVal = SmApplicationUtil.disableEpRoleDropDown(smPersonnel,
				getManagedBean().getPrevApprovedPersonnels());
		return retVal;
	}

	/**
	 * Check whether need to disable authorized radio button
	 * 
	 * @param SmPersonnel
	 *            personnel
	 * @return boolean
	 */
	public boolean disableAuthorizedRadio(SmPersonnel personnel) {
		boolean retVal = false;
		retVal = SmApplicationUtil.isDisableAuthorizedRadio(personnel,
				getManagedBean().isValidSmUserLimit());
		getManagedBean().setDisableAuthorizedRadio(retVal);
		if (getManagedBean().isInDbAlreadyAuth()) {
			getManagedBean().setDisableAuthorizedRadio(false);
		}
		return retVal;
	}

	/**
	 * To redirect from personnel information page the main screen when click
	 * Back.
	 */
	public void backFromPersonnelPage() {
		log.info("*** Back to perssonel page ******");
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			calculateCapital();
		}
		getManagedBean().setRedirectToPersonPage(false);
		getManagedBean().setValidSmUserLimit(false);
		if (getCurrAuthPersonnelCount() >= SmRuleHelper.getInstance()
				.getMaxAdditionalMofAuthorizedUser()) {
			getManagedBean().setValidSmUserLimit(true);
		}
		RequestContext.getCurrentInstance().reset(":form:tt:shareHolderDialog");
	}

	/**
	 * Assign selected SmPersonnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		getManagedBean().setPopupAction("U");
		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		SmApplicationUtil.assignSelShareHolder(shareHolderBean,
				getManagedBean(), currentTab);
		// start #21946
		if (shareHolderBean.getIdentityResponseCode() != null) {
			if (shareHolderBean.getIdentityResponseCode().equals("70019")) {
				getManagedBean().setSelectedPersonnelDtl(shareHolderBean);
			}
		}
		// end #21946
		// set personnel field change value
		SmFieldChangeUtil.initPersonnelChangeField(getManagedBean());
		//start #31479
		//#32014 - START
		if (SmCommonUtil.getInstance().ssmActive()
				&& businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupplier().getRegNo(), this
						.getManagedBean().getSmSupplier().getBusinessType()) && this.getManagedBean().isSupplierUser()) {
		//#32014 - END
			SsmReqDTO ssmReqDTO = new SsmReqDTO();
			ssmReqDTO.setRegNo(this.getManagedBean().getSmSupplier().getRegNo());
			ssmReqDTO.setBusinessType(this.getManagedBean().getSmSupplier().getBusinessType());
			//29582
			ssmReqDTO.setSmSupp(this.getManagedBean().getSmSupplier());
			if(SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())){
				ssmReqDTO.setBasicAcc(true);
			}else{
				ssmReqDTO.setBasicAcc(false);
			}
			SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
			SmPersonnel personnelSSM = (SmPersonnel) EntityHelper
					.getEntityBySinglePropertyFromList(shareHolderBean.getIdentificationNo(),
							ssmResDTO.getSmPersonnelAll(), "identificationNo");
			// start - #32014
			if (personnelSSM!=null && personnelSSM.getIdentificationType() != null && this.getManagedBean().getSelectedShareHolder()!=null) {
				log.debug(" #32014 - personnelSSM.getIdentificationType() - " + personnelSSM.getIdentificationType());
				log.debug(" #32014 - personnelSSM.getIdentificationTypeId() - " + personnelSSM.getIdentificationTypeId());
				this.getManagedBean().getSelectedShareHolder()
						.setIdentificationType(personnelSSM.getIdentificationType());
				this.getManagedBean().getSelectedShareHolder()
						.setIdentificationTypeId(personnelSSM.getIdentificationTypeId());
			}
			if (personnelSSM != null && personnelSSM.getSmPersonnelAddresses() != null // #32014
					&& personnelSSM.getSmPersonnelAddresses().size() > 0) {
				for (SmPersonnelAddress smpersonAddress : personnelSSM.getSmPersonnelAddresses()) {
					if (SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES.equals(smpersonAddress.getAddressType())
							&& (getManagedBean().getShareHolderResdAddr().getSmAddress() == null || getManagedBean()
									.getShareHolderResdAddr().getSmAddress().getAddressId() == null)) {
						initializeSmAddressChilds(smpersonAddress.getSmAddress());
						PmCountry defaultCountry = new PmCountry();
						if ((smpersonAddress.getSmAddress().getPmCountry() == null || smpersonAddress.getSmAddress()
								.getPmCountry().getCountryCode().equals(ParameterConst.COUNTRY_UNKNOWN_CODE))
								&& shareHolderBean.getIdentificationType() != null
								&& ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(shareHolderBean
										.getIdentificationType().getParameterCode()) &&
										smpersonAddress.getSmAddress().getPmState() == null) {
							defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(ParameterConst.COUNTRY_UNKNOWN_CODE);
						} else if(smpersonAddress.getSmAddress().getPmCountry() != null && smpersonAddress.getSmAddress().getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) ){
							defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
						} else {
							defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_UNKNOWN_CODE);
						}
						smpersonAddress.getSmAddress().setPmCountry(defaultCountry);
						if (smpersonAddress.getSmAddress().getPmCountry() != null) {
							smpersonAddress.getSmAddress().setCountryId(
									smpersonAddress.getSmAddress().getPmCountry().getCountryId());
						}
						//#34799 - start
						PmState curState = smpersonAddress.getSmAddress().getPmState();
						onChangeCountry(smpersonAddress.getSmAddress(),
								SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
						smpersonAddress.getSmAddress().setPmState(curState);
						//#34799 - end
						if (smpersonAddress.getSmAddress().getPmState() != null) {
							smpersonAddress.getSmAddress().setStateId(
									smpersonAddress.getSmAddress().getPmState().getStateId());
						}
						onChangeState(smpersonAddress.getSmAddress(),
								SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
						getManagedBean().getShareHolderResdAddr().setSmAddress(smpersonAddress.getSmAddress());
					}
				}
			}
			if (shareHolderBean.getSmPersonnelAddresses() != null
					&& shareHolderBean.getSmPersonnelAddresses().size() > 0) {
				for (SmPersonnelAddress smpersonAddress : shareHolderBean.getSmPersonnelAddresses()) {
					if (SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES.equals(smpersonAddress.getAddressType())) {
						SmAddress smAddressRes = getManagedBean().getShareHolderResdAddr().getSmAddress();
						initializeSmAddressChilds(smpersonAddress.getSmAddress());
						
						if(smAddressRes.getPmCountry().getCountryCode().equals(SupplierManagementConst.COUNTRY_UNKNOWN_CODE) && smpersonAddress.getSmAddress().getPostcode()!=null){
							smAddressRes.setPmCountry(smpersonAddress.getSmAddress().getPmCountry());
							smAddressRes.setCountryId(smpersonAddress.getSmAddress().getPmCountry().getCountryId());
						}
						//#34799 - start
						PmState curState = smAddressRes.getPmState();
						onChangeCountry(smAddressRes, SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
						smAddressRes.setPmState(curState);
						//#34799 - end
						if(smpersonAddress.getSmAddress().getPmState() != null){
							smAddressRes.setPmState(smpersonAddress.getSmAddress().getPmState());
							smAddressRes.setStateId(smpersonAddress.getSmAddress().getPmState().getStateId());
						}
						onChangeState(smAddressRes, SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
						if (smpersonAddress.getSmAddress().getPmDivision() != null) {
							smAddressRes.setPmDivision(smpersonAddress.getSmAddress().getPmDivision());
							smAddressRes.setDivisionId(smpersonAddress.getSmAddress().getPmDivision().getDivisionId());
						}

						if (smpersonAddress.getSmAddress().getPmDistrict() != null) {
							onChangeDistrictByDivision(smAddressRes, SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
							smAddressRes.setPmDistrict(smpersonAddress.getSmAddress().getPmDistrict());
							smAddressRes.setDistrictId(smpersonAddress.getSmAddress().getPmDistrict().getDistrictId());
						}
							
						onChangeDistrict(smAddressRes, SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
						if (smpersonAddress.getSmAddress().getPmCity() != null) {
							smAddressRes.setPmCity(smpersonAddress.getSmAddress().getPmCity());
							smAddressRes.setCityId(smpersonAddress.getSmAddress().getPmCity().getCityId());
						}
						if (smAddressRes.getPostcode().isEmpty() || smAddressRes.getPostcode() == null || smAddressRes.getPostcode() == ""){
							smAddressRes.setPostcode(smpersonAddress.getSmAddress().getPostcode());
						}
					}
				}
			}
			//#35145
			if(personnelSSM != null){
				getManagedBean().getSelectedShareHolder().setOtherRaceSsm(personnelSSM.isOtherRaceSsm());
			}
		}
		// end - #32014
		//end #31479
		
		//START 27821
		if(shareHolderBean.getIdentificationType() != null && ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(shareHolderBean.getIdentificationType().getParameterCode())){
			List<PmCountry> countryLs = SmApplicationUtil.retrievePmCountryList();			
			
			for (Iterator<PmCountry> iterator = countryLs.iterator(); iterator.hasNext(); ) {
				PmCountry country = iterator.next();
				if(country.getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)){
			        iterator.remove();
			        break;
			    }
			}
			getManagedBean().setCountryLs(countryLs);
			
		}else{
			getManagedBean().setCountryLs(SmApplicationUtil.retrievePmCountryList());
		}
		//END 27821
		//#34138 - start - reset all state lists
		if (getManagedBean().getShareHolderPermAddr().getSmAddress().getStateId() == null){
		SmApplicationUtil.getInstance().populateStateByCountry(
				getManagedBean().getShareHolderPermAddr().getSmAddress(), 
				SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL, getManagedBean());
		}
		if (getManagedBean().getShareHolderOffAddr().getSmAddress().getStateId() == null){
		SmApplicationUtil.getInstance().populateStateByCountry(
				getManagedBean().getShareHolderOffAddr().getSmAddress(), 
				SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL, getManagedBean());
		}
		if (getManagedBean().getShareHolderMailAddr().getSmAddress().getStateId() == null){
		SmApplicationUtil.getInstance().populateStateByCountry(
				getManagedBean().getShareHolderMailAddr() .getSmAddress(), 
				SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL, getManagedBean());
		}
		if (getManagedBean().getShareHolderResdAddr().getSmAddress().getStateId() == null){
		SmApplicationUtil.getInstance().populateStateByCountry(
				getManagedBean().getShareHolderResdAddr().getSmAddress(), 
				SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL, getManagedBean());
		}
		//#34138 - end
	}
	
	//start #31479
	public void initializeSmAddressChilds(SmAddress addr) {
		if (addr.getPmCountry() != null) {
			addr.getPmCountry().getCountryId();
			addr.setCountryId(addr.getPmCountry().getCountryId());
		}
		if (addr.getPmCity() != null) {
			addr.getPmCity().getCityId();
			addr.setCityId(addr.getPmCity().getCityId());
		}
		if (addr.getPmDistrict() != null) {
			addr.getPmDistrict().getDistrictId();
			addr.setDistrictId(addr.getPmDistrict().getDistrictId());
		}
		if (addr.getPmState() != null) {
			addr.getPmState().getStateId();
			addr.setStateId(addr.getPmState().getStateId());
		}
		if (addr.getPmDivision() != null) {
			addr.getPmDivision().getDivisionId();
			addr.setDivisionId(addr.getPmDivision().getDivisionId());
		}
	}
	//end #31479

	/**
	 * Getys Authorized Personnel Account Status
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public String getPersonnelActivationStatus(SmPersonnel smPersonnel,
			SmSupplier smSupplier) {
		return SmApplicationUtil.getPersonnelActivationStatus(smPersonnel,
				smSupplier);
	}

	@SuppressWarnings("unused")
	private void disableJustification(SmPersonnel shareHolderBean) {
		getManagedBean().setDisabledJustification(false);
		if (getManagedBean().getSmAppl() != null && shareHolderBean != null
				&& shareHolderBean.getSmAppl() != null) {
			if (getManagedBean().getSmAppl().getApplId()
					.compareTo(shareHolderBean.getSmAppl().getApplId()) == 0) {
				getManagedBean().setDisabledJustification(true);
			}
		}
	}

	/**
	 * Adds or update addresses information to the selected SmPersonnel
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateShareHolderAddress(SmPersonnel shareHolderBean)
			throws ParseException {

		// validate address if role is staff
		validateSaveAddressForRoleStaff();

		if (shareHolderBean.getAuthorizedFlg() == 0) {
			getManagedBean().getShareHolderMailAddr().setSameAsPerm(true);
		}

		if (!shareHolderBean.isDirector()
				&& !shareHolderBean.isHigherManagement()
				   && !shareHolderBean.isEquityOwner()) {
			getManagedBean().getShareHolderOffAddr().setSameAsPerm(true);
			getManagedBean().getShareHolderResdAddr().setSameAsPerm(true);
		}

		// clone the addrs start
		SmApplicationUtil.cloneShareHolderAddress(getManagedBean());
		// clone the addrs end

		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderPermAddr());
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderMailAddr());
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderOffAddr());
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderResdAddr());
		} else {

			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				processUpdatedAddressList(addr, shareHolderBean);
			}
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderPermAddr());
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderMailAddr());
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderOffAddr());
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderResdAddr());
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);

	}

	public void onTickSameAsAddress(SmAddress srcAddr,
			SmPersonnelAddress destAddr) {

		if (srcAddr == null || StringUtils.isBlank(srcAddr.getAddress1())
				|| srcAddr.getPmState() == null
				|| srcAddr.getPmState().getStateId() == null
				|| srcAddr.getPmDistrict() == null
				|| srcAddr.getPmDistrict().getDistrictId() == null
				|| srcAddr.getPmCity() == null
				|| srcAddr.getPmCity().getCityId() == null
				|| StringUtils.isBlank(srcAddr.getPostcode())) {
			String addrType = SmCommonUtil.getMessage(
					"lbl.sm.permenantaddress", null);
			if (destAddr.getAddressType().equalsIgnoreCase(
					SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
				addrType = SmCommonUtil.getMessage("lbl.sm.companyaddress",
						null);
			}
			destAddr.setSameAsPerm(false);
			throw new EpException(MessageConst.ERROR_722656,
					new String[] { addrType });
		} else {
			if (destAddr.isSameAsPerm()) {
				String panelName = StringUtils.EMPTY;
				destAddr.setSmAddress(SmApplicationUtil.cloneAsPermenantAddr(
						srcAddr, destAddr.getSmAddress(), destAddr,
						getManagedBean().getDefPmCountry()));
				if (destAddr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
					panelName = SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL;

				}
				if (destAddr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					panelName = SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL;

				}
				if (destAddr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					panelName = SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL;

				}
				if (destAddr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					panelName = SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL;
				}

				SmApplicationUtil.getInstance()
						.assignStateDistAndCityForSelVal(getManagedBean(),
								destAddr.getSmAddress(), panelName);
			} else {
				destAddr.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			}
		}
	}

	/**
	 * Builds the role list.
	 */
	public void buildRoleList(SmPersonnel personnel) {
		getManagedBean().setePRoleList(
				SmApplicationUtil.getPersonnelRoleItems(personnel,
						getManagedBean().getSmAppl(), getManagedBean()
								.getAuthVO()));
	}

	/**
	 * Add or create a new SmPersonnel bank object
	 * 
	 * @param personelType
	 *            - the personel type to define equity or not
	 */
	public void assignNewShareHolder() {
		/*log.info("fatintestadd" + this.getManagedBean().getSmAppl().getSmPersonnels().size());
		
		for (SmPersonnel smPersonnel : this.getManagedBean().getSmAppl().getSmPersonnels()){
			
			if (smPersonnel.isEquityOwner()== true && 
					smPersonnel.isManagement()== true &&
					smPersonnel.isClerical() == true){
				
				log.info("equityowner+management+clerical" + smPersonnel.getName());
				this.getManagedBean().getSelectedShareHolder()
				.setDisableStaff(true);
			}
			
			if (smPersonnel.isClerical()== true &&
					(smPersonnel.isEquityOwner()== false ||
					smPersonnel.isManagement()== false)){

				log.info("staff" + smPersonnel.getName());
				this.getManagedBean().getSelectedShareHolder()
				.setDisableStaff(false);
			}
		}*/
		if (getManagedBean().getIdentificationTypes() == null) {
			getManagedBean()
					.setIdentificationTypes(getPmIdTypeDropdownValues());
		}
		//#34138
		getManagedBean().setCountryLs(SmApplicationUtil.retrievePmCountryList());
		//#29315
		getManagedBean().setFlagMandatoryEpfNo(false);
		getManagedBean().setPopupAction("N");
		SmApplicationUtil.initNewSmPersonnel(getManagedBean(), currentTab);

	}

	/**
	 * Delete or remove selected personnel from the SmPeronnel list of the
	 * application.
	 * 
	 * @param personType
	 *            - the person type
	 */
	public void deleteSelectedShareHolders(String personType,
			boolean confirmDelete) {
		int countDeleteAuthPersonnel = SmApplicationUtil
				.validateDeletePersonnel(personType, getManagedBean());

		if (!confirmDelete && countDeleteAuthPersonnel > 0) {
			if (personType
					.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_EQUITY)) {
				RequestContext.getCurrentInstance().execute(
						"deleteAuthEquityConfirmDialog.show()");
			} else {
				RequestContext.getCurrentInstance().execute(
						"deleteAuthPersConfirmDialog.show()");
			}
		} else {
			if (getManagedBean().getSelectedShareHolders().size() > 0) {
				if (personType
						.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_EQUITY)) {
					validatePaidCaptialAmt(false);
				}
				for (SmPersonnel shr : getManagedBean()
						.getSelectedShareHolders()) {
					getManagedBean().getSmAppl().getSmPersonnels().remove(shr);
					getManagedBean().getSmapplShareHolders().remove(shr);
				}
				saveOrUpdateAppl();
				SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
						getManagedBean().getSmAppl(), getManagedBean()
								.getPrevApprovedPersonnels(), getManagedBean()
								.isDisAllowOwnerEdit());
				SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
						.getSmAppl());
				getManagedBean().getSelectedShareHolders().clear();
				if (!personType
						.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_EQUITY)) {
					calcPersonnelListCount();
				} else {
					calculateCapital();
				}
			} else {
				throw new EpException(MessageConst.ERROR_722534,
						new String[] { SmCommonUtil.getMessage(
								"lbl.sm.personnel", null) });
			}
		}

	}

	/**
	 * Confirmation on deleting authorized personnel
	 */
	public void confirmDeleteAuthorizedPersonnel() {
		deleteSelectedShareHolders(getManagedBean().getPersonType(), true);
	}

	// -- Process Share Holder Personnel End
	// -- Process Common for both Personnel and Share Holder Start --
	/**
	 * Add or create a new sm staff experience object
	 */
	public void addShrHldrJobFunction() {
		assignValReqdToTrue();
		SmApplicationUtil.addNewPersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				new SmStaffExperience(), "selectedJobFunction",
				"equiJobFunctionInfo");
	}

	/**
	 * Assign selected job function - SmStaffExperience object to managed bean
	 * for editing the information.
	 * 
	 * @param exp
	 *            - SmStaffExperience object
	 */
	public void assignSelShrHldrJobFunc(SmStaffExperience exp) {
		SmApplicationUtil.assignSelShrHldrJobFunc(exp, getManagedBean());
		assignValReqdToTrue();
	}

	/**
	 * Add or update selected job function info to SmStaffExperience list of the
	 * selected SmPersonnel.
	 */
	public void addOrUpdateJobFunctions() {
		SmApplicationUtil.addOrUpdateJobFunctions(getManagedBean());
		assignValReqdToFalse();
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());
	}

	public Long getMaxExpCode() {
		return SmApplicationUtil.getPersonnelMaxExpCode(getManagedBean()
				.getSelectedShareHolder());
	}

	/**
	 * Cancel add or update functionality and close the job function information
	 * dialog
	 */
	public void cancelJobFunctions() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(
				":form:tt:equiJobFunctionInfo");
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.hide()");
	}

	/**
	 * Delete or remove selected job functions from the SmStaffExperience list
	 * of the selected SmPersonnel
	 */
	public void deleteSelectedShrHldrJobFunction() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrJobFunctions", "smStaffExperiences",
				MessageConst.ERROR_722534,
				new String[] { SmCommonUtil.getMessage(
						"lbl.sm.personnel.dash.jobfunction", null) });

	}

	/**
	 * Add or create a new related company object
	 */
	public void addShrHldrRelatedCompany() {
		SmApplicationUtil.addNewPersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				new SmRelatedCompany(), "selectedRelatedCompany",
				"equiRelCompInfo");
		assignValReqdToTrue();
	}

	/**
	 * Assign selected related company - SmRelatedCompany object to managed bean
	 * for editing the information.
	 * 
	 * @param com
	 *            -SmRelatedCompany object
	 */
	public void assignSelShrHldrRelatedCompany(SmRelatedCompany com) {
		SmApplicationUtil.assignSelShrHldrRelatedCompany(com, getManagedBean());
		assignValReqdToTrue();
	}

	/**
	 * Add or update selected related company info to SmRelatedCompany list of
	 * the selected SmPersonnel.
	 */
	public void addOrUpdateRelatedCompanies() {
		SmApplicationUtil.addOrUpdateRelatedCompanies(getManagedBean());
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());
		assignValReqdToFalse();
	}

	/**
	 * Delete or remove selected related companies from the SmRelatedCompany
	 * list of the selected SmPersonnel
	 */
	public void deleteSelectedShrHldrRelatedCompanies() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrRelatedCompanies", "smRelatedCompanies",
				MessageConst.ERROR_722534, new String[] { SmCommonUtil
						.getMessage("lbl.sm.personnel.dash.relatedcompany",
								null) });
	}

	/**
	 * Cancel add or update functionality and close the related company
	 * information dialog
	 */
	public void cancelShrHldrRelatedCompanies() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equiRelCompInfo");
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.hide()");
	}

	/**
	 * Add or create a new staff academics object for selected Smpersonnel
	 */
	public void addShrHldrAcademicQualification() {
		SmStaffAcademic aca = new SmStaffAcademic();
		aca.setPmEduField(new PmEduField());
		// aca.setPmEduLevel(new PmEduLevel());
		aca.setQualificationId(new PmParameter());

		aca.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedStaffAcademic(aca);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equiAcadInfo.show()");
	}

	/**
	 * Assign selected staff academics - SmStaffAcademic object to managed bean
	 * for editing the information.
	 * 
	 * @param aca
	 *            -SmStaffAcademic
	 */
	public void assignSelShrHldrAcademics(SmStaffAcademic aca) {
		SmApplicationUtil.assignSelShrHldrAcademics(aca, getManagedBean());
		assignValReqdToTrue();
	}

	/**
	 * Delete or remove selected records from the SmStaffAcademics list of the
	 * selected SmPersonnel.
	 */
	public void deleteSelectedShrHldrAcademics() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrAcademics", "smStaffAcademics",
				MessageConst.ERROR_722534, new String[] { SmCommonUtil
						.getMessage("lbl.sm.personnel.dash.academics", null) });
	}

	/**
	 * Cancel add or update functionality and close the staff academics
	 * information dialog
	 */
	public void cancelShrHldrAcademic() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equiAcadInfo");
		RequestContext.getCurrentInstance().execute("equiAcadInfo.hide()");
	}

	/**
	 * Add or update selected staff academics info to SmStaffAcademic list of
	 * the selected SmPersonnel.
	 */
	public void addOrUpdateShrHldrAcademic() {
		SmApplicationUtil.addOrUpdateShrHldrAcademic(getManagedBean());
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());
		assignValReqdToFalse();
	}

	/**
	 * Add or create a new shr hldr prof body object
	 */
	public void addShrHldrProfBody() {
		SmAssociationBody assBody = new SmAssociationBody();
		assBody.setPmCertBody(new PmCertBody());
		assBody.setRevNo(1);
		assBody.setSmAppl(getManagedBean().getSmAppl());
		assBody.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedStfPrfBdy(assBody);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.show()");
	}

	/**
	 * Assign selected professional body - SmAssocication object to managed bean
	 * for editing the information.
	 * 
	 * @param asb
	 *            -SmAssociationBody object
	 */
	public void assignSelShrHldrProfBody(SmAssociationBody asb) {
		SmApplicationUtil.assignSelShrHldrProfBody(asb, getManagedBean());
		assignValReqdToTrue();
	}

	/**
	 * Cancel add or update functionality and close the professional body
	 * information dialog
	 */
	public void cancelShrHldrProfBody() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(
				":form:tt:equiShrProfBeanInfo");
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.hide()");
	}

	/**
	 * Add or update selected prof body info to SmAssocationBody list of the
	 * selected SmPersonnel.
	 */
	public void addOrUpdateShrHldrProfBody() {
		SmApplicationUtil.addOrUpdateShrHldrProfBody(getManagedBean());
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());
		assignValReqdToFalse();
	}

	/**
	 * Delete or remove selected prof bodies from the SmAssocationBody list of
	 * the selected SmPersonnel.
	 */
	public void deleteSelectedShrHldrProfBodies() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrProfBodies", "smAssociationBodies",
				MessageConst.ERROR_722534, new String[] { SmCommonUtil
						.getMessage("lbl.sm.personnel.dash.profbody", null) });
	}

	/**
	 * Load cert body from DB based on selected id and set to PmCertBody object
	 */
	public void onChangeProfBody() {
		CertBodyReqDTO reqDTO = new CertBodyReqDTO();
		reqDTO.setCertBodyId(getManagedBean().getSelectedStfPrfBdy()
				.getPmCertBody().getCertBodyId());
		PmCertBody selCertBody = SmServiceLookup.getCertBodySO()
				.retrievePmCertBodyDtlById(reqDTO).getCertBody();
		getManagedBean().getSelectedStfPrfBdy().setPmCertBody(selCertBody);
	}

	/**
	 * Assign personnel for sm supplier branch.
	 * 
	 * @param brn
	 *            - SmSupplierBranch object
	 */
	public void assignPersonForBranch(SmSupplierBranch brn) {
		SmCommonUtil.getInstance().setUserDetails(brn);
		SmSupplierBranch br = new SmSupplierBranch();
		for (int i = 0; i < getManagedBean().getSmAppl()
				.getSmSupplierBranches().size(); i++) {
			br = getManagedBean().getSmAppl().getSmSupplierBranches().get(i);
			if (br.getSupplierBranchId().equals(brn.getSupplierBranchId())) {
				getManagedBean().getSmAppl().getSmSupplierBranches()
						.set(i, brn);
				break;
			}
		}
		saveOrUpdateAppl();
		calcPersonnelListCount();

	}

	/**
	 * Assign selected personnel identity type.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 */
	public void assignPersonIdentType(AjaxBehaviorEvent e) {
		SmApplicationUtil
				.setSelectedPersonnelIdentificationType(getManagedBean()
						.getSelectedShareHolder());
	}

	/**
	 * Load selected staff academic qualification
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void assignQualification(AjaxBehaviorEvent e) {
		SmApplicationUtil.assignQualification(getManagedBean()
				.getSelectedStaffAcademic());
	}

	/**
	 * Assign selected personnel nationality.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 */
	public void assignPersonNationality(AjaxBehaviorEvent e) {
		SmApplicationUtil.setSelectedPersonnelNationality(getManagedBean());
	}

	/**
	 * Assign selected personnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public void assignSelShareHolderAddress(SmPersonnel shareHolderBean) {
		SmApplicationUtil.assignSelShareHolder(shareHolderBean,
				getManagedBean(), currentTab);
	}

	// -- Process Common for both Personnel and Share Holder Start --
	// -- Process Personnel Start
	// -- Process Personnel End

	// Process Bank Info Start
	/**
	 * Add or update selected bank info to SmSupplierBanks of the application.
	 */
	public void addOrUpdateBankInfoToSmAppl() {
		getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
		if (!StringUtils.isNumeric(getManagedBean().getSelectedBank()
				.getAccountNo())) {
			throw new EpException(MessageConst.ERROR_722616);
		}
		
		// #34166 - Start
		if (getManagedBean().getSelectedBank().getSignatureInstruction().contains("<")
				|| getManagedBean().getSelectedBank().getSignatureInstruction().contains(">")) {
			StringBuilder sb = new StringBuilder();
			Locale locale = new FacesScopes().getUserLocale();
			sb.append(ResourceBundleUtils.getMessage("lbl.sm.bankDtls.chequesign.tnc",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			throw new EpException(MessageConst.ERROR_775191, new String[] { sb.toString().toLowerCase() });
		}
		// #34166 - End
		
		if (getManagedBean().isPendingTransaction()){
			getManagedBean().setPendingTransaction(false);			
		}

		if (SmApplicationUtil.checkValidBank(
				getManagedBean().getSelectedBank(), getManagedBean()
						.getSmAppl())) {
			// set cheque signatories
			getManagedBean().getSelectedBank().getSmChequeSignatories().clear();
			for (Long perId : getManagedBean().getChequeSingPerId()) {
				for (SmPersonnel sm : getManagedBean().getSmAppl()
						.getSmPersonnels()) {
					if (sm.getPersonnelId().equals(perId)) {
						SmChequeSignatory ch = new SmChequeSignatory();
						ch.setSmPersonnel(sm);
						ch.setSmSupplierBank(getManagedBean().getSelectedBank());
						getManagedBean().getSelectedBank()
								.getSmChequeSignatories().add(ch);
					}
				}
			}

			// set pmFinancialOrg
			for (PmFinancialOrg org : getManagedBean().getFinOrgLs()) {
				if (getManagedBean().getSelectedBank().getPmFinancialOrg()
						.getFinancialOrgId().equals(org.getFinancialOrgId())) {
					getManagedBean().getSelectedBank().setPmFinancialOrg(org);

				}
			}

			// set associatedBranches
			if (getManagedBean().getSelectedBankBranchIds() != null
					&& getManagedBean().getSelectedBankBranchIds().length > 0) {

				// validate branch bank account limit
				int maxPaymentBankForBranch = SmRuleHelper.getInstance()
						.getMaxPaymentBankForEachBranch();
				int paymntBankCount = 0;

				for (Long brId : getManagedBean().getSelectedBankBranchIds()) {
					for (SmSupplierBranch br : getManagedBean().getSmAppl()
							.getSmSupplierBranches()) {
						if (br.getSupplierBranchId().equals(brId)) {

							paymntBankCount = SmApplicationUtil
									.getBranchPaymentBankCount(br);

							SmSupplierBranchBank brBank = SmApplicationUtil
									.getBranchBankFromListByBranchIdAndBankId(
											br.getSupplierBranchId(),
											getManagedBean().getSelectedBank()
													.getSupplierBankId(),
											getManagedBean().getSelectedBank()
													.getSmSupplierBranchBanks());
							// newly added
							if (brBank == null) {
								if (getManagedBean().getSelectedBank()
										.getAccountPurpose() != null
										&& (getManagedBean().getSelectedBank()
												.getAccountPurpose()
												.equals("B") || getManagedBean()
												.getSelectedBank()
												.getAccountPurpose()
												.equals("P"))) {
									paymntBankCount++;
								}

								brBank = new SmSupplierBranchBank();
								brBank.setSmSupplierBranch(br);
								brBank.setSmSupplierBank(getManagedBean()
										.getSelectedBank());
								getManagedBean().getSelectedBank()
										.getSmSupplierBranchBanks().add(brBank);
							}

							// validate branch bank account limit
							if (paymntBankCount > maxPaymentBankForBranch) {
								throw new EpException(
										MessageConst.ERROR_722620,
										new String[] { br.getBranchName(),
												maxPaymentBankForBranch + "" });
							}
						}
					}
				}

				// remove if not selected
				if (!SmCommonUtil.isCollectionEmpty(getManagedBean()
						.getSelectedBank().getSmSupplierBranchBanks())) {
					Iterator<SmSupplierBranchBank> latestBrBankIt = getManagedBean()
							.getSelectedBank().getSmSupplierBranchBanks()
							.iterator();
					while (latestBrBankIt.hasNext()) {
						SmSupplierBranchBank latestBrBank = latestBrBankIt
								.next();

						if (latestBrBank.getSmSupplierBranch() != null
								&& !SmApplicationUtil.isBranchIdExistInList(
										latestBrBank.getSmSupplierBranch()
												.getSupplierBranchId(),
										getManagedBean()
												.getSelectedBankBranchIds())) {
							latestBrBankIt.remove();
						}
					}
				}

			} else {
				if (!getManagedBean().getSelectedBank().getAccountPurpose()
						.equals("R")
						&& getManagedBean().getSelectedBank().getForHqFlg() == Integer
								.parseInt(ParameterConst.FLAG_NO)) {
					// At least one branch or HQ is required for bank account
					// with account purpose payment or both (payment and
					// registration)
					throw new EpException(MessageConst.ERROR_722619);
				} else {
					getManagedBean().getSelectedBank()
							.getSmSupplierBranchBanks().clear();
				}
			}

			// validate HQ
			SmApplicationUtil.validateBankLimitForHq(getManagedBean()
					.getSmAppl(), getManagedBean().getSelectedBank());
			
			SmCommonUtil.getInstance().setUserDetails(getManagedBean()
					.getSelectedBank());
			EntityHelper.addOrUpdateEntityToList(getManagedBean()
					.getSelectedBank(), getManagedBean().getSmAppl()
					.getSmSupplierBanks());

			assignValReqdToFalse();
			saveOrUpdateAppl();

			// reload cheque signatory table if needed
			if (!EntityHelper.isCollectionEmpty(getManagedBean()
					.getSelectedSignatoryBankAccts())) {
				List<SmSupplierBank> suppBanks = new ArrayList<SmSupplierBank>();
				SmSupplierBank bankTmp = null;
				for (SmSupplierBank bank : getManagedBean()
						.getSelectedSignatoryBankAccts()) {
					bankTmp = (SmSupplierBank) EntityHelper
							.getEntityBySinglePropertyFromList(bank
									.getAccountNo(), getManagedBean()
									.getSmAppl().getSmSupplierBanks(),
									"accountNo");
					if (bankTmp != null) {
						suppBanks.add(bankTmp);
					}
				}
				getManagedBean().setSelectedSignatoryBankAccts(suppBanks);
			}
			getManagedBean().setPendingTransaction(false);
			RequestContext.getCurrentInstance().execute("bankDialog.hide()");
		}
	}

	/**
	 * Cancel add or update functionality and close the bank information dialog
	 */
	public void cancelSmBankFunctions() {
		assignValReqdToFalse();
		getManagedBean().setPendingTransaction(false);
		RequestContext.getCurrentInstance().reset(":form:tt:bankDialog");
		RequestContext.getCurrentInstance().execute("bankDialog.hide()");
	}

	/**
	 * Assign selected bank object to managed bean for editing the information.
	 * 
	 * @param shareBankBean
	 *            - SmSupplierBank object
	 */
	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().setChequeSingPerId(null);
		getManagedBean().setSelectedBankBranchIds(null);

		// assign cheque signatories
		Long[] chequSigns = new Long[shareBankBean.getSmChequeSignatories()
				.size()];
		for (int i = 0; i < chequSigns.length; i++) {
			chequSigns[i] = shareBankBean.getSmChequeSignatories().get(i)
					.getSmPersonnel().getPersonnelId();

		}
		getManagedBean().setChequeSingPerId(chequSigns);

		// assign associated branches
		Long[] bankBranchesIds = null;
		if (SmCommonUtil.isCollectionEmpty(shareBankBean
				.getSmSupplierBranchBanks())) {
			bankBranchesIds = new Long[0];
		} else {
			bankBranchesIds = new Long[shareBankBean.getSmSupplierBranchBanks()
					.size()];
			for (int i = 0; i < bankBranchesIds.length; i++) {
				bankBranchesIds[i] = shareBankBean.getSmSupplierBranchBanks()
						.get(i).getSmSupplierBranch().getSupplierBranchId();

			}
		}
		getManagedBean().setSelectedBankBranchIds(bankBranchesIds);

		if (shareBankBean.getSmAddress() != null
				&& shareBankBean.getSmAddress().getPmCountry().getCountryId() != null
				&& shareBankBean.getSmAddress().getPmState().getStateId() != null
				&& shareBankBean.getSmAddress().getPmDistrict().getDistrictId() != null) {
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(), shareBankBean.getSmAddress(),
					SupplierManagementConst.SM_BANK_INFO_PANEL);
		} else {
			shareBankBean.setSmAddress(SmApplicationUtil
					.setDefaultAddr(getManagedBean().getDefPmCountry()));
			SmApplicationUtil.getInstance().populateStateByCountry(
					shareBankBean.getSmAddress(),
					SupplierManagementConst.SM_BANK_INFO_PANEL,
					getManagedBean());
		}
		getManagedBean().setSelectedBank(
				(SmSupplierBank) SerializationUtils.clone(shareBankBean));
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestBank(
					(SmSupplierBank) EntityHelper
							.getEntityByMultiplePropertiesFromList(
									shareBankBean, getManagedBean()
											.getLatestAppl()
											.getSmSupplierBanks(),
									new String[] { "accountNo",
											"accountPurpose" }, null, null));
		}
		retrieveBankFinOrgListByType();
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("bankDialog.show()");
	}

	/**
	 * Delete or remove selected banks from the SmSupplierBank list of the
	 * application.
	 */
	public void deleteSelectedBanks() {
		if (getManagedBean().getSelectedBanks().size() > 0) {
			for (SmSupplierBank bank : getManagedBean().getSelectedBanks()) {
				// remove cheque signatories
				bank.getSmChequeSignatories().clear();

				// remove associate branches
				if (!SmCommonUtil.isCollectionEmpty(bank
						.getSmSupplierBranchBanks())) {
					bank.getSmSupplierBranchBanks().clear();
				}

				getManagedBean().getSmAppl().getSmSupplierBanks().remove(bank);
			}
			saveOrUpdateAppl();
			getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());

			if (!EntityHelper.isCollectionEmpty(getManagedBean()
					.getSelectedSignatoryBankAccts())) {
				getManagedBean().getSelectedSignatoryBankAccts().removeAll(
						getManagedBean().getSelectedBanks());
			}
			getManagedBean().getSelectedBanks().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.supplier.bank", null) });
		}
	}

	/**
	 * View signatories for selected SmSupplierBank.
	 * 
	 * @param signAcctBean
	 *            -SmSupplierBank object
	 */
	public void viewSignatories() {
		if (EntityHelper.isCollectionEmpty(getManagedBean().getSelectedBanks())) {
			throw new EpException(MessageConst.ERROR_722640);
		} else {
			getManagedBean().setSelectedSignatoryBankAccts(
					new ArrayList<SmSupplierBank>());
			for (SmSupplierBank bank : getManagedBean().getSelectedBanks()) {
				if (!EntityHelper.isCollectionEmpty(bank
						.getSmChequeSignatories())) {
					getManagedBean().getSelectedSignatoryBankAccts().add(bank);
				}
			}

			if (CollectionUtils.isNotEmpty(getManagedBean()
					.getSelectedSignatoryBankAccts())) {

				// sort by bank
				Collections.sort(getManagedBean()
						.getSelectedSignatoryBankAccts(),
						new Comparator<SmSupplierBank>() {
							@Override
							public int compare(final SmSupplierBank s1,
									final SmSupplierBank s2) {
								String bankName1 = StringUtils.EMPTY;
								String bankName2 = StringUtils.EMPTY;

								if (s1.getPmFinancialOrg() != null) {
									bankName1 = s1.getPmFinancialOrg()
											.getFinOrgName();
								}
								if (s2.getPmFinancialOrg() != null) {
									bankName2 = s2.getPmFinancialOrg()
											.getFinOrgName();
								}
								return StringUtil.upperCase(bankName1)
										.compareTo(bankName2);
							}
						});

				// sort by acc no
				Collections.sort(getManagedBean()
						.getSelectedSignatoryBankAccts(),
						new Comparator<SmSupplierBank>() {
							@Override
							public int compare(final SmSupplierBank s1,
									final SmSupplierBank s2) {
								return s1.getAccountNo().compareTo(
										s2.getAccountNo());
							}
						});
			}

			getManagedBean().getSelectedBanks().clear();

		}
	}

	/**
	 * Invoke myidentity dialog
	 * 
	 * @param SmPersonnel
	 *            per
	 */
	public void viewMyIdentityResponse(SmPersonnel per) {
		getManagedBean().setSelectedShareHolder(per);
		RequestContext.getCurrentInstance().execute("myIdentityDlg.show()");
	}

	/**
	 * Invoke personnel dialog
	 * 
	 * @param SmPersonnel
	 *            per
	 * @param String
	 *            dialogWidgetVar
	 */
	public void invokePersonnelDialog(SmPersonnel per, String dialogWidgetVar) {
		getManagedBean().setSelectedShareHolder(per);
		RequestContext.getCurrentInstance()
				.execute(dialogWidgetVar + ".show()");
	}

	/**
	 * Add or create a new SmSupplier bank object
	 */
	public void assignNewBank() {
		//#12492 - start
		getManagedBean().setShowDifferenceBankInfoAction(true);
		//#12492 - end
		getManagedBean().setPendingTransaction(false);
		getManagedBean().setChequeSingPerId(null);
		getManagedBean().setSelBankBranchId(null);
		getManagedBean().setSelectedBankBranchIds(null);
		SmSupplierBank bank = new SmSupplierBank();
		bank.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		bank.setSmAppl(getManagedBean().getSmAppl());
		bank.setPmFinancialOrg(new PmFinancialOrg());
		bank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		bank.setSmSupplierBranchBanks(new ArrayList<SmSupplierBranchBank>());
		bank.setForHqFlg(1);
		bank.setRevNo(1);
		getManagedBean().setSelectedBank(bank);
		getManagedBean().setBankStates(
				SmCommonUtil.getStateList(getManagedBean().getSelectedBank()
						.getSmAddress().getPmCountry().getCountryId()));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("bankDialog.show()");
	}

	/**
	 * Make a call to the BPM for supplier submitting the application.
	 */
	public void initiateSmApplicationTask() {
		IMofRegistrationSO regSo = null;
		SmAppl bpmAppl = getManagedBean().getSmAppl();
		SmCommonUtil.getInstance().setUserDetails(bpmAppl);
		if (getManagedBean().getSmAppl().isResubmit()) {
			setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_RE_REVIEW);

		} else {
			setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_VERIFICATION);
		}

		getManagedBean().getSmAppl().setApplSubmitDate(new Date());

		if (bpmAppl.getSupportingDocMode() == null) {
			//28771
			bpmAppl.setSupportingDocMode(StringUtils.EMPTY);
		}
		if (!bpmAppl.isActiveAppl()) {
			bpmAppl.setActiveAppl(true);
		}
		String s = "";
		if (getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			s = SupplierManagementConst.SUPP_SUBMIT_CANCREG_APPL;
		} else {
			s = SupplierManagementConst.SUPP_SUBMIT_NORM_APPL;
		}
		for (SmSupplierBranch br : getManagedBean().getSmAppl()
				.getSmSupplierBranches()) {
			if (br.getBranchCode() == null) {
				br.setBranchCode(br.getSupplierBranchId());
			}
		}

		MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
				SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT, s);

		if (getManagedBean().getTaskId() != null
				&& !getManagedBean().getTaskId().equalsIgnoreCase("")) {
			log.info("***** Update the task *******");
			reqDTO.setUpdateTask(true);
			reqDTO.setTaskId(getManagedBean().getTaskId());
		} else {
			reqDTO.setUpdateTask(false);
		}

		if (getManagedBean().getSmAppl().isResubmit()) {
			if (getManagedBean().getSmAppl().getPoUserId() == null) {
				reqDTO.setPoLoginId(SmApplicationUtil.getPOLoginId(
						getManagedBean().getSmSupplier(), getManagedBean()
								.getSmAppl()));
			} else {
				PmUser po = PpCommonUtil.retrieveUserByLoginIdOrUserId(null,
						getManagedBean().getSmAppl().getPoUserId());
				reqDTO.setPoLoginId(po.getLoginId());
			}
		} else {
			PmState state = getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
			if (state != null && state.getStateId() != null) {
				PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
						.getStateId());
				reqDTO.setSdoLoginIds(SmCommonUtil
						.retrieveLoginIdsByZoneAndRole(zone.getZoneCode(),
								"UPS_SDO", bpmAppl.getSupplierType(),
								bpmAppl.getApplType(),
								bpmAppl.getApplCategoryType()));
			}
		}

		// Set list of users to send to BPM
		if (getManagedBean().getSmAppl().getApplType().equalsIgnoreCase("N")
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase("A")
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase("B")
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase("U")
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase("R")
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase("C")) {
			reqDTO.setUserList(new ArrayList<String>());
			String sendMode = EpPropertiesUtils.getInstance().getProperty(
					"sm.send.bpm.mode");
			if (!StringUtils.isBlank(sendMode) && sendMode.equals("B")) {
				List<PmUser> supplierUserList = SmApplicationUtil
						.getActivatedSupplierUserList(getManagedBean()
								.getSmAppl());
				if (!EntityHelper.isCollectionEmpty(supplierUserList)) {
					for (PmUser user : supplierUserList) {
						reqDTO.getUserList().add(user.getLoginId());
					}
				} else {
					PmUser pmUser = new FacesScopes().getUserSessionBean();
					reqDTO.getUserList().add(pmUser.getLoginId());
				}
			} else {
				PmUser pmUser = new FacesScopes().getUserSessionBean();
				reqDTO.getUserList().add(pmUser.getLoginId());
			}
		}

		if (this.getManagedBean().getPmDigiSign() != null) {
			reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
		}
		reqDTO.setRespondPoInquiry(getManagedBean().isPoInquiryRespond());

		@SuppressWarnings("unused")
		MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
		regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IMofRegistrationSO.class);
		SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
				.getCompanyBasicGst());
		reqDTO.setCompanyBasicGst(getManagedBean().getCompanyBasicGst());

		log.info("supplier declare submit bpmTask for applNo:"
				+ getManagedBean().getSmAppl().getApplNo() + " taskId:"
				+ getManagedBean().getTaskId());
		reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
		resDTO = regSo.initiateSmApplicationTask(reqDTO);
		regSo = null;
		retriveLatestApplAfterUpdate("", "");

	}

	/**
	 * Resubmit sm application inquiry to bpm.
	 * 
	 * @param applType
	 *            - the appl type
	 * @return the redirect page
	 */
	public String resubmitSmApplInquiryToBpm(String applType) {
		log.info("*** Resubmit for send inquiry **** ");
		if (getManagedBean().getSmAppl().getSupportingDocMode() != null
				&& getManagedBean().getSmAppl().getSupportingDocMode()
						.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			// 34392 - Start
			validateMandatoryEnquirySupportingDocs();
			// 34392 - End
		}
		getManagedBean().getSmAppl().setResubmit(true);
		getManagedBean().setPoInquiryRespond(true);
		getManagedBean().getPoToSupplierInquiry().setResponseDate(new Date());
		
		// #34392 - start
		for (SmApplSupportDoc smDocu : getManagedBean().getSmAppl().getSmApplSupportDocs()) {
			if (!smDocu.isVerified() && smDocu.getNewAddedFiles() != null) {
				replaceNewFile(smDocu);
			}
		}
		// #34392 - end
		
		return submitSmApplToBpm(applType);

	}
	
	// #34392 - start
	private void replaceNewFile(SmApplSupportDoc smDocu) {
		
		smDocu.setSmApplDocFiles(new ArrayList<SmApplDocFile>());

		List<SmApplDocFile> newDocFile = new ArrayList<>();

		if (smDocu.getNewAddedFiles() != null) {
			for (SmApplDocFile smDocFileNewAdded : smDocu.getNewAddedFiles()) {
				newDocFile.add(smDocFileNewAdded);
			}
		}
		smDocu.setSmApplDocFiles(newDocFile);
	}
	//#34392 - end

	/**
	 * Submit sm application to bpm and save the latest information to DB.
	 * 
	 * @param applType
	 *            - the appl type
	 * @return the redirect page
	 */
	public String submitSmApplToBpm(String applType) {
		
		// #34166 - Start
		validateText();
		// #34166 - End
		
		//30953 & 30945 - Start		
		//add checking to make sure, supporting doc mode is not null
		log.info("This is the Supporting Document Mode === " +getManagedBean().getSmAppl().getSupportingDocMode());
		if(getManagedBean().getSmAppl().getSupportingDocMode() == null){
			throw new EpException(MessageConst.ERROR_722648);		
		}
		//30953 & 30945 - End

		// validateDontHavePersonnelWithLogin();
		validateNoChangesBeenMadeToApplication();
		//#28775 - Site Grouping Enhancement
		validateAllWorkshops();

		// try {
		//#28776 - start
		if(getManagedBean().getSmAppl().getPmStatus() != null 
				&& getManagedBean().getSmAppl().getPmStatus().getStatusId() == SupplierManagementConst.PM_STATUS_PENDING_APPL_DECLARATION){
			getManagedBean().getSmAppl().setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
		}
		//#28776 - end
		if (getManagedBean().getSmAppl().getSupportingDocMode() != null
				&& getManagedBean().getSmAppl().getSupportingDocMode()
						.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			//#34392 - Start
			if (!getManagedBean().getSmAppl().getPmStatus().getStatusId()
					.equals(Long.valueOf(SupplierManagementConst.PM_STATUS_PENDING_REPLY_INQUIRY_BY_PROCESSING_OFFICER))) {
				validateSupportingDocs();
			}
			//#34392 - end
		} else {
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				if (doc.getSmApplDocFiles() != null
						&& !doc.getSmApplDocFiles().isEmpty()) {
					doc.getSmApplDocFiles().clear();
				}
			}
		}
		if (!this.getManagedBean().isPoInquiryRespond()) {
			validateBeforeSubmit();

			// validate if no SDO exist then dont allowed to submit
			SmAddress hqAddr = getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress();
			ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
			zoneReqDTO.setStateId(hqAddr.getPmState().getStateId());
			PmZone zone = SmServiceLookup.getZoneSO()
					.retrieveZoneByStateId(zoneReqDTO).getPmZone();
			if (zone != null) {
				String sdoLoginIds = SmCommonUtil
						.retrieveLoginIdsByZoneAndRole(zone.getZoneCode(),
								RoleCode.UPS_SDO.value(), getManagedBean()
										.getSmAppl().getSupplierType(),
								getManagedBean().getSmAppl().getApplType(),
								getManagedBean().getSmAppl()
										.getApplCategoryType());
				if (StringUtils.isBlank(sdoLoginIds)) {
					throw new EpException(MessageConst.ERROR_722660);
				}
			}

		}

		log.info("supplier declare applNo:"
				+ getManagedBean().getSmAppl().getApplNo());
		initiateSmApplicationTask();
		String msgConst = "";
		String urlParam = SmCommonUtil.getBasePortalUrl()
				+ "/web/epapp/task-list";
		if ((applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) || applType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW))
				&& !getManagedBean().getSmAppl().isResubmit()) {
			msgConst = MessageConst.INFO_700053;
			urlParam = SmApplicationUtil
					.getMofApplicationSaveDraftUrl(getManagedBean().getSmAppl());
		} else {
			msgConst = MessageConst.INFO_721225;
		}
		FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils
										.getMessage(
												msgConst,
												new String[] {
														getParameterDesc(
																ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																getManagedBean()
																		.getSmAppl()
																		.getApplType()),
														getManagedBean()
																.getSmAppl()
																.getApplNo(),
														SmRuleHelper
																.getInstance()
																.getProcessingFee()
																.setScale(
																		2,
																		BigDecimal.ROUND_UP)
																.toString(),
														SmRuleHelper
																.getInstance()
																.getApplicationSubmissionDays()
																+ "", urlParam },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes()
														.getUserLocale())));
		SmOutputMsgBackingBean backingBean = new SmOutputMsgBackingBean();
		backingBean.getManagedBean().setSmAppl(getManagedBean().getSmAppl());
		renewSesionObject();
		return "/xhtml/sm/shared/mofRegistration/mofApplicationOutputMsg.xhtml";
	}

	public String submitSmApplToBpm(PmDigiSign pmDigiSign, String applType) {

		if (pmDigiSign != null) {
			updateSpkiDigiSignData(pmDigiSign, "SR", this.getManagedBean()
					.getSmAppl().getApplId(), this.getManagedBean().getSmAppl()
					.getApplNo(), prepareOriginalText());
			byte[] signedData = getManagedBean().getSpkiBackingBean()
					.spkiSignTransaction(prepareOriginalText());

			log.info("signedData: " + signedData);

			pmDigiSign.setSpkiSignedData(signedData);
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		if (applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			log.info(" *** This is cancellation application **** ");
			return submitCancelMofApplToBpm();
		} else {
			return submitSmApplToBpm(applType);
		}
	}

	/**
	 * Submit cancel mof application to bpm and update the DB.
	 * 
	 * @return the redirect page
	 */
	public String submitCancelMofApplToBpm() {
		log.info(" Call Cancel ---------------->>>>>>>>>>");
		try {
			SmRemark remark = new SmRemark();
			remark.setDocId(getManagedBean().getSmAppl().getApplId());
			remark.setDocType(ParameterConst.NEW_SM_APPLN_FOR_CANCEL);
			remark.setRemark(getManagedBean().getSupplierRemarks());
			SmCommonUtil.getInstance().setUserDetails(remark);
			saveSmRemarks(remark);
			saveSmCancellationAttachment(getManagedBean()
					.getCancellationAttachment());
			initiateSmApplicationTask();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700005,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	ParameterConst.NEW_SM_APPLN_FOR_CANCEL),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			renewSesionObject();
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Submit delete pending mof appl to bpm.
	 * 
	 * @return the string
	 */
	public String submitDeletePendingMofApplToBpm() {
		SmCommonUtil.getInstance().setUserDetails(getManagedBean().getSmAppl());
		IMofRegistrationSO regSo = null;
		try {
			StringBuilder sb = new StringBuilder();
			for (Long resId : getManagedBean().getReasonId()) {
				for (PmReasonDesc desc : getManagedBean().getReasonDescList()) {
					if (desc.getPmReason().getReasonId().equals(resId)) {
						if (!desc
								.getPmReason()
								.getReasonCode()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_REASON_CODE_OTHER)) {
							sb.append(desc.getReasonDesc());
							sb.append(",");
						} else {
							sb.append(desc.getReasonDesc()).append(" (");
							sb.append(getManagedBean().getSupplierRemarks());
							sb.append("),");
						}
					}
				}
			}

			if (sb.toString() == null || sb.toString().equalsIgnoreCase("")) {
				throw new EpException(MessageConst.ERROR_722529);
			}
			SmRemark remark = new SmRemark();
			remark.setDocId(getManagedBean().getSmAppl().getApplId());
			remark.setDocType(ParameterConst.NEW_SM_APPLN_FOR_DELETE_PENDING_APPL);
			remark.setRemark(sb.toString());
			if (remark.getRemark().endsWith(",")) {
				remark.setRemark(remark.getRemark().substring(0,
						remark.getRemark().length() - 1));
			}
			if (remark.getRemark().length() > 2500) {
				remark.setRemark(remark.getRemark().substring(0, 2500));
			}
			SmCommonUtil.getInstance().setUserDetails(remark);
			saveSmRemarks(remark);
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setOutcome(SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT);
			reqDTO.setSupplierId(this.getManagedBean().getSmAppl()
					.getSmSupplier().getSupplierId());
			reqDTO.setUserList(new ArrayList<String>());
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			reqDTO.getUserList().add(pmUser.getLoginId());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setSupplierType(this.getManagedBean().getSmAppl().getSupplierType());//#31181
			//#30516 - start
			if (this.getManagedBean()
					.getSmAppl()
					.getSupportingDocMode()== null || this.getManagedBean()
							.getSmAppl()
							.getSupportingDocMode()
							.equalsIgnoreCase(
									SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}
			//#30516 - end

			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);

			SmWorkflowStatus sts = SmCommonUtil
					.getCurrentActiveWokFlowStatus(getManagedBean().getSmAppl()
							.getApplId());

			reqDTO.setSmWorkflowStatus(sts);
			reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
			regSo.initiateSmDelPendingApplicationTask(reqDTO);
			String msg = "";

			if (sts != null && sts.getPmStatus() != null) {
				if (sts.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_PROCESS_FEE) == 0
						|| sts.getPmStatus()
								.getStatusId()
								.compareTo(
										SupplierManagementConst.PM_STATUS_PENDING_VERIFY_SUPP_DOCS) == 0
						|| sts.getPmStatus()
								.getStatusId()
								.compareTo(
										SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_INQUIRY) == 0) {
					msg = MessageConst.INFO_700056;
				} else {
					msg = MessageConst.INFO_700057;
				}
			} else {
				msg = SmCommonUtil.getMessage(
						"lbl.sm.appl.no.status.submitted.msg", null);
			}
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(msg, new String[] {

							getManagedBean().getSmAppl().getApplNo() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			renewSesionObject();
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} finally {
			regSo = null;
		}
	}

	/**
	 * Delete draft applciation from Db and clear task from BPM if required.
	 * 
	 * @return the redirect page
	 */
	public String deleteDraftApplication() {

		boolean valid = false;

		// check if delete draft action perform for application that is already
		// approved
		if (getManagedBean().getSmAppl().getApplSubmitDate() != null) {
			throw new EpException(MessageConst.ERROR_722663);
		}

		try {
			valid = deleteDraftApplciationTask();
			if (valid) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_721205,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																		getManagedBean()
																				.getSmAppl()
																				.getApplType()),
																getManagedBean()
																		.getSmAppl()
																		.getApplNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				return null;
			}
		} finally {

		}
	}

	/**
	 * Click print icon and process offline supporting document checklist pdf
	 */
	public void printDocCheckList() {
		if (!getManagedBean().getSmAppl().isActiveAppl()) {
			boolean valid = false;
			if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				valid = SmApplicationUtil.validateCategoryOtherDesc(true,
						getManagedBean(), currentTab);
			} else {
				valid = validateFinal(getManagedBean().getSmAppl()
						.getApplType(), getManagedBean().getCurrentPageType());
			}

			if (valid) {
				valid = intializeSaveDraftBpm(getManagedBean()
						.getCurrentPageType());
			}

			if (valid) {

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_721222,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																		getManagedBean()
																				.getSmAppl()
																				.getApplType()),
																getManagedBean()
																		.getSmAppl()
																		.getApplNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
			}
		}
		SmApplicationUtil.generateOfflineSuppDocChecklistPdf(getManagedBean()
				.getSmAppl().getSmApplSupportDocs(), getManagedBean()
				.getSmAppl(), "SUPPLIER_DECLARE");
		RequestContext.getCurrentInstance().execute("docCheckListDlg.show()");
	}

	/**
	 * On click print icon for respond to PO inquiry supporting document check
	 * list
	 */
	public void printSupplierRespondPoInquiry() {
		SmApplicationUtil.generateOfflineSuppDocChecklistPdf(getManagedBean()
				.getMissinDocs(), getManagedBean().getSmAppl(),
				"SUPPIER_RESPOND_PO");
		RequestContext.getCurrentInstance().execute("docCheckListDlg.show()");
	}

	/**
	 * After click yes in confirmation save application for first time (New MOF
	 * application only)
	 */
	public void confirmSaveDraftFirstTime() {
		boolean valid = false;
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			valid = SmApplicationUtil.validateCategoryOtherDesc(true,
					getManagedBean(), currentTab);
		} else {
			valid = validateFinal(getManagedBean().getSmAppl().getApplType(),
					getManagedBean().getCurrentPageType());
		}

		if (valid) {
			valid = intializeSaveDraftBpm(getManagedBean().getCurrentPageType());
		}

		if (valid) {
			setTabValue(0, 1);
			getManagedBean().setTabActiveIndex(outputTab);
			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			}
			getManagedBean().setCurrentPageType(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH);
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_721222,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	getManagedBean()
																			.getSmAppl()
																			.getApplType()),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
		}
	}

	/**
	 * Save the application as draft in DB and Intialize bpm if required.
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @return the redirect page
	 */
	@SuppressWarnings("deprecation")
	public void saveApplicationAsDraft(String applType, String currentPageType) {
		log.info("****** Save As Draft **********" + currentPageType
				+ currentTab);
		boolean valid = false;
		boolean isActiveAppl = getManagedBean().getSmAppl().isActiveAppl();
		try {
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				valid = SmApplicationUtil.validateCategoryOtherDesc(true,
						getManagedBean(), currentTab);
			} else {
				valid = validateFinal(applType, currentPageType);
			}

			if (valid) {
				valid = intializeSaveDraftBpm(currentPageType);
			}
			if (valid) {
				if (isActiveAppl) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_721222,
															new String[] {
																	getParameterDesc(
																			ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																			getManagedBean()
																					.getSmAppl()
																					.getApplType()),
																	getManagedBean()
																			.getSmAppl()
																			.getApplNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				} else {
					SmCommonUtil.redirectToPage(SmApplicationUtil.getApplicationErrorPage(), MessageConst.INFO_721204,
							new String[] { getParameterDesc(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES, getManagedBean().getSmAppl().getApplType()),
									getManagedBean().getSmAppl().getApplNo(), SmApplicationUtil.getMofApplicationSaveDraftUrl(getManagedBean().getSmAppl()) });
					renewSesionObject();
				}
			}
		} finally {
			initNonMandatoryAndMandatoryDocs();
		}
		
		// start #28689 - set percentage to 0.000 if null
		if (currentPageType.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)
				&& getManagedBean().getSmAppl() != null
				&& getManagedBean().getSmAppl().getSmCompanyBasic() != null) {
			SmCompanyBasic smCompanyBasic = getManagedBean().getSmAppl().getSmCompanyBasic();
			if (smCompanyBasic.getBumiPercentage() != null) {
				smCompanyBasic.setBumiPercent(smCompanyBasic.getBumiPercentage());
			} else {
				smCompanyBasic.setBumiPercent(new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
			}

			if (smCompanyBasic.getNonBumiPercentage() != null) {
				smCompanyBasic.setNonBumiPercent(smCompanyBasic.getNonBumiPercentage());
			} else {
				smCompanyBasic.setNonBumiPercent(new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
			}

			if (smCompanyBasic.getForiegnerPercent() == null) {
				smCompanyBasic.setForiegnerPercent(new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
			}

			if (smCompanyBasic.getGovtUTPercent() == null) {
				smCompanyBasic.setGovtUTPercent(new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
			}

			if (smCompanyBasic.getGovtEquityPercent() == null) {
				smCompanyBasic.setGovtEquityPercent(new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
			}

			if (smCompanyBasic.getForiegnCompPercent() == null) {
				smCompanyBasic.setForiegnCompPercent(new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
			}

			if (smCompanyBasic.getLocalCompPercent() == null) {
				smCompanyBasic.setLocalCompPercent(new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
			}
		}
		// end #28689
	}

	/**
	 * Save the application as draft in DB and Intialize bpm if required.
	 * 
	 * @param currentPageType
	 *            - the current page type
	 * @return true, if successful
	 */
	private boolean intializeSaveDraftBpm(String currentPageType) {
		log.info("*********  intializeSaveDraftBpm ************* ");
		IMofRegistrationSO regSo = null;
		boolean valid = false;
		try {
			setSupplierChangeValues(currentPageType);
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			bpmAppl.setApplSaveDate(new Timestamp(System.currentTimeMillis()));
			if (bpmAppl.getSupportingDocMode() == null) {
				//28771
				bpmAppl.setSupportingDocMode(StringUtils.EMPTY);
			}
			String outcome = "";
			if (!getManagedBean().isDraftedAppl()) {
				outcome = SupplierManagementConst.SM_APPL_BPM_OUTCOME_DRAFT;
			} else {
				outcome = "";
			}
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					outcome, SupplierManagementConst.SUPP_SAVE_APPL_DRAFT);
			List<String> useList = new ArrayList<String>();
			PmUser loginUser = new FacesScopes().getUserSessionBean();
			useList.add(loginUser.getLoginId());
			reqDTO.setUserList(useList);
			//#36483 - START
			if(currentPageType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC) 
					&& getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)){
				for(SmFacility fac : reqDTO.getSmAppl().getSmFacilities()){
					if(fac.getFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE) && fac.isHeadquarters()){
						fac.getSmAddress().setMapLatitude(reqDTO.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLatitude());
						fac.getSmAddress().setMapLongitude(reqDTO.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLongitude());
						getManagedBean().getBasicCompanyFacility().getSmAddress().setMapLatitude(reqDTO.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLatitude());
						getManagedBean().getBasicCompanyFacility().getSmAddress().setMapLongitude(reqDTO.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getMapLongitude());
						
						getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress1(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddress1());
						getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress2(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddress2());
						getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress3(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddress3());
						getManagedBean().getBasicCompanyFacility().getSmAddress().getPmCountry().setCountryId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCountry().getCountryId());
						getManagedBean().getBasicCompanyFacility().getSmAddress().getPmState().setStateId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState().getStateId());
						if(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision() != null && getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision().getDivisionId() != null){
							getManagedBean().getBasicCompanyFacility().getSmAddress().getPmDivision().setDivisionId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision().getDivisionId());
						}
						getManagedBean().getBasicCompanyFacility().getSmAddress().getPmDistrict().setDistrictId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDistrict().getDistrictId());
						getManagedBean().getBasicCompanyFacility().getSmAddress().getPmCity().setCityId(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCity().getCityId());
						getManagedBean().getBasicCompanyFacility().getSmAddress().setPostcode(getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPostcode());
					}
				}
			}
			//#36483 - END
			log.info("******* Save as draft User List to BPM Size *******"
					+ reqDTO.getUserList());
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
					.getCompanyBasicGst());
			SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
					.getSelectedBranch().getGst());
			SmApplicationUtil.getInstance()
					.removeNotApplicableInformationBeforeSave(getManagedBean());

			reqDTO.setCompanyBasicGst(getManagedBean().getCompanyBasicGst());
			reqDTO.setBranchGst(getManagedBean().getSelectedBranch().getGst());
			reqDTO.setSameAsHq(getManagedBean().getSelectedBranch()
					.isSameAsHq());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setChangedBy(loginUser.getUserId());
			if (reqDTO.getSmAppl().isActiveAppl()) {
				resDTO = regSo.initiateSmApplSaveAsDraftTask(reqDTO);
				valid = resDTO.isBpmSuccess();
			} else {
				boolean cannotProceedAlertMsg = SmApplicationUtil
						.checkActiveSmApplForSupp(getManagedBean()
								.getSmSupplier());
				if (cannotProceedAlertMsg) {
					throw new EpException(MessageConst.ERROR_722542);
				}
				//SSM INTEGRATION
				//if(bpmAppl.getSmSupplier() != null && bpmAppl.getSmSupplier().getSmSupplierBusinesses()!= null){
					//reqDTO.setSmSupplierBusiness(bpmAppl.getSmSupplier().getSmSupplierBusinesses());
				//}
				resDTO = regSo.saveNewDraftApplication(reqDTO);

				reqDTO.setSmAppl(resDTO.getSmAppl());
				resDTO = regSo.initiateMofApplNewBpmTask(reqDTO);
				getManagedBean().setTaskId(resDTO.getTaskId());
				valid = resDTO.isBpmSuccess();

				if (!valid && resDTO.getSmAppl() != null) {
					reqDTO.setSmAppl(resDTO.getSmAppl());
					regSo.rollbackNewDraftApplication(reqDTO);
				}
			}
			if (!valid) {
				throw new EpException(MessageConst.ERROR_700020);
			}
			return valid;
		} finally {
			regSo = null;
			retriveLatestApplAfterUpdate(currentPageType, "");
		}
	}

	/**
	 * Delete draft applciation from Db and clear task from BPM if required.
	 * 
	 * @return true, if successful
	 */
	private boolean deleteDraftApplciationTask() {
		log.info("*********  Delete draft application task ************* ");
		IMofRegistrationSO regSo = null;
		boolean valid = true;
		try {
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			// #28175
			updateCategoryCodeNames();
			String outcome = "CANCEL";
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					outcome, SupplierManagementConst.SUPP_SUBMIT_DELDRFT_APPL);
			List<String> useList = new ArrayList<String>();
			PmUser loginUser = new FacesScopes().getUserSessionBean();
			useList.add(loginUser.getLoginId());
			reqDTO.setUserList(useList);
			reqDTO.setChangedBy(loginUser.getUserId());
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmApplDeleteDraftTask(reqDTO);
			if (resDTO != null) {
				valid = resDTO.isBpmSuccess();
				if (!valid) {
					log.info("******* Process No Valid ******");
					throw new EpException(MessageConst.ERROR_700020);
				}
			} else {
				log.info("*** The Save as draft returned null **** ");
				valid = false;
				throw new EpException(MessageConst.ERROR_722519);
			}
			return valid;
		} catch (Exception e) {
			log.info("***Error in Delete Draft  **** ");
			retriveLatestApplAfterUpdate(getManagedBean().getCurrentPageType(),
					"");
			throw new EpException(e.getMessage());
		}

		finally {
			regSo = null;
		}

	}

	/**
	 * Sets default values for the mof registration req dto.
	 * 
	 * @param bpmAppl
	 *            - SmAppl object
	 * 
	 * @param outcome
	 *            - the outcome value
	 * @param actionDesc
	 *            - the action desc
	 * @return the mof registration req dto object
	 */
	private MofRegistrationReqDTO setMofRegistrationReqDTO(SmAppl bpmAppl,
			String outcome, String actionDesc) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setOutcome(outcome);
		reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
		reqDTO.setSupplierId(bpmAppl.getSmSupplier().getSupplierId());
		reqDTO.setAppType(bpmAppl.getApplType());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setSmAppl(bpmAppl);
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
				.getSupplierType().toUpperCase());
		reqDTO.setTrackingDairyVO(SmApplicationUtil.assignTrackingDairyValues(
				getManagedBean(), actionDesc, getManagedBean().getSmAppl()
						.getPmStatus()));
		PmState state = getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
		if (state != null && state.getStateId() != null) {
			PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
					.getStateId());
			log.info("*** ZONE SENT TO BPM ***** " + zone.getZoneName());
			if (zone != null && zone.getZoneName() != null) {
				reqDTO.setBaseOfficeState(zone.getZoneName().toUpperCase()
						.trim());
			}
		} else {
			throw new EpException(MessageConst.ERROR_722568);
		}

		if (this.getManagedBean()
				.getSmAppl()
				.getSupportingDocMode()
				.equalsIgnoreCase(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			reqDTO.setOnlineDoc(true);
		} else {
			reqDTO.setOnlineDoc(false);
		}

		reqDTO.setResubmit(bpmAppl.isResubmit());
		reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
		return reqDTO;
	}

	/**
	 * Assign missing or invalid supporting docs list.
	 */
	public void assignMissingDocsList(boolean isNewInquiry, String dialogId) {
		log.info("*** Send Inquiry **** ");
		initMissingDocs(isNewInquiry, dialogId);

		// if missing docs is empty, set other application inquiry to required
		if (dialogId != null
				&& EntityHelper.isCollectionEmpty(getManagedBean()
						.getMissinDocs())) {
			assignValReqdToTrue();
		}
	}

	public void initMissingDocs(boolean isNewInquiry, String dialogId) {
		SmApplicationUtil.initPoInquiry(getManagedBean(), isNewInquiry);
		// #34392 - START
		getManagedBean().setMissinDocs(new ArrayList<SmApplSupportDoc>());
		getManagedBean().setMissinDocsMandatory(new ArrayList<SmApplSupportDoc>());
		getManagedBean().setMissinDocsNonMandatory(new ArrayList<SmApplSupportDoc>());

		for (SmApplSupportDoc doc : getManagedBean().getSmAppl().getSmApplSupportDocs()) {

			if (doc.getPmDocument().isMandatory() && (doc.getVerifiedFlg() == null || doc.getVerifiedFlg() != 1)) {
				getManagedBean().getMissinDocs().add(doc);
				getManagedBean().getMissinDocsMandatory().add(doc);
			} else if (!doc.getPmDocument().isMandatory()
					&& (doc.getVerifiedFlg() == null || doc.getVerifiedFlg() != 1)) {
				getManagedBean().getMissinDocs().add(doc);
				getManagedBean().getMissinDocsNonMandatory().add(doc);
			}
		}

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl().getSmApplSupportDocs())) {
			if (!EntityHelper.isCollectionEmpty(getManagedBean().getMissinDocsMandatory())) {
				Collections.sort(getManagedBean().getMissinDocsMandatory(), new Comparator<SmApplSupportDoc>() {
					@Override
					public int compare(SmApplSupportDoc doc1, SmApplSupportDoc doc2) {
						if (doc1.getPmDocument().getDisplaySeq() == null) {
							return (doc2.getPmDocument().getDisplaySeq() == null) ? 0 : 1;
						}
						if (doc2.getPmDocument().getDisplaySeq() == null) {
							return -1;
						}
						return Integer.valueOf(doc1.getPmDocument().getDisplaySeq()
								.compareTo(doc2.getPmDocument().getDisplaySeq()));
					}
				});
			}

			if (!EntityHelper.isCollectionEmpty(getManagedBean().getMissinDocsNonMandatory())) {
				Collections.sort(getManagedBean().getMissinDocsNonMandatory(), new Comparator<SmApplSupportDoc>() {
					@Override
					public int compare(SmApplSupportDoc doc1, SmApplSupportDoc doc2) {
						if (doc1.getPmDocument().getDisplaySeq() == null) {
							return (doc2.getPmDocument().getDisplaySeq() == null) ? 0 : 1;
						}
						if (doc2.getPmDocument().getDisplaySeq() == null) {
							return -1;
						}
						return Integer.valueOf(doc1.getPmDocument().getDisplaySeq()
								.compareTo(doc2.getPmDocument().getDisplaySeq()));
					}
				});
			}
		}
		// #34392 - End
		//log.info("**** Send Inquiry List Size ***** " + getManagedBean().getMissinDocs().size());
		if (dialogId != null) {
			RequestContext.getCurrentInstance().execute(dialogId + ".show()");
		}
	}

	public void cancelInquiry() {
		if (getManagedBean().isValueReqd()) {
			assignValReqdToFalse();
		}
	}

	/**
	 * Submit po escalation to approver.
	 * 
	 * @param recommendation
	 *            - the recommendation
	 */
	public void submitPOEscalationToApprover(String recommendation) {
		assignValReqdToTrue();
		// validate equity owner
		SmApplicationUtil.validateEquityOwnerCount(getManagedBean(), -1,
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);
		if(!recommendation.equalsIgnoreCase("REJECT")){
			SmApplicationUtil.validateCategoriesDecision(getManagedBean());
			// #31043 - start
			SmApplicationUtil.validateSupportingDocsDecision(getManagedBean());
			// #31043 - end
		}
		
		getManagedBean().getPoSecReview().setRecommendation(recommendation);
		MofRegistrationResDTO resDTO = getCurrentRegStatus();
		if (resDTO.getStatusId() != null
				&& !getManagedBean().getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			getManagedBean().setApprovedStatusId(resDTO.getStatusId());
			getManagedBean().setPoRecommendStatusId(resDTO.getStatusId());
			getManagedBean().setCurrRegStatus(resDTO.getRegStatusDesc());
		}
		log.info(">>>>CURR REGSTATUS:" + getManagedBean().getCurrRegStatus());
		if (StringUtils.isBlank(getManagedBean().getCurrRegStatus())) {
			getManagedBean().setChangeCurrRegStatus(true);
		} else {
			getManagedBean().setChangeCurrRegStatus(false);
		}
		RequestContext.getCurrentInstance()
				.execute("submitApprvrDialog.show()");
	}

	/**
	 * Cancel po escalation to approver.
	 */
	public void cancelPOEscalationToApprover() {
		assignValReqdToFalse();
		resetSearchRejectedReason();
		getManagedBean().setReasonId(null);
		getManagedBean().setRejectReasonReqd(false);
		getManagedBean().getPoSecReview().setRecommendation("");
		//#3460 - Start
		getManagedBean().setSelectedRecommendReasonDescs(new ArrayList<PmReasonDesc>());
		//#3460 - End
		RequestContext.getCurrentInstance().execute("submitApprvrDialog.hide()");
	}

	/**
	 * PO select to assign final approve or reject to apparover.
	 * 
	 * @param dialog
	 *            - the dialog id to be opened
	 */
	public void assignFinalApproveOrReject(String dialog) {
		log.info("**** assignFinalApproveOrReject *** ");
		if (!dialog.equalsIgnoreCase("rejectDialog")
				&& !dialog.equalsIgnoreCase("approverRejDialog")) {
			SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
			// validate equity owner
			SmApplicationUtil.validateEquityOwnerCount(getManagedBean(), -1,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);
		}
		//#12483 - START
		if(dialog.equalsIgnoreCase("approverRevDialog")){
			this.getManagedBean().setReviseMandatory(true);
		}
		//#12483 - END
		//Bug #26705
		getlatestPoOverlappingRemarks();
		
		// 27877 - start - not required when pending site visit
		BPMTaskListBackingBean bpmBean = new BPMTaskListBackingBean();
		String pendingSiteVisitReviewAfterApprovedStatus = bpmBean.applicationStatus((int) SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED);
		if (!getManagedBean().getCurrentStatus().equals(pendingSiteVisitReviewAfterApprovedStatus)) {
			assignValReqdToTrue();
		} else {
			assignValReqdToFalse();
		}
		// 27877 - end
		getManagedBean().getSmAppl().setBumi(
				getManagedBean().getSmAppl().getSmSupplier().isBumi());
		if (getManagedBean().getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
			getManagedBean().getSmAppl().setBumi(true);
		}
		MofRegistrationResDTO resDTO = getCurrentRegStatus();
		if (resDTO.getStatusId() != null
				&& !getManagedBean().getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			getManagedBean().setApprovedStatusId(resDTO.getStatusId());
			getManagedBean().setPoRecommendStatusId(resDTO.getStatusId());
			getManagedBean().setCurrRegStatus(resDTO.getRegStatusDesc());
		}
		if (StringUtils.isBlank(getManagedBean().getCurrRegStatus())) {
			getManagedBean().setChangeCurrRegStatus(true);
		} else {
			getManagedBean().setChangeCurrRegStatus(false);
		}
		
		//30974 && 37896 - start
		getManagedBean().setSelectedRecommendRejectReasonDescs(getManagedBean().getSelectedReasonDescs());
		getManagedBean().setSelectedRejectReasonDescs(getManagedBean().getSelectedReasonDescs());
		getManagedBean().setShowDifferenceRejection(true);
		setReasonIdList();
		//30974 && 37896 - end
		
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	/**
	 * Approver select to Approve or reject mof cancellation.
	 * 
	 * @param dialog
	 *            - the dialog id to be opened
	 */
	public void approveOrRejectMofCancellation(String dialog) {
		log.info("**** assignFinalApproveOrReject *** ");
		assignValReqdToTrue();
		getManagedBean().setRejectReasonReqd(true);
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	/**
	 * PO select to Assign site visit and pop up opens.
	 * 
	 * @param dialog
	 *            - the dialog id to be opened
	 */
	public void assignSiteVisitPopUp(String dialog) {
		log.info("**** assignSiteVisitPopUp *** ");
		// validate equity owner
		SmApplicationUtil.validateEquityOwnerCount(getManagedBean(), -1,
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);
		SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
		getManagedBean()
				.setSplCategoryList(new ArrayList<SmSupplierCategory>());
		for (SmSupplierCategory category : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (category.isSpecialCategory()
					&& category.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				getManagedBean().getSplCategoryList().add(category);
			}
		}
		assignValReqdToTrue();
		resetAssignSiteVisitPopup();
		SmApplicationUtil.handleShowOrHideFacilityPanels(getManagedBean());
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	/**
	 * Assign view site visit report.
	 */
	public void assignViewSiteVisitReport() {
		log.info("**** assignViewSiteVisitReport *** ");
		SvoSiteVisitBackingBean svo = new SvoSiteVisitBackingBean();
		svo.getManagedBean().setAppId(getManagedBean().getSmAppl().getApplId());
		svo.reviewSiteVisitReport();
	}

	/**
	 * Submit po descision for site visit.
	 * 
	 * @param dialog
	 *            - the dialog id be closed
	 * @param decision
	 *            - the decision
	 * @return the redirect page.
	 */
	public String submitPODescisionForSiteVisit(String dialog, String decision) {
		log.info("**** assignSiteVisitPopUp *** ");
		List<String> updateIds = new ArrayList<String>();
		updateIds.add(":form");
		Boolean valid = SmApplicationUtil
				.validateSiteVisitSelections(getManagedBean());
		if (valid) {
			getManagedBean().setShowParentMsg(true);
			RequestContext.getCurrentInstance().update(updateIds);
			RequestContext.getCurrentInstance().execute(dialog + ".hide()");
			return submitPOorApproverReviewToBPM(decision);
		} else {
			return null;
		}
	}

	/**
	 * Call alert for HQ GST Effective Date not first day of month
	 */
	public void onCheckHqEffectiveDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getManagedBean().getCompanyBasicGst().getGstEffDate());
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (day != 1) {
			RequestContext.getCurrentInstance().execute("alertHq.show();");
		}
	}

	/**
	 * Reset HQ Effective Date to first day of month
	 */
	public void resetHqEffectiveDate() {
		SmCommonUtil.resetGstEffectiveDate(getManagedBean()
				.getCompanyBasicGst());
	}

	/**
	 * Cancel button action for site visit action.
	 * 
	 * @param dialog
	 *            - the dialog id to be close
	 */
	public void cancelSiteVisitAction(String dialog) {
		assignValReqdToFalse();
		resetAssignSiteVisitPopup();
		RequestContext.getCurrentInstance().reset(":form:tt:" + dialog);
	}

	private void resetAssignSiteVisitPopup() {
		getManagedBean().setPoSiteVisitType(StringUtils.EMPTY);
		if (getManagedBean().getPoSecReview() != null) {
			getManagedBean().getPoSecReview().setSitevisitRemark(
					StringUtils.EMPTY);
			getManagedBean().getPoSecReview().setRecommendation(
					StringUtils.EMPTY);
			getManagedBean().getPoSecReview().setRemarkToApprover(
					StringUtils.EMPTY);
		}
	}

	/**
	 * Cancel button action for approve or reject.Close the pop up without any
	 * action.
	 * 
	 * @param dialog
	 *            - the dialog id to be close
	 */
	public void cancelFinalApproveOrReject(String dialog) {
		assignValReqdToFalse();
		resetSearchRejectedReason();
		getManagedBean().setReasonId(null);
		// #3460 - start
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSelectedReasonDescs())
				&& dialog.equalsIgnoreCase("rejectDialog")) {
			getManagedBean().setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
			resetSearchRejectedReason();
			getManagedBean().setReasonId(null);
		}
			
			if(dialog.equalsIgnoreCase("approverRejDialog")){
				if(!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl().getSmApplRejectReasons())) {
					getManagedBean().setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
					List<Long> reasonIdList = new ArrayList<Long>();
					for(SmApplRejectReason smApplRejectReason : getManagedBean().getSmAppl().getSmApplRejectReasons()){
							for(PmReasonDesc pmReasonDesc : getManagedBean().getReasonDescList()){
								if(smApplRejectReason.getPmReason().getReasonId().equals(pmReasonDesc.getPmReason().getReasonId())){
									getManagedBean().getSelectedReasonDescs().add(pmReasonDesc);
									reasonIdList.add(pmReasonDesc.getPmReason().getReasonId());
								}
							}			
					}
					getManagedBean().setReasonId(
							reasonIdList.toArray(new Long[reasonIdList.size()]));
					verifySelectedRejectReasons();
				}
				
				getManagedBean().setReasonDescList(
						getManagedBean().getReasonDescInitList());
				getManagedBean().setRejectionOtherReasonId(
						SmApplicationUtil
								.retrieveOtherRejectReasonIdByParamCodeAndType(
										ReasonCategory.SUPPLIER_REJECTION_REASON
												.value(),
										SupplierManagementConst.SM_REASON_CODE_OTHERS));
			}
		// #3460 - end
		
		getManagedBean().setRejectReasonReqd(false);
		//#12483 - START
		if(dialog.equalsIgnoreCase("approverRevDialog")){
			this.getManagedBean().setReviseMandatory(false);
		}
		//#12483 - END
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

	/**
	 * Reset mof cancellation final approve or reject.
	 * 
	 * @param dialog
	 *            - the dialog id to be closed
	 */
	public void resetMofCancellationFinalApproveOrReject(String dialog) {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:" + dialog);
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

	/**
	 * Final decision approve or reject application.
	 * 
	 * @param dialog
	 *            - the dialog id be closed
	 * @param decision
	 *            - the decision
	 * @return the redirect page.
	 */
	public String finalDecisionApproveOrRejectAppln(String dialog,
			String decision) {
		//#31043 - start
		if(getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)){
			SmApplicationUtil.checkCategoryRemark(getManagedBean());
		}
		//#31043 - end
		
		//24964 - start
		if (getManagedBean().getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
				&& decision.equalsIgnoreCase("REJECT")) {
			
			PmRegStatus pmRegStatus = new PmRegStatus();
			PmStatus pmStatus = new PmStatus();
			
			pmStatus.setStatusId(SupplierManagementConst.PM_STATUS_REGISTRATION_STATUS_REGISTERED);
			pmRegStatus.setPmStatus(pmStatus);
			getManagedBean().getSmAppl().setPmRegStatus(pmRegStatus);
			
			if(getManagedBean().getSmAppl().getSmSupplier() != null){
				SmApplicationUtil.rejectSmMofCertForSupplier(
						getManagedBean().getSmAppl().getSmSupplier());
				
				// #28534 START
				getManagedBean().getSmAppl().getSmSupplier().setBumi(false);
				getManagedBean().getSmSupplier().setBumi(false);
				// #28534 END
			}
		}	
		//#12483 - START
		if(decision.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REVISE)){
			SmApplRevise smApplRevise = new SmApplRevise();
			smApplRevise.setReviseReason(this.getManagedBean().getReviseReason());
			smApplRevise.setSmAppl(this.getManagedBean().getSmAppl());
			this.getManagedBean().setSmApplRevise(smApplRevise);
		}
		//#12483 - STOP
		//log.info("<<<<Appl type>>>>");
		//log.info(getManagedBean().getSmAppl().getApplType());
		//24964 - end
		List<String> updateIds = new ArrayList<String>();
		updateIds.add(":form");
		log.info("*** Submit Approve *** " + decision);
		assignValReqdToFalse();
		//#34400
		RequestContext.getCurrentInstance().update(updateIds);
		
		//#34400
		return "";
	}

	/**
	 * Final decision of PO on delete pending appln
	 * 
	 * @param dialog
	 *            - the dialog id to be closed
	 * @param decision
	 *            - the decision
	 * @return the redirect page.
	 */
	public String finalDecisionDeletePendingAppln(String dialog, String decision) {
		List<String> updateIds = new ArrayList<String>();
		updateIds.add(":form");
		log.info("*** Submit Approve *** " + decision);
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
		RequestContext.getCurrentInstance().update(updateIds);
		return approveOrRejectPendingApplicationCancellation(decision);

	}

	/**
	 * Approve or reject pending application cancellation.
	 * 
	 * @param res
	 *            - the PO or Approver resposne
	 * @return the redirect page.
	 */
	public String approveOrRejectPendingApplicationCancellation(String res) {
		IReviewApplicationSO reqSO = null;

		ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
		//#38195 START
		if (StringUtils.isNotBlank(getManagedBean().getApplnRejectionRemarks())) {
			SmApplicationUtil.manageAndSaveNewSmRemarksWithRoleCode(getManagedBean()
					.getSmAppl().getApplId(),
					ParameterConst.SM_APPLN_FOR_DELETE_PENDING_APPL_DECISION_REJECT,
					getManagedBean().getApplnRejectionRemarks(),  getManagedBean().getRoleCode());
		}
		
		PmState state = this.getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
		if (state != null && state.getStateId() != null) {
			PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
					.getStateId());
			log.info("*** ZONE SENT TO BPM ***** " + zone.getZoneName());
			if (zone != null && zone.getZoneName() != null) {
				reqDTO.setLocation(zone.getZoneName().toUpperCase());
			}
		} else {
			throw new EpException(MessageConst.ERROR_722568);
		}
		
		if (!StringUtils.isEmpty(reqDTO.getLocation())) {
			if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SABAH)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.sabah", null));
			} else if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SARAWAK)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.sarawak", null));
			} else {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.semunanjung", null));
			}
		}
		reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IReviewApplicationSO.class);
		reqDTO.setOutcome(res);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setSmAppl(getManagedBean().getSmAppl());
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		
		SMMOFAppCanDataType smAppCanObj = (SMMOFAppCanDataType) getManagedBean().getPayLoad();
		//if old version & approve outcome use payload to set bpmpojo
		if (reqDTO.getOutcome()
				.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPROVE) ) {
			reqDTO.setBpmPojo(getManagedBean().getPayLoad());
		}else{
			if(checkWIPVersion()){
				reqDTO.setBpmPojo(getManagedBean().getPayLoad());
			}else{
				SMNotificationCommonDataType notif = new SMNotificationCommonDataType();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");	
				
				notif.setCompanyName(getManagedBean().getSmSupplier().getCompanyName() != null
						? getManagedBean().getSmSupplier().getCompanyName() : " ");
				notif.setBusinessRegNo(getManagedBean().getSmSupplier().getRegNo() != null
						? getManagedBean().getSmSupplier().getRegNo() : " ");
				notif.setPaymentMethods("");
				notif.setNotifLink("");
				notif.setNotifDescription("");
				notif.setAddress(reqDTO.getNotifSignAddr());
				notif.setApplNo(getManagedBean().getSmAppl().getApplNo());
				notif.setApplSubmitDate(sdf.format(getManagedBean().getSmAppl().getApplSubmitDate()));
				notif.setApplRejectDate(sdf.format(new Date()));
				notif.setMofNo(!getManagedBean().getSmSupplier().getSmMofAccounts().isEmpty() 
						? getManagedBean().getSmSupplier().getSmMofAccounts().get(0).getMofNo() : " ");
				notif.setCompanyAddr(!this.getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().isEmpty() ? AddressUtils.getFullAddress(this.getManagedBean().getSmAppl().getSmCompanyBasic()
								.getSmCompanyAddresses().get(0).getSmAddress()) : " ");
				notif.setSuppReason(getManagedBean().getSupplierRemarks() != null 
						? getManagedBean().getSupplierRemarks() : " ");
				notif.setPoReason(getManagedBean().getApplnRejectionRemarks() != null
						? getManagedBean().getApplnRejectionRemarks() : " ");
				smAppCanObj.setNotificationData(notif);
				reqDTO.setBpmPojo(smAppCanObj); 
			}
		}
		reqDTO.setCurrWorkflowStatus(smAppCanObj.getCurrWfStatus().longValue());
		reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
		//#38195 END
		reqSO.approveOrRejectPendingApplicationCancellation(reqDTO);

		res = res.equals("REJECT") ? SmCommonUtil.getMessage(
				"lbl.sm.cancelpending.reject.uppercase", null) : SmCommonUtil
				.getMessage("lbl.sm.cancelpending.approve.uppercase", null);
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_700055,
								new String[] {
										getManagedBean().getSmAppl()
												.getApplNo(), res },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		renewSesionObject();
		return "cancelPendingAcknowledgePage";
	}

	/**
	 * Submit po or approver review to bpm.
	 * 
	 * @param res
	 *            - the PO or Approver resposne
	 * @return the redirect page.
	 */
	public String submitPOorApproverReviewToBPM(String res) {
		//#31043 & #36576 - start
		if(!res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REJECT) 
				&& !res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.INQUIRY)
				&& (getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) ){
			SmApplicationUtil.checkCategoryRemark(getManagedBean());
		}
		//#31043 & #36576 - end
		
		IReviewApplicationSO reqSO = null;
		String retMsg = null;

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		Long pmStatId = getManagedBean().getApprovedStatusId();
		retriveCurrentAppl();

		if (res.equalsIgnoreCase("APPROVE") || res.equalsIgnoreCase("ESCALATE")) {
			if (!getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				validateBeforeSubmit();
			}
		}

		log.info(" **** After retrieve ***"
				+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
		log.info("----------------------Submit PO / Approver Decision For BPM-------------------------"
				+ res);
		log.info(" **** The Value from screen of approval ***" + pmStatId);
		ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
		reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IReviewApplicationSO.class);
		
		//#12483 - START
		if(res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REVISE)){
			//#12483 - reopen - START
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(SmServiceLookup.getUserSO().retrieveLastPoOrApprover(this.getManagedBean().getSmAppl().getApplNo(), SupplierManagementConst.PO_HQ));
			PmUser po = SmServiceLookup.getUserSO().retrieveUserByUserId(userReqDTO).getPmUser();

			boolean pmOrgStats = false;
			if (po.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE && !EntityHelper.isCollectionEmpty(po.getPmUserOrgs())) {
				for (PmUserOrg userOrg : po.getPmUserOrgs()) {
					if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						pmOrgStats = true;
						break;
					}
				}
			}
				
			if(po.getRecordStatusFlg() == 1 && pmOrgStats){
				//#12483 - reopen - END
				reqDTO.setPoLoginId(po.getLoginId());
				//#12483 - START
				reqDTO.setUserId(po.getUserId());
				//#12483 - END
			}else{
				IZoneSO zoneReqSO = (IZoneSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
				ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
				zoneReqDTO.setStateId(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState().getStateId());
				
				po = SmCommonUtil.getInstance().retrievePOWithLowestWeightage(getManagedBean().getSmAppl().getSupplierType(), 
																			  getManagedBean().getSmAppl().getApplType(), 
																			  getManagedBean().getSmAppl().getApplCategoryType(), 
																			  zoneReqSO.retrieveZoneByStateId(zoneReqDTO).getPmZone().getZoneCode());
				reqDTO.setPoLoginId(po.getLoginId());
				//#12483 - START
				reqDTO.setUserId(po.getUserId());
				//#12483 - END
			}	
		}else{
			reqDTO.setPoLoginId(pmUser.getLoginId());
		}
		//#12483 - END
		
		reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
		reqDTO.setApplNo(this.getManagedBean().getSmAppl().getApplNo());
		reqDTO.setOutcome(res);
		reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier()
				.getSupplierId());
		reqDTO.setBpmTaskName(getManagedBean().getBpmTaskName());
		reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		PmState state = getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
		if (state != null && state.getStateId() != null) {
			PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
					.getStateId());
			log.info("*** ZONE SENT TO BPM ***** " + zone.getZoneName());
			if (zone != null && zone.getZoneName() != null) {
				reqDTO.setLocation(zone.getZoneName().toUpperCase());
			}
		} else {
			throw new EpException(MessageConst.ERROR_722568);
		}
		reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
				.getSupplierType().toUpperCase());
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
		if (getManagedBean().getSupplierToPOList() != null) {
			log.info("**** LIST of supp from BPM earlier task *******"
					+ getManagedBean().getSupplierToPOList().size());
			reqDTO.setUserList(getManagedBean().getSupplierToPOList());
		} else {
			log.info("***** NO SUPPLIER LIST FROM BPM TO DECISION ***** ");
			if (this.getManagedBean().getSmAppl().getCreatedBy() != null) {
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getManagedBean().getSmAppl()
						.getCreatedBy());
				PmUser user = SmServiceLookup.getUserSO()
						.retrieveUserByUserId(userReqDTO).getPmUser();
				if (user == null || StringUtils.isBlank(user.getLoginId())) {
					throw new EpException(
							"Internal service error: No user to send to BPM!");
				} else {
					reqDTO.setUserList(new ArrayList<String>());
					reqDTO.getUserList().add(user.getLoginId());
				}
			} else {
				throw new EpException(
						"Internal service error: No user to send to BPM!");
			}
		}

		if (this.getManagedBean()
				.getSmAppl()
				.getSupportingDocMode()
				.equalsIgnoreCase(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			reqDTO.setOnlineDoc(true);
		} else {
			reqDTO.setOnlineDoc(false);
		}

		boolean validSiteVisit = true;
		String actionDesc = "";
		if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.SITE_VISIT) || res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.SITE_VISIT_AFTER_APPROVE)) {
			SmApplicationUtil.setSiteVisitData(getManagedBean(), getManagedBean().getBumiSiteVisitVOList());
			SmApplicationUtil.setSiteVisitData(getManagedBean(), getManagedBean().getDockYardSiteVisitVOList());
			SmApplicationUtil.setSiteVisitData(getManagedBean(), getManagedBean().getStoreSiteVisitVOList());
			SmApplicationUtil.setSiteVisitData(getManagedBean(), getManagedBean().getFactorySiteVisitVOList());
			SmApplicationUtil.setSiteVisitData(getManagedBean(), getManagedBean().getWorkshopSiteVisitVOList());
			SmApplicationUtil.setSiteVisitData(getManagedBean(), getManagedBean().getBusinessOfficeSiteVisitVOList());
			if (!StringUtils.isBlank(getManagedBean().getPoSiteVisitType()) && getManagedBean().getPoSiteVisitType().equals("A")) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_AFTER_APPROVED + "");
			} else {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_ASSIGNMENT);
			}
			//28775 - Site Visit Grouping Enhancement - 28775 - start
			getManagedBean().getSmAppl().setSiteVisitGroup(true);
			getManagedBean().getSmAppl().setSmSvTasks(getManagedBean().getSvTaskList());
			//reqDTO.setSvTask(getManagedBean().getSvTask());
			//28775 - Site Visit Grouping Enhancement - 28775 - end
			reqDTO.setSvTaskList(getManagedBean().getSvTaskList());
			actionDesc = SupplierManagementConst.PO_RECOM_SITE_VISIT;

		}
		if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.INQUIRY)) {
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl().getSmApplSupportDocs()) {
				for (SmApplSupportDoc misDoc : getManagedBean().getMissinDocs()) {
					if (misDoc.getApplSupportDocId().equals(doc.getApplSupportDocId())) {
						getManagedBean().getSmAppl().getSmApplSupportDocs().set(getManagedBean().getSmAppl().getSmApplSupportDocs().indexOf(doc), misDoc);
					}
				}
			}
			setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_REPLY_INQUIRY_BY_PROCESSING_OFFICER);

			// #14530 - start
			// add new inquiry by PO
			if (getManagedBean().getPoToSupplierInquiry() != null) {
				if (getManagedBean().getSmAppl().getSmApplInquiries() == null) {
					getManagedBean().getSmAppl().setSmApplInquiries(new ArrayList<SmApplInquiry>());
				}
				getManagedBean().getPoToSupplierInquiry().setInquirySubject(SupplierManagementConst.INQUIRY_PO_SUPPLIER);
				getManagedBean().getPoToSupplierInquiry().setSmAppl(getManagedBean().getSmAppl());
				getManagedBean().getPoToSupplierInquiry().setInquiryDate(new Date());
				getManagedBean().getSmAppl().getSmApplInquiries().add(getManagedBean().getPoToSupplierInquiry());
			}
			// #14530 - end
			
			// #14530 - Start
			this.getManagedBean().getPoToSupplierInquiry().setApplInquiryId(reqSO.retrieveSeqNextVal("SM_APPL_INQUIRY_SEQ"));
			List<SmApplSupportDocInquiry> smApplSupportDocInquiries = new ArrayList<SmApplSupportDocInquiry>();
			List<SmApplInquiryDocFile> smApplInquiryDocFiles = new ArrayList<SmApplInquiryDocFile>();

			for (SmApplSupportDoc smApplSupportDoc : getManagedBean().getMissinDocs()) {
				SmApplSupportDocInquiry smApplSupportDocInquiry = new SmApplSupportDocInquiry();
				smApplSupportDocInquiry.setPmDocument(smApplSupportDoc.getPmDocument());
				smApplSupportDocInquiry.setPoRemarks(smApplSupportDoc.getPoRemarks());
				smApplSupportDocInquiry.setReceived(smApplSupportDoc.isInquiryReceived());
				smApplSupportDocInquiry.setSmApplInquiry(this.getManagedBean().getPoToSupplierInquiry());
				smApplSupportDocInquiry.setApplSupportDocInquiryId(reqSO
						.retrieveSeqNextVal("SM_APPL_SUPPDOC_INQUIRY_SEQ"));
				
				//#34392 - Start
				setValueForInquiryRemarks(smApplSupportDoc);
				smApplSupportDocInquiry.setPoRemarks(smApplSupportDoc.getPoRemarks());
				//#34392 - End

				smApplSupportDoc.setInquiryRemark("");
				smApplSupportDoc.setInquiryReceived(false);

				for (SmApplDocFile smApplDocFile : smApplSupportDoc.getSmApplDocFiles()) {

					SmApplInquiryDocFile smApplInquiryDocFile = new SmApplInquiryDocFile();
					smApplInquiryDocFile.setApplInquiryDocFileId(reqSO.retrieveSeqNextVal("SM_APPL_INQ_FILE_SEQ"));
					smApplInquiryDocFile.setFileName(smApplDocFile.getFileName());
					smApplInquiryDocFile.setFilePath(smApplDocFile.getFilePath());
					smApplInquiryDocFile.setSmApplSupportDocInquiry(smApplSupportDocInquiry);

					smApplInquiryDocFiles.add(smApplInquiryDocFile);
				}

				smApplSupportDocInquiry.setSmApplInquiryDocFiles(smApplInquiryDocFiles);
				smApplSupportDocInquiries.add(smApplSupportDocInquiry);
			}
			this.getManagedBean().getPoToSupplierInquiry().setSmApplSupportDocInquiries(smApplSupportDocInquiries);
			// #14530 - End
			log.info("**** Send Inquiry List Size ***** "
					+ getManagedBean().getPoToSupplierInquiry().getSmApplSupportDocInquiries().size());

			actionDesc = SupplierManagementConst.PO_SEND_INQUIRY_SUPP;
			// 14530
			sendEmailtoSupplier(reqDTO);
		}
		if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REJECT)) {

			if (getManagedBean().isPoUser()) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER);
				actionDesc = SupplierManagementConst.PO_REJECT_APPL;
			} else if (getManagedBean().isApproverUser()) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER);
				actionDesc = SupplierManagementConst.APPROVER_REJECT_APPL;
			}
			if (!getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				setSmApplRejectionReasons();
				reqDTO.setRejectionReasons(getManagedBean().getReasonDesc());
			}
			if (StringUtils.isNotBlank(getManagedBean().getApplnRejectionRemarks())) {
				SmApplicationUtil.manageAndSaveNewSmRemarks(getManagedBean().getSmAppl().getApplId(),
						ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
						getManagedBean().getApplnRejectionRemarks());
			}
			
			//10908
			String loginId="";
			String langCode="";
			for(SmPersonnel personnel:getManagedBean().getSmAppl().getSmPersonnels()){
				log.info("personnel.getEpRole()>>>"+personnel.getEpRole()+"-------"+RoleCode.MOF_SUPPLIER_ADMIN.toString());
				if(personnel.getPmUser() != null //#32435 
						&& personnel.getEpRole() != null && (personnel.getEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.toString()) || personnel.getEpRole().equalsIgnoreCase(RoleCode.SUPPLIER_TEMP.value()))){
					loginId = personnel.getPmUser().getLoginId();
					langCode = personnel.getPmUser().getPreferredLanguage();
					log.info("loginId109082>>>>>>>>>>" + loginId);
					log.info("Language>>>>>>>>>>" + personnel.getPmUser().getPreferredLanguage());
					continue;
				}
			}
			log.info("langCode>>>>>>>>>>" + langCode);
			reqDTO.setLangCode(langCode);
			reqDTO.setSuppLoginId(loginId);
			
			//10908
			SmApplicationNotificationUtil.setRejectionNotificationParameters(
					getManagedBean().getSmAppl(), getManagedBean().getSmSupplier(), 
					getManagedBean().getBasicInfo(), getManagedBean()
					.getSupplierRemarks(), getManagedBean()
					.getApplnRejectionRemarks(), reqDTO);
			//#30766 & #34209 - START
			//List<SmSupplierCategory> catList = getManagedBean().getSmAppl().getSmSupplierCategories();
			//setPOorApprDec(catList);
			//#30766 & #34209 - END
		} else if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPROVE)) {
			PmRegStatus regStatus = null;
			if (getManagedBean().isPoUser()) {
				actionDesc = SupplierManagementConst.PO_APPROVE_APPL;
			} else if (getManagedBean().isApproverUser()) {
				actionDesc = SupplierManagementConst.APPROVER_APPROVE_APPL;
			}
			if (getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_APPROVED_BY_APPROVER);
			} else {
				regStatus = SmApplicationUtil.setApplicationApproveStatus(getManagedBean().getSmAppl(), pmStatId);
				reqDTO.setPaymentReqd(SmApplicationUtil.getPaymentMode(getManagedBean().getSmAppl()));
			}

			if (regStatus == null && !getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				throw new EpException(MessageConst.ERROR_722662);
			}

			// approve categories
			SmApplicationUtil.approveApplCategories(getManagedBean().isApproverUser(), getManagedBean().isPoUser(), getManagedBean().getSmAppl());
			
			//10908
			String loginId="";
			String langCode="";
			for(SmPersonnel personnel:getManagedBean().getSmAppl().getSmPersonnels()){
				log.info("personnel.getEpRole()>>>"+personnel.getEpRole()+"-------"+RoleCode.MOF_SUPPLIER_ADMIN.toString());
				if(personnel.getPmUser() != null //#32435
						&& personnel.getEpRole() != null && (personnel.getEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.toString()) || personnel.getEpRole().equalsIgnoreCase(RoleCode.SUPPLIER_TEMP.value()))){
					loginId = personnel.getPmUser().getLoginId();
					langCode = personnel.getPmUser().getPreferredLanguage();
					log.info("loginId109082>>>>>>>>>>" + loginId);
					log.info("Language>>>>>>>>>>" + personnel.getPmUser().getPreferredLanguage());
					continue;
				}
			}
			log.info("langCode>>>>>>>>>>" + langCode);
			reqDTO.setLangCode(langCode);
			reqDTO.setSuppLoginId(loginId);
			
			//10908
			SmApplicationNotificationUtil.setApprovalNotificationParameters(getManagedBean().getSmAppl(), getManagedBean().getBasicInfo(), 
					getManagedBean().isPoUser(), getManagedBean().getSmSupplier(), getManagedBean().getSupplierRemarks(), 
					getManagedBean().getApplnApprovalRemarks(), reqDTO);
			
			// #29664 - start
			/*if (StringUtils.isNotBlank(getManagedBean()
					.getApplnApprovalRemarks())) {
				SmApplicationUtil.manageAndSaveNewSmRemarks(getManagedBean()
						.getSmAppl().getApplId(),
						ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE,
						getManagedBean().getApplnApprovalRemarks());
			}*/
			if (StringUtils.isNotBlank(getManagedBean().getApplnApprovalRemarks())) {
				SmApplicationUtil.manageAndSaveNewSmRemarksWithRoleCode(getManagedBean().getSmAppl().getApplId(),
						ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE,
						getManagedBean().getApplnApprovalRemarks(), getManagedBean().getRoleCode());
			}
			// #29664 - end
			
			if (getManagedBean().isOverLappedCategoryExists() && StringUtils.isNotBlank(getManagedBean().getApplnOverLapRemarks())) {
				SmApplicationUtil.manageAndSaveNewSmRemarks(getManagedBean().getSmAppl().getApplId(),
						ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY,
						getManagedBean().getOverLappedCategoryRemarks());
				SmApplicationUtil.manageAndSaveNewSmRemarks(
								getManagedBean().getSmAppl().getApplId(),
								ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER,
								getManagedBean().getApplnOverLapRemarks());
			}

			// #17064 - update establish_date in supplier if there is any
			// changes
			if (SmCommonUtil.isHasDifferences(getManagedBean().getLatestAppl(),
					getManagedBean().getSmAppl(),
					new String[] { "establishDate" })) {
				reqDTO.setEstablishDate(getManagedBean().getSmAppl().getSmCompanyBasic().getEstablishDate());

				SmApplicationUtil.updateSmSupplierInfo(getManagedBean(), reqDTO);
			}

		}

		//#12483 - START
		else if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REVISE)){
			setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_REVISE_APPLICATION);
			actionDesc = SupplierManagementConst.APPROVER_SEND_REVISE_APPL;
			reqDTO.setSmApplRevise(this.getManagedBean().getSmApplRevise());
		}
		//#12483 - END
		//log.info("Before final decision ---"
				//+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
		validSiteVisit = testSmAppl(getManagedBean().getSmAppl().getApplType(),
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC, "", true);
		//#28175
		updateCategoryCodeNames();
		if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPROVE)) {
		}
		if (validSiteVisit) {
			String successMsg = "";
			SmMofAccount mofAcc = getManagedBean().getSmSupplier() != null
					&& getManagedBean().getSmAppl() != null ? SmCommonUtil.getMofAccountBySuppIdAndSuppType(getManagedBean().getSmSupplier().getSupplierId(), 
							getManagedBean().getSmAppl().getSupplierType()) : null;
			String mofNo = mofAcc != null
					&& StringUtils.isNotBlank(mofAcc.getMofNo()) ? mofAcc
					.getMofNo() : "";
					
			String applNo = getManagedBean().getSmAppl().getApplNo();
			String applTypeDesc = getParameterDesc(
					ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
					SmApplicationUtil.setParameterConst(getManagedBean().getSmAppl().getApplType()));
			if (getManagedBean().isPoUser() || getManagedBean().isApproverUser()) {
				log.info(" **** Final Decision from PO ****");
				if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPROVE)) {

					// set in progress application other than cancel application
					// type to be withdraw (#24139)
					if (getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
						List<SmAppl> inProgressApplList = SmCommonUtil.getActiveApplListBySupplier(getManagedBean().getSmAppl().getSmSupplier());

						if (!EntityHelper.isCollectionEmpty(inProgressApplList)) {
							log.info("in progress applicaition size:" + inProgressApplList.size());
							for (SmAppl tmp : inProgressApplList) {
								if (tmp.getApplType() != null && !tmp.getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
									reqDTO.setInProgressAppl(tmp);
								}
							}
						}
						//#31386 - START
						List<SmAppl> allAppl = reqSO.getActiveApplForSVAP(getManagedBean().getSmAppl());
						if(!EntityHelper.isCollectionEmpty(allAppl)){
							reqDTO.setPendingAppls(allAppl);
						}
						//#31386 - END
					}

					if (getManagedBean().getSmAppl().isBumi()
							&& getManagedBean().getSmAppl().getPmRegStatus() != null
							&& !getManagedBean().getSmAppl().getPmRegStatus().isBumiAppl()) {
						throw new EpException(MessageConst.ERROR_722671);
					}
					if (StringUtils.isNotBlank(mofNo)) {
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700027, new String[] {
										applNo, mofNo });
					} else {
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700030,
								new String[] { applNo });
					}
				} else if (res
						.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REJECT)) {
					if (getManagedBean().getBpmTaskName() != null
							&& getManagedBean()
									.getBpmTaskName()
									.equalsIgnoreCase(
											SupplierManagementConst.BPM_TASK_REVIEW_SITE_VISIT_AFTER_APPROVE)) {
						//#29823 - Start
						if(this.getManagedBean().getSmAppl() != null){
							//#29663 - START
							if (getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
								reqDTO.setLastApprovedAppl(SmApplicationUtil.getLastApprovedApplictionForSupplier(getManagedBean(), res));
								reqDTO.getLastApprovedAppl().setBumi(false);
								getManagedBean().getSmAppl().setBumi(false);
								getManagedBean().getSmAppl().setPmRegStatus(null);
								getManagedBean().getSmAppl().getSmSupplier().setBumi(false);
							}else{
								SmAppl lastApplAppr = SmApplicationUtil.getLastApprovedApplictionForSupplier(getManagedBean(), res);
								reqDTO.setLastApprovedAppl(lastApplAppr);
							}
							//#29663 - END
						}else{
							reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
						}
						/*	if (getManagedBean().getSmAppl() == null){
							reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
						}*/
						//#29823 - end
						/*reqDTO.setLastApprovedAppl(SmApplicationUtil
								.getLastApprovedApplictionForSupplier(getManagedBean()));*/
						
					}
					
					// #29823 - start
					//For NEW_SM_APPLN_FOR_MOF 
					if (getManagedBean().getSmAppl()== null) {
						reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
						getManagedBean().getSmAppl().getSmSupplier().setSmAppl(null);
					}
					// #29823 - end
					
					if (StringUtils.isNotBlank(mofNo)) {
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700028, new String[] {
										applNo, mofNo });
					} else {
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700029,
								new String[] { applNo });
					}
				} else if (res
						.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.ESCALATE)) {
					String statusDesc = "";
					if (getManagedBean().getPoSecReview() != null
							&& getManagedBean().getPoSecReview()
									.getRecommendation().equals("APPROVE")) {
						statusDesc = SmCommonUtil.getMessage(
								"lbl.sm.approval.lowercaps", null);
						// set po recommendation registration status
						PmStatus status = PmCommonUtil
								.getPmStatusById(getManagedBean()
										.getPoRecommendStatusId());
						getManagedBean().getPoSecReview().setPmRegStatus(
								status.getPmRegStatus());
						setPoOrApprvrSectionReview();
					} else {
						statusDesc = SmCommonUtil.getMessage(
								"lbl.sm.rejected.lowercaps", null);
						// Bug #21123
						if (getManagedBean().getPoSecReview() != null
								&& getManagedBean().getPoSecReview()
										.getRecommendation().equals("REJECT")) {
							//21937 - start
							PmStatus status = PmCommonUtil
									.getPmStatusById(getManagedBean()
											.getPoRecommendStatusId());
							getManagedBean().getPoSecReview().setPmRegStatus(
									status.getPmRegStatus());
							//21937 - end
							
							if (getManagedBean().isPoUser()
									&& getManagedBean().getPoSecReview()
											.getApplSectionReviewId() != null) {
								for (SmApplSectionReview rev : getManagedBean()
										.getSmAppl().getSmApplSectionReviews()) {
									if (rev.getApplSectionReviewId().equals(
											getManagedBean().getPoSecReview()
													.getApplSectionReviewId())) {
										if (getManagedBean().getPoSecReview()
												.getRemarkToApprover() != null) {
											getManagedBean()
													.getPoSecReview()
													.setRemarkDate(
															new Timestamp(
																	System.currentTimeMillis()));
										}
										getManagedBean()
												.getSmAppl()
												.getSmApplSectionReviews()
												.set(getManagedBean()
														.getSmAppl()
														.getSmApplSectionReviews()
														.indexOf(rev),
														getManagedBean()
																.getPoSecReview());
									}
								}

							}
						}
					}

					actionDesc = SupplierManagementConst.PO_ESCALATE_APPROVER;
					//#12483 - START
					if(this.getManagedBean().isPoUser() && currentReviseReason() != null){
						PmUser approver = PpCommonUtil.retrieveUserByLoginIdOrUserId(null, SmServiceLookup.getUserSO().retrieveLastPoOrApprover(this.getManagedBean().getSmAppl().getApplNo(), SupplierManagementConst.APPROVER_HQ));
						if(approver.getRecordStatusFlg() == 1){
							this.getManagedBean().setReviseApprover(approver);
							successMsg = SmCommonUtil.getOutputMsgBundle(
									MessageConst.INFO_700014,
									new String[] { applNo, statusDesc,
											"<tr><td>1)</td><td>"+PpCommonUtil.retrieveUserByLoginIdOrUserId(approver.getLoginId(), null).getUserName()+"</td></tr>" });
							
						} else {
							successMsg = SmCommonUtil.getOutputMsgBundle(
									MessageConst.INFO_700014,
									new String[] { applNo, statusDesc,
											getApproverListForSuccessMsg() });
						}
					}else{
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700014,
								new String[] { applNo, statusDesc,
										getApproverListForSuccessMsg() });
					}
					//#12483 - END
				} else if (res
						.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.INQUIRY)) {
					successMsg = SmCommonUtil.getOutputMsgBundle(
							MessageConst.INFO_700016, new String[] {
									applTypeDesc, applNo });
				} else if (res
						.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.SITE_VISIT)) {
					successMsg = SmCommonUtil.getOutputMsgBundle(
							MessageConst.INFO_700015, new String[] { applNo });
				} else if (res
						.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.SITE_VISIT_AFTER_APPROVE)) {
					successMsg = SmCommonUtil.getOutputMsgBundle(
							MessageConst.INFO_700017, new String[] { applNo });
				}
				//#12483 - START
				else if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REVISE)){
					successMsg = SmCommonUtil.getOutputMsgBundle(
							MessageConst.INFO_700023, new String[] { applNo, PpCommonUtil.retrieveUserByLoginIdOrUserId(reqDTO.getPoLoginId(), null).getUserName() });
				}
				//#12483 - END
				reqDTO.setPoOrApproverDecision(res);
				SmCommonUtil.getInstance().setUserDetails(
						this.getManagedBean().getSmAppl());
				//14530 - START
				if(this.getManagedBean().getPoToSupplierInquiry()!= null){
					this.getManagedBean().getPoToSupplierInquiry().setSmAppl(this.getManagedBean().getSmAppl());
					reqDTO.setPoToSupplierInquiry(this.getManagedBean().getPoToSupplierInquiry());
				}
				//14530 - END
				
				reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
				reqDTO.setPoRole(getManagedBean().isPoUser());
				reqDTO.setApproverRole(getManagedBean().isApproverUser());
				
				//#12483 - START
				if(reqDTO.getUserId() != null){
					reqDTO.getSmAppl().setPoUserId(reqDTO.getUserId());
				}
				//#12483 - END

				FacesScopes fs = new FacesScopes();
				reqDTO.setServerName(fs.getRequest().getServerName());
				reqDTO.setServerPort(fs.getRequest().getServerPort());
				reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());

				reqDTO.setSvTaskIdsNeedToBeRemoved(getManagedBean()
						.getSvTaskIdsNeedToDelete());

				SmAddress hqAddr = getHqAddress();
				if (hqAddr != null) {
					ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
					zoneReqDTO.setStateId(hqAddr.getPmState().getStateId());
					PmZone zone = SmServiceLookup.getZoneSO()
							.retrieveZoneByStateId(zoneReqDTO).getPmZone();
					String approverLoginIds = SmCommonUtil
							.retrieveLoginIdsByZoneAndRole(zone.getZoneCode(),
									"UPS_APPROVER", getManagedBean()
											.getSmAppl().getSupplierType(),
									getManagedBean().getSmAppl().getApplType(),
									getManagedBean().getSmAppl()
											.getApplCategoryType());
					//#12483 - START
					if(this.getManagedBean().isPoUser() && currentReviseReason() != null){
						if(this.getManagedBean().getReviseApprover() != null && this.getManagedBean().getReviseApprover().getRecordStatusFlg() == 1){
							reqDTO.setApproverLoginIds(this.getManagedBean().getReviseApprover().getLoginId());
						} else {
							reqDTO.setApproverLoginIds(approverLoginIds);
						}
					}else{
						reqDTO.setApproverLoginIds(approverLoginIds);
					}
					//#12483 - END
				}
				reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
				reqSO.submitPOorApproverReviewToBPM(reqDTO);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								successMsg));
				renewSesionObject();
				retMsg = SmApplicationUtil.getApplicationErrorPage();

			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Sample error message",
								"Un Authorized user to perform this action"));
				retMsg = null;
			}
		} else {
			retMsg = null;
		}
		//#34400 - start
		RequestContext.getCurrentInstance().execute(
				"$('.ui-dialog:visible').hide();$('.ui-widget-overlay').remove();");
		//#34400 - end
		return retMsg;
	}
	
	// #34392 - Start
	private void setValueForInquiryRemarks(SmApplSupportDoc smApplSupportDoc) {

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getMissinDocsMandatory()) && smApplSupportDoc.getPmDocument().isMandatory()) {
			for (SmApplSupportDoc smApplSupportDocMandatory : getManagedBean().getMissinDocsMandatory()) {
				if (smApplSupportDoc.getApplSupportDocId().equals(smApplSupportDocMandatory.getApplSupportDocId())) {
					smApplSupportDoc.setPoRemarks(smApplSupportDocMandatory.getInquiryRemark());
				}
			}
		}

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getMissinDocsNonMandatory())
				&& !smApplSupportDoc.getPmDocument().isMandatory()) {
			for (SmApplSupportDoc smApplSupportDocNonMandatory : getManagedBean().getMissinDocsNonMandatory()) {
				if (smApplSupportDoc.getApplSupportDocId().equals(smApplSupportDocNonMandatory.getApplSupportDocId())) {
					smApplSupportDoc.setPoRemarks(smApplSupportDocNonMandatory.getInquiryRemark());
				}
			}
		}
	}
	// #34392 - End

	private boolean showPoApprovedOrRecommendButton() {
		boolean retVal = true;
		if (getManagedBean().getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
				|| getManagedBean().getSmAppl().getApplCategoryType()
						.equals("S")) {
			if (EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
					.getSmSvTasks())) {
				retVal = false;
			}
		}
		return retVal;
	}

	private String getApproverListForSuccessMsg() {
		StringBuilder bf = new StringBuilder();
		String retVal = SmCommonUtil.getMessage("lb.sm.approver", null);
		SmAddress hqAddr = getHqAddress();
		if (hqAddr != null) {
			ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
			zoneReqDTO.setStateId(hqAddr.getPmState().getStateId());
			PmZone zone = SmServiceLookup.getZoneSO()
					.retrieveZoneByStateId(zoneReqDTO).getPmZone();
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			/*#28154 - start*/
			if (getManagedBean().getSmSupplier().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
				reviewApplicationReqDTO.setZoneCode("1");
			} else {
				reviewApplicationReqDTO.setZoneCode(zone.getZoneCode());
			}
			/*#28154 - end*/
			List<PmUser> approverList = SmServiceLookup
					.getReviewApplicationSO()
					.getApproversByZoneAfterEscalate(reviewApplicationReqDTO)
					.getUserAssignList();
			if (SmCommonUtil.isCollectionEmpty(approverList)) {
				throw new EpException(MessageConst.ERROR_722658,
						new String[] { zone.getZoneName() });
			} else {
				List<PmUser> displayApproverList = new ArrayList<>();
				for (PmUser user : approverList) {
					if (!user.getUserId().equals(getManagedBean().getSmAppl().getPoUserId())) {
						displayApproverList.add(user);
					}
				}
				int i = 0;
				for (PmUser user : displayApproverList) {
					i++;
					bf.append("<tr><td>").append(i).append(")</td><td>")
							.append(user.getUserName()).append("</td></tr>");
				}
			}
			/*#28154 - end*/
		}
		if (bf.length() > 0) {
			retVal = bf.toString();
		}
		return retVal;
	}

	/**
	 * Test sm application validations before save or update to DB.
	 * 
	 * @param applType
	 *            - the application type
	 * @param currentPageType
	 *            -the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @param isBpmSubmit
	 *            - boolean
	 * @return true, if successful
	 */
	public boolean testSmAppl(String applType, String currentPageType, String outputPageType, Boolean isBpmSubmit) {
		boolean valid = false;
		boolean isError = false;
		try {

			if (getManagedBean().isPoUser()
					|| getManagedBean().isApproverUser()) {
				setPoOrApprvrSectionReview();
			}

			valid = validateBeforeSave(applType, currentPageType);
			// valid = true;
			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				log.info(" ****** REMAIN PREV VALUE ********");
			}
			if (valid) {
				// Save or update to DB
				getManagedBean().setCurrentPageType(outputPageType);

			} else {
				getManagedBean().setTabActiveIndex(currentTab);
				getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
				getManagedBean().setCurrentPageType(currentPageType);
			}
			isError = valid;
		}

		finally {
		}
		return isError;
	}

	/**
	 * Sets the payment mode i.e. to determine if payment required.
	 * 
	 * @return true, if successful
	 */
	/*
	private boolean setPaymentMode() {
		boolean payReq = false;
		if (getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			payReq = true;
		}
		return payReq;
	}
	*/

	/**
	 * Change all sections verification value based on selection on screen.
	 * 
	 * @param isVerifiedAll
	 *            - boolean
	 */
	public void changeVerifyAll(Boolean isVerifiedAll) {
		log.info(" **** Enter to  verified all ***** " + isVerifiedAll);
		SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			sec = getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					SmApplicationUtil.setVerificationValues(sec, isVerifiedAll,
							getManagedBean().getMaxReviewCount(),
							getManagedBean()));
			SmApplicationUtil.checkAllowedToRecommendSiteVisit(getManagedBean()); //#31043
		} else if (getManagedBean().isApproverUser()) {
			sec = getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					SmApplicationUtil.setVerificationValues(sec, isVerifiedAll,
							getManagedBean().getMaxReviewCount(),
							getManagedBean()));
		}

	}

	/**
	 * Change section verification value based on selection on screen.
	 * 
	 * @param isSectionVerified
	 *            - boolean
	 */
	public void changeVerifySection(Boolean isSectionVerified) {
		if (getManagedBean().isPoUser()) {
			getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					assignVerificationCount(getManagedBean().getPoSecReview(),
							isSectionVerified));
		} else if (getManagedBean().isApproverUser()) {
			getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					assignVerificationCount(getManagedBean()
							.getApprvrSecReview(), isSectionVerified));
		}
	}

	/**
	 * Set company information details (MOF No. and address contact details by
	 * registration no.)
	 * 
	 * @param Object
	 *            obj
	 * @param String
	 *            panel
	 */
	public void onBlurRegNo(Object obj, String panel) {
		try {
			String objRegNoVal = obj == null ? StringUtils.EMPTY
					: PropertyUtils.getProperty(obj, "regNo").toString();
			if (!StringUtils.isBlank(objRegNoVal)) {
				RegistrationReqDTO reqDTO = new RegistrationReqDTO();
				reqDTO.setRegNo(objRegNoVal);
				SmSupplier supplier = null;
				List<SmSupplier> suppliers = SmServiceLookup
						.getRegistrationSO()
						.retrieveActiveSupplierByRegNo(reqDTO).getSmSuppliers();
				supplier = !EntityHelper.isCollectionEmpty(suppliers) ? suppliers
						.get(0) : supplier;

				if (supplier != null
						&& !EntityHelper.isCollectionEmpty(supplier
								.getSmMofAccounts())
						&& supplier.getSmMofAccounts().get(0)
								.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {

					PropertyUtils.setProperty(obj, "mofNo", supplier
							.getSmMofAccounts().get(0).getMofNo());
					PropertyUtils.setProperty(obj, "companyName",
							supplier.getCompanyName());
					SmAddress addr = (SmAddress) PropertyUtils.getProperty(obj,
							"smAddress");
					if (supplier.getSmAppl() != null
							&& supplier.getSmAppl().getSmCompanyBasic() != null
							&& !EntityHelper.isCollectionEmpty(supplier
									.getSmAppl().getSmCompanyBasic()
									.getSmCompanyAddresses())) {
						if (supplier.getSmAppl().getSmCompanyBasic()
								.getSmCompanyAddresses().get(0).getSmAddress() != null
								&& addr != null) {
							SmCompanyBasic compBasic = supplier.getSmAppl()
									.getSmCompanyBasic();
							SmAddress compBasicAddr = compBasic
									.getSmCompanyAddresses().get(0)
									.getSmAddress();
							addr.setAddress1(compBasicAddr.getAddress1());
							addr.setAddress2(compBasicAddr.getAddress2());
							addr.setAddress3(compBasicAddr.getAddress3());
							addr.setPostcode(compBasicAddr.getPostcode());
							if (compBasicAddr.getPmCountry() != null) {
								addr.setCountryId(compBasicAddr.getPmCountry()
										.getCountryId());
								addr.setPmCountry(SmCommonUtil.getInstance()
										.getCountryById(addr.getCountryId()));
								onChangeCountry(addr, panel);
							}
							if (compBasicAddr.getPmState() != null) {
								addr.setStateId(compBasicAddr.getPmState()
										.getStateId());
								addr.setPmState(SmCommonUtil.getInstance()
										.getStateById(addr.getStateId()));
								onChangeState(addr, panel);
							}
							//#13320 - added division - start
							if(compBasicAddr.getPmDivision() != null){
								addr.setDivisionId(compBasicAddr.getPmDivision()
										.getDivisionId());
								addr.setPmDivision(SmCommonUtil.getInstance()
										.getDivisionByUsingId(addr.getDivisionId()));
								onChangeDistrictByDivision(addr, panel);
							}
							//#13320 - added division - end
							if (compBasicAddr.getPmDistrict() != null) {
								addr.setDistrictId(compBasicAddr
										.getPmDistrict().getDistrictId());
								addr.setPmDistrict(SmCommonUtil.getInstance()
										.getDistrictById(addr.getDistrictId()));
								onChangeDistrict(addr, panel);
							}
							if (compBasicAddr.getPmCity() != null) {
								addr.setCityId(compBasicAddr.getPmCity()
										.getCityId());
								addr.setPmCity(SmCommonUtil.getInstance()
										.getCityById(addr.getCityId()));
								onChangeCity(addr, panel);
							}

							PropertyUtils.setProperty(obj, "phoneCountry",
									compBasic.getPhoneCountry());
							PropertyUtils.setProperty(obj, "phoneArea",
									compBasic.getPhoneArea());
							PropertyUtils.setProperty(obj, "phoneNo",
									compBasic.getPhoneNo());
							PropertyUtils.setProperty(obj, "faxCountry",
									compBasic.getFaxCountry());
							PropertyUtils.setProperty(obj, "faxArea",
									compBasic.getFaxArea());
							PropertyUtils.setProperty(obj, "faxNo",
									compBasic.getFaxNo());
						}
					}
				}
			}
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * When click radio button at facility type, check whether is allowed to
	 * change
	 */
	public void onChangeFacilityType() {
		// #32236 - Start
		if (getManagedBean().isAlreadyBusinessSpecialCatcode() && getManagedBean().getBusinessOfficeCatCodeMsg() != null) {
			getManagedBean().getBasicCompanyFacility().setFacilityType("B");
			RequestContext.getCurrentInstance().execute(
					"alertFacTypeChangeDlg.show();");
		}
		// #32236 - End
	}

	/**
	 * Get whether supplier already is business office and had approved special
	 * category
	 */
	private void setBusinessAndHadApprovedSpecialCatcode() {
		getManagedBean().setAlreadyBusinessSpecialCatcode(false);
		// #32236 - Start
		getManagedBean().setBusinessOfficeCatCodeMsg(null);
		StringBuilder bf = new StringBuilder();
		int index = 1;
		//Bug #26703
//		if (getManagedBean().getSmAppl().getApplType()
//				.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
//				&& SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
//						.getSmSupplierCategories())) {
		SmFacility hq = getHq();
		// #32236 - Start
		if (hq != null && hq.getFacilityType().equals("B")) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl().getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
						&& cat.getPmCategoryL3().getHomeOperated() != DbValueConst.RECORD_STATUS_ACTIVE
						// Bug #27188-start
						|| cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
						&& cat.getPmCategoryL3().getHomeOperated() != DbValueConst.RECORD_STATUS_ACTIVE
						|| cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL
						&& cat.getPmCategoryL3().getHomeOperated() != DbValueConst.RECORD_STATUS_ACTIVE) {
					// Bug #27188-end
					getManagedBean().setAlreadyBusinessSpecialCatcode(true);
					// #32236 - Start
					if (index == 1) {
						bf.append("<br> " + index + "." + " " + cat.getPmCategoryL1().getCategoryL1Code()
								+ cat.getPmCategoryL2().getCategoryL2Code() + cat.getPmCategoryL3().getCategoryL3Code()
								+ "," + "<br />");
						index++;
					} else if (index > 1) {
						bf.append(index + "." + " " + cat.getPmCategoryL1().getCategoryL1Code()
								+ cat.getPmCategoryL2().getCategoryL2Code() + cat.getPmCategoryL3().getCategoryL3Code()
								+ "," + "<br />");
						index++;
					}
				}
				// }
			}
			Locale locale = new FacesScopes().getUserLocale();

			String msg = ResourceBundleUtils.getMessage("lbl.sm.not.allowed.change.factype", new String[] { "" + "-"
					+ bf.toString() }, ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
			// #32236 - End
			// getManagedBean().setBusinessOfficeCatCodeMsg(
			// SmCommonUtil.getMessage(
			// "lbl.sm.not.allowed.change.factype.home.office", null)
			// + "<br /> - " + bf.toString());
			getManagedBean().setBusinessOfficeCatCodeMsg(msg);
			getManagedBean().setBusinessOfficeCatCodeMsg(
					StringUtils.removeEnd(getManagedBean().getBusinessOfficeCatCodeMsg(), ","));
		}
		// #32236 - End
	}

	/**
	 * Get HQ
	 * 
	 * @return SmFacility
	 */
	private SmFacility getHq() {
		SmFacility hq = null;
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmFacilities())) {
			for (SmFacility fac : getManagedBean().getSmAppl()
					.getSmFacilities()) {
				//#32236 - start
				if (StringUtils.isNotBlank(fac.getFacilityType())
						&& (fac.getFacilityType().equals("H") || fac
								.getFacilityType().equals("B")) && (fac.isHeadquarters())) {
					hq = fac;
					break;
				}
				//#32236 - end
			}
		}
		return hq;
	}

	/**
	 * Add or remove verification count for sections.
	 * 
	 * @param sec
	 *            -SmApplSectionReview object
	 * @param isSectionVerified
	 *            - boolean
	 * @return the sm appl section review object
	 */
	private SmApplSectionReview assignVerificationCount(
			SmApplSectionReview sec, Boolean isSectionVerified) {
		int maxReviewCount = getMaxSecCountByShowPanel();
		if (isSectionVerified) {
			sec.setCheckCount(sec.getCheckCount() + 1);

		} else {
			sec.setCheckCount(sec.getCheckCount() - 1);

		}
		if (sec.getCheckCount() >= maxReviewCount) {
			sec.setVerifiedAll(Boolean.TRUE);
		} else {
			sec.setVerifiedAll(Boolean.FALSE);
		}

		return sec;
	}

	private void reCheckVerifiedSection() {
		if (getManagedBean().isPoUser() || getManagedBean().isApproverUser()) {
			SmApplicationUtil.checkAllowedToApprove(getManagedBean());
			SmApplicationUtil.checkAllowedToRecommendSiteVisit(getManagedBean()); //31043
			int maxReviewCount = getMaxSecCountByShowPanel();

			SmApplSectionReview sec = getManagedBean().isPoUser() ? getManagedBean()
					.getPoSecReview() : getManagedBean().getApprvrSecReview();

			if (!getManagedBean().isShowTravelSoftwarePanel()
					&& sec.isSupplierSystemVerified()) {
				sec.setSupplierSystemVerified(false);
			}
			if (!getManagedBean().isShowMachineryEquipmentPanel()
					&& sec.isMachineryVerified()) {
				sec.setMachineryVerified(false);
			}
			if (!getManagedBean().isShowFactoryPanel()
					&& sec.isManufacturingVerified()) {
				sec.setManufacturingVerified(false);
			}

			if (!getManagedBean().isShowDockyardPanel()
					&& !getManagedBean().isShowFactoryPanel()
					&& !getManagedBean().isShowStorePanel()
					&& !getManagedBean().isShowWorkShopPanel()) {
				if (getManagedBean().getSmAppl().getApplCategoryType()
						.equals("S")) {
					sec.setFactoryVerify(true);
				} else {
					sec.setFactoryVerify(false);
				}
			}

			sec.setCheckCount(getTotalVerifiedSec(sec));
			if (sec.getCheckCount() >= maxReviewCount
					&& getManagedBean().isAllowedToApprove()) {
				sec.setVerifiedAll(Boolean.TRUE);
			} else {
				sec.setVerifiedAll(Boolean.FALSE);
			}
		}
	}

	private int getTotalVerifiedSec(SmApplSectionReview sec) {
		int retVal = 0;

		/* STEP 1 */

		// company information
		if (sec.isBasicVerified()) {
			retVal = retVal + 1;
		}
		// staff information
		if (sec.isStaffVerified()) {
			retVal = retVal + 1;
		}
		// premise type
		if (sec.isOfficeFacilityVerfied()) {
			retVal = retVal + 1;
		}
		// certificate of recognition
		if (sec.isCertVerified()) {
			retVal = retVal + 1;
		}
		// business network
		if (sec.isTrnsNetworkVerified()) {
			retVal = retVal + 1;
		}
		// professional body
		if (sec.isAssocBodyVerified()
				&& getManagedBean().getSmAppl().getSupplierType().equals("J")) {
			retVal = retVal + 1;
		}

		/* STEP 2 */

		// associate companies
		if (sec.isAssociateVerified()) {
			retVal = retVal + 1;
		}
		// company project experience
		if (sec.isExperienceVerified()) {
			retVal = retVal + 1;
		}

		/* STEP 3 */

		// company BOD/Staff/Management
		if (sec.isPersonnelVerified()) {
			retVal = retVal + 1;
		}
		// company branches
		if (sec.isBranchVerified()) {
			retVal = retVal + 1;
		}

		/* STEP 4 */

		// capital/equity status
		if (sec.isCaptialVerified()) {
			retVal = retVal + 1;
		}
		// capital/equity shareholders
		if (sec.isShareholderVerifed()) {
			retVal = retVal + 1;
		}
		// capital/equity shareholding companies
		if (sec.isEquityCompVerifed()
				&& getManagedBean()
						.getBusinessTypeVal()
						.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
				|| getManagedBean()
						.getBusinessTypeVal()
						.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT)) {
			retVal = retVal + 1;
		}

		/* STEP 5 */

		// bank information
		if (sec.isBankVerified()) {
			retVal = retVal + 1;
		}

		/* STEP 6 */

		// factory/workshop/dockyard/store
		if (sec.isFactoryVerify()) {
			retVal = retVal + 1;
		}

		/* STEP 7 */

		// manufacturing
		if (sec.isManufacturingVerified()
				&& getManagedBean().isShowFactoryPanel()) {
			retVal = retVal + 1;
		}
		// machinery
		if (sec.isMachineryVerified()
				&& getManagedBean().isShowMachineryEquipmentPanel()) {
			retVal = retVal + 1;
		}
		// travelling
		if (sec.isSupplierSystemVerified()
				&& getManagedBean().isShowTravelSoftwarePanel()) {
			retVal = retVal + 1;
		}

		/* STEP 8 */

		// site visit report
		if (sec.isSitevisitVerified()) {
			retVal = retVal + 1;
		}

		return retVal;
	}

	private int getMaxSecCountByShowPanel() {
		getManagedBean().setMaxReviewCount(
				assignMaxReviewCount(
						getManagedBean().getSmAppl().getApplType(),
						getManagedBean().getSmAppl().getApplCategoryType(),
						getManagedBean().getSmAppl().getSupplierType(),
						getManagedBean().getBusinessTypeVal()));
		int retVal = getManagedBean().getMaxReviewCount();
		return retVal;
	}

	/**
	 * Save sm remarks.
	 * 
	 * @param remarks
	 *            - SmRemark object
	 */
	private void saveSmRemarks(SmRemark remarks) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setSmRemark(remarks);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		reqSO.saveSmRemarks(reqDTO);

	}

	/**
	 * Save sm cancellation attachment.
	 * 
	 * @param attach
	 *            -SmAttachment
	 */
	private void saveSmCancellationAttachment(SmAttachment attach) {
		SmAttachmentReqDTO reqDTO = new SmAttachmentReqDTO();
		reqDTO.setAttachment(attach);
		ISmAttachmentSO reqSO = (ISmAttachmentSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISmAttachmentSO.class);
		reqSO.updateSmAttachment(reqDTO);

	}

	/**
	 * Retrieve supporting document list.
	 * 
	 * @param reqDTO
	 *            -DocumentReqDTO
	 * @return the document res dto object
	 */
	private DocumentResDTO retrieveSupportingDocumentList(DocumentReqDTO reqDTO) {
		DocumentResDTO resDto = new DocumentResDTO();
		IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDocumentSO.class);
		resDto = reqSO.retrieveSupportingDocuments(reqDTO);
		return resDto;

	}

	/**
	 * Set financial organization list and hq flag when on change account
	 * purpose
	 */
	public void onChangeBankAccPurpose() {
		retrieveBankFinOrgListByType();
		if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("R")) {
			getManagedBean().getSelectedBank().setForHqFlg(
					Integer.parseInt(ParameterConst.FLAG_YES));
		}
	}

	/**
	 * Retrieve bank fin org list by type.
	 */
	public void retrieveBankFinOrgListByType() {
		boolean runQuery = true;
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();

		log.info("******* Selected Value **********"
				+ getManagedBean().getSelectedBank().getAccountPurpose());
		if (EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmSupplierBranches())) {
			getManagedBean().getSelectedBank().setForHqFlg(1);
		}
		if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("P")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("R")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_REGISTRATION);
			getManagedBean().getSelectedBank().setForHqFlg(1);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("B")) {
			reqDTO.setServiceTypeList(Arrays.asList(
					ParameterConst.BANK_SERVICES_PAYMENT_BANK,
					ParameterConst.BANK_SERVICES_REGISTRATION));
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
			getManagedBean().getSelectedBank().setForHqFlg(1);
		} else {
			this.getManagedBean().setFinOrgLs(new ArrayList<PmFinancialOrg>());
			runQuery = false;
		}
		if (runQuery) {
			IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
					.getInstance()
					.getServiceObject("pm", IFinancialOrgSO.class);
			log.info("******* Service Type ********" + reqDTO.getServiceType());
			FinancialOrgResDTO resDTO = reqSO
					.retrieveFinancialOrgListByServiceType(reqDTO);

			this.getManagedBean().setFinOrgLs(resDTO.getFinancialOrgList());
		}
		log.info("****** After getting the fin org list *******"
				+ getManagedBean().getFinOrgLs().size());
	}

	/**
	 * Assign selected support doc.
	 * 
	 * @param doc
	 *            the doc
	 */
	public void assignSelectedSupportDoc(SmApplSupportDoc doc) {
		log.info("****** Assign Supporting Doc *******");
		getManagedBean().setSupportDoc(doc);
	}
	
	public void assignSelectedSupportDocIndex(SmApplSupportDoc doc, int index) {
		this.getManagedBean().setAttachedFileIndex(index);
		getManagedBean().setSupportDoc(doc);
	}

	/**
	 * Handle and save file attachment for resubmit supproting doc file(s).
	 * 
	 * @param event
	 *            - the FileUploadEvent
	 */
	public void handleResubmitSupDocFileAttachment(FileUploadEvent event) {
		log.info("********handleFileAttachment********");
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				if (doc.getApplSupportDocId().compareTo(
						getManagedBean().getSupportDoc().getApplSupportDocId()) == 0) {
					log.info("**** This is the selected object ***** ");
					//#34552 - start
					SmApplDocFile file = new SmApplDocFile();
					file.setFileName(StringUtils.replace(event.getFile()
							.getFileName(), " ", "-"));
					String filePath = Calendar.getInstance().get(Calendar.YEAR)
							+ "/SM/" + getManagedBean().getSmAppl().getApplNo()
							+ "/SD/";
					file.setFilePath(filePath);
					file.setSmApplSupportDoc(doc);
					//#34392 - start
					if(doc.getNewAddedFiles() == null){
						doc.setNewAddedFiles(new ArrayList<SmApplDocFile>());
					}
//					doc.getSmApplDocFiles().add(file);
					doc.getNewAddedFiles().add(file);
					//#34392 - end
					//#34552 - end
					commonUploadFile(event, filePath);
				}
			}
		}
	}

	/**
	 * Handle and save file attachment for supporting documents
	 * 
	 * @param event
	 *            - the FileUploadEvent
	 */
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		log.info("********handleFileAttachment********");
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			if (getManagedBean().getSmAppl().getSmApplSupportDocs()
					.contains(getManagedBean().getSupportDoc())) {
				log.info("******* Contains selected file ****");
				String filePath = Calendar.getInstance().get(Calendar.YEAR)
						+ "/SM/APPLICATION/"
						+ getManagedBean().getSmAppl().getApplNo() + "/SD/";
				SmApplSupportDoc doc = getManagedBean().getSupportDoc();

				if (EntityHelper.isCollectionEmpty(doc.getSmApplDocFiles())) {
					SmApplicationUtil.setDefaultSmApplDocFiles(doc);
				}
				
				//#28807 - START
				SmApplDocFile aaa = new SmApplDocFile();
				aaa.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", "-"));
				aaa.setSmApplSupportDoc(doc);
				aaa.setFilePath(filePath);
				doc.getSmApplDocFiles().add(aaa);
						
				for(int i=0; i<doc.getSmApplDocFiles().size(); i++){
					if(doc.getSmApplDocFiles().get(i).getFileName()==null || 
							doc.getSmApplDocFiles().get(i).getFileName().equalsIgnoreCase(" ")){
						doc.getSmApplDocFiles().remove(i);
					}
					//log.info(i+". NAME -> " + doc.getSmApplDocFiles().get(i).getFileName());
				}
				//#28807 - END
				doc.getSmApplDocFiles().get(0).setFilePath(filePath);

				getManagedBean()
						.getSmAppl()
						.getSmApplSupportDocs()
						.set(getManagedBean().getSmAppl()
								.getSmApplSupportDocs()
								.indexOf(getManagedBean().getSupportDoc()),
								getManagedBean().getSupportDoc());

				commonUploadFile(event, filePath);
			}
		}

	}

	// harris
	public void deleteSupDocFileAttachment() {

		SmApplSupportDoc mainDoc = getManagedBean().getSupportDoc();
		// #37775 - Start
		if (this.getManagedBean().getSmApplDocFile() != null) {
			if (mainDoc.getSmApplDocFiles().size() == 1) {
				// If there is only one file, clear its name
				this.getManagedBean().getSmApplDocFile().setFileName(" ");
				this.getManagedBean().getSmApplDocFile().setFilePath(" ");
				// Reassign the modified object back to the list (ensuring persistence)
				List<SmApplDocFile> updatedFiles = new ArrayList<>(mainDoc.getSmApplDocFiles());
				updatedFiles.set(0, this.getManagedBean().getSmApplDocFile());
				mainDoc.setSmApplDocFiles(updatedFiles);
			} else {
				// #28807 - Start
				mainDoc.getSmApplDocFiles().remove(this.getManagedBean().getSmApplDocFile());
			}
		}
		// #37775 - End
		this.getManagedBean().setSmApplDocFile(null);
	}
	//#28807 - End

	/**
	 * Handle and save file attachment for cancel mof registration.
	 * 
	 * @param event
	 *            - the FileUploadEvent
	 */
	public void handleCancelMofRegFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			StringUtils.replace(event.getFile().getFileName(), " ", "-");
			getManagedBean().getCancellationAttachment().setFileName(
					StringUtils
							.replace(event.getFile().getFileName(), " ", "-"));
			log.info("***** File attached name to DB *******"
					+ getManagedBean().getCancellationAttachment()
							.getFileName());

			String filePath = Calendar.getInstance().get(Calendar.YEAR)
					+ "/SM/" + getManagedBean().getSmAppl().getApplNo()
					+ "/SD/";
			getManagedBean().getCancellationAttachment().setFilePath(filePath);
			commonUploadFile(event, filePath);

		}
	}
	
	//harris
	public void deleteCancelMofRegFileAttachment() {
		this.getManagedBean().getCancellationAttachment().setFileName(null);
	}
	
	

	/**
	 * Clone sm application draft from the stable or prev approved application.
	 * 
	 * @param id
	 *            - supplier Id
	 * @param applicationType
	 *            - the application type
	 * @param supplierType
	 *            - the supplier type
	 * @return the mof registration res dto object
	 */
	private MofRegistrationResDTO cloneSmApplicationDraft(long id,
			String applicationType, String supplierType) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(id);
			reqDto.setAppType(applicationType);
			reqDto.setSupplierType(supplierType);
			reqDto.setResubmitExpired(false);

			SmAppl rejectedAppl = SmApplicationUtil
					.getLatestRejectedApplIdBySuppIdAndApplType(id,
							applicationType);
			if (rejectedAppl != null) {
				reqDto.setRejectedApplId(rejectedAppl.getApplId());

				if (SmApplicationUtil.checkRejectedApplExpired(rejectedAppl)) {
					reqDto.setResubmitExpired(true);
				}
			}
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.cloneSmApplicationDraft(reqDto);
			return resDTO;

		} catch (Exception e) {
			log.info("**** THIS IS THE EXCEPTION IN CLONE APPL ****");
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			mofRegistrationSO = null;
		}
	}

	/**
	 * Assign max count of sections to be review based on application.
	 * 
	 * @param appType
	 *            - the application type
	 * @param applCatType
	 *            - the applcation category type
	 * @param supplierType
	 *            - the supplier type
	 * @param businessType
	 *            - the business type
	 * @return the int - count of sections
	 */
	private int assignMaxReviewCount(String appType, String applCatType,
			String supplierType, String businessType) {
		return SmApplicationUtil.assignMaxReviewCount(appType, applCatType,
				supplierType, businessType, getManagedBean()
						.isReviewSiteVisit(), getManagedBean()
						.isShowTravelSoftwarePanel(), getManagedBean()
						.isShowMachineryEquipmentPanel(), getManagedBean()
						.isShowFactoryPanel(), getManagedBean()
						.isShowDockyardPanel(), getManagedBean()
						.isShowStorePanel(), getManagedBean()
						.isShowWorkShopPanel());
	}

	//#36483 - START
	public void onChangeAddress(SmAddress addr, String addrLine, String panelName){
		if(panelName.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL) && getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)){
			if(addrLine.equals("Addr1")){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress1(addr.getAddress1());
			}
			if(addrLine.equals("Addr2")){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress2(addr.getAddress2());
			}
			if(addrLine.equals("Addr3")){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setAddress3(addr.getAddress3());
			}
		}
	}
	
	public void onChangePostcode(SmAddress addr, String panelName){
		if(panelName.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL) && getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)){
			getManagedBean().getBasicCompanyFacility().getSmAddress().setPostcode(addr.getPostcode());
		}
	}
	//#36483 - END

	/**
	 * Ajax call when select country to populate states
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeCountry(SmAddress addr, String panelName) {
		//#34799
		addr.setPmState(null);
		//#34135 START
		if(addr.getPmDivision() != null || addr.getDivisionId() != null){
			addr.setPmDivision(null);
			addr.setDivisionId(null);
			this.getManagedBean().setBranchCompanyDivisions(new ArrayList<SelectItem>());
			//#34799 - start
			if(getManagedBean().getSelectedBranch().getSmAddress().getPmState() != null){
				getManagedBean().getSelectedBranch().getSmAddress().getPmState().setAdoptDivision(false);
			}
			//#34799 - end
		}//#34135 END
		SmApplicationUtil.getInstance().populateStateByCountry(addr, panelName,
				getManagedBean());
		setRoleStaffAddrReqd();
		//#36483 - START
		if(panelName.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL)){
			if(getManagedBean().getBasicCompanyFacility().getOwnershipType() != null 
					&& getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setCountryId(addr.getCountryId());
			}
			
			if(!addr.isSsmData()){
				addr.setPostcode("");
			}
		}
		addr.setMapLatitude("");
		addr.setMapLongitude("");
		//#36483 - END
	}

	/**
	 * Ajax call when select state to populate district
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeState(SmAddress addr, String panelName) {
		//13320-start
		Long stateId = addr.getStateId();
		if (stateId != null) {
			addr.setPmState(SmCommonUtil.getInstance()
					.getStateById(stateId));
		}
		//#34799 - start
		addr.setPmDivision(null);
		addr.setPmDistrict(null);
		if(addr.getPmState() != null && addr.getPmState().getStateCode()
				.equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
			//#34799 - end
			SmApplicationUtil.getInstance().populateDivisionsByState(addr,
					panelName, getManagedBean());
			setRoleStaffAddrReqd();
			
		}else{//13320-end
			//#34135 START
			if(addr.getPmDivision() != null || addr.getDivisionId() != null){
				addr.setPmDivision(null);
				addr.setDivisionId(null);
				this.getManagedBean().setBranchCompanyDivisions(new ArrayList<SelectItem>());
			}//#34135 END
			SmApplicationUtil.getInstance().populateDistrictsByState(addr,
					panelName, getManagedBean());
			setRoleStaffAddrReqd();
		}
		
		//#36483 - START
		if(panelName.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL)){
			if(getManagedBean().getBasicCompanyFacility().getOwnershipType() != null
					&& getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)
					&& addr.getStateId()!=null){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setStateId(addr.getStateId());
				if(!addr.getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
					getManagedBean().getBasicCompanyFacility().getSmAddress().setPmDivision(null);
					getManagedBean().getBasicCompanyFacility().getSmAddress().setDivisionId(null);
				}
			}
			
			if(!addr.isSsmData()){
				addr.setPostcode("");
			}
		}
		addr.setMapLatitude("");
		addr.setMapLongitude("");
		//#36483 - END
	}

	/**
	 * Ajax call when select district to populate cities
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeDistrict(SmAddress addr, String panelName) {
		//#34799
		addr.setPmCity(null);
		
		SmApplicationUtil.getInstance().populateCitiesByDistrict(addr,
				panelName, getManagedBean());
		setRoleStaffAddrReqd();
		populateLocalAuthority(getManagedBean().getSmSupplier(),
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress());
		//#36483 - START
		if(panelName.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL)){
			if(getManagedBean().getBasicCompanyFacility().getOwnershipType() != null
					&& getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)
					&& addr.getDistrictId()!=null){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setDistrictId(addr.getDistrictId());
			}
			
			if(!addr.isSsmData()){
				addr.setPostcode("");
			}
		}
		addr.setMapLatitude("");
		addr.setMapLongitude("");
		//#36483 - END
	}

	/**
	 * Ajax call when select city to reload and set city by city id transient
	 * value
	 * 
	 * @param SmAddress
	 *            addr
	 */
	public void onChangeCity(SmAddress addr, String panelName) {
		addr.setPmCity(SmCommonUtil.getInstance().getCityById(addr.getCityId()));
		setRoleStaffAddrReqd();
		//#36483 - START
		if(panelName.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL)){
			if(getManagedBean().getBasicCompanyFacility().getOwnershipType() != null
					&& getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)
					&& addr.getCityId()!=null){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setCityId(addr.getCityId());
			}
			
			if(!addr.isSsmData()){
				addr.setPostcode("");
			}
		}
		addr.setMapLatitude("");
		addr.setMapLongitude("");
		//#36483 - END
	}

	/**
	 * Populate experience page child category list by parent category id.
	 * 
	 * @param parentId
	 *            - Long parent id
	 * @param catL2
	 *            - PmCategoryL2 object
	 * @param catL3
	 *            - PmCategoryL3 object
	 * 
	 */
	public void populateExperienceChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL2> categoryL2s = SmApplicationUtil
					.retrieveExperienceChildCategoryList(parentId,
							getManagedBean());
			catL2.setPmCategoryL2s(categoryL2s);
			catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate experience page sub child category by child id.
	 * 
	 * @param childId
	 *            - Long the child category id
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	public void populateExperienceSubChildCategoryByChild(long childId,
			PmCategoryL3 catL3) {
		try {
			List<PmCategoryL3> categoryL3s = SmApplicationUtil
					.retrieveExperienceSubChildCategoryList(childId,
							getManagedBean());
			catL3.setPmCategoryL3s(categoryL3s);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Populate and update child category list by parent category Id.
	 * 
	 * @param parentId
	 *            - Long parent id
	 * @param catL2
	 *            - PmCategoryL2 object
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	public void populateChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL2> categoryL2s = SmApplicationUtil
					.retrieveChildCategoryList(parentId, getManagedBean());

			this.getManagedBean().setSupplierChildCatList(categoryL2s);
			catL2.setPmCategoryL2s(categoryL2s);

			this.getManagedBean().setSupplierSubChildCatList(
					new ArrayList<PmCategoryL3>());
			catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());

			resetChildCategory(catL2);
			resetSubChildCategory(catL3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate and update sub child category list by child category Id.
	 * 
	 * @param childId
	 *            - Long the child category id
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	public void populateSubChildCategoryByChild(long childId, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL3> categoryL3s = SmApplicationUtil
					.retrieveSubChildCategoryList(childId, getManagedBean());

			this.getManagedBean().setSupplierSubChildCatList(categoryL3s);
			catL3.setPmCategoryL3s(categoryL3s);

			resetSubChildCategory(catL3);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Generate Offline Supporting Document Stream Content
	 * 
	 * @return StreamedContent
	 */
	public StreamedContent getOfflineSuppDocCheckList() {
		StreamedContent retVal = null;
		try {
			retVal = SmCommonUtil
					.getPdfFileStreamContentByActualPath(SmCommonUtil
							.getUploadFilePath(SupplierManagementConst.OFFLINE_DOC_CHECKLIST_PATH)
							+ getManagedBean().getSmAppl().getApplNo()
							+ SupplierManagementConst.OFFLINE_SUPP_DOC_CHECKLIST_PREFIX);
		} catch (IOException e) {
			throw new EpException(e.getMessage());
		}
		return retVal;
	}

	/**
	 * Reset selected child category id to null.
	 * 
	 * @param catL2
	 *            -PmCategoryL2 object
	 */
	private void resetChildCategory(PmCategoryL2 catL2) {
		catL2.setCategoryL2Id(null);

	}

	/**
	 * Reset selected sub child category id to null.
	 * 
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	private void resetSubChildCategory(PmCategoryL3 catL3) {
		catL3.setCategoryL3Id(null);

	}

	/**
	 * Reset child categroy list in SmSupplier Category.
	 */
	private void resetChildCatList() {
		this.getManagedBean().setSupplierChildCatList(
				new ArrayList<PmCategoryL2>());
		this.getManagedBean().setSupplierSubChildCatList(
				new ArrayList<PmCategoryL3>());
	}

	/**
	 * Sets or filter the failities based on the facility type from all
	 * smFacilities.
	 * 
	 * @param smFacilities
	 *            - List of SmFacility in the application
	 */
	private void setApplicationFailities(List<SmFacility> smFacilities) {
		log.info("*** Existing Facility******");
		getManagedBean().setApplFactoryFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplDockyardFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplWorkShopFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplStoreFacilities(new ArrayList<SmFacility>());
		for (SmFacility smFac : smFacilities) {
			setFacilityDefaultPrices(smFac);
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
					|| smFac.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
				// #34303 - start
				if (smFac.isHeadquarters()) {
					if (smFac.getSmAddress() == null
							|| smFac.getSmAddress().getAddressId() == null) {
						smFac.setSmAddress(SmApplicationUtil
								.setDefaultAddr(null));
						SmApplicationUtil.getInstance().cloneAddress(
								smFac.getSmAddress(), getHqAddress());
					}
					if (!smFac.getOwnershipType().equalsIgnoreCase("O")) {
						getManagedBean().setBasicOffFacStates(
								SmCommonUtil.getStateList(smFac.getSmAddress()
										.getPmCountry().getCountryId()));
					}
					log.info("*********Setting Basic company value **********");
					smFac.setFacilityName(getManagedBean().getSmAppl()
							.getSmCompanyBasic().getCompanyName());
					this.getManagedBean().setBasicCompanyFacility(smFac);
					if (smFac.getSmAddress() != null
							&& smFac.getSmAddress().getPmCountry() != null
							&& smFac.getSmAddress().getPmState() != null
							&& smFac.getSmAddress().getPmCity() != null) {
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										getManagedBean(),
										smFac.getSmAddress(),
										SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL);
					}
				}
				// #34303 - end
			}
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_FACTORY)) {
				getManagedBean().getApplFactoryFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD)) {
				getManagedBean().getApplDockyardFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP)) {
				getManagedBean().getApplWorkShopFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_STORE)) {
				getManagedBean().getApplStoreFacilities().add(smFac);
			}
		}

	}

	/**
	 * Sets or filter the list the application share holders from SmPersonnels.
	 * 
	 * @param perList
	 *            - List of all SmPersonnels in the application
	 */
	private void setApplicationShareHolders(List<SmPersonnel> perList) {
		getManagedBean().setSmapplShareHolders(new ArrayList<SmPersonnel>());

		for (SmPersonnel per : perList) {

			if (per.isEquityOwner()) {
				getManagedBean().getSmapplShareHolders().add(per);
			}
		}

	}

	/**
	 * Sets the default review section for approver in the SmAppSectionReview.
	 */
	private void setDefaultApproverReviewSection() {
		//#Enhancement #11765
		if(!alreadyExecute && SupplierManagementConst.SM_APPL_PAGE_TYPE_APPRDEC.equalsIgnoreCase(getManagedBean().getCurrentPageType())){ //#36576
			List<String> catList = SmApplicationUtil.getUnverifiedCatCodes(getManagedBean().getSmAppl()
					.getSmSupplierCategories(), getManagedBean().getSmAppl().getApplType(), getManagedBean()
					.isApproverUser());
			if (catList.size() != 0) {
				FacesContext fc = FacesContext.getCurrentInstance();
				//#Enhancement #11765
				alreadyExecute = true;
				fc.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, SmCommonUtil.getMessage(
								"lbl.sm.appl.catCodes.noPoAppr.action",
								new String[] { catList.toString().replaceAll("[\\[\\]]", "") })));
			}
			//#36576 START
			if (getManagedBean().isOverLappedCategoryExists()
					&& EntityHelper.isCollectionEmpty(getManagedBean()
							.getSmAppl().getSmCategoryOverlapDocs())) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								SmCommonUtil
										.getMessage(
												"lbl.sm.category.overlap.pleaseReject",
												null)));
			}//#36576 END
		}
		
		boolean containsAppRev = false;

		if (!getManagedBean().getSmAppl().getSmApplSectionReviews().isEmpty()) {
			containsAppRev = applSectionRevExist();
			if (getManagedBean().isReviewSiteVisit()
					&& !getManagedBean().getApprvrSecReview()
							.isSitevisitVerified()) {
				getManagedBean().getApprvrSecReview().setVerifiedAll(false);
			}
		}
		if (!containsAppRev) {
			// SmApplSectionReview appRev = new SmApplSectionReview();
			// appRev.setSmAppl(getManagedBean().getSmAppl());
			// appRev.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_APP);
			// getManagedBean().setApprvrSecReview(appRev);
			// getManagedBean().getSmAppl().getSmApplSectionReviews()
			// .add(getManagedBean().getApprvrSecReview());

			SmApplicationUtil.clonePoApplSectionReview(getManagedBean());
		}

	}

	/**
	 * Sets the default review section for PO in the SmAppSectionReview.
	 */
	private void setDefaultPoReviewSection() {
		//#Enhancement #11765
		if(!alreadyExecute && SupplierManagementConst.SM_APPL_PAGE_TYPE_PODEC.equalsIgnoreCase(getManagedBean().getCurrentPageType())){ //#36576
			////START Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen
			//Add checking category code 
			if (!getManagedBean().isSiteVisitAfterApproval()) {
				boolean validNewCatAdded = SmApplicationUtil
						.valiateNewCategoryAdded(getManagedBean());
				if (!validNewCatAdded) {
					//Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen
					getManagedBean().getPoSecReview().setVerifiedAll(false);
					getManagedBean().setShowParentMsg(true);
					throw new EpException(MessageConst.ERROR_722570);
				}
			}
		////END Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen
			List<String> catList = SmApplicationUtil.getUnverifiedCatCodes(getManagedBean().getSmAppl()
					.getSmSupplierCategories(), getManagedBean().getSmAppl().getApplType(), getManagedBean()
					.isApproverUser());
			if (catList.size() != 0) {
				alreadyExecute = true;
				FacesContext fc = FacesContext.getCurrentInstance();				
				fc.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, SmCommonUtil.getMessage(
								"lbl.sm.appl.catCodes.noPoAppr.action",
								new String[] { catList.toString().replaceAll("[\\[\\]]", "") })));
			}
			//#36576 START
			if (getManagedBean().isOverLappedCategoryExists()
					&& EntityHelper.isCollectionEmpty(getManagedBean()
							.getSmAppl().getSmCategoryOverlapDocs())) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								SmCommonUtil
										.getMessage(
												"lbl.sm.category.overlap.pleaseReject",
												null)));
			}//#36576 END
		}
		
		boolean containsPORev = false;

		if (!getManagedBean().getSmAppl().getSmApplSectionReviews().isEmpty()) {
			containsPORev = applSectionRevExist();
			if (getManagedBean().isReviewSiteVisit() && !getManagedBean().getPoSecReview().isSitevisitVerified()) {
				getManagedBean().getPoSecReview().setVerifiedAll(false);
			}
		}
		if (!containsPORev) {
			SmApplSectionReview poRev = new SmApplSectionReview();
			poRev.setSmAppl(getManagedBean().getSmAppl());
			poRev.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_PO);
			getManagedBean().setPoSecReview(poRev);
			getManagedBean().getSmAppl().getSmApplSectionReviews().add(getManagedBean().getPoSecReview());
		}

	}

	public boolean applSectionRevExist() {
		boolean containsSecRev = false;
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmApplSectionReviews())) {
			if (getManagedBean().isApproverUser()) {

				if (getManagedBean().getSmAppl().getPoUserId() != null) {
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getManagedBean().getSmAppl()
							.getPoUserId());
					UserResDTO userResDTO = SmServiceLookup.getUserSO()
							.retrieveUserByUserId(userReqDTO);
					getManagedBean().setPoUserName(
							userResDTO.getPmUser().getUserName());
				}

				for (SmApplSectionReview rev : getManagedBean().getSmAppl()
						.getSmApplSectionReviews()) {
					if (rev.getRoleType().equalsIgnoreCase(
							SupplierManagementConst.SECTION_REV_ROLE_APP)) {
						getManagedBean().setApprvrSecReview(rev);
						containsSecRev = true;
					}
					if (rev.getRoleType().equalsIgnoreCase(
							SupplierManagementConst.SECTION_REV_ROLE_PO)) {
						getManagedBean().setPoSecReview(rev);
					}
					if (rev.getPmRegStatus() != null
							&& rev.getPmRegStatus().getPmStatus() != null) {
						StatusReqDTO statusReqDTO = new StatusReqDTO();
						statusReqDTO.setStatusId(rev.getPmRegStatus()
								.getPmStatus().getStatusId());
						statusReqDTO.setLanguageCode(new FacesScopes()
								.getUserLocale().getLanguage());
						PmStatusDesc statusDesc = SmServiceLookup.getStatusSO()
								.retrieveStatusDescById(statusReqDTO)
								.getPmStatusDesc();
						if (statusDesc != null) {
							getManagedBean().setPoRecommendationDesc(
									statusDesc.getStatusName());
						}
					}
					//31210-start
					if (rev.getRecommendation() !=null) {
						getManagedBean().setPoRecommendationDesc(rev.getRecommendation());
					}//31210-end
				}
			} else if (getManagedBean().isPoUser()
					|| (getManagedBean().isSupplierUser() && getManagedBean()
							.getSmAppl().isResubmit())) {
				for (SmApplSectionReview rev : getManagedBean().getSmAppl()
						.getSmApplSectionReviews()) {
					if (rev.getRoleType().equalsIgnoreCase(
							SupplierManagementConst.SECTION_REV_ROLE_PO)) {
						getManagedBean().setPoSecReview(rev);
						containsSecRev = true;
					}
				}
			}
		}
		return containsSecRev;
	}

	/**
	 * Calculate share holder capital percentage for the supplier.
	 */
	public void calculateShareHolderPercent() {
		log.info("********* calculateShareHolderPercent ********  ");
		SmApplicationUtil.calculateShareHolderPercent(getManagedBean());
	}

	/**
	 * Calculate equity or share holding company's percentage for the supplier.
	 */
	public void calculateEquityCompanyPercent() {
		try {
			if (getManagedBean().getSelectedEquityCompany().getShareAmt() != null) {
				getManagedBean().getSelectedEquityCompany().setShareAmt(
						getManagedBean().getSelectedEquityCompany()
								.getShareAmt()
								.setScale(2, RoundingMode.HALF_UP));
			}
			getManagedBean().setEquiCompCapTotal(new BigDecimal(0));
			for (SmEquityCompany comp : getManagedBean().getSmAppl()
					.getSmEquityCompanies()) {
				if (comp.getShareAmt() == null) {
					comp.setShareAmt(new BigDecimal(0));
				}
				if (!(comp.getEquityCompanyId() == null && comp
						.equals(getManagedBean().getSelectedEquityCompany()))
						&& !(comp.getEquityCompanyId() != null && comp
								.getEquityCompanyId().equals(
										getManagedBean()
												.getSelectedEquityCompany()
												.getEquityCompanyId()))) {

					getManagedBean().setEquiCompCapTotal(
							getManagedBean().getEquiCompCapTotal()
									.add(comp.getShareAmt())
									.setScale(2, RoundingMode.HALF_UP));
				}
			}
			getManagedBean().setEquiCompCapTotal(
					getManagedBean()
							.getEquiCompCapTotal()
							.add(getManagedBean().getSelectedEquityCompany()
									.getShareAmt())
							.setScale(2, RoundingMode.HALF_UP));
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getCompaniesEquityTotal() != null
					&& getManagedBean().getEquiCompCapTotal().compareTo(
							getManagedBean().getSmAppl().getSmCompanyBasic()
									.getCompaniesEquityTotal()) <= 0) {
				BigDecimal mul = new BigDecimal(100);
				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					getManagedBean().getSelectedEquityCompany()
							.setSharePercentage(
									getManagedBean()
											.getSelectedEquityCompany()
											.getShareAmt()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 8,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(3, RoundingMode.HALF_UP));
				}

			} else {
				getManagedBean().getSelectedEquityCompany().setSharePercentage(
						new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
				getManagedBean().getSelectedEquityCompany().setShareAmt(
						new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
				throw new EpException(MessageConst.ERROR_722521);
			}
		} catch (NumberFormatException ne) {
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

	/**
	 * Calculate capital amounts and percentage for the supplier.
	 */
	public void calculateCapital() {
		try {
			// start #30900
			if (businessTypeIsROC(this.getManagedBean().getSmSupplier().getBusinessType())
					&& !this.getManagedBean().getSmAppl().getApplType()
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
				calcPersonnelShareAmt(getManagedBean().getSmAppl().getSmPersonnels(), getManagedBean().getSmAppl()
						.getSmCompanyBasic());
			}
			//end #30900
			SmCommonUtil.calculateSmApplicationPaidUpCapitalSum(getManagedBean().getSmAppl().getSmCompanyBasic(),
					getManagedBean().getSmAppl().getSmEquityCompanies(), getManagedBean().getBusinessTypeVal());
			// #28466 start - fix reopen
			SmCommonUtil
					.calculateSmApplicationPaidUpCapitalPercent(getManagedBean()
							.getSmAppl().getSmCompanyBasic());
			SmCommonUtil.calcAllEquityOwnerPercent(getManagedBean());
			//25929 - start
			SmCommonUtil.calculateEquityCompanyPercent(getManagedBean());
			//25929 - end
			getManagedBean().setBumiEquityPercentage(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getBumiPercent());
			getManagedBean().setCurrBumiEquity(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getBumiEquity());
			getManagedBean().setCurrNonBumiEquity(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getNonBumiEquity());
			getManagedBean().setCurrForeignerEquity(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getForeignerEquity());
			getManagedBean().setCurrGovtUnitrustEquity(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getGovtUnitrustEquity());
			getManagedBean().setCurrGovtEquity(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getGovtEquity());
			getManagedBean().setCurrForeignCompanyEquity(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getForeignCompanyEquity());
			getManagedBean().setCurrLocalCompanyEquity(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getLocalCompanyEquity());
			if (!getManagedBean().isDisAllowOwnerEdit()){//#31461-start
				validatePaidCaptialAmt(false);
			}//#31461-end
			// #28466 end - fix reopen
		} catch (NumberFormatException ne) {
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

	public void onCallEquityChangeAlert(BigDecimal newVal,
			BigDecimal existingVal) {
		if (newVal != null && newVal.compareTo(BigDecimal.ZERO) != 0
				&& newVal.compareTo(existingVal) != 0) {
			RequestContext.getCurrentInstance().execute(
					"equityChangeAlertDialog.show();");
		} else {
			calculateCapital();
		}
	}

	public void handleGstSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSelectedGstSuppDoc() != null) {

				// construct file path
				String filePath = SmCommonUtil
						.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH)
						+ getManagedBean().getSmSupplier().getRegNo()
						+ "/"
						+ System.currentTimeMillis() + "/";

				// init suppDocFile (currently only default one file)
				SmGstSupplierDoc doc = getManagedBean().getSelectedGstSuppDoc();
				doc.setFileName(StringUtils.replace(event.getFile()
						.getFileName(), " ", "-"));
				doc.setFilePath(filePath);

				// set selected doc to main doc list
				setSuppDocFileAttachment(doc);

				// upload file
				commonUploadFile(event, filePath);
			}
		}

	}

	public void handleOverlapSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSelectedOverlapDoc() != null) {

				if (getManagedBean().getOverlapDocs() == null) {
					getManagedBean().setOverlapDocs(
							new ArrayList<SmCategoryOverlapDoc>());
				}

				// construct file path
				String filePath = SmCommonUtil
						.getUploadFilePath(SupplierManagementConst.OVERLAP_CATEGORY_PATH)
						+ getManagedBean().getSmAppl().getApplNo()
						+ "/"
						+ System.currentTimeMillis() + "/";

				// init suppDocFile (currently only default one file)
				SmCategoryOverlapDoc doc = getManagedBean()
						.getSelectedOverlapDoc();
				doc.setFileName(StringUtils.replace(event.getFile()
						.getFileName(), " ", "-"));
				doc.setFilePath(filePath);

				int i = 0;
				for (SmCategoryOverlapDoc docTmp : getManagedBean()
						.getOverlapDocs()) {
					if (docTmp.getPmDocument().getDocumentId()
							.compareTo(doc.getPmDocument().getDocumentId()) == 0) {
						getManagedBean().getOverlapDocs().set(i, doc);
						break;
					}
					i++;
				}

				// upload file
				commonUploadFile(event, filePath);
			}
		}

	}

	public void assignSelectedGstDoc(SmGstSupplierDoc doc) {
		getManagedBean().setSelectedGstSuppDoc(doc);
	}

	public void assignSelectedOverlapDoc(SmCategoryOverlapDoc doc) {
		getManagedBean().setSelectedOverlapDoc(doc);
	}

	private void setSuppDocFileAttachment(SmGstSupplierDoc doc) {
		List<SmGstSupplierDoc> gstSuppDocList = null;
		if (getManagedBean().getCurrentPageType().equals(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)) {
			gstSuppDocList = getManagedBean().getCompanyBasicGst()
					.getSmGstSupplierDocs();
		} else if (getManagedBean().getCurrentPageType().equals(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)) {
			gstSuppDocList = getManagedBean().getSelectedGst()
					.getSmGstSupplierDocs();
		}

		if (gstSuppDocList != null && !gstSuppDocList.isEmpty()) {
			Iterator<SmGstSupplierDoc> docVOIter = gstSuppDocList.iterator();
			while (docVOIter.hasNext()) {
				SmGstSupplierDoc docVO = docVOIter.next();
				if (docVO.getPmDocument().getDocumentId()
						.compareTo(doc.getPmDocument().getDocumentId()) == 0) {
					docVO = doc;
				}
			}
		}
	}

	/**
	 * Calc personnel list count for supplier.
	 */
	private void calcPersonnelListCount() {
		log.info("*** Start calcPersonnelListCount**** ");
		SmPersonnleCalcVO calcVO = SmCommonUtil.calcPersonnelListCount(
				getManagedBean().getSmAppl().getSmPersonnels(),
				getManagedBean().getSmAppl().getSmStaffTotals(),
				getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().setPersonCalc(calcVO);
		calcAuthorizedPersonnel();
		log.info("*** END calcPersonnelListCount**** ");
	}

	private void calcAuthorizedPersonnel() {
		// set already reach valid max authorized personnel and justification
		SmAuthorizedPersonnelCountVO countVO = SmApplicationUtil
				.getSmAuthorizedPersonnelCountVO(getManagedBean().getSmAppl());
		getManagedBean().setValidSmUserLimit(
				countVO.isReachAuthorizedPersonnelLimit());
		getManagedBean().setAuthVO(countVO);
	}

	/**
	 * Calculate bumi percentage totals for supplier.
	 */
	private void calculateBumiPercentageTotals() {
		SmPersonnleCalcVO calcVO = SmCommonUtil.calculateBumiPercentageTotals(
				getManagedBean().getPersonCalc(), getManagedBean().getSmAppl()
						.getSmPersonnels());
		getManagedBean().setPersonCalc(calcVO);
	}

	/**
	 * Sets the navigation for resubmit application.
	 * 
	 * @param applType
	 *            - type of application submitted
	 * 
	 */
	private void setNavigationForResubmitAppln(String applType) {
		SmApplicationUtil.setNavigationForResubmitAppln(applType);
	}

	/**
	 * Assign supplier business type for screen validation purpose.
	 * 
	 * @param sup
	 *            - SmSupplier object
	 */
	private void assignSupplierBusinessType(SmSupplier sup) {
		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(sup));

	}

	/**
	 * Sets the application current pm status.
	 * 
	 * @param statusIdpm
	 *            - pm status id passed as String
	 */
	private void setApplPmStatus(String statusId) {
		StatusResDTO resDTO = SmApplicationUtil.setApplPmStatus(statusId);
		if (resDTO != null && resDTO.getPmStatus() != null) {
			getManagedBean().getSmAppl().setPmStatus(resDTO.getPmStatus());
		}
	}

	/**
	 * Sets the pm status desc.
	 * 
	 * @param statusId
	 *            - pm status id passed as String
	 */
	private void setPmStatusDesc(String statusId) {
		log.info("****** Status Id ******" + statusId);
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(Long.parseLong(statusId));
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			getManagedBean().setCurrentStatus(
					resDTO.getPmStatusDesc().getStatusName());
		}
	}

	/**
	 * Assign value reqd to false when anyof the pop up is opened in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	private void assignValReqdToTrue() {
		SmApplicationUtil.assignValReqdToTrue(getManagedBean());
	}

	/**
	 * Assign value reqd to false when anyof the pop up is closed in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	private void assignValReqdToFalse() {
		getManagedBean().setShowParentMsg(true);
		getManagedBean().setValueReqd(false);
	}

	/**
	 * Validate supporting docs.
	 * 
	 * @return true, if successful
	 */
	private boolean validateSupportingDocs() {
		return SmApplicationUtil.validateSupportingDocs(getManagedBean()
				.getSmAppl().getSmApplSupportDocs());
	}
	
	//#34392 - Start
	/**
	 * Validate supporting docs.
	 * 
	 * @return true, if successful
	 */
	private boolean validateMandatoryEnquirySupportingDocs() {
		return SmApplicationUtil.validateSupportingDocsEnquiry(getManagedBean().getMissinDocsMandatory());
	}
	//#34392 - End

	/**
	 * Validate if no changes been made to application not allowed to submit
	 */
	private void validateNoChangesBeenMadeToApplication() {
		// check if supporting docs is empty means no changes been made
		if (EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmApplSupportDocs())) {
			throw new EpException(MessageConst.ERROR_722648);
		}
	}

	/**
	 * Retrieve business type list.
	 */
	public void retrieveBusinessTypeList() {
		SelectItem[] selectItems = getDropdownCodeValues(SupplierManagementConst.SUPPLIER_BUSINESS_TYPE);
		List<SelectItem> businessTypeSelectItems = new ArrayList<SelectItem>();
		if (selectItems != null && selectItems.length > 0) {
			String bizType = getManagedBean().getSmSupplier().getBusinessType();
			for (SelectItem item : selectItems) {
				if (item.getValue() != null) {
					if (bizType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
							|| bizType
									.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {

						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {
							businessTypeSelectItems.add(item);
						}
					} else if (bizType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
							|| bizType
									.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
							businessTypeSelectItems.add(item);
						}
					} else if (bizType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
							|| bizType
									.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {
						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {
							businessTypeSelectItems.add(item);
						}
						// #16052019 - SSM INTEGRATION CR
					} else if (bizType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
							|| bizType
									.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)) {
						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)) {
							businessTypeSelectItems.add(item);
						}
					}
				}
			}
		}

		// sort
		if (CollectionUtils.isNotEmpty(businessTypeSelectItems)) {
			Collections.sort(businessTypeSelectItems,
					new Comparator<SelectItem>() {
						@Override
						public int compare(final SelectItem s1,
								final SelectItem s2) {
							return StringUtil
									.upperCase(s1.getLabel())
									.compareTo(
											StringUtil.upperCase(s2.getLabel()));
						}
					});
		}
		this.getManagedBean()
				.setBusinessTypeLs(
						businessTypeSelectItems
								.toArray(new SelectItem[businessTypeSelectItems
										.size()]));
	}

	/**
	 * Validate paid captial amt.
	 * 
	 * @param setNavigationCurrentPage
	 *            -boolean value determining the navigation to current page
	 */
	private void validatePaidCaptialAmt(boolean setNavigationCurrentPage) {
		SmApplicationUtil.validatePaidCaptialAmt(setNavigationCurrentPage,
				getManagedBean(), currentTab);
	}

	/**
	 * Manage personnel roles for the selected SmPersonnel in the application.
	 */
	public void managePersonnelRoles(String updateAddressDetails) {
		SmApplicationUtil.managePersonnelRoles(getManagedBean(),
				updateAddressDetails);
	}

	/**
	 * Download selected supporting document.
	 * 
	 * @param fileName
	 *            -the file name
	 * @param filePath
	 *            - the file path
	 */
	public void downloadSelectedSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath + fileName;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalFilePath = null;
	}

	/**
	 * Download signature supporting document template.
	 * 
	 * @param fileName
	 *            - the file name
	 * @param filePath
	 *            - the file path
	 */
	public void downloadSignatureSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalFilePath = null;
	}

	/**
	 * download the generated MOF declaratiion form.
	 * 
	 * @throws Exception
	 */
	public void printDeclarationForm() throws Exception {
		String language = setFixedLanguageDisplay(true,"ms"); // 33410 set only malay language
		if (language.equals("en")) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("PAPPNO", getManagedBean().getSmAppl().getApplNo());
			paramMap.put(
					"PAPPTYP",
					SmCommonUtil.retrieveParameterDescByTypeAndCodeAndLang(
							ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
							getManagedBean().getSmAppl().getApplType(),
							language).getCodeName());
			paramMap.put("PSUPPNAME", getManagedBean().getSmAppl().getSmCompanyBasic()  //33410 
					.getCompanyName());
			generateReport(ReportConst.SM_DECLARATION_FORM_REPORT_EN, paramMap);
		} else {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("PAPPNO", getManagedBean().getSmAppl().getApplNo());
			paramMap.put(
					"PAPPTYP",
					SmCommonUtil.retrieveParameterDescByTypeAndCodeAndLang(
							ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
							getManagedBean().getSmAppl().getApplType(),
							language).getCodeName());
			paramMap.put("PSUPPNAME", getManagedBean().getSmAppl().getSmCompanyBasic()  //33410
					.getCompanyName());
			generateReport(ReportConst.SM_DECLARATION_FORM_REPORT, paramMap);
		}
	}

	// #34392 - START
	public void printBankVerificationForm() throws Exception {
		SmApplicationUtil.generateBankVerificationFormPdf(getManagedBean()
				.getSmAppl());
		String fileName = SmCommonUtil
				.getUploadFilePath(SupplierManagementConst.SUPPORTING_DOC_TEMP_PATH)
				+ File.separator
				+ getManagedBean().getSmAppl().getApplNo()
				+ SupplierManagementConst.BANK_VERIFICATION_FORM_PREFIX;
		commonDownload(fileName, getManagedBean().getSmAppl().getApplNo()
				+ SupplierManagementConst.BANK_VERIFICATION_FORM_PREFIX);
	}
	// #34392 - END

	public void downloadSignatureSuppDoc(String docDefinitionKey)
			throws Exception {
		if (org.apache.commons.lang.StringUtils.isNotBlank(docDefinitionKey)) {
			if (docDefinitionKey.equals("SBP")) {
				printDeclarationForm();
			} else if (docDefinitionKey.equals("SBV")) {
				printBankVerificationForm();
			}
		}
	}

	/**
	 * Print Supplier Profile
	 * 
	 * @throws IOException
	 */
	public void printSupplierProfile() throws IOException {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PAPPLNO", getManagedBean().getSmAppl().getApplNo());
		generateReport(ReportConst.SM_SUPPLIER_UNREGIS_REPORT, paramMap);
	}

	/**
	 * Sets the url for revision application.
	 */
	public void setRevUrl() {
		String url = SmCommonUtil.getEpBaseUrl() + "/view-revision-application";
		String revApprvrAppNo = StringUtils.EMPTY;
		String revSupplAppNo = StringUtils.EMPTY;
		if (getManagedBean().getSmAppl() != null) {
			if (getManagedBean().getSmSupplier() != null
					&& getManagedBean().getSmSupplier().getSmAppl() != null) {
				revApprvrAppNo = getManagedBean().getSmSupplier().getSmAppl()
						.getApplNo()
						+ "_APP";
				getManagedBean().setRevisionApplType(
						getManagedBean().getSmSupplier().getSmAppl()
								.getApplType());
			}

			revSupplAppNo = getManagedBean().getSmAppl().getApplNo() == null ? ""
					: getManagedBean().getSmAppl().getApplNo() + "_SUP";
		}

		getManagedBean().setRevisionApproverAppNoParam(revApprvrAppNo);
		getManagedBean().setRevisionSupplierAppNoParam(revSupplAppNo);
		getManagedBean().setRevisionBaseUrl(url);
	}

	/**
	 * Remove un selected admin from the selected personnel list.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void unSelectAdmin(AjaxBehaviorEvent e) {
		SmApplicationUtil
				.removePersonnelAdminOrSoftcertProcessFromList(getManagedBean()
						.getSelectedShareHolders());
	}

	//#28775 - 38498
	/**
	 * Disable delete button
	 * @param facilitiesVOList
	 * @return
	 */
	public boolean disableDeleteBtnFacility(List<PmCategoryL3> facilitiesVOList) {
		boolean retVal = true;
		if (EntityHelper.isCollectionEmpty(facilitiesVOList)){
			return true;
		}
		for (PmCategoryL3 l3 : facilitiesVOList){
			if (EntityHelper.isCollectionEmpty(l3.getFacilityList())){
				continue;
			}
			for (SmFacility fac : l3.getFacilityList()){
				if (fac.isSelected()){
					return false;
				}
			}
		}
		return retVal;
	}

	public <ENTITY extends BaseEntity> boolean disableDeleteBtn(
			List<ENTITY> entityList) {
		boolean retVal = true;
		if (!EntityHelper.isCollectionEmpty(entityList)) {
			for (ENTITY entity : entityList) {
				if (entity.isSelected()) {
					retVal = false;
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * On tick checkbox during search rejection reason
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void onTickOrUnTickReason(AjaxBehaviorEvent e) {
		getManagedBean().setRejectReasonReqd(false);
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSelectedReasonDescs())) {
			List<Long> reasonIdList = new ArrayList<Long>();
			for (PmReasonDesc reason : getManagedBean()
					.getSelectedReasonDescs()) {
				reasonIdList.add(reason.getPmReason().getReasonId());
			}
			getManagedBean().setReasonId(
					reasonIdList.toArray(new Long[reasonIdList.size()]));
		}else{
			getManagedBean().setReasonId(null);
		}
		verifySelectedRejectReasons();
	}

	/**
	 * Search rejected reason
	 */
	public void searchRejectedReason() {
		
		// reset selected recommend reject reason during search
		//		#15629 && 37896 -- start - 25/1/2020
		if(SmCommonUtil.isCollectionEmpty(getManagedBean().getSelectedReasonDescs())) {
			getManagedBean().setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
		} 
		//		#15629 && 37896 -- end - 25/1/2020
		
		// reset selected reject reason during search
		getManagedBean().setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
		// reset remark for other and remove remark required flag during search
		getManagedBean().setReasonId(null);
		// reset selected reject reason remarks during search
		verifySelectedRejectReasons();

		getManagedBean().setReasonDescList(
				SmApplicationUtil.getWildCardSearchRejectReason(
						getManagedBean().getSearchRejectReason(),
						getManagedBean().getReasonDescInitList()));
		
		//30974 && 37896 - start
		if (getManagedBean().getSelectedRecommendRejectReasonDescs() != null
				&& getManagedBean().getSelectedRecommendRejectReasonDescs().size() > 0) {
			for (PmReasonDesc reason : getManagedBean().getSelectedRecommendRejectReasonDescs()) {
				if (getManagedBean().getSearchRejectReason() != null 
						&& !StringUtils.upperCase(reason.getReasonDesc()).contains(StringUtils.upperCase(getManagedBean().getSearchRejectReason()))) {
					getManagedBean().getReasonDescList().add(reason);
				}
			}
		}
		
		if (getManagedBean().getSelectedRecommendRejectReasonDescs() != null
				&& getManagedBean().getSelectedRecommendRejectReasonDescs().size() > 0) {
			getManagedBean().setSelectedReasonDescs(getManagedBean().getSelectedRecommendRejectReasonDescs());
			getManagedBean().setSelectedRecommendRejectReasonDescs(getManagedBean().getSelectedRecommendRejectReasonDescs());	
			setReasonIdList();
		}else{
			getManagedBean().setSelectedReasonDescs(getManagedBean().getSelectedRejectReasonDescs());
			getManagedBean().setSelectedRecommendRejectReasonDescs(getManagedBean().getSelectedRejectReasonDescs());
			setReasonIdList();
		}
		verifySelectedRejectReasons();
		//30974 && 37896 - end
	}

	/**
	 * Reset search rejected reason
	 */
	public void resetSearchRejectedReason() {
		//#37896 - START
		//#30974 - start
		getManagedBean().setApplnRejectionRemarks(StringUtils.EMPTY);
		//#30974 - end
		getManagedBean().setSearchRejectReason(StringUtils.EMPTY);
		//	#30974 -- start - 15/6/2020
		getManagedBean().setSearchRecommendRejectReason(StringUtils.EMPTY);
		//	#30974 -- end - 15/6/2020

		getManagedBean().setReasonDescList(
				SmApplicationUtil.getWildCardSearchRejectReason(
						getManagedBean().getSearchRejectReason(),
						getManagedBean().getReasonDescInitList()));
		getManagedBean().setSelectedReasonDescs(new ArrayList<PmReasonDesc>());	
		getManagedBean().setSelectedRecommendRejectReasonDescs(new ArrayList<PmReasonDesc>());
		setReasonIdList();
		//#37896 - END
	}

	/**
	 * Auto ticked and disabled hq facility
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void autoTickedHq(AjaxBehaviorEvent e) {
		setHqFacilityInSelectedFacility();
	}

	/**
	 * On click check or uncheck check box for site visit associate company
	 * assignment
	 */
	public void onCheckedSiteVisitAssocCompanyAddress() {
		getManagedBean().setSelectedAssocCompanies(
				new ArrayList<SmAssociateCompany>());
		for (SmSiteVisitFacilityVO siteVisit : getManagedBean()
				.getBumiSiteVisitVOList()) {
			if (!SmCommonUtil.isCollectionEmpty(siteVisit.getAssocCompanies())) {
				for (SmAssociateCompany assoc : siteVisit.getAssocCompanies()) {
					if (assoc.isSelected()) {
						getManagedBean().getSelectedAssocCompanies().add(assoc);
					}
				}
			}
		}
	}

	/**
	 * On click check or uncheck check box for site visit branches assignment
	 */
	public void onCheckedSiteVisitBranchAddress() {
		getManagedBean().setSelectedBranches(new ArrayList<SmSupplierBranch>());
		for (SmSiteVisitFacilityVO siteVisit : getManagedBean()
				.getBumiSiteVisitVOList()) {
			if (!SmCommonUtil.isCollectionEmpty(siteVisit.getSuppBranches())) {
				for (SmSupplierBranch br : siteVisit.getSuppBranches()) {
					if (br.isSelected()) {
						getManagedBean().getSelectedBranches().add(br);
					}
				}
			}
		}
	}

	/**
	 * Removed un select apporved categories from the list.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void unSelectApporvedCategories(AjaxBehaviorEvent e) {
		if (getManagedBean().getDeleteNewApplCategoryList() != null
				&& getManagedBean().isSupplierUser()) {
			Iterator<SmSupplierCategory> iterator = getManagedBean()
					.getDeleteNewApplCategoryList().iterator();
			while (iterator.hasNext()) {
				SmSupplierCategory cat = iterator.next();
				if ((cat.getRecordStatusFlg() != null && cat
						.getRecordStatusFlg().compareTo(
								SupplierManagementConst.APPROVED_RECORD_STATUS) == 0)
						|| (cat.getApprovedDate() != null && cat
								.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Retrieve identity type from PM.
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPmIdTypeDropdownValues() {
		return SmCommonUtil.getInstance().getPmIdTypeDropdownValues();

	}

	/**
	 * Initiate the application category type i.e. normal or special.
	 */
	private void initApplCategoryType() {
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			initPhysicalConsultingCatcode();
			if (getManagedBean().getBasicCompanyFacility() != null
					&& getManagedBean()
							.getBasicCompanyFacility()
							.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)) {
				if (getManagedBean().getSmAppl() != null) {
					getManagedBean()
							.getSmAppl()
							.setApplCategoryType(
									SupplierManagementConst.SM_APPL_CATEGORY_TYPE_NORMAL);
					SmApplicationUtil
							.setApplicationCategorySelected(getManagedBean());
					assignApplnCategoryType();
				}
			}
		}
	}

	private void initPhysicalConsultingCatcode() {
		if (!StringUtils
				.isBlank(getManagedBean().getSmAppl().getSupplierType())
				&& getManagedBean().getSmAppl().getSupplierType().equals("J")) {
			if (SmCommonUtil.isApplPhysicalConsultant(getManagedBean()
					.getSmAppl())) {
				getManagedBean()
						.setPhysicalConsulting(
								my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE);
			} else {
				getManagedBean()
						.setPhysicalConsulting(
								my.ep.constant.sm.SupplierManagementConst.NON_PHYSICAL_SERVICES_CAT_CODE);
			}
		}
	}

	/**
	 * Display bumi recommend note for the application by supplier in managed
	 * bean.
	 */
	private void displayBumiRecommendNote() {
		log.info("*** Start displayBumiRecommendNote *******");
		getManagedBean().setBumiRecommendationNote("");
		if (getManagedBean().getSmAppl() != null) {
			String applType = getManagedBean().getSmAppl().getApplType() == null ? ""
					: getManagedBean().getSmAppl().getApplType();
			String suppType = "";

			if (getManagedBean().isSupplierUser()) {
				if (applType.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
					suppType = StringUtils.isNotBlank(getManagedBean()
							.getSmAppl().getSupplierType()) ? getManagedBean()
							.getSmAppl().getSupplierType() : getManagedBean()
							.getNewSupplierType();
				} else {
					suppType = getManagedBean().getSmAppl().getSupplierType() == null ? ""
							: getManagedBean().getSmAppl().getSupplierType();
				}
			} else {
				suppType = getManagedBean().getSmAppl().getSupplierType() == null ? ""
						: getManagedBean().getSmAppl().getSupplierType();
			}

			if (getManagedBean().getBumiEquityPercentage() == null) {
				calculateCapital();
			}
			getManagedBean().setBumiRecommendationNote(
					SmCommonUtil.displayBumiRecommendNote(getManagedBean()
							.getSmAppl().getSmSupplierBanks(), getManagedBean()
							.getPersonCalc(), getManagedBean()
							.getCurrentPageType(), applType, suppType,
							getManagedBean().getBumiEquityPercentage(),
							getManagedBean().isSupplierUser()));
		}
		log.info("*** End displayBumiRecommendNote ********");
	}

	/**
	 * Initialize latest supporting docs to mandatory and non mandatory document
	 * list
	 */
	private void initNonMandatoryAndMandatoryDocs() {
		//#28776 - start
		if(getManagedBean().getSmAppl().getPmStatus() != null
				&& getManagedBean().getSmAppl().getPmStatus().getStatusId() == SupplierManagementConst.PM_STATUS_PENDING_APPL_DECLARATION){
			getManagedBean().getSmAppl().setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
		}
		//#28776 - end
		if (getManagedBean().getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
						.getSmApplSupportDocs())) {
			List<SmApplSupportDoc> mandatoryLicenseDocs = new ArrayList<SmApplSupportDoc>();
			getManagedBean().setNonMandatoryDocs(
					new ArrayList<SmApplSupportDoc>());
			getManagedBean()
					.setMandatoryDocs(new ArrayList<SmApplSupportDoc>());
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				if (doc.getPmDocument() != null) {
					if (doc.getPmDocument().isMandatory()) {
						if (doc.getPmDocument().isLicenseReq()
								&& !doc.getPmDocument().getPmDocumentRestrs()
										.isEmpty()) {
							mandatoryLicenseDocs.add(doc);
						} else {
							getManagedBean().getMandatoryDocs().add(doc);							
						}
					} else {
						getManagedBean().getNonMandatoryDocs().add(doc);
					}
				}
			}
			initMandatoryLicenseInfo(mandatoryLicenseDocs);

			// sort by display seq as per redmine #30294
			if (!EntityHelper.isCollectionEmpty(getManagedBean()
					.getMandatoryDocs())) {
				Collections.sort(getManagedBean().getMandatoryDocs(),
						new Comparator<SmApplSupportDoc>() {
							@Override
							public int compare(final SmApplSupportDoc s1,
									final SmApplSupportDoc s2) {
								//#30294 - START
								if(s1.getPmDocument().getDisplaySeq() == null){
									return (s2.getPmDocument().getDisplaySeq() == null) ?  0 : 1;
								}
								if(s2.getPmDocument().getDisplaySeq() == null){
									return -1;
								}
								return Integer.valueOf(s1.getPmDocument().getDisplaySeq().compareTo(s2.getPmDocument().getDisplaySeq()));
								//#30294 - END
							}
						});
			}

			// sort by display seq as per redmine #30294
			if (!EntityHelper.isCollectionEmpty(getManagedBean()
					.getNonMandatoryDocs())) {
				Collections.sort(getManagedBean().getNonMandatoryDocs(),
						new Comparator<SmApplSupportDoc>() {
							@Override
							public int compare(final SmApplSupportDoc s1,
									final SmApplSupportDoc s2) {
								//#30294 - START
								if(s1.getPmDocument().getDisplaySeq() == null){
									return (s2.getPmDocument().getDisplaySeq() == null) ?  0 : 1;
								}
								if(s2.getPmDocument().getDisplaySeq() == null){
									return -1;
								}
								return Integer.valueOf(s1.getPmDocument().getDisplaySeq().compareTo(s2.getPmDocument().getDisplaySeq()));
								//#30294 - END
							}
						});
			}
			
		}
	}
	
	//3460 - Start
	/**
	 * To displaying the SDO remarks when viewing the applications
	 */
	private void initSDORemarkInfo(){
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
		reqDTO.setDocId(getManagedBean().getSmAppl().getApplId());
		
		//retrieving sdo remarks if it is resubmit application
		if(getManagedBean().getSmAppl().isResubmit() && getManagedBean().getSmAppl().getOriginalAppl() != null && getManagedBean().isSupplierUser()){
			retrieveSdoRemark(getManagedBean().getSmAppl().getApplType());
		}else{
			SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);
			
			if(resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
				SmRemark remark = resDTO.getRemarksList().get(0);
				getManagedBean().setSdoRemark(remark.getRemark());
			}	
		}
	}
	//3460 - End

	private void initMandatoryLicenseInfo(
			List<SmApplSupportDoc> mandatoryLicenseDocs) {
		//SmSupplierCategory cat = null;
		getManagedBean().setMandatoryLicenseDocs(
				new ArrayList<SmLicenseInfoVO>());
		SmLicenseInfoVO licenseVO = null;
		int count = getManagedBean().getMandatoryDocs().size();
		for (SmApplSupportDoc doc : mandatoryLicenseDocs) {
			count++;
			licenseVO = new SmLicenseInfoVO();
			//cat = getLicenseCategory(doc.getPmDocument());
			//licenseVO.setL3(cat != null ? cat.getPmCategoryL3() : null);
			licenseVO.setCategoryDesc(getLicenseConcatCategories(doc
					.getPmDocument()));
			licenseVO.setLicenseSuppDocs(new ArrayList<SmApplSupportDoc>());
			licenseVO.getLicenseSuppDocs().add(doc);
			licenseVO.setCount(count);
			getManagedBean().getMandatoryLicenseDocs().add(licenseVO);
		}
	}

	//28775 - Site Visit Grouping Enhancement -28775 - start
	/**
	 * @param doc
	 * @return supplier category for license
	 */
	public List<SmSupplierCategory> getLicenseCategory(PmDocument doc) {
		List<SmSupplierCategory> retVal = new ArrayList<>();
		if (doc == null || EntityHelper.isCollectionEmpty(doc.getPmDocumentRestrs())) {
			return retVal;
		}
		List<SmSupplierCategory> activeList = getLicenseCategoryPending();
		//28775 - Site Visit Grouping Enhancement -28775 - end
		
		for (PmDocumentRestr tmp : doc.getPmDocumentRestrs()) {
			if (tmp.getPmCategoryL3() != null) {
				SmSupplierCategory cat = (SmSupplierCategory) EntityHelper.getEntityBySinglePropertyFromList(
						tmp.getPmCategoryL3().getCategoryL3Id(), activeList, "pmCategoryL3");
				if (cat != null) {
					retVal.add(cat);
				}
			}
		}
		return retVal;
	}
	
	//28775 - Site Visit Grouping Enhancement -28775
	private List<SmSupplierCategory> getLicenseCategoryPending(){
		List<SmSupplierCategory> list = new ArrayList<>();
		List<SmSupplierCategory> smSuppCats = getManagedBean().getSmAppl().getSmSupplierCategories();
		boolean isRenew = getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		
		if (!EntityHelper.isCollectionEmpty(smSuppCats)){
			for (SmSupplierCategory activeSuppCat : smSuppCats){
				int status = activeSuppCat.getRecordStatusFlg();
				if (status == SupplierManagementConst.PENDING_ACT_STATUS
						|| (isRenew && status == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)) {
					list.add(activeSuppCat);
				}
			}
		}
		return list;
	}

	public String getLicenseConcatCategories(PmDocument doc) {
		String retVal = SmCommonUtil.getMessage("lbl.txt.na", null);
		List<SmSupplierCategory> catList = getLicenseCategory(doc);
		StringBuilder sb = new StringBuilder();
		if (catList != null && catList.size()>0) {
			for(SmSupplierCategory cat: catList){
				sb.append(
						cat.getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
								+ cat.getPmCategoryL2().getCategoryL2Code()
								+ cat.getPmCategoryL3().getCategoryL3Code())
						.append(" (")
						.append(cat.getPmCategoryL3().getCategoryName())
						.append(")").append(", ");
			}
			if (sb != null && sb.length() > 0) {
				retVal = sb.toString().trim();
				if (retVal.endsWith(",")) {
					retVal = retVal.substring(0, retVal.length() - 1);
				}
			}
		}
		return retVal;
	}

	/**
	 * Initialized and copy PO doc verified flag to Approver Verified flag for
	 * first time
	 */
	private void copyPoVerifiedDocsToApprover() {
		// set copy po verified to approver verified
		if (getManagedBean().isApproverUser()) {
			if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
					.getSmApplSupportDocs())) {
				for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
						.getSmApplSupportDocs()) {
					if (doc.getApproverVerifiedFlg() == null
							&& doc.getVerifiedFlg() != null) {
						doc.setApproverVerifiedFlg(doc.getVerifiedFlg());
					}
				}
			}
		}
	}

	/**
	 * Update mandatory and non mandatory documents back to application main
	 * list
	 */
	private void updateMandatoryAndNonMandatorySuppDocToApplDoc() {
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getNonMandatoryDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getNonMandatoryDocs()) {
				EntityHelper.addOrUpdateEntityToList(doc, getManagedBean()
						.getSmAppl().getSmApplSupportDocs());
			}
		}
		if (!EntityHelper
				.isCollectionEmpty(getManagedBean().getMandatoryDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getMandatoryDocs()) {
				EntityHelper.addOrUpdateEntityToList(doc, getManagedBean()
						.getSmAppl().getSmApplSupportDocs());
			}
		}
	}

	/**
	 * checked if machine declare as new without nullified, need to be nullified
	 * before save to db
	 */
	private void nullifiedPmMachineWithNoId() {
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmMachineDtls())) {
			for (SmMachineDtl mcDtl : getManagedBean().getSmAppl()
					.getSmMachineDtls()) {
				if (mcDtl.getMachine() != null
						&& mcDtl.getMachine().getMachineId() == null) {
					mcDtl.setMachine(null);
				}
			}
		}
	}

	/**
	 * Initiate the business premise of the supplier in the managed bean.
	 */
	private void initBusinessPremise() {
		getManagedBean().setDisabledHomeOffice(false);
		String suppType = "";
		if (getManagedBean().getSmAppl() != null) {
			suppType = StringUtils.isNotBlank(getManagedBean()
					.getNewSupplierType()) ? getManagedBean()
					.getNewSupplierType() : getManagedBean().getSmAppl()
					.getSupplierType();
		}
		// hide home office for consultant
		if (suppType.equalsIgnoreCase("J")
				&& getManagedBean().getBasicCompanyFacility() != null) {
			getManagedBean().setDisabledHomeOffice(true);
			getManagedBean().getBasicCompanyFacility().setFacilityType(
					SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE);

		}
	}

	/**
	 * Update and set all categories, added in the company experience section to
	 * the managed bean mapping
	 */
	public void selectAllCompanyExperinceCategories() {

		if (getManagedBean().isSelectedAll()) {
			getManagedBean().setSelected(true);
			getManagedBean().setSelectedCompExpCats(
					getManagedBean().getSelectedCompanyExperience()
							.getSmCompanyExperienceCats());
		} else {
			getManagedBean().setSelected(false);
			getManagedBean().setSelectedCompExpCats(null);

		}

	}

	/**
	 * Update and set selected category, added in the company experience section
	 * to the managed bean mapping
	 * 
	 * @param index
	 *            - int value of the selected index
	 **/
	public void selectedCompanyExperinceCategory(int index) {

		List<SmCompanyExperienceCat> selectedCompExpCatsList = getManagedBean()
				.getSelectedCompExpCats();

		if (getManagedBean().isSelected()) {
			if (selectedCompExpCatsList == null) {
				selectedCompExpCatsList = new ArrayList<SmCompanyExperienceCat>();
			}
			selectedCompExpCatsList.add(getManagedBean()
					.getSelectedCompanyExperience()
					.getSmCompanyExperienceCats().get(index));

		} else {

			if (selectedCompExpCatsList.contains(getManagedBean()
					.getSelectedCompanyExperience()
					.getSmCompanyExperienceCats().get(index))) {
				selectedCompExpCatsList.remove(getManagedBean()
						.getSelectedCompanyExperience()
						.getSmCompanyExperienceCats().get(index));
			}
		}
		getManagedBean().setSelectedCompExpCats(selectedCompExpCatsList);
	}

	/**
	 * Trim personnel passport value of the selected shareholder in the managed
	 * bean.
	 */
	public void trimPersonnelPassportValue() {
		String newPassportVal = "";
		if (StringUtils.isNotBlank(this.getManagedBean()
				.getSelectedShareHolder().getIdentificationNo())) {
			newPassportVal = this.getManagedBean().getSelectedShareHolder()
					.getIdentificationNo().replaceAll("\\s", "");
			this.getManagedBean().getSelectedShareHolder()
					.setIdentificationNo(newPassportVal);
		}
	}

	/**
	 * Gets the my identity status description based on the responsecode.
	 * 
	 * @param identityResponseCode
	 *            - the identity response code
	 * @return String - MY Identity status description value
	 */
	public String getMyIdentityStatusDescription(String identityResponseCode) {
		return SmCommonUtil.getInstance().getMyIdentityStatusDescription(
				identityResponseCode);
	}

	/**
	 * Checks if is personnel view only.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object to check
	 * @return true, if is personnel view only
	 */
	public boolean isPersonnelViewOnly(SmPersonnel smPersonnel) {
		boolean retVal = SmApplicationUtil.isPersonnelViewOnly(smPersonnel);
		return retVal;
	}

	/**
	 * Check if pending category status.
	 * 
	 * @param recordStatusFlg
	 *            - the record status integer value
	 * @return true, if successful
	 */
	public boolean checkIfPendingCategoryStatus(Integer recordStatusFlg) {
		log.info("****** checkIfPendingCategoryStatus *********"
				+ recordStatusFlg);
		if (recordStatusFlg != null && recordStatusFlg.compareTo(8) == 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Get valid epRole list for personnel
	 * 
	 * @return List<SelectItem> epRoles
	 */
	public List<SelectItem> getPersonnelValidEpRoles() {
		List<SelectItem> ePRoleList = new ArrayList<SelectItem>();
		SelectItem roleToRemove = null;
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getePRoleList())) {
			ePRoleList = getManagedBean().getePRoleList();
			if (getManagedBean().getSmAppl().getSmCompanyBasic() != null
					&& !getManagedBean().getSmAppl().getSmCompanyBasic()
							.isWithFederal()) {
				for (SelectItem role : getManagedBean().getePRoleList()) {
					if (role.getValue().equals(RoleCode.CM_USER.value())) {
						roleToRemove = role;
						break;
					}
				}
			}
		}
		if (roleToRemove != null) {
			ePRoleList.remove(roleToRemove);
		}
		return ePRoleList;
	}

	/**
	 * Get epRole from select items list
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	public String getEpRoleLabelByRole(String roleCode) {
		String retVal = StringUtils.EMPTY;
		SelectItem item = (SelectItem) EntityHelper
				.getEntityBySinglePropertyFromList(roleCode, getManagedBean()
						.getePRoleList(), "value");
		if (item != null) {
			retVal = item.getLabel();
		}
		return retVal;
	}

	/**
	 * Get list of academic qualification
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getAcademicQualifications() {
		return SmApplicationUtil.getQualificationLevels(getManagedBean());
	}

	/**
	 * Set supplier change values
	 * 
	 * @param currPage
	 */
	private void setSupplierChangeValues(String currPage) {
		if (currPage
				.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)) {
			SmSupplier supplier = getManagedBean().getSmAppl().getSmSupplier();
			supplier.setEstablishDate(getManagedBean().getSmSupplier()
					.getEstablishDate());
			if (!getManagedBean().isDisabledBusinessType()) {
				supplier.setBusinessType(getManagedBean().getSmSupplier()
						.getBusinessType());
				supplier.setLocalAuthorityId(getManagedBean().getSmSupplier()
						.getLocalAuthorityId());
			}
		}
	}

	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		boolean validCatCode = true;
		int maxCatSize = ruleHelper.getMaxCategoryAllowed();
		int currentApprovedCatSize = getManagedBean().getApprovedCategoryList()
				.size();
		int additionalCatSize = (!EntityHelper
				.isCollectionEmpty(getManagedBean().getActiveCategoryList()) ? getManagedBean()
				.getActiveCategoryList().size() : 0) + 1;
		if ((currentApprovedCatSize + additionalCatSize) > maxCatSize) {
			validCatCode = false;
			throw new EpException(MessageConst.ERROR_722517,
					new String[] { maxCatSize + "" });
		}

		log.info("pmCategoryL3.getCategoryName(): "
				+ pmCategoryL3.getCategoryName());

		if (validCatCode) {
			SmSupplierCategory categ = new SmSupplierCategory();
			categ.setRevNo(1);
			categ.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
			if (getManagedBean().getSmAppl().getApplCategoryType()
					.equalsIgnoreCase("S")) {
				categ.setSpecialCategory(true);
			}
			categ.setSmAppl(getManagedBean().getSmAppl());
			categ.setSupplierType(getManagedBean().getSmAppl()
					.getSupplierType());
			getManagedBean().setSelectedCategory(categ);

			categ.setPmCategoryL3(pmCategoryL3);
			categ.setPmCategoryL2(pmCategoryL3.getPmCategoryL2());
			categ.setPmCategoryL1(pmCategoryL3.getPmCategoryL2()
					.getPmCategoryL1());

			// direct set approved by po if the category is added by PO
			if (getManagedBean().isPoUser()) {
				categ.setApprovedByPo(true);
			}
			getManagedBean().setSelectedCategory(categ);
			saveSelectedCategory();

			log.info("getManagedBean().getActiveCategoryList(): "
					+ getManagedBean().getActiveCategoryList());
		}

		// show or hide travel software panel
		SmApplicationUtil.getInstance()
				.removeTravellingSoftwareIfNotApplicable(getManagedBean());

		// show or hide equipment panel
		SmApplicationUtil.getInstance().removeEquipmentIfNotApplicable(
				getManagedBean());

	}

	public void prerenderDirectMofRegistration() {
		//#36474- Start
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		SmAppl rejectedAppl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppType(
				currSupp.getSupplierId(), "N");
		//Add checking check expired for KN
		SmWorkflowStatus rejectedWf = rejectedApplExist(currSupp.getSupplierId());
		if (rejectedAppl != null && my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_MOF.equalsIgnoreCase(currSupp.getSmAppl().getApplType())) {
			Timestamp currentDate = new Timestamp(System.currentTimeMillis());
			long diff = currentDate.getTime()
					- rejectedWf.getCreatedDate().getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			//If expired set null
			if (diffDays >= SmRuleHelper.getInstance().getApplicationResubmissionDays()) {
				rejectedAppl = null;
			}
		}
		//#36474 - End

		ISupplierSearchSO supplierSearchSO = null;
		SmSupplier smSupplier = null;
		try {
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
			smSupplier = smResDTO.getResultBean().getResultList().get(0);
		} finally {
			supplierSearchSO = null;
		}

		getManagedBean().setNewSupplierType(
				smSupplier.getSmAppl().getSupplierType());
		String prerenderUrl = proceedToApplication("N");
		log.info("prerenderUrl: " + prerenderUrl);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/sm/NewMofApplication/mofRegSmUser_BaseWizardPage.xhtml");
	}

	/**
	 * Redirect to Previous or Original Application Details
	 * 
	 * @param String
	 *            linkType
	 */
	public void redirectToRevisionSupplierProfilePage(String linkType) {
		String applNo = linkType.equals("O") ? getManagedBean()
				.getRevisionSupplierAppNoParam() : getManagedBean()
				.getRevisionApproverAppNoParam();
		String applType = linkType.equals("O") ? getManagedBean().getSmAppl()
				.getApplType() : getManagedBean().getRevisionApplType();
		String currTab = Integer.toString(getManagedBean().getTabActiveIndex());
		SmCommonUtil.redirectToExternalUrl(getManagedBean()
				.getRevisionBaseUrl()
				+ "?applicationNo="
				+ applNo
				+ "&applicationType=" + applType + "&currentTab=" + currTab);
	}
	
	//#34099 - start
	public void initRevisionDialog(String linkType) {
		//SmApplicationUtil.getReviewApplicationSupplierBasePage(smAppl.getApplType());
		try {
			ViewSmApplBackingBean vBean = new ViewSmApplBackingBean();
			if(linkType.equals("O")){
				vBean.initMainDialog(getManagedBean().getSmAppl().getApplNo(), "SUP", 
						getManagedBean().getCurrentPageType());
			} else {
				vBean.initMainDialog(
						getManagedBean().getSmSupplier().getSmAppl().getApplNo(), "APP", 
						getManagedBean().getCurrentPageType());
			}
			RequestContext.getCurrentInstance().execute("revisionViewDlg.show()");
		} catch (NullPointerException e){
			throw new EpException(MessageConst.INFO_721212);
		}
	}
	//#34099 - end

	/**
	 * Revert po application to supplier application data
	 */
	public void revertToOriginalApplication() {
		RevertApplicationReqDTO reqDTO = new RevertApplicationReqDTO();
		reqDTO.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().setSmAppl(
				SmServiceLookup.getRevertApplicationSO()
						.revertToSupplierOriginalApplication(reqDTO)
						.getSmAppl());
		initSmCompanyBasic(getManagedBean().getSmAppl());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_721033,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
	}

	/**
	 * Initialized company basic to ManagedBean
	 * 
	 * @param SmAppl
	 *            smAppl
	 */
	private void initSmCompanyBasic(SmAppl smAppl) {
		if (smAppl.getSmCompanyBasic() != null) {
			if (smAppl.getSmCompanyBasic().getSmCompanyAddresses() != null
					&& !smAppl.getSmCompanyBasic().getSmCompanyAddresses()
							.isEmpty()) {
				SmCompanyAddress comAdd = smAppl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0);
				SmAddress addr = comAdd.getSmAddress();
				SmApplicationUtil.setPmStateDropdownValuesForBasicInfo(this
						.getManagedBean(), addr.getPmCountry().getCountryId());
				this.getManagedBean().setBasicCompanyDistricts(
						SmCommonUtil.getDistrictList(addr.getPmState()
								.getStateId()));
				getManagedBean().setPrevBasicStateId(
						addr.getPmState().getStateId());
				this.getManagedBean().setBasicCompanyCities(
						SmCommonUtil.getCityList(addr.getPmDistrict()
								.getDistrictId()));
			} else {
				SmCompanyAddress comAdd = new SmCompanyAddress();
				SmAddress addr = SmApplicationUtil
						.setDefaultAddr(getManagedBean().getDefPmCountry());
				comAdd.setSmAddress(addr);
				List<SmCompanyAddress> addrs = new ArrayList<SmCompanyAddress>();
				addrs.add(comAdd);
				smAppl.getSmCompanyBasic().setSmCompanyAddresses(addrs);
			}
		}
	}

	/**
	 * Set address fields whether need to be mandatory
	 */
	public void setRoleStaffAddrReqd() {
		SmApplicationUtil.setRoleStaffAddrReqd(getManagedBean());
		// BUG #28173 & #28315 -START
		managePersonnelStaff();
		// BUG #28173 & #28315 -END
	}

	/**
	 * Copy hq/branch selected address,owner,price and rate to Facility
	 * 
	 * @param SmFacility
	 *            facility
	 * @param String
	 *            panelName
	 */
	public void onChangeFacAddrSameAs(SmFacility facility, String panelName) {
		if (StringUtils.isNotBlank(getManagedBean().getFacAddresSameAs())) {
			facility.setOwnerName(StringUtils.EMPTY);
			facility.setPurchasePrice(null);
			facility.setRentalRate(null);
			facility.setOwnershipType(StringUtils.EMPTY);
			if (getManagedBean().getFacAddresSameAs().equals("HQ")) {
				SmFacility hq = SmApplicationUtil
						.getHqFacilityByAppl(getManagedBean().getSmAppl());
				if (hq != null) {
					facility.setOwnerName(hq.getOwnerName());
					facility.setPurchasePrice(hq.getPurchasePrice());
					facility.setRentalRate(hq.getRentalRate());
					facility.setOwnershipType(hq.getOwnershipType());
					//#34711 - START
					if(facility.getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)){
						SmApplicationUtil.getInstance().cloneAddress(
								facility.getSmAddress(), this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
					} else {
						SmApplicationUtil.getInstance().cloneAddress(
								facility.getSmAddress(), hq.getSmAddress());
					}
					//#34711 - END	
				}
			} else {
				Long branchId = Long.parseLong(getManagedBean()
						.getFacAddresSameAs());
				SmSupplierBranch branch = (SmSupplierBranch) EntityHelper
						.getEntityBySinglePropertyFromList(branchId,
								getManagedBean().getSmAppl()
										.getSmSupplierBranches(),
								"supplierBranchId");
				SmApplicationUtil.getInstance().cloneAddress(
						facility.getSmAddress(), branch.getSmAddress());
			}
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(), facility.getSmAddress(), panelName);
		}
	}

	/**
	 * Get new additional categories as select item
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getSelectedCategoryItemsByFacilityType(
			String facType) {
		List<SmSupplierCategory> applCategories = null;
		List<SelectItem> retVal = new ArrayList<SelectItem>();
		SelectItem item = null;
		//start #30043
		Boolean businessType = false;
		if (facType.equals("S")) {
			businessType = true;
		}
		//end #30043
		//#28775 - Site Grouping Enhancement
		if (EntityHelper.isCollectionEmpty(getManagedBean().getActiveCategoryList())
				|| getManagedBean().getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			applCategories = getManagedBean().getSmAppl()
					.getSmSupplierCategories();
		} else {
			applCategories = getManagedBean().getActiveCategoryList();
		}
		for (SmSupplierCategory cat : applCategories) {
			if ((cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0 || cat
					.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0)
					&& (cat.getPmCategoryL3() != null && !StringUtils.isBlank(cat.getPmCategoryL3().getFacilityType()) && (cat
							.getPmCategoryL3().getFacilityType().equalsIgnoreCase(facType) || (businessType &&cat.getPmCategoryL3()
							.getFacilityType().equalsIgnoreCase("B") && cat.isSpecialCategory())))) { //#30043

				item = new SelectItem();
				item.setValue(cat.getPmCategoryL3().getCategoryL3Id());
				item.setLabel(cat.getPmCategoryL1().getCategoryL1Code()
						+ cat.getPmCategoryL2().getCategoryL2Code()
						+ cat.getPmCategoryL3().getCategoryL3Code() + " - "
						+ cat.getPmCategoryL3().getCategoryName());
				retVal.add(item);
			}
		}
		return retVal;
	}

	/**
	 * Get new additional categories as select item
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getSelectedCategoryItemsByCategoryType(
			Long categoryTypeId) {
		//28778 - Site Visit Grouping Enhancement -28775 - start
		if (categoryTypeId == null){
			return new ArrayList<>();
		}
		List<SelectItem> retVal = new ArrayList<>();
		List<Integer> validStatus = Arrays.asList(
				DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL, DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		
		for (SmSupplierCategory cat : getManagedBean().getSmAppl().getSmSupplierCategories()) {
			if (cat.getPmCategoryL3() == null || cat.getPmCategoryL3().getCategoryTypeCode() == null
					|| cat.getPmCategoryL3().getCategoryTypeCode().getParameterId() == null){
				continue;
			}
			if (categoryTypeId.equals(cat.getPmCategoryL3().getCategoryTypeCode().getParameterId())
					&& validStatus.contains(cat.getRecordStatusFlg())) {
				SelectItem item = new SelectItem();
				//28778 - Site Visit Grouping Enhancement -28775 - end
				item.setValue(cat.getPmCategoryL3().getCategoryL3Id());
				item.setLabel(cat.getPmCategoryL1().getCategoryL1Code()
						+ cat.getPmCategoryL2().getCategoryL2Code()
						+ cat.getPmCategoryL3().getCategoryL3Code() + " - "
						+ cat.getPmCategoryL3().getCategoryName());
				retVal.add(item);
			}
		}
		return retVal;
	}

	/**
	 * Get whether address fields is mandatory for role staff
	 * 
	 * @param smAddress
	 * @return boolean
	 */
	private boolean isAddressReqdForRoleStaff(SmAddress smAddress) {
		boolean retVal = false;
		if (getManagedBean().getSelectedShareHolder().isClerical()) {
			if (StringUtils.isNotBlank(smAddress.getAddress1())) {
				retVal = true;
			} else if (smAddress.getPmState() != null
					&& smAddress.getPmState().getStateId() != null) {
				retVal = true;
			} else if (smAddress.getPmDistrict() != null
					&& smAddress.getPmDistrict().getDistrictId() != null) {
				retVal = true;
			} else if (smAddress.getPmCity() != null
					&& smAddress.getPmCity().getCityId() != null) {
				retVal = true;
			} else if (StringUtils.isNotBlank(smAddress.getPostcode())) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Validation for saving address for role staff
	 */
	private void validateSaveAddressForRoleStaff() {
		StringBuilder sb = new StringBuilder();

		if (getManagedBean().getShareHolderPermAddr().getSmAddress() != null) {
			SmAddress permAddr = getManagedBean().getShareHolderPermAddr()
					.getSmAddress();
			if (isAddressReqdForRoleStaff(permAddr)) {
				constructSaveStaffRoleAddressErrMsg(permAddr,
						"lbl.sm.person.permAddr", sb);
			}
		}

		if (getManagedBean().getShareHolderMailAddr() != null
				&& getManagedBean().getShareHolderMailAddr().getSmAddress() != null) {
			SmAddress mailAddr = getManagedBean().getShareHolderMailAddr()
					.getSmAddress();
			if (isAddressReqdForRoleStaff(mailAddr)) {
				constructSaveStaffRoleAddressErrMsg(mailAddr,
						"lbl.sm.person.mailAddr", sb);
			}
		}

		if (sb.length() > 0) {
			throw new EpException(sb.toString());
		}
	}

	/**
	 * Construct validation message for saving address for role staff
	 * 
	 * @param smAddress
	 * @param addressTypeKey
	 * @param sb
	 */
	private void constructSaveStaffRoleAddressErrMsg(SmAddress smAddress,
			String addressTypeKey, StringBuilder sb) {

		if (StringUtils.isBlank(smAddress.getAddress1())) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.address"))
					.append("<br />");
		}
		if (smAddress.getPmState() == null
				|| smAddress.getPmState().getStateId() == null) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.state"))
					.append("<br />");
		}
		if (smAddress.getPmDistrict() == null
				|| smAddress.getPmDistrict().getDistrictId() == null) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey,
							"lbl.sm.district")).append("<br />");
		}
		if (smAddress.getPmCity() == null
				|| smAddress.getPmCity().getCityId() == null) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey,
							"lbl.sm.towncity")).append("<br />");
		}
		if (StringUtils.isBlank(smAddress.getPostcode())) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey,
							"lbl.sm.postcode")).append("<br />");
		}
	}

	/**
	 * Check whether address need to be saved
	 * 
	 * @param smAddress
	 * @return
	 */
	private boolean isNoNeedToSaveAddress(SmAddress smAddress) {
		boolean retVal = false;
		if (StringUtils.isBlank(smAddress.getAddress1())
				&& (smAddress.getPmState() == null || smAddress.getPmState()
						.getStateId() == null)
				&& (smAddress.getPmDistrict() == null || smAddress
						.getPmDistrict().getDistrictId() == null)
				&& (smAddress.getPmCity() == null || smAddress.getPmCity()
						.getCityId() == null)
				&& StringUtils.isBlank(smAddress.getPostcode())) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Add new address for personnel
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 * @param SmPersonnelAddress
	 *            smPersonnelAddress
	 */
	private void processAddNewAddress(SmPersonnel shareHolderBean,
			SmPersonnelAddress smPersonnelAddress) {
		if (!isNoNeedToSaveAddress(smPersonnelAddress.getSmAddress())) {
			shareHolderBean.getSmPersonnelAddresses().add(smPersonnelAddress);
		}
	}

	/**
	 * Update edited address or remove if is not needed for personnel
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 * @param SmPersonnelAddress
	 *            addr
	 */
	private void processUpdatedAddress(SmPersonnel shareHolderBean,
			SmPersonnelAddress addr) {
		if (!shareHolderBean.getSmPersonnelAddresses().contains(addr)
				&& !isNoNeedToSaveAddress(addr.getSmAddress())) {
			shareHolderBean.getSmPersonnelAddresses().add(addr);
		} else {
			if (shareHolderBean.getSmPersonnelAddresses().contains(addr)
					&& isNoNeedToSaveAddress(addr.getSmAddress())) {
				shareHolderBean.getSmPersonnelAddresses().remove(addr);
			}
		}
	}

	/**
	 * Update personnel address list and if not exist add to the list, if not
	 * needed remove from list
	 * 
	 * @param SmPersonnelAddress
	 *            addr
	 * @param SmPersonnel
	 *            shareHolderBean
	 */
	private void processUpdatedAddressList(SmPersonnelAddress addr,
			SmPersonnel shareHolderBean) {
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderMailAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderOffAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderResdAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
				&& !isNoNeedToSaveAddress(addr.getSmAddress())) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderPermAddr(addr);
			}
		}
	}

	/**
	 * Get address required msg by address type and address field
	 * 
	 * @param String
	 *            addrTypeKey
	 * @param String
	 *            addrFieldKey
	 * @return String
	 */
	private String getAddressFieldRequiredMsg(String addrTypeKey,
			String addrFieldKey) {
		return SmCommonUtil
				.getMessage(
						"lbl.sm.required.msg",
						new String[] { SmCommonUtil.getMessage(addrTypeKey,
								null)
								+ " - "
								+ SmCommonUtil.getMessage(addrFieldKey, null) });
	}

	/**
	 * Get head quarters facility and set to disabled from ticked
	 * 
	 * @return SmFacility
	 */
	private SmFacility getHqFacility() {
		SmAddress hqAddress = getHqAddress();
		SmFacility hqFacility = null;
		if (hqAddress != null
				&& getManagedBean().getSmAppl().getSmFacilities() != null
				&& !getManagedBean().getSmAppl().getSmFacilities().isEmpty()) {
			for (SmFacility fac : getManagedBean().getSmAppl()
					.getSmFacilities()) {
				if (fac.getSmAddress() != null
						&& fac.getSmAddress().getAddressId()
								.compareTo(hqAddress.getAddressId()) == 0) {
					fac.setBooleanValue(true);
					hqFacility = fac;
					break;
				}
			}
		}
		return hqFacility;
	}

	/**
	 * Get head quarters address
	 * 
	 * @return SmAddress
	 */
	private SmAddress getHqAddress() {
		SmAddress hqAddress = null;
		if (getManagedBean().getSmAppl().getSmCompanyBasic() != null
				&& getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses() != null
				&& !getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().isEmpty()) {
			hqAddress = getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress();
		}
		return hqAddress;
	}

	/**
	 * Set hq facility in selected facility
	 */
	private void setHqFacilityInSelectedFacility() {
		SmFacility hq = getHq();
		if (!isFacilityExistInList(hq, getManagedBean()
				.getSelectedFactoryFacilities())) {
			if (getManagedBean().getSelectedFactoryFacilities() == null
					|| getManagedBean().getSelectedFactoryFacilities()
							.isEmpty()) {
				getManagedBean().setSelectedFactoryFacilities(
						new ArrayList<SmFacility>());

			}
			getManagedBean().getSelectedFactoryFacilities().add(hq);
		}
	}

	/**
	 * Check if selected facility exist in selected facility list
	 * 
	 * @param SmFacility
	 *            facParam
	 * @param List
	 *            <SmFacility> facParamList
	 * @return boolean
	 */
	private boolean isFacilityExistInList(SmFacility facParam,
			List<SmFacility> facParamList) {
		boolean retVal = false;
		if (facParamList != null && !facParamList.isEmpty()) {
			for (SmFacility facTmp : facParamList) {
				if (facParam.getFacilityId() != null
						&& facTmp.getFacilityId() != null
						&& facParam.getFacilityId().compareTo(
								facTmp.getFacilityId()) == 0) {
					retVal = true;
				}
			}
		}
		return retVal;
	}

	/**
	 * Initialized latest gst information for HQ and branch
	 */
	private void initGst() {
		// init GST information
		getManagedBean().setCompanyBasicGst(
				SmApplicationUtil.initHqGstInformation(getManagedBean()
						.getSmAppl(), getManagedBean().getCompanyBasicGst()));
		SmApplicationUtil.initBranchGstInformation(getManagedBean()
				.getCompanyBasicGst(), getManagedBean().getSmAppl());
		if (getManagedBean().getSelectedBranch() != null) {
			getManagedBean().getSelectedBranch().setGst(null);
		}
	}

	/**
	 * Set softcert rejection reason for each personnels
	 */
	private void setPersonnelSoftcertRejectReason() {
		if (getManagedBean().getSmAppl() != null
				&& getManagedBean().getSmAppl().getSmSupplier() != null
				&& getManagedBean().getSmAppl().getSmPersonnels() != null
				&& !getManagedBean().getSmAppl().getSmPersonnels().isEmpty()) {
			for (SmPersonnel personnel : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				SmCommonUtil.setRejectedPersonnelSoftcert(personnel,
						getManagedBean().getSmAppl().getSmSupplier()
								.getSupplierId());
			}
		}
	}
	//#27356
	private void setFacilityAddressSameAs(SmFacility facilityBean) {
		getManagedBean().setFacAddresSameAs(StringUtils.EMPTY);
		//#28775 - 38499
		String[] exclude = new String[] {"pmDivision", "ssmData", "countrySsmNull" };
		if (!EntityHelper.isObjectDifference(getManagedBean().getSmAppl()
				.getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress(), facilityBean.getSmAddress(), exclude)) {
			getManagedBean().setFacAddresSameAs("HQ");
		} else if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmSupplierBranches())) {
			for (SmSupplierBranch br : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				if (!EntityHelper.isObjectDifference(br.getSmAddress(),
						facilityBean.getSmAddress(), new String[] {
								"pmDivision" })) {
					getManagedBean().setFacAddresSameAs(
							br.getSupplierBranchId().toString());
				}
			}
		}
	}

	private void validateOwnerIndividualAcc(SmPersonnel personnel) {
		boolean retVal = false;
		if (personnel.getIdentificationType() != null) {
			PmParameter identificationType = SmApplicationUtil
					.retrievePmParameterById(personnel
							.getIdentificationTypeId());
			retVal = SmCommonUtil
					.isSupplierOwnerIndividualAcc(
							identificationType.getParameterCode(),
							personnel.getIdentificationNo(),
							my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL);
		}
		if (retVal && personnel.isEquityOwner()) {
			throw new EpException(MessageConst.ERROR_721052);
		}
	}

	private void validateAuthorizedPersonnel(SmPersonnel personnel) {
		if (personnel.getAuthorizedFlg() == 1) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setPersonnel(personnel);

			if (!SmServiceLookup.getMofRegistrationSO()
					.validSaveAuthPersonnel(reqDTO).isValidSaveAuthPersonnel()) {
				throw new EpException(MessageConst.ERROR_722667);
			}
		}
	}

	private void deleteSelectedFacilities(List<PmCategoryL3> list) {
		SmFacility facMain = null;
		for (PmCategoryL3 l3 : list) {
			if (!EntityHelper.isCollectionEmpty(l3.getFacilityList())
					&& !EntityHelper.isCollectionEmpty(getManagedBean()
							.getSmAppl().getSmFacilities())) {
				for (SmFacility fac : l3.getFacilityList()) {
					if (fac.isSelected()) {
						facMain = (SmFacility) EntityHelper
								.getEntityBySinglePropertyFromList(fac
										.getFacilityId(), getManagedBean()
										.getSmAppl().getSmFacilities(),
										"facilityId");
						if (facMain != null) {
							getManagedBean().getSmAppl().getSmFacilities()
									.remove(facMain);
						}
					}
				}
			}
		}
	}

	/**
	 * On change check box for machine list
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void onChangeMachineChecked(AjaxBehaviorEvent e) {
		log.info(">>>>>>SELECTED MACHINE SIZE:"
				+ getManagedBean().getSelectedMachineList().size() + "<<<<<<<<");
	}

	public List<SelectItem> getCategoryCodeItems(SmMachineDtl mac) {
		List<SelectItem> retVal = new ArrayList<SelectItem>();
		if (mac.getCategoryType() != null) {
			retVal = SmApplicationUtil.getCategoryCodesItemsByCatTypeId(mac
					.getCategoryType().getParameterId(), getManagedBean()
					.getSmAppl());
		}

		return retVal;
	}

	public List<SelectItem> getMachineItems(SmMachineDtl mac) {
		List<SelectItem> retVal = new ArrayList<SelectItem>();
		if (mac.getCategoryType() != null) {
			retVal = SmApplicationUtil.getMachineItemsByMachineDetails(mac,
					getManagedBean().getSelectedMachineCategoryIds());
		}

		return retVal;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public void validateCategoryOtherDescription() {
		for (SmSupplierCategory cat : this.getManagedBean()
				.getActiveCategoryList()) {
			if (cat.getPmCategoryL3().getCategoryL3Code().equals("99")) {
				if (cat.getOtherDesc() == null
						|| cat.getOtherDesc().trim().length() == 0) {
					StringBuilder sb = new StringBuilder();
					sb.append("<br/>")
							// .append(++i)
							// .append(".) ")
							.append(cat.getPmCategoryL1().getCategoryL1Code())
							.append(cat.getPmCategoryL2().getCategoryL2Code())
							.append(cat.getPmCategoryL3().getCategoryL3Code());

					throw new EpException(MessageConst.ERROR_722514,
							new String[] { sb.toString() });
				}
			}
		}
		RequestContext.getCurrentInstance().execute("catCodePopup.show()");
	}

	private MofRegistrationResDTO getCurrentRegStatus() {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		return SmServiceLookup.getMofRegistrationSO()
				.retrieveApprovedStatusDesc(reqDTO);
	}

	public void setVerifiedSupplierCategory() {
		if (!getManagedBean().getSmAppl().getSmSupplierCategories().isEmpty()) {
			for (SmSupplierCategory suppCategory : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (suppCategory.getPoDecFlg() != null
						&& (suppCategory.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION || suppCategory
								.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)) {
					suppCategory.setApprvrDecFlg(suppCategory.getPoDecFlg());
					setApproverRemark(suppCategory); //#36587
				}
			}
		}
	}

	public void setVerifiedSupportDoc() {
		//#31043 START
		List<SmApplSupportDoc> allSupportDocs = getManagedBean().getSmAppl().getSmApplSupportDocs();
	    	// Create a map of all documents with their IDs for quick lookup
	    	Map<Long, SmApplSupportDoc> allDocsMap = createDocsMap();
	    	for (SmApplSupportDoc applSuppDoc : allSupportDocs) {
	        	if (shouldSetApproverVerifiedFlg(applSuppDoc)) {
	            		applSuppDoc.setApproverVerifiedFlg(applSuppDoc.getVerifiedFlg());
	        	} else {
	            		updateApproverVerifiedFlgFromDocs(applSuppDoc, allDocsMap);
	        	}
	    	}
	   	//#31043 END
	}

	public void viewMyIdentityRespond() {
		if (getManagedBean().getSelectedShareHolder() != null && getManagedBean().getSelectedShareHolder().getIdentityResponseCode() != null) {
			String postCode = "";
			for (SmPersonnelAddress addr : getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()) {
				if (addr.getAddressType() != null && addr.getAddressType().equalsIgnoreCase(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM) && addr.getSmAddress() != null) {
					postCode = addr.getSmAddress().getPostcode();
					log.debug(" *** Perm Addr Post Code ****" + postCode);
				}
			}
			SmCommonUtil.getInstance().verifyPersonnelWithMyIdentity(this.getManagedBean().getSelectedShareHolder(), postCode,
					my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_ONLINE, null);
		}
		RequestContext.getCurrentInstance().execute("myIdentityDialog.show();");
	}
	
//	17906	
	public void checkActiveAndPendingTransaction(){
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		//#17906 - start
		boolean hasActiveTransactionContractManagement = false, hasActiveTransactionFulfilment = false;
		
		if (getManagedBean().getSelectedBanks().size() > 0) {
			for (int i = 0; i < getManagedBean().getSelectedBanks().size(); i++) {
				SmSupplierBank bank = getManagedBean().getSelectedBanks().get(i);
				hasActiveTransactionContractManagement = SmApplicationUtil
						.hasActiveTransactionForContractManagement(currSupp, bank.getAccountNo(), 
								bank.getSmAppl().getSmSupplier().getSupplierId()) //#30693
						|| hasActiveTransactionContractManagement;
				hasActiveTransactionFulfilment = SmApplicationUtil
						.hasActiveTransactionForFulfilment(currSupp, bank.getAccountNo(), 
								bank.getSmAppl().getSmSupplier().getSupplierId()) //#30693
						|| hasActiveTransactionFulfilment;				
			
			}
			String labelNameVal = "";
			if (hasActiveTransactionContractManagement || hasActiveTransactionFulfilment) {
				String msgId = "bankDeleteMsg";
				getManagedBean().setItemDeleteButtonShow(true);
				if (hasActiveTransactionContractManagement && hasActiveTransactionFulfilment) {
					String msg = "<table width='100%' style='font-size:12px'><tr><td style=\"vertical-align: baseline;\">i) </td><td style='text-align: justify'>"
							+ SmCommonUtil.getMessage("lbl.sm.alert.active.transaction", null)
							+ "</td></tr><tr><td style=\"vertical-align: baseline;\">ii) </td><td style='text-align: justify'>"
							+ SmCommonUtil.getMessage("lbl.sm.alert.other.active.transaction", null)
							+"</td></tr></table>";
					FacesContext.getCurrentInstance().addMessage(msgId, 
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, msg));
					
				} else if (hasActiveTransactionContractManagement) {					
					FacesContext.getCurrentInstance().addMessage(msgId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							SmCommonUtil.getMessage("lbl.sm.alert.active.transaction",
									new String[] { labelNameVal })));
					
				} else if (hasActiveTransactionFulfilment) {
					FacesContext.getCurrentInstance().addMessage(msgId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						SmCommonUtil.getMessage("lbl.sm.alert.other.active.transaction",
								new String[] { labelNameVal })));
				} 
			}
			//#17906 - end
			
			if(!getManagedBean().isItemDeleteButtonShow() ){
				RequestContext.getCurrentInstance().execute("bankDeletePopUp.show()");
			}
		} else {
			getManagedBean().setItemDeleteButtonShow(false);
		}
	}
	
	public void itemRowSelectCheckbox(AjaxBehaviorEvent e){
		getManagedBean().setItemDeleteButtonShow(false);	
	}
	
	public void itemRowUnselectCheckbox(AjaxBehaviorEvent e){
		getManagedBean().setItemDeleteButtonShow(false);	
	}
	
	public void assignSelBankWithPendingTransaction(SmSupplierBank shareBankBean) {
		//#12492 - start
		getManagedBean().setShowDifferenceBankInfoAction(false);
		//#12492 - end
		
		getManagedBean().setChequeSingPerId(null);
		getManagedBean().setSelectedBankBranchIds(null);
		
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		
		log.info("checksupplierId" + shareBankBean.getSmAppl().getSmSupplier().getSupplierId());
		boolean hasActiveTransactionContractManagement = SmApplicationUtil
				.hasActiveTransactionForContractManagement(currSupp, shareBankBean.getAccountNo(), 
						shareBankBean.getSmAppl().getSmSupplier().getSupplierId()); //#30693
		boolean hasActiveTransactionFulfilment = SmApplicationUtil
				.hasActiveTransactionForFulfilment(currSupp, shareBankBean.getAccountNo(), 
						shareBankBean.getSmAppl().getSmSupplier().getSupplierId());//#30693
		
		String labelNameVal = "";
		//#17906 - start
		if (hasActiveTransactionContractManagement && hasActiveTransactionFulfilment){
			getManagedBean().setPendingTransaction(true);		
			String msg = "<table width='100%' style='font-size:12px'><tr><td style=\"vertical-align: baseline;\">i) </td><td style='text-align: justify'>" 
					+ SmCommonUtil.getMessage("lbl.sm.bankDtls.is.active.contract.tied", null)
					+"</td></tr><tr><td style=\"vertical-align: baseline;\">ii) </td><td style='text-align: justify'>"
					+SmCommonUtil.getMessage("lbl.sm.bankDtls.is.other.active.contract.tied", null)
					+"</td></tr></table>";
			FacesContext.getCurrentInstance().addMessage("activecontract", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, msg));
			
		} else if (hasActiveTransactionContractManagement) {
			getManagedBean().setPendingTransaction(true);
			String msg = "<div style='text-align: justify'>"
					+SmCommonUtil.getMessage("lbl.sm.bankDtls.is.active.contract.tied", new String[] { labelNameVal })
					+"</div>";
			FacesContext.getCurrentInstance().addMessage("activecontract", new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					msg));
			
		} else if (hasActiveTransactionFulfilment) {
			getManagedBean().setPendingTransaction(true);
			String msg = "<div style='text-align: justify'>"
					+SmCommonUtil.getMessage("lbl.sm.bankDtls.is.other.active.contract.tied", new String[] { labelNameVal })
					+"</div>";
			FacesContext.getCurrentInstance().addMessage("pendingtransaction", new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					msg));
		}
		//#17906 - end
		
		// assign cheque signatories
		Long[] chequSigns = new Long[shareBankBean.getSmChequeSignatories()
				.size()];
		for (int i = 0; i < chequSigns.length; i++) {
			chequSigns[i] = shareBankBean.getSmChequeSignatories().get(i)
					.getSmPersonnel().getPersonnelId();

		}
		getManagedBean().setChequeSingPerId(chequSigns);

		// assign associated branches
		Long[] bankBranchesIds = null;
		if (SmCommonUtil.isCollectionEmpty(shareBankBean
				.getSmSupplierBranchBanks())) {
			bankBranchesIds = new Long[0];
		} else {
			bankBranchesIds = new Long[shareBankBean.getSmSupplierBranchBanks()
					.size()];
			for (int i = 0; i < bankBranchesIds.length; i++) {
				bankBranchesIds[i] = shareBankBean.getSmSupplierBranchBanks()
						.get(i).getSmSupplierBranch().getSupplierBranchId();

			}
		}
		getManagedBean().setSelectedBankBranchIds(bankBranchesIds);
		//#34135 START - remove deleted branch from list at bank information
		List<SmSupplierBranch> displayBranches = new ArrayList<SmSupplierBranch>();
		if (getManagedBean().getSmAppl().getSmSupplierBranches().size() > 0) {
			for (SmSupplierBranch smSupplierBranch : getManagedBean().getSmAppl().getSmSupplierBranches()) {
				if (smSupplierBranch.getRecordStatusFlg() != SupplierManagementConst.RECORD_DELETED) {
					continue;
				}
				displayBranches.add(smSupplierBranch);
			}
		}
		getManagedBean().setDisplayBranches(displayBranches);
		//#34135 END
		if (shareBankBean.getSmAddress() != null
				&& shareBankBean.getSmAddress().getPmCountry().getCountryId() != null
				&& shareBankBean.getSmAddress().getPmState().getStateId() != null
				&& shareBankBean.getSmAddress().getPmDistrict().getDistrictId() != null) {
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(), shareBankBean.getSmAddress(),
					SupplierManagementConst.SM_BANK_INFO_PANEL);
		} else {
			shareBankBean.setSmAddress(SmApplicationUtil
					.setDefaultAddr(getManagedBean().getDefPmCountry()));
			SmApplicationUtil.getInstance().populateStateByCountry(
					shareBankBean.getSmAddress(),
					SupplierManagementConst.SM_BANK_INFO_PANEL,
					getManagedBean());
		}
		getManagedBean().setSelectedBank(
				(SmSupplierBank) SerializationUtils.clone(shareBankBean));
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestBank(
					(SmSupplierBank) EntityHelper
							.getEntityByMultiplePropertiesFromList(
									shareBankBean, getManagedBean()
											.getLatestAppl()
											.getSmSupplierBanks(),
									new String[] { "accountNo",
											"accountPurpose" }, null, null));
		}
		retrieveBankFinOrgListByType();
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("bankDialog.show()");
	}
	
	public void assignBankAccountNo(SmSupplierBank bank){
		this.getManagedBean().setAccountNo(bank.getAccountNo());
	}
	
	//FIXME : 16052019 - SSM INTEGRATION CR - start
	public void redirectToViewSSMInfo() {
		clearPersonnelList();
		//#27165
		boolean showOld = true;
		if (!this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) 
				&& !this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
				&& !this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			SmApplicationUtil.checkEquityOwnerUpdateAllowance(getManagedBean());
		}
		
		if(getManagedBean().isDisAllowOwnerEdit()){
			SsmReqDTO ssmReqDTO = new SsmReqDTO();
			ssmReqDTO.setRegNo(this.getManagedBean().getSmSupplier().getRegNo());
			ssmReqDTO.setBusinessType(this.getManagedBean().getSmSupplier().getBusinessType());
			if (SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())) {
				ssmReqDTO.setBasicAcc(true);
			} else {
				ssmReqDTO.setBasicAcc(false);
			}
			SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
			this.getManagedBean().setCompanyValidWithSSM(false);
			this.getManagedBean().setValidateWithSSM(true);
			if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
				for(SmPersonnel personnel:ssmResDTO.getSmPersonnelAll()){
					personnel = setDesignationSSM(personnel);
					
					//#30944 START
					if (!this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) 
							&& !this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
							&& !this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
						for (SmPersonnel per : this.getManagedBean().getSmAppl().getSmPersonnels()) {
							if (per.getName().equals(personnel.getName())) {
								personnel.setShareAmt(per.getShareAmt());
							}
						}
					}
					//#30944 END
					
					if(personnel.isEquityOwner()){
						this.getManagedBean().getPersonnelsOwner().add(personnel);
					}
					if(personnel.isDirector()){
						this.getManagedBean().getPersonnelsDirector().add(personnel);
					}
					this.getManagedBean().getPersonnelsSsm().add(personnel);									
				}
				showOld = false;
			}
		}
		if(showOld){
			for(SmPersonnel personnel:getManagedBean().getSmAppl().getSmPersonnels()){
				if(personnel.isSsmData()){
					if(personnel.isEquityOwner()){
						this.getManagedBean().getPersonnelsOwner().add(personnel);
					}
					if(personnel.isDirector()){
						this.getManagedBean().getPersonnelsDirector().add(personnel);
					}
					this.getManagedBean().getPersonnelsSsm().add(personnel);
				}				
			}			
		}
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(getManagedBean().getSmAppl().getApplNo());
		MofRegistrationResDTO resDTO =  SmServiceLookup.getMofRegistrationSO().retrieveSmApplSSM(reqDTO);
		if(resDTO.getSmAppl().getSmSupplierBranches() != null){
			this.getManagedBean().getSmSupplierBranchSsm().clear();
			for(SmSupplierBranch branch:resDTO.getSmAppl().getSmSupplierBranches()){
				if(branch.isSsmData())
					this.getManagedBean().getSmSupplierBranchSsm().add(branch);
			}
		}
		if(resDTO.getSmAppl().getSmSupplier().getSmSupplierBusinesses() != null){
			this.getManagedBean().getSmSupplierBusinessSsm().clear();
			for(SmSupplierBusiness business:resDTO.getSmAppl().getSmSupplier().getSmSupplierBusinesses()){
				if(business.isSsmData())
					this.getManagedBean().getSmSupplierBusinessSsm().add(business);
			}
		}
		if(resDTO.getSmAppl().getSmEquityCompanies() != null){
			this.getManagedBean().getSmEquityCompaniesSsm().clear();
			for(SmEquityCompany company:resDTO.getSmAppl().getSmEquityCompanies()){
				if(company.isSsmData())
					this.getManagedBean().getSmEquityCompaniesSsm().add(company);
			}
		}
		//#27165
		//34099
		RequestContext.getCurrentInstance().execute("viewssminfoDlg.show()");
		//SmCommonUtil.redirectToExternalUrl(SupplierManagementConst.VIEW_SSM_INFOMATION);
	}
	
	public void clearPersonnelList() {
		this.getManagedBean().getPersonnelsOwner().clear();
		this.getManagedBean().getPersonnelsDirector().clear();
		//27346 - start
		this.getManagedBean().getPersonnelsOwnerSsm().clear();
		this.getManagedBean().getSmPersonnelDirectorSsm().clear();
		//27346-end
		this.getManagedBean().getSmSupplierBranchSsm().clear();
		this.getManagedBean().getSmSupplierBusinessSsm().clear();
		this.getManagedBean().getPersonnelsSsm().clear();
	}
	
	public String getBusinessNatureSection(SmSupplierBusiness suppBizNature){
		RegistrationBackingBean registrationBackingBean = new RegistrationBackingBean();
		return registrationBackingBean.getBusinessNatureSection(suppBizNature);
	}
	
	public void calcPersonnelShareAmt(List<SmPersonnel> personnels,SmCompanyBasic companyBasic){
		BigDecimal bumiputeraCapital = new BigDecimal(0);
		BigDecimal nonBumiputeraCapital = new BigDecimal(0);
		BigDecimal nonMalaysianCapital = new BigDecimal(0);
		/* 
		BigDecimal govermentAgencyEquityCapital = new BigDecimal(0);// XAK,XAU 
	 	BigDecimal govermentEquityCapital = new BigDecimal(0); // XG 
	 	BigDecimal foreignCapital = new BigDecimal(0);// f
	 	BigDecimal localCapital = new BigDecimal(0);// C 
	 	*/
		for (SmPersonnel personnel : personnels) {
			if (personnel.isEquityOwner() && personnel.getShareAmt() != null && personnel.getPmCountry() != null) {//#29582 #35300
				if (ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(personnel.getIdentificationType().getParameterCode()) 
						|| !SupplierManagementConst.COUNTRY_MALAYSIA_CODE.equalsIgnoreCase(personnel.getPmCountry().getCountryCode())) { //28545
					nonMalaysianCapital = nonMalaysianCapital.add(personnel.getShareAmt());
				} else {
					if (personnel.getBumiFlg() != null && personnel.getBumiFlg() == 1) {
						bumiputeraCapital = bumiputeraCapital.add(personnel.getShareAmt());
					} else {
						nonBumiputeraCapital = nonBumiputeraCapital.add(personnel.getShareAmt());
					}
				}

			}
		}
		//set latest Value to company basic
		companyBasic.setBumiEquity(bumiputeraCapital);
		companyBasic.setNonBumiEquity(nonBumiputeraCapital);
		companyBasic.setForeignerEquity(nonMalaysianCapital);
	}
	
	// #26734 START
	public void redirectToHomepage() throws IOException {
		log.debug("redirectToHomepage()  <<--- here ");
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String path = SmCommonUtil.getBasePortalUrl();
		context.redirect(path);
	}
	// #26734 END
	//FIXME : 16052019 - SSM INTEGRATION CR - end
	
	//Bug #26705 - start
	public void getlatestPoOverlappingRemarks() {
		SmAppl applId = SmApplicationUtil
				.getLatestApplBySupplierId(getManagedBean().getSmAppl()
						.getSmSupplier().getSupplierId());
		if (applId != null) {
			SmRemaksResDTO remResDTO = SmApplicationUtil.getRemarksByDoc(
					ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER,
					applId.getApplId());
			if (getManagedBean().isOverLappedCategoryExists()) {
				for (SmRemark rmk : remResDTO.getRemarksList())
					if (rmk.getDocType()
							.equals(ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER)) {
						getManagedBean()
								.setApplnOverLapRemarks(rmk.getRemark());

					}
			}
		}
	}
	//Bug #26705 - End
	
	//SSM INTEGRATION
	public void reCalculateCapitalPersonnel(){
		BigDecimal bumiputeraCapital = new BigDecimal(0);
		BigDecimal nonBumiputeraCapital = new BigDecimal(0);
		BigDecimal nonMalaysianCapital = new BigDecimal(0);
			
		if(this.getManagedBean().getSmAppl().getSmPersonnels()!=null){
			for(SmPersonnel per : this.getManagedBean().getSmAppl().getSmPersonnels()){
				if(per.isEquityOwner() && per.getShareAmt() !=null && per.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED){
					if(per.getPmCountry() != null && SupplierManagementConst.COUNTRY_MALAYSIA_CODE.equalsIgnoreCase(per.getPmCountry().getCountryCode())){
						if(per.isBumi() != null && per.isBumi()){
							bumiputeraCapital = bumiputeraCapital.add(per.getShareAmt());
						}else{
							nonBumiputeraCapital = nonBumiputeraCapital.add(per.getShareAmt());
						}
					}else{
						nonMalaysianCapital = nonMalaysianCapital.add(per.getShareAmt());
					}
				}
			}
		}
		
		log.info("Local Company = "+this.getManagedBean().getSmAppl().getSmCompanyBasic().getLocalCompanyEquity());
		log.info("Foreign Company = "+this.getManagedBean().getSmAppl().getSmCompanyBasic().getForeignCompanyEquity());
		this.getManagedBean().getSmAppl().getSmCompanyBasic().setNonBumiEquity(nonBumiputeraCapital);
		this.getManagedBean().getSmAppl().getSmCompanyBasic().setBumiEquity(bumiputeraCapital);
		this.getManagedBean().getSmAppl().getSmCompanyBasic().setForeignerEquity(nonMalaysianCapital);
		// 28466 - to maintain paid up capital from SSM - fix reopen
		if(!SmCommonUtil.getInstance().ssmActive()){
			this.getManagedBean().getSmAppl().getSmCompanyBasic().setPaidUpCapital(this.getManagedBean().getSmAppl().getSmCompanyBasic().getBumiEquity()
					.add(this.getManagedBean().getSmAppl().getSmCompanyBasic().getNonBumiEquity())
					.add(this.getManagedBean().getSmAppl().getSmCompanyBasic().getLocalCompanyEquity())
					.add(this.getManagedBean().getSmAppl().getSmCompanyBasic().getForeignCompanyEquity())
					.add(this.getManagedBean().getSmAppl().getSmCompanyBasic().getForeignerEquity())
					.add(this.getManagedBean().getSmAppl().getSmCompanyBasic().getGovtEquity())
					.add(this.getManagedBean().getSmAppl().getSmCompanyBasic().getGovtUnitrustEquity())
					.setScale(2, RoundingMode.HALF_UP));
		}
		
		if(this.getManagedBean().getSmAppl().getSmEquityCompanies() != null){
			for(SmEquityCompany company:this.getManagedBean().getSmAppl().getSmEquityCompanies()){
				if(company.getShareAmt() != null){
					company.setSharePercentage(calPercentage(this.getManagedBean().getSmAppl().getSmCompanyBasic().getPaidUpCapital(), company.getShareAmt()));
				}
			}
		}
	}

	public void validateRole(SmPersonnel personnel){
		if(personnel.isEquityOwner() || personnel.isDirector() || personnel.isDirOrHghrMngmt()){
			personnel.setClerical(false);
			personnel.setSemiProf(false);//#38610
		}
	}
	public BigDecimal calPercentage(BigDecimal totalCapitalCompany,BigDecimal shareAmt){
		BigDecimal sharePercentage = new BigDecimal(0);
		if(totalCapitalCompany.longValue() > 0){
			sharePercentage = new BigDecimal(100).multiply(shareAmt)
					.divide(totalCapitalCompany,RoundingMode.HALF_UP);
		}
		return sharePercentage;
			
	}
		
	public void unSelectEquityCompany(AjaxBehaviorEvent e) {
		SmApplicationUtil.removeEquityCompanyFromList(getManagedBean()
						.getSelectedEquityCompanies());
	}
	
	//SSM INTEGRATION
	public boolean showDoc(PmDocument doc,boolean changesInCapital){
		boolean showDoc = true;
		//ssm changes to hide doc for roc if paid up capital = paid up capital ssm. if paid up capital != paid up capital ssm show doc.
		for(PmDocumentRestr documentRestr:doc.getPmDocumentRestrs()){
			log.info(doc.getDocName()+"--"+documentRestr.getDocumentRestrId()+"documentRestr.isCapitalEquityChanged()="+documentRestr.isCapitalEquityChanged());
			if (documentRestr.isCapitalEquityChanged()){
				if (changesInCapital) {
					showDoc = true;
					break;
				}else{
					showDoc = false;
				}
			}else{
				showDoc = true;
			}
			
		}
		return showDoc;
	}
	
	// START #29727 & 31627
	public void unSelectSsmBranch(AjaxBehaviorEvent e) {
		SmApplicationUtil.ajaxRemoveSelectedSSMBranches(getManagedBean().getSelectedBranches());
	}
	
	public boolean showEquity(SmApplicationManagedBean managedBean){
		boolean show = true;
		if(managedBean!= null && managedBean.getSmAppl()!=null){
			if(!managedBean.getSmAppl().isApplValidWithSsm()){
				show = true;
			}else if(managedBean.getSmAppl().isApplValidWithSsm()){
				if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
					if(managedBean.getSelectedShareHolder().isEquityOwner()){
						show = true;
					}else{
						show = false;
					}
				}else{
					show = true;
				}
			}
		}
		
		return show;
	}
	
	//#27232 - START SSM CHANGES FOR RESUBMIT
	public void ssmIntegrationResubmit(){
		//#27165
		boolean dontGetSsmData = this.getManagedBean().isDisAllowOwnerEdit();
		SsmReqDTO ssmReqDTO = new SsmReqDTO();
		ssmReqDTO.setRegNo(this.getManagedBean().getSmSupplier().getRegNo());
		ssmReqDTO.setBusinessType(this.getManagedBean().getSmSupplier().getBusinessType());
		if(SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())){
			ssmReqDTO.setBasicAcc(true);
		}else{
			ssmReqDTO.setBasicAcc(false);
		}
		//#27165
		this.getManagedBean().setCompanyValidWithSSM(false);
		this.getManagedBean().setValidateWithSSM(true);
		SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
		if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
			this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
			if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
			//27382 - start
			if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("L")){
				throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
			}//27382 - Terminated 
			if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("T")){
				throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
			}//27382 - Dissolved Conversion to LLP
			if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("B")){
				if(businessTypeIsROC(this.getManagedBean().getSmSupplier().getBusinessType())){
					throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null) });
				}
				if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
					throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null) });
				}
			}else{ 
				throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});}
			}
			//27382 - end
			this.getManagedBean().setCompanyValidWithSSM(true);
			SmCommonUtil.ssmIdentificationTypeError(ssmResDTO); //#29582
			// #24819
			this.getManagedBean().getSmSupplier().setSsmNewRegNo(ssmResDTO.getSsmNewRegNo());
			//SET COMPANY NAME
			//if rob no need to copy capital.
			//long tempAnnualRevenue = this.getManagedBean().getSmAppl().getSmCompanyBasic().getAnnualRevenue();
			if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
				String[] copyProp = {"companyName","robExpiryDate","businessDesc","annualRevenue","revenueSsmNull","robExpirySsmNull","authorizedCapitalSsmNull","companySsmStatus","annualRevenueSSM"};
				EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(),this.getManagedBean().getSmAppl().getSmCompanyBasic(),copyProp);							
			}else{
				// 28466 - add paid up capital & #30660 start - fix reopen
				if (getManagedBean().isDisAllowOwnerEdit()) {
					String[] copyProp = { "companyName", "robExpiryDate", "annualRevenue",  "businessDesc", "revenueSsmNull", "robExpirySsmNull",
							"authorizedCapitalSsmNull", "companySsmStatus", "annualRevenueSSM",	"paidUpCapitalSSM" };
					EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(), this.getManagedBean()
							.getSmAppl().getSmCompanyBasic(), copyProp);
				} else {
					String[] copyProp = { "companyName", "robExpiryDate", "authorizedCapital", "nonBumiEquity",	"annualRevenue", "bumiEquity",
							"localCompanyEquity", "foreignCompanyEquity", "foreignerEquity", "govtEquity", "govtUnitrustEquity", "businessDesc",
							"revenueSsmNull", "robExpirySsmNull", "authorizedCapitalSsmNull", "companySsmStatus", "annualRevenueSSM", "paidUpCapitalSSM",
							"paidUpCapital" };
					EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(), this.getManagedBean()
							.getSmAppl().getSmCompanyBasic(), copyProp);
				}
				//#30660 end
			}
			//#27165
			if(dontGetSsmData){	
				String[] copyProp = {"companyName","robExpiryDate","businessDesc","annualRevenue","revenueSsmNull","robExpirySsmNull","authorizedCapitalSsmNull","companySsmStatus","annualRevenueSSM"};
				EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(),this.getManagedBean().getSmAppl().getSmCompanyBasic(),copyProp);														
			}
			/*if((ssmResDTO.getSmCompanyBasic().getAnnualRevenue() == null || ssmResDTO.getSmCompanyBasic().getAnnualRevenue()==0) && tempAnnualRevenue > 0){
				this.getManagedBean().getSmAppl().getSmCompanyBasic().setAnnualRevenue(tempAnnualRevenue);
			}*/
			//SET ADDRESS
			//27495
			String[] copyPropAddress = {"address1","address2","address3","addressSsmNull","postcodeSsmNull","stateSsmNull","countrySsmNull","ssmData","postcode","stateId"};

			EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmAddress(),this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress(),copyPropAddress);
			this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPostcode(ssmResDTO.getSmAddress().getPostcode());
			this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmState(ssmResDTO.getSmAddress().getPmState());
			//END #27256
			this.getManagedBean().getSmAppl().setApplValidWithSsm(true);
			//set establish date
			this.getManagedBean().getSmAppl().getSmCompanyBasic().setEstablishDate(ssmResDTO.getSmCompanyBasic().getEstablishDate());
			
			//POPUP USER NOT SSM OWNER #26734 START
			//FacesScopes facesScope = new FacesScopes();
			//PmUser userSession = facesScope.getUserSessionBean();
			/*if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) || applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW) || applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)){
				boolean checkWithSSM = true;
				SmPersonnel personnelOld = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(
						userSession.getIdentificationNo(), this.getManagedBean().getSmAppl()
								.getSmPersonnels(), "identificationNo");
				if (personnelOld.isEquityOwner()) {
					checkWithSSM = true;
				} else {
					checkWithSSM = false;
				}
				
				if(userSession != null && checkWithSSM){
					SmPersonnel personnelSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(userSession.getIdentificationNo(),ssmResDTO.getSmPersonnelOwners(),"identificationNo");
					if(personnelSSM == null) {	
						getManagedBean().setSmSupplierSsm(this.getManagedBean().getSmSupplier());	
						getManagedBean().setSmPersonnelSsm(personnelOld);
						RequestContext.getCurrentInstance().execute("ownerCheckingWidgetVar.show()");
						return SmApplicationUtil.setStringNavigationForApplTnCPage(applicationType);
					}
				}
			}*/
			// #26734 END
			
			//CHECK EQUITY OWNER EXIST IN SSM TO DISABLE CHECKBOX
			int i = 0;
			for(SmPersonnel personnel : this.getManagedBean().getSmAppl().getSmPersonnels()){
				//#27165
				if(dontGetSsmData){
					break;
				}
				validateRole(personnel);
				SmPersonnel personnelSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),ssmResDTO.getSmPersonnelAll(),"identificationNo");
				if(personnelSSM != null){								
					//if rob no need to copy shareamt.
					if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
						personnel.setSsmData(true);
						personnel.setShareAmtSsmNull(true);
						//SSM CHANGE 11012020
						//#27315, #35145
						EntityHelper.copyEntityPropertiesOnly(personnelSSM,personnel,getEquityOwnerField(true));
					}else{
						personnel.setSsmData(true);
						//#27315
						//27408 - Remove Pmcountry for ROC
						//28767 - add birthdate & designation, #35145
						EntityHelper.copyEntityPropertiesOnly(personnelSSM,personnel,getEquityOwnerField(false));
					}
					validateRole(personnel);
					//personnel.setSmPersonnelAddresses(personnelSSM.getSmPersonnelAddresses());
					//personnel.setSmAppl(this.getManagedBean().getSmAppl());
					///check ssm address exist or not
					if(personnelSSM.getSmPersonnelAddresses()!= null && personnelSSM.getSmPersonnelAddresses().size() > 0){
						//check ssm address exist type = R
						SmPersonnelAddress personnelAddressSSM = (SmPersonnelAddress)EntityHelper.getEntityBySinglePropertyFromList(my.ep.constant.sm.SupplierManagementConst.ADDRESS_TYPE_RESIDENTIAL,personnelSSM.getSmPersonnelAddresses(),"addressType");
						if(personnelAddressSSM != null){
							if(personnel.getSmPersonnelAddresses()!= null && personnel.getSmPersonnelAddresses().size() > 0){
								for(SmPersonnelAddress personnelAddress : personnel.getSmPersonnelAddresses()){
									if(my.ep.constant.sm.SupplierManagementConst.ADDRESS_TYPE_RESIDENTIAL.equalsIgnoreCase(personnelAddress.getAddressType())){
										personnelAddress.setSmPersonnel(personnel);
										EntityHelper.copyEntityPropertiesOnly(personnelAddressSSM.getSmAddress(),personnelAddress.getSmAddress(),copyPropAddress);
										break;
									}
								}
							}
						}
						
					}
				}else{
					//27239
					setDeletePersonnel(personnel);
				}
				this.getManagedBean().getSmAppl().getSmPersonnels().set(i, personnel);
				i++;
			}
			//SET EQUITY COMPANY
			//if(this.getManagedBean().getSmAppl().getSmEquityCompanies()!=null){
			// #30660
			if(!businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType()) && ssmResDTO.getSmEquityCompanys()!=null && !dontGetSsmData){
				this.getManagedBean().setSmEquityCompanyTemp(ssmResDTO.getSmEquityCompanys());
				if(this.getManagedBean().getSmAppl().getSmEquityCompanies()==null){
					this.getManagedBean().getSmAppl().setSmEquityCompanies(new ArrayList<SmEquityCompany>());
				}
				//delete all old company share #27145
				Iterator<SmEquityCompany> iterequityCompany = this.getManagedBean().getSmAppl().getSmEquityCompanies().iterator();
				while(iterequityCompany.hasNext()){
					iterequityCompany.next();
					iterequityCompany.remove();
				}
				
				//TO Set equity compny from ssm only #27145
				for(SmEquityCompany equityCompany : ssmResDTO.getSmEquityCompanys()){
						equityCompany.setSmAppl(this.getManagedBean().getSmAppl());
						this.getManagedBean().getSmAppl().getSmEquityCompanies().add(equityCompany);
				}		
			}
			//SET SMPERSONNEL
			if(ssmResDTO.getSmPersonnelAll()!=null){
				if(this.getManagedBean().getSmAppl().getSmPersonnels()==null){
					this.getManagedBean().getSmAppl().setSmPersonnels(new ArrayList<SmPersonnel>());
				}
				for(SmPersonnel personnel : ssmResDTO.getSmPersonnelAll()){
					//#27165
					if(dontGetSsmData){
						break;
					}
					SmPersonnel oldPersonnel = (SmPersonnel)EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), this.getManagedBean().getSmAppl().getSmPersonnels(), "identificationNo");
					
					if(oldPersonnel == null){
						personnel.setBrandNew(true);
						personnel.setSmAppl(this.getManagedBean().getSmAppl());
						personnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);	
						if(personnel.getSmPersonnelAdd()!= null){
							personnel.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
							personnel.getSmPersonnelAdd().setSmPersonnel(personnel);
							personnel.getSmPersonnelAddresses().add(personnel.getSmPersonnelAdd());
						}
						if(personnel.getPmCountry() == null || personnel.getPmCountry().getCountryId() == null){
							//#27408
							if(personnel.getIdentificationType() != null && ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(personnel.getIdentificationType().getParameterCode())){
								PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(ParameterConst.COUNTRY_UNKNOWN_CODE);
								personnel.setPmCountry(defaultCountry);
							}else{
								PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
								personnel.setPmCountry(defaultCountry);
							}
						}
						
						this.getManagedBean().getSmAppl().getSmPersonnels().add(personnel);
					}else{
						validateRole(oldPersonnel);
						if(personnel.getPmCountry() == null || personnel.getPmCountry().getCountryId() == null){
							//#27408
							if(personnel.getIdentificationType() != null && ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(personnel.getIdentificationType().getParameterCode())){
								PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(ParameterConst.COUNTRY_UNKNOWN_CODE);
								personnel.setPmCountry(defaultCountry);
							}else{
								PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
								personnel.setPmCountry(defaultCountry);
							}
						}
					}
				}
			}
			//SET EQUITY BRANCH
			if (ssmResDTO.getSmSupplierBranches() != null) {
				if (this.getManagedBean().getSmAppl().getSmSupplierBranches() == null) {
					this.getManagedBean().getSmAppl().setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
				}

				for (SmSupplierBranch branchSSM : ssmResDTO.getSmSupplierBranches()) {
					copyBranchFromSSM(branchSSM);
				}
			}
			//SET BUSIESS NATURE
			/*
			boolean existInOld=false;
			//compare old data with ssm, if ssm has new data, add data into list
			if(getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses().size() > 0){
				for(SmSupplierBusiness smSupplierBusiness:ssmResDTO.getSmSupplierBusinessList()){
					if(smSupplierBusiness.getPmBusinessNature()!=null){
						for(SmSupplierBusiness smSuppBussOld:getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses()){
							if(smSupplierBusiness.getPmBusinessNature().getLevelCode().equalsIgnoreCase(smSuppBussOld.getPmBusinessNature().getLevelCode())){
								existInOld=true;
							}
						}
					}
					if(!existInOld){
						smSupplierBusiness.setSmSupplier(getManagedBean().getSmSupplier());
						getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses().add(smSupplierBusiness);
					}
				}
			//if old data not exits,set all data from ssm into list
			}else if(ssmResDTO.getSmSupplierBusinessList().size() > 0){
				getManagedBean().getSmAppl().getSmSupplier().setSmSupplierBusinesses(ssmResDTO.getSmSupplierBusinessList());
				for(SmSupplierBusiness business :getManagedBean().getSmAppl().getSmSupplier().getSmSupplierBusinesses()){
					getManagedBean().getSmAppl().getSmSupplier().setSmAppl(getManagedBean().getSmAppl());
					business.setSmSupplier(getManagedBean().getSmAppl().getSmSupplier());
				}
			}*/
			// maybe here to put method to check with ssm
			//SmCommonUtil.getInstance().validateWithSSM(regNo,businessType,"");
			// 28466 - to maintain paid up capital from SSM
			if(!businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
				reCalculateCapitalPersonnel();
			}
			//Remove equity/director personnel not exist in ssm
			if(this.getManagedBean().getSmAppl().getSmPersonnels()!= null){
				Iterator<SmPersonnel> iterPersonnel = this.getManagedBean().getSmAppl().getSmPersonnels().iterator();
				while(iterPersonnel.hasNext()){
					SmPersonnel personnel = iterPersonnel.next();
					//START SSM CHANGES #27201 - SET DESIGNATION
					personnel = setDesignationSSM(personnel);
					//END SSM CHANGES #27201 - SET DESIGNATION
					if(personnel.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_DELETED){
						if (this.getManagedBean().getSmAppl().getSmSupplierBanks() != null && !this.getManagedBean().getSmAppl().getSmSupplierBanks().isEmpty()) {
							
							for (SmSupplierBank smSupplierBank : this.getManagedBean().getSmAppl().getSmSupplierBanks()) {
								if (smSupplierBank.getSmChequeSignatories() != null && !smSupplierBank.getSmChequeSignatories().isEmpty()) {
									Iterator<SmChequeSignatory> itersmChequeSignatory = smSupplierBank.getSmChequeSignatories().iterator();
									while (itersmChequeSignatory.hasNext()) {
										SmChequeSignatory smChequeSignatory = itersmChequeSignatory.next();
										if (smChequeSignatory.getSmPersonnel() != null && smChequeSignatory.getSmPersonnel().getPersonnelId() != null && smChequeSignatory.getSmPersonnel().getPersonnelId().compareTo(personnel.getPersonnelId()) == 0 ) {
											itersmChequeSignatory.remove();
										}
									}
								}
							}
						}
						
						if (this.getManagedBean().getSmAppl().getSmSupplierBranches() != null && !this.getManagedBean().getSmAppl().getSmSupplierBranches().isEmpty()) {
							Iterator<SmSupplierBranch> itersmSupplierBranch = this.getManagedBean().getSmAppl().getSmSupplierBranches().iterator();
							while (itersmSupplierBranch.hasNext()) {
								SmSupplierBranch smSupplierBranch = itersmSupplierBranch.next();
								if (smSupplierBranch.getSmPersonnel() != null && smSupplierBranch.getSmPersonnel().getPersonnelId().compareTo(personnel.getPersonnelId()) == 0) {
										smSupplierBranch.setSmPersonnel(null);
								}
							}
						}
						
						iterPersonnel.remove();
					}
				}
			}
		}else{
			this.getManagedBean().setCompanyValidWithSSM(false);
		}
			
		//#33569, #28767 - start
		if(ssmResDTO.getSmApplIndebtedness() != null){
			ssmResDTO.getSmApplIndebtedness().setSmAppl(this.getManagedBean().getSmAppl());
			this.getManagedBean().getSmAppl().setSmApplIndebtedness(ssmResDTO.getSmApplIndebtedness());
		}
		
		if(ssmResDTO.getSmPersonnelSecretaries() != null){
			this.getManagedBean().getSmAppl().setSmPersonnelSecretaries(new ArrayList<SmPersonnelSecretaries>());
			for(SmPersonnelSecretaries personnelSecretaries: ssmResDTO.getSmPersonnelSecretaries()){
				personnelSecretaries.setSmAppl(this.getManagedBean().getSmAppl());
				this.getManagedBean().getSmAppl().getSmPersonnelSecretaries().add(personnelSecretaries);
			}
		}
		
		for(SmPersonnelSecretaries personnelSec : this.getManagedBean().getSmAppl().getSmPersonnelSecretaries()){
			if(personnelSec.getSecDesignation() != null){
				String designation = populateDesignationSSM(personnelSec.getSecDesignation());
				personnelSec.setSecDesignation(designation);
			}
		}
		
		if(ssmResDTO.getSmApplAuditors()!= null){
			this.getManagedBean().getSmAppl().setSmApplAuditors(new ArrayList<SmApplAuditors>());
			for(SmApplAuditors auditors: ssmResDTO.getSmApplAuditors()){
				auditors.setSmAppl(this.getManagedBean().getSmAppl());
				this.getManagedBean().getSmAppl().getSmApplAuditors().add(auditors);
			}
		}
		//#33569, #28767 - end
		
		if(!this.getManagedBean().isCompanyValidWithSSM()){
//						throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.contractor",null), getManagedBean().getSmSupp().getRegNo()});
			throw new EpException(MessageConst.ERROR_721070);
		}else{
			IMofRegistrationSO mofRegistrationSO = null;
			//save smappl
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDto = new MofRegistrationReqDTO();
			mofReqDto.setSmAppl(this.getManagedBean().getSmAppl());
			//#24819
			mofReqDto.setSmSupplier(getManagedBean().getSmAppl().getSmSupplier());
			mofRegistrationSO.updateSupplierProfile(mofReqDto);
			MofRegistrationResDTO mofRespDto = mofRegistrationSO.updateSmAppl(mofReqDto);
			this.getManagedBean().setSmAppl(mofRespDto.getSmAppl());
			
		}
	
	}
		
	//#27232 - END SSM CHANGES FOR RESUBMIT
	
	//START : #27239 
	public void setDeletePersonnel(SmPersonnel personnel){
		if(personnel.getEpRole() == null && (personnel.isEquityOwner() ||personnel.isDirector())){
			log.info(personnel.getEpRole()+"eprol-----Personnel Id = "+personnel.getPersonnelId()+"=APPL ID="+personnel.getSmAppl().getApplId()+"--Deleted : "+personnel.getName());									
			personnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
		}else{//Start 28651
			personnel.setSsmData(false);
			
			// START : #29956
			for(SmPersonnelAddress smPersonnelAddress : personnel.getSmPersonnelAddresses()){
				if(smPersonnelAddress.getSmAddress() != null){
					smPersonnelAddress.getSmAddress().setSsmData(false);
				}
				
			}
			// END : #29956			
		}
		//end 28651
	}
	//END : #27239 
	
	//13320-Start	
	public void onChangeDistrictByDivision(SmAddress addr, String panelName) {
		SmApplicationUtil.getInstance().populateDistrictsByUsingDivision(addr,
				panelName, getManagedBean());
		setRoleStaffAddrReqd();
		//#13320 - SmCommonUtil change method
		addr.setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(addr.getDivisionId()));
		//#36483 - START
		if(panelName.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL)){
			if(getManagedBean().getBasicCompanyFacility().getOwnershipType() != null
					&& getManagedBean().getBasicCompanyFacility().getOwnershipType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_OWNERSHIP_OWN)
					&& addr.getDivisionId()!=null){
				getManagedBean().getBasicCompanyFacility().getSmAddress().setDivisionId(addr.getDivisionId());
			}
			
			if(!addr.isSsmData()){
				addr.setPostcode("");
			}
		}
		
		addr.setMapLatitude("");
		addr.setMapLongitude("");
		//#36483 - END
	}
	//13320-End	
	
	// BUG #28173 & #28315 -START
	public void managePersonnelStaff() {
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
			//#30444 START--25082021--
			if(getManagedBean().getBusinessTypeVal()
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)
						&& !alreadyHadEquityOwnerAsStaff(getManagedBean().getSmAppl(), getManagedBean().getSelectedShareHolder().getPersonnelId())){
				if(getManagedBean().getSelectedShareHolder().isClerical()
						&& !getManagedBean().getSelectedShareHolder().isEquityOwner()) {
					getManagedBean().getSelectedShareHolder().setManagement(false);
					getManagedBean().getSelectedShareHolder().setDisableManagement(true);
				}else{
					getManagedBean().getSelectedShareHolder().setDisableManagement(false);
				}
			} else {
				if (getManagedBean().getSelectedShareHolder().isClerical()) {
					getManagedBean().getSelectedShareHolder().setManagement(false);
					getManagedBean().getSelectedShareHolder().setEquityOwner(false);
					getManagedBean().getSelectedShareHolder().setHigherManagement(false);
					getManagedBean().getSelectedShareHolder().setDirector(false);
					getManagedBean().getSelectedShareHolder().setDisableManagement(true);
					getManagedBean().getSelectedShareHolder().setDisableEquityOwner(true);
				} else {
					getManagedBean().getSelectedShareHolder().setDisableManagement(false);
					getManagedBean().getSelectedShareHolder().setDisableEquityOwner(false);
				}
			}
			//#30444 END--25082021--
	   } else if (getManagedBean().getSmAppl().getSupplierType()
			.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
		   //#30444 START--25082021--
		   if (getManagedBean().getBusinessTypeVal()     
					.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)
					&& !alreadyHadEquityOwnerAsStaff(getManagedBean().getSmAppl(), 
				    				getManagedBean().getSelectedShareHolder().getPersonnelId())) {   		
					if (getManagedBean().getSelectedShareHolder().isClerical()
								&& !getManagedBean().getSelectedShareHolder().isEquityOwner()) {
						getManagedBean().getSelectedShareHolder().setProf(false);
						getManagedBean().getSelectedShareHolder().setManagement(false);
						getManagedBean().getSelectedShareHolder().setDisableProf(true);
						getManagedBean().getSelectedShareHolder().setDisableManagement(true);
					} else {
						//#30444 START--27032022--
						if(getManagedBean().getSelectedShareHolder().isSemiProf()){
							getManagedBean().getSelectedShareHolder().setDisableProf(true);
							getManagedBean().getSelectedShareHolder().setDisableManagement(true);	
						}else{
							getManagedBean().getSelectedShareHolder().setDisableProf(false);
							getManagedBean().getSelectedShareHolder().setDisableManagement(false);
						}
						//#30444 END--27032022--
					}
					if (getManagedBean().getSelectedShareHolder().isClerical()){
						getManagedBean().getSelectedShareHolder().setSemiProf(false);
						getManagedBean().getSelectedShareHolder().setDisableSemiProf(true);
		    		}else{
		    			if(getManagedBean().getSelectedShareHolder().isEquityOwner()
		    					||getManagedBean().getSelectedShareHolder().isProf()
		    					||getManagedBean().getSelectedShareHolder().isManagement()){
		    				getManagedBean().getSelectedShareHolder().setDisableSemiProf(true);
		    			} else {
		    			getManagedBean().getSelectedShareHolder().setDisableSemiProf(false);
		    			}
		    		}
		    		
		   } else {
			   if (getManagedBean().getSelectedShareHolder().isClerical()) {
					getManagedBean().getSelectedShareHolder().setProf(false);
					getManagedBean().getSelectedShareHolder().setManagement(false);
					getManagedBean().getSelectedShareHolder().setEquityOwner(false);
					getManagedBean().getSelectedShareHolder().setSemiProf(false);
					getManagedBean().getSelectedShareHolder().setHigherManagement(false); //#30444 --27032022--
					getManagedBean().getSelectedShareHolder().setDisableProf(true);
					getManagedBean().getSelectedShareHolder().setDisableManagement(true);
					getManagedBean().getSelectedShareHolder().setDisableEquityOwner(true);
					getManagedBean().getSelectedShareHolder().setDisableSemiProf(true);
			   }else{
				 //#30444 START--27032022--
				   if(getManagedBean().getSelectedShareHolder().isEquityOwner()
						   ||getManagedBean().getSelectedShareHolder().isDirector()
						   ||getManagedBean().getSelectedShareHolder().isManagement()
						   ||getManagedBean().getSelectedShareHolder().isHigherManagement()
						   ||getManagedBean().getSelectedShareHolder().isProf()){
					   getManagedBean().getSelectedShareHolder().setSemiProf(false);
					   getManagedBean().getSelectedShareHolder().setDisableSemiProf(true);				   
				   }else if(getManagedBean().getSelectedShareHolder().isSemiProf()){
					    getManagedBean().getSelectedShareHolder().setDisableProf(true);
						getManagedBean().getSelectedShareHolder().setDisableManagement(true);
						getManagedBean().getSelectedShareHolder().setDisableEquityOwner(true);	
				   }else{
					    getManagedBean().getSelectedShareHolder().setDisableProf(false);
						getManagedBean().getSelectedShareHolder().setDisableManagement(false);
						getManagedBean().getSelectedShareHolder().setDisableEquityOwner(false);	
						getManagedBean().getSelectedShareHolder().setDisableSemiProf(false);	   
				   }
			   //#30444 END--27032022--
			   }
		   }
	   }
	}
		
	//#30444 END--25082021--
    private static boolean alreadyHadEquityOwnerAsStaff(SmAppl appl, Long personnelId) { //#29666- add Long variable
		boolean retVal = false;
		for (SmPersonnel per : appl.getSmPersonnels()) {
			if (!per.getPersonnelId().equals(personnelId)){  //#29666- add condition
				if (per.isEquityOwner() && per.isClerical()) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}
    // BUG #28173 & #28315 -END
    
    //#37896 - START
    /**
	 * Verify selected recommend rejection reason
	 */
	public void verifySelectedRecommendRejectReasons() {
		getManagedBean().setRecommendRejectReasonReqd(isRecommendDpaOthersSelected());
		if (!getManagedBean().isRecommendRejectReasonReqd()) {
			//30974 - start
			if(getManagedBean().isPoUser()){
				getManagedBean().getPoSecReview().setRemarkToApprover(StringUtils.EMPTY);
			}else{
				getManagedBean().setApplnRejectionRemarks(StringUtils.EMPTY);
			}
			//30974 - end
		}
	}
	
	/**
	 * Return true or false whether there is Others selected in Delete Pending
	 * Application Reason
	 * 
	 * @return boolean
	 */
	private boolean isRecommendDpaOthersSelected() {
		boolean retVal = false;
		if (getManagedBean().getRecommendReasonId() != null
				&& getManagedBean().getRecommendReasonId().length > 0) {
			for (Long reasonId : getManagedBean().getRecommendReasonId()) {
				ReasonReqDTO reqDTO = new ReasonReqDTO();
				reqDTO.setReasonId(reasonId);
				PmReason reason = SmServiceLookup.getReasonSO()
						.retrieveReasonById(reqDTO).getPmReason();
				if (reason != null
						&& !StringUtils.isBlank(reason.getReasonCode())
						&& SupplierManagementConst.DPA_REASON_OTHERS_LIST
								.contains(reason.getReasonCode())) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

    /**
	 * On tick checkbox during search recommend rejection reason
	 * 
	 * @param SelectEvent
	 *            e
	 */
	//30974 - Start
	public void onTickOrUnTickRecommendReason(SelectEvent e) {
		getManagedBean().setRecommendRejectReasonReqd(false);
		
		PmReasonDesc reasonEvent = (PmReasonDesc) e.getObject();
		System.out.println(reasonEvent);
		
		List<PmReasonDesc> reasonIdList1 = new ArrayList<PmReasonDesc>();
		
		if (getManagedBean().getSelectedRecommendRejectReasonDescs() != null && getManagedBean().getSelectedRecommendRejectReasonDescs().size() > 0) {
			for (PmReasonDesc reason : getManagedBean().getSelectedRecommendRejectReasonDescs()) {
				reasonIdList1.add(reason);
			}
		}
		
		reasonIdList1.add(reasonEvent);
		getManagedBean().setSelectedRecommendRejectReasonDescs(reasonIdList1);
		
		//Comment to see data during submit
/*		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSelectedRecommendReasonDescs())) {
			List<Long> reasonIdList = new ArrayList<Long>();
			for (PmReasonDesc reason : getManagedBean()
					.getSelectedRecommendReasonDescs()) {
				reasonIdList.add(reason.getPmReason().getReasonId());
			}
			getManagedBean().setRecommendReasonId(
					reasonIdList.toArray(new Long[reasonIdList.size()]));
		}else{
			getManagedBean().setRecommendReasonId(null);
		}*/
		setReasonIdList();
		
		if(getManagedBean().isShowDifferenceRejection()){
			verifySelectedRejectReasons();
		}else{
			verifySelectedRecommendRejectReasons();
		}
		
	}
	//30974 - End

	//30974 - Start
	public void unTickRecommendReason(UnselectEvent e) {
		getManagedBean().setRecommendRejectReasonReqd(false);

		PmReasonDesc reasonEvent = (PmReasonDesc) e.getObject();

		getManagedBean().getSelectedRecommendRejectReasonDescs().remove(reasonEvent);

		setReasonIdList();
		
		if(getManagedBean().isShowDifferenceRejection()){
			verifySelectedRejectReasons();
		}else{
			verifySelectedRecommendRejectReasons();
		}
	}
	//30974 - End
	
	/**
	 * On tick checkbox during search recommend rejection reason
	 * 
	 * @param ToggleSelectEvent
	 *            e
	 */
	//30974 - Start
	public void allOnTickOrUnTickRecommendReason(ToggleSelectEvent e) {
		getManagedBean().setRecommendRejectReasonReqd(false);
		
		if(getManagedBean().isShowDifferenceRejection()){
			if(e.isSelected()){
				getManagedBean().setSelectedRecommendRejectReasonDescs(getManagedBean().getSelectedReasonDescs());
			}else{
				getManagedBean().setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
				getManagedBean().setSelectedRecommendRejectReasonDescs(new ArrayList<PmReasonDesc>());
			}
		}else{
			if(e.isSelected()){
				getManagedBean().setSelectedRecommendRejectReasonDescs(getManagedBean().getSelectedRecommendReasonDescs());
			}else{
				getManagedBean().setSelectedRecommendReasonDescs(new ArrayList<PmReasonDesc>());
				getManagedBean().setSelectedRecommendRejectReasonDescs(new ArrayList<PmReasonDesc>());
			}	
		}	
		
		setReasonIdList();
		if(getManagedBean().isShowDifferenceRejection()){
			verifySelectedRejectReasons();
		}else{
			verifySelectedRecommendRejectReasons();
		}
	}
	//30974 - End
	
	//#12483 - START
	public String currentReviseReason() {
		String currReason = null;
		Timestamp currDate = null;
		for (SmApplRevise smApplRevise : this.getManagedBean().getSmAppl().getSmApplRevises()) {
			if (currDate == null) {
				currDate = smApplRevise.getCreatedDate();
				currReason = smApplRevise.getReviseReason();
			} else {
				if (smApplRevise.getCreatedDate().after(currDate)) {
					currReason = smApplRevise.getReviseReason();
					currDate = smApplRevise.getCreatedDate();
				}
			}
		}

		return currReason;
	}
	//37896 - END
	//#12483 - END
	
	// 14530 - START
	private void sendEmailtoSupplier(ReviewApplicationReqDTO reviewAppDTO) {
		IReviewApplicationSO reviewAppSO = SmServiceLookup.getReviewApplicationSO();
		ReviewApplicationResDTO reviewAppResDTO = reviewAppSO.retrieveApplById(reviewAppDTO);
		SmAppl smappl = reviewAppResDTO.getSmAppl();

		if (smappl.getApplId() == reviewAppDTO.getApplId()) {
			try {
				log.info("***sendEmailPOInquirytoSupplpier - Start*****");
				reviewAppSO.sendEmailPOInquirytoSupplier(reviewAppDTO);
				log.info("***sendEmailPOInquirytoSupplpier - Start*****" + reviewAppDTO);
			} catch (Exception ex) {
				log.info("sendEmailPOEnquirytoSupplier ERROR :: " + ex.getMessage());
			}
		}
	}

	/**
	 * inquiry row select
	 */
	public void POinquiryRowSelect(SmApplInquiry applInquiry) {
		// #34392 - START
//		resetSmApplSupportDocInquiries(applInquiry);
		this.getManagedBean().setPoToSupplierInquiry(applInquiry);
		// #34392 - START
		getManagedBean().setMandatoryDocsInquiry(new ArrayList<SmApplSupportDocInquiry>());
		getManagedBean().setNonMandatoryDocsInquiry(new ArrayList<SmApplSupportDocInquiry>());
		for (SmApplSupportDocInquiry doc : applInquiry.getSmApplSupportDocInquiries()) {
			doc.setSmApplInquiry(applInquiry);
			doc.getSmApplInquiry().getSmAppl().getSupportingDocMode();
			if (doc.getPmDocument().isMandatory()) {
				getManagedBean().getMandatoryDocsInquiry().add(doc);
			} else if (!doc.getPmDocument().isMandatory()) {
				getManagedBean().getNonMandatoryDocsInquiry().add(doc);
			}

		}

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getMandatoryDocsInquiry())) {
			Collections.sort(getManagedBean().getMandatoryDocsInquiry(), new Comparator<SmApplSupportDocInquiry>() {
				@Override
				public int compare(SmApplSupportDocInquiry doc1, SmApplSupportDocInquiry doc2) {
					if (doc1.getPmDocument().getDisplaySeq() == null) {
						return (doc2.getPmDocument().getDisplaySeq() == null) ? 0 : 1;
					}
					if (doc2.getPmDocument().getDisplaySeq() == null) {
						return -1;
					}
					return Integer.valueOf(doc1.getPmDocument().getDisplaySeq().compareTo(doc2.getPmDocument().getDisplaySeq()));
				}
			});
		}

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getNonMandatoryDocsInquiry())) {
			Collections.sort(getManagedBean().getNonMandatoryDocsInquiry(), new Comparator<SmApplSupportDocInquiry>() {
				@Override
				public int compare(SmApplSupportDocInquiry doc1, SmApplSupportDocInquiry doc2) {
					if (doc1.getPmDocument().getDisplaySeq() == null) {
						return (doc2.getPmDocument().getDisplaySeq() == null) ? 0 : 1;
					}
					if (doc2.getPmDocument().getDisplaySeq() == null) {
						return -1;
					}
					return Integer.valueOf(doc1.getPmDocument().getDisplaySeq().compareTo(doc2.getPmDocument().getDisplaySeq()));
				}
			});
		}

		// #34392 - End
		RequestContext.getCurrentInstance().update("inquiryDetails");
	}
	// 14530 - END
	
	// #34392 - start
	/**
	 * re-select inquiry before opening popup
	 * @param applInquiry 
	 */
	public void resetSmApplSupportDocInquiries(SmApplInquiry applInquiry) {
		ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);
		SupportingDocInquiryResDTO resDTO = reqSO.retrieveInquiryByApplInquiryId(applInquiry.getApplInquiryId());
		applInquiry.setSmApplSupportDocInquiries(resDTO.getSmApplInquiry().getSmApplSupportDocInquiries());
	}

	/**
	 * re-select doc file before opening popup
	 * 
	 * @param applInquiry
	 */
	public List<SmApplInquiryDocFile> reselectDocFileValue(long applSupportDocInquiryId) {
		ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);

		SupportingDocInquiryResDTO resDTO = reqSO.retrieveInquiryFileByApplSupportDocId(applSupportDocInquiryId);

		return resDTO.getSmApplInquiryDocFileList();
	}
	// #34392 - end
	
	//12492 - START
	public void CheckBankAccountInfoForm(String fromPage) {

		HashSet<String> hsetAccountNo = new HashSet<String>();
		HashSet<Long> hsetAccountName = new HashSet<Long>();

		//boolean bankInfo = false;

		if (getManagedBean().isShowDifferenceBankInfoAction()) {
			if (fromPage.equalsIgnoreCase("fromBankInfo")) {
				if (this.getManagedBean().getSmAppl().getSmSupplierBanks().size() > 0) {
					for (SmSupplierBank bankName : this.getManagedBean().getSmAppl().getSmSupplierBanks()) {
						if (bankName.getPmFinancialOrg().getFinancialOrgId() != null) {
							hsetAccountName.add(bankName.getPmFinancialOrg().getFinancialOrgId());
							hsetAccountNo.add(bankName.getAccountNo());
						}
					}
				}

				if (hsetAccountName.add(getManagedBean().getSelectedBank().getPmFinancialOrg().getFinancialOrgId()) == false) {
					if (hsetAccountNo.add(getManagedBean().getSelectedBank().getAccountNo()) == false) {
						RequestContext.getCurrentInstance().execute("bankDialogDuplicate.show()");
					}
				}
			}
		} else {
			for (SmSupplierBank supBank : getManagedBean().getSmAppl().getSmSupplierBanks()) {
				if (getManagedBean().getSelectedBank().getSupplierBankId() != null 
						&& getManagedBean().getSelectedBank().getSupplierBankId().equals(supBank.getSupplierBankId())) {
					log.debug("**** Same Bank object edit with exisiting id ******");
				} else {
					if (getManagedBean().getSelectedBank().getAccountNo().equalsIgnoreCase(supBank.getAccountNo())) {
						//bankInfo = true;
						RequestContext.getCurrentInstance().execute("bankDialogDuplicate.show()");
					}
				}
			}
		}
	}
	//12492 - END
	
	public boolean checkSpecialCat(SmAppl smAppl){
		if(!smAppl.getSmSupplierCategories().isEmpty()){
			if(smAppl.getSmSupplierCategories().get(0).isSpecialCategory()){
				return true;
			}
		}
		return false;
	}
	//#12483 - END
	
	//#11641 - START
	public void inquiryRowSelect() {
		//#11641 - START
		this.getManagedBean().setEmptyMandatoryDocHistory(true);
		this.getManagedBean().setEmptyNonMandatoryDocHistory(true);
		for (SmApplSupportDocInquiry historySupportDoc : this.getManagedBean().getSelectedInquiryResponse().getSmApplSupportDocInquiries()) {
			if (historySupportDoc.getPmDocument().isMandatory()) {
				this.getManagedBean().setEmptyMandatoryDocHistory(false);
			} else if (!historySupportDoc.getPmDocument().isMandatory()) {
				this.getManagedBean().setEmptyNonMandatoryDocHistory(false);
			}
		}
		//#11641 - END

		// #34392 - Start
		if (!EntityHelper.isCollectionEmpty(this.getManagedBean().getSelectedInquiryResponse().getSmApplSupportDocInquiries())) {
			Collections.sort(this.getManagedBean().getSelectedInquiryResponse().getSmApplSupportDocInquiries(), new Comparator<SmApplSupportDocInquiry>() {
				@Override
				public int compare(SmApplSupportDocInquiry doc1, SmApplSupportDocInquiry doc2) {
					if (doc1.getPmDocument().getDisplaySeq() == null) {
						return (doc2.getPmDocument().getDisplaySeq() == null) ? 0 : 1;
					}
					if (doc2.getPmDocument().getDisplaySeq() == null) {
						return -1;
					}
					return Integer.valueOf(doc1.getPmDocument().getDisplaySeq().compareTo(doc2.getPmDocument().getDisplaySeq()));
				}
			});
		}
		// #34392 - END
		RequestContext.getCurrentInstance().update("resOutPanelId");

	}
	
	public int getMandatoryCounter() {
		return mandatoryCounter++;
	}

	public int getNonMandatoryCounter() {
		return nonMandatoryCounter++;
	}
	//#11641 - END
	
	//#33240 - start
	/**
	 * Delete file for license certificate information
	 */
	public void onDeleteMandatoryLicenseFile(SmApplDocFile doc, int index) {

		this.getManagedBean().setSupportDoc(doc.getSmApplSupportDoc());
		this.getManagedBean().setAttachedFileIndex(index);
		this.getManagedBean().setSmApplDocFile(doc);

		RequestContext.getCurrentInstance().execute("confirmDeleteLicenseCertDialog.show();");
	}
	
	public void deleteMandatoryLicenseFile() {
		for (SmApplSupportDoc doc : getManagedBean().getSmAppl().getSmApplSupportDocs()) {
			if (doc.getPmDocument() != null) {
				if (doc.getPmDocument().isMandatory()) {
					if (doc.getPmDocument().isLicenseReq() && !doc.getPmDocument().getPmDocumentRestrs().isEmpty()) {
						if (doc.getSmApplDocFiles().contains(this.getManagedBean().getSmApplDocFile())) {
							doc.getSmApplDocFiles().remove(this.getManagedBean().getSmApplDocFile());
							this.getManagedBean().setSmApplDocFile(null);
							break;
						}
					}
				}
			}
		}
	}
	//#33240 - end
	
	//#32014 - START
	//reassign the address since the first time assigning the data is not displayed/populate
	public void reassignSelShareHolderResAddress(SmPersonnelAddress personnelAddress) {
		//log.info("========#32014 - inside reassignSelShareHolderAddress=======");
		//log.info("#32014 - isSupplierUser : " + getManagedBean().isSupplierUser());
		//log.info("#32014 - AddressType : " + personnelAddress.getAddressType());
		if (getManagedBean().isSupplierUser() && personnelAddress.getAddressType().equalsIgnoreCase(SupplierManagementConst.RES_ADD)) {
			//log.info("#32014 - inside first if");
			//log.info("#32014 - isSameAsPerm : " + personnelAddress.isSameAsPerm());
			//log.info("#32014 - SSM Application : " + getManagedBean().getSmAppl().isApplValidWithSsm());
			//log.info("#32014 - SSM isActive : " + SmCommonUtil.getInstance().ssmActive());
			if (!personnelAddress.isSameAsPerm() && getManagedBean().getSmAppl().isApplValidWithSsm() && SmCommonUtil.getInstance().ssmActive()) {
				SmPersonnelAddress res = new SmPersonnelAddress();
				res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
				res.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
				res.setSmPersonnel(this.getManagedBean().getSelectedShareHolder());
				getManagedBean().setShareHolderResdAddr(res);

				for (SmPersonnelAddress addr : this.getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()) {
					if (addr.getAddressType().equalsIgnoreCase(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
						SmAddress smAddress = new SmAddress();
						smAddress.setAddress1(addr.getSmAddress().getAddress1());
						smAddress.setAddress2(addr.getSmAddress().getAddress2());
						smAddress.setAddress3(addr.getSmAddress().getAddress3());
						//START SSM INTEGRATION
						smAddress.setSsmData(addr.getSmAddress().isSsmData());
						smAddress.setAddressSsmNull(addr.getSmAddress().isAddressSsmNull());
						smAddress.setPostcodeSsmNull(addr.getSmAddress().isPostcodeSsmNull());
						smAddress.setStateSsmNull(addr.getSmAddress().isStateSsmNull());
						smAddress.setCountrySsmNull(addr.getSmAddress().isCountrySsmNull());

						if (addr.getSmAddress().getPmCountry() != null) {
							smAddress.setPmCountry(addr.getSmAddress().getPmCountry());
							smAddress.setCountryId(addr.getSmAddress().getPmCountry().getCountryId());
						} else {
							if (addr.getSmAddress().getPmState() != null) {
								PmCountry country = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
								smAddress.setPmCountry(country);
								smAddress.setCountryId(country.getCountryId());
							}
						}

						PmState state = new PmState();
						//#34799 - start
						if (personnelAddress.getSmPersonnel().getIdentificationType() != null 
								&& personnelAddress.getSmPersonnel().getIdentificationType().getParameterCode().equalsIgnoreCase(ParameterConst.IDENTIFICATION_TYPES_IC)
								&& addr.getSmAddress().getPmState() != null) {
							state.setStateId(addr.getSmAddress().getPmState().getStateId());
						}
						if (personnelAddress.getSmPersonnel().getIdentificationType() != null 
								&& personnelAddress.getSmPersonnel().getIdentificationType().getParameterCode().equalsIgnoreCase(ParameterConst.IDENTIFICATION_TYPES_PASSPORT)) {
							if (smAddress.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) && addr.getSmAddress().getPmState() != null) {
								state.setStateId(addr.getSmAddress().getPmState().getStateId());
							} else if (addr.getSmAddress().getPmDistrict() != null && addr.getSmAddress().getPmDistrict().getPmState() != null) {
								state.setStateId(addr.getSmAddress().getPmDistrict().getPmState().getStateId());
							}
							//#34799 - end
						}

						smAddress.setPmState(state);
						smAddress.setStateId(state.getStateId());

						if (addr.getSmAddress().getPmDivision() != null) {
							PmDivision div = new PmDivision();
							div.setDivisionId(addr.getSmAddress().getPmDivision().getDivisionId());
							smAddress.setPmDivision(div);
							smAddress.setDivisionId(div.getDivisionId());
						}
						if (addr.getSmAddress().getPmDistrict() != null) {
							PmDistrict district = new PmDistrict();
							district.setDistrictId(addr.getSmAddress().getPmDistrict().getDistrictId());
							smAddress.setPmDistrict(district);
							smAddress.setDistrictId(district.getDistrictId());
						}

						if (addr.getSmAddress().getPmCity() != null) {
							PmCity city = new PmCity();
							city.setCityId(addr.getSmAddress().getPmCity().getCityId());
							smAddress.setPmCity(city);
							smAddress.setCityId(city.getCityId());
						}

						smAddress.setPostcode(addr.getSmAddress().getPostcode());
						addr.setSmAddress(smAddress);

						if (addr.getAddressType().equalsIgnoreCase(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
							getManagedBean().setShareHolderResdAddr(addr);
							SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(getManagedBean(), 
									getManagedBean().getShareHolderResdAddr().getSmAddress(), SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
							//to check, if district state not equal to pm state set district to null & City
							if (getManagedBean().getShareHolderResdAddr().getSmAddress().isSsmData()) {
								SmAddress address = getManagedBean().getShareHolderResdAddr().getSmAddress();
								if (address != null) {
									if (address.getPmState() != null && address.getPmDistrict() != null) {
										if (address.getPmDistrict().getPmState() != null) {
											if (address.getPmDistrict().getPmState().getStateId() != address.getPmState().getStateId()) {
												address.setPmDistrict(new PmDistrict());
												address.setPmCity(new PmCity());
											}

										}
									}
								}
							}
						}
					}
				}
				getManagedBean().setSelectedShareHolder(this.getManagedBean().getSelectedShareHolder());
			}
		}
	}
	//#32014 - END
	
	//#30766 & #34209 - START
	/*
	private List<SmSupplierCategory> setPOorApprDec(List<SmSupplierCategory> catList) {
		
		for(SmSupplierCategory smSupplierCategory : catList){
			if (getManagedBean().isPoUser()) {
				smSupplierCategory.setPoDecFlg(0);
			} else if (getManagedBean().isApproverUser()) {
				smSupplierCategory.setApprvrDecFlg(0);
			}
		}
		return catList;
	}
	*/
	
	public String dynamicTableColumnWidth(String supplierUser, String poUser, String approverUser) {
		if (getManagedBean().isSupplierUser()) {
			return supplierUser;
		} else if (getManagedBean().isPoUser()) {
			return poUser;
		} else if (getManagedBean().isApproverUser()) {
			return approverUser;
		} else {
			return "auto";
		}
	}
	//#30766 & #34209 - END	
	
	// #34166 - Start
	public void validateText() {
		if (!getManagedBean().isPoUser()) {
			if (!getManagedBean().isApproverUser()) {
				if (getManagedBean().getSmAppl() != null) {
					if (!(getManagedBean().getSmAppl().getSmSupplierBanks().isEmpty())) {
						for (SmSupplierBank banks : getManagedBean().getSmAppl().getSmSupplierBanks()) {
							//#35635 - start
							if ((banks.getSignatureInstruction() != null) && (banks.getSignatureInstruction().contains("<") 
									|| banks.getSignatureInstruction().contains(">"))) {
								StringBuilder sb = new StringBuilder();
								Locale locale = new FacesScopes().getUserLocale();
								sb.append(ResourceBundleUtils.getMessage("lbl.sm.bankDtls.chequesign.tnc", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
								throw new EpException(MessageConst.ERROR_775191, new String[] { sb.toString().toLowerCase() });
							}
							//#35635 - end
						}
					}
				}
			}
		}
	}
	// #34166 - End
	
	//#12483 - START
	public boolean renderRevisedReason(){
		if(currentReviseReason() != null){
			if(!this.getManagedBean().getCurrentStatus().equals("Pending Review Site Visit Report - After Approval") 
					&& !this.getManagedBean().getCurrentStatus().equals("Menunggu Laporan Lawatan Tapak - Selepas Diluluskan")){
				return true;
			}
		}
		return false;
	}
	//#12483 - END
	
	//#34135 START - Commit half part from Redmine 29727 into pds
	public void setBranchDisplayTable() {
		List<SmSupplierBranch> displayBranches = new ArrayList<SmSupplierBranch>();
		if (getManagedBean().getSmAppl().getSmSupplierBranches().size() > 0) {
			for (SmSupplierBranch smSupplierBranch : getManagedBean().getSmAppl().getSmSupplierBranches()) {
				if (smSupplierBranch.getRecordStatusFlg() != SupplierManagementConst.RECORD_DELETED) {
					continue;
				}
				displayBranches.add(smSupplierBranch);
			}
		}
		getManagedBean().setDisplayBranches(displayBranches);
	}
	//#34135 END
	
	//#34799 - start
	public void onTickSameAsAddress(SmAddress srcAddr, SmPersonnelAddress destAddr, String componentId, boolean isTick) {
		if (isTick) {
			UIComponent comp = org.primefaces.util.ComponentUtils.findComponent(FacesContext.getCurrentInstance().getViewRoot(), componentId);
			if (comp != null) {
				resetEditableValue(comp);
			}
		}
		onTickSameAsAddress(srcAddr, destAddr);
	}
	
	private void resetEditableValue(UIComponent comp) {
		if (comp instanceof EditableValueHolder) {
			EditableValueHolder val = (EditableValueHolder) comp;
			val.resetValue();
		}
		for (Iterator<UIComponent> it = comp.getFacetsAndChildren(); it.hasNext();) {
			resetEditableValue(it.next());
		}
	}
	//#34799 - end
	
	//#32008, #35086 - start
	public String getSelectedBranchBank(SmSupplierBank bank) {
		//#35086 - end
		if (bank == null || SmCommonUtil.isCollectionEmpty(bank.getSmSupplierBranchBanks())) {
			return null;
		}
		if (bank.getSmSupplierBranchBanks().size() == 1) {
			return bank.getSmSupplierBranchBanks().get(0).getSmSupplierBranch().getBranchName();
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (SmSupplierBranchBank brTmp : bank.getSmSupplierBranchBanks()) {
			sb.append(++i + ") " + brTmp.getSmSupplierBranch().getBranchName() + "<br/>");
		}
		return sb.toString();
	}
	
	//#35086 - start
	public String getSelectedSignatoryBank(SmSupplierBank bank) {
		//#35086 - end
		if (bank == null || SmCommonUtil.isCollectionEmpty(bank.getSmChequeSignatories())) {
			return null;
		}
		if (bank.getSmChequeSignatories().size() == 1) {
			return bank.getSmChequeSignatories().get(0).getSmPersonnel().getName();
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (SmChequeSignatory sign : bank.getSmChequeSignatories()) {
			sb.append(++i + ") " + sign.getSmPersonnel().getName() + "<br/>");
		}
		return sb.toString();
	}
	//#32008 - END
	
	//#28175 - start
	/**
	 * @param cat
	 * @return latest or old category code description
	 */
	public String getCompanyExpCatName(PmCategoryL1 cat) {
		if (cat == null) {
			return null;
		}
		if (getManagedBean().getSmAppl().isActiveAppl() 
				|| EntityHelper.isCollectionEmpty(getManagedBean().getSelectedCompanyExperience().getSmCompanyExperienceCats())) {
			return cat.getCategoryName();
		}
		SmCompanyExperienceCat expCat = (SmCompanyExperienceCat) EntityHelper.getEntityBySinglePropertyFromList(cat.getCategoryL1Id(), 
				getManagedBean().getSelectedCompanyExperience().getSmCompanyExperienceCats(), "pmCategoryL1");

		return expCat != null ? expCat.getCategoryL1Name() : cat.getCategoryName();
	}
	
	/**
	 * @param cat
	 * @return latest or old category code description
	 */
	public String getCompanyExpCatName(PmCategoryL2 cat) {
		if (cat == null) {
			return null;
		}
		if (getManagedBean().getSmAppl().isActiveAppl() 
				|| EntityHelper.isCollectionEmpty(getManagedBean().getSelectedCompanyExperience().getSmCompanyExperienceCats())) {
			return cat.getCategoryName();
		}
		SmCompanyExperienceCat expCat = (SmCompanyExperienceCat) EntityHelper.getEntityBySinglePropertyFromList(cat.getCategoryL2Id(), 
				getManagedBean().getSelectedCompanyExperience().getSmCompanyExperienceCats(), "pmCategoryL2");

		return expCat != null ? expCat.getCategoryL2Name() : cat.getCategoryName();
	}
	
	/**
	 * @param cat
	 * @return latest or old category code description
	 */
	public String getCompanyExpCatName(PmCategoryL3 cat) {
		if (cat == null) {
			return null;
		}
		if (getManagedBean().getSmAppl().isActiveAppl() 
				|| EntityHelper.isCollectionEmpty(getManagedBean().getSelectedCompanyExperience().getSmCompanyExperienceCats())) {
			return cat.getCategoryName();
		}
		SmCompanyExperienceCat expCat = (SmCompanyExperienceCat) EntityHelper.getEntityBySinglePropertyFromList(cat.getCategoryL3Id(), 
				getManagedBean().getSelectedCompanyExperience().getSmCompanyExperienceCats(), "pmCategoryL3");

		return expCat != null ? expCat.getCategoryL3Name() : cat.getCategoryName();
	}
	
	private void updateCategoryCodeNames() {
		if (!getManagedBean().getSmAppl().isActiveAppl()) {
			return;
		}
		SmAppl appl = getManagedBean().getSmAppl();
		if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				cat.setCategoryL1Name(cat.getPmCategoryL1().getCategoryName());
				cat.setCategoryL2Name(cat.getPmCategoryL2().getCategoryName());
				cat.setCategoryL3Name(cat.getPmCategoryL3().getCategoryName());
			}
		}
		if (!EntityHelper.isCollectionEmpty(appl.getSmCompanyExperiences())) {
			for (SmCompanyExperience exp : appl.getSmCompanyExperiences()) {
				updateCompanyExpCatNames(exp);
			}
		}
	}
	
	private void updateCompanyExpCatNames(SmCompanyExperience exp) {
		if (EntityHelper.isCollectionEmpty(exp.getSmCompanyExperienceCats())) {
			return;
		}
		for (SmCompanyExperienceCat cat : exp.getSmCompanyExperienceCats()) {
			cat.setCategoryL1Name(cat.getPmCategoryL1().getCategoryName());
			cat.setCategoryL2Name(cat.getPmCategoryL2().getCategoryName());
			cat.setCategoryL3Name(cat.getPmCategoryL3().getCategoryName());
		}
	}
	//#28175 - end
	
	//#35145 - start
	/**
	 * Race field is editable for non-SSM data & Lain2 Bangsa
	 * @return
	 */
	public boolean isRaceDisabled() {
		SmPersonnel per = getManagedBean().getSelectedShareHolder();
		if (per.getPmRace() == null || per.getPmRace().getRaceId() == null || per.isOtherRaceSsm()) {
			return false;
		}
		return disableFieldSSm(per.isSsmData(), getManagedBean().getSmAppl().isApplValidWithSsm(), per.isRaceSsmNull());
	}
	
	private String[] getEquityOwnerField(boolean isROB){
		if(isROB){
			return new String[]{"name","birthDate","nameInSsm","raceSsmNull","birthDateSsmNull",
					"designationSsmNull","equityOwner","director","pmCountry","directorSsm",
					"equityOwnerSsm","managementSsm","pmRace", "otherRaceSsm","permanentResident"};//35300
		}
		return new String[]{"name","birthDate","designation","shareAmt","nameInSsm","raceSsmNull",
				"birthDateSsmNull","shareAmtSsmNull","shareAmtSsm","designationSsmNull",
				"equityOwner","director","directorSsm","equityOwnerSsm","managementSsm", "otherRaceSsm","permanentResident"};//35300
	}
	//#35145 - end
	
	//#31386 - START
	public String finalCancelDecisionApproveOrRejectAppln(String dialog, String decision) {
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
		return submitPOorApproverReviewToBPM(decision);
	}
	//31386 - END
	
	//29380 - Site Visit Grouping Enhancement -28775 - start
	/**
	 * @param catL3
	 * @return other description for category code
	 */
	public String getCategoryCodeOtherDesc(PmCategoryL3 catL3){
		return SmApplicationUtil.getCategoryCodeOtherDesc(
				getManagedBean().getSmAppl().getSmSupplierCategories(), catL3);
	}
	//29380 - Site Visit Grouping Enhancement -28775 - end
	//#31043 START
	private boolean shouldSetApproverVerifiedFlg(SmApplSupportDoc applSuppDoc) {
	    return applSuppDoc.getVerifiedFlg() != null && applSuppDoc.getApproverVerifiedFlg() == null;
	}

	private void updateApproverVerifiedFlgFromDocs(SmApplSupportDoc applSuppDoc, Map<Long, SmApplSupportDoc> allDocsMap) {
	    SmApplSupportDoc matchedDoc = allDocsMap.get(applSuppDoc.getApplSupportDocId());
	    if (matchedDoc != null) {
	        applSuppDoc.setApproverVerifiedFlg(matchedDoc.getApproverVerifiedFlg());
	    }
	}

	private Map<Long, SmApplSupportDoc> createDocsMap() {
	    Map<Long, SmApplSupportDoc> allDocsMap = new HashMap<>();

	    // Add all mandatory docs to the map
	    if (!EntityHelper.isCollectionEmpty(getManagedBean().getMandatoryDocs())) {
	    	for (SmApplSupportDoc doc : getManagedBean().getMandatoryDocs()) {
		        allDocsMap.put(doc.getApplSupportDocId(), doc);
		    }
	    }

	    // Add all non-mandatory docs to the map
	    if (!EntityHelper.isCollectionEmpty(getManagedBean().getNonMandatoryDocs())) {
	    	for (SmApplSupportDoc doc : getManagedBean().getNonMandatoryDocs()) {
		        allDocsMap.put(doc.getApplSupportDocId(), doc);
		    }
	    }

	    // Add all license mandatory docs to the map
	    if (!EntityHelper.isCollectionEmpty(getManagedBean().getMandatoryLicenseDocs())) {
	        for (SmLicenseInfoVO docVO : getManagedBean().getMandatoryLicenseDocs()) {
	            for (SmApplSupportDoc doc : docVO.getLicenseSuppDocs()) {
	                allDocsMap.put(doc.getApplSupportDocId(), doc);
	            }
	        }
	    }

	    return allDocsMap;
	}
	//#31043 END
	
	//#36474 - Start
	private SmWorkflowStatus rejectedApplExist(Long supplierId){
		SmWorkflowStatus wf = null;
		ISmWorkFlowStatusSO workFlowStatusSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		
		reqDTO.setStatusIds(new ArrayList<Long>());
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_PO_REJECTED_APPLICATION);
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_APPROVER_REJECTED_APPLICATION);
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYS);
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYS_SDO_INQUIRY);
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYS_PO_INQUIRY);
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_AUTOREJECT_UNSUBMIT_OFFLINE_DOCUMENT);
		reqDTO.setSupplierId(supplierId);
		
		SmWorkFlowStatusResDTO resDTO = workFlowStatusSO.retrieveWorkFlowBySuppIdAndStatusIds(reqDTO);
		
		if(resDTO.getWorkFlowStatusLs().size() > 0){
			wf = resDTO.getWorkFlowStatusLs().get(0);
		}
		
		return wf;
	}
	//#36474 - End
	
	//30974 && 37896 - Start
	public void setReasonIdList() {
		List<Long> reasonIdList = new ArrayList<Long>();

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSelectedRecommendRejectReasonDescs())) {
			for (PmReasonDesc reason : getManagedBean().getSelectedRecommendRejectReasonDescs()) {
				reasonIdList.add(reason.getPmReason().getReasonId());
			}

			if (getManagedBean().isShowDifferenceRejection()) {
				getManagedBean().setReasonId(reasonIdList.toArray(new Long[reasonIdList.size()]));
			} else {
				getManagedBean().setRecommendReasonId(reasonIdList.toArray(new Long[reasonIdList.size()]));
			}
		} else {
			getManagedBean().setReasonId(null);
			getManagedBean().setRecommendReasonId(null);
		}
	}
	//30974 && 37896 - End
	
	//#36587 START
	/**
	 * To auto-populate po remark to approver remark
	 * when po reject the category
	 * @param cat
	 * @return void
	 */
	private void setApproverRemark(SmSupplierCategory cat){
		if(cat.getPoDecFlg().equals(0)){
			cat.setApproverRemarks(cat.getPoRemarks() != null ? cat.getPoRemarks() : null);
		}
	}
	//#36587 END
	
	//#36594 - Start
	/**
	 * Set terms and condition message with parameter.
	 * 
	 * @return String
	 */
	public String setRenewalTnc(String applicationType){
		int renewalPeriod = SmServiceLookup.getSmRuleSO().retrieveRenewalPeriodForAppl(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		
		String word = NumberWordUtil.changeNumberToWord(renewalPeriod, new FacesScopes().getUserLocale());
		String label = "";
		
		if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)){
			label = "lbl.sm.renewMof.descData";
		}else if(applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
			label = "lbl.sm.upgradeMof.descData";
		}
		
		return ResourceBundleUtils.getMessage(
				label, new String[] {word , String.valueOf(renewalPeriod)},
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());		
	}
	//#36594 - End
	//#38195 START
	/**
	 * This method will compare between old and latest BPM version 
	 *            .
	 * @return boolean.
	 */
	public boolean checkWIPVersion() {
		ICompositeValiditySO compositeSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICompositeValiditySO.class);

		CompositeValidityReqDTO cReqDTO = new CompositeValidityReqDTO();
		cReqDTO.setCompositeValidityId(SupplierManagementConst.COMPOSITE_VALIDITY_ID_SM_DEL_PEN);

		String minVersion = compositeSO.getCompositeByValidityId(cReqDTO).getPmCompositeValidity()
				.getMinCompositeVersion();
		String version = this.getManagedBean().getBpmVersionNo();

		return isOldVersion(minVersion, version);
	}

	/**
	 * Method to check if bpm is old version will return true
	 * 
	 * @param Long workFlowStatus, String minVersion, String version
	 *            .
	 * @return boolean.
	 */
	private boolean isOldVersion(String minVersion, String version) {
		boolean isOldVersion = StringUtils.isNotBlank(minVersion) && StringUtils.isNotBlank(version)
				&& CustomUtils.compositeVersionComparator(version, minVersion) < 0;
		if (isOldVersion) {
			return true;
		}
		return false;
	}
	//#38195 END
}
