/**
 * CFS Command & Data Dictionary scheduler editor handler. Copyright 2017
 * United States Government as represented by the Administrator of the National
 * Aeronautics and Space Administration. No copyright is claimed in the United
 * States under Title 17, U.S. Code. All Other Rights Reserved.
 */
package CCDD;

import static CCDD.CcddConstants.CELL_FONT;
import static CCDD.CcddConstants.FOCUS_COLOR;
import static CCDD.CcddConstants.INVALID_TEXT_COLOR;
import static CCDD.CcddConstants.LABEL_FONT_BOLD;
import static CCDD.CcddConstants.LABEL_FONT_PLAIN;
import static CCDD.CcddConstants.LABEL_TEXT_COLOR;
import static CCDD.CcddConstants.LABEL_VERTICAL_SPACING;
import static CCDD.CcddConstants.OK_BUTTON;
import static CCDD.CcddConstants.PROTECTED_BACK_COLOR;
import static CCDD.CcddConstants.PROTECTED_TEXT_COLOR;
import static CCDD.CcddConstants.SELECTED_BACK_COLOR;
import static CCDD.CcddConstants.TABLE_BACK_COLOR;
import static CCDD.CcddConstants.VALID_TEXT_COLOR;
import static CCDD.CcddConstants.SchedulerType.APPLICATION_SCHEDULER;
import static CCDD.CcddConstants.SchedulerType.TELEMETRY_SCHEDULER;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import CCDD.CcddClasses.ApplicationData;
import CCDD.CcddClasses.CCDDException;
import CCDD.CcddClasses.CustomSplitPane;
import CCDD.CcddClasses.Message;
import CCDD.CcddClasses.TelemetryData;
import CCDD.CcddClasses.Variable;
import CCDD.CcddClasses.VariableGenerator;
import CCDD.CcddConstants.DialogOption;
import CCDD.CcddConstants.InputDataType;
import CCDD.CcddConstants.SchedulerType;
import CCDD.CcddConstants.TableSelectionMode;
import CCDD.CcddJTableHandler.UndoableTableModel;

/******************************************************************************
 * CFS Command & Data Dictionary scheduler editor handler class
 *****************************************************************************/
public class CcddSchedulerEditorHandler extends CcddEditorPanelHandler
{
    // Class references
    private final CcddMain ccddMain;
    private final CcddSchedulerHandler schedulerHndlr;
    private UndoableTableModel schTableModel;
    private CcddJTableHandler schedulerTable;
    private CcddAssignmentTreeHandler assignmentTree;

    // Tabbed pane for displaying the variable trees for messages and
    // sub-messages
    private JTabbedPane tabbedPane;

    // Size of each message
    private int emptyMessageSize;

    // Total number of bytes per a second
    private int totalBytes;

    // Total number of messages per second
    private final int msgsPerSecond;

    // Total number of parent messages
    private final int totalMessages;

    // List of messages that contain the information for the table
    private List<Message> messages;
    private List<Message> committedMessages;

    // The data currently stored in the database
    private Object[][] currentData;

    // Split pane containing the scheduler and assignment objects
    private JSplitPane tableSpltPn;

    // List of a scheduled applications
    private JList<String> assignmentList;

    // Cycle when message repeats
    private final float period;

    // Flag used to inhibit tab selection changes while the tabbed pane is
    // updated
    private boolean isTabUpdate;

    // Indices of the row and column selected in the scheduler table
    private int previousRow = -1;
    private int previousColumn = -1;

    // Empty message text
    private final String MESSAGE_EMPTY = "<html><i>Message is empty";

    /**************************************************************************
     * Scheduler table columns
     *************************************************************************/
    // Scheduler table columns
    private enum SchedulerColumn
    {
        NAME("<html><center>Message<br>Name", "Time Slot", "Column A"),
        SIZE("<html><center>Free<br>Bytes", "Time (msec)", "Column B"),
        ID("<html><center>Common<br>ID", "", "Column C");

        private String tlmColumn;
        private String appColumn;
        private String otherColumn;

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
     * Scheduler editor handler class constructor
     * 
     * @param schedulerHndlr
     *            reference to the scheduler dialog that created this class
     * 
     * @param totalMessages
     * 
     * @param totalBytes
     * 
     * @param msgsPerSecond
     *************************************************************************/
    public CcddSchedulerEditorHandler(CcddMain ccddMain,
                                      CcddSchedulerHandler schedulerHndlr,
                                      int totalMessages,
                                      int totalBytes,
                                      int msgsPerSecond)
    {
        this.ccddMain = ccddMain;
        this.schedulerHndlr = schedulerHndlr;
        this.totalMessages = totalMessages;
        this.totalBytes = totalBytes;
        this.msgsPerSecond = msgsPerSecond;

        // Set the reference to the scheduler editor in the scheduler handler.
        // This is done in this manner since if auto-validation is enabled a
        // reference to the editor is needed before returning to the handler
        schedulerHndlr.setSchedulerEditor(this);

        // Calculate the period (= total messages / total messages per second)
        period = Float.valueOf(totalMessages) / Float.valueOf(msgsPerSecond);

        // Calculate the message size
        emptyMessageSize = totalBytes / msgsPerSecond;

        isTabUpdate = false;

        // Initialize the telemetry table
        initialize();
    }

    /**************************************************************************
     * Get the reference to the scheduler table
     * 
     * @return Reference to the scheduler table
     *************************************************************************/
    protected CcddJTableHandler getTable()
    {
        return schedulerTable;
    }

    /**************************************************************************
     * Create a telemetry table
     *************************************************************************/
    @SuppressWarnings("serial")
    private void initialize()
    {
        // Create a border for the table and list panes, and an empty border
        Border border = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
                                                                                           Color.LIGHT_GRAY,
                                                                                           Color.GRAY),
                                                           BorderFactory.createEmptyBorder(2, 2, 2, 2));
        Border emptyBorder = BorderFactory.createEmptyBorder();

        // Initialize the layout constraints
        GridBagConstraints gbc = new GridBagConstraints(0,
                                                        0,
                                                        1,
                                                        1,
                                                        1.0,
                                                        0.0,
                                                        GridBagConstraints.LINE_START,
                                                        GridBagConstraints.BOTH,
                                                        new Insets(0, 0, 0, 0),
                                                        0,
                                                        0);

        // Initialize the telemetry scheduler
        initializeSchedulerTable();

        // Initialize the scheduler table object
        schedulerTable = new CcddJTableHandler(5)
        {
            /******************************************************************
             * Allow multiple line display in the specified column only
             *****************************************************************/
            @Override
            protected boolean isColumnMultiLine(int column)
            {
                return column == SchedulerColumn.NAME.ordinal();
            }

            /******************************************************************
             * Allow editing of the table cells in the specified column only
             *****************************************************************/
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return column == SchedulerColumn.NAME.ordinal()
                       || (column == SchedulerColumn.ID.ordinal()
                       && messages.get(row).getNumberOfSubMessages() <= 1)
                       || (column > SchedulerColumn.ID.ordinal()
                           && messages.get(row).getNumberOfSubMessages() > 1
                           && column <= SchedulerColumn.ID.ordinal()
                                        + messages.get(row).getNumberOfSubMessages());
            }

