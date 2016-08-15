package net.launcher.utils;

import java.io.IOException;
import java.io.InputStream;

public class Base64InputStream extends InputStream {

   public InputStream inputStream;
   public int[] buffer;
   public int bufferCounter = 0;
   public boolean eof = false;
   public static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
   public static char pad = 61;


   public Base64InputStream(InputStream inputStream) {
      this.inputStream = inputStream;
   }

   public int read() throws IOException {
      if(this.buffer == null || this.bufferCounter == this.buffer.length) {
         if(this.eof) {
            return -1;
         }

         this.acquire();
         if(this.buffer.length == 0) {
            this.buffer = null;
            return -1;
         }

         this.bufferCounter = 0;
      }

      return this.buffer[this.bufferCounter++];
   }

   public void acquire() throws IOException {
      char[] four = new char[4];
      int i = 0;

      do {
         int padded = this.inputStream.read();
         if(padded == -1) {
            if(i != 0) {
               throw new IOException("Bad base64 stream");
            }

            this.buffer = new int[0];
            this.eof = true;
            return;
         }

         char l = (char)padded;
         if(chars.indexOf(l) == -1 && l != pad) {
            if(l != 13 && l != 10) {
               throw new IOException("Bad base64 stream");
            }
         } else {
            four[i++] = l;
         }
      } while(i < 4);

      boolean var6 = false;

      for(i = 0; i < 4; ++i) {
         if(four[i] != pad) {
            if(var6) {
               throw new IOException("Bad base64 stream");
            }
         } else if(!var6) {
            var6 = true;
         }
      }

      byte var7;
      if(four[3] == pad) {
         if(this.inputStream.read() != -1) {
            throw new IOException("Bad base64 stream");
         }

         this.eof = true;
         if(four[2] == pad) {
            var7 = 1;
         } else {
            var7 = 2;
         }
      } else {
         var7 = 3;
      }

      int aux = 0;

      for(i = 0; i < 4; ++i) {
         if(four[i] != pad) {
            aux |= chars.indexOf(four[i]) << 6 * (3 - i);
         }
      }

      this.buffer = new int[var7];

      for(i = 0; i < var7; ++i) {
         this.buffer[i] = aux >>> 8 * (2 - i) & 255;
      }

   }

   public void close() throws IOException {
      this.inputStream.close();
   }

}
