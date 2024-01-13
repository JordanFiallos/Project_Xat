/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecte1_xat;

/**
 *
 * @author Houssam
 */
public class Message {

    private int id_message;
    private int id_forwarder;
    private String receiver;
    private int id_receiver;
    private String content;

    public Message(int id_forwarder, String receiver, int id_receiver, String content) {
        this.id_forwarder = id_forwarder;
        this.receiver = receiver;
        this.id_receiver = id_receiver;
        this.content = content;
    }

    public int getId_forwarder() {
        return id_forwarder;
    }

    public void setId_forwarder(int id_forwarder) {
        this.id_forwarder = id_forwarder;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getId_receiver() {
        return id_receiver;
    }

    public void setId_receiver(int id_receiver) {
        this.id_receiver = id_receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId_message() {
        return id_message;
    }

}
