
/**
 * Address of Agent's servlet
 * @type String
 */
var host = "https://127.0.0.1:8441/";


/**
 * The time for Agent to response
 * @type Number
 */
var transaction_timeout = 9000;
var url_timeout = 15000;



/**
 * Gpki Class to add functions to GPKI-Agent API
 * @type GpkiClass
 */
var Gpki = {
/**
 * Loading JS file and scripts
 * @param {type} src address of file
 * @param {type} callback The function that will be running after a successfull loading
 * @returns {undefined}
 */
load: function(src, callback) {
	var script = document.createElement('script'), loaded;
	script.setAttribute('src', src);
	if (callback) {
		script.onreadystatechange = script.onload = function()
		{
			if (!loaded)
			{
				callback();
			}
			loaded = true;
		};
	}
	document.getElementsByTagName('head')[0].appendChild(script);
},
/**
 * Run an Gpki specified function
 * @param {type} src address of function
 * @param {type} callback function to be run after a successful calling
 * @param {type} failedcallback function to be run after failing
 * @returns {undefined} Not applicable
 */
runScript: function(src,callback,failedcallback){
	//$.ajaxSetup({cache:false})
 	Gpki.start();
	$("#spacer").ajaxError(function(e, jqxhr, settings, exception) {
		if (settings.dataType=='script') {
			Gpki.alert('Error in parsing data'+ exception);
		}
	});
	
	var jqxhr;
	var reqURLTimeout = setTimeout(function()
	{
		Gpki.end();	
		parseTimeout('{"status_code":888,"status_message":"AGENT TIMEOUT","status_ext_message":"The result was not recieved whithin expected time. Please check if GPKI Agent is running and try again."}');
		if(jqxhr!=null){ 
			jqxhr.abort();
		}			
	}, url_timeout);
	
	jqxhr = $.ajax({ 
		url: src,
		type: "GET",
		cache: false,
		async: false,
		dataType: "script", 
		statusCode: {
	    	404: function()
	    	{
	    		Gpki.alert("page not found");
	    	}
		},
		timeout: transaction_timeout
	}).done(function()
		{
		if (callback){
    		callback();
		}
	}).fail(function(jqXHR, textStatus)
		{
			console.log(jqXHR);
			if (textStatus == "timeout"){
				Gpki.alert("The result was not recieved whithin expected time.\n Please check if GPKI Agent is running and try again."); 
			}
			//else if (textStatus == "abort"){
				//Gpki.alert("Aborting reque");
			//}
			else{
				//Gpki.alert("A "+textStatus+" error hass happened. \nPlease try again later.");
			}
			if (failedcallback){
				failedcallback(); 
			}
	}).always(function() {
		clearTimeout(reqURLTimeout)
		Gpki.end();});

	},
 /*
 * Shows loading bar
 * @param {String()} message message that should be shown while waiting, it is "Please Wait..." by defualt
 * @returns {undefined}
 */
start : function (message){
   if($('.modal').length < 1)	{
		var $div = $('<div/>').appendTo('body');
		$div.attr('class', 'modal');
    }
    $("body").addClass("loading");
    $('.modal').show();
    if ($(".modal > p").length < 1){
    	$(".modal").append("<p/>");
    }
    if (typeof message == 'undefined'){
       // $(".modal > p").html("Please Wait...");
    }
    else {    
        $(".modal > p").html(message);
    }
},
/*
 * Hides loading bar
 * @returns {undefined}
 */
end: function(){
    $("body").removeClass("loading");
    $('.modal').hide();
},

alert: function(message){
	setTimeout(function(){alert(message);},0);
},

showError: function(message,header){
Gpki.showMessage(header+'<br>'+message,{'type':'error'})
},

showMessage: function(message, args, callback){
	var default_args =
		{
		'confirm'		:	false, 		// Ok and Cancel buttons
		'verify'		:	false,		// Yes and No buttons
		'input'			:	false, 		// Text input (can be true or string for default text)
		'animate'		:	false,		// Groovy animation (can true or number, default is 400)
		'type'          :	'info',     // info , error , success and question
        'textOk'		:	'Ok',		// Ok button default text
		'textCancel'	:	'Cancel',	// Cancel button default text
		'textYes'		:	'Yes',		// Yes button default text
		'textNo'		:	'No'		// No button default text
		}
	
	if(args) 
	{
		for(var index in default_args) 
			{ if(typeof args[index] == "undefined") args[index] = default_args[index]; } 
	}
	
	var aHeight = $(document).height();
	var aWidth = $(document).width();
	$('body').append('<div class="Gpki_Overlay" id="aOverlay"></div>');
	$('.Gpki_Overlay').css('height', aHeight).css('width', aWidth).fadeIn(100);
	$('body').append('<div class="Gpki_Outer"></div>');
	$('.Gpki_Outer').append('<div class="Gpki_Inner"></div>');
        //add images of error and info
    var cssclass = "";
    if(args){
    	cssclass = "Gpki_"+args['type'];
    }
    else cssclass = "Gpki_info";

	$('.Gpki_Inner').append('<div class="'+cssclass+'"><div></div><span>'+message+"</span></div>");
    $('.Gpki_Outer').css("left", ( $(window).width() - $('.Gpki_Outer').width() ) / 2+$(window).scrollLeft() + "px");
    
    if(args)
		{
		if(args['animate'])
			{ 
			var aniSpeed = args['animate'];
			if(isNaN(aniSpeed)) { aniSpeed = 400; }
			$('.Gpki_Outer').css('top', '-200px').show().animate({top:"100px"}, aniSpeed);
			}
		else
			{ $('.Gpki_Outer').css('top', '100px').fadeIn(200); }
		}
	else
		{ $('.Gpki_Outer').css('top', '100px').fadeIn(200); }
    
    if(args)
    	{
    	if(args['input'])
    		{
    		if(typeof(args['input'])=='string')
    			{
    			$('.Gpki_Inner').append('<div class="aInput"><input type="text" class="aTextbox" t="aTextbox" value="'+args['input']+'" /></div>');
    			}
    		else
    			{
				$('.Gpki_Inner').append('<div class="aInput"><input type="text" class="aTextbox" t="aTextbox" /></div>');
				}
			$('.aTextbox').focus();
    		}
    	}
    
    $('.Gpki_Inner').append('<div class="aButtons"></div>');
    if(args)
    	{
		if(args['confirm'] || args['input'])
			{ 
			$('.aButtons').append('<button value="ok">'+args['textOk']+'</button>');
			$('.aButtons').append('<button value="cancel">'+args['textCancel']+'</button>'); 
			}
		else if(args['verify'])
			{
			$('.aButtons').append('<button value="ok">'+args['textYes']+'</button>');
			$('.aButtons').append('<button value="cancel">'+args['textNo']+'</button>');
			}
		else
			{ $('.aButtons').append('<button value="ok">'+args['textOk']+'</button>'); }
		}
    else
    	{ $('.aButtons').append('<button value="ok">Ok</button>'); }
	
	$(document).keydown(function(e) 
		{
		if($('.Gpki_Overlay').is(':visible'))
			{
			if(e.keyCode == 13) 
				{ $('.aButtons > button[value="ok"]').click(); }
			if(e.keyCode == 27) 
				{ $('.aButtons > button[value="cancel"]').click(); }
			}
		});
	
	var aText = $('.aTextbox').val();
	if(!aText) { aText = false; }
	$('.aTextbox').keyup(function()
    	{ aText = $(this).val(); });
   
    $('.aButtons > button').click(function()
    	{
    	$('.Gpki_Overlay').remove();
		$('.Gpki_Outer').remove();
    	if(callback)
    		{
			var wButton = $(this).attr("value");
			if(wButton=='ok')
				{ 
				if(args)
					{
					if(args['input'])
						{ callback(aText); }
					else
						{ callback(true); }
					}
				else
					{ callback(true); }
				}
			else if(wButton=='cancel')
				{ callback(false); }
			}
		});

	}
};


