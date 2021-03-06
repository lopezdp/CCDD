/**
 * CFS Command & Data Dictionary search database tables and scripts handler.
 * Copyright 2017 United States Government as represented by the Administrator
 * of the National Aeronautics and Space Administration. No copyright is
 * claimed in the United States under Title 17, U.S. Code. All Other Rights
 * Reserved.
 */
package CCDD;

import static CCDD.CcddConstants.INTERNAL_TABLE_PREFIX;
import static CCDD.CcddConstants.TABLE_DESCRIPTION_SEPARATOR;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import CCDD.CcddClasses.ArrayVariable;
import CCDD.CcddConstants.DatabaseListCommand;
import CCDD.CcddConstants.DefaultColumn;
import CCDD.CcddConstants.DialogOption;
import CCDD.CcddConstants.EventColumns;
import CCDD.CcddConstants.InputDataType;
import CCDD.CcddConstants.InternalTable;
import CCDD.CcddConstants.InternalTable.AppSchedulerColumn;
import CCDD.CcddConstants.InternalTable.AssociationsColumn;
import CCDD.CcddConstants.InternalTable.DataTypesColumn;
import CCDD.CcddConstants.InternalTable.FieldsColumn;
import CCDD.CcddConstants.InternalTable.GroupsColumn;
import CCDD.CcddConstants.InternalTable.LinksColumn;
import CCDD.CcddConstants.InternalTable.ScriptColumn;
import CCDD.CcddConstants.InternalTable.TableTypesColumn;
import CCDD.CcddConstants.InternalTable.TlmSchedulerColumn;
import CCDD.CcddConstants.InternalTable.ValuesColumn;
import CCDD.CcddConstants.SearchDialogType;
import CCDD.CcddConstants.SearchResultsQueryColumn;
import CCDD.CcddConstants.SearchType;
import CCDD.CcddTableTypeHandler.TypeDefinition;

/******************************************************************************
 * CFS Command & Data Dictionary search database tables, scripts, and event log
 * handler class
 *****************************************************************************/
@SuppressWarnings("serial")
public class CcddSearchHandler extends CcddDialogHandler
{
    // Class references
    private final CcddDbCommandHandler dbCommand;
    private final CcddTableTypeHandler tableTypeHandler;
    private final CcddEventLogDialog eventLog;

    // Search dialog type
    private final SearchDialogType searchDlgType;

    /**************************************************************************
     * Search database tables, scripts, and event log handler class constructor
     * 
     * @param ccddMain
     *            main class
     * 
     * @param searchType
     *            search dialog type: TABLES, SCRIPTS, or LOG
     * 
     * @param targetRow
     *            row index to match if this is an event log entry search on a
     *            table that displays only a single log entry; null otherwise
     * 
     * @param eventLog
     *            event log to search; null if not searching a log
     *************************************************************************/
    CcddSearchHandler(CcddMain ccddMain,
                      SearchDialogType searchType,
                      Long targetRow,
                      CcddEventLogDialog eventLog)
    {
        this.searchDlgType = searchType;
        this.eventLog = eventLog;

        // Create references to shorten subsequent calls
        dbCommand = ccddMain.getDbCommandHandler();
        tableTypeHandler = ccddMain.getTableTypeHandler();
    }

    /**************************************************************************
     * Search database tables and scripts class constructor
     * 
     * @param ccddMain
     *            main class
     * 
     * @param searchType
     *            search dialog type: TABLES or SCRIPTS
     *************************************************************************/
    CcddSearchHandler(CcddMain ccddMain, SearchDialogType searchType)
    {
        this(ccddMain, searchType, null, null);
    }

