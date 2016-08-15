package net.launcher.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ConfigUtils {

   private File out;
   private Boolean cached = Boolean.valueOf(false);
   private String filename = null;
   private HashMap<String, Object> cache;
   private InputStream input = null;


   public ConfigUtils(String filename, String out) {
      this.filename = filename;
      this.out = new File(out);
   }

   public ConfigUtils(String filename, File out) {
      this.filename = filename;
      this.out = out;
   }

   public ConfigUtils(InputStream input, File out) {
      this.input = input;
      this.out = out;
   }

   public ConfigUtils(File out) throws FileNotFoundException {
      if(!out.exists()) {
         throw new FileNotFoundException("-");
      } else {
         this.out = out;
      }
   }

   public Boolean isCached() {
      return this.cached;
   }

   public void setCached(Boolean cached) {
      this.cached = cached;
      if(Boolean.valueOf(false).booleanValue()) {
         this.cache = null;
      }

   }

   private void create(String filename) {
      InputStream input = this.getClass().getResourceAsStream(filename);
      if(input != null) {
         FileOutputStream output = null;

         try {
            this.out.getParentFile().mkdirs();
            output = new FileOutputStream(this.out);
            byte[] ignored = new byte[8192];

            int length;
            while((length = input.read(ignored)) > 0) {
               output.write(ignored, 0, length);
            }
         } catch (Exception var18) {
            ;
         } finally {
            try {
               input.close();
            } catch (Exception var17) {
               ;
            }

            try {
               if(output != null) {
                  output.close();
               }
            } catch (Exception var16) {
               ;
            }

         }
      }

   }

   private void create(InputStream input) {
      if(input != null) {
         FileOutputStream output = null;

         try {
            output = new FileOutputStream(this.out);
            byte[] ignored = new byte[8192];

            int length;
            while((length = input.read(ignored)) > 0) {
               output.write(ignored, 0, length);
            }
         } catch (Exception var17) {
            ;
         } finally {
            try {
               input.close();
            } catch (Exception var16) {
               ;
            }

            try {
               if(output != null) {
                  output.close();
               }
            } catch (Exception var15) {
               ;
            }

         }
      }

   }

   private HashMap<String, Object> loadHashMap() {
      HashMap<String, Object> result = new HashMap<String, Object>();
      BufferedReader br = null;

      try {
         br = new BufferedReader(new FileReader(this.out));

         String e;
         while((e = br.readLine()) != null) {
            if(!e.isEmpty() && !e.startsWith("#") && e.contains(": ")) {
               String[] args = e.split(": ");
               if(args.length < 2) {
                  result.put(args[0], (Object)null);
               } else {
                  result.put(args[0], args[1]);
               }
            }
         }
      } catch (IOException var13) {
         ;
      } finally {
         try {
            br.close();
         } catch (Exception var12) {
            ;
         }

      }

      return result;
   }

   public void load() {
      if(this.filename != null && !this.out.exists()) {
         this.create(this.filename);
      }

      if(this.input != null && !this.out.exists()) {
         this.create(this.input);
      }

      if(this.cached.booleanValue()) {
         this.cache = this.loadHashMap();
      }

   }

   public String getPropertyString(String property) {
      try {
         if(this.cached.booleanValue()) {
            return (String)this.cache.get(property);
         } else {
            HashMap<String, Object> e = this.loadHashMap();
            return (String)e.get(property);
         }
      } catch (Exception var3) {
         return null;
      }
   }

   public Integer getPropertyInteger(String property) {
      try {
         if(this.cached.booleanValue()) {
            return Integer.valueOf(Integer.parseInt((String)this.cache.get(property)));
         } else {
            HashMap<String, Object> e = this.loadHashMap();
            return Integer.valueOf(Integer.parseInt((String)e.get(property)));
         }
      } catch (Exception var3) {
         return null;
      }
   }

   public Boolean getPropertyBoolean(String property) {
      try {
         String e;
         if(this.cached.booleanValue()) {
            e = (String)this.cache.get(property);
         } else {
            HashMap<String, Object> contents = this.loadHashMap();
            e = (String)contents.get(property);
         }

         return e != null && e.equalsIgnoreCase("true")?Boolean.valueOf(true):Boolean.valueOf(false);
      } catch (Exception var4) {
         return null;
      }
   }

   public Double getPropertyDouble(String property) {
      try {
         String e;
         if(this.cached.booleanValue()) {
            e = (String)this.cache.get(property);
         } else {
            HashMap<String, Object> contents = this.loadHashMap();
            e = (String)contents.get(property);
         }

         if(!e.contains("")) {
            e = e + ".0";
         }

         return Double.valueOf(Double.parseDouble(e));
      } catch (Exception var4) {
         return null;
      }
   }

   public Boolean checkProperty(String property) {
      try {
         String check;
         if(this.cached.booleanValue()) {
            check = (String)this.cache.get(property);
         } else {
            HashMap<String, Object> e = this.loadHashMap();
            check = (String)e.get(property);
         }

         if(check != null) {
            return Boolean.valueOf(true);
         }
      } catch (Exception var4) {
         return Boolean.valueOf(false);
      }

      return Boolean.valueOf(false);
   }

   private void flush(HashMap<Integer, Object> newContents) {
      try {
         this.delFile(this.out);
         this.out.createNewFile();
         BufferedWriter e = new BufferedWriter(new FileWriter(this.out));

         for(int i = 1; i <= newContents.size(); ++i) {
            String line = (String)newContents.get(Integer.valueOf(i));
            if(line == null) {
               e.append("\n");
            } else {
               e.append(line);
               e.append("\n");
            }
         }

         e.flush();
         e.close();
         if(this.cached.booleanValue()) {
            this.load();
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   private void delFile(File file) {
      if(file.exists()) {
         file.delete();
      }

   }

   private HashMap<Integer, Object> getAllFileContents() {
      HashMap<Integer, Object> result = new HashMap<Integer, Object>();
      BufferedReader br = null;
      Integer i = Integer.valueOf(1);

      try {
         br = new BufferedReader(new FileReader(this.out));

         String e;
         while((e = br.readLine()) != null) {
            if(e.isEmpty()) {
               result.put(i, (Object)null);
               i = Integer.valueOf(i.intValue() + 1);
            } else {
               result.put(i, e);
               i = Integer.valueOf(i.intValue() + 1);
            }
         }
      } catch (Exception var15) {
         var15.printStackTrace();
      } finally {
         try {
            br.close();
         } catch (Exception var14) {
            ;
         }

      }

      return result;
   }

   public void insertComment(String comment) {
      HashMap<Integer, Object> contents = this.getAllFileContents();
      contents.put(Integer.valueOf(contents.size() + 1), "#" + comment);
      this.flush(contents);
   }

   public void insertComment(String comment, Integer line) {
      HashMap<Integer, Object> contents = this.getAllFileContents();
      if(line.intValue() < contents.size() + 1) {
         HashMap<Integer, Object> newContents = new HashMap<Integer, Object>();

         int i;
         for(i = 1; i < line.intValue(); ++i) {
            newContents.put(Integer.valueOf(i), contents.get(Integer.valueOf(i)));
         }

         newContents.put(line, "#" + comment);

         for(i = line.intValue(); i <= contents.size(); ++i) {
            newContents.put(Integer.valueOf(i + 1), contents.get(Integer.valueOf(i)));
         }

         this.flush(newContents);
      }
   }

   public void put(String property, Object obj) {
      HashMap<Integer, Object> contents = this.getAllFileContents();
      contents.put(Integer.valueOf(contents.size() + 1), property + ": " + obj.toString());
      this.flush(contents);
   }

   public void put(String property, Object obj, Integer line) {
      HashMap<Integer, Object> contents = this.getAllFileContents();
      if(line.intValue() < contents.size() + 1) {
         HashMap<Integer, Object> newContents = new HashMap<Integer, Object>();

         int i;
         for(i = 1; i < line.intValue(); ++i) {
            newContents.put(Integer.valueOf(i), contents.get(Integer.valueOf(i)));
         }

         newContents.put(line, property + ": " + obj.toString());

         for(i = line.intValue(); i <= contents.size(); ++i) {
            newContents.put(Integer.valueOf(i + 1), contents.get(Integer.valueOf(i)));
         }

         this.flush(newContents);
      }
   }

   public void changeProperty(String property, Object obj) {
      HashMap<Integer, Object> contents = this.getAllFileContents();
      if(contents != null) {
         for(int i = 1; i <= contents.size(); ++i) {
            if(contents.get(Integer.valueOf(i)) != null) {
               String check = (String)contents.get(Integer.valueOf(i));
               if(check.startsWith(property)) {
                  check = check.replace(property, "");
                  if(check.startsWith(": ")) {
                     contents.remove(Integer.valueOf(i));
                     contents.put(Integer.valueOf(i), property + ": " + obj.toString());
                  }
               }
            }
         }

         this.flush(contents);
      }
   }

   public Integer getLineCount() {
      HashMap<Integer, Object> contents = this.getAllFileContents();
      return Integer.valueOf(contents.size());
   }
}