function activate(type, certID, pin, question, answer)
{
	var url = host+"activate_cert?";	
	var params = "type=" + type + "&certID=" + certID  + "&pin=" + pin + "&question=" + question + "&answer=" + answer;	
	Gpki.runScript(url + params);
}

function sign(type, id, pin, plainText)
{
    var url = host+"sign?";	
    var params = "";
    if (arguments.length == 4)
         params = "type=" + type + "&text=" + plainText  + "&id=" + id + "&pin=" + pin;
    else
         params = "type=" + type + "&text=" + pin  + "&sessionID=" + id;
	params += "&detach=false";
    Gpki.runScript(url + params);
}

function sign_detach(type, id, pin, plainText)
{
    var url = host+"sign?";	
    var params = "";
    if (arguments.length == 4)
         params = "type=" + type + "&text=" + plainText  + "&id=" + id + "&pin=" + pin;
    else
         params = "type=" + type + "&text=" + pin  + "&sessionID=" + id;
	params += "&detach=true";
    Gpki.runScript(url + params);
}

/**
 * To sign using smart cards
 * @param pin PIN number of smart card
 * @param plainText the text that we want to sign
 * @returns
 */
function sign_sc(pin, plainText)
{
	var url = host+"sign_sc?";	
	var params = "pin=" + pin + "&text=" + plainText ;	
	params += "&detach=false";
	Gpki.runScript(url + params);

}

