package lullabylandsaga.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.*;
import javax.swing.JFrame;
import lullabylandsaga.data.LullabyLandSagaDataModel;
import mini_game.MiniGame;
import static lullabylandsaga.LullabyLandSagaConstants.*;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import lullabylandsaga.LullabyLandSaga.LullabyLandSagaPropertyType;
import lullabylandsaga.file.LullabyLandSagaFileManager;
import lullabylandsaga.data.LullabyLandSagaRecord;
//import lullabylandsaga.events.BackHandler;
import lullabylandsaga.events.ExitHandler;
import lullabylandsaga.events.LevelScoreHandler;
import lullabylandsaga.events.GameScreenHandler;
import lullabylandsaga.events.LullabyLandKeyHandler;
//import lullabylandsaga.events.NewGameHandler;
import lullabylandsaga.events.SagaScrollHandler;
import lullabylandsaga.events.SplashHandler;
//import lullabylandsaga.events.StatsHandler;
//import lullabylandsaga.events.UndoHandler;

/**
 * This is the actual mini game, as extended from the mini game framework. It
 * manages all the UI elements.
 * 
 * @author Richard McKenna, Thomas Marion
 */
public class LullabyLandSagaMiniGame extends MiniGame
{
    // THE PLAYER RECORD FOR EACH LEVEL, WHICH LIVES BEYOND ONE SESSION
    private LullabyLandSagaRecord record;
    
    // HANDLES ERROR CONDITIONS
    private LullabyLandSagaErrorHandler errorHandler;
    
    // MANAGES LOADING OF LEVELS AND THE PLAYER RECORDS FILES
    private LullabyLandSagaFileManager fileManager;
    
    // THE SCREEN CURRENTLY BEING PLAYED
    private String currentScreenState;
    
    private int currentLevel = 1;
    
    private String levelHolder;
    
    // ACCESSOR METHODS
        // - getPlayerRecord
        // - getErrorHandler
        // - getFileManager
        // - isCurrentScreenState
    
    
    /**
     * Accessor method for getting the player record object, which
     * summarizes the player's record on all levels.
     * 
     * @return The player's complete record.
     */
    public LullabyLandSagaRecord getPlayerRecord() 
    { 
        return record; 
    }

    /**
     * Accessor method for getting the application's error handler.
     * 
     * @return The error handler.
     */
    public LullabyLandSagaErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * Accessor method for getting the app's file manager.
     * 
     * @return The file manager.
     */
    public LullabyLandSagaFileManager getFileManager()
    {
        return fileManager;
    }

    public int getCurrentLevel()
    {
        return currentLevel;
    }
    
    /**
     * Used for testing to see if the current screen state matches
     * the testScreenState argument. If it mates, true is returned,
     * else false.
     * 
     * @param testScreenState Screen state to test against the 
     * current state.
     * 
     * @return true if the current state is testScreenState, false otherwise.
     */
    public boolean isCurrentScreenState(String testScreenState)
    {
        return testScreenState.equals(currentScreenState);
    }
    
    public void setCurrentLevel(int i){
        currentLevel = i;
    }

    public void incrementLevelPermissions()
    {
        currentLevel++;
    }
    
    // SERVICE METHODS
        // - displayStats
        // - savePlayerRecord
        // - switchToGameScreen
        // - switchToSplashScreen
        // - updateBoundaries
   
    
    
    /**
     * This method forces the file manager to save the current player record.
     */
    public void savePlayerRecord()
    {
        // THIS CURRENTLY DOES NOTHING, INSTEAD, IT MUST SAVE ALL THE
        // PLAYER RECORDS IN THE SAME FORMAT IT IS BEING LOADED
    }
    
    public void switchToLevelWinScreen()
    {
        String number = Character.toString(((LullabyLandSagaDataModel)data).getCurrentLevel().charAt(24));
        if(((LullabyLandSagaDataModel)data).getCurrentLevel().charAt(25) == '0' )
        {
            number = number.concat("0");
        }
        String state = "LEVEL_X_WIN_SCREEN_STATE";
        String end = state.substring(7,state.length());
        state = state.substring(0,6).concat(number).concat(end);
        guiDecor.get(BACKGROUND_TYPE).setState(state);
        currentScreenState = state;
        
        
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setEnabled(true);
        
        ((LullabyLandSagaDataModel)data).enableTiles(false);
        
    }
    
    public void switchToLevelLoseScreen()
    {
        String number = Character.toString(((LullabyLandSagaDataModel)data).getCurrentLevel().charAt(24));
        if(((LullabyLandSagaDataModel)data).getCurrentLevel().charAt(25) == '0' )
        {
            number = number.concat("0");
        }
        String state = "LEVEL_X_LOSE_SCREEN_STATE";
        String end = state.substring(7,state.length());
        state = state.substring(0,6).concat(number).concat(end);
        guiDecor.get(BACKGROUND_TYPE).setState(state);
        currentScreenState = state;
        
        
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setEnabled(true);
        
    }
    
