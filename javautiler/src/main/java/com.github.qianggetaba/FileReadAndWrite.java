package com.github.qianggetaba;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class FileReadAndWrite {

    public static void main(String[] args) throws Exception{

        // 转换流, .write()时把字符流通过OutputStreamWriter设置的编码utf-8转为字节流，存入文件
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("test.txt",true), StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw); // 包装流，提高性能
        bw.write("test \n");
        bw.flush();
        bw.close();

        // 继承OutputStreamWriter，默认为系统编码，注意乱码
        FileWriter fw = new FileWriter("test2.txt",true);
        BufferedWriter bw2 = new BufferedWriter(fw);
        bw2.write("test");
        bw2.newLine();
        bw2.flush();
        bw2.close();


        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("test3.txt",true)) ;
        bos.write("test \n".getBytes());
        bos.flush();
        bos.close();

    }
}