/**
 * To detach_sign using smart cards
 * @param pin PIN number of smart card
 * @param plainText the text that we want to sign
 * @returns
 */
function sign_sc_detach(pin, plainText)
{
	var url = host+"sign_sc?";	
	var params = "pin=" + pin + "&text=" + plainText ;	
	params += "&detach=true";
	Gpki.runScript(url + params);

}

/**
 * To sign using Secure Token
 * @param pin PIN number of smart card
 * @param plainText the text that we want to sign
 * @returns
 */
function sign_st(pin, plainText)
{
	var url = host+"sign_st?";	
	var params = "pin=" + pin + "&text=" + plainText ;	
	params += "&detach=false";
	Gpki.runScript(url + params);
}

/**
 * To detach_sign using Secure Token
 * @param pin PIN number of smart card
 * @param plainText the text that we want to sign
 * @returns
 */
function sign_st_detach(pin, plainText)
{
	var url = host+"sign_st?";	
	var params = "pin=" + pin + "&text=" + plainText ;	
	params += "&detach=true";
	Gpki.runScript(url + params);
}

/**
 * Verifies the signed text, it checks if the signed text is the same with the original text 
 * use verify(orignalText, signedText) if want to verify detached signature
 * use verify(signedText) if want to verify attached signature
 * @param originalText Text before signing
 * @param signedText Text after signing
 * @returns
 */
function verify(originalText, signedText)
{
	var url = host+"verify?";
	var params;
    if (arguments.length == 2){
        params = "originalText=" + originalText + "&signedText=" + signedText;
		params += "&detach=true";	
	}else{
        params = "signedText=" + originalText;	//originalText = signedText
		params += "&detach=false";	
	}	
	Gpki.runScript(url + params);
}

/**
 * Verifies the detached signed text, it checks if the signed text is the same with the original text 
 * @param originalText Text before signing
 * @param signedText Text after signing
 * @returns
 */
function verify_detach(originalText, signedText)
{
	var url = host+"verify?";	
	var params = "originalText=" + originalText + "&signedText=" + signedText;
	params += "&detach=true";
	Gpki.runScript(url + params);
}

/**
 * Encrypt a text for a recipent
 * @param recipient_id id of the reciever 
 * @param plainText Text to be encrypted
 * @returns
 */
function encrypt(recipient_id, plainText)
{
	var url = host+"encrypt?";	
	var params = "recipient=" + recipient_id + "&text=" + plainText;	
	Gpki.runScript(url + params);
}

/**
 * Decrypt a text
 * @param cipherText encrypted text
 * @param id id of the user 
 * @param pin pin number of user
 * @returns
 */
function decrypt(cipherText,id ,pin)
{
    var url = host+"decrypt?";	
    if (api_version =="1.3")
        var params = "type=softcert&text=" + cipherText+"&id="+id+"&pin="+pin;
    else
        var params = "type=" + cipherText + "&text=" + pin + "&sessionID=" + id;
    Gpki.runScript(url + params);
}



function download(certID, pinNo)
{
	var url = host+"download?";	
	var params = "id=" + certID + "&pin=" + pinNo;	
	Gpki.runScript(url + params);
}
/**
 * List all certificates
 * @returns
 */
function list()
{
	var url = host+"list?";	
		Gpki.runScript(url);
}

/**
 * Verify ID in the roaming server 
 * @param type softcert, roaming or smartcard
 * @param certID id of cert
 * @returns appends callback_verify_id() java script function
 */
function verify_id(type, certID)
{
	var url = host+"verify_id?";	
	var params = "certID=" + certID + "&type=" + type;
	Gpki.runScript(url + params);
}


