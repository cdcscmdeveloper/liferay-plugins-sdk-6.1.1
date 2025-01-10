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
    otpExp = Number(args.otpExp);
    if (typeof otpExp !== "number") {
      otpExp = 120;
    }
    hostOTP = args.hostOTP;
    hostBroker = args.hostBroker;
    gpkiNonce = args.gpkiNonce;
    gpkiRequestTime = Number(args.nonceTime);
    if (typeof gpkiRequestTime !== "number" || isNaN(gpkiRequestTime)) {
    	gpkiRequestTime = 0;
    }
    this.requestMedium();
  };

  this.requestMedium = function () {
    request_mediumList(nric, appCode, modeUsed);
  };

  this.requestOtp = function () {
    request_otp(nric, agencyId, nric, modeUsed, otpExp, saluran, email);
  };

  this.signToken = function (a) {
    gpkiBlockWidget.show();
    text = encodeURIComponent(Gpki.hash(a[0]));
    const pin = a[1];
    if (pin === "") {
      this.mandatoryError(a[2]);
      return;
    }
    sign("token", nric, pin, text, "false", "data");
  };

  this.signRoam = function (a) {
	// check if undefined, null, NaN, empty string, etc.
	if (!gpkiNonce) {
	  errorAction([{ name: 'gpkiNonceNull', value: 'invalid nonce'}]);
	  return;
	}
	
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
    const data = encodeURIComponent(Gpki.hash(text));
    sign_roaming(nric, gpkiNonce, otpCode, data, pin, agencyId, modeUsed, signType, 0);
  };

  this.otpExp = function () { return otpExp; };
  
  this.textBeforeSign = function () { return text; };

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
                       { name: 'response', value: response }]);
        if (mediumTypeId === 5) {
          gpki.requestOtp();
        } else if (mediumTypeId !== 4) {
          gpki_dlg.hide();
          gpki_dlg2.show();
          showError(obj.status, "Medium Token atau Roaming OTP sahaja");
        }
      } else {
        gpki_dlg.hide();
        gpki_dlg2.show();
        showError(obj.status, obj.message);
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
        showError(obj.status, obj.message);
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
                      { name: 'signedText', value: obj.signed_data }]);
      } else {
        showError(obj.status_code, obj.status_message);
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
			         { name: 'signedText', value: response.signed_data }]);
      } else {
        showError(obj.status, obj.message);
      }
      gpkiBlockWidget.hide();
    });
}

function showError(code, msg) {
  errorAction([{ name: 'statusCode', value: code },
               { name: 'statusMsg', value: msg }]);
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