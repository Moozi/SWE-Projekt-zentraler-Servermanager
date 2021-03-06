package mainpackage;

import java.util.InputMismatchException;
import java.util.Scanner;
/**
 * This class manages the whole program.
 * @author Philipp Tendyra
 *
 */
public class Administration extends Thread {

	/** The programs task-list , unique */
	private TaskList<Command> _task_list;
	/** The programs communication-thread which is used to communicate with equivalent programs */
	private Communication _com;
	/**The Programms Config - unique */
	private Config _config;
	
	private Dispatcher _dispatch;
	
	/**
	 * initializes the task-list
	 * initializes the communication-thread
	 * starts the communication-thread
	 * initializes the worker-thread
	 * starts the worker-thread
	 */
	protected void startProgram(){
	 System.out.println("Program start");
		this._config = new Config();
	 this._task_list = new TaskList<Command>();
	 this._com = new Communication(this._task_list, this._config);
	 this._dispatch = new Dispatcher(this._task_list, this._com, this._config);
	 this._com.start();
	 this._task_list.addListDataListener(this._dispatch);
	}//startProgram()
	
	
	/**
	 * starts scanner on the system-input
	 * prints options(0-4) for the input
	 * switches to chosen option, without option sends text as message
	 */
	private void runDialog(){
		
		int input = -1;
		Scanner sc = null;
		while (input != 0) {
			System.out
					.println(	"######## Main Menue ########" +
								"\n# 0 -> Stop " +
								"\n# 1 -> Manage Clients" +
								"\n# 2 -> Manage Repository" +
								"\n# 3 -> Reload the Configfile" +
								"\n# 4 -> write Configfile" +
								"\n# 5 -> Send a Message with Infos to a Client" +
								"\n######## Main Menue ########");
			sc = new Scanner(System.in);
			try{
			input = sc.nextInt();
			}//try
			catch(InputMismatchException ime){
				System.out.println("Please use the menue");
			}//catch
			
			switch (input) {
			case 1:
				clientList(sc);
				input = -1;
				break; 
			case 2:
				repomenue(sc);
				input = -1;
				break;
			case 3:
				this._config.loadConfig();
				input = -1;
				break;
			case 4:
				this._config.writeConfig();
				break;
			case 5:
				sendMessage();
				input = -1;
				break;
			case 0:
				stopProgram();
				break;
			}//switch
		}//while
		
	}//runDialog()
	
