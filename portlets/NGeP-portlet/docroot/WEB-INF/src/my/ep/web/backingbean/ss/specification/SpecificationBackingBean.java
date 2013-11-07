/**
 * 
 */
package my.ep.web.backingbean.ss.specification;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.common.procurementcategory.IProcurementSO;
import my.ep.dto.pm.common.currency.CurrencyResDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementReqDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementResDTO;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmProcurementCategory;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ss.directpurchase.SimpleQuoteBidBean;
import my.ep.web.managedbean.ss.specification.SpecificationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author ACER
 *
 */
@ManagedBean(name = "specificationBackingBean")
@RequestScoped
public class SpecificationBackingBean extends
BaseBackingBean<SpecificationManagedBean>{
	 private int count;  
	
	 private List<SpecificationCompilanceBean> selectedCompilenceList;
	 private String selctValue;
	  
	    public int getCount() {  
	        return count;  
	    }  
	  
	    public void setCount(int count) {  
	        this.count = count;  
	    }  
public SpecificationBackingBean(){
	/*System.out.println("====constructor=====");
	ICurrencySO CurrencySO =  BusinessDelegate.getInstance()
				.getServiceObject("pm", ICurrencySO.class);
		System.out.println(CurrencySO);
		CurrencyResDTO resDTO=CurrencySO.retriveCurrencyList();
		System.out.println("======currency List Size===="+resDTO.getCurrencyList().size());
		CurrencyReqDTO reqDTO=new CurrencyReqDTO();
		reqDTO.setCurrencyCode("1");
		CurrencyResDTO resDTO1=CurrencySO.retriveCurrencyCodeByCodeId(reqDTO);
		System.out.println("======currency entity value===="+resDTO1.getPmCurrency().getCurrencyName());*/
	/*IProcurementSO CurrencySO =  BusinessDelegate.getInstance()
			.getServiceObject("pm", IProcurementSO.class);
	System.out.println(CurrencySO);
	ProcurementResDTO resDTO=CurrencySO.retriveProcurementList();
	System.out.println("======procurement List Size===="+resDTO.getProcurementList().size());
	ProcurementReqDTO reqDTO=new ProcurementReqDTO();
	reqDTO.setProcurementId(1);
	ProcurementResDTO resDTO1=CurrencySO.retriveProcurementCategoryById(reqDTO);
	System.out.println("======procurement name value===="+resDTO1.getProcureCategory().getCategoryName());
*/
	compilenceList();
	}
	
	public void save(){
		try{
		/*System.out.println("===========enter method========");
		System.out.println("===========this.getManagedBean().getScQtSpec().getEvaluationType()========"+this.getManagedBean().getScQtSpec().getEvaluationType());
		System.out.println("===========+this.getManagedBean().getScQtSpec().getPmProcurementCategory().getProcurementCategoryId()========"+this.getManagedBean().getScQtSpec().getPmProcurementCategory().getProcurementCategoryId());
		System.out.println("===========this.getManagedBean().getScQtSpec().getPmCurrency().getCurrencyCode()========"+this.getManagedBean().getScQtSpec().getPmCurrency().getCurrencyCode());
		System.out.println("===========approval date========"+this.getManagedBean().getScQtSpec().getApprovalDate());
		System.out.println("===========approver name========"+this.getManagedBean().getScQtSpec().getApproverName());
		System.out.println("===========approver action========"+this.getManagedBean().getScQtSpec().getApproverAction());
		System.out.println("===========remrks========"+this.getManagedBean().getScQtRemarks().getRemark());
		System.out.println("===========getMaxProposal========"+this.getManagedBean().getScQtSpec().getMaxProposal());
		System.out.println("===========award type========"+this.getManagedBean().getScQtSpec().getAwardType());
		System.out.println("===========fulfilment type========"+this.getManagedBean().getScQtSpec().getFulfilmentType());
		System.out.println("===========getJustificationRemark========"+this.getManagedBean().getScQtSpec().getJustificationRemark());
		System.out.println("===========.product required()========"+this.getManagedBean().getScQtSpec().getIsSampleReq());
		System.out.println("===========getIsApprovalReq========"+this.getManagedBean().getScQtSpec().getIsApprovalReq());
		System.out.println("===========amount========"+this.getManagedBean().getScQtSpec().getScQt().getQtAmount());
		System.out.println("===========status id========"+this.getManagedBean().getScQtSpec().getStatusId());
		System.out.println("===========contract period======="+this.getManagedBean().getScQtSpec().getContractDuration());
		*/
		
		List<ScQtDeduction> listdedudt=	this.getManagedBean().getScQtDeductionList();
		System.out.println("=========deduction list==="+listdedudt.size());
		if(listdedudt!=null){
			System.out.println("=========enter if==="+listdedudt.size());
		for(int p=0;p<listdedudt.size();p++){
			ScQtDeduction checkList=listdedudt.get(p);
			/*System.out.println("=== enetr getScQtDeductionList=="+listdedudt.size());
			System.out.println("====getDeductionType==="+checkList.getDeductionType());
			System.out.println("====getDeductionAmount==="+checkList.getDeductionAmount());
			System.out.println("====getIsFixed==="+checkList.getIsFixed());
			*/
		}
		}
		System.out.println("=========stage one list====");
	List< ScQtCheckList> list=	this.getManagedBean().getScQtCheckSt1techProposal();
	for(int i=0;i<list.size();i++){
		ScQtCheckList checkList=list.get(i);
		/*System.out.println("=======enter getScQtCheckSt1techProposal==== ");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());
		System.out.println("====check list doctype==="+checkList.getDocType());
		System.out.println("====check list evaluation==="+checkList.getEvaluationStage());*/
	}
	List< ScQtCheckList> list1=	this.getManagedBean().getScQtCheckSt1techAdditionalProposalList();
	for(int j=0;j<list1.size();j++){
		ScQtCheckList checkList=list1.get(j);
		/*System.out.println("=== enetr getScQtCheckSt1techAdditionalProposalList==");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());*/
		
	}
	
	List< ScQtCheckList> list2=	this.getManagedBean().getScQtCheckSt1financeProposalList();
	for(int k=0;k<list2.size();k++){
		ScQtCheckList checkList=list2.get(k);
		/*System.out.println("=== enetr getScQtCheckSt1financeProposalList==");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());*/
		
	}
	
	List< ScQtCheckList> list3=	this.getManagedBean().getScQtCheckSt1financeAdditionalProposalList();
	for(int l=0;l<list3.size();l++){
		ScQtCheckList checkList=list3.get(l);
	/*	System.out.println("=== enetr getScQtCheckSt1financeAdditionalProposalList==");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());
		*/
	}
	
	List< ScQtCheckList> list4=	this.getManagedBean().getScQtCheckSt2techAdditionalProposalList();
	for(int l=0;l<list4.size();l++){
		ScQtCheckList checkList=list4.get(l);
		/*System.out.println("=== enetr getScQtCheckSt2techAdditionalProposalList==");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());
		*/
	}
	
	List< ScQtCheckList> list5=	this.getManagedBean().getScQtCheckSt2financeProposalList();
	for(int m=0;m<list5.size();m++){
		ScQtCheckList checkList=list5.get(m);
		/*System.out.println("=== enetr getScQtCheckSt2financeProposalList==");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());
		*/
	}
	
	List< ScQtCheckList> list6=	this.getManagedBean().getScQtCheckSt2financeAdditionalProposalList();
	for(int n=0;n<list6.size();n++){
		ScQtCheckList checkList=list6.get(n);
		/*System.out.println("=== enetr getScQtCheckSt2financeAdditionalProposalList==");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());
		*/
	}
	
	
	List< ScQtCheckList> list7=	this.getManagedBean().getScQtCheckSt2techProposal();
	for(int r=0;r<list7.size();r++){
		ScQtCheckList checkList=list7.get(r);
		/*System.out.println("=======enter getScQtCheckSt2techProposal==== ");
		System.out.println("====check list title==="+checkList.getTitle());
		System.out.println("====check list type==="+checkList.getChecklistType());
		System.out.println("====check list seq==="+checkList.getChecklistSeq());
		*/
	}
	
		}catch(Exception e ){
			System.out.println("=====cath exception method===");
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	
	}

	
	@PostConstruct
    public void init() {
		try{
        if (getManagedBean().getScQtSpec() == null) {
            getManagedBean().setScQtSpec(new ScQtSpec());
            getManagedBean().getScQtSpec().setPmCurrency(new PmCurrency());
            getManagedBean().getScQtSpec().setPmProcurementCategory(new PmProcurementCategory());
            getManagedBean().getScQtSpec().setScQt(new ScQt());
            getManagedBean().setScQtRemarks(new ScQtRemark());
            getManagedBean().setScQtChecklist(new ScQtCheckList());
           // getManagedBean().setScQtDeduction(new ScQtDeduction());
          //  getManagedBean().setScQtDeductionList(new ScQtDeduction());
          //  getManagedBean().getScQtChecklist().setTitle("Manga");
          //  getManagedBean().getScQtChecklist().setChecklistType("upload");
            //List<ScQtCheckList> list=new ArrayList<ScQtCheckList>();
           // ScQtCheckList checklist=new ScQtCheckList();
            //checklist.setTitle("Manga");
           // checklist.setChecklistType("Upload");
            //checklist.setChecklistType("Upload");
           // checklist.setDocType("1");
            //checklist.setEvaluationStage("1");
           // list.add(checklist);
           // this.getManagedBean().setScQtCheckSt1techProposal(list);
           
        }
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
    }
	
	
	public void specificAdd(){
		System.out.println("====enter specification details add method====");
		try{
			System.out.println("enter try loop");
			List<ScQtDeduction> deductionList= getManagedBean().getScQtDeductionList();
			if(null == deductionList){
				System.out.println("==enter null loop===");
				deductionList= new ArrayList<ScQtDeduction>();
												
			}
			/*if(deductionList!=null){
			for(int i=0;i<deductionList.size();i++){
				ScQtDeduction deduction=	deductionList.get(i);
				System.out.println("====add method value==="+deduction.getDeductionType());
			}
			}*/
		ScQtDeduction deduction=new ScQtDeduction();
		
	 deductionList.add(deduction);
		//this.getManagedBean().getScQtDeductionList().addAll(c);
		this.getManagedBean().setScQtDeductionList(deductionList);
		}catch(Exception e){
			System.out.println("===enter catch loop");
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
		//return getManagedBean().getScQtDeductionList();
	}
	
	public void stageOneAdditionalAdd(){
		System.out.println("====enter stage1 additional add method====");
		try{
			List<ScQtCheckList> st1additionalList= getManagedBean().getScQtCheckSt1techAdditionalProposalList();
			if(null == st1additionalList){
				st1additionalList= new ArrayList<ScQtCheckList>();
								
			}
			
			ScQtCheckList additionProposalst1=new ScQtCheckList();
		
			st1additionalList.add(additionProposalst1);
		this.getManagedBean().setScQtCheckSt1techAdditionalProposalList(st1additionalList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	public void stageOneFiProposalAdd(){
		System.out.println("====enter stage1 finance proposal add method====");
		try{
			List<ScQtCheckList> st1financeproposalList= getManagedBean().getScQtCheckSt1financeProposalList();
			if(null == st1financeproposalList){
				st1financeproposalList= new ArrayList<ScQtCheckList>();
								
			}
			
			ScQtCheckList st1financeproposalList1=new ScQtCheckList();
		
			st1financeproposalList.add(st1financeproposalList1);
		this.getManagedBean().setScQtCheckSt1financeProposalList(st1financeproposalList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	public void stageOneFiAdditioalProposalAdd(){
		System.out.println("====enter stage1 finannce additional add method====");
		try{
			List<ScQtCheckList> st1financeAdditionalproposalList= getManagedBean().getScQtCheckSt1financeAdditionalProposalList();
			if(null == st1financeAdditionalproposalList){
				st1financeAdditionalproposalList= new ArrayList<ScQtCheckList>();
								
			}
			
			ScQtCheckList st1financeAdditionalproposalList1=new ScQtCheckList();
		
			st1financeAdditionalproposalList.add(st1financeAdditionalproposalList1);
		this.getManagedBean().setScQtCheckSt1financeAdditionalProposalList(st1financeAdditionalproposalList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	
	public void stageTwoAdditionalAdd(){
		System.out.println("====enter stage2 additional add method====");
		try{
			List<ScQtCheckList> st2additionalList= getManagedBean().getScQtCheckSt2techAdditionalProposalList();
			if(null == st2additionalList){
				st2additionalList= new ArrayList<ScQtCheckList>();
								
			}
			
			ScQtCheckList additionProposalst2=new ScQtCheckList();
		
			st2additionalList.add(additionProposalst2);
		this.getManagedBean().setScQtCheckSt2techAdditionalProposalList(st2additionalList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	public void stageTwoFiProposalAdd(){
		System.out.println("====enter stage2 finance proposal add method====");
		try{
			List<ScQtCheckList> st2financeproposalList= getManagedBean().getScQtCheckSt2financeProposalList();
			if(null == st2financeproposalList){
				st2financeproposalList= new ArrayList<ScQtCheckList>();
								
			}
			
			ScQtCheckList st2financeproposalList2=new ScQtCheckList();
		
			st2financeproposalList.add(st2financeproposalList2);
		this.getManagedBean().setScQtCheckSt2financeProposalList(st2financeproposalList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	public void stageTwoFiAdditioalProposalAdd(){
		System.out.println("====enter stage2 finannce additional proposal add method====");
		try{
			List<ScQtCheckList> st2financeAdditionalproposalList= getManagedBean().getScQtCheckSt2financeAdditionalProposalList();
			if(null == st2financeAdditionalproposalList){
				st2financeAdditionalproposalList= new ArrayList<ScQtCheckList>();
								
			}
			
			ScQtCheckList st2financeAdditionalproposalList1=new ScQtCheckList();
		
			st2financeAdditionalproposalList.add(st2financeAdditionalproposalList1);
		this.getManagedBean().setScQtCheckSt2financeAdditionalProposalList(st2financeAdditionalproposalList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	public void stageOneTechProposal(){
		System.out.println("====enter stage1 tech proposal====");
		try{
			/*List<String> compilnceList= getManagedBean().getCompilanceList();
			System.out.println("========compilance List size in backing bean====="+compilnceList.size());
			String[] strarray = (String[]) compilnceList.toArray(new String[0]);
			String value = "";
			List<ScQtCheckList> compilance = new ArrayList<ScQtCheckList>();
			for (int i = 0; i < strarray.length; i++) {
				value = strarray[i];
				System.out.println("=======display compilance loop values===="+value);
				ScQtCheckList scqtlist=new ScQtCheckList();
				scqtlist.setTitle(value);
				
				//TabBean mcities1 = getManagedBean().getTabview().get(value);

				compilance.add(scqtlist);

				//getManagedBean().setCompilanceList(compilance);
				getManagedBean().setScQtCheckSt1techProposal(compilance);*/
			List<ScQtCheckList> compilance = new ArrayList<ScQtCheckList>();
			List<SpecificationCompilanceBean> list=getSelectedCompilenceList();
			System.out.println("======tech one list size===="+list.size());
			for(int i=0;i<list.size();i++){
				SpecificationCompilanceBean bean=list.get(i);
				ScQtCheckList scqtlist=new ScQtCheckList();
				scqtlist.setTitle(bean.getCompilanceTitle());
				compilance.add(scqtlist);

				//getManagedBean().setCompilanceList(compilance);
				getManagedBean().setScQtCheckSt1techProposal(compilance);
				
			}
			
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	
	public void stageTwoTechProposal(){
		System.out.println("====enter stage2 tech proposal====");
		try{
			/*List<String> compilnceList= getManagedBean().getCompilanceList();
			System.out.println("========compilance List size in backing bean====="+compilnceList.size());
			String[] strarray = (String[]) compilnceList.toArray(new String[0]);
			String value = ""*/;
			List<ScQtCheckList> compilance = this.getManagedBean().getScQtCheckSt2techProposal();
			if(compilance==null){
				compilance=new ArrayList<ScQtCheckList>();
			}
				
				ScQtCheckList scqtlist=new ScQtCheckList();
				scqtlist.setTitle("TemplateTitle");
				scqtlist.setChecklistType("Technical/Financial/Others");
				
				//TabBean mcities1 = getManagedBean().getTabview().get(value);

				compilance.add(scqtlist);

				//getManagedBean().setCompilanceList(compilance);
				getManagedBean().setScQtCheckSt2techProposal(compilance);
			
			
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	
	public void stageTwoTechnicalAddProposal(){
		System.out.println("====enter stage2 stageTwoTechnicalAddProposal====");
		try{
			List<ScQtCheckList> st2technicalproposalList= getManagedBean().getScQtCheckSt2techProposal();
			if(null == st2technicalproposalList){
				st2technicalproposalList= new ArrayList<ScQtCheckList>();
								
			}
			
			ScQtCheckList st2technicalproposalList1=new ScQtCheckList();
		
			st2technicalproposalList.add(st2technicalproposalList1);
		this.getManagedBean().setScQtCheckSt2techProposal(st2technicalproposalList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	public void stageTwotechproposal(AjaxBehaviorEvent e){
		System.out.println("=====stage two drop down ====");
		try{
		String dropdown = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=====stage two drop down value===="+dropdown);
		List<ScQtCheckList> compilance = this.getManagedBean().getScQtCheckSt2techProposal();
		/*List<ScQtCheckList> compilance = this.getManagedBean().getScQtCheckSt2techProposal();
		if(compilance==null){
			compilance=new ArrayList<ScQtCheckList>();
		}
			
			ScQtCheckList scqtlist=new ScQtCheckList();
			scqtlist.setChecklistType(dropdown);
			compilance.add(scqtlist);*/
		//this.getManagedBean().setScQtCheckSt2techProposal(compilance);
		
		setSelctValue(dropdown);
		}catch(Exception e1){
			throw new IllegalArgumentException();
		}
		//this.getManagedBean().getScQtChecklist().setChecklistType(dropdown);
	}
	
	
	public void compilenceList(){
	
		try{
			/*List<String> compilnceList= getManagedBean().getCompilanceList();
			System.out.println("========compilance List size in backing bean====="+compilnceList.size());
			String[] strarray = (String[]) compilnceList.toArray(new String[0]);
			String value = "";
			List<ScQtCheckList> compilance = new ArrayList<ScQtCheckList>();
			for (int i = 0; i < strarray.length; i++) {
				value = strarray[i];
				System.out.println("=======display compilance loop values===="+value);
				ScQtCheckList scqtlist=new ScQtCheckList();
				scqtlist.setTitle(value);
				
				//TabBean mcities1 = getManagedBean().getTabview().get(value);

				compilance.add(scqtlist);

				//getManagedBean().setCompilanceList(compilance);
				getManagedBean().setScQtCheckSt1techProposal(compilance);*/
		List<SpecificationCompilanceBean> compilaenceList = new ArrayList<SpecificationCompilanceBean>();
			 
	          
			compilaenceList.add(new SpecificationCompilanceBean(1,"Sijil MOF"));
			compilaenceList.add(new SpecificationCompilanceBean(2,"Sijil 29"));
			compilaenceList.add(new SpecificationCompilanceBean(3,"Sijil 49"));
			compilaenceList.add(new SpecificationCompilanceBean(4,"Sijil KDN"));
			compilaenceList.add(new SpecificationCompilanceBean(5,"Sijil Bumiputera"));
			
			this.getManagedBean().setCompilaenceList(compilaenceList);
		}catch(Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException();
		}
		
	}
	

	/**
	 * @return the selctValue
	 */
	public String getSelctValue() {
		return selctValue;
	}

	/**
	 * @param selctValue the selctValue to set
	 */
	public void setSelctValue(String selctValue) {
		this.selctValue = selctValue;
	}

	

	/**
	 * @return the selectedCompilenceList
	 */
	public List<SpecificationCompilanceBean> getSelectedCompilenceList() {
		return selectedCompilenceList;
	}

	/**
	 * @param selectedCompilenceList the selectedCompilenceList to set
	 */
	public void setSelectedCompilenceList(
			List<SpecificationCompilanceBean> selectedCompilenceList) {
		this.selectedCompilenceList = selectedCompilenceList;
	}
}
