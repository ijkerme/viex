package start.module.myvim.handler.action;

import start.module.myvim.state.ActionType;
import start.module.myvim.state.CommandType;
import static start.module.myvim.handler.action.EVIActionFacade.*;
import static start.module.myvim.state.Mode.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import start.module.myvim.utilities.ComponentUtils.*;

import static start.module.myvim.state.CommandType.*;
import static start.module.myvim.state.ActionType.*;

/**
 *  
 *  @author jker
 */
public class ActionFactory {
    
    protected Map<String, CommandAction> defaultActions = new HashMap<String, CommandAction>();
    protected Map<String, CommandAction> colonActions = new HashMap<String, CommandAction>();
    protected Map<String, CommandAction> slashActions = new HashMap<String, CommandAction>();
    protected Map<String, CommandAction> visualActions = new HashMap<String, CommandAction>();
    protected Map<String, CommandAction> visualBlockActions = new HashMap<String, CommandAction>();
    protected Map<String, CommandAction> noterminateActions = new HashMap<String, CommandAction>();
    protected Map<String, CommandAction> digitalActions = new HashMap<String, CommandAction>();
    protected Map<String, CommandAction> miscActions = new HashMap<String, CommandAction>();
    
    private Logger logger = Logger.getLogger(ActionFactory.class.getName());

    private static ActionFactory actionFactory;   
    private ActionFactory() {
    }
    
    public static ActionFactory getInstance() {
        if (actionFactory == null) {
            actionFactory = new ActionFactory();
        }
        return actionFactory;
    }
    
    public String[] getCommands(CommandType ct) {
        switch(ct) {
            case DEFAULT_COMMAND:
                return getDefaultCommands();
            case COLON_COMMAND:
                return getColonCommands();
            case SLASH_COMMAND:
                return getSlashCommands();
            case NO_TERMINATE_COMMAND:
                return getNoTerminateCommands();
            case VISUAL_COMMAND:
                return getVisualCommands();
            case VISUAL_BLOCK_COMMAND:
                return getVisualBlockCommands();
            case DIGITAL_COMMAND:
                return getDigitalCommands();
            case MISC_COMMAND:
                return getMiscCommands();
            default:
                return null;
        }
    }
    
    public CommandAction getAction(ActionType at, String cmdString) {
        switch(at) {
            case DEFAULT_ACTION:
                return getDefaultAction(cmdString);
            case COLON_ACTION:
                return getColonAction(cmdString);
            case SLASH_ACTION:
                return getSlashAction(cmdString);
            case NO_TERMINATE_ACTION:
                return getNoTerminateAction(cmdString);
            case VISUAL_ACTION:
                return getVisualAction(cmdString);
            case VISUAL_BLOCK_ACTION:
                return getVisualBlockAction(cmdString);
            case DIGITAL_ACTION:
                return getDigitalAction(cmdString);
            case MISC_ACTION:
                return getMiscAction(cmdString);
            default:
                return null;
        }
    }

    private static InsertCharsPairAction icpa = new InsertCharsPairAction();
    public CommandAction getInsertCharsPairAction() {
        return icpa;
    }
    
    public CommandAction getDefaultAction(String cmdString) {
        return defaultActions.get(cmdString);
    }
    
    public CommandAction getColonAction(String cmdString) {
        return colonActions.get(cmdString);
    }    
    
    public CommandAction getSlashAction(String cmdString) {
        return slashActions.get(cmdString);
    }
    
    public CommandAction getVisualAction(String cmdString) {
        return visualActions.get(cmdString);
    }

    public CommandAction getVisualBlockAction(String cmdString) {
        return visualBlockActions.get(cmdString);
    }
    
    public CommandAction getNoTerminateAction(String cmdString) {
        return noterminateActions.get(cmdString);
    }
    
    public CommandAction getDigitalAction(String cmdString) {
        return digitalActions.get(cmdString);
    }

    public CommandAction getMiscAction(String cmdString) {
        return miscActions.get(cmdString);
    }
    
    public String[] getDefaultCommands() {
        return defaultCommands;
    }    
    
    public String[] getColonCommands() {
        return colonCommands;
    }   
    
