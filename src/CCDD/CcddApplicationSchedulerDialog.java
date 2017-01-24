/**
 * CFS Command & Data Dictionary application scheduler dialog. Copyright 2017
 * United States Government as represented by the Administrator of the National
 * Aeronautics and Space Administration. No copyright is claimed in the United
 * States under Title 17, U.S. Code. All Other Rights Reserved.
 */
package CCDD;

import static CCDD.CcddConstants.AUTO_CREATE_ICON;
import static CCDD.CcddConstants.CLOSE_ICON;
import static CCDD.CcddConstants.OK_BUTTON;
import static CCDD.CcddConstants.OK_ICON;
import static CCDD.CcddConstants.STORE_ICON;
import static CCDD.CcddConstants.UNDO_ICON;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import CCDD.CcddClasses.DataStream;
import CCDD.CcddClasses.ValidateCellActionListener;
import CCDD.CcddClasses.Variable;
import CCDD.CcddConstants.DialogOption;
import CCDD.CcddConstants.SchedulerType;

/******************************************************************************
 * CFS Command & Data Dictionary application scheduler dialog class
 *****************************************************************************/
@SuppressWarnings("serial")
public class CcddApplicationSchedulerDialog extends CcddDialogHandler implements CcddSchedulerDialogInterface
{
    // Class references
    private final CcddMain ccddMain;
    private CcddSchedulerHandler schedulerHndlr;
    private final CcddSchedulerDbIOHandler schedulerDb;
    private final CcddSchedulerValidator validator;

    // Components referenced by multiple methods
    private JButton btnAutoFill;
    private JButton btnValidate;
    private JButton btnStore;
    private JButton btnClear;
    private JButton btnClose;

    /**************************************************************************
     * Application scheduler dialog class constructor
     * 
     * @param ccddMain
     *            main class
     *************************************************************************/
    CcddApplicationSchedulerDialog(CcddMain ccddMain)
    {
        this.ccddMain = ccddMain;
        schedulerDb = new CcddSchedulerDbIOHandler(ccddMain,
                                                   SchedulerType.APPLICATION_SCHEDULER,
                                                   this);
        validator = new CcddSchedulerValidator(ccddMain, this);

        // Create the application scheduler dialog
        initialize();
    }

