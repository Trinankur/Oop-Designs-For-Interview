import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class User {
    private String userName;
    private List<Group> groups;
    private ChatRoom chatRoom;

    public User(String userName, ChatRoom chatRoom) {
        this.groups = new ArrayList<>();
        this.userName = userName;
        this.chatRoom = chatRoom;
    }

    public String getUserName() {
        return userName;
    }

    public void sendMessageToUser(String message, User user) {
        chatRoom.sendMessageToUser(message, this, user);
    }

    public void sendMessageToGroup(String message, Group group) {
        chatRoom.sendMessageToGroup(message, this, group);
    }

    public void sendMediaToUser(Media media, User user) {
        chatRoom.sendMediaToUser(media, this, user);
    }

    public void sendMediaToGroup(Media media, Group group) {
        chatRoom.sendMediaToGroup(media, this, group);
    }

    public Group createGroup(String groupName) {

        Group group = new Group(groupName, chatRoom);
        return chatRoom.createGroup(group, this);

    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void addUserToGroup(User user, Group group) {
        for(Group g : groups) {
            if(g == group) {
                chatRoom.addUserToGroup(user, group);
                break;
            }
        }
    }

    public void makeUserAsAdmin(Group group, User user) {
        for(Group g : groups) {
            if(g == group) {
                if(group.getAdmins().contains(this)) {
                    g.makeAdmin(user);
                }
            }
        }
    }

    public void receiveNotification(String msg) {
        System.out.println("(" + this.userName + ") " + msg);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                '}';
    }
}

class Group {

    private String groupName;
    private Set<User> admins;
    private List<User> users;
    private List<Media> mediaList;
    private ChatRoom chatRoom;

    public Group(String groupName, ChatRoom chatRoom) {
        this.users = new ArrayList<>();
        this.groupName = groupName;
        this.chatRoom = chatRoom;
    }

    public String getGroupName() {
        return groupName;
    }

    public Set<User> getAdmins() {
        return admins;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void makeAdmin(User user) {

    }

    public void receiveNotification(User theUser, String msg) {
        for(User user : users) {
            if(user == theUser)
                continue;
            user.receiveNotification(msg);
        }
    }

}

enum MediaType {
    PHOTO, VIDEO, DOCUMENT;
}

class Media {
    private MediaType mediaType;
    private User sentBy;

    public Media(MediaType mediaType, User sentBy) {
        this.mediaType = mediaType;
        this.sentBy = sentBy;
    }

    @Override
    public String toString() {
        return "Media{" +
                "mediaType=" + mediaType +
                ", sentBy=" + sentBy +
                '}';
    }
}

interface ChatRoom {
    void sendMessageToUser(String message, User sender, User receiver);
    void sendMessageToGroup(String message, User sender, Group receiver);
    void sendMediaToUser(Media media, User sender, User receiver);
    void sendMediaToGroup(Media media, User sender, Group receiver);
    Group createGroup(Group group, User user);
    void addUserToGroup(User user, Group group);
}

class ChatRoomImpl implements ChatRoom {


    private void sendNotificationToUser(User user, String msg) {
        user.receiveNotification(msg);
    }

    private void sendNotificationToGroup(Group group, User sender, String msg) {
        group.receiveNotification(sender, msg);
    }

    @Override
    public void sendMessageToUser(String message, User sender, User receiver) {
        String msg = sender.getUserName() + " sent to " + receiver.getUserName() + " : [" + message + "]";
        sendNotificationToUser(receiver, msg);
    }

    @Override
    public void sendMessageToGroup(String message, User sender, Group receiver) {
        String msg = sender.getUserName() + " sent to group " + receiver.getGroupName() + " : [" + message + "]";
        sendNotificationToGroup(receiver, sender,  msg);
    }

    @Override
    public void sendMediaToUser(Media media, User sender, User receiver) {
        String msg = sender.getUserName() + " sent to group " + receiver.getUserName() + " : [" + media + "]";
        sendNotificationToUser(receiver, msg);
    }

    @Override
    public void sendMediaToGroup(Media media, User sender, Group receiver) {
        String msg = sender.getUserName() + " sent to group " + receiver.getGroupName() + " : [" + media + "]";
        sendNotificationToGroup(receiver, sender, msg);
    }

    @Override
    public Group createGroup(Group group, User user) {
        group.addUser(user);
        user.getGroups().add(group);
        return group;
    }

    @Override
    public void addUserToGroup(User user, Group group) {
        group.addUser(user);
        user.addGroup(group);
    }

}

public class ChatApplicationDemo {

    public static void main(String[] args) {
        ChatRoom chatRoom = new ChatRoomImpl();
        User user1 = new User("Rehan", chatRoom);
        User user2 = new User("Puchku", chatRoom);

        Group group = user1.createGroup("Family");

        User user3 = new User("Baba", chatRoom);
        User user4 = new User("Mummum", chatRoom);

        user1.addUserToGroup(user2, group);
        user1.addUserToGroup(user3, group);
        user1.addUserToGroup(user4, group);


        user1.sendMessageToUser("Hello", user2);
        user2.sendMessageToUser("Hi", user1);

        user1.sendMessageToGroup("Hello Everyone!", group);
        user2.sendMessageToGroup("Hi!", group);

        Media media = new Media(MediaType.PHOTO, user1);

    }

}
