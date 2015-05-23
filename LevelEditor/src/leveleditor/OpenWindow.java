package leveleditor;

import java.awt.Dialog;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import static leveleditor.Globals.*;

public class OpenWindow extends JDialog implements WindowListener {

    private final DefaultListModel mainListModel = new DefaultListModel();
    private final JList mainList = new JList(mainListModel);

    public OpenWindow(JFrame frame) {
        super(frame, Dialog.ModalityType.APPLICATION_MODAL);
        mainList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mainList.setVisibleRowCount(20);
        mainList.setPrototypeCellValue("                                                                     ");
        JScrollPane mainScrollPane = new JScrollPane(mainList);
        mainList.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                int key = ke.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    open();
                } else if (key == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        });
        mainScrollPane.setWheelScrollingEnabled(true);
        setTitle("Open");
        JPanel mainPanel = new JPanel();
        JButton mainAddButton = new JButton("Open");
        mainAddButton.setFocusable(false);
        add(mainPanel);
        mainPanel.add(mainScrollPane);
        mainPanel.add(mainAddButton);
        mainAddButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                open();
            }
        });
        addWindowListener(this);
        pack();
        setLocationRelativeTo(frame);
        setResizable(false);
    }

    public final void display(ArrayList<World> worlds, JFrame frame) {
        setLocationRelativeTo(frame);
        refreshWorldsList(worlds);
        setVisible(true);
    }

    public final void open() {
        setVisible(false);
        int[] selected = mainList.getSelectedIndices();
        for (int i = 0; i < selected.length; i++) {
            loadOne((String) mainListModel.get(selected[i]));
        }
        currentWorld = worlds.size() - 1;
    }

    private void refreshWorldsList(ArrayList<World> worlds) {
        mainListModel.clear();
        for (String worldName : allWorlds) {
            breakpoint:
            {
                for (World world : worlds) {
                    if (worldName.equals(world.getName())) {
                        break breakpoint;
                    }
                }
                mainListModel.addElement(worldName);
            }
        }
        if (!mainListModel.isEmpty()) {
            mainList.setSelectedIndex(0);
        }
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosing(WindowEvent we) {
        setVisible(false);
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }
}
