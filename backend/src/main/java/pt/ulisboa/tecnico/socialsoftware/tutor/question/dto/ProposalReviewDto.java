package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;
import java.io.Serializable;

public class ProposalReviewDto implements Serializable {
    private String reason;
    private boolean approve;

    public ProposalReviewDto() {
        //empty
    }

    public boolean getApprove() {
        return approve;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ProposalReviewDto{" +
                "reason=" + reason +
                ", approve=" + approve +
                '}';
    }
}
