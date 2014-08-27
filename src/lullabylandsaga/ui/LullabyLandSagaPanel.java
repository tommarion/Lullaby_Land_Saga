package lullabylandsaga.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JPanel;
import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;
import lullabylandsaga.data.LullabyLandSagaDataModel;
import static lullabylandsaga.LullabyLandSagaConstants.*;
import lullabylandsaga.data.LullabyLandSagaRecord;


/**
 *
 * @author tomma_000
 */
public class LullabyLandSagaPanel extends JPanel
{
    private MiniGame game;
    
    private LullabyLandSagaDataModel data;
    
    // WE'LL USE THIS TO FORMAT SOME TEXT FOR DISPLAY PURPOSES
    private NumberFormat numberFormatter;
 
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING UNSELECTED TILES
    private BufferedImage blankTileImage;
    
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING SELECTED TILES
    private BufferedImage blankTileSelectedImage;
    
    /**
     * This constructor stores the game and data references,
     * which we'll need for rendering.
     * 
     * @param initGame the Mahjong Solitaire game that is using
     * this panel for rendering.
     * 
     * @param initData the Mahjong Solitaire game data.
     */
    public LullabyLandSagaPanel(MiniGame initGame, LullabyLandSagaDataModel initData)
    {
        game = initGame;
        data = initData;
        numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMinimumFractionDigits(3);
        numberFormatter.setMaximumFractionDigits(3);
    }
    
    // MUTATOR METHODS
        // -setBlankTileImage
        // -setBlankTileSelectedImage
    
    /**
     * This mutator method sets the base image to use for rendering tiles.
     * 
     * @param initBlankTileImage The image to use as the base for rendering tiles.
     */
    public void setBlankTileImage(BufferedImage initBlankTileImage)
    {
        blankTileImage = initBlankTileImage;
    }
    
    /**
     * This mutator method sets the base image to use for rendering selected tiles.
     * 
     * @param initBlankTileSelectedImage The image to use as the base for rendering
     * selected tiles.
     */
    public void setBlankTileSelectedImage(BufferedImage initBlankTileSelectedImage)
    {
        blankTileSelectedImage = initBlankTileSelectedImage;
    }

    /**
     * This is where rendering starts. This method is called each frame, and the
     * entire game application is rendered here with the help of a number of
     * helper methods.
     * 
     * @param g The Graphics context for this panel.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        try
        {
            // MAKE SURE WE HAVE EXCLUSIVE ACCESS TO THE GAME DATA
            game.beginUsingData();
        
            // CLEAR THE PANEL
            super.paintComponent(g);
        
            // RENDER THE BACKGROUND, WHICHEVER SCREEN WE'RE ON
            renderBackground(g);

            // AND THE BUTTONS AND DECOR
            renderGUIControls(g);
                
            // AND THE TILES
            renderTiles(g);

            // AND THE DIALOGS, IF THERE ARE ANY
            renderDialogs(g);

            // AND THE TIME AND TILES STATS
            renderStats(g);
                
            // RENDERING THE GRID WHERE ALL THE TILES GO CAN BE HELPFUL
            // DURING DEBUGGIN TO BETTER UNDERSTAND HOW THEY RE LAID OUT
            renderGrid(g);
        
            // AND FINALLY, TEXT FOR DEBUGGING
            renderDebuggingText(g);
        }
        finally
        {
            // RELEASE THE LOCK
            game.endUsingData();    
        }
    }
    
    // RENDERING HELPER METHODS
        // - renderBackground
        // - renderGUIControls
        // - renderTiles
        // - renderDialogs
        // - renderGrid
        // - renderDebuggingText
    
    /**
     * Renders the background image, which is different depending on the screen. 
     * 
     * @param g the Graphics context of this panel.
     */
    public void renderBackground(Graphics g)
    {
        // THERE IS ONLY ONE CURRENTLY SET
        Sprite bg = game.getGUIDecor().get(BACKGROUND_TYPE);
        renderSprite(g, bg);
    }

    /**
     * Renders all the GUI decor and buttons.
     * 
     * @param g this panel's rendering context.
     */
    public void renderGUIControls(Graphics g)
    {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> decorSprites = game.getGUIDecor().values();
        for (Sprite s : decorSprites)
        {
            renderSprite(g, s);
        }
        
        // AND NOW RENDER THE BUTTONS
        Collection<Sprite> buttonSprites = game.getGUIButtons().values();
        for (Sprite s : buttonSprites)
        {
            renderSprite(g, s);
        }
    }