    /**************************************************************************
     * Search for occurrences of a string in the tables or scripts
     * 
     * @param searchText
     *            text string to search for in the database
     * 
     * @param ignoreCase
     *            true to ignore case when looking for matching text
     * 
     * @param allowRegex
     *            true to allow a regular expression search string
     * 
     * @param dataTablesOnly
     *            true if only the data tables, and not references in the
     *            internal tables, are to be searched
     * 
     * @param searchColumns
     *            string containing the names of columns, separated by commas,
     *            to which to constrain a table search
     * 
     * @return Search results List containing object arrays providing each
     *         match's location in the database tables or event log, the column
     *         within the location, and an extract for the located match
     *         showing its context
     *************************************************************************/
    protected List<Object[]> searchTablesOrScripts(String searchText,
                                                   boolean ignoreCase,
                                                   boolean allowRegex,
                                                   boolean dataTablesOnly,
                                                   String searchColumns)
    {
        // Initialize the list to contain the search results
        List<Object[]> resultsDataList = new ArrayList<Object[]>();

        // Set the search type based on the dialog type and, for a table
        // search, the state of the 'data tables only' check box
        String searchType = searchDlgType == SearchDialogType.TABLES
                                                                    ? (dataTablesOnly
                                                                                     ? SearchType.DATA.toString()
                                                                                     : SearchType.ALL.toString())
                                                                    : SearchType.SCRIPT.toString();

        // Search the database for the text
        String[] hits = dbCommand.getList(DatabaseListCommand.SEARCH,
                                          new String[][] { {"_search_text_",
                                                            searchText},
                                                          {"_case_insensitive_",
                                                           String.valueOf(ignoreCase)},
                                                          {"_allow_regex_",
                                                           String.valueOf(allowRegex)},
                                                          {"_selected_tables_",
                                                           searchType},
                                                          {"_columns_",
                                                           searchColumns}},
                                          CcddSearchHandler.this);

        // Step through each table/column containing the search text
        for (String hit : hits)
        {
            // Split the found item into table, column, description, and
            // context
            String[] tblColDescAndCntxt = hit.split(TABLE_DESCRIPTION_SEPARATOR, 4);

            // Create a reference to the search result's column name to shorten
            // comparisons below
            String hitColumnName = tblColDescAndCntxt[SearchResultsQueryColumn.COLUMN.ordinal()];

            // Check that the column isn't the primary key or row index
            if (!hitColumnName.equals(DefaultColumn.PRIMARY_KEY.getDbName())
                && !hitColumnName.equals(DefaultColumn.ROW_INDEX.getDbName()))
            {
                // Create references to the the remaining search result columns
                // to shorten comparisons below
                String hitTableName = tblColDescAndCntxt[SearchResultsQueryColumn.TABLE.ordinal()];
                String hitTableComment = tblColDescAndCntxt[SearchResultsQueryColumn.COMMENT.ordinal()];
                String hitContext = tblColDescAndCntxt[SearchResultsQueryColumn.CONTEXT.ordinal()];

                // Separate the table comment into the viewable table name and
                // table type, or for scripts the script name and description
                String[] nameAndType = hitTableComment.split(",");

                // Split the row in which the match is found into its separate
                // columns, accounting for quotes around the comma separated
                // column values (i.e., ignore commas within quotes)
                String[] columnValue = CcddUtilities.splitAndRemoveQuotes(hitContext);

                String target = null;
                String location = null;
                String context = null;

                // Check if this is a table search
                if (searchDlgType == SearchDialogType.TABLES)
                {
                    // The reference is to a prototype table
                    if (!hitTableName.startsWith(INTERNAL_TABLE_PREFIX))
                    {
                        // Get the table's type definition based on its table
                        // type
                        TypeDefinition typeDefn = tableTypeHandler.getTypeDefinition(nameAndType[1]);

                        // Get the index of the column where the match exists
                        int colIndex = typeDefn.getColumnIndexByDbName(hitColumnName);

                        // Set the row number for the row location if the
                        // variable name or command name aren't present
                        String row = "row "
                                     + columnValue[DefaultColumn.ROW_INDEX.ordinal()];

                        // Check if this is a structure table
                        if (typeDefn.isStructure())
                        {
                            // Get the variable name column index
                            int index = typeDefn.getColumnIndexByInputType(InputDataType.VARIABLE);

                            // Check that a variable name exists
                            if (index != -1 && !columnValue[index].isEmpty())
                            {
                                // Set the row location to the variable name
                                row = "variable '"
                                      + columnValue[index]
                                      + "'";
                            }
                        }
                        // Check if this is a command table
                        else if (typeDefn.isCommand())
                        {
                            // Get the command name column index
                            int index = typeDefn.getColumnIndexByInputType(InputDataType.COMMAND_NAME);

                            // Check that a command name exists
                            if (index != -1 && !columnValue[index].isEmpty())
                            {
                                // Set the row location to the command name
                                row = "command '"
                                      + columnValue[index]
                                      + "'";
                            }
                        }

                        // Set the search result table values
                        target = nameAndType[0];
                        location = "Column '"
                                   + typeDefn.getColumnNamesUser()[colIndex]
                                   + "', "
                                   + row;
                        context = columnValue[colIndex];
                    }
                    // Check if the match is in the custom values internal
                    // table
                    else if (hitTableName.equals(InternalTable.VALUES.getTableName()))
                    {
                        // Check if the match is in the value column
                        if (hitColumnName.equals(ValuesColumn.VALUE.getColumnName()))
                        {
                            // Get the column values from the row in which the
                            // match occurs
                            String tablePath = columnValue[ValuesColumn.TABLE_PATH.ordinal()];
                            String columnName = columnValue[ValuesColumn.COLUMN_NAME.ordinal()];
                            String value = columnValue[ValuesColumn.VALUE.ordinal()];

                            // Check if this is a table definition entry in the
                            // values table
                            if (columnName.isEmpty())
                            {
                                // Set the location
                                location = "Table description";
                            }
                            // Column value from a child table stored in the
                            // internal values table. Since this isn't a table
                            // description the reference must be to a structure
                            // table (for other table types the match would be
                            // in the table prototype)
                            else
                            {
                                // Set the location
                                location = "Column '"
                                           + columnName
                                           + "'";

                                // Initialize the variable name and get the
                                // index where the last variable name begins
                                int index = tablePath.lastIndexOf(',');

                                // Check if a variable name exists
                                if (index != -1)
                                {
                                    // Extract the variable from the path, then
                                    // remove it from the variable path
                                    location += ", variable '"
                                                + tablePath.substring(index + 1).replaceFirst("^.+\\.", "")
                                                + "'";
                                    tablePath = tablePath.substring(0, index).replaceFirst(",", ":");
                                }
                            }

                            // Set the search result table values
                            target = tablePath;
                            context = value;
                        }
                    }
                    // Check if the match is in the data types internal table
                    else if (hitTableName.equals(InternalTable.DATA_TYPES.getTableName()))
                    {
                        target = "Data type";
                        location = "Data type '"
                                   + CcddDataTypeHandler.getDataTypeName(columnValue[DataTypesColumn.USER_NAME.ordinal()],
                                                                         columnValue[DataTypesColumn.C_NAME.ordinal()])
                                   + "' ";

                        // Check if the match is with the user-defined name
                        if (hitColumnName.equals(DataTypesColumn.USER_NAME.getColumnName()))
                        {
                            location += "user-defined name";
                            context = columnValue[DataTypesColumn.USER_NAME.ordinal()];
                        }
                        // Check if the match is with the C-language name
                        else if (hitColumnName.equals(DataTypesColumn.C_NAME.getColumnName()))
                        {
                            location += "C-language name";
                            context = columnValue[DataTypesColumn.C_NAME.ordinal()];
                        }
                        // Check if the match is with the data type size
                        else if (hitColumnName.equals(DataTypesColumn.SIZE.getColumnName()))
                        {
                            location += "data type size";
                            context = columnValue[DataTypesColumn.SIZE.ordinal()];
                        }
                        // Check if the match is with the base type
                        else if (hitColumnName.equals(DataTypesColumn.BASE_TYPE.getColumnName()))
                        {
                            location += "base data type";
                            context = columnValue[DataTypesColumn.BASE_TYPE.ordinal()];
                        }
                    }
                    // Check if the match is in the groups table
                    else if (hitTableName.equals(InternalTable.GROUPS.getTableName()))
                    {
                        target = "Group";
                        location = "Group '"
                                   + columnValue[GroupsColumn.GROUP_NAME.ordinal()]
                                   + "' ";

                        // Check if the match is with the group name
                        if (hitColumnName.equals(GroupsColumn.GROUP_NAME.getColumnName()))
                        {
                            location += "name";
                            context = columnValue[GroupsColumn.GROUP_NAME.ordinal()];
                        }
                        // The match is with a group definition or member
                        else
                        {
                            // Check if the column begins with a number; this
                            // is the group definition
                            if (columnValue[GroupsColumn.MEMBERS.ordinal()].matches("^\\d+"))
                            {
                                // Get the group description (remove the dummy
                                // number and comma that flags this as a group
                                // definition)
                                context = columnValue[GroupsColumn.MEMBERS.ordinal()].split(",")[1];

                                // Check if the description contains the search
                                // text (i.e., the dummy number and comma
                                // aren't part of the match)
                                if (context.toLowerCase().contains(searchText.toLowerCase()))
                                {
                                    location += "description";
                                }
                                // The match includes the dummy number and
                                // comma; ignore
                                else
                                {
                                    target = null;
                                }
                            }
                            // This is a group member
                            else
                            {
                                location += "member table";
                                context = columnValue[GroupsColumn.MEMBERS.ordinal()];
                            }
                        }
                    }
                    // Check if the match is in the fields internal table
                    else if (hitTableName.equals(InternalTable.FIELDS.getTableName()))
                    {
                        location = "Data field '"
                                   + columnValue[FieldsColumn.FIELD_NAME.ordinal()]
                                   + "' ";

                        // Check if this is a default data field
                        if ((columnValue[FieldsColumn.OWNER_NAME.ordinal()] + ":").startsWith(CcddFieldHandler.getFieldTypeName("")))
                        {
                            target = "Default data field";
                        }
                        // Check if this is a group data field
                        else if ((columnValue[FieldsColumn.OWNER_NAME.ordinal()] + ":").startsWith(CcddFieldHandler.getFieldGroupName("")))
                        {
                            target = "Group data field";
                        }
                        // Table data field
                        else
                        {
                            target = columnValue[FieldsColumn.OWNER_NAME.ordinal()].replaceFirst(",", ":");
                        }

                        // Check if the match is with the field owner name
                        if (hitColumnName.equals(FieldsColumn.OWNER_NAME.getColumnName()))
                        {
                            location += "owner";
                            context = columnValue[FieldsColumn.OWNER_NAME.ordinal()];
                        }
                        // Check if the match is with the field name
                        else if (hitColumnName.equals(FieldsColumn.FIELD_NAME.getColumnName()))
                        {
                            location += "name";
                            context = columnValue[FieldsColumn.FIELD_NAME.ordinal()];
                        }
                        // Check if the match is with the field description
                        else if (hitColumnName.equals(FieldsColumn.FIELD_DESC.getColumnName()))
                        {
                            location += "description";
                            context = columnValue[FieldsColumn.FIELD_DESC.ordinal()];
                        }
                        // Check if the match is with the field size
                        else if (hitColumnName.equals(FieldsColumn.FIELD_SIZE.getColumnName()))
                        {
                            location += "size";
                            context = columnValue[FieldsColumn.FIELD_SIZE.ordinal()];
                        }
                        // Check if the match is with the field input type
                        else if (hitColumnName.equals(FieldsColumn.FIELD_TYPE.getColumnName()))
                        {
                            location += "input type";
                            context = columnValue[FieldsColumn.FIELD_TYPE.ordinal()];
                        }
                        // Check if the match is with the field
                        // applicability
                        else if (hitColumnName.equals(FieldsColumn.FIELD_APPLICABILITY.getColumnName()))
                        {
                            location += "applicability";
                            context = columnValue[FieldsColumn.FIELD_APPLICABILITY.ordinal()];
                        }
                        // Check if the match is with the field value
                        else if (hitColumnName.equals(FieldsColumn.FIELD_VALUE.getColumnName()))
                        {
                            location += "value";
                            context = columnValue[FieldsColumn.FIELD_VALUE.ordinal()];
                        }
                        // Check if the match is with the field required flag
                        else if (hitColumnName.equals(FieldsColumn.FIELD_REQUIRED.getColumnName()))
                        {
                            location += "required flag";
                            context = columnValue[FieldsColumn.FIELD_REQUIRED.ordinal()];
                        }
                    }
                    // Check if the match is in the associations internal table
                    else if (hitTableName.equals(InternalTable.ASSOCIATIONS.getTableName()))
                    {
                        target = "Script association";
                        location = "Script '"
                                   + columnValue[AssociationsColumn.SCRIPT_FILE.ordinal()]
                                   + "' association ";

                        // Check if the match is with the script file path
                        // and/or name
                        if (hitColumnName.equals(AssociationsColumn.SCRIPT_FILE.getColumnName()))
                        {
                            location += "file path and name";
                            context = columnValue[AssociationsColumn.SCRIPT_FILE.ordinal()];
                        }
                        // The match is with a script association member
                        else
                        {
                            location += "member table";
                            context = columnValue[AssociationsColumn.MEMBERS.ordinal()];
                        }
                    }
                    // Check if the match is in the telemetry scheduler
                    // internal table
                    else if (hitTableName.equals(InternalTable.TLM_SCHEDULER.getTableName()))
                    {
                        target = "Telemetry message";
                        location = "Message '"
                                   + columnValue[TlmSchedulerColumn.MESSAGE_NAME.ordinal()]
                                   + "' ";

                        // Check if the match is with the message name
                        if (hitColumnName.equals(TlmSchedulerColumn.MESSAGE_NAME.getColumnName()))
                        {
                            location += "name";
                            context = columnValue[TlmSchedulerColumn.MESSAGE_NAME.ordinal()];
                        }
                        // Check if the match is with the message rate name
                        else if (hitColumnName.equals(TlmSchedulerColumn.RATE_NAME.getColumnName()))
                        {
                            location += "rate name";
                            context = columnValue[TlmSchedulerColumn.RATE_NAME.ordinal()];
                        }
                        // Check if the match is with the message ID
                        else if (hitColumnName.equals(TlmSchedulerColumn.MESSAGE_ID.getColumnName()))
                        {
                            location += "ID";
                            context = columnValue[TlmSchedulerColumn.MESSAGE_ID.ordinal()];
                        }
                        // The match is with a message definition or member
                        else
                        {
                            context = columnValue[TlmSchedulerColumn.MEMBER.ordinal()];

                            // Check if the column begins with a number; this
                            // is the message definition
                            if (columnValue[TlmSchedulerColumn.MEMBER.ordinal()].matches("^\\d+"))
                            {
                                location += "rate and description";
                            }
                            // This is a message member
                            else
                            {
                                location += "member rate, table, and variable";
                            }
                        }
                    }
                    // Check if the match is in the links internal table
                    else if (hitTableName.equals(InternalTable.LINKS.getTableName()))
                    {
                        target = "Telemetry link";
                        location = "Link '"
                                   + columnValue[LinksColumn.LINK_NAME.ordinal()]
                                   + "' ";

                        // Check if the match is with the link name
                        if (hitColumnName.equals(LinksColumn.LINK_NAME.getColumnName()))
                        {
                            location += "name";
                            context = columnValue[LinksColumn.LINK_NAME.ordinal()];
                        }
                        // Check if the match is with the link rate name
                        else if (hitColumnName.equals(LinksColumn.RATE_NAME.getColumnName()))
                        {
                            location += "rate name";
                            context = columnValue[LinksColumn.RATE_NAME.ordinal()];
                        }
                        // The match is with a link definition or member
                        else
                        {
                            context = columnValue[LinksColumn.MEMBER.ordinal()];

                            // Check if the column begins with a number; this
                            // is the link definition
                            if (columnValue[1].matches("^\\d+"))
                            {
                                location += "rate and description";
                            }
                            // This is a link member
                            else
                            {
                                location += "member table and variable";
                            }
                        }
                    }
                    // Check if the match is in the table types internal table
                    else if (hitTableName.equals(InternalTable.TABLE_TYPES.getTableName()))
                    {
                        target = "Table type";
                        location = "Table type '"
                                   + columnValue[TableTypesColumn.TYPE_NAME.ordinal()]
                                   + "' ";

                        // Check if the match is with the column name
                        if (hitColumnName.equals(TableTypesColumn.COLUMN_NAME_VISIBLE.getColumnName()))
                        {
                            location += "column name";
                            context = columnValue[TableTypesColumn.COLUMN_NAME_VISIBLE.ordinal()];
                        }
                        // Check if the match is with the column description
                        else if (hitColumnName.equals(TableTypesColumn.COLUMN_DESCRIPTION.getColumnName()))
                        {
                            location += "column description";
                            context = columnValue[TableTypesColumn.COLUMN_DESCRIPTION.ordinal()];
                        }
                        // Check if the match is with the column input type
                        else if (hitColumnName.equals(TableTypesColumn.INPUT_TYPE.getColumnName()))
                        {
                            location += "column input type";
                            context = columnValue[TableTypesColumn.INPUT_TYPE.ordinal()];
                        }
                        // Check if the match is with the column required flag
                        else if (hitColumnName.equals(TableTypesColumn.COLUMN_REQUIRED.getColumnName()))
                        {
                            location += "column required flag";
                            context = columnValue[TableTypesColumn.COLUMN_REQUIRED.ordinal()];
                        }
                        // Check if the match is with the row value unique flag
                        else if (hitColumnName.equals(TableTypesColumn.ROW_VALUE_UNIQUE.getColumnName()))
                        {
                            location += "row value unique flag";
                            context = columnValue[TableTypesColumn.ROW_VALUE_UNIQUE.ordinal()];
                        }
                        // Match is in one of the remaining table type columns
                        else
                        {
                            // Ignore this match
                            target = null;
                        }
                    }
                    // Check if the match is in the application scheduler
                    // internal table
                    else if (hitTableName.equals(InternalTable.APP_SCHEDULER.getTableName()))
                    {
                        target = "Scheduler";
                        location = "Application '"
                                   + columnValue[AppSchedulerColumn.TIME_SLOT.ordinal()]
                                   + "' ";

                        // Check if the match is with the application name
                        if (hitColumnName.equals(AppSchedulerColumn.TIME_SLOT.getColumnName()))
                        {
                            location += "name";
                            context = columnValue[AppSchedulerColumn.TIME_SLOT.ordinal()];
                        }
                        // The match is with a scheduler member
                        else
                        {
                            context = columnValue[AppSchedulerColumn.APP_INFO.ordinal()];
                            location += "member information";
                        }
                    }
                }
                // This is a script search and the match is in a stored script
                else
                {
                    // Set the search result table values
                    target = nameAndType[0];
                    location = columnValue[ScriptColumn.LINE_NUM.ordinal()];
                    context = columnValue[ScriptColumn.LINE_TEXT.ordinal()];
                }

                // Check if a search result exists
                if (target != null)
                {
                    // Add the search result to the list
                    resultsDataList.add(new Object[] {target,
                                                      location,
                                                      context});
                }
            }
        }

        // Display the search results
        return sortSearchResults(resultsDataList);
    }

