/**
 * CFS Command & Data Dictionary table type editor handler. Copyright 2017
 * United States Government as represented by the Administrator of the National
 * Aeronautics and Space Administration. No copyright is claimed in the United
 * States under Title 17, U.S. Code. All Other Rights Reserved.
 */
package CCDD;

import static CCDD.CcddConstants.CANCEL_BUTTON;
import static CCDD.CcddConstants.DISABLED_TEXT_COLOR;
import static CCDD.CcddConstants.TYPE_COMMAND;
import static CCDD.CcddConstants.TYPE_STRUCTURE;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import CCDD.CcddClasses.CCDDException;
import CCDD.CcddClasses.PaddedComboBox;
import CCDD.CcddConstants.DefaultColumn;
import CCDD.CcddConstants.DialogOption;
import CCDD.CcddConstants.InputDataType;
import CCDD.CcddConstants.ModifiableColorInfo;
import CCDD.CcddConstants.ModifiableFontInfo;
import CCDD.CcddConstants.TableSelectionMode;
import CCDD.CcddConstants.TableTypeEditorColumnInfo;
import CCDD.CcddTableTypeHandler.TypeDefinition;

/******************************************************************************
 * CFS Command & Data Dictionary table type editor handler class
 *****************************************************************************/
@SuppressWarnings("serial")
public class CcddTableTypeEditorHandler extends CcddInputFieldPanelHandler
{
    // Class references
    private String tableTypeName;
    private final CcddTableTypeEditorDialog editorDialog;
    private final CcddFieldHandler fieldHandler;
    private final CcddTableTypeHandler tableTypeHandler;
    private TypeDefinition typeDefinition;

    // Components referenced by multiple methods
    private CcddJTableHandler table;
    private PaddedComboBox comboBox;

    // Index for the table type editor's data type column
    private int inputTypeIndex;

    // Table instance model data. Committed copy is the table information as it
    // exists in the database and is used to determine what changes have been
    // made to the table since the previous database update
    private Object[][] committedData;

    // Storage for the most recent committed field information
    private CcddFieldHandler committedInfo;

    // Type description
    private String committedDescription;

    // Lists of type content changes to process
    private final List<String[]> typeAdditions;
    private final List<String[]> typeModifications;
    private final List<String[]> typeDeletions;

    // Flag indicating if a change in column order occurred
    private boolean columnOrderChange;

    // Flag indicating the table type definition has an invalid input type
    private boolean isBadType;

    // Table type indicator
    private TableTypeIndicator typeOfTable;

    // Table types
    protected static enum TableTypeIndicator
    {
        IS_STRUCTURE,
        IS_COMMAND,
        IS_OTHER;
    }

    /**************************************************************************
     * Table type editor handler class constructor
     * 
     * @param ccddMain
     *            main class
     * 
     * @param tableTypeName
     *            table type name
     * 
     * @param fieldDefinitions
     *            data field definitions
     * 
     * @param editorDialog
     *            editor dialog from which this editor was created
     *************************************************************************/
    protected CcddTableTypeEditorHandler(CcddMain ccddMain,
                                         String tableTypeName,
                                         Object[][] fieldDefinitions,
                                         CcddTableTypeEditorDialog editorDialog)
    {
        this.tableTypeName = tableTypeName;
        this.editorDialog = editorDialog;
        this.tableTypeHandler = ccddMain.getTableTypeHandler();

        // Create the field information for this table type
        fieldHandler = new CcddFieldHandler();
        fieldHandler.buildFieldInformation(fieldDefinitions,
                                           CcddFieldHandler.getFieldTypeName(tableTypeName));

        // Get the type definition for the specified type name
        typeDefinition = tableTypeHandler.getTypeDefinition(tableTypeName);

        // Check if the type definition exists
        if (typeDefinition != null)
        {
            // Store the type's data and description
            committedData = typeDefinition.getData();
            committedDescription = typeDefinition.getDescription();

            // Set the flag to indicate the current table type: structure,
            // command, or other
            typeOfTable = typeDefinition.isStructure()
                                                      ? TableTypeIndicator.IS_STRUCTURE
                                                      : (typeDefinition.isCommand()
                                                                                   ? TableTypeIndicator.IS_COMMAND
                                                                                   : TableTypeIndicator.IS_OTHER);
        }
        // This is a new type
        else
        {
            // Initialize the type data and description
            committedData = new Object[0][0];
            committedDescription = "";

            // Set the flag to indicate that the table type currently
            // represents neither a structure or a command
            typeOfTable = TableTypeIndicator.IS_OTHER;
        }

        isBadType = false;

        // Create a copy of the field information
        setCommittedInformation(fieldHandler);

        // Initialize the lists of type content changes
        typeAdditions = new ArrayList<String[]>();
        typeModifications = new ArrayList<String[]>();
        typeDeletions = new ArrayList<String[]>();

        // Create the table type editor
        initialize();
    }

    /**************************************************************************
     * Get the table handler
     * 
     * @return Table handler
     *************************************************************************/
    protected CcddJTableHandler getTable()
    {
        return table;
    }

    /**************************************************************************
     * Get the table type name
     * 
     * @return Table type name
     *************************************************************************/
    protected String getTypeName()
    {
        return tableTypeName;
    }

