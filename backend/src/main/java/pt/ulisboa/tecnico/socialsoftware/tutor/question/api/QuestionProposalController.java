package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionProposalDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposalReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionProposal;

@RestController
public class QuestionProposalController {
    private QuestionProposalService service;

    @Autowired
    private CourseRepository courseRepository;

    QuestionProposalController(QuestionProposalService questionProposalService) {
        this.service = questionProposalService;
    }

    @PostMapping("/courses/{courseId}/questionProposals")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionProposalDto submitQuestionProposal(Principal principal, @PathVariable int courseId, @Valid @RequestBody QuestionProposalDto questionProposal) {
        User author = (User) ((Authentication) principal).getPrincipal();
        questionProposal.setStatus(QuestionProposal.Status.PENDING.name());
        return this.service.submitQuestionProposal(
                courseId,
                author.getId().intValue(),
                questionProposal);
    }

    @PutMapping("/courses/{courseId}/questionProposals/{proposalId}/review")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionProposalDto reviewQuestionProposal(@PathVariable int proposalId, @PathVariable int courseId, @Valid @RequestBody ProposalReviewDto review) {
        QuestionProposalService.Decision decision = review.getApprove()
            ? QuestionProposalService.Decision.ACCEPT
            : QuestionProposalService.Decision.REJECT;
        return this.service.reviewQuestionProposal(proposalId, decision, review.getReason());
    }

    @PutMapping("/courses/{courseId}/questionProposals/{proposalId}/edit")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionProposalDto editQuestionProposal(Principal principal, @PathVariable int proposalId, @PathVariable int courseId, @Valid @RequestBody QuestionProposalDto editedQuestionProposalDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return this.service.editQuestionProposal(proposalId, user.getId(), editedQuestionProposalDto);
    }

    @PutMapping("/courses/{courseId}/questionProposals/{proposalId}/resubmit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionProposalDto reSubmitQuestionProposal(Principal principal, @PathVariable int proposalId, @PathVariable int courseId, @Valid @RequestBody QuestionProposalDto editedQuestionProposalDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return this.service.reSubmitQuestionProposal(proposalId, user.getId(), editedQuestionProposalDto);
    }

    @PutMapping("/courses/{courseId}/questionProposals/{proposalId}/convert")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionDto convertQuestionProposal(@PathVariable int proposalId, @PathVariable int courseId) {
        return this.service.convertQuestionProposal(proposalId);
    }

    @GetMapping("/questionProposals")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<QuestionProposalDto> listQuestionProposals(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        switch (user.getRole()) {
            case STUDENT:
                return this.service.listAuthorQuestionProposals(user.getId().intValue()).stream()
                    .map(QuestionProposalDto::new)
                    .collect(Collectors.toList());
            case TEACHER:
                return this.service.listTeacherQuestionProposals(user.getId().intValue()).stream()
                    .map(QuestionProposalDto::new)
                    .collect(Collectors.toList());
            default:
                // can safely be ignored due to PreAuthorize
                return null;
        }
    }
}
