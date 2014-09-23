package my.ep.web.bean.report;


import my.ep.web.constant.report.ReportConst;

/**
 * @Author Jangari Kondal Reddy
 */
public class ReportsGenerateBean {
	
		private boolean itemTypeView = false;
		private boolean itemStatusView = false;
		private boolean unspscCodeView = false;
		private boolean unspscTitleView = false;
		private boolean extentionCodeView = false;
		private boolean companyNameView = false;
		private boolean registrationTypeView = false;
		private boolean fromDateView = false;
		private boolean toDateView = false;
		private boolean quotationNoView = false;
		private boolean paymentIdView = false;
		private boolean itemCategoryView = false;
		private boolean itemNameView = false;
		private boolean basicRegView = false;
		private boolean mofRegView = false;	
		/*FL Start*/
		private boolean ptjView = false;
		private boolean contractNoView = false;
		private boolean sstView = false;
		private boolean jabatanView = false;
		private boolean ministryView = false;
		private boolean supplierView = false;	
		private boolean statusView = false;
		private boolean durationView = false;
		private boolean recievingOfficerView = false;
		private boolean deliveryAddressView = false;
		private boolean flRecNoteNoView = false;
		private boolean contractTypeView = false;
		private boolean requisitionerView = false;
		private boolean mtoContractView = false;
		private boolean invoiceNoView = false;
		private boolean deductionTypeView = false;
		private boolean crNoView = false;
		private boolean approverNameView = false;
		private boolean documentTypeView = false;
		private boolean criteriaView = false;
		private boolean scoringView = false;
		private boolean pkpNoView = false;
		private boolean deliveryOrderNoView = false;	
		private boolean itemCodeView = false;
		private boolean poAmountView = false;
		private boolean prNoView = false;
		private boolean pmoView = false;
		/*FL end*/
		/*SM start*/
		private boolean supportingDocumentView = false;	
		private boolean docStatusView = false;
		private boolean SuppDocStatusView = false;
		private boolean registrationNoView = false;
		private boolean ePNoView = false;
		private boolean applicationNoView = false;
		private boolean zoneView = false;
		private boolean supplierTypeView = false;
		private boolean mofRegNoView = false;
		private boolean mofRegNoFromView = false;
		private boolean mofRegNoToView = false;
		private boolean registrationStatusView = false;
		private boolean capitalNaccumView = false;
		private boolean paidUpCapitalNaccumShareView = false;
		private boolean categoryView = false;
		private boolean noOfCategoryView = false;
		private boolean mainCategoryView = false;
		private boolean subCategoryView = false;
		private boolean subCategoryDivisionView = false;
		private boolean roleView = false;
		private boolean applicationStatusView = false;
		private boolean cancelledByView = false;
		private boolean paymentTypeView = false;
		private boolean applicationTypeView = false;
		private boolean stateView = false;
		private boolean districtView = false;
		private boolean supplierNameView = false;
		private boolean reportTypeView = false;
		private boolean organizationTypeView = false;
		private boolean kementerianView = false;
		private boolean namAgensiView = false;
		private boolean complaintNoView = false;
		private boolean proofIdentityView = false;
		private boolean mofNoView = false;	
		private boolean companyStatusView = false;
		private boolean caseNoView = false;
		private boolean extraTwoColumnView = false;	
		private boolean compApplStatusView = false;		
		private boolean smCompanyNameView = false;	
		private boolean categoryCodeView = false;
		private boolean siteVisitOfficerView = false;
		private boolean releaseDateView = false;
		private boolean applicationDateView = false;
		private boolean receiveDateView = false;
		private boolean expiryDateView = false;
		private boolean printedDateView = false;
		private boolean mandatoryZoneView = false;
		private boolean mandatorySupplierTypeView = false;
		private boolean mandatoryApplicationDateView = false;
		private boolean mandatoryApplicationStatusView = false;
		private boolean mandatoryMofRegNoView = false;
		private boolean mandatoryExpiryDateView = false;
		private boolean mandatoryPrintedDateView = false;
		
