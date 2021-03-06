/**
 * CFS Command & Data Dictionary common constants. Copyright 2017 United States
 * Government as represented by the Administrator of the National Aeronautics
 * and Space Administration. No copyright is claimed in the United States under
 * Title 17, U.S. Code. All Other Rights Reserved.
 */
package CCDD;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;

import CCDD.CcddClasses.FieldInformation;
import CCDD.CcddClasses.ModifiableColor;
import CCDD.CcddClasses.ModifiableFont;
import CCDD.CcddConstants.InternalTable.TableTypesColumn;
import CCDD.CcddConstants.InternalTable.ValuesColumn;

/******************************************************************************
 * CFS Command & Data Dictionary common constants class
 *****************************************************************************/
public class CcddConstants
{
    // CCDD version number and version date
    protected static final String CCDD_AUTHOR = "NASA JSC: ER6/Kevin McCluney";

    // Default server information
    protected static final String DEFAULT_POSTGRESQL_HOST = "localhost";
    protected static final String DEFAULT_POSTGRESQL_PORT = "5432";
    protected static final String DEFAULT_DATABASE = "postgres";
    protected static final String DEFAULT_SERVER = "PostgreSQL";
    protected static final String DEFAULT_WEB_SERVER_PORT = "7070";

    // Create the database driver class name
    protected static final String DATABASE_DRIVER = "org.postgresql.Driver";

    // Maximum database, table, and column name length, characters
    protected static final int MAX_SQL_NAME_LENGTH = 64;

    // String used as a comment on the database to identify it as a CCDD
    // project
    protected static final String CCDD_PROJECT_IDENTIFIER = "Core Flight Software Command and Data Dictionary";

    // User's guide file name
    protected static final String USERS_GUIDE = "CCDD_Users_Guide.pdf";

    // Program preferences backing store keys
    protected static final String POSTGRESQL_SERVER_HOST = "PostgreSQLServerHost";
    protected static final String POSTGRESQL_SERVER_PORT = "PostgreSQLServerPort";
    protected static final String DATABASE = "Database";
    protected static final String USER = "User";
    protected static final String LOG_FILE_PATH = "LogFilePath";
    protected static final String DATABASE_BACKUP_PATH = "DatabaseBackupPath";
    protected static final String TABLE_EXPORT_PATH = "TableExportPath";
    protected static final String SCRIPT_PATH = "ScriptPath";
    protected static final String LOOK_AND_FEEL = "LookAndFeel";
    protected static final String WEB_SERVER_PORT = "WebServerPort";
    protected static final String SEARCH_STRINGS = "SearchStrings";
    protected static final String SERVER_STRINGS = "ServerStrings";
    protected static final String VARIABLE_PATH_SEPARATOR = "VariablePathSeparator";
    protected static final String TYPE_NAME_SEPARATOR = "TypeNameSeparator";
    protected static final String HIDE_DATA_TYPE = "HideDataType";

    // Prefix assigned to internally created CCDD database tables
    protected static final String INTERNAL_TABLE_PREFIX = "__";

    // Name of the database save point
    protected static final String DB_SAVE_POINT_NAME = "ccdd_savepoint";

    // Script description text tag
    protected static final String SCRIPT_DESCRIPTION_TAG = "description:";

    // Data field owner name identifiers for table type and group fields
    protected static final String TYPE_DATA_FIELD_IDENT = "Type:";
    protected static final String GROUP_DATA_FIELD_IDENT = "Group:";

    // Number of columns in a data table that are not displayed (these columns
    // are the primary key and row index)
    protected static final int NUM_HIDDEN_COLUMNS = 2;

    // Table type and path column index offsets from the last column of the
    // table data array used for script access
    protected static final int TYPE_COLUMN_DELTA = 2;
    protected static final int PATH_COLUMN_DELTA = 1;

    // Default table type names
    protected static final String TYPE_STRUCTURE = "Structure";
    protected static final String TYPE_COMMAND = "Command";
    protected static final String TYPE_OTHER = "Other";

    // Column names/prefixes
    protected static final String COL_ARGUMENT = "Arg";
    protected static final String COL_DATA_TYPE = "Data Type";
    protected static final String COL_DESCRIPTION = "Description";
    protected static final String COL_UNITS = "Units";
    protected static final String COL_ENUMERATION = "Enumeration";
    protected static final String COL_MINIMUM = "Minimum";
    protected static final String COL_MAXIMUM = "Maximum";

    // Information list definition item separators
    protected static final String LIST_TABLE_SEPARATOR = ";\n";
    protected static final String LIST_TABLE_DESC_SEPARATOR = " : ";

    // Separator for the project database comment
    protected static final String DATABASE_COMMENT_SEPARATOR = ";";

    // Separator for the table description list database query
    protected static final String TABLE_DESCRIPTION_SEPARATOR = "\\\\";

    // Separator for multiple enumeration columns in a table member query
    protected static final String ENUMERATION_SEPARATOR = "\\\\\\";

    // Separator for rate values and table names in the telemetry scheduler
    // table
    protected static final String TLM_SCH_SEPARATOR = "\\";

    // Characters used to separate individual remembered strings that are
    // stored as a single string in the program preferences
    protected static final String AUTO_COMPLETE_TEXT_SEPARATOR = "%~%";

    // Special character to denote that a child data table cell value is to be
    // replaced with the corresponding entry in its prototype table, and that
    // its entry in the custom values table is to be removed
    protected static final String REPLACE_INDICATOR = "\u00d8";

    // Radio button, and check box change event names
    protected static final String RADIO_BUTTON_CHANGE_EVENT = "radioButtonChanged";
    protected static final String CHECK_BOX_CHANGE_EVENT = "checkBoxChanged";

    // Table cell renderer name
    protected static final String BOOLEAN_CELL_RENDERER = "BooleanCellRenderer";

    // Characters used to encompass a macro name
    protected static final String MACRO_IDENTIFIER = "##";

    // Regular expression to detect reserved characters. The backslash
    // character as a reserved character isn't included here
    protected static final String POSTGRESQL_RESERVED_CHARS = "(.*?)([\\[\\]\\(\\)\\{\\}\\.\\+\\*\\^\\$\\|\\?\\-])(.*?)";

    // Regular expression patterns for matching trailing zeroes (with or
    // without a leading decimal)
    protected static final String TRAILING_ZEROES = "\\.??0*$";

    // Regular expression for separating text string at a specified separator
    // character. Separator characters between double quotes are ignored so
    // that an erroneous separation doesn't occur
    protected static final String SPLIT_IGNORE_QUOTES = "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    // Regular expressions that detect the presence of the specified text
    // within a string (case insensitive)
    protected static final String CONTAINS_DESCRIPTION = "(?i).*\\s*" + COL_DESCRIPTION + "\\s*.*";
    protected static final String CONTAINS_UNITS = "(?i).*\\s*" + COL_UNITS + "\\s*.*";

    // Regular expression for identifying part or all of a table and its path
    protected static final String PATH_IDENT = "[a-zA-Z0-9_,\\\\.\\\\[\\\\]]+";

    // Regular expression that detects the strings 'true' or 'false' (case
    // insensitive)
    protected static final String TRUE_OR_FALSE = "(?i:true|false)";

    // Node name for the linked and unlinked variables in trees displaying
    // (un)linked variables
    protected static final String LINKED_VARIABLES_NODE_NAME = "Linked Variables";
    protected static final String UNLINKED_VARIABLES_NODE_NAME = "Unlinked Variables";

    // Main window initial and minimum window size
    protected static final int INIT_WINDOW_WIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth() / 2;
    protected static final int INIT_WINDOW_HEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight() / 2;
    protected static final int MIN_WINDOW_WIDTH = Math.min(Math.min(Math.max(INIT_WINDOW_WIDTH,
                                                                             ModifiableSizeInfo.MIN_WINDOW_WIDTH.getSize()),
                                                                    ModifiableSizeInfo.MIN_WINDOW_WIDTH.getSize()),
                                                           INIT_WINDOW_WIDTH * 2);
    protected static final int MIN_WINDOW_HEIGHT = Math.min(Math.min(Math.max(INIT_WINDOW_HEIGHT,
                                                                              ModifiableSizeInfo.MIN_WINDOW_HEIGHT.getSize()),
                                                                     ModifiableSizeInfo.MIN_WINDOW_HEIGHT.getSize()),
                                                            INIT_WINDOW_HEIGHT * 2);

    // Disabled item highlight color
    protected static final String DISABLED_TEXT_COLOR = "<html><font color=#b0b0b0>";

    // Button and table icon file names
    protected static final String OK_ICON = "/images/ok.png";
    protected static final String CANCEL_ICON = "/images/cancel.png";
    protected static final String CLOSE_ICON = "/images/close.png";
    protected static final String INSERT_ICON = "/images/insert.png";
    protected static final String DELETE_ICON = "/images/delete.png";
    protected static final String UP_ICON = "/images/up.png";
    protected static final String DOWN_ICON = "/images/down.png";
    protected static final String LEFT_ICON = "/images/left.png";
    protected static final String RIGHT_ICON = "/images/right.png";
    protected static final String IMPORT_ICON = "/images/import.png";
    protected static final String EXPORT_ICON = "/images/export.png";
    protected static final String STORE_ICON = "/images/store.png";
    protected static final String UNDO_ICON = "/images/undo.png";
    protected static final String REDO_ICON = "/images/redo.png";
    protected static final String UNLOCK_ICON = "/images/unlock.png";
    protected static final String PRINT_ICON = "/images/print.png";
    protected static final String SCRIPTS_ICON = "/images/scripts.png";
    protected static final String AUTO_CREATE_ICON = "/images/auto_create.png";
    protected static final String RENAME_ICON = "/images/rename.png";
    protected static final String COPY_ICON = "/images/copy.png";
    protected static final String EXECUTE_ICON = "/images/execute.png";
    protected static final String EXECUTE_ALL_ICON = "/images/execute_all.png";
    protected static final String HALT_EXECUTION_ICON = "/images/halt.png";
    protected static final String GROUP_ICON = "/images/group.png";
    protected static final String SEPARATOR_ICON = "/images/separator.png";
    protected static final String BREAK_ICON = "/images/line_break.png";
    protected static final String FIELD_ICON = "/images/field.png";
    protected static final String CLEAR_ICON = "/images/clear.png";
    protected static final String SEARCH_ICON = "/images/search.png";
    protected static final String TABLE_ICON = "/images/table.png";
    protected static final String CCDD_ICON = "/images/CCDD.png";
    protected static final String CALCULATOR_ICON = "/images/calculator.png";
    protected static final String VARIABLE_ICON = "/images/variable.png";
    protected static final String BIT_VARIABLE_ICON = "/images/bit_variable.png";
    protected static final String PACKED_VARIABLE_ICON = "/images/packed_variable.png";
    protected static final String LINKED_VARIABLE_ICON = "/images/linked_variable.png";
    protected static final String LINKED_BIT_VARIABLE_ICON = "/images/linked_bit_variable.png";
    protected static final String LINKED_PACKED_VARIABLE_ICON = "/images/linked_packed_variable.png";

    // Dialog box default icon file names
    protected static final String INFORMATION_ICON = "/images/information.png";
    protected static final String QUESTION_ICON = "/images/question.png";
    protected static final String WARNING_ICON = "/images/warning.png";
    protected static final String ERROR_ICON = "/images/error.png";

    // Width, in pixels, of a vertical scroll bar. This is used when sizing
    // tables in dialogs
    protected static int LAF_SCROLL_BAR_WIDTH = 38;

    // Size of a check box icon (width and height) in pixels
    protected static int LAF_CHECK_BOX_HEIGHT = 0;

    // Dialog box button return values
    protected static final int OK_BUTTON = JOptionPane.OK_OPTION;
    protected static final int CANCEL_BUTTON = JOptionPane.CANCEL_OPTION;
    protected static final int UPDATE_BUTTON = 0xfd;
    protected static final int IGNORE_BUTTON = 0xfc;
    protected static final int PRINT_BUTTON = 0xfb;

    // GUI update type
    protected static enum GUIUpdateType
    {
        LAF,
        FONT,
        COLOR
    }

    // Table selection modes
    protected static enum TableSelectionMode
    {
        SELECT_BY_ROW,
        SELECT_BY_COLUMN,
        SELECT_BY_CELL
    }

    // Arrow keys focus options - used for determining keyboard traversal
    // actions in response to arrow key presses
    protected static enum ArrowFocusOption
    {
        USE_DEFAULT_HANDLER,
        HANDLE_UP_ARROW,
        HANDLE_DOWN_ARROW,
        HANDLE_UP_AND_DOWN_ARROWS,
        HANDLE_ALL_ARROWS,
        IGNORE_UP_AND_DOWN_ARROWS
    }

    // Command line command types
    protected static enum CommandLineType
    {
        NAME,
        MINMAX,
        SIZE,
        COLOR,
        OPTION
    }

    // Server connection types
    protected static enum ConnectionType
    {
        NO_CONNECTION,
        TO_SERVER_ONLY,
        TO_DATABASE
    }

    // Database command types
    protected static enum DbCommandType
    {
        QUERY,
        UPDATE,
        COMMAND
    }

    // Database manager dialog types
    protected static enum DbManagerDialogType
    {
        CREATE,
        OPEN,
        RENAME,
        COPY,
        DELETE,
        UNLOCK
    }

    // Server properties dialog types
    protected static enum ServerPropertyDialogType
    {
        LOGIN,
        DB_SERVER,
        WEB_SERVER
    }

    // Database objects
    protected static enum DatabaseObject
    {
        DATABASE,
        TABLE,
        FUNCTION,
        SEQUENCE
    }

    // Manager dialog types
    protected static enum ManagerDialogType
    {
        NEW,
        EDIT,
        RENAME,
        COPY,
        DELETE,
        IMPORT,
        EXPORT_CSV,
        EXPORT_XTCE,
        EXPORT_EDS,
        EXPORT_JSON
    }

    // Table tree types
    protected static enum TableTreeType
    {
        // Prototype tables only, all types
        PROTOTYPE_TABLES,

        // Instance tables only, all types
        INSTANCE_TABLES,

        // Prototype and instance tables, all types
        TABLES,

        // Prototype and instance tables, structure types only, with primitive
        // variables
        STRUCTURES_WITH_PRIMITIVES,

        // Instance tables only, structure types only, with primitive variables
        INSTANCE_STRUCTURES_WITH_PRIMITIVES,

        // Instance tables only, structure types only, with primitive variables
        // and their sample rates
        INSTANCE_STRUCTURES_WITH_PRIMITIVES_AND_RATES,

        // Prototype and instance tables, all types, with primitive variables
        // (for structures)
        TABLES_WITH_PRIMITIVES,

        // Instance tables only, all types, with primitive variables (for
        // structures)
        INSTANCE_TABLES_WITH_PRIMITIVES
    }

    // Table member types
    enum TableMemberType
    {
        TABLES_ONLY,
        INCLUDE_PRIMITIVES
    }

    // Script store/retrieve types
    protected static enum ScriptIOType
    {
        STORE,
        RETRIEVE,
        DELETE
    }

    // Table path format types
    protected static enum TablePathType
    {
        PARENT_AND_VARIABLE,
        VARIABLE_ONLY,
        ITOS_RECORD
    }

    // Project search dialog types
    protected static enum SearchDialogType
    {
        TABLES,
        SCRIPTS,
        LOG
    }

    // Project search types
    protected static enum SearchType
    {
        ALL,
        PROTO,
        DATA,
        SCRIPT
    }

    // Message ID assignment dialog type
    protected static enum MessageIDType
    {
        TABLE_DATA_FIELD,
        TELEMETRY
    }

    // Rate parameters
    protected static enum RateParameter
    {
        MAXIMUM_SECONDS_PER_MESSAGE,
        MAXIMUM_MESSAGES_PER_SECOND,
        INCLUDE_UNEVEN_RATES,
        RATE_COLUMN_NAME,
        STREAM_NAME,
        MAXIMUM_MESSAGES_PER_CYCLE,
        MAXIMUM_BYTES_PER_SECOND
    }

    // Table type update
    protected static enum TableTypeUpdate
    {
        NEW,
        MATCH,
        MISMATCH
    }

    // Project database search result query columns
    protected static enum SearchResultsQueryColumn
    {
        TABLE,
        COLUMN,
        COMMENT,
        CONTEXT
    }

    // Application parameters
    protected static enum ApplicationParameter
    {
        MAXIMUM_NUMBER_OF_SLOTS,
        MAXIMUM_MESSAGES_PER_SECOND,
        MAXIMUM_MESSAGES_PER_CYCLE,
        MAXIMUM_NUMBEROF_COMMANDS
    }

    // Scheduler options
    protected static enum SchedulerType
    {
        TELEMETRY_SCHEDULER,
        APPLICATION_SCHEDULER
    }

    // Data table update types
    protected static enum UpdateType
    {
        ADDITION,
        MODIFICATION,
        DELETION
    }

    // Modifiable font information
    protected static enum ModifiableFontInfo
    {
        INPUT_TEXT("Input Field", "Text input field font", "InputFieldTextFont", "DejaVu Sans", Font.PLAIN, 13),
        LABEL_PLAIN("Label (plain)", "Label and list font (plain)", "LabelPlainFont", "DejaVu Sans", Font.PLAIN, 13),
        LABEL_BOLD("Label (bold)", "Label font (bold); used for dialog labels", "LabelBoldFont", "DejaVu Sans", Font.BOLD, 13),
        TABLE_HEADER("Table Header", "Table column name font", "TableHeaderFont", "DejaVu Sans", Font.BOLD, 13),
        DATA_TABLE_CELL("Data Table Cell", "Table cell font (data tables)", "DataTableCellFont", "Monospaced", Font.PLAIN, 13),
        OTHER_TABLE_CELL("Other Table Cell", "Table cell font (non-data tables)", "OtherTableCellFont", "DejaVu Sans", Font.PLAIN, 13),
        DIALOG_BUTTON("Button", "Dialog box button font", "DialogButtonFont", "Dialog", Font.BOLD, 13),
        MENU_ITEM("Menu Item", "Menu item font", "MenuItemFont", "DejaVu Sans", Font.PLAIN, 13),
        TOOL_TIP("Tool Tip", "Tool tip font. Ignored by some look & feels", "ToolTipFont", "DejaVu Sans", Font.PLAIN, 13),
        TREE_NODE("Tree Node", "Tree node font", "TreeNodeFont", "DejaVu Sans", Font.PLAIN, 13);

        private final String name;
        private final String description;
        private final String preferenceKey;
        private final String defaultFamily;
        private final int defaultStyle;
        private final int defaultSize;
        private ModifiableFont font;

        /**********************************************************************
         * Modifiable font information constructor
         *
         * @param name
         *            font name (for display)
         *
         * @param description
         *            font description (for tool tip)
         *
         * @param preferenceKey
         *            font program preferences key
         * 
         * @param defaultFamily
         *            default font family
         * 
         * @param defaultStyle
         *            default font style
         * 
         * @param defaultSize
         *            default font size
         *********************************************************************/
        ModifiableFontInfo(String name,
                           String description,
                           String preferenceKey,
                           String defaultFamily,
                           int defaultStyle,
                           int defaultSize)
        {
            this.name = name;
            this.description = description;
            this.preferenceKey = preferenceKey;
            this.defaultFamily = defaultFamily;
            this.defaultStyle = defaultStyle;
            this.defaultSize = defaultSize;

            // Create the modifiable font using the default settings
            font = new ModifiableFont(preferenceKey,
                                      defaultFamily,
                                      defaultStyle,
                                      defaultSize);
        }

        /**********************************************************************
         * Get the modifiable font name
         *
         * @return Modifiable font name
         *********************************************************************/
        protected String getName()
        {
            return name;
        }

