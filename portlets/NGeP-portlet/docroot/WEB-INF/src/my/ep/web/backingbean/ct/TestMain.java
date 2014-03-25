package my.ep.web.backingbean.ct;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.kernel.search.ParseException;

public class TestMain {

	public static void main(String[] args) throws ParseException, java.text.ParseException {
		
	/*	IContractTemplateSO iContractTemplateSO = null;
		iContractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject(IContractTemplateSO.class);
		ContractTemplateReqDTO ReqDTO = new ContractTemplateReqDTO();
		CtTemplate ctTemplate = new CtTemplate();
		ctTemplate.setName("khan");
		String example = "This is an example";
	    byte[] bytes = example.getBytes();
		ctTemplate.setContent(bytes);
		ctTemplate.setCreatedBy(new Long(2345));
		ctTemplate.setChangedBy(new Long(2345));
		Date d1=new Date();
	    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    Date date = dateFormat.parse(dateFormat.format(d1));
	    long time = date.getTime();
	    ctTemplate.setCreatedDate(new Timestamp(time));
	    ctTemplate.setRecordStatus(true);
		ReqDTO.setCtTemplate(ctTemplate);
	
		ContractTemplateResDTO ResDTO = iContractTemplateSO.createTemplate(ReqDTO);*/
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date fromDate = sdf.parse("20-01-2014");
		Date toDate = sdf.parse("21-02-2014");
		
		Date today =  sdf.parse("21-03-2014");
				
		if(today.after(fromDate)){
			//System.out.println("***** start date greater than today  !");
			
		} else if(toDate.before(fromDate)){
			
		   //System.out.println("  @@@@@ start date before end date !");
		
		}
		
		
		String itemcode= "4346372947";
		
		CharSequence charseq = itemcode.subSequence(0, 2);
		Integer icode= Integer.valueOf(charseq.toString());
		
	
		
		if(icode>75){
			System.out.println("icode : " +icode);
			System.out.println("Service");
		} else if(icode < 75){
			System.out.println("icode : " +icode);
			System.out.println("Product");
		}
		
		
		
		//Date fdate = new Date();
		
		//System.out.println("dddddddddd "+fdate);
	/*	System.out.println("Days :" + daysBetween(toDate, fromDate));
		System.out.println("Hours :" + hoursDifference(toDate, fromDate));
		System.out.println("Mins :" + minsDifference(toDate, fromDate));*/
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	private static int minsDifference(Date toDate, Date fromDate) {
		final int MILLI_TO_MINS = 1000 * 60;
		return (int) (toDate.getTime() - fromDate.getTime()) / MILLI_TO_MINS;
	}

	private static int hoursDifference(Date toDate, Date fromDate) {
		final int MILLI_TO_HOUR = 1000 * 60 * 60;
		return (int) (toDate.getTime() - fromDate.getTime()) / MILLI_TO_HOUR;
	}

	public static int daysBetween(Date toDate, Date fromDate) {
		final int MILLI_TO_DAYS = 1000 * 60 * 60 * 24;
		return (int) ((toDate.getTime() - fromDate.getTime()) / MILLI_TO_DAYS );
	}
	
}
