package VkClient;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import VkClient.VkAuth;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static VkClient.VkAuth.getCode;

public class Controller {
    private Scene scene = null;
    private Stage stage = null;
    private VkApiClient vk = null;

    private void initApi() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        initApi();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static String threadResponse = "", login = "", password = "", usercaptcha = "";
    public static boolean captchaButtonClicked = false;
    Thread currentRunning = null;

    public void loginThreadCompleted()
    {
        String code = threadResponse;
        apiWrapper.loginUsingCode(code);
        System.out.println("Login success!");


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {

                    WindowsConfig.createMainWindow();

                    stage.setOnCloseRequest(e -> Platform.exit());
                    scene.getWindow().hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void closeClicked(ActionEvent actionEvent) {
        System.exit(0);
    }
    public void loginClicked(ActionEvent actionEvent) {
        TextField loginField = (TextField) scene.lookup("#TALogin");
        currentRunning=new Thread(new LoginThread());
        PasswordField passwordField = (PasswordField) scene.lookup("#TAPass");
        try {
            login = loginField.getText();
            password = passwordField.getText();
            currentRunning.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String login(String username, String password) throws IOException,ElementNotFoundException {
        //get login page
        String url = apiWrapper.url, redirectUrl = apiWrapper.redirectUrl;
        if( !apiWrapper.captchaUrl.equals("")) {
            url = apiWrapper.captchaUrl;
        }
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.addRequestHeader("accept-language","ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setAppletEnabled(false);
        HtmlPage page = webClient.getPage(url);
        List<HtmlForm> list = page.getForms();
        //get form
        HtmlForm form = list.get(0);
        if(page.getElementById("captcha")!=null)
        {
            //We already have captcha, check completed?
            if(captchaButtonClicked)
            {
                ((HtmlTextInput)form.getInputByName("captcha_key")).setText(usercaptcha);
            } else
            {
                apiWrapper.captchaUrl = page.getBaseURI();
                return "captcha";
            }
        }
        HtmlTextInput loginInput = form.getInputByName("email");
        HtmlPasswordInput passwordInput = form.getInputByName("pass");
        //HtmlSubmitInput submitButton = form.getInputByValue("Log in");
        HtmlSubmitInput submitButton = (HtmlSubmitInput) page.getByXPath("/html/body//form//input[@type='submit']").get(0);

        //fill form
        loginInput.setText(username);
        passwordInput.setText(password);
        HtmlPage newPage = submitButton.click();

        //get code from redirected page
        String location = newPage.getBaseURL().toString();

        if (location.startsWith(redirectUrl)) {//if we got out code
            return getCode(location,redirectUrl);
        } else {

            if(newPage.getElementById("captcha")!=null)
            {
                //captcha displayed, set base auth url to current
                apiWrapper.captchaUrl=newPage.getBaseURI();
                return "captcha";
            }
            if(newPage.querySelector(".service_msg_warning")!=null)
            {
                return "err"+newPage.querySelector(".service_msg_warning").getFirstChild().toString();
            }
            System.out.println("VkAuth - Allow page was displayed");
            if(newPage.getWebResponse().getContentAsString().indexOf("азре")!=-1) {
                form = newPage.getForms().get(0);//vk show 'allow' page
                //submitButton = form.getInputByValue("Allow");
                submitButton = (HtmlSubmitInput) newPage.getByXPath("/html/body//form//input[@type='submit']").get(0);
                newPage = submitButton.click();
            }
            location = newPage.getBaseURL().toString();
            return getCode(location,redirectUrl);
        }
    }

    public void capchaEnteredClick(ActionEvent actionEvent) {
        usercaptcha = ((TextField) scene.lookup("#captchaText")).getText();
        captchaButtonClicked = true;
    }

    private double xOffset = 0;
    private double yOffset = 0;

    public void moveWindow(MouseEvent mouseEvent) {
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    public void moveWindowDragged(MouseEvent mouseEvent) {
        stage.setX(mouseEvent.getScreenX() - xOffset);
        stage.setY(mouseEvent.getScreenY() - yOffset);
    }

    public class LoginThread implements Runnable{
        public void run()
        {

            boolean completed = false;
            String code = "err";
            while(!completed) {
                try {
                    code = login(login, password);
                    if (code.startsWith("err")) {
                        String error = code.substring(3);
                        System.out.println("Some error occurred! Error: " + error);
                        apiWrapper.captchaUrl = "";
                        usercaptcha = "";
                        scene.lookup("#forget").setVisible(true);
                        scene.lookup("#TALogin").setVisible(true);
                        scene.lookup("#loginLabel").setVisible(true);
                        scene.lookup("#TAPass").setVisible(true);
                        scene.lookup("#passwordLabel").setVisible(true);
                        scene.lookup("#BtnLogIn").setVisible(true);
                        //captcha components  - > not visible
                        scene.lookup("#captchaImg").setVisible(false);
                        scene.lookup("#captchLabel").setVisible(false);
                        scene.lookup("#captchaButtonAccept").setVisible(false);
                        scene.lookup("#captchaText").setVisible(false);
                        scene.lookup("#TALogin").setDisable(false);
                        scene.lookup("#TAPass").setDisable(false);
                        scene.lookup("#BtnLogIn").setDisable(false);
                        scene.lookup("#forget").setDisable(false);

                        Label textField = (Label) scene.lookup("#errorText");
                        textField.setVisible(true);
                        Platform.runLater(()->
                        {
                            textField.setText(error);
                        });

                        //stage.setHeight(300);

                        return;
                    } else
                    if (code.equals("captcha")) {
                        System.out.println("captcha challenge! Wait user to resolve it...");
                        captchaButtonClicked = false;

                        String captchaUrl = apiWrapper.captchaUrl.substring(apiWrapper.captchaUrl.indexOf("?") + 1);
                        Map<String, String> args = VkAuth.getQueryMap(captchaUrl);

                        String sid = args.get("sid");

                        ImageView view = (ImageView) scene.lookup("#captchaImg");
                        view.setImage(new Image("https://m.vk.com/captcha.php?dif=1&sid=".concat(sid)));
                        view.setVisible(true);

                        scene.lookup("#forget").setVisible(false);
                        scene.lookup("#TALogin").setVisible(false);
                        scene.lookup("#loginLabel").setVisible(false);
                        scene.lookup("#TAPass").setVisible(false);
                        scene.lookup("#passwordLabel").setVisible(false);
                        scene.lookup("#BtnLogIn").setVisible(false);
                        //captcha components  - > visible
                        scene.lookup("#errorText").setVisible(false);
                        scene.lookup("#captchLabel").setVisible(true);
                        scene.lookup("#captchaButtonAccept").setVisible(true);
                        scene.lookup("#captchaText").setVisible(true);
                        scene.lookup("#TALogin").setDisable(true);
                        scene.lookup("#TAPass").setDisable(true);
                        scene.lookup("#BtnLogIn").setDisable(true);
                        scene.lookup("#forget").setDisable(true);


                        while (!captchaButtonClicked){
                            System.out.println("Wait...");
                            TimeUnit.SECONDS.sleep(1);
                        };

                    } else {
                        completed = true;
                        threadResponse = code;
                        loginThreadCompleted();
                    }
                } catch (Exception e) {
                    e=e;
                }
            }
        }
    }
}