		/*SM end*/
	   //URL variables start
		private String quotationIdTotVar=ReportConst.EMPTY_VAL;
		private String itemTypeTotVar=ReportConst.EMPTY_VAL;
		private String itemStatusTotVar=ReportConst.EMPTY_VAL;		
		private String unspscCodeTotVar=ReportConst.EMPTY_VAL;
		private String unspscTitleTotVar=ReportConst.EMPTY_VAL;
		private String extentionCodeTotVar=ReportConst.EMPTY_VAL;
		private String extentionTypeTotVar=ReportConst.EMPTY_VAL;
		private String extentionBrandTotVar=ReportConst.EMPTY_VAL;
		private String extentionMeasurmentTotVar=ReportConst.EMPTY_VAL;
		private String extentionColourTotVar=ReportConst.EMPTY_VAL;
		private String companyNameTotVar=ReportConst.EMPTY_VAL;
		private String registrationTypeTotVar=ReportConst.EMPTY_VAL;	
		private String itemCategoryTotVar=ReportConst.EMPTY_VAL;
		private String itemNameTotVar=ReportConst.EMPTY_VAL;
		private String proofIdentityTotVar=ReportConst.EMPTY_VAL;
		private String epNumberTotVar=ReportConst.EMPTY_VAL;	
		private String businessRegNoTotVar=ReportConst.EMPTY_VAL;
		private String icNumberTotVar=ReportConst.EMPTY_VAL;
		private String mofNumberTotVar=ReportConst.EMPTY_VAL;
		private String ptjTotVar=ReportConst.EMPTY_VAL;
		private String contractNoTotVar=ReportConst.EMPTY_VAL;
		private String sstTotVar=ReportConst.EMPTY_VAL;
		private String jabatanTotVar=ReportConst.EMPTY_VAL;
		private String ministryTotVar=ReportConst.EMPTY_VAL;
		private String supplierTotVar=ReportConst.EMPTY_VAL;
		private String statusTotVar=ReportConst.EMPTY_VAL;
		private String durationTotVar=ReportConst.EMPTY_VAL;
		private String recievingOfficerTotVar=ReportConst.EMPTY_VAL;
		private String deliveryAddressTotVar=ReportConst.EMPTY_VAL;
		private String flRecNoteNoTotVar=ReportConst.EMPTY_VAL;
		private String contractTypeTotVar=ReportConst.EMPTY_VAL;
		private String requisitionerTotVar=ReportConst.EMPTY_VAL;
		private String mtoContractTotVar=ReportConst.EMPTY_VAL;
		private String invoiceNoTotVar=ReportConst.EMPTY_VAL;
		private String deductionTypeTotVar=ReportConst.EMPTY_VAL;
		private String crNoTotVar=ReportConst.EMPTY_VAL;
		private String approverNameTotVar=ReportConst.EMPTY_VAL;
		private String documentTypeTotVar=ReportConst.EMPTY_VAL;
		private String criteriaTotVar=ReportConst.EMPTY_VAL;
		private String scoringTotVar=ReportConst.EMPTY_VAL;
		private String pkpNoTotVar=ReportConst.EMPTY_VAL;
		private String deliveryOrderNoTotVar=ReportConst.EMPTY_VAL;	
		private String itemCodeTotVar=ReportConst.EMPTY_VAL;
		private String poAmountTotVar=ReportConst.EMPTY_VAL;
		private String prNoTotVar=ReportConst.EMPTY_VAL;
		private String pmoTotVar=ReportConst.EMPTY_VAL;
		private String fromDateVar=ReportConst.EMPTY_VAL;
		private String toDateVar=ReportConst.EMPTY_VAL;

		
		
