package net.launcher.run;


public class Settings {
	/** Настройка заголовка лаунчера */
   public static final String title 			= "";
   public static final String titleInGame 		= "SpaceTechnology";
   public static final String baseconf 			= "SpaceTechnology";
   public static final String pathconst 		= "SpaceTechnology/%SERVERNAME%";
   public static final String skins 			= "MinecraftSkins/";
   public static final String cloaks 			= "MinecraftCloaks/";
	/** Параметры подключения */
   public static final String http 				= "http://";   
   public static final String domain 			= "galaxytechnology.myarena.ru";
   public static final String siteDir 			= "site";
   public static final String RegisterUrl 		= http+"localhost/"; //Ссылка на регистрацию, при useRegister = false   
   public static final String updateFile 		= http+domain+"/"+siteDir+"/launcher/SpaceTechnology";
   public static final String buyVauncherLink 	= "";
   public static final String iMusicname 		= "001.mp3";
   public static final String[] p = new String[]{"wireshark", "cheat"};   //Список запрещенных процессов.

   public static final String configFilename 	= "launcher.config";
   public static int height 					= 532;
   public static int width 						= 900;
   public static int defaultmemory 				= 768;

   public static String[] servers =
   {
		"SpaceTechnology, 46.174.49.40, 25681, 1.7.10", "SpaceTechnologyLite, 46.174.49.40, 25681, 1.7.10",
   };
   
	/** Настройка панели ссылок **/
   public static final String[] links =
   {
	    //Для отключения добавьте в адрес ссылки #
		//"url::http://",
		" Сайт::http://galaxytechnology.myarena.ru",
   };
   
	/** Настройки структуры лаунчера */   
   public static boolean useAutoenter = false;			//Использовать функцию автозахода на выбранный сервер
   public static boolean useRegister = true;			//Использовать Регистрацию в лаунчере
   public static boolean useMulticlient = true;			//Использовать функцию "по клиенту на сервер"
   public static boolean useStandartWB = true;			//Использовать стандартный браузер для открытия ссылок
   public static boolean usePersonal = false;			//Использовать Личный кабинет
   public static boolean customframe = true;
   public static boolean useConsoleHider = false;
   
   public static boolean useModCheckerTimer = true;
   public static int useModCheckerint = 2;
   public static boolean assetsfolder = false;
   
   public static final String protectionKey = "0460469bohdan4ik7691397";
   public static final String key1 = "bohdan4ik0460469";
   public static final String key2 = "bohdan4ik0460469";
   
   public static boolean debug = true;
   public static boolean drawTracers = false;
   public static final String masterVersion = "1.2";

   public static boolean patchDir = false;				 //Использовать автоматическую замену директории игры (true/false)
   
   public static boolean release = true;

   

   public static void onStart() {}
   public static void onStartMinecraft() {}
}