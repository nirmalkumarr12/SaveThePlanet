package savetheplanet.team8.project.com.savetheplanet.model;

public class Product {

    private String name;
    private String description;
    private String location;
    private String tag;
    private String imageUrl;

    public Product(String name, String description, String location, String tag, String imageUrl) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.tag = tag;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getTag() {
        return tag;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
