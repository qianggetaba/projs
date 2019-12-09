package com.github.qianggetaba;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * java代码模拟postman请求接口
 */
public class PostMan {

    /**
     *
     * @param method http请求方式，get，post
     * @param url    http请求地址
     * @param params 如果get，会拼接到querystring，post，会模拟表达提交(拼接为querystring，放到body里提交)
     */
    public static void request(String method, String url, LinkedHashMap<String,String> params){
        try { if ("get".equals(method)){
            get(url,params);
        }else if ("post".equals(method)) {
            post(url, params);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void get(String urlStr, LinkedHashMap<String,String> params) throws Exception{

        String queryStr = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));

        // http get request
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlStr +"?"+queryStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
    }

    public static void post(String urlStr, LinkedHashMap<String,String> params) throws Exception{

        String queryStr = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();

        PrintWriter pw = new PrintWriter(conn.getOutputStream());
        pw.print(queryStr);
        pw.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        pw.close();
        rd.close();
        System.out.println(sb);
    }
}
