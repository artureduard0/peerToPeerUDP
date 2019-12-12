import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
	private int port;
	private String dir;
	private DatagramSocket cSocket;
	private ArrayList<String> ips;
	
	public Client(String dir) {
		this.port = 29000;
		this.dir = dir;
		this.ips = new ArrayList<>();
		try {
			cSocket = new DatagramSocket();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void carregarIps() {
		String[] splitted = null;
		try {
			FileInputStream fstream = new FileInputStream(dir + File.separatorChar + "listaIPs.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				splitted = strLine.split(",");
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < splitted.length; i++)
			if(!ips.contains(splitted[i]))
				ips.add(splitted[i]);
	}
	
	//PTA
	public void PTA(){
		this.carregarIps();
		for(int i = 0; i < ips.size(); i++) {
			InetAddress ip = null;
			try {
				ip = InetAddress.getByName(ips.get(i));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			byte[] sendData = new byte[1024];
			// pede ao servidor a lista de seus arquivos
			String pedido = "PTA";
			sendData = pedido.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
			try {
				cSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//PAE
	public void PAE(InetAddress ip,String nomeArq) {
		//pede o arquivo ao servidor do IP indicado
		byte[] sendData = new byte[1024];
		String pedido = "PAE;"+nomeArq;
		sendData = pedido.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
		try {
			cSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
