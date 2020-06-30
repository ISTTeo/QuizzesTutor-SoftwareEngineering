package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionRepository;

import java.util.List;

@RestController
public class DiscussionController {

    private DiscussionService service;

    DiscussionController(DiscussionService discussionService) {
        this.service = discussionService;
    }
                  
    @PostMapping("/questions/{questionId}/discussions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public DiscussionDto createDiscussion(@PathVariable int questionId, Authentication authentication, @Valid @RequestBody DiscussionDto discussion) {
        Integer userId = ((User) authentication.getPrincipal()).getId();
        return this.service.createDiscussion(questionId, userId, discussion);
    }

     @GetMapping("/questions/{questionId}/publicdiscussions")
     @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionId, 'QUESTION.ACCESS')")
     public List<DiscussionDto> getQuestionPublicDiscussions(@PathVariable int questionId) {
        return this.service.getQuestionPublicDiscussions(questionId);
     }

    @GetMapping("/discussions/student")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<DiscussionDto> checkUserDiscussionsStatus(Authentication authentication) {
        Integer userId = ((User) authentication.getPrincipal()).getId();
        return this.service.checkUserDiscussionsStatus(userId);
    }

    @PostMapping("/discussions/teacher")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getQuestionsDiscussions(@Valid @RequestBody List<Integer> questionIds) {
        return this.service.getQuestionsDiscussions(questionIds);
    }

    @GetMapping("/discussions/{discussionId}/question")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public QuestionDto getDiscussionQuestion(Authentication authentication, @PathVariable int discussionId) {
        Integer userId = ((User) authentication.getPrincipal()).getId();
        return this.service.getDiscussionQuestion(discussionId);
    }

    @PostMapping("/discussions/{discussionId}/public")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#discussionId, 'DISCUSSION.ACCESS')")
    public Boolean changeDiscussionPublicStatus(@PathVariable int discussionId, @Valid @RequestBody Boolean isPublic) {
        return this.service.changeDiscussionPublicStatus(discussionId, isPublic);
    }
}