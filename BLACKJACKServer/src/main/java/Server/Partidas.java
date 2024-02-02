package Server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.fp.dam.naipes.blackjack.Blackjack;

public class Partidas {

	private Map<String, Blackjack> partidas = new HashMap<>();
	
	public synchronized String crear() {
		String uuid = UUID.randomUUID().toString();
		partidas.put(uuid, new Blackjack());
		return uuid;
	}
	
	public synchronized Blackjack get(String hash) {
		return partidas.get(hash);
	}
	
	public synchronized void borrar(String hash) {
		partidas.remove(hash);
	}
}