package com.ksj.eoisa.controller;

import java.util.List;

import com.ksj.eoisa.dto.ReplyDTO;
import com.ksj.eoisa.service.ReplyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reply/")
public class ReplyController {
	
	@Autowired
	private ReplyService service;

	@PostMapping(value = "/new", consumes = "application/json", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> insertReply(@RequestBody ReplyDTO dto) {
		boolean isSucceedInsertReply = service.insertReplyService(dto);

		return isSucceedInsertReply ? new ResponseEntity<>("REPLY_INSERT_SUCCESS", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "/list/{dealno}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<ReplyDTO>> getReplylist(@PathVariable("dealno") int dealno) {
		return new ResponseEntity<>(service.getReplylistService(dealno), HttpStatus.OK);
	}

	@DeleteMapping(value = "/{replyno}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteReply(@PathVariable("replyno") int replyno) {
		boolean isSucceedDeleteReply = service.deleteReplyService(replyno);

		return isSucceedDeleteReply ? new ResponseEntity<>("REPLY_DELETE_SUCCESS", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(method = { RequestMethod.PUT, RequestMethod.PATCH }, value = "/{replyno}", consumes = "application/json", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> modifyReply(@RequestBody ReplyDTO dto, @PathVariable("replyno") int replyno) {
		dto.setReplyno(replyno);

		boolean isSucceedModifyReply = service.modifyReplyService(dto);

		return isSucceedModifyReply ? new ResponseEntity<>("REPLY_MODIFY_SUCCESS", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping(value = "/likeit", consumes = "application/json", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> manageReplyLikeit(@RequestBody ReplyDTO dto) {
		boolean isSucceedLikeit = service.manageReplyLikeitService(dto);

		if(isSucceedLikeit) {
			return new ResponseEntity<>("REPLY_LIKEIT_SUCCESS", HttpStatus.OK);
		} else if(!isSucceedLikeit) {
			return new ResponseEntity<>("REPLY_ALREADY_LIKEIT", HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}