	private void clientList(Scanner sc){
		Database base = new Database();
		int input = -1;
		String[][] clients = new String[0][0];
		try {
			String[] tmp = base.getInfo_getClients().split("#");
			clients = new String [tmp.length][];
			for(int i = 0; i < tmp.length; i++)
				clients[i] = tmp[i].split(":");
		} catch (Exception e) {
			System.out.println("Cannot access Database!");
			input = -1;
		}
		
		while (input != 0) {
			String output = "+-------------------->" +
							"\n| Client-List" +
							"\n| 0 -> Back" +
							"\n| 1 -> Add a Client";
			if(clients.length != 0)
			{	for(int i = 2; i < clients.length+2; i++ )
					output +="| "+ i + " -> "+ clients[i-2][0] +"\n";
			
			output += "\n+-------------------->";
			}else
				output += 	"\n|No Clients were found!" +
							"\n+-------------------->";
			
			System.out.println(output);
			try{
			input = sc.nextInt();
			if(clients.length != 0)
			{	
				if(input >= 0+2 && input < clients.length+2)
				{
					try{
					clientmenue(sc,base, clients[input-2][0], Integer.parseInt(clients[input-2][1]));
					}catch(Exception e){System.out.println("Problems with the ClientID");}
					input = -1;
				}else
				{
					System.out.println("## Please insert valid Number ##");
					input = -1;
				}
				
				if(input == 1)
					addclient(base);
			}
			}//try
			catch(InputMismatchException ime){
				System.out.println("Please use the menue");
			}//catch
			
		}//while
	}
	private void addclient(Database base){
		boolean confirm = false;
		String name = "default";
		String ip 	= "default:default";
		String user = "default";
		String pwd	= "default";
		/* Client-Name */
		while(!confirm)
		{
		name = getText("Please enter the clientname: ");
		String tmp = getText("Name = "+ name + " -> correct? (yes/no)");
		if(tmp.toLowerCase().equals("yes") || tmp.toLowerCase().equals("y"))
				confirm = true;
		} 
		confirm = false;
		/* Client-IP*/
		while(!confirm)
		{
			ip = getText("Please enter the client-ip(like ip:port ): ");
			if(!ip.contains(":"))
				System.out.println("Please specify IP and Port like ip:port");
			else{
				String tmp = getText("Name = "+ ip + " -> correct? (yes/no)");
				if(tmp.toLowerCase().equals("yes") || tmp.toLowerCase().equals("y"))
					confirm = true;
			}
		} 
		confirm = false;
		/* Clinet-User */
		while(!confirm){
			user = getText("Please enter the Client-Username: ");
			String tmp = getText("Name = "+ user + " -> correct? (yes/no)");
			if(tmp.toLowerCase().equals("yes") || tmp.toLowerCase().equals("y"))
					confirm = true;
			} 
		/* Client-Password*/
		while(!confirm){
			java.io.Console console = System.console();
			if(console != null)
			{
				System.out.println("Please insert Password (will be masked):");
				String s1 = new String(console.readPassword());
				System.out.println("Please insert once again to confirm:");
				String s2 = new String(console.readPassword());
				if(s1.equals(s2))
				{
					pwd = s1;
					System.out.println("Passwords match and will be stored.");
					confirm = true;
				}else
				{
					System.out.println("Passwords do not match. Please try again.");
					confirm = false;
				}
				
			}else
			{
				System.out.println("Console could not be referred. Insecure Insert:");
				pwd = getText("Please enter the Password: ");
				String tmp = getText("Name = "+ name + " -> correct? (yes/no)");
				if(tmp.toLowerCase().equals("yes") || tmp.toLowerCase().equals("y"))
						confirm = true;
			}
		}
		try{
		base.insertNewClient(name, ip, user, pwd);
		}catch(Exception e)
		{
			System.out.println("Could not add Client. Problems with the Database");
		}
	}
	private void clientmenue(Scanner sc,Database base,  String client, int clientid){
		int input = -1;
		while (input != 0) {
			System.out.println("+-------------------->" +
					"\n| Client: " + client + " ID: "+ clientid +
					"\n| 0 -> back " +
					"\n| 1 -> Get ClientStatus " +
					"\n| 2 -> Get HardwareInfo " +
					"\n| 3 -> Get SoftwareINfo " +
					"\n| 4 -> Install new Software " +
					"\n+-------------------->");
			sc = new Scanner(System.in);
			try{
			input = sc.nextInt();
			}//try
			catch(InputMismatchException ime){
				System.out.println("Please use the menue");
			}//catch
			
			switch (input) {
			case 1:
				try {
					System.out.println(base.getInfo_getClientStatus(clientid));
				} catch (Exception e) {System.out.println("Could not get Client-Status!");}
				break; 
			case 2:
				try {
					System.out.println(base.getInfo_hwInfo(clientid));
				} catch (Exception e) {System.out.println("Could not get Client-Status!");}
				break;
			case 3:
				clientsoftware(sc, base, client, clientid);
				break;
			case 4:
				break;
			case 0:
				break;
			}//switch
		}//while
	}
	
