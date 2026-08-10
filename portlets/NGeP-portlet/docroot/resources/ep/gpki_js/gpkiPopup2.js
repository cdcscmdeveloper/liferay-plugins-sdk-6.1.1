/**
 * Obfuscated with default preset at:
 * https://obfuscator.io
 * File: obfuscate_gpkiPopup2.js
 */
var hostOTP = "";
var hostBroker = "";
var gpkiNonce;
var gpki = {};
var gpkiRequestTime = 0;
// per-request timeout, overridden from mygpki.requestTimeout in setUser()
var gpkiReqTimeout = 30;
// cooldown on the Re-send button after an unanswered OTP request
var OTP_RETRY_COOLDOWN = 30 * 1000;
(function () {
  var nric = "";
  var email = "";
  var modeUsed = "";
  var appCode = "";
  var agencyId = "";
  var signType = "";
  var saluran = "";
  var text = "";
  var otpExp = 120;

  this.setUser = function (xhr, status, args) {
    nric = args.nric;
    email = args.email;
    modeUsed = args.modeUsed;
    appCode = args.appCode;
    agencyId = args.agencyId;
    signType = args.signType;
    saluran = args.saluran;
    otpExp = positiveOr(args.otpExp, 120);
    hostOTP = args.hostOTP;
    hostBroker = args.hostBroker;
    gpkiNonce = args.gpkiNonce;
    gpkiRequestTime = positiveOr(args.nonceTime, 0);	// 0 = not requested yet
    gpkiReqTimeout = positiveOr(args.reqTimeout, 30);
    this.requestMedium();
  };

  this.requestMedium = function () {
    //request_mediumList(nric, appCode, modeUsed);
    gpkiPost(hostBroker + "mediumList_new/",
      { nric: nric, applicationCode: appCode, mode: modeUsed },
      parseResultMedium, giveUpMedium);
  };

  this.requestOtp = function () {
    //request_otp(nric, agencyId, nric, modeUsed, otpExp, saluran, email);
    gpkiPost(hostOTP + "request_otp_new/",
      { nric: nric, agencyId: agencyId, userId: nric, mode: modeUsed,
        otpExp: otpExp, saluran: saluran, emailuser: email },
      parseTxResultRequestOTP, giveUpOtp);
  };

  this.signToken = function (a) {
    gpkiBlockWidget.show();
    text = a[0];
    data = encodeURIComponent(Gpki.hash(a[0]));
    const pin = a[1];
    if (pin === "") {
      this.mandatoryError(a[2]);
      return;
    }
    sign("token", nric, pin, data, "false", "data");
  };

  this.signRoam = function (a) {
    gpkiBlockWidget.show();
    text = a[0];
    const pin = a[1];
    if (pin === "") {
      this.mandatoryError(a[3]);
      return;
    }
    const otpCode = a[2];
    if (otpCode === "") {
      this.mandatoryError(a[4]);
      return;
    }
	// check if undefined, null, NaN, empty string, etc.
	if (!gpkiNonce) {
	  errorAction([{ name: 'gpkiNonceNull', value: 'invalid nonce'}]);
	  gpkiBlockWidget.hide();
	  return;
	}
    const data = encodeURIComponent(Gpki.hash(text));
    //sign_roaming(nric, gpkiNonce, otpCode, data, pin, agencyId, modeUsed, signType, 0);
    gpkiPost(hostOTP + "sign_roaming/",
      { nric: nric, nonce: gpkiNonce, otpCode: otpCode, data: data, pin: pin,
        agencyId: agencyId, mode: modeUsed, signType: signType, dataSeq: 0 },
      parseTxResultSigningRoaming, giveUpSign);
  };

  this.otpExp = function () { return otpExp; };
  
  this.textBeforeSign = function () { return text; };
  
  this.nric = function () { return nric; };

  this.mandatoryError = function (f) {
    errorAction([{ name: 'field', value: f }]);
    gpkiBlockWidget.hide();
  };
}).apply(gpki);

/**
 * After request_mediumList()
 * @param msg
 */
function parseResultMedium(msg) {
  $(document).ready(
    function () {
		const obj = jQuery.parseJSON(msg);
      const response = obj.response;
      var mediumTypeId;
		if (obj.status) {
        var mediumArr = jQuery.parseJSON(response);
				mediumTypeId = mediumArr[0].id;
        var id;
        //pick roaming otp over token
				for (var i = 0; i < mediumArr.length; i++) {
          id = parseInt(mediumArr[i].id, 10);
					if (id === 5) {
						mediumTypeId = 5;
						break;
					} else if (id === 4 && mediumTypeId !== 4) {
						mediumTypeId = 4;
					}
				}
        setMediumType([{ name: 'message', value: obj.message },
	        	{ name: 'mediumTypeId', value: mediumTypeId },
	        	{ name: 'response', value: msg },
                       { name: 'js_nric', value: gpki.nric()} ]);
			if (mediumTypeId === 5) {
				gpki.requestOtp();
			} else if (mediumTypeId !== 4) {
				gpki_dlg.hide();
				gpki_dlg2.show();
				showError(obj.status, "Medium Token atau Roaming OTP sahaja", msg);
			}
		} else {
			gpki_dlg.hide();
			gpki_dlg2.show();
			showError(obj.status, obj.message, msg);
		}
    });
}

