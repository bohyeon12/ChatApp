import java.net.*;
import java.util.*;
import java.io.*;

public class Server {

	ServerSocket ss = null;
	public static Map<String,Socket> clients;
	Server()
	{
		//동기화 처리가 가능하도록  Map 객체 생성 => 여러 쓰레드가 한번에 오면서 겹치는 것 방지
		clients = Collections.synchronizedMap(new HashMap<>()); //
	}
	
	
	public static void main(String [] args)
	{
		Server server = new Server();
		Socket socket = null;
				
		try
		{
			server.ss = new ServerSocket(55555); // 서버 소켓 생성
			System.out.println("서버 생성에 성공하였습니다.");
			
			while(true)
			{
				socket = server.ss.accept(); // 새로운 소켓이 들어오면 허락해줌
				String name = null;
				ClientHandle c = new ClientHandle(socket); 
				c.start();
			}
		}
		catch(SocketException e)
		{
			System.out.println("Server > 소켓 관련 예외 발생, 서버종료");
		}
		catch(IOException e)
		{
			System.out.println("Server > 입출력 예외 발생");
		}
		
	}

	
	
	
}

