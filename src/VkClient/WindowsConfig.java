package VkClient;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowsConfig {


    //login window
    public static String login_name = "Vk Client";
    public static int login_width = 265;
    public static int login_height = 310;
    public static String login_resource = "Login.fxml";


    //main
    public static String main_name = "Vk Client";
    public static int main_width = 1400;
    public static int main_height = 750;
    public static String main_resource = "YourPage.fxml";
    public static int minWidth = 1400, minHeight=750;


    public static void createMainWindow() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(main_resource));
                    Parent root = fxmlLoader.load();
                    Stage newStage = new Stage();
                    YourPageController controller = fxmlLoader.getController();
                    Scene newScene = new Scene(root, main_width, main_height);
                    newStage.setMinWidth(minWidth);
                    newStage.setMinHeight(minHeight);
                    controller.setStage(newStage);
                    controller.setScene(newScene);
                    newStage.setTitle(main_name);
                    newStage.setScene(newScene);


                    newStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
