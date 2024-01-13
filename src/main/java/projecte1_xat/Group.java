/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecte1_xat;

/**
 *
 * @author Houssam
 */
public class Group {

    private int id_group;
    private int id_admin;
    private String name;

    public Group(String name) {
        this.name = name;
    }

    public Group(int id_group, int id_admin, String name) {
        this.id_group = id_group;
        this.id_admin = id_admin;
        this.name = name;
    }

    public int getId_admin() {
        return id_admin;
    }

    public void setId_admin(int id_admin) {
        this.id_admin = id_admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId_group() {
        return id_group;
    }

}
