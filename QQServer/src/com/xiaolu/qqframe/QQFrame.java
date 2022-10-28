package com.xiaolu.qqframe;

import com.xiaolu.qqserver.service.QQServer;

/**
 * @author 林小鹿
 * @version 1.0
 * 该类创建QQServer ,启动后台的服务
 */
public class QQFrame {
    public static void main(String[] args) {
        new QQServer();
    }
}
