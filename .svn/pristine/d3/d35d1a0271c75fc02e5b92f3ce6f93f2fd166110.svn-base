package lullabylandsaga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import lullabylandsaga.ui.LullabyLandSagaMiniGame;

/**
 *
 * @author Thomas Marion
 */
public class BackHandler implements ActionListener{
    
    private LullabyLandSagaMiniGame game;
    
    public BackHandler(LullabyLandSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        game.reset();
        game.switchToSplashScreen();
    }
}
