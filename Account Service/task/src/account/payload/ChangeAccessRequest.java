package account.payload;

import javax.validation.constraints.NotNull;


public class ChangeAccessRequest {
    @NotNull
    private String user;
    @NotNull
    private String operation;

    public ChangeAccessRequest() {
    }

    public ChangeAccessRequest(String user, String operation) {
        this.user = user;
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}