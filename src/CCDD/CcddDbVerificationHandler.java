/**
 * CFS Command & Data Dictionary database verification handler. Copyright 2017
 * United States Government as represented by the Administrator of the National
 * Aeronautics and Space Administration. No copyright is claimed in the United
 * States under Title 17, U.S. Code. All Other Rights Reserved.
 */
package CCDD;

import static CCDD.CcddConstants.CANCEL_BUTTON;
import static CCDD.CcddConstants.CANCEL_ICON;
import static CCDD.CcddConstants.DB_SAVE_POINT_NAME;
import static CCDD.CcddConstants.INTERNAL_TABLE_PREFIX;
import static CCDD.CcddConstants.LIST_TABLE_SEPARATOR;
import static CCDD.CcddConstants.OK_BUTTON;
import static CCDD.CcddConstants.OK_ICON;
import static CCDD.CcddConstants.PRINT_ICON;
import static CCDD.CcddConstants.TLM_SCH_SEPARATOR;
import static CCDD.CcddConstants.EventLogMessageType.STATUS_MSG;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.TreePath;

import CCDD.CcddBackgroundCommand.BackgroundCommand;
import CCDD.CcddClasses.ArrayVariable;
import CCDD.CcddClasses.TableInformation;
import CCDD.CcddClasses.TableModification;
import CCDD.CcddClasses.ToolTipTreeNode;
import CCDD.CcddConstants.DefaultColumn;
import CCDD.CcddConstants.DialogOption;
import CCDD.CcddConstants.InputDataType;
import CCDD.CcddConstants.InternalTable;
import CCDD.CcddConstants.InternalTable.AssociationsColumn;
import CCDD.CcddConstants.InternalTable.FieldsColumn;
import CCDD.CcddConstants.InternalTable.GroupsColumn;
import CCDD.CcddConstants.InternalTable.LinksColumn;
import CCDD.CcddConstants.InternalTable.OrdersColumn;
import CCDD.CcddConstants.InternalTable.TlmSchedulerColumn;
import CCDD.CcddConstants.InternalTable.ValuesColumn;
import CCDD.CcddConstants.ModifiableColorInfo;
import CCDD.CcddConstants.ModifiableFontInfo;
import CCDD.CcddConstants.ModifiableSpacingInfo;
import CCDD.CcddConstants.TableCommentIndex;
import CCDD.CcddConstants.TableSelectionMode;
import CCDD.CcddConstants.TableTreeType;
import CCDD.CcddConstants.VerificationColumnInfo;
import CCDD.CcddTableTypeHandler.TypeDefinition;

/******************************************************************************
 * CFS Command & Data Dictionary database verification handler class
 *****************************************************************************/
public class CcddDbVerificationHandler
{
    // Class references
    private final CcddMain ccddMain;
    private final CcddDbControlHandler dbControl;
    private final CcddDbCommandHandler dbCommand;
    private final CcddDbTableCommandHandler dbTable;
    private final CcddEventLogDialog eventLog;
    private final CcddTableTypeHandler tableTypeHandler;
    private final CcddMacroHandler macroHandler;
    private TypeDefinition typeDefinition;
    private CcddJTableHandler updateTable;

    // Comments for all tables
    private String[][] comments;

    // List of data table information, table issues, and changes made to the
    // tables
    private List<TableStorage> tableStorage;
    private final List<TableIssue> issues;
    private List<TableChange> tableChanges;

    // Number of remaining array members expected
    private int membersRemaining;

    // Data table column indices
    private final int primaryKeyIndex;
    private final int rowIndex;
    private final int variableNameIndex;
    private final int dataTypeIndex;
    private final int arraySizeIndex;
    private final int bitLengthIndex;

    // Array member index values
    private int[] totalArraySize;
    private int[] currentArrayIndex;

    // Array definition row index
    private int definitionRow;

    // Flag indicating if changes are to be made to the tables
    private boolean isChanges;

    // Flag indicating that the user elected to cancel project database
    // verification
    private boolean canceled = false;

    /**************************************************************************
     * Table data storage class. An instance is created for each data table to
     * contain its table information and current cell values
     *************************************************************************/
    private class TableStorage
    {
        TableInformation tableInformation;
        String[][] committedData;

        /**********************************************************************
         * Table data storage class constructor
         * 
         * @param tableInformation
         *            reference to the table information
         * 
         * @param committedData
         *            array containing the table data as it exists in the
         *            project database
         *********************************************************************/
        protected TableStorage(TableInformation tableInformation,
                               String[][] committedData)
        {
            this.tableInformation = tableInformation;
            this.committedData = committedData;
        }

        /**********************************************************************
         * Get the table information
         *
         * @return table information reference
         *********************************************************************/
        protected TableInformation getTableInformation()
        {
            return tableInformation;
        }

        /**********************************************************************
         * Get the table committed data
         *
         * @return table committed data
         *********************************************************************/
        protected String[][] getCommittedData()
        {
            return committedData;
        }
    }

    /**************************************************************************
     * Table issue class. An instance is created for each issue found in a data
     * or internal table, and contains the information needed to identify and
     * correct the issue
     *************************************************************************/
    private class TableIssue
    {
        private boolean fix;
        private final String issue;
        private final String action;
        private final String command;
        private final int row;
        private final int column;
        private final String data;
        private final String[] rowData;
        private final TableInformation tableInfo;

        /**********************************************************************
         * Table issue class constructor
         *
         * @param issue
         *            description of the issue
         *
         * @param action
         *            action to be taken to correct the issue
         *
         * @param command
         *            PostgreSQL command to correct the issue; null if this is
         *            not a change to a table cell value
         *
         * @param row
         *            row index for the update
         *
         * @param column
         *            column index for the update; -1 if the update does not
         *            affect a specific column
         *
         * @param data
         *            cell value; null if a specific cell isn't updated
         *
         * @param rowData
         *            array of cell values for a table row; null if a row is to
         *            be deleted or the update does not affect an entire row
         *
         * @param tableInfo
         *            reference to the table information
         *********************************************************************/
        TableIssue(String issue,
                   String action,
                   String command,
                   int row,
                   int column,
                   String data,
                   String[] rowData,
                   TableInformation tableInfo)
        {
            this.issue = issue;
            this.action = action;
            this.command = command;
            this.row = row;
            this.column = column;
            this.data = data;
            this.rowData = rowData;
            this.tableInfo = tableInfo;
            fix = false;
        }

        /**********************************************************************
         * Table issue class constructor to issue a PostgreSQL command
         *
         * @param issue
         *            description of the issue
         *
         * @param action
         *            action to be taken to correct the issue
         *
         * @param command
         *            PostgreSQL command to correct the issue; null if this is
         *            not a change to a table cell value
         *********************************************************************/
        TableIssue(String issue,
                   String action,
                   String command)
        {
            this(issue, action, command, -1, -1, null, null, null);
        }

        /**********************************************************************
         * Table issue class constructor to update a specific table cell value
         *
         * @param issue
         *            description of the issue
         *
         * @param action
         *            action to be taken to correct the issue
         *
         * @param row
         *            row index for the update
         *
         * @param column
         *            column index for the update; -1 if the update does not
         *            affect a specific column
         *
         * @param data
         *            cell value; null if a specific cell isn't updated
         *
         * @param tableInfo
         *            reference to the table information
         *********************************************************************/
        TableIssue(String issue,
                   String action,
                   int row,
                   int column,
                   String data,
                   TableInformation tableInfo)
        {
            this(issue, action, null, row, column, data, null, tableInfo);
        }

        /**********************************************************************
         * Table issue class constructor to update a table row of data
         *
         * @param issue
         *            description of the issue
         *
         * @param action
         *            action to be taken to correct the issue
         *
         * @param row
         *            row index for the update
         *
         * @param rowData
         *            array of cell values for a table row; null if a row is to
         *            be deleted or the update does not affect an entire row
         *
         * @param tableInfo
         *            reference to the table information
         *********************************************************************/
        TableIssue(String issue,
                   String action,
                   int row,
                   String[] rowData,
                   TableInformation tableInfo)
        {
            this(issue, action, null, row, -1, null, rowData, tableInfo);
        }

        /**********************************************************************
         * Get the issue text
         *
         * @return Issue text
         *********************************************************************/
        protected String getIssue()
        {
            return issue;
        }

        /**********************************************************************
         * Get the corrective action text
         *
         * @return Corrective action text
         *********************************************************************/
        protected String getAction()
        {
            return action;
        }

        /**********************************************************************
         * Get the PostgreSQL command
         *
         * @return PostgreSQL command
         *********************************************************************/
        protected String getCommand()
        {
            return command;
        }

        /**********************************************************************
         * Get the table row index
         *
         * @return Table row index
         *********************************************************************/
        protected int getRow()
        {
            return row;
        }

        /**********************************************************************
         * Get the table column index
         *
         * @return Table column index
         *********************************************************************/
        protected int getColumn()
        {
            return column;
        }

        /**********************************************************************
         * Get the table cell value
         *
         * @return Table cell value
         *********************************************************************/
        protected String getData()
        {
            return data;
        }

        /**********************************************************************
         * Get the table row values
         *
         * @return Table row values
         *********************************************************************/
        protected String[] getRowData()
        {
            return rowData;
        }

        /**********************************************************************
         * Get the table information reference
         *
         * @return Table information reference
         *********************************************************************/
        protected TableInformation getTableInformation()
        {
            return tableInfo;
        }

        /**********************************************************************
         * Get the flag that indicates if the user elects to correct the issue
         *
         * @return Flag that indicates if the user elects to correct the issue
         *********************************************************************/
        protected boolean isFix()
        {
            return fix;
        }

        /**********************************************************************
         * Set the flag that indicates if the user elects to correct the issue
         *
         * @param fix
         *            true to correct the issue; false to ignore it
         *********************************************************************/
        protected void setFix(boolean fix)
        {
            this.fix = fix;
        }
    }

    /**************************************************************************
     * Table change class. An instance is created for each data table that has
     * one or more issues that the user elects to correct
     *************************************************************************/
    private class TableChange
    {
        private final TableInformation tableInformation;
        private final List<TableModification> additions;
        private final List<TableModification> modifications;
        private final List<TableModification> deletions;

        /**********************************************************************
         * Table change class constructor
         *
         * @param tableInformation
         *            table information reference
         *
         * @param additions
         *            list of table additions
         *
         * @param modifications
         *            list of table modifications
         *
         * @param deletions
         *            list of table deletions
         *********************************************************************/
        protected TableChange(TableInformation tableInformation,
                              List<TableModification> additions,
                              List<TableModification> modifications,
                              List<TableModification> deletions)
        {
            this.tableInformation = tableInformation;
            this.additions = additions;
            this.modifications = modifications;
            this.deletions = deletions;
        }

        /**********************************************************************
         * Get the table information
         *
         * @return table information reference
         *********************************************************************/
        protected TableInformation getTableInformation()
        {
            return tableInformation;
        }

