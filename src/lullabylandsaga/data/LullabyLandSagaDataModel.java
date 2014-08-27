package lullabylandsaga.data;

import lullabylandsaga.ui.LullabyLandSagaTile;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import lullabylandsaga.LullabyLandSaga.LullabyLandSagaPropertyType;
import mini_game.MiniGame;
import mini_game.MiniGameDataModel;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import static lullabylandsaga.LullabyLandSagaConstants.*;
import lullabylandsaga.ui.LullabyLandSagaMiniGame;
import lullabylandsaga.ui.LullabyLandSagaPanel;

/**
 * This class manages the game data for Mahjong Solitaire. 
 * 
 * @author Richard McKenna, Thomas Marion
 */
public class LullabyLandSagaDataModel extends MiniGameDataModel
{
    // THIS CLASS HAS A REFERERENCE TO THE MINI GAME SO THAT IT
    // CAN NOTIFY IT TO UPDATE THE DISPLAY WHEN THE DATA MODEL CHANGES
    private MiniGame miniGame;
    
    // THE LEVEL GRID REFERS TO THE LAYOUT FOR A GIVEN LEVEL, MEANING
    // HOW MANY TILES FIT INTO EACH CELL WHEN FIRST STARTING A LEVEL
    private int[][] levelGrid;
    
    // LEVEL GRID DIMENSIONS
    private int gridColumns;
    private int gridRows;
    
    // THIS STORES THE TILES ON THE GRID DURING THE GAME
    private ArrayList<LullabyLandSagaTile>[][] tileGrid;
    
    // THESE ARE THE TILES THE PLAYER HAS MATCHED
    private ArrayList<LullabyLandSagaTile> stackTiles;
    
    // THESE ARE THE TILES THAT ARE MOVING AROUND, AND SO WE HAVE TO UPDATE
    private ArrayList<LullabyLandSagaTile> movingTiles;
    
    private ArrayList<LullabyLandSagaTile> fillerTiles;
    private ArrayList<Integer> colsToAdd;
    
    // THIS IS A SELECTED TILE, MEANING THE FIRST OF A PAIR THE PLAYER
    // IS TRYING TO MATCH. THERE CAN ONLY BE ONE OF THESE AT ANY TIME
    private LullabyLandSagaTile selectedTile;
    
    // THE INITIAL LOCATION OF TILES BEFORE BEING PLACED IN THE GRID
    private int unassignedTilesX;
    private int unassignedTilesY;
    
    // THESE ARE USED FOR TIMING THE GAME
    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    
    // THE REFERENCE TO THE FILE BEING PLAYED
    private String currentLevel;
    private int latestLevel = 1;
    
    // TILE COUNT
    private int tileCount;
    
    private int score;
    private int scoreMultiplier;
    private int scoreObjective;
    
    private int clouds;
    
    private int turns;

    String stateHolder = VISIBLE_STATE;
    
    boolean processSpecial = false;
    /**
     * Constructor for initializing this data model, it will create
     * the data structures for storing tiles, but not the tile grid
     * itself, that is dependent of file loading, and so should be
     * subsequently initialized.
     * 
     * @param initMiniGame The Mahjong game UI.
     */
    public LullabyLandSagaDataModel(MiniGame initMiniGame)
    {
        // KEEP THE GAME FOR LATER
        miniGame = initMiniGame;
        
        // INIT THESE FOR HOLDING MATCHED AND MOVING TILES
        stackTiles = new ArrayList();
        movingTiles = new ArrayList();
        fillerTiles = new ArrayList();
        colsToAdd = new ArrayList();
        
        score = 0;
        scoreMultiplier = 1;
        clouds = 0;
    }
    
    // INIT METHODS - AFTER CONSTRUCTION, THESE METHODS SETUP A GAME FOR USE
        // - initTiles
        // - initTile
        // - initLevelGrid
        // - initSpriteType

    /**
     * This method loads the tiles, creating an individual sprite for each. Note
     * that tiles may be of various types, which is important during the tile
     * matching tests.
     */
    public void initTiles()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();        
        String imgPath = props.getProperty(LullabyLandSagaPropertyType.IMG_PATH);
        int spriteTypeID = 0;
        SpriteType sT;
        
        // WE'LL RENDER ALL THE TILES ON TOP OF THE BLANK TILE
        String blankTileFileName = props.getProperty(LullabyLandSagaPropertyType.BLANK_TILE_IMAGE_NAME);
        BufferedImage blankTileImage = miniGame.loadImageWithColorKey(imgPath + blankTileFileName, COLOR_KEY);
        ((LullabyLandSagaPanel)(miniGame.getCanvas())).setBlankTileImage(blankTileImage);
        
        // THIS IS A HIGHLIGHTED BLANK TILE FOR WHEN THE PLAYER SELECTS ONE
        String blankTileSelectedFileName = props.getProperty(LullabyLandSagaPropertyType.BLANK_TILE_SELECTED_IMAGE_NAME);
        BufferedImage blankTileSelectedImage = miniGame.loadImageWithColorKey(imgPath + blankTileSelectedFileName, COLOR_KEY);
        ((LullabyLandSagaPanel)(miniGame.getCanvas())).setBlankTileSelectedImage(blankTileSelectedImage);
        
        // FIRST THE TYPE A TILES, OF WHICH THERE IS ONLY ONE OF EACH
        // THIS IS ANALOGOUS TO THE SEASON TILES IN FLAVORLESS MAHJONG
        ArrayList<String> typeATiles = props.getPropertyOptionsList(LullabyLandSagaPropertyType.TYPE_A_TILES);
        for (int i = 0; i < typeATiles.size(); i++)
        {
            String imgFile = imgPath + typeATiles.get(i);            
            sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
            for (int j = 0; j < 20; j++)
            {
                initTile(sT, TILE_A_TYPE);
            }
            for (int j = 0; j < 6; j++)
            {
                initFillerTile(sT, TILE_A_TYPE);
            }
            spriteTypeID++;
        }
        
        // THEN THE TYPE B TILES, WHICH ALSO ONLY HAVE ONE OF EACH
        // THIS IS ANALOGOUS TO THE FLOWER TILES IN FLAVORLESS MAHJONG
        ArrayList<String> typeBTiles = props.getPropertyOptionsList(LullabyLandSagaPropertyType.TYPE_B_TILES);
        for (int i = 0; i < typeBTiles.size(); i++)
        {
            String imgFile = imgPath + typeBTiles.get(i);            
            sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
            for (int j = 0; j < 20; j++)
            {
                initTile(sT, TILE_B_TYPE);
            }
            for (int j = 0; j < 6; j++)
            {
                initFillerTile(sT, TILE_B_TYPE);
            }
            spriteTypeID++;
        }
        