function getagentinfo(type,ignoreerrors)
{
	var url = host+"agent_info?";	
	var params = "type=" + type;
	Gpki.runScript(url + params ,null,null,ignoreerrors);
}

/**
 * Check user's security questions
 * @param type type of certificates
 * @param certID ID of cert
 * @param pin Pin number
 * @param question choosen security question
 * @param answer answer to the security question 
 * @param timeout the timeout limitation
 * @returns
 */
function verify_question(type, certID, pin, question, answer, timeout)
{
	var url = host+"verify_question?";
	var params = "certID=" + certID + "&type=" + type + "&pin=" + pin + "&question=" + question + "&answer=" + answer + "&timeout=" + timeout;
	Gpki.runScript(url + params);	
}


function get_token_id(){
	var url = host+"get_token_id";	
	Gpki.runScript(url);
}

function change_so_pin(sopin,newsopin){
	var url = host+"change_sopin?";	
	var params = "sopin=" + sopin + "&newsopin=" + newsopin ;
	Gpki.runScript(url + params);	
}

function verify_so_pin(sopin){
        var url = host+"verify_sopin?";
	var params = "sopin=" + sopin;
	return Gpki.runScript(url + params);	
}



/**
 * Generates SHA-1 hash of string
 *
 * @param {String} msg                String to be hashed
 * @param {Boolean} [utf8encode=true] Encode msg as UTF-8 before generating hash
 * @returns {String}                  Hash of msg as hex character string
 */
Gpki.hash = function(msg, utf8encode) {
  utf8encode =  (typeof utf8encode == 'undefined') ? true : utf8encode;
  
  // convert string to UTF-8, as SHA only deals with byte-streams
  if (utf8encode) msg = Utf8.encode(msg);
  
  // constants [§4.2.1]
  var K = [0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6];
  
  // PREPROCESSING 
  
  msg += String.fromCharCode(0x80);  // add trailing '1' bit (+ 0's padding) to string [§5.1.1]
  
  // convert string msg into 512-bit/16-integer blocks arrays of ints [§5.2.1]
  var l = msg.length/4 + 2;  // length (in 32-bit integers) of msg + ‘1’ + appended length
  var N = Math.ceil(l/16);   // number of 16-integer-blocks required to hold 'l' ints
  var M = new Array(N);
  
  for (var i=0; i<N; i++) {
    M[i] = new Array(16);
    for (var j=0; j<16; j++) {  // encode 4 chars per integer, big-endian encoding
      M[i][j] = (msg.charCodeAt(i*64+j*4)<<24) | (msg.charCodeAt(i*64+j*4+1)<<16) | 
        (msg.charCodeAt(i*64+j*4+2)<<8) | (msg.charCodeAt(i*64+j*4+3));
    } // note running off the end of msg is ok 'cos bitwise ops on NaN return 0
  }
  // add length (in bits) into final pair of 32-bit integers (big-endian) [§5.1.1]
  // note: most significant word would be (len-1)*8 >>> 32, but since JS converts
  // bitwise-op args to 32 bits, we need to simulate this by arithmetic operators
  M[N-1][14] = ((msg.length-1)*8) / Math.pow(2, 32); M[N-1][14] = Math.floor(M[N-1][14]);
  M[N-1][15] = ((msg.length-1)*8) & 0xffffffff;
  
  // set initial hash value [§5.3.1]
  var H0 = 0x67452301;
  var H1 = 0xefcdab89;
  var H2 = 0x98badcfe;
  var H3 = 0x10325476;
  var H4 = 0xc3d2e1f0;
  
  // HASH COMPUTATION [§6.1.2]
  
  var W = new Array(80); var a, b, c, d, e;
  for (var i=0; i<N; i++) {
  
    // 1 - prepare message schedule 'W'
    for (var t=0;  t<16; t++) W[t] = M[i][t];
    for (var t=16; t<80; t++) W[t] = Gpki.ROTL(W[t-3] ^ W[t-8] ^ W[t-14] ^ W[t-16], 1);
    
    // 2 - initialise five working variables a, b, c, d, e with previous hash value
    a = H0; b = H1; c = H2; d = H3; e = H4;
    
    // 3 - main loop
    for (var t=0; t<80; t++) {
      var s = Math.floor(t/20); // seq for blocks of 'f' functions and 'K' constants
      var T = (Gpki.ROTL(a,5) + Gpki.f(s,b,c,d) + e + K[s] + W[t]) & 0xffffffff;
      e = d;
      d = c;
      c = Gpki.ROTL(b, 30);
      b = a;
      a = T;
    }
    
    // 4 - compute the new intermediate hash value
    H0 = (H0+a) & 0xffffffff;  // note 'addition modulo 2^32'
    H1 = (H1+b) & 0xffffffff; 
    H2 = (H2+c) & 0xffffffff; 
    H3 = (H3+d) & 0xffffffff; 
    H4 = (H4+e) & 0xffffffff;
  }

  return Gpki.toHexStr(H0) + Gpki.toHexStr(H1) + 
    Gpki.toHexStr(H2) + Gpki.toHexStr(H3) + Gpki.toHexStr(H4);
}

