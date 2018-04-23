package savetheplanet.team8.project.com.savetheplanet.model;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String userId;
    private String email;
    private String companyName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    private List<Product> products;
    public User() {

    }

    public User(String email, String name, String lastName, String companyName){
        this.email = email;
        this.firstName = name;
        this.lastName = lastName;
        this.companyName = companyName;
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
}
