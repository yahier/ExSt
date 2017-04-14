package yahier.exst.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;

public class HttpGetConnection {
    final static int RETRY_TIME = 3;
    static String UTF_8 = "utf-8";

    public static String getConnectUrl(String path) {

        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();


        try {
            url = new URL(path);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("Charset", "utf-8");
            //Log.i("conn:", conn.getResponseMessage());
            int code = conn.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK) {

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception ee) {
            return null;
        } finally {
            try {
                if (in != null)
                    in.close();
                if (is != null)
                    is.close();
                if (conn != null)
                    conn.disconnect();
            } catch (Exception e) {
                return null;
            }
        }
        return sb.toString();
    }


    public static String getResult(HttpsURLConnection conn) {
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        BufferedReader in = null;
        try {
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("Charset", "utf-8");
            //Log.i("conn:", conn.getResponseMessage());
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception ee) {
            return null;
        } finally {
            try {
                if (in != null)
                    in.close();
                if (is != null)
                    is.close();
                if (conn != null)
                    conn.disconnect();
            } catch (Exception e) {
                return null;
            }
        }
        return sb.toString();
    }

    public static Bitmap getImageByURI(String path) {
        try {
            HttpURLConnection conn = null;
            URL imgURL = new URL(path);
            conn = (HttpURLConnection) imgURL.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(45000);
            conn.setDoInput(true);
            //conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            //BitmapFactory.Options opts = new BitmapFactory.Options();
            //opts.inJustDecodeBounds = true;
            //Bitmap bm = BitmapFactory.decodeStream(bis,new Rect(-1,-1,-1,-1),opts);
            Bitmap bm = BitmapFactory.decodeStream(is);
            bis.close();
            is.close();
            return bm;

        } catch (Exception e) {
            System.out.println("e  is  " + e.getCause());
            return null;
        }
    }

    public String getContent(String url) throws Exception {
        StringBuilder sb = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        HttpResponse response = client.execute(new HttpGet(url));
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
        }
        return sb.toString();
    }

    public static String httpGet(String url) {
        StringBuilder sb = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = null;
        HttpEntity httpEntity = null;
        try {
            httpResponse = client.execute(httpGet);
            httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), UTF_8), 8192);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String httpPost(String url, List<NameValuePair> params) {
        //Log.e("httpPost", "begin");
        StringBuilder sb = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse httpResponse = null;
        HttpEntity httpEntity = null;
        try {
            //Log.e("httpPost", "running");
            httpResponse = client.execute(httpPost);
            httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"), 8192);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
            }
            //Log.e("httpPost", "done");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 上传文件
     *
     * @param urlPath  请求路径
     * @param filePath 文件路径
     * @param newName  上传之后的文件名
     * @param field
     * @return String
     * @throws Throwable
     * @throws AppException
     */
    public static String sendFile(String urlPath, String filePath, String newName, String field) throws Throwable {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        StringBuffer b = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
			/* 设置传�?的method=POST */
            con.setRequestMethod("POST");
			/* setRequestProperty */

            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + field + "\";filename=\"" + newName + "\"" + end);
            ds.writeBytes(end);

			/* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(filePath);
			/* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
			/* 从文件读取数据至缓冲�?*/
            while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream�?*/
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			/* close streams */
            fStream.close();
            ds.flush();

            InputStream is = con.getInputStream();
            int ch;
            b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
        } catch (IOException e) {

            throw e.fillInStackTrace();

        }

        // if (BuildConfig.DEBUG)
        // Log.d("responseBody", b.toString());
        return b.toString();
    }


}
