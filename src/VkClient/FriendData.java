package VkClient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class FriendData {
    @FXML
    private HBox hBox;
    @FXML
    private Label username;
    @FXML
    private ImageView userImage;

    public FriendData()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Friend.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(String name, Image img)
    {
        userImage.setImage(img);
        username.setText(name);
    }

    public HBox getBox()
    {
        return hBox;
    }
}
