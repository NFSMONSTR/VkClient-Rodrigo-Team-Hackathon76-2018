package VkClient;

import com.google.gson.JsonArray;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.UserXtrInvitedBy;
import com.vk.api.sdk.objects.newsfeed.responses.GetResponse;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.users.UserCounters;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.EnumParam;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetFilter;
import com.vk.api.sdk.queries.users.UserField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import VkClient.messagesList.Ava;


import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Profile {
    public String fullName = "", firstName = "", lastName = "";
    public int userId = 1;
    public List<Integer> friends = new ArrayList<Integer>();
    public Profile(int userId)
    {
        this.userId = userId;
    }
    public void getFriends()
    {

        try {

            friends =apiWrapper.apiClient.friends().get(apiWrapper.currentUser).userId(userId).execute().getItems();
            //friends=apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(qe).
        } catch (Exception e)
        {

        }
    }
    public static String getFullName(String id)
    {
        try {
            return apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(id).execute().get(0).getFirstName() + " " + apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(id).execute().get(0).getLastName();
            
        }catch (Exception e)
        {
            return e.getMessage()+" "+id;
        }

    }
    public static String getFullNameImpr(int id) {
        try {
            TimeUnit.MILLISECONDS.sleep(800);
            UserXtrCounters user = apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(String.valueOf(id)).execute().get(0);
            return user.getFirstName().concat(" ").concat(user.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR";
        }
    }

    public static List<String> getFullNames(List<String> id) {
        try {
            TimeUnit.MILLISECONDS.sleep(800);
            List<UserXtrCounters> lst = apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(id).execute();
            List<String> names = new ArrayList<>();
            for (UserXtrCounters u: lst) {
                names.add(u.getFirstName().concat(" ").concat(u.getLastName()));
            }
            return names;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    static String[] months={"января","февраля","марта","апреля","мая","июня","июля","августа","сентября","октября","ноября","декабря"};

    public static String getBirthDate(int id) {
        try {
            TimeUnit.MILLISECONDS.sleep(800);
            String x=apiWrapper.apiClient.users().get(apiWrapper.currentUser).fields(UserField.BDATE).userIds(String.valueOf(id)).execute().get(0).getBdate();
            return x;
            /*if (x.equals("")) return "";
            if(x.indexOf(".")==1)x="0"+x;
            if(x.substring(3).indexOf(".")==1){
                x=(x.charAt(0)+"")+(x.charAt(1)+"")+".0"+ x.substring(3);
            }
            x=(x.charAt(0)+"")+(x.charAt(1)+" ")+months[Integer.parseInt((x.charAt(3)+"")+(x.charAt(4)+""))-1]+" "+x.substring(6) + " г.";
            return x;*/
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR";
        }
    }

    public static String getPhoto400(int id) {
        try {
            UserXtrCounters user = apiWrapper.apiClient.users().get(apiWrapper.currentUser).userIds(String.valueOf(id)).fields(UserField.PHOTO_400_ORIG,UserField.PHOTO_MAX).execute().get(0);
            if (user.getPhoto400Orig()==null) {
                return user.getPhotoMax();
            } else {
                return user.getPhoto400Orig();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR";
        }
    }

    public static List<Dialog> getDialogs(int offset, int count, int preview_length) throws Exception {
        TimeUnit.MILLISECONDS.sleep(400);
        return apiWrapper.apiClient.messages().getDialogs(apiWrapper.currentUser).offset(offset).count(count).previewLength(preview_length).execute().getItems();

    }

    public static List<Post> getPosts(int count) {
        try {
            String request = "https://api.vk.com/method/newsfeed.get?user_id="+String.valueOf(apiWrapper.currentUser.getId())+"&filters=post&count="+String.valueOf(count)+"&v=5.71&access_token="+apiWrapper.currentUser.getAccessToken();
            String response = Jsoup.connect(request).ignoreContentType(true).execute().body();
            JSONObject obj = (JSONObject) new JSONParser().parse(response);

            List<VkGroup> users = new ArrayList<>();
            List<VkGroup> groups = new ArrayList<>();

            JSONArray profJs = (JSONArray) ((JSONObject)obj.get("response")).get("profiles");
            JSONArray groupJs = (JSONArray) ((JSONObject)obj.get("response")).get("groups");

            for (Object o: profJs) {
                if ( o instanceof  JSONObject) {
                    JSONObject prof = (JSONObject) o;
                    long id = (long)prof.get("id");
                    String name = prof.get("first_name").toString().concat(" ").concat(prof.get("last_name").toString());
                    String ava = prof.get("photo_50").toString();
                    users.add(new VkGroup(id,name,ava));
                }
            }

            for (Object o: groupJs) {
                if ( o instanceof  JSONObject) {
                    JSONObject group = (JSONObject) o;
                    long id = (long)group.get("id");
                    String name = group.get("name").toString();
                    String ava = group.get("photo_50").toString();
                    groups.add(new VkGroup(id,name,ava));
                }
            }

            JSONArray items = (JSONArray) ((JSONObject)obj.get("response")).get("items");
            List<Post> posts = new ArrayList<Post>();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");



            for(Object o: items){
                if ( o instanceof JSONObject ) {
                    JSONObject post = ((JSONObject)o);
                    Date date = new Date((long)post.get("date")*1000);
                    VkGroup gr = null;
                    long id = (long)post.get("source_id");
                    if (id<0) {
                        gr = groups.stream().filter(item -> item.id == -id).collect(Collectors.toList()).get(0);
                    } else {
                        gr = users.stream().filter(item -> item.id == id).collect(Collectors.toList()).get(0);
                    }
                    JSONArray attach = (JSONArray) post.get("attachments");
                    String img = "";
                    if (attach!=null) {
                        for (Object a: attach) {
                            if (a instanceof JSONObject) {
                                JSONObject at = (JSONObject) a;
                                if (at.get("type").toString().equals("photo")) {
                                    img = ((JSONObject)at.get("photo")).get("photo_604").toString();
                                    break;
                                }
                            }
                        }
                    }
                    posts.add(new Post(gr.name,post.get("text").toString(),img,gr.ava,dateFormat.format(date)));
                }
            }
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<Message> getMessages(int userId, int peerId, int offset, int count) {
        try {
            //тут можно и не прочитанность получить и количество
            TimeUnit.MILLISECONDS.sleep(700);
            List<Message> messages = apiWrapper.apiClient.messages().getHistory(apiWrapper.currentUser).count(count).offset(offset).userId(userId).peerId(peerId).execute().getItems();
            Collections.reverse(messages);
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Ava> getChatAvas(int chatId) {
        try {
            List<UserXtrInvitedBy> users = apiWrapper.apiClient.messages().getChatUsers(apiWrapper.currentUser,UserField.PHOTO_50).chatId(chatId).execute();
            List<Ava> list = new ArrayList<>();
            for (UserXtrInvitedBy u: users) {
                list.add(new Ava(u.getId(),u.getFirstName().concat(" ").concat(u.getLastName()),u.getPhoto50()));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Ava getAva(int userId) {
        try {
            UserXtrCounters user = apiWrapper.apiClient.users().get(apiWrapper.currentUser).fields(UserField.PHOTO_50).userIds(String.valueOf(userId)).execute().get(0);
            return new Ava(user.getId(),Profile.getFullNameImpr(user.getId()),user.getPhoto50());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
