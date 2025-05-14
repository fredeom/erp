package main;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;

import main.pages.MenuPermission;
import main.records.UserGroup;
import main.records.UserShort;
import main.records.User;
import main.records.Notification;

class ResponseLoginJson {
    public String message;
    public String token;
    public String name;
    public String group;
    public String toString() {
        return "Message: " + message + " Token: " + token + " Name: " + name + " Group: " + group;
    }
}

class ResponseMenuJson {
    public String message;
    public List<MenuItem> menus;
    public String toString() {
        StringBuffer sb = new StringBuffer();
        menus.forEach((menu) -> {
            sb.append(menu);
        });
        return "Message: " + message + " Menus: " + sb.toString();
    }
}

class ResponseMenuPermissionJson {
    public String message;
    public List<MenuPermission> menuPermissions;
    public String toString() {
        StringBuffer sb = new StringBuffer();
        menuPermissions.forEach((menuPermission) -> {
            sb.append(menuPermission);
        });
        return "Message: " + message + " MenuPermissions: " + sb.toString();
    }
}

class ResponseMenuPermissionSingleJson {
    public String message;
    public MenuPermission menuPermission;
    public String toString() {
        return "Message: " + message + " MenuPermission: " + menuPermission;
    }
}

class ResponseUserGroupJson {
    public String message;
    public List<UserGroup> user_groups;
    public String toString() {
        StringBuffer sb = new StringBuffer();
        user_groups.forEach((user_group) -> {
            sb.append(user_group);
        });
        return "Message: " + message + " UserGroups: " + sb.toString();
    }
}

class ResponseUserShortJson {
    public String message;
    public List<UserShort> user_shorts;
    public String toString() {
        StringBuffer sb = new StringBuffer();
        user_shorts.forEach((user_short) -> {
            sb.append(user_short);
        });
        return "Message: " + message + " UserShorts: " + sb.toString();
    }
}

class ResponseUserDetailsJson {
    public String message;
    public User user;
    public String toString() {
        return "Message: " + message + " User: " + user;
    }
}

class ResponseNotificationCountJson {
    public int count;
    public String toString() {
        return "NotificationCount: " + count;
    }
}

class ResponseNotificationListJson {
    public List<Notification> notifications;
    public String toString() {
        StringBuffer sb = new StringBuffer();
        notifications.forEach((notification) -> {
            sb.append(notification);
        });
        return "Notifications: " + sb.toString();
    }
}

/**
 * ServerService class responsible for communication with server.
 *
 * @author Alexander Smirnov
 * @version 2025-07-01
 */
public class ServerService
{
    private static ServerService service;
    
    private HttpClient client = HttpClient.newHttpClient();
    private String host;
    private String token;
    private List<MenuItem> menus;
    
    public Map<String, String> env;
    
    
    private ServerService() throws Exception {
        env = loadEnvironment();
        host = env.get("ERP_SERVER_URI");
        if (host == null || host.trim().isEmpty()) {
            throw new Exception("ERP server uri is not set in .env (ERP_SERVER_URI)");
        }
    }
    
    public static ServerService getInstance() throws Exception {
        if (service == null) {
            service = new ServerService();
        }
        return service;
    }
    
