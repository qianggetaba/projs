package hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;

public class HdfsTest {

    //列出文件目录
    public static void ls(FileSystem fs, String path){
        FileStatus[] fileStatuses = new FileStatus[0];
        try {
            fileStatuses = fs.listStatus(new Path(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()){
                System.out.println("dir:"+fileStatus.getPath());
            }else {
                System.out.println("file:"+fileStatus.getPath());
            }
        }
    }

    //创建目录
    public static void mkdir(FileSystem fs, String path){
        try {
            fs.mkdirs(new Path(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //写入文件
    public static void writeNewFile(FileSystem fs, String path){

        try {
            FSDataOutputStream hos=fs.create(new Path(path),true) ;
            hos.write("test\n".getBytes());
            hos.flush();
            hos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFile(FileSystem fs, String path){
        try {
            FSDataInputStream his = fs.open(new Path(path));
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = his.read(buff)) != -1) {
            System.out.println(new String(buff, 0, length));
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rm(FileSystem fs, String path){
        try {
            fs.delete(new Path(path),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        //当远程【写文件】报错没有权限时，设置hadoop的用户
        System.setProperty("HADOOP_USER_NAME","duoduo");
        //创建configuration对象
        Configuration conf = new Configuration();
        //配置在etc/hadoop/core-site.xml   fs.defaultFS
        conf.set("fs.defaultFS","hdfs://192.168.1.210:9000");
        //创建FileSystem对象
        //查看hdfs集群服务器/user/passwd.txt的内容
        FileSystem fs = FileSystem.get(conf);

//        ls(fs,"/");
//        writeNewFile(fs,"hdfs://192.168.1.210:9000/mytest.data"); //需要正确的hadoop用户权限
//        readFile(fs,"hdfs://192.168.1.210:9000/mytest.data");
//        mkdir(fs,"/ddd");
        rm(fs,"/ddd");
    }

}
