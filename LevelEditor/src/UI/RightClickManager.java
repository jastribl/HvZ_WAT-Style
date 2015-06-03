package UI;

import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;
import static leveleditor.Globals.*;

public class RightClickManager {

    private final String[] tabsRightClickText = {"Close", "Close All", "Rename", "Save", "Save All", "Delete"};
    private final JMenuItem tabsRightClickMenuItems[] = new JMenuItem[tabsRightClickText.length];
    private final JPopupMenu tabsRightClickMenu = new JPopupMenu();

    public RightClickManager() {
        ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String command = ae.getActionCommand();
                switch (command) {
                    case "Close":
                        closeTab(currentWorld);
                        break;
                    case "Close All":
                        closeAllTabs();
                        break;
                    case "Rename":
                        worlds.get(currentWorld).rename();
                        break;
                    case "Save":
                        worlds.get(currentWorld).save();
                        break;
                    case "Save All":
                        saveAll();
                        break;
                    case "Delete":
                        removeCurrentWorld();
                        break;
                    default:
                        return;
                }
                ((JMenuItem) ae.getSource()).getParent().setVisible(false);
            }

        };
        for (int i = 0; i < tabsRightClickMenuItems.length; i++) {
            tabsRightClickMenuItems[i] = new JMenuItem(tabsRightClickText[i]);
            tabsRightClickMenuItems[i].setActionCommand(tabsRightClickText[i]);
            tabsRightClickMenuItems[i].addActionListener(actionListener);
            tabsRightClickMenu.add(tabsRightClickMenuItems[i]);
        }
    }

    public final void closeAll() {
        tabsRightClickMenu.setVisible(false);
    }

    public final void showTabMenu(Point point) {
        tabsRightClickMenu.setLocation(point);
        tabsRightClickMenu.setVisible(true);
    }
}
