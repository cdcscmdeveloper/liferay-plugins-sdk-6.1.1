package my.ep.web.backingbean.report.sm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xwpf.usermodel.VerticalAlign;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ColPaymentRptManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "colPaymentRptBB")
@ViewScoped
public class ColPaymentRptBackingBean extends SmReportBackingBean<ColPaymentRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ColPaymentRptBackingBean.class);
	
	public ColPaymentRptBackingBean(){
		getManagedBean().setPaymentTypeMethodLs(getPaymentTypeMethodList());
		
		//Enhancement #19245
		getManagedBean().setCdcCollector(isRole(ReportConst.ROLE_CODE_COLLECTOR_CDC));
		getManagedBean().setSoftCert(SmReportConst.PAYMENT_SOFTC_CERT);
		
		setDateFromAndDateTo();
		//end of Enhancement #19245
	}
	
	public void generateReport() {
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		addCompanyNameParam(params);
		addSelectedPaymentTypeParam(params);

		if (StringUtils.isNotBlank(getManagedBean().getReportType())) {
			params.put(SmReportConst.PARAM_RPT_TYPE, getManagedBean()
					.getReportType());
		}

		if (getManagedBean().getDateFrom() != null) {
			params.put(SmReportConst.PARAM_DATE_FROM,
					df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO,
					df.format(getManagedBean().getDateTo()));
		}

		if (!StringUtils.isBlank(getManagedBean().getPaymentMethod())) {
			params.put(SmReportConst.PARAM_PAYMENT_METHOD,
					StringUtils.upperCase(getManagedBean().getPaymentMethod()));
		}

		if(getManagedBean().getDocumentType() == null){
			if(getManagedBean().getDefaultOutputType().equals("P")){
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "pdf");
			}else{
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "enhancedspreadsheet");
			}	
		}else{
			if(getManagedBean().getDocumentType().equals("P")){
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "pdf");
			}else{
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "enhancedspreadsheet");
			}			
			getManagedBean().setDefaultOutputType(getManagedBean().getDocumentType());
		}
		
		//Enhancement #15990
		//If Document Type - Excel will bypass runJob from oracle report.
		if(getManagedBean().getDocumentType() == null){
			if("P".equals(getManagedBean().getDefaultOutputType())){
				generateSmReport(params);
			}else{
				try{
					generateSMReportInExcel(getManagedBean().getReportCode(), params);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else{
			if("P".equals(getManagedBean().getDocumentType())){
				generateSmReport(params);
			}else{
				try{
					generateSMReportInExcel(getManagedBean().getReportCode(), params);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void resetSearch(){
		getManagedBean().setReportType(null);
		getManagedBean().setPaymentMethod(null);
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
		getManagedBean().setSelectedOrgProfile(null);
		
		//Enhancement #19245
		setDateFromAndDateTo();
		//end of Enhancement #19245
	}
	
	public static SelectItem[] getDocumentTypeOptions() {
		log.info("getReportTypeOptions");
		return PmCommonUtil.getDocumentTypeOption();
	}
	
	
	
	//Start- Enhancement #15990
	//Only for Laporan Terimaan Berdasarkan Kaedah Bayaran
	public void exportAndDownloadToExcel() throws FileNotFoundException, IOException,
	EncryptedDocumentException, InvalidFormatException{
		
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		
		addCompanyNameParam(params);
		addSelectedPaymentTypeParam(params);

		if (StringUtils.isNotBlank(getManagedBean().getReportType())) {
			params.put(SmReportConst.PARAM_RPT_TYPE, getManagedBean()
					.getReportType());
		}

		if (getManagedBean().getDateFrom() != null) {
			params.put(SmReportConst.PARAM_DATE_FROM,
					df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO,
					df.format(getManagedBean().getDateTo()));
		}

		if (!StringUtils.isBlank(getManagedBean().getPaymentMethod())) {
			params.put(SmReportConst.PARAM_PAYMENT_METHOD,
					StringUtils.upperCase(getManagedBean().getPaymentMethod()));
		}
		
		List<String> payType = new ArrayList<String>();
		if("M".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE1))){//MOF Registration Fee
			payType.add("MF");
			payType.add("RF");
		}
		
		if("P".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE2))){//Processing Fee
			payType.add("PF");
			payType.add("RPF");
		}
		
		String paymentType = "";
		if("S".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE3))){//Soft Certificate Fee
			payType.add("SF");
		}
		
		for(int b=0; b<payType.size(); b++){
			if(b != payType.size()-1){
				paymentType += "'"+payType.get(b).toString() +"',";
			}else{
				paymentType += "'"+payType.get(b).toString()+"'";
			}
		}
		
		log.info("PAYMET TYPE ==== "+paymentType);
		params.put("PAYMENTTYPE",paymentType);
		
		List<String> paymentTypeList = retrievePaymentType(paymentType);
		String paymentTypeDesc = "";
		if(paymentTypeList != null){
			for(int x = 0; x<paymentTypeList.size(); x++){
				if(x != paymentTypeList.size() - 1){
					paymentTypeDesc += paymentTypeList.get(x).toString()+",\n";
				}else{
					paymentTypeDesc += paymentTypeList.get(x).toString()+" ";
				}
			}
		}
		
		
		String fileName = getManagedBean().getReportTitle();
		log.info("FILE NAME == "+fileName);
		String[] reportTitleList = {"Laporan Terimaan Berdasarkan Kaedah Bayaran Bagi Tempoh","Ringkasan Laporan Terimaan Berdasarkan Kaedah Bayaran Bagi Tempoh"};
		String[] sheetFileName = {"Senarai", "Ringkasan"};
		String[] rowItems = {"Kod PTJ","Nama PTJ","Kaedah Bayaran","Nama Pembekal","Jenis Bayaran"};
		String name = "";
		String paymentMethod = "";
		
		String[] columnsLaporan = {
				"Bil", 
				"Tarikh", 
				"No Resit", 
				"Nama Pembekal",
				"No Permohonan", 
				"Jenis Terimaan-Kod",  
				"Jenis Terimaan-Perihal",
				"Harga (RM)",
				"GST (RM)",
				"Caj Perkhidmatan",
				"GST Caj Perkhidmatan",
				"Amaun Bersih (RM)",
				"Jumlah Keseluruhan (RM)",
				"Status"
		};
		
		String[] columnsRingkasan = {
				"Tarikh Resit",
				"  Bil  ", 
				"Harga (RM)",
				"GST (RM)",
				"Caj Perkhidmatan (RM)",
				"GST Caj Perkhidmatan (RM)",
				"Amaun Bersih (RM)"
		};
		
		if("Credit".equalsIgnoreCase(getManagedBean().getPaymentMethod())){
			name = "CC";
			paymentMethod = "Kad Kredit";
			
		}else{
			name = "FPX";
			paymentMethod = "FPX";
		}
				
		//START HERE POPULATE REPORT ------ 
		Workbook wb = new HSSFWorkbook(); 
		// Creating Sheets using sheet object 
		Sheet sheetLaporan = wb.createSheet(sheetFileName[0]+" "+name); 
		Sheet sheetRingkasan = wb.createSheet(sheetFileName[1]+" "+name); 
        
        Font titleReportFont = wb.createFont();
		titleReportFont.setBold(true);
		//titleReportFont.setFontName("Times New Roman");
		
		
		CellStyle titleReportCellStyle = wb.createCellStyle();
		titleReportCellStyle.setFont(titleReportFont);
		
		CellStyle headerCellStyle = wb.createCellStyle();
		headerCellStyle.setFont(titleReportFont);
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());  
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle itemListStyle = wb.createCellStyle();
		itemListStyle.setBorderTop(BorderStyle.THIN);
		itemListStyle.setBorderBottom(BorderStyle.THIN);
		itemListStyle.setBorderLeft(BorderStyle.THIN);
		itemListStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle itemListStyle_2 = wb.createCellStyle();
		itemListStyle_2.setBorderTop(BorderStyle.THIN);
		itemListStyle_2.setBorderBottom(BorderStyle.THIN);
		itemListStyle_2.setBorderLeft(BorderStyle.THIN);
		itemListStyle_2.setBorderRight(BorderStyle.THIN);
		itemListStyle_2.setAlignment(HorizontalAlignment.CENTER);
		
		CellStyle wrapTextStyle = wb.createCellStyle();
		wrapTextStyle.setWrapText(true);
		wrapTextStyle.setVerticalAlignment(VerticalAlignment.TOP);
		
		
		CellStyle currencyStyle = wb.createCellStyle();
		currencyStyle.setBorderTop(BorderStyle.THIN);
		currencyStyle.setBorderBottom(BorderStyle.THIN);
		currencyStyle.setBorderLeft(BorderStyle.THIN);
		currencyStyle.setBorderRight(BorderStyle.THIN);
		currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
		DataFormat datafrmt = wb.createDataFormat();
		currencyStyle.setDataFormat(datafrmt.getFormat("###,###,##0.00")); 
		
        
        Row titleRow = sheetLaporan.createRow(0);
       
        Cell cellTitleRow = titleRow.createCell(1);
        cellTitleRow.setCellValue(reportTitleList[0] + " "
        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) 
        		+ " hingga "
        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
        		+" seperti pada "
        		+ DateUtils.dateToString("dd/MM/yyyy",new Date())
        		+" bagi tahun kewangan "
        		+ DateUtils.dateToString("yyyy",new Date())
        );
        cellTitleRow.setCellStyle(titleReportCellStyle);
	    
        sheetLaporan.addMergedRegion(new CellRangeAddress(0,0,1,columnsLaporan.length)); 
        
        //Sheet 1:
        for(int i = 1; i <= rowItems.length; i++) {
        	Row itemsDtlRow = sheetLaporan.createRow(i);
        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
        	cellItemsDtl.setCellValue(rowItems[i-1]);
        	if(i==5){
        		cellItemsDtl.setCellStyle(wrapTextStyle);
        	}
        	
        	Cell cellItemsVal = itemsDtlRow.createCell(2);
        	if(i==1){
        		cellItemsVal.setCellValue("24010168");        		
        	}
        	
        	if(i==2){
        		cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
        	}
        	
        	if(i==3){
        		cellItemsVal.setCellValue(paymentMethod);
        	}
        	
        	if(i==4){
        		if(StringUtils.isNotBlank(params.get(SmReportConst.PARAM_COMPANY_NAME))){
        			cellItemsVal.setCellValue(params.get(SmReportConst.PARAM_COMPANY_NAME));
        		}else{
        			cellItemsVal.setCellValue("SEMUA");
        		}
        	}
        	
        	if(i==5){
        		//sheetLaporan.addMergedRegion(new CellRangeAddress(itemsDtlRow.getRowNum(),itemsDtlRow.getRowNum(),2,3));
        		cellItemsVal.setCellValue(paymentTypeDesc);
        		cellItemsVal.setCellStyle(wrapTextStyle);
        	}
        }
        
        //Row Senarai Laporan Terimaan
        Row headerRowListLaporan = sheetLaporan.createRow(sheetLaporan.getLastRowNum()+2);
        
        for(int i = 0; i < columnsLaporan.length; i++) {
            Cell cell = headerRowListLaporan.createCell(i);
            cell.setCellValue(columnsLaporan[i]);
            cell.setCellStyle(headerCellStyle);
        }
        
        try{
        	//Query-Paparan Laporan Terimaan Berdasarkan Kaedah Bayaran
			List<Object[]> reportList = generateSmCollectionReportBasedPaymentMethod(params,"L");
			log.info("SIZE LIST === >>> "+reportList.size());
			if(reportList != null){
				int rowCount = 1;
				for(int i = 0; i<reportList.size(); i++){
					Row itemLaporanTerimaan = sheetLaporan.createRow(sheetLaporan.getLastRowNum()+1);
					Object[] row = (Object[]) reportList.get(i);
					String applNo = "";
					String kod = "";
					String perihal = "";
					int rowCellCount = 0;					
					for(int a=0; a<row.length; a++){
						double amount = row[7] != null ? Double.parseDouble(String.valueOf(row[7])) : 0.00;
						double gstAmount = row[8] != null ? Double.parseDouble(String.valueOf(row[8])) : 0.00 ;
						double cajPerkhidmatan = row[9] != null ? Double.parseDouble(String.valueOf(row[9])) : 0.00;
						double gstRate = row[10] != null ? Double.parseDouble(String.valueOf(row[10])) : 0.00;
						double gstServiceChargeAmt = cajPerkhidmatan * gstRate;
						double netPrice = amount + gstAmount - cajPerkhidmatan - gstServiceChargeAmt;
						Cell cell;
						if(a==0){
							cell = itemLaporanTerimaan.createCell(rowCellCount);
				            cell.setCellValue(rowCount);
				            cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==1){ //Column : Tarikh
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(String.valueOf(row[1]));
							cell.setCellStyle(itemListStyle_2);
				            rowCellCount++;
						}else if(a==2) { // Column : No Resit
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(String.valueOf(row[2]));
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==3){ // Column : Nama Pembekal
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue( String.valueOf(row[3]));
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==4){ // Column : No Permohonan 
							applNo = String.valueOf(row[4]);
							log.info("NO PERMOHONAN === "+applNo);
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(StringUtils.isNotEmpty(applNo) ? applNo : "isemptyla");
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==5){// Column : Kod
							kod = String.valueOf(row[5]);
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(kod != null ? kod : "");
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==6){// Column : Perihal
							perihal = String.valueOf(row[6]);							
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(StringUtils.isNotBlank(perihal) ? perihal : "");
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==7){//Column : Harga (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(amount);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==8){//Column : GST (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(gstAmount);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==9){//Column : Caj Perkhidmatan
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(cajPerkhidmatan);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==10){//Column : GST Caj Perkhidmatan
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(gstServiceChargeAmt);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==11){//Column : Amaun Bersih (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(netPrice);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==12){//Column : Jumlah Keseluruhan (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(netPrice);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==13) {//Status
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(String.valueOf(row[13]));
							cell.setCellStyle(itemListStyle);
						}						
					}
					rowCount++;
				}
			}
			
		}catch(Exception e){
			log.info("ADA ERROR LAA SHEET LAPORAN !!!! ");
			e.printStackTrace();
		}
        
        sheetLaporan.setAutoFilter(new CellRangeAddress(headerRowListLaporan.getRowNum(),sheetLaporan.getLastRowNum(),headerRowListLaporan.getFirstCellNum(),columnsLaporan.length-1));
        
        // Resize all columns to fit the content size
        for(int i = 0; i < columnsLaporan.length; i++) {
        	sheetLaporan.autoSizeColumn(i);
        }
        
        //Second Sheet
        try{
        	//Query-Paparan Ringkasan Terimaan Berdasarkan Kaedah Bayaran
			List<Object[]> reportRingkasanList = generateSmCollectionReportBasedPaymentMethod(params,"R");
			log.info("SIZE REPORT RINGKASAN TERIMAAN LIST === >>> "+reportRingkasanList.size());
			
			Row titleRowRingkasan = sheetRingkasan.createRow(0);
	        Cell cellTitleRingkasan = titleRowRingkasan.createCell(0);
	        cellTitleRingkasan.setCellValue(reportTitleList[1]+ " "
	        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) 
	        		+ " hingga "
	        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
	        		+" seperti pada "
	        		+ DateUtils.dateToString("dd/MM/yyyy",new Date())
	        		+" bagi tahun kewangan "
	        		+ DateUtils.dateToString("yyyy",new Date()));
	        cellTitleRingkasan.setCellStyle(titleReportCellStyle);
	        
	        sheetRingkasan.addMergedRegion(new CellRangeAddress(0,0,0,columnsLaporan.length-1)); 
	        
	        Row headerRowRingkasan = sheetRingkasan.createRow(2);
	        // Create cells
	        for(int i = 0; i < columnsRingkasan.length; i++) {
	            Cell cell = headerRowRingkasan.createCell(i);
	            cell.setCellValue(columnsRingkasan[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
	        
	        int totalCount = 0;
	        double totalAmt = 0.00;
        	double totalGstAmt = 0.00;
        	double totalServiceCaj = 0.00;
        	double totalNetAmt = 0.00;
        	
	        if(reportRingkasanList != null){
	        	for(int m=0; m<reportRingkasanList.size(); m++){
	        		Row itemRingkasanTerimaan = sheetRingkasan.createRow(sheetRingkasan.getLastRowNum()+1);
					Object[] rowRingkasan = (Object[]) reportRingkasanList.get(m);
					int rowCellRingkasanCount = 0;
					for(int x=0; x<rowRingkasan.length+1; x++){
						double amt = rowRingkasan[2] != null ? Double.parseDouble(String.valueOf(rowRingkasan[2])) : 0.00;
						double gstAmt = rowRingkasan[3] != null ? Double.parseDouble(String.valueOf(rowRingkasan[3])) : 0.00;
						double serviceCaj = rowRingkasan[4] != null ? Double.parseDouble(String.valueOf(rowRingkasan[4])) : 0.00;
						double netAmt = amt + gstAmt - serviceCaj;
						Cell cellRingkasan;
						if(x==0){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(String.valueOf(rowRingkasan[x]));
							cellRingkasan.setCellStyle(itemListStyle_2);
							rowCellRingkasanCount++;
						}else if(x==1){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(Integer.parseInt(String.valueOf(rowRingkasan[x])));
							cellRingkasan.setCellStyle(itemListStyle);
							rowCellRingkasanCount++;
							totalCount += Integer.parseInt(String.valueOf(rowRingkasan[x]));
						}else if(x==2){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(amt);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalAmt += amt;
						}else if(x==3){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(gstAmt);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalGstAmt += gstAmt;
						}else if(x==4){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(serviceCaj);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalServiceCaj += serviceCaj;
						}else if(x==5){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(0.00);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
						}else if(x==6){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(netAmt);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalNetAmt += netAmt;
						}
					}
					
	        	}
	        }
	        
	        //Row : Display Sum of each amount column (Harga/GST/Caj Perkhidmatan/GST Caj Perkhidmatan/Amaun Bersih)
	        Row rowSum = sheetRingkasan.createRow(sheetRingkasan.getLastRowNum()+1);
	        for(int x=0; x<columnsRingkasan.length; x++){
	        	Cell cellSumRingkasan = rowSum.createCell(x);;
	        	if(x==0){
	        		cellSumRingkasan.setCellValue("Keseluruhan");
	        		cellSumRingkasan.setCellStyle(itemListStyle);
	        	}else if(x==1){
	        		cellSumRingkasan.setCellValue(totalCount);
	        		cellSumRingkasan.setCellStyle(itemListStyle);
	        	}else if(x==2){
	        		cellSumRingkasan.setCellValue(totalAmt);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==3){
	        		cellSumRingkasan.setCellValue(totalGstAmt);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==4){
	        		cellSumRingkasan.setCellValue(totalServiceCaj);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==5){
	        		cellSumRingkasan.setCellValue(0.00);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==6){
	        		cellSumRingkasan.setCellValue(totalNetAmt);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}
	        }
	        
	        // Resize all columns to fit the content size
	        for(int i = 0; i < columnsRingkasan.length; i++) {
	        	sheetRingkasan.autoSizeColumn(i);
	        }
	        			
        }catch(Exception e){
        	log.info("ADA ERROR LAA SHEET RINGKASAN !!!! ");
			e.printStackTrace();
        }
        
        PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);
		
		response.setContentType("application/ms-excel");
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + ".xls\"");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.flushBuffer();
		
		OutputStream outStream = response.getOutputStream();
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		wb.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();					
		
		outStream.write(outArray);
		outStream.flush();
		outStream.close();
		wb.close();
               
	}
	//END - CR #15990
}
