package kawaiiklash;

import java.awt.Dimension;
import java.awt.Toolkit;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 * The main class from which the game is started.
 *
 * @author Jeff Niu
 */
public class Main {

    public static void main(String[] args) {
        try {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            AppGameContainer appgc = new AppGameContainer(new GameMaster());
            appgc.setDisplayMode(screen.width, screen.height, false);
            appgc.setTargetFrameRate(120);
            //appgc.setFullscreen(true);
            appgc.start();
        } catch (SlickException ex) {
            fail(ex);
        }
    }

}
