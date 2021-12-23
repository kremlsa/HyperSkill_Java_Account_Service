package account.payload;

public class ChangeRequest {
    String new_password;

    public ChangeRequest() {
    }

    public ChangeRequest(String new_password) {
        this.new_password = new_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