    public static Map<String, String> loadEnvironment() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(".env"));
            try {
                String line = br.readLine();
                while (line != null) {
                    if (!line.equals("")) {
                        String[] words = line.split("=");
                        map.put(words[0], String.join("=", Arrays.copyOfRange(words, 1, words.length)));
                    }
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        } catch (java.io.FileNotFoundException fnfException) {
            System.out.println("Error: Can't find .env");
        } catch (java.io.IOException ioException) {
            System.out.println("Error: " + ioException.getMessage());
        }
        return map;
    }
    
    public String[] login(String login, String pass) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("login", login);
        data.put("pass", pass);
        
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(buildFormDataFromMap(data))
                                .uri(URI.create(host + "/login"))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseLoginJson responseJson = gson.fromJson(json, ResponseLoginJson.class);
        token = responseJson.token;
        if (token == null) {
            throw new Exception(responseJson.message);
        }
        return new String[]{responseJson.name, responseJson.group};
    }
    
    public void logout() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(""))
                                .uri(URI.create(host + "/logout"))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        token = null;
    }
    
    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
    
    public List<MenuItem> getMenu() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/menu"))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseMenuJson responseJson = gson.fromJson(json, ResponseMenuJson.class);
        menus = responseJson.menus;
        if (menus == null) {
            throw new Exception(responseJson.message);
        }
        return menus;
    }
    
    public List<MenuItem> getMenuFull() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/menu/full"))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseMenuJson responseJson = gson.fromJson(json, ResponseMenuJson.class);
        menus = responseJson.menus;
        if (menus == null) {
            throw new Exception(responseJson.message);
        }
        return menus;
    }
    
    public void addMenu(int parent_id, String name, String pane_class, int active) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("parent_id", parent_id);
        data.put("name", name);
        data.put("pane_class", pane_class);
        data.put("active", active);
        Gson gson = new Gson();
        String jsonInput = gson.toJson(data); 
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/menu/add"))
                                .header("Content-Type", "application/json") // x-www-form-urlencoded
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }

    public void editMenu(int id, MenuItem parent, String name, String pane_class, int posit, int active) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("parent_id", parent.id);
        data.put("name", name);
        data.put("pane_class", pane_class);
        data.put("posit", posit);
        data.put("active", active);
        Gson gson = new Gson();
        String jsonInput = gson.toJson(data); 
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/menu/edit"))
                                .header("Content-Type", "application/json") // x-www-form-urlencoded
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public void updateMenuItems(List<MenuItem> menuItems) throws Exception {
        List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
        for (MenuItem menuItem : menuItems) {
            Map<Object, Object> data = new HashMap<>();
            data.put("id", menuItem.id);
            data.put("name", menuItem.name);
            data.put("posit", list.size());
            list.add(data);
        }
        Gson gson = new Gson();
        String jsonInput = gson.toJson(list);
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/menu/order"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);

    }
    
    public void deleteMenu(int delete_id) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("id", delete_id);
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(buildFormDataFromMap(data))
                                .uri(URI.create(host + "/api/menu/delete"))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public List<MenuPermission> getMenuPermissions(int menuItemId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/menu/permission/" + menuItemId))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseMenuPermissionJson responseJson = gson.fromJson(json, ResponseMenuPermissionJson.class);
        List<MenuPermission> menuPermissions = responseJson.menuPermissions;
        if (menuPermissions == null) {
            throw new Exception(responseJson.message);
        }
        return menuPermissions;
    }
    
    public List<UserGroup> getUserGroupFull() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/user/group/full"))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseUserGroupJson responseJson = gson.fromJson(json, ResponseUserGroupJson.class);
        List<UserGroup> userGroups = responseJson.user_groups;
        if (userGroups == null) {
            throw new Exception(responseJson.message);
        }
        return userGroups;
    }
    
    public List<UserShort> getUserShortFull() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/user/full"))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        
        ResponseUserShortJson responseJson = gson.fromJson(json, ResponseUserShortJson.class);
        List<UserShort> userShorts = responseJson.user_shorts;
        if (userShorts == null) {
            throw new Exception(responseJson.message);
        }
        return userShorts;
    }
    
    public List<UserGroup> getUserGroupsForPermission(String permissionName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/user/group/permission/" + permissionName))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseUserGroupJson responseJson = gson.fromJson(json, ResponseUserGroupJson.class);
        List<UserGroup> userGroup = responseJson.user_groups;
        if (userGroup == null) {
            throw new Exception(responseJson.message);
        }
        return userGroup;
    }
    
    public void deleteUserGroupForPermission(String permissionName, int userGroupId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(""))
                                .uri(URI.create(host + "/api/user/group/permission/delete/" + permissionName + "/" + userGroupId))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public void addUserGroupForPermission(String permissionName, int userGroupId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(""))
                                .uri(URI.create(host + "/api/user/group/permission/add/" + permissionName + "/" + userGroupId))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public void deletePermission(String permissionName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(""))
                                .uri(URI.create(host + "/api/permission/delete/" + permissionName))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public MenuPermission addMenuPermission(String permissionName, int menu_id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(""))
                                .uri(URI.create(host + "/api/permission/add/" + permissionName + "/" + menu_id))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseMenuPermissionSingleJson responseJson = gson.fromJson(json, ResponseMenuPermissionSingleJson.class);
        MenuPermission menuPermission = responseJson.menuPermission;
        if (menuPermission == null) {
            throw new Exception(responseJson.message);
        }
        return menuPermission;
    }
    
    public void applyGroupPermissionByAffected(List<String> permissionGroups) throws Exception {
        Gson gson = new Gson();
        String jsonInput = gson.toJson(permissionGroups);
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/permission/apply"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public void addUser(String lgn, String pass, String name, String phone, int userGroupId) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("login", lgn);
        data.put("pass", pass);
        data.put("name", name);
        data.put("phone", phone);
        data.put("user_group_id", userGroupId);
        Gson gson = new Gson();
        String jsonInput = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/user/add"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }

    public void addUserGroup(String name) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("name", name);
        Gson gson = new Gson();
        String jsonInput = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/user/group/add"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }

    public void editUser(int id, String lgn, String pass, String name, String phone, int userGroupId) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("login", lgn);
        data.put("pass", pass);
        data.put("name", name);
        data.put("phone", phone);
        data.put("user_group_id", userGroupId);
        Gson gson = new Gson();
        String jsonInput = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/user/edit"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public void deleteUser(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(""))
                                .uri(URI.create(host + "/api/user/delete/" + id))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
    
    public User getUserWithDetails(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/user/details/" + id))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseUserDetailsJson responseJson = gson.fromJson(json, ResponseUserDetailsJson.class);
        User user = responseJson.user;
        if (user == null) {
            throw new Exception(responseJson.message);
        }
        return user;
    }
    
    public void sendNotification(String text) throws Exception {
        Map<Object, Object> data = new HashMap<>();
        data.put("text", text);
        Gson gson = new Gson();
        String jsonInput = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                                .uri(URI.create(host + "/api/notification"))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);        
    }
    
    public int getNotificationCount() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/notification/count"))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseNotificationCountJson responseJson = gson.fromJson(json, ResponseNotificationCountJson.class);
        return responseJson.count;
    }
    
    public List<Notification> getNotifications() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(host + "/api/notification"))
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Gson gson = new Gson();
        ResponseNotificationListJson responseJson = gson.fromJson(json, ResponseNotificationListJson.class);
        return responseJson.notifications;
    }
    
    public void notificationSeen(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(""))
                                .uri(URI.create(host + "/api/notification/seen/" + id))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        throw new Exception(json);
    }
}
