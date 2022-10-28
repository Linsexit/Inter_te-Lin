package com.xiaolu.service;

import com.xiaolu.qqcommon.Message;
import com.xiaolu.qqcommon.MessageType;

import java.io.*;
import java.util.Date;

/**
 * @author 林小鹿
 * @version 1.0
 * 该类/对象完成 文件传输服务
 */
public class FileClientService {
    /**
     * @param src      源文件
     * @param dest     把该文件传输到对方的哪个目录
     * @param senderId 发送用户id
     * @param getterId 接收7用户id
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId) {

        // 读取src文件  --> message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);
        message.setSendTime(new Date().toString());

        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src文件读入到程序的字节数组
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 提示信息
        System.out.println("\n" + senderId + "给" + getterId + "发送文件：" + src + "到对方的电脑的目录" + dest);

        // 发送
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
