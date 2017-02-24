package com.liuruichao.app.dto;

import java.io.Serializable;

/**
 * Result
 *
 * @author liuruichao
 * @date 15/11/2 下午4:12
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private int status;
    private int sender; // 发送者
    private String senderName;
    private int receiver; // 接受者
    private String receiverName;

    public Result() {
    }

    public Result(int status) {
        this.status = status;
    }

    public Result(int status, int sender, int receiver, String senderName, String receiverName) {
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
