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
	public static List<String> keyList = new ArrayList<>(); // ClientList용 keyList
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
			
			LocalTime startTime = LocalTime.now(); //시작 시간 저장
			
			while(true)
			{
				
				String rsp = "";
				String rMsg = din.readUTF(); // 클라이언트에서 보낸 문자열을 읽어들임
				st = new StringTokenizer(rMsg,"///",false); // 프로토콜을 "///" 인자로 구분하여 쪼갬
				/*String [] array = new String[st.countTokens()]; // st의 토큰 갯수만큼 문자열 배열길이 설정
				for(int i=0;i<st.countTokens();i++) //토큰들을 배열에 저장
				{
					array[i] = st.nextToken();                                     Chat 객체에 값이 안들어감;;
				}*/
				String RequestMessage =st.nextToken();
				String msg =st.nextToken();
				String Cid =st.nextToken();
				String Num_Req =st.nextToken();
				String Chat_CID =st.nextToken();
				String send =st.nextToken();
				String END_Req =st.nextToken();
				
				if(msg.equals("Hello")) // 해당 명령어가 입력되면 ArrayList에 cid 저장 
				{
					rsp = "ReponseMessage" + "///" + "100" + "///" +"CID를 저장했습니다."+"///" + "END_MSG";   //프로토콜 설정
					cid = Cid;
					server.clients.put(cid, socket);
					keyList.add(cid);
					dout.writeUTF(rsp);
				}
				else if(msg.equals("Clock"))
				{
					LocalTime thisTime = LocalTime.now();
					String strtime = thisTime.format(DateTimeFormatter.ofPattern("HH시 mm분 ss초"));
					rsp = "ReponseMessage" + "///" + "110" + "///" + strtime + "///" + "END_MSG";
					dout.writeUTF(rsp);
				}
				else if(msg.equals("ConnectionTime"))
				{
					LocalTime thisTime = LocalTime.now(); //명령어가 입력될 때 현재시간 저장
					long until = startTime.until(thisTime, ChronoUnit.SECONDS); //starttime과 thistime의 시간만큼 초 계산  
					String setime = Long.toString(until); //문자열로 치환
					rsp = "ReponseMessage" + "///" + "180" + "///" + setime + "초" + "///" + "END_MSG";
					dout.writeUTF(rsp);
					
				}
				else if(msg.equals("ClientList")) // cid 목록 출력
				{
					rsp = "ReponseMessage" + "///" + "300" + "///" + "ClientList :" + keyList.toString() + "///" + "END_MSG";
					dout.writeUTF(rsp);
				}
				else if(msg.equals("Quit")) //클라이언트와의 연결 종료 , ArrayList에서 cid 삭제
				{
					rsp = "ReponseMessage" + "///" + "110" + "///" + "클라이언트 종료" + "///" + "END_MSG";
					dout.writeUTF(rsp); 
					keyList.remove(cid);
					socket.close(); // 해당 소켓 스레드 종료
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
				else //다른 명령어를 입력할시 rsp가 바뀜
				{
					rsp = "ReponseMessage" + "///" + "500" + "///" + "명령어를 제대로 입력해주세요." + "///" + "END_MSG";
					dout.writeUTF(rsp);
				}
				
			}
		}
		catch (SocketException e)
		{
			System.out.println("클라이언트와의 연결이 종료되었습니다.");
		}
		catch(Exception e)
		{
			System.out.println("예외가 발생했습니다.");
		}
	}
	public void sendMessage(String msg,String from) {
		//Map에 저장된 유저의 대화명 리스트를 추출(key값 구하기)
		Iterator<String> it =server.clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next();//대화명 (key값 구하기)
				
				//대화명에 해당하는 Socket의 OutputStream객체 구하기
				DataOutputStream out = new DataOutputStream(server.clients.get(name).getOutputStream());
				out.writeUTF("["+from+"]"+msg);//메시지 보내기
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void sendMessage(String msg,String from, String whisper) {
		//Map에 저장된 유저의 대화명 리스트를 추출(key값 구하기)
		Iterator<String> it =server.clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next();//대화명 (key값 구하기)
				boolean chk = false;
				//대화명에 해당하는 Socket의 OutputStream객체 구하기
				
				
				if(name.equals(whisper)) {
					chk=true;
				}
				if(chk==true) {
				DataOutputStream out = new DataOutputStream(server.clients.get(whisper).getOutputStream());
				out.writeUTF("["+from+"->"+whisper+"] "+msg);//메시지 보내기
				}
		
			
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
