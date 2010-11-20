package com.pitecan.gyacouploader;

/*
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicNameValuePair;


import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.os.Bundle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.net.URI;
import android.net.*;
import android.util.*;
import android.os.Environment;
*/

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.*;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.media.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GyacoUploader extends Activity
{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = null;
        try{
            uri = Uri.parse(getIntent().getExtras().get("android.intent.extra.STREAM").toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
	Log.v("Gyaco","uri="+uri); // e.g. "content://media/external/audio/media/346" ContentURIという?

	try {
	    InputStream in = getContentResolver().openInputStream(uri);
	    in2file(in,"junk.amr");

	}
        catch(Exception e){
            e.printStackTrace();
	}

	try{
	    upload();
	}
	catch(ParseException e){
	    e.printStackTrace();
	}
	catch(IOException e){
	    e.printStackTrace();
	}

	/*
	HttpClient httpClient = new DefaultHttpClient();
	Log.v("Gyaco","httpClient="+httpClient);
	HttpPost post = new HttpPost("http://masui.sfc.keio.ac.jp/gyaco/upload");
	Log.v("Gyaco","post="+post);

	MultipartEntity entity = new MultipartEntity();
	entity.addPart("file_ext", new StringBody("amr"));
	entity.addPart("data", new FileBody(new File("junk.amr"), "audio/amr"));
	//File f = new File("junk.amr");
	//Log.v("Gyaco", "length="+f.length());
	post.setEntity(entity);
	post.setHeader("User-Agent", "GyacoUploader/0.1");
	Log.v("Gyaco",post.getHeaders("Content-Type").toString());
	httpClient.execute(post);
	*/

        setContentView(new GyacoView(this));
    }

    public void upload() throws ParseException, IOException{
	HttpClient httpClient = new DefaultHttpClient();
	Log.v("Gyaco","httpClient="+httpClient);
	HttpPost post = new HttpPost("http://masui.sfc.keio.ac.jp/gyaco/upload");
	// <uses-permission android:name="android.permission.INTERNET" /> が必要!
	Log.v("Gyaco","post="+post);

        Log.v("Gyaco","StrageDir="+Environment.getExternalStorageDirectory());
	Log.v("Gyaco", "PackageName="+this.getPackageName());

	MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	//MultipartEntity entity = new MultipartEntity();

	//entity.addPart("data", new FileBody(new File("junk.amr"), "audio/amr"));

	entity.addPart("file_ext", new StringBody("amr"));
	// entity.addPart("data", new FileBody(new File("/data/data/" + this.getPackageName() + "/files/junk.amr"), "application/octet-stream"));
	entity.addPart("data", new FileBody(new File("/data/data/" + this.getPackageName() + "/files/junk.amr"), "audio/amr"));

	File f = new File("/data/data/" + this.getPackageName() + "/files/junk.amr");
	Log.v("Gyaco", "length="+f.length());
	post.setEntity(entity);
	post.setHeader("User-Agent", "GyacoUploader/0.1");
	//post.setHeader("Content-Disposition", "attachment; filename=\"junk.amr\"");
	//Log.v("Gyaco",post.getHeaders("Content-Type").toString());
	httpClient.execute(post);
    }
    /*
      // マルチパートフォーム
      final MultipartEntity reqEntity =
         new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
      // タイトル項目
      reqEntity.addPart(titleNameValuePair.getName(),
            new StringBody(titleNameValuePair.getValue(), DEFAULT_CHARSET));
      // ファイル項目
      final File file = new File(fileNameValuePair.getValue());
      reqEntity.addPart(fileNameValuePair.getName(),
            new FileBody(file, CONTENTTYPE_BINARY));

      httpPost.setEntity(reqEntity);
      return getResponseStatusLine(httpClient, httpPost);
    */
 

    // 入力ストリームからファイルに保存
    private void in2file(InputStream in, String fileName) throws Exception {
        int size;
        byte[] w = new byte[1024];
        OutputStream out = null;
        try {
            out = this.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            while (true) {
                size = in.read(w);
                if (size <= 0) break;
                out.write(w, 0, size);
            };
            out.close();
            in.close();
        } catch (Exception e) {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception e2) {}
            throw e;
        }
    }
}

class GyacoView extends View
{
    public GyacoView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
    }

    //描画
    @Override 
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();       
        canvas.drawText("Hello World!",0,12,paint);
    }
}