public class Admin {
    private String emailAddress; // email address
    private String password; // password

    //Account constructor initializes attributes
    public Admin(String theEmailAddress, String thePassword) {
        emailAddress = theEmailAddress;
        password = thePassword;
    } // end of the constructor

    // validate admin password
    public boolean validatePassword (String adminPassword) {
        return password.equals(adminPassword);
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }
}