    /**************************************************************************
     * Search for occurrences of a string in the event log file (session log or
     * other log file)
     * 
     * @param searchText
     *            text string to search for in the database
     * 
     * @param ignoreCase
     *            true to ignore case when looking for matching text
     * 
     * @param targetRow
     *            row index to match if this is an event log entry search on a
     *            table that displays only a single log entry; null otherwise
     * 
     * @return Search results List containing object arrays providing each
     *         match's location in the database tables or event log, the column
     *         within the location, and an extract for the located match
     *         showing its context
     *************************************************************************/
    protected List<Object[]> searchEventLogFile(String searchText,
                                                boolean ignoreCase,
                                                Long targetRow)
    {
        Pattern pattern;

        // Initialize the list to contain the search results
        List<Object[]> resultsDataList = new ArrayList<Object[]>();

        // Check if case is to be ignored
        if (ignoreCase)
        {
            // Create the match pattern with case ignored
            pattern = Pattern.compile(Pattern.quote(searchText),
                                      Pattern.CASE_INSENSITIVE);
        }
        // Only match if the same case
        else
        {
            // Create the match pattern, preserving case
            pattern = Pattern.compile(Pattern.quote(searchText));
        }

        // Set up Charset and CharsetDecoder for ISO-8859-15
        Charset charset = Charset.forName("ISO-8859-15");
        CharsetDecoder decoder = charset.newDecoder();

        // Pattern used to detect separate lines
        Pattern linePattern = Pattern.compile(".*\r?\n");

        try
        {
            // Open a file stream on the event log file and then get a channel
            // from the stream
            FileInputStream fis = new FileInputStream(eventLog.getEventLogFile());
            FileChannel fc = fis.getChannel();

            // Get the file's size and then map it into memory
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY,
                                                 0,
                                                 fc.size());

            // Decode the file into a char buffer
            CharBuffer charBuffer = decoder.decode(byteBuffer);

            // Create the line and pattern matchers, then perform the search
            Matcher lineMatch = linePattern.matcher(charBuffer);

            long row = 1;

            // For each line in the file
            while (lineMatch.find())
            {
                // Check if no target row is provided ,or if one is that it
                // matches this log entry's row
                if (targetRow == null || row == targetRow)
                {
                    // Get the line from the file and strip any leading or
                    // trailing whitespace
                    String line = lineMatch.group().toString();

                    // Break the input line into its separate columns
                    String[] parts = line.split("[|]", EventColumns.values().length - 1);

                    // Step through each log entry column
                    for (int column = 0; column < parts.length; column++)
                    {
                        // Create the pattern matcher from the pattern. Ignore
                        // any HTML tags in the log entry column text
                        Matcher matcher = pattern.matcher(CcddUtilities.removeHTMLTags(parts[column].toString()));

                        // Check if a match exists in the text string
                        if (matcher.find())
                        {
                            // Add the search result to the list
                            resultsDataList.add(new Object[] {row,
                                                              eventLog.getEventTable().getColumnName(column + 1),
                                                              parts[column]});
                        }
                    }

                    // Check if the end of the file has been reached or if this
                    // is a single log entry row search
                    if (lineMatch.end() == charBuffer.limit()
                        || targetRow != null)
                    {
                        // Exit the loop
                        break;
                    }
                }

                row++;
            }

            // Close the channel and the stream
            fc.close();
            fis.close();
        }
        catch (IOException ioe)
        {
            // Inform the user that an error occurred reading the log
            new CcddDialogHandler().showMessageDialog(this,
                                                      "<html><b>Cannot read event log file",
                                                      "Log Error",
                                                      JOptionPane.WARNING_MESSAGE,
                                                      DialogOption.OK_OPTION);
        }

