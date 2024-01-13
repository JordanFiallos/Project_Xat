package projecte1_xat;

/**
 *
 * @author Houssam
 */
public class GroupMember extends User {

    private int id_group;
    private boolean isAdmin = false;

    public GroupMember(int id_group, User u) {
        super(u.user, u.password);
        this.id_group = id_group;
    }

    public int getId_group() {
        return id_group;
    }

    public void setId_group(int id_group) {
        this.id_group = id_group;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public int getId_user() {
        return id_user;
    }

}
