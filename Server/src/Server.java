import java.net.*;
import java.util.*;
import java.io.*;

public class Server {

	ServerSocket ss = null;
	public static Map<String,Socket> clients;
	Server()
	{
		//����ȭ ó���� �����ϵ���  Map ��ü ���� => ���� �����尡 �ѹ��� ���鼭 ��ġ�� �� ����
		clients = Collections.synchronizedMap(new HashMap<>()); //
	}
	
	
	public static void main(String [] args)
	{
		Server server = new Server();
		Socket socket = null;
				
		try
		{
			server.ss = new ServerSocket(55555); // ���� ���� ����
			System.out.println("���� ������ �����Ͽ����ϴ�.");
			
			while(true)
			{
				socket = server.ss.accept(); // ���ο� ������ ������ �������
				String name = null;
				ClientHandle c = new ClientHandle(socket); 
				c.start();
			}
		}
		catch(SocketException e)
		{
			System.out.println("Server > ���� ���� ���� �߻�, ��������");
		}
		catch(IOException e)
		{
			System.out.println("Server > ����� ���� �߻�");
		}
		
	}

	
	
	
}