        /**********************************************************************
         * Get the modifiable font description
         *
         * @return Modifiable font description
         *********************************************************************/
        protected String getDescription()
        {
            return description;
        }

        /**********************************************************************
         * Get the modifiable font program preferences key
         *
         * @return Modifiable font program preferences key
         *********************************************************************/
        protected String getPreferenceKey()
        {
            return preferenceKey;
        }

        /**********************************************************************
         * Get the modifiable font's default family
         * 
         * @return Modifiable font's default family
         *********************************************************************/
        protected String getDefaultFamily()
        {
            return defaultFamily;
        }

        /**********************************************************************
         * Get the modifiable font's default style
         * 
         * @return Modifiable font's default style
         *********************************************************************/
        protected int getDefaultStyle()
        {
            return defaultStyle;
        }

        /**********************************************************************
         * Get the modifiable font's default size
         * 
         * @return Modifiable font's default size
         *********************************************************************/
        protected int getDefaultSize()
        {
            return defaultSize;
        }

        /**********************************************************************
         * Get the modifiable font
         *
         * @return Modifiable font
         *********************************************************************/
        protected ModifiableFont getFont()
        {
            return font;
        }

        /**********************************************************************
         * Get the modifiable font information reference with the specified
         * program preferences key
         *
         * @param prefKey
         *            modifiable font program preferences key
         *
         * @return Modifiable font information reference that has a program
         *         preferences key matching the one specified; null if no
         *         modifiable font's key matches
         *********************************************************************/
        protected static ModifiableFontInfo getModifiableFontInfo(String prefKey)
        {
            ModifiableFontInfo modifiableFont = null;

            // Step through each modifiable font
            for (ModifiableFontInfo modFont : ModifiableFontInfo.values())
            {
                // Check if the font's program preferences key matches the one
                // supplied
                if (modFont.preferenceKey.equals(prefKey))
                {
                    // Store the modifiable font information reference and stop
                    // searching
                    modifiableFont = modFont;
                    break;
                }
            }

            return modifiableFont;
        }

        /**********************************************************************
         * Set the modifiable font to the new font values and store the changes
         * in the program preferences
         *
         * @param family
         *            font family
         * 
         * @param style
         *            font style
         * 
         * @param size
         *            font size
         * 
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected void setFont(String family,
                               int style,
                               int size,
                               Preferences progPrefs)
        {
            font = new ModifiableFont(preferenceKey, family, style, size);
            progPrefs.put(preferenceKey, family + "," + style + "," + size);
        }

        /**********************************************************************
         * Set the modifiable fonts to the values stored in the program
         * preferences
         *
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected static void setModifiableFonts(Preferences progPrefs)
        {
            // Step through each modifiable font
            for (ModifiableFontInfo modFont : ModifiableFontInfo.values())
            {
                // Retrieve the font information from the program preferences
                // and use this information to set the modifiable font
                String[] fontInfo = progPrefs.get(modFont.getPreferenceKey(),
                                                  modFont.getDefaultFamily()
                                                      + ","
                                                      + modFont.getDefaultStyle()
                                                      + ","
                                                      + modFont.getDefaultSize()).split(",");
                modFont.font = new ModifiableFont(modFont.getPreferenceKey(),
                                                  fontInfo[0],
                                                  Integer.valueOf(fontInfo[1]),
                                                  Integer.valueOf(fontInfo[2]));
            }
        }

        /**********************************************************************
         * Update the components of the specified container that use a
         * modifiable font to the updated font
         *
         * @param container
         *            container in which to check the fonts of the components
         *********************************************************************/
        protected static void updateFonts(Container container)
        {
            // Step through each component in the container
            for (Component comp : container.getComponents())
            {
                // Check if the component represents a Swing component and that
                // it has child components
                if (comp instanceof JComponent && ((JComponent) comp).getComponentCount() != 0)
                {
                    // Update the child's components
                    updateFonts((Container) comp);
                }

                // Check if the component is a table
                if (comp instanceof JTable)
                {
                    // Step through each row in the table
                    for (int row = 0; row < ((JTable) comp).getRowCount(); row++)
                    {
                        // Step through each column in the table
                        for (int column = 0; column < ((JTable) comp).getColumnCount(); column++)
                        {
                            // Set the font for the cell specified by the row
                            // and column to the modifiable font
                            setModifiableFont(((JTable) comp).getCellRenderer(row, column).getTableCellRendererComponent((JTable) comp,
                                                                                                                         "",
                                                                                                                         false,
                                                                                                                         false,
                                                                                                                         row,
                                                                                                                         column));
                        }
                    }
                }

                // Set the component's font to the updated font
                setModifiableFont(comp);
            }
        }

        /**********************************************************************
         * Compare the font for the component to the modifiable fonts. If a
         * match is found set the component's font to the current preferred
         * font setting
         *
         * @param comp
         *            component for which to check the font
         *********************************************************************/
        private static void setModifiableFont(Component comp)
        {
            // Check if the component is using a modifiable font
            if (comp.getFont() instanceof ModifiableFont)
            {
                // Step through the defined modifiable fonts
                for (ModifiableFontInfo modFont : ModifiableFontInfo.values())
                {
                    // Check if the component is using this font
                    if (((ModifiableFont) comp.getFont()).getModifiableFontIdentifier().equals(modFont.getPreferenceKey()))
                    {
                        // Set the component's font to the one specified by the
                        // modifiable font and stop searching
                        comp.setFont(modFont.getFont());
                        break;
                    }
                }
            }
        }
    }

    // Modifiable color information
    protected static enum ModifiableColorInfo
    {
        INPUT_TEXT("Input field text", "Input field text color", "InputTextColor", 0, 0, 0),
        INPUT_BACK("Input field background", "Input field background color", "InputBackgroundColor", 255, 255, 255),
        INPUT_DISABLE_BACK("Input field background (disabled)", "Input field background color when disabled", "InputDisabledBackgroundColor", 192, 192, 192),
        LABEL_TEXT("Label text", "General text label color", "LabelTextColor", 0, 0, 0),
        TABLE_TEXT("Table text", "Table text color (when not selected)", "TableTextColor", 0, 0, 0),
        TABLE_BACK("Table background", "Table background color (when not selected)", "TableBackgroundColor", 255, 255, 255),
        ALTERNATE_BACK("Alternating table row background", "Background color for alternating table rows (when not selected)", "TableAlternateBackgroundColor", 240, 245, 245),
        SELECTED_TEXT("Selected table cell text", "Text color for a table's selected cell(s)", "SelectedTextColor", 255, 255, 255),
        SELECTED_BACK("Selected table cell background", "Background color for a table's selected cell(s)", "SelectedBackgroundColor", 110, 150, 200),
        FOCUS_BACK("Focused table cell background", "Background color for the table cell that has the input focus", "FocusBackgroundColor", 60, 100, 180),
        TABLE_GRID("Table grid lines", "Color for table grid lines. The log table grid lines use the table background color", "TableGridColor", 230, 240, 240),
        TEXT_HIGHLIGHT("Highlighted macro and matching search text", "Highlight color for macros and matching search text", "TextHighlightColor", 200, 235, 245),
        REQUIRED_BACK("Required cell and data field background", "Background color for input fields that are required", "RequiredBackgroundColor", 255, 255, 0),
        PROTECTED_TEXT("Protected cell text", "Text color for a protected (non-editable) cell", "ProtectedTextColor", 0, 0, 255),
        PROTECTED_BACK("Protected cell background", "Background color for a protected (non-editable) cell", "ProtectedBackgroundColor", 192, 192, 192),
        VALID_TEXT("Valid table text", "Text color for valid table rows. Used in the telemetry and application scheduler tables", "ValidTextColor", 0, 200, 0),
        INVALID_TEXT("Invalid table text", "Text color for invalid table rows. Used in the telemetry and application scheduler tables", "InvalidTextColor", 200, 0, 0),
        SPECIAL_LABEL_TEXT("Dialog component group label", "Text color for a group of components in a dialog", "SpecialLabelTextColor", 170, 40, 80),
        TOOL_TIP_TEXT("Tool tip text", "Text color for tool tip pop-ups. Ignored by some look & feels", "ToolTipTextColor", 0, 0, 0),
        TOOL_TIP_BACK("Tool tip background", "Background color for tool tip pop-ups. Ignored by some look & feels", "ToolTipBackgroundColor", 245, 245, 180);

        private final String name;
        private final String description;
        private final String preferenceKey;
        private final int defaultRed;
        private final int defaultGreen;
        private final int defaultBlue;
        private ModifiableColor color;

        /**********************************************************************
         * Modifiable color information constructor
         *
         * @param name
         *            color name (for display)
         *
         * @param description
         *            color description (for tool tip)
         *
         * @param preferenceKey
         *            color program preferences key
         * 
         * @param defaultRed
         *            default red color component
         * 
         * @param defaultGreen
         *            default green color component
         * 
         * @param defaultBlue
         *            default blue color component
         *********************************************************************/
        ModifiableColorInfo(String name,
                            String description,
                            String preferenceKey,
                            int defaultRed,
                            int defaultGreen,
                            int defaultBlue)
        {
            this.name = name;
            this.description = description;
            this.preferenceKey = preferenceKey;
            this.defaultRed = defaultRed;
            this.defaultGreen = defaultGreen;
            this.defaultBlue = defaultBlue;

            // Create the modifiable color using the default settings
            color = new ModifiableColor(preferenceKey,
                                        defaultRed,
                                        defaultGreen,
                                        defaultBlue);
        }

        /**********************************************************************
         * Get the size name (for display)
         *
         * @return Size value name (for display)
         *********************************************************************/
        protected String getName()
        {
            return name;
        }

        /**********************************************************************
         * Get the modifiable color description
         *
         * @return Modifiable color description
         *********************************************************************/
        protected String getDescription()
        {
            return description;
        }

        /**********************************************************************
         * Get the modifiable color program preferences key
         *
         * @return Modifiable color program preferences key
         *********************************************************************/
        protected String getPreferenceKey()
        {
            return preferenceKey;
        }

        /**********************************************************************
         * Get the modifiable color's default red component
         * 
         * @return Modifiable color's default red component
         *********************************************************************/
        protected int getDefaultRed()
        {
            return defaultRed;
        }

        /**********************************************************************
         * Get the modifiable color's default green component
         * 
         * @return Modifiable color's default green component
         *********************************************************************/
        protected int getDefaultGreen()
        {
            return defaultGreen;
        }

        /**********************************************************************
         * Get the modifiable color's default blue component
         * 
         * @return Modifiable color's default blue component
         *********************************************************************/
        protected int getDefaultBlue()
        {
            return defaultBlue;
        }

        /**********************************************************************
         * Get the modifiable color
         *
         * @return Modifiable color
         *********************************************************************/
        protected ModifiableColor getColor()
        {
            return color;
        }

        /**********************************************************************
         * Set the modifiable color
         *
         * @param color
         *            modifiable color
         *********************************************************************/
        protected void setColor(ModifiableColor color)
        {
            this.color = color;
        }

        /**********************************************************************
         * Get the modifiable color information reference with the specified
         * program preferences key
         *
         * @param prefKey
         *            modifiable color program preferences key
         *
         * @return Modifiable color information reference that has a program
         *         preferences key matching the one specified; null if no
         *         modifiable color's key matches
         *********************************************************************/
        protected static ModifiableColorInfo getModifiableColorInfo(String prefKey)
        {
            ModifiableColorInfo modifiableColor = null;

            // Step through each modifiable color
            for (ModifiableColorInfo modFont : ModifiableColorInfo.values())
            {
                // Check if the color's program preferences key matches the one
                // supplied
                if (modFont.preferenceKey.equals(prefKey))
                {
                    // Store the modifiable color information reference and
                    // stop searching
                    modifiableColor = modFont;
                    break;
                }
            }

            return modifiableColor;
        }

        /**********************************************************************
         * Set the modifiable color to the new color values and store the
         * changes in the program preferences
         *
         * @param red
         *            red color component
         * 
         * @param green
         *            green color component
         * 
         * @param blue
         *            blue color component
         * 
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected void setModifiableColor(int red,
                                          int green,
                                          int blue,
                                          Preferences progPrefs)
        {
            color = new ModifiableColor(preferenceKey, red, green, blue);
            progPrefs.put(preferenceKey, red + "," + green + "," + blue);
        }

        /**********************************************************************
         * Set the modifiable colors to the values stored in the program
         * preferences
         *
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected static void setModifiableColors(Preferences progPrefs)
        {
            // Step through each modifiable color
            for (ModifiableColorInfo modColor : ModifiableColorInfo.values())
            {
                // Retrieve the color information from the program preferences
                // and use this information to set the modifiable color
                String[] fontInfo = progPrefs.get(modColor.getPreferenceKey(),
                                                  modColor.getDefaultRed()
                                                      + ","
                                                      + modColor.getDefaultGreen()
                                                      + ","
                                                      + modColor.getDefaultBlue()).split(",");
                modColor.color = new ModifiableColor(modColor.getPreferenceKey(),
                                                     Integer.valueOf(fontInfo[0]),
                                                     Integer.valueOf(fontInfo[1]),
                                                     Integer.valueOf(fontInfo[2]));
            }
        }

        /**********************************************************************
         * Update the components of the specified container that use a
         * modifiable color to the updated color
         *
         * @param container
         *            container in which to check the colors of the components
         *********************************************************************/
        protected static void updateColors(Container container)
        {
            // Step through each component in the container
            for (Component comp : container.getComponents())
            {
                // Check if the component represents a Swing component and that
                // it has child components
                if (comp instanceof JComponent && ((JComponent) comp).getComponentCount() != 0)
                {
                    // Update the child's components
                    updateColors((Container) comp);
                }

                // Check if the component is a table
                if (comp instanceof JTable)
                {
                    // Step through each row in the table
                    for (int row = 0; row < ((JTable) comp).getRowCount(); row++)
                    {
                        // Step through each column in the table
                        for (int column = 0; column < ((JTable) comp).getColumnCount(); column++)
                        {
                            // Set the font for the cell specified by the row
                            // and column to the modifiable color
                            setModifiableColor(((JTable) comp).getCellRenderer(row, column).getTableCellRendererComponent((JTable) comp,
                                                                                                                          "",
                                                                                                                          false,
                                                                                                                          false,
                                                                                                                          row,
                                                                                                                          column));
                        }
                    }
                }

                // Set the component's color to the updated color
                setModifiableColor(comp);
            }
        }

        /**********************************************************************
         * Compare the color for the component to the modifiable colors. If a
         * match is found set the component's color to the current preferred
         * color setting
         *
         * @param comp
         *            component for which to check the color
         *********************************************************************/
        private static void setModifiableColor(Component comp)
        {
            // Check if the component is using a modifiable color as a
            // foreground color
            if (comp.getForeground() instanceof ModifiableColor)
            {
                // Step through the defined modifiable colors
                for (ModifiableColorInfo modColor : ModifiableColorInfo.values())
                {
                    // Check if the component is using this color
                    if (((ModifiableColor) comp.getForeground()).getModifiableColorIdentifier().equals(modColor.getPreferenceKey()))
                    {
                        // Set the component's color to the one specified by
                        // the modifiable color and stop searching
                        comp.setForeground(modColor.getColor());
                        break;
                    }
                }
            }

            // Check if the component is using a modifiable color as a
            // background color
            if (comp.getBackground() instanceof ModifiableColor)
            {
                // Step through the defined modifiable colors
                for (ModifiableColorInfo modColor : ModifiableColorInfo.values())
                {
                    // Check if the component is using this color
                    if (((ModifiableColor) comp.getBackground()).getModifiableColorIdentifier().equals(modColor.getPreferenceKey()))
                    {
                        // Set the component's color to the one specified by
                        // the modifiable color and stop searching
                        comp.setBackground(modColor.getColor());
                        break;
                    }
                }
            }
        }
    }

    // Modifiable size information
    protected static enum ModifiableSizeInfo
    {
        MAX_LOG_MESSAGE_LENGTH("Maximum log message length", "Maximum number of characters to display in the session event log", "MaximumLogMessageLength", 250, 10, 1000),
        NUM_REMEMBERED_SEARCHES("Maximum remembered search strings", "Maximum number of remembered search strings", "NumberOfRememberedSearches", 30, 1, 100),
        NUM_REMEMBERED_SERVERS("Maximum remembered server names", "Maximum number of remembered server names", "NumberOfRememberedServers", 30, 1, 100),
        MIN_DIALOG_WIDTH("Minimum dialog width", "Minimum dialog window width in pixels", "MinimumDialogWidth", 300, 100, 1000),
        MIN_WINDOW_WIDTH("Minimum frame width", "Minimum frame window width in pixels", "MinimumWindowWidth", 750, 100, 1000),
        MIN_WINDOW_HEIGHT("Minimum frame height", "Minimum frame window height in pixels", "MinimumWindowHeight", 400, 100, 1000),
        MAX_DIALOG_LINE_LENGTH("Maximum dialog line length", "Maximum number of characters per line in a dialog", "MaximumDialogLineLength", 80, 10, 500),
        MAX_DIALOG_MESSAGE_LENGTH("Maximum dialog message length", "Maximum number of characters to display in a dialog", "MaximumDialogMessageLength", 400, 10, 1000),
        MAX_TOOL_TIP_LENGTH("Maximum tool tip line length", "Maximum number of characters per line in a tool tip", "MaximumToolTipLineLength", 40, 10, 200),
        MAX_DATA_FIELD_CHAR_WIDTH("Maximum data field length", "Maximum number of characters in a data field", "MaximumDataFieldLength", 100, 5, 200),
        INIT_VIEWABLE_TABLE_ROWS("Initial table rows (non-data)", "Number of non-data table rows to display initially", "InitialViewableTableRows", 10, 1, 50),
        INIT_VIEWABLE_DATA_TABLE_ROWS("Initial table rows (data)", "Number of data table rows to display initially", "InitialViewableDataTableRows", 16, 1, 50),
        INIT_VIEWABLE_LIST_ROWS("Initial list rows", "Number of rows of radio buttons or check boxes to display initially", "InitalViewableListRows", 12, 1, 50),
        MAX_INIT_CELL_WIDTH("Maximum initial table cell width", "Maximum initial table cell width in pixels", "MaximumInitialTableCellWidth", 250, 25, 1000);

        private final String name;
        private final String description;
        private final String preferenceKey;
        private final int defaultValue;
        private final int minimumValue;
        private final int maximumValue;
        private int size;

        /**********************************************************************
         * Modifiable size information constructor
         *
         * @param name
         *            size value name (for display)
         * 
         * @param description
         *            size value description (for tool tip)
         * 
         * @param preferenceKey
         *            size value program preferences key
         * 
         * @param defaultValue
         *            default size value
         * 
         * @param minimumValue
         *            minimum size value
         * 
         * @param maximumValue
         *            maximum size value
         *********************************************************************/
        ModifiableSizeInfo(String name,
                           String description,
                           String preferenceKey,
                           int defaultValue,
                           int minimumValue,
                           int maximumValue)
        {
            this.name = name;
            this.description = description;
            this.preferenceKey = preferenceKey;
            this.defaultValue = defaultValue;
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
            size = defaultValue;
        }

        /**********************************************************************
         * Get the size name (for display)
         *
         * @return Size value name (for display)
         *********************************************************************/
        protected String getName()
        {
            return name;
        }

        /**********************************************************************
         * Get the size description (for tool tip)
         *
         * @return Size value description (for tool tip)
         *********************************************************************/
        protected String getDescription()
        {
            return description;
        }

        /**********************************************************************
         * Get the size value program preferences key
         *
         * @return Size value program preferences key
         *********************************************************************/
        protected String getPreferenceKey()
        {
            return preferenceKey;
        }