        // AND THEN TYPE C, FOR WHICH THERE ARE 4 OF EACH 
        // THIS IS ANALOGOUS TO THE CHARACTER AND NUMBER TILES IN FLAVORLESS MAHJONG
        ArrayList<String> typeCTiles = props.getPropertyOptionsList(LullabyLandSagaPropertyType.TYPE_C_TILES);
        for (int i = 0; i < typeCTiles.size(); i++)
        {
            String imgFile = imgPath + typeCTiles.get(i);
            sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);            
            for (int j = 0; j < 20; j++)
            {
                initTile(sT, TILE_C_TYPE);
            }
            for (int j = 0; j < 6; j++)
            {
                initFillerTile(sT, TILE_C_TYPE);
            }
            spriteTypeID++;
        }
        
        // AND THEN TYPE C, FOR WHICH THERE ARE 4 OF EACH 
        // THIS IS ANALOGOUS TO THE CHARACTER AND NUMBER TILES IN FLAVORLESS MAHJONG
        ArrayList<String> typeDTiles = props.getPropertyOptionsList(LullabyLandSagaPropertyType.TYPE_D_TILES);
        for (int i = 0; i < typeDTiles.size(); i++)
        {
            String imgFile = imgPath + typeDTiles.get(i);
            sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);            
            for (int j = 0; j < 20; j++)
            {
                initTile(sT, TILE_D_TYPE);
            }
            for (int j = 0; j < 6; j++)
            {
                initFillerTile(sT, TILE_D_TYPE);
            }
            spriteTypeID++;
        }
                
        // AND THEN TYPE C, FOR WHICH THERE ARE 4 OF EACH 
        // THIS IS ANALOGOUS TO THE CHARACTER AND NUMBER TILES IN FLAVORLESS MAHJONG
        ArrayList<String> typeETiles = props.getPropertyOptionsList(LullabyLandSagaPropertyType.TYPE_E_TILES);
        for (int i = 0; i < typeETiles.size(); i++)
        {
            String imgFile = imgPath + typeETiles.get(i);
            sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);            
            for (int j = 0; j < 20; j++)
            {
                initTile(sT, TILE_E_TYPE);
            }        
            for (int j = 0; j < 6; j++)
            {
                initFillerTile(sT, TILE_E_TYPE);
            }
            spriteTypeID++;
        }
                
        // AND THEN TYPE C, FOR WHICH THERE ARE 4 OF EACH 
        // THIS IS ANALOGOUS TO THE CHARACTER AND NUMBER TILES IN FLAVORLESS MAHJONG
        ArrayList<String> typeFTiles = props.getPropertyOptionsList(LullabyLandSagaPropertyType.TYPE_F_TILES);
        for (int i = 0; i < typeFTiles.size(); i++)
        {
            String imgFile = imgPath + typeFTiles.get(i);
            sT = initTileSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);            
            for (int j = 0; j < 20; j++)
            {
                initTile(sT, TILE_F_TYPE);
            }   
            for (int j = 0; j < 6; j++)
            {
                initFillerTile(sT, TILE_F_TYPE);
            }
            spriteTypeID++;
        }
    }
    
    

    /**
     * Helper method for loading the tiles, it constructs the prescribed
     * tile type using the provided sprite type.
     * 
     * @param sT The sprite type to use to represent this tile during rendering.
     * 
     * @param tileType The type of tile. Note that there are 3 broad categories.
     */
    private void initTile(SpriteType sT, String tileType)
    {
        // CONSTRUCT THE TILE
        LullabyLandSagaTile newTile = new LullabyLandSagaTile(sT, unassignedTilesX, unassignedTilesY, 0, 0, INVISIBLE_STATE, tileType);
        
        // AND ADD IT TO THE STACK
        stackTiles.add(newTile);        
    }
 
    private void initFillerTile(SpriteType sT, String tileType)
    {
        // CONSTRUCT THE TILE
        LullabyLandSagaTile newTile = new LullabyLandSagaTile(sT, unassignedTilesX, unassignedTilesY, 0, 0, INVISIBLE_STATE, tileType);
        
        // AND ADD IT TO THE STACK
        fillerTiles.add(newTile);        
    }
    
    /**
     * Called after a level has been selected, it initializes the grid
     * so that it is the proper dimensions.
     * 
     * @param initGrid The grid distribution of tiles, where each cell 
     * specifies the number of tiles to be stacked in that cell.
     * 
     * @param initGridColumns The columns in the grid for the level selected.
     * 
     * @param initGridRows The rows in the grid for the level selected.
     */
    public void initLevelGrid(int[][] initGrid, int initGridColumns, int initGridRows) throws InterruptedException
    {
        // KEEP ALL THE GRID INFO
        levelGrid = initGrid;
        gridColumns = initGridColumns;
        gridRows = initGridRows;

        // AND BUILD THE TILE GRID FOR STORING THE TILES
        // SINCE WE NOW KNOW ITS DIMENSIONS
        tileGrid = new ArrayList[gridColumns][gridRows];
        for (int i = 0; i < gridColumns; i++)
        {
            for (int j = 0; j < gridRows; j++)
            {
                // EACH CELL HAS A STACK OF TILES, WE'LL USE
                // AN ARRAY LIST FOR THE STACK
                tileGrid[i][j] = new ArrayList();
            }
        }
        // MAKE ALL THE TILES VISIBLE
        enableTiles(true);
        
        while(matchOnGrid() != null)
        {
            System.out.println("DM288 - GRID RESHUFFLE");
            LullabyLandSagaMove move = matchOnGrid();
            int k = isValidMove(tileGrid[move.col1][move.row1].get(0),
                    tileGrid[move.col2][move.row2].get(0));
            processMove(move, k);
        }
    }
    
    /**
     * This helper method initializes a sprite type for a tile or set of
     * similar tiles to be created.
     */
    private SpriteType initTileSpriteType(String imgFile, String spriteTypeID)
    {
        // WE'LL MAKE A NEW SPRITE TYPE FOR EACH GROUP OF SIMILAR LOOKING TILES
        SpriteType sT = new SpriteType(spriteTypeID);
        addSpriteType(sT);
        
        // LOAD THE ART
        BufferedImage img = miniGame.loadImageWithColorKey(imgFile, COLOR_KEY);
        Image tempImage = img.getScaledInstance(TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT, BufferedImage.SCALE_SMOOTH);
        img = new BufferedImage(TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(tempImage, 0, 0, null);
        
        // WE'LL USE THE SAME IMAGE FOR ALL STATES
        sT.addState(INVISIBLE_STATE, img);
        sT.addState(VISIBLE_STATE, img);
        sT.addState(SELECTED_STATE, img);
        sT.addState(ROW_CLEAR_STATE, img);
        sT.addState(COL_CLEAR_STATE, img);
        sT.addState(WRAPPED_STATE,img);
        sT.addState(SPECIAL_SELECTED_STATE,img);
        //sT.addState(CLOUD_STATE, img);
        return sT;
    }
        
    // ACCESSOR METHODS

    /**
     * Accessor method for getting the level currently being played.
     * 
     * @return The level name used currently for the game screen.
     */
    public String getCurrentLevel() 
    { 
        return currentLevel; 
    }

    public int getLatestLevel()
    {
        return latestLevel;
    }
    /**
     * Accessor method for getting the number of tile columns in the game grid.
     * 
     * @return The number of columns (left to right) in the grid for the level
     * currently loaded.
     */
    public int getGridColumns() 
    { 
        return gridColumns; 
    }
    
    /**
     * Accessor method for getting the number of tile rows in the game grid.
     * 
     * @return The number of rows (top to bottom) in the grid for the level
     * currently loaded.
     */
    public int getGridRows() 
    { 
        return gridRows; 
    }

    /**
     * Accessor method for getting the tile grid, which has all the
     * tiles the user may select from.
     * 
     * @return The main 2D grid of tiles the user selects tiles from.
     */
    public ArrayList<LullabyLandSagaTile>[][] getTileGrid() 
    { 
        return tileGrid; 
    }
    
    /**
     * Accessor method for getting the stack tiles.
     * 
     * @return The stack tiles, which are the tiles the matched tiles
     * are placed in.
     */
    public ArrayList<LullabyLandSagaTile> getStackTiles()
    {
        return stackTiles;
    }

    /**
     * Accessor method for getting the moving tiles.
     * 
     * @return The moving tiles, which are the tiles currently being
     * animated as they move around the game. 
     */
    public Iterator<LullabyLandSagaTile> getMovingTiles()
    {
        return movingTiles.iterator();
    }
    
    public int getStackTileCount()
    {
        return stackTiles.size();
    }
    
    /**
     * Mutator method for setting the currently loaded level.
     * 
     * @param initCurrentLevel The level name currently being used
     * to play the game.
     */
    public void setCurrentLevel(String initCurrentLevel)
    {
        currentLevel = initCurrentLevel;
        if(currentLevel.charAt(24) == '1')
        {
            if(currentLevel.charAt(25) != '0')
            {
                turns = 6;
                scoreObjective = 6800;
            } else {
                turns = 40;
                scoreObjective = 1339540;
                clouds = 39;
            }
        }
        if(currentLevel.charAt(24) == '2')
        {
            turns = 15;
            scoreObjective = 9880;
        }
        if(currentLevel.charAt(24) == '3')
        {
            turns = 18;
            scoreObjective = 15840;
        }
        if(currentLevel.charAt(24) == '4')
        {
            turns = 15;
            scoreObjective = 15180;
        }
        if(currentLevel.charAt(24) == '5')
        {
            turns = 20;
            scoreObjective = 34380;
        }
        if(currentLevel.charAt(24) == '6')
        {
            turns = 16;
            scoreObjective = 44660;
            clouds = 12;
        }
        if(currentLevel.charAt(24) == '7')
        {
            turns = 50;
            scoreObjective = 173360;
            clouds = 52;
        }
        if(currentLevel.charAt(24) == '8')
        {
            turns = 20;
            scoreObjective = 52520;
            clouds = 17;
        }
        if(currentLevel.charAt(24) == '9')
        {
            turns = 25;
            scoreObjective = 66000;
            clouds = 22;
        }
    }

    /**
     * Used to calculate the x-axis pixel location in the game grid for a tile
     * placed at column with stack position z.
     * 
     * @param column The column in the grid the tile is located.
     * 
     * @param z The level of the tile in the stack at the given grid location.
     * 
     * @return The x-axis pixel location of the tile 
     */
    public int calculateTileXInGrid(int column, int z)
    {
        int cellWidth = TILE_IMAGE_WIDTH;
        float leftEdge = miniGame.getBoundaryLeft();
        return (int)(leftEdge + (cellWidth * column));
    }
    
    /**
     * Used to calculate the y-axis pixel location in the game grid for a tile
     * placed at row with stack position z.
     * 
     * @param row The row in the grid the tile is located.
     * 
     * @param z The level of the tile in the stack at the given grid location.
     * 
     * @return The y-axis pixel location of the tile 
     */
    public int calculateTileYInGrid(int row, int z)
    {
        int cellHeight = TILE_IMAGE_HEIGHT;
        float topEdge = miniGame.getBoundaryTop();
        return (int)(topEdge + (cellHeight * row));
    }

    /**
     * Used to calculate the grid column for the x-axis pixel location.
     * 
     * @param x The x-axis pixel location for the request.
     * 
     * @return The column that corresponds to the x-axis location x.
     */
    public int calculateGridCellColumn(int x)
    {
        float leftEdge = miniGame.getBoundaryLeft();
        x = (int)(x - leftEdge);
        return x / TILE_IMAGE_WIDTH;
    }

    /**
     * Used to calculate the grid row for the y-axis pixel location.
     * 
     * @param y The y-axis pixel location for the request.
     * 
     * @return The row that corresponds to the y-axis location y.
     */
    public int calculateGridCellRow(int y)
    {
        float topEdge = miniGame.getBoundaryTop();
        y = (int)(y - topEdge);
        return y / TILE_IMAGE_HEIGHT;
    }
    
    // TIME TEXT METHODS
        // - timeToText
        // - gameTimeToText
    
    /**
     * This method creates and returns a textual description of
     * the timeInMillis argument as a time duration in the format
     * of (H:MM:SS).
     * 
     * @param timeInMillis The time to be represented textually.
     * 
     * @return A textual representation of timeInMillis.
     */
    public String timeToText(long timeInMillis)
    {
        // FIRST CALCULATE THE NUMBER OF HOURS,
        // SECONDS, AND MINUTES
        long hours = timeInMillis/MILLIS_IN_AN_HOUR;
        timeInMillis -= hours * MILLIS_IN_AN_HOUR;        
        long minutes = timeInMillis/MILLIS_IN_A_MINUTE;
        timeInMillis -= minutes * MILLIS_IN_A_MINUTE;
        long seconds = timeInMillis/MILLIS_IN_A_SECOND;
              
        // THEN ADD THE TIME OF GAME SUMMARIZED IN PARENTHESES
        String minutesText = "" + minutes;
        if (minutes < 10)   minutesText = "0" + minutesText;
        String secondsText = "" + seconds;
        if (seconds < 10)   secondsText = "0" + secondsText;
        return hours + ":" + minutesText + ":" + secondsText;
    }

    /**
     * This method builds and returns a textual representation of
     * the game time. Note that the game may still be in progress.
     * 
     * @return The duration of the current game represented textually.
     */
    public String gameTimeToText()
    {
        // CALCULATE GAME TIME USING HOURS : MINUTES : SECONDS
        if ((startTime == null) || (endTime == null))
            return "";
        long timeInMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        return timeToText(timeInMillis);
    }
    
    // GAME DATA SERVICE METHODS
        // -enableTiles
        // -findMove
        // -moveAllTilesToStack
        // -moveTiles
        // -playWinAnimation
        // -processMove
        // -selectTile
        // -undoLastMove

    /**
     * This method can be used to make all of the tiles either visible (true)
     * or invisible (false). This should be used when switching between the
     * splash and game screens.
     * 
     * @param enable Specifies whether the tiles should be made visible or not.
     */
    public void enableTiles(boolean enable)
    {
        // PUT ALL THE TILES IN ONE PLACE WHERE WE CAN PROCESS THEM TOGETHER
        moveAllTilesToStack();
        
        // GO THROUGH ALL OF THEM 
        for (LullabyLandSagaTile tile : stackTiles)
        {
            // AND SET THEM PROPERLY
            if (enable)
                tile.setState(VISIBLE_STATE);
            else
                tile.setState(INVISIBLE_STATE);
        }        
    }

    /**
     * This method moves all the tiles not currently in the stack 
     * to the stack.
     */
    public void moveAllTilesToStack()
    {
        for (int i = 0; i < gridColumns; i++)
        {
            for (int j = 0; j < gridRows; j++)
            {
                ArrayList<LullabyLandSagaTile> cellStack = tileGrid[i][j];
                moveTiles(cellStack, stackTiles);
            }
        }        
    }

    /**
     * This method removes all the tiles in from argument and moves them
     * to argument.
     * 
     * @param from The source data structure of tiles.
     * 
     * @param to The destination data structure of tiles.
     */
    private void moveTiles(ArrayList<LullabyLandSagaTile> from, ArrayList<LullabyLandSagaTile> to)
    {
        // GO THROUGH ALL THE TILES, TOP TO BOTTOM
        for (int i = from.size()-1; i >= 0; i--)
        {
            LullabyLandSagaTile tile = from.remove(i);
            
            // ONLY ADD IT IF IT'S NOT THERE ALREADY
            if (!to.contains(tile))
                to.add(tile);
        }        
    }

    /**
     * This method sets up and starts the animation shown after
     * a game is won.
     */
    public void playWinAnimation()
    {
        // MAKE A NEW PATH
        ArrayList<Integer> winPath = new ArrayList();
        
        // THIS HAS THE APPROXIMATE PATH NODES, WHICH WE'LL SLIGHTLY
        // RANDOMIZE FOR EACH TILE FOLLOWING THE PATH
        winPath.add(getGameWidth() - WIN_PATH_COORD - 290); winPath.add(WIN_PATH_COORD + 100);                  //1 - upper right
        winPath.add(getGameWidth()/2);                      winPath.add(WIN_PATH_COORD);                        //2 - top
        winPath.add(WIN_PATH_COORD + 270);                  winPath.add(WIN_PATH_COORD + 80);                  //3 - upper left
        winPath.add(WIN_PATH_COORD+200);                    winPath.add(getGameHeight()/2);                     //4 - left
        winPath.add(WIN_PATH_COORD + 270);                  winPath.add(getGameHeight()-WIN_PATH_COORD-80);    //5 - lower left 
        winPath.add(getGameWidth()/2);                      winPath.add(getGameHeight()-WIN_PATH_COORD);        //6 - bottom
        winPath.add(getGameWidth() - WIN_PATH_COORD - 320); winPath.add(getGameHeight()-WIN_PATH_COORD-100);    //7 - lower right
        winPath.add(getGameWidth() - WIN_PATH_COORD - 260); winPath.add(getGameHeight()/2);                     //8 - right
        
        // START THE ANIMATION FOR ALL THE TILES
        for (int i = 0; i < stackTiles.size(); i++)
        {
            // GET EACH TILE
            LullabyLandSagaTile tile = stackTiles.get(i);
            
            // MAKE SURE IT'S MOVED EACH FRAME
            movingTiles.add(tile);       
            
            // AND GET IT ON A PATH
            tile.initWinPath(winPath);
        }
    }    

    /**
     * This method updates all the necessary state information
     * to process the move argument.
     * 
     * @param move The move to make. Note that a move specifies
     * the cell locations for a isNeighbor.
     */
    public void processMove(LullabyLandSagaMove move, int value) throws InterruptedException
    {
        ArrayList<LullabyLandSagaTile> stack;
        LullabyLandSagaTile tile;
        boolean special = false;
        
        // 4 IN A ROW, 50/50 CHANCE OF ROW OR COLUMN WIPE dont remove
        if(value>99 || value <-99)
        {
            int colSpec;
            int rowSpec;
            if(value<0)
            {
                colSpec = move.col1;
                rowSpec = move.row1;
            } else {
                colSpec = move.col2;
                rowSpec = move.row2;
            }
            double i;
            i = Math.random();
            if(i>=.5)
            {
                tileGrid[colSpec][rowSpec].get(0).setState(ROW_CLEAR_STATE);
            } else {
                tileGrid[colSpec][rowSpec].get(0).setState(COL_CLEAR_STATE);
            }
            special = true;
        }
        
        // L OR T SHAPED FORMATION GETS A "WRAPPED STATE" dont remove
        else if(value > 9 || value < -9)
        {
            tileGrid[move.col1][move.row1].get(0).setState(WRAPPED_STATE);
            special = true;
        }
        
        else if(value == 7 || value == 8)
        {
            System.out.println("5 COMBO");
                    special = true;
        }
        
        if(!special)   
        {
            if(value < 0)
            {
                value = value/-1;
                stack = tileGrid[move.col1][move.row1];
            } else {
                stack = tileGrid[move.col2][move.row2];
            }
                    
                tile = stack.remove(stack.size()-1);
                tile.setState(INVISIBLE_STATE);        
                fillerTiles.add(tile);
        } else {
            if(value < 0)
            {
                value = value/-1;
                stack = tileGrid[move.col1][move.row1];
            } else {
                stack = tileGrid[move.col2][move.row2];
            }
                    
                tile = stack.get(stack.size()-1);
        }
        
        int col = tile.getGridColumn();
        colsToAdd.add(col);
        int row = tile.getGridRow();
        switch(value)
        {
            case 1:
                removeTile(col+1, row);
                removeTile(col+2, row);
                break;
            case 100:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col-1, row);
                break;
            case 10:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col+2, row+1);
                removeTile(col+2, row-1);
                break;
            case 11:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col+2, row-1);
                removeTile(col+2, row-2);
                break;
            case 12:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col+2, row+1);
                removeTile(col+2, row+2);
                break;
            case 13:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col+1, row-1);
                removeTile(col+1, row-2);
                break;
            case 14:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col+1, row+1);
                removeTile(col+1, row+2);
                break;
            case 15:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col, row-1);
                removeTile(col, row-2);
                break;
            case 16:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col, row+1);
                removeTile(col, row+2);
                break;
            case 17:
                removeTile(col+1, row);
                removeTile(col+2, row);
                removeTile(col, row+1);
                removeTile(col, row-1);
                break;
            case 2:
                removeTile(col, row+1);
                removeTile(col, row+2);
                break;
            case 200:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col, row-1);
                break;
            case 20:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col+1, row+2);
                removeTile(col-1, row+2);
                break;
            case 21:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col+1, row+2);
                removeTile(col+2, row+2);
                break;
            case 22:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col-1, row+2);
                removeTile(col-2, row+2);
                break;
            case 23:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col+1, row+1);
                removeTile(col+2, row+1);
                break;
            case 24:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col-1, row+1);
                removeTile(col-2, row+1);
                break;
            case 25:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col+1, row);
                removeTile(col+2, row);
                break;
            case 26:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col-1, row);
                removeTile(col-2, row);
                break;
            case 27:
                removeTile(col, row+1);
                removeTile(col, row+2);
                removeTile(col+1, row);
                removeTile(col-1, row);
                break;
            case 3:
                removeTile(col-1, row);
                removeTile(col-2, row); 
                break;
            case 300:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col+1, row);
                break;
            case 30:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col-2, row-1);
                removeTile(col-2, row+1);
                break;
            case 31:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col-2, row-2);
                removeTile(col-2, row-1);
                break;
            case 32:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col-2, row+1);
                removeTile(col-2, row+2);
                break;
            case 33:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col-1, row-1);
                removeTile(col-1, row-2);
                break;
            case 34:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col-1, row+1);
                removeTile(col-1, row+2);
                break;
            case 35:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col, row-1);
                removeTile(col, row-2);
                break;
            case 37:
                removeTile(col-1, row);
                removeTile(col-2, row);
                removeTile(col, row+1);
                removeTile(col, row-1);
                break;
            case 4:
                removeTile(col, row-1);
                removeTile(col, row-2);
                break;
            case 400:
                removeTile(col, row-1);
                removeTile(col, row-2);
                removeTile(col, row+1);
                break;
            case 40:
                removeTile(col, row-1);
                removeTile(col, row-2);
                removeTile(col-1, row-2);
                removeTile(col+1, row-2);
                break;
            case 41:
                removeTile(col, row-1);
                removeTile(col, row-2);
                removeTile(col+1, row-2);
                removeTile(col+2, row-2);
                break;
            case 42:
                removeTile(col, row-1);
                removeTile(col, row-2);
                removeTile(col-1, row-2);
                removeTile(col-2, row-2);
                break;
            case 43:
                removeTile(col, row-1);
                removeTile(col, row-2);
                removeTile(col+1, row-1);
                removeTile(col+2, row-1);
                break;
            case 44:
                removeTile(col, row-1);
                removeTile(col, row-2);
                removeTile(col-1, row-1);
                removeTile(col-2, row-1);
                break;
            case 47:
                removeTile(col, row-1);
                removeTile(col, row-2);
                removeTile(col-1, row);
                removeTile(col+1, row);
                break;
            case 5:
                removeTile(col-1, row);
                removeTile(col+1, row);
                break;
            case 50:
                removeTile(col-1, row);
                removeTile(col+1, row);
                removeTile(col-1, row-1);
                removeTile(col-1, row+1);
                break;
            case 51:
                removeTile(col-1, row);
                removeTile(col+1, row);
                removeTile(col-1, row-1);
                removeTile(col-1, row-2);
                break;
            case 52:
                removeTile(col-1, row);
                removeTile(col+1, row);
                removeTile(col-1, row+1);
                removeTile(col-1, row+2);
                break;
            case 53:
                removeTile(col-1, row);
                removeTile(col+1, row);
                removeTile(col+1, row-1);
                removeTile(col+1, row-2);
                break;
            case 54:
                removeTile(col-1, row);
                removeTile(col+1, row);
                removeTile(col+1, row+1);
                removeTile(col+1, row+2);
                break;
            case 55:
                removeTile(col-1, row);
                removeTile(col+1, row);
                removeTile(col+1, row-1);
                removeTile(col+1, row+1);
                break;
            case 6:
                removeTile(col, row+1);
                removeTile(col, row-1);
                break;
            case 60:
                removeTile(col, row+1);
                removeTile(col, row-1);
                removeTile(col+1, row+1);
                removeTile(col-1, row+1);
                break;
            case 61:
                removeTile(col, row+1);
                removeTile(col, row-1);
                removeTile(col+1, row+1);
                removeTile(col+2, row+1);
                break;
            case 62:
                removeTile(col, row+1);
                removeTile(col, row-1);
                removeTile(col-1, row+1);
                removeTile(col-2, row+1);
                break;
            case 63:
                removeTile(col, row+1);
                removeTile(col, row-1);
                removeTile(col+1, row-1);
                removeTile(col+2, row-1);
                break;
            case 64:
                removeTile(col, row+1);
                removeTile(col, row-1);
                removeTile(col-1, row-1);
                removeTile(col-2, row-1);
                break;
            case 65:
                removeTile(col, row+1);
                removeTile(col, row-1);
                removeTile(col+1, row-1);
                removeTile(col-1, row-1);
                break;
            case 7:
                removeTile(col, row-2);
                removeTile(col, row-1);
                removeTile(col, row+1);
                removeTile(col, row+2);
                break;
            case 8:
                removeTile(col-2, row);
                removeTile(col-1, row);
                removeTile(col+1, row);
                removeTile(col+2, row);
                break;
               
        }
        
        moveTilesDown();
        
        // AND MAKE SURE NEW TILES CAN BE SELECTED
        selectedTile = null;   

        
    }
    
    public void removeTile(int col, int row)
    {
       try {
        String ts = tileGrid[col][row].get(0).getState();
        score += (60*scoreMultiplier);
        System.out.println(score);
        ArrayList<LullabyLandSagaTile> stack = tileGrid[col][row];
        LullabyLandSagaTile tile = stack.remove(stack.size()-1);
        tile.setState(INVISIBLE_STATE);
        fillerTiles.add(tile);
        colsToAdd.add(col);
       
        if(!processSpecial)
        {
        // REMOVE A SPECIAL ROW-CLEAR INSTRUMENT
        try{
            if(ts.equals(COL_CLEAR_STATE)
                    || (stateHolder.equals(COL_CLEAR_STATE)) )
            {
                processSpecial = true;
                System.out.println("COLUMN CLEAR");
                for(int i=0; i<gridRows; i++)
                {
                    if(i!=row)
                    {
                        try {
                            removeTile(col, i);
                        } catch(ArrayIndexOutOfBoundsException aiobe){}
                    }
                }
                processSpecial = false;
            }
        }catch(IndexOutOfBoundsException iobe){}
        
        // REMOVE A SPECIAL COLUMN-CLEAR INSTRUMENT
        try {
            
            if(ts.equals(ROW_CLEAR_STATE)
                    || (stateHolder.equals(ROW_CLEAR_STATE)) )
            {
                        
                processSpecial = true;
                System.out.println("ROW CLEAR");
                for (int i = 0; i < gridColumns; i++) {
                    try {
                        if(i != col)
                        {
                            removeTile(i, row);
                        }
                    } catch(ArrayIndexOutOfBoundsException aiobe){}
                }
                processSpecial = false;
            }
        }catch(IndexOutOfBoundsException iobe){}
        }
       }catch(IndexOutOfBoundsException iobe){}
        // REMOVE A WRAPPED INSTRUMENT WITH BURST
       
       
        
    }
    
    public void moveTilesDown() throws InterruptedException
    {
        boolean added = false;
        float yAdjust;
        boolean bottom = false;
        for(int col : colsToAdd)
        {
            yAdjust = calculateTileYInGrid(0,0);
            int i;
            if(currentLevel.equals("./data/./lullaland/Level3.zom"))
            {
                if(col>0&&col<gridColumns-1)
                {
                    i = gridRows-1;
                } else{
                    i = gridRows-2;
                }
            } else {
                i = gridRows - 1;
            }
            for( ;i>=0; i--)
            {
                if(tileGrid[col][i].isEmpty())
                {
                    
                    for(int j=i; j>=0; j--)
                    {
                        if(!tileGrid[col][j].isEmpty())
                        {
                            // REMOVE THE MOVE TILES FROM THE GRID
                            ArrayList<LullabyLandSagaTile> stack = tileGrid[col][j];      
                            LullabyLandSagaTile tile = stack.remove(stack.size()-1);

                            yAdjust = tile.getY();
                            bottom=true;
                            
                            // MAKE SURE BOTH ARE UNSELECTED
                            if(tile.getState().equals(SELECTED_STATE) )
                            {
                                tile.setState(VISIBLE_STATE);
                            } else if(tile.getState().equals(SPECIAL_SELECTED_STATE))
                            {
                                tile.setState(stateHolder);
                            }
                            float x = calculateTileXInGrid(col, 0);
                            float y = calculateTileYInGrid(i, 0);
                            tile.setTarget(x, y);

                            // SEND THEM TO THE STACK
                            tile.startMovingToTarget(4);

                            // MAKE SURE THEY MOVE
                            movingTiles.add(tile);

                            selectedTile = null;

                            tileGrid[col][j].remove(tile);
                            tile.setGridCell(col, i);
                            tileGrid[col][i].add(tile);
                            added = true;
                            break;
                        }
                    }
                    if(added)
                    {
                        added = false;
                    } else {
                        // REMOVE THE MOVE TILES FROM THE GRID
                            ArrayList<LullabyLandSagaTile> stack = fillerTiles;
                            Collections.shuffle(stack);
                            LullabyLandSagaTile tile = stack.remove(stack.size()-1);

                            // MAKE SURE BOTH ARE UNSELECTED
                            if(tile.getState().equals(SELECTED_STATE)
                                    || tile.getState().equals(INVISIBLE_STATE))
                            {
                                tile.setState(VISIBLE_STATE);
                            } else if(tile.getState().equals(SPECIAL_SELECTED_STATE))
                            {
                                tile.setState(stateHolder);
                            }
                            float x = calculateTileXInGrid(col, 0);
                            float y = calculateTileYInGrid(i, 0);
                            
                            if(bottom)
                            {
                                yAdjust = y-60;
                            } else {
                                yAdjust -= 60;
                            }
                            
                            tile.setX(x);
                            tile.setY(yAdjust);
                            tile.setTarget(x, y);

                            // SEND THEM TO THE STACK
                            tile.startMovingToTarget(4);

                            // MAKE SURE THEY MOVE
                            movingTiles.add(tile);

                            selectedTile = null;

                            fillerTiles.remove(tile);
                            tile.setGridCell(col, i);
                            tileGrid[col][i].add(tile);
                            break;
                    }
                    bottom = false;
                }
            }
        }
        colsToAdd = new ArrayList<>();
        
        if(moveOnGrid()== null)
        {
            System.out.println("DM1156 - NO POSSIBLE MOVES, GRID RESHUFFLE");
            for(int i=0; i<10; i++)
            {
                reset(this.miniGame);
                updateAll(this.miniGame);
            }
        }
        
        updateAll(this.miniGame);
        
        
        while(matchOnGrid()!= null)
        {
            scoreMultiplier++;
            System.out.println("DM1161 - COMPOUND MOVE");
            LullabyLandSagaMove move = matchOnGrid();
            int k = isValidMove(tileGrid[move.col1][move.row1].get(0),
                    tileGrid[move.col2][move.row2].get(0));
            processMove(move, k);
            updateAll(this.miniGame);
        }
    }
    
    public LullabyLandSagaMove moveOnGrid()
    {
        LullabyLandSagaMove move = new LullabyLandSagaMove();
        for(int i=0; i<gridColumns-1; i++)
        {
            for(int j=0; j<gridRows-1; j++)
            {
                
                try{
                    if(isValidMove(tileGrid[i][j].get(0), tileGrid[i+1][j].get(0)) != 0)
                    {
                        move.col1 = i;
                        move.col2 = i+1;
                        move.row1 = j;
                        move.row2 = j;
                        return move;
                    }
                    if(isValidMove(tileGrid[i][j].get(0), tileGrid[i-1][j].get(0)) != 0)
                    {
                        move.col1 = i;
                        move.col2 = i-1;
                        move.row1 = j;
                        move.row2 = j;
                        return move;
                    }
                    if(isValidMove(tileGrid[i][j].get(0), tileGrid[i][j+1].get(0)) != 0)
                    {
                        move.col1 = i;
                        move.col2 = i;
                        move.row1 = j;
                        move.row2 = j+1;
                        return move;
                    }
                    if(isValidMove(tileGrid[i][j].get(0), tileGrid[i][j-1].get(0)) != 0)
                    {
                        move.col1 = i;
                        move.col2 = i;
                        move.row1 = j;
                        move.row2 = j-1;
                        return move;
                    }
                } catch (IndexOutOfBoundsException iobe){}
            }
        }
        return null;
    }
    
    public LullabyLandSagaMove matchOnGrid()
    {
        LullabyLandSagaMove move = new LullabyLandSagaMove();
        for(int i=gridColumns-1; i>0; i--)
        {
            for(int j=gridRows-1; j>0; j--)
            {
                try{
                    if(isValidMove(tileGrid[i][j].get(0), tileGrid[i][j].get(0)) != 0)
                    {
                        move.col1 = i;
                        move.col2 = i;
                        move.row1 = j;
                        move.row2 = j;
                        return move;
                    }
                } catch (IndexOutOfBoundsException iobe){}
            }
        }
        
        for(int i=0; i <gridRows-3; i++)
        {
            try{
                String tt = tileGrid[0][i].get(0).getTileType();
                
                if(tt.equals(tileGrid[0][i+1].get(0).getTileType())
                        && tt.equals(tileGrid[0][i+2].get(0).getTileType()))
                {
                    move.col1 = 0;
                    move.col2 = 0;
                    move.row1 = i;
                    move.row2 = i;
                    return move;
          
                }
                tt = tileGrid[gridColumns-1][i].get(0).getTileType();
                if(tt.equals(tileGrid[gridColumns-1][i+1].get(0).getTileType())
                        && tt.equals(tileGrid[gridColumns-1][i+2].get(0).getTileType()))
                {
                    move.col1 = gridColumns-1;
                    move.col2 = gridColumns-1;
                    move.row1 = i;
                    move.row2 = i;
                    return move;
                }
            } catch (IndexOutOfBoundsException iobe){}
        }
        
        for(int i=0; i <gridColumns-1; i++)
        {
            try{
                if(isValidMove(tileGrid[i][0].get(0), tileGrid[i][0].get(0)) != 0)
                {
                    move.col1 = i;
                    move.col2 = i;
                    move.row1 = 0;
                    move.row2 = 0;
                    return move;
                }
                if(isValidMove(tileGrid[i][gridRows-1].get(0), tileGrid[i][gridRows-1].get(0)) != 0)
                {
                    move.col1 = i;
                    move.col2 = i;
                    move.row1 = gridColumns-1;
                    move.row2 = gridColumns-1;
                    return move;
                }
            } catch(IndexOutOfBoundsException iobe) {}
        }
        
        return null;
    }
    
    public int isValidMove(LullabyLandSagaTile tile1, LullabyLandSagaTile tile2)
    {
        /**
         *       X
         *       X
         *       0
         *       X
         *       X
         */
        if( tile1.getGridRow() == tile2.getGridRow() )
        {
            String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
            try {
                if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                        && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-2].get(0).getTileType())
                        && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+1].get(0).getTileType())
                        && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+2].get(0).getTileType()))
                {
                    return 7;
                }
            } catch(ArrayIndexOutOfBoundsException aiobe){} catch(IndexOutOfBoundsException iobe){}
        }
        
        /**
         *       X
         *       X
         *       2
         *       X
         *       X
         */
        if( tile1.getGridRow() == tile2.getGridRow() )
        {
            String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
            try{
                if(tt.equals(tileGrid[tile1.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                        && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-2].get(0).getTileType())
                        && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()+1].get(0).getTileType())
                        && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()+2].get(0).getTileType()))
                {
                    return -7;
                }
            } catch (IndexOutOfBoundsException iobe) {}
        }
        
        /**
         * 
         *    X X 0 X X
         * 
         */
        if( tile1.getGridColumn() == tile2.getGridColumn())
        {
            String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
            
            try{
                if(tt.equals(tileGrid[tile2.getGridColumn()-1][tile2.getGridRow()].get(0).getTileType())
                        && tt.equals(tileGrid[tile2.getGridColumn()-2][tile2.getGridRow()].get(0).getTileType())
                        && tt.equals(tileGrid[tile2.getGridColumn()+1][tile2.getGridRow()].get(0).getTileType())
                        && tt.equals(tileGrid[tile2.getGridColumn()+2][tile2.getGridRow()].get(0).getTileType()))
                {
                    return 8;
                }
            } catch (IndexOutOfBoundsException iobe) {}
        }
        
        /**
         * 
         *    X X 2 X X
         * 
         */
        if( tile1.getGridColumn() == tile2.getGridColumn() )
        {
            String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
            
            try{
            if(tt.equals(tileGrid[tile1.getGridColumn()-1][tile1.getGridRow()].get(0).getTileType())
                    && tt.equals(tileGrid[tile1.getGridColumn()-2][tile1.getGridRow()].get(0).getTileType())
                    && tt.equals(tileGrid[tile1.getGridColumn()+1][tile1.getGridRow()].get(0).getTileType())
                    && tt.equals(tileGrid[tile1.getGridColumn()+2][tile1.getGridRow()].get(0).getTileType()))
            {
                return -8;
            }
            } catch (IndexOutOfBoundsException iobe) {}
        }
        
        /**
         * 
         *    0 X X
         * 
         */
        if( (tile1.getGridColumn() != tile2.getGridColumn()+1) ){
        try {
            String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
            if(tt.equals(tileGrid[tile2.getGridColumn()+1][tile2.getGridRow()].get(0).getTileType())
                    && tt.equals(tileGrid[tile2.getGridColumn()+2][tile2.getGridRow()].get(0).getTileType()))
            {    

                 
                //  X
                //  X
                //  0 X X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-2].get(0).getTileType())
                            && tile1.getGridRow() != tile2.getGridRow()-1)
                    {
                        return 15;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //  0 X X
                //  X
                //  X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+1].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+2].get(0).getTileType())
                            && tile1.getGridRow() != tile2.getGridRow()+1)
                    {
                        return 16;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //  X                                   
                //  0 X X
                //  X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+1].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                            && tile1.getGridColumn() == tile2.getGridColumn()-1)
                    {
                        return 17;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()-1][tile2.getGridRow()].get(0).getTileType())
                            && tile1.getGridRow() != tile2.getGridRow())
                    {
                        return 100;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                
                return 1;
            }
        }catch(ArrayIndexOutOfBoundsException aiobe){}
        }
        
        /**
         * 
         *    0
         *    X
         *    X
         */
        if( (tile1.getGridRow()!= tile2.getGridRow()+1) )
        {
        try {
            String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
            if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+1].get(0).getTileType())
                    && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+2].get(0).getTileType()))
            {
                
                
                // X X 0
                //     X
                //     X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()-1][tile2.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()-2][tile2.getGridRow()].get(0).getTileType()))
                    {
                        if((tile1.getGridColumn() != tile2.getGridColumn() || tile1.getGridRow() != tile2.getGridRow()+1)
                                && (tile1.getGridColumn() != tile2.getGridColumn()-1 || tile1.getGridRow() != tile2.getGridRow())
                                && tile1.getGridColumn() != tile2.getGridColumn()-1)
                        {
                            return 26;
                        }
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //  0 X X
                //  X
                //  X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()+1][tile2.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()+2][tile2.getGridRow()].get(0).getTileType())
                            && tile1.getGridColumn() != tile2.getGridColumn() +1)
                    {
                        return 25;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                // X 0 X
                //   X
                //   X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()-1][tile2.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()+1][tile2.getGridRow()].get(0).getTileType())
                            && tile1.getGridRow() == tile2.getGridRow()-1)
                    {
                        return 27;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                            && tile1.getGridColumn() != tile2.getGridColumn())
                    {
                        return 200;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                return 2;
            }
        } catch(ArrayIndexOutOfBoundsException aiobe){} catch(IndexOutOfBoundsException iobe){}
        }
        
        /**
         * 
         *   2 X X
         * 
         */
        if( (tile1.getGridColumn() != tile2.getGridColumn()-1)  ){
            try{
            if(tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()+1][tile1.getGridRow()].get(0).getTileType())
                    && tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()+2][tile1.getGridRow()].get(0).getTileType()))
            {   
                String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
                
                
                //  X
                //  X
                //  2 X X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-1].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-2].get(0).getTileType())
                            && tile2.getGridRow() != tile1.getGridRow()-1)
                    {
                        return -15;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //  2 X X
                //  X
                //  X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()+1].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()+2].get(0).getTileType())
                            && tile2.getGridRow() != tile1.getGridRow()+1)
                    {
                        return -16;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //  X
                //  2 X X
                //  X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-1].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()+1].get(0).getTileType())
                            && tile2.getGridColumn() == tile1.getGridColumn()-1)
                    {
                        return -17;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()-1][tile1.getGridRow()].get(0).getTileType())
                            && tile1.getGridRow() != tile2.getGridRow())
                    {
                        return -100;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                return -1;
            }
            } catch (IndexOutOfBoundsException iobe) {}
        }
        
        /**
         * 
         *      2
         *      X
         *      X
         */
        if(  (tile1.getGridRow()!= tile2.getGridRow()-1) )
        {
        try {
            String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
            if(tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()][tile1.getGridRow()+1].get(0).getTileType())
                    && tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()][tile1.getGridRow()+2].get(0).getTileType()))
            {   
                
                
                //  2 X X
                //  X
                //  X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()+1][tile1.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()+2][tile1.getGridRow()].get(0).getTileType())
                            && tile2.getGridColumn() != tile1.getGridColumn() +1)
                    {
                        return -25;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                // X X 2
                //     X
                //     X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()-1][tile1.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()-2][tile1.getGridRow()].get(0).getTileType())
                            && tile2.getGridColumn() != tile1.getGridColumn()-1)
                    {
                        return -26;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //  X 2 X
                //    X
                //    X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()-1][tile1.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()+1][tile1.getGridRow()].get(0).getTileType())
                            && tile2.getGridRow() == tile1.getGridRow()-1)
                    {
                        return -27;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-1].get(0).getTileType())
                            & tile1.getGridColumn() != tile2.getGridColumn())
                    {
                        return -200;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                return -2;
            }
        } catch(ArrayIndexOutOfBoundsException aiobe){}
        }
        
        
        /**
         * 
         *    X X 0
         * 
         */
        if( (tile1.getGridColumn() != tile2.getGridColumn()-1) ){
        try {
            if(tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()-1][tile2.getGridRow()].get(0).getTileType())
                    && tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()-2][tile2.getGridRow()].get(0).getTileType()))
            { 
                String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
                
                
                
                //        X
                //    X X 0
                //        X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+1].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                            && tile1.getGridColumn() == tile2.getGridColumn()-1)
                    {
                        return 37;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //        X
                //        X
                //    X X 0
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()-2].get(0).getTileType())
                            && tile1.getGridRow() == tile2.getGridRow()-1)
                    {
                        return 35;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()+1][tile2.getGridRow()].get(0).getTileType())
                            && tile1.getGridRow() != tile2.getGridRow())
                    {
                        return 300;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                return 3;
            }
        }catch(ArrayIndexOutOfBoundsException aiobe){}
        }
        
        /**
         * 
         *    X
         *    X
         *    0
         * 
         */
        if( (tile1.getGridRow()!= tile2.getGridRow()-1) )
        {
            try {
            if(tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType())
                    && tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()][tile2.getGridRow()-2].get(0).getTileType()))
            {   
                String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
                
                
                
                //   X
                //   X
                // X 0 X
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()-1][tile2.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile2.getGridColumn()+1][tile2.getGridRow()].get(0).getTileType())
                            && tile1.getGridRow() == tile2.getGridRow()+1)
                    {
                        return 47;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile2.getGridColumn()][tile2.getGridRow()+1].get(0).getTileType())
                            && tile1.getGridColumn() != tile2.getGridColumn())
                    {
                        return 400;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                return 4;
            }
            } catch(ArrayIndexOutOfBoundsException aiobe){}
        }
        
        /**
         * 
         *    X X 2
         * 
         */
        if( (tile1.getGridColumn() != tile2.getGridColumn()+1) ){
        try {
            if(tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()-1][tile1.getGridRow()].get(0).getTileType())
                    && tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()-2][tile1.getGridRow()].get(0).getTileType()))
            {     
                String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
                
                //        X
                //    X X 2
                //        X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()+1].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-1].get(0).getTileType())
                            && tile2.getGridColumn() == tile1.getGridColumn()+1) 
                    {
                        return -37;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                //        X
                //        X
                //    X X 2
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-1].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()-2].get(0).getTileType())
                            && tile2.getGridRow() != tile1.getGridRow()-1) 
                    {
                        return -35;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()+1][tile1.getGridRow()].get(0).getTileType())
                            && tile1.getGridRow() != tile2.getGridRow())
                    {
                        return -300;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                return -3;
            }
        } catch(ArrayIndexOutOfBoundsException aiobe){}
        }
        
        /**
         * 
         *    X
         *    X
         *    2
         */
        try{
        if( (tile1.getGridRow()!= tile2.getGridRow()+1) )
        {
            if(tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()][tile1.getGridRow()-1].get(0).getTileType())
                    && tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()][tile1.getGridRow()-2].get(0).getTileType()))
            {   
                String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
                
                
                //   X
                //   X
                // X 2 X
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()-1][tile1.getGridRow()].get(0).getTileType())
                            && tt.equals(tileGrid[tile1.getGridColumn()+1][tile1.getGridRow()].get(0).getTileType())
                            && tile2.getGridRow() == tile1.getGridRow()+1)
                    {
                        return -47;
                    }
                } catch (IndexOutOfBoundsException iobe) {}
                
                try{
                    if(tt.equals(tileGrid[tile1.getGridColumn()][tile1.getGridRow()+1].get(0).getTileType())
                            && tile1.getGridColumn() != tile2.getGridColumn())
                    {
                        return -400;
                    }
                }catch(IndexOutOfBoundsException iobe){}
                return -4;
            }
        }
        } catch (ArrayIndexOutOfBoundsException aiobe) {}
        /**
         * 
         *    X 0 X
         * 
         */
        if( (tile1.getGridColumn() != tile2.getGridColumn()+1 && tile1.getGridColumn() != tile2.getGridColumn()-1) ){
            if(tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()+1][tile2.getGridRow()].get(0).getTileType())
                    && tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()-1][tile2.getGridRow()].get(0).getTileType()))
            {
                String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
                 
                return 5;
            }
        }
        
        /**
         * 
         *    X
         *    0
         *    X
         * 
         */
        if( (tile1.getGridRow() != tile2.getGridRow()+1 && tile1.getGridRow() != tile2.getGridRow()-1) ){
            if(tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()][tile2.getGridRow()+1].get(0).getTileType())
                    && tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile2.getGridColumn()][tile2.getGridRow()-1].get(0).getTileType()))
            {  
                String tt = tileGrid[tile1.getGridColumn()][tile1.getGridRow()].get(0).getTileType();
                
                return 6;
            }
        }
        
        /**
         * 
         *    X 2 X
         * 
         */
        if( (tile1.getGridColumn() != tile2.getGridColumn()+1 && tile1.getGridColumn() != tile2.getGridColumn()-1) ){
            if(tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()+1][tile1.getGridRow()].get(0).getTileType())
                    && tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()-1][tile1.getGridRow()].get(0).getTileType()))
            {    
                String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
               
                return -5;
            }
        }
        
        /**
         * 
         *    X
         *    2
         *    X
         * 
         */
        if(  (tile1.getGridRow() != tile2.getGridRow()+1 && tile1.getGridRow() != tile2.getGridRow()-1) ){
            if(tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()][tile1.getGridRow()+1].get(0).getTileType())
                    && tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType().equals(
                    tileGrid[tile1.getGridColumn()][tile1.getGridRow()-1].get(0).getTileType()))
            {   
                String tt = tileGrid[tile2.getGridColumn()][tile2.getGridRow()].get(0).getTileType();
                
                return -6;
            }
        }
        return 0;
    }
    
    public boolean areTilesMoving()
    {
        Iterator<LullabyLandSagaTile> movingList = getMovingTiles();
        while (movingList.hasNext())
        {
            System.out.println("WAIT!! Tiles are moving!");
            return true;
        }
        return false;
    }
    
    /**
     * This method attempts to select the selectTile argument. Note that
     * this may be the first or second selected tile. If a tile is already
     * selected, it will attempt to process a isNeighbor/move.
     * 
     * @param selectTile The tile to select.
     */
    public void selectTile(LullabyLandSagaTile selectTile) throws InterruptedException
    {
        if(areTilesMoving())
        {
            return;
        }
        
        scoreMultiplier = 1;
        
        // IF IT'S ALREADY THE SELECTED TILE, DESELECT IT
        if (selectTile == selectedTile)
        {
            clearAllIncorrect();
            selectedTile = null;
            if(selectTile.getState().equals(SELECTED_STATE))
            {
                selectTile.setState(VISIBLE_STATE);
            } else if(selectTile.getState().equals(SPECIAL_SELECTED_STATE))
            {
                selectTile.setState(stateHolder);
            }
            return;
        }

        // NOT SELECTED SO TRY TO
        if (selectedTile == null)
        {
            selectedTile = selectTile;
            if(selectedTile.getState().equals(VISIBLE_STATE))
            {
                selectedTile.setState(SELECTED_STATE);
            } else {
                    stateHolder = selectedTile.getState();
                    selectedTile.setState(SPECIAL_SELECTED_STATE);
            }
            return;
        }
        // THEY DON'T MATCH, GIVE SOME AUDIO FEEDBACK
        else
        {
            if(!selectTile.isNeighbor(selectedTile))
            {
                if(selectedTile.getState().equals(SELECTED_STATE))
                {
                    selectedTile.setState(VISIBLE_STATE);
                } else if(selectedTile.getState().equals(SPECIAL_SELECTED_STATE))
                {
                    selectedTile.setState(stateHolder);
                }
                selectedTile = selectTile;
                if(selectedTile.getState().equals(VISIBLE_STATE)
                        || selectedTile.getState().equals(INVISIBLE_STATE))
                {
                    selectedTile.setState(SELECTED_STATE);
                } else {
                        stateHolder = selectedTile.getState();
                        selectedTile.setState(SPECIAL_SELECTED_STATE);
                }
                return;
            } else {
                
                LullabyLandSagaMove move = new LullabyLandSagaMove();
                move.col2 = selectTile.getGridColumn();
                move.row2 = selectTile.getGridRow();
                move.col1 = selectedTile.getGridColumn();
                move.row1 = selectedTile.getGridRow();
                
                int valid = isValidMove(selectedTile, selectTile);
                
                // REMOVE THE MOVE TILES FROM THE GRID
                ArrayList<LullabyLandSagaTile> stack1 = tileGrid[move.col1][move.row1];
                ArrayList<LullabyLandSagaTile> stack2 = tileGrid[move.col2][move.row2];        
                LullabyLandSagaTile tile1 = stack1.remove(stack1.size()-1);
                LullabyLandSagaTile tile2 = stack2.remove(stack2.size()-1);
                
                // MAKE SURE BOTH ARE UNSELECTED
                if(tile1.getState().equals(SELECTED_STATE))
                {
                    tile1.setState(VISIBLE_STATE);
                } else if(tile1.getState().equals(SPECIAL_SELECTED_STATE)) {
                    tile1.setState(stateHolder);
                }
                float x = tile1.getX();
                float y = tile1.getY();
                float x2 = tile2.getX();
                float y2 = tile2.getY();
                tile1.setTarget(tile2.getX(), tile2.getY());
                if(tile2.getState().equals(SELECTED_STATE))
                {
                    tile2.setState(VISIBLE_STATE);
                } else if(tile2.getState().equals(SPECIAL_SELECTED_STATE)) {
                    tile2.setState(stateHolder);
                }
                tile2.setTarget(x, y);

                // SEND THEM TO THE STACK
                tile1.startMovingToTarget(4);
                tile2.startMovingToTarget(4);

                // MAKE SURE THEY MOVE
                movingTiles.add(tile1);
                movingTiles.add(tile2);
                
                selectedTile = null;
                
                tileGrid[move.col1][move.row1].remove(tile1);
                tile2.setGridCell(move.col1, move.row1);
                tileGrid[move.col1][move.row1].add(tile2);
                
                tileGrid[move.col2][move.row2].remove(tile2);
                tile1.setGridCell(move.col2, move.row2);
                tileGrid[move.col2][move.row2].add(tile1);
                
                
                if(valid != 0){
                    turns--;
                    processMove(move, valid);
                } else {
                    stack1.add(tile1);
                    stack2.add(tile2);
                    tile1.setTarget(x, y);
                    tile2.setTarget(x2, y2);
                    
                    // SEND THEM TO THE STACK
                    tile1.startMovingToTarget(4);
                    tile2.startMovingToTarget(4);
                    
                    // MAKE SURE THEY MOVE
                    movingTiles.add(tile1);
                    movingTiles.add(tile2);

                    selectedTile = null;

                    tileGrid[move.col1][move.row1].remove(tile2);
                    tile1.setGridCell(move.col1, move.row1);
                    tileGrid[move.col1][move.row1].add(tile1);

                    tileGrid[move.col2][move.row2].remove(tile1);
                    tile2.setGridCell(move.col2, move.row2);
                    tileGrid[move.col2][move.row2].add(tile2);
                    
                }
            }
        }
        
        if(turns == 0)
        {
            if(score-scoreObjective >=0 && clouds == 0)
            {
                endGameAsWin();
            } else {
                endGameAsLoss();
            }
        }
        
        if(moveOnGrid() == null)
        {
            reset(this.miniGame);
        }
        while(matchOnGrid() != null)
        {
            System.out.println("DM2649 - COMBO MOVE");
            LullabyLandSagaMove move = matchOnGrid();
            int k = isValidMove(tileGrid[move.col1][move.row1].get(0),
                    tileGrid[move.col2][move.row2].get(0));
            processMove(move, k);
        }
        updateAll(this.miniGame);
    }
    
    public void clearAllIncorrect() {
        for (int i = 0; i < gridColumns; i++)
        {
            for (int j = 0; j < gridRows; j++)
            {
                ArrayList<LullabyLandSagaTile> stackSearch = tileGrid[i][j];
                if (stackSearch.size() > 0)
                {
                    // GET THE FIRST TILE
                    LullabyLandSagaTile searchTile = stackSearch.get(stackSearch.size()-1);
                    //if(searchTile.getState().equals(INCORRECTLY_SELECTED_STATE))
                    //    searchTile.setState(VISIBLE_STATE);
                }
            }
        }
    }
    
    /**
     * This method undoes the previous move, sending the two tiles on top
     * of the tile stack back to the game grid.
     */    
    public void undoLastMove()
    {
        if (inProgress() && stackTiles.size() > 1)
        {
            // TAKE THE TOP 2 TILES
            LullabyLandSagaTile topTile = stackTiles.remove(stackTiles.size()-1);
            LullabyLandSagaTile nextToTopTile = stackTiles.remove(stackTiles.size() - 1);
            
            // SET THEIR DESTINATIONS
            float boundaryLeft = miniGame.getBoundaryLeft();
            float boundaryTop = miniGame.getBoundaryTop();
            
            // FIRST TILE 1
            int col = topTile.getGridColumn();
            int row = topTile.getGridRow();
            int z = tileGrid[col][row].size();
            float targetX = this.calculateTileXInGrid(col, z);
            float targetY = this.calculateTileYInGrid(row, z);
            topTile.setTarget(targetX, targetY);
            movingTiles.add(topTile);
            topTile.startMovingToTarget(MAX_TILE_VELOCITY);
            tileGrid[col][row].add(topTile);
            
            // AND THEN TILE 2
            col = nextToTopTile.getGridColumn();
            row = nextToTopTile.getGridRow();
            z = tileGrid[col][row].size();
            targetX = this.calculateTileXInGrid(col, z);
            targetY = this.calculateTileYInGrid(row, z);
            nextToTopTile.setTarget(targetX, targetY);
            movingTiles.add(nextToTopTile);
            nextToTopTile.startMovingToTarget(MAX_TILE_VELOCITY);
            tileGrid[col][row].add(nextToTopTile);
            
            // PLAY THE AUDIO CUE
            miniGame.getAudio().play(LullabyLandSagaPropertyType.UNDO_AUDIO_CUE.toString(), false);   
        }
    }
    
    // OVERRIDDEN METHODS
        // - checkMousePressOnSprites
        // - endGameAsWin
        // - endGameAsLoss
        // - reset
        // - updateAll
        // - updateDebugText

    /**
     * This method provides a custom game response for handling mouse clicks
     * on the game screen. We'll use this to close game dialogs as well as to
     * listen for mouse clicks on grid cells.
     * 
     * @param game The Mahjong game.
     * 
     * @param x The x-axis pixel location of the mouse click.
     * 
     * @param y The y-axis pixel location of the mouse click.
     */
    @Override
    public void checkMousePressOnSprites(MiniGame game, int x, int y)
    {
        // FIGURE OUT THE CELL IN THE GRID
        int col = calculateGridCellColumn(x);
        int row = calculateGridCellRow(y);
        
        // CHECK THE TOP OF THE STACK AT col, row
        try {
            ArrayList<LullabyLandSagaTile> tileStack = tileGrid[col][row];
            if (tileStack.size() > 0)
            {
                // GET AND TRY TO SELECT THE TOP TILE IN THAT CELL, IF THERE IS ONE
                LullabyLandSagaTile testTile = tileStack.get(tileStack.size()-1);
                if (testTile.containsPoint(x, y))
                    selectTile(testTile);
            }
        } catch (ArrayIndexOutOfBoundsException aiobe){} catch (InterruptedException ex) {
            Logger.getLogger(LullabyLandSagaDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    /**
     * Called when the game is won, it will record the ending game time, update
     * the player record, display the win dialog, and play the win animation.
     */
    @Override
    public void endGameAsWin()
    {
        // UPDATE THE GAME STATE USING THE INHERITED FUNCTIONALITY
        super.endGameAsWin();
        
        
        // RECORD IT AS A WIN
        ((LullabyLandSagaMiniGame)miniGame).getPlayerRecord().addWin(currentLevel, 100L);
        ((LullabyLandSagaMiniGame)miniGame).savePlayerRecord();
        
            if( Integer.parseInt(currentLevel.substring(24,25)) == latestLevel)
            {
                latestLevel++;
                System.out.println(currentLevel.substring(24,25));
                ((LullabyLandSagaMiniGame)miniGame).incrementLevelPermissions();
            }
                ((LullabyLandSagaMiniGame)miniGame).switchToLevelWinScreen();
        miniGame.getGUIButtons().get(PLAY_AGAIN_BUTTON_TYPE).setState(VISIBLE_STATE);
        miniGame.getGUIButtons().get(PLAY_AGAIN_BUTTON_TYPE).setEnabled(true);
    }
    @Override
    public void endGameAsLoss()
    {
        // UPDATE THE GAME STATE USING THE INHERITED FUNCTIONALITY
        super.endGameAsLoss();
        
        enableTiles(false);
        
        // RECORD IT AS A WIN
        ((LullabyLandSagaMiniGame)miniGame).getPlayerRecord().addLoss(currentLevel);
        ((LullabyLandSagaMiniGame)miniGame).savePlayerRecord();
        
                ((LullabyLandSagaMiniGame)miniGame).switchToLevelLoseScreen();
        miniGame.getGUIButtons().get(PLAY_AGAIN_BUTTON_TYPE).setState(VISIBLE_STATE);
        miniGame.getGUIButtons().get(PLAY_AGAIN_BUTTON_TYPE).setEnabled(true);
        
        miniGame.getGUIButtons().get(PLAY_AGAIN_BUTTON_TYPE).setEnabled(true);
        
        enableTiles(false);
    }
    /**
     * Called when a game is started, the game grid is reset.
     * 
     * @param game 
     */
    @Override
    public void reset(MiniGame game)
    {
        
        // PUT ALL THE TILES IN ONE PLACE AND MAKE THEM VISIBLE
        moveAllTilesToStack();
        for (LullabyLandSagaTile tile : stackTiles)
        {
            tile.setX(TILE_STACK_X);
            tile.setY(TILE_STACK_Y);
            tile.setState(VISIBLE_STATE);
        }        

        // RANDOMLY ORDER THEM
        Collections.shuffle(stackTiles);
                    
        // NOW LET'S REMOVE THEM FROM THE STACK
        // AND PUT THE TILES IN THE GRID        
        for (int i = 0; i < gridColumns; i++)
        {
            for (int j = 0; j < gridRows; j++)
            {
                for (int k = 0; k < levelGrid[i][j]; k++)
                {
                    // TAKE THE TILE OUT OF THE STACK
                    try{
                    LullabyLandSagaTile tile = stackTiles.remove(stackTiles.size()-1);
                    
                    
                    // PUT IT IN THE GRID
                    tileGrid[i][j].add(tile);
                    tile.setGridCell(i, j);
                    
                    // WE'LL ANIMATE IT GOING TO THE GRID, SO FIGURE
                    // OUT WHERE IT'S GOING AND GET IT MOVING
                    float x = calculateTileXInGrid(i, k);
                    float y = calculateTileYInGrid(j, k);
                    tile.setTarget(x, y);
                    tile.startMovingToTarget(6);
                    movingTiles.add(tile);
                    } catch(ArrayIndexOutOfBoundsException aiobe){}
                }
            }
        }        
        // AND START ALL UPDATES
        beginGame();
        
        // CLEAR ANY WIN OR LOSS DISPLAY\
    }    

    /**
     * Called each frame, this method updates all the game objects.
     * 
     * @param game The Mahjong game to be updated.
     */
    @Override
    public void updateAll(MiniGame game)
    {
        // MAKE SURE THIS THREAD HAS EXCLUSIVE ACCESS TO THE DATA
        try
        {
            game.beginUsingData();
        
            // WE ONLY NEED TO UPDATE AND MOVE THE MOVING TILES
            for (int i = 0; i < movingTiles.size(); i++)
            {
                // GET THE NEXT TILE
                LullabyLandSagaTile tile = movingTiles.get(i);
            
                // THIS WILL UPDATE IT'S POSITION USING ITS VELOCITY
                tile.update(game);
            
                // IF IT'S REACHED ITS DESTINATION, REMOVE IT
                // FROM THE LIST OF MOVING TILES
                if (!tile.isMovingToTarget())
                {
                    movingTiles.remove(tile);
                }
            }
        
            // IF THE GAME IS STILL ON, THE TIMER SHOULD CONTINUE
            if (inProgress())
            {
                // KEEP THE GAME TIMER GOING IF THE GAME STILL IS
                endTime = new GregorianCalendar();
            }
        }
        finally
        {
            // MAKE SURE WE RELEASE THE LOCK WHETHER THERE IS
            // AN EXCEPTION THROWN OR NOT
            game.endUsingData();
        }
    }

    /**
     * This method is for updating any debug text to present to
     * the screen. In a graphical application like this it's sometimes
     * useful to display data in the GUI.
     * 
     * @param game The Mahjong game about which to display info.
     */
    @Override
    public void updateDebugText(MiniGame game)
    {
    }      

    
}