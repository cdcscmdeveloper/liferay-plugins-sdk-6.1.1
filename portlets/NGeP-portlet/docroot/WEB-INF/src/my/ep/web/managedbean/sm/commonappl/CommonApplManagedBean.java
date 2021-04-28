package my.ep.web.managedbean.sm.commonappl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "commonApplMB")
@SessionScoped
public class CommonApplManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private String[] bpmAction;
	private String taskName;
	private List<SmPersonnel> personnelList;
	private List<SmPersonnel> selectPersonnelList;
	private List<Double> payList;
	private List<Double> finalPayList;
	private BigDecimal totalAmount;
	private int pListSize;
	private String applType;
	//10724
	private Date expDate;
	private String days;
	private String hours;
	private String mins;
	private String secs;		
	private String mofBeforeExpiryMsg;
	private String mofAfterExpiryMsg;

	public String[] getBpmAction() {
		return bpmAction;
	}
	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public List<SmPersonnel> getPersonnelList() {
		return personnelList;
	}
	public void setPersonnelList(List<SmPersonnel> personnelList) {
		this.personnelList = personnelList;
	}
	public List<SmPersonnel> getSelectPersonnelList() {
		return selectPersonnelList;
	}
	public void setSelectPersonnelList(List<SmPersonnel> selectPersonnelList) {
		this.selectPersonnelList = selectPersonnelList;
	}
	public List<Double> getPayList() {
		if(payList == null)
			payList = new ArrayList<Double>();
		return payList;
	}
	public void setPayList(List<Double> payList) {
		this.payList = payList;
	}
	public List<Double> getFinalPayList() {
		if(finalPayList == null)
			finalPayList = new ArrayList<Double>();
		return finalPayList;
	}
	public void setFinalPayList(List<Double> finalPayList) {
		this.finalPayList = finalPayList;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getpListSize() {
		return pListSize;
	}
	public void setpListSize(int pListSize) {
		this.pListSize = pListSize;
	}
	public String getApplType() {
		return applType;
	}
	public void setApplType(String applType) {
		this.applType = applType;
	}
	//10724 START
		public Date getExpDate() {
			return expDate;
		}

		public void setExpDate(Date expDate) {
			this.expDate = expDate;
		}

		public String getDays() {
			return days;
		}

		public void setDays(String days) {
			this.days = days;
		}

		public String getHours() {
			return hours;
		}

		public void setHours(String hours) {
			this.hours = hours;
		}

		public String getMins() {
			return mins;
		}

		public void setMins(String mins) {
			this.mins = mins;
		}

		public String getSecs() {
			return secs;
		}

		public void setSecs(String secs) {
			this.secs = secs;
		}

		public String getMofBeforeExpiryMsg() {
			return mofBeforeExpiryMsg;
		}

		public void setMofBeforeExpiryMsg(String mofBeforeExpiryMsg) {
			this.mofBeforeExpiryMsg = mofBeforeExpiryMsg;
		}

		public String getMofAfterExpiryMsg() {
			return mofAfterExpiryMsg;
		}

		public void setMofAfterExpiryMsg(String mofAfterExpiryMsg) {
			this.mofAfterExpiryMsg = mofAfterExpiryMsg;
		}
		//10724 END
}
