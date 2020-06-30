package pt.ulisboa.tecnico.socialsoftware.tutor.reply;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyService;
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.reply.ReplyRepository;

import java.util.List;


@RestController
public class ReplyController {

    private ReplyService service;

    ReplyController(ReplyService replyService) {
        this.service = replyService;
    }

    @PostMapping("/discussions/{discussionId}/replies")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#discussionId, 'DISCUSSION.ACCESS')")
    public ReplyDto createReply(@PathVariable int discussionId, Authentication authentication, @Valid @RequestBody ReplyDto reply) {
        Integer userId = ((User) authentication.getPrincipal()).getId();
        return this.service.createReply(discussionId, reply, userId);
    }

    @GetMapping("/discussions/{discussionId}/replies")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#discussionId, 'DISCUSSION.ACCESS')")
    public List<ReplyDto> findReply(@PathVariable int discussionId) {
        return this.service.findReply(discussionId);
    }
}
