package leveleditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import static leveleditor.Globals.*;

public class OpenWindow extends JFrame {

    public final DefaultListModel mainListModel = new DefaultListModel();
    public final JList mainList = new JList(mainListModel);

    public OpenWindow() {
        mainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainList.setVisibleRowCount(20);
        mainList.setPrototypeCellValue("                                                                     ");
        JScrollPane mainScrollPane = new JScrollPane(mainList);
        mainList.addKeyListener(new KeyListener() {
            boolean ENTERisDown = false, DELETEisDown = false, CTRNisDown = false, CTRRisDown = false;

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                int key = ke.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    ENTERisDown = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                int key = ke.getKeyCode();
                if (key == KeyEvent.VK_ENTER && ENTERisDown) {
                    open();
                    ENTERisDown = false;
                }
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
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public final void display(ArrayList<World> worlds) {
        refreshMainFrame(worlds);
        setVisible(true);
    }

    public final void open() {
        for (int i = 0; i < worlds.size(); i++) {
            if (worlds.get(i).getName() == mainList.getSelectedValue()) {
                worlds.get(i).setOpen(true);
                setVisible(false);
                numberOfWorldsOpen++;
                currentWorld = i;
                drawOpen = true;
                break;
            }
        }
    }

    private void refreshMainFrame(ArrayList<World> worlds) {
        mainListModel.clear();
        for (World world : worlds) {
            if (!world.isOpen()) {
                mainListModel.addElement(world.getName());
            }
        }
        if (!mainListModel.isEmpty()) {
            mainList.setSelectedIndex(0);
        }
    }
}