		public boolean isMandatoryExpiryDateView() {
			return mandatoryExpiryDateView;
		}
		public void setMandatoryExpiryDateView(boolean mandatoryExpiryDateView) {
			this.mandatoryExpiryDateView = mandatoryExpiryDateView;
		}
		public boolean isMandatoryPrintedDateView() {
			return mandatoryPrintedDateView;
		}
		public void setMandatoryPrintedDateView(boolean mandatoryPrintedDateView) {
			this.mandatoryPrintedDateView = mandatoryPrintedDateView;
		}
		public boolean isMandatoryMofRegNoView() {
			return mandatoryMofRegNoView;
		}
		public void setMandatoryMofRegNoView(boolean mandatoryMofRegNoView) {
			this.mandatoryMofRegNoView = mandatoryMofRegNoView;
		}
		public boolean isMandatoryApplicationDateView() {
			return mandatoryApplicationDateView;
		}
		public void setMandatoryApplicationDateView(boolean mandatoryApplicationDateView) {
			this.mandatoryApplicationDateView = mandatoryApplicationDateView;
		}
		public boolean isMandatoryApplicationStatusView() {
			return mandatoryApplicationStatusView;
		}
		public void setMandatoryApplicationStatusView(
				boolean mandatoryApplicationStatusView) {
			this.mandatoryApplicationStatusView = mandatoryApplicationStatusView;
		}
		public boolean isMandatorySupplierTypeView() {
			return mandatorySupplierTypeView;
		}
		public void setMandatorySupplierTypeView(boolean mandatorySupplierTypeView) {
			this.mandatorySupplierTypeView = mandatorySupplierTypeView;
		}
		public boolean isMandatoryZoneView() {
			return mandatoryZoneView;
		}
		public void setMandatoryZoneView(boolean mandatoryZoneView) {
			this.mandatoryZoneView = mandatoryZoneView;
		}
		public boolean isPrintedDateView() {
			return printedDateView;
		}
		public void setPrintedDateView(boolean printedDateView) {
			this.printedDateView = printedDateView;
		}
		public boolean isExpiryDateView() {
			return expiryDateView;
		}
		public void setExpiryDateView(boolean expiryDateView) {
			this.expiryDateView = expiryDateView;
		}
		public boolean isReceiveDateView() {
			return receiveDateView;
		}
		public void setReceiveDateView(boolean receiveDateView) {
			this.receiveDateView = receiveDateView;
		}
		public boolean isApplicationDateView() {
			return applicationDateView;
		}
		public void setApplicationDateView(boolean applicationDateView) {
			this.applicationDateView = applicationDateView;
		}
		public boolean isReleaseDateView() {
			return releaseDateView;
		}
		public void setReleaseDateView(boolean releaseDateView) {
			this.releaseDateView = releaseDateView;
		}
		public boolean isSiteVisitOfficerView() {
			return siteVisitOfficerView;
		}
		public void setSiteVisitOfficerView(boolean siteVisitOfficerView) {
			this.siteVisitOfficerView = siteVisitOfficerView;
		}
		public boolean isCategoryCodeView() {
			return categoryCodeView;
		}
		public void setCategoryCodeView(boolean categoryCodeView) {
			this.categoryCodeView = categoryCodeView;
		}
		public boolean isSmCompanyNameView() {
			return smCompanyNameView;
		}
		public void setSmCompanyNameView(boolean smCompanyNameView) {
			this.smCompanyNameView = smCompanyNameView;
		}
		public boolean isCompApplStatusView() {
			return compApplStatusView;
		}
		public void setCompApplStatusView(boolean compApplStatusView) {
			this.compApplStatusView = compApplStatusView;
		}
		public boolean isExtraTwoColumnView() {
			return extraTwoColumnView;
		}
		public void setExtraTwoColumnView(boolean extraTwoColumnView) {
			this.extraTwoColumnView = extraTwoColumnView;
		}
		public boolean isCaseNoView() {
			return caseNoView;
		}
		public void setCaseNoView(boolean caseNoView) {
			this.caseNoView = caseNoView;
		}
		public boolean isCompanyStatusView() {
			return companyStatusView;
		}
		public void setCompanyStatusView(boolean companyStatusView) {
			this.companyStatusView = companyStatusView;
		}
		public boolean isMofNoView() {
			return mofNoView;
		}
		public void setMofNoView(boolean mofNoView) {
			this.mofNoView = mofNoView;
		}	
		public boolean isSupportingDocumentView() {
			return supportingDocumentView;
		}
		public void setSupportingDocumentView(boolean supportingDocumentView) {
			this.supportingDocumentView = supportingDocumentView;
		}
		public boolean isDocStatusView() {
			return docStatusView;
		}
		public void setDocStatusView(boolean docStatusView) {
			this.docStatusView = docStatusView;
		}
		public boolean isSuppDocStatusView() {
			return SuppDocStatusView;
		}
		public void setSuppDocStatusView(boolean suppDocStatusView) {
			SuppDocStatusView = suppDocStatusView;
		}
		public boolean isRegistrationNoView() {
			return registrationNoView;
		}
		public void setRegistrationNoView(boolean registrationNoView) {
			this.registrationNoView = registrationNoView;
		}
		public boolean isePNoView() {
			return ePNoView;
		}
		public void setePNoView(boolean ePNoView) {
			this.ePNoView = ePNoView;
		}
		public boolean isApplicationNoView() {
			return applicationNoView;
		}
		public void setApplicationNoView(boolean applicationNoView) {
			this.applicationNoView = applicationNoView;
		}
		public boolean isZoneView() {
			return zoneView;
		}
		public void setZoneView(boolean zoneView) {
			this.zoneView = zoneView;
		}
		public boolean isSupplierTypeView() {
			return supplierTypeView;
		}
		public void setSupplierTypeView(boolean supplierTypeView) {
			this.supplierTypeView = supplierTypeView;
		}
		public boolean isMofRegNoView() {
			return mofRegNoView;
		}
		public void setMofRegNoView(boolean mofRegNoView) {
			this.mofRegNoView = mofRegNoView;
		}
		public boolean isMofRegNoFromView() {
			return mofRegNoFromView;
		}
		public void setMofRegNoFromView(boolean mofRegNoFromView) {
			this.mofRegNoFromView = mofRegNoFromView;
		}
		public boolean isMofRegNoToView() {
			return mofRegNoToView;
		}
		public void setMofRegNoToView(boolean mofRegNoToView) {
			this.mofRegNoToView = mofRegNoToView;
		}
		public boolean isRegistrationStatusView() {
			return registrationStatusView;
		}
		public void setRegistrationStatusView(boolean registrationStatusView) {
			this.registrationStatusView = registrationStatusView;
		}
		public boolean isCapitalNaccumView() {
			return capitalNaccumView;
		}
		public void setCapitalNaccumView(boolean capitalNaccumView) {
			this.capitalNaccumView = capitalNaccumView;
		}
		public boolean isPaidUpCapitalNaccumShareView() {
			return paidUpCapitalNaccumShareView;
		}
		public void setPaidUpCapitalNaccumShareView(boolean paidUpCapitalNaccumShareView) {
			this.paidUpCapitalNaccumShareView = paidUpCapitalNaccumShareView;
		}
		public boolean isCategoryView() {
			return categoryView;
		}
		public void setCategoryView(boolean categoryView) {
			this.categoryView = categoryView;
		}
		public boolean isNoOfCategoryView() {
			return noOfCategoryView;
		}
		public void setNoOfCategoryView(boolean noOfCategoryView) {
			this.noOfCategoryView = noOfCategoryView;
		}
		public boolean isMainCategoryView() {
			return mainCategoryView;
		}
		public void setMainCategoryView(boolean mainCategoryView) {
			this.mainCategoryView = mainCategoryView;
		}
		public boolean isSubCategoryView() {
			return subCategoryView;
		}
		public void setSubCategoryView(boolean subCategoryView) {
			this.subCategoryView = subCategoryView;
		}
		public boolean isSubCategoryDivisionView() {
			return subCategoryDivisionView;
		}
		public void setSubCategoryDivisionView(boolean subCategoryDivisionView) {
			this.subCategoryDivisionView = subCategoryDivisionView;
		}
		public boolean isRoleView() {
			return roleView;
		}
		public void setRoleView(boolean roleView) {
			this.roleView = roleView;
		}
		public boolean isApplicationStatusView() {
			return applicationStatusView;
		}
		public void setApplicationStatusView(boolean applicationStatusView) {
			this.applicationStatusView = applicationStatusView;
		}
		public boolean isCancelledByView() {
			return cancelledByView;
		}
		public void setCancelledByView(boolean cancelledByView) {
			this.cancelledByView = cancelledByView;
		}
		public boolean isPaymentTypeView() {
			return paymentTypeView;
		}
		public void setPaymentTypeView(boolean paymentTypeView) {
			this.paymentTypeView = paymentTypeView;
		}
		public boolean isApplicationTypeView() {
			return applicationTypeView;
		}
		public void setApplicationTypeView(boolean applicationTypeView) {
			this.applicationTypeView = applicationTypeView;
		}
		public boolean isStateView() {
			return stateView;
		}
		public void setStateView(boolean stateView) {
			this.stateView = stateView;
		}
		public boolean isDistrictView() {
			return districtView;
		}
		public void setDistrictView(boolean districtView) {
			this.districtView = districtView;
		}
		public boolean isSupplierNameView() {
			return supplierNameView;
		}
		public void setSupplierNameView(boolean supplierNameView) {
			this.supplierNameView = supplierNameView;
		}
		public boolean isReportTypeView() {
			return reportTypeView;
		}
		public void setReportTypeView(boolean reportTypeView) {
			this.reportTypeView = reportTypeView;
		}
		public boolean isOrganizationTypeView() {
			return organizationTypeView;
		}
		public void setOrganizationTypeView(boolean organizationTypeView) {
			this.organizationTypeView = organizationTypeView;
		}
		public boolean isKementerianView() {
			return kementerianView;
		}
		public void setKementerianView(boolean kementerianView) {
			this.kementerianView = kementerianView;
		}
		public boolean isNamAgensiView() {
			return namAgensiView;
		}
		public void setNamAgensiView(boolean namAgensiView) {
			this.namAgensiView = namAgensiView;
		}
		public boolean isComplaintNoView() {
			return complaintNoView;
		}
		public void setComplaintNoView(boolean complaintNoView) {
			this.complaintNoView = complaintNoView;
		}		
		