        /**********************************************************************
         * Get the default size value
         *
         * @return Default size value
         *********************************************************************/
        protected int getDefault()
        {
            return defaultValue;
        }

        /**********************************************************************
         * Get the minimum size value
         *
         * @return Minimum size value
         *********************************************************************/
        protected int getMinimum()
        {
            return minimumValue;
        }

        /**********************************************************************
         * Get the maximum size value
         *
         * @return Maximum size value
         *********************************************************************/
        protected int getMaximum()
        {
            return maximumValue;
        }

        /**********************************************************************
         * Get the current size value
         *
         * @return Current size value
         *********************************************************************/
        protected int getSize()
        {
            return size;
        }

        /**********************************************************************
         * Set the size value
         *
         * @param size
         *            new size value
         * 
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected void setSize(int size, Preferences progPrefs)
        {
            this.size = size;
            progPrefs.putInt(preferenceKey, size);
        }

        /**********************************************************************
         * Get the modifiable size information reference with the specified
         * program preferences key
         *
         * @param prefKey
         *            modifiable size program preferences key
         *
         * @return Modifiable size information reference that has a program
         *         preferences key matching the one specified; null if no
         *         modifiable size's key matches
         *********************************************************************/
        protected static ModifiableSizeInfo getModifiableSizeInfo(String prefKey)
        {
            ModifiableSizeInfo modifiableSize = null;

            // Step through each modifiable size
            for (ModifiableSizeInfo modSize : ModifiableSizeInfo.values())
            {
                // Check if the size's program preferences key matches the one
                // supplied
                if (modSize.preferenceKey.equals(prefKey))
                {
                    // Store the modifiable size information reference and
                    // stop searching
                    modifiableSize = modSize;
                    break;
                }
            }

            return modifiableSize;
        }

        /**********************************************************************
         * Set the modifiable sizes to the values stored in the program
         * preferences
         *
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected static void setSizes(Preferences progPrefs)
        {
            // Step through each modifiable size value
            for (ModifiableSizeInfo modSize : ModifiableSizeInfo.values())
            {
                // Retrieve the size value from the program preferences and use
                // this information to set the modifiable size value
                String sizeInfo = progPrefs.get(modSize.getPreferenceKey(),
                                                String.valueOf(modSize.getDefault()));
                modSize.size = Integer.valueOf(sizeInfo);
            }
        }
    }

    // Modifiable spacing information
    protected static enum ModifiableSpacingInfo
    {
        BUTTON_PAD("Button padding", "Dialog box button padding between the group of buttons and the edge of the dialog window or upper dialog components, in pixels", "ButtonPad", 16, 0, 50),
        BUTTON_GAP("Button gap", "Dialog box button padding, between individual buttons, in pixels", "ButtonGap", 4, 0, 50),
        LABEL_HORIZONTAL_SPACING("Label horizontal spacing", "Defines the horizontal spacing between a text label and an adjacent component, in pixels", "LabelHorizontalSpacing", 10, 1, 50),
        LABEL_VERTICAL_SPACING("Label vertical spacing", "Defines the vertical spacing between a text label and an adjacent component, in pixels", "LabelVerticalSpacing", 7, 1, 50),
        HEADER_HORIZONTAL_PADDING("Table header horizontal spacing", "Table header horizontal padding, in pixels. The padding is split equally between each side of the header text. This padding provides room for the column sort arrow", "HeaderHorizontalPadding", 28, 1, 50),
        HEADER_VERTICAL_PADDING("Table header vertical spacing", "Table header vertical padding, in pixels. The padding is split equally between the top and bottom of the header text", "HeaderVerticalPadding", 4, 1, 50),
        CELL_HORIZONTAL_PADDING("Table cell horizontal padding", "Table cell horizontal padding, in pixels", "CellHorizontalPadding", 5, 1, 50),
        CELL_VERTICAL_PADDING("Table cell vertical padding", "Table cell vertical padding, in pixels", "CellVerticalPadding", 3, 1, 50),
        DIALOG_BORDER_PAD("Dialog border padding", "Padding between the dialog contents and the dialog's frame, in pixels", "DialogBorderPadding", 4, 1, 50),
        DIALOG_ICON_PAD("Dialog icon padding", "Padding between the a dialog's icon and text message, in pixels", "DialogIconPadding", 10, 1, 50);

        private final String name;
        private final String description;
        private final String preferenceKey;
        private final int defaultValue;
        private final int minimumValue;
        private final int maximumValue;
        private int spacing;

        /**********************************************************************
         * Modifiable spacing information constructor
         *
         * @param name
         *            spacing value name (for display)
         * 
         * @param description
         *            spacing value description (for tool tip)
         * 
         * @param preferenceKey
         *            spacing value program preferences key
         * 
         * @param defaultValue
         *            default spacing value
         * 
         * @param minimumValue
         *            minimum spacing value
         * 
         * @param maximumValue
         *            maximum spacing value
         *********************************************************************/
        ModifiableSpacingInfo(String name,
                              String description,
                              String preferenceKey,
                              int defaultValue,
                              int minimumValue,
                              int maximumValue)
        {
            this.name = name;
            this.description = description;
            this.preferenceKey = preferenceKey;
            this.defaultValue = defaultValue;
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
            spacing = defaultValue;
        }

        /**********************************************************************
         * Get the spacing name (for display)
         *
         * @return Spacing value name (for display)
         *********************************************************************/
        protected String getName()
        {
            return name;
        }

        /**********************************************************************
         * Get the spacing description (for tool tip)
         *
         * @return Spacing value description (for tool tip)
         *********************************************************************/
        protected String getDescription()
        {
            return description;
        }

        /**********************************************************************
         * Get the spacing value program preferences key
         *
         * @return Spacing value program preferences key
         *********************************************************************/
        protected String getPreferenceKey()
        {
            return preferenceKey;
        }

        /**********************************************************************
         * Get the default spacing value
         *
         * @return Default spacing value
         *********************************************************************/
        protected int getDefault()
        {
            return defaultValue;
        }

        /**********************************************************************
         * Get the minimum spacing value
         *
         * @return Minimum spacing value
         *********************************************************************/
        protected int getMinimum()
        {
            return minimumValue;
        }

        /**********************************************************************
         * Get the maximum spacing value
         *
         * @return Maximum spacing value
         *********************************************************************/
        protected int getMaximum()
        {
            return maximumValue;
        }

        /**********************************************************************
         * Get the current spacing value
         *
         * @return Current spacing value
         *********************************************************************/
        protected int getSpacing()
        {
            return spacing;
        }

        /**********************************************************************
         * Set the spacing value
         *
         * @param spacing
         *            new spacing value
         * 
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected void setSpacing(int spacing, Preferences progPrefs)
        {
            this.spacing = spacing;
            progPrefs.putInt(preferenceKey, spacing);
        }

        /**********************************************************************
         * Get the modifiable spacing information reference with the specified
         * program preferences key
         *
         * @param prefKey
         *            modifiable spacing program preferences key
         *
         * @return Modifiable spacing information reference that has a program
         *         preferences key matching the one specified; null if no
         *         modifiable spacing's key matches
         *********************************************************************/
        protected static ModifiableSpacingInfo getModifiableSpacingInfo(String prefKey)
        {
            ModifiableSpacingInfo modifiableSpacing = null;

            // Step through each modifiable spacing
            for (ModifiableSpacingInfo modSpacing : ModifiableSpacingInfo.values())
            {
                // Check if the spacing's program preferences key matches the
                // one supplied
                if (modSpacing.preferenceKey.equals(prefKey))
                {
                    // Store the modifiable spacing information reference and
                    // stop searching
                    modifiableSpacing = modSpacing;
                    break;
                }
            }

            return modifiableSpacing;
        }

        /**********************************************************************
         * Set the modifiable spacings to the values stored in the program
         * preferences
         *
         * @param progPrefs
         *            reference to the program preferences
         *********************************************************************/
        protected static void setSpacings(Preferences progPrefs)
        {
            // Step through each modifiable spacing value
            for (ModifiableSpacingInfo modSpacing : ModifiableSpacingInfo.values())
            {
                // Retrieve the spacing value from the program preferences and
                // use this information to set the modifiable spacing value
                String spacingInfo = progPrefs.get(modSpacing.getPreferenceKey(),
                                                   String.valueOf(modSpacing.getDefault()));
                modSpacing.spacing = Integer.valueOf(spacingInfo);
            }
        }
    }

    /**************************************************************************
     * File extensions
     *************************************************************************/
    protected static enum FileExtension
    {
        LOG("log", "CCDD project event logs"),
        DBU("dbu", "database backup files"),
        CSV("csv", "comma-separated values"),
        XTCE("xtce", "extensible markup language telemetric and command exchange XML"),
        EDS("eds", "electronic data sheet XML"),
        JSON("json", "JavaScript object notation");

        private final String entensionName;
        private final String description;

        /**********************************************************************
         * File extensions constructor
         * 
         * @param extensionName
         *            file extension name
         * 
         * @param description
         *            file extension description
         *********************************************************************/
        FileExtension(String extensionName, String description)
        {
            this.entensionName = extensionName;
            this.description = description;
        }

        /**********************************************************************
         * Get the file extension name
         * 
         * @return File extension name
         *********************************************************************/
        protected String getExtensionName()
        {
            return entensionName;
        }

        /**********************************************************************
         * Get the file extension description
         * 
         * @return File extension description
         *********************************************************************/
        protected String getDescription()
        {
            return description;
        }

        /**********************************************************************
         * Get the file extension
         * 
         * @return File extension
         *********************************************************************/
        protected String getExtension()
        {
            return "." + entensionName;
        }
    }

    /**************************************************************************
     * Base data type information
     *************************************************************************/
    protected static enum BaseDataTypeInfo
    {
        SIGNED_INT("signed integer"),
        UNSIGNED_INT("unsigned integer"),
        FLOATING_POINT("floating point"),
        CHARACTER("character"),
        POINTER("pointer");

        private final String name;

        /**********************************************************************
         * Base data type information constructor
         * 
         * @param name
         *            base data type name
         *********************************************************************/
        BaseDataTypeInfo(String name)
        {
            this.name = name;
        }

        /**********************************************************************
         * Get the base data type name
         * 
         * @return Base data type name
         *********************************************************************/
        protected String getName()
        {
            return name;
        }

        /**********************************************************************
         * Get the base data type with the specified name
         * 
         * @param baseTypeName
         *            base data type name
         * 
         * @return Base data type with the specified name
         *********************************************************************/
        protected static BaseDataTypeInfo getBaseType(String baseTypeName)
        {
            BaseDataTypeInfo baseType = null;

            // Step through each base data type
            for (BaseDataTypeInfo baseTypeInfo : BaseDataTypeInfo.values())
            {
                // Check if the base data type name matches the target name
                if (baseTypeInfo.getName().equals(baseTypeName))
                {
                    // Store the base data type and stop searching
                    baseType = baseTypeInfo;
                    break;
                }
            }

            return baseType;
        }
    }

    /**************************************************************************
     * Default primitive data type information
     *************************************************************************/
    protected static enum DefaultPrimitiveTypeInfo
    {
        INT8("int8_t", "signed char", 1, BaseDataTypeInfo.SIGNED_INT),
        INT16("int16_t", "signed short int", 2, BaseDataTypeInfo.SIGNED_INT),
        INT32("int32_t", "signed int", 4, BaseDataTypeInfo.SIGNED_INT),
        INT64("int64_t", "signed long int", 8, BaseDataTypeInfo.SIGNED_INT),
        UINT8("uint8_t", "unsigned char", 1, BaseDataTypeInfo.UNSIGNED_INT),
        UINT16("uint16_t", "unsigned short int", 2, BaseDataTypeInfo.UNSIGNED_INT),
        UINT32("uint32_t", "unsigned int", 4, BaseDataTypeInfo.UNSIGNED_INT),
        UINT64("uint64_t", "unsigned long int", 8, BaseDataTypeInfo.UNSIGNED_INT),
        FLOAT("float", "float", 4, BaseDataTypeInfo.FLOATING_POINT),
        DOUBLE("double", "double", 8, BaseDataTypeInfo.FLOATING_POINT),
        CHAR("char", "char", 1, BaseDataTypeInfo.CHARACTER),
        STRING("string", "char", 2, BaseDataTypeInfo.CHARACTER),
        ADDRESS("address", "void *", 4, BaseDataTypeInfo.POINTER);

        private final String userName;
        private final String cType;
        private final int bytes;
        private final BaseDataTypeInfo baseType;

        /**********************************************************************
         * Default primitive data type information constructor
         * 
         * @param bytes
         *            number of bytes for this data type
         * 
         * @param cType
         *            C language data type name
         * 
         * @param userName
         *            user-defined data type name
         * 
         * @param baseType
         *            base data type
         *********************************************************************/
        DefaultPrimitiveTypeInfo(String userName,
                                 String cType,
                                 int bytes,
                                 BaseDataTypeInfo baseType)
        {
            this.userName = userName;
            this.cType = cType;
            this.bytes = bytes;
            this.baseType = baseType;
        }

        /**********************************************************************
         * Get the data type user-defined name
         * 
         * @return Data type user-defined name
         *********************************************************************/
        protected String getUserName()
        {
            return userName;
        }

        /**********************************************************************
         * Get the data type C language name
         * 
         * @return Data type C language name
         *********************************************************************/
        protected String getCType()
        {
            return cType;
        }

        /**********************************************************************
         * Get the data type size in bytes
         * 
         * @return Data type size in bytes
         *********************************************************************/
        protected int getSizeInBytes()
        {
            return bytes;
        }

        /**********************************************************************
         * Get the base data type
         * 
         * @return Base data type
         *********************************************************************/
        protected BaseDataTypeInfo getBaseType()
        {
            return baseType;
        }

        /**********************************************************************
         * Get the default data type definitions for use in building the data
         * type definitions table in the database
         * 
         * @return Default column definitions statement
         *********************************************************************/
        protected static String getDataTypeDefinitions()
        {
            String columnDefn = "";

            // Step through the default data types
            for (DefaultPrimitiveTypeInfo defType : DefaultPrimitiveTypeInfo.values())
            {
                // Add the column definition
                columnDefn += "('"
                              + defType.getUserName()
                              + "', '"
                              + defType.getCType()
                              + "', "
                              + defType.getSizeInBytes()
                              + ", '"
                              + defType.getBaseType().getName()
                              + "'), ";
            }

            // Remove the ending comma
            return CcddUtilities.removeTrailer(columnDefn, ", ");
        }
    }

    /**************************************************************************
     * Input data types. The Break and Separator types are used by data fields
     *************************************************************************/
    protected static enum InputDataType
    {
        ALPHANUMERIC("Alphanumeric",
                     "[a-zA-Z_][a-zA-Z0-9_]*",
                     "text",
                     "Alphabetic or underscore first character followed by zero "
                         + "or more alphabetic, numeric, and underscore characters"),

        ALPHANUMERIC_MULTI("Alphanumeric (multi)",
                           "(?:" + ALPHANUMERIC.getInputMatch() + "\\s*?)+",
                           "text",
                           "One or more alphanumeric entries (see Alphanumeric) "
                               + "separated by one or more white space characters"),

        ARGUMENT_NAME("Argument name",
                      ALPHANUMERIC.getInputMatch(),
                      "text",
                      "Command argument name; same constraints as for an "
                          + "alphanumeric (see Alphanumeric)"),

        ARRAY_INDEX("Array index",
                    "^\\s*\\+??\\s*0*([2-9]|[1-9]\\d+)(\\s*,\\s*\\+??\\s*0*([2-9]|[1-9]\\d+))*",
                    "array",
                    "Variable array index in the format #<, #<...>>"),

        BIT_LENGTH("Bit length",
                   "^\\+??\\s*0*([1-9]\\d*)",
                   "integer",
                   "Bit length; positive integer (initial '+' and leading "
                       + "zeroes are optional)"),

        BOOLEAN("Boolean",
                "(?i)true|false",
                "boolean",
                "Boolean value; true or false"),

        COMMAND_CODE("Command code",
                     "^(?:0x)?[a-fA-F0-9]*",
                     "hexadecimal",
                     "Command code; hexadecimal number (see Hexadecimal)"),

        COMMAND_NAME("Command name",
                     ALPHANUMERIC.getInputMatch(),
                     "text",
                     "Command name; same constraints as for an "
                         + "alphanumeric (see Alphanumeric)"),

        DESCRIPTION("Description",
                    ".*",
                    "text",
                    "Data description; same constraints as for text (see Text)"),

        ENUMERATION("Enumeration",
                    ".*",
                    "enumeration",
                    "Text, including alphabetic, numeric, and special characters"),

        FLOAT("Floating point",
              "^[\\+-]??\\s*0*(\\.0*)??\\d+\\d*(\\.\\d*)??",
              "float",
              "Floating point value consisting of one or more of the numerals "
                  + "0 - 9 and a single optional decimal point (leading '+' or "
                  + "'-' is optional)"),

        FLOAT_POSITIVE("Positive float",
                       "^\\+??\\s*0*\\.??0*[1-9]+\\d*(\\.\\d*)??",
                       "float",
                       "Floating point value > 0.0 (leading '+' is optional; "
                           + "see Floating point)"),

        FLOAT_NON_NEGATIVE("Non-negative float",
                           "^\\+??\\s*0*(\\.0*)??\\d+\\d*(\\.\\d*)??",
                           "float",
                           "Floating point value >= 0.0 (leading '+' is "
                               + "optional; see Floating point)"),

        FLOAT_NEGATIVE("Negative float",
                       "^-\\s*0*(\\.0*)??\\d+\\d*(\\.\\d*)??",
                       "float",
                       "Floating point value < 0.0 (leading '-' is required; "
                           + "see Floating point)"),

        INTEGER("Integer",
                "^[\\+-]??\\s*\\d*",
                "integer",
                "Integer value consisting of one or more of the "
                    + "numerals 0 - 9 (leading '+' or '-' is optional)"),

        INT_POSITIVE("Positive integer",
                     "^\\+??\\s*0*([1-9][0-9]*)",
                     "integer",
                     "Integer value > 0 (leading '+' is optional; see Integer)"),

        INT_GTR_THN_1("Integer > 1",
                      "^\\+??\\s*0*([2-9]|[1-9]\\d+)",
                      "integer",
                      "Integer value > 1 (leading '+' is optional; see Integer)"),

        INT_NON_NEGATIVE("Non-negative integer",
                         "^\\+??\\s*0*\\d+",
                         "integer",
                         "Integer value > -1 (leading '+' is optional; see Integer)"),

        INT_NEGATIVE("Negative integer",
                     "^-\\s*0*\\d+",
                     "integer",
                     "Integer value < 0 (leading '-' is required; see Integer)"),

        HEXADECIMAL("Hexadecimal",
                    "^(?:0x)?[a-fA-F0-9]*",
                    "hexadecimal",
                    "Hexadecimal number; optional initial '0x' or '0X' "
                        + "followed by one or more hexadecimal digits (0 - 9, "
                        + "a - f (case insensitive))"),

        HEXADECIMAL_RANGE("Hexadecimal range",
                          "^(?:$|(?:0x)?([a-fA-F0-9]+)(?:\\s*-\\s*(?:0x)?([a-fA-F0-9]*)|$))",
                          "hexadecimal",
                          "Hexadecimal range; hexadecimal value followed optionally by a "
                              + "hyphen and a second hexadecimal value (see Hexadecimal)"),

        MESSAGE_ID("Message ID",
                   "^(?:0x)?[a-fA-F0-9]*",
                   "hexadecimal",
                   "Message ID: hexadecimal; optional initial '0x' or '0X' "
                       + "followed by one or more hexadecimal digits (0 - 9, "
                       + "a - f (case insensitive))"),

