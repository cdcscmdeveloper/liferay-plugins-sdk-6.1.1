package my.ep.web.backingbean.ct.dashboard;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ct.dashboard.ContractDashboardManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="contractDashboardBackingBean")
@ViewScoped
public class ContractDashboardBackingBean extends BaseBackingBean<ContractDashboardManagedBean>{

		 
		public ContractDashboardBackingBean(){
		}
		
		@PostConstruct
		public void init(){
			getManagedBean().setLessThan30Days(calculateTotalRecord(1));
			getManagedBean().setLessThan120Days(calculateTotalRecord(2));
		}
		
		public void goToMaintenancePage() throws IOException {
			FacesScopes facesScope = new FacesScopes();
			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);
			String commonUrl;
			try {
				commonUrl = themeDisplay.getURLCurrent().substring(
						0,
						themeDisplay.getURLCurrent().indexOf(
								themeDisplay.getLayout().getGroup()
								.getFriendlyURL())
								+ themeDisplay.getLayout().getGroup()
								.getFriendlyURL().length());
				System.out.println("commonUrl" + commonUrl);

				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect(commonUrl + "/" +ContractConst.NAV_TO_CONTRACT_MAINT);
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static Date addDays(Date date, int days)
			{
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(date);
			    cal.add(Calendar.DATE, days); 
			    return cal.getTime();
			}
	
		public int calculateTotalRecord(int index){
			ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
			IContractSO contractSO = null;
				
			try {
						if(index ==1){
					     	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_LESS.value(), addDays(new Date(), 30)));		
					     	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), new Date()));	
					    }else if(index == 2){
					    	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_LESS.value(), addDays(new Date(), 120)));		
					     	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), addDays(new Date(), 90)));	
					    } 
						
						contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
						ContractReqDTO reqDTO = new ContractReqDTO();
						reqDTO.setContractSearchBean(searchBean);
						ContractResDTO resDTO = contractSO.searchDashBoardContract(reqDTO);
							
						if (resDTO.isSuccessful()) {
								return resDTO.getContractResultBean().getTotalRecord();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			return 0;
		}
}