    /**
     * This method renders the on-screen stats that change as
     * the game progresses. This means things like the game time
     * and the number of tiles remaining.
     * 
     * @param g the Graphics context for this panel
     */
    public void renderStats(Graphics g)
    {
        // RENDER THE GAME TIME
        if (((LullabyLandSagaMiniGame)game).isCurrentScreenState(GAME_SCREEN_STATE) 
                && data.inProgress() || data.isPaused())
        {
            Font statsFont = new Font("Verdana", Font.BOLD, 35);
            Color fontColor = new Color(0, 160, 0);
          
            // RENDER THE TIME
            String time = data.gameTimeToText();
            //int x = TIME_X + TIME_OFFSET;
            //int y = TIME_Y + TIME_TEXT_OFFSET;
            g.setFont(statsFont);
            g.setColor(fontColor);
            //g.drawString(time, x, y);
            
            // RENDER THE TILE COUNT
            //x = TILE_COUNT_X + TILE_TEXT_OFFSET;
            //y = TILE_COUNT_Y + TIME_TEXT_OFFSET;
            //g.drawString(Integer.toString( 144 - data.getStackTileCount() ), x, y);
            
            
        }        
    }
        
    /**
     * Renders all the game tiles, doing so carefully such
     * that they are rendered in the proper order.
     * 
     * @param g the Graphics context of this panel.
     */
    public void renderTiles(Graphics g)
    {
        // DRAW THE TOP TILES ON THE STACK
        if (!data.won())
        {
            // WE DRAW ONLY THE TOP 4 (OR 2 IF THERE ARE ONLY 2). THE REASON
            // WE DRAW 4 IS THAT WHILE WE MOVE MATCHES TO THE STACK WE WANT
            // TO SEE THE STACK
            ArrayList<LullabyLandSagaTile> stackTiles = data.getStackTiles();
            if (stackTiles.size() > 3)
            {
                renderTile(g, stackTiles.get(stackTiles.size()-3));
                renderTile(g, stackTiles.get(stackTiles.size()-4));
            }
            if (stackTiles.size() > 1)
            {
                renderTile(g, stackTiles.get(stackTiles.size()-1));
                renderTile(g, stackTiles.get(stackTiles.size()-2));
            }
        }
        
        // THEN DRAW THE GRID TILES BOTTOM TO TOP USING
        // THE TILE'S Z TO STAGGER THEM AND GIVE THE ILLUSION
        // OF DEPTH
        ArrayList<LullabyLandSagaTile>[][] tileGrid = data.getTileGrid();
        boolean noneOnLevel = false;
        int zIndex = 0;
        while (!noneOnLevel)
        {
            int levelCounter = 0;
            for (int i = 0; i < data.getGridColumns(); i++)
            {
                for (int j = 0; j < data.getGridRows(); j++)
                {
                    if (tileGrid[i][j].size() > zIndex)
                    {
                        LullabyLandSagaTile tile2;
                            LullabyLandSagaTile tile = tileGrid[i][j].get(zIndex);
                            renderTile(g, tile);
                            if(zIndex>0)
                            {
                                tile2 = tileGrid[i][j].get(zIndex);
                                tile2.setState(CLOUD_STATE);
                                tile2.setEnabled(false);
                            }
                        levelCounter++;
                    }
                }
            }
            if (levelCounter == 0)
                noneOnLevel = true;
            zIndex++;
        }
        
        while(data.matchOnGrid() != null)
        {
            System.out.println("P260 - GRID RESHUFFLE");
            data.reset(this.game);
        }
        
        // THEN DRAW ALL THE MOVING TILES
        Iterator<LullabyLandSagaTile> movingTiles = data.getMovingTiles();
        while (movingTiles.hasNext())
        {
            LullabyLandSagaTile tile = movingTiles.next();
            renderTile(g, tile);
        }
        
        // MAKE THE REST OF THE TILES IN THE STACK INVISIBLE
        ArrayList<LullabyLandSagaTile>stackTiles = data.getStackTiles();
        for(LullabyLandSagaTile tile : stackTiles)
        {
            tile.setState(INVISIBLE_STATE);
            tile.setEnabled(false);
        }
    }

