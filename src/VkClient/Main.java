package VkClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowsConfig.login_resource));
        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        primaryStage.setTitle(WindowsConfig.login_name);
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, WindowsConfig.login_width, WindowsConfig.login_height);
        apiWrapper.connectToApi();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        controller.setStage(primaryStage);
        controller.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {
        if(new File("settings.vk").isFile()){
            BufferedReader br = new BufferedReader(new FileReader("settings.vk"));
            String token = br.readLine();
            if(token.indexOf("code")!=-1)
            {
                apiWrapper.connectToApi();
                String id = br.readLine();
                apiWrapper.loginUsingToken(token.substring(token.indexOf("=")+1),id.substring(id.indexOf("=")+1));
                WindowsConfig.createMainWindow();
            } else
                launch(args);
        } else {
            PrintWriter writer = new PrintWriter("settings.vk", "UTF-8");
            writer.println("");
            writer.println("");
            writer.close();
            launch(args);
        }
    }
}
