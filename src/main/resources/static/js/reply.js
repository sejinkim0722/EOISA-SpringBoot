/**
 * Reply
 */
$(function() { 	
	// Reply form height control
	$(document).on('input change cut paste', 'textarea[name=reply], textarea[name=re-reply]', function() {
		this.style.height = 'auto';
		this.style.height = this.scrollHeight + 'px';
	});

	// Show List
	$(document).on('click', '.btn-toggle', function() {
		let dealno = $(this).data('dealno');

		showReplyList(dealno)
		.done(() => {
			$('div.reply-box-' + dealno).slideToggle();
		});
	});
	
	function showReplyList(dealno) {
		let deferred = $.Deferred();
		let targetElement = $(document).find('.reply-list-' + dealno);
		
		replyService.list({ dealno: dealno }, function(data) {
			if(data.length === 0) {
				targetElement.html('<div style="text-align: center;"><p class="text-black-50">댓글이 없습니다.</p></div>');

				deferred.resolve();
				return deferred.promise();
			}

			let str = '';
			for(let i=0, len=data.length || 0; i < len; i++) {
				let reply = (data[i].depth == 1) ? 're-reply' : 'reply',
					username = $('input[name=username]').val(),
					modal = (!username) ? 'data-toggle=\"modal\" data-target=\"#modal-signin\"' : '';

				if(!data[i].profile_pic) data[i].profile_pic = '/images/profile.png';
				
				str += '<li class=\"' + reply + '\" data-replyno=' + data[i].replyno + '>';
				str += '	<div class="reply-thumbnail"><img src="' + data[i].profile_pic + '" class="rounded-circle"></div>';
				str += '	<div class="reply-body">';
				str += '		<div class="reply-content">';
				str += '			<p><strong>' + data[i].nickname + '</strong> <small class="text-black-50">' + data[i].writedate + '</small></p>';
				str += '			<div class="text-dark">' + data[i].content + '</div>';
				str += '		</div>';
				str += '	</div>';
				str += '	<div class="reply-opinion">';
				str += '		<p>';
				if(data[i].depth == 0) str += '<button type="button" class="btn btn-xs btn-re-reply" data-toggle="collapse" data-target=\".reply-reinput[data-replyno=\'' + data[i].replyno + '\']\"><small class="text-muted"><i class="fas fa-reply"></i>대댓글　</small></button>';
				str += '<button type="button" class="btn btn-xs btn-likeit-reply" ' + modal + ' data-replyno=\'' + data[i].replyno + '\' data-dealno=\'' + data[i].dealno + '\'><small class="text-muted"><i class="far fa-thumbs-up"></i>좋아요　</small></button><span class="badge badge-pill badge-success">' + data[i].likeitcount + '</span>';
				if(username == data[i].username) str += '<button type="button" class="btn btn-xs btn-delete-reply" data-replyno=\'' + data[i].replyno + '\' data-dealno=\'' + data[i].dealno + '\'><small class="text-muted"><i class="fas fa-eraser"></i>삭제</small></button>';
				str += '		</p>';
				str += '	</div>';
				str += '	<div class="reply-reinput collapse" data-replyno=\"' + data[i].replyno + '\">';
				str += '		<form data-replyno=\"' + data[i].replyno + '\" data-dealno=\"' + data[i].dealno + '\"><textarea name="re-reply" rows="1" maxlength="500" spellcheck="false" required ' + modal + '></textarea></form>';
				if(username != null) str += '<button class="btn btn-submit">확인</button>';
				str += '	</div>';
				str += '</li>';
				if(i < len - 1) str += '<hr>';
			}
			targetElement.html(str);

			deferred.resolve();
		});
		return deferred.promise();
	}
	
	// Delete
	$(document).on('click', '.btn-delete-reply', function() {
		let replyno = $(this).data('replyno'),
			dealno = $(this).data('dealno');
		
		if(confirm('정말로 댓글을 삭제하시겠습니까?')) {
			replyService.remove(replyno, dealno);
		} else {
			return;
		}
	});
	
	// Submit
	$(document).on('click', '.btn-submit', function() {		
		let content = $(this).prev().find('textarea').val();

		if(!content.trim()) {
			alert('댓글 내용을 입력해 주세요.'); 
			return;
		}
		if(confirm('댓글을 작성하시겠습니까?') === false) return;

		let replyData = { 
			dealno: $(this).prev().data('dealno'), 
			username: $('input[name=username]').val(), 
			nickname: $('input[name=nickname]').val(), 
			profile_pic: $('input[name=profile_pic]').val(), 
			content: content, 
			ref: $(this).prev().data('replyno'), 
			depth: $(this).prev().data('replyno') ? 1 : 0
		};
		
		replyService.insert(replyData, () => {
			$('textarea').val('');
			showReplyList(replyData.dealno).done(() => {
				$('div.reply-box-' + replyData.dealno).show();
			});
		});
	});
	
	// Likeit
	$(document).on('click', '.btn-likeit-reply', function() {	
		let username = $('input[name=username]').val(),
			replyno = $(this).data('replyno'),
			dealno = $(this).data('dealno'),
			params = { 
				username: username, 
				replyno: replyno
			};
		
		if(!username) return;

		replyService.likeit(params, (result) => {
			if(result == 'REPLY_ALREADY_LIKEIT') {
				alert('이미 좋아요한 댓글입니다.'); 
				return;
			} else {
				showReplyList(dealno);
			}
		});
	});
	
	// Services
	let replyService = (function() {
		function insert(reply, callback, error) {
			$.ajax({
				type: 'POST',
				url: '/reply/new',
				data: JSON.stringify(reply),
				contentType: 'application/json',
				success: function(result) {
					if(callback) {
						callback(result);
					}
				},
				error: function(err) {
					if(error) {
						error(err);
					} 
				}
			});
		}
		
		function list(param, callback, error) {
			let dealno = param.dealno;
			
			$.getJSON('/reply/list/' + dealno, (data) => {
				if(callback) {
					callback(data);
				}
			})
			.fail((err) => {
				if(error) {
					error(err);
				}
			});
		}
		
		function remove(replyno, dealno, callback, error) {
			$.ajax({
				type: 'DELETE',
				url: '/reply/' + replyno,
				success: (result) => {
					if(callback) {
						callback(result);
					}
				},
				error: (err) => {
					if(error) {
						error(err);
					}
				},
				complete: () => {
					showReplyList(dealno);
				}
			});
		}
		
	  	function modify(reply, callback, error) {
	  		$.ajax({
	  			type: 'PATCH',
	  			url: '/reply/' + reply.replyno,
	  			data: JSON.stringify(reply),
	  			contentType: 'application/json',
	  			success: (result) => {
	  				if(callback) {
	  					callback(result);
	  				}
	  			},
	  			error: (err) => {
	  				if(error) {
	  					error(err);
	  				}
	  			}
	  		});
	  	}
	  	
	  	function likeit(params, callback, error) {
			$.ajax({
				type: 'POST',
				url: '/reply/likeit',
				data: JSON.stringify(params),
				contentType: 'application/json',
				success: (result) => {
					if(callback) {
						callback(result);
					}
				},
				error: (err) => {
					if(error) {
						error(err);
					} 
				}
			});
	  	}
		
		return { 
			list: list,
			insert: insert,
			remove: remove,
			modify: modify,
			likeit: likeit
		};
	})();
});