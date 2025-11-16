package com.workify.worksphere.model.dto.response;

public class SignupResponse {
    private String userName;
    private String jobTitle;
    private String role;
    private String department;
    private String profilePictureUrl;
    private String email;
    private String password;

    public SignupResponse(String userName, String jobTitle, String role, String department,
                          String profilePictureUrl, String email) {
        this.userName = userName;
        this.jobTitle = jobTitle;
        this.role = role;
        this.department = department;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
    }

    // Getters and Setters
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
