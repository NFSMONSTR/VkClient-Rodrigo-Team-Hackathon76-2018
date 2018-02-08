package VkClient;

public class Post {
    private String name;
    private String text;
    private String image;
    private String ava;
    private String date;

    public Post(String name, String text, String image, String ava, String date) {
        this.ava = ava;
        this.image = image;
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public String getAva() {
        return ava;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }
}
