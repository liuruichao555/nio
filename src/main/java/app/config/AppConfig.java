package app.config;

import io.netty.handler.logging.LogLevel;

import java.nio.charset.Charset;

/**
 * 程序配置
 * 
 * @author liuruichao
 * Created on 2015-12-22 16:20
 */
public interface AppConfig {
    String SERVER_HOST = "127.0.0.1";
    int SERVER_PORT = 9999;
    int SERVER_BACK_LOG = 1024; // 等待队列长度
    LogLevel SERVER_LOG_LEVEL = LogLevel.INFO;
    int READ_TIME_OUT = 50; // 单位:秒
    Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
}
