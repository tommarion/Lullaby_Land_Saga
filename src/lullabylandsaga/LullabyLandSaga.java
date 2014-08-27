/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lullabylandsaga;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import lullabylandsaga.ui.LullabyLandSagaErrorHandler;
import lullabylandsaga.ui.LullabyLandSagaMiniGame;
import properties_manager.PropertiesManager;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import xml_utilities.InvalidXMLFileFormatException;

/**
 *
 * @author tomma_000
 */
public class LullabyLandSaga {
    // THIS HAS THE FULL USER INTERFACE AND ONCE IN EVENT
    // HANDLING MODE, BASICALLY IT BECOMES THE FOCAL
    // POINT, RUNNING THE UI AND EVERYTHING ELSE
    static LullabyLandSagaMiniGame miniGame = new LullabyLandSagaMiniGame();
    
    
    // WE'LL LOAD ALL THE UI AND ART PROPERTIES FROM FILES,
    // BUT WE'LL NEED THESE VALUES TO START THE PROCESS
    static String PROPERTY_TYPES_LIST = "property_types.txt";
    static String UI_PROPERTIES_FILE_NAME = "properties.xml";
    static String PROPERTIES_SCHEMA_FILE_NAME = "properties_schema.xsd";    
    static String DATA_PATH = "./data/";

    /**
     * This is where the Mahjong Solitaire game application starts execution. We'll
     * load the application properties and then use them to build our
     * user interface and start the window in event handling mode. Once
     * in that mode, all code execution will happen in response to a 
     * user request.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        try
        {
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(LullabyLandSagaPropertyType.UI_PROPERTIES_FILE_NAME, UI_PROPERTIES_FILE_NAME);
            props.addProperty(LullabyLandSagaPropertyType.PROPERTIES_SCHEMA_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            props.addProperty(LullabyLandSagaPropertyType.DATA_PATH.toString(), DATA_PATH);
            props.loadProperties(UI_PROPERTIES_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            
            // THEN WE'LL LOAD THE MAHJONG FLAVOR AS SPECIFIED BY THE PROPERTIES FILE
            String gameFlavorFile = props.getProperty(LullabyLandSagaPropertyType.GAME_FLAVOR_FILE_NAME);
            props.loadProperties(gameFlavorFile, PROPERTIES_SCHEMA_FILE_NAME);
                               
            // NOW WE CAN LOAD THE UI, WHICH WILL USE ALL THE FLAVORED CONTENT
            String appTitle = props.getProperty(LullabyLandSagaPropertyType.GAME_TITLE_TEXT);
            int fps = Integer.parseInt(props.getProperty(LullabyLandSagaPropertyType.FPS));
            miniGame.initMiniGame(appTitle, fps);
            miniGame.startGame();
            
            String splashAudio = "./audio/zomjong/Dream.wav";
            InputStream in = new FileInputStream(splashAudio);
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
            
        }
        // THERE WAS A PROBLEM LOADING THE PROPERTIES FILE
        catch(InvalidXMLFileFormatException ixmlffe)
        {
            // LET THE ERROR HANDLER PROVIDE THE RESPONSE
            LullabyLandSagaErrorHandler errorHandler = miniGame.getErrorHandler();
            errorHandler.processError(LullabyLandSagaPropertyType.INVALID_XML_FILE_ERROR_TEXT);
        }
    }
    
    /**
     * Mahjong SolitairePropertyType represents the types of data that will need
     * to be extracted from XML files.
     */
    public enum LullabyLandSagaPropertyType
    {
        /* SETUP FILE NAMES */
        UI_PROPERTIES_FILE_NAME,
        PROPERTIES_SCHEMA_FILE_NAME,
        GAME_FLAVOR_FILE_NAME,
        RECORD_FILE_NAME,

        /* DIRECTORIES FOR FILE LOADING */
        AUDIO_PATH,
        DATA_PATH,
        IMG_PATH,
        
        /* WINDOW DIMENSIONS & FRAME RATE */
        WINDOW_WIDTH,
        WINDOW_HEIGHT,
        FPS,
        GAME_WIDTH,
        GAME_HEIGHT,
        GAME_LEFT_OFFSET,
        GAME_TOP_OFFSET,
        
        /* GAME TEXT */
        GAME_TITLE_TEXT,
        EXIT_REQUEST_TEXT,
        INVALID_XML_FILE_ERROR_TEXT,
        ERROR_DIALOG_TITLE_TEXT,
        
        /* ERROR TYPES */
        AUDIO_FILE_ERROR,
        LOAD_LEVEL_ERROR,
        RECORD_SAVE_ERROR,