    /**************************************************************************
     * Get the reference to the table type definition as it exists prior to
     * making the updates
     * 
     * @return Reference to the table type definition as it exists prior to
     *         making the updates
     *************************************************************************/
    protected TypeDefinition getTypeDefinition()
    {
        return typeDefinition;
    }

    /**************************************************************************
     * Set the table type name
     * 
     * @param table
     *            table type name
     *************************************************************************/
    protected void setTableTypeName(String name)
    {
        tableTypeName = name;

        // Set the JTable name so that table change events can be identified
        // with this table
        table.setName(name);

        // Check if the table type has uncommitted changes
        if (isTableChanged())
        {
            // Send a change event so that the editor tab name reflects that
            // the table type has changed
            table.getUndoManager().ownerHasChanged();
        }
    }

    /**************************************************************************
     * Set the committed table information
     * 
     * @param info
     *            table information class for extracting the current table
     *            name, type, column order, and description
     *************************************************************************/
    private void setCommittedInformation(CcddFieldHandler handler)
    {
        // Create a new field handler and copy the current field information
        // into it
        committedInfo = new CcddFieldHandler();
        committedInfo.setFieldInformation(handler.getFieldInformationCopy());

        // Check if the table has been created
        if (table != null)
        {
            // Clear the undo/redo cell edits stack
            table.getUndoManager().discardAllEdits();
        }
    }

    /**************************************************************************
     * Get the UndoManager for this table editor
     * 
     * @return Table UndoManager
     *************************************************************************/
    @Override
    protected CcddUndoManager getFieldPanelUndoManager()
    {
        return table.getUndoManager();
    }

    /**************************************************************************
     * Get the column additions for this table type
     * 
     * @return List of column additions
     *************************************************************************/
    protected List<String[]> getTypeAdditions()
    {
        return typeAdditions;
    }

    /**************************************************************************
     * Get the column changes for this table type
     * 
     * @return List of column changes
     *************************************************************************/
    protected List<String[]> getTypeModifications()
    {
        return typeModifications;
    }

    /**************************************************************************
     * Get the column deletions for this table type
     * 
     * @return List of column deletions
     *************************************************************************/
    protected List<String[]> getTypeDeletions()
    {
        return typeDeletions;
    }

    /**************************************************************************
     * Get the column order change status
     * 
     * @return true if the table type's column order changed
     *************************************************************************/
    protected boolean getColumnOrderChange()
    {
        return columnOrderChange;
    }

    /**************************************************************************
     * Perform the steps needed following execution of table type changes
     * 
     * @param commandError
     *            false if the database commands successfully completed; true
     *            if an error occurred and the changes were not made
     *************************************************************************/
    protected void doTypeUpdatesComplete(boolean commandError)
    {
        // Update the reference to the altered table type definition
        typeDefinition = tableTypeHandler.getTypeDefinition(tableTypeName);

        // Check that no error occurred performing the database commands
        if (!commandError)
        {
            // Store the current table data and description as the last
            // committed
            committedData = table.getTableData(true);
            committedDescription = getDescription();
            setCommittedInformation(fieldHandler);

            // Send a change event so that the editor tab name reflects that
            // the table has changed
            table.getUndoManager().ownerHasChanged();

            // Clear the undo/redo cell edits stack
            table.getUndoManager().discardAllEdits();
        }
    }

