
public class ExecServer {
	public static void main(String[] args) {
		String dir = "C:\\Users\\artur\\Documents\\shared\\";
		Server server = new Server(dir);
		server.run();
	}
}