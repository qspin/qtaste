package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class JavaComplexComponent extends JPanel {

    public JavaComplexComponent() {
        super();
        setName(COMPONENT_NAME);
        genUI();
    }

    private void genUI() {
        prepareComponent();

        FormLayout layout = new FormLayout("3dlu, pref:grow, 3dlu, pref, 3dlu", "3dlu, pref, 3dlu, pref, 3dlu:grow");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.add(m_JCC, cc.xyw(2, 2, 3));
        builder.add(m_selectedFile, cc.xy(2, 4));
        builder.add(m_openJFC, cc.xy(4, 4));

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);
    }

    private void prepareComponent() {
        m_JCC.setBorder(BorderFactory.createTitledBorder("JColorChooser"));
        m_JCC.setName("COLOR_CHOOSER");

        m_selectedFile.setName("FILECHOOSER_RESULT");
        m_openJFC.setName("OPEN_FILECHOOSER");
        m_openJFC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                m_JFC = new JFileChooser();
                m_JFC.setBorder(BorderFactory.createTitledBorder("JFileChooser"));
                m_JFC.setName("FILE_CHOOSER");
                if (m_JFC.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    m_selectedFile.setText(m_JFC.getSelectedFile().getName());
                } else {
                    m_selectedFile.setText(null);
                }
            }
        });
    }

    private JColorChooser m_JCC = new JColorChooser();

    private JTextField m_selectedFile = new JTextField();
    private JButton m_openJFC = new JButton("Open JFileChooser");
    private JFileChooser m_JFC;

    private static final int NUMBER_OF_COMPONENT = 2;
    public static final String COMPONENT_NAME = "COMPLEX_JAVA_COMP";
}