        MINIMUM("Minimum",
                "(" + INTEGER.getInputMatch() + ")|("
                    + FLOAT.getInputMatch() + ")",
                "minimum",
                "Minimum value; a boolean, integer, floating point, or "
                    + "hexadecimal value (depending on context; see Boolean, "
                    + "Integer, Floating point, and Hexadecimal) that must be "
                    + "less than or equal to the corresponding maximum value "
                    + "(see Maximum)"),

        MAXIMUM("Maximum",
                "(" + INTEGER.getInputMatch() + ")|("
                    + FLOAT.getInputMatch() + ")",
                "maximum",
                "Maximum value; a boolean, integer, floating point, or "
                    + "hexadecimal value (depending on context; see Boolean, "
                    + "Integer, Floating point, and Hexadecimal) that must be "
                    + "greater than or equal to the corresponding minimum value "
                    + "(see Minimum)"),

        PRIMITIVE("Primitive",
                  ".*",
                  "data type",
                  "A primitive data type as defined in the data type editor "
                      + "(for example, int16, float)"),

        PRIM_AND_STRUCT("Primitive & Structure",
                        ".*",
                        "data type",
                        "A primitive data type (see Primitive) or a prototype "
                            + "structure name"),

        RATE("Rate",
             "^\\+??\\s*(0*+1/)??(\\d*|\\d*\\.|\\d*\\.\\d+)",
             "rate",
             "Rate value; positive integer value (see Positive integer) or a "
                 + "positive integer followed by a '/' and another positive "
                 + "integer to denote rates faster than 1 Hz"),

        TEXT("Text",
             ".*",
             "text",
             "Text, including alphabetic, numeric, and special characters"),

        TEXT_MULTI("Text (multi-line)",
                   "(?s).*",
                   "text",
                   "Multi-line text, including alphabetic, numeric, special, and new line characters"),

        UNITS("Units",
              ".*",
              "text",
              "Data units; same constraints as for text (see Text)"),

        VARIABLE("Variable name",
                 "[a-zA-Z_][a-zA-Z0-9_]*",
                 "text",
                 "Variable name; same constraints as for an alphanumeric (see Alphanumeric)"),

        VARIABLE_PATH("Variable path",
                      "",
                      "variable path",
                      "Display a variable's full path"),

        BREAK("Break", "", "page format", "Line break"),
        SEPARATOR("Separator", "", "page format", "Line separator");

        private final String inputName;
        private final String inputMatch;
        private final String inputFormat;
        private final String inputDescription;

        /**********************************************************************
         * Input data types constructor
         * 
         * @param inputName
         *            input data type
         * 
         * @param inputMatch
         *            regular expression match for the input type
         * 
         * @param inputFormat
         *            field numerical type
         * 
         * @param inputDescription
         *            input type description
         *********************************************************************/
        InputDataType(String inputName,
                      String inputMatch,
                      String inputFormat,
                      String inputDescription)
        {
            this.inputName = inputName;
            this.inputMatch = inputMatch;
            this.inputFormat = inputFormat;
            this.inputDescription = inputDescription;
        }

        /**********************************************************************
         * Get the input data type
         * 
         * @return Input data type
         *********************************************************************/
        protected String getInputName()
        {
            return inputName;
        }

        /**********************************************************************
         * Get the input type matching regular expression
         * 
         * @return Input type matching regular expression
         *********************************************************************/
        protected String getInputMatch()
        {
            return inputMatch;
        }

        /**********************************************************************
         * Get the input type description
         * 
         * @return Input type description
         *********************************************************************/
        protected String getInputDescription()
        {
            return inputDescription;
        }

        /**********************************************************************
         * Reformat the input value for numeric types. This adds a leading zero
         * to floating point values if the first character is a decimal, and
         * removes '+' signs and unneeded leading zeroes from integer and
         * floating point values. Leading zeroes are preserved for hexadecimal
         * values
         * 
         * @param valueS
         *            value, represented as a string, to reformat
         * 
         * @return Input value reformatted based on its input type
         *********************************************************************/
        protected String formatInput(String valueS)
        {
            return formatInput(valueS, true);
        }

        /**********************************************************************
         * Reformat the input value for numeric types. This adds a leading zero
         * to floating point values if the first character is a decimal, and
         * removes '+' signs and unneeded leading zeroes from integer and
         * floating point values
         * 
         * @param valueS
         *            value, represented as a string, to reformat
         * 
         * @param preserveZeroes
         *            true to preserve leading zeroes in hexadecimal values;
         *            false to eliminate the extra zeroes (this is useful when
         *            comparing the text representation of two hexadecimal
         *            values)
         * 
         * @return Input value reformatted based on its input type
         *********************************************************************/
        protected String formatInput(String valueS, boolean preserveZeroes)
        {
            // Check that the value is not blank
            if (!valueS.isEmpty())
            {
                // Check if the value is an integer
                if (inputFormat.equals("integer"))
                {
                    // Format the string as an integer
                    valueS = Integer.valueOf(valueS).toString();
                }
                // Check if the value is a floating point
                else if (inputFormat.equals("float"))
                {
                    // Format the string as a floating point
                    valueS = Double.valueOf(valueS).toString();
                }
                // Check if the value is in hexadecimal
                else if (inputFormat.equals("hexadecimal"))
                {
                    // Remove leading hexadecimal identifier if present, then
                    // convert the value to an integer (base 16)
                    valueS = valueS.replaceFirst("^0x|^0X", "");
                    int value = Integer.valueOf(valueS, 16);

                    // Get the leading zeroes, if any
                    String leadZeroes = valueS.replaceFirst("(^0*)[a-fA-F0-9]*", "$1");

                    // Check if the value is zero
                    if (value == 0)
                    {
                        // Remove the first leading zero so it isn't
                        // duplicated, but retain any extra zeroes added by the
                        // user so these can be restored
                        leadZeroes = leadZeroes.substring(0, leadZeroes.length() - 1);
                    }

                    // Format the string as a hexadecimal, adding the
                    // hexadecimal identifier, if needed, and preserving any
                    // leading zeroes
                    valueS = String.format("0x%s%x",
                                           (preserveZeroes
                                                          ? leadZeroes
                                                          : ""),
                                           value);
                }
                // Check if the value is a boolean
                else if (inputFormat.equals("boolean"))
                {
                    // Format the string as a boolean
                    valueS = valueS.toLowerCase();
                }
                // Check if the values represents array index values
                else if (inputFormat.equals("array"))
                {
                    // Remove all spaces and replace any commas with a comma
                    // and space
                    valueS = valueS.replaceAll("\\s", "").replaceAll(",", ", ");
                }
            }

            return valueS;
        }

        /**********************************************************************
         * Get the input data type with the name that matches the one specified
         * 
         * @param name
         *            input data type name to match
         * 
         * @return Input data type with the name that matches the one
         *         specified; returns null if the input type doesn't exist
         *********************************************************************/
        protected static InputDataType getInputTypeByName(String name)
        {
            InputDataType inputType = null;

            // Step through each input data type
            for (InputDataType type : InputDataType.values())
            {
                // Check if the input type name matches the target name,
                // ignoring case
                if (type.inputName.equalsIgnoreCase(name))
                {
                    // Store the matching input type and stop searching
                    inputType = type;
                    break;
                }
            }

            return inputType;
        }

        /**********************************************************************
         * Get an array of all of the input data type names, excluding
         * separators and breaks
         * 
         * @param includeSpecialTypes
         *            true to include special input types (data type,
         *            enumeration, and variable path); false to exclude
         * 
         * @return Array of all of the input data type names
         *********************************************************************/
        protected static String[] getInputNames(boolean includeSpecialTypes)
        {
            // Create an array to hold the input type names
            List<String> inputNames = new ArrayList<String>();

            // Step through each input type
            for (InputDataType inputType : InputDataType.values())
            {
                // Check that this isn't a page format type and, if special
                // types are to be excluded, that this isn't one of those types
                if (!inputType.inputFormat.equals("page format")
                    && (includeSpecialTypes
                    || (!inputType.inputFormat.equals("data type")
                        && !inputType.inputFormat.equals("enumeration")
                        && !inputType.inputFormat.equals("variable path"))))
                {
                    // Store the input type name in the array
                    inputNames.add(inputType.inputName);
                }
            }

            // Sort the input type names alphabetically
            Collections.sort(inputNames);

            return inputNames.toArray(new String[0]);
        }

        /**********************************************************************
         * Get an array of all of the input data type descriptions, sorted
         * based on the alphabetically sorted input names, excluding separators
         * and breaks
         * 
         * @param includeSpecialTypes
         *            true to include special input types (data type and
         *            enumeration); false to exclude
         * 
         * @return Array of all of the input data type descriptions
         *********************************************************************/
        protected static String[] getDescriptions(boolean includeSpecialTypes)
        {
            // Get the list of input names, sorted alphabetically
            String[] inputNames = getInputNames(includeSpecialTypes);

            // Create an array to hold the input type descriptions
            String[] inputDescriptions = new String[inputNames.length];

            // Step through each input type name
            for (int nameIndex = 0; nameIndex < inputNames.length; nameIndex++)
            {
                // Step through each input type
                for (int index = 0; index < InputDataType.values().length; index++)
                {
                    // Check if the input type names match
                    if (inputNames[nameIndex].equals(InputDataType.values()[index].getInputName()))
                    {
                        // Store the description corresponding to this input
                        // name and stop searching
                        inputDescriptions[nameIndex] = InputDataType.values()[index].getInputDescription();
                        break;
                    }
                }
            }

            return inputDescriptions;
        }
    }

    /**************************************************************************
     * Data field applicability types
     *************************************************************************/
    protected static enum ApplicabilityType
    {
        ALL("All tables"),
        PARENT_ONLY("Parents only"),
        CHILD_ONLY("Children only");

        private final String applicabilityName;

        /**********************************************************************
         * Applicability types constructor
         * 
         * @param applicabilityName
         *            applicability type name
         *********************************************************************/
        ApplicabilityType(String applicabilityName)
        {
            this.applicabilityName = applicabilityName;
        }

        /**********************************************************************
         * Get the applicability type name
         * 
         * @return Applicability type name
         *********************************************************************/
        protected String getApplicabilityName()
        {
            return applicabilityName;
        }

        /**********************************************************************
         * Get an array of all of the applicability type names
         * 
         * @return Array of all of the applicability type names
         *********************************************************************/
        protected static String[] getApplicabilityNames()
        {
            // Create an array to hold the applicability type names
            List<String> applicabilityNames = new ArrayList<String>();

            // Step through each applicability type
            for (ApplicabilityType applicabilityType : ApplicabilityType.values())
            {
                // Store the applicability name in the array
                applicabilityNames.add(applicabilityType.applicabilityName);
            }

            return applicabilityNames.toArray(new String[0]);
        }
    }

    /**************************************************************************
     * Default table types and column names. The column names flagged as
     * protected are inherent to the specified table type and are not allowed
     * to be altered by the user
     *************************************************************************/
    protected static enum DefaultColumn
    {
        // Format: Table type, Column name, Column description (tool tip),
        // protected flag, required flag

        // Common columns
        PRIMARY_KEY("",
                    "_Key_",
                    "Primary key",
                    InputDataType.INT_POSITIVE,
                    true,
                    true,
                    true,
                    false,
                    false,
                    false),
        ROW_INDEX("",
                  "_Index_",
                  "Row index",
                  InputDataType.INT_POSITIVE,
                  true,
                  true,
                  true,
                  false,
                  false,
                  false),

        // Structure table type
        VARIABLE_NAME(TYPE_STRUCTURE,
                      "Variable Name",
                      "Parameter name",
                      InputDataType.VARIABLE,
                      true,
                      true,
                      true,
                      true,
                      true,
                      true),
        DESCRIPTION_STRUCT(TYPE_STRUCTURE,
                           COL_DESCRIPTION,
                           "Parameter description",
                           InputDataType.DESCRIPTION,
                           false,
                           false,
                           false,
                           true,
                           true,
                           true),
        UNITS(TYPE_STRUCTURE,
              COL_UNITS,
              "Parameter units",
              InputDataType.UNITS,
              false,
              false,
              false,
              true,
              true,
              true),
        DATA_TYPE(TYPE_STRUCTURE,
                  COL_DATA_TYPE,
                  "Parameter data type",
                  InputDataType.PRIM_AND_STRUCT,
                  true,
                  false,
                  true,
                  true,
                  true,
                  true),
        ARRAY_SIZE(TYPE_STRUCTURE,
                   "Array Size",
                   "Parameter array size",
                   InputDataType.ARRAY_INDEX,
                   true,
                   false,
                   false,
                   true,
                   true,
                   true),
        BIT_LENGTH(TYPE_STRUCTURE,
                   "Bit Length",
                   "Parameter number of bits (bit values only)",
                   InputDataType.BIT_LENGTH,
                   true,
                   false,
                   false,
                   false,
                   false,
                   true),
        ENUMERATION(TYPE_STRUCTURE,
                    COL_ENUMERATION,
                    "Enumerated parameters",
                    InputDataType.ENUMERATION,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false),
        RATE(TYPE_STRUCTURE,
             "Rate",
             "Downlink data rate, samples/second",
             InputDataType.RATE,
             true,
             false,
             false,
             false,
             true,
             false),

        // Command table type
        COMMAND_NAME(TYPE_COMMAND,
                     "Command Name",
                     "Command name",
                     InputDataType.COMMAND_NAME,
                     true,
                     true,
                     true,
                     false,
                     true,
                     true),
        COMMAND_CODE(TYPE_COMMAND,
                     "Command Code",
                     "Command code",
                     InputDataType.COMMAND_CODE,
                     true,
                     true,
                     true,
                     false,
                     true,
                     true),
        DESCRIPTION_CMD(TYPE_COMMAND,
                        COL_DESCRIPTION,
                        "Command description",
                        InputDataType.DESCRIPTION,
                        false,
                        false,
                        false,
                        false,
                        true,
                        true),
        ARG_NAME_1(TYPE_COMMAND,
                   COL_ARGUMENT + " 1 Name",
                   "Command argument 1 name",
                   InputDataType.ARGUMENT_NAME,
                   true,
                   false,
                   false,
                   false,
                   true,
                   false),
        ARG_DESCRIPTION_1(TYPE_COMMAND,
                          COL_ARGUMENT + " 1 " + COL_DESCRIPTION,
                          "Command argument 1 description",
                          InputDataType.TEXT,
                          false,
                          false,
                          false,
                          false,
                          true,
                          false),
        ARG_UNITS_1(TYPE_COMMAND,
                    COL_ARGUMENT + " 1 " + COL_UNITS,
                    "Command argument 1 units",
                    InputDataType.TEXT,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false),
        ARG_TYPE_1(TYPE_COMMAND,
                   COL_ARGUMENT + " 1 " + COL_DATA_TYPE,
                   "Command argument 1 data type",
                   InputDataType.PRIMITIVE,
                   true,
                   false,
                   false,
                   false,
                   true,
                   false),
        ARG_ARRAY_SIZE_1(TYPE_COMMAND,
                         COL_ARGUMENT + " 1 " + "Array Size",
                         "Command argument 1 array size",
                         InputDataType.ARRAY_INDEX,
                         false,
                         false,
                         false,
                         false,
                         false,
                         false),
        ARG_ENUMS_1(TYPE_COMMAND,
                    COL_ARGUMENT + " 1 " + COL_ENUMERATION,
                    "Command argument 1 enumeration",
                    InputDataType.ENUMERATION,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false),
        ARG_MIN_1(TYPE_COMMAND,
                  COL_ARGUMENT + " 1 " + COL_MINIMUM,
                  "Command argument 1 minimum value",
                  InputDataType.MINIMUM,
                  true,
                  false,
                  false,
                  false,
                  false,
                  false),
        ARG_MAX_1(TYPE_COMMAND,
                  COL_ARGUMENT + " 1 " + COL_MAXIMUM,
                  "Command argument 1 maximum value",
                  InputDataType.MAXIMUM,
                  true,
                  false,
                  false,
                  false,
                  false,
                  false);

        private final String tableType;
        private final String columnName;
        private final String description;
        private final InputDataType inputType;
        private final boolean isProtected;
        private final boolean isRowValueUnique;
        private final boolean isRequired;
        private final boolean isStructure;
        private final boolean isPointer;
        private final boolean isInputTypeUnique;

        /**********************************************************************
         * Default table types and column names constructor
         * 
         * @param tableType
         *            table type to which this column belongs
         * 
         * @param columnName
         *            table column name
         * 
         * @param description
         *            column description; this is used as the column's tool tip
         *            text
         * 
         * @param inputType
         *            column input data type
         * 
         * @param isProtected
         *            true if this column cannot be altered or removed by the
         *            user. Tables that include all of the protected columns
         *            are considered a table of this type, even if other
         *            columns are present or differ
         * 
         * @param isRowValueUnique
         *            true if this parameter must be unique in this column of
         *            this table. The user can change this flag in the type
         *            editor
         * 
         * @param isRequired
         *            true if this parameter requires a data value. This flag
         *            is used to determine if the cell in the table is
         *            highlighted when empty; it does not enforce entering a
         *            value. The user can change this flag in the type editor
         * 
         * @param isStructure
         *            true if the the column applies to structure data types.
         *            The user can change this flag in the type editor
         * 
         * @param isPointer
         *            true if the the column applies to pointer data types. The
         *            user can change this flag in the type editor
         * 
         * @param isInputTypeUnique
         *            true if this parameter's input type must be unique in its
         *            table type
         *********************************************************************/
        DefaultColumn(String tableType,
                      String columnName,
                      String description,
                      InputDataType inputType,
                      boolean isProtected,
                      boolean isRowValueUnique,
                      boolean isRequired,
                      boolean isStructure,
                      boolean isPointer,
                      boolean isInputTypeUnique)
        {
            this.tableType = tableType;
            this.columnName = columnName;
            this.description = description;
            this.inputType = inputType;
            this.isProtected = isProtected;
            this.isRowValueUnique = isRowValueUnique;
            this.isRequired = isRequired;
            this.isStructure = isStructure;
            this.isPointer = isPointer;
            this.isInputTypeUnique = isInputTypeUnique;
        }

        /**********************************************************************
         * Get the default column name
         * 
         * @return Default column name
         *********************************************************************/
        protected String getName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the default column database name. The conversion sets the name
         * to all lower case text and replaces any spaces with underlines
         * 
         * @return Default column database name
         *********************************************************************/
        protected String getDbName()
        {
            return columnName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        }

        /**********************************************************************
         * Get the default column description
         * 
         * @return Default column description
         *********************************************************************/
        protected String getDescription()
        {
            return description;
        }

        /**********************************************************************
         * Get the column protected status
         * 
         * @return true if the column is protected
         *********************************************************************/
        protected boolean isProtected()
        {
            return isProtected;
        }

        /**********************************************************************
         * Get the row value unique status
         * 
         * @return true if the row value must be unique in this column
         *********************************************************************/
        protected boolean isRowValueUnique()
        {
            return isRowValueUnique;
        }

        /**********************************************************************
         * Get the column required status
         * 
         * @return true if the column is required
         *********************************************************************/
        protected boolean isRequired()
        {
            return isRequired;
        }

        /**********************************************************************
         * Get the structure data type allowed status
         * 
         * @return true if the column applies to structure data types
         *********************************************************************/
        protected boolean isStructure()
        {
            return isStructure;
        }

        /**********************************************************************
         * Get the pointer data type allowed status
         * 
         * @return true if the column applies to pointer data types
         *********************************************************************/
        protected boolean isPointer()
        {
            return isPointer;
        }