    /**************************************************************************
     * Create the table type editor
     *************************************************************************/
    private void initialize()
    {
        // Define the table type editor JTable
        table = new CcddJTableHandler()
        {
            /******************************************************************
             * Return true if the type data, description, or data field changes
             *****************************************************************/
            @Override
            protected boolean isTableChanged(Object[][] data)
            {
                // Update the field information with the current text field
                // values
                updateCurrentFields(fieldHandler.getFieldInformation());

                // Set the change flag if the number of fields in the committed
                // version differs from the current version of the table
                boolean isFieldChanged = fieldHandler.getFieldInformation().size() != committedInfo.getFieldInformation().size();

                // Check if the number of fields is the same between the
                // committed and current versions
                if (!isFieldChanged)
                {
                    // Get the current and committed field descriptions
                    Object[][] current = fieldHandler.getFieldDefinitionArray(true);
                    Object[][] committed = committedInfo.getFieldDefinitionArray(true);

                    // Step through each field
                    for (int row = 0; row < current.length; row++)
                    {
                        // Step through each field member
                        for (int column = 0; column < current[row].length; column++)
                        {
                            // Check if the current and committed values differ
                            if (!current[row][column].equals(committed[row][column]))
                            {
                                // Set the flag indicating a field is changed
                                // and stop searching
                                isFieldChanged = true;
                                break;
                            }
                        }
                    }
                }

                return isFieldChanged
                       || !committedDescription.equals(getDescription())
                       || super.isTableChanged(data);
            }

            /******************************************************************
             * Allow resizing of the specified columns
             *****************************************************************/
            @Override
            protected boolean isColumnResizable(int column)
            {
                return column == TableTypeEditorColumnInfo.NAME.ordinal()
                       || column == TableTypeEditorColumnInfo.DESCRIPTION.ordinal()
                       || column == TableTypeEditorColumnInfo.INPUT_TYPE.ordinal();
            }

            /******************************************************************
             * Allow multiple line display in the specified columns
             *****************************************************************/
            @Override
            protected boolean isColumnMultiLine(int column)
            {
                return column == TableTypeEditorColumnInfo.NAME.ordinal()
                       || column == TableTypeEditorColumnInfo.DESCRIPTION.ordinal()
                       || column == TableTypeEditorColumnInfo.INPUT_TYPE.ordinal();
            }

            /******************************************************************
             * Override isCellEditable so that all columns can be edited
             *****************************************************************/
            @Override
            public boolean isCellEditable(int row, int column)
            {
                boolean isEditable = true;

                // Check if the table is displayable (to prevent corruption of
                // the cell editor), if the table model exists, and if the
                // table has at least one row
                if (isDisplayable()
                    && getModel() != null
                    && getModel().getRowCount() != 0)
                {
                    // Create storage for the row of table data
                    Object[] rowData = new Object[getModel().getColumnCount()];

                    // Convert the view row and column indices to model
                    // coordinates
                    int modelRow = convertRowIndexToModel(row);
                    int modelColumn = convertColumnIndexToModel(column);

                    // Step through each column in the row
                    for (int index = 0; index < rowData.length; index++)
                    {
                        // Store the column value into the row data array
                        rowData[index] = getModel().getValueAt(modelRow, index);
                    }

                    // Check if the cell is editable
                    isEditable = isDataAlterable(rowData, modelRow, modelColumn);
                }

                return isEditable;
            }

            /******************************************************************
             * Override isDataAlterable to determine which table data values
             * can be changed
             * 
             * @param rowData
             *            array containing the table row data
             * 
             * @param row
             *            table row index in model coordinates
             * 
             * @param column
             *            table column index in model coordinates
             * 
             * @return true if the data value can be changed
             *****************************************************************/
            @Override
            protected boolean isDataAlterable(Object[] rowData,
                                              int row,
                                              int column)
            {
                // Allow editing if:
                return
                // This is the column name or description
                // column
                column == TableTypeEditorColumnInfo.NAME.ordinal()
                    || column == TableTypeEditorColumnInfo.DESCRIPTION.ordinal()
                    || typeDefinition == null

                    // The column isn't protected...
                    || (!DefaultColumn.isProtectedColumn(typeDefinition.getName(),
                                                         rowData[TableTypeEditorColumnInfo.NAME.ordinal()].toString())

                    // ... and this isn't the structure allowed column, or it
                    // is and the input type isn't a rate or enumeration
                    && ((column != TableTypeEditorColumnInfo.STRUCTURE_ALLOWED.ordinal()
                    || (!rowData[TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].equals(InputDataType.RATE.getInputName())
                    && !rowData[TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].equals(InputDataType.ENUMERATION.getInputName())))

                    // ... and this isn't the pointer allowed column, or it is
                    // and the input type isn't a bit length or enumeration
                    && (column != TableTypeEditorColumnInfo.POINTER_ALLOWED.ordinal()
                    || (!rowData[TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].equals(InputDataType.BIT_LENGTH.getInputName())
                    && !rowData[TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].equals(InputDataType.ENUMERATION.getInputName())))));
            }

            /******************************************************************
             * Override the CcddJTableHandler method to prevent deleting the
             * contents of the cell at the specified row and column
             * 
             * @param row
             *            table row index in view coordinates
             * 
             * @param column
             *            table column index in view coordinates
             * 
             * @return false if the cell contains a combo box; true otherwise
             *****************************************************************/
            @Override
            protected boolean isCellBlankable(int row, int column)
            {
                return convertColumnIndexToModel(column) != TableTypeEditorColumnInfo.INPUT_TYPE.ordinal();
            }

            /******************************************************************
             * Validate changes to the editable cells
             * 
             * @param tableData
             *            list containing the table data row arrays
             * 
             * @param row
             *            table model row index
             * 
             * @param column
             *            table model column index
             * 
             * @param oldValue
             *            original cell contents
             * 
             * @param newValue
             *            new cell contents
             * 
             * @param showMessage
             *            true to display the invalid input dialog, if
             *            applicable
             * 
             * @param isMultiple
             *            true if this is one of multiple cells to be entered
             *            and checked; false if only a single input is being
             *            entered
             * 
             * @return Always returns false
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
                    // Check if the column name has been changed and if the
                    // name isn't blank
                    if (column == TableTypeEditorColumnInfo.NAME.ordinal()
                        && !newValueS.isEmpty())
                    {
                        // Check if the column name matches a default name
                        // (case insensitive)
                        if (newValueS.equalsIgnoreCase(DefaultColumn.PRIMARY_KEY.getDbName())
                            || newValueS.equalsIgnoreCase(DefaultColumn.ROW_INDEX.getDbName()))
                        {
                            throw new CCDDException("Column name '"
                                                    + newValueS
                                                    + "' already in use (hidden)");
                        }

                        // Get the database form of the column name
                        String dbName = DefaultColumn.convertVisibleToDatabase(newValueS,
                                                                               InputDataType.getInputTypeByName(tableData.get(row)[TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString()));

                        // Compare this column name to the others in the table
                        // in order to avoid creating a duplicate
                        for (int otherRow = 0; otherRow < getRowCount(); otherRow++)
                        {
                            // Check if this row isn't the one being edited,
                            if (otherRow != row)
                            {
                                // Check if the column name matches the one
                                // being added (case insensitive)
                                if (newValueS.equalsIgnoreCase(tableData.get(otherRow)[column].toString()))
                                {
                                    throw new CCDDException("Column name '"
                                                            + newValueS
                                                            + "' already in use");
                                }

                                // Check if the database form of the column
                                // name matches matches the database form of
                                // the one being added
                                if (dbName.equalsIgnoreCase(DefaultColumn.convertVisibleToDatabase(tableData.get(otherRow)[TableTypeEditorColumnInfo.NAME.ordinal()].toString(),
                                                                                                   InputDataType.getInputTypeByName(tableData.get(otherRow)[TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString()))))
                                {
                                    throw new CCDDException("Column name '"
                                                            + newValueS
                                                            + "' already in use (database)");
                                }
                            }
                        }
                    }
                    // Check if a non-boolean value is being put into a cell
                    // that expects a boolean value
                    else if ((column == TableTypeEditorColumnInfo.UNIQUE.ordinal()
                             || column == TableTypeEditorColumnInfo.REQUIRED.ordinal())
                             && !newValue.equals(true)
                             && !newValue.equals(false))
                    {
                        throw new CCDDException("Column '"
                                                + TableTypeEditorColumnInfo.getColumnNames()[column]
                                                + "' expects a boolean value");
                    }
                    // Check if this is the input type column
                    else if (column == TableTypeEditorColumnInfo.INPUT_TYPE.ordinal())
                    {
                        // Check if the input type is disabled
                        if (newValueS.startsWith(DISABLED_TEXT_COLOR))
                        {
                            throw new CCDDException();
                        }

                        // Check if the input type is invalid
                        if (InputDataType.getInputTypeByName(newValueS) == null)
                        {
                            throw new CCDDException("Unknown input type '"
                                                    + newValueS
                                                    + "'");
                        }

                        // Get the table type (structure, command, or other)
                        // based on the column definition input types
                        TableTypeIndicator typeOfTableNew = getTypeOfTable();

                        // Check if the table type changed and if the table
                        // type represents a structure or command
                        if (typeOfTableNew != typeOfTable
                            && typeOfTableNew != TableTypeIndicator.IS_OTHER)
                        {
                            // Get the invalid input types, if any
                            String msg = getInvalidInputTypes(typeOfTableNew);

                            // Check if an input type defined as unique is
                            // used more than once in the table type
                            // definition
                            if (!msg.isEmpty())
                            {
                                throw new CCDDException(msg);
                            }
                        }
                    }
                }
                catch (CCDDException ce)
                {
                    // Set the flag that indicates the last edited cell's
                    // content is invalid
                    setLastCellValid(false);

                    // Check if the input error dialog should be displayed
                    if (showMessage && !ce.getMessage().isEmpty())
                    {
                        // Inform the user that the input value is invalid
                        new CcddDialogHandler().showMessageDialog(editorDialog,
                                                                  "<html><b>"
                                                                      + ce.getMessage(),
                                                                  "Invalid Input",
                                                                  JOptionPane.WARNING_MESSAGE,
                                                                  DialogOption.OK_OPTION);
                    }

                    // Restore the cell contents to its original value
                    tableData.get(row)[column] = oldValue;
                    table.getUndoManager().undoRemoveEdit();
                }

                return showMessage;
            }

            /******************************************************************
             * Load the table type definition values into the table and format
             * the table cells
             *****************************************************************/
            @Override
            protected void loadAndFormatData()
            {
                // Create a list for any columns to be hidden
                List<Integer> hiddenColumns = new ArrayList<Integer>();

                // Hide the index column
                hiddenColumns.add(TableTypeEditorColumnInfo.INDEX.ordinal());

                // Check if the columns only applicable to a structure table
                // should be hidden
                if (typeOfTable != TableTypeIndicator.IS_STRUCTURE)
                {
                    // Hide the structure table type columns
                    hiddenColumns.add(TableTypeEditorColumnInfo.STRUCTURE_ALLOWED.ordinal());
                    hiddenColumns.add(TableTypeEditorColumnInfo.POINTER_ALLOWED.ordinal());
                }

                // Place the data into the table model along with the column
                // names, set up the editors and renderers for the table cells,
                // set up the table grid lines, and calculate the minimum width
                // required to display the table information
                int totalWidth = setUpdatableCharacteristics(committedData,
                                                             TableTypeEditorColumnInfo.getColumnNames(),
                                                             null,
                                                             hiddenColumns.toArray(new Integer[0]),
                                                             new Integer[] {TableTypeEditorColumnInfo.UNIQUE.ordinal(),
                                                                            TableTypeEditorColumnInfo.REQUIRED.ordinal(),
                                                                            TableTypeEditorColumnInfo.STRUCTURE_ALLOWED.ordinal(),
                                                                            TableTypeEditorColumnInfo.POINTER_ALLOWED.ordinal()},
                                                             TableTypeEditorColumnInfo.getToolTips(),
                                                             true,
                                                             true,
                                                             true,
                                                             true);

                // Check if this is the widest editor table in this tabbed
                // editor dialog
                if (editorDialog.getTableWidth() < totalWidth)
                {
                    // Set the minimum table size based on the column widths
                    editorDialog.setTableWidth(totalWidth);
                }
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

                // Check if the cell doesn't have the focus or is selected. The
                // focus and selection highlight colors override the invalid
                // highlight color
                if (comp.getBackground() != ModifiableColorInfo.FOCUS_BACK.getColor()
                    && comp.getBackground() != ModifiableColorInfo.SELECTED_BACK.getColor())
                {
                    boolean found = true;
                    String value = table.getValueAt(row, column).toString();

                    // Check if the cell is required and is empty
                    if (TableTypeEditorColumnInfo.values()[table.convertColumnIndexToModel(column)].isRequired()
                        && value.isEmpty())
                    {
                        // Set the flag indicating that the cell value is
                        // invalid
                        found = false;
                    }
                    // Check if this is the input type column
                    else if (column == inputTypeIndex && comboBox != null)
                    {
                        found = false;

                        // Step through each combo box item
                        for (int index = 0; index < comboBox.getItemCount() && !found; index++)
                        {
                            // Check if the cell matches the combo box item.
                            // Remove any HTML tags in case this item is
                            // displayed as disabled
                            if (CcddUtilities.removeHTMLTags(comboBox.getItemAt(index)).equals(value))
                            {
                                // Set the flag indicating that the cell value
                                // is valid and stop searching
                                found = true;
                                break;
                            }
                        }
                    }

                    // Check if the cell value is invalid
                    if (!found)
                    {
                        // Change the cell's background color
                        comp.setBackground(ModifiableColorInfo.REQUIRED_BACK.getColor());
                    }
                    // Check if this cell is protected from changes
                    else if (!isCellEditable(row, column))
                    {
                        // Shade the cell's background
                        comp.setForeground(ModifiableColorInfo.PROTECTED_TEXT.getColor());
                        comp.setBackground(ModifiableColorInfo.PROTECTED_BACK.getColor());
                    }
                }

                return comp;
            }

            /******************************************************************
             * Override the CcddJTableHandler method to produce an array
             * containing empty values for a new row in this table
             * 
             * @return Array containing blank cell values for a new row
             *****************************************************************/
            @Override
            protected Object[] getEmptyRow()
            {
                return TableTypeEditorColumnInfo.getEmptyRow();
            }

            /******************************************************************
             * Override the CcddJTableHandler method for removing a row from
             * the table. Don't allow deletion of a row that represents a
             * protected column definition for this table type
             * 
             * @param tableData
             *            list containing the table data row arrays
             * 
             * @param modelRow
             *            row to remove (model coordinates)
             * 
             * @return The index of the row prior to the last deleted row's
             *         index
             *****************************************************************/
            @Override
            protected int removeRow(List<Object[]> tableData, int modelRow)
            {
                // Check if this row doesn't represent a protected column
                // definition (i.e., isn't a default column for the table type)
                if (typeDefinition == null
                    || !DefaultColumn.isProtectedColumn(typeDefinition.getName(),
                                                        tableData.get(modelRow)[TableTypeEditorColumnInfo.NAME.ordinal()].toString()))
                {
                    // Remove the row from the table
                    modelRow = super.removeRow(tableData, modelRow);
                }
                // This row represents a protected column definition
                else
                {
                    // Adjust the row index, but don't delete the row
                    modelRow--;
                }

                return modelRow;
            }

            /******************************************************************
             * Handle a change to the table's content
             *****************************************************************/
            @Override
            protected void processTableContentChange()
            {
                // Check if there are no duplicated input types that are
                // defined as unique
                if (!isBadType)
                {
                    // Get the table type based on the column definition input
                    // types
                    TableTypeIndicator typeOfTableNew = getTypeOfTable();

                    // Check if the table type changed to/from representing a
                    // structure
                    if (typeOfTableNew != typeOfTable)
                    {
                        // Store the new table type
                        typeOfTable = typeOfTableNew;

                        // Show/hide the structure table type specific editor
                        // columns
                        table.showHiddenColumns(typeOfTableNew == TableTypeIndicator.IS_STRUCTURE,
                                                new Integer[] {TableTypeEditorColumnInfo.STRUCTURE_ALLOWED.ordinal(),
                                                               TableTypeEditorColumnInfo.POINTER_ALLOWED.ordinal()});

                        // Update the input type combo box item list, enabling
                        // and/or disabling items based on those currently in
                        // use
                        comboBox.setModel(new DefaultComboBoxModel<String>(getInputTypeNames()));
                    }

                    // Update the change indicator for the table
                    editorDialog.updateChangeIndicator(CcddTableTypeEditorHandler.this);
                }

                // Reset the bad input type flag so that subsequent table type
                // changes are processed
                isBadType = false;
            }
        };

        // Place the table into a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Set common table parameters and characteristics
        table.setFixedCharacteristics(scrollPane,
                                      true,
                                      ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
                                      TableSelectionMode.SELECT_BY_CELL,
                                      false,
                                      ModifiableColorInfo.TABLE_BACK.getColor(),
                                      true,
                                      true,
                                      ModifiableFontInfo.DATA_TABLE_CELL.getFont(),
                                      true);

        // Discard the edits created by adding the columns initially
        table.getUndoManager().discardAllEdits();

        // Create a drop-down combo box to display the available table type
        // input data types
        setUpInputTypeColumn();

        // Set the reference to the editor's data field handler in the undo
        // handler so that data field value changes can be undone/redone
        // correctly
        table.getUndoHandler().setFieldHandler(fieldHandler);

        // Set the undo/redo manager and handler for the description and data
        // field values
        setEditPanelUndo(table.getUndoManager(), table.getUndoHandler());

        // Create the input field panel to contain the type editor
        createDescAndDataFieldPanel(editorDialog,
                                    scrollPane,
                                    tableTypeName,
                                    committedDescription,
                                    fieldHandler);

        // Set the JTable name so that table change events can be identified
        // with this table
        setTableTypeName(tableTypeName);
    }

