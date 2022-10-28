package com.xiaolu.service;

import com.xiaolu.qqcommon.Message;
import com.xiaolu.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * @author 林小鹿
 * @version 1.0
 * 该类/对象，提供和消息相关的服务方法
 */
public class MessageClientService {

    /**
     *
     * @param content 内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content, String senderId) {
        Message message = new Message();
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());//发生事件设置到message对象
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        System.out.println(senderId + "对大家说" + content);

        //发送给服务端
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param content  内容
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sendMessageToOne(String content, String senderId, String getterId) {
        Message message = new Message();
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());//发生事件设置到message对象
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        System.out.println(senderId + "对" + getterId + "说" + content);

        //发送给服务端
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
