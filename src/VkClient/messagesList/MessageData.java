package VkClient.messagesList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.io.IOException;

public class MessageData {
    @FXML
    private HBox hBox;
    @FXML
    private ImageView messageImage;
    @FXML
    private Label messageTitle;
    @FXML
    private Label messageText;

    public MessageData()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Message.fxml"));
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

    public void setInfo(String title, String text, Image image) {
        messageImage.setImage(image);
        messageText.setText(text);
        messageTitle.setText(title);
    }

    public HBox getBox() {
        return hBox;
    }
}