    /**************************************************************************
     * Get the table type based on the column definition input types
     * 
     * @return Table type indicator
     *************************************************************************/
    private TableTypeIndicator getTypeOfTable()
    {
        // Set the flags that indicate if the table type currently represents a
        // structure or a command
        boolean isStructure = true;
        boolean isCommand = true;

        // Step through the target table types: Structure and Command
        for (String tableType : new String[] {TYPE_STRUCTURE, TYPE_COMMAND})
        {
            // Step through each of the default columns
            for (DefaultColumn defColumn : DefaultColumn.values())
            {
                // Check if this column belongs to the target table type and
                // that it is a protected column
                if (defColumn.getTableType().equals(tableType)
                    && defColumn.isProtected())
                {
                    boolean isFound = false;

                    // Step through each row in the table
                    for (int tableRow = 0; tableRow < table.getModel().getRowCount(); tableRow++)
                    {
                        // Check if the input type column value matches the
                        // target table type input type
                        if (table.getModel().getValueAt(tableRow,
                                                        TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()).equals(defColumn.getInputType().getInputName()))
                        {
                            // Set the flag to indicate the target table type
                            // input type is in use and stop searching
                            isFound = true;
                            break;
                        }
                    }

                    // Check if a target table type input type is missing
                    if (!isFound)
                    {
                        // Check if the target table type represents a
                        // structure
                        if (tableType.equals(TYPE_STRUCTURE))
                        {
                            // Set the flag to indicate that this table type
                            // doesn't have all of the structure type's columns
                            isStructure = false;
                        }
                        // The target table type represents a command
                        else
                        {
                            // Set the flag to indicate that this table type
                            // doesn't have all of the command type's columns
                            isCommand = false;
                        }

                        break;
                    }
                }
            }
        }

        return isStructure
                          ? TableTypeIndicator.IS_STRUCTURE
                          : (isCommand
                                      ? TableTypeIndicator.IS_COMMAND
                                      : TableTypeIndicator.IS_OTHER);
    }