		public String getEpNumberTotVar() {
			return epNumberTotVar;
		}
		public void setEpNumberTotVar(String epNumberTotVar) {
			this.epNumberTotVar = epNumberTotVar;
		}
		public String getBusinessRegNoTotVar() {
			return businessRegNoTotVar;
		}
		public void setBusinessRegNoTotVar(String businessRegNoTotVar) {
			this.businessRegNoTotVar = businessRegNoTotVar;
		}
		public String getIcNumberTotVar() {
			return icNumberTotVar;
		}
		public void setIcNumberTotVar(String icNumberTotVar) {
			this.icNumberTotVar = icNumberTotVar;
		}
		public String getMofNumberTotVar() {
			return mofNumberTotVar;
		}
		public void setMofNumberTotVar(String mofNumberTotVar) {
			this.mofNumberTotVar = mofNumberTotVar;
		}
		public boolean isBasicRegView() {
			return basicRegView;
		}
		public void setBasicRegView(boolean basicRegView) {
			this.basicRegView = basicRegView;
		}
		public boolean isMofRegView() {
			return mofRegView;
		}
		public void setMofRegView(boolean mofRegView) {
			this.mofRegView = mofRegView;
		}
		public String getExtentionTypeTotVar() {
			return extentionTypeTotVar;
		}
		public void setExtentionTypeTotVar(String extentionTypeTotVar) {
			this.extentionTypeTotVar = extentionTypeTotVar;
		}
		public String getExtentionBrandTotVar() {
			return extentionBrandTotVar;
		}
		public void setExtentionBrandTotVar(String extentionBrandTotVar) {
			this.extentionBrandTotVar = extentionBrandTotVar;
		}
		public String getExtentionMeasurmentTotVar() {
			return extentionMeasurmentTotVar;
		}
		public void setExtentionMeasurmentTotVar(String extentionMeasurmentTotVar) {
			this.extentionMeasurmentTotVar = extentionMeasurmentTotVar;
		}
		public String getExtentionColourTotVar() {
			return extentionColourTotVar;
		}
		public void setExtentionColourTotVar(String extentionColourTotVar) {
			this.extentionColourTotVar = extentionColourTotVar;
		}
		public String getFromDateVar() {
			return fromDateVar;
		}
		public void setFromDateVar(String fromDateVar) {
			this.fromDateVar = fromDateVar;
		}
		public String getToDateVar() {
			return toDateVar;
		}
		public void setToDateVar(String toDateVar) {
			this.toDateVar = toDateVar;
		}
		private String urlReportJSPName=ReportConst.EMPTY_VAL;
		//URL variables end
		
