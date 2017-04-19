package net.launcher.run;

import java.util.concurrent.atomic.AtomicInteger;

import net.launcher.utils.BaseUtils;

public class Settings
{
	/** Настройка заголовка лаунчера */
	public static final String  title		         = "TheBetweenLand"; //Заголовок лаунчера
	public static final String  titleInGame  	     = "TheBetweenLand"; //Заголовок лаунчера после авторизации
	public static final String  baseconf		     = "TheBetweenLand"; //Папка с файлом конфигурации
	public static final String  pathconst		     = "TheBetweenLand/%SERVERNAME%"; //Конструктор пути к папке с MC
	public static final String  skins                = "MinecraftSkins/"; //Папка скинов
	public static final String  cloaks               = "MinecraftCloaks/"; //Папка плащей
	/** Параметры подключения */
	public static final String  http	             = "http://";  //Протокол подключения https:// если есть ssl сертификат
	public static final String  domain	 	         = "betweenland.ru";//Домен сайта
	public static final String  siteDir		         = "addons/launcher";//Папка с файлами лаунчера на сайте
	public static final String RegisterUrl	         = "http://betweenland.ru/index.php?do=register";	//Ссылка на регистрацию, при useRegister = false
	public static final String  updateFile		     = "http://betweenland.ru/addons/launcher/launcher/TheBetweenLand";//Ссылка на обновления лаунчера. Не писать на конце ".exe .jar"!
	public static final String  buyVauncherLink      = "http://betweenland.ru/"; //Ссылка на страницу покупки ваучеров
	public static final String  iMusicname           = "/assets/audio/001.mp3";
	public static final int  thread                  = 4; //Количество потоков для загрузки файлов.
    public static boolean useMulticlient = true;			//Использовать функцию "по клиенту на сервер"
	public static final String[] p = {"wireshark", "cheat", "ECManager32", "ECManager64"};  //Список запрещенных процессов.
	
	public static final String configFilename 	= "launcher.config";
	public static boolean patchDir = false;				 //Использовать автоматическую замену директории игры (true/false)	
	public static int height                         = 532;      //Высота окна клиента
	public static int width                          = 900;      //Ширина окна клиента
	public static int defaultmemory                  = 768;      //Выделение памяти при первом запуске.
        
	public static String[] servers =
	{
		"Fentasy, 46.174.54.55, 25565, 1.7.10",
	};

	/** Настройка панели ссылок **/
	public static final String[] links = 
	{
		//Для отключения добавьте в адрес ссылки #
		//"url::http://",
		"::#",
	};

	/** Настройки структуры лаунчера */
	public static boolean useAutoenter	         =  false;  //Использовать функцию автозахода на выбранный сервер
	public static boolean useRegister		     =  false;  //Использовать Регистрацию в лаунчере
	public static boolean useStandartWB		     =  true;   //Использовать стандартный браузер для открытия ссылок
	public static boolean usePersonal		     =  true;   //Использовать Личный кабинет
	public static boolean customframe 		     =  true;   //Использовать кастомный фрейм
	public static boolean useConsoleHider		 =  false;  //Использовать скрытие консоли клиента
	public static boolean useModCheckerTimer	 =  true;   //Перепроверка jar через 30 секунд
	public static int     useModCheckerint       =  2;      //Количество раз перепроверки jar во время игры
	public static boolean assetsfolder           =  false;  //Скачивать assets из папки, или из архива (true=из папки false=из архива) в connect.php должно быть так же.

	public static final String protectionKey	 = "456vgbe5tdrt3rtfsgj"; //Ключ сессии.
	public static final String key1              = "krofingd74jdsnme"; //16 Character Key Ключ пост запросов
	public static final String key2              = "krofingd74jdsnme"; //16 Character Key Ключ пост запросов
	
	public static boolean debug		 	         =  true; //Отображать все действия лаунчера (отладка)(true/false)
    public static boolean drawTracers		     =  false; //Отрисовывать границы элементов лаунчера
	public static final String masterVersion     = "0.9"; //Версия лаунчера

	public static boolean release 		         =  false;
															/**Ставим true после окончания настройки! 
														    Необходимо для автообновления лаунчера 
														    после смены ключей key1 key2.*/
	public static AtomicInteger GuardState		 = new AtomicInteger(0);	// состояние модуля античит  
	public static void onStart() {
		BaseUtils.send("*** osStart() signalled");
	}
	public static void onStartMinecraft() {
		BaseUtils.send("*** osStartMinecraft() signalled");
	}
	
}