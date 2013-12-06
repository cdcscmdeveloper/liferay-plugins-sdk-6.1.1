package my.ep.web.managedbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmApplSiteVisit;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "svoSiteVisitMB")
@SessionScoped
public class SvoSiteVisitManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private long appId;
	private String[] bpmAction;
	private String taskId;
	private String appType;
	
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String[] getBpmAction() {
		return bpmAction;
	}

	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	private SmApplSiteVisit smApplSv;
	private List<SiteVisitDetails> svDetailList;
	private List<SiteVisitCheckList> svCheckList;
	private List<ManufacturingFinancialList> manuFactList;
	private List<SiteVisitDetInfoList> svDetInfoList;
	private List<SiteVisitReportList> svReportList;

	private String svReviewNavi;
	private String rptSubsMethod;

	public SmApplSiteVisit getSmApplSv() {
		return smApplSv;
	}

	public void setSmApplSv(SmApplSiteVisit smApplSv) {
		this.smApplSv = smApplSv;
	}

	public String getSvReviewNavi() {
		return svReviewNavi;
	}

	public void setSvReviewNavi(String svReviewNavi) {
		this.svReviewNavi = svReviewNavi;
	}

	public List<SiteVisitReportList> getSvReportList() {
		return svReportList;
	}

	public void setSvReportList(List<SiteVisitReportList> svReportList) {
		this.svReportList = svReportList;
	}

	public List<SiteVisitDetInfoList> getSvDetInfoList() {
		return svDetInfoList;
	}

	public void setSvDetInfoList(List<SiteVisitDetInfoList> svDetInfoList) {
		this.svDetInfoList = svDetInfoList;
	}

	public List<SiteVisitDetails> getSvDetailList() {
		return svDetailList;
	}

	public void setSvDetailList(List<SiteVisitDetails> svDetailList) {
		this.svDetailList = svDetailList;
	}

	public List<SiteVisitCheckList> getSvCheckList() {
		return svCheckList;
	}

	public void setSvCheckList(List<SiteVisitCheckList> svCheckList) {
		this.svCheckList = svCheckList;
	}

	public List<ManufacturingFinancialList> getManuFactList() {
		return manuFactList;
	}

	public void setManuFactList(List<ManufacturingFinancialList> manuFactList) {
		this.manuFactList = manuFactList;
	}

	public String getRptSubsMethod() {
		return rptSubsMethod;
	}

	public void setRptSubsMethod(String rptSubsMethod) {
		this.rptSubsMethod = rptSubsMethod;
	}

	public SvoSiteVisitManagedBean() {
		svDetailList = new ArrayList<SiteVisitDetails>();
		svDetailList.add(new SiteVisitDetails("Test Sdn Bhd", "Abc123",
				"Pending Review", new Date(), new Date()));
		svDetailList.add(new SiteVisitDetails("Demo Sdn Bhd", "Abc123",
				"Pending Review", new Date(), new Date()));

		svCheckList = new ArrayList<SiteVisitCheckList>();
		svCheckList.add(new SiteVisitCheckList(1,
				"Semakan Sejarah (Kepilkan Jika Ada)"));
		svCheckList.add(new SiteVisitCheckList(2, "Peta Lakar Ke Premis"));
		svCheckList.add(new SiteVisitCheckList(3, "Sebab-Sebab Disyor Lawat:"));
		svCheckList.add(new SiteVisitCheckList(4, "Pegawai Pelawat"));
		svCheckList.add(new SiteVisitCheckList(5,
				"Syor Pegawai Lawatan: LULUS / TOLAK"));
		svCheckList.add(new SiteVisitCheckList(6,
				"Serahan Kepada Penyelaras Lawatan"));
		svCheckList.add(new SiteVisitCheckList(7, "Lawatan"));
		svCheckList.add(new SiteVisitCheckList(8,
				"Masukkan Laporan Dalam Sistem"));
		svCheckList.add(new SiteVisitCheckList(9,
				"Serahan Kepada Pegawai Proses"));
		svCheckList.add(new SiteVisitCheckList(10,
				"Serahan Kepada Pegawai Pelulus"));
		svCheckList.add(new SiteVisitCheckList(11, "Surat Pemberitahuan"));
		svCheckList.add(new SiteVisitCheckList(12, "Tandatangan Surat"));

		manuFactList = new ArrayList<ManufacturingFinancialList>();
		manuFactList.add(new ManufacturingFinancialList(1, "Lesen Pembuat"));
		manuFactList.add(new ManufacturingFinancialList(2,
				"Lesen Pengecualian Pembuat"));
		manuFactList.add(new ManufacturingFinancialList(3,
				"Surat Pemeriksaan Kilang"));
		manuFactList.add(new ManufacturingFinancialList(4,
				"Keterangan Cukai Jualan C.J. 3"));
		manuFactList.add(new ManufacturingFinancialList(5,
				"Borang Kastam 8 dan 9"));

		svDetInfoList = new ArrayList<SiteVisitDetInfoList>();
		svDetInfoList.add(new SiteVisitDetInfoList("Jalan cyber 123", "47000",
				"CyberJaya", "Selangor", "Sepang", ""));
		svDetInfoList.add(new SiteVisitDetInfoList("Jalan cyber 456", "47000",
				"CyberJaya", "Selangor", "Sepang", ""));

		svReportList = new ArrayList<SiteVisitReportList>();
		svReportList.add(new SiteVisitReportList("1", "Manufacturing",
				"Uploaded", "2013-01-04"));
	}

	public class SiteVisitDetails {
		public String suppName;
		public String appNo;
		public String accStage;
		public Date expSvDate;
		public Date rptUploadDate;

		public SiteVisitDetails(String suppName, String appNo, String accStage,
				Date expSvDate, Date rptUploadDate) {
			// SimpleDateFormat dt = new
			// SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");

			this.suppName = suppName;
			this.appNo = appNo;
			this.accStage = accStage;
			this.expSvDate = expSvDate;
			this.rptUploadDate = rptUploadDate;
		}

		public String getSuppName() {
			return suppName;
		}

		public void setSuppName(String suppName) {
			this.suppName = suppName;
		}

		public String getAppNo() {
			return appNo;
		}

		public void setAppNo(String appNo) {
			this.appNo = appNo;
		}

		public String getAccStage() {
			return accStage;
		}

		public void setAccStage(String accStage) {
			this.accStage = accStage;
		}

		public Date getExpSvDate() {
			return expSvDate;
		}

		public void setExpSvDate(Date expSvDate) {
			this.expSvDate = expSvDate;
		}

		public Date getRptUploadDate() {
			return rptUploadDate;
		}

		public void setRptUploadDate(Date rptUploadDate) {
			this.rptUploadDate = rptUploadDate;
		}
	}

	public class SiteVisitCheckList {
		public int checkNo;
		public String checkDesc;

		public SiteVisitCheckList(int num, String desc) {
			this.checkNo = num;
			this.checkDesc = desc;
		}

		public int getCheckNo() {
			return checkNo;
		}

		public void setCheckNo(int checkNo) {
			this.checkNo = checkNo;
		}

		public String getCheckDesc() {
			return checkDesc;
		}

		public void setCheckDesc(String checkDesc) {
			this.checkDesc = checkDesc;
		}
	}

	public class ManufacturingFinancialList {
		public int manuNo;
		public String manuDoc;
		public String manuReferee;
		public Date dateIssued;
		public String manuNotes;

		public ManufacturingFinancialList(int manuNo, String manuDoc) {
			this.manuDoc = manuDoc;
			this.manuNo = manuNo;
		}

		public int getManuNo() {
			return manuNo;
		}

		public void setManuNo(int manuNo) {
			this.manuNo = manuNo;
		}

		public String getManuDoc() {
			return manuDoc;
		}

		public void setManuDoc(String manuDoc) {
			this.manuDoc = manuDoc;
		}

		public String getManuReferee() {
			return manuReferee;
		}

		public void setManuReferee(String manuReferee) {
			this.manuReferee = manuReferee;
		}

		public Date getDateIssued() {
			return dateIssued;
		}

		public void setDateIssued(Date dateIssued) {
			this.dateIssued = dateIssued;
		}

		public String getManuNotes() {
			return manuNotes;
		}

		public void setManuNotes(String manuNotes) {
			this.manuNotes = manuNotes;
		}

	}

	public class SiteVisitDetInfoList {
		private String svAddress;
		private String svPostcode;
		private String svTownCity;
		private String svState;
		private String svDistrict;
		private String svAppType;

		public SiteVisitDetInfoList(String address, String postCode,
				String tCity, String state, String Dist, String appType) {

			this.svAddress = address;
			this.svPostcode = postCode;
			this.svTownCity = tCity;
			this.svState = state;
			this.svDistrict = Dist;
			this.svAppType = appType;

		}

		public String getSvAddress() {
			return svAddress;
		}

		public void setSvAddress(String svAddress) {
			this.svAddress = svAddress;
		}

		public String getSvPostcode() {
			return svPostcode;
		}

		public void setSvPostcode(String svPostcode) {
			this.svPostcode = svPostcode;
		}

		public String getSvTownCity() {
			return svTownCity;
		}

		public void setSvTownCity(String svTownCity) {
			this.svTownCity = svTownCity;
		}

		public String getSvState() {
			return svState;
		}

		public void setSvState(String svState) {
			this.svState = svState;
		}

		public String getSvDistrict() {
			return svDistrict;
		}

		public void setSvDistrict(String svDistrict) {
			this.svDistrict = svDistrict;
		}

		public String getSvAppType() {
			return svAppType;
		}

		public void setSvAppType(String svAppType) {
			this.svAppType = svAppType;
		}
	}

	public class SiteVisitReportList {
		private String siteID;
		private String category;
		private String uploadDate;
		private String currStatus;

		public SiteVisitReportList(String siteID, String category,
				String currStatus, String uploadDate) {
			this.siteID = siteID;
			this.category = category;
			this.uploadDate = uploadDate;
			this.currStatus = currStatus;
		}

		public String getSiteID() {
			return siteID;
		}

		public void setSiteID(String siteID) {
			this.siteID = siteID;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getUploadDate() {
			return uploadDate;
		}

		public void setUploadDate(String uploadDate) {
			this.uploadDate = uploadDate;
		}

		public String getCurrStatus() {
			return currStatus;
		}

		public void setCurrStatus(String currStatus) {
			this.currStatus = currStatus;
		}

	}
}
