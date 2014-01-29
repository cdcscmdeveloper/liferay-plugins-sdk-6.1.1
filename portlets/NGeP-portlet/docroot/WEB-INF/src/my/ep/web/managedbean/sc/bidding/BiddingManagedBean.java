package my.ep.web.managedbean.sc.bidding;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="biddingMB")
@RequestScoped
public class BiddingManagedBean extends BaseManagedBean{


private static final long serialVersionUID = 1L; 
	
		private long docNo; 
		private long bidNo; 
		private String bidTitle; 
		private Date createdDate; 
		private String createdBy; 
		private int spinNumber1; 
		private int spinNumber2; 
		private int spinNumber3; 
		private int spinNumber4; 
		private int spinNumber5; 
		
		
		
		
		public long getDocNo() {
			return docNo;
		}
		public void setDocNo(long docNo) {
			this.docNo = docNo;
		}
		public long getBidNo() {
			return bidNo;
		}
		public void setBidNo(long bidNo) {
			this.bidNo = bidNo;
		}
		public String getBidTitle() {
			return bidTitle;
		}
		public void setBidTitle(String bidTitle) {
			this.bidTitle = bidTitle;
		}
		public Date getCreatedDate() {
			return createdDate;
		}
		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}
		public String getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}
		public int getSpinNumber1() {
			return spinNumber1;
		}
		public void setSpinNumber1(int spinNumber1) {
			this.spinNumber1 = spinNumber1;
		}
		public int getSpinNumber2() {
			return spinNumber2;
		}
		public void setSpinNumber2(int spinNumber2) {
			this.spinNumber2 = spinNumber2;
		}
		public int getSpinNumber3() {
			return spinNumber3;
		}
		public void setSpinNumber3(int spinNumber3) {
			this.spinNumber3 = spinNumber3;
		}
		public int getSpinNumber4() {
			return spinNumber4;
		}
		public void setSpinNumber4(int spinNumber4) {
			this.spinNumber4 = spinNumber4;
		}
		public int getSpinNumber5() {
			return spinNumber5;
		}
		public void setSpinNumber5(int spinNumber5) {
			this.spinNumber5 = spinNumber5;
		}
		
		
	}

	
	

	