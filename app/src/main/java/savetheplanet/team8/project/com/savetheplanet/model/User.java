package savetheplanet.team8.project.com.savetheplanet.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String firstName;
    private String lastName;
    private String userId;
    private String email;
    private String companyName;
    private String userType;
    private HashMap<String, Object> products;
    private String profile;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Map<String, Object> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, Object> products) {
        this.products = products;
    }

    public User() {

    }

    public User(String email, String name, String lastName, String companyName, String userType, HashMap<String, Object> products, String profile) {
        this.email = email;
        this.firstName = name;
        this.lastName = lastName;
        this.companyName = companyName;
        this.userType = userType;
        this.products = products;
        this.profile = profile;
    }

    public String getName() {
        return firstName;
    }

    public void setName(String name) {
        this.firstName = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
