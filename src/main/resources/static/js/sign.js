// AJAX setup
$.ajaxSetup({
	headers: {
		'X-CSRF-TOKEN': $('meta[name=_csrf_token]').attr('content')
	},
	timeout: 10000,
	cache: false
});

$(function() {
	// Hide Message Box
	$('.message-box').delay(3000).fadeOut('slow');

	// Popover init
	$('input').popover({
		container: 'body',
		placement: 'top',
		viewport: { selector: '.container' }
	});

	// Hide Popover
	$('input').on('focusout', function() {
		$(this).popover('hide');
	});

	// Toggle form
    $('.message a').on('click', () => {
		$('.register-form').animate({ height: 'toggle', opacity: 'toggle' }, 'slow');
		$('.signin-form').fadeToggle();
    });
 
	$('.find a').on('click', () => {
		$('.findpw-form').animate({ height: 'toggle', opacity: 'toggle' }, 'slow');
		$('.signin-form').fadeToggle();
	});


	// Validations
	let isEmailChecked = false,
		isNicknameChecked = false,
		isPasswordChecked = false;

	const emailRegex = new RegExp("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[A-Z]{2}|com|org|net|gov|mil|biz|info|ac.kr|name|aero|jobs|museum)");
	const passwordRegex = new RegExp("^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{4,}$");

	// Email(username) Check
	$(document).on('change keyup paste', '#input-signup-email', () => {
		let email = $('#input-signup-email').val();
		if(emailRegex.test(email)) {
			$('#input-signup-email').css('border', '2px solid seagreen');
			isEmailChecked = true;
		} else {
			$('#input-signup-email').css('border', '2px solid crimson');
			isEmailChecked = false;
		}
	});

	// Nickname Check
	$(document).on('change keyup paste', '#input-signup-nickname', () => {
		if($('#input-signup-nickname').val().trim().length >= 2) {
			$('#input-signup-nickname').popover('hide').css('border', '2px solid seagreen');
			isNicknameChecked = true;
		} else {
			$('#input-signup-nickname').popover('show').css('border', '2px solid crimson');
			isNicknameChecked = false;
		}
	});

	// Password Check
	$(document).on('change keyup paste', '#input-signup-password', () => {
		var password = $('#input-signup-password').val();
		if(passwordRegex.test(password)) {
			$('#input-signup-password').popover('hide').css('border', '2px solid seagreen');
			isPasswordChecked = true;
		} else {
			$('#input-signup-password').popover('show').css('border', '2px solid crimson');
			isPasswordChecked = false;
		}
	});

	// Signin Submit
	$(document).on('keypress', '#input-signin-email, #input-signin-password', (event) => {
		if(event.keyCode == 13) {
			$('#btn-signin').trigger('click');
		}
	});

	$(document).on('click', '#btn-signin', (event) => {
		event.preventDefault();

		if(emailRegex.test($('#input-signin-email').val()) && passwordRegex.test($('#input-signin-password').val())) {
			$('.signin-form').submit();
		} else {
			alert('이메일 주소와 비밀번호를 다시 확인해 주세요.');
			return false;
		}
	});

	// Signup Submit
	$(document).on('click', '#btn-signup', () => {
		if(isEmailChecked && isNicknameChecked && isPasswordChecked) {
			$('#btn-signup').prop('disabled', true);

			let params = { 
				username: $('#input-signup-email').val(),
				nickname: $('#input-signup-nickname').val(),
				password: $('#input-signup-password').val(),
				platform: $('#input-signup-platform').val()
			};

			$.post('/sign/verification/nickname', { nickname: params.nickname }, (data) => {
				if(data == 'NICKNAME_ALREADY_EXIST') {
					$('#btn-signup').prop('disabled', false);
					alert('중복된 닉네임이 존재합니다. 다른 닉네임을 사용해 주세요.');

					return;
				} else if(data == 'NICKNAME_CHECK_SUCCESS') {
					$('#btn-signup').text('잠시만 기다려 주세요 …');
					$('.register-form').submit();
				}
			});
		} else if(isEmailChecked === false) {
			alert('이메일 주소를 확인해 주세요.');
			return;
		} else if(isNicknameChecked === false) {
			alert('닉네임을 확인해 주세요.');
			return;
		} else if(isPasswordChecked === false) {
			alert('비밀번호를 확인해 주세요.');
			return;
		}
	});

	// Find Password
	$(document).on('click', '#btn-findpw', function() {
		if(emailRegex.test($('#input-findpw-email').val())) {
    		$('#btn-findpw').prop('disabled', true);
    		$('#btn-findpw').text('잠시만 기다려 주세요 …');
    		$('.findpw-form').submit();
		} else {
			alert('올바른 이메일 주소 형식이 아닙니다.');
			return;
		}
	});
});