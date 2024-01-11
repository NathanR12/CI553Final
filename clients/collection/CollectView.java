package clients.collection;

import middle.MiddleFactory;
import middle.OrderProcessing;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Customer view with a pop-up menu for reviews.
 * Allows users to provide additional feedback after selecting a review.
 * @author  Mike Smith University of Brighton
 * @version 2.0
 */

public class CollectView implements Observer {
    private static final String COLLECT = "Collect";

    private static final int H = 300;       // Height of window pixels
    private static final int W = 400;       // Width  of window pixels

    private final JLabel theAction = new JLabel();
    private final JTextField theInput = new JTextField();
    private final JTextArea theOutput = new JTextArea();
    private final JScrollPane theSP = new JScrollPane();
    private final JButton theBtCollect = new JButton(COLLECT);

    private OrderProcessing theOrder = null;
    private CollectController cont = null;

    /**
     * Construct the view
     *
     * @param rpc Window in which to construct
     * @param mf  Factor to deliver order and stock objects
     * @param x   x-coordinate of position of window on screen
     * @param y   y-coordinate of position of window on screen
     */
    public CollectView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        try                                           //
        {
            theOrder = mf.makeOrderProcessing();        // Process order
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        Container cp = rpc.getContentPane();    // Content Pane
        Container rootWindow = (Container) rpc;         // Root Window
        cp.setLayout(null);                             // No layout manager
        rootWindow.setSize(W, H);                     // Size of Window
        rootWindow.setLocation(x, y);

        Font f = new Font("Monospaced", Font.PLAIN, 12);  // Font f is

        theBtCollect.setBounds(16, 25 + 60 * 0, 80, 40);  // Check Button
        theBtCollect.addActionListener(                 // Call back code
                e -> {
                    cont.doCollect(theInput.getText());
                    showReviewPopup();
                });
        cp.add(theBtCollect);                         //  Add to canvas

        theAction.setBounds(110, 25, 270, 20);       // Message area
        theAction.setText("");                        // Blank
        cp.add(theAction);                            //  Add to canvas

        theInput.setBounds(110, 50, 270, 40);         // Input Area
        theInput.setText("");                           // Blank
        cp.add(theInput);                             //  Add to canvas

        theSP.setBounds(110, 100, 270, 160);          // Scrolling pane
        theOutput.setText("");                        //  Blank
        theOutput.setFont(f);                         //  Uses font
        cp.add(theSP);                                //  Add to canvas
        theSP.getViewport().add(theOutput);           //  In TextArea
        rootWindow.setVisible(true);                  // Make visible
        theInput.requestFocus();                        // Focus is here
    }

    public void setController(CollectController c) {
        cont = c;
    }

    /**
     * Update the view
     *
     * @param modelC The observed model
     * @param arg    Specific args
     */
    @Override
    public void update(Observable modelC, Object arg) {
        CollectModel model = (CollectModel) modelC;
        String message = (String) arg;
        theAction.setText(message);

        theOutput.setText(model.getResponce());
        theInput.requestFocus();               // Focus is here
    }

    /**
     * Show a pop-up menu for leaving a review.
     * Allows users to provide additional feedback after selecting a review.
     */
    private void showReviewPopup() {
        String[] options = {"Excellent", "Good", "Average", "Poor"};
        int choice = JOptionPane.showOptionDialog(null,
                "Please leave a review for the service:",
                "Review Service",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice != JOptionPane.CLOSED_OPTION) {
            String reviewMessage = getReviewFeedback();
            JOptionPane.showMessageDialog(null, "Review Submitted\n" + reviewMessage, "Review Submitted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Show a pop-up with a text box for users to provide feedback.
     * @return The feedback provided by the user.
     */
    private String getReviewFeedback() {
        JTextArea textArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        int option = JOptionPane.showOptionDialog(null,
                scrollPane,
                "Additional Feedback",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);

        if (option == JOptionPane.OK_OPTION) {
            return textArea.getText();
        } else {
            return "No additional feedback provided.";
        }
    }
}
