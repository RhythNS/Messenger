package user;

import java.io.File;
import java.util.ArrayList;

public class User {

	private String name;
	private String private_key;
	private ArrayList<Kontakt> kontakte;

	public ArrayList<Kontakt> getKontakte() {
		return kontakte;
	}

	public void sendMessage(String message, Kontakt kontakt) {

	}

	public void sendData(File file, Kontakt kontakt) {

	}

	public void sendMessage(String message, Gruppe gruppe) {

	}

	public void sendData(File file, Gruppe gruppe) {

	}

	public void löschenGruppe(Gruppe gruppe) {

	}

	public boolean gruppeUmbennen(Gruppe gruppe, String neuerName) {
		return false;
	}


}