    public String[] getSlashCommands() {
        return slashCommands;
    }
    
    public String[] getNoTerminateCommands() {
        return noterminateCommands;
    }
    
    public String[] getVisualCommands() {
        return visualCommands;
    }   

    public String[] getVisualBlockCommands() {
        return visualBlockCommands;
    }
    
    public String[] getDigitalCommands() {
        return digitalCommands;
    }

    public String[] getMiscCommands() {
        return miscCommands;
    }
    
    /**
     */
    public void initActions() {
        initDefaultAction();
        initColonAction();
        initSlashAction();
        initVisualAction();
        initVisualBlockAction();
        initNoTerminateAction();
        initDigitalAction();
        initMiscAction();
    }
    
    private void initDefaultAction() {
        //logger.info("initializing default action...");
        defaultActions.put(LEFT, new LeftCommandAction());
        defaultActions.put(RIGHT, new RightCommandAction());
        defaultActions.put(UP, new UpCommandAction());
        defaultActions.put(DOWN, new DownCommandAction());
        defaultActions.put(LAST_CHAR_LINE, new LastCharLineAction());
        defaultActions.put(FIRST_CHAR_LINE, new FirstCharLineAction());
        defaultActions.put(BEGIN_LINE, new BeginLineAction());
        defaultActions.put(MIDDLE_LINE, new MiddleLineAction());
        defaultActions.put(FIRST_LINE, new FirstLineAction());
        defaultActions.put(LAST_LINE, new LastLineAction());
        defaultActions.put(NEXT_WORD, new NextWordAction());
        defaultActions.put(PREV_WORD, new PrevWordAction());
        defaultActions.put(NEXT_WORD_SPACE, new NextWordSpaceAction());
        defaultActions.put(PREV_WORD_SPACE, new PrevWordSpaceAction());
        
        defaultActions.put(MOVE_LINE_UP, new MoveLineUpAction());
        defaultActions.put(MOVE_LINE_DOWN, new MoveLineDownAction());
        defaultActions.put(COPY_LINE_UP, new CopyLineUpAction());
        defaultActions.put(COPY_LINE_DOWN, new CopyLineDownAction());

        defaultActions.put(COPY_LINE_TO_CLIPBOARD, new CopyLineToClipboard());

        defaultActions.put(MOVE_NEXT_ANNOTATION, new MoveNextAnnotationAction());
        defaultActions.put(SHIFT_LINE_LEFT, new ShiftLineLeftAction());
        defaultActions.put(SHIFT_LINE_RIGHT, new ShiftLineRightAction());

        defaultActions.put(INSERT_AT_CARET, new InsertAtCaretAction());
        defaultActions.put(INSERT_BEGIN_LINE, new InsertBeginLineAction());
        defaultActions.put(INSERT_AFTER_CARET, new InsertAfterCaretAction());
        defaultActions.put(INSERT_BELOW_LINE, new InsertBelowLineAction());
        defaultActions.put(INSERT_END_LINE, new InsertEndLineAction());

        defaultActions.put(CHANGE_TO_END_LINE, new ChangeToEndLineAction());
        defaultActions.put(CHANGE_CURRENT_LINE, new ChangeCurrentLineAction());
        defaultActions.put(SWITCH_CASE, new SwitchCaseAction());
        defaultActions.put(SWITCH_UPPER_LINE, new SwitchUpperLineAction());
        defaultActions.put(SWITCH_LOWER_LINE, new SwitchLowerLineAction());
        defaultActions.put(SWITCH_CASE_LINE, new SwitchCaseLineAction());
        defaultActions.put(SWITCH_UPPER, new SwitchUpperAction());
        defaultActions.put(SWITCH_LOWER, new SwitchLowerAction());
        
        defaultActions.put(DELETE_NEXT_CHAR, new DeleteNextCharAction());
        defaultActions.put(DELETE_PREV_CHAR, new DeletePrevCharAction());
        defaultActions.put(DELETE_LINE, new DeleteLineAction());
        defaultActions.put(DELETE_TO_END, new DeleteToEndAction());
        defaultActions.put(DELETE_TO_END_LINE, new DeleteToEndAction());
        defaultActions.put(DELETE_TO_BEGIN, new DeleteToBeginAction());
        defaultActions.put(DELETE_NEXT_WORD, new DeleteNextWordAction());
        defaultActions.put(DELETE_PREV_WORD, new DeletePrevWordAction());
        defaultActions.put(DELETE_AND_INSERT, new DeleteAndInsertAction());
        defaultActions.put(DELETE_CARET_WORD, new DeleteCaretWordAction());
        defaultActions.put(DELETE_NEWLINE, new DeleteNewlineAction());
        defaultActions.put(DELETE_INCSPACE_CARET_WORD, new DeleteIncspaceCaretWordAction());
        defaultActions.put(CHANGE_WORD, new ChangeWordAction());
              
        defaultActions.put(SHOW_LINE_NUMBER, new ShowLineNumberAction());
        defaultActions.put(SEARCH_NEXT_BY_HISTORY_KEY, new SearchNextByHistoryKeyAction());
        defaultActions.put(SEARCH_PREV_BY_HISTORY_KEY, new SearchPrevByHistoryKeyAction());
        defaultActions.put(SEARCH_WORD_AT_CARET, new SearchWordAtCaretAction());
        defaultActions.put(PASTE_FROM_CLIPBOARD, new PasteFromClipboardAction());
        defaultActions.put(PASTE_UP_FROM_CLIPBOARD, new PasteUpFromClipboardAction());
        defaultActions.put(PASTE_SELECTED_RECTANGLE_TEXT, new PasteSelectedRectangleTextAction());
        defaultActions.put(UNDO, new UndoAction());

        defaultActions.put(GOTO_FILE, new GotoFileAction());
        defaultActions.put(HEX_VALUE, new HexValueAction());
        defaultActions.put(ASCII_VALUE, new AsciiValueAction());
        
        defaultActions.put(MISC_MODE, new MiscModeCommandAction());

        defaultActions.put(MATCH_BRACE, new NBActionFacade.MatchBraceAction());
        // relative the folds
        defaultActions.put(OPEN_ALL_FOLD, new NBActionFacade.OpenAllFoldAction());
        defaultActions.put(CLOSE_ALL_FOLD, new NBActionFacade.CloseAllFoldAction());
        defaultActions.put(OPEN_FOLD, new NBActionFacade.OpenFoldAction());
        defaultActions.put(CLOSE_FOLD, new NBActionFacade.CloseFoldAction());
        //
        defaultActions.put(SURROUND_S_MARK, new SurroundWithSingleMarkAction());
        defaultActions.put(SURROUND_D_MARK, new SurroundWithDoubleMarkAction());
        defaultActions.put(DEL_CHARS_PAIR, new DelCharsPairAction());
    }
      
