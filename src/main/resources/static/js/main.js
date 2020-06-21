
// AJAX setup
$.ajaxSetup({
	headers: {
		'X-CSRF-TOKEN': $('meta[name=_csrf_token]').attr('content')
	},
	timeout: 10000,
	cache: false
});

$(function() {
	// Hide page loading indicator
	$('.page-loading-indicator').fadeOut(100);

	// Sticky Navbar
	let stickyToggle = (sticky, stickyWrapper, scrollElement) => {
		let stickyHeight = sticky.outerHeight();
		let stickyTop = stickyWrapper.offset().top;
		
		if(scrollElement.scrollTop() >= stickyTop) {
			stickyWrapper.height(stickyHeight);
			sticky.addClass('is-sticky');
		} else {
			sticky.removeClass('is-sticky');
			stickyWrapper.height('auto');
		}
	};

	$('nav[data-toggle=sticky-onscroll]').each(function() {
		let sticky = $(this),
			stickyWrapper = $('<div>').addClass('sticky-wrapper');

		sticky.before(stickyWrapper);
		sticky.addClass('sticky');

		$(window).on('scroll.sticky-onscroll resize.sticky-onscrol', function() {
			stickyToggle(sticky, stickyWrapper, $(this));
		});

		stickyToggle(sticky, stickyWrapper, $(window));
	});


	// Sidenav(Mobile) btn
	$(document).on('click', '.btn-sidenav-open', () => {
		$('.sidenav').css('left', '0vw');
		$('.sidenav-overlay').show();
	});

	$(document).on('click', '.btn-sidenav-close', () => {
		$('.sidenav').css('left', '-70vw');
		$('.sidenav-overlay').hide();
	});
    
	$(document).on('click', '.sidenav-overlay', () => {
		$('.sidenav').css('left', '-70vw');
		$('.sidenav-overlay').hide();
	});


	// Follow Sidebar
	let mobileDetect = new MobileDetect(window.navigator.userAgent);
	if(!mobileDetect.mobile() && $(document).width() > 576) {
		$('#filter-body').addClass('show');
		
		let $sidebar = $('.follow'),
			$window = $(window),
			offset = $sidebar.offset(),
			topPadding = 45;

		$window.scroll(() => {
			if($window.scrollTop() > (offset.top + 500)) {
				$sidebar.stop().animate({
					marginTop: $window.scrollTop() - offset.top + topPadding
				}, 750);
			} else {
				$sidebar.stop().animate({
					marginTop: 0
				}, 750);
			}
		});
	}


	// Scroll to top button
	$(window).scroll(() =>  {
		if($(this).scrollTop() > 600) {
			$('#scrolltop').fadeIn(200);
		} else {
			$('#scrolltop').fadeOut(200);
		}
	});

	$('#scrolltop').click(() => {
		$('html, body').animate({
			scrollTop: 0
		}, 500);
		
		return;
	});


	// Ticker
	$('.ticker').easyTicker({
		direction: 'up',
		easing: 'swing',
		speed: 'slow',
		interval: 2500,
		height: 24,
		visible: 1,
		mousePause: 1,
	});
    
	$('#ranking').hover(() => {
		$('.ticker').stop().animate({ height: '240px' }, 250);
	}, () => {
		$('.ticker').stop().animate({ height: '24px' }, 250);
	});


	// Navbar Dropdown
	$(document).on('click', '.dropdown .dropdown-menu', (event) => {
		event.stopPropagation();
	});
	
	$('.dropdown').on('show.bs.dropdown', function() {
		$(this).find('.dropdown-menu').first().stop(true, true).fadeIn(200);
	});

	$('.dropdown').on('hide.bs.dropdown', function() {
		$(this).find('.dropdown-menu').first().stop(true, true).fadeOut(200);
	});


	// Mobile Topnav Active Effect   	
	let url = location.href;
    
	if(!url.includes('rank') && !url.includes('theme')) {
		$('.nav-item.home').addClass('active');
	} else if(url.includes('rank')) {
		$('.nav-item.rank').addClass('active');
	} else if(url.includes('theme')) {
		$('.nav-item.theme').addClass('active');
	} else {
		$('#left-sidebar').removeClass('component-desktop');
	}

                 
	// Search
	$(document).on('click', '#div-search .fa-search', function() {
		$('#form-search').submit();
	});
	
	$('#form-search').submit(function() {
		if($(this).children('input[name=keyword]').val().trim().length < 2) {
			alert('검색어는 최소 두 글자 이상이어야 합니다.');
			return;
		} else {
			$(this).attr('action', '/search/' + $('input[name=keyword]').val());
		}
	});


	// Clipboard Copy  
	let clipboard = new ClipboardJS('.btn-share');

	clipboard.on('success', function(e) {
		e.clearSelection();
		
		alert('URL이 클립보드에 복사되었습니다.');
	});

	clipboard.on('error', function(e) {
		alert('URL 복사 중 에러가 발생하였습니다.');
	});


	// Wishlist
	$(document).on('click', '.btn-wish', function() {
		let dealno = $(this).data('dealno'),
			username = $('input[name=username]').val(),
			params = { 
				username: username,
				dealno: dealno
			};

		if(!params.username) return;

		$.post('/wishlist', params)
		.done((result) => {
			if(result == 'WISHLIST_FULL') {
				alert('최대 10개의 핫딜만 찜 목록에 추가할 수 있습니다.');
				return;
			} else if(result == 'WISHLIST_SUCCESS') {
				$('.btn-wish[data-dealno=' + dealno + ']').children().toggleClass('far fas');
			}
		})
		.fail(() => {
			alert('찜하기 중 에러가 발생하였습니다.\n다시 시도해 주세요.');
			return;
		});
	});


	// Modify Userinfo
	let isNicknameChecked = true,
		isPasswordChecked = true;
	
	// Nickname Duplicate Check Button
	$(document).on('change keyup paste', '#input-nickname', () => {
		if($('#input-nickname').val() == $('input[name=nickname').val()) {
			$('#btn-namecheck').prop('disabled', true);
			isNicknameChecked = true;
		} else if(!$('#input-nickname').val().trim()) { 
			$('#btn-namecheck').prop('disabled', true);
			isNicknameChecked = false;
		} else {
			$('#btn-namecheck').prop('disabled', false);
			isNicknameChecked = false;
		}
	});
	
	// Nickname Duplicate Check
	$(document).on('click', '#btn-namecheck', () => {
		let nickname = $('#input-nickname').val();
		
		$.post('/sign/verification/nickname', { nickname: nickname }, (result) => {
			if(result == 'NICKNAME_ALREADY_EXIST') {
				$('.message-namecheck').css('color', '#ff5a5f');
				$('.message-namecheck').html('이미 존재하는 닉네임입니다.');
				isNicknameChecked = false;
			} else if(result == 'NICKNAME_CHECK_SUCCESS') {
				$('.message-namecheck').css('color', '#8ce071');
				$('.message-namecheck').html('사용 가능한 닉네임입니다.');
				isNicknameChecked = true;
			}
		});
	});
    
	// Password Check
	let passwordRegex = new RegExp('^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{4,}$');
	
	$(document).on('change keyup paste', '#input-password', () => {
		let password = $('#input-password').val();
		
		if(passwordRegex.test(password)) {
			$('.message-passwordcheck').css('color', '#8ce071');
			$('.message-passwordcheck').html('사용 가능한 비밀번호입니다.');
			isPasswordChecked = true;
		} else if(!passwordRegex.test(password) && password == '') {
			$('.message-passwordcheck').html('');
			isPasswordChecked = true;
		} else {
			$('.message-passwordcheck').css('color', '#ff5a5f');
			$('.message-passwordcheck').html('잘못된 비밀번호 형식입니다.');
			isPasswordChecked = false;
		}
	});
	
	// Profile Image Upload
	let isProfileImageChecked = false,
		uploadPath = '',
		blob;

	$(document).on('change', '#input-profile', function(event) {
		let file = event.target.files[0];
		console.log(file.type);

		if(file.size > 5242880) {
			alert('최대 5MB의 이미지만 허용됩니다. 다시 선택해 주세요.');
			$('#input-profile').val('');
			
			return;
		} else if(!file.type.match('image/jpeg|image/png')) {
			alert('업로드할 수 없는 파일 형식입니다. 다시 선택해 주세요.');
			$('#input-profile').val('');
			
			return;
		} else {
			let reader = new FileReader();
			reader.onload = function(event) {
				// Image Resizing
				let image = new Image();
				image.onload = function() {
					let canvas = document.createElement('canvas'),
						ctx = canvas.getContext('2d');
					ctx.drawImage(image, 0, 0);

					const MAX_WIDTH = 500,
						MAX_HEIGHT = 500;

					let width = image.width,
						height = image.height;

					if(width > height) {
						if(width > MAX_WIDTH) {
							height *= MAX_WIDTH / width;
							width = MAX_WIDTH;
						}
					} else {
						if(height > MAX_HEIGHT) {
							width *= MAX_HEIGHT / height;
							height = MAX_HEIGHT;
						}
					}
					canvas.width = width;
					canvas.height = height;
					ctx.drawImage(image, 0, 0, width, height);
                    
					dataURL = canvas.toDataURL(file.type, 0.5);
					$('.div-profile #profile-img').attr('src', dataURL);
					blob = dataURItoBlob(dataURL);

					isProfileImageChecked = true;
				}
				image.src = event.target.result;
			}
			reader.readAsDataURL(file);
		}
		
		function dataURItoBlob(dataURI) {
			let byteString;
			if(dataURI.split(',')[0].indexOf('base64') >= 0) {
				byteString = atob(dataURI.split(',')[1]);
			} else {
				byteString = unescape(dataURI.split(',')[1]);
			}
			let mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

			let ia = new Uint8Array(byteString.length);
			for(let i=0; i<byteString.length; i++) {
				ia[i] = byteString.charCodeAt(i);
			}

			return new Blob([ia], { type:mimeString });
		}
	});

	$(document).on('click', '.div-profile button', function(event) {
		$('.div-profile button').prop('disabled', true);
		
		if(isProfileImageChecked) {
			let formData = new FormData($('#form-profile')[0]);
			formData.set('profile_pic', blob);

			$.ajax({
				url: '/sign/upload/profileimage',
				type: 'POST',
				data: formData,
				processData: false,
				contentType: false,
				success: function(data) {
					uploadPath = data;
					alert('프로필 사진이 업로드되었습니다.');
				},
				error: function(err) {
					console.log(err);
					$('.div-profile button').prop('disabled', false);
					alert('파일 첨부 중 문제가 발생하였습니다.\n다시 시도해 주세요.');

					return;
				}
			});
		} else {
			$('.div-profile button').prop('disabled', false);
			alert('업로드된 사진이 없습니다.');
			
			return;
		}
	});
	
	// Modify Submit
	$(document).on('click', '#btn-complete', () => {
		if(isNicknameChecked == true && isPasswordChecked == true) {
			let params = { 
				username: $('input[name=username]').val() || '', 
				nickname: $('#input-nickname').val() || '', 
				password: $('#input-password').val() || '',
				profile_pic: uploadPath || ''
			};
			modify(params);
		} else if(isNicknameChecked == false) {
			alert('닉네임 중복 확인 여부를 확인해 주세요.');
			return;
		} else if(isPasswordChecked == false) {
			alert('비밀번호 형식을 확인해 주세요.');
			return;
		}
	});
	
	function modify(params) {
		$.post('/sign/modify/userinfo', params)
		.done((result) => {
			if(result == 'MODIFY_USERINFO_SUCCESS') {
				alert('회원 정보가 정상적으로 수정되었습니다.\n변경 사항은 다음 로그인부터 적용됩니다.');
			} else if(result == 'MODIFY_USERINFO_FAIL') {
				alert('회원 정보가 정상적으로 수정되지 않았습니다.\n다시 시도해 주세요.');
			}

			$('#modal-modify-userinfo').modal('hide');
		})
		.fail(() => {
			alert('회원 정보 수정 중 문제가 발생하였습니다.\n다시 시도해 주세요.');
			location.reload();
		});
	};

	// Infinite Scroll
	let path = location.pathname !== '/' ? location.pathname.concat('/') : location.pathname,
		param = '',
		lastIndex = $('#total-page').data('value');
	
	let initIS = function() {
		$('#main-content-wrapper').infiniteScroll({
			path: function() {
				if(this.loadCount < (lastIndex - 1)) {
					let nextIndex = this.loadCount + 2;

					return path + nextIndex + param;
				}
			},
			append: '#main-content',
			scrollThreshold: 200,
			history: true
		});
	}
	initIS();

	$('#main-content-wrapper').on('request.infiniteScroll', () => {
		$('.loading-indicator').show();
	});

	$('#main-content-wrapper').on('load.infiniteScroll', () => {
		$('.loading-indicator').hide();
	});

	// Filtering
	let scanFilterList = function() {
		let seletedFilter = { regions:[], sites:[], shops:[], isended:[] };

		$('ul.filter-menu li.filter-list').each(function() {
			let key = $(this).data('key'),
				value = $(this).data('value');

			if(key == 'region' && (!seletedFilter.regions.includes(value)) && $(this).hasClass('active')) {
				seletedFilter.regions.push(value);
			} else if(key == 'site' && (!seletedFilter.sites.includes(value)) && !$(this).hasClass('active')) {
				seletedFilter.sites.push(value);
			} else if(key == 'shop' && (!seletedFilter.shops.includes(value)) && !$(this).hasClass('active')) {
				seletedFilter.shops.push(value);
			} else if(key == 'isended' && (!seletedFilter.isended.includes(value)) && !$(this).hasClass('active')) {
				seletedFilter.isended.push(value);
			}
		});

		return seletedFilter;
	}
    
	let checkRegionFilterIsSelected = () => {
		if($('li.filter-list.active[data-key=region]').length < 1) {
			alert('지역 필터 조건은 최소 하나가 선택되어야 합니다.');
			return;
		} else {
			return true;
		}
	}
    
	$.ajaxSettings.traditional = true;
	$('.filter-list').click(function() {
		$(this).toggleClass('active');

		if(checkRegionFilterIsSelected() === false) {
			$(this).toggleClass('active');
			return;
		}
		
		let params = scanFilterList();

		$.ajax({
			type: 'GET',
			url: '/filter/1',
			data: { 
				region: params.regions, 
				site: params.sites, 
				shop: params.shops, 
				isended: params.isended 
			},
			dataType: 'html',
			success: function(data) {
				$('#main-content-wrapper').infiniteScroll('destroy');
				$('#main-content').remove();
				$(document).scrollTop(0);

				$('#main-content-wrapper').html(data);
				path = this.url.substr(0, this.url.indexOf('?') - 1);
				
				if($('li.filter-list.active').length == 20) {
					param = '';
					$('.current-page-info').hide();
				} else {
					param = this.url.substr(this.url.indexOf('?'));
				}
				
				lastIndex = $('#total-page').data('value');

				initIS(); // Reinitialize Infinite Scroll
			},
			error: () => {
				alert('페이지 로드 중 에러가 발생하였습니다.\n다시 시도해 주세요.');
				return;
			}
		});
	});

	$(document).on('click', '#filter button', function() {
		$(this).children().toggleClass('fa-angle-down fa-angle-up');
	});
});