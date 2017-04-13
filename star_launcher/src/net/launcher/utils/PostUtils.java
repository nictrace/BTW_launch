package net.launcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class PostUtils {

   private static Random random = new Random();
   private URLConnection connection;
   private OutputStream os;
   private String boundary;


   private void connect() throws IOException {
      if(this.os == null) {
         this.os = this.connection.getOutputStream();
      }

   }

   private void write(char c) throws IOException {
      this.connect();
      this.os.write(c);
   }

   private void write(String s) throws IOException {
      this.connect();
      this.os.write(s.getBytes());
   }

   private void newline() throws IOException {
      this.connect();
      this.write("\r\n");
   }

   private void writeln(String s) throws IOException {
      this.connect();
      this.write(s);
      this.newline();
   }

   static String randomString() {
      return Long.toString(random.nextLong(), 36);
   }

   private void boundary() throws IOException {
      this.write("--");
      this.write(this.boundary);
   }

   private PostUtils(URLConnection connection) throws IOException {
      this.os = null;
      this.boundary = randomString() + randomString() + randomString();
      this.connection = connection;
      connection.setDoOutput(true);
      connection.setRequestProperty("User-Agent", "Launcher/64.0");
      connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
      connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
   }

   private PostUtils(URL url) throws IOException {
      this(url.openConnection());
   }

   private void writeName(String name) throws IOException {
      this.newline();
      this.write("Content-Disposition: form-data; name=\"");
      this.write(name);
      this.write('\"');
   }

   private void setParameter(String name, String value) throws IOException {
      this.boundary();
      this.writeName(name);
      this.newline();
      this.newline();
      this.writeln(value);
   }

   private static void pipe(InputStream in, OutputStream out) throws IOException {
      byte[] buf = new byte[500000];
      int nread;
      synchronized(in) {
         while((nread = in.read(buf, 0, buf.length)) >= 0) {
            out.write(buf, 0, nread);
         }
      }

      out.flush();
//      Object buf1 = null;
   }

   private void setParameter(String name, String filename, InputStream is) throws IOException {
      this.boundary();
      this.writeName(name);
      this.write("; filename=\"");
      this.write(filename);
      this.write('\"');
      this.newline();
      this.write("Content-Type: ");
      String type = URLConnection.guessContentTypeFromName(filename);
      if(type == null) {
         type = "application/octet-stream";
      }

      this.writeln(type);
      this.newline();
      pipe(is, this.os);
      this.newline();
   }

   private void setParameter(String name, File file) throws IOException {
      this.setParameter(name, file.getPath(), new FileInputStream(file));
   }

   private void setParameter(String name, Object object) throws IOException {
      if(object instanceof File) {
         this.setParameter(name, (File)object);
      } else {
         this.setParameter(name, object.toString());
      }

   }

   private void setParameters(Object[] parameters) throws IOException {
      if(parameters != null) {
         for(int i = 0; i < parameters.length - 1; i += 2) {
            this.setParameter(parameters[i].toString(), parameters[i + 1]);
         }

      }
   }

   private InputStream post() throws IOException {
      this.boundary();
      this.writeln("--");
      this.os.close();
      return this.connection.getInputStream();
   }

   public InputStream post(Object[] parameters) throws IOException {
      this.setParameters(parameters);
      return this.post();
   }

   public static InputStream post(URL url, Object[] parameters) throws IOException {
      return (new PostUtils(url)).post(parameters);
   }

}
