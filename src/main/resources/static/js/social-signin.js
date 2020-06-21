/**
 * Social Signin Popup
 */ 
$(function() { 
	window.name = 'parent';
	
	$(document).on('click', '#social-signin-naver', () => {
		$.get(location.origin + '/sign/oauth/naver/url', (data) => {
    		window.open(data, 'naver_signin', 'width=400, height=700');
		});
	});
	
	$(document).on('click', '#social-signin-kakao', () => {
		window.open('https://kauth.kakao.com/oauth/authorize?client_id=&redirect_uri=' + location.origin + '/sign/oauth/kakao/signin&response_type=code', 'kakao_signin', 'width=400, height=700');
	});
});