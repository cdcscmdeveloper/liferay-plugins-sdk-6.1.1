package my.ep.web.backingbean.sm.supplieralert;

import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.supplieralert.SupplierAlertManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

@ManagedBean(name = "supplierAlertBackingBean")
@RequestScoped
public class SupplierAlertBackingBean extends
		BaseBackingBean<SupplierAlertManagedBean> {
	/**
	 * Pre - render MOF expiry alert page
	 */
	public void preRenderMofExpiryAlert() {
		renewSesionObject();
		SmSupplier supp = SmApplicationUtil.getCurrentSupplier();
		if (supp != null) {
			SmMofAccount acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(
					supp.getSupplierId(), supp.getSmAppl().getSupplierType());
			if (acc != null
					&& acc.getExpDate() != null
					&& acc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				Date currDate = new Date();
				Calendar currDateAfterThreeMonths = Calendar.getInstance();
				currDateAfterThreeMonths.setTime(currDate);
				currDateAfterThreeMonths.add(Calendar.MONTH, 3);
				if (currDateAfterThreeMonths.getTime().after(acc.getExpDate())) {
					getManagedBean().setMofExpiryLeftDaysMsg(
							SmCommonUtil.getMessage(
									"lbl.sm.suppalert.mofexpiry.msg", null));
					getManagedBean().setExpDate(acc.getExpDate());
					initCountDown();
				}
			}
		}
		SmCommonUtil.redirectToPage("mofExpiryAlertPage.xhtml", "",null);
	}

	public void initCountDown() {
		Date currDate = new Date();
		long togo = (getManagedBean().getExpDate().getTime() - currDate
				.getTime()) / 1000;
		getManagedBean().setSecs((togo % 60) + "");
		togo = togo / 60;
		getManagedBean().setMins((togo % 60) + "");
		togo = togo / 60;
		getManagedBean().setHours((togo % 24) + "");
		togo = togo / 24;
		getManagedBean().setDays((togo % 7) + "");
	}
}
