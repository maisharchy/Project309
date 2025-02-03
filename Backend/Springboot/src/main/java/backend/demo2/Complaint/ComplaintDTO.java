package backend.demo2.Complaint;

public class ComplaintDTO {
    private Long complainedUserId; // ID of the user being complained against
    private Long complainantUserId; // ID of the user making the complaint
    private String reason; // Reason for the complaint

    // Getters and Setters
    public Long getComplainedUserId() {
        return complainedUserId;
    }

    public void setComplainedUserId(Long complainedUserId) {
        this.complainedUserId = complainedUserId;
    }

    public Long getComplainantUserId() {
        return complainantUserId;
    }

    public void setComplainantUserId(Long complainantUserId) {
        this.complainantUserId = complainantUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