        // Display the search results
        return sortSearchResults(resultsDataList);
    }

    /**************************************************************************
     * Sort the search results by the first (target) column, and if the same
     * then by second (location) column. Array variable member references in
     * the location column are arranged by array dimension value
     * 
     * @param resultsDataList
     *            list containing the sorted search results
     *************************************************************************/
    private List<Object[]> sortSearchResults(List<Object[]> resultsDataList)
    {
        // Sort the results by target, then by location, ignoring case
        Collections.sort(resultsDataList, new Comparator<Object[]>()
        {
            /******************************************************************
             * Compare the target names of two search result rows. If the same
             * compare the locations. Move the tables to the top. Ignore case
             * when comparing
             *****************************************************************/
            @Override
            public int compare(Object[] entry1, Object[] entry2)
            {
                int result = 0;

                System.out.println(Arrays.toString(entry1) + "  " + Arrays.toString(entry2));// TODO
                switch (searchDlgType)
                {
                    case TABLES:
                    case SCRIPTS:
                        // Compare the first column as strings, ignoring case
                        result = entry1[0].toString().toLowerCase().compareTo(entry2[0].toString().toLowerCase());
                        break;

                    case LOG:
                        // Compare the first column as integers
                        result = Long.valueOf(entry1[0].toString()).compareTo(Long.valueOf(entry2[0].toString()));
                        break;
                }

                // Check if the first column values are the same
                if (result == 0)
                {
                    // Check if the second column values are both references to
                    // array variable members. The compareTo() method sorts the
                    // array members alphabetically, not numerically by array
                    // dimension (for example, a[10] will be placed immediately
                    // after a[1]). The following code sorts the array members
                    // numerically by array dimension
                    if (entry1[1].toString().matches("Column '.*', variable '.*\\]'")
                        && entry2[1].toString().matches("Column '.*', variable '.*\\]'"))
                    {
                        // Get the array variable references from the second
                        // column values
                        String arrayVariable1 = entry1[1].toString().replaceFirst("Column '.*', variable '(.*\\])'", "$1");
                        String arrayVariable2 = entry2[1].toString().replaceFirst("Column '.*', variable '(.*\\])'", "$1");

                        // Check if the variables are members of the same array
                        if (ArrayVariable.removeArrayIndex(arrayVariable1)
                                         .equals(ArrayVariable.removeArrayIndex(arrayVariable2)))
                        {
                            // Compare the two array members by dimension
                            // value(s)
                            result = ArrayVariable.compareTo(arrayVariable1, arrayVariable2);
                        }
                        // The second column values are not references to the
                        // same array variable
                        else
                        {
                            // Compare the second column, ignoring case
                            result = entry1[1].toString().toLowerCase().compareTo(entry2[1].toString().toLowerCase());
                        }
                    }
                    // The second column values are not both references to
                    // array variable members
                    else
                    {
                        // Compare the second column, ignoring case
                        result = entry1[1].toString().toLowerCase().compareTo(entry2[1].toString().toLowerCase());
                    }
                }

                return result;
            }
        });

        return resultsDataList;
    }
}
