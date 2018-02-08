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

public class PostData
{
    @FXML
    private VBox vBox;
    @FXML
    private Label postTitle;
    @FXML
    private Label postDate;
    @FXML
    private Label postText;
    @FXML
    private ImageView postAva;
    @FXML
    private ImageView postImg;

    public PostData()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Post.fxml"));
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

    public void setInfo(String name, String text, String ava, String date, String img)
    {
        postDate.setText(date);
        postText.setText(text);
        postTitle.setText(name);
        postAva.setImage(new Image(ava));
        if (!img.equals("")) {
            postImg.setImage(new Image(img));
        } else {
            postImg.setFitHeight(0);
            postImg.setFitWidth(0);
        }
    }

    public VBox getBox()
    {
        return vBox;
    }
}