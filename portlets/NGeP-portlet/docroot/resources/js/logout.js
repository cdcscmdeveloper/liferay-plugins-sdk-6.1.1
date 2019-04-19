function clickLogout() {
	var logoutUrl=document.getElementsByClassName('sign-out ')[0].getElementsByTagName('a')[0];
	window.location=logoutUrl.href;
}