    public void switchToLevelScoreScreen(int level)
    {
        currentLevel = level;
        String screen = "LEVEL_";
        screen = screen.concat(Integer.toString(level)).concat("_SCORE_SCREEN_STATE");
        levelHolder = screen.substring(6,7);
        if(screen.length() == 25)
        {
            levelHolder = levelHolder.concat(Character.toString(screen.charAt(7)));
        }
        guiDecor.get(BACKGROUND_TYPE).setState(screen);
        currentScreenState = screen;
        
        // DEACTIVATE THE TOOLBAR AND ITS CONTROLS
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setEnabled(false);
        disableLevelButtons();
        guiButtons.get("Quit").setState(INVISIBLE_STATE);
        guiButtons.get("Quit").setEnabled(false);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SAGA_RETURN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SAGA_RETURN_BUTTON_TYPE).setEnabled(false);
        
        
        // ACTIVATE GAME SCORE SCREEN BUTTONS
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setEnabled(true);
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setEnabled(true);
        
        ((LullabyLandSagaDataModel)data).enableTiles(false);
        
        
        System.out.println(currentScreenState);
        
    }
    
    /**
     * This method switches the application to the game screen, making
     * all the appropriate UI controls visible & invisible.
     */
    public void switchToGameScreen()
    {
        
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        ((LullabyLandSagaDataModel)data).moveAllTilesToStack();
        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(GAME_SCREEN_STATE);
        
        // DEACTIVATE THE LEVEL SELECT BUTTONS
        ArrayList<String> splashes = props.getPropertyOptionsList(LullabyLandSagaPropertyType.SPLASH_OPTIONS);
        for (String splash : splashes)
        {
            guiButtons.get(splash).setState(INVISIBLE_STATE);
            guiButtons.get(splash).setEnabled(false);
        }
        
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setEnabled(false);

        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setEnabled(true);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setState(VISIBLE_STATE);
       
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        
        // MOVE THE TILES TO THE STACK AND MAKE THEM VISIBLE
        ((LullabyLandSagaDataModel)data).enableTiles(true);
        data.reset(this);
        
        // AND CHANGE THE SCREEN STATE
        currentScreenState = GAME_SCREEN_STATE;
    
    }
    
    /**
     * This method switches the application to the splash screen, making
     * all the appropriate UI controls visible & invisible.
     */    
    public void switchToSplashScreen()
    {
        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(SPLASH_SCREEN_STATE);
        
        
        // DEACTIVATE THE TOOLBAR CONTROLS
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setEnabled(false);
        
        
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setEnabled(false);
        guiButtons.get("Quit").setState(INVISIBLE_STATE);
        guiButtons.get("Quit").setEnabled(false);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setEnabled(false);
        
        // ACTIVATE THE LEVEL SELECT BUTTONS
        // DEACTIVATE THE LEVEL SELECT BUTTONS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        ArrayList<String> splashes = props.getPropertyOptionsList(LullabyLandSagaPropertyType.SPLASH_OPTIONS);
        for (String splash : splashes)
        {
            guiButtons.get(splash).setState(VISIBLE_STATE);
            guiButtons.get(splash).setEnabled(true);
        }        

        // DEACTIVATE ALL DIALOGS
        guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
        //guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);

        // HIDE THE TILES
        ((LullabyLandSagaDataModel)data).enableTiles(false);

        // MAKE THE CURRENT SCREEN THE SPLASH SCREEN
        currentScreenState = SPLASH_SCREEN_STATE;

    }
    
    public void switchToSagaScreen()
    {         
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(BACKGROUND_TYPE).setState(SAGA_SCREEN_1_STATE);
        
        // DEACTIVATE THE LEVEL SELECT BUTTONS
        ArrayList<String> splashes = props.getPropertyOptionsList(LullabyLandSagaPropertyType.SPLASH_OPTIONS);
        for (String splash : splashes)
        {
            if(splash.equals("Quit"))
            {
                break;
            }
            guiButtons.get(splash).setState(INVISIBLE_STATE);
            guiButtons.get(splash).setEnabled(false);
        }
        
        // ACTIVATE THE TOOLBAR AND ITS CONTROLS
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setState(INCORRECTLY_SELECTED_STATE);
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setEnabled(true);
        
        guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setEnabled(true);
        try{
        enableValidLevels();
        }catch(NullPointerException npe){}
        
        guiButtons.get("Quit").setState(VISIBLE_STATE);
        guiButtons.get("Quit").setEnabled(true);
        
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setEnabled(false);
        
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setEnabled(false);
        
        currentScreenState = SAGA_SCREEN_1_STATE;
    }
    
    public void enableValidLevels()
    {
        
        int j = ((LullabyLandSagaDataModel)data).getLatestLevel();
        for(int i = 2; i < 11; i++)
        {
            String level = "LEVEL_SELECT_";
            level = level.concat(Integer.toString(i)).concat("_BUTTON_TYPE");
            if(i<=j)
            {
                guiButtons.get(level).setState(VISIBLE_STATE);
                guiButtons.get(level).setEnabled(true);
            } else {
                guiButtons.get(level).setState(INVISIBLE_STATE);
                guiButtons.get(level).setEnabled(false);
            }
        }
    }
    
    public void disableLevelButtons()
    {
        for(int i=1; i<11; i++)
        {
            try {
            String level = "LEVEL_SELECT_";
            level = level.concat(Integer.toString(i)).concat("_BUTTON_TYPE");
            guiButtons.get(level).setState(INVISIBLE_STATE);
            guiButtons.get(level).setEnabled(false);
            } catch(NullPointerException npe){}
        }
    }
    
    public void updateSagaScreen(int Dir)
    {
        String currentScreen = currentScreenState;
        char num = currentScreen.charAt(12);
        char num2 = num;
        if(Dir == 1)
        {
            num2++;
            currentScreenState = currentScreen.replace(num, num2);
        } else {
            num2--;
            currentScreenState = currentScreen.replace(num, num2);
        }
        
        if(num2 == '5')
        {
            guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setEnabled(false);
            guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setState(INCORRECTLY_SELECTED_STATE);
        }
        else if(num2 == '1')
        {
            guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setEnabled(false);
            guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setState(INCORRECTLY_SELECTED_STATE);
            guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setState(VISIBLE_STATE);
            guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setEnabled(true);
            enableValidLevels();
        }
        else {
            guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setEnabled(true);
            guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setState(VISIBLE_STATE);
            guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setEnabled(true);
            guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setState(VISIBLE_STATE);
            disableLevelButtons();
        }
        
        guiDecor.get(BACKGROUND_TYPE).setState(currentScreenState);
    }
    
    /**
     * This method updates the game grid boundaries, which will depend
     * on the level loaded.
     */    
    public void updateBoundaries()
    {
        // NOTE THAT THE ONLY ONES WE CARE ABOUT ARE THE LEFT & TOP BOUNDARIES
        float totalWidth = ((LullabyLandSagaDataModel)data).getGridColumns() * TILE_IMAGE_WIDTH;
        float halfTotalWidth = totalWidth/2.0f;
        float halfViewportWidth = data.getGameWidth()/2.0f;
        boundaryLeft = halfViewportWidth - halfTotalWidth + 20;

        // THE LEFT & TOP BOUNDARIES ARE WHERE WE START RENDERING TILES IN THE GRID
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        float topOffset = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.GAME_TOP_OFFSET.toString()));
        float totalHeight = ((LullabyLandSagaDataModel)data).getGridRows() * TILE_IMAGE_HEIGHT;
        float halfTotalHeight = totalHeight/2.0f;
        float halfViewportHeight = (data.getGameHeight() - topOffset)/2.0f;
        boundaryTop = topOffset + halfViewportHeight - halfTotalHeight - 30;
    }
    
    // METHODS OVERRIDDEN FROM MiniGame
        // - initAudioContent
        // - initData
        // - initGUIControls
        // - initGUIHandlers
        // - reset
        // - updateGUI

    @Override
    /**
     * Initializes the sound and music to be used by the application.
     */
    public void initAudioContent()
    {
        try
        {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String audioPath = props.getProperty(LullabyLandSagaPropertyType.AUDIO_PATH);

            // LOAD ALL THE AUDIO
            loadAudioCue(LullabyLandSagaPropertyType.SELECT_AUDIO_CUE);
            loadAudioCue(LullabyLandSagaPropertyType.MATCH_AUDIO_CUE);
            loadAudioCue(LullabyLandSagaPropertyType.NO_MATCH_AUDIO_CUE);
            loadAudioCue(LullabyLandSagaPropertyType.BLOCKED_TILE_AUDIO_CUE);
            loadAudioCue(LullabyLandSagaPropertyType.UNDO_AUDIO_CUE);
            loadAudioCue(LullabyLandSagaPropertyType.WIN_AUDIO_CUE);
            loadAudioCue(LullabyLandSagaPropertyType.SPLASH_SCREEN_SONG_CUE);
            loadAudioCue(LullabyLandSagaPropertyType.GAMEPLAY_SONG_CUE);

            // PLAY THE WELCOME SCREEN SONG
            audio.play(LullabyLandSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString(), true);
        }
        catch(UnsupportedAudioFileException | IOException | LineUnavailableException | InvalidMidiDataException | MidiUnavailableException e)
        {
            errorHandler.processError(LullabyLandSagaPropertyType.AUDIO_FILE_ERROR);
        }        
    }

    /**
     * This helper method loads the audio file associated with audioCueType,
     * which should have been specified via an XML properties file.
     */
    private void loadAudioCue(LullabyLandSagaPropertyType audioCueType) 
            throws  UnsupportedAudioFileException, IOException, LineUnavailableException, 
                    InvalidMidiDataException, MidiUnavailableException
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String audioPath = props.getProperty(LullabyLandSagaPropertyType.AUDIO_PATH);
        String cue = props.getProperty(audioCueType.toString());
        audio.loadAudio(audioCueType.toString(), audioPath + cue);        
    }
    
    /**
     * Initializes the game data used by the application. Note
     * that it is this method's obligation to construct and set
     * this Game's custom GameDataModel object as well as any
     * other needed game objects.
     */
    @Override
    public void initData()
    {        
        // INIT OUR ERROR HANDLER
        errorHandler = new LullabyLandSagaErrorHandler(window);
        
        // INIT OUR FILE MANAGER
        fileManager = new LullabyLandSagaFileManager(this);

        // LOAD THE PLAYER'S RECORD FROM A FILE
        record = fileManager.loadRecord();
        
        // INIT OUR DATA MANAGER
        data = new LullabyLandSagaDataModel(this);

        // LOAD THE GAME DIMENSIONS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        int gameWidth = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.GAME_WIDTH.toString()));
        int gameHeight = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.GAME_HEIGHT.toString()));
        data.setGameDimensions(gameWidth, gameHeight);

        // THIS WILL CHANGE WHEN WE LOAD A LEVEL
        boundaryLeft = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.GAME_LEFT_OFFSET.toString()));
        boundaryTop = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.GAME_TOP_OFFSET.toString()));
        boundaryRight = gameWidth - boundaryLeft;
        boundaryBottom = gameHeight;
    }
    
    /**
     * Initializes the game controls, like buttons, used by
     * the game application. Note that this includes the tiles,
     * which serve as buttons of sorts.
     */
    @Override
    public void initGUIControls()
    {
        // WE'LL USE AND REUSE THESE FOR LOADING STUFF
        BufferedImage img;
        float x, y;
        SpriteType sT;
        Sprite s;
 
        // FIRST PUT THE ICON IN THE WINDOW
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(LullabyLandSagaPropertyType.IMG_PATH);        
        String windowIconFile = props.getProperty(LullabyLandSagaPropertyType.WINDOW_ICON);
        img = loadImage(imgPath + windowIconFile);
        window.setIconImage(img);

        // CONSTRUCT THE PANEL WHERE WE'LL DRAW EVERYTHING
        canvas = new LullabyLandSagaPanel(this, (LullabyLandSagaDataModel)data);
        
        // LOAD THE BACKGROUNDS, WHICH ARE GUI DECOR
        currentScreenState = SPLASH_SCREEN_STATE;
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.SPLASH_SCREEN_IMAGE_NAME));
        sT = new SpriteType(BACKGROUND_TYPE);
        sT.addState(SPLASH_SCREEN_STATE, img);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_1_IMAGE_NAME));
        sT.addState(SAGA_SCREEN_1_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, SAGA_SCREEN_1_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        
        
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_1_WIN_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_1_WIN_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_1_WIN_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_1_LOSE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_1_LOSE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_1_LOSE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        
        
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_2_IMAGE_NAME));
        sT.addState(SAGA_SCREEN_2_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, SAGA_SCREEN_2_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_3_IMAGE_NAME));
        sT.addState(SAGA_SCREEN_3_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, SAGA_SCREEN_3_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_4_IMAGE_NAME));
        sT.addState(SAGA_SCREEN_4_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, SAGA_SCREEN_4_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_5_IMAGE_NAME));
        sT.addState(SAGA_SCREEN_5_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, SAGA_SCREEN_5_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_1_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_1_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_1_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_2_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_2_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_2_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_3_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_3_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_3_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_4_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_4_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_4_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_5_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_5_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_5_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_6_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_6_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_6_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_7_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_7_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_7_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_8_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_8_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_8_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_9_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_9_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_9_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.LEVEL_10_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_10_SCORE_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, LEVEL_10_SCORE_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        
        
        img = loadImage(imgPath + props.getProperty(LullabyLandSagaPropertyType.GAME_BACKGROUND_IMAGE_NAME));
        sT.addState(GAME_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, SPLASH_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        
        // ADD A BUTTON FOR EACH LEVEL AVAILABLE
        ArrayList<String> splashes = props.getPropertyOptionsList(LullabyLandSagaPropertyType.SPLASH_OPTIONS);
        ArrayList<String> splashImageNames = props.getPropertyOptionsList(LullabyLandSagaPropertyType.SPLASH_IMAGE_OPTIONS);
        ArrayList<String> splashMouseOverImageNames = props.getPropertyOptionsList(LullabyLandSagaPropertyType.SPLASH_MOUSE_OVER_IMAGE_OPTIONS);
        float totalWidth = splashes.size() * (SPLASH_BUTTON_WIDTH + SPLASH_BUTTON_MARGIN) - SPLASH_BUTTON_MARGIN;
        float totalHeight = splashes.size() * (SPLASH_BUTTON_HEIGHT + SPLASH_BUTTON_MARGIN) - SPLASH_BUTTON_MARGIN;
        float gameWidth = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.GAME_WIDTH));
        float gameHeight = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.GAME_HEIGHT));
        x = 120 + (gameWidth)/2.0f;
        y = 80 + (gameHeight - totalHeight)/2.0f;
        for (int i = 0; i < splashes.size(); i++)
        {
            sT = new SpriteType(SPLASH_SCREEN_BUTTON_TYPE);
            img = loadImageWithColorKey(imgPath + splashImageNames.get(i), COLOR_KEY);
            sT.addState(VISIBLE_STATE, img);
            img = loadImageWithColorKey(imgPath + splashMouseOverImageNames.get(i), COLOR_KEY);
            sT.addState(MOUSE_OVER_STATE, img);
            s = new Sprite(sT, x, y, 0, 0, VISIBLE_STATE);
            guiButtons.put(splashes.get(i), s);
            y += SPLASH_BUTTON_HEIGHT + SPLASH_BUTTON_MARGIN;
        }
        
        // ADD THE CONTROLS ALONG THE NORTH OF THE GAME SCREEN
                
        // THEN THE SAGA SCREEN LEFT BUTTON
        String leftButton = props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_LEFT_IMAGE_NAME);
        sT = new SpriteType(SAGA_SCREEN_LEFT_BUTTON_TYPE);
	img = loadImage(imgPath + leftButton);
        sT.addState(VISIBLE_STATE, img);
        String leftMouseOverButton = props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_LEFT_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + leftMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        String leftUnavailableButton = props.getProperty(LullabyLandSagaPropertyType.SAGA_LEFT_UNAVAILABLE_IMAGE_NAME);
        img = loadImage(imgPath + leftUnavailableButton);
        sT.addState(INCORRECTLY_SELECTED_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X, SAGA_LEFT_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(SAGA_SCREEN_LEFT_BUTTON_TYPE, s);
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setEnabled(false);
        
        // AND THE SAGA SCREEN RIGHT BUTTON
        String backButton = props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_RIGHT_IMAGE_NAME);
        sT = new SpriteType(SAGA_SCREEN_RIGHT_BUTTON_TYPE);
        img = loadImage(imgPath + backButton);
        sT.addState(VISIBLE_STATE, img);
        String backMouseOverButton = props.getProperty(LullabyLandSagaPropertyType.SAGA_SCREEN_RIGHT_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + backMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        String rightUnavailableButton = props.getProperty(LullabyLandSagaPropertyType.SAGA_RIGHT_UNAVAILABLE_IMAGE_NAME);
        img = loadImage(imgPath + rightUnavailableButton);
        sT.addState(INCORRECTLY_SELECTED_STATE, img);
        s = new Sprite(sT, SAGA_RIGHT_BUTTON_X, SAGA_RIGHT_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(SAGA_SCREEN_RIGHT_BUTTON_TYPE, s);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 1 BUTTON
        String levelButton = props.getProperty(LullabyLandSagaPropertyType.LEVEL_1_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_1_BUTTON_TYPE);
        img = loadImage(imgPath + levelButton);
        sT.addState(VISIBLE_STATE, img);
        String levelButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_1_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + levelButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+127, SAGA_LEFT_BUTTON_Y-93, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_1_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 2 BUTTON
        String level2Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_2_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_2_BUTTON_TYPE);
        img = loadImage(imgPath+level2Button);
        sT.addState(VISIBLE_STATE, img);
        String level2ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_2_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level2ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+166, SAGA_LEFT_BUTTON_Y-106, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_2_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_2_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 3 BUTTON
        String level3Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_3_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_3_BUTTON_TYPE);
        img = loadImage(imgPath+level3Button);
        sT.addState(VISIBLE_STATE, img);
        String level3ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_3_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level3ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+196, SAGA_LEFT_BUTTON_Y-116, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_3_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_3_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 4 BUTTON
        String level4Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_4_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_4_BUTTON_TYPE);
        img = loadImage(imgPath+level4Button);
        sT.addState(VISIBLE_STATE, img);
        String level4ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_4_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level4ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+226, SAGA_LEFT_BUTTON_Y-130, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_4_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_4_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 5 BUTTON
        String level5Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_5_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_5_BUTTON_TYPE);
        img = loadImage(imgPath+level5Button);
        sT.addState(VISIBLE_STATE, img);
        String level5ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_5_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level5ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+256, SAGA_LEFT_BUTTON_Y-130, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_5_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_5_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 6 BUTTON
        String level6Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_6_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_6_BUTTON_TYPE);
        img = loadImage(imgPath+level6Button);
        sT.addState(VISIBLE_STATE, img);
        String level6ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_6_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level6ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+286, SAGA_LEFT_BUTTON_Y-130, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_6_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_6_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 6 BUTTON
        String level7Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_7_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_7_BUTTON_TYPE);
        img = loadImage(imgPath+level7Button);
        sT.addState(VISIBLE_STATE, img);
        String level7ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_7_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level7ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+306, SAGA_LEFT_BUTTON_Y-130, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_7_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_7_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 8 BUTTON
        String level8Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_8_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_8_BUTTON_TYPE);
        img = loadImage(imgPath+level8Button);
        sT.addState(VISIBLE_STATE, img);
        String level8ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_8_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level8ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+326, SAGA_LEFT_BUTTON_Y-130, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_8_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_8_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 9 BUTTON
        String level9Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_9_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_9_BUTTON_TYPE);
        img = loadImage(imgPath+level9Button);
        sT.addState(VISIBLE_STATE, img);
        String level9ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_9_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level9ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+356, SAGA_LEFT_BUTTON_Y-130, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_9_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_9_BUTTON_TYPE).setEnabled(false);
        
        // ADD THE LEVEL 6 BUTTON
        String level10Button = props.getProperty(LullabyLandSagaPropertyType.LEVEL_10_IMAGE_NAME);
        sT = new SpriteType(LEVEL_SELECT_10_BUTTON_TYPE);
        img = loadImage(imgPath+level10Button);
        sT.addState(VISIBLE_STATE, img);
        String level10ButtonMouseOver = props.getProperty(LullabyLandSagaPropertyType.LEVEL_10_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + level10ButtonMouseOver);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SAGA_LEFT_BUTTON_X+386, SAGA_LEFT_BUTTON_Y-130, 0, 0, INVISIBLE_STATE);
        guiButtons.put(LEVEL_SELECT_10_BUTTON_TYPE, s);
        guiButtons.get(LEVEL_SELECT_10_BUTTON_TYPE).setEnabled(false);
        
        // ADD PLAY LEVEL BUTTON
        String playLevelButton = props.getProperty(LullabyLandSagaPropertyType.SCORE_SCREEN_PLAY_LEVEL_IMAGE_NAME);
        sT = new SpriteType(SCORE_TO_GAME_SCREEN_BUTTON_TYPE);
        img = loadImage(imgPath + playLevelButton);
        sT.addState(VISIBLE_STATE, img);
        String playLevelMouse = props.getProperty(LullabyLandSagaPropertyType.SCORE_PLAY_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + playLevelMouse);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, 250,280,0,0, INVISIBLE_STATE);
        guiButtons.put(SCORE_TO_GAME_SCREEN_BUTTON_TYPE, s);
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setEnabled(false);
        
        // ADD BACK BUTTON
        String backToSaga = props.getProperty(LullabyLandSagaPropertyType.SCORE_SCREEN_BACK_IMAGE_NAME);
        sT = new SpriteType(SCORE_SCREEN_BUTTON_TYPE);
        img = loadImage(imgPath + backToSaga);
        sT.addState(VISIBLE_STATE, img);
        String backToSagaMouse = props.getProperty(LullabyLandSagaPropertyType.SCORE_SCREEN_BACK_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + backToSagaMouse);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, 250,380,0,0, INVISIBLE_STATE);
        guiButtons.put(SCORE_SCREEN_BUTTON_TYPE, s);
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setEnabled(false);
        
        // BACK ARROW FOR GAMEPLAY SCREEN
        String backArrow = props.getProperty(LullabyLandSagaPropertyType.BACK_ARROW_IMAGE_NAME);
        sT = new SpriteType(GAME_TO_SCORE_SCREEN_BUTTON_TYPE);
        img = loadImage(imgPath + backArrow);
        sT.addState(VISIBLE_STATE, img);
        String backArrowMouse = props.getProperty(LullabyLandSagaPropertyType.BACK_ARROW_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + backArrowMouse);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, 10,40,0,0,INVISIBLE_STATE);
        guiButtons.put(GAME_TO_SCORE_SCREEN_BUTTON_TYPE, s);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setEnabled(false);
        
        // PLAY AGAIN BUTTON FOR WIN/LOSS CONDITION
        String playAgain = props.getProperty(LullabyLandSagaPropertyType.PLAY_AGAIN_IMAGE_NAME);
        sT = new SpriteType(PLAY_AGAIN_BUTTON_TYPE);
        img = loadImage(imgPath + playAgain);
        sT.addState(VISIBLE_STATE, img);
        String playAgainMO = props.getProperty(LullabyLandSagaPropertyType.PLAY_AGAIN_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + playAgainMO);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, 250, 280, 0, 0, INVISIBLE_STATE);
        guiButtons.put(PLAY_AGAIN_BUTTON_TYPE, s);
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setEnabled(false);
        
        // RETURN TO SAGA BUTTON FOR WIN/LOSS CONDITION
        String sagaReturn = props.getProperty(LullabyLandSagaPropertyType.SAGA_RETURN_IMAGE_NAME);
        sT = new SpriteType(SAGA_RETURN_BUTTON_TYPE);
        img = loadImage(imgPath + sagaReturn);
        sT.addState(VISIBLE_STATE, img);
        String sagaReturnMO = props.getProperty(LullabyLandSagaPropertyType.SAGA_RETURN_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + sagaReturnMO);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, 250, 380, 0, 0, INVISIBLE_STATE);
        guiButtons.put(SAGA_RETURN_BUTTON_TYPE, s);
        guiButtons.get(SAGA_RETURN_BUTTON_TYPE).setEnabled(false);
        
        // AND THE WIN CONDITION DISPLAY
        String winDisplay = props.getProperty(LullabyLandSagaPropertyType.WIN_DIALOG_IMAGE_NAME);
        sT = new SpriteType(WIN_DIALOG_TYPE);
        img = loadImageWithColorKey(imgPath + winDisplay, COLOR_KEY);
        sT.addState(VISIBLE_STATE, img);
        x = (data.getGameWidth()/2) - (img.getWidth(null)/2);
        y = (data.getGameHeight()/2) - (img.getHeight(null)/2);
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDialogs.put(WIN_DIALOG_TYPE, s);
        
        // AND THE LOSS CONDITION DISPLAY
        String lossDisplay = props.getProperty(LullabyLandSagaPropertyType.LEVEL_1_LOSE_SCREEN_IMAGE_NAME);
        sT = new SpriteType(LOSS_DIALOG_TYPE);
        img = loadImageWithColorKey(imgPath + lossDisplay, COLOR_KEY);
        sT.addState(VISIBLE_STATE, img);
        x = (data.getGameWidth()/2) - (img.getWidth(null)/2);
        y = (data.getGameHeight()/2) - (img.getHeight(null)/2);
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDialogs.put(LOSS_DIALOG_TYPE, s);
		        
        // THEN THE TILES STACKED TO THE TOP LEFT
        ((LullabyLandSagaDataModel)data).initTiles();
    }
    
    public String getLevelHolder()
    {
        return levelHolder;
    }
    
    /**
     * Initializes the game event handlers for things like
     * game gui buttons.
     */
    @Override
    public void initGUIHandlers()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String dataPath = props.getProperty(LullabyLandSagaPropertyType.DATA_PATH);
        
        // WE'LL HAVE A CUSTOM RESPONSE FOR WHEN THE USER CLOSES THE WINDOW
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ExitHandler eh = new ExitHandler(this);
        window.addWindowListener(eh);
        
        // LEVEL BUTTON EVENT HANDLERS
        ArrayList<String> splashes = props.getPropertyOptionsList(LullabyLandSagaPropertyType.SPLASH_OPTIONS);
        for (String splashButton : splashes)
        {        
            SplashHandler slh = new SplashHandler(this, splashButton);
            guiButtons.get(splashButton).setActionListener(slh);
        }   
              
        // SAGA SCROLL EVENT HANDLERS
        SagaScrollHandler ssrh = new SagaScrollHandler(this, 1);
        guiButtons.get(SAGA_SCREEN_RIGHT_BUTTON_TYPE).setActionListener(ssrh);
        SagaScrollHandler sslh = new SagaScrollHandler(this, 0);
        guiButtons.get(SAGA_SCREEN_LEFT_BUTTON_TYPE).setActionListener(sslh);
        
        // LEVEL SELECT HANDLER
        LevelScoreHandler lsh = new LevelScoreHandler(this, 1);
        LevelScoreHandler lsh2 = new LevelScoreHandler(this,2);
        LevelScoreHandler lsh3 = new LevelScoreHandler(this,3);
        LevelScoreHandler lsh4 = new LevelScoreHandler(this,4);
        LevelScoreHandler lsh5 = new LevelScoreHandler(this,5);
        LevelScoreHandler lsh6 = new LevelScoreHandler(this,6);
        LevelScoreHandler lsh7 = new LevelScoreHandler(this,7);
        LevelScoreHandler lsh8 = new LevelScoreHandler(this,8);
        LevelScoreHandler lsh9 = new LevelScoreHandler(this,9);
        LevelScoreHandler lsh10 = new LevelScoreHandler(this,10);
        
        guiButtons.get(LEVEL_SELECT_1_BUTTON_TYPE).setActionListener(lsh);
        guiButtons.get(LEVEL_SELECT_2_BUTTON_TYPE).setActionListener(lsh2);
        guiButtons.get(LEVEL_SELECT_3_BUTTON_TYPE).setActionListener(lsh3);
        guiButtons.get(LEVEL_SELECT_4_BUTTON_TYPE).setActionListener(lsh4);
        guiButtons.get(LEVEL_SELECT_5_BUTTON_TYPE).setActionListener(lsh5);
        guiButtons.get(LEVEL_SELECT_6_BUTTON_TYPE).setActionListener(lsh6);
        guiButtons.get(LEVEL_SELECT_7_BUTTON_TYPE).setActionListener(lsh7);
        guiButtons.get(LEVEL_SELECT_8_BUTTON_TYPE).setActionListener(lsh8);
        guiButtons.get(LEVEL_SELECT_9_BUTTON_TYPE).setActionListener(lsh9);
        guiButtons.get(LEVEL_SELECT_10_BUTTON_TYPE).setActionListener(lsh10);
        
        // SPLASH HANDLER FOR BACK BUTTON
        SplashHandler sh = new SplashHandler(this, "Back");
        guiButtons.get(SCORE_SCREEN_BUTTON_TYPE).setActionListener(sh);
        
        GameScreenHandler gsh = new GameScreenHandler(this);
        guiButtons.get(SCORE_TO_GAME_SCREEN_BUTTON_TYPE).setActionListener(gsh);
        guiButtons.get(PLAY_AGAIN_BUTTON_TYPE).setActionListener(gsh);
        
        //USES LEVEL SCORE HANDLER FOR GAME SCREEN
        LevelScoreHandler lsh22 = new LevelScoreHandler(this, 0);
        guiButtons.get(GAME_TO_SCORE_SCREEN_BUTTON_TYPE).setActionListener(lsh22);
        
        
        // NEW GAME EVENT HANDLER
        //NewGameHandler ngh = new NewGameHandler(this);
        //guiButtons.get(NEW_GAME_BUTTON_TYPE).setActionListener(ngh);
        
        // BACK EVENT HANDLER
        //BackHandler bh = new BackHandler(this);
        //guiButtons.get(BACK_BUTTON_TYPE).setActionListener(bh);

        // UNDO EVENT HANDLER
        //UndoHandler uh = new UndoHandler(this);
        //guiButtons.get(UNDO_BUTTON_TYPE).setActionListener(uh);
        
        // KEY LISTENER - LET'S US PROVIDE CUSTOM RESPONSES
        LullabyLandKeyHandler mkh = new LullabyLandKeyHandler(this);
        this.setKeyListener(mkh);

        // STATS BUTTON EVENT HANDLER
        //StatsHandler sh = new StatsHandler(this);
        //guiButtons.get(STATS_BUTTON_TYPE).setActionListener(sh);
    }
    
    /**
     * Invoked when a new game is started, it resets all relevant
     * game data and gui control states. 
     */
    @Override
    public void reset()
    {
        data.reset(this);
    }
    
    /**
     * Updates the state of all gui controls according to the 
     * current game conditions.
     */
    @Override
    public void updateGUI()
    {
        // GO THROUGH THE VISIBLE BUTTONS TO TRIGGER MOUSE OVERS
        Iterator<Sprite> buttonsIt = guiButtons.values().iterator();
        while (buttonsIt.hasNext())
        {
            Sprite button = buttonsIt.next();
            
            // ARE WE ENTERING A BUTTON?
            if (button.getState().equals(VISIBLE_STATE))
            {
                if (button.containsPoint(data.getLastMouseX(), data.getLastMouseY()))
                {
                    button.setState(MOUSE_OVER_STATE);
                }
            }
            // ARE WE EXITING A BUTTON?
            else if (button.getState().equals(MOUSE_OVER_STATE))
            {
                 if (!button.containsPoint(data.getLastMouseX(), data.getLastMouseY()))
                {
                    button.setState(VISIBLE_STATE);
                }
            }
        }
    }    
}