import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class Parameters {
	
	public static final int port = 2126;
	public static final String SERVER = "70.176.84.115";
	public static final String PASSWORD = "6972126";
	public static final double VERSION_ID = 1.160;
	public static ImageIcon APP_ICON;
	public static final String rootDir = System.getProperty("user.home") + File.separator + "CaskChat" + File.separator;
	
	public static void load() {
		try {
			APP_ICON = new ImageIcon(ImageIO.read(new File(rootDir + "icon.png")));
		} catch (IOException e) {
			System.out.println("Error reading icon image file");
			e.printStackTrace();
		}
	}
	
}
