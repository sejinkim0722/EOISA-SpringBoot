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

	$('[data-toggle="sticky-onscroll"]').each(function() {
		let sticky = $(this),
			stickyWrapper = $('<div>').addClass('sticky-wrapper');

		sticky.before(stickyWrapper);
		sticky.addClass('sticky');

		$(window).on('scroll.sticky-onscroll resize.sticky-onscrol', () => {
			stickyToggle(sticky, stickyWrapper, $(this));
		});

		stickyToggle(sticky, stickyWrapper, $(window));
	});


	// Sidenav(Mobile) btn
	$(document).on("click", ".btn-sidenav-open", () => {
		$(".sidenav").css("left", "0vw");
		$(".sidenav-overlay").show();
	});

	$(document).on("click", ".btn-sidenav-close", () => {
		$(".sidenav").css("left", "-70vw");
		$(".sidenav-overlay").hide();
	});
    
	$(document).on("click", ".sidenav-overlay", () => {
		$(".sidenav").css("left", "-70vw");
		$(".sidenav-overlay").hide();
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


	// Quick Link
	let $link = $('#navbar a.dot');

	$link.on('click', function() {
		let target = $($(this).attr('href'));
		
		$('html, body').animate({
			scrollTop: target.offset().top
		}, 500);
	});


	// page anchor
	$(window).scroll(function() {
		let bottomObject = $('#gr-1').offset().top + $('#gr-1').outerHeight() - 300,
			bottomObject2 = $('#gr-2').offset().top + $('#gr-2').outerHeight() - 300,
			bottomObject3 = $('#gr-3').offset().top + $('#gr-3').outerHeight() - 300,
			bottomObject4 = $('#gr-4').offset().top + $('#gr-4').outerHeight() - 300,
			bottomObject5 = $('#gr-5').offset().top + $('#gr-5').outerHeight() - 300,
			bottomObject6 = $('#gr-6').offset().top + $('#gr-6').outerHeight() - 300,
			bottomWindow = $(window).scrollTop() + $(window).height();
	    
		if(bottomWindow > bottomObject * 0.5) {
			$('#border').animate({'opacity':'1'}, 1200);
		}
		if(bottomWindow > bottomObject2) {
			$('#border2').animate({'opacity':'1'}, 1200);
		}
		if(bottomWindow > bottomObject3) {
			$('#border3').animate({'opacity':'1'}, 1200);
		}
		if(bottomWindow > bottomObject4) {
			$('#border4').animate({'opacity':'1'}, 1200);
		}
		if(bottomWindow > bottomObject5) {
			$('#border5').animate({'opacity':'1'}, 1200);
		}
		if(bottomWindow > bottomObject6) {
			$('#border6').animate({'opacity':'1'}, 1200);
		}
	});
});