        /* IMAGE FILE NAMES */
        WINDOW_ICON,
        SPLASH_SCREEN_IMAGE_NAME,
        SAGA_SCREEN_1_IMAGE_NAME,
        SAGA_SCREEN_2_IMAGE_NAME,
        SAGA_SCREEN_3_IMAGE_NAME,
        SAGA_SCREEN_4_IMAGE_NAME,
        SAGA_SCREEN_5_IMAGE_NAME,
        LEVEL_1_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_2_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_3_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_4_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_5_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_6_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_7_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_8_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_9_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_10_SCORE_SCREEN_IMAGE_NAME,
        LEVEL_1_WIN_SCREEN_IMAGE_NAME,
        LEVEL_1_LOSE_SCREEN_IMAGE_NAME,
        GAME_BACKGROUND_IMAGE_NAME,
        BLANK_TILE_IMAGE_NAME,
        BLANK_TILE_SELECTED_IMAGE_NAME,
        SAGA_SCREEN_LEFT_IMAGE_NAME,
        SAGA_SCREEN_LEFT_MOUSE_OVER_IMAGE_NAME,
        SAGA_LEFT_UNAVAILABLE_IMAGE_NAME,
        SAGA_SCREEN_RIGHT_IMAGE_NAME,
        SAGA_SCREEN_RIGHT_MOUSE_OVER_IMAGE_NAME,
        SAGA_RIGHT_UNAVAILABLE_IMAGE_NAME,
        
        LEVEL_1_IMAGE_NAME,
        LEVEL_1_MOUSE_OVER_IMAGE_NAME,
        LEVEL_2_IMAGE_NAME,
        LEVEL_2_MOUSE_OVER_IMAGE_NAME,
        LEVEL_3_IMAGE_NAME,
        LEVEL_3_MOUSE_OVER_IMAGE_NAME,
        LEVEL_4_IMAGE_NAME,
        LEVEL_4_MOUSE_OVER_IMAGE_NAME,
        LEVEL_5_IMAGE_NAME,
        LEVEL_5_MOUSE_OVER_IMAGE_NAME,
        LEVEL_6_IMAGE_NAME,
        LEVEL_6_MOUSE_OVER_IMAGE_NAME,
        LEVEL_7_IMAGE_NAME,
        LEVEL_7_MOUSE_OVER_IMAGE_NAME,
        LEVEL_8_IMAGE_NAME,
        LEVEL_8_MOUSE_OVER_IMAGE_NAME,
        LEVEL_9_IMAGE_NAME,
        LEVEL_9_MOUSE_OVER_IMAGE_NAME,
        LEVEL_10_IMAGE_NAME,
        LEVEL_10_MOUSE_OVER_IMAGE_NAME,
        
        LEVEL_1_INACTIVE_IMAGE_NAME,
        SCORE_SCREEN_BACK_IMAGE_NAME,
        SCORE_SCREEN_BACK_MOUSE_OVER_IMAGE_NAME,
        SCORE_SCREEN_PLAY_LEVEL_IMAGE_NAME,
        SCORE_PLAY_MOUSE_OVER_IMAGE_NAME,
        BACK_ARROW_IMAGE_NAME,
        BACK_ARROW_MOUSE_OVER_IMAGE_NAME,
        PLAY_AGAIN_IMAGE_NAME,
        PLAY_AGAIN_MOUSE_OVER_IMAGE_NAME,
        SAGA_RETURN_IMAGE_NAME,
        SAGA_RETURN_MOUSE_OVER_IMAGE_NAME,
        
        // AND THE DIALOGS
        WIN_DIALOG_IMAGE_NAME,
        LOSS_DIALOG_IMAGE_NAME,
        
        /* TILE LOADING STUFF */
        SPLASH_OPTIONS,
        LEVEL_OPTIONS,
        LEVEL_IMAGE_OPTIONS,
        LEVEL_MOUSE_OVER_IMAGE_OPTIONS,
        SPLASH_IMAGE_OPTIONS,
        SPLASH_MOUSE_OVER_IMAGE_OPTIONS,
        TYPE_A_TILES,
        TYPE_B_TILES,
        TYPE_C_TILES,
        TYPE_D_TILES,
        TYPE_E_TILES,
        TYPE_F_TILES,
        
        /* AUDIO CUES */
        SELECT_AUDIO_CUE,
        MATCH_AUDIO_CUE,
        NO_MATCH_AUDIO_CUE,
        BLOCKED_TILE_AUDIO_CUE,
        UNDO_AUDIO_CUE,
        WIN_AUDIO_CUE,
        SPLASH_SCREEN_SONG_CUE,
        GAMEPLAY_SONG_CUE
    }
}