    private void initColonAction() {
        //logger.info("initializing colon action...");
        colonActions.put(SAVE, new SaveAction());
        colonActions.put(SAVE_AND_EXIT, new SaveAndExitAction());
        colonActions.put(EXIT_FORCE, new ExitForceAction());
        colonActions.put(EXIT, new ExitAction());
        colonActions.put(NEW, new NewAction());
    }
    
    private void initSlashAction() {
        //logger.info("initializing slash action...");
        slashActions.put(SLASH_COMMAND_MODE, new SearchStringAction());
        slashActions.put(SLASH_MISC_MODE, new SearchMatchItemAction());
    }
    
    private void initVisualAction() {
        //logger.info("initializing visual action...");
        visualActions.put(LEFT, new LeftCommandAction());
        visualActions.put(RIGHT, new RightCommandAction());
        visualActions.put(UP, new UpCommandAction());
        visualActions.put(DOWN, new DownCommandAction());
        visualActions.put(DIGITAL_LEFT, new LeftCommandAction());
        visualActions.put(DIGITAL_RIGHT, new RightCommandAction());
        visualActions.put(DIGITAL_UP, new UpCommandAction());
        visualActions.put(DIGITAL_DOWN, new DownCommandAction());
        visualActions.put(FIRST_CHAR_LINE, new FirstCharLineAction());
        visualActions.put(LAST_CHAR_LINE, new LastCharLineAction());
        visualActions.put(FIRST_LINE, new FirstLineAction());
        visualActions.put(LAST_LINE, new LastLineAction());
        visualActions.put(NEXT_WORD, new NextWordAction());
        visualActions.put(PREV_WORD, new PrevWordAction());
        visualActions.put(SHIFT_LINE_LEFT, new ShiftLineLeftAction());
        visualActions.put(SHIFT_LINE_RIGHT, new ShiftLineRightAction());
        visualActions.put(DELETE_NEXT_CHAR, new DeleteNextCharAction());
        visualActions.put(DEL_CHARS_PAIR, new DelCharsPairAction());
        visualActions.put(DELETE_LINE, new DeleteSelectionCharAction());
        visualActions.put(DELETE_AND_INSERT, new DeleteAndInsertAction());
        visualActions.put(COPY_TO_CLIPBOARD, new CopyToClipboardAction());
        visualActions.put(DIGITAL_MOVE_LINE_UP, new MoveLineUpAction());
        visualActions.put(DIGITAL_MOVE_LINE_DOWN, new MoveLineDownAction());
        visualActions.put(DIGITAL_COPY_LINE_UP, new CopyLineUpAction());
        visualActions.put(DIGITAL_COPY_LINE_DOWN, new CopyLineDownAction());
        visualActions.put(SWITCH_CASE, new SwitchCaseAction());
        visualActions.put(SWITCH_UPPER, new SwitchUpperAction());
        visualActions.put(SWITCH_LOWER, new SwitchLowerAction());
        visualActions.put(VISUAL_BLOCK, new VisualBlockAction());
        visualActions.put(NATIVE2ASCII, new Native2AsciiAction());
        visualActions.put(ASCII2NATIVE, new Ascii2NativeAction());
        visualActions.put(SURROUND_S_MARK, new SurroundWithSingleMarkAction());
        visualActions.put(SURROUND_D_MARK, new SurroundWithDoubleMarkAction());
    }

