import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server extends Thread {
	private int port;
	private String dir;
	private DatagramSocket sSocket;
	private Client c;

	public Server(String dir) {
		this.dir = dir;
		this.port = 29000;
		c = new Client(dir);
		try {
			sSocket = new DatagramSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			long tempoInicial = System.currentTimeMillis();
			File f = new File(dir);
			File arquivos[] = f.listFiles();

			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			try {
				sSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}

			InetAddress ipOrigem = receivePacket.getAddress();
			byte[] converter = new byte[receivePacket.getLength()];
			System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), converter, 0,
					receivePacket.getLength());
			String data = new String(converter);

			// se receber PTA, enviar a lista de arquivos
			if (data.startsWith("PTA")) {
				String resp = "ETA;";
				for (int i = 0; i < arquivos.length; i++)
					if (arquivos[i].isFile() && !arquivos[i].getName().equalsIgnoreCase("listaIPs.txt"))
						resp += arquivos[i].getName() + ",";
				sendData = resp.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipOrigem, port);
				try {
					sSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Lista de arquivos enviada...");
			}
			// se receber PAE, enviar arquivo especifico
			if (data.startsWith("PAE")) {
				String[] pedido = data.split(";");
				int pos = -1;

				for (int i = 0; i < arquivos.length; i++)
					if (arquivos[i].getName().equalsIgnoreCase(pedido[1]))
						pos = i;

				if (pos >= 0) {
					String send = "EAE;" + arquivos[pos].length() + ";" + arquivos[pos].getName() + ";";
					File arq = new File(arquivos[pos].getAbsolutePath());
					FileReader fr = null;
					try {
						fr = new FileReader(arq);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					BufferedReader bf = new BufferedReader(fr);
					StringBuilder sb = new StringBuilder();
					// ler dados do arquivo
					String str = null;
					try {
						while ((str = bf.readLine()) != null) {
							sb.append(str);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					sendData = (send + (sb.toString())).getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipOrigem, port);
					try {
						sSocket.send(sendPacket);
						bf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Enviado [Arquivo: "+arquivos[pos].getName()+"]");
				}
			}

			// se o cliente enviou PTA, recebe o ETA
			if (data.startsWith("ETA")) {
				System.out.println("Lista de arquivos recebida...");
				String dirServidor = dir + ipOrigem;
				new File(dirServidor).mkdir();
				File f1 = new File(dirServidor);
				File[] arqListados = f1.listFiles();

				String[] array = data.split(";");

				if (array.length > 1) {

					String aux = array[1];
					String[] lista = aux.split(",");

					// arquivos recebidos pelo servidor
					ArrayList<String> arqRecebidos = new ArrayList<>();
					// arquivos que estao na pasta do meu servidor
					ArrayList<String> arqDispServidor = new ArrayList<>();
					// arquivos que estao faltando em um dos lados
					// e precisam ser obtidos ou apagados
					ArrayList<String> arqFaltando = new ArrayList<>();

					for (int i = 0; i < lista.length; i++)
						arqRecebidos.add(lista[i]);

					if (arqListados.length != 0) {
						for (int i = 0; i < arqListados.length; i++)
							arqDispServidor.add(arqListados[i].getName());
						arqFaltando.addAll(arqDispServidor);

						for (String s : arqRecebidos) {
							if (arqFaltando.contains(s)) {
								arqFaltando.remove(s);
							} else {
								arqFaltando.add(s);
							}
						}

						if (arqFaltando.size() > 0) {
							ArrayList<String> deletados = new ArrayList<>();
							for (int i = 0; i < arqFaltando.size(); i++)
								if (!arqRecebidos.contains(arqFaltando.get(i)))
									deletados.add(arqFaltando.get(i));

							for (int i = 0; i < deletados.size(); i++) {
								System.out.println("Excluido [Arquivo: "+ deletados.get(i)+ "]");
								File f2 = new File(dirServidor + File.separatorChar +  deletados.get(i));
								f2.delete();
							}
						}

						for (int i = 0; i < arqFaltando.size(); i++)
							c.PAE(ipOrigem, arqFaltando.get(i));
					} else {
						for (int i = 0; i < arqRecebidos.size(); i++)
							c.PAE(ipOrigem, arqRecebidos.get(i));
					}
				} else {
					if (arqListados.length != 0) {
						for(int i = 0; i < arqListados.length; i++) {
							System.out.println("Excluido [Arquivo: "+ arqListados[i].getName() + "]");
							new File(arqListados[i].getAbsolutePath()).delete();
						}
					}
				}
			}

			if (data.startsWith("EAE")) {
				String dirServidor = dir + ipOrigem;
				String[] recebido = data.split(";");
				String tamanho = recebido[1];
				String nome = recebido[2];

				try {
					BufferedWriter wr = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(dirServidor + File.separatorChar + nome)));
					if (recebido.length == 4)
						wr.write(recebido[3]);
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Baixado [Arquivo: " + nome + " - Tamanho: " + tamanho + "]");
			}
			
			long tempoGasto = System.currentTimeMillis() - tempoInicial;
			System.out.println("Tempo gasto: " + tempoGasto + "ms");
		}
	}
}
