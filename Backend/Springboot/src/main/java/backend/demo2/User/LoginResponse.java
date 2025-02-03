package backend.demo2.User;



public class LoginResponse {
    private String message;
    private Integer userId; // Use the appropriate type based on your User ID type

    public LoginResponse(String message, Integer userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUserId() {
        return userId;
    }
}
