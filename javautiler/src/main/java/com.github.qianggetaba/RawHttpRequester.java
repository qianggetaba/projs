package com.github.qianggetaba;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 显示原始http信息，http头，boundary等等，便于在测试与服务器访问同一接口结果不同时排查
 *
 * http头信息不一致，utf8或gbk编码不一致，urlencode,content-type不一致等等
 */
public class RawHttpRequester {

    public static void start(int port) throws Exception{
        ServerSocket serverSocket=new ServerSocket(port,1);

        while (true){
            System.out.println("ready to conn:"+port);
            Socket client = serverSocket.accept();
            System.out.println("----get one conn");

            byte[] buf = new byte[10240];
            int len    = -1;
            InputStream is = client.getInputStream();

            // 确定http长度是复杂的，这里简单处理
            len=is.read(buf);
            for (int i = 0; i < len; i++) {
                System.out.print((char)buf[i]);
            }

            is.close();
            client.close();
            System.out.println("----finish");
        }
    }

    public static void main(String[] args) throws Exception {
        RawHttpRequester.start(8080);
    }
}