    private void initVisualBlockAction() {
        visualBlockActions.put(UP, new UpCommandAction());
        visualBlockActions.put(DOWN, new DownCommandAction());
        visualBlockActions.put(LEFT, new LeftCommandAction());
        visualBlockActions.put(RIGHT, new RightCommandAction());
        visualBlockActions.put(COPY_TO_CLIPBOARD, new CopyToClipboardAction());
        visualBlockActions.put(DELETE_SELECTION, new DeleteSelectionCharAction());
    }
    
    private void initNoTerminateAction() {
        //logger.info("initializing no terminate action...");
        noterminateActions.put(REPLACE_SINGLE_CHAR, new ReplaceSingleCharAction());
        noterminateActions.put(FIND_WITHIN_LINE, new FindWithinLineAction());
        noterminateActions.put(FIND_REVERSE_WITHIN_LINE, new FindReverseWithinLineAction());
    }
    
    private void initDigitalAction() {
        //logger.info("initializing digital action...");
        digitalActions.put(GOTO_LINE, new GotoLineAction());
        digitalActions.put(LEFT, new LeftCommandAction());
        digitalActions.put(RIGHT, new RightCommandAction());
        digitalActions.put(UP, new UpCommandAction());
        digitalActions.put(DOWN, new DownCommandAction());
    }

    private void initMiscAction() {
        miscActions.put(UP, new UpMiscCommandAction());
        miscActions.put(DOWN, new DownMiscCommandAction());
        //miscActions.put(LEFT, new LeftCommandAction());
        //miscActions.put(RIGHT, new RightCommandAction());
        miscActions.put(OPEN_EDITOR_LIST, new OpenEditorListAction());
        //miscActions.put(OPEN_BOOKMARK_LIST, new OpenBookmarkListAction());
        miscActions.put(CLOSE_POPUP, new ClosePopupAction());
        miscActions.put(MISC_DIGITAL, new MiscDigitalAction());
        miscActions.put(OPEN_PROJECT_FILES_LIST, new OpenProjectFilesListAction());
        miscActions.put(LIST_PAGE_UP, new ListPageUpAction());
        miscActions.put(LIST_PAGE_DOWN, new ListPageDownAction());
        miscActions.put(REVERSE_LIST, new ReverseListAction());
        miscActions.put(G_SVUID, new GenerateSUIDAction());
    }
    