		public String getUrlReportJSPName() {
			return urlReportJSPName;
		}
		public void setUrlReportJSPName(String urlReportJSPName) {
			this.urlReportJSPName = urlReportJSPName;
		}
		public String getQuotationIdTotVar() {
			return quotationIdTotVar;
		}
		public void setQuotationIdTotVar(String quotationIdTotVar) {
			this.quotationIdTotVar = quotationIdTotVar;
		}
		public String getItemTypeTotVar() {
			return itemTypeTotVar;
		}
		public void setItemTypeTotVar(String itemTypeTotVar) {
			this.itemTypeTotVar = itemTypeTotVar;
		}
		public String getItemStatusTotVar() {
			return itemStatusTotVar;
		}
		public void setItemStatusTotVar(String itemStatusTotVar) {
			this.itemStatusTotVar = itemStatusTotVar;
		}
		public String getUnspscCodeTotVar() {
			return unspscCodeTotVar;
		}
		public void setUnspscCodeTotVar(String unspscCodeTotVar) {
			this.unspscCodeTotVar = unspscCodeTotVar;
		}
		public String getUnspscTitleTotVar() {
			return unspscTitleTotVar;
		}
		public void setUnspscTitleTotVar(String unspscTitleTotVar) {
			this.unspscTitleTotVar = unspscTitleTotVar;
		}
		public String getExtentionCodeTotVar() {
			return extentionCodeTotVar;
		}
		public void setExtentionCodeTotVar(String extentionCodeTotVar) {
			this.extentionCodeTotVar = extentionCodeTotVar;
		}
		public String getCompanyNameTotVar() {
			return companyNameTotVar;
		}
		public void setCompanyNameTotVar(String companyNameTotVar) {
			this.companyNameTotVar = companyNameTotVar;
		}
		public String getRegistrationTypeTotVar() {
			return registrationTypeTotVar;
		}
		public void setRegistrationTypeTotVar(String registrationTypeTotVar) {
			this.registrationTypeTotVar = registrationTypeTotVar;
		}
		public String getItemCategoryTotVar() {
			return itemCategoryTotVar;
		}
		public void setItemCategoryTotVar(String itemCategoryTotVar) {
			this.itemCategoryTotVar = itemCategoryTotVar;
		}
		public String getItemNameTotVar() {
			return itemNameTotVar;
		}
		public void setItemNameTotVar(String itemNameTotVar) {
			this.itemNameTotVar = itemNameTotVar;
		}
		public String getProofIdentityTotVar() {
			return proofIdentityTotVar;
		}
		public void setProofIdentityTotVar(String proofIdentityTotVar) {
			this.proofIdentityTotVar = proofIdentityTotVar;
		}
		public String getPtjTotVar() {
			return ptjTotVar;
		}
		public void setPtjTotVar(String ptjTotVar) {
			this.ptjTotVar = ptjTotVar;
		}
		public String getContractNoTotVar() {
			return contractNoTotVar;
		}
		public void setContractNoTotVar(String contractNoTotVar) {
			this.contractNoTotVar = contractNoTotVar;
		}
		public String getSstTotVar() {
			return sstTotVar;
		}
		public void setSstTotVar(String sstTotVar) {
			this.sstTotVar = sstTotVar;
		}
		public String getJabatanTotVar() {
			return jabatanTotVar;
		}
		public void setJabatanTotVar(String jabatanTotVar) {
			this.jabatanTotVar = jabatanTotVar;
		}
		public String getMinistryTotVar() {
			return ministryTotVar;
		}
		public void setMinistryTotVar(String ministryTotVar) {
			this.ministryTotVar = ministryTotVar;
		}
		public String getSupplierTotVar() {
			return supplierTotVar;
		}
		public void setSupplierTotVar(String supplierTotVar) {
			this.supplierTotVar = supplierTotVar;
		}
		public String getStatusTotVar() {
			return statusTotVar;
		}
		public void setStatusTotVar(String statusTotVar) {
			this.statusTotVar = statusTotVar;
		}
		public String getDurationTotVar() {
			return durationTotVar;
		}
		public void setDurationTotVar(String durationTotVar) {
			this.durationTotVar = durationTotVar;
		}
		public String getRecievingOfficerTotVar() {
			return recievingOfficerTotVar;
		}
		public void setRecievingOfficerTotVar(String recievingOfficerTotVar) {
			this.recievingOfficerTotVar = recievingOfficerTotVar;
		}
		public String getDeliveryAddressTotVar() {
			return deliveryAddressTotVar;
		}
		public void setDeliveryAddressTotVar(String deliveryAddressTotVar) {
			this.deliveryAddressTotVar = deliveryAddressTotVar;
		}
		public String getFlRecNoteNoTotVar() {
			return flRecNoteNoTotVar;
		}
		public void setFlRecNoteNoTotVar(String flRecNoteNoTotVar) {
			this.flRecNoteNoTotVar = flRecNoteNoTotVar;
		}
		public String getContractTypeTotVar() {
			return contractTypeTotVar;
		}
		public void setContractTypeTotVar(String contractTypeTotVar) {
			this.contractTypeTotVar = contractTypeTotVar;
		}
		public String getRequisitionerTotVar() {
			return requisitionerTotVar;
		}
		public void setRequisitionerTotVar(String requisitionerTotVar) {
			this.requisitionerTotVar = requisitionerTotVar;
		}
		public String getMtoContractTotVar() {
			return mtoContractTotVar;
		}
		public void setMtoContractTotVar(String mtoContractTotVar) {
			this.mtoContractTotVar = mtoContractTotVar;
		}
		public String getInvoiceNoTotVar() {
			return invoiceNoTotVar;
		}
		public void setInvoiceNoTotVar(String invoiceNoTotVar) {
			this.invoiceNoTotVar = invoiceNoTotVar;
		}
		public String getDeductionTypeTotVar() {
			return deductionTypeTotVar;
		}
		public void setDeductionTypeTotVar(String deductionTypeTotVar) {
			this.deductionTypeTotVar = deductionTypeTotVar;
		}
		public String getCrNoTotVar() {
			return crNoTotVar;
		}
		public void setCrNoTotVar(String crNoTotVar) {
			this.crNoTotVar = crNoTotVar;
		}
		public String getApproverNameTotVar() {
			return approverNameTotVar;
		}
		public void setApproverNameTotVar(String approverNameTotVar) {
			this.approverNameTotVar = approverNameTotVar;
		}
		public String getDocumentTypeTotVar() {
			return documentTypeTotVar;
		}
		public void setDocumentTypeTotVar(String documentTypeTotVar) {
			this.documentTypeTotVar = documentTypeTotVar;
		}
		public String getCriteriaTotVar() {
			return criteriaTotVar;
		}
		public void setCriteriaTotVar(String criteriaTotVar) {
			this.criteriaTotVar = criteriaTotVar;
		}
		public String getScoringTotVar() {
			return scoringTotVar;
		}
		public void setScoringTotVar(String scoringTotVar) {
			this.scoringTotVar = scoringTotVar;
		}
		public String getPkpNoTotVar() {
			return pkpNoTotVar;
		}
		public void setPkpNoTotVar(String pkpNoTotVar) {
			this.pkpNoTotVar = pkpNoTotVar;
		}
		public String getDeliveryOrderNoTotVar() {
			return deliveryOrderNoTotVar;
		}
		public void setDeliveryOrderNoTotVar(String deliveryOrderNoTotVar) {
			this.deliveryOrderNoTotVar = deliveryOrderNoTotVar;
		}
		public String getItemCodeTotVar() {
			return itemCodeTotVar;
		}
		public void setItemCodeTotVar(String itemCodeTotVar) {
			this.itemCodeTotVar = itemCodeTotVar;
		}
		public String getPoAmountTotVar() {
			return poAmountTotVar;
		}
		public void setPoAmountTotVar(String poAmountTotVar) {
			this.poAmountTotVar = poAmountTotVar;
		}
		public String getPrNoTotVar() {
			return prNoTotVar;
		}
		public void setPrNoTotVar(String prNoTotVar) {
			this.prNoTotVar = prNoTotVar;
		}
		public String getPmoTotVar() {
			return pmoTotVar;
		}
		public void setPmoTotVar(String pmoTotVar) {
			this.pmoTotVar = pmoTotVar;
		}
		
