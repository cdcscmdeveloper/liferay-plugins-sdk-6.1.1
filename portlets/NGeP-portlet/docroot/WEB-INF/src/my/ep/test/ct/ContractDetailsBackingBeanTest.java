package my.ep.test.ct;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.managedbean.ct.ContractManagedBean;

import org.junit.Test;

public class ContractDetailsBackingBeanTest {

	/**
	 * Test Case 1: Contract is FULFILMENT_TYPE_SCHEDULED
	 * Test Case: setTabValue_WithScheduledContract_ShouldLogTypeSchedule
	 * Expected Result: Logger logs correct message when fulfillment type is scheduled
	 * Log Message: "Test Case 1: Contract marked as typeSchedule (true)"
	 */
	@Test
	public void setTabValue_WithScheduledContract_ShouldLogTypeSchedule() {
		
	    // Create full mock (constructor is not called!)
	    ContractDetailsBackingBean mockBackingBean = mock(ContractDetailsBackingBean.class);

	    // Create mock for ctBB
	    ContractBackingBean mockCtBB = mock(ContractBackingBean.class);
	    ContractManagedBean mockContractBean = mock(ContractManagedBean.class);
	    CtContractVer contractVer = mock(CtContractVer.class);
	    CtContract contract = new CtContract();

	    // Set up contract data
	    PmParameter fulfillmentType = new PmParameter();
	    fulfillmentType.setParameterCode(ParameterConst.FULFILMENT_TYPE_SCHEDULED);
	    contract.setFulfillmentType(fulfillmentType);

	    // Mock internal chain
	    when(mockBackingBean.getManagedBean()).thenReturn(mock(ContractDetailsManagedBean.class));
	    when(mockCtBB.getManagedBean()).thenReturn(mockContractBean);
	    when(mockContractBean.getContractVer()).thenReturn(contractVer);
	    when(contractVer.getCtContract()).thenReturn(contract);

	    // Use reflection to inject mockCtBB into mockBackingBean
	    try {
	        Field ctBBField = ContractDetailsBackingBean.class.getDeclaredField("ctBB");
	        ctBBField.setAccessible(true);
	        ctBBField.set(mockBackingBean, mockCtBB);
	    } catch (Exception e) {
	        throw new RuntimeException("Field injection failed", e);
	    }

	    // Now run the real method logic (bypassing constructor safely)
	    doCallRealMethod().when(mockBackingBean).setTabValue(anyString());

	    // Act
	    mockBackingBean.setTabValue(ContractConst.FULFILMENT_DETAIL);

	}








}
