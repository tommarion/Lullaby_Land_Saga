package lullabylandsaga.events;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static lullabylandsaga.LullabyLandSagaConstants.GAME_SCREEN_STATE;
import lullabylandsaga.data.LullabyLandSagaDataModel;
import lullabylandsaga.data.LullabyLandSagaMove;
import lullabylandsaga.ui.LullabyLandSagaMiniGame;
import lullabylandsaga.ui.LullabyLandSagaTile;
import mini_game.MiniGameDataModel;

/**
 * This event handler lets us provide additional custom responses
 * to key presses while Mahjong is running.
 * 
 * @author Richard McKenna, Thomas Marion
 */
public class LullabyLandKeyHandler extends KeyAdapter
{
    // THE MAHJONG GAME ON WHICH WE'LL RESPOND
    private LullabyLandSagaMiniGame game;

    /**
     * This constructor simply inits the object by 
     * keeping the game for later.
     * 
     * @param initGame The Mahjong game that contains
     * the back button.
     */    
    public LullabyLandKeyHandler(LullabyLandSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * This method provides a custom game response to when the user
     * presses a keyboard key.
     * 
     * @param ke Event object containing information about the event,
     * like which key was pressed.
     */
    @Override
    public void keyPressed(KeyEvent ke)
    {
        // CHEAT BY ONE MOVE. NOTE THAT IF WE HOLD THE C
        // KEY DOWN IT WILL CONTINUALLY CHEAT
        if (ke.getKeyCode() == KeyEvent.VK_C)
        {
            LullabyLandSagaDataModel data = (LullabyLandSagaDataModel)game.getDataModel();
            ArrayList<LullabyLandSagaTile> stack[][] = data.getTileGrid();
            
            if(data.areTilesMoving())
            {
                return;
            }
            // FIND A MOVE IF THERE IS ONE
            LullabyLandSagaMove move = data.moveOnGrid();
            if (move != null)
                try {
                data.processMove(move, data.isValidMove(stack[move.col1][move.row1].get(0), stack[move.col2][move.row2].get(0)));
            } catch (InterruptedException ex) {
                Logger.getLogger(LullabyLandKeyHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //PRESSING U DOES UNDO
        if(ke.getKeyCode() == KeyEvent.VK_U)
        {
            
            LullabyLandSagaDataModel data = (LullabyLandSagaDataModel)game.getDataModel();
            data.undoLastMove();
            
        }
        
        //PRESSING R RESHUFFLES THE GRID
        if(ke.getKeyCode() == KeyEvent.VK_R)
        {
            for(int i=0; i<10; i++)
            {
                LullabyLandSagaDataModel data = (LullabyLandSagaDataModel)game.getDataModel();
                data.reset(game);
                data.updateAll(game);
            }
        }
        //PRESSING L LOSES THE GAME
        if(ke.getKeyCode() == KeyEvent.VK_L)
        {
            
                LullabyLandSagaDataModel data = (LullabyLandSagaDataModel)game.getDataModel();
                if(game.isCurrentScreenState(GAME_SCREEN_STATE))
                {
                    data.endGameAsLoss();
                }
            
        }
        //PRESSING W ENDS GAME AS WIN
        if(ke.getKeyCode() == KeyEvent.VK_W)
        {
            for(int i=0; i<10; i++)
            {
                LullabyLandSagaDataModel data = (LullabyLandSagaDataModel)game.getDataModel();
                if(game.isCurrentScreenState(GAME_SCREEN_STATE))
                {
                    data.endGameAsWin();
                }
            }
        }
    }
}