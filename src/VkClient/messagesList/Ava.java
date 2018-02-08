package VkClient.messagesList;

import javafx.scene.image.Image;

public class Ava {
    int id;
    Image img;
    String name;
    public Ava(int id, String name, String image) {
        this.id = id;
        this.name = name;
        img = new Image(image,true);
    }

    public Integer getId() {
        return id;
    }

    public Image getImg() {
        return img;
    }

    public String getName() {
        return name;
    }
}
