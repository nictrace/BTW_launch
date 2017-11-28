/**
 * 
 */
package net.launcher.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.launcher.utils.PostUtils;
import net.launcher.run.Settings;

/**
 * @author nictrace
 *
 */
public class VoxelSaver {
	
	private File pointsfile = null;
	private String servname;
	private final String addr = "http://yarmine.3d-game.com/voxel"; 
	
	public VoxelSaver(){
	    // определим путь к файлу
	    local();
	    
	    if(this.pointsfile != null){
	    // теперь нужно собрать правильное имя файла (сервер-логин)
	    	Integer s = BaseUtils.config.getPropertyInteger("server");
	    	this.servname = s + BaseUtils.getPropertyString("login") + ".points";
	    }
	}
	
	public int push() throws IOException{
		if(this.pointsfile == null) return 0;
		InputStream is = PostUtils.post(new URL(this.addr),
				new Object[]{"sname", this.servname,
						"file", this.pointsfile});
	    
	    ByteArrayOutputStream result = new ByteArrayOutputStream();
	    byte[] buffer = new byte[10240];
	    int length;
	    while ((length = is.read(buffer)) != -1) {
	        result.write(buffer, 0, length);
	    }
	    // StandardCharsets.UTF_8.name() > JDK 7
	    BaseUtils.sendp(result.toString("UTF-8"));

	    return 200; // как выцепить код ответа?
	}

	public long check(int server, String login){
		String resp = BaseUtils.runHTTP(this.addr, "?fname="+server+login, true);
		if(resp == null) return -1;
		resp = resp.replaceAll(System.lineSeparator(), "");
		return Long.parseLong(resp);
	}
/**
 * Поиск локального файла поинтов
 * @return File объект нужного файла
 */
	public File local(){
	    File folderToScan = new File(BaseUtils.getMcDir().getAbsolutePath() + File.separator + "config" + File.separator
	    		+ "VoxelMods" + File.separator + "voxelMap");
	    File[] listOfFiles = folderToScan.listFiles();
	    if(listOfFiles != null){
	    	for (int i = 0; i < listOfFiles.length; i++) {
	    		if (listOfFiles[i].isFile()) {
	    			String target_file = listOfFiles[i].getName();
	    			if (target_file.endsWith(".points")) {
	    				this.pointsfile = listOfFiles[i];
	    			}
	    		}
	    	}
	    }
	    else{
	    	return null;
	    }
		return this.pointsfile;
	}
	/**
	 * Заливка удаленного файла поинтов в клиент
	 * @return 0: удача, -1: неудача
	 * @throws IOException 
	 */
	public int pull(int server, String login){
		
		String resp = BaseUtils.runHTTP("http://yarmine.3d-game.com/voxel", "?fname="+server+login+"&pull=1", true);
		if(resp == null) return -1;
		if(this.pointsfile == null){
			// Теперь нужно выудить данные сервера для привильного имени
			String p = Settings.servers[server];
			String m[] = p.split(",");
			m[1] = m[1].replaceAll(" ", "");
			m[2] = m[2].replaceAll(" ", "");
			String outname = m[1];
			if(m[2] != "25565") outname +="~colon~" + m[2];
			
			this.pointsfile = new File(BaseUtils.getMcDir().getAbsolutePath() + File.separator + "config" + File.separator
		    		+ "VoxelMods" + File.separator + "voxelMap/"+ outname + ".points");
		}
		// файл создан, осталось сбросить в него данные
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(this.pointsfile.getAbsolutePath()));
			out.write(resp);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
