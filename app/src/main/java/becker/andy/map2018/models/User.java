package becker.andy.map2018.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    private int UserId;
    @SerializedName("name")
    private String UserName;
    @SerializedName("user")
    private String User;
    @SerializedName("email")
    private String Email;
    @SerializedName("response")
    private String Response;
    @SerializedName("institution")
    private String Institution;

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getInstitution() {
        return Institution;
    }

    public void setInstitution(String institution) {
        Institution = institution;
    }
}
