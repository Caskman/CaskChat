import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Launcher {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.load();
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		new ChatMenu();
	}

}
