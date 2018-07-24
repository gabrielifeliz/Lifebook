package com.lifebook.Model;
import com.lifebook.Service.ValidEmail;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @NotNull
    @NotEmpty(message = "Please provide your first name")
    private String firstName;

    @NotNull
    @NotEmpty(message = "Please provide your last name")
    private String lastName;

    @NotNull
    @ValidEmail(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;

    @NotNull
    @NotEmpty(message = "Please provide an username")
    private String username;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<AppRole> roles;

    @ManyToMany
    private Set<AppUser> following;

    @OneToMany(mappedBy = "creator")
    private Set<UserPost> posts;

    @OneToMany(mappedBy = "settingUser")
    private Set<Setting> settings;

    @ManyToMany
    private Set<Interest> interests = new HashSet<>();

    private String password;

    private String profilePic;

    private String zipCode;

    private boolean enabled;

    private String confirmationToken;

    private boolean myFriend = false;

    private int noOfFriends = 0;

    public AppUser() {
        this.roles = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<AppRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AppRole> roles) {
        this.roles = roles;
    }

    public Set<AppUser> getFollowing() {
        return following;
    }

    public void setFollowing(Set<AppUser> following) {
        this.following = following;
    }

    public Set<UserPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<UserPost> posts) {
        this.posts = posts;
    }

    public Set<Setting> getSettings() {
        return settings;
    }

    public void setSettings(Set<Setting> settings) {
        this.settings = settings;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public boolean isMyFriend() {
        return myFriend;
    }

    public void setMyFriend(boolean myFriend) {
        this.myFriend = myFriend;
    }

    public int getNoOfFriends() {
        return noOfFriends;
    }

    public void setNoOfFriends(int noOfFriends) {
        this.noOfFriends = noOfFriends;
    }

    public Set<Interest> getInterests() {
        return interests;
    }

    public void setInterests(Set<Interest> interests) {
        this.interests = interests;
    }
}