/**
 * After request_otp()
 * @param msg
 */
function parseTxResultRequestOTP(msg) {
  $(document).ready(
    function () {
		const obj = jQuery.parseJSON(msg);
		if (obj.status) {
			gpkiRequestTime = Date.now();
			disableOtpTimeout();
			const response = jQuery.parseJSON(obj.response);
			gpkiNonce = response.nonce;
			saveNonce([ { name: 'gpkiNonce', value: gpkiNonce }]);
		} else {
			disableOtpTimeout();
			showError(obj.status, obj.message, msg);
		}
    });
}

/**
 * After sign()
 * @param msg
 */
function parseSignResult(msg) {
  $(document).ready(
    function () {
		const obj = jQuery.parseJSON(msg);
		if (obj.status_code === 0) {
        postSignOTP([ { name: 'message', value: obj.message },
				{ name: 'subjectDN', value: obj.subject_dn },
				{ name: 'textBeforeSign', value: gpki.textBeforeSign() },
				{ name: 'signedText', value: obj.signed_data },
                      { name: 'response', value: msg }]);
		} else {
			showError(obj.status_code, obj.status_message, msg);
		}
		gpkiBlockWidget.hide();
    });
}

/**
 * After sign_roaming()
 * @param msg
 */
function parseTxResultSigningRoaming(msg) {
  $(document).ready(
    function () {
		const obj = jQuery.parseJSON(msg);
		if (obj.status) {
			clearTimeout(otpTimeoutId);
			const response = jQuery.parseJSON(obj.response);
        postSignOTP([{ name: 'message', value: obj.message },
	        	{ name: 'subjectDN', value: response.subject_dn },
	        	{ name: 'textBeforeSign', value: gpki.textBeforeSign() },
	        	{ name: 'signedText', value: response.signed_data },
			         { name: 'response', value: msg }]);
		} else {
			showError(obj.status, obj.message, msg);
		}
		gpkiBlockWidget.hide();
    });
}

function showError(code, msg, response) {
  errorAction([{ name: 'statusCode', value: code },
               { name: 'statusMsg', value: msg },
               { name: 'response', value: response }]);
}

/**
 * Get timeout in ms, depending on when OTP was last requested
 * @returns {Number}
 */
function getTimeoutMillis(){
  const DEFAULT_TIMEOUT = 120 * 1000;
  try {
    const timeout = gpki.otpExp() * 1000;
    const diff = Date.now() - gpkiRequestTime;
    if (isNaN(timeout) || isNaN(diff) || diff < 0 || diff > timeout) {
      return DEFAULT_TIMEOUT;
    }
	return Math.max(timeout - diff, 0);
  } catch (e){ }
  return DEFAULT_TIMEOUT;
}

/**
 * @param v raw property value
 * @param d fallback when absent or not a positive number
 * @returns {Number}
 */
function positiveOr(v, d) {
  const n = Number(v);
  return isNaN(n) || n <= 0 ? d : n;
}

/**
 * POST to GPKI with a bounded timeout, single attempt. Replaces the vendor
 * Gpki.runScript* wrappers, whose async:false requests cannot honour a
 * timeout and report nothing when the host is unreachable.
 * @param url endpoint
 * @param param request body, sent base64-encoded
 * @param onDone parser for a successful response
 * @param onGiveUp called with the failure reason when the request fails or times out
 */
function gpkiPost(url, param, onDone, onGiveUp) {
  const body = JSON.stringify(param);
  const encoded = window.btoa ? btoa(body) : Base64.encode(body);
  Gpki.start();
  $.ajax({
    url: url,
    type: "POST",
    cache: false,
    async: true,
    dataType: "json",
    data: encoded,
    contentType: "application/json; charset=utf-8",
    timeout: gpkiReqTimeout * 1000
  }).done(function (data, textStatus, jqxhr) {
    Gpki.end();
    onDone(jqxhr.responseText);
  }).fail(function (jqxhr, textStatus) {
    Gpki.end();
    // "timeout"/0, "error"/0 unreachable, "error"/502 host down, "parsererror"/200 non-JSON body
    onGiveUp(textStatus + "/" + jqxhr.status);
  });
}

/**
 * Medium lookup unanswered: drop the spinner dialog, which has no close button,
 * and report on the signing dialog the same way a rejected lookup does.
 */
function giveUpMedium(reason) {
  gpki_dlg.hide();
  gpki_dlg2.show();
  unavailableError("medium " + reason);
}

/**
 * OTP request unanswered: brief cooldown, then let the user try again.
 */
function giveUpOtp(reason) {
  disableBtn();
  // same slot the success path uses, so closing the dialog cancels it
  otpTimeoutId = setTimeout(enableBtn, OTP_RETRY_COOLDOWN);
  unavailableError("otp " + reason);
}

/**
 * Signing unanswered: unblock the panel, never re-sign on the user's behalf.
 */
function giveUpSign(reason) {
  gpkiBlockWidget.hide();
  unavailableError("sign " + reason);
}

/**
 * @param reason which call failed and why, logged server-side for triage
 */
function unavailableError(reason) {
  errorAction([{ name: 'gpkiUnavailable', value: reason }]);
}
