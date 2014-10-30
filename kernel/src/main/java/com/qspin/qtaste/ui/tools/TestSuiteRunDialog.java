/*
    Copyright 2007-2009 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.ui.tools;

import com.qspin.qtaste.ui.util.QSpinTheme;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author vdubois
 */

@SuppressWarnings("serial")
public class TestSuiteRunDialog extends JDialog {
//public class TestBedConfigSelectionPanel extends JFrame {

    private JRadioButton  mNumberofLoopsOption = new JRadioButton ("Number of loops:");
    private JRadioButton  mNumberofLoopsInTimeOption = new JRadioButton ("Loop during time:");
    private JTextField mNumberOfLoopsTextArea = new JFormattedTextField(createFormatter("###"));

    private JFormattedTextField mNumberOfLoopsInHoursTextArea = new JFormattedTextField(createFormatter("###"));
    private JLabel mNumberOfLoopsInHoursLabel = new JLabel("h");
    private JFormattedTextField mNumberOfLoopsInMinutesTextArea = new JFormattedTextField(createFormatter("###"));
    private JLabel mNumberOfLoopsInMinutesLabel = new JLabel("m");

    private JButton okButton = new JButton("Ok");
    private JButton cancelButton = new JButton("Cancel");
    private JPanel mainPanel = new JPanel(new GridBagLayout());
    private JPanel bottomPanel = new JPanel(new BorderLayout());
    private QSpinTheme mTheme;

    public boolean IsCancelled = false;
    //private static Logger logger = Log4jLoggerFactory.getLogger(TestSuiteRunDialog.class);

    public TestSuiteRunDialog(Window owner,String title) {

        super(owner, title, ModalityType.APPLICATION_MODAL);
        //super();
        setTitle(title);
        setUpFrame();
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		IsCancelled = true;
        	}
		});

        genUI();
    }

    public int getNumberOfLoops() {
        if (mNumberofLoopsOption.isSelected()) {
        	int nLoops = 0;
        	if (!mNumberOfLoopsTextArea.getText().trim().isEmpty())
        		nLoops = Integer.parseInt(mNumberOfLoopsTextArea.getText());
        	return nLoops;
        }
        if (mNumberofLoopsInTimeOption.isSelected()) {
        	int nLoopMinutes = 0;
        	if (!mNumberOfLoopsInHoursTextArea.getText().trim().isEmpty())
        		nLoopMinutes += (Integer.parseInt(mNumberOfLoopsInHoursTextArea.getText()) * 60);
        	if (!mNumberOfLoopsInMinutesTextArea.getText().trim().isEmpty())
        		nLoopMinutes += (Integer.parseInt(mNumberOfLoopsInMinutesTextArea.getText()));
        	return nLoopMinutes;
        }
        return 1;
    }
    public boolean isLoopsInTime() {
        return mNumberofLoopsInTimeOption.isSelected();
    }

    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    private void genUI() {

        this.setLayout(new GridBagLayout());

        ButtonGroup group = new ButtonGroup();
        group.add(mNumberofLoopsOption);
        mNumberofLoopsOption.setSelected(true);
        group.add(mNumberofLoopsInTimeOption);
        mNumberofLoopsOption.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mNumberOfLoopsTextArea.setEnabled(true);
                mNumberOfLoopsInHoursTextArea.setEnabled(false);
                mNumberOfLoopsInHoursLabel.setEnabled(false);
                mNumberOfLoopsInMinutesTextArea.setEnabled(false);
                mNumberOfLoopsInMinutesLabel.setEnabled(false);
            }
        });

        mNumberofLoopsInTimeOption.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mNumberOfLoopsTextArea.setEnabled(false);
                mNumberOfLoopsInHoursTextArea.setEnabled(true);
                mNumberOfLoopsInHoursLabel.setEnabled(true);
                mNumberOfLoopsInMinutesTextArea.setEnabled(true);
                mNumberOfLoopsInMinutesLabel.setEnabled(true);
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx=0;
        c.gridy=0;
        c.insets = new Insets(10,10,0,10);  //top padding
        mainPanel.add(mNumberofLoopsOption, c);

        c.gridx=1;
        c.gridy=0;
        mNumberOfLoopsTextArea.setColumns(3);
        mainPanel.add(mNumberOfLoopsTextArea, c);

        c.gridx=0;
        c.gridy=1;
        mainPanel.add(mNumberofLoopsInTimeOption, c);

        c.gridwidth = 1;
        c.gridx=1;
        c.gridy=1;
        mNumberOfLoopsInHoursTextArea.setColumns(3);
        mNumberOfLoopsInHoursTextArea.setEnabled(false);
        mainPanel.add(mNumberOfLoopsInHoursTextArea, c);

        c.gridx=2;
        c.gridy=1;
        mNumberOfLoopsInHoursLabel.setEnabled(false);
        mainPanel.add(mNumberOfLoopsInHoursLabel, c);

        c.gridx=3;
        c.gridy=1;
        mNumberOfLoopsInMinutesTextArea.setColumns(3);
        mNumberOfLoopsInMinutesTextArea.setEnabled(false);
        mainPanel.add(mNumberOfLoopsInMinutesTextArea, c);

        c.gridx=4;
        c.gridy=1;
        mNumberOfLoopsInMinutesLabel.setEnabled(false);
        mainPanel.add(mNumberOfLoopsInMinutesLabel, c);

        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton, BorderLayout.EAST);

        ActionListener okActionListener = new OkActionListener();
        okButton.addActionListener(okActionListener);
        ActionListener cancelActionListener = new CancelActionListener();
        cancelButton.addActionListener(cancelActionListener);

        // ok on ENTER keypress
        getRootPane().registerKeyboardAction(okActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // cancel on ESCAPE keypress
        getRootPane().registerKeyboardAction(cancelActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx=0;
        c.gridy=0;
        c.insets = new Insets(10,10,0,10);  //top padding
        this.add(mainPanel, c);


        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx=1;
        c.gridy=1;
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        c.insets = new Insets(30,10,10,10);  //top padding

        this.add(bottomPanel, c);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void setUpFrame() {
//        setName(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void setQSpinTheme() {
        mTheme = new QSpinTheme();
        MetalLookAndFeel.setCurrentTheme(mTheme);
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {

        }
    }


    protected class OkActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
                /**
                 * when the windows is closing, the isCancelled variable is always set to true. (see the constructor)
                 * The variable has to be reset to false.
                 */
                IsCancelled = false;
        }
    };


    protected class CancelActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
                IsCancelled = true;
                setVisible(false);
                dispose();
        }
    }
}

