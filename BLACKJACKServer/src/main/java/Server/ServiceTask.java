package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.fp.dam.naipes.blackjack.Blackjack;
import org.fp.dam.naipes.blackjack.BlackjackPedirException;
import org.fp.dam.naipes.blackjack.BlackjackPlantarseException;
import org.fp.dam.naipes.blackjack.BlackjackRepartirException;

public class ServiceTask implements Runnable {
	Socket socket;
	Partidas partidas;

	public ServiceTask(Socket socket, Partidas p) throws SocketException {
		socket.setSoTimeout(100000);
		this.socket = socket;
		this.partidas = p;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
			String line;
			System.out.println("(" + socket.getInetAddress() + ")");
			line = in.readLine();
			System.out.println("> " + line);
			peticiones(line, out);
		} catch (SocketTimeoutException e) {
			System.err.println("TIMEOUT: " + e.getLocalizedMessage() + "(" + socket.getInetAddress() + ")");
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getLocalizedMessage() + "(" + socket.getInetAddress() + ")");
		}
	}

	public void peticiones(String peticion, PrintWriter out) {

		if (peticion.startsWith("nueva:")) {
			String hash = partidas.crear();
			enviar(out, "OK#" + hash);
		}

		else if (peticion.startsWith("repartir#")) {
			try {
				String hash = peticion.split("#")[1];
				partidas.get(hash).repartir();
				enviar(out, partidas.get(hash).toString());
			} catch (BlackjackRepartirException e) {
				enviar(out, "ERROR:Error al repartir");
			}
		}

		else if (peticion.startsWith("pedir#")) {
			try {
				String hash = peticion.split("#")[1];
				partidas.get(hash).pedir();
				enviar(out, partidas.get(hash).toString());
			} catch (BlackjackPedirException e) {
				enviar(out, "ERROR:Error al pedir");
			}
		}

		else if (peticion.startsWith("plantarse#")) {
			try {
				String hash = peticion.split("#")[1];
				partidas.get(hash).plantarse();
				enviar(out, partidas.get(hash).toString());
			} catch (BlackjackPlantarseException e) {
				enviar(out, "ERROR:Error al plantarse");
			}
		}

		else if (peticion.startsWith("fin#")) {
			String hash = peticion.split("#")[1];
			partidas.borrar(hash);
			enviar(out, "SE HA DESCONECTADO DEL SERVIDOR");
		} else {
			enviar(out, "Error : Sintaxis Incorrecta");
		
		}

		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getLocalizedMessage() + "(" + socket.getInetAddress() + ")");
		}
	}
	
	private void enviar(PrintWriter out, String msg) {
		System.out.println("< " + msg);
		out.println(msg);
		out.flush();
	}

}