        /**********************************************************************
         * Get the default column input type
         * 
         * @return Default column input type
         *********************************************************************/
        protected InputDataType getInputType()
        {
            return inputType;
        }

        /**********************************************************************
         * Get the table type
         * 
         * @return Table type
         *********************************************************************/
        protected String getTableType()
        {
            return tableType;
        }

        /**************************************************************************
         * Convert the visible column name to its database equivalent by
         * replacing all characters that are invalid in a database column name
         * with underscores. Specific input types use predefined names in place
         * of the conversion name
         * 
         * @param columnName
         *            column name (as seen by the user)
         * 
         * @param inputType
         *            column input type (InputDataType)
         * 
         * @return Database column name corresponding to the visible column
         *         name
         *************************************************************************/
        protected static String convertVisibleToDatabase(String columnName,
                                                         InputDataType inputType)
        {
            String dbColumnName = null;

            switch (inputType)
            {
                case VARIABLE:
                    // Use the default database name for the variable name
                    // column
                    dbColumnName = DefaultColumn.VARIABLE_NAME.getDbName();
                    break;

                case ARRAY_INDEX:
                    // Use the default database name for the array size column
                    dbColumnName = DefaultColumn.ARRAY_SIZE.getDbName();
                    break;

                case BIT_LENGTH:
                    // Use the default database name for the bit length column
                    dbColumnName = DefaultColumn.BIT_LENGTH.getDbName();
                    break;

                case PRIM_AND_STRUCT:
                    // Use the default database name for the data type column
                    dbColumnName = DefaultColumn.DATA_TYPE.getDbName();
                    break;

                default:
                    // Replace any characters that aren't allowed in a database
                    // column name with underscores
                    dbColumnName = columnName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
            }

            return dbColumnName;
        }

        /**********************************************************************
         * Get the array of default table types
         * 
         * @return Array containing the default table types
         *********************************************************************/
        protected static String[] getTableTypes()
        {
            List<String> tableTypes = new ArrayList<String>();

            // Step through the default columns
            for (DefaultColumn defCol : DefaultColumn.values())
            {
                // Check if the table type is not already in the list and that
                // it's not a common column
                if (!tableTypes.contains(defCol.tableType)
                    && !defCol.tableType.isEmpty())
                {
                    // Add the table type to the list
                    tableTypes.add(defCol.tableType);
                }
            }

            return tableTypes.toArray(new String[0]);
        }

        /**********************************************************************
         * Get the number of protected columns for the specified table type
         * 
         * @param type
         *            table type
         * 
         * @return Number of the protected columns for the specified table type
         *********************************************************************/
        protected static int getProtectedColumnCount(String type)
        {
            int numProtectedColumns = 0;

            // Step through the default columns
            for (DefaultColumn defCol : DefaultColumn.values())
            {
                // Check if the column is protected and that the column's table
                // type matches the specified type
                if (defCol.isProtected && type.equals(defCol.tableType))
                {
                    // Increment the protected column counter
                    numProtectedColumns++;
                }
            }

            return numProtectedColumns;
        }

        /**********************************************************************
         * Check if the supplied table type and column name match one of the
         * default table type & column name pairs
         * 
         * @param compareTableType
         *            table type
         * 
         * @param compareColumnName
         *            column name
         * 
         * @return true if the supplied table type and column name match a
         *         protected table type and column name combination, and if
         *         this pair is flagged as protected
         *********************************************************************/
        protected static boolean isProtectedColumn(String compareTableType,
                                                   String compareColumnName)
        {
            boolean isColumnProtected = false;

            // Step through the default columns
            for (DefaultColumn defCol : DefaultColumn.values())
            {
                // Check if the table type and column name matches the one in
                // the table
                if (defCol.tableType.equals(compareTableType)
                    && defCol.columnName.equals(compareColumnName))
                {
                    // Set the flag based on this parameter's protected status
                    // and stop searching
                    isColumnProtected = defCol.isProtected;
                    break;
                }
            }

            return isColumnProtected;
        }

        /**********************************************************************
         * Check if the supplied table type and input data type match one of
         * the default table type & input type pairs
         * 
         * @param compareTableType
         *            table type
         * 
         * @param compareColumnName
         *            column name
         * 
         * @return true if the supplied table type and input data type match a
         *         protected table type and input type combination, and if this
         *         pair is flagged as protected
         *********************************************************************/
        protected static boolean isInputTypeUnique(String compareTableType,
                                                   String compareInputType)
        {
            boolean isUniqueInputType = false;

            // Step through the default columns
            for (DefaultColumn defCol : DefaultColumn.values())
            {
                // Check if the table type and input type matches the one in
                // the table
                if (defCol.tableType.equals(compareTableType)
                    && defCol.inputType.inputName.equals(compareInputType))
                {
                    // Set the flag based on this parameter's input type status
                    // and stop searching
                    isUniqueInputType = defCol.isInputTypeUnique;
                    break;
                }
            }

            return isUniqueInputType;
        }

        /**********************************************************************
         * Get the default column definitions for use in building the table
         * definitions table in the database
         * 
         * @return Default column definitions statement
         *********************************************************************/
        protected static String getColumnDefinitions()
        {
            String columnDefn = "";

            // Get the array of default table types
            String[] defTypes = getTableTypes();

            // Step through each table type
            for (String type : defTypes)
            {
                int index = 0;

                // Step through the default columns
                for (DefaultColumn defCol : DefaultColumn.values())
                {
                    // Check if the table type matches the current column's
                    // type or if it's a column common to all tables
                    if (type.equals(defCol.tableType)
                        || defCol.tableType.isEmpty())
                    {
                        String typeDescription = defCol.description;

                        // Check if this is the primary key column. The
                        // description for this column is used to hold the
                        // table type's description
                        if (defCol.columnName.equals(PRIMARY_KEY.columnName))
                        {
                            // Check if this is a structure table type
                            if (type.equals(TYPE_STRUCTURE))
                            {
                                // Set the description of the structure table
                                // type
                                typeDescription = "Telemetry and data structure table definition";
                            }
                            // Check if this is a command table type
                            else if (type.equals(TYPE_COMMAND))
                            {
                                // Set the description of the command table
                                // type
                                typeDescription = "Command table definition";
                            }
                            // Not a structure or command table type
                            else
                            {
                                // Use the generic table type description
                                typeDescription = "User-defined table definition";
                            }
                        }

                        // Add the column definition
                        columnDefn += "('"
                                      + type
                                      + "', "
                                      + index
                                      + ", '"
                                      + defCol.getDbName()
                                      + "', '"
                                      + defCol.columnName
                                      + "', '"
                                      + typeDescription
                                      + "', '"
                                      + defCol.getInputType().inputName
                                      + "', "
                                      + defCol.isRowValueUnique
                                      + ", "
                                      + defCol.isRequired
                                      + ", "
                                      + defCol.isStructure
                                      + ", "
                                      + defCol.isPointer
                                      + "), ";

                        index++;
                    }
                }
            }

            // Remove the ending comma
            return CcddUtilities.removeTrailer(columnDefn, ", ");
        }

        /**********************************************************************
         * Get the column database data type based on the column index
         * 
         * @column column index
         * 
         * @return Column database data type
         *********************************************************************/
        protected static String getColumnDbType(int column)
        {
            String columnType;

            // Check if this is the primary key column
            if (column == DefaultColumn.PRIMARY_KEY.ordinal())
            {
                columnType = "serial PRIMARY KEY";
            }
            // Check if this is the row index column
            else if (column == DefaultColumn.ROW_INDEX.ordinal())
            {
                columnType = "integer";
            }
            // Columns other than the primary key and row index
            else
            {
                columnType = "text";
            }

            return columnType;
        }
    }

    /**************************************************************************
     * Database internal table definitions
     *************************************************************************/
    protected static enum InternalTable
    {
        // Application scheduler
        APP_SCHEDULER("app_scheduler",
                      new String[][] { {AppSchedulerColumn.TIME_SLOT.columnName,
                                        AppSchedulerColumn.TIME_SLOT.dataType},
                                      {AppSchedulerColumn.APP_INFO.columnName,
                                       AppSchedulerColumn.APP_INFO.dataType}},
                      "WITH OIDS",
                      "COMMENT ON TABLE "
                          + INTERNAL_TABLE_PREFIX
                          + "app_scheduler IS '1,10,10,128'"),

        // Script & data table combinations
        ASSOCIATIONS("associations",
                     new String[][] { {AssociationsColumn.DESCRIPTION.columnName,
                                       AssociationsColumn.DESCRIPTION.dataType},
                                     {AssociationsColumn.SCRIPT_FILE.columnName,
                                      AssociationsColumn.SCRIPT_FILE.dataType},
                                     {AssociationsColumn.MEMBERS.columnName,
                                      AssociationsColumn.MEMBERS.dataType}},
                     "WITH OIDS",
                     ""),

        // Data types
        DATA_TYPES("data_types",
                   new String[][] { {DataTypesColumn.USER_NAME.columnName,
                                     DataTypesColumn.USER_NAME.dataType},
                                   {DataTypesColumn.C_NAME.columnName,
                                    DataTypesColumn.C_NAME.dataType},
                                   {DataTypesColumn.SIZE.columnName,
                                    DataTypesColumn.SIZE.dataType},
                                   {DataTypesColumn.BASE_TYPE.columnName,
                                    DataTypesColumn.BASE_TYPE.dataType}},
                   "WITH OIDS",

                   // Create default data type definitions
                   "INSERT INTO "
                       + INTERNAL_TABLE_PREFIX
                       + "data_types VALUES "
                       + DefaultPrimitiveTypeInfo.getDataTypeDefinitions()),

        // Table data fields
        FIELDS("fields",
               new String[][] { {FieldsColumn.OWNER_NAME.columnName,
                                 FieldsColumn.OWNER_NAME.dataType},
                               {FieldsColumn.FIELD_NAME.columnName,
                                FieldsColumn.FIELD_NAME.dataType},
                               {FieldsColumn.FIELD_DESC.columnName,
                                FieldsColumn.FIELD_DESC.dataType},
                               {FieldsColumn.FIELD_SIZE.columnName,
                                FieldsColumn.FIELD_SIZE.dataType},
                               {FieldsColumn.FIELD_TYPE.columnName,
                                FieldsColumn.FIELD_TYPE.dataType},
                               {FieldsColumn.FIELD_REQUIRED.columnName,
                                FieldsColumn.FIELD_REQUIRED.dataType},
                               {FieldsColumn.FIELD_APPLICABILITY.columnName,
                                FieldsColumn.FIELD_APPLICABILITY.dataType},
                               {FieldsColumn.FIELD_VALUE.columnName,
                                FieldsColumn.FIELD_VALUE.dataType}},
               "WITH OIDS",
               ""),

        // Data table groupings
        GROUPS("groups",
               new String[][] { {GroupsColumn.GROUP_NAME.columnName,
                                 GroupsColumn.GROUP_NAME.dataType},
                               {GroupsColumn.MEMBERS.columnName,
                                GroupsColumn.MEMBERS.dataType}},
               "WITH OIDS",
               ""),

        // Variable links
        LINKS("links",
              new String[][] { {LinksColumn.RATE_NAME.columnName,
                                LinksColumn.RATE_NAME.dataType},
                              {LinksColumn.LINK_NAME.columnName,
                               LinksColumn.LINK_NAME.dataType},
                              {LinksColumn.MEMBER.columnName,
                               LinksColumn.MEMBER.dataType}},
              "WITH OIDS",
              ""),

        // Macro values
        MACROS("macros",
               new String[][] { {MacrosColumn.MACRO_NAME.columnName,
                                 MacrosColumn.MACRO_NAME.dataType},
                               {MacrosColumn.VALUE.columnName,
                                MacrosColumn.VALUE.dataType}},
               "WITH OIDS",
               ""),

        // Table column orders
        ORDERS("orders",
               new String[][] { {OrdersColumn.USER_NAME.columnName,
                                 OrdersColumn.USER_NAME.dataType},
                               {OrdersColumn.TABLE_PATH.columnName,
                                OrdersColumn.TABLE_PATH.dataType},
                               {OrdersColumn.COLUMN_ORDER.columnName,
                                OrdersColumn.COLUMN_ORDER.dataType}},
               "WITH OIDS",
               ""),

        // Reserved message IDs
        RESERVED_MSG_IDS("reserved_msg_ids",
                         new String[][] { {ReservedMsgIDsColumn.MSG_ID.columnName,
                                           ReservedMsgIDsColumn.MSG_ID.dataType},
                                         {ReservedMsgIDsColumn.DESCRIPTION.columnName,
                                          ReservedMsgIDsColumn.DESCRIPTION.dataType}},
                         "WITH OIDS",
                         "INSERT INTO "
                             + INTERNAL_TABLE_PREFIX
                             + "reserved_msg_ids ("
                             + ReservedMsgIDsColumn.MSG_ID.columnName
                             + ", "
                             + ReservedMsgIDsColumn.DESCRIPTION.columnName
                             + ") VALUES ('0x0800 - 0x08FF', 'cFE telemetry IDs'), "
                             + "('0x1800 - 0x18FF', 'cFE command IDs')"),

        // Script files
        SCRIPT("script_",
               new String[][] { {ScriptColumn.LINE_NUM.columnName,
                                 ScriptColumn.LINE_NUM.dataType},
                               {ScriptColumn.LINE_TEXT.columnName,
                                ScriptColumn.LINE_TEXT.dataType}},
               "WITH OIDS",
               ""),

        // Data table types
        TABLE_TYPES("table_types",
                    new String[][] { {TableTypesColumn.TYPE_NAME.columnName,
                                      TableTypesColumn.TYPE_NAME.dataType},
                                    {TableTypesColumn.INDEX.columnName,
                                     TableTypesColumn.INDEX.dataType},
                                    {TableTypesColumn.COLUMN_NAME_DB.columnName,
                                     TableTypesColumn.COLUMN_NAME_DB.dataType},
                                    {TableTypesColumn.COLUMN_NAME_VISIBLE.columnName,
                                     TableTypesColumn.COLUMN_NAME_VISIBLE.dataType},
                                    {TableTypesColumn.COLUMN_DESCRIPTION.columnName,
                                     TableTypesColumn.COLUMN_DESCRIPTION.dataType},
                                    {TableTypesColumn.INPUT_TYPE.columnName,
                                     TableTypesColumn.INPUT_TYPE.dataType},
                                    {TableTypesColumn.ROW_VALUE_UNIQUE.columnName,
                                     TableTypesColumn.ROW_VALUE_UNIQUE.dataType},
                                    {TableTypesColumn.COLUMN_REQUIRED.columnName,
                                     TableTypesColumn.COLUMN_REQUIRED.dataType},
                                    {TableTypesColumn.STRUCTURE_ALLOWED.columnName,
                                     TableTypesColumn.STRUCTURE_ALLOWED.dataType},
                                    {TableTypesColumn.POINTER_ALLOWED.columnName,
                                     TableTypesColumn.POINTER_ALLOWED.dataType}},
                    "WITH OIDS",

                    // Enforce that (type, index) must be unique
                    "CREATE UNIQUE INDEX "
                        + INTERNAL_TABLE_PREFIX
                        + "table_types_idx ON "
                        + INTERNAL_TABLE_PREFIX
                        + "table_types (type, index); "

                        // Create default table definition for the telemetry
                        // and command table types
                        + "INSERT INTO "
                        + INTERNAL_TABLE_PREFIX
                        + "table_types VALUES "
                        + DefaultColumn.getColumnDefinitions()),

        // Telemetry scheduler
        TLM_SCHEDULER("tlm_scheduler",
                      new String[][] { {TlmSchedulerColumn.RATE_NAME.columnName,
                                        TlmSchedulerColumn.RATE_NAME.dataType},
                                      {TlmSchedulerColumn.MESSAGE_NAME.columnName,
                                       TlmSchedulerColumn.MESSAGE_NAME.dataType},
                                      {TlmSchedulerColumn.MESSAGE_ID.columnName,
                                       TlmSchedulerColumn.MESSAGE_ID.dataType},
                                      {TlmSchedulerColumn.MEMBER.columnName,
                                       TlmSchedulerColumn.MEMBER.dataType}},
                      "WITH OIDS",
                      "COMMENT ON TABLE "
                          + INTERNAL_TABLE_PREFIX
                          + "tlm_scheduler IS '1,1,false,\""
                          + DefaultColumn.RATE.getName()
                          + "\",\""
                          + DefaultColumn.RATE.getName()
                          + "\",1,56000'"),

        // Data table values for non-prototype tables
        VALUES("values",
               new String[][] { {ValuesColumn.TABLE_PATH.columnName,
                                 ValuesColumn.TABLE_PATH.dataType},
                               {ValuesColumn.COLUMN_NAME.columnName,
                                ValuesColumn.COLUMN_NAME.dataType},
                               {ValuesColumn.VALUE.columnName,
                                ValuesColumn.VALUE.dataType}},
               "",
               "");