    /**************************************************************************
     * Create the application scheduler dialog
     *************************************************************************/
    private void initialize()
    {
        // Create a button panel
        JPanel buttonPanel = new JPanel();

        // Load the stored application data into the data streams
        schedulerDb.loadStoredData();

        try
        {
            // Create the application scheduler
            schedulerHndlr = new CcddSchedulerHandler(ccddMain, "0", this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // Auto-fill button
        btnAutoFill = CcddButtonPanelHandler.createButton("Auto-fill",
                                                          AUTO_CREATE_ICON,
                                                          KeyEvent.VK_A,
                                                          "Auto-fill the message table with the variables");

        // Create a listener for the Auto-fill button
        btnAutoFill.addActionListener(new ValidateCellActionListener(schedulerHndlr.getSchedulerEditor().getTable())
        {
            /******************************************************************
             * Auto-fill the applications into the application scheduler
             *****************************************************************/
            @Override
            protected void performAction(ActionEvent ae)
            {
                // Run auto-fill
                schedulerHndlr.autoFill();
            }
        });

        // Clear Slots button
        btnClear = CcddButtonPanelHandler.createButton("Clear Slots",
                                                       UNDO_ICON,
                                                       KeyEvent.VK_R,
                                                       "Remove applications from the time slots");
        // Add a listener for the Clear Slots button
        btnClear.addActionListener(new ValidateCellActionListener(schedulerHndlr.getSchedulerEditor().getTable())
        {
            /******************************************************************
             * Remove the applications from all time slots
             *****************************************************************/
            @Override
            protected void performAction(ActionEvent ae)
            {
                schedulerHndlr.getSchedulerEditor().clearMessages();
            }
        });

        // Validate button
        btnValidate = CcddButtonPanelHandler.createButton("Validate",
                                                          OK_ICON,
                                                          KeyEvent.VK_V,
                                                          "Remove any invalid applications from the messages");

        // Add a listener for the Validate button
        btnValidate.addActionListener(new ValidateCellActionListener(schedulerHndlr.getSchedulerEditor().getTable())
        {
            /******************************************************************
             * Validate the applications for all of the messages
             *****************************************************************/
            @Override
            protected void performAction(ActionEvent ae)
            {
                // Check if any time slots contain invalid entries
                if (schedulerHndlr.validateMessages())
                {
                    // Update the assigned applications list
                    schedulerHndlr.getSchedulerEditor().updateAssignmentDefinitions();
                    schedulerHndlr.getSchedulerEditor().updateAssignmentList();
                }
            }
        });

        // Store button
        btnStore = CcddButtonPanelHandler.createButton("Store",
                                                       STORE_ICON,
                                                       KeyEvent.VK_S,
                                                       "Store the application updates in the project database");
        // Add a listener for the Store button
        btnStore.addActionListener(new ValidateCellActionListener(schedulerHndlr.getSchedulerEditor().getTable())
        {
            /******************************************************************
             * Store the application scheduler data in the project database
             *****************************************************************/
            @Override
            protected void performAction(ActionEvent ae)
            {
                // Check if any message has changed and, if so, that the user
                // confirms storing the changes
                if (schedulerHndlr.getSchedulerEditor().isMessagesChanged()
                    && new CcddDialogHandler().showMessageDialog(CcddApplicationSchedulerDialog.this,
                                                                 "<html><b>Store changes?",
                                                                 "Store Changes",
                                                                 JOptionPane.QUESTION_MESSAGE,
                                                                 DialogOption.OK_CANCEL_OPTION) == OK_BUTTON)
                {
                    // Store the messages in the project database
                    storeData();
                }
            }
        });

        // Close button
        btnClose = CcddButtonPanelHandler.createButton("Close",
                                                       CLOSE_ICON,
                                                       KeyEvent.VK_C,
                                                       "Close the application scheduler");
        // Add a listener for the Close button
        btnClose.addActionListener(new ValidateCellActionListener(schedulerHndlr.getSchedulerEditor().getTable())
        {
            /******************************************************************
             * Close the application scheduler dialog
             *****************************************************************/
            @Override
            protected void performAction(ActionEvent ae)
            {
                windowCloseButtonAction();
            }
        });

        // Invisible button for aligning the buttons in the button panel
        JButton btnDummy = new JButton("");
        btnDummy.setFocusable(false);
        btnDummy.setOpaque(false);
        btnDummy.setContentAreaFilled(false);
        btnDummy.setBorderPainted(false);

        // Add buttons in the order in which they'll appear (left to right)
        buttonPanel.add(btnAutoFill);
        buttonPanel.add(btnValidate);
        buttonPanel.add(btnStore);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnDummy);
        buttonPanel.add(btnClose);

        // Create two rows of buttons
        setButtonRows(2);

        // Display the application scheduler dialog
        showOptionsDialog(ccddMain.getMainFrame(),
                          schedulerHndlr.getSchedulerPanel(),
                          buttonPanel,
                          "Application Scheduler",
                          true);
    }

    /**************************************************************************
     * Store the data into the database
     *************************************************************************/
    private void storeData()
    {
        // Create a data stream to pass into the schedule database handler
        List<DataStream> stream = new ArrayList<DataStream>();

        // Add the messages to the data stream
        stream.add(new DataStream(schedulerHndlr.getCurrentMessages(),
                                  new ArrayList<Variable>()));

        // Update the copy of the messages so that subsequent changes can
        // be detected
        schedulerHndlr.getSchedulerEditor().copyMessages();

        // Pass the data stream into the scheduler database handler
        schedulerDb.storeData(stream);
    }

    /**************************************************************************
     * Handle the dialog close button press event
     *************************************************************************/
    @Override
    protected void windowCloseButtonAction()
    {
        // Check if the contents of the last cell edited in the scheduler table
        // is validated and that no message has changed. If a change exists
        // then confirm discarding the changes
        if (schedulerHndlr.getSchedulerEditor().getTable().isLastCellValid()
            && (!schedulerHndlr.getSchedulerEditor().isMessagesChanged()
            || new CcddDialogHandler().showMessageDialog(CcddApplicationSchedulerDialog.this,
                                                         "<html><b>Discard changes?",
                                                         "Discard Changes",
                                                         JOptionPane.QUESTION_MESSAGE,
                                                         DialogOption.OK_CANCEL_OPTION) == OK_BUTTON))
        {
            // Close the application scheduler dialog
            closeDialog();
        }
    }

    /**************************************************************************
     * Get the scheduler dialog
     * 
     * @return Scheduler dialog
     *************************************************************************/
    @Override
    public CcddDialogHandler getDialog()
    {
        return CcddApplicationSchedulerDialog.this;
    }

    /**************************************************************************
     * Enable/disable the dialog controls
     * 
     * @param enable
     *            true to enable the controls, false to disable
     *************************************************************************/
    @Override
    public void setControlsEnabled(boolean enable)
    {
        super.setControlsEnabled(enable);
        schedulerHndlr.setArrowsEnabled(enable);
    }

    /**************************************************************************
     * Gets the scheduler database handler
     * 
     * @return Schedule database handler
     *************************************************************************/
    @Override
    public CcddSchedulerDbIOHandler getSchedulerDatabaseHandler()
    {
        return schedulerDb;
    }

    /**************************************************************************
     * Gets the scheduler validator
     * 
     * @return Schedule validator
     *************************************************************************/
    @Override
    public CcddSchedulerValidator getSchedulerValidator()
    {
        return validator;
    }

    /**************************************************************************
     * Creates and returns a scheduler input object
     * 
     * @param unused
     *            not used for the application scheduler dialog
     * 
     * @return Application input object
     *************************************************************************/
    @Override
    public CcddSchedulerInputInterface createSchedulerInput(String unused)
    {
        return new CcddApplicationSchedulerInput(ccddMain, this);
    }

    /**************************************************************************
     * Get the scheduler handler
     * 
     * @return Scheduler handler
     *************************************************************************/
    @Override
    public CcddSchedulerHandler getSchedulerHandler()
    {
        return schedulerHndlr;
    }
}
