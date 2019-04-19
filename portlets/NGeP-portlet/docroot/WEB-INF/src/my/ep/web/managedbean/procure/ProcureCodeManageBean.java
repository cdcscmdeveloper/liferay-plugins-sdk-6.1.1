package my.ep.web.managedbean.procure;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
*
* @author David
*/
@ManagedBean(name="procureMB")
@SessionScoped
public class ProcureCodeManageBean { 
    
	
	private List<ProcureList> procurementlist;

	public List<ProcureList> getProcurementlist() {
		
		if(procurementlist==null){
			procurementlist = new ArrayList<ProcureList>();
		
	    ProcureList pro = new ProcureList();
		ProcureList bro = new ProcureList();
		ProcureList cro = new ProcureList();
		ProcureList dro = new ProcureList();
		
		pro.setPlanNumber(1);
		pro.setDescript("Abcdef");
		pro.setModes("urgent");
		pro.setPlannedAmount(001);
		pro.setPlanStatus("Activate");
		pro.setCreations("9/2/13 ");
		pro.setDays(5);
		pro.setTender("on");
		pro.setAction("Delete");
		
		bro.setPlanNumber(2);
		bro.setDescript("This is plan B");
		bro.setModes("urgent");
		bro.setPlannedAmount(001);
		bro.setPlanStatus("Activate");
		bro.setCreations("10/11/13 ");
		bro.setDays(5);
		bro.setTender("on");
		bro.setAction("Delete");

		cro.setPlanNumber(3);
		cro.setDescript("This is Plan C ");
		cro.setModes("urgent");
		cro.setPlannedAmount(003);
		cro.setPlanStatus("Activate");
		cro.setCreations("12/1/13 ");
		cro.setDays(10);
		cro.setTender("on");
		cro.setAction("Delete");
		
		dro.setPlanNumber(4);
		dro.setDescript("Abcdef");
		dro.setModes("urgent");
		dro.setPlannedAmount(001);
		dro.setPlanStatus("Activate");
		dro.setCreations("11/3/13 ");
		dro.setDays(5);
		dro.setTender("off");
		dro.setAction("Edit");
		
		
		procurementlist.add(pro);
		procurementlist.add(bro);
		procurementlist.add(cro);
		procurementlist.add(dro);
		
		}
		return procurementlist;
	}

	public void setProcurementlist(List<ProcureList> procurementlist) {
		this.procurementlist = procurementlist;
	}

	

	
	
	
	/*public ProcureCodeManageBean() {
    } 
    public ArrayList<ProcureList> getProcurementList() {
        ArrayList<ProcureList> procurementlist = new ArrayList<ProcureList>();
        procurementlist.add(new ProcureList(1, "Bangalore", "g", 4.2, "Bangalore", "Bangalore", 
        		"Bangalore", "Bangalore", "Bangalore", "Bangalore"));

        return procurementlist;
    }*/
} 