            /******************************************************************
             * Validate changes to the data field value cells; e.g., verify
             * cell content and, if found invalid, revert to the original value
             * 
             * @param tableData
             *            list containing the table data row arrays
             * 
             * @param row
             *            table model row number
             * 
             * @param column
             *            table model column number
             * 
             * @param oldValue
             *            original cell contents
             * 
             * @param newValue
             *            new cell contents
             * 
             * @param showMessage
             *            unused
             * 
             * @param isMultiple
             *            unused
             * 
             * @return Value of ShowMessage
             ****************************************************************/
            @Override
            protected Boolean validateCellContent(List<Object[]> tableData,
                                                  int row,
                                                  int column,
                                                  Object oldValue,
                                                  Object newValue,
                                                  Boolean showMessage,
                                                  boolean isMultiple)
            {
                // Reset the flag that indicates the last edited cell's content
                // is invalid
                setLastCellValid(true);

                // Create a string version of the new value
                String newValueS = newValue.toString();

                try
                {
                    // Check if this is the name column
                    if (column == SchedulerColumn.NAME.ordinal())
                    {
                        // Check if the value name is blank
                        if (newValueS.isEmpty())
                        {
                            // Inform the user that the message name cannot be
                            // blank
                            throw new CCDDException("Message name must be entered");
                        }

                        // Check if the message name is an alphanumeric
                        if (!newValueS.matches(InputDataType.ALPHANUMERIC.getInputMatch()))
                        {
                            // Inform the user that the message name contains
                            // invalid characters
                            throw new CCDDException("Invalid characters in message name");
                        }

                        // Step through each message
                        for (Message message : messages)
                        {
                            // Check if the new name matches an existing one
                            if (messages.indexOf(message) != row
                                && message.getName().equals(newValueS))
                            {
                                // Inform the user that the message name
                                // already is in use
                                throw new CCDDException("Message name is already in use");
                            }
                        }

                        // Store the new message name
                        messages.get(row).setName(newValueS);

                        // Update the assigned variables tab and options list
                        // with the new name
                        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), newValueS);
                        schedulerHndlr.getTelemetryOptions();
                    }
                    // Check if this is an ID column
                    else if (column >= SchedulerColumn.ID.ordinal())
                    {
                        // Check if the message ID is a hexadecimal
                        if (!newValueS.matches(InputDataType.HEXADECIMAL.getInputMatch()))
                        {
                            // Inform the user that the message name contains
                            // invalid characters
                            throw new CCDDException("Invalid characters in message ID");
                        }

                        // Format the hexadecimal value
                        newValueS = InputDataType.HEXADECIMAL.formatInput(newValueS);

                        // Check that the new value isn't a blank
                        if (!newValueS.isEmpty())
                        {
                            // Convert the ID to an integer
                            int id = Integer.valueOf(newValueS.replaceFirst("^0x|^0X", ""), 16);

                            // Step through each row in the table
                            for (int checkRow = 0; checkRow < tableData.size(); checkRow++)
                            {
                                // Step through each column containing an ID
                                for (int checkCol = SchedulerColumn.ID.ordinal(); checkCol < tableData.get(checkRow).length; checkCol++)
                                {
                                    // Check if this isn't the same row and
                                    // column as the one being updated, and
                                    // that the new ID matches that in another
                                    // ID cell
                                    if (!(row == checkRow
                                        && column == checkCol)
                                        && !tableData.get(checkRow)[checkCol].toString().isEmpty()
                                        && id == Integer.valueOf(tableData.get(checkRow)[checkCol].toString().replaceFirst("^0x|^0X", ""), 16))
                                    {
                                        // Inform the user that the message
                                        // name already is in use
                                        throw new CCDDException("Message ID is already in use");
                                    }
                                }
                            }
                        }

                        // Update the table with the formatted value
                        tableData.get(row)[column] = newValueS;

                        // Check if this is the parent message's ID
                        if (column == SchedulerColumn.ID.ordinal())
                        {
                            // Store the new message ID
                            messages.get(row).setID(newValueS);
                        }
                        // This is a sub-message ID
                        else
                        {
                            // Store the new sub-message ID
                            messages.get(row).getSubMessage(column
                                                            - SchedulerColumn.ID.ordinal()
                                                            - 1).setID(newValueS);
                        }
                    }
                }
                catch (CCDDException ce)
                {
                    // Set the flag that indicates the last edited cell's
                    // content is invalid
                    setLastCellValid(false);

                    // Inform the user that the input value is invalid
                    new CcddDialogHandler().showMessageDialog(schedulerHndlr.getSchedulerDialog(),
                                                              "<html><b>"
                                                                  + ce.getMessage(),
                                                              "Invalid Input",
                                                              JOptionPane.WARNING_MESSAGE,
                                                              DialogOption.OK_OPTION);

                    // Restore the cell contents to its original value
                    tableData.get(row)[column] = oldValue;
                    getUndoManager().undoRemoveEdit();
                }

                return showMessage;
            }

            /******************************************************************
             * Load the table data field definition values into the table and
             * format the table cells
             *****************************************************************/
            @Override
            protected void loadAndFormatData()
            {
                // Place the data into the table model along with the column
                // names, set up the editors and renderers for the table cells,
                // set up the table grid lines, and calculate the minimum width
                // required to display the table information
                setUpdatableCharacteristics(currentData,
                                            getColumnNames(),
                                            null,
                                            new Integer[] {},
                                            null,
                                            true,
                                            true,
                                            true,
                                            true);
            }

            /******************************************************************
             * Override prepareRenderer to allow adjusting the background
             * colors of table cells
             *****************************************************************/
            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                                             int row,
                                             int column)
            {
                JComponent comp = (JComponent) super.prepareRenderer(renderer,
                                                                     row,
                                                                     column);

                // Check if the cell doesn't have the focus or is selected, and
                // is protected from changes. The focus and selection highlight
                // colors override the invalid highlight color
                if (comp.getBackground() != FOCUS_COLOR
                    && comp.getBackground() != SELECTED_BACK_COLOR
                    && !isCellEditable(row, column))
                {
                    // Shade the cell's foreground and background colors
                    comp.setForeground(getValueAt(row, column).toString().startsWith("-")
                                                                                         ? INVALID_TEXT_COLOR
                                                                                         : PROTECTED_TEXT_COLOR);
                    comp.setBackground(PROTECTED_BACK_COLOR);
                }

                return comp;
            }
        };

        // Create a listener for scheduler table row and column selection
        // changes
        ListSelectionListener rowColListener = new ListSelectionListener()
        {
            /******************************************************************
             * Handle a scheduler table row or column selection change
             *****************************************************************/
            @Override
            public void valueChanged(ListSelectionEvent lse)
            {
                // Check if this is the last of the series of changes
                if (!lse.getValueIsAdjusting()
                    && (schedulerTable.getSelectedRow() != previousRow
                    || schedulerTable.getSelectedColumn() != previousColumn))
                {
                    // Update the tabbed pane for the selected message
                    updateAssignedVariablesTabs();

                    // Update the assignment tree/list
                    updateAssignmentList();

                    // Store the selected row and column indices for comparison
                    // when another cell is selected
                    previousRow = schedulerTable.getSelectedRow();
                    previousColumn = schedulerTable.getSelectedColumn();
                }
            }
        };

        // Add a listener for changes to the table's row selection
        schedulerTable.getSelectionModel().addListSelectionListener(rowColListener);

        // Add a listener for changes to the table's column selection
        schedulerTable.getColumnModel().getSelectionModel().addListSelectionListener(rowColListener);

        // Place the table into a scroll pane
        JScrollPane schedulerScrollPane = new JScrollPane(schedulerTable);
        schedulerScrollPane.setBorder(border);

        // Set common table parameters and characteristics
        schedulerTable.setFixedCharacteristics(schedulerScrollPane,
                                               false,
                                               ListSelectionModel.SINGLE_SELECTION,
                                               TableSelectionMode.SELECT_BY_CELL,
                                               false,
                                               TABLE_BACK_COLOR,
                                               true,
                                               true,
                                               CELL_FONT,
                                               null,
                                               true);

        // Get the table model and undo manager to shorten later calls
        schTableModel = (UndoableTableModel) schedulerTable.getModel();

        // Create a scroll pane to contain the assignment tree/list
        JScrollPane assignScrollPane = null;

        // Check if this is the telemetry scheduler
        if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
        {
            // Create an assignment tree specifying a null rate & message so
            // the tree is initially empty
            assignmentTree = new CcddAssignmentTreeHandler(ccddMain,
                                                           null,
                                                           ((CcddTelemetrySchedulerInput) schedulerHndlr.getSchedulerInput()).getLinkTree().getLinkHandler(),
                                                           ccddMain.getMainFrame());
        }
        // Check if this is the application scheduler
        else if (schedulerHndlr.getSchedulerOption() == APPLICATION_SCHEDULER)
        {
            // Initialize the assignment list and add it to a scroll pane that
            // will
            // be placed next to the variable list
            assignmentList = new JList<String>();
            assignmentList.setModel(new DefaultListModel<String>());
            assignmentList.setFont(LABEL_FONT_PLAIN);
            assignScrollPane = new JScrollPane(assignmentList);
            assignScrollPane.setBorder(border);

            // Set the size of the assignment scroll pane
            assignScrollPane.setPreferredSize(new Dimension(Math.min(Math.max(assignScrollPane.getPreferredSize().width,
                                                                              150),
                                                                     250),
                                                            assignScrollPane.getPreferredSize().height));
            assignScrollPane.setMinimumSize(assignScrollPane.getPreferredSize());
        }

        // Create panels to hold the components
        // tablePnl = new JPanel(new GridBagLayout());
        JPanel schedulerPnl = new JPanel(new GridBagLayout());
        JPanel assignmentPnl = new JPanel(new GridBagLayout());
        schedulerPnl.setBorder(emptyBorder);
        assignmentPnl.setBorder(emptyBorder);

        // Set the scheduler panel size so that the panel can't be resized in
        // width less than that needed to display the default columns
        Integer[] colWidths = schedulerTable.getColumnWidths();
        int prefWidth = 8 + colWidths[0] + colWidths[1] +
                        (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER
                                                                                   ? colWidths[2]
                                                                                   : 0);
        schedulerScrollPane.setPreferredSize(new Dimension(prefWidth,
                                                           schedulerScrollPane.getPreferredSize().height));
        schedulerPnl.setMinimumSize(schedulerScrollPane.getPreferredSize());

        // Create the scheduler table label
        JLabel schedulerLbl = new JLabel("Scheduler");
        schedulerLbl.setFont(LABEL_FONT_BOLD);
        schedulerLbl.setForeground(LABEL_TEXT_COLOR);

        // Add the scheduler label and scroll pane to the panel
        schedulerPnl.add(schedulerLbl, gbc);
        gbc.weighty = 1.0;
        gbc.insets.top = LABEL_VERTICAL_SPACING / 2;
        gbc.insets.bottom = LABEL_VERTICAL_SPACING / 2;
        gbc.gridy++;
        schedulerPnl.add(schedulerScrollPane, gbc);

        // Create the assignment list label and add it to the panel
        JLabel assignmentLbl = new JLabel("");
        assignmentLbl.setFont(LABEL_FONT_BOLD);
        assignmentLbl.setForeground(LABEL_TEXT_COLOR);
        gbc.insets.top = 0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        assignmentPnl.add(assignmentLbl, gbc);
        gbc.weighty = 1.0;
        gbc.gridy = 1;

        // Check if this is the telemetry scheduler
        if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
        {
            // Adjust the tab area's insets so that the scheduler and tabs are
            // aligned. Note that the Nimbus L&F has hard-coded insets, so
            // can't be changed;
            UIManager.getDefaults().put("TabbedPane.tabAreaInsets",
                                        new Insets(0, 0, 0, 0));
            UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

            // Create a tabbed pane to contain the variable tree for the
            // message and any sub-messages
            tabbedPane = new JTabbedPane();
            tabbedPane.setFont(LABEL_FONT_BOLD);

            // Listen for tab selection changes
            tabbedPane.addChangeListener(new ChangeListener()
            {
                /**************************************************************
                 * Handle tab selection change
                 *************************************************************/
                @Override
                public void stateChanged(ChangeEvent ce)
                {
                    // Check that a tab update isn't in progress. This prevents
                    // repeated calls to update the assignment tree
                    if (!isTabUpdate)
                    {
                        // Get the currently selected tab index
                        int tabIndex = tabbedPane.getSelectedIndex();

                        // Check if a tab is selected
                        if (tabIndex != -1)
                        {
                            // Get the currently selected message in the
                            // scheduler table
                            Message message = getSelectedMessage();

                            // Check if a message is selected
                            if (message != null)
                            {
                                // Select the row and column in the scheduler
                                // table corresponding to the selected message
                                // tab
                                schedulerTable.changeSelection(schedulerTable.getSelectedRow(),
                                                               SchedulerColumn.ID.ordinal()
                                                                   + tabIndex,
                                                               false,
                                                               false);
                            }
                        }

                        // Update the assignment tree/list
                        updateAssignmentList();
                    }
                }
            });

            // Set the assignment tree title
            assignmentLbl.setText("Assigned Variables");

            // Create the assignment tree and place it within the tabbed pane
            tabbedPane.insertTab("<html><i>No message selected",
                                 null,
                                 assignmentTree.createTreePanel(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION),
                                 null,
                                 0);

            // Add the tabbed pane to the panel
            assignmentPnl.add(tabbedPane, gbc);
        }
        // Check if this is the application scheduler
        else if (schedulerHndlr.getSchedulerOption() == APPLICATION_SCHEDULER)
        {
            // Set the assignment list title
            assignmentLbl.setText("Assigned Applications");

            // Add the assignment list to the panel
            gbc.insets.top = 0;
            assignmentPnl.add(assignScrollPane, gbc);

            // Set the tabbed pane to null so that the application scheduler
            // ignores it
            tabbedPane = null;
        }

        // Add the scheduler table and assignment tree/list to the split pane
        tableSpltPn = new CustomSplitPane(schedulerPnl, assignmentPnl, null);
    }

    /**************************************************************************
     * Update the tabs in the assigned variables tabbed pane based on the
     * currently selected message
     *************************************************************************/
    private void updateAssignedVariablesTabs()
    {
        // Check if this is the telemetry scheduler
        if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
        {
            // Get the currently selected message
            Message message = getSelectedMessage();

            // Check if a message is selected
            if (message != null)
            {
                int subMsgIndex = 0;

                // Set the flag indicating a tabbed pane update is in progress.
                // This flag is used to inhibit tab selection changes while the
                // tabbed pane is updated
                isTabUpdate = true;

                // Check if a sub-message is selected
                if (message.getParentMessage() != null)
                {
                    // Get the index of the selected sub-message
                    subMsgIndex = message.getParentMessage().getSubMessages().indexOf(message) + 1;

                    // Change the reference to the sub-messages parent message
                    message = message.getParentMessage();
                }

                // Set the first tab's title to the message name
                tabbedPane.setTitleAt(0, message.getName());

                // Step backwards through the sub-message tabs, if any
                for (int index = tabbedPane.getTabCount() - 1; index > 0; index--)
                {
                    // Remove the sub-message tab
                    tabbedPane.remove(index);
                }

                // Check if more than one sub-message exists. A 'default'
                // sub-message is automatically created, but if it's the only
                // one then it's hidden until another sub-message is added
                if (message.getNumberOfSubMessages() > 1)
                {
                    // Step through each sub-message
                    for (int index = 1; index <= message.getNumberOfSubMessages(); index++)
                    {
                        // Add a tab for the sub-message
                        tabbedPane.insertTab(getSubHeaderOrTabName(index),
                                             null,
                                             null,
                                             null,
                                             index);
                    }
                }

                // Select the tab associated with the selected (sub-)message
                tabbedPane.setSelectedIndex(subMsgIndex);

                // Reenable tab selection changes
                isTabUpdate = false;
            }
            // No message is selected
            else
            {
                // Set the first tab's title to indicate no selection
                tabbedPane.setTitleAt(0, "<html><i>No message selected");
            }
        }
    }

    /**************************************************************************
     * Update the assignment tree/list based on the currently selected message
     *************************************************************************/
    protected void updateAssignmentList()
    {
        // Get the selected message
        Message message = getSelectedMessage();

        // Check if a message is selected
        if (message != null)
        {
            // Update the package list
            updateAssignmentList(message);
        }
        // No message is selected
        else
        {
            // Check if this is the telemetry scheduler
            if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
            {
                // Step backwards through each tab that represents a
                // sub-message in the tabbed pane
                for (int index = tabbedPane.getTabCount() - 1; index > 0; index--)
                {
                    // Remove the sub-message tab
                    tabbedPane.remove(index);
                }

                // Remove any nodes and display the 'empty message' text
                assignmentTree.getRootNode().removeAllChildren();
                ((DefaultTreeModel) assignmentTree.getModel()).reload();
            }
            // Check if this is the application scheduler
            else if (schedulerHndlr.getSchedulerOption() == APPLICATION_SCHEDULER)
            {
                DefaultListModel<String> packageModel = (DefaultListModel<String>) assignmentList.getModel();

                // Clear the package list and display the 'empty message' text
                packageModel.clear();
            }
        }
    }

    /**************************************************************************
     * Update the assignment tree/list with the specified message's variables
     * 
     * @param message
     *            reference to the message from which the package list is
     *            populated
     *************************************************************************/
    private void updateAssignmentList(Message message)
    {
        // Checks if the message is null
        if (message != null)
        {
            // Get the list of variables assigned to this message
            List<Variable> variables = message.getVariables();

            // Check if this is the telemetry scheduler
            if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
            {
                // Check if there are any variables to display
                if (!variables.isEmpty())
                {
                    // (Re)build the assignment tree using the message's
                    // variables
                    assignmentTree.buildTree(false,
                                             false,
                                             schedulerHndlr.getRateName()
                                                 + "\\"
                                                 + message.getName(),
                                             false,
                                             schedulerHndlr.getSchedulerPanel());
                }
                // There are no variables to display
                else
                {
                    // Remove any nodes and display the 'empty message' text
                    assignmentTree.getRootNode().removeAllChildren();
                    ((DefaultTreeModel) assignmentTree.getModel()).reload();
                    assignmentTree.addNodeToInfoNode(assignmentTree.getRootNode(),
                                                     new Object[] {MESSAGE_EMPTY},
                                                     0);
                }
            }
            // Check if this is the application scheduler
            else if (schedulerHndlr.getSchedulerOption() == APPLICATION_SCHEDULER)
            {
                DefaultListModel<String> packageModel = (DefaultListModel<String>) assignmentList.getModel();

                // Remove all current elements in the packageList
                packageModel.clear();

                // Step through each variable
                for (Variable var : variables)
                {
                    // Add the variable to the package list
                    packageModel.add(packageModel.size(), var.getFullName());
                }

                // Check if the pack list is empty
                if (packageModel.getSize() == 0)
                {
                    // Add the empty variable string to the list
                    packageModel.add(0, MESSAGE_EMPTY);
                }
            }
        }
    }

    /**************************************************************************
     * Update the total bytes and message size
     * 
     * @param totalBytes
     *            number of total bytes per second
     *************************************************************************/
    protected void changeTotalBytes(int totalBytes)
    {
        // Assign total bytes
        this.totalBytes = totalBytes;

        // Assign the current message size
        int delta = emptyMessageSize;

        // Update the message size
        emptyMessageSize = totalBytes / msgsPerSecond;

        // Find the difference between the old and new message sizes
        delta = emptyMessageSize - delta;

        // Step through each message
        for (int msgIndex = 0; msgIndex < messages.size(); msgIndex++)
        {
            // Add the difference to the message
            messages.get(msgIndex).addBytes(delta);

            // Update the value for the bytes column
            updateRemainingBytesColumn(msgIndex);
        }
    }

    /**************************************************************************
     * Set the bytes column for all messages in the scheduler table
     *************************************************************************/
    protected void updateRemainingBytesColumn()
    {
        // Check if the scheduler table has been created
        if (schTableModel != null)
        {
            // Step through each message
            for (int msgIndex = 0; msgIndex < messages.size(); msgIndex++)
            {
                // Set the table to display the total remaining bytes
                updateRemainingBytesColumn(msgIndex);
            }
        }
    }

    /**************************************************************************
     * Set the bytes column for the specified message in the scheduler table.
     * If the remaining bytes is less than zero the text is displayed in red
     *
     * @param msgIndex
     *            index of the message to update, which is the same as the row
     *            in the scheduler table
     *************************************************************************/
    private void updateRemainingBytesColumn(int msgIndex)
    {
        schTableModel.setValueAt(messages.get(msgIndex).getBytesRemaining(),
                                 msgIndex,
                                 SchedulerColumn.SIZE.ordinal());
    }

    /**************************************************************************
     * Get the table panel containing the scheduler table and assignment
     * tree/list panes
     * 
     * @return Split pane containing the scheduler table and assignment
     *         tree/list panes
     *************************************************************************/
    protected JSplitPane getSchedulerAndAssignPanel()
    {
        return tableSpltPn;
    }

    /**************************************************************************
     * Get the total amount of unused bytes
     * 
     * @return Total amount of bytes remaining
     *************************************************************************/
    protected int getTotalBytesRemaining()
    {
        // Initialize the total remaining bytes. This is zero unless the total
        // bytes is not evenly divisible by the number of messages
        int remainingBytes = totalBytes
                             - totalBytes / messages.size()
                             * messages.size();

        // Step through each row
        for (int row = 0; row < schTableModel.getRowCount(); row++)
        {
            // Add the bytes remaining from that row to the total
            remainingBytes += messages.get(row).getBytesRemaining();
        }

        return remainingBytes;
    }

    /**************************************************************************
     * Get the maximum number of sub-messages from the messages
     * 
     * @return Maximum number of sub-messages; 0 if only the message(s) only
     *         contains a default sub-message
     *************************************************************************/
    private int getMaxNumberOfSubMessages()
    {
        int maxSubMessages = 0;

        // Step through each message
        for (Message message : messages)
        {
            // Determine the maximum number of sub-messages
            maxSubMessages = Math.max(maxSubMessages,
                                      message.getNumberOfSubMessages());
        }

        // Check if only the default sub-messages exist
        if (maxSubMessages == 1)
        {
            // Set the maximum to zero to indicate no sub-messages
            maxSubMessages = 0;
        }

        return maxSubMessages;
    }

    /**************************************************************************
     * Set the column names based on the scheduler type
     * 
     * @return Array containing the column names
     *************************************************************************/
    private String[] getColumnNames()
    {
        List<String> columns = new ArrayList<String>();

        // Step through each scheduler column
        for (SchedulerColumn column : SchedulerColumn.values())
        {
            // Get the column name for this column
            String columnName = column.getColumn(schedulerHndlr.getSchedulerOption());

            // Check if the column is applicable to this scheduler
            if (!columnName.isEmpty())
            {
                // Add the column to the scheduler
                columns.add(columnName);
            }
        }

        // Check if this is the telemetry scheduler
        if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
        {
            // Step through the sub-message count
            for (int index = 1; index <= getMaxNumberOfSubMessages(); index++)
            {
                // Add an ID column for the sub-message
                columns.add("<html><center>"
                            + getSubHeaderOrTabName(index)
                            + "<br>ID");
            }
        }

        return columns.toArray(new String[0]);
    }

    /**************************************************************************
     * Get the sub-message scheduler column name or assigned variables tab name
     * 
     * @param index
     *            sub-message index
     * 
     * @return Sub-message scheduler column name or assigned variables tab name
     *************************************************************************/
    private String getSubHeaderOrTabName(int index)
    {
        return "Sub " + index;
    }

    /**************************************************************************
     * Initialize the scheduler table. Add values to the current data which is
     * used when creating the table. The message list is also initialized
     *************************************************************************/
    private void initializeSchedulerTable()
    {
        // Initialize the messages lists
        messages = new ArrayList<Message>();

        // Load the stored variables
        List<Variable> excludedVars = schedulerHndlr.getVariableList();

        // Get the messages from the stored data
        List<Message> storedMsgs = schedulerHndlr.getStoredData();

        // Check if data is stored and automatic validation is enabled
        if (!excludedVars.isEmpty() && ccddMain.isAutoValidate())
        {
            // Validate the stored messages
            schedulerHndlr.validateMessages();
        }

        // Check if the stored data is either not accurate or not set
        if (storedMsgs.size() != totalMessages)
        {
            Message msg;
            currentData = new Object[totalMessages][SchedulerColumn.values().length];

            // Step through each row
            for (int row = 0; row < currentData.length; row++)
            {
                // Create a new message. The space in the name is necessary
                // when parsing the message row for the message indices
                msg = new Message((schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER
                                                                                             ? "Message"
                                                                                             : "Time Slot")
                                  + "_"
                                  + (row + 1),
                                  "",
                                  emptyMessageSize);

                // Add the message to the existing list
                messages.add(msg);

                // Add message name, size, and ID to the table's current data
                currentData[row][SchedulerColumn.NAME.ordinal()] = msg.getName();
                currentData[row][SchedulerColumn.SIZE.ordinal()] = msg.getBytesRemaining();
                currentData[row][SchedulerColumn.ID.ordinal()] = msg.getID();
            }
        }
        // The data stored in the database is accurate
        else
        {
            // Add the messages to the existing list
            messages.addAll(storedMsgs);

            // Set the scheduler table data array to the current message
            // information
            updateSchedulerTable(false);

            // Check if there are any excluded variables
            if (excludedVars != null && !excludedVars.isEmpty())
            {
                List<String> varNames = new ArrayList<String>();

                // Step through each variable in the list of excluded variables
                for (Variable var : excludedVars)
                {
                    // Add the variable name with path to the list
                    varNames.add(var.getFullName());
                }

                // Make each variable in the excluded variables list
                // unavailable in the variable tree
                schedulerHndlr.setVariableUnavailable(varNames);
            }
        }

        // Copy the current messages for change comparison purposes
        copyMessages();
    }

    /**************************************************************************
     * Update the scheduler table based on the current messages
     * 
     * @param isLoadData
     *            true to load the scheduler table with the data; false to only
     *            update the scheduler table data array
     *************************************************************************/
    protected void updateSchedulerTable(boolean isLoadData)
    {
        // Get the maximum number of sub-messages in the existing messages
        int maxSubMsgs = getMaxNumberOfSubMessages();

        currentData = new Object[totalMessages][SchedulerColumn.values().length
                                                + maxSubMsgs];

        // Adjust the message byte count, if applicable
        calculateTotalBytesRemaining();

        // Step through each message
        for (int msgIndex = 0; msgIndex < messages.size(); msgIndex++)
        {
            // Get the number of sub-messages for this message
            int numSubMessages = messages.get(msgIndex).getNumberOfSubMessages();

            // Add the message name and size to the table's current data
            currentData[msgIndex][SchedulerColumn.NAME.ordinal()] = messages.get(msgIndex).getName();
            currentData[msgIndex][SchedulerColumn.SIZE.ordinal()] = messages.get(msgIndex).getBytesRemaining();

            // Check if the message only has the default sub-message
            if (numSubMessages == 1)
            {
                // Add the common message ID to the table's current data
                currentData[msgIndex][SchedulerColumn.ID.ordinal()] = messages.get(msgIndex).getID();
            }
            // The message has more than just the default sub-message
            else
            {
                // Set the message ID to a blank
                currentData[msgIndex][SchedulerColumn.ID.ordinal()] = "";
            }

            // Step through each of the remaining columns
            for (int subIndex = 0; subIndex < maxSubMsgs; subIndex++)
            {
                // Check if this message has more than just the default
                // sub-message and that this sub-message index exists for this
                // message
                if (numSubMessages > 1 && subIndex < numSubMessages)
                {
                    // Add the sub-message ID
                    currentData[msgIndex][SchedulerColumn.ID.ordinal() + subIndex + 1] = messages.get(msgIndex).getSubMessage(subIndex).getID();
                }
                // This message has only the default sub-message or doesn't
                // have this sub-message index
                else
                {
                    // Set the sub-message ID to a blank
                    currentData[msgIndex][SchedulerColumn.ID.ordinal() + subIndex + 1] = "";
                }
            }
        }

        // Check if the table should be loaded with the message data. This is
        // done automatically when the table is created, so this flag should be
        // set to true only if updating the table after it's created
        if (isLoadData)
        {
            // Load the data into the scheduler table
            schedulerTable.loadAndFormatData();
        }
    }

    /**************************************************************************
     * Calculate the number of unused bytes for all messages and sub-messages,
     * accounting for bit-packed variables
     *************************************************************************/
    protected void calculateTotalBytesRemaining()
    {
        // Step through each message
        for (Message message : messages)
        {
            // Update the message's byte count
            message.setBytesRemaining(emptyMessageSize
                                      - schedulerHndlr.getSchedulerInput().getSelectedValuesSize(message.getVariables()));

            // Step through each sub-message
            for (Message subMessage : message.getSubMessages())
            {
                // Update the sub-message's byte counts
                subMessage.setBytesRemaining(emptyMessageSize
                                             - schedulerHndlr.getSchedulerInput().getSelectedValuesSize(subMessage.getAllVariables()));
            }
        }
    }

    /**************************************************************************
     * Copy the current messages so that a comparison can be made to detect
     * changes
     *************************************************************************/
    protected void copyMessages()
    {
        // Create storage for the existing messages
        committedMessages = new ArrayList<Message>();

        // Copy the messages
        copyMessages(messages, committedMessages, null);

        // Step through each message
        for (int msgIndex = 0; msgIndex < messages.size(); msgIndex++)
        {
            // Copy the message's sub-messages
            copyMessages(messages.get(msgIndex).getSubMessages(),
                         committedMessages.get(msgIndex).getSubMessages(),
                         committedMessages.get(msgIndex));
        }
    }

    /**************************************************************************
     * Copy the specified (sub-)messages to the specified copy location
     * 
     * @param messageList
     *            list of (sub-)messages to copy
     * 
     * @param copyList
     *            reference to the list to which to copy the (sub-)messages
     * 
     * @param parentMessage
     *            parent of the sub-message; null if this is not a sub-message
     *************************************************************************/
    private void copyMessages(List<Message> messageList,
                              List<Message> copyList,
                              Message parentMessage)
    {
        // Step through each (sub-)message
        for (Message message : messageList)
        {
            // Create and store a copy of the (sub-)message
            copyList.add(new Message(message.getName(),
                                     message.getID(),
                                     message.getBytesRemaining(),
                                     parentMessage,
                                     parentMessage == null
                                                          ? new ArrayList<Message>()
                                                          : null));

            // Step through each variable in the (sub-)message
            for (Variable variable : message.getVariables())
            {
                Variable copyVar = null;

                // Check if this is a telemetry scheduler
                if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
                {
                    TelemetryData tlmData = (TelemetryData) variable;

                    // Create a copy of the telemetry data
                    copyVar = VariableGenerator.generateTelemetryData(tlmData.getRate()
                                                                      + "\\"
                                                                      + tlmData.getFullName());
                }
                // Check if this is an application scheduler
                else if (schedulerHndlr.getSchedulerOption() == APPLICATION_SCHEDULER)
                {
                    ApplicationData appData = (ApplicationData) variable;

                    // Create a copy of the application data
                    copyVar = VariableGenerator.generateApplicationData(appData.getFullName() + ","
                                                                        + appData.getRate() + ","
                                                                        + appData.getSize() + ","
                                                                        + appData.getPriority() + ","
                                                                        + appData.getMessageRate() + ","
                                                                        + appData.getWakeUpID() + ","
                                                                        + appData.getWakeUpName() + ","
                                                                        + appData.getHkSendRate() + ","
                                                                        + appData.getHkWakeUpID() + ","
                                                                        + appData.getHkWakeUpName() + ","
                                                                        + appData.getSchGroup());
                }

                // Check if a copy was produced
                if (copyVar != null)
                {
                    // Add the variable to the copy
                    copyList.get(copyList.size() - 1).addVariable(copyVar);
                }
            }
        }
    }

    /**************************************************************************
     * Add a variable to the specified (sub-)message. Update the message and
     * table with the new values
     * 
     * @param variable
     *            variable that will be added
     * 
     * @param messageIndex
     *            message index if the variable is not assigned to a
     *            sub-message; sub-message index if the message is assigned to
     *            a sub-message
     * 
     * @param subMsgIndex
     *            message index if the variable is assigned to a sub-message,
     *            -1 if not
     *************************************************************************/
    protected void addVariableToMessage(Variable variable,
                                        int messageIndex,
                                        int subMsgIndex)
    {
        Integer index = -1;
        Message targetMsg;

        // Check if the variable should be assigned to a sub-message
        if (subMsgIndex >= 0)
        {
            // Get the reference to the sub-message
            targetMsg = messages.get(subMsgIndex).getSubMessage(messageIndex);
        }
        // Variable will be assigned to the general message
        else
        {
            // Get the reference to the message
            targetMsg = messages.get(messageIndex);
        }

        // Get the index at which the variable/application should be inserted
        // in the message
        index = schedulerHndlr.getSchedulerInput().getVariableRelativeIndex(variable,
                                                                            targetMsg.getVariables());

        // Check that the variable isn't already in the message
        if (index != null)
        {
            // Add the variable to the (sub-)message
            targetMsg.addVariable(variable, index);
        }
    }

    /**************************************************************************
     * Update the assignment tree with the current messages and rate
     *************************************************************************/
    protected void updateAssignmentDefinitions()
    {
        assignmentTree.updateAssignmentDefinitions(messages,
                                                   schedulerHndlr.getRateName());
    }

    /**************************************************************************
     * Get the number of variables in the specified message
     * 
     * @param index
     *            message index
     * 
     * @return Number of variables in the specified message
     *************************************************************************/
    protected int getPacketSize(int index)
    {
        return messages.get(index).getNumberOfVariables();
    }

    /**************************************************************************
     * Set the table to select the specified row
     * 
     * @param row
     *            row to select
     *************************************************************************/
    protected void setSelectedRow(int row)
    {
        schedulerTable.getSelectionModel().setSelectionInterval(row, row);
    }

    /**************************************************************************
     * Remove the selected variable(s). This will remove it from any other
     * messages the variable is in. If the variable is a member of a link it
     * removes all the other link member variables as well
     * 
     * @return List of variable names removed
     *************************************************************************/
    protected List<String> removeSelectedVariable()
    {
        List<String> removedVarNames = new ArrayList<String>();
        List<String> selectedVars = null;

        // Check if this is a telemetry scheduler
        if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
        {
            // Get the selected variable(s)
            selectedVars = assignmentTree.getSelectedVariables();
        }
        // Check if this is a application scheduler
        else if (schedulerHndlr.getSchedulerOption() == APPLICATION_SCHEDULER)
        {
            // Get the selected variable(s)
            selectedVars = assignmentList.getSelectedValuesList();
        }

        // Check if an item is selected and that the selected value is not the
        // empty message value
        if (selectedVars != null
            && !selectedVars.isEmpty()
            && !selectedVars.get(0).equals(MESSAGE_EMPTY))
        {
            // Row of selected variable
            int row = schedulerTable.getSelectedRow();

            // List of variables to be removed
            List<Variable> removedVars = new ArrayList<Variable>();

            // Step through each selected variable
            for (String selectedVar : selectedVars)
            {
                // Variable object to be returned
                Variable variable = messages.get(row).getVariable(selectedVar);

                // Check if the selected variable hasn't already been added to
                // removed list
                if (variable != null && !removedVars.contains(variable))
                {
                    // Check to see if the variable is linked
                    if (variable.getLink() != null)
                    {
                        // Add all the variables in the link to the removed
                        // list
                        for (Variable packetVar : messages.get(row).getVariables())
                        {
                            // Check if the variable is in the link of the
                            // selected item
                            if (packetVar.getLink() != null
                                && packetVar.getLink().equals(variable.getLink()))
                            {
                                // Add the variable to the removed list
                                removedVars.add(packetVar);
                            }
                        }
                    }
                    // The variable is not in a link; add the selected variable
                    // to the removed variables list
                    else
                    {
                        // Add the variable to the list of removed variables
                        removedVars.add(variable);
                    }
                }
            }

            // Remove the variables in the list from the message
            removedVarNames = removeVariableFromMessages(removedVars, row);

            // Check if this is a telemetry scheduler
            if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
            {
                // Update the assignment definition list for when the
                // assignment tree is rebuilt
                assignmentTree.updateAssignmentDefinitions(messages,
                                                           schedulerHndlr.getRateName());
            }

            // Calculate the bytes remaining in the messages
            calculateTotalBytesRemaining();

            // Update the remaining bytes column values
            updateRemainingBytesColumn();

            // Update the package list to the new message's variables
            updateAssignmentList();
        }

        return removedVarNames;
    }

    /**************************************************************************
     * Remove the specified variable(s) from all messages it is contained in
     * 
     * @param variables
     *            list of variables to remove
     * 
     * @param row
     *            message row index
     * 
     * @return List of the variable names removed
     *************************************************************************/
    protected List<String> removeVariableFromMessages(List<Variable> variables,
                                                      int row)
    {
        List<Integer> msgIndices;
        List<String> removedVarNames = new ArrayList<String>();

        // Step through the variable list
        for (Variable variable : variables)
        {
            removedVarNames.add(variable.getFullName());

            // Check if the variable is in a sub-message
            if (variable.getRate() < (1 / period))
            {
                // Remove it from a general message
                messages.get(row).removeVariable(variable.getFullName());
            }
            // Variable is not in a sub-message
            else
            {
                // Assign the message indices
                msgIndices = variable.getMessageIndices();

                // Step through all the messages the variable is contained in
                for (Integer msgIndex : msgIndices)
                {
                    // Remove it from a general message
                    messages.get(msgIndex).removeVariable(variable.getFullName());
                }
            }
        }

        return removedVarNames;
    }

    /**************************************************************************
     * Find all the combinations of messages into which a a rate fits
     * 
     * @param rate
     *            rate, in hertz, of the selected variable
     * 
     * @return List of the combinations
     *************************************************************************/
    protected List<String> getMessageAvailability(float rate)
    {
        List<String> combos;

        // Check if the rate is a sub-rate
        if (rate < (1 / period))
        {
            // Call the sub-rate option handler
            combos = getSubOptions(rate);
        }
        // The message is not sub-rates
        else
        {
            // Call the rate handler
            combos = getOptions(Math.round(rate * period));
        }

        return combos;
    }

    /**************************************************************************
     * Get a list of the options for non-sub-hertz rates in the format
     * [MessageName1][, MessageName2],...]]
     * 
     * @param rate
     *            rate filter, in hertz
     * 
     * @return List of all the combinations
     *************************************************************************/
    private List<String> getOptions(int rate)
    {
        List<String> options = new ArrayList<String>();

        // Total number of different message options
        int numOptions = totalMessages / rate;

        // Step through each different message option
        for (int row = 0; row < numOptions; row++)
        {
            // Assign the next message
            int nextMsg = 0;

            // Create the start of the option
            String option = "";

            // Step through each rate
            for (int rateIndex = 0; rateIndex < rate; rateIndex++)
            {
                // Add the message to the option string
                option += messages.get(row + nextMsg).getName() + ", ";

                // Assign the next message
                nextMsg += numOptions;
            }

            // Remove the trailing comma from the option
            option = CcddUtilities.removeTrailer(option, ", ");

            // Add the option to the list of options
            options.add(option);
        }

        return options;
    }

    /**************************************************************************
     * Find all the options for sub-hertz
     * 
     * @param rate
     *            hertz of the selected variable
     * 
     * @return List of all the combinations
     *************************************************************************/
    private List<String> getSubOptions(float rate)
    {
        List<String> msgs = new ArrayList<>();

        // Current message option
        String msg = "";

        // Number for the next message
        int nextMsg;

        // Step through each existing message
        for (Message message : messages)
        {
            // Check if the message's sub-message count can be even split by
            // the rate
            if ((message.getNumberOfSubMessages() * rate) % 1 == 0.0)
            {
                // Calculate the number of options
                int optionCount = (int) (1 / rate);

                // Distance between next message
                int diff = (int) (message.getNumberOfSubMessages() * rate);

                // Step through the number of options
                for (int index = 0; index < optionCount; index++)
                {
                    // Set next message
                    nextMsg = index + 1;

                    // Create the start of the option
                    msg = message.getName()
                          + " sub-msg" + (diff == 1 ? "" : "s") + " ";

                    // Step through each message in an option
                    for (int subIndex = 0; subIndex < diff; subIndex++)
                    {
                        // Add the message to the option
                        msg = msg + nextMsg + ", ";

                        // Assign next message
                        nextMsg = nextMsg + diff;
                    }

                    // Remove the trailing comma
                    msg = CcddUtilities.removeTrailer(msg.trim(), ",");

                    // Add the option to the list of options
                    msgs.add(msg);
                }
            }
        }

        return msgs;
    }

    /**************************************************************************
     * Set the message name to green if the message has room for the size; if
     * not set the name to red. Also set the table model to display the number
     * of bytes after the variable is added
     * 
     * @param messageIndex
     *            row number for which to set the values
     * 
     * @param size
     *            size that needs to fit in the message
     *************************************************************************/
    protected void setMessageAvailability(int messageIndex, int size)
    {
        // Check if the size is not negative
        if (size >= 0)
        {
            // Change the row text color to indicate the message is available
            schedulerTable.setRowTextColor(messageIndex, VALID_TEXT_COLOR);
        }
        // The size is a negative number
        else
        {
            // Change the row text color to indicate the message is over
            // subscribed
            schedulerTable.setRowTextColor(messageIndex, INVALID_TEXT_COLOR);
        }

        // Set the bytes to display the new size
        schedulerTable.setValueAt(size,
                                  messageIndex,
                                  SchedulerColumn.SIZE.ordinal());
    }

    /**************************************************************************
     * Reset the message name to its normal state
     *************************************************************************/
    protected void resetMessageAvailability()
    {
        // Step through each row
        for (int row = 0; row < schTableModel.getRowCount(); row++)
        {
            // Reset the row text color to normal
            schedulerTable.setRowTextColor(row, null);

            // Set the bytes column to the messages remaining bytes
            updateRemainingBytesColumn(row);
        }
    }

    /**************************************************************************
     * Get the message size for the specified message index
     * 
     * @param index
     *            message index
     * 
     * @return Message size
     *************************************************************************/
    protected int getMessageSize(int index)
    {
        return messages.get(index).getBytesRemaining();
    }

    /**************************************************************************
     * Get the message at the specified indices
     * 
     * @param messageIndex
     *            message index if this is a parent message; sub-message index
     *            if this is a sub-message
     * 
     * @param parentIndex
     *            parent message index if this is a sub-message; -1 if not a
     *            sub-message
     * 
     * @return Message object at the given indices
     *************************************************************************/
    protected Message getMessage(int messageIndex, int parentIndex)
    {
        Message msg = null;

        // Check if desired message is not sub-message
        if (parentIndex == -1)
        {
            // Assign the general message to the message object
            msg = messages.get(messageIndex);
        }
        // The message is a sub-message
        else
        {
            // Assign the sub-message to the message object
            msg = messages.get(parentIndex).getSubMessage(messageIndex);
        }

        return msg;
    }

    /**************************************************************************
     * Get the (sub-)message currently selected in the scheduler table
     * 
     * @return The currently selected (sub-)message; null if none is selected
     *************************************************************************/
    protected Message getSelectedMessage()
    {
        Message message = null;

        // Get the selected row
        int row = schedulerTable.getSelectedRow();

        // Check if a row is selected
        if (row != -1)
        {
            // Convert the row index to model coordinates in case the rows are
            // sorted by the column header
            row = schedulerTable.convertRowIndexToModel(row);

            // Get the message for the selected row
            message = messages.get(row);

            // Check if this is the telemetry scheduler
            if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
            {
                // Get the selected column
                int column = schedulerTable.getSelectedColumn();

                // Calculate the index of the selected sub-message
                int subMsgIndex = column - SchedulerColumn.ID.ordinal();

                // Check if a valid sub-message is selected
                if (subMsgIndex > 0
                    && message.getNumberOfSubMessages() > 1
                    && subMsgIndex <= message.getNumberOfSubMessages())
                {
                    // Get the message for the selected row
                    message = messages.get(row).getSubMessage(subMsgIndex - 1);
                }
            }
        }

        return message;
    }

    /**************************************************************************
     * Remove all variables
     * 
     * @return List of all the variables removed
     *************************************************************************/
    protected List<Variable> removeAll()
    {
        // Create lists for the variables and the variables that are removed
        List<Variable> variables = new ArrayList<Variable>();
        List<Variable> removedVars = new ArrayList<Variable>();

        // Step through each message
        for (int msgIndex = 0; msgIndex < messages.size(); msgIndex++)
        {
            // Add the message's variables to the list of removed variables
            removedVars.addAll(messages.get(msgIndex).getAllVariables());

            // Remove the variables from the messages
            removeVariableFromMessages(removedVars, msgIndex);

            // Add the message's removed variables to the list of all removed
            // variables
            variables.addAll(removedVars);

            // Clear the removed variables list for the next pass
            removedVars.clear();
        }

        // Check if this is a telemetry scheduler
        if (schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER)
        {
            // Update the assignment definition list for when the assignment
            // tree is rebuilt
            assignmentTree.updateAssignmentDefinitions(messages,
                                                       schedulerHndlr.getRateName());
        }

        // Calculate the bytes remaining in the messages
        calculateTotalBytesRemaining();

        // Update the remaining bytes column values
        updateRemainingBytesColumn();

        // Update the assignment tree/list
        updateAssignmentList();

        return variables;
    }

    /**************************************************************************
     * Remove the variables (applications) assigned to the messages (time
     * slots)
     *************************************************************************/
    protected void clearMessages()
    {
        // Get the scheduler type text based on the scheduler type
        String type = schedulerHndlr.getSchedulerOption() == TELEMETRY_SCHEDULER
                                                                                ? "Messages"
                                                                                : "Time Slots";

        boolean isVariable = false;

        // Step through each message (time slot) in the stream
        for (Message msg : getCurrentMessages())
        {
            // Check if a variable (application) exists in the message
            if (!msg.getAllVariables().isEmpty())
            {
                // Set the flag indicating a variable (application)exists and
                // stop searching
                isVariable = true;
                break;
            }
        }

        // Check if there are any messages (time slots) to reset and, if so,
        // that the user confirms resetting the messages (time slots)
        if (isVariable
            && new CcddDialogHandler().showMessageDialog(schedulerHndlr.getSchedulerDialog(),
                                                         "<html><b>Clear "
                                                             + type.toLowerCase(),
                                                         "Clear "
                                                             + type,
                                                         JOptionPane.QUESTION_MESSAGE,
                                                         DialogOption.OK_CANCEL_OPTION) == OK_BUTTON)
        {
            // Reset the telemetry (application) scheduler table
            schedulerHndlr.resetTelemetryTable();
        }
    }

    /**************************************************************************
     * Add a sub-message to the message currently selected in the scheduler
     * table
     *************************************************************************/
    protected void addSubMessage()
    {
        // Get the currently selected (sub-)message
        Message message = getSelectedMessage();

        // Check if a sub-message is selected
        if (message.getNumberOfSubMessages() == 0)
        {
            // Get the sub-message's parent message
            message = message.getParentMessage();
        }

        // Check if a message is selected and if there are variables in the
        // sub-messages and deallocate them if present
        if (message != null && deAllocateSubVariables(message))
        {
            // Check if a sub-message tab is selected
            if (message.getParentMessage() != null)
            {
                // Change the reference to the sub-messages parent message
                message = message.getParentMessage();
            }

            // Add a sub-message to the message object
            message.addNewSubMessage("");

            // Update the tabbed pane for the selected message
            updateAssignedVariablesTabs();

            // Update the assignment tree/list
            updateAssignmentList();

            // Update the options panel to display the options for the selected
            // rate
            schedulerHndlr.getTelemetryOptions();

            // Update the scheduler table to reflect the added sub-message
            updateSchedulerTable(true);
        }
    }

    /**************************************************************************
     * Delete the sub-message associated with the currently selected assignment
     * tab
     *************************************************************************/
    protected void deleteSubMessage()
    {
        // Get the currently selected (sub-)message
        Message message = getSelectedMessage();

        // Check if a message is selected
        if (message != null)
        {
            // Check if a sub-message tab is selected
            if (message.getParentMessage() != null)
            {
                // Change the reference to the sub-messages parent message
                message = message.getParentMessage();
            }

            // Get the number of sub-messages
            int index = message.getNumberOfSubMessages();

            // Check if the selected message isn't the general (parent) or
            // default message, and if there are variables in the sub-messages
            if (index > 1 && deAllocateSubVariables(message))
            {
                // Remove the sub-message from the message
                message.removeSubMessage(index - 1);

                // Update the tabbed pane for the selected message
                updateAssignedVariablesTabs();

                // Update the assignment tree/list
                updateAssignmentList();

                // Update the options panel to display the options for the
                // selected rate
                schedulerHndlr.getTelemetryOptions();

                // Recalculate the total bytes remaining in each message and
                // update the Scheduler table Bytes column
                calculateTotalBytesRemaining();

                // Update the remaining bytes column values
                updateRemainingBytesColumn();
            }

            // Update the scheduler table to reflect the deleted sub-message
            updateSchedulerTable(true);
        }
    }

    /**************************************************************************
     * Deallocate any sub-message variables if the number of sub-messages is
     * changed
     *
     * @param message
     *            message for which the variables are deallocated from each
     *            sub-message
     *
     * @return true if the variable is deallocated or there are no variables to
     *         deallocate; false if there are variable to deallocate but the
     *         user cancels the operation
     *************************************************************************/
    private boolean deAllocateSubVariables(Message message)
    {
        boolean isDeallocated = true;
        boolean isSubVariable = false;

        // Check if this message has sub-messages (i.e., is a parent message)
        if (message.getNumberOfSubMessages() != 0)
        {
            // Step through each sub-message
            for (Message msg : message.getSubMessages())
            {
                // Check if the sub-message has a variable
                if (msg.getNumberOfVariables() != 0)
                {
                    // Set the flag to indicate a sub-message contains a
                    // variable and stop searching
                    isSubVariable = true;
                    break;
                }
            }
        }

        // Check if a sub-message has a variable
        if (isSubVariable)
        {
            // Check if the user confirms deallocating the variables from the
            // sub-messages
            if (new CcddDialogHandler().showMessageDialog(schedulerHndlr.getSchedulerDialog(),
                                                          "<html><b>Note: All of this message's sub-message<br>"
                                                              + "variables will be de-assigned!<br><br>Proceed?",
                                                          "Confirmation",
                                                          JOptionPane.QUESTION_MESSAGE,
                                                          DialogOption.OK_CANCEL_OPTION) == OK_BUTTON)
            {
                List<String> excludedVars = new ArrayList<String>();

                // Step through each sub-message
                for (Message subMsg : message.getSubMessages())
                {
                    // Step through each variable in the sub-message
                    for (Variable var : subMsg.getVariables())
                    {
                        // Check if the variable has not been added to the
                        // excluded variables list
                        if (!excludedVars.contains(var.getFullName()))
                        {
                            // Add the variable name to the exclude list
                            excludedVars.add(var.getFullName());
                        }
                    }

                    // Remove the variables
                    subMsg.getVariables().clear();
                }

                // Set the variable(s) to available
                schedulerHndlr.makeVariableAvailable(excludedVars);
            }
            // User canceled the operation
            else
            {
                // Set the flag to indicate that the user canceled changing the
                // number of sub-messages
                isDeallocated = false;
            }
        }

        return isDeallocated;
    }

    /**************************************************************************
     * Get the list of current messages
     * 
     * @return List of current messages
     *************************************************************************/
    protected List<Message> getCurrentMessages()
    {
        return messages;
    }

    /**************************************************************************
     * Unused, but required for this class
     *************************************************************************/
    @Override
    protected CcddFrameHandler getTableEditor()
    {
        return null;
    }

    /**************************************************************************
     * Compare the current messages to the committed messages to detect any
     * changes
     * 
     * @return true if a message's content changed; false if no change exists
     *************************************************************************/
    protected boolean isMessagesChanged()
    {
        // Initialize the flag to true if the number of messages differs
        boolean isChanged = committedMessages.size() != messages.size();

        // Step through each message while no change is detected
        for (int msgIndex = 0; msgIndex < messages.size() && !isChanged; msgIndex++)
        {
            // Compare the message to its committed version
            isChanged = isMessageChanged(messages.get(msgIndex),
                                         committedMessages.get(msgIndex));

            // Step through each sub-message while no change is detected
            for (int subMsgIndex = 0; subMsgIndex < messages.get(msgIndex).getSubMessages().size() && !isChanged; subMsgIndex++)
            {
                // Compare the sub-message to its committed version
                isChanged = isMessageChanged(messages.get(msgIndex).getSubMessages().get(subMsgIndex),
                                             committedMessages.get(msgIndex).getSubMessages().get(subMsgIndex));
            }
        }

        return isChanged;
    }

    /**************************************************************************
     * Compare the current (sub-)message to the committed (sub-)message to
     * detect any changes
     * 
     * @param currMsg
     *            reference to the message's current values
     * 
     * @param commMsg
     *            reference to the message's original values
     * 
     * @return true if the (sub-)message's content changed; false if no change
     *         exists
     *************************************************************************/
    private boolean isMessageChanged(Message currMsg, Message commMsg)
    {
        boolean isChanged = false;

        // Get the list of variables for the current and committed messages
        List<Variable> currVars = currMsg.getVariables();
        List<Variable> commVars = commMsg.getVariables();

        // Check if the message number of bytes, name, number of variables, or
        // number of sub-messages changed
        if (currMsg.getBytesRemaining() != commMsg.getBytesRemaining()
            || !currMsg.getName().equals(commMsg.getName())
            || !currMsg.getID().equals(commMsg.getID())
            || currVars.size() != commVars.size()
            || currMsg.getNumberOfSubMessages() != commMsg.getNumberOfSubMessages())
        {
            isChanged = true;
        }
        // The message's number of bytes, name, number of variables, or number
        // of sub-messages is the same
        else
        {
            // Step through each variable
            for (int varIndex = 0; varIndex < currVars.size(); varIndex++)
            {
                // Get a reference to the current and committed variable to
                // make subsequent calls shorter
                Variable currVar = currVars.get(varIndex);
                Variable commVar = commVars.get(varIndex);

                // Check if the variable number of bytes, variable path or
                // name, or the variable rate changed
                if (currVar.getSize() != commVar.getSize()
                    || !currVar.getFullName().equals(commVar.getFullName())
                    || currVar.getRate() != commVar.getRate())
                {
                    // Set the flag indicating a change and stop searching
                    isChanged = true;
                    break;
                }
            }
        }

        return isChanged;
    }
}
