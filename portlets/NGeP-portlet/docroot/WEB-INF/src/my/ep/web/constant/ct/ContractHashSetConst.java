/**
 * 
 */
package my.ep.web.constant.ct;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.type.pm.RoleCode;

/**
 * @author hafizuddin
 *
 */
public final class ContractHashSetConst {
	
	//#34097/#26186 start
	public static final Set<Long> CANCEL_STATUS_CONST = 
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ContractStatusConst.FA_PENDING_SUBMISSION,
            ContractStatusConst.FA_PENDING_REVISION,
            ContractStatusConst.FA_PENDING_MEETING_CONFIRMATION,
            ContractStatusConst.FA_PENDING_MEETING_CONFIRMATION_REVISION,
            ContractStatusConst.FA_PENDING_MEETING_DECISION,
            ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION,
            ContractStatusConst.FA_PENDING_MOF_MEETING_DECISION,
            ContractStatusConst.FA_PENDING_LOA_SUBMISSION,
            ContractStatusConst.FA_PENDING_LOA_REVISION
    )));
	public static final Set<Long> REJECT_STATUS_CONST = 
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			ContractStatusConst.FA_PENDING_APPROVAL,
			ContractStatusConst.FA_PENDING_REVISION_APPROVAL,
			ContractStatusConst.FA_PENDING_LOA_APPROVAL,
			ContractStatusConst.FA_PENDING_LOA_REVISION_APPROVAL
    )));
	//#34097/#26186 end
	
	//#35238 start
	/**
	 * Constant roles for PBQC (Procurement/Quotation Committees).
	 */
	public static final Set<String> PBQC_ROLES = 
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
	        // QCA
	        RoleCode.QCA_CHAIRPERSON.value(),
	        RoleCode.QCA_MEMBER.value(),
	        RoleCode.QCA_SECRETARY.value(),
	        // QCB
	        RoleCode.QCB_CHAIRPERSON.value(),
	        RoleCode.QCB_MEMBER.value(),
	        RoleCode.QCB_SECRETARY.value(),
	        // QQC
	        RoleCode.QQC_CHAIRPERSON.value(),
	        RoleCode.QQC_MEMBER.value(),
	        RoleCode.QQC_SECRETARY.value(),
	        // PBA
	        RoleCode.PBA_CHAIRPERSON.value(),
	        RoleCode.PBA_MEMBER.value(),
	        RoleCode.PBA_SECRETARY.value(),
	        // PBB
	        RoleCode.PBB_CHAIRPERSON.value(),
	        RoleCode.PBB_MEMBER.value(),
	        RoleCode.PBB_SECRETARY.value(),
	        // PPB
	        RoleCode.PPB_CHAIRPERSON.value(),
	        RoleCode.PPB_MEMBER.value(),
	        RoleCode.PPB_SECRETARY.value()
	)));
	
	public static final Set<String> ROLE_USER_TO_SEARCH = 
	    Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
	        RoleCode.MIN_CONTRACT_ADMIN.value(),
	        RoleCode.BPK_CCA.value(),
	        RoleCode.CT_APPROVER.value(),
	        RoleCode.BPK_CC_APPROVER.value()
	)));
	
	public static final Set<String> ROLE_USER_TO_RESET_SEARCH = 
	    Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
	        RoleCode.AUDITOR_GENERAL.value(),
	        RoleCode.BASIC_SUPPLIER_ADMIN.value(),
	        RoleCode.BASIC_SUPPLIER_USER.value(),
	        RoleCode.MOF_SUPPLIER_ADMIN.value(),
	        RoleCode.MOF_SUPPLIER_USER.value(),
	        RoleCode.BPK_AUDITOR.value(),
	        RoleCode.BPK_APPROVER.value()
	)));
	
	public static final Set<String> COMMITTEE_TYPE_QUOTATION = 
			Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A,
			ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B,
			ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE
    )));
	
	public static final Set<String> QUOTATION_MEMBER_ROLES = 
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            RoleCode.QCA_MEMBER.value(),
            RoleCode.QCB_MEMBER.value(),
            RoleCode.QQC_MEMBER.value()
	)));
	
	public static final Set<String> QUOTATION_SECRETARY_ROLES = 
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            RoleCode.QCA_SECRETARY.value(),
            RoleCode.QCB_SECRETARY.value(),
            RoleCode.QQC_SECRETARY.value()
	 )));
	
	public static final Set<String> QUOTATION_CHAIRPERSON_ROLES = 
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
	            RoleCode.QCA_CHAIRPERSON.value(),
	            RoleCode.QCB_CHAIRPERSON.value(),
	            RoleCode.QQC_CHAIRPERSON.value()
	)));
	//#35238 end
	
	// Private constructor to prevent instantiation
    private ContractHashSetConst() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
