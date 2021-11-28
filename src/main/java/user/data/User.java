package user.data;

public abstract class User {
    private String fName,
                   lName,
                   username,
                   email,
                   password;

    public String getFName() {return fName;}
    public void setFName(String fName) {this.fName = fName;}

    public String getLName() {return lName;}
    public void setLName(String lName) {this.lName = lName;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}