package me.joseph.component;

import me.joseph.App;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class SongList extends JList implements ListSelectionListener {

    private App app;

    public SongList(App app) {
        this.app = app;

        setListData(
                app.getSongTitles().toArray()
        );
        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        addListSelectionListener(this);
        setVisible(true);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.app.setCurrentSong(getSelectedIndex());
        this.app.play();
    }
}