    private String[] defaultCommands = {
        LEFT, RIGHT, UP, DOWN, FIRST_CHAR_LINE, LAST_CHAR_LINE, FIRST_LINE, LAST_LINE,
        BEGIN_LINE, MIDDLE_LINE,
        NEXT_WORD, PREV_WORD, NEXT_WORD_SPACE, PREV_WORD_SPACE,
        MOVE_LINE_UP, MOVE_LINE_DOWN, COPY_LINE_UP, COPY_LINE_DOWN, 
        COPY_LINE_TO_CLIPBOARD,
        MOVE_NEXT_ANNOTATION, SHIFT_LINE_LEFT, SHIFT_LINE_RIGHT,
        INSERT_AT_CARET, INSERT_AFTER_CARET, INSERT_BELOW_LINE, INSERT_END_LINE, INSERT_BEGIN_LINE,
        CHANGE_TO_END_LINE, CHANGE_CURRENT_LINE, 
        SWITCH_CASE, SWITCH_UPPER_LINE, SWITCH_LOWER_LINE, SWITCH_CASE_LINE, SWITCH_UPPER, SWITCH_LOWER,
        DELETE_NEXT_CHAR, DELETE_PREV_CHAR, DELETE_LINE, DELETE_TO_END,
        DELETE_TO_BEGIN, DELETE_NEXT_WORD, DELETE_PREV_WORD, 
        DELETE_CARET_WORD, DELETE_INCSPACE_CARET_WORD, DELETE_NEWLINE,
        DELETE_AND_INSERT, DELETE_TO_END_LINE, 
        CHANGE_WORD,
        SHOW_LINE_NUMBER,
        SEARCH_NEXT_BY_HISTORY_KEY, SEARCH_PREV_BY_HISTORY_KEY,
        SEARCH_WORD_AT_CARET,
        PASTE_FROM_CLIPBOARD, PASTE_SELECTED_RECTANGLE_TEXT, PASTE_UP_FROM_CLIPBOARD,
        UNDO,
        GOTO_FILE, 
        HEX_VALUE, ASCII_VALUE,
        MISC_MODE,
        MATCH_BRACE,
        OPEN_ALL_FOLD, CLOSE_ALL_FOLD, OPEN_FOLD, CLOSE_FOLD,
        SURROUND_S_MARK, SURROUND_D_MARK, DEL_CHARS_PAIR
    };
    
    private String[] colonCommands = {
        SAVE, SAVE_AND_EXIT, EXIT_FORCE, EXIT, NEW,
    };
    
    private String[] slashCommands = {
        SLASH_COMMAND_MODE, SLASH_MISC_MODE,
    };
    
    private String[] visualCommands = {
        LEFT, RIGHT, UP, DOWN,
        DELETE_LINE,
        DIGITAL_LEFT, DIGITAL_RIGHT, DIGITAL_UP, DIGITAL_DOWN, FIRST_CHAR_LINE, LAST_CHAR_LINE, FIRST_LINE, LAST_LINE,
        DIGITAL_MOVE_LINE_UP, DIGITAL_MOVE_LINE_DOWN, DIGITAL_COPY_LINE_UP, DIGITAL_COPY_LINE_DOWN,
        NEXT_WORD, PREV_WORD,
        SHIFT_LINE_LEFT, SHIFT_LINE_RIGHT,
        DELETE_NEXT_CHAR, DELETE_AND_INSERT, DEL_CHARS_PAIR,
        COPY_TO_CLIPBOARD,
        SWITCH_CASE, SWITCH_UPPER, SWITCH_LOWER,
        VISUAL_BLOCK,
        NATIVE2ASCII, ASCII2NATIVE,
        SURROUND_S_MARK, SURROUND_D_MARK
    };
    
    private String[] visualBlockCommands = {
        UP, DOWN, LEFT, RIGHT,
        COPY_TO_CLIPBOARD, DELETE_SELECTION,
    };

    private String[] noterminateCommands = {
        REPLACE_SINGLE_CHAR, FIND_WITHIN_LINE, FIND_REVERSE_WITHIN_LINE,
    };
    
