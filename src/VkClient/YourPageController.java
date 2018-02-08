package VkClient;

import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.photos.PhotoXtrRealOffset;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import VkClient.messagesList.Ava;
import VkClient.messagesList.MessageData;

import java.sql.Time;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class YourPageController extends CustomController {

    private List < Dialog > allDialog = new ArrayList < >();
    private int messageLoadOffset = 0;
    private boolean loadEnded = false;

    private void dialogsNext() throws Exception {
        System.out.println("Getting next page of dialogs, current offset: " + messageLoadOffset + "");
        ListView < String > listView = (ListView < String > ) scene.lookup("#dialogsList");
        String sel = listView.getSelectionModel().getSelectedItem();

        if (loadEnded) return;
        List < Dialog > dialogs = Profile.getDialogs(messageLoadOffset, 200, 40);
        messageLoadOffset += 200;
        if (dialogs.size() < 200) loadEnded = true;
        if (dialogs.size() == 0) return;
        List < String > ids = new ArrayList < >();
        List < Dialog > nw = new ArrayList < >(dialogs);
        for (Dialog d: dialogs) {
            String title = d.getMessage().getTitle();
            if (title.equals("")) {
                if (d.getMessage().getUserId() < 0) nw.remove(d);
                else ids.add(((long) d.getMessage().getUserId()) + "");
            } else {
                //listView.getItems().add(d.getMessage().getTitle());
                System.out.flush();
            }
        }
        dialogs = nw;
        TimeUnit.MILLISECONDS.sleep(500);
        ids = Profile.getFullNames(ids);
        int i = 0;
        for (Dialog d: dialogs) {
            String title = d.getMessage().getTitle();
            if (title.equals("")) {
                if (ids != null && ids.size() > i) {
                    listView.getItems().add(ids.get(i));
                    i++;
                }
            } else {
                listView.getItems().add(d.getMessage().getTitle());
            }
            allDialog.add(d);
        }
        listView.getSelectionModel().select(sel);
        System.out.println("Ok.");
    }
    private Thread msgUpdater;@Override
    public void setScene(Scene scene) { // on create
        super.setScene(scene);
        try {

            stage.setOnCloseRequest(new EventHandler < WindowEvent > () {
                public void handle(WindowEvent we) {
                    msgUpdater.stop();
                }
            });
            msgUpdater = new Thread(new messageUpdater());
            msgUpdater.start();

            postsNew();



        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void sendMessageClick(ActionEvent ev){
        int sel=((ListView<Dialog>)scene.lookup("#dialogsList")).getSelectionModel().getSelectedIndex();
        if(sel<0) return;
        try {
            if (allDialog.get(sel).getMessage().getTitle().equals("")) {
                apiWrapper.apiClient.messages().send(apiWrapper.currentUser).userId(allDialog.get(sel).getMessage().getUserId()).peerId(allDialog.get(sel).getMessage().getUserId()).message(((TextArea) scene.lookup("#message")).getText()).execute();
            } else if (allDialog.get(sel).getMessage().getUserId() > 0) {
                System.out.println(sel + " " + allDialog.get(sel).getMessage().getTitle());
                apiWrapper.apiClient.messages().send(apiWrapper.currentUser).userId(allDialog.get(sel).getMessage().getUserId()).peerId(2000000000 + allDialog.get(sel).getMessage().getChatId()).message(((TextArea) scene.lookup("#message")).getText()).execute();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        ((TextArea)scene.lookup("#message")).setText("");
    }
    private boolean updateCompleted = true;
    public int selReq=0;
    public boolean isSelReq=false;
    private void updateDialogs() {

        System.out.println("Starting updating messages!");
        List < Dialog > nwq = null;
        try {
            TimeUnit.MILLISECONDS.sleep(800);
            nwq = apiWrapper.apiClient.messages().getDialogs(apiWrapper.currentUser).count(20).execute().getItems();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        ListView < String > listView = (ListView < String > ) scene.lookup("#dialogsList");
        List<Message> gt=null;
        if(listView.getSelectionModel().getSelectedIndex()!=-1){
            int sel=listView.getSelectionModel().getSelectedIndex();
            if(allDialog.get(sel).getMessage().getTitle().equals("")) {
                gt = Profile.getMessages(allDialog.get(sel).getMessage().getUserId(),allDialog.get(sel).getMessage().getUserId(),0,100);
            } else if(allDialog.get(sel).getMessage().getUserId()>0) {
                System.out.println(sel + " "+allDialog.get(sel).getMessage().getTitle());
                gt = Profile.getMessages(allDialog.get(sel).getMessage().getUserId(), 2000000000+allDialog.get(sel).getMessage().getChatId(),0,100);
            }
            System.out.println("Updating messages...");
            ListView<Message> o=((ListView<Message>)(scene.lookup("#messagesListView")));
            List<Message> nw=new ArrayList<Message>();
            for(Message nn: gt) {
                boolean f=true;
                for (Message x : o.getItems()) {
                    if (x.getId().equals(nn.getId())) {
                        f = false;

                    }
                }
                if(f) nw.add(nn);

            }
            //Collections.reverse(nw);
            if(nw.size()>0)
            System.out.println("New messages found: "+nw.size());

            final ObservableList < Message > myObservableList = FXCollections.observableList(nw);
            Platform.runLater(()->{
                try {
                    o.getItems().addAll(myObservableList);
                } catch (Exception e) {

                }
            });
        }try {
            TimeUnit.MILLISECONDS.sleep(800);
        }
        catch (Exception e)
        {

        }
        List < String > ids = new ArrayList < >();
        List < Dialog > q = new ArrayList < >();
        String selection1 = "";
        try {
            selection1 = listView.getSelectionModel().getSelectedItem();
        } catch(Exception e) {
            e.printStackTrace();
        }
        final String sel = selection1;
        if (nwq != null) for (Dialog c: nwq) if (c.getMessage().getUserId() > 0 || !c.getMessage().getTitle().equals("")) q.add(c);

        final List < Dialog > nw = new ArrayList < >(q);
        List < String > lst = new ArrayList < >(listView.getItems());

        int i;
        for (Dialog c: nw) {
            if (c.getMessage().getTitle().equals("") && c.getMessage().getUserId() > 0) ids.add(c.getMessage().getUserId() + "");

            i = 0;
            for (Dialog x: allDialog) {
                if ((c.getMessage().getTitle().equals(x.getMessage().getTitle()) && !c.getMessage().getTitle().equals("")) || c.getMessage().getUserId().equals(x.getMessage().getUserId())) {
                    allDialog.remove(x);
                    int j;
                    j = i;
                    lst.remove(j);
                    break;
                }
                i++;
            }
        }
        i = 0;
        List < String > titles = Profile.getFullNames(ids);
        int j = 0;
        for (Dialog c: nw) {
            if (c.getMessage().getTitle().equals("")) {
                String title = "";
                if (titles != null && titles.size() > i) title = titles.get(i);
                lst.add(j, title);
                j++;
                i++;
            } else {
                String title = c.getMessage().getTitle();
                lst.add(j, title);
                j++;

            }
        }
        nw.addAll(allDialog);
        allDialog = nw;
        final List < String > x = new ArrayList < >(lst);
        System.gc();
        Task task = new Task < Void > () {@Override public Void call() {
            updateCompleted=false;
            updateCompleted = false;
            Calendar cq = Calendar.getInstance();
            long xx = cq.getTimeInMillis();
            try {
                listView.getItems().setAll(x);
            } catch(Exception e) {
                System.out.println("Some problems with threads, FX");
                return null;
            }
            if(!isSelReq) {
                listView.getSelectionModel().select(sel);

            }
            else {
                System.out.println("Selection request accepted.");
                reselect=true;lastSel=selReq;
                System.out.println("qqz");
                listView.getSelectionModel().select(selReq);
                System.out.println(lastSel);
                dialogListClicked(null);
            }
            isSelReq=false;
            System.out.println("It take: " + (Calendar.getInstance().getTimeInMillis() - xx) + "ms.");

            updateCompleted = true;
            return null;
        }
        };
        new Thread(task).start();

    }
    private void loadPage(int userId, boolean updateImages) {
        initFriendsList(userId);
        currentPageUserId=userId;
        if(updateImages)
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
            List < PhotoXtrRealOffset > wallPhotos = apiWrapper.apiClient.photos().getAll(apiWrapper.currentUser).ownerId(userId).execute().getItems();
            int i = 1;
            for (PhotoXtrRealOffset p: wallPhotos) {
                System.out.println("Loading photo № " + i);
                Image img = new Image(p.getPhoto130(), 130, 130, true, true);
                System.out.println("Ok, loaded, checking sizes...");
                System.out.flush();
                if ((img.getWidth() >= 130) && (img.getHeight() < 130)) {
                    double scale = 130 / img.getHeight();
                    //img.
                    img = new Image(p.getPhoto604(), img.getWidth() * scale, 130, true, true);
                } else {
                    if ((img.getWidth() < 130) && (img.getHeight() >= 130)) {
                        double scale = 130 / img.getWidth();
                        img = new Image(p.getPhoto604(), 130, img.getHeight() * scale, true, true);

                    }
                }
                System.out.println("Photo: " + i + " successfully loaded!");
                System.out.flush();
                Image x = img;
                final ImageView imageView = (ImageView) scene.lookup("#wallPhoto" + i);
                Task task = new Task < Void > () {@Override
                public Void call() {
                    imageView.setImage(x);
                    imageView.setCache(true);
                    Rectangle2D rectangle2D = new Rectangle2D((x.getWidth() - 130) / 2, 0, 130, 130);
                    imageView.setViewport(rectangle2D);
                    return null;
                }
                };
                new Thread(task).start();
                i++;
                //System.out.println(i);
                if (i == 7) break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(()->{
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
            ((Button)scene.lookup("#backToMe")).setVisible(false);
//            ((Button)scene.lookup("#reload")).setVisible(true);
            ((Button)scene.lookup("#friendsButton")).setVisible(false);
            ((Button)scene.lookup("#sendMessage")).setVisible(false);
            ((Button)scene.lookup("#deleteFromFriends")).setVisible(false);

            TimeUnit.MILLISECONDS.sleep(1000);
            Label label = (Label) scene.lookup("#LblStat");
            UserXtrCounters cur=apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(userId+"").fields(UserField.INTERESTS,UserField.ONLINE, UserField.LAST_SEEN,UserField.CITY,UserField.IS_FRIEND,UserField.CAN_WRITE_PRIVATE_MESSAGE).execute().get(0);
            //stage.getScene().
            try {
                label.setText("Статус: ".concat(apiWrapper.getStatus(userId)));
            } catch(Exception e) {
                e.printStackTrace();
            }
            System.out.println("Status ok.");
            if(userId!=apiWrapper.currentUser.getId()) {
                ((Button) scene.lookup("#backToMe")).setVisible(true);
                if (!cur.isFriend())
                    ((Button) scene.lookup("#friendsButton")).setVisible(true);
                else
                    ((Button) scene.lookup("#deleteFromFriends")).setVisible(true);
            }
            if(cur.canWritePrivateMessage())
                ((Button)scene.lookup("#sendMessage")).setVisible(true);
            System.out.println("Friend status parsed");
            System.out.flush();

            ((Label) scene.lookup("#PageName")).setText(Profile.getFullName(userId + ""));
            System.out.println("Pagename ok.");
            System.out.flush();
            TimeUnit.MILLISECONDS.sleep(1000);
            String url = Profile.getPhoto400(userId);
            if (url!=null) {
                ((ImageView) scene.lookup("#Ava")).setImage(new Image(url));
            } else {
                ((ImageView) scene.lookup("#Ava")).setImage(new Image("https://vk.com/images/camera_400.png",true));
            }
            System.out.println("AVA ok.");
            ((Label) scene.lookup("#LblBornDate")).setText("Дата рождения: " + Profile.getBirthDate(userId));
            System.out.println("Birth Date ok.");
            UserXtrCounters user = null;
            ((Label)scene.lookup("#lblHobbies")).setText("Увлечения: " + cur.getInterests());
            System.out.println("Interests ok.");
            if (cur.getCity()!=null) {
                ((Label) scene.lookup("#lblCity")).setText("Город: " + cur.getCity().getTitle());
            } else {
                ((Label) scene.lookup("#lblCity")).setText("Город: не указан");
            }
            System.out.println("City ok.");
            System.out.flush();
            try {
                user = cur;
                //UsersGetQuery x=apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(userId + "").fields(UserField.ONLINE, UserField.LAST_SEEN);
            } catch(Exception e) {
                e.printStackTrace();
            }
            String dateFormatted = "n/a ;)";
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                //@NotNull
                if (user != null) {
                    Date date = new Date((long) user.getLastSeen().getTime() * 1000);
                    DateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy"); //:ss
                    dateFormatted = formatter.format(date);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            if (user != null && user.isOnline())((Label) scene.lookup("#onlineLabel")).setText("Online");
            else if (user != null && user.isOnlineMobile())((Label) scene.lookup("#onlineLabel")).setText("Online(Mobile)");
            else((Label) scene.lookup("#onlineLabel")).setText("Последний раз в сети в " + dateFormatted);
            try {
                userR = new Profile(userId);
                userR.getFriends();
                ((Button) scene.lookup("#btnFriends")).setText("Друзья: " + userR.friends.size());
            } catch(Exception e) {
                e.printStackTrace();
            }
            System.out.println("Online ok.");
            System.out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
		});
        System.out.println("Page started.");
    }
    private Profile userR = null;
    private int lastSel = -1;public boolean reselect=false;
    public void dialogListClicked(MouseEvent mouseEvent) {
        ListView listView = (ListView) scene.lookup("#dialogsList");
        if(!updateCompleted)
        {
            if (!(listView.getSelectionModel().getSelectedIndex() == lastSel&&!reselect)) {
                isSelReq = true;
                selReq=listView.getSelectionModel().getSelectedIndex();
                System.out.println("Selection request: " + selReq);
            }
            return;
        }
        double qx = 0;
        while (!updateCompleted) {
            qx = Math.pow(10, 10);
        }
        try{

            TimeUnit.MILLISECONDS.sleep(200);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        lastSel = (int)(lastSel + qx * Math.pow(1, 1)  - qx);
        if (listView.getSelectionModel().getSelectedIndex() == lastSel&&!reselect) return;
        int sel=0;
        if(reselect)
        {
            sel=lastSel;
        } else if(listView.getSelectionModel().getSelectedIndex()!=-1){
            lastSel = listView.getSelectionModel().getSelectedIndex();
            sel=lastSel;
        } else return;
        Dialog dialog = allDialog.get(sel);
        Integer userId = dialog.getMessage().getUserId();
        Integer chatId = dialog.getMessage().getChatId();
        int peerId = 0;
        System.out.println("Set selection: " + sel);
        if (chatId == null) { //simple dialog
            try {
                UserXtrCounters user = apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(String.valueOf(dialog.getMessage().getUserId())).fields(UserField.PHOTO_50).execute().get(0);
                ((Label) scene.lookup("#chatName")).setText(user.getFirstName().concat(" ").concat(user.getLastName()));
                ((ImageView) scene.lookup("#chatAva")).setImage(new Image(user.getPhoto50()));
                peerId = userId;
                avas = new ArrayList<>();
                avas.add(Profile.getAva(apiWrapper.currentUser.getId()));
                avas.add(Profile.getAva(peerId));
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else { //group chat
            peerId = chatId + 2000000000;
            ((Label) scene.lookup("#chatName")).setText(dialog.getMessage().getTitle());
            if (dialog.getMessage().getPhoto100() != null) { ((ImageView) scene.lookup("#chatAva")).setImage(new Image(dialog.getMessage().getPhoto100()));
            } else { ((ImageView) scene.lookup("#chatAva")).setImage(new Image("https://vk.com/images/camera_50.png",true));
            }
            avas = Profile.getChatAvas(chatId);
        }
        reselect=false;
        initMessagesList(userId, peerId, 0);
    }
    private int curPeerId=0,curUserId=0,curOffset=0,currentPageUserId=0;
    private boolean hasNext=true,initCompleted=true;
    public  void deleteFromFriendClick(ActionEvent actionEvent)
    {
        try{
        apiWrapper.apiClient.friends().delete(apiWrapper.currentUser,currentPageUserId).execute();
        loadPage(currentPageUserId,false);}
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public  void sendMessageClicked(ActionEvent actionEvent)
    {
        try{
//            while(!updateCompleted);
       //     updateCompleted=false;
            if(!updateCompleted)return;
            if(!loadEnded)return;

            ((TabPane)scene.lookup("#tabs")).getSelectionModel().select(2);
            List<String> q=((ListView) scene.lookup("#dialogsList")).getItems();
            TimeUnit.MILLISECONDS.sleep(1800);
            System.out.println("qq");
            String un=Profile.getFullName(currentPageUserId+"");
            reselect=true;lastSel=0;

            for(String x:q) {
              // System.out.println(x+" "+un);
                if (x.equals(un)) {
                    un = x;
                    break;
                }
                lastSel++;
            }
            ((ListView) scene.lookup("#dialogsList")).getSelectionModel().select(lastSel);
            if(!updateCompleted)
            {
                isSelReq=true;
                selReq=lastSel+1;
                selReq--;
            }
          //  lastSel=0;
            //if(lastSel==)
            reselect=true;
            dialogListClicked(null);
        //    updateCompleted=true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public  void realoadPageClicked(ActionEvent actionEvent)
    {
        try{
            loadPage(currentPageUserId,true);}
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public  void backToMeClicked(ActionEvent actionEvent)
    {
        try{
            loadPage(apiWrapper.currentUser.getId(),true);}
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public  void addFromFriendClick(ActionEvent actionEvent)
    {
        try{
            apiWrapper.apiClient.friends().add(apiWrapper.currentUser,currentPageUserId).execute();
            loadPage(currentPageUserId,false);}
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    List<Ava> avas = null;

    private void initMessagesList(int userId, int peerId ,int offset) {
        try {
            initCompleted=false;
            System.out.println("Loading messages, offset: " + offset);
            ListView <Message> messagesList = (ListView < Message > ) scene.lookup("#messagesListView");
            if(curUserId!=userId) {
                messagesList.getItems().clear();
                curOffset=0;
            }
            TimeUnit.MILLISECONDS.sleep(600);
            List<Message> messages = Profile.getMessages(userId, peerId, offset,200);System.out.println(messages.size()+"");
            if(messages.size()<200)hasNext=false;
            if(messages.size()==0)return;
            ObservableList < Message > myObservableList = FXCollections.observableList(messages);
            curPeerId=peerId;
            curUserId=userId;
            try {
                messagesList.getItems().addAll(0, myObservableList);
            }
            catch (Exception e)
            {

            }
            for (Node node: messagesList.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar) {
                    final ScrollBar bar = (ScrollBar) node;
                    bar.valueProperty().addListener(new ChangeListener<Number>() {
                        @Override public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
                            if((double)newValue<0.1&&initCompleted)
                            {
                                initCompleted=false;
                                initMessagesList(curUserId,curPeerId,curOffset);
                            }
                        }
                    });
                }
            }
        curOffset+=messages.size();

            messagesList.setCellFactory(new Callback < ListView < Message > , ListCell < Message >> () {

                @Override
                public ListCell < Message > call(ListView < Message > p) {

                    return new ListCell < Message > () {

                        @Override
                        protected void updateItem(Message t, boolean bln) {
                            super.updateItem(t, bln);
                            if (t != null) {
                                //setText(t.getText() + ":" + t.getName());
                                MessageData messageData = new MessageData();
                                Ava ava = avas.stream().filter(o -> o.getId().equals(t.getFromId())).findFirst().get();
                                messageData.setInfo(ava.getName(),t.getBody(),ava.getImg());
                                setGraphic(messageData.getBox());
                            }
                        }

                    };
                }
            });if(offset==0)
           Platform.runLater(()->{ messagesList.scrollTo(messagesList.getItems().size()-1);});
            else messagesList.scrollTo((int)Math.round(messagesList.getItems().size()-1*0.2));

        } catch(Exception e) {
            e.printStackTrace();

        }
        initCompleted=true;
    }

    public void initFriendsList(int id) {
        try {
            List<Ava> friends = apiWrapper.getFriendsA(id);
            ((TabPane)(scene.lookup("#tabs"))).getSelectionModel().select(0);
            ListView<Ava> list = (ListView<Ava>) scene.lookup("#friendsList");
            ObservableList<Ava> avaObservableList = FXCollections.observableList(friends);
            list.setItems(avaObservableList);
            list.setCellFactory(new Callback<ListView<Ava>, ListCell<Ava>>() {
                @Override
                public ListCell<Ava> call(ListView<Ava> p) {
                    return new ListCell < Ava > () {

                        @Override
                        protected void updateItem(Ava t, boolean bln) {
                            super.updateItem(t, bln);
                            if (t != null) {
                                //setText(t.getText() + ":" + t.getName());
                                FriendData friendData = new FriendData();
                                friendData.setInfo(t.getName(),t.getImg());
                                setGraphic(friendData.getBox());
                            }
                        }

                    };
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    VirtualFlow < Post,
            ?>postVirtualFlow = null;
    ScrollBar postsScrollBar = null;
    private void postsNew() {
        List < Post > posts = Profile.getPosts(40);
        if (posts == null) {
            posts = new ArrayList < Post > ();
        }

        ObservableList < Post > myObservableList = FXCollections.observableList(posts);

        postVirtualFlow = VirtualFlow.createVertical(
                myObservableList, post ->postCell(post));

        postVirtualFlow.setPrefSize(1256, 698);
        postVirtualFlow.setLayoutX(14);
        postVirtualFlow.setLayoutY(14);
        AnchorPane.setBottomAnchor(postVirtualFlow, 14.0);
        AnchorPane.setTopAnchor(postVirtualFlow, 14.0);
        AnchorPane.setLeftAnchor(postVirtualFlow, 14.0);
        AnchorPane.setRightAnchor(postVirtualFlow, 14.0);

        AnchorPane anchorPane = (AnchorPane) scene.lookup("#postsAnchor");
        postsScrollBar = new ScrollBar();
        postsScrollBar.setMin(0);
        postsScrollBar.setMax(500);
        postsScrollBar.setLayoutX(1300);
        postsScrollBar.setLayoutY(14);
        postsScrollBar.setOrientation(Orientation.VERTICAL);
        AnchorPane.setBottomAnchor(postsScrollBar,0.0);
        AnchorPane.setTopAnchor(postsScrollBar,0.0);
        AnchorPane.setRightAnchor(postsScrollBar,2.0);

        postVirtualFlow.setOnScroll(event -> onScrollFlowPosts(event));


        postsScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (postVirtualFlow!=null) {
                    if (!scolledPosts)
                        postVirtualFlow.scrollYToPixel((postVirtualFlow.getTotalHeightEstimate()-postVirtualFlow.getHeight())*newValue.doubleValue()/500);
                    scolledPosts = false;
                }
            }
        });

        anchorPane.getChildren().addAll(postVirtualFlow,postsScrollBar);

    }
    boolean scolledPosts = false;

    public void onScrollFlowPosts(ScrollEvent event) {
        if (postsScrollBar!=null) {
            postsScrollBar.setValue(postVirtualFlow.getEstimatedScrollY()*500/(postVirtualFlow.getTotalHeightEstimate()-postVirtualFlow.getHeight()));
            scolledPosts = true;
        }
    }

    private Cell < Post,
            VBox > postCell(Post post) {
        PostData postData = new PostData();
        postData.setInfo(post.getName(), post.getText(), post.getAva(), post.getDate(), post.getImage());
        return Cell.wrapNode(postData.getBox());
    }

    public void goSearchFriends(ActionEvent actionEvent) {
        TextField text = (TextField) scene.lookup("#searchQ");
        initSearchList(text.getText());
    }

    public void userSelectedEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ListView) {
            ListView<Ava> scrollBar = (ListView<Ava>)mouseEvent.getSource();
            //if (scrollBar.getId().equals("friendsList")) {
                loadPage(scrollBar.getSelectionModel().getSelectedItems().get(0).getId(),true);
            //}
        }
    }

    public void addNew(ActionEvent actionEvent) {
        TextArea textArea = (TextArea) scene.lookup("#TANewZap");
        String text = textArea.getText();
        textArea.setText("");
        try {
            apiWrapper.apiClient.wall().post(apiWrapper.currentUser).message(text).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class messageUpdater implements Runnable {
        public void run() {
            try {

                System.out.println("Loading page...");
                loadPage(apiWrapper.currentUser.getId(),true);
                while (!loadEnded) {
                    dialogsNext();
                    TimeUnit.MILLISECONDS.sleep(900);
                }
                System.out.println("Ok, messages loaded!");
                while (true) {
                    updateDialogs();
                    if(isSelReq)
                    {
                        reselect=true;
                        lastSel=selReq;
                        Platform.runLater(()-> {
                            dialogListClicked(null);
                        });
                    }
                    TimeUnit.MILLISECONDS.sleep(5000);
                    if (Math.pow(1, 1) < 0) break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fuckKeys(KeyEvent keyEvent) {
        keyEvent.consume();
    }

    public void initSearchList(String username) {
        try {
            List<Ava> users = apiWrapper.getSearch(username,10);
            ListView<Ava> list = (ListView<Ava>) scene.lookup("#searchList");
            ObservableList<Ava> avaObservableList = FXCollections.observableList(users);
            list.setItems(avaObservableList);
            list.setCellFactory(new Callback<ListView<Ava>, ListCell<Ava>>() {
                @Override
                public ListCell<Ava> call(ListView<Ava> p) {
                    return new ListCell < Ava > () {

                        @Override
                        protected void updateItem(Ava t, boolean bln) {
                            super.updateItem(t, bln);
                            if (t != null) {
                                //setText(t.getText() + ":" + t.getName());
                                FriendData friendData = new FriendData();
                                friendData.setInfo(t.getName(),t.getImg());
                                setGraphic(friendData.getBox());
                            }
                        }

                    };
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}