        /**********************************************************************
         * Application scheduler table columns
         *********************************************************************/
        protected static enum AppSchedulerColumn
        {
            TIME_SLOT("time_slot", "text"),
            APP_INFO("application_info", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Scheduler table columns constructor
             * 
             * @param columnName
             *            scheduler table column name
             * 
             * @param dataType
             *            scheduler table column data type
             *****************************************************************/
            AppSchedulerColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the scheduler table column name
             * 
             * @return Scheduler table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Script associations table columns
         *********************************************************************/
        protected static enum AssociationsColumn
        {
            DESCRIPTION("description", "text"),
            SCRIPT_FILE("script_file", "text"),
            MEMBERS("member_tables", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Scripts associations table columns constructor
             * 
             * @param columnName
             *            scripts table column name
             * 
             * @param dataType
             *            scripts table column data type
             *****************************************************************/
            AssociationsColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the scripts table column name
             * 
             * @return Scripts table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Data types table columns
         *********************************************************************/
        protected static enum DataTypesColumn
        {
            USER_NAME("user_name", "text"),
            C_NAME("c_name", "text"),
            SIZE("size", "integer"),
            BASE_TYPE("base_type", "text"),
            OID("index", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Macro values table columns constructor
             * 
             * @param columnName
             *            data types table column name
             * 
             * @param dataType
             *            data types table column data type
             *****************************************************************/
            DataTypesColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the data types table column name
             * 
             * @return Data types table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Data fields table columns
         *********************************************************************/
        protected static enum FieldsColumn
        {
            OWNER_NAME("owner_name", "text"),
            FIELD_NAME("field_name", "text"),
            FIELD_DESC("field_description", "text"),
            FIELD_SIZE("field_size", "text"),
            FIELD_TYPE("field_type", "text"),
            FIELD_REQUIRED("field_required", "text"),
            FIELD_APPLICABILITY("field_applicability", "text"),
            FIELD_VALUE("field_value", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Data fields table columns constructor
             * 
             * @param columnName
             *            data fields table column name
             * 
             * @param dataType
             *            data fields table column data type
             *****************************************************************/
            FieldsColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the data fields table column name
             * 
             * @return Data fields table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Groups table columns
         *********************************************************************/
        protected static enum GroupsColumn
        {
            GROUP_NAME("group_name", "text"),
            MEMBERS("member_tables", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Groups table columns constructor
             * 
             * @param columnName
             *            groups table column name
             * 
             * @param dataType
             *            groups table column data type
             *****************************************************************/
            GroupsColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the groups table column name
             * 
             * @return Groups table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Links table columns
         *********************************************************************/
        protected static enum LinksColumn
        {
            RATE_NAME("rate_name", "text"),
            LINK_NAME("link_name", "text"),
            MEMBER("member_variables", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Links table columns constructor
             * 
             * @param columnName
             *            links table column name
             * 
             * @param dataType
             *            links table column data type
             *****************************************************************/
            LinksColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the links table column name
             * 
             * @return Links table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Macro values table columns
         *********************************************************************/
        protected static enum MacrosColumn
        {
            MACRO_NAME("macro_name", "text"),
            VALUE("value", "text"),
            OID("index", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Macro values table columns constructor
             * 
             * @param columnName
             *            macros table column name
             * 
             * @param dataType
             *            macros table column data type
             *****************************************************************/
            MacrosColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the macros table column name
             * 
             * @return Macros table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Column order table columns
         *********************************************************************/
        protected static enum OrdersColumn
        {
            USER_NAME("user_name", "text"),
            TABLE_PATH("table_path", "text"),
            COLUMN_ORDER("column_order", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Column order table columns constructor
             * 
             * @param columnName
             *            orders table column name
             * 
             * @param dataType
             *            orders table column data type
             *****************************************************************/
            OrdersColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the orders table column name
             * 
             * @return Orders table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Reserved message ID table columns
         *********************************************************************/
        protected static enum ReservedMsgIDsColumn
        {
            MSG_ID("msg_id", "text"),
            DESCRIPTION("description", "text"),
            OID("index", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Reserved message IDs table columns constructor
             * 
             * @param columnName
             *            reserved message IDs table column name
             * 
             * @param dataType
             *            reserved message IDs table column data type
             *****************************************************************/
            ReservedMsgIDsColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the reserved message IDs table column name
             * 
             * @return Reserved message IDs table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Script table columns
         *********************************************************************/
        protected static enum ScriptColumn
        {
            LINE_NUM("line_number", "text"),
            LINE_TEXT("line_text", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Script table columns constructor
             * 
             * @param columnName
             *            scripts table column name
             * 
             * @param dataType
             *            scripts table column data type
             *****************************************************************/
            ScriptColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }
        }

        /**********************************************************************
         * Table type definitions table columns
         *********************************************************************/
        protected static enum TableTypesColumn
        {
            TYPE_NAME("type", "text"),
            INDEX("index", "integer CHECK (index >= 0)"),
            COLUMN_NAME_DB("column_name", "text"),
            COLUMN_NAME_VISIBLE("column_name_user", "text"),
            COLUMN_DESCRIPTION("column_description", "text"),
            INPUT_TYPE("input_type", "text"),
            ROW_VALUE_UNIQUE("row_value_unique", "boolean"),
            COLUMN_REQUIRED("column_required", "boolean"),
            STRUCTURE_ALLOWED("allow_structure", "boolean"),
            POINTER_ALLOWED("allow_pointer", "boolean");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Table type definitions table columns constructor
             * 
             * @param columnName
             *            table types table column name
             * 
             * @param dataType
             *            table types table column data type
             *****************************************************************/
            TableTypesColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the table type column name
             * 
             * @return Table type column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Telemetry scheduler table columns
         *********************************************************************/
        protected static enum TlmSchedulerColumn
        {
            RATE_NAME("rate_name", "text"),
            MESSAGE_NAME("message_name", "text"),
            MESSAGE_ID("message_id", "text"),
            MEMBER("member_variable", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Messages table columns constructor
             * 
             * @param columnName
             *            messages table column name
             * 
             * @param dataType
             *            messages table column data type
             *****************************************************************/
            TlmSchedulerColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the messages table column name
             * 
             * @return Messages table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        /**********************************************************************
         * Custom values table columns
         *********************************************************************/
        protected static enum ValuesColumn
        {
            TABLE_PATH("table_path", "text"),
            COLUMN_NAME("column_name", "text"),
            VALUE("value", "text");

            private final String columnName;
            private final String dataType;

            /******************************************************************
             * Custom values table columns constructor
             * 
             * @param columnName
             *            custom values table column name
             * 
             * @param dataType
             *            custom values table column data type
             *****************************************************************/
            ValuesColumn(String columnName, String dataType)
            {
                this.columnName = columnName;
                this.dataType = dataType;
            }

            /******************************************************************
             * Get the custom values table column name
             * 
             * @return Custom values table column name
             *****************************************************************/
            protected String getColumnName()
            {
                return columnName;
            }
        }

        private final String tableName;
        private final String[][] columns;
        private final String initCommand;
        private String command;

        /**********************************************************************
         * Database internal table names constructor
         * 
         * @param tableName
         *            internal table name
         * 
         * @param columns
         *            array of internal table column names and data types
         * 
         * @param createCommand
         *            any special database command(s) required when creating
         *            this internal table
         * 
         * @param initCommand
         *            table initialization command(s) when creating this
         *            internal table, if any
         *********************************************************************/
        InternalTable(String tableName,
                      String[][] columns,
                      String createCommand,
                      String initCommand)
        {
            // Prepend the character(s) that flag this as a non-data table to
            // create the table name
            this.tableName = INTERNAL_TABLE_PREFIX + tableName;

            this.columns = columns;
            this.initCommand = initCommand;

            // Create the command substring for building the columns for the
            // internal table
            command = "(";

            // Step through each column definition
            for (String[] column : columns)
            {
                // Build the command to create the column
                command += column[0] + " " + column[1] + ", ";
            }

            // Replace the trailing comma with a closing parenthesis
            command = CcddUtilities.removeTrailer(command, ", ")
                      + ") "
                      + createCommand
                      + ";";
        }

        /**********************************************************************
         * Get the internal table name
         * 
         * @return Internal table name
         *********************************************************************/
        protected String getTableName()
        {
            return tableName;
        }

        /**********************************************************************
         * Get the internal table name
         * 
         * @param scriptComment
         *            script file comment from which to extract the script's
         *            original name (only applicable to script file tables)
         * 
         * @return Internal table name
         *********************************************************************/
        protected String getTableName(String scriptComment)
        {
            String fullName = tableName;

            // Check if this is a script file
            if (this == SCRIPT)
            {
                // Append the script file name, which is the first element of
                // the table comment, converted to use as a database table
                // name, to the internal table name
                fullName += scriptComment.split(",", 2)[0].toLowerCase().replaceAll("[ .]", "_");
            }

            return fullName;
        }

        /**********************************************************************
         * Get the number of internal table columns
         * 
         * @return Number of internal table columns
         *********************************************************************/
        protected int getNumColumns()
        {
            return columns.length;
        }

        /**********************************************************************
         * Get the internal table column name by index
         * 
         * @return Internal table column name for the specified index; null if
         *         the index is invalid
         *********************************************************************/
        protected String getColumnName(int index)
        {
            String columnName = null;

            // Check if the index is valid
            if (index < columns.length)
            {
                // Store the column name
                columnName = columns[index][0];
            }

            return columnName;
        }

        /**********************************************************************
         * Get the internal table column data type by index
         * 
         * @return Internal table column data type for the specified index;
         *         null if the index is invalid
         *********************************************************************/
        protected String getColumnType(int index)
        {
            String columnType = null;

            // Check if the index is valid
            if (index < columns.length)
            {
                // Store the column type
                columnType = columns[index][1];
            }

            return columnType;
        }

        /**********************************************************************
         * Get the command substring the defines the data fields table columns
         * and any special command(s) required to build this internal table
         * 
         * @param includeInitCmd
         *            true to include the table initialization command(s);
         *            false to only include the column definition and create
         *            commands
         * 
         * @return Data fields table columns command substring and special
         *         command(s)
         *********************************************************************/
        protected String getColumnCommand(boolean includeInitCmd)
        {
            return command
                   + (includeInitCmd
                      && !initCommand.isEmpty()
                                               ? " " + initCommand + "; "
                                               : " ");
        }
    }

    /**************************************************************************
     * Table type editor column information
     *************************************************************************/
    protected static enum TableTypeEditorColumnInfo
    {
        INDEX("Column Index", "Column index", "", true),
        NAME("Column Name", "Table column name", "", true),
        DESCRIPTION("Description", "Table column description", "", false),
        INPUT_TYPE("Input Type",
                   "Input type that can be entered in this column",
                   InputDataType.TEXT.getInputName(),
                   true),
        UNIQUE("Unique",
               "Select if each row value in this column must be unique",
               false,
               false),
        REQUIRED("Required",
                 "Select if a value is required in the column",
                 false,
                 false),
        STRUCTURE_ALLOWED("<html><center><p style=\"font-size:8px\">Enable if<br>Structure",
                          "Select if this column is allowed with structure data types",
                          false,
                          false),
        POINTER_ALLOWED("<html><center><p style=\"font-size:8px\">Enable if<br>Pointer",
                        "Select if this column is allowed with pointer data types",
                        false,
                        false);

        private final String columnName;
        private final String toolTip;
        private final Object initialValue;
        private final boolean isRequired;

        /**********************************************************************
         * Table type editor column information constructor
         * 
         * @param columnName
         *            text to display for the type editor column name
         * 
         * @param toolTip
         *            tool tip text to display for the column
         * 
         * @param initialValue
         *            initial column value
         * 
         * @param isRequired
         *            true if a value is required in this column
         *********************************************************************/
        TableTypeEditorColumnInfo(String columnName,
                                  String toolTip,
                                  Object initialValue,
                                  boolean isRequired)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
            this.initialValue = initialValue;
            this.isRequired = isRequired;
        }

        /**********************************************************************
         * Get the type editor column required flag
         * 
         * @return Type editor column required flag
         *********************************************************************/
        protected boolean isRequired()
        {
            return isRequired;
        }

        /**********************************************************************
         * Get the type editor column name
         * 
         * @return Type editor column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the type editor column names
         * 
         * @return Array containing the type editor column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[TableTypeEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (TableTypeEditorColumnInfo type : TableTypeEditorColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the type editor column tool tips
         * 
         * @return Array containing the type editor column tool tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[TableTypeEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (TableTypeEditorColumnInfo type : TableTypeEditorColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }

        /**********************************************************************
         * Get a row with initialized values for the type editor
         * 
         * @return Array containing initial values for a row in the type editor
         *********************************************************************/
        protected static Object[] getEmptyRow()
        {
            Object[] emptyRow = new Object[TableTypeEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (TableTypeEditorColumnInfo type : TableTypeEditorColumnInfo.values())
            {
                // Initialize the column value
                emptyRow[index] = type.initialValue;
                index++;
            }

            return emptyRow;
        }
    }

    /**************************************************************************
     * Scheduler table columns
     *************************************************************************/
    protected static enum SchedulerColumn
    {
        NAME("<html><center>Message<br>Name", "Time Slot", "Column A"),
        SIZE("<html><center>Free<br>Bytes", "Time (msec)", "Column B"),
        ID("<html><center>Common<br>ID", "", "Column C");

        private final String tlmColumn;
        private final String appColumn;
        private final String otherColumn;

        /**********************************************************************
         * Scheduler table columns constructor
         * 
         * @param tlmColumn
         *            telemetry scheduler column name
         * 
         * @param appColumn
         *            application scheduler column name
         * 
         * @param otherColumn
         *            unknown scheduler type column name
         *********************************************************************/
        SchedulerColumn(String tlmColumn, String appColumn, String otherColumn)
        {
            this.tlmColumn = tlmColumn;
            this.appColumn = appColumn;
            this.otherColumn = otherColumn;
        }

        /**********************************************************************
         * Get the scheduler column name based on the scheduler type
         * 
         * @param schType
         *            scheduler type
         * 
         * @return Scheduler column name for the specified scheduler type
         *********************************************************************/
        protected String getColumn(SchedulerType schType)
        {
            String columnName;

            switch (schType)
            {
                case TELEMETRY_SCHEDULER:
                    columnName = tlmColumn;
                    break;

                case APPLICATION_SCHEDULER:
                    columnName = appColumn;
                    break;

                default:
                    columnName = otherColumn;
                    break;
            };

            return columnName;
        }
    }

    /**************************************************************************
     * Default application data fields
     *************************************************************************/
    protected static enum DefaultApplicationField
    {
        SCHEDULE_RATE("Schedule Rate",
                      "Application execution rate, cycles/second",
                      7,
                      InputDataType.INT_POSITIVE,
                      true,
                      ApplicabilityType.ALL,
                      "1"),
        EXECUTION_TIME("Execution Time",
                       "Estimated time for this application to execute",
                       7,
                       InputDataType.INT_POSITIVE,
                       true,
                       ApplicabilityType.ALL,
                       "1"),
        PRIORITY("Execution Priority",
                 "Application execution priority",
                 3,
                 InputDataType.INT_POSITIVE,
                 true,
                 ApplicabilityType.ALL,
                 "1"),
        MESSAGE_RATE("Message rate",
                     "Application message rate, samples/second",
                     7,
                     InputDataType.INT_POSITIVE,
                     true,
                     ApplicabilityType.ALL,
                     "1"),
        WAKE_UP_NAME("Wake-Up Name",
                     "Application wake-up name",
                     10,
                     InputDataType.ALPHANUMERIC,
                     true,
                     ApplicabilityType.ALL,
                     ""),
        WAKE_UP_ID("Wake-Up ID",
                   "Application wake-up ID",
                   7,
                   InputDataType.HEXADECIMAL,
                   true,
                   ApplicabilityType.ALL,
                   "0x0"),
        HK_SEND_RATE("HK_Send Rate",
                     "Application housekeeping send rate",
                     7,
                     InputDataType.INT_POSITIVE,
                     true,
                     ApplicabilityType.ALL,
                     "1"),
        HK_WAKE_UP_NAME("HK Wake-Up Name",
                        "Application housekeeping wake-up name",
                        10,
                        InputDataType.ALPHANUMERIC,
                        true,
                        ApplicabilityType.ALL,
                        ""),
        HK_WAKE_UP_ID("HK Wake-Up ID",
                      "Application housekeeping wake-up ID",
                      7,
                      InputDataType.HEXADECIMAL,
                      true,
                      ApplicabilityType.ALL,
                      "0x0"),
        SCH_GROUP("SCH Group",
                  "Application Schedule group",
                  10,
                  InputDataType.ALPHANUMERIC,
                  true,
                  ApplicabilityType.ALL,
                  "");

        private final String fieldName;
        private final String description;
        private final int size;
        private final InputDataType dataType;
        private final boolean isRequired;
        private final ApplicabilityType applicability;
        private final String initialValue;

        /**********************************************************************
         * Default application data fields constructor
         * 
         * @param fieldName
         *            data field name
         * 
         * @param description
         *            data field description
         * 
         * @param size
         *            data field size in characters
         * 
         * @param dataType
         *            data field input data type
         * 
         * @param isRequired
         *            true if a value is required in the data field
         * 
         * @param applicability
         *            data field applicability type
         * 
         * @param initialValue
         *            initial value for the data field
         *********************************************************************/
        DefaultApplicationField(String fieldName,
                                String description,
                                int size,
                                InputDataType dataType,
                                boolean isRequired,
                                ApplicabilityType applicability,
                                String initialValue)
        {
            this.fieldName = fieldName;
            this.description = description;
            this.size = size;
            this.dataType = dataType;
            this.isRequired = isRequired;
            this.applicability = applicability;
            this.initialValue = initialValue;
        }

        /**********************************************************************
         * Get the default application field name
         * 
         * @return Default application field name
         *********************************************************************/
        protected String getFieldName()
        {
            return fieldName;
        }

        /**********************************************************************
         * Get the default application field initial value
         * 
         * @return Default application field initial value
         *********************************************************************/
        protected String getInitialValue()
        {
            return initialValue;
        }

        /**********************************************************************
         * Create the default data field's information for the specified owner
         * 
         * @param ownerName
         *            table or group name to which the field belongs
         * 
         * @return FieldInformation for the default data field
         *********************************************************************/
        protected FieldInformation createFieldInformation(String ownerName)
        {
            return new FieldInformation(ownerName,
                                        fieldName,
                                        description,
                                        size,
                                        dataType,
                                        isRequired,
                                        applicability,
                                        initialValue);
        }
    }

    /**************************************************************************
     * Macro editor column information
     *************************************************************************/
    protected static enum MacroEditorColumnInfo
    {
        NAME("Macro Name", "Macro name", "", true),
        VALUE("Value", "Macro value", "", false),
        OID("OID", "Macro index", "", false);

        private final String columnName;
        private final String toolTip;
        private final String initialValue;
        private final boolean isRequired;

        /**********************************************************************
         * Macro editor column information constructor
         * 
         * @param columnName
         *            text to display for the macro editor column header
         * 
         * @param toolTip
         *            tool tip text to display for the column
         * 
         * @param initialValue
         *            initial column value
         * 
         * @param isRequired
         *            true if a value is required in this column
         *********************************************************************/
        MacroEditorColumnInfo(String columnName,
                              String toolTip,
                              String initialValue,
                              boolean isRequired)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
            this.initialValue = initialValue;
            this.isRequired = isRequired;
        }

        /**********************************************************************
         * Get the macro editor column name
         * 
         * @return Macro editor column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the macro editor column required flag
         * 
         * @return Macro editor column required flag
         *********************************************************************/
        protected boolean isRequired()
        {
            return isRequired;
        }

        /**********************************************************************
         * Get the macro editor column names
         * 
         * @return Array containing the macro editor column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[MacroEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (MacroEditorColumnInfo type : MacroEditorColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the macro editor column tool tips
         * 
         * @return Array containing the macro editor column tool tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[MacroEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (MacroEditorColumnInfo type : MacroEditorColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }

        /**********************************************************************
         * Get a row with initialized values for the macro editor
         * 
         * @return Array containing initial values for a row in the macro
         *         editor
         *********************************************************************/
        protected static String[] getEmptyRow()
        {
            String[] emptyRow = new String[MacroEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (MacroEditorColumnInfo type : MacroEditorColumnInfo.values())
            {
                // Initialize the column value
                emptyRow[index] = type.initialValue;
                index++;
            }

            return emptyRow;
        }
    }

    /**************************************************************************
     * Data type editor column information
     *************************************************************************/
    protected static enum DataTypeEditorColumnInfo
    {
        USER_NAME("Type Name", "User-defined data type name", "", false),
        C_NAME("C Name", "C-language data type name", "", false),
        SIZE("Size", "Data type size in bytes", "", true),
        BASE_TYPE("Base Type", "Base data type", "", true),
        OID("OID", "Data type index", "", false);

        private final String columnName;
        private final String toolTip;
        private final String initialValue;
        private final boolean isRequired;

        /**********************************************************************
         * Data type editor column information constructor
         * 
         * @param columnName
         *            text to display for the data type editor column header
         * 
         * @param toolTip
         *            tool tip text to display for the column
         * 
         * @param initialValue
         *            initial column value
         * 
         * @param isRequired
         *            true if a value is required in this column
         *********************************************************************/
        DataTypeEditorColumnInfo(String columnName,
                                 String toolTip,
                                 String initialValue,
                                 boolean isRequired)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
            this.initialValue = initialValue;
            this.isRequired = isRequired;
        }

        /**********************************************************************
         * Get the data type editor column name
         * 
         * @return Data type editor column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the data type editor column required flag
         * 
         * @return Data type editor column required flag
         *********************************************************************/
        protected boolean isRequired()
        {
            return isRequired;
        }

        /**********************************************************************
         * Get the data type editor column names
         * 
         * @return Array containing the data type editor column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[DataTypeEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (DataTypeEditorColumnInfo type : DataTypeEditorColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the data type editor column tool tips
         * 
         * @return Array containing the data type editor column tool tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[DataTypeEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (DataTypeEditorColumnInfo type : DataTypeEditorColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }

        /**********************************************************************
         * Get a row with initialized values for the data type editor
         * 
         * @return Array containing initial values for a row in the data type
         *         editor
         *********************************************************************/
        protected static String[] getEmptyRow()
        {
            String[] emptyRow = new String[DataTypeEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (DataTypeEditorColumnInfo type : DataTypeEditorColumnInfo.values())
            {
                // Initialize the column value
                emptyRow[index] = type.initialValue;
                index++;
            }

            return emptyRow;
        }
    }

    /**************************************************************************
     * Data field editor column information
     *************************************************************************/
    protected static enum FieldEditorColumnInfo
    {
        NAME("Field Name", "Data field name", "", true),
        DESCRIPTION("Description", "Data field description", "", false),
        SIZE("Size", "Data field size (characters)", "", true),
        INPUT_TYPE("Input Type", "Data field input data type", "Text", true),
        REQUIRED("Required",
                 "Select if a value is required in the column",
                 false,
                 false),
        APPLICABILITY("Applicability",
                      "Add field to all tables, parent tables only, or child tables only",
                      ApplicabilityType.ALL,
                      true),
        VALUE("Value", "", "", false);

        private final String columnName;
        private final String toolTip;
        private final Object initialValue;
        private final boolean isRequired;

        /**********************************************************************
         * Data field editor column information constructor
         * 
         * @param columnName
         *            text to display for the field editor column header
         * 
         * @param toolTip
         *            tool tip text to display for the column
         * 
         * @param initialValue
         *            initial column value
         * 
         * @param isRequired
         *            true if a value is required in this column
         *********************************************************************/
        FieldEditorColumnInfo(String columnName,
                              String toolTip,
                              Object initialValue,
                              boolean isRequired)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
            this.initialValue = initialValue;
            this.isRequired = isRequired;
        }

        /**********************************************************************
         * Get the field editor column name
         * 
         * @return Field editor column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the field editor column required flag
         * 
         * @return Field editor column required flag
         *********************************************************************/
        protected boolean isRequired()
        {
            return isRequired;
        }

        /**********************************************************************
         * Get the field editor column names
         * 
         * @return Array containing the field editor column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[FieldEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (FieldEditorColumnInfo type : FieldEditorColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the field editor column tool tips
         * 
         * @return Array containing the field editor column tool tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[FieldEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (FieldEditorColumnInfo type : FieldEditorColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }

        /**********************************************************************
         * Get a row with initialized values for the field editor
         * 
         * @return Array containing initial values for a row in the field
         *         editor
         *********************************************************************/
        protected static Object[] getEmptyRow()
        {
            Object[] emptyRow = new Object[FieldEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (FieldEditorColumnInfo type : FieldEditorColumnInfo.values())
            {
                // Initialize the column value
                emptyRow[index] = type.initialValue;
                index++;
            }

            return emptyRow;
        }
    }

    /**************************************************************************
     * Data field table editor column information
     *************************************************************************/
    protected static enum FieldTableEditorColumnInfo
    {
        OWNER("Field Owner", "Data field owner (table or group name)"),
        PATH("Structure Path", "Structure table path");

        private final String columnName;
        private final String toolTip;

        /**********************************************************************
         * Data field table editor column information constructor
         * 
         * @param columnName
         *            text to display for the data field editor column name
         * 
         * @param toolTip
         *            tool tip text to display for the column
         *********************************************************************/
        FieldTableEditorColumnInfo(String columnName, String toolTip)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
        }

        /**********************************************************************
         * Get the data field table editor column name
         * 
         * @return Data field table editor column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the data field table editor column tool tip
         * 
         * @return Data field table editor column tool tip
         *********************************************************************/
        protected String getToolTip()
        {
            return toolTip;
        }
    }

    /**************************************************************************
     * Reserved message ID editor column information
     *************************************************************************/
    protected static enum ReservedMsgIDEditorColumnInfo
    {
        MSG_ID("Message ID(s)", "Message ID or range of IDs", "", true),
        DESCRIPTION("Description", "Description", "", false),
        OID("OID", "Reserved message ID index", "", false);

        private final String columnName;
        private final String toolTip;
        private final String initialValue;
        private final boolean isRequired;

        /**********************************************************************
         * Reserved message ID editor column information constructor
         * 
         * @param columnName
         *            text to display for the reserved message ID editor column
         *            header
         * 
         * @param toolTip
         *            tool tip text to display for the column
         * 
         * @param initialValue
         *            initial column value
         * 
         * @param isRequired
         *            true if a value is required in this column
         *********************************************************************/
        ReservedMsgIDEditorColumnInfo(String columnName,
                                      String toolTip,
                                      String initialValue,
                                      boolean isRequired)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
            this.initialValue = initialValue;
            this.isRequired = isRequired;
        }

        /**********************************************************************
         * Get the reserved message ID editor column name
         * 
         * @return Reserved message ID editor column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the reserved message ID editor column required flag
         * 
         * @return Reserved message ID editor column required flag
         *********************************************************************/
        protected boolean isRequired()
        {
            return isRequired;
        }

        /**********************************************************************
         * Get the reserved message ID editor column names
         * 
         * @return Array containing the reserved message ID editor column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[ReservedMsgIDEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (ReservedMsgIDEditorColumnInfo type : ReservedMsgIDEditorColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the reserved message ID editor column tool tips
         * 
         * @return Array containing the reserved message ID editor column tool
         *         tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[ReservedMsgIDEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (ReservedMsgIDEditorColumnInfo type : ReservedMsgIDEditorColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }

        /**********************************************************************
         * Get a row with initialized values for the reserved message ID editor
         * 
         * @return Array containing initial values for a row in the reserved
         *         message ID editor
         *********************************************************************/
        protected static String[] getEmptyRow()
        {
            String[] emptyRow = new String[ReservedMsgIDEditorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (ReservedMsgIDEditorColumnInfo type : ReservedMsgIDEditorColumnInfo.values())
            {
                // Initialize the column value
                emptyRow[index] = type.initialValue;
                index++;
            }

            return emptyRow;
        }
    }

    /**************************************************************************
     * Script associations table column information
     *************************************************************************/
    protected static enum AssociationsTableColumnInfo
    {
        DESCRIPTION("Description", "Script association description"),
        SCRIPT_FILE("Script File", "Script file name"),
        MEMBERS("Table(s)", "Table(s) associated with the script file"),
        AVAILABLE("Available", "Indicates if the association is available (true or false)");

        private final String columnName;
        private final String toolTip;

        /**********************************************************************
         * Script association table column information constructor
         * 
         * @param columnName
         *            text to display for the table verification column name
         * 
         * @param toolTip
         *            tool tip text to display for the table verification
         *            column
         *********************************************************************/
        AssociationsTableColumnInfo(String columnName,
                                    String toolTip)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
        }

        /**********************************************************************
         * Get the script association table column name
         * 
         * @return Script association table column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the script association table column tool tip
         * 
         * @return Script association table column tool tip
         *********************************************************************/
        protected String getToolTip()
        {
            return toolTip;
        }

        /**********************************************************************
         * Get the script association table column names
         * 
         * @return Array containing the script association table column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[AssociationsTableColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (AssociationsTableColumnInfo type : AssociationsTableColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the script association table column tool tips
         * 
         * @return Array containing the script association table column tool
         *         tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[AssociationsTableColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (AssociationsTableColumnInfo type : AssociationsTableColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }
    }

    /**************************************************************************
     * Search results table column information
     *************************************************************************/
    protected static enum SearchResultsColumnInfo
    {
        TARGET("Table / Object",
               "Name of the table or data object containing the search text",
               "Script",
               "Name of the script containing the search text",
               "Log Index",
               "Event log entry index containing the search text"),
        LOCATION("Location",
                 "Location containing the search text",
                 "Line Number",
                 "Line number in the script containing the search text",
                 "Column Name",
                 "Column name in the script containing the search text"),
        CONTEXT("Context",
                "Search text context",
                "Context",
                "Search text context",
                "Context",
                "Search text context");

        private final String tableColumnName;
        private final String tableToolTip;
        private final String scriptColumnName;
        private final String scriptToolTip;
        private final String logColumnName;
        private final String logToolTip;

        /**********************************************************************
         * Search results table column information constructor
         * 
         * @param tableColumnName
         *            text to display for the table search results column name
         * 
         * @param tableToolTip
         *            tool tip text to display for the table search results
         *            column
         * 
         * @param scriptColumnName
         *            text to display for the script search results column name
         * 
         * @param scriptToolTip
         *            tool tip text to display for the script search results
         *            column
         * 
         * @param logColumnName
         *            text to display for the event log search results column
         *            name
         * 
         * @param logToolTip
         *            tool tip text to display for the event log search results
         *            column
         *********************************************************************/
        SearchResultsColumnInfo(String tableColumnName,
                                String tableToolTip,
                                String scriptColumnName,
                                String scriptToolTip,
                                String logColumnName,
                                String logToolTip)
        {
            this.tableColumnName = tableColumnName;
            this.tableToolTip = tableToolTip;
            this.scriptColumnName = scriptColumnName;
            this.scriptToolTip = scriptToolTip;
            this.logColumnName = logColumnName;
            this.logToolTip = logToolTip;
        }

        /**********************************************************************
         * Get the search results table column header for the specified search
         * dialog type
         * 
         * @param searchType
         *            search dialog type: TABLES, SCRIPTS, or LOG
         * 
         * @return Search results table column name
         *********************************************************************/
        protected String getColumnName(SearchDialogType searchType)
        {
            String columnName = null;

            switch (searchType)
            {
                case TABLES:
                    columnName = tableColumnName;
                    break;

                case SCRIPTS:
                    columnName = scriptColumnName;
                    break;

                case LOG:
                    columnName = logColumnName;
                    break;
            }

            return columnName;
        }

        /**********************************************************************
         * Get the search results table column tool tip for the specified
         * search dialog type
         * 
         * @param searchType
         *            search dialog type: TABLES, SCRIPTS, or LOG
         * 
         * @return Search results table column tool tip
         *********************************************************************/
        protected String getToolTip(SearchDialogType searchType)
        {
            String toolTip = null;

            switch (searchType)
            {
                case TABLES:
                    toolTip = tableToolTip;
                    break;

                case SCRIPTS:
                    toolTip = scriptToolTip;
                    break;

                case LOG:
                    toolTip = logToolTip;
                    break;
            }

            return toolTip;
        }

        /**********************************************************************
         * Get the search results table column names for the specified search
         * dialog type
         * 
         * @param searchType
         *            search dialog type: TABLES, SCRIPTS, or LOG
         * 
         * @return Array containing the search results table column names
         *********************************************************************/
        protected static String[] getColumnNames(SearchDialogType searchType)
        {
            String[] names = new String[SearchResultsColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (SearchResultsColumnInfo type : SearchResultsColumnInfo.values())
            {
                switch (searchType)
                {
                    case TABLES:
                        names[index] = type.tableColumnName;
                        break;

                    case SCRIPTS:
                        names[index] = type.scriptColumnName;
                        break;

                    case LOG:
                        names[index] = type.logColumnName;
                        break;
                }

                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the search results column tool tips for the specified search
         * dialog type
         * 
         * @param searchType
         *            search dialog type: TABLES, SCRIPTS, or LOG
         * 
         * @return Array containing the search results column tool tips
         *********************************************************************/
        protected static String[] getToolTips(SearchDialogType searchType)
        {
            String[] toolTips = new String[SearchResultsColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (SearchResultsColumnInfo type : SearchResultsColumnInfo.values())
            {
                switch (searchType)
                {
                    case TABLES:
                        toolTips[index] = type.tableToolTip;
                        break;

                    case SCRIPTS:
                        toolTips[index] = type.scriptToolTip;
                        break;

                    case LOG:
                        toolTips[index] = type.logToolTip;
                        break;
                }

                index++;
            }

            return toolTips;
        }
    }

    /**************************************************************************
     * Duplicate message ID table column information
     *************************************************************************/
    protected static enum DuplicateMsgIDColumnInfo
    {
        OWNERS("Owners", "Message ID owners (tables and telemetry messages)"),
        MESSAGE_ID("Message ID", "Message ID");

        private final String columnName;
        private final String toolTip;

        /**********************************************************************
         * Duplicate message ID table column information constructor
         * 
         * @param columnName
         *            text to display for the duplicate message ID table column
         * 
         * @param toolTip
         *            tool tip text to display for the duplicate message ID
         *            table column
         *********************************************************************/
        DuplicateMsgIDColumnInfo(String columnName, String toolTip)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
        }

        /**********************************************************************
         * Get the duplicate message ID table column header
         * 
         * @return Duplicate message ID table column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the duplicate message ID table column tool tip
         * 
         * @return Duplicate message ID table column tool tip
         *********************************************************************/
        protected String getToolTip()
        {
            return toolTip;
        }

        /**********************************************************************
         * Get the duplicate message ID table column names
         * 
         * @return Array containing the duplicate message ID table column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[DuplicateMsgIDColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (DuplicateMsgIDColumnInfo type : DuplicateMsgIDColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the duplicate message ID table column tool tips
         * 
         * @return Array containing the duplicate message ID table column tool
         *         tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[DuplicateMsgIDColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (DuplicateMsgIDColumnInfo type : DuplicateMsgIDColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }
    }

    /**************************************************************************
     * Link copy error table column information
     *************************************************************************/
    protected static enum LinkCopyErrorColumnInfo
    {
        LINK("Link", "Name of link that failed to copy"),
        MEMBER("Member", "Variable that failed to copy"),
        STREAM("Target Stream", "Name of the target data stream"),
        CAUSE("Error Cause", "Description of the error");

        private final String columnName;
        private final String toolTip;

        /**********************************************************************
         * Link copy error table column information constructor
         * 
         * @param columnName
         *            text to display for the link copy error table column
         * 
         * @param toolTip
         *            tool tip text to display for the link copy error table
         *            column
         *********************************************************************/
        LinkCopyErrorColumnInfo(String columnName, String toolTip)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
        }

        /**********************************************************************
         * Get the link copy error table column header
         * 
         * @return Link copy error table column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the link copy error table column tool tip
         * 
         * @return Link copy error table column tool tip
         *********************************************************************/
        protected String getToolTip()
        {
            return toolTip;
        }

        /**********************************************************************
         * Get the link copy error table column names
         * 
         * @return Array containing the link copy error table column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[LinkCopyErrorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (LinkCopyErrorColumnInfo type : LinkCopyErrorColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the link copy error table column tool tips
         * 
         * @return Array containing the link copy error table column tool tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[LinkCopyErrorColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (LinkCopyErrorColumnInfo type : LinkCopyErrorColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }
    }

    /**************************************************************************
     * Database verification table column information
     *************************************************************************/
    protected static enum VerificationColumnInfo
    {
        FIX("Action", "Action to perform concerning the issue"),
        ISSUE("Issue", "Details on the issue detected in the project database"),
        ACTION("Corrective Action", "Action to be taken to correct the issue");

        private final String columnName;
        private final String toolTip;

        /**********************************************************************
         * Verification table column information constructor
         * 
         * @param columnName
         *            text to display for the table verification column name
         * 
         * @param toolTip
         *            tool tip text to display for the table verification
         *            column
         *********************************************************************/
        VerificationColumnInfo(String columnName,
                               String toolTip)
        {
            this.columnName = columnName;
            this.toolTip = toolTip;
        }

        /**********************************************************************
         * Get the verification table column name
         * 
         * @return Verification table column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }

        /**********************************************************************
         * Get the verification table column tool tip
         * 
         * @return Verification table column tool tip
         *********************************************************************/
        protected String getToolTip()
        {
            return toolTip;
        }

        /**********************************************************************
         * Get the verification table column names
         * 
         * @return Array containing the verification table column names
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] names = new String[VerificationColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (VerificationColumnInfo type : VerificationColumnInfo.values())
            {
                // Store the column name
                names[index] = type.columnName;
                index++;
            }

            return names;
        }

        /**********************************************************************
         * Get the verification table column tool tips
         * 
         * @return Array containing the verification table column tool tips
         *********************************************************************/
        protected static String[] getToolTips()
        {
            String[] toolTips = new String[VerificationColumnInfo.values().length];
            int index = 0;

            // Step through each column
            for (VerificationColumnInfo type : VerificationColumnInfo.values())
            {
                // Get the tool tip text
                toolTips[index] = type.toolTip;
                index++;
            }

            return toolTips;
        }
    }

    /**************************************************************************
     * Database table comment indices
     *************************************************************************/
    protected static enum TableCommentIndex
    {
        NAME,
        TYPE;

        /**********************************************************************
         * Build the comment by arranging the comment parameters in the correct
         * order
         * 
         * @param tableName
         *            table name
         * 
         * @param tableType
         *            table type
         * 
         * @return Table comment parameters, separated by commas
         *********************************************************************/
        protected static String buildComment(String tableName, String tableType)
        {
            String comment = "";

            // Step through each comment parameter in order
            for (TableCommentIndex tcIndex : TableCommentIndex.values())
            {
                switch (tcIndex)
                {
                    case NAME:
                        // Add the table name
                        comment += tableName;
                        break;

                    case TYPE:
                        // Add the table type
                        comment += tableType;
                }

                // Add a comma to separate the parameters
                comment += ",";
            }

            // Remove the trailing comma
            return CcddUtilities.removeTrailer(comment, ",");
        }
    }

    /**************************************************************************
     * Copy table entries
     *************************************************************************/
    protected static enum CopyTableEntry
    {
        INPUT_MSG_ID("Input Message ID"),
        INPUT_OFFSET("Input Offset"),
        OUTPUT_MSG_ID("Output Message ID"),
        OUTPUT_OFFSET("Output Offset"),
        VARIABLE_BYTES("Number of Bytes"),
        VARIABLE_ROOT("Root Table"),
        VARIABLE_NAME("Variable Path");

        private final String columnName;

        /**********************************************************************
         * Copy table entries constructor
         * 
         * @param name
         *            copy table column name
         *********************************************************************/
        CopyTableEntry(String columnName)
        {
            this.columnName = columnName;
        }

        /**********************************************************************
         * Get the copy table column name
         * 
         * @Return Copy table column name
         *********************************************************************/
        protected String getColumnName()
        {
            return columnName;
        }
    }

    /**************************************************************************
     * JSON data type tags
     *************************************************************************/
    protected static enum JSONTags
    {
        FILE_DESCRIPTION("File Description"),
        DATA_TYPE_DEFN("Data Type Definition"),
        TABLE_TYPE_DEFN("Table Type Definition"),
        TABLE_TYPE_NAME("Table Type Name"),
        TABLE_TYPE_DESCRIPTION("Table Type Description"),
        TABLE_TYPE_COLUMN("Table Type Column"),
        TABLE_TYPE_FIELD("Table Type Data Field"),
        MACRO_DEFN("Macro Definition"),
        RESERVED_MSG_ID_DEFN("Reserved Message ID Definition"),
        VARIABLE_PATH("Variable Path"),
        TABLE_DEFN("Table Definition"),
        TABLE_NAMES("Table Names"),
        TABLE_NAME("Table Name"),
        TABLE_TYPE("Table Type"),
        TABLE_DESCRIPTION("Table Description"),
        TABLE_DATA("Table Data"),
        TABLE_SYSTEM("System Name"),
        TABLE_FIELD("Data Field"),
        TABLE_BYTE_SIZE("Byte Size"),
        GROUP_NAMES("Group Names"),
        GROUP_NAME("Group Name"),
        GROUP_DESCRIPTION("Group Description"),
        GROUP_FIELD("Group Data Field"),
        GROUP_TABLE("Group Table"),
        APPLICATION_NAMES("Application Names"),
        APPLICATION_NAME("Application Name"),
        APPLICATION_DESCRIPTION("Application Description"),
        APPLICATION_FIELD("Application Data Field"),
        APPLICATION_TABLE("Application Table"),
        COPY_TABLE_STREAM("Stream Name"),
        COPY_TABLE_HDR_SIZE("Header Size"),
        COPY_TABLE_OPTIMIZE("Optimized"),
        COPY_TABLE_DATA("Copy Table");

        private final String tag;

        /**********************************************************************
         * JSON data type tags constructor
         * 
         * @param tag
         *            text describing the data
         *********************************************************************/
        JSONTags(String tag)
        {
            this.tag = tag;
        }

        /**********************************************************************
         * Get the data type tag
         * 
         * @return Text describing the data
         *********************************************************************/
        protected String getTag()
        {
            return tag;
        }
    }

    /**************************************************************************
     * Database list query commands
     *************************************************************************/
    protected static enum DatabaseListCommand
    {
        // Get the list of data tables only, extracted from the table comments
        // to retain their original capitalization, sorted alphabetically
        DATA_TABLES("SELECT name FROM (SELECT split_part(obj_description, ',', "
                    + (TableCommentIndex.NAME.ordinal() + 1)
                    + ") AS name FROM (SELECT obj_description(oid) "
                    + "FROM pg_class WHERE substr(relname, 1, "
                    + INTERNAL_TABLE_PREFIX.length()
                    + ") != '"
                    + INTERNAL_TABLE_PREFIX
                    + "' AND relkind = 'r' "
                    + "AND obj_description(oid) != '') AS alias1) AS alias2 "
                    + "ORDER BY name ASC;"),

        // Get the list containing the user-viewable table name, database table
        // name, and table type for all data tables, sorted alphabetically
        DATA_TABLES_WITH_TYPE("SELECT name || E',' || relname || E',' || type FROM "
                              + "(SELECT split_part(obj_description, ',', 1) AS name, "
                              + "lower(split_part(obj_description, ',', 2)) AS type,"
                              + " relname FROM (SELECT obj_description(oid), relname"
                              + " FROM pg_class WHERE substr(relname, 1, "
                              + INTERNAL_TABLE_PREFIX.length()
                              + ") != '"
                              + INTERNAL_TABLE_PREFIX
                              + "' AND relkind = 'r' AND obj_description(oid) != '') "
                              + "AS alias1) AS alias2 ORDER BY name ASC;"),

        // Get the list of all tables (data and information), sorted
        // alphabetically
        ALL_TABLES("SELECT tablename FROM pg_tables "
                   + "WHERE schemaname = 'public' ORDER BY tablename ASC;"),

        // Check if a specific table exists in the database (case insensitive)
        SPECIFIC_TABLE("SELECT 1 FROM pg_tables WHERE tablename ~* E'^_table_name_$';"),

        // Get the list of CCDD databases (in the form 'database name,lock
        // status,visible name,description'), sorted alphabetically
        DATABASES("SELECT datname || E'"
                  + DATABASE_COMMENT_SEPARATOR
                  + "' || split_part(description, '"
                  + DATABASE_COMMENT_SEPARATOR
                  + "', 2) || E'"
                  + DATABASE_COMMENT_SEPARATOR
                  + "' || split_part(split_part(description, '"
                  + DATABASE_COMMENT_SEPARATOR
                  + "', 1), '"
                  + CCDD_PROJECT_IDENTIFIER
                  + "', 2) || E'"
                  + DATABASE_COMMENT_SEPARATOR
                  + "' || substr(description, length(datName || '"
                  + CCDD_PROJECT_IDENTIFIER
                  + "') + 4) AS lock_name_desc FROM pg_database d LEFT JOIN "
                  + "pg_shdescription ON pg_shdescription.objoid = d.oid "
                  + "WHERE d.datistemplate = false AND description LIKE '"
                  + CCDD_PROJECT_IDENTIFIER
                  + "%' ORDER BY datname ASC;"),

        // Get the list of CCDD databases (in the form 'database name,lock
        // status,visible name,description'), sorted alphabetically, for which
        // the user has access. '_user_' must be replaced by the user name
        DATABASES_BY_USER("SELECT datname || E'"
                          + DATABASE_COMMENT_SEPARATOR
                          + "' || split_part(description, '"
                          + DATABASE_COMMENT_SEPARATOR
                          + "', 2) || E'"
                          + DATABASE_COMMENT_SEPARATOR
                          + "' || split_part(split_part(description, '"
                          + DATABASE_COMMENT_SEPARATOR
                          + "', 1), '"
                          + CCDD_PROJECT_IDENTIFIER
                          + "', 2) || E'"
                          + DATABASE_COMMENT_SEPARATOR
                          + "' || substr(description, length(datName || '"
                          + CCDD_PROJECT_IDENTIFIER
                          + "') + 4) AS lock_name_desc FROM pg_database d LEFT JOIN "
                          + "pg_shdescription ON pg_shdescription.objoid = d.oid "
                          + "WHERE d.datistemplate = false AND description LIKE '"
                          + CCDD_PROJECT_IDENTIFIER
                          + "%' AND pg_has_role('_user_', pg_catalog.pg_get_userbyid("
                          + "d.datdba), 'member') = 't' ORDER BY datname ASC;"),

        // Get the list of active database connections by user. The database
        // and user names are concatenated (separated by a comma) in order to
        // use the getList method
        ACTIVE_BY_USER("select distinct datname || ',' || usename "
                       + "AS names from pg_stat_activity ORDER BY names ASC;"),

        // Get the list of users, sorted alphabetically
        USERS("SELECT u.usename FROM pg_catalog.pg_user u ORDER BY u.usename ASC;"),

        // Get the list of roles, sorted alphabetically
        ROLES("SELECT r.rolname FROM pg_catalog.pg_roles r ORDER BY r.rolname ASC;"),

        // Get the owner of the specified database
        DATABASE_OWNER("SELECT pg_catalog.pg_get_userbyid(d.datdba) AS owner "
                       + "FROM pg_catalog.pg_database d "
                       + "WHERE d.datname = '__';"),

        // Get the list of PostgreSQL keywords
        KEYWORDS("SELECT * FROM pg_get_keywords()"),

        // Get the list of tables of type '_type_', sorted alphabetically.
        // '_type_' must be replaced by the type of table for which to search.
        // _type_ is case insensitive
        TABLES_OF_TYPE("SELECT name FROM (SELECT split_part(obj_description, ',', "
                       + (TableCommentIndex.NAME.ordinal() + 1)
                       + ") AS name, lower(split_part(obj_description, ',', "
                       + (TableCommentIndex.TYPE.ordinal() + 1)
                       + ")) AS type FROM (SELECT obj_description(oid) FROM pg_class "
                       + "WHERE relkind = 'r' AND obj_description(oid) != '') alias1) "
                       + "alias2 WHERE type = '_type_' ORDER BY name ASC;"),

        // Get the list of table types, sorted alphabetically
        TABLE_TYPES("SELECT DISTINCT "
                    + TableTypesColumn.TYPE_NAME.getColumnName()
                    + " FROM "
                    + InternalTable.TABLE_TYPES.getTableName()
                    + " ORDER BY "
                    + TableTypesColumn.TYPE_NAME.getColumnName()
                    + ";"),

        // TODO THIS DISPLAYS A TYPE ONLY IF A TABLE OF THAT TYPE EXISTS
        // Get the list of table types, sorted alphabetically
        // TABLE_TYPES("SELECT DISTINCT type FROM (SELECT split_part(obj_description, ',', "
        // + (TableCommentIndex.TYPE.ordinal() + 1)
        // + ") as type FROM (SELECT obj_description(oid) FROM "
        // + "pg_class WHERE relkind = 'r' AND obj_description(oid) != '' "
        // + "AND substr(relname, 1, "
        // + INTERNAL_TABLE_PREFIX.length()
        // + ") != '"
        // + INTERNAL_TABLE_PREFIX
        // + "') alias1) alias2 ORDER BY type ASC;"),

        // Get the list of table names, variable paths, and descriptions (only
        // for those tables with descriptions), sorted alphabetically
        TABLE_DESCRIPTIONS("SELECT "
                           + ValuesColumn.TABLE_PATH.getColumnName()
                           + " || E'"
                           + Matcher.quoteReplacement(TABLE_DESCRIPTION_SEPARATOR)
                           + "' || "
                           + ValuesColumn.VALUE.getColumnName()
                           + " AS description FROM "
                           + InternalTable.VALUES.getTableName()
                           + " WHERE "
                           + ValuesColumn.COLUMN_NAME.getColumnName()
                           + " = '' AND "
                           + ValuesColumn.VALUE.getColumnName()
                           + " != '' ORDER BY "
                           + ValuesColumn.TABLE_PATH.getColumnName()
                           + " ASC;"),

        // Get the list of data tables and their comments, sorted
        // alphabetically
        TABLE_COMMENTS("SELECT description FROM (SELECT obj_description "
                       + "AS description FROM (SELECT obj_description(oid) "
                       + "FROM pg_class WHERE relkind = 'r' AND "
                       + "obj_description(oid) != '' AND substr(relname, 1, "
                       + INTERNAL_TABLE_PREFIX.length()
                       + ") != '"
                       + INTERNAL_TABLE_PREFIX
                       + "') alias1) alias2 ORDER BY description ASC;"),

        // Get the list of stored scripts, sorted alphabetically
        SCRIPTS("SELECT script_name FROM (SELECT obj_description AS "
                + "script_name FROM (SELECT obj_description(oid) FROM "
                + "pg_class WHERE relkind = 'r' AND obj_description(oid) "
                + "!= '' AND substr(relname, 1, "
                + InternalTable.SCRIPT.getTableName().length()
                + ") = '"
                + InternalTable.SCRIPT.getTableName()
                + "') alias1) alias2 ORDER BY script_name ASC;"),

        // Get the list of table and column names that contain the specified
        // search text. '___' should be replaced by the text for which to
        // search
        SEARCH("SELECT table_name::text || E'"
               + TABLE_DESCRIPTION_SEPARATOR
               + "' || column_name || E'"
               + TABLE_DESCRIPTION_SEPARATOR
               + "' || table_description || E'"
               + TABLE_DESCRIPTION_SEPARATOR
               + "' || column_value AS search_result "
               + "FROM search_tables(E'_search_text_', "
               + "_case_insensitive_, _allow_regex_,"
               + "'_selected_tables_', '{_columns_}') "
               + "ORDER BY table_name, column_name ASC;"),

        // ////////////////////////////////////////////////////////////////////
        // THE REMAINING COMMANDS ARE NOT USED BUT ARE RETAINED AS EXAMPLES
        // ////////////////////////////////////////////////////////////////////
        // Get the list of columns for a table, sorted alphabetically. '___'
        // should be replaced by the table to search
        TABLE_COLUMNS("SELECT column_name FROM information_schema.columns "
                      + "WHERE table_name = '___' ORDER BY column_name ASC;"),

        // Get the tables that are members of the specified table. '___' should
        // be replaced by the table to search
        TABLE_MEMBERS("SELECT DISTINCT ON (data_type) "
                      + "CASE WHEN EXISTS "
                      + "(SELECT 1 FROM pg_catalog.pg_attribute "
                      + "WHERE attrelid = '___'::regclass "
                      + "AND attname = 'data_type' "
                      + "AND NOT attisdropped AND attnum > 0) "
                      + "THEN data_type::text ELSE ''::text END "
                      + "AS data_type FROM ___ AS data_type;");

        private final String listCommand;

        /**********************************************************************
         * Database list query commands constructor
         * 
         * @param listCommand
         *            postgreSQL query command
         *********************************************************************/
        DatabaseListCommand(String listCommand)
        {
            this.listCommand = listCommand;
        }

        /**********************************************************************
         * Get the list command
         * 
         * @param listOption
         *            array containing replacement text for those commands that
         *            must be tailored
         * 
         * @return List command string
         *********************************************************************/
        protected String getListCommand(String[][] listOptions)
        {
            String command = listCommand;

            // Check if replacement text is supplied
            if (listOptions != null)
            {
                // Step through each option
                for (String[] option : listOptions)
                {
                    // Replace the text within the command
                    command = command.replace(option[0], option[1]);
                }
            }

            return command;
        }
    }

    /**************************************************************************
     * Event log table columns
     *************************************************************************/
    // Event log table header indices
    protected static enum EventColumns
    {
        INDEX("Index"),
        SERVER("Server"),
        PROJECT("Project"),
        USER(" User "),
        TIME("Date/Time"),
        TYPE("  Type  "),
        MESSAGE("Message");

        private final String columnName;

        /**********************************************************************
         * Event log table columns constructor
         * 
         * @param name
         *            column name
         *********************************************************************/
        EventColumns(String name)
        {
            columnName = name;
        }

        /**********************************************************************
         * Get the event log column name array
         * 
         * @return Event log column name array
         *********************************************************************/
        protected static String[] getColumnNames()
        {
            String[] columnNames = new String[EventColumns.values().length];
            int index = 0;

            // Step through each column
            for (EventColumns column : EventColumns.values())
            {
                // Insert the column name into the array
                columnNames[index] = column.columnName;
                index++;
            }

            return columnNames;
        }
    }

    /**************************************************************************
     * Event log message types
     *************************************************************************/
    protected static enum EventLogMessageType
    {
        // Master filter; this creates a check box that, when selected, toggles
        // all of the other filters below
        SELECT_ALL("All ", "#000000"),

        // Database command message filter
        COMMAND_MSG("Command", "#0000C0"),

        // Database command successfully completed message filter
        SUCCESS_MSG("Success", "#00A000"),

        // Database command failed to complete successfully message filter
        FAIL_MSG("Fail", "#A00000"),

        // Application status message filter
        STATUS_MSG("Status", "#000000"),

        // Web server message filter
        SERVER_MSG("Server", "#A08020");

        private final String typeName;
        private final String typeColor;

        /**********************************************************************
         * Event log message type constructor
         * 
         * @param typeName
         *            filter name for display beside the check box and in the
         *            event log Type column
         * 
         * @param typeColor
         *            color in which to display the filter name
         *********************************************************************/
        EventLogMessageType(String typeName, String typeColor)
        {
            this.typeName = typeName;
            this.typeColor = typeColor;
        }

        /**********************************************************************
         * Event log message type name
         *********************************************************************/
        protected String getTypeName()
        {
            return typeName;
        }

        /**********************************************************************
         * Event log message type color
         *********************************************************************/
        protected String getTypeColor()
        {
            return typeColor;
        }

        /**********************************************************************
         * Event log message type message
         *********************************************************************/
        protected String getTypeMsg()
        {
            return "<html><span style=\"color:" + typeColor
                   + "\"><b>"
                   + typeName
                   + "</b>";
        }
    }

    /**************************************************************************
     * Dialog option types
     *************************************************************************/
    protected static enum DialogOption
    {
        OK_CANCEL_OPTION("Okay", 'O', "Cancel", OK_ICON, 2),
        OK_OPTION("Okay", 'O', "", OK_ICON, 1),
        CLOSE_OPTION("Close", 'C', "", CLOSE_ICON, 1),
        OPEN_OPTION("Open", 'O', "Cancel", OK_ICON, 2),
        SAVE_OPTION("Save", 'S', "Cancel", STORE_ICON, 2),
        PRINT_OPTION("Print", 'P', "Close", PRINT_ICON, 2),
        CREATE_OPTION("Create", 'R', "Cancel", INSERT_ICON, 2),
        DELETE_OPTION("Delete", 'D', "Cancel", DELETE_ICON, 2),
        IMPORT_OPTION("Import", 'I', "Cancel", IMPORT_ICON, 2),
        EXPORT_OPTION("Export", 'E', "Cancel", EXPORT_ICON, 2),
        LOAD_OPTION("Load", 'L', "Cancel", IMPORT_ICON, 2),
        RENAME_OPTION("Rename", 'R', "Cancel", RENAME_ICON, 2),
        COPY_OPTION("Copy", 'P', "Cancel", COPY_ICON, 2),
        BACKUP_OPTION("Backup", 'B', "Cancel", COPY_ICON, 2),
        RESTORE_OPTION("Restore", 'R', "Cancel", UNDO_ICON, 2),
        STORE_OPTION("Store", 'S', "Cancel", COPY_ICON, 2),
        RETRIEVE_OPTION("Retrieve", 'R', "Cancel", UNDO_ICON, 2),
        UNLOCK_OPTION("Unlock", 'U', "Cancel", UNLOCK_ICON, 2),
        SEARCH_OPTION("Search", 'S', "Cancel", SEARCH_ICON, 2),
        HALT_OPTION("Halt", 'H', "", HALT_EXECUTION_ICON, 1);

        private final String buttonText;
        private final char buttonMnemonic;
        private final String secondaryButtonText;
        private final String buttonIcon;
        private final int numButtons;

        /**********************************************************************
         * Dialog option types constructor
         * 
         * @param buttonText
         *            text to display on the primary button
         * 
         * @param buttonMnemonic
         *            character for actuating the primary button via the
         *            keyboard
         * 
         * @param secondaryButtonText
         *            text to display on the secondary button (if present)
         * 
         * @param buttonIcon
         *            identifier for the icon to display on the primary button
         * 
         * @param numButtons
         *            number of buttons to display in the dialog
         *********************************************************************/
        DialogOption(String buttonText,
                     char buttonMnemonic,
                     String secondaryButtonText,
                     String buttonIcon,
                     int numButtons)
        {
            this.buttonText = buttonText;
            this.buttonMnemonic = buttonMnemonic;
            this.secondaryButtonText = secondaryButtonText;
            this.buttonIcon = buttonIcon;
            this.numButtons = numButtons;
        }

        /**********************************************************************
         * Get the primary button text
         * 
         * @return Text to display on the primary button
         *********************************************************************/
        protected String getButtonText()
        {
            return buttonText;
        }

        /**********************************************************************
         * Get the primary button mnemonic
         * 
         * @return Character to actuate the primary button via the keyboard
         *********************************************************************/
        protected char getButtonMnemonic()
        {
            return buttonMnemonic;
        }

        /**********************************************************************
         * Get the secondary button text
         * 
         * @return Text to display on the secondary button
         *********************************************************************/
        protected String getSecondaryButtonText()
        {
            return secondaryButtonText;
        }

        /**********************************************************************
         * Get the primary button icon reference
         * 
         * @return Reference for the icon to display on the primary button
         *********************************************************************/
        protected String getButtonIcon()
        {
            return buttonIcon;
        }

        /**********************************************************************
         * Get the number of buttons to display in the dialog
         * 
         * @return Number of buttons to display in the dialog
         *********************************************************************/
        protected int getNumButtons()
        {
            return numButtons;
        }
    }

    /**************************************************************************
     * Set GUI adjustment(s) based on the selected look & feel
     * 
     * @param lookAndFeel
     *            name of the look & feel in effect
     *************************************************************************/
    protected static void setLaFAdjustments(String lookAndFeel)
    {
        // Find the width, in pixels, needed to display a vertical scroll bar
        // by creating a dummy scroll bar. Set the scroll bar to null
        // afterwards to free up its memory
        JScrollPane sp = new JScrollPane(null,
                                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        LAF_SCROLL_BAR_WIDTH = sp.getPreferredSize().width * 2 + 2;
        sp = null;

        // Determine the height, in pixels, of a check box and check box icon.
        // The correct height is not returned by the UI manager for all look &
        // feels unless the check box is first realized, so an invisible window
        // is created, a check box instantiated, then the window removed. The
        // check box height returned by the UI manager is now correct
        JWindow wndw = new JWindow();
        JCheckBox chbx = new JCheckBox();
        chbx.setFont(ModifiableFontInfo.LABEL_BOLD.getFont());
        wndw.add(chbx);
        wndw.pack();
        LAF_CHECK_BOX_HEIGHT = chbx.getHeight();
        wndw.dispose();
    }
}
