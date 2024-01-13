/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecte1_xat;

/**
 *
 * @author Houssam
 */
public class Fitxer {

    public String[] accesType = {"private", "protected", "public"};

    private int id_file;
    private String name;
    private String path;
    private String acces;
    private int id_user;
    private int id_group;

    public Fitxer(String name, String path, int id_user) {
        this.name = name;
        this.path = path;
        this.id_user = id_user;
    }

    public Fitxer(String name, String path, int id_user, int id_group) {
        this.name = name;
        this.path = path;
        this.id_user = id_user;
        this.id_group = id_group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAcces() {
        return acces;
    }

    public void setAcces(String acces) {
        this.acces = acces;
    }

    public int getId_group() {
        return id_group;
    }

    public void setId_group(int id_group) {
        this.id_group = id_group;
    }

    public int getId_file() {
        return id_file;
    }

    public int getId_user() {
        return id_user;
    }

}
