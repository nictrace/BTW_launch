package net.launcher.run;

import java.util.concurrent.atomic.AtomicInteger;

import net.launcher.utils.BaseUtils;

public class Settings
{
	/** Настройка заголовка лаунчера */
	public static final String  title		         = ""; //Заголовок лаунчера
	public static final String  titleInGame  	     = "300miners.com <3"; //Заголовок лаунчера после авторизации
	public static final String  baseconf		     = "300miners"; //Папка с файлом конфигурации
	public static final String  pathconst		     = "300miners/%SERVERNAME%"; //Конструктор пути к папке с MC
	public static final String  skins                = "MinecraftSkins/"; //Папка скинов
	public static final String  cloaks               = "MinecraftCloaks/"; //Папка плащей
	/** Параметры подключения */
	public static final String  http	             = "http://";  		//Протокол подключения https:// если есть ssl сертификат
	public static final String  domain	 	         = "300miners.com";	//Домен сайта
	public static final String  siteDir		         = "site";			//Папка с файлами лаунчера на сайте
	public static final String  RegisterUrl	         = http + domain +"/start.html";	//Ссылка на регистрацию, при useRegister = false
	public static final String  updateFile		     = http + domain +"/site/launcher/300pixels";//Ссылка на обновления лаунчера. Не писать на конце ".exe .jar"!
	public static final String  buyVauncherLink      = http + domain +"/"; //Ссылка на страницу покупки ваучеров
	public static final String  iMusicname           = "001.mp3";
	public static final int  thread                  = 4;		//Количество потоков для загрузки файлов.
    public static boolean useMulticlient			 = true;	//Использовать функцию "по клиенту на сервер"
	public static final String[] p = {"wireshark", "cheat", "ECManager32", "ECManager64"};  //Список запрещенных процессов.
	
	public static final String configFilename 	= "launcher.config";
	// для 1.5.2 обязательно true
	public static boolean patchDir 					 = true;	 //Использовать автоматическую замену директории игры (true/false)	
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

	public static final String protectionKey	 = "KJNda42IXn1zJA"; //Ключ сессии.
	public static final String key1              = "KJNanaM81JHxaW2z"; //16 Character Key Ключ пост запросов
	public static final String key2              = "KJNanaM81JHxaW2z"; //16 Character Key Ключ пост запросов
	
	public static boolean debug		 	         =  true; //Отображать все действия лаунчера (отладка)(true/false)
    public static boolean drawTracers		     =  false; //Отрисовывать границы элементов лаунчера
	public static final String masterVersion     = "final_4"; //Версия лаунчера

	public static boolean release 		         =  false;
															/**Ставим true после окончания настройки! 
														    Необходимо для автообновления лаунчера 
														    после смены ключей key1 key2.*/
	public static AtomicInteger GuardState		 = new AtomicInteger(0);	// состояние модуля античит
	public static String FakeHash				 = "22f0ee3396b37d1c092101818073fec1"; 

	public static void onStart() {
		BaseUtils.send("*** onStart() signalled");
	}
	public static void onStartMinecraft() {
		BaseUtils.send("*** onStartMinecraft() signalled");
	}
}