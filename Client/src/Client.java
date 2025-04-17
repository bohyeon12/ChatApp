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
		System.out.print("CID 를 입력해주세요: ");
		client.CID = scan.nextLine();
		System.out.println();
		client.mySocket = new Socket("localhost",55555);
		System.out.println("Client가 서버에 연결되었습니다.");
		
		msgListener = new MessageListener(client.mySocket);
		msgListener.start();
		
		msgSender = new MessageSender(client.mySocket, client.CID);
		msgSender.start();
		
		}
		catch(Exception e)
		{
			System.out.println("서버와의 연결에 실패하였습니다.");
		}
	}
	
	
}

class MessageSender extends Thread          // 메시지를 보내는 쓰레드
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
		boolean b = true; // while을 종료시키는 용도로 사용할 boolean형 변수 선언
		int count = 1; //Num_Req 표시용 int형 변수 선언
		while(b)
		{
			String msg = null;
			String send = null;
			String Whisper = null;
			System.out.println("메세지를 입력하세요 : ");
			msg = scan.nextLine();
			if(msg.equals("ChatAll"))
			{
				System.out.print("채팅입력 : ");
				send = scan.nextLine();
				RequestMessage = "RequestMessage" + "///" + msg + "///" + cid + "///" + "Num_Req : " + count + "///" +"Chat_CID"+ "///"+ send +"///" + "END_Req";
			}
			else if(msg.equals("Whisper"))
			{
				System.out.println("상대입력: ");
				Whisper = scan.nextLine();
				System.out.print("채팅입력 : ");
				send = scan.nextLine();
				RequestMessage = "RequestMessage" + "///" + msg + "///" + cid + "///" + "Num_Req : " + count + "///" + Whisper + "///"+ send +"///" + "END_Req";
			}
			else
			{
				RequestMessage = "RequestMessage" + "///" + msg + "///" + cid + "///" + "Num_Req : " + count + "///" +"Chat_CID"+ "///"+ send +"///" + "END_Req";
			}
			dataOutStream.writeUTF(RequestMessage);
			count++;
			if(msg.equals("Quit")) //Quit 을 입력하면 while문 종료
				b = false;
			
			Thread.sleep(100);
		}
	}
	catch(Exception e)
	{

	}
 }
}
class MessageListener extends Thread // 메시지를 받는 쓰레드
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
				String msg = dataInStream.readUTF(); //클라이언트는 종료되기 전까지 항상 메시지를 전달받을 준비를 한다.
				StringTokenizer st = new StringTokenizer(msg,"///"); //받아온 문자열 쪼개기
				String[] arr = new String[st.countTokens()];
				int i = 0;
				while(st.hasMoreTokens())
				{
					arr[i] = st.nextToken();
					i++;	
				}
				if(arr[0].equals("ReponseMessage")) //일반적인 프로토콜
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

