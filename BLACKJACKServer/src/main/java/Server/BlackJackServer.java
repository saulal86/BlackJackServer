package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackJackServer {
	
	public static void main(String[] args) throws IOException {
		ExecutorService es = Executors.newFixedThreadPool(100);
		ServerSocket ss = new ServerSocket(9999);
		System.out.println("BlackJackServer listening on port 9999");
		Partidas p = new Partidas();
		while (true) {
			es.submit(new ServiceTask(ss.accept(),p));
		}
	}
}