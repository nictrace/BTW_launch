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

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import net.launcher.run.Settings;

/**
 * @author nictrace
 *
 */
public class VoxelSaver {
	
	private CloseableHttpClient client;
	private HttpPost httpPost;
	private MultipartEntityBuilder meb;
	private HttpEntity multipart;
	private File pointsfile = null;
	
	public VoxelSaver() throws ClientProtocolException, IOException{
		this.client = HttpClients.createDefault();
		
		this.meb = MultipartEntityBuilder.create();
	    meb.addTextBody("username", "John");
	    meb.addTextBody("password", "pass");
	    // определим путь к файлу
	    local();
	    
	    if(this.pointsfile != null){
	    // теперь нужно собрать правильное имя файла (сервер-логин)
	    	Integer s = BaseUtils.config.getPropertyInteger("server");
	    	String d = s + BaseUtils.getPropertyString("login") + ".points";
    
	    	if(this.pointsfile != null){
	    		meb.addBinaryBody("file", this.pointsfile, ContentType.APPLICATION_OCTET_STREAM, d);
	    		this.multipart = meb.build();
	    	}
	    }
	}
	
	public int push() throws ClientProtocolException, IOException{
		if(this.pointsfile == null) return 0;
		this.httpPost = new HttpPost("http://yarmine.3d-game.com/voxel");
		this.httpPost.setEntity(this.multipart);
	    CloseableHttpResponse response = this.client.execute(httpPost);

	    HttpEntity entity = response.getEntity();
	    InputStream is = entity.getContent();
	    
	    ByteArrayOutputStream result = new ByteArrayOutputStream();
	    byte[] buffer = new byte[10240];
	    int length;
	    while ((length = is.read(buffer)) != -1) {
	        result.write(buffer, 0, length);
	    }
	    // StandardCharsets.UTF_8.name() > JDK 7
	    BaseUtils.sendp(result.toString("UTF-8"));

	    return response.getStatusLine().getStatusCode();
	}

	public long check(int server, String login){
		String resp = BaseUtils.runHTTP("http://yarmine.3d-game.com/voxel", "?fname="+server+login, true);
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
