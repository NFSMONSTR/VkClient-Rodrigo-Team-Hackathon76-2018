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

import com.vk.api.sdk.client.*;
import com.vk.api.sdk.client.actors.*;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.status.Status;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.queries.users.UserField;
import VkClient.messagesList.Ava;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class apiWrapper {
    private static String permissions = "photos,status,messages,wall,friends";
    public static String url = "https://oauth.vk.com/authorize?client_id=6358323&display=mobile&redirect_uri="
            +"https://oauth.vk.com/blank.html&scope="+permissions+"&response_type=code&v=5.69",
            captchaUrl = "",
            redirectUrl = "https://oauth.vk.com/blank.html";
    static Integer APP_ID = 6358323;
    static String  CLIENT_SECRET = "xSg6beBIT7SFoIoX3RCj",
            REDIRECT_URI = "https://oauth.vk.com/blank.html",
            code = "code";
    public static UserActor currentUser = null;
    public static VkApiClient apiClient = null;
    public static void loginUsingToken(String token, String id)
    {
        currentUser = new UserActor(Integer.parseInt(id), token);
    }
    public static void loginUsingCode(String code)
    {
        UserAuthResponse authResponse;
        try {
            authResponse = apiClient.oauth()
                    .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code)
                    .execute();
        } catch (Exception e) {
            //e.getRedirectUri();
            e.printStackTrace();
            return;
        }

        currentUser = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        try{
            PrintWriter p=new PrintWriter("settings.vk");
            p.println("code="+authResponse.getAccessToken());
            p.println("id="+authResponse.getUserId());
            p.close();
        } catch (Exception e)
        {

        }

    }
    public static void connectToApi() {
        if (apiClient==null) {
            TransportClient transportClient = HttpTransportClient.getInstance();
            apiClient = new VkApiClient(transportClient);
        }
    }
    public static List<Profile> getFriends(int userId) throws Exception
    {
        List<Integer> friendList = apiClient.friends().get(currentUser).userId(userId).execute().getItems();
        List<Profile> profiles = new ArrayList<Profile>();
        for(int friendId : friendList)
        {
            Profile profile = new Profile(friendId);
            profiles.add(profile);
        }
        return profiles;
    }

    public static List<Ava> getFriendsA(int userId)
    {
        try {
            List<UserXtrLists> friendList = apiClient.friends().get(currentUser,UserField.PHOTO_50).userId(userId).execute().getItems();
            List<Ava> profiles = new ArrayList<Ava>();
            for (UserXtrLists friend : friendList) {
                Ava profile = new Ava(friend.getId(),friend.getFirstName().concat(" ").concat(friend.getLastName()),friend.getPhoto50());
                profiles.add(profile);
            }
            return profiles;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Ava> getSearch(String username,int count) {
        try {
            List<UserFull> userLisit = apiClient.users().search(currentUser).fields(UserField.PHOTO_50).q(username).count(count).execute().getItems();
            List<Ava> avas = new ArrayList<>();
            for (UserFull full: userLisit) {
                Ava ava = new Ava(full.getId(),full.getFirstName().concat(" ").concat(full.getLastName()),full.getPhoto50());
                avas.add(ava);
            }
            return avas;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getStatus(int userId) throws Exception {
        Status status = apiClient.status().get(currentUser).userId(userId).execute();
        return status.getText();
    }
}
