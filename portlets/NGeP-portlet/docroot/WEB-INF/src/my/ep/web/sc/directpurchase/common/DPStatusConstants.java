package my.ep.web.sc.directpurchase.common;
/**
 * @author sri
 * 24.04.2014
 */
public class DPStatusConstants {
	public	static	final	String	CREATE_SIMPLE_QUOTE						=	"CREATE_SIMPLE_QUOTE";
	public	static	final	String	CREATE_SIMPLE_BID						=	"CREATE_SIMPLE_BID";
	
	//PM status
	public static	final	Long	RN_PENDING_SUBMISSION	=	60700L;
	public static	final	Long	RN_PENDING_APPROVAL		=	60701L;
	public static	final	Long	RN_PENDING_REVISION		=	60702L;
	public static	final	Long	RN_APPROVAL				=	60703L;
	public static	final	Long	RN_CANCELLED			=	60704L;
	public static	final	Long	RN_REJECTED 			=	60705L;
	
	public	static	final	Long	PRI_PENDING_SUBMISSION	=	60750L;
	public	static	final	Long	PRI_PENDING_SUPPLIER_RESPONSE = 60751L;
	public	static	final	Long	PRI_PENDING_RN_CREATION	=	60752L;
	public	static	final	Long	PRI_COMPLETED			=	60753L;
	public	static	final	Long	PRI_CANCELLED			=	60754L;
	
	public	static	final	Long	PID_PENDING_SUBMISSION	=	60800L;
	public	static	final	Long	PID_PENDING_APPROVAL	=	60801L;
	public	static	final	Long	PID_PENDING_RN_CREATION	=	60802L;
	public	static	final	Long	PID_COMPLETED			=	60803L;
	public	static	final	Long	PID_REJECTED			=	60804L;
	
	public	static	final	Long	SQ_PENDING_SUBMISSION	=	60850L;
	public	static	final	Long	SQ_PENDING_SUPPLIER_RESPONSE	=	60851L;
	public	static	final	Long	SQ_PENDING_RN_CREATION	=	60852L;
	public	static	final	Long	SQ_COMPLETED			=	60853L;
	public	static	final	Long	SQ_CANCELLED			=	60854L;
	
	
	
	
	public String getCreateSimpleQuote() {
		return CREATE_SIMPLE_QUOTE;
	}
	public String getCreateSimpleBid() {
		return CREATE_SIMPLE_BID;
	}
	public static Long getRnPendingSubmission() {
		return RN_PENDING_SUBMISSION;
	}
	public static Long getRnPendingApproval() {
		return RN_PENDING_APPROVAL;
	}
	public static Long getRnApproval() {
		return RN_APPROVAL;
	}
	public static Long getRnCancel() {
		return RN_CANCELLED;
	}
	public static Long getRnRejected() {
		return RN_REJECTED;
	}
	public static Long getPriPendingSubmission() {
		return PRI_PENDING_SUBMISSION;
	}
	public static Long getPriPendingSupplierResponse() {
		return PRI_PENDING_SUPPLIER_RESPONSE;
	}
	public static Long getPriPendingRnCreation() {
		return PRI_PENDING_RN_CREATION;
	}
	public static Long getPriRnCreated() {
		return PRI_COMPLETED;
	}
	public static Long getPriCanceled() {
		return PRI_CANCELLED;
	}
	public static Long getPidPendingSubmission() {
		return PID_PENDING_SUBMISSION;
	}
	public static Long getPidPendingApproval() {
		return PID_PENDING_APPROVAL;
	}
	public static Long getPidPendingRnCreation() {
		return PID_PENDING_RN_CREATION;
	}
	public static Long getPidRnCreated() {
		return PID_COMPLETED;
	}
	
	public static Long getPidRejected() {
		return PID_REJECTED;
	}
	public static Long getSqPendingSubmission() {
		return SQ_PENDING_SUBMISSION;
	}
	public static Long getSqPendingSupplierResponse() {
		return SQ_PENDING_SUPPLIER_RESPONSE;
	}
	public static Long getSqPendingRnCreation() {
		return SQ_PENDING_RN_CREATION;
	}
	public static Long getSqRnCreated() {
		return SQ_COMPLETED;
	}
	public static Long getSqCanceled() {
		return SQ_CANCELLED;
	}
	
}
