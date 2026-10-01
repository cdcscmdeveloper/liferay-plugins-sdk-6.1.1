package my.ep.web.backingbean.au;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.au.IAuditPatchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.au.AuditPatchConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.au.AuditPatchReqDTO;
import my.ep.dto.au.VAuditPatchDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.au.AuditPatchSearchBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.managedbean.au.AuditPatchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.rpt.excel.ExcelCellStyleBuilder;

/**
 * #35740
 * 
 * @author shaqimi
 * 
 */
@ManagedBean(name = "auditPatchBackingBean")
@ViewScoped
public class AuditPatchBackingBean extends BaseBackingBean<AuditPatchManagedBean> {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditPatchBackingBean.class);
	private transient IPagingDataModel<VAuditPatchDTO> searchMaster = new SearchMasterPagingDataModel();
	private transient IPagingDataModel<VAuditPatchDTO> searchDetail = new SearchDetailPagingDataModel();
	
	private List<VAuditPatchDTO> documentTypeList;
	
	public static final String NAV_TO_SEARCH = "auditPatchMain";
	public static final String NAV_TO_MAINTAIN = "auditPatchDetailMain";
	
	public static final boolean SEARCH_DEFAULT = false;
	
	private transient Workbook wb;
	private List<VAuditPatchDTO> details;

	private String roleCode;

	AuditPatchReqDTO auditPatchReqDTO = new AuditPatchReqDTO();

	public AuditPatchBackingBean() {
		
	}

	/**
	 * this method resets data
	 * this method also initializes data on first load
	 */
	public void preLoading() {
		this.resetSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	/**
	 * this method returns master data for search
	 */
	public void searchMaster() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getSearchBean().setSearching(true);
		
		if(null == this.getManagedBean().getSearchBean().getChangeTypeID()){ 
			throw new EpException(MessageConst.ERROR_880003);
		}
		
		if(this.getManagedBean().getSearchBean().getChangeTypeID().equals(AuditPatchConst.CHANGE_TYPE_EKONTRAK)){
			checkEKontrakValidation();
		} else {
			checkValidation();
		}
		
		this.getManagedBean().getSearchBean().setChangeTypeIDOldVal(this.getManagedBean().getSearchBean().getChangeTypeID());
		this.getManagedBean().getSearchBean().setDocTypeIDOldVal(this.getManagedBean().getSearchBean().getDocTypeID());
		this.getManagedBean().getSearchBean().setDocNoOldVal(this.getManagedBean().getSearchBean().getDocNo());
		this.getManagedBean().getSearchBean().seteAduanNoOldVal(this.getManagedBean().getSearchBean().geteAduanNo());
		
		searchMaster.reloadData();
	}

	/**
	 * This method checks data validation for change type of user request/system errors
	 */
	public void checkValidation(){
		if(this.getManagedBean().getSearchBean().getDocNo().isEmpty() && this.getManagedBean().getSearchBean().geteAduanNo().isEmpty()){
			throw new EpException(MessageConst.ERROR_880002);
		}
	}
	
	/**
	 * This method checks data validation for change type of eKontrak (Lampiran C)
	 */
	public void checkEKontrakValidation(){
		if(this.getManagedBean().getSearchBean().getDocNo().isEmpty()){
			throw new EpException(MessageConst.ERROR_880002);
		}
	}

	/**
	 * this method loads detail data for selected search data
	 */
	public void searchDetail() {
		this.getManagedBean().setLazy(true);
		searchDetail.reloadData();
	}

	/**
	 * method to return to the previous page                                                                 
	 */
	public String backToSearch() {
		getManagedBean().setSelectedType(null);
		getManagedBean().getSearchBean().setSearching(false);
		
		getManagedBean().getSearchBean().setChangeTypeID(getManagedBean().getSearchBean().getChangeTypeIDOldVal());
		getManagedBean().getSearchBean().setDocTypeID(getManagedBean().getSearchBean().getDocTypeIDOldVal());
		getManagedBean().getSearchBean().setDocNo(getManagedBean().getSearchBean().getDocNoOldVal());
		getManagedBean().getSearchBean().seteAduanNo(getManagedBean().getSearchBean().geteAduanNoOldVal());
		
		return NAV_TO_SEARCH;
	}

	/**
	 * To redirect Audit Trail Details Page.
	 * @param changeTypeID
	 * @param eAduanNo
	 * @param docTypeID
	 * @param docNo
	 * @return
	 */
	public String redirectToDetails(Integer changeTypeID, String eAduanNo, Integer docTypeID, String docNo) {
		getManagedBean().getSearchBean().setChangeTypeID(changeTypeID);
		getManagedBean().getSearchBean().seteAduanNo(eAduanNo);
		getManagedBean().getSearchBean().setDocTypeID(docTypeID);
		getManagedBean().getSearchBean().setDocNo(docNo);
		getManagedBean().getSearchBean().setpType(AuditPatchConst.APP_TYPE);

		return NAV_TO_MAINTAIN;
	}

	/**
	 * @param search
	 * @return
	 */
	public boolean searching(boolean search) {
		getManagedBean().getSearchBean().setSearching(search);
		return getManagedBean().getSearchBean().isSearching();
	}

	/**
	 * Retrieve Dropdown Change Type
	 * 
	 */
	public List<VAuditPatchDTO> getChangeTypeList() {
		IAuditPatchSO auditPatchSO;
		
		documentTypeList = getManagedBean().getSearchBean().getChangeTypeID() != null ? 
				getDocumentTypeList(getManagedBean().getSearchBean().getChangeTypeID()) : null;
				
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPatchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditPatchSO.class);
		return auditPatchSO.getChangeTypeList(languageCode);
	}
	
	/**
	 * Retrieve Dropdown Document Type
	 * 
	 */
	public List<VAuditPatchDTO> getDocumentTypeList(Integer changeTypeID) {
		IAuditPatchSO auditPatchSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPatchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditPatchSO.class);
		return auditPatchSO.getDocumentTypeList(languageCode, changeTypeID);
	}

	/**
	 * search Audit Patch Result
	 * 
	 */
	private class SearchMasterPagingDataModel extends PagingDataModel<VAuditPatchDTO> {

		private static final long serialVersionUID = 1L;

		public SearchMasterPagingDataModel() {

			super("searchPatchMaster", getManagedBean().isLazy());

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VAuditPatchDTO> pageChangeListener(ISearchBean<VAuditPatchDTO> searchBean) {
			IAuditPatchSO auditPatchSO;

			try {

				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				auditPatchReqDTO.setpLanguage(languageCode);
				AuditPatchSearchBean lSearchBean = getManagedBean().getSearchBean();
				if (lSearchBean.isSearching()) {
					auditPatchReqDTO.setChangeTypeID(lSearchBean.getChangeTypeID());
					auditPatchReqDTO.setDocTypeID(lSearchBean.getDocTypeID());
					auditPatchReqDTO.setDocNo(lSearchBean.getDocNo());
					auditPatchReqDTO.seteAduanNo(lSearchBean.geteAduanNo());

					lSearchBean.setChangeTypeIDOldVal(lSearchBean.getChangeTypeID());
					lSearchBean.setDocTypeIDOldVal(lSearchBean.getDocTypeID());
					lSearchBean.setDocNoOldVal(lSearchBean.getDocNo());
					lSearchBean.seteAduanNoOldVal(lSearchBean.geteAduanNo());
				} else if (!lSearchBean.isSearching()) {
					auditPatchReqDTO.setChangeTypeID(lSearchBean.getChangeTypeID());
					auditPatchReqDTO.setDocTypeID(lSearchBean.getDocTypeID());
					auditPatchReqDTO.setDocNo(lSearchBean.getDocNo());
					auditPatchReqDTO.seteAduanNo(lSearchBean.geteAduanNo());
				}
				
				auditPatchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditPatchSO.class);
				return auditPatchSO.searchMasterResultList(auditPatchReqDTO, searchBean);

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.AUDIT, "FirstUIViewMaster Patch Audit", "",
						"auditPatchSO", new LogKey(getUserIdSessionFromLoginId()).getUserId(), e);
			}
			return null;
		}
	}

	/**
	 * get Details for selected master row
	 * 
	 */
	private class SearchDetailPagingDataModel extends PagingDataModel<VAuditPatchDTO> {

		private static final long serialVersionUID = 1L;

		public SearchDetailPagingDataModel() {

			super("searchPatchDetails", getManagedBean().isLazy());

		}

		@Override
		public ISearchResultBean<VAuditPatchDTO> pageChangeListener(ISearchBean<VAuditPatchDTO> searchBean) {
			IAuditPatchSO auditPatchSO;

			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				auditPatchReqDTO.setpLanguage(languageCode);

				auditPatchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditPatchSO.class);

				ISearchResultBean<VAuditPatchDTO> resDTO;
				

				auditPatchReqDTO.setpType(getManagedBean().getSearchBean().getpType());
				auditPatchReqDTO.setChangeTypeID(getManagedBean().getSearchBean().getChangeTypeID());
				auditPatchReqDTO.seteAduanNo(getManagedBean().getSearchBean().geteAduanNo());
				auditPatchReqDTO.setDocTypeID(getManagedBean().getSearchBean().getDocTypeID());
				auditPatchReqDTO.setDocNo(getManagedBean().getSearchBean().getDocNo());
				
				getManagedBean().setHideAduan(!getManagedBean().getSearchBean().geteAduanNo().isEmpty());
				
				resDTO = auditPatchSO.searchDetailResultList(auditPatchReqDTO, searchBean);
				return resDTO;

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.AUDIT, "FirstUIViewDetail Patch Audit", "",
						"auditPatchSO", new LogKey(getUserIdSessionFromLoginId()).getUserId(), e);
			}
			return null;
		}
	}

	/**
	 * reset search bean fields
	 */
	public void resetSearchBean() {
		changeType();
		AuditPatchSearchBean searchBean = new AuditPatchSearchBean();
		this.getManagedBean().setSearchBean(searchBean);
		getManagedBean().setSelectedType(null);
		getManagedBean().setLazy(false);
		getSearchMaster().resetData();
		searchMaster = new SearchMasterPagingDataModel();
	}

	/**
	 * Resets the other search criteria when on change Change Type.
	 */
	public void changeType() {
		
		documentTypeList = getManagedBean().getSearchBean().getChangeTypeID() != null ? 
				getDocumentTypeList(getManagedBean().getSearchBean().getChangeTypeID()) : null;
				
		this.getManagedBean().getSearchBean().setDocTypeID(null);
		this.getManagedBean().getSearchBean().setDocNo(null);
		this.getManagedBean().getSearchBean().seteAduanNo(null);	
		
		getManagedBean().setLazy(false);//#40761#8, #35740
		getSearchMaster().resetData();//#40761, #35740
		searchMaster = new SearchMasterPagingDataModel();//#40761#8, #35740
	}
	
	/**
	 * Generate report for single tab in PDF format.
	 * @param reqDTO
	 */
	public void generateReport(AuditPatchReqDTO reqDTO) {
		try {
			Map<String, String> paramMap = new HashMap<>();

			paramMap.put(AuditPatchConst.P_LANGUAGE, AuditPatchConst.LANGUAGE_MY);
			paramMap.put(AuditPatchConst.P_CHANGE_TYPE, reqDTO.getChangeTypeID().toString());
			paramMap.put(AuditPatchConst.P_DOC_TYPE, reqDTO.getDocTypeID().toString());
			paramMap.put(AuditPatchConst.P_DOC_NO, reqDTO.getDocNo());
			paramMap.put(AuditPatchConst.P_USER, String.valueOf(getUserIdSessionFromLoginId()));
			if(reqDTO.getChangeTypeID() != AuditPatchConst.CHANGE_TYPE_EKONTRAK){
				paramMap.put(AuditPatchConst.P_EADUAN_NO, reqDTO.geteAduanNo());
			}
			
			paramMap.values().removeAll(Collections.singleton(null));
			generateReport(AuditReportConst.RPT_AUDIT_TRAIL_PAL, paramMap);
			
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Generate single tab excel format.
	 * @param reqDTO
	 * @param searchDetail
	 */
	public void generateRptExcel(AuditPatchReqDTO reqDTO, IPagingDataModel<VAuditPatchDTO> searchDetail) {
		String oriLanguange = reqDTO.getpLanguage();
		try {
			reqDTO.setFetchAll(true);
			reqDTO.setpLanguage(AuditPatchConst.LANGUAGE_MY);
			ISearchResultBean<VAuditPatchDTO> res = getAuditPatchSO().searchDetailResultList(reqDTO, searchDetail.getSearchBean());
			details = res.getResultList();
			
			createSheetLayout(reqDTO.getChangeTypeID());
			exportExcelReportAudit();
			
		} catch (Exception e) {
			log.error(e);
		}
		reqDTO.setFetchAll(false);
		reqDTO.setpLanguage(oriLanguange);
		wb = null;
	}
	
	/**
	 * Creates the Excel sheet layout by populating the report row by row
	 * based on the specified change type.
	 *  
	 * @param changeTypeID
	 */
	private void createSheetLayout(Integer changeTypeID){
		if (wb == null){
			wb = new XSSFWorkbook();
		}
		Sheet sheetRptAudit = wb.createSheet("Sheet1");
		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableRpt = 
				changeTypeID.equals(AuditPatchConst.CHANGE_TYPE_EKONTRAK) ? 
						ReportExcelAuditConstant.ColHeaderListType6 : 
							ReportExcelAuditConstant.ColHeaderListType5;
		CellStyle cellStyleTitleRpt = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();
		// Section - Setting Table Header Cell Style for each cell
		CellStyle colHeaderDataTableRptStyle = new ExcelCellStyleBuilder(wb).setHAlignment(HorizontalAlignment.CENTER)
				.setVAlignment(VerticalAlignment.CENTER).buildHeaderCellStyle();
		
		// Cell Format: String
		CellStyle cellStyleString = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE).buildDataCellStyleString(); 
		// Cell Format: DateTime
		CellStyle cellStyleDateTime = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();
		// End Section - Setting Cell Style for each cell
		
		// Create Row/Cell - Report Title
		Row titleRptRow = sheetRptAudit.createRow(1); // Create Row
		Cell cellTitleRow = titleRptRow.createCell(1);// Create Cell (Column)
		cellTitleRow.setCellValue(ReportExcelAuditConstant.RPT_TITLE); // Set Cell Value																		
		cellTitleRow.setCellStyle(cellStyleTitleRpt);
		sheetRptAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 11)); // Merge Column Cell

		// Create Row/Cell - Report Header Datatable
		Row titleHeaderRow = sheetRptAudit.createRow(3);
		for (int i = 0; i < colHeaderDataTableRpt.length; i++) {
			Cell cell = titleHeaderRow.createCell(i + 1);
			cell.setCellValue(colHeaderDataTableRpt[i]);
			cell.setCellStyle(colHeaderDataTableRptStyle);
		}
		
		// Section: Populate Audit Trail Details Record
		// Create Row/Cell - Audit Trail Details
		int rowIndex = sheetRptAudit.getLastRowNum() + 1;
		if (!EntityHelper.isCollectionEmpty(details)) {
			for(VAuditPatchDTO vapdto : details){
				Row currRow = sheetRptAudit.createRow(rowIndex++);
				Cell cellActionDate = currRow.createCell(1);
				cellActionDate.setCellValue(vapdto.getActionDate());//getActionDate
				cellActionDate.setCellStyle(cellStyleDateTime);
				
				Cell cellActionType = currRow.createCell(2);
				cellActionType.setCellValue(vapdto.getActionType());
				cellActionType.setCellStyle(cellStyleString);
				
				Cell cellActionBy = currRow.createCell(3);
				cellActionBy.setCellValue(vapdto.getActionBy());
				cellActionBy.setCellStyle(cellStyleString);
				
				Cell cellOrganisation = currRow.createCell(4);
				cellOrganisation.setCellValue(vapdto.getOrganization());
				cellOrganisation.setCellStyle(cellStyleString);
				
				Cell cellRole = currRow.createCell(5);
				cellRole.setCellValue(vapdto.getRole());
				cellRole.setCellStyle(cellStyleString);
				Cell cellDocType = currRow.createCell(6);
				cellDocType.setCellValue(vapdto.getDocTypeName());
				cellDocType.setCellStyle(cellStyleString);
				
				Cell cellDocNo = currRow.createCell(7);
				cellDocNo.setCellValue(vapdto.getDocNo());
				cellDocNo.setCellStyle(cellStyleString);
				
				Cell cellFieldName = currRow.createCell(8);
				cellFieldName.setCellValue(vapdto.getFieldName());
				cellFieldName.setCellStyle(cellStyleString);
				
				Cell cellOldValue = currRow.createCell(9);
				cellOldValue.setCellValue(vapdto.getOldValue());
				cellOldValue.setCellStyle(cellStyleString);
				
				Cell cellNewValue = currRow.createCell(10);
				cellNewValue.setCellValue(vapdto.getNewValue());
				cellNewValue.setCellStyle(cellStyleString);
				
				Cell cellChangeType = currRow.createCell(11);
				cellChangeType.setCellValue(vapdto.getChangeTypeName());
				cellChangeType.setCellStyle(cellStyleString);
				
				Cell cellReqBy = currRow.createCell(12);
				cellReqBy.setCellValue(vapdto.getRequestBy());
				cellReqBy.setCellStyle(cellStyleString);
				
				Cell cellReqOrg = currRow.createCell(13);
				cellReqOrg.setCellValue(vapdto.getRequestOrg());
				cellReqOrg.setCellStyle(cellStyleString);
				
				if(!changeTypeID.equals(AuditPatchConst.CHANGE_TYPE_EKONTRAK)){
					Cell celleAduanNo = currRow.createCell(14);
					celleAduanNo.setCellValue(vapdto.geteAduanNo());
					celleAduanNo.setCellStyle(cellStyleString);
				}
			}
		} else {
			createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length);
		}
		// Set column width
		setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
		
	}
	
	/**
	 * Sets the column widths for the Excel sheet.
	 *
	 * @param totalNoOfCell ,the total number of columns to be configured
	 * @param sheetName ,the Excel sheet for which the column widths are set
	 */
	private void setExlSheetColumnWidth(int totalNoOfCell, Sheet sheetName) {		
		// Default width for specific column ranges
		int defaultWidth = 19 * 256;

		// Define a map for specific column widths
		Map<Integer, Integer> columnWidths = new HashMap<>();
		columnWidths.put(0, 23 * 256);
		columnWidths.put(1, defaultWidth);
		columnWidths.put(2, defaultWidth);
		columnWidths.put(3, defaultWidth);
		columnWidths.put(4, defaultWidth);
		columnWidths.put(5, defaultWidth);
		columnWidths.put(6, defaultWidth);
		columnWidths.put(7, defaultWidth);
		columnWidths.put(8, defaultWidth);
		columnWidths.put(9, defaultWidth);
		columnWidths.put(10, defaultWidth);
		columnWidths.put(11, defaultWidth);
		columnWidths.put(12, defaultWidth);
		columnWidths.put(13, defaultWidth);
		
		for (int i = 0; i < totalNoOfCell; i++) {
			sheetName.setColumnWidth(i + 1, columnWidths.get(i));
		}
	}

	/**
	 * Creates a No Records Found row with the predefined cell style.
	 *
	 * @param sheet ,the target Excel sheet
	 * @param rowIndex ,the row index for the new row
	 * @param totalCell ,the last column index for merging the cell
	 */
	private void createNotFoundRecord(Sheet sheet, int rowIndex, int totalCell) {
		// Section - Setting Cell Style for No Records Found message.
		CellStyle noRecordsStyle = new ExcelCellStyleBuilder(wb).buildDataCellStyleString();
		Row noRecordRow = sheet.createRow(rowIndex);
		Cell cellNoRecord = noRecordRow.createCell(1);
		cellNoRecord.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
		cellNoRecord.setCellStyle(noRecordsStyle);
		sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 1, totalCell));
	}
	
	private void exportExcelReportAudit() {
		try {
			PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);
			response.setContentType("application/ms-excel");
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition", ReportExcelAuditConstant.RPT_FILE_CONTENT_DISPOSITION);
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.flushBuffer();
			OutputStream outStream = response.getOutputStream();
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			wb.write(outByteStream);// Write into file
			byte[] outArray = outByteStream.toByteArray();
			outStream.write(outArray);
			outStream.flush();
			outStream.close();
			wb.close();// Close Workbook
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private IAuditPatchSO getAuditPatchSO(){
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.AUDIT, IAuditPatchSO.class);
	}

	/**
	 * @return the searchMaster
	 */
	public IPagingDataModel<VAuditPatchDTO> getSearchMaster() {
		return searchMaster;
	}

	/**
	 * @param searchMaster the searchMaster to set
	 */
	public void setSearchMaster(IPagingDataModel<VAuditPatchDTO> searchMaster) {
		this.searchMaster = searchMaster;
	}

	public String getRoleCode() {
		if (getManagedBean().getSearchBean().getRoleCode() != null) {
			roleCode = getManagedBean().getSearchBean().getRoleCode();
		}
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public AuditPatchReqDTO getReqDTO() {
		return auditPatchReqDTO;
	}

	/**
	 * @return the searchDetail
	 */
	public IPagingDataModel<VAuditPatchDTO> getSearchDetail() {
		return searchDetail;
	}

	/**
	 * @param searchDetail the searchDetail to set
	 */
	public void setSearchDetail(IPagingDataModel<VAuditPatchDTO> searchDetail) {
		this.searchDetail = searchDetail;
	}
	
	public List<VAuditPatchDTO> getDocumentTypeList() {
	    return documentTypeList;
	}
}