    /**************************************************************************
     * Get the input types that are defined as unique, but are referenced by
     * more than one column definition
     * 
     * @param tableTypeInd
     *            TableTypeIndicator, indicating the type of table to check
     * 
     * @return Blank if there are no duplicated input type that are defined as
     *         unique; otherwise, a text message indicating the invalid input
     *         types
     *************************************************************************/
    private String getInvalidInputTypes(TableTypeIndicator tableTypeInd)
    {
        String invalidTypes = "";

        // Get the table type from the table type indicator
        String tableType = tableTypeInd == TableTypeIndicator.IS_STRUCTURE
                                                                          ? TYPE_STRUCTURE
                                                                          : (tableTypeInd == TableTypeIndicator.IS_COMMAND
                                                                                                                          ? TYPE_COMMAND
                                                                                                                          : null);

        // Check if the table type currently represents a structure or command
        if (tableType != null)
        {
            // Get the table type data array
            Object[][] typeData = table.getTableData(true);
            boolean[] checkedRow = new boolean[typeData.length];

            // Step through each row (column definition) in the table type
            for (int row = 0; row < typeData.length - 1; row++)
            {
                // Set the flag indicating this row has been checked
                checkedRow[row] = true;

                // Get the column definition's input type
                String inputType = typeData[row][TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString();

                // Check if the input type must be unique for the target table
                // type
                if (DefaultColumn.isInputTypeUnique(tableType, inputType))
                {
                    // Step through the remaining row (column definitions)
                    for (int remRow = row + 1; remRow < typeData.length; remRow++)
                    {
                        // Check if the row hasn't already been found to be a
                        // duplicate and if the input type for the current row
                        // matches the input type for this row
                        if (!checkedRow[remRow]
                            && inputType.equals(typeData[remRow][TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString()))
                        {
                            // This is a duplicate of an input type that must
                            // be unique. Add the type to the list and set the
                            // flag indicating the row has been found to be
                            // invalid
                            invalidTypes += inputType + ", ";
                            checkedRow[remRow] = true;
                        }
                    }
                }
            }
        }

        // Check if an invalid input type exists
        if (!invalidTypes.isEmpty())
        {
            // Update the invalid input type message
            invalidTypes = "Tables of type '"
                           + tableType
                           + "' may not have more than one column with "
                           + "input type(s):</b><br>&#160;&#160;&#160;"
                           + CcddUtilities.removeTrailer(invalidTypes, ", ");

            // Set the flag to indicate the table type definition has an
            // invalid input type. This flag is used to inhibit processing of
            // the table content change
            isBadType = true;
        }

        return invalidTypes;
    }

    /**************************************************************************
     * Get the item names for the combo box containing the available table type
     * input data types for display in the table's Input Type cells.
     * Enable/disable the items based on the current usage in the table type
     * and the type's input type flag settings
     * 
     * @return Array of item names for the combo box containing the available
     *         table type input data types for display in the table's Input
     *         Type cells
     *************************************************************************/
    private String[] getInputTypeNames()
    {
        // Get the list of all input data types
        String[] inputNames = InputDataType.getInputNames(true);

        // Step through each row in the table type
        for (int row = 0; row < table.getRowCount(); row++)
        {
            // Step through each input type
            for (int index = 0; index < inputNames.length; index++)
            {
                // Get the input type for this row
                String inputType = table.getModel().getValueAt(row, TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()).toString();

                // Get the input name without any HTML tags
                String inputName = CcddUtilities.removeHTMLTags(inputNames[index]);

                // Check if the input type should be disabled based on the
                // following criteria:
                if ((
                    // The input type matches the one on this row and this
                    // input type can only be used once in a table type
                    (inputType.equals(inputNames[index])
                    && ((typeOfTable == TableTypeIndicator.IS_STRUCTURE &&
                    DefaultColumn.isInputTypeUnique(TYPE_STRUCTURE, inputNames[index]))
                    || (typeOfTable == TableTypeIndicator.IS_COMMAND &&
                    DefaultColumn.isInputTypeUnique(TYPE_COMMAND, inputNames[index])))))

                    // The table type doesn't represent a structure and the
                    // input type is the variable path
                    || (typeOfTable != TableTypeIndicator.IS_STRUCTURE
                    && inputName.equals(InputDataType.VARIABLE_PATH.getInputName()))

                    // The input type is for a primitive+structure data type
                    // and this is the primitive data type input type
                    || (inputType.equals(InputDataType.PRIM_AND_STRUCT.getInputName())
                    && inputName.equals(InputDataType.PRIMITIVE.getInputName()))

                    // The input type is for a primitive data type and this is
                    // the primitive+structure data type input type
                    || (inputType.equals(InputDataType.PRIMITIVE.getInputName())
                    && inputName.equals(InputDataType.PRIM_AND_STRUCT.getInputName())))
                {
                    // Set the input type so that it appears disabled in the
                    // list
                    inputNames[index] = DISABLED_TEXT_COLOR + inputNames[index];
                }
            }
        }

        return inputNames;
    }

    /**************************************************************************
     * Set up the combo box containing the available table type input data
     * types for display in the table's Input Type cells
     *************************************************************************/
    private void setUpInputTypeColumn()
    {
        // Create a combo box for displaying table type input types
        comboBox = new PaddedComboBox(getInputTypeNames(),
                                      InputDataType.getDescriptions(true),
                                      ModifiableFontInfo.DATA_TABLE_CELL.getFont())
        {
            /******************************************************************
             * Override so that items flagged as disabled (grayed out) are
             * correctly identified and selected
             *****************************************************************/
            @Override
            public void setSelectedItem(Object anObject)
            {
                // Check if the specified item with the disable tag prepended
                // is in the combo box list
                if (getIndexOfItem(DISABLED_TEXT_COLOR +
                                   anObject.toString()) != -1)
                {
                    // Update the specified item to include the disable tag
                    anObject = DISABLED_TEXT_COLOR + anObject;
                }

                // Set the selected item to the specified item, if it exists in
                // the list
                super.setSelectedItem(anObject);
            }
        };

        // Add a listener to the combo box for focus changes
        comboBox.addFocusListener(new FocusAdapter()
        {
            /******************************************************************
             * Handle a focus gained event so that the combo box automatically
             * expands when selected
             *****************************************************************/
            @Override
            public void focusGained(FocusEvent fe)
            {
                comboBox.showPopup();
            }
        });

        // Get the index of the input data type column in view coordinates
        inputTypeIndex = table.convertColumnIndexToView(TableTypeEditorColumnInfo.INPUT_TYPE.ordinal());

        // Set the column table editor to the combo box
        table.getColumnModel().getColumn(inputTypeIndex).setCellEditor(new DefaultCellEditor(comboBox));

        // Set the default selected type
        comboBox.setSelectedItem(InputDataType.TEXT.getInputName());
    }

    /**************************************************************************
     * Determine if any changes have been made compared to the most recently
     * committed table data
     * 
     * @return true if any cell in the table has been changed, if the column
     *         order has changed, or if the table description has changed
     *************************************************************************/
    protected boolean isTableChanged()
    {
        return table.isTableChanged(committedData);
    }

    /**************************************************************************
     * Compare the current table type data to the committed table type data and
     * create lists of the changed values necessary to update the table
     * definitions table in the database to match the current values
     *************************************************************************/
    protected void buildUpdates()
    {
        // Get the table type data array
        Object[][] typeData = table.getTableData(true);

        // Create/replace the type definition
        tableTypeHandler.createTypeDefinition(tableTypeName,
                                              typeData,
                                              getDescription());

        // Remove existing changes, if any
        typeAdditions.clear();
        typeModifications.clear();
        typeDeletions.clear();

        // Initialize the column order change status
        columnOrderChange = false;

        // Create storage for flags that indicate if a row has been matched
        boolean[] rowFound = new boolean[committedData.length];

        // Step through each row of the current data
        for (int tblRow = 0; tblRow < typeData.length; tblRow++)
        {
            boolean matchFound = false;

            // Get the current column name
            String currColumnName = typeData[tblRow][TableTypeEditorColumnInfo.NAME.ordinal()].toString();

            // Step through each row of the committed data
            for (int comRow = 0; comRow < committedData.length; comRow++)
            {
                // Get the previous column name
                String prevColumnName = committedData[comRow][TableTypeEditorColumnInfo.NAME.ordinal()].toString();

                // Check if the committed row hasn't already been matched and
                // if the current and committed column indices are the same
                if (!rowFound[comRow]
                    && typeData[tblRow][TableTypeEditorColumnInfo.INDEX.ordinal()].equals(committedData[comRow][TableTypeEditorColumnInfo.INDEX.ordinal()]))
                {
                    // Set the flags indicating a matching row has been found
                    rowFound[comRow] = true;
                    matchFound = true;

                    // Check if the previous and current column definition row
                    // is different
                    if (tblRow != comRow)
                    {
                        // Set the flag indicating the column order changed
                        columnOrderChange = true;
                    }

                    // Get the original and current input data type
                    String oldInputType = committedData[comRow][TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString();
                    String newInputType = typeData[tblRow][TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString();

                    // Check if the column name changed or if the input type
                    // changed to/from a rate
                    if (!prevColumnName.equals(currColumnName)
                        || ((newInputType.equals(InputDataType.RATE.getInputName())
                        || oldInputType.equals(InputDataType.RATE.getInputName()))
                        && !newInputType.equals(oldInputType)))
                    {
                        // The column name is changed. Add the new and old
                        // column names to the list
                        typeModifications.add(new String[] {prevColumnName,
                                                            currColumnName,
                                                            oldInputType,
                                                            newInputType});
                    }

                    // Stop searching since a match exists
                    break;
                }
            }

            // Check if no match was made with the committed data for the
            // current table row
            if (!matchFound)
            {
                // The column definition is being added; add the column name to
                // the list
                typeAdditions.add(new String[] {currColumnName,
                                                typeData[tblRow][TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString()});
            }
        }

        // Step through each row of the committed data
        for (int comRow = 0; comRow < committedData.length; comRow++)
        {
            // Check if no matching row was found with the current data
            if (!rowFound[comRow])
            {
                // The column definition has been deleted; add the column name
                // and input type to the list
                typeDeletions.add(new String[] {committedData[comRow][TableTypeEditorColumnInfo.NAME.ordinal()].toString(),
                                                committedData[comRow][TableTypeEditorColumnInfo.INPUT_TYPE.ordinal()].toString()});
            }
        }
    }

    /**************************************************************************
     * Check that a row with contains data in the required columns
     * 
     * @return true if a row is missing data in a required column
     *************************************************************************/
    protected boolean checkForMissingColumns()
    {
        boolean dataIsMissing = false;
        boolean stopCheck = false;

        // Step through each row in the table
        for (int row = 0; row < table.getRowCount() && !stopCheck; row++)
        {
            // Skip rows in the table that are empty
            row = table.getNextPopulatedRowNumber(row);

            // Check that the end of the table hasn't been reached
            if (row < table.getRowCount())
            {
                // Step through each column in the row
                for (int column = 0; column < table.getColumnCount() && !stopCheck; column++)
                {
                    // Check if the cell is required and is empty
                    if (column == table.convertColumnIndexToView(TableTypeEditorColumnInfo.NAME.ordinal())
                        && table.getValueAt(row, column).toString().isEmpty())
                    {
                        // Set the 'data is missing' flag
                        dataIsMissing = true;

                        // Inform the user that a row is missing required data.
                        // If Cancel is selected then do not perform checks on
                        // other columns and rows
                        if (new CcddDialogHandler().showMessageDialog(editorDialog,
                                                                      "<html><b>Data must be provided for column '"
                                                                          + table.getColumnName(column)
                                                                          + "' [row "
                                                                          + (row + 1)
                                                                          + "]",
                                                                      "Missing Data",
                                                                      JOptionPane.WARNING_MESSAGE,
                                                                      DialogOption.OK_CANCEL_OPTION) == CANCEL_BUTTON)
                        {
                            // Set the stop flag to prevent further error
                            // checking
                            stopCheck = true;
                        }

                        break;
                    }
                }
            }
        }

        return dataIsMissing;
    }

    /**************************************************************************
     * Update the tab for this table in the table editor dialog change
     * indicator
     *************************************************************************/
    @Override
    protected void updateOwnerChangeIndicator()
    {
        editorDialog.updateChangeIndicator(this);
    }
}