    /**
     * Helper method for rendering the tiles that are currently moving.
     * 
     * @param g Rendering context for this panel.
     * 
     * @param tileToRender Tile to render to this panel.
     */
    public void renderTile(Graphics g, LullabyLandSagaTile tileToRender)
    {
        // ONLY RENDER VISIBLE TILES
        if (!tileToRender.getState().equals(INVISIBLE_STATE))
        {
            // FIRST DRAW THE BLANK TILE IMAGE
            if (tileToRender.getState().equals(SELECTED_STATE))
                g.drawImage(blankTileSelectedImage, (int)tileToRender.getX(), (int)tileToRender.getY(), null);
            else if (tileToRender.getState().equals(VISIBLE_STATE))
                g.drawImage(blankTileImage, (int)tileToRender.getX(), (int)tileToRender.getY(), null);
            
            // THEN THE TILE IMAGE
            SpriteType bgST = tileToRender.getSpriteType();
            Image img = bgST.getStateImage(tileToRender.getState());
            g.drawImage(img, (int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(), null); 
            // IF THE TILE IS SELECTED, HIGHLIGHT IT
            switch (tileToRender.getState()) {
                case SELECTED_STATE:
                    g.setColor(SELECTED_TILE_COLOR);
                    g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
                    break;
                case ROW_CLEAR_STATE:
                    g.setColor(ROW_CLEAR_TILE_COLOR);
                    g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
                    break;
                case COL_CLEAR_STATE:
                    g.setColor(COL_CLEAR_TILE_COLOR);
                    g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
                    break;
                case WRAPPED_STATE:
                    g.setColor(WRAPPED_TILE_COLOR);
                    g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
                    break;
                case SPECIAL_SELECTED_STATE:
                    g.setColor(SPECIAL_SELECTED_COLOR);
                    g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
                    break;
                case CLOUD_STATE:
                    g.setColor(CLOUD_COLOR);
                    g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
                    break;
            }
        }        
    }
    
    /**
     * Renders the game dialog boxes.
     * 
     * @param g This panel's graphics context.
     */
    public void renderDialogs(Graphics g)
    {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> dialogSprites = game.getGUIDialogs().values();
        for (Sprite s : dialogSprites)
        {
            // RENDER THE DIALOG, NOTE IT WILL ONLY DO IT IF IT'S VISIBLE
            renderSprite(g, s);
        }
    }
    
    /**
     * Renders the s Sprite into the Graphics context g. Note
     * that each Sprite knows its own x,y coordinate location.
     * 
     * @param g the Graphics context of this panel
     * 
     * @param s the Sprite to be rendered
     */
    public void renderSprite(Graphics g, Sprite s)
    {
        // ONLY RENDER THE VISIBLE ONES
        if (!s.getState().equals(INVISIBLE_STATE))
        {
            SpriteType bgST = s.getSpriteType();
            Image img = bgST.getStateImage(s.getState());
            g.drawImage(img, (int)s.getX(), (int)s.getY(), bgST.getWidth(), bgST.getHeight(), null); 
        }
    }

    /**
     * This method renders grid lines in the game tile grid to help
     * during debugging.
     * 
     * @param g Graphics context for this panel.
     */
    public void renderGrid(Graphics g)
    {
        // ONLY RENDER THE GRID IF WE'RE DEBUGGING
        if (data.isDebugTextRenderingActive())
        {
            for (int i = 0; i < data.getGridColumns(); i++)
            {
                for (int j = 0; j < data.getGridRows(); j++)
                {
                    int x = data.calculateTileXInGrid(i, 0);
                    int y = data.calculateTileYInGrid(j, 0);
                    g.drawRect(x, y, TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT);
                }
            }
        }
    }
    
    /**
     * Renders the debugging text to the panel. Note
     * that the rendering will only actually be done
     * if data has activated debug text rendering.
     * 
     * @param g the Graphics context for this panel
     */
    public void renderDebuggingText(Graphics g)
    {
        // IF IT'S ACTIVATED
        if (data.isDebugTextRenderingActive())
        {
            // ENABLE PROPER RENDER SETTINGS
            g.setFont(DEBUG_TEXT_FONT);
            g.setColor(DEBUG_TEXT_COLOR);
            
            // GO THROUGH ALL THE DEBUG TEXT
            Iterator<String> it = data.getDebugText().iterator();
            int x = data.getDebugTextX();
            int y = data.getDebugTextY();
            while (it.hasNext())
            {
                // RENDER THE TEXT
                String text = it.next();
                g.drawString(text, x, y);
                y += 20;
            }   
        } 
    }
}
