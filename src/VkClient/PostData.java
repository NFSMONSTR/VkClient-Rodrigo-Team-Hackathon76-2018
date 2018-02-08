/*
    VkClient - client for vk.com
    Copyright (C) 2018  Belyaev Maxim(NFS_MONSTR), Aleksandr Novozhilov(MrLolthe1st), Grigoriy Zhukov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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