        /**********************************************************************
         * Get the table additions
         *
         * @return List of table additions
         *********************************************************************/
        protected List<TableModification> getAdditions()
        {
            return additions;
        }

        /**********************************************************************
         * Get the table modifications
         *
         * @return List of table modifications
         *********************************************************************/
        protected List<TableModification> getModifications()
        {
            return modifications;
        }

        /**********************************************************************
         * Get the table deletions
         *
         * @return List of table deletions
         *********************************************************************/
        protected List<TableModification> getDeletions()
        {
            return deletions;
        }
    }

    /**************************************************************************
     * Database verification handler class constructor
     *
     * @param ccddMain
     *            main class
     *************************************************************************/
    CcddDbVerificationHandler(CcddMain ccddMain)
    {
        this.ccddMain = ccddMain;

        // Create references to shorten subsequent calls
        dbControl = ccddMain.getDbControlHandler();
        dbCommand = ccddMain.getDbCommandHandler();
        dbTable = ccddMain.getDbTableCommandHandler();
        eventLog = ccddMain.getSessionEventLog();
        tableTypeHandler = ccddMain.getTableTypeHandler();
        macroHandler = ccddMain.getMacroHandler();

        // Data table column indices
        primaryKeyIndex = DefaultColumn.PRIMARY_KEY.ordinal();
        rowIndex = DefaultColumn.ROW_INDEX.ordinal();
        variableNameIndex = DefaultColumn.VARIABLE_NAME.ordinal();
        dataTypeIndex = DefaultColumn.DATA_TYPE.ordinal();
        arraySizeIndex = DefaultColumn.ARRAY_SIZE.ordinal();
        bitLengthIndex = DefaultColumn.BIT_LENGTH.ordinal();

        // Initialize the database issues list
        issues = new ArrayList<TableIssue>();

        // Execute the consistency check
        verifyDatabase();
    }

    /**************************************************************************
     * Perform a consistency check of the project database. Query the user for
     * permission to make corrections, then apply the corrections at completion
     * of the check. This method is executed in a separate thread since it can
     * take a noticeable amount time to complete, and by using a separate
     * thread the GUI is allowed to continue to update. The GUI menu commands,
     * however, are disabled until the table data consistency check completes
     * execution
     *************************************************************************/
    private void verifyDatabase()
    {
        CcddBackgroundCommand.executeInBackground(ccddMain, new BackgroundCommand()
        {
            /******************************************************************
             * Verification cancellation dialog class
             *****************************************************************/
            @SuppressWarnings("serial")
            class HaltDialog extends CcddDialogHandler
            {
                /**************************************************************
                 * Handle the close dialog button action
                 *************************************************************/
                @Override
                protected void closeDialog(int button)
                {
                    // Set the flag to cancel verification
                    canceled = true;

                    super.closeDialog(button);
                };
            }

            HaltDialog cancelDialog = new HaltDialog();

            /******************************************************************
             * Perform project database verification
             *****************************************************************/
            @Override
            protected void execute()
            {
                // Set the initial layout manager characteristics
                GridBagConstraints gbc = new GridBagConstraints(0,
                                                                0,
                                                                1,
                                                                1,
                                                                1.0,
                                                                0.0,
                                                                GridBagConstraints.LINE_START,
                                                                GridBagConstraints.BOTH,
                                                                new Insets(ModifiableSpacingInfo.LABEL_VERTICAL_SPACING.getSpacing() / 2,
                                                                           ModifiableSpacingInfo.LABEL_HORIZONTAL_SPACING.getSpacing(),
                                                                           0,
                                                                           ModifiableSpacingInfo.LABEL_HORIZONTAL_SPACING.getSpacing()),
                                                                0,
                                                                0);

                // Create the cancellation dialog
                JPanel dialogPnl = new JPanel(new GridBagLayout());
                dialogPnl.setBorder(BorderFactory.createEmptyBorder());
                JLabel textLbl = new JLabel("<html><b>Verification in progress...<br><br>",
                                            SwingConstants.LEFT);
                textLbl.setFont(ModifiableFontInfo.LABEL_PLAIN.getFont());
                gbc.gridy++;
                dialogPnl.add(textLbl, gbc);
                JLabel textLbl2 = new JLabel("<html><b>"
                                             + CcddUtilities.colorHTMLText("*** Press </i>Halt<i> "
                                                                           + "to terminate verification ***",
                                                                           Color.RED)
                                             + "<br><br>",
                                             SwingConstants.CENTER);
                textLbl2.setFont(ModifiableFontInfo.LABEL_PLAIN.getFont());
                gbc.gridy++;
                dialogPnl.add(textLbl2, gbc);

                // Add a progress bar to the dialog
                JProgressBar progBar = new JProgressBar(0, 5);
                progBar.setValue(0);
                progBar.setStringPainted(true);
                progBar.setPreferredSize(new Dimension(100, 20));
                gbc.insets.left = ModifiableSpacingInfo.LABEL_HORIZONTAL_SPACING.getSpacing() * 2;
                gbc.insets.right = ModifiableSpacingInfo.LABEL_HORIZONTAL_SPACING.getSpacing() * 2;
                gbc.insets.bottom = 0;
                gbc.gridy++;
                dialogPnl.add(progBar, gbc);

                // Display he verification cancellation dialog
                cancelDialog.showOptionsDialog(ccddMain.getMainFrame(),
                                               dialogPnl,
                                               "Verifying Project",
                                               DialogOption.HALT_OPTION,
                                               false,
                                               false);

                // Set flags indicating no changes are pending, no
                // inconsistencies exist, and the user hasn't canceled the
                // check
                isChanges = false;

                try
                {
                    // Get the comments for all data tables
                    comments = dbTable.queryDataTableComments(ccddMain.getMainFrame());

                    // Get metadata for all tables
                    ResultSet tableResult = dbCommand.getConnection().getMetaData().getTables(null,
                                                                                              null,
                                                                                              null,
                                                                                              new String[] {"TABLE"});

                    // Check if verification isn't canceled
                    if (!canceled)
                    {
                        // Check for inconsistencies in the owner role of the
                        // project database and its tables, sequences, indices,
                        // and functions
                        verifyOwners();

                        // Check if verification isn't canceled
                        if (!canceled)
                        {
                            // Update the progress bar
                            progBar.setValue(1);

                            // Check for inconsistencies in the internal tables
                            verifyInternalTables(tableResult);

                            // Check if verification isn't canceled
                            if (!canceled)
                            {
                                // Update the progress bar
                                progBar.setValue(2);

                                // Verify the table and variable path
                                // references in the internal tables
                                verifyPathReferences(tableResult);

                                // Check if verification isn't canceled
                                if (!canceled)
                                {
                                    // Update the progress bar
                                    progBar.setValue(3);

                                    // Check for inconsistencies between the
                                    // table type definitions and the tables of
                                    // that type
                                    verifyTableTypes(tableResult);

                                    // Check if verification isn't canceled
                                    if (!canceled)
                                    {
                                        // Update the progress bar
                                        progBar.setValue(4);

                                        // Check for inconsistencies within the
                                        // data tables
                                        verifyDataTables();

                                        // Update the progress bar
                                        progBar.setValue(5);
                                    }
                                }
                            }
                        }
                    }
                }
                catch (SQLException se)
                {
                    // Inform the user that obtaining the project database
                    // metadata failed
                    eventLog.logFailEvent(ccddMain.getMainFrame(),
                                          "Error obtaining project database '"
                                              + dbControl.getDatabase()
                                              + "' metadata; cause '"
                                              + se.getMessage()
                                              + "'",
                                          "<html><b>Error obtaining project database '"
                                              + dbControl.getDatabase()
                                              + "'metadata");
                }
                catch (Exception e)
                {
                    // Display a dialog providing details on the unanticipated
                    // error
                    CcddUtilities.displayException(e, ccddMain.getMainFrame());
                }
            }

            /******************************************************************
             * Project database verification command complete
             *****************************************************************/
            @Override
            protected void complete()
            {
                // Check if the user didn't cancel verification
                if (!canceled)
                {
                    // Close the cancellation dialog
                    cancelDialog.closeDialog();

                    // Perform any corrections to the database authorized by
                    // the user
                    updateDatabase();
                }
                // Verification was canceled
                else
                {
                    // Note that verification was canceled in the event log
                    eventLog.logEvent(STATUS_MSG, "Verification terminated by user");
                }
            }
        });
    }

    /**************************************************************************
     * Check that the owner role matches for the project database and its
     * tables, sequences, indices, and functions. A database administrator is
     * required in order to alter the owner role(s) to match the database
     * owner; this must be done outside the application. If any mismatch is
     * detected then the issue is logged for information purposes only
     *************************************************************************/
    private void verifyOwners()
    {
        try
        {
            // Check if the owner differs for the project database and its
            // tables, sequences, or indices
            ResultSet mismatch = dbCommand.executeDbQuery("SELECT name, type, "
                                                          + "pg_catalog.pg_get_userbyid(tbl.relowner) AS "
                                                          + "owner, pg_catalog.pg_get_userbyid(db.datdba) "
                                                          + "AS db_ownwer FROM (SELECT datdba FROM "
                                                          + "pg_catalog.pg_database d WHERE d.datname = "
                                                          + "(SELECT current_database())) AS db, (SELECT "
                                                          + "relname AS name, relkind AS type, relowner "
                                                          + "FROM pg_class cls JOIN pg_namespace nsp ON "
                                                          + "cls.relnamespace = nsp.oid WHERE nsp.nspname "
                                                          + "= 'public') AS tbl WHERE db.datdba != tbl.relowner",
                                                          ccddMain.getMainFrame());

            // Step through each component with a mismatched owner
            while (mismatch.next())
            {
                // Check if the user canceled verification
                if (canceled)
                {
                    break;
                }

                String type;

                // Get the component type
                switch (mismatch.getString(2))
                {
                    case "r":
                        type = "table";
                        break;

                    case "i":
                        type = "index";
                        break;

                    case "S":
                        type = "sequence";
                        break;

                    case "v":
                        type = "view";
                        break;

                    case "c":
                        type = "composite type";
                        break;

                    default:
                        type = "unknown component";
                        break;
                }

                // Component owner differs from the database owner
                issues.add(new TableIssue("Owner for "
                                          + type
                                          + " "
                                          + mismatch.getString(1)
                                          + " ("
                                          + mismatch.getString(3)
                                          + ") does not match project database owner ("
                                          + mismatch.getString(4)
                                          + ")",
                                          "Database administrator must change "
                                              + type
                                              + " "
                                              + mismatch.getString(1)
                                              + " owner to match project database owner",
                                          null));
            }

            mismatch.close();

            // Check if the owner differs for the project database and its
            // functions
            mismatch = dbCommand.executeDbQuery("SELECT name, "
                                                + "pg_catalog.pg_get_userbyid(func.proowner) AS "
                                                + "owner, pg_catalog.pg_get_userbyid(db.datdba) "
                                                + "AS db_ownwer FROM (SELECT datdba FROM "
                                                + "pg_catalog.pg_database d WHERE d.datname = "
                                                + "(SELECT current_database())) AS db, (SELECT "
                                                + "prc.proname || '(' || "
                                                + "pg_get_function_identity_arguments(prc.oid) "
                                                + "|| ')' AS name, proowner FROM pg_proc prc "
                                                + "JOIN pg_namespace nsp ON prc.pronamespace = "
                                                + "nsp.oid WHERE nsp.nspname = 'public') AS func "
                                                + "WHERE db.datdba != func.proowner;",
                                                ccddMain.getMainFrame());

            // Step through each function with a mismatched owner
            while (mismatch.next())
            {
                // Function owner differs from database owner
                issues.add(new TableIssue("Owner for function "
                                          + mismatch.getString(1)
                                          + " ("
                                          + mismatch.getString(2)
                                          + ") does not match project database owner ("
                                          + mismatch.getString(3)
                                          + ")",
                                          "Database administrator must change function "
                                              + mismatch.getString(1)
                                              + " owner to match project database owner",
                                          null));
            }

            mismatch.close();
        }
        catch (SQLException se)
        {
            // Inform the user that checking the table consistency failed
            eventLog.logFailEvent(ccddMain.getMainFrame(),
                                  "Error verifying project database '"
                                      + dbControl.getDatabase()
                                      + "'consistency; cause '"
                                      + se.getMessage()
                                      + "'",
                                  "<html><b>Error verifying project database '"
                                      + dbControl.getDatabase()
                                      + "'consistency");
        }
    }

    /**************************************************************************
     * Check that the internal tables are consistent with their definitions. If
     * any inconsistencies are detected then get user approval to alter the
     * table(s)
     * 
     * @param tableResult
     *            metadata for all tables
     *************************************************************************/
    private void verifyInternalTables(ResultSet tableResult)
    {
        String tableNameDb = "";

        try
        {
            // Go to the first row in the result set
            tableResult.first();

            // Step through each database table
            while (tableResult.next())
            {
                // Check if the user canceled verification
                if (canceled)
                {
                    break;
                }

                // Get the table name
                tableNameDb = tableResult.getString("TABLE_NAME");

                // Check if this is an internal table other than a script file
                if (tableNameDb.startsWith(INTERNAL_TABLE_PREFIX)
                    && !tableNameDb.startsWith(InternalTable.SCRIPT.getTableName()))
                {
                    boolean isFound = false;

                    // Step through each internal table type
                    for (InternalTable intTable : InternalTable.values())
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Check if the table name matches that of a known
                        // internal table
                        if (tableNameDb.equals(intTable.getTableName()))
                        {
                            // Get the table's column metadata
                            ResultSet columnResult = dbCommand.getConnection().getMetaData().getColumns(null,
                                                                                                        null,
                                                                                                        tableNameDb,
                                                                                                        null);

                            columnResult.last();

                            // Check if the table is one or more missing
                            // columns
                            if (columnResult.getRow() < intTable.getNumColumns())
                            {
                                // Table has too few columns
                                issues.add(new TableIssue("Internal table '"
                                                          + tableNameDb
                                                          + "' is missing one or more columns",
                                                          "Delete table",
                                                          "DROP TABLE "
                                                              + tableNameDb
                                                              + "; "
                                                              + dbControl.buildInformationTableCommand(intTable)
                                                              + " "));
                            }
                            // The number of columns is correct
                            else
                            {
                                // Reset the result set pointer
                                columnResult.beforeFirst();

                                int columnIndex = 0;

                                // Step through each column in the table
                                while (columnResult.next())
                                {
                                    // Check if the user canceled verification
                                    if (canceled)
                                    {
                                        break;
                                    }

                                    // Check if the table has more columns than
                                    // expected
                                    if (columnIndex == intTable.getNumColumns())
                                    {
                                        // Table has too many columns
                                        issues.add(new TableIssue("Internal table '"
                                                                  + tableNameDb
                                                                  + "' has too many columns",
                                                                  "Delete extra column(s)",
                                                                  "ALTER TABLE "
                                                                      + tableNameDb
                                                                      + " DROP COLUMN "
                                                                      + columnResult.getString("COLUMN_NAME")
                                                                      + "; "));

                                        // Stop checking this table
                                        break;
                                    }

                                    // Get the column name and data type
                                    String columnName = columnResult.getString("COLUMN_NAME");
                                    String columnType = columnResult.getString("TYPE_NAME");

                                    String expectedName = intTable.getColumnName(columnIndex);
                                    String expectedType = intTable.getColumnType(columnIndex);

                                    // Check if the column names don't match
                                    if (!columnName.equals(expectedName))
                                    {
                                        // Column name is unknown
                                        issues.add(new TableIssue("Internal table '"
                                                                  + tableNameDb
                                                                  + "' column "
                                                                  + columnIndex
                                                                  + " name mismatch (expected: '"
                                                                  + expectedName
                                                                  + "', actual: '"
                                                                  + columnName
                                                                  + "')",
                                                                  "Rename column",
                                                                  "ALTER TABLE "
                                                                      + tableNameDb
                                                                      + " RENAME COLUMN "
                                                                      + columnName
                                                                      + " TO "
                                                                      + expectedName
                                                                      + "; "));
                                    }

                                    // Check if the data type of the column in
                                    // the database matches the data type in
                                    // the type definition. Only the first
                                    // three letters are compared since the
                                    // data type representation can change in
                                    // the database
                                    if (!columnType.startsWith(expectedType.substring(0, 3)))
                                    {
                                        // Column's type is incorrect
                                        issues.add(new TableIssue("Internal table '"
                                                                  + tableNameDb
                                                                  + "' column '"
                                                                  + columnName
                                                                  + "' type mismatch (expected: '"
                                                                  + expectedType
                                                                  + "', actual: '"
                                                                  + columnType
                                                                  + "')",
                                                                  "Modify table type",
                                                                  "ALTER TABLE "
                                                                      + tableNameDb
                                                                      + " ALTER COLUMN "
                                                                      + columnName
                                                                      + " TYPE "
                                                                      + expectedType
                                                                      + " USING "
                                                                      + columnName
                                                                      + "::"
                                                                      + expectedType
                                                                      + "; "));
                                    }

                                    columnIndex++;
                                }
                            }

                            isFound = true;
                        }
                    }

                    // Check if the table name doesn't match a known one
                    if (!isFound)
                    {
                        // Internal table name doesn't match one of the
                        // expected ones
                        issues.add(new TableIssue("Unknown internal table '"
                                                  + tableNameDb
                                                  + "'",
                                                  "Delete table",
                                                  "DROP TABLE " + tableNameDb + "; "));
                    }
                }
            }
        }
        catch (SQLException se)
        {
            // Inform the user that obtaining the internal table metadata
            // failed
            eventLog.logFailEvent(ccddMain.getMainFrame(),
                                  "Error obtaining metadata for internal table '"
                                      + tableNameDb
                                      + "'; cause '"
                                      + se.getMessage()
                                      + "'",
                                  "<html><b>Error obtaining metadata for internal table '</b>"
                                      + tableNameDb
                                      + "<b>'");
        }
    }

    /**************************************************************************
     * Check that the references to tables and variables in the internal tables
     * are valid. If any invalid entries are detected then get user approval to
     * alter the table(s)
     * 
     * @param tableResult
     *            metadata for all tables
     *************************************************************************/
    private void verifyPathReferences(ResultSet tableResult)
    {
        String tableNameDb = "";

        try
        {
            // Get the list of table and variable paths and names, retaining
            // any macros and bit lengths
            CcddVariableConversionHandler variableHandler = new CcddVariableConversionHandler(ccddMain,
                                                                                              TableTreeType.TABLES_WITH_PRIMITIVES);

            // List to contain invalid table or variable references. This is
            // used to prevent logging multiple issues for the same
            // table/variable in the same internal table
            List<String> badRefs = new ArrayList<String>();

            // Go to the first row in the result set
            tableResult.first();

            // Step through each database table
            while (tableResult.next())
            {
                // Get the table name
                tableNameDb = tableResult.getString("TABLE_NAME");

                // Check if this is the script associations table
                if (tableNameDb.equals(InternalTable.ASSOCIATIONS.getTableName()))
                {
                    // Step through the associated tables
                    for (String[] member : getInternalTableMembers(tableNameDb,
                                                                   AssociationsColumn.MEMBERS.getColumnName(),
                                                                   AssociationsColumn.SCRIPT_FILE.getColumnName()))
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Check if a this association references a table . If
                        // no table is associated the member column contains a
                        // space
                        if (!member[0].trim().isEmpty())
                        {
                            // Step through each table in the association
                            for (String table : member[0].split(Pattern.quote(LIST_TABLE_SEPARATOR)))
                            {
                                // Check if the table doesn't exist
                                if (!variableHandler.getAllVariableNameList().contains(table))
                                {
                                    // Association table reference is invalid
                                    issues.add(new TableIssue("Internal table '"
                                                              + tableNameDb
                                                              + "' references a non-existent table, '"
                                                              + table
                                                              + "', associated with script '"
                                                              + member[1]
                                                              + "'",
                                                              "Delete script association",
                                                              "DELETE FROM "
                                                                  + tableNameDb
                                                                  + " WHERE "
                                                                  + AssociationsColumn.SCRIPT_FILE.getColumnName()
                                                                  + " = "
                                                                  + dbTable.delimitText(member[1])
                                                                  + " AND "
                                                                  + AssociationsColumn.MEMBERS.getColumnName()
                                                                  + " = "
                                                                  + dbTable.delimitText(member[0])
                                                                  + "; "));

                                    // Skip any other invalid table references
                                    // in this association; this prevents
                                    // allowing the user to select removal of
                                    // the same association more than once
                                    break;
                                }
                            }
                        }
                    }
                }
                // Check if this is the data fields table
                else if (tableNameDb.equals(InternalTable.FIELDS.getTableName()))
                {
                    // Clear any entries created while checking other tables
                    badRefs.clear();

                    // Step through the field owners
                    for (String[] member : getInternalTableMembers(tableNameDb,
                                                                   FieldsColumn.OWNER_NAME.getColumnName(),
                                                                   null))
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Check if this data field doesn't belong to a group
                        // or table type, the table hasn't already been
                        // detected, and if the table isn't in the list of
                        // valid names
                        if (!member[0].matches("^.*:.*")
                            && !badRefs.contains(member[0])
                            && !variableHandler.getAllVariableNameList().contains(member[0]))
                        {
                            // Data field table owner reference is invalid
                            issues.add(new TableIssue("Internal table '"
                                                      + tableNameDb
                                                      + "' references a non-existent table, '"
                                                      + member[0]
                                                      + "'",
                                                      "Delete table's data field(s)",
                                                      "DELETE FROM "
                                                          + tableNameDb
                                                          + " WHERE "
                                                          + FieldsColumn.OWNER_NAME.getColumnName()
                                                          + " = "
                                                          + dbTable.delimitText(member[0])
                                                          + "; "));

                            // Add the invalid entry to the bad reference list
                            // so that any other references to it (for other
                            // columns) aren't logged as duplicate issues
                            badRefs.add(member[0]);
                        }
                    }
                }
                // Check if this is the groups table
                else if (tableNameDb.equals(InternalTable.GROUPS.getTableName()))
                {
                    // Clear any entries created while checking other tables
                    badRefs.clear();

                    // Step through the group tables
                    for (String[] member : getInternalTableMembers(tableNameDb,
                                                                   GroupsColumn.MEMBERS.getColumnName(),
                                                                   null))
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Check if this isn't a group definition entry, the
                        // table hasn't already been detected, and if the table
                        // isn't in the list of valid names
                        if (!member[0].matches("^\\d+.*")
                            && !badRefs.contains(member[0])
                            && !variableHandler.getAllVariableNameList().contains(member[0]))
                        {
                            // Group table member reference is invalid
                            issues.add(new TableIssue("Internal table '"
                                                      + tableNameDb
                                                      + "' references a non-existent table, '"
                                                      + member[0]
                                                      + "'",
                                                      "Delete table from group",
                                                      "DELETE FROM "
                                                          + tableNameDb
                                                          + " WHERE "
                                                          + GroupsColumn.MEMBERS.getColumnName()
                                                          + " = "
                                                          + dbTable.delimitText(member[0])
                                                          + "; "));

                            // Add the invalid entry to the bad reference list
                            // so that any other references to it (for other
                            // columns) aren't logged as duplicate issues
                            badRefs.add(member[0]);
                        }
                    }
                }
                // Check if this is the links table
                else if (tableNameDb.equals(InternalTable.LINKS.getTableName()))
                {
                    // Clear any entries created while checking other tables
                    badRefs.clear();

                    // Step through the link variables
                    for (String[] member : getInternalTableMembers(tableNameDb,
                                                                   LinksColumn.MEMBER.getColumnName(),
                                                                   null))
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Check if this isn't a link definition entry, hasn't
                        // already been detected, and if it isn't in the list
                        // of valid names
                        if (!member[0].matches("^\\d+.*")
                            && !badRefs.contains(member[0])
                            && !variableHandler.getAllVariableNameList().contains(member[0]))
                        {
                            // Link variable member reference is invalid
                            issues.add(new TableIssue("Internal table '"
                                                      + tableNameDb
                                                      + "' references a non-existent variable, '"
                                                      + member[0]
                                                      + "'",
                                                      "Delete variable from link",
                                                      "DELETE FROM "
                                                          + tableNameDb
                                                          + " WHERE "
                                                          + LinksColumn.MEMBER.getColumnName()
                                                          + " = "
                                                          + dbTable.delimitText(member[0])
                                                          + "; "));

                            // Add the invalid entry to the bad reference list
                            // so that any other references to it (for other
                            // columns) aren't logged as duplicate issues
                            badRefs.add(member[0]);
                        }
                    }
                }
                // Check if this is the telemetry scheduler table
                else if (tableNameDb.equals(InternalTable.TLM_SCHEDULER.getTableName()))
                {
                    // Clear any entries created while checking other tables
                    badRefs.clear();

                    // Step through each variable in the telemetry table
                    for (String[] member : getInternalTableMembers(tableNameDb,
                                                                   TlmSchedulerColumn.MEMBER.getColumnName(),
                                                                   null))
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Check if the variable isn't blank (i.e., an empty
                        // message), hasn't already been detected, and if it
                        // isn't in the list of valid names
                        if (!member[0].isEmpty()
                            && !badRefs.contains(member[0])
                            && !variableHandler.getAllVariableNameList().contains(member[0].replaceFirst(InputDataType.FLOAT_POSITIVE.getInputMatch()
                                                                                                         + Pattern.quote(TLM_SCH_SEPARATOR),
                                                                                                         "")))
                        {
                            // Telemetry scheduler message variable member
                            // reference is invalid
                            issues.add(new TableIssue("Internal table '"
                                                      + tableNameDb
                                                      + "' references a non-existent variable, '"
                                                      + member[0].replaceFirst(".*"
                                                                               + Pattern.quote(TLM_SCH_SEPARATOR),
                                                                               "")
                                                      + "'",
                                                      "Delete variable from message(s)",
                                                      "DELETE FROM "
                                                          + tableNameDb
                                                          + " WHERE "
                                                          + TlmSchedulerColumn.MEMBER.getColumnName()
                                                          + " = "
                                                          + dbTable.delimitText(member[0])
                                                          + "; "));

                            // Add the invalid entry to the bad reference list
                            // so that any other references to it (for other
                            // columns) aren't logged as duplicate issues
                            badRefs.add(member[0]);
                        }
                    }
                }
                // Check if this is the custom values table
                else if (tableNameDb.equals(InternalTable.VALUES.getTableName()))
                {
                    // Clear any entries created while checking other tables
                    badRefs.clear();

                    // List to contain the variables without bit lengths and to
                    // include array definitions. A separate list is created to
                    // speed the comparisons
                    List<String> cleanName = new ArrayList<String>();

                    // Step through each variable in the list
                    for (String variablePath : variableHandler.getAllVariableNameList())
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Remove the bit length, if present, and store the
                        // variable in the new list
                        cleanName.add(variablePath.replaceFirst("\\:\\d+$", ""));

                        // Check if the variable is an array member
                        if (variablePath.endsWith("]"))
                        {
                            // Strip the array index form the end to create a
                            // reference to the variable's array definition
                            String name = variablePath.substring(0,
                                                                 variablePath.lastIndexOf("["));

                            // Check if this array definition isn't already in
                            // the new list
                            if (!cleanName.contains(name))
                            {
                                // Add the array definition to the list
                                cleanName.add(name);
                            }
                        }
                    }

                    // Step through the custom values variables
                    for (String[] member : getInternalTableMembers(tableNameDb,
                                                                   ValuesColumn.TABLE_PATH.getColumnName(),
                                                                   null))
                    {
                        // Check if the user canceled verification
                        if (canceled)
                        {
                            break;
                        }

                        // Check if the variable hasn't already been detected
                        // and if it isn't in the list of valid names
                        if (!badRefs.contains(member[0])
                            && !cleanName.contains(member[0]))
                        {
                            // Custom values variable member reference is
                            // invalid
                            issues.add(new TableIssue("Internal table '"
                                                      + tableNameDb
                                                      + "' references a non-existent variable, '"
                                                      + member[0]
                                                      + "'",
                                                      "Delete variable reference",
                                                      "DELETE FROM "
                                                          + tableNameDb
                                                          + " WHERE "
                                                          + ValuesColumn.TABLE_PATH.getColumnName()
                                                          + " = "
                                                          + dbTable.delimitText(member[0])
                                                          + "; "));

                            // Add the invalid entry to the bad reference list
                            // so that any other references to it (for other
                            // columns) aren't logged as duplicate issues
                            badRefs.add(member[0]);
                        }
                    }
                }
            }
        }
        catch (SQLException se)
        {
            // Inform the user that obtaining the internal table metadata
            // failed
            eventLog.logFailEvent(ccddMain.getMainFrame(),
                                  "Error obtaining metadata for internal table '"
                                      + tableNameDb
                                      + "'; cause '"
                                      + se.getMessage()
                                      + "'",
                                  "<html><b>Error obtaining metadata for internal table '</b>"
                                      + tableNameDb
                                      + "<b>'");
        }
    }

    /**************************************************************************
     * Query the specified internal table in the database for the specified
     * column entries that reference tables or variables
     * 
     * @param intTableName
     *            internal table name to query
     * 
     * @param intTableColumnA
     *            name of the column in the internal table column name from
     *            which to obtain the entries
     * 
     * @param intTableColumnB
     *            name of the second column in the internal table column name
     *            from which to obtain the entries; null if no second column is
     *            requested
     * 
     * @return List of table or variable entries from the specified column in
     *         the specified internal table
     *************************************************************************/
    private List<String[]> getInternalTableMembers(String intTableName,
                                                   String intTableColumnA,
                                                   String intTableColumnB)
    {
        // Get the entries from the specified column in the specified table
        List<String[]> members = dbTable.queryDatabase("SELECT "
                                                       + intTableColumnA
                                                       + (intTableColumnB != null
                                                                                 ? ", "
                                                                                   + intTableColumnB
                                                                                 : "")
                                                       + " FROM "
                                                       + intTableName,
                                                       ccddMain.getMainFrame());

        // Check if an error occurred obtaining the entries
        if (members == null)
        {
            // Return an empty list; the error is logged by the query method
            members = new ArrayList<String[]>();
        }

        return members;
    }

    /**************************************************************************
     * Check that the tables are consistent with their type definitions. If any
     * inconsistencies are detected then get user approval to alter the
     * table(s)
     * 
     * @param tableResult
     *            metadata for all tables
     *************************************************************************/
    private void verifyTableTypes(ResultSet tableResult)
    {
        String tableNameDb = "";

        try
        {
            // Get the column order information from the column order table
            List<String[]> orders = dbTable.retrieveInformationTable(InternalTable.ORDERS,
                                                                     ccddMain.getMainFrame());

            // Go to the first row in the result set
            tableResult.first();

            // Step through each database table
            while (tableResult.next())
            {
                // Check if the user canceled verification
                if (canceled)
                {
                    break;
                }

                // Get the table name
                tableNameDb = tableResult.getString("TABLE_NAME");

                // Check if this is a data table
                if (!tableNameDb.startsWith(INTERNAL_TABLE_PREFIX))
                {
                    String tableName = "";

                    // Get the comment array for this table
                    String[] comment = dbTable.getTableComment(tableNameDb,
                                                               comments);

                    // Get the table name as seen by the user
                    tableName = comment[TableCommentIndex.NAME.ordinal()];

                    // Get the table's type definition
                    TypeDefinition typeDefinition = tableTypeHandler.getTypeDefinition(comment[TableCommentIndex.TYPE.ordinal()]);

                    // Check if the type definition is defined in the table
                    // type definitions
                    if (typeDefinition != null)
                    {
                        // Step through each table in the column order table
                        for (int index = 0; index < orders.size(); index++)
                        {
                            // Check if the user canceled verification
                            if (canceled)
                            {
                                break;
                            }

                            // Check if the number of columns indicated in the
                            // column order table doesn't match the number of
                            // columns for this table's type
                            if (orders.get(index)[OrdersColumn.TABLE_PATH.ordinal()].matches("^"
                                                                                             + tableName
                                                                                             + "(,|$)")
                                && orders.get(index)[OrdersColumn.COLUMN_ORDER.ordinal()].split(":").length != typeDefinition.getColumnCountDatabase())
                            {
                                // Column order table has an invalid entry for
                                // this table
                                issues.add(new TableIssue("Incorrect number of columns indicated for table '"
                                                          + orders.get(index)[OrdersColumn.TABLE_PATH.ordinal()]
                                                          + "' in the column order table for user '"
                                                          + orders.get(index)[OrdersColumn.USER_NAME.ordinal()]
                                                          + "'",
                                                          "Modify column order table",
                                                          "UPDATE "
                                                              + InternalTable.ORDERS.getTableName()
                                                              + " SET "
                                                              + OrdersColumn.COLUMN_ORDER.getColumnName()
                                                              + " = '"
                                                              + tableTypeHandler.getDefaultColumnOrder(typeDefinition.getName())
                                                              + "' WHERE "
                                                              + OrdersColumn.USER_NAME.getColumnName()
                                                              + " = '"
                                                              + orders.get(index)[OrdersColumn.USER_NAME.ordinal()]
                                                              + "' AND "
                                                              + OrdersColumn.TABLE_PATH.getColumnName()
                                                              + " = '"
                                                              + orders.get(index)[OrdersColumn.TABLE_PATH.ordinal()]
                                                              + "'; "));
                            }
                        }

                        // Get the table's column metadata
                        ResultSet columnResult = dbCommand.getConnection().getMetaData().getColumns(null,
                                                                                                    null,
                                                                                                    tableNameDb,
                                                                                                    null);

                        // Create storage for flags indicating which columns
                        // have been matched
                        boolean[] isFound = new boolean[typeDefinition.getColumnCountDatabase()];

                        // Step through each column in the table
                        while (columnResult.next())
                        {
                            // Check if the user canceled verification
                            if (canceled)
                            {
                                break;
                            }

                            // Get the column name and data type
                            String columnName = columnResult.getString("COLUMN_NAME");
                            String columnType = columnResult.getString("TYPE_NAME");

                            // Get the index of the column based on its name
                            int columnIndex = typeDefinition.getColumnIndexByDbName(columnName);

                            // Check if the column name is defined in the type
                            // definition
                            if (columnIndex != -1)
                            {
                                // Check if the data type of the column in the
                                // database matches the data type in the type
                                // definition. Only the first three letters are
                                // compared since the data type representation
                                // can change in the database
                                if (!columnType.startsWith(DefaultColumn.getColumnDbType(columnIndex).substring(0, 3)))
                                {
                                    // Column's data type is incorrect
                                    issues.add(new TableIssue("Table '"
                                                              + tableName
                                                              + "' column '"
                                                              + columnName
                                                              + "' data type is invalid ("
                                                              + columnType
                                                              + ")",
                                                              "Modify data type",
                                                              "ALTER TABLE "
                                                                  + tableNameDb
                                                                  + " ALTER COLUMN "
                                                                  + columnName
                                                                  + " TYPE "
                                                                  + DefaultColumn.getColumnDbType(columnIndex)
                                                                  + "; "));
                                }

                                // Set the flag to indicate the column exists
                                // for this table
                                isFound[columnIndex] = true;
                            }
                            // The column in the database table is not defined
                            // in the type definition
                            else
                            {
                                // Column name is unknown
                                issues.add(new TableIssue("Table '"
                                                          + tableName
                                                          + "' has an unknown column ("
                                                          + columnName
                                                          + ")",
                                                          "Delete column",
                                                          "ALTER TABLE "
                                                              + tableNameDb
                                                              + " DROP COLUMN "
                                                              + columnName
                                                              + "; "));
                            }
                        }

                        columnResult.close();

                        // Step through the column found flags
                        for (int index = 0; index < isFound.length; index++)
                        {
                            // Check if the user canceled verification
                            if (canceled)
                            {
                                break;
                            }

                            // Check if the column wasn't located in the table
                            if (!isFound[index])
                            {
                                // Column is missing
                                issues.add(new TableIssue("Table '"
                                                          + tableName
                                                          + "' is missing column '"
                                                          + typeDefinition.getColumnNamesUser()[index]
                                                          + "'",
                                                          "Add missing column",
                                                          "ALTER TABLE "
                                                              + tableNameDb
                                                              + " ADD COLUMN "
                                                              + typeDefinition.getColumnNamesDatabase()[index]
                                                              + " "
                                                              + DefaultColumn.getColumnDbType(index)
                                                              + " DEFAULT ''; "));
                            }
                        }
                    }
                    // The table type definition is unknown
                    else
                    {
                        // Table type is unknown
                        issues.add(new TableIssue("Table '"
                                                  + tableName
                                                  + "' is an unknown type ("
                                                  + comment[TableCommentIndex.TYPE.ordinal()]
                                                  + ")",
                                                  "Delete table",
                                                  "DROP TABLE " + tableNameDb + "; "));
                    }
                }
            }

            tableResult.close();
        }
        catch (SQLException se)
        {
            // Inform the user that obtaining the table metadata failed
            eventLog.logFailEvent(ccddMain.getMainFrame(),
                                  "Error obtaining metadata for table '"
                                      + tableNameDb
                                      + "'; cause '"
                                      + se.getMessage()
                                      + "'",
                                  "<html><b>Error obtaining metadata for table '</b>"
                                      + tableNameDb
                                      + "<b>'");
        }
    }

    /**************************************************************************
     * Check that the tables' data are consistent with their type definitions.
     * If any inconsistencies are detected then get user approval to alter the
     * table(s)
     *************************************************************************/
    private void verifyDataTables()
    {
        // Build the table tree
        CcddTableTreeHandler tableTree = new CcddTableTreeHandler(ccddMain,
                                                                  TableTreeType.PROTOTYPE_TABLES,
                                                                  ccddMain.getMainFrame());

        // Get the list of root structure tables
        List<String> rootStructure = dbTable.getRootStructures(ccddMain.getMainFrame());

        // Initialize the storage for each table's information and committed
        // data
        tableStorage = new ArrayList<TableStorage>();

        // Step through the root node's children
        for (Enumeration<?> element = tableTree.getRootNode().preorderEnumeration(); element.hasMoreElements();)
        {
            // Check if the user canceled verification
            if (canceled)
            {
                break;
            }

            // Get the referenced node and the path to the node
            ToolTipTreeNode tableNode = (ToolTipTreeNode) element.nextElement();
            TreePath path = new TreePath(tableNode.getPath());

            // Check if the path references a table
            if (path.getPathCount() > tableTree.getHeaderNodeLevel())
            {
                // Get the information from the database for the specified
                // table
                TableInformation tableInfo = dbTable.loadTableData(tableTree.getFullVariablePath(path.getPath()),
                                                                   rootStructure.contains(path.getLastPathComponent().toString()),
                                                                   false,
                                                                   false,
                                                                   false,
                                                                   ccddMain.getMainFrame());

                // Check if the table loaded successfully and that the table
                // has data
                if (!tableInfo.isErrorFlag() && tableInfo.getData().length > 0)
                {
                    // Create storage for the table data as it exists in the
                    // database
                    String[][] committedData = new String[tableInfo.getData().length][tableInfo.getData()[0].length];

                    // Step through each row in the table
                    for (int row = 0; row < tableInfo.getData().length && !canceled; row++)
                    {
                        // Step through each column in the table
                        for (int column = 0; column < tableInfo.getData()[0].length && !canceled; column++)
                        {
                            // Store the table value into the committed storage
                            // array
                            committedData[row][column] = tableInfo.getData()[row][column];
                        }
                    }

                    // Add the table information and data to the list
                    tableStorage.add(new TableStorage(tableInfo, committedData));

                    // Get the table's type definition
                    typeDefinition = tableTypeHandler.getTypeDefinition(tableInfo.getType());

                    // Initialize the array check parameters: array data type,
                    // name, number of members, array dimension sizes, and
                    // current index position
                    String dataType = "";
                    String arrayName = "";
                    membersRemaining = 0;
                    totalArraySize = new int[0];
                    currentArrayIndex = new int[0];

                    // Initialize the array definition row index
                    definitionRow = 0;

                    // Step through each row in the table
                    for (int row = 0; row < tableInfo.getData().length && !canceled; row++)
                    {
                        // Step through each column in the table
                        for (int column = 0; column < tableInfo.getData()[row].length && !canceled; column++)
                        {
                            // Check if the cell value doesn't match the cell's
                            // input type
                            checkInputType(tableInfo, row, column);
                        }

                        // Check if this is a structure table and that the
                        // array size isn't blank
                        if (typeDefinition.isStructure()
                            && tableInfo.getData()[row][arraySizeIndex] != null
                            && !tableInfo.getData()[row][arraySizeIndex].isEmpty())
                        {
                            // Check if this is the first pass through the
                            // array; an array definition is expected
                            if (membersRemaining == 0)
                            {
                                // Get the variable name for this row
                                arrayName = tableInfo.getData()[row][variableNameIndex];

                                // Store the index of the array definition row
                                definitionRow = row;

                                // Check that no extra array member exists
                                if (!checkExcessArrayMember(tableInfo,
                                                            row,
                                                            arrayName))
                                {
                                    // Get the number of array members
                                    // remaining and data type for this row and
                                    // initialize the array index
                                    totalArraySize = ArrayVariable.getArrayIndexFromSize(macroHandler.getMacroExpansion(tableInfo.getData()[row][arraySizeIndex]));

                                    // Get the total number of members for this
                                    // array
                                    membersRemaining = ArrayVariable.getNumMembersFromArrayDimension(totalArraySize);

                                    // Initialize the current array index
                                    // values
                                    currentArrayIndex = new int[totalArraySize.length];

                                    // Get the data type
                                    dataType = tableInfo.getData()[row][dataTypeIndex];

                                    // Check if the expected array definition
                                    // is missing
                                    if (checkForArrayDefinition(tableInfo,
                                                                row,
                                                                arrayName))
                                    {
                                        // Remove the array index from the
                                        // array variable name and back up a
                                        // row so that the array members can be
                                        // checked
                                        arrayName = ArrayVariable.removeArrayIndex(arrayName);
                                        row--;
                                    }
                                }
                            }
                            // This is not the first pass through this array;
                            // i.e., an array member is expected
                            else
                            {
                                // Check if the array definition and all of its
                                // members don't have the same variable name
                                if (checkArrayNamesMatch(tableInfo,
                                                         row,
                                                         arrayName))
                                {
                                    // Back up a row so that it can be checked
                                    // as a separate variable
                                    row--;
                                }
                                // The array names match
                                else
                                {
                                    // Check if the array definition and all of
                                    // its members have the same array size
                                    checkArraySizesMatch(tableInfo,
                                                         row,
                                                         arrayName,
                                                         tableInfo.getData()[row][arraySizeIndex]);

                                    // Check if the array definition and all of
                                    // its members have the same data type
                                    checkDataTypesMatch(tableInfo,
                                                        row,
                                                        arrayName,
                                                        dataType);
                                }

                                // Update the array member counters
                                membersRemaining--;

                                // Update the current array index value(s)
                                goToNextArrayMember();
                            }
                        }
                        // Check if there are remaining array members that
                        // don't exist
                        else
                        {
                            // Check if an array member is expected but not
                            // present
                            checkForMissingArrayMember(tableInfo,
                                                       row,
                                                       arrayName);
                        }
                    }

                    // Perform for each remaining missing array member
                    while (membersRemaining != 0)
                    {
                        // Check if there are remaining array members that
                        // don't exist
                        checkForMissingArrayMember(tableInfo,
                                                   tableInfo.getData().length,
                                                   arrayName);
                    }

                    // Check if the flag to make changes is not already set
                    if (!isChanges)
                    {
                        // Check if a row is missing based on the row indices
                        checkForRowIndexMismatch(tableInfo);
                    }

                    // Check if columns marked as unique contain duplicate
                    // values
                    checkForDuplicates(tableInfo);
                }
            }
        }
    }

    /**************************************************************************
     * Increment the current array index value(s) to the next array member
     *************************************************************************/
    private void goToNextArrayMember()
    {
        // Step through the array index values, starting with the last and
        // working backward
        for (int index = currentArrayIndex.length - 1; index >= 0; index--)
        {
            // Increment the array index
            currentArrayIndex[index]++;

            // Check if the array index hasn't reached its limit
            if (currentArrayIndex[index] < totalArraySize[index])
            {
                // Stop adjusting the array index values
                break;
            }

            // Reset this array index back to zero and continue looping to
            // adjust the next array index up the chain
            currentArrayIndex[index] = 0;
        }
    }

    /**************************************************************************
     * Check if the cell value matches the expected input type
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param row
     *            row index
     *
     * @param column
     *            column index
     *************************************************************************/
    private void checkInputType(TableInformation tableInfo,
                                int row,
                                int column)
    {
        // Get the cell value
        String data = macroHandler.getMacroExpansion(tableInfo.getData()[row][column]);

        // Check if the cell is not an array member variable name and if the
        // value doesn't match the input type expected for this column
        if (data != null
            && !data.isEmpty()
            && !(column == variableNameIndex
            && ArrayVariable.isArrayMember(data))
            && !data.matches(typeDefinition.getInputTypes()[column].getInputMatch()))
        {
            // Value doesn't match the input type specified in the type
            // definition
            issues.add(new TableIssue("Table '"
                                      + tableInfo.getProtoVariableName()
                                      + "' row "
                                      + row
                                      + " column '"
                                      + typeDefinition.getColumnNamesUser()[column]
                                      + "' type mismatch",
                                      "Replace with a blank",
                                      row,
                                      column,
                                      "",
                                      tableInfo));
        }
    }

    /**************************************************************************
     * Check if an extra array member exists
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param row
     *            row index
     *
     * @param arrayName
     *            array variable name
     *
     * @return true if an extra array member is detected
     *************************************************************************/
    private boolean checkExcessArrayMember(TableInformation tableInfo,
                                           int row,
                                           String arrayName)
    {
        boolean isGoToNextRow = false;

        // Check if this isn't the first row
        if (row != 0)
        {
            // Get the variable name from the preceding row
            String previousName = tableInfo.getData()[row - 1][variableNameIndex];

            // Check if the current and previous rows contain array members
            if (ArrayVariable.isArrayMember(arrayName)
                && ArrayVariable.isArrayMember(previousName))
            {
                // Remove the array index from the variable names
                arrayName = ArrayVariable.removeArrayIndex(arrayName);
                previousName = ArrayVariable.removeArrayIndex(previousName);

                // Check if the variable names match
                if (arrayName.equals(previousName))
                {
                    // Array has an extra member
                    issues.add(new TableIssue("Table '"
                                              + tableInfo.getProtoVariableName()
                                              + "' variable '"
                                              + arrayName
                                              + "' has an extra array member",
                                              "Remove extra member",
                                              row,
                                              null,
                                              tableInfo));

                    // Set the flag indicating that no further checks are to be
                    // made for this row
                    isGoToNextRow = true;
                }
            }
        }

        return isGoToNextRow;
    }

    /**************************************************************************
     * Check if the expected array definition exists
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param row
     *            row index
     *
     * @param arrayName
     *            array variable name
     * 
     * @return true if the array definition is missing
     *************************************************************************/
    private boolean checkForArrayDefinition(TableInformation tableInfo,
                                            int row,
                                            String arrayName)
    {
        boolean isMissing = false;

        // Check if an array member is found instead of the expected array
        // definition
        if (ArrayVariable.isArrayMember(arrayName))
        {
            // Get the expected variable name for the array definition
            arrayName = ArrayVariable.removeArrayIndex(arrayName);

            // Array definition is missing
            issues.add(new TableIssue("Table '"
                                      + tableInfo.getProtoVariableName()
                                      + "' variable '"
                                      + arrayName
                                      + "' is missing the array definition",
                                      "Add missing array definition",
                                      row,
                                      addMissingArrayRow(tableInfo,
                                                         arrayName,
                                                         new int[0]),
                                      tableInfo));

            // Set the flag indicating the array definition is missing
            isMissing = true;
        }

        return isMissing;
    }

    /**************************************************************************
     * Check if the variable name is an array member, but it doesn't match the
     * array definition and its members. This implies that one or more array
     * members are missing as well as the next array variable's definition
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param row
     *            row index
     *
     * @param arrayName
     *            array variable name
     *
     * @return true if an an array name mismatch is detected
     *************************************************************************/
    private boolean checkArrayNamesMatch(TableInformation tableInfo,
                                         int row,
                                         String arrayName)
    {
        boolean isMismatch = false;

        // Build the array index string for the expected array member
        String expectedArrayIndex = ArrayVariable.formatArrayIndex(currentArrayIndex);

        // Check if the variable name doesn't match the expected array member
        // name
        if (!tableInfo.getData()[row][variableNameIndex].matches(Pattern.quote(arrayName
                                                                               + expectedArrayIndex)))
        {
            // Expected array member is missing
            issues.add(new TableIssue("Table '"
                                      + tableInfo.getProtoVariableName()
                                      + "' variable '"
                                      + arrayName
                                      + "' is missing array member "
                                      + expectedArrayIndex,
                                      "Add missing array member",
                                      row,
                                      addMissingArrayRow(tableInfo,
                                                         arrayName,
                                                         currentArrayIndex),
                                      tableInfo));

            isMismatch = true;
        }

        return isMismatch;
    }

    /**************************************************************************
     * Check if the array size is the same for the array definition and all of
     * it members
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param row
     *            row index
     *
     * @param arrayName
     *            array variable name
     *
     * @param arraySize
     *            number of members in the array
     *************************************************************************/
    private void checkArraySizesMatch(TableInformation tableInfo,
                                      int row,
                                      String arrayName,
                                      String arraySize)
    {
        // Check if the member's array size doesn't match the array definition
        if (!arraySize.equals(tableInfo.getData()[row][arraySizeIndex]))
        {
            // Array size doesn't match the array definition
            issues.add(new TableIssue("Table '"
                                      + tableInfo.getProtoVariableName()
                                      + "' variable '"
                                      + arrayName
                                      + "' array member "
                                      + ArrayVariable.formatArrayIndex(currentArrayIndex)
                                      + " array size doesn't match the array definition "
                                      + tableInfo.getData()[row][arraySizeIndex],
                                      "Change array size",
                                      row,
                                      arraySizeIndex,
                                      arraySize,
                                      tableInfo));
        }
    }

    /**************************************************************************
     * Check if the data type is the same for the array definition and all of
     * it members
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param row
     *            row index
     *
     * @param arrayName
     *            array variable name
     *
     * @param data
     *            type array data type
     *************************************************************************/
    private void checkDataTypesMatch(TableInformation tableInfo,
                                     int row,
                                     String arrayName,
                                     String dataType)
    {
        // Check if the member's array size doesn't match the array definition
        if (!dataType.equals(tableInfo.getData()[row][dataTypeIndex]))
        {
            // Data type doesn't match the array definition
            issues.add(new TableIssue("Table '"
                                      + tableInfo.getProtoVariableName()
                                      + "' variable '"
                                      + arrayName
                                      + "' array member "
                                      + ArrayVariable.formatArrayIndex(currentArrayIndex)
                                      + " data type doesn't match the array definition",
                                      "Change data type",
                                      row,
                                      dataTypeIndex,
                                      dataType,
                                      tableInfo));
        }
    }

    /**************************************************************************
     * Check if the array doesn't have an expected member
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param row
     *            row index
     *
     * @param arrayName
     *            array variable name
     *************************************************************************/
    private void checkForMissingArrayMember(TableInformation tableInfo,
                                            int row,
                                            String arrayName)
    {
        // Check if there are remaining array members that don't exist
        if (membersRemaining != 0)
        {
            // Expected array member is missing
            issues.add(new TableIssue("Table '"
                                      + tableInfo.getProtoVariableName()
                                      + "' variable '"
                                      + (ArrayVariable.isArrayMember(arrayName)
                                                                               ? ArrayVariable.removeArrayIndex(arrayName)
                                                                               : arrayName)
                                      + "' is missing array member "
                                      + ArrayVariable.formatArrayIndex(currentArrayIndex),
                                      "Add missing array member",
                                      row,
                                      addMissingArrayRow(tableInfo,
                                                         arrayName,
                                                         currentArrayIndex),
                                      tableInfo));

            // Update the array member counter
            membersRemaining--;

            // Update the current array index value(s)
            goToNextArrayMember();
        }
    }

    /**************************************************************************
     * Check if a row index doesn't match the expected value
     * 
     * @param tableInfo
     *            reference to the table information
     *************************************************************************/
    private void checkForRowIndexMismatch(TableInformation tableInfo)
    {
        // Step through each row in the table
        for (int row = 0; row < tableInfo.getData().length; row++)
        {
            // Check if the row index doesn't match the next consecutive row
            // number
            if (!tableInfo.getData()[row][rowIndex].equals(String.valueOf(row + 1)))
            {
                // Row index mismatch
                issues.add(new TableIssue("Table '"
                                          + tableInfo.getProtoVariableName()
                                          + "' row "
                                          + (row + 1)
                                          + " index mismatch",
                                          "Update row index",
                                          row,
                                          rowIndex,
                                          String.valueOf(row + 1),
                                          tableInfo));

                // Stop checking the row indices
                break;
            }
        }
    }

    /**************************************************************************
     * Check if a column marked as unique for this table type has duplicate
     * values in one or more rows
     *
     * @param tableInfo
     *            reference to the table information
     *************************************************************************/
    private void checkForDuplicates(TableInformation tableInfo)
    {
        // Get the comment array for this table
        String[] comment = dbTable.getTableComment(tableInfo.getProtoVariableName().toLowerCase(),
                                                   comments);

        // Get the table's type definition
        TypeDefinition typeDefinition = tableTypeHandler.getTypeDefinition(comment[TableCommentIndex.TYPE.ordinal()]);

        // Step through each column in the table
        for (int column = 0; column < tableInfo.getData()[0].length; column++)
        {
            // Check if the values in this column must be unique
            if (typeDefinition.isRowValueUnique()[column])
            {
                // Step through each row in the table
                for (int row = 0; row < tableInfo.getData().length - 1; row++)
                {
                    // Step through the remaining rows in the table
                    for (int otherRow = row + 1; otherRow < tableInfo.getData().length; otherRow++)
                    {
                        // Check if the values in the columns for these two
                        // rows match and that the values aren't blank
                        if (!tableInfo.getData()[row][column].isEmpty()
                            && macroHandler.getMacroExpansion(tableInfo.getData()[row][column])
                                           .equals(macroHandler.getMacroExpansion(tableInfo.getData()[otherRow][column])))
                        {
                            // Duplicate item exists in a column designated as
                            // having unique values
                            issues.add(new TableIssue("Table '"
                                                      + tableInfo.getProtoVariableName()
                                                      + "' column '"
                                                      + typeDefinition.getColumnNamesUser()[column]
                                                      + "' rows "
                                                      + (row + 1)
                                                      + " and "
                                                      + (otherRow + 1)
                                                      + " have duplicate values",
                                                      "Replace with a blank",
                                                      otherRow,
                                                      column,
                                                      "",
                                                      tableInfo));

                            // Stop checking the row indices
                            break;
                        }
                    }
                }
            }
        }
    }

    /**************************************************************************
     * Add a missing array definition or member
     *
     * @param tableInfo
     *            reference to the table information
     *
     * @param arrayName
     *            array variable name
     *
     * @param arrayMemberIndex
     *            current array member index
     *
     * @return Array containing the new array row data
     *************************************************************************/
    private String[] addMissingArrayRow(TableInformation tableInfo,
                                        String arrayName,
                                        int[] arrayMemberIndex)
    {
        // Check if this row represents an array member
        if (arrayMemberIndex.length != 0)
        {
            // Build the variable name for the expected array member
            arrayName = arrayName
                        + ArrayVariable.formatArrayIndex(arrayMemberIndex);
        }

        // Create an empty row
        String[] arrayRow = new String[tableInfo.getData()[definitionRow].length];
        Arrays.fill(arrayRow, "");

        // Initialize the primary key and row index for the new row, and
        // replace the variable name with the expected array name
        arrayRow[primaryKeyIndex] = "";
        arrayRow[rowIndex] = "";
        arrayRow[variableNameIndex] = arrayName;
        arrayRow[dataTypeIndex] = tableInfo.getData()[definitionRow][dataTypeIndex];
        arrayRow[arraySizeIndex] = tableInfo.getData()[definitionRow][arraySizeIndex];

        // Set the flag indicating a table changed
        isChanges = true;

        return arrayRow;
    }

    /**************************************************************************
     * Compare the current table data to the committed table data and create
     * lists of the changed values necessary to update the table in the
     * database to match the current values
     * 
     * @param tblStrg
     *            reference to the table information and committed data
     *************************************************************************/
    private void buildUpdates(TableStorage tblStrg)
    {
        // Create storage for any changes
        List<TableModification> additions = new ArrayList<TableModification>();
        List<TableModification> modifications = new ArrayList<TableModification>();
        List<TableModification> deletions = new ArrayList<TableModification>();

        // Step through each row in the table
        for (int row = 0; row < tblStrg.getTableInformation().getData().length; row++)
        {
            // Reindex the rows in case any have been added, moved, or deleted
            tblStrg.getTableInformation().getData()[row][rowIndex] = String.valueOf(row + 1);
        }

        // Create storage for the array used to indicate if a row has a match
        // between the current and committed data
        boolean[] rowFound = new boolean[tblStrg.getCommittedData().length];

        // Step through each row in the table
        for (int tblRow = 0; tblRow < tblStrg.getTableInformation().getData().length; tblRow++)
        {
            boolean matchFound = false;

            // Step through each row in the committed version of the table data
            for (int comRow = 0; comRow < tblStrg.getCommittedData().length && !matchFound; comRow++)
            {
                // Check if the primary key values match for these rows
                if (tblStrg.getTableInformation().getData()[tblRow][primaryKeyIndex].equals(tblStrg.getCommittedData()[comRow][primaryKeyIndex]))
                {
                    // Set the flags indicating this row has a match
                    rowFound[comRow] = true;
                    matchFound = true;

                    boolean isChangedColumn = false;

                    // Step through each column in the row
                    for (int column = 0; column < tblStrg.getTableInformation().getData()[tblRow].length; column++)
                    {
                        // Check if the current and committed values don't
                        // match
                        if (tblStrg.getCommittedData()[comRow][column] == null
                            || !macroHandler.getMacroExpansion(tblStrg.getTableInformation().getData()[tblRow][column]).equals(macroHandler.getMacroExpansion(tblStrg.getCommittedData()[comRow][column])))
                        {
                            // Set the flag to indicate a column value changed
                            // and stop searching
                            isChangedColumn = true;
                            break;
                        }
                    }

                    // Check if any columns were changed
                    if (isChangedColumn)
                    {
                        // Store the row modification information
                        modifications.add(new TableModification(tblStrg.getTableInformation().getData()[tblRow],
                                                                tblStrg.getCommittedData()[comRow],
                                                                variableNameIndex,
                                                                dataTypeIndex,
                                                                arraySizeIndex,
                                                                bitLengthIndex,
                                                                new ArrayList<Integer>()));
                    }
                }
            }

            // Check if no matching row was found with the current data
            if (!matchFound)
            {
                // Store the row addition information
                additions.add(new TableModification(tblStrg.getTableInformation().getData()[tblRow],
                                                    variableNameIndex,
                                                    dataTypeIndex,
                                                    arraySizeIndex,
                                                    bitLengthIndex));
            }
        }

        // Step through each row of the committed data
        for (int comRow = 0; comRow < tblStrg.getCommittedData().length; comRow++)
        {
            // Check if no matching row was found with the current data
            if (!rowFound[comRow])
            {
                // Store the row deletion information
                deletions.add(new TableModification(tblStrg.getCommittedData()[comRow],
                                                    variableNameIndex,
                                                    dataTypeIndex,
                                                    arraySizeIndex,
                                                    bitLengthIndex));
            }
        }

        // Check if any updates need to be made
        if (!additions.isEmpty()
            || !modifications.isEmpty()
            || !deletions.isEmpty())
        {
            // Store the updates to the change list
            tableChanges.add(new TableChange(tblStrg.getTableInformation(),
                                             additions,
                                             modifications,
                                             deletions));
        }
    }

    /**************************************************************************
     * Perform the corrections to the database authorized by the user
     *************************************************************************/
    @SuppressWarnings("serial")
    private void updateDatabase()
    {
        // Initialize the event log status message
        String message = "No project database inconsistencies detected";

        // Check if any issues exist
        if (!issues.isEmpty())
        {
            // Set the initial layout manager characteristics
            GridBagConstraints gbc = new GridBagConstraints(0,
                                                            0,
                                                            1,
                                                            1,
                                                            1.0,
                                                            0.0,
                                                            GridBagConstraints.LINE_START,
                                                            GridBagConstraints.BOTH,
                                                            new Insets(ModifiableSpacingInfo.LABEL_VERTICAL_SPACING.getSpacing(),
                                                                       0,
                                                                       0,
                                                                       0),
                                                            ModifiableSpacingInfo.LABEL_HORIZONTAL_SPACING.getSpacing(),
                                                            0);

            // Create the confirmation dialog
            final CcddDialogHandler dialog = new CcddDialogHandler();

            // Create an empty border
            Border emptyBorder = BorderFactory.createEmptyBorder();

            // Create the dialog's upper panel
            JPanel dialogPnl = new JPanel(new GridBagLayout());
            dialogPnl.setBorder(emptyBorder);

            // Add the text label and issues table scroll pane to the dialog
            // panel
            JLabel correctLbl = new JLabel(issues.size()
                                           + " issue(s) detected; select issue(s) to correct");
            correctLbl.setFont(ModifiableFontInfo.LABEL_BOLD.getFont());
            gbc.gridy++;
            dialogPnl.add(correctLbl, gbc);

            // Create the table to display the database inconsistencies
            updateTable = new CcddJTableHandler()
            {
                /**************************************************************
                 * Allows resizing of the issue and corrective action columns
                 *************************************************************/
                @Override
                protected boolean isColumnResizable(int column)
                {
                    return column == VerificationColumnInfo.ISSUE.ordinal()
                           || column == VerificationColumnInfo.ACTION.ordinal();
                }

                /**************************************************************
                 * Allow multiple line display in the issue and corrective
                 * action columns
                 *************************************************************/
                @Override
                protected boolean isColumnMultiLine(int column)
                {
                    return column == VerificationColumnInfo.ISSUE.ordinal()
                           || column == VerificationColumnInfo.ACTION.ordinal();
                }

                /**************************************************************
                 * Allow the fix column to be edited (enables toggling of the
                 * check box via the mouse) unless there is no command to
                 * correct to the issue (i.e., the issue must be corrected
                 * outside the application)
                 *************************************************************/
                @Override
                public boolean isCellEditable(int row, int column)
                {
                    return column == VerificationColumnInfo.FIX.ordinal()
                           && (issues.get(row).getCommand() != null
                           || issues.get(row).getRow() != -1);
                }

                /**************************************************************
                 * Load the verification data into the table and format the
                 * table cells
                 *************************************************************/
                @Override
                protected void loadAndFormatData()
                {
                    // Create an array to store the database issues
                    Object[][] tableData = new Object[issues.size()][VerificationColumnInfo.values().length];
                    int row = 0;

                    // Step through each detected issue
                    for (TableIssue issue : issues)
                    {
                        // Add the issue data to the array
                        tableData[row][VerificationColumnInfo.FIX.ordinal()] = issue.isFix();
                        tableData[row][VerificationColumnInfo.ISSUE.ordinal()] = issue.getIssue();
                        tableData[row][VerificationColumnInfo.ACTION.ordinal()] = issue.getAction();
                        row++;
                    }

                    // Place the data into the table model along with the
                    // column names, set up the editors and renderers for the
                    // table cells, set up the table grid lines, and calculate
                    // the minimum width required to display the table
                    // information
                    setUpdatableCharacteristics(tableData,
                                                VerificationColumnInfo.getColumnNames(),
                                                null,
                                                null,
                                                new Integer[] {VerificationColumnInfo.FIX.ordinal()},
                                                VerificationColumnInfo.getToolTips(),
                                                true,
                                                true,
                                                true,
                                                true);
                }
            };

            // Place the table into a scroll pane
            JScrollPane scrollPane = new JScrollPane(updateTable);

            // Set up the field table parameters
            updateTable.setFixedCharacteristics(scrollPane,
                                                false,
                                                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
                                                TableSelectionMode.SELECT_BY_CELL,
                                                true,
                                                ModifiableColorInfo.TABLE_BACK.getColor(),
                                                false,
                                                true,
                                                ModifiableFontInfo.OTHER_TABLE_CELL.getFont(),
                                                true);

            // Define the panel to contain the table
            JPanel updateTblPnl = new JPanel();
            updateTblPnl.setLayout(new BoxLayout(updateTblPnl, BoxLayout.X_AXIS));
            updateTblPnl.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            updateTblPnl.add(scrollPane);

            // Add the table to the dialog
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.gridx = 0;
            gbc.gridy++;
            dialogPnl.add(updateTblPnl, gbc);

            // Create a check box for selecting/deselecting all updates
            final JCheckBox selectAllCb = new JCheckBox("Select all");
            selectAllCb.setFont(ModifiableFontInfo.LABEL_BOLD.getFont());
            selectAllCb.setBorder(emptyBorder);

            // Create a listener for changes in selection of 'select all' check
            // box
            selectAllCb.addActionListener(new ActionListener()
            {
                /**************************************************************
                 * Handle a change to the 'select all' check box selection
                 *************************************************************/
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    // Step through each row in the updates table
                    for (int row = 0; row < updateTable.getRowCount(); row++)
                    {
                        // Check if the check box column's cell is editable;
                        // i.e., the application can fix the issue described on
                        // this row
                        if (updateTable.isCellEditable(row, VerificationColumnInfo.FIX.ordinal()))
                        {
                            // Enable/disable the update based on the 'select
                            // all' check box state
                            updateTable.setValueAt(selectAllCb.isSelected(),
                                                   row,
                                                   VerificationColumnInfo.FIX.ordinal());
                        }
                    }
                }
            });

            gbc.insets.bottom = 0;
            gbc.weighty = 0.0;
            gbc.gridy++;
            dialogPnl.add(selectAllCb, gbc);

            // Update the selected inconsistencies button
            JButton btnOk = CcddButtonPanelHandler.createButton("Okay",
                                                                OK_ICON,
                                                                KeyEvent.VK_O,
                                                                "Correct the inconsistencies");

            // Add a listener for the Okay button
            btnOk.addActionListener(new ActionListener()
            {
                /**************************************************************
                 * Update the inconsistencies
                 *************************************************************/
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    dialog.closeDialog(OK_BUTTON);
                }
            });

            // Print inconsistencies button
            JButton btnPrint = CcddButtonPanelHandler.createButton("Print",
                                                                   PRINT_ICON,
                                                                   KeyEvent.VK_P,
                                                                   "Print the inconsistency list");

            // Add a listener for the Print button
            btnPrint.addActionListener(new ActionListener()
            {
                /**************************************************************
                 * Print the inconsistency table
                 *************************************************************/
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    updateTable.printTable("Project '"
                                           + dbControl.getDatabase()
                                           + " Inconsistencies", null,
                                           dialog,
                                           PageFormat.LANDSCAPE);
                }
            });

            // Cancel inconsistency update button
            JButton btnCancel = CcddButtonPanelHandler.createButton("Cancel",
                                                                    CANCEL_ICON,
                                                                    KeyEvent.VK_C,
                                                                    "Cancel inconsistency corrections");

            // Add a listener for the Cancel button
            btnCancel.addActionListener(new ActionListener()
            {
                /**************************************************************
                 * Cancel updating the inconsistencies
                 *************************************************************/
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    dialog.closeDialog(CANCEL_BUTTON);
                }
            });

            // Create a panel for the dialog buttons and add the buttons to the
            // panel
            JPanel buttonPnl = new JPanel();
            buttonPnl.setBorder(emptyBorder);
            buttonPnl.add(btnOk);
            buttonPnl.add(btnPrint);
            buttonPnl.add(btnCancel);

            boolean isAllIgnored = true;

            // Check if any changes are queued and that the user confirms
            // performing the updates
            if (dialog.showOptionsDialog(ccddMain.getMainFrame(),
                                         dialogPnl,
                                         buttonPnl,
                                         btnOk,
                                         "Perform Corrections",
                                         true) == OK_BUTTON)
            {
                try
                {
                    String command = "";
                    int row = 0;
                    boolean isSomeIgnored = false;

                    // Enable creation of a save point in case an error occurs
                    // while modifying a table. This prevents committing the
                    // changes to the database until after all tables are
                    // modified
                    dbCommand.setSavePointEnable(true);

                    // Step through each issue detected
                    for (TableIssue issue : issues)
                    {
                        // Update the issue's flag indicating if it should be
                        // fixed
                        issue.setFix(Boolean.valueOf(updateTable.getTableData(false)[row][VerificationColumnInfo.FIX.ordinal()].toString()));

                        // Check if the issue is not flagged to be fixed
                        if (!issue.isFix())
                        {
                            isSomeIgnored = true;
                        }
                        // Check if the PostgreSQL command has been assigned
                        else if (issue.getCommand() != null)
                        {
                            // Add the command to fix the issue to the command
                            // string
                            command += issue.getCommand();
                        }

                        row++;
                    }

                    // Check if any updates are approved by the user
                    if (!command.isEmpty())
                    {
                        isAllIgnored = false;

                        // Make the changes to the table(s) in the database
                        dbCommand.executeDbCommand(command,
                                                   ccddMain.getMainFrame());
                    }

                    boolean isErrors = false;

                    // Row index adjustment. An adjustment of a row to be
                    // inserted/deleted must be made based on the user's
                    // selected issues to update
                    int rowAdjust = 0;

                    // Name of the previously updated table
                    String previousTable = "";

                    // Step through each issue detected
                    for (TableIssue issue : issues)
                    {
                        // Check if the issue is flagged to be fixed and that a
                        // row is set. This indicates that the change affects
                        // the data value(s) within a table
                        if (issue.isFix() && issue.getRow() != -1)
                        {
                            // Check if the name of the table to be updated
                            // differs from the previously updated table
                            if (!previousTable.equals(issue.getTableInformation().getProtoVariableName()))
                            {
                                // Reset the row index adjustment
                                rowAdjust = 0;
                            }

                            // Store the name of the table for the next loop
                            previousTable = issue.getTableInformation().getProtoVariableName();

                            // Check if a column is specified. This indicates a
                            // specific row and column value is changed
                            if (issue.getColumn() != -1)
                            {
                                // Update the cell value
                                issue.getTableInformation().getData()[issue.getRow()][issue.getColumn()] = issue.getData();
                            }
                            // Check if the row data is specified. This
                            // indicates an entire row is added
                            else if (issue.getRowData() != null)
                            {
                                // Insert the row into the existing table data
                                List<String[]> tableData = new ArrayList<String[]>(Arrays.asList(issue.getTableInformation().getData()));
                                tableData.add(issue.getRow() + rowAdjust, issue.getRowData());
                                issue.getTableInformation().setData(tableData.toArray(new String[0][0]));
                                rowAdjust++;
                            }
                            // An entire row is deleted
                            else
                            {
                                // Remove the row from the existing table data
                                List<String[]> tableData = new ArrayList<String[]>(Arrays.asList(issue.getTableInformation().getData()));
                                tableData.remove(issue.getRow() + rowAdjust);
                                issue.getTableInformation().setData(tableData.toArray(new String[0][0]));
                                rowAdjust--;
                            }
                        }
                    }

                    // Initialize the storage for any table changes
                    tableChanges = new ArrayList<TableChange>();

                    // Step through each table's information
                    for (TableStorage tblStrg : tableStorage)
                    {
                        // Build the updates, if any, for the table and add the
                        // updates to the table changes list
                        buildUpdates(tblStrg);
                    }

                    // Check if there are any changes
                    if (!tableChanges.isEmpty())
                    {
                        isAllIgnored = false;

                        // Step through each table's changes
                        for (TableChange tableChange : tableChanges)
                        {
                            // Modify the table
                            if (dbTable.modifyTableData(tableChange.getTableInformation(),
                                                        tableChange.getAdditions(),
                                                        tableChange.getModifications(),
                                                        tableChange.getDeletions(),
                                                        true,
                                                        false,
                                                        false,
                                                        false,
                                                        false,
                                                        null,
                                                        null,
                                                        ccddMain.getMainFrame()))
                            {
                                // Set the flag indicating an error occurred
                                // updating one or more tables, and stop
                                // modifying
                                isErrors = true;
                                break;
                            }
                        }
                    }

                    // Check if an errors occurred when making the updates
                    if (!isErrors)
                    {
                        // Commit the change(s) to the database
                        dbCommand.getConnection().commit();

                        // Log that the table update(s) succeeded
                        message = "One or more project database inconsistencies were detected and corrected";

                        if (isSomeIgnored)
                        {
                            message += "; some issues ignored";
                        }
                    }
                    // An error occurred
                    else
                    {
                        // Log that the table update(s) did not succeed
                        message = "One or more project database inconsistencies were "
                                  + "detected, but an error occurred while updating";

                        if (isSomeIgnored)
                        {
                            message += "; some issues ignored";
                        }
                    }
                }
                catch (SQLException se)
                {
                    try
                    {
                        // Inform the user that checking the table consistency
                        // failed
                        eventLog.logFailEvent(ccddMain.getMainFrame(),
                                              "Error verifying project database '"
                                                  + dbControl.getDatabase()
                                                  + "' consistency; cause '"
                                                  + se.getMessage()
                                                  + "'",
                                              "<html><b>Error verifying project database '"
                                                  + dbControl.getDatabase()
                                                  + "' consistency");

                        // Log that the table update(s) did not succeed
                        message = "One or more project database inconsistencies were "
                                  + "detected, but an error occurred while updating";

                        // Revert the changes to the tables that were
                        // successfully updated prior the current table
                        dbCommand.executeDbCommand("ROLLBACK TO SAVEPOINT "
                                                   + DB_SAVE_POINT_NAME
                                                   + ";",
                                                   dialog);
                    }
                    catch (SQLException se2)
                    {
                        // Inform the user that the reversion to the save point
                        // failed
                        eventLog.logFailEvent(dialog,
                                              "Cannot revert changes to table(s); cause '"
                                                  + se.getMessage()
                                                  + "'",
                                              "<html><b>Cannot revert changes to table(s)");
                    }
                }
                finally
                {
                    // Reset the flag for creating a save point
                    dbCommand.setSavePointEnable(false);
                }
            }

            // Unresolved inconsistencies exist
            if (isAllIgnored)
            {
                // Create the log message indicating that inconsistencies were
                // detected, but none were corrected
                message = "Project database inconsistencies were detected, but ignored";
            }
        }

        // Log the consistency check completion message
        eventLog.logEvent(STATUS_MSG, message);
    }
}
