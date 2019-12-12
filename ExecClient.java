import java.util.Timer;
import java.util.TimerTask;

public class ExecClient {
	public static void main(String[] args) {
		String dir = "C:\\Users\\artur\\Documents\\shared\\";
		Client client = new Client(dir);
		// incio sem atraso e atualiza��o a cada 6 segundos
		int delay = 0;
		int interval = 5000;
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				try {
					client.PTA();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		timer.schedule(task, delay, interval);
	}
}
