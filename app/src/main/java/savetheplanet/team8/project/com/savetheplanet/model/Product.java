package savetheplanet.team8.project.com.savetheplanet.model;

public class Product {

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

    private String name;

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getTag() {
        return tag;
    }

    private String description;
    private String location;
    private String tag;
    public Product(String name,String description,String location,String tag){
        this.name=name;
        this.description=description;
        this.location=location;
        this.tag=tag;
    }


}
