package lullabylandsaga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import lullabylandsaga.data.LullabyLandSagaDataModel;
//import mahjong_solitaire.file.MahjongSolitaireFileManager;
import lullabylandsaga.ui.LullabyLandSagaMiniGame;
import lullabylandsaga.file.LullabyLandSagaFileManager;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * This event handler responds to when the user selects
 * a Mahjong level to play on the splash screen.
 * 
 * @author Richard McKenna
 */
public class GameScreenHandler implements ActionListener
{
    // HERE'S THE GAME WE'LL UPDATE
    private LullabyLandSagaMiniGame game;
    
    
    /**
     * This constructor just stores the game and the level to
     * load for later.
     *     
     * @param initGame The game to update.
     * 
     * @param initLevelFile The level to load when the user requests it. 
     */
    public GameScreenHandler(  LullabyLandSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on a button for selecting a level
     * which is how the user starts a game. Note that the game 
     * data is already locked for this thread before it is called, 
     * and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        String levelFile = "./data/./lullaland/Level5.zom";
        System.out.println("<<<<<<<<<" + levelFile);
        // GET THE GAME'S DATA MODEL, WHICH IS ALREADY LOCKED FOR US
            LullabyLandSagaDataModel data = (LullabyLandSagaDataModel)game.getDataModel();
        
            // UPDATE THE DATA
            LullabyLandSagaFileManager fileManager = game.getFileManager();
            fileManager.loadLevel(levelFile);
            
            game.switchToGameScreen();
        try
        {
            String splashAudio = "./audio/zomjong/ButtonPress.wav";
            InputStream in = new FileInputStream(splashAudio);
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        } catch(FileNotFoundException fnfe){} catch(IOException ioe){}
    }
}