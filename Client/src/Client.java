import java.net.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;

public class Client {
	Socket mySocket = null;
	String CID;
	public static void main(String[] args)
	{
		Client client = new Client();
		MessageListener msgListener = null;
		MessageSender msgSender = null;
		OutputStream outStream = null;
		DataOutputStream dataOutStream = null;
		Scanner scan = new Scanner(System.in);
		
		try
		{
		System.out.print("CID �� �Է����ּ���: ");
		client.CID = scan.nextLine();
		System.out.println();
		client.mySocket = new Socket("localhost",55555);
		System.out.println("Client�� ������ ����Ǿ����ϴ�.");
		
		msgListener = new MessageListener(client.mySocket);
		msgListener.start();
		
		msgSender = new MessageSender(client.mySocket, client.CID);
		msgSender.start();
		
		}
		catch(Exception e)
		{
			System.out.println("�������� ���ῡ �����Ͽ����ϴ�.");
		}
	}
	
	
}

class MessageSender extends Thread          // �޽����� ������ ������
{
	Socket socket;
	OutputStream outStream;
	DataOutputStream dataOutStream;
	String cid;
	Scanner scan = new Scanner(System.in);
	
	MessageSender(Socket _s, String _cid) 
	{
		this.socket = _s;
		this.cid = _cid;
	}
	public void run()
	{
	try
	{
		
		outStream = this.socket.getOutputStream();
		dataOutStream = new DataOutputStream(outStream);
		String RequestMessage = null;
		boolean b = true; // while�� �����Ű�� �뵵�� ����� boolean�� ���� ����
		int count = 1; //Num_Req ǥ�ÿ� int�� ���� ����
		while(b)
		{
			String msg = null;
			String send = null;
			String Whisper = null;
			System.out.println("�޼����� �Է��ϼ��� : ");
			msg = scan.nextLine();
			if(msg.equals("ChatAll"))
			{
				System.out.print("ä���Է� : ");
				send = scan.nextLine();
				RequestMessage = "RequestMessage" + "///" + msg + "///" + cid + "///" + "Num_Req : " + count + "///" +"Chat_CID"+ "///"+ send +"///" + "END_Req";
			}
			else if(msg.equals("Whisper"))
			{
				System.out.println("����Է�: ");
				Whisper = scan.nextLine();
				System.out.print("ä���Է� : ");
				send = scan.nextLine();
				RequestMessage = "RequestMessage" + "///" + msg + "///" + cid + "///" + "Num_Req : " + count + "///" + Whisper + "///"+ send +"///" + "END_Req";
			}
			else
			{
				RequestMessage = "RequestMessage" + "///" + msg + "///" + cid + "///" + "Num_Req : " + count + "///" +"Chat_CID"+ "///"+ send +"///" + "END_Req";
			}
			dataOutStream.writeUTF(RequestMessage);
			count++;
			if(msg.equals("Quit")) //Quit �� �Է��ϸ� while�� ����
				b = false;
			
			Thread.sleep(100);
		}
	}
	catch(Exception e)
	{

	}
 }
}
class MessageListener extends Thread // �޽����� �޴� ������
{
	Socket socket;
	InputStream inStream;
	DataInputStream dataInStream;
	

	MessageListener(Socket _s)
	{
		this.socket = _s;
	}

	public void run()
	{

		try
		{
			inStream = this.socket.getInputStream();
			dataInStream = new DataInputStream(inStream);
			
	
			while(true)
			{
				String msg = dataInStream.readUTF(); //Ŭ���̾�Ʈ�� ����Ǳ� ������ �׻� �޽����� ���޹��� �غ� �Ѵ�.
				StringTokenizer st = new StringTokenizer(msg,"///"); //�޾ƿ� ���ڿ� �ɰ���
				String[] arr = new String[st.countTokens()];
				int i = 0;
				while(st.hasMoreTokens())
				{
					arr[i] = st.nextToken();
					i++;	
				}
				if(arr[0].equals("ReponseMessage")) //�Ϲ����� ��������
				{
					System.out.println(arr[2]);
				}
				else {System.out.println(msg);} // Chat Listener message
				
			}
		}
		catch(Exception e)
		{
	
		}
	}
}

