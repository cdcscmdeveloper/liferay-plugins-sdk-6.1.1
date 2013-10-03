package my.ep.web.backingbean.ct;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.kernel.search.ParseException;

import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.persistence.entity.ct.template.CtTemplate;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class TestMain {

	public static void main(String[] args) throws ParseException, java.text.ParseException {
		
		IContractTemplateSO iContractTemplateSO = null;
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
	
		ContractTemplateResDTO ResDTO = iContractTemplateSO.createTemplate(ReqDTO);
	}
}
