package my.ep.web.backingbean.sm.reviewApplication;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationResDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.reviewApplication.ReviewApplicationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "reviewApplicationBackingBean")
@RequestScoped
public class ReviewApplicationBackingBean extends
		BaseBackingBean<ReviewApplicationManagedBean> {
	public static Logger log = Logger
			.getLogger(ReviewApplicationManagedBean.class);

	public ReviewApplicationBackingBean() {

	}

	/**
	 * Sample Skeleton to call SO -- Start
	 */

	public void retrieveSupportingDocuments() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.retrieveSupportingDocuments(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void sendEnquiryToSupplier() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.sendEnquiryToSupplier(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void escalateToPO() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.escalateToPO(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void retrieveSMApplication() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.retrieveSMApplication(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void updateApplication() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.updateApplication(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void approveApplication() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.approveApplication(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void rejectApplication() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.rejectApplication(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void recommendSiteVisit() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.recommendSiteVisit(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void escateToApprover() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.escateToApprover(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void cancelApproval() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.cancelApproval(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void retrieveCancellationRequest() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.retrieveCancellationRequest(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void approveCancellation() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.approveCancellation(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	public void rejectCancellation() {
		IReviewApplicationSO iReviewApplicationSO = null;
		try {
			iReviewApplicationSO = BusinessDelegate.getInstance()
					.getServiceObject(IReviewApplicationSO.class);
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			ReviewApplicationResDTO reviewApplicationResDTO = iReviewApplicationSO
					.rejectCancellation(reviewApplicationReqDTO);
		} finally {
			iReviewApplicationSO = null;
		}
	}

	/**
	 * Sample Skeleton to call SO -- End
	 */
}
