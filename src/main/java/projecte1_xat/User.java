/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecte1_xat;

/**
 *
 * @author Houssam
 */
public class User {

    protected int id_user;
    protected String user;
    protected String password;
    protected boolean isOnline;

    public User() {
    }

    public User(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public int setId_user() {
        return id_user;
    }

    public int getId_user() {
        return id_user;
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

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

}
