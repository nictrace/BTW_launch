package net.launcher.utils;

import java.io.IOException;
import java.io.OutputStream;

public class Base64OutputStream extends OutputStream {

   public OutputStream outputStream;
   public int buffer;
   public int bytecounter;
   public static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
   public static char pad = 61;
   public int linecounter;
   public int linelength;


   public Base64OutputStream(OutputStream outputStream) {
      this(outputStream, 76);
   }

   public Base64OutputStream(OutputStream outputStream, int wrapAt) {
      this.outputStream = null;
      this.buffer = 0;
      this.bytecounter = 0;
      this.linecounter = 0;
      this.linelength = 0;
      this.outputStream = outputStream;
      this.linelength = wrapAt;
   }

   public void write(int b) throws IOException {
      int value = (b & 255) << 16 - this.bytecounter * 8;
      this.buffer |= value;
      ++this.bytecounter;
      if(this.bytecounter == 3) {
         this.commit();
      }

   }

   public void close() throws IOException {
      this.commit();
      this.outputStream.close();
   }

   public void commit() throws IOException {
      if(this.bytecounter > 0) {
         if(this.linelength > 0 && this.linecounter == this.linelength) {
            this.outputStream.write("\r\n".getBytes());
            this.linecounter = 0;
         }

         char b1 = chars.charAt(this.buffer << 8 >>> 26);
         char b2 = chars.charAt(this.buffer << 14 >>> 26);
         char b3 = this.bytecounter < 2?pad:chars.charAt(this.buffer << 20 >>> 26);
         char b4 = this.bytecounter < 3?pad:chars.charAt(this.buffer << 26 >>> 26);
         this.outputStream.write(b1);
         this.outputStream.write(b2);
         this.outputStream.write(b3);
         this.outputStream.write(b4);
         this.linecounter += 4;
         this.bytecounter = 0;
         this.buffer = 0;
      }

   }

}
