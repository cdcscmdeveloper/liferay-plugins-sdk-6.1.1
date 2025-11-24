/**
 * 
 */
package my.ep.web.constant.ct;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import my.ep.constant.ct.ContractStatusConst;

/**
 * =============================================================================
 * <p>
 *  File Name   : ContractHashSetConst.java
 *  Description : Centralized constants class for predefined immutable HashSet 
 *                collections used across contract-related modules.
 *  </p>
 *  <p>
 *  This class defines reusable and unmodifiable sets of role codes and other 
 *  constants to improve performance, maintainability, and consistency across 
 *  the application. 
 *  </p>
 *  <p>
 *  Instead of recreating HashSet instances repeatedly within business logic 
 *  (which can lead to unnecessary object creation and garbage collection 
 *  overhead), all frequently used role-based sets are defined here as 
 *  static final members.
 *  </p>
 *  <p>
 *  Example usage:
 *      if (hasAnyRoleUser(ContractHashSetConst.PBQC_ROLES)) {
 *          // perform PBQC-related logic
 *      }
 *  </p>
 *  Key Design Principles:
 *  - Performance: Avoids redundant Set instantiation at runtime.
 *  - Immutability: All collections are wrapped with Collections.unmodifiableSet().
 *  - Reusability: Exposed as public/protected static final constants for 
 *                 cross-class accessibility.
 *  - Readability: Centralized location for managing role-related sets.
 * </p>
 * =============================================================================
 * @author hafizuddin
 * @since 7/11/2025
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
	
	// Private constructor to prevent instantiation
    private ContractHashSetConst() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