    private String[] digitalCommands = {
        GOTO_LINE, LEFT, RIGHT, UP, DOWN, 
        MOVE_LINE_UP, MOVE_LINE_DOWN, COPY_LINE_UP, COPY_LINE_DOWN,
    };
    
    private String[] miscCommands = {
        UP, DOWN, OPEN_EDITOR_LIST, /*OPEN_BOOKMARK_LIST, */CLOSE_POPUP,
        OPEN_PROJECT_FILES_LIST,
        LIST_PAGE_UP, LIST_PAGE_DOWN,
        REVERSE_LIST,
        G_SVUID
    };
    
    //<editor-fold defaultstate="collapsed" desc="Commands in command mode">
    public static final String LEFT =                       "h";
    public static final String RIGHT =                      "l";
    public static final String UP =                         "k";
    public static final String DOWN =                       "j";
    public static final String FIRST_CHAR_LINE =            "^";
    public static final String LAST_CHAR_LINE =             "$";
    public static final String BEGIN_LINE =                 "0"; // begin of line
    public static final String MIDDLE_LINE =                "gm"; // middle of line
    public static final String FIRST_LINE =                 "gg";
    public static final String LAST_LINE =                  "G";
    public static final String NEXT_WORD =                  "w";
    public static final String NEXT_WORD_SPACE =            "W"; // space-separated word left
    public static final String PREV_WORD =                  "b";
    public static final String PREV_WORD_SPACE =            "B"; // space-separated word right
    
    public static final String MOVE_LINE_UP =               "mk";
    public static final String MOVE_LINE_DOWN =             "mj";
    public static final String COPY_LINE_UP =               "ck";
    public static final String COPY_LINE_DOWN =             "cj";

    public static final String MOVE_NEXT_ANNOTATION =       "ma";
    public static final String SHIFT_LINE_LEFT =            "<<"; // shift line left
    public static final String SHIFT_LINE_RIGHT =           ">>"; // shift line right

    public static final String INSERT_AT_CARET =            "i";
    public static final String INSERT_BEGIN_LINE =          "I"; // insert at begin of line
    public static final String INSERT_AFTER_CARET =         "a";
    public static final String INSERT_END_LINE =            "A"; // insert the end of line
    public static final String INSERT_BELOW_LINE =          "o";
    public static final String INSERT_ABOVE_LINE =          "O"; // open a newline above ----no
    public static final String CHANGE_TO_END_LINE =         "C"; // change to the end of line
    public static final String CHANGE_CURRENT_LINE =        "S"; // change current line, same "cc"

    public static final String SWITCH_CASE =                "~"; // switch case
    public static final String SWITCH_UPPER =               "gU"; // switch to upper case
    public static final String SWITCH_LOWER =               "gu"; // switch to lower case
    public static final String SWITCH_UPPER_LINE =          "gUU"; // switch to upper case for line
    public static final String SWITCH_LOWER_LINE =          "guu"; // switch to lower case for line
    public static final String SWITCH_CASE_LINE  =          "g~~"; // switch case of line chars
    
    public static final String DELETE_NEXT_CHAR =           "x";
    public static final String DELETE_PREV_CHAR =           "X";
    public static final String DELETE_LINE =                "dd"; // Delete selected texts in visual mode
    public static final String DELETE_TO_END_LINE =         "D"; // delete to the end of line
    public static final String DELETE_TO_END =              "d$";
    public static final String DELETE_TO_BEGIN =            "d^";
    public static final String DELETE_NEXT_WORD =           "dw";
    public static final String DELETE_PREV_WORD =           "db";
    public static final String CHANGE_WORD =                "cw";
    public static final String DELETE_CARET_WORD =          "diw";
    public static final String DELETE_INCSPACE_CARET_WORD = "daw";
    public static final String DELETE_NEWLINE =             "J";
    public static final String DELETE_AND_INSERT =          "s";
               
    public static final String SHOW_LINE_NUMBER =           "Y";
    
    public static final String SEARCH_NEXT_BY_HISTORY_KEY = "n";
    public static final String SEARCH_PREV_BY_HISTORY_KEY = "N";
    public static final String SEARCH_WORD_AT_CARET =       "#";
    
