package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import tags.Decode;
import tags.Tags;

public class ClientServer {
	private String username = "";
	private ServerSocket serverPeer;
//	private ServerSocket serverPeer;
	private int port;
	private boolean isStop = false;

	public void stopServerPeer() {
		isStop = true;
	}

	public boolean getStop() {
		return isStop;
	}
	public ClientServer(String name) throws Exception {
		// TODO Auto-generated constructor stub
		username = name;
		port = Client.getPort();
		serverPeer = new ServerSocket(port);
		(new WaitPeerConnect()).start();
	}
	public void exit() throws IOException {
		isStop = true;
//		 Đóng đối tượng ServerSocket serverPeer để ngừng lắng nghe các kết nối từ máy khách.
		serverPeer.close();
	}
	class WaitPeerConnect extends Thread {

		Socket connection;
//		Dọc dữ liệu từ luồng đầu vào của kết nối.
		ObjectInputStream getRequest;

		@Override
		public void run() {
			super.run();
//			liên tục chờ và xử lý các kết nối từ máy khách. Vòng lặp sẽ tiếp tục thực thi cho đến khi biến isStop được đặt thành true.
			while (!isStop) {
				try {
					connection = serverPeer.accept();
//					 tạo một đối tượng ObjectInputStream để đọc dữ liệu từ luồng đầu vào của kết nối connection.
					getRequest = new ObjectInputStream(connection.getInputStream());
					String msg = (String) getRequest.readObject();
					String name = Decode.getNameRequestChat(msg);
					int res = MainFrame.request("Account: " + name + " want to connect with you !", true);
					ObjectOutputStream send = new ObjectOutputStream(connection.getOutputStream());
					if (res == 1) {
						send.writeObject(Tags.CHAT_DENY_TAG);

					} else if (res == 0) {
						send.writeObject(Tags.CHAT_ACCEPT_TAG);
						new ChatFrame(username, name, connection, port);
					}
					send.flush();
				} catch (Exception e) {
					break;
				}
			}
			try {
				serverPeer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

}