		public boolean isPmoView() {
			return pmoView;
		}
		public void setPmoView(boolean pmoView) {
			this.pmoView = pmoView;
		}	
		public boolean isPtjView() {
			return ptjView;
		}
		public void setPtjView(boolean ptjView) {
			this.ptjView = ptjView;
		}
		public boolean isContractNoView() {
			return contractNoView;
		}
		public void setContractNoView(boolean contractNoView) {
			this.contractNoView = contractNoView;
		}
		public boolean isSstView() {
			return sstView;
		}
		public void setSstView(boolean sstView) {
			this.sstView = sstView;
		}
		public boolean isJabatanView() {
			return jabatanView;
		}
		public void setJabatanView(boolean jabatanView) {
			this.jabatanView = jabatanView;
		}
		public boolean isMinistryView() {
			return ministryView;
		}
		public void setMinistryView(boolean ministryView) {
			this.ministryView = ministryView;
		}
		public boolean isSupplierView() {
			return supplierView;
		}
		public void setSupplierView(boolean supplierView) {
			this.supplierView = supplierView;
		}
		public boolean isStatusView() {
			return statusView;
		}
		public void setStatusView(boolean statusView) {
			this.statusView = statusView;
		}
		public boolean isDurationView() {
			return durationView;
		}
		public void setDurationView(boolean durationView) {
			this.durationView = durationView;
		}
		public boolean isRecievingOfficerView() {
			return recievingOfficerView;
		}
		public void setRecievingOfficerView(boolean recievingOfficerView) {
			this.recievingOfficerView = recievingOfficerView;
		}
		public boolean isDeliveryAddressView() {
			return deliveryAddressView;
		}
		public void setDeliveryAddressView(boolean deliveryAddressView) {
			this.deliveryAddressView = deliveryAddressView;
		}
		public boolean isFlRecNoteNoView() {
			return flRecNoteNoView;
		}
		public void setFlRecNoteNoView(boolean flRecNoteNoView) {
			this.flRecNoteNoView = flRecNoteNoView;
		}
		public boolean isContractTypeView() {
			return contractTypeView;
		}
		public void setContractTypeView(boolean contractTypeView) {
			this.contractTypeView = contractTypeView;
		}
		public boolean isRequisitionerView() {
			return requisitionerView;
		}
		public void setRequisitionerView(boolean requisitionerView) {
			this.requisitionerView = requisitionerView;
		}
		public boolean isMtoContractView() {
			return mtoContractView;
		}
		public void setMtoContractView(boolean mtoContractView) {
			this.mtoContractView = mtoContractView;
		}
		public boolean isInvoiceNoView() {
			return invoiceNoView;
		}
		public void setInvoiceNoView(boolean invoiceNoView) {
			this.invoiceNoView = invoiceNoView;
		}
		public boolean isDeductionTypeView() {
			return deductionTypeView;
		}
		public void setDeductionTypeView(boolean deductionTypeView) {
			this.deductionTypeView = deductionTypeView;
		}
		public boolean isCrNoView() {
			return crNoView;
		}
		public void setCrNoView(boolean crNoView) {
			this.crNoView = crNoView;
		}
		public boolean isApproverNameView() {
			return approverNameView;
		}
		public void setApproverNameView(boolean approverNameView) {
			this.approverNameView = approverNameView;
		}
		public boolean isDocumentTypeView() {
			return documentTypeView;
		}
		public void setDocumentTypeView(boolean documentTypeView) {
			this.documentTypeView = documentTypeView;
		}
		public boolean isCriteriaView() {
			return criteriaView;
		}
		public void setCriteriaView(boolean criteriaView) {
			this.criteriaView = criteriaView;
		}
		public boolean isScoringView() {
			return scoringView;
		}
		public void setScoringView(boolean scoringView) {
			this.scoringView = scoringView;
		}
		public boolean isPkpNoView() {
			return pkpNoView;
		}
		public void setPkpNoView(boolean pkpNoView) {
			this.pkpNoView = pkpNoView;
		}
		public boolean isDeliveryOrderNoView() {
			return deliveryOrderNoView;
		}
		public void setDeliveryOrderNoView(boolean deliveryOrderNoView) {
			this.deliveryOrderNoView = deliveryOrderNoView;
		}	
		public boolean isItemCodeView() {
			return itemCodeView;
		}
		public void setItemCodeView(boolean itemCodeView) {
			this.itemCodeView = itemCodeView;
		}
		public boolean isPoAmountView() {
			return poAmountView;
		}
		public void setPoAmountView(boolean poAmountView) {
			this.poAmountView = poAmountView;
		}
		public boolean isPrNoView() {
			return prNoView;
		}
		public void setPrNoView(boolean prNoView) {
			this.prNoView = prNoView;
		}
		
		
		public boolean isProofIdentityView() {
			return proofIdentityView;
		}
		public void setProofIdentityView(boolean proofIdentityView) {
			this.proofIdentityView = proofIdentityView;
		}
		public boolean isItemCategoryView() {
			return itemCategoryView;
		}
		public void setItemCategoryView(boolean itemCategoryView) {
			this.itemCategoryView = itemCategoryView;
		}
		public boolean isItemNameView() {
			return itemNameView;
		}
		public void setItemNameView(boolean itemNameView) {
			this.itemNameView = itemNameView;
		}		
		public boolean isQuotationNoView() {
			return quotationNoView;
		}
		public void setQuotationNoView(boolean quotationNoView) {
			this.quotationNoView = quotationNoView;
		}
		public boolean isPaymentIdView() {
			return paymentIdView;
		}
		public void setPaymentIdView(boolean paymentIdIdView) {
			this.paymentIdView = paymentIdIdView;
		}	
		public boolean isItemTypeView() {
			return itemTypeView;
		}
		public void setItemTypeView(boolean itemTypeView) {
			this.itemTypeView = itemTypeView;
		}
		public boolean isItemStatusView() {
			return itemStatusView;
		}
		public void setItemStatusView(boolean itemStatusView) {
			this.itemStatusView = itemStatusView;
		}
		public boolean isUnspscCodeView() {
			return unspscCodeView;
		}
		public void setUnspscCodeView(boolean unspscCodeView) {
			this.unspscCodeView = unspscCodeView;
		}
		public boolean isUnspscTitleView() {
			return unspscTitleView;
		}
		public void setUnspscTitleView(boolean unspscTitleView) {
			this.unspscTitleView = unspscTitleView;
		}
		public boolean isExtentionCodeView() {
			return extentionCodeView;
		}
		public void setExtentionCodeView(boolean extentionCodeView) {
			this.extentionCodeView = extentionCodeView;
		}
		public boolean isCompanyNameView() {
			return companyNameView;
		}
		public void setCompanyNameView(boolean companyNameView) {
			this.companyNameView = companyNameView;
		}
		public boolean isRegistrationTypeView() {
			return registrationTypeView;
		}
		public void setRegistrationTypeView(boolean registrationTypeView) {
			this.registrationTypeView = registrationTypeView;
		}
		public boolean isFromDateView() {
			return fromDateView;
		}
		public void setFromDateView(boolean fromDateView) {
			this.fromDateView = fromDateView;
		}
		public boolean isToDateView() {
			return toDateView;
		}
		public void setToDateView(boolean toDateView) {
			this.toDateView = toDateView;
		}
}