    public static final String COPY_TO_CLIPBOARD =          "y";
    public static final String COPY_LINE_TO_CLIPBOARD =     "yy";
    public static final String PASTE_FROM_CLIPBOARD =       "p";
    public static final String PASTE_UP_FROM_CLIPBOARD =    "P"; //put register before
    public static final String PASTE_SELECTED_RECTANGLE_TEXT = "mp";
    //
    public static final String SURROUND_S_MARK =            "'";  // surround word with a single quotation
    public static final String SURROUND_D_MARK =            "\""; // surround word with a double quotation
    public static final String DEL_CHARS_PAIR =             "d'"; // Delete a chars pair under the cursor or the selected text
    //public static final String DEL_D_MARK = "d\"";      // Delete a char pair of a char '"'
    // Commands for fold
    public static final String OPEN_ALL_FOLD =              "zR"; // Open all folds
    public static final String CLOSE_ALL_FOLD =             "zM"; // Close all folds
    public static final String OPEN_FOLD =                  "za"; // Open a fold closed
    public static final String CLOSE_FOLD =                 "zc"; // Close a fold under the cursor
    //</editor-fold>

    public static final String UNDO =                       "u";
    
    public static final String MISC_MODE =                  "t";
    
    // for netbeans actions
    public static final String MATCH_BRACE =                "%"; // match brace

    //<editor-fold defaultstate="collapsed" desc="Quick List commands in misc mode">
    // Quick List commands in Misc Mode
    public static final String OPEN_BOOKMARK_LIST =         "b";
    public static final String OPEN_EDITOR_LIST =           "t";
    public static final String OPEN_PROJECT_FILES_LIST =    "p";
    public static final String CLOSE_POPUP =                "q";
    public static final String MISC_DIGITAL =               "MISC_DIGITAL";
    public static final String LIST_PAGE_UP =               "u";
    public static final String LIST_PAGE_DOWN =             "d";
    public static final String REVERSE_LIST =               "r";
    public static final String G_SVUID =                    "gs"; // Generate a serial version UID
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Commands in command mode">
    // Colon Commands in command mode
    public static final String SAVE =                       ":w";
    public static final String SAVE_AND_EXIT =              ":wq";
    public static final String EXIT_FORCE =                 ":q!";
    public static final String EXIT =                       ":q";
    public static final String NEW  =                       ":new"; // new or open special file
    
    public static final String SLASH_COMMAND_MODE =         "c/";
    public static final String SLASH_MISC_MODE =            "m/";

    public static final String VISUAL_BLOCK =               "b";

    public static final String DELETE_SELECTION =           "d";
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Commands in visual mode">
    // Commands in visual mode
    public static final String NATIVE2ASCII =               "na";                   // Native to ascii
    public static final String ASCII2NATIVE =               "an";                   // Ascii to native
    //</editor-fold>
    //
    public static final String REPLACE_SINGLE_CHAR =        "r";
    public static final String FIND_WITHIN_LINE =           "f";
    public static final String FIND_REVERSE_WITHIN_LINE =   "F";
    
    //
    public static final String GOTO_LINE =                  "G";
    public static final String GOTO_FILE =                  "gf"; // goto file, file name is chars under cursor
    public static final String HEX_VALUE =                  "g8"; // print hex value of char under cursor
    public static final String ASCII_VALUE =                "ga"; // print ascii value of char under cursor
    public static final String SELECT_LINE =                "gH"; // no
    
    //<editor-fold defaultstate="collapsed" desc="Commands of start with a digital in visual mode">
    public static final String DIGITAL_LEFT =               "h0";
    public static final String DIGITAL_RIGHT =              "l0";
    public static final String DIGITAL_UP =                 "k0";
    public static final String DIGITAL_DOWN =               "j0";

    public static final String DIGITAL_MOVE_LINE_UP =       "mk0";
    public static final String DIGITAL_MOVE_LINE_DOWN =     "mj0";
    public static final String DIGITAL_COPY_LINE_UP =       "ck0";
    public static final String DIGITAL_COPY_LINE_DOWN =     "cj0";
    //</editor-fold>
}