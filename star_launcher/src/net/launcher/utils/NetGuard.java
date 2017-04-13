package net.launcher.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import net.launcher.components.Game;
import net.launcher.run.Settings;

public class NetGuard extends Thread {

    private String TEMPL_CONN = "The client '%d' closed the connection";

    private  Socket socket;
    private  int    num;

    public NetGuard() {}
    public void setSocket(int num, Socket socket)
    {
        // Определение значений
        this.num    = num;
        this.socket = socket;
        Settings.GuardState.set(0);

        // Установка daemon-потока
        setDaemon(true);
        // Старт потока
        start();
    }

    @SuppressWarnings("deprecation")
	public void run()
    {
        try {
        	Thread.currentThread().setName("NetGuard");
        	Settings.GuardState.set(1);	// Клиент подключен
            // Определяем входной и выходной потоки сокета
            // для обмена данными с клиентом 
            InputStream  sin  = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            DataInputStream  dis = new DataInputStream (sin );
            DataOutputStream dos = new DataOutputStream(sout);

            String line = null;
            BaseUtils.send("[NETGUARD]: entering main loop");
            while(true) {
                // Ожидание сообщения от клиента
                //line = dis.readUTF();
            	
//            	if(0 < dis.available()){
           		line =  dis.readLine();
           		if(line == null){
           			BaseUtils.sendp("null detected - socket is closed!");
           			if(Game.start.isAlive()){
           				BaseUtils.sendp("interrupting game");
           				Game.start.interrupt();		// этот метод просто вежливо просит поток закрыться)
           				Game.start.stop();			// этот метод вырубает его "с ноги"
           			}
           			Runtime.getRuntime().halt(1);
           			System.out.println("Current VM terminated...");
           		}
           		// Отсылаем клиенту обратно эту самую строку текста
           		dos.writeUTF("RE: " + line);
           		// Завершаем передачу данных
           		dos.flush();

           		if (line.equalsIgnoreCase("quit")) {
           			// завершаем соединение
           			socket.close();
           			BaseUtils.send(String.format(TEMPL_CONN, num));
           			break;
           		}
            }
        }
        catch(SocketException se){
        	BaseUtils.send(se.getMessage());
   			if(Game.start.isAlive()){
   				BaseUtils.send("[NETGUARD]: interrupting game");
   				Game.start.interrupt();		// этот метод просто вежливо просит поток закрыться)
   				Game.start.stop();			// этот метод вырубает его "с ноги"
   			}
   			else{
   				Runtime.getRuntime().halt(1);
   			}
        }
        catch(Exception e) {		// что-то непонятное с сокетом, закроем программу...
        	Settings.GuardState.set(0);
            BaseUtils.sendErr("Worker exception : " + e);
			Runtime.getRuntime().halt(1);
        }        
    }}

