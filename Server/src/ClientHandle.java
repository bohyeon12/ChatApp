import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ClientHandle extends Thread{
	Socket socket;
	Server server = new Server();
	String ip;
	String cid;
	String Chat;
	public static List<String> keyList = new ArrayList<>(); // ClientList�� keyList
	StringTokenizer st;
	ClientHandle(Socket _socket)
	{
		this.socket = _socket;
	}

	public void run()
	{
		try
		{
			OutputStream out = socket.getOutputStream();
			DataOutputStream dout = new DataOutputStream(out);
			InputStream in = socket.getInputStream();
			DataInputStream din = new DataInputStream(in);
			
			LocalTime startTime = LocalTime.now(); //���� �ð� ����
			
			while(true)
			{
				
				String rsp = "";
				String rMsg = din.readUTF(); // Ŭ���̾�Ʈ���� ���� ���ڿ��� �о����
				st = new StringTokenizer(rMsg,"///",false); // ���������� "///" ���ڷ� �����Ͽ� �ɰ�
				/*String [] array = new String[st.countTokens()]; // st�� ��ū ������ŭ ���ڿ� �迭���� ����
				for(int i=0;i<st.countTokens();i++) //��ū���� �迭�� ����
				{
					array[i] = st.nextToken();                                     Chat ��ü�� ���� �ȵ�;;
				}*/
				String RequestMessage =st.nextToken();
				String msg =st.nextToken();
				String Cid =st.nextToken();
				String Num_Req =st.nextToken();
				String Chat_CID =st.nextToken();
				String send =st.nextToken();
				String END_Req =st.nextToken();
				
				if(msg.equals("Hello")) // �ش� ��ɾ �ԷµǸ� ArrayList�� cid ���� 
				{
					rsp = "ReponseMessage" + "///" + "100" + "///" +"CID�� �����߽��ϴ�."+"///" + "END_MSG";   //�������� ����
					cid = Cid;
					server.clients.put(cid, socket);
					keyList.add(cid);
					dout.writeUTF(rsp);
				}
				else if(msg.equals("Clock"))
				{
					LocalTime thisTime = LocalTime.now();
					String strtime = thisTime.format(DateTimeFormatter.ofPattern("HH�� mm�� ss��"));
					rsp = "ReponseMessage" + "///" + "110" + "///" + strtime + "///" + "END_MSG";
					dout.writeUTF(rsp);
				}
				else if(msg.equals("ConnectionTime"))
				{
					LocalTime thisTime = LocalTime.now(); //��ɾ �Էµ� �� ����ð� ����
					long until = startTime.until(thisTime, ChronoUnit.SECONDS); //starttime�� thistime�� �ð���ŭ �� ���  
					String setime = Long.toString(until); //���ڿ��� ġȯ
					rsp = "ReponseMessage" + "///" + "180" + "///" + setime + "��" + "///" + "END_MSG";
					dout.writeUTF(rsp);
					
				}
				else if(msg.equals("ClientList")) // cid ��� ���
				{
					rsp = "ReponseMessage" + "///" + "300" + "///" + "ClientList :" + keyList.toString() + "///" + "END_MSG";
					dout.writeUTF(rsp);
				}
				else if(msg.equals("Quit")) //Ŭ���̾�Ʈ���� ���� ���� , ArrayList���� cid ����
				{
					rsp = "ReponseMessage" + "///" + "110" + "///" + "Ŭ���̾�Ʈ ����" + "///" + "END_MSG";
					dout.writeUTF(rsp); 
					keyList.remove(cid);
					socket.close(); // �ش� ���� ������ ����
				}
				else if(msg.equals("ChatAll"))
				{
					Chat = send;
					//System.out.println(array.length);
					rsp = "ReponseMessage" + "///" + "400" + "///" + Chat + "///" + "END_MSG";
					dout.writeUTF(rsp);
					sendMessage(Chat ,cid);
				}
				else if(msg.equals("Whisper"))
				{
					Chat = send;
					rsp = "ReponseMessage" + "///" + "450" + "///" + Chat + "///" + "END_MSG";
					dout.writeUTF(rsp);
					sendMessage(Chat, cid, Chat_CID);
				}
				else //�ٸ� ��ɾ �Է��ҽ� rsp�� �ٲ�
				{
					rsp = "ReponseMessage" + "///" + "500" + "///" + "��ɾ ����� �Է����ּ���." + "///" + "END_MSG";
					dout.writeUTF(rsp);
				}
				
			}
		}
		catch (SocketException e)
		{
			System.out.println("Ŭ���̾�Ʈ���� ������ ����Ǿ����ϴ�.");
		}
		catch(Exception e)
		{
			System.out.println("���ܰ� �߻��߽��ϴ�.");
		}
	}
	public void sendMessage(String msg,String from) {
		//Map�� ����� ������ ��ȭ�� ����Ʈ�� ����(key�� ���ϱ�)
		Iterator<String> it =server.clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next();//��ȭ�� (key�� ���ϱ�)
				
				//��ȭ�� �ش��ϴ� Socket�� OutputStream��ü ���ϱ�
				DataOutputStream out = new DataOutputStream(server.clients.get(name).getOutputStream());
				out.writeUTF("["+from+"]"+msg);//�޽��� ������
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void sendMessage(String msg,String from, String whisper) {
		//Map�� ����� ������ ��ȭ�� ����Ʈ�� ����(key�� ���ϱ�)
		Iterator<String> it =server.clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next();//��ȭ�� (key�� ���ϱ�)
				boolean chk = false;
				//��ȭ�� �ش��ϴ� Socket�� OutputStream��ü ���ϱ�
				
				
				if(name.equals(whisper)) {
					chk=true;
				}
				if(chk==true) {
				DataOutputStream out = new DataOutputStream(server.clients.get(whisper).getOutputStream());
				out.writeUTF("["+from+"->"+whisper+"] "+msg);//�޽��� ������
				}
		
			
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