//
// function 'f' [§4.1.1]
//
Gpki.f = function(s, x, y, z)  {
  switch (s) {
  case 0: return (x & y) ^ (~x & z);           // Ch()
  case 1: return x ^ y ^ z;                    // Parity()
  case 2: return (x & y) ^ (x & z) ^ (y & z);  // Maj()
  case 3: return x ^ y ^ z;                    // Parity()
  }
}

//
// rotate left (circular left shift) value x by n positions [§3.2.5]
//
Gpki.ROTL = function(x, n) {
  return (x<<n) | (x>>>(32-n));
}

//
// hexadecimal representation of a number 
//   (note toString(16) is implementation-dependant, and  
//   in IE returns signed numbers when used on full words)
//
Gpki.toHexStr = function(n) {
  var s="", v;
  for (var i=7; i>=0; i--) { v = (n>>>(i*4)) & 0xf; s += v.toString(16); }
  return s;
}


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  Utf8 class: encode / decode between multi-byte Unicode characters and UTF-8 multiple          */
/*              single-byte character encoding (c) Chris Veness 2002-2010                         */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

var Utf8 = {};  // Utf8 namespace

/**
 * Encode multi-byte Unicode string into utf-8 multiple single-byte characters 
 * (BMP / basic multilingual plane only)
 *
 * Chars in range U+0080 - U+07FF are encoded in 2 chars, U+0800 - U+FFFF in 3 chars
 *
 * @param {String} strUni Unicode string to be encoded as UTF-8
 * @returns {String} encoded string
 */
Utf8.encode = function(strUni) {
  // use regular expressions & String.replace callback function for better efficiency 
  // than procedural approaches
  var strUtf = strUni.replace(
      /[\u0080-\u07ff]/g,  // U+0080 - U+07FF => 2 bytes 110yyyyy, 10zzzzzz
      function(c) { 
        var cc = c.charCodeAt(0);
        return String.fromCharCode(0xc0 | cc>>6, 0x80 | cc&0x3f); }
    );
  strUtf = strUtf.replace(
      /[\u0800-\uffff]/g,  // U+0800 - U+FFFF => 3 bytes 1110xxxx, 10yyyyyy, 10zzzzzz
      function(c) { 
        var cc = c.charCodeAt(0); 
        return String.fromCharCode(0xe0 | cc>>12, 0x80 | cc>>6&0x3F, 0x80 | cc&0x3f); }
    );
  return strUtf;
}

/**
 * Decode utf-8 encoded string back into multi-byte Unicode characters
 *
 * @param {String} strUtf UTF-8 string to be decoded back to Unicode
 * @returns {String} decoded string
 */
Utf8.decode = function(strUtf){
  // note: decode 3-byte chars first as decoded 2-byte strings could appear to be 3-byte char!
	var strUni = strUtf.replace(/[\u00e0-\u00ef][\u0080-\u00bf][\u0080-\u00bf]/g,  // 3-byte chars
	    function(c)
	    	{  // (note parentheses for precence)
		  		var cc = ((c.charCodeAt(0)&0x0f)<<12) | ((c.charCodeAt(1)&0x3f)<<6) | ( c.charCodeAt(2)&0x3f); 
		  		return String.fromCharCode(cc); 
		  	}
    );
	strUni = strUni.replace(/[\u00c0-\u00df][\u0080-\u00bf]/g,                 // 2-byte chars
      function(c)
      		{  // (note parentheses for precence)
        		var cc = (c.charCodeAt(0)&0x1f)<<6 | c.charCodeAt(1)&0x3f;
        		return String.fromCharCode(cc); 
        	}
    );
  return strUni;
}
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */