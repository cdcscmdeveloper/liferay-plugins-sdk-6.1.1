package my.ep.web.managedbean.ss.procure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanMaster;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;

/**
*
* @author David
*/
@ManagedBean(name="procureMB")
@SessionScoped
public class ProcureCodeManageBean extends BaseManagedBean{ 
    private	static String datePatern = "MM/dd/yyyy";
	private	List<PpPlan> procurementPlanList = new ArrayList<PpPlan>();
	public ProcureCodeManageBean() throws Exception{
		super();
		prepareDummyData();
	}
	public void prepareDummyData() throws Exception{
		PpPlan pro = new PpPlan();
		PpPlan bro = new PpPlan();
		PpPlan cro = new PpPlan();
		PpPlan dro = new PpPlan();
		
		pro.setPlanNo("1");
		pro.setPlanDesc("Abcdef");
		PpPlanMaster ppPlanMaster = new PpPlanMaster();
		ppPlanMaster.setProcurementMode("Emergency");
		pro.setPpPlanMaster(ppPlanMaster);
		pro.setActualAmount(new BigDecimal("001"));
		pro.setStatusId(1L);
		pro.setCreatedDate(DateUtils.convertStringToTimestamp(datePatern,"09/02/2013 "));
		pro.setNotificationDays(5);
		
		bro.setPlanNo("2");
		bro.setPlanDesc("This is plan B");
		PpPlanMaster ppPlanMaster1 = new PpPlanMaster();
		ppPlanMaster1.setProcurementMode("Emergency");
		bro.setPpPlanMaster(ppPlanMaster1);
		bro.setActualAmount(new BigDecimal("001"));
		bro.setStatusId(1L);
		bro.setCreatedDate(DateUtils.convertStringToTimestamp(datePatern,"09/02/2013 "));
		bro.setNotificationDays(5);

		cro.setPlanNo("3");
		cro.setPlanDesc("This is Plan C ");
		PpPlanMaster ppPlanMaster2 = new PpPlanMaster();
		ppPlanMaster2.setProcurementMode("Emergency");
		cro.setPpPlanMaster(ppPlanMaster2);
		cro.setActualAmount(new BigDecimal("003"));
		cro.setStatusId(1L);
		cro.setCreatedDate(DateUtils.convertStringToTimestamp(datePatern,"09/02/2013 "));
		cro.setNotificationDays(10);
		
		dro.setPlanNo("4");
		dro.setPlanDesc("Abcdef");
		PpPlanMaster ppPlanMaster3 = new PpPlanMaster();
		ppPlanMaster3.setProcurementMode("Emergency");
		dro.setPpPlanMaster(ppPlanMaster3);
		dro.setActualAmount(new BigDecimal("001"));
		dro.setStatusId(1L);
		dro.setCreatedDate(DateUtils.convertStringToTimestamp(datePatern,"09/02/2013 "));
		dro.setNotificationDays(5);
		
		procurementPlanList.add(pro);
		procurementPlanList.add(bro);
		procurementPlanList.add(cro);
		procurementPlanList.add(dro);
	}
	public List<PpPlan> getProcurementPlanList() {
		return procurementPlanList;
	}
	public void setProcurementPlanList(List<PpPlan> procurementPlanList) {
		this.procurementPlanList = procurementPlanList;
	}
} 
