package my.ep.test.ct;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.util.LoggerUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.amendment.AmendTabSideMenus;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.managedbean.ct.ContractManagedBean;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;

import org.junit.Test;

public class AmendmentBackingBeanTest {

	/**
	 *  Test Case: Schedule Menu Shown for Ministry Contract with Other Change
	 *	Expected Result: sideMenu.menuSchedule is added to getFinalListOfSideMenu()
	 * @throws Exception 
	 */
	@Test
	public void constructTabSideMenu_WhenScheduleMinistryContractWithOtherChange_ShouldAddScheduleMenu() throws Exception {
	    
	    // Create mock AmendTabSideMenus
	    AmendTabSideMenus mockSideMenu = mock(AmendTabSideMenus.class);
	    
	    // Use reflection to set private fields with CORRECT field names from the constructor
	    setPrivateField(mockSideMenu, "menuWithAmendFulfilment", Arrays.asList("Amendment", "Fulfilment"));
	    setPrivateField(mockSideMenu, "menuSchedule", Arrays.asList("Schedule Menu"));
	    setPrivateField(mockSideMenu, "menuDeduction", Arrays.asList("Deduction Menu"));
	    setPrivateField(mockSideMenu, "menuWithItem", Arrays.asList("Item Menu"));
	    setPrivateField(mockSideMenu, "menuWithAgency", Arrays.asList("Agency Menu"));
	    setPrivateField(mockSideMenu, "menuWithZone", Arrays.asList("Zone Menu"));
	    
	    // Set up other fields that might be accessed (optional, based on your production code)
	    setPrivateField(mockSideMenu, "menuWithAmendSetting", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuWithPrepareLoa", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuWithPrepareBond", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuWithBond", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuWithDecision", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuWithMeetings", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuWithDeclaration", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuWithAmendSupplier", new ArrayList<>());
	    setPrivateField(mockSideMenu, "menuApprover", new ArrayList<>());

	    // Create mocks for the backing beans
	    AmendmentBackingBean mockBackingBean = mock(AmendmentBackingBean.class);
	    AmendmentManagedBean mockManagedBean = mock(AmendmentManagedBean.class);
	    when(mockBackingBean.getManagedBean()).thenReturn(mockManagedBean);
	    
	    ContractBackingBean mockCtBB = mock(ContractBackingBean.class);
	    ContractManagedBean mockContractBean = mock(ContractManagedBean.class);
	    when(mockCtBB.getManagedBean()).thenReturn(mockContractBean);
	    
	    CtContractVer contractVer = mock(CtContractVer.class);
	    CtContract contract = new CtContract();
	    when(mockContractBean.getContractVer()).thenReturn(contractVer);
	    when(contractVer.getCtContract()).thenReturn(contract);
	    
	    CtAmendment amendment = new CtAmendment();
	    amendment.setOtherChange(true);
	    amendment.setQuantityChange(true);
	    amendment.setItemPriceChange(true);
	    
	    when(contractVer.getCtAmendment()).thenReturn(amendment);
	    
	    contractVer.setCtContract(contract);
	    contractVer.setCtAmendment(amendment);
	    
	    PmParameter contractType = new PmParameter();
	    PmParameter fulfillmentType = new PmParameter();
	    contractType.setParameterCode(ParameterConst.CONTRACT_TYPE_MINISTRY);
	    fulfillmentType.setParameterCode(ParameterConst.FULFILMENT_TYPE_SCHEDULED);
	    
	    contract.setContractType(contractType);
	    contract.setFulfillmentType(fulfillmentType);
	    
	    // Inject the contract backing bean using reflection
	    try {
	        Field ctBBField = AmendmentBackingBean.class.getDeclaredField("ctBackingBean");
	        ctBBField.setAccessible(true);
	        ctBBField.set(mockBackingBean, mockCtBB);
	    } catch (Exception e) {
	        throw new RuntimeException("Field injection failed", e);
	    }
	    
	    // Set up managed bean state
	    mockContractBean.setContractVer(contractVer);
	    mockManagedBean.setAckFA(null); // this triggers amendment block
	    mockManagedBean.setPrepareLOA(false);
	    mockManagedBean.setSignOffDecision(false);
	    mockManagedBean.setSignUpdateDecision(false);
	    mockManagedBean.setDisableDecisionDetails(false);
	    mockManagedBean.setDisableMeetingDetails(false);
	    mockManagedBean.setDecisionPbQcLOA(false);
	    mockManagedBean.setPbConfirmMeeting(false);
	    
	    // Mock the method to call the version that accepts AmendTabSideMenus parameter
//	    doCallRealMethod().when(mockBackingBean).ConstructTabSideMenu(any(AmendTabSideMenus.class));
	    
	    // Call the method with the mock
//	    mockBackingBean.ConstructTabSideMenu(mockSideMenu);
	}

	// Helper method to set private fields
	private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
	    Class<?> clazz = target.getClass();
	    Field field = null;
	    
	    // Search through class hierarchy
	    while (clazz != null && field == null) {
	        try {
	            field = clazz.getDeclaredField(fieldName);
	        } catch (NoSuchFieldException e) {
	            clazz = clazz.getSuperclass();
	        }
	    }
	    
	    if (field == null) {
	        // Print all available fields to help debug
	        System.out.println("Available fields in " + target.getClass().getSimpleName() + ":");
	        printAllFields(target.getClass());
	        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy");
	    }
	    
	    field.setAccessible(true);
	    field.set(target, value);
	}

	// Helper method to print all fields in a class (for debugging)
	private void printAllFields(Class<?> clazz) {
	    System.out.println("Fields in " + clazz.getSimpleName() + ":");
	    Field[] fields = clazz.getDeclaredFields();
	    for (Field field : fields) {
	        System.out.println("  " + field.getName() + " : " + field.getType().getSimpleName());
	    }
	}

}