	private void clientsoftware(Scanner sc, Database base, String client, int clientid){
		int input = -1;
		while (input != 0) {
			String[][] soft = new String[0][];
			String output = "+-------------------->" +
							"\n| 0 -> back";
			try {
				String tmp = base.getInfo_swInfo(clientid);
				String[] tmp2 = tmp.split("#");
				soft = new String[tmp2.length][];
				if(tmp2.length == 0)
					output += "\n| No Software installed";
				else{
					for(int i = 1; i < tmp2.length+1; i++)
					{
						soft[i-1] = tmp2[i-1].split(":");
						output += "\n| "+ i + " -> "+ soft[i-1];
					}	
				}
				output += "+-------------------->";
			} catch (Exception e) {
				output += "\n| Could not get Software from Database";
				output += "+-------------------->";
			}
			System.out.println(output);
			try{
				input = sc.nextInt();
				if(soft.length != 0)
				{	
					if(input >= 0+1 && input < soft.length+1)
					{
						softmenue(sc,base, client, clientid, soft[input -1][0]);
						input = -1;
					}else
					{
						System.out.println("## Please insert valid Number ##");
						input = -1;
					}
					
					if(input == 1)
						addclient(base);
				}
				}//try
				catch(InputMismatchException ime){
					System.out.println("Please use the menue");
				}//catch
		}
	}
	private void softmenue(Scanner sc, Database base, String client, int clientid, String software){
		int input = -1;
		while(input != 0)
		{
			try{
				System.out.println(	"+-------------------->" +
									"\n| Client: " + client + " Software: "+ software+
									"\n| 0 -> back" +
									"\n| 1 -> start" +
									"\n| 2 -> stop" +
									"\n| 3 -> restart" +
									"\n+-------------------->");
				input = sc.nextInt();
				switch(input)
				{
				case 0:
					break;
				case 1:
					sendCommand("start", software, client, clientid, base);
					break;
				case 2:
					sendCommand("stop", software, client, clientid, base);
					break;
				case 3:
					sendCommand("restart", software, client, clientid, base);
					break;
				}
			}catch(InputMismatchException ime){
				System.out.println("Please use the menue");
				input = -1;
			}//catch
		}
	}
	
	private void sendCommand(String name, String user, String client, int clientid, Database base){
		Command c = new Command();
		c.setName(name);
		c.setStatus(100);
		c.setUser(user);
		c.setClient(client);
		c.setClientID(clientid);
		c.setFrom(this._config.getIP_own());
		String ip ="none";
		int port = -1;
		try{
			String tmp = base.getClientIP(clientid);
			ip = tmp.split(":")[0];
			port = Integer.parseInt(tmp.split(":")[1]);
		}catch(Exception e){
			System.out.println("No IP from Database!");
		}
		this._com.send(c, ip, port);
	}
	
	private void repomenue(Scanner sc){
		Database base = new Database();
		int input = -1;
		while(input != 0)
		{
		System.out.println(	"+-------------------->" +
							"\n| 0 -> back" +
							"\n| 1 -> list Software" +
							"\n| 2 -> update Repository");
		try{
			input = sc.nextInt();
			switch(input)
			{
			case 0:
				break;
			case 1:
				try{
					String[] soft = base.getInfo_getRepoList().split("#");
					System.out.println("The Repository contains following software:");
					for(String i : soft)
						System.out.println("|| " + i);
					System.out.println(	"++--------------------------" +
										"++--------------------------");
				}catch(Exception e){
					System.out.println("Cannot get Softwarelist from Database!");
				}
				break;
			case 2:
				Command c = new Command();
				c.setName("updaterepolist");
				c.setStatus(101);
				this._task_list.add(c);
				break;
			}
		}catch(InputMismatchException m)
		{
			System.out.println("Please use the Menue!");
			input = -1;
		}
		}
	}
	
	private void stopProgram(){
		this._config.writeConfig();
	}
	private void sendMessage(){
		Command tmp = new Command();
		tmp.setStatus(100);
		tmp.setInfo(getText("Please enter the Info you like to send: "));
		String ip = getText("To IP= ");
		int port = -1;
		boolean confirm = false;
		while(!confirm){
			try{
				port = Integer.parseInt(getText("Port= "));
				confirm = true;
			}catch(Exception e){
				System.out.println("Please try again!");
				confirm = false;}
		}
		this._com.send(tmp, ip, port);
	}
	
	private String getText(String message){
		Scanner sc = new Scanner(System.in);
		try{
			System.out.println(message);
			String tmp = sc.nextLine();
			if(tmp.length() > 0)
				return tmp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "default";
	}
	
	/**
	 * runs the startProgram-method
	 * runs the runDialog-method
	 */
	public void run(){
		startProgram();
		runDialog();
	}//run()
}//class
