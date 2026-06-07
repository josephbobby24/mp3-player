package me.joseph;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import lombok.Getter;
import lombok.Setter;
import me.joseph.component.SongList;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App extends JFrame {

    @Setter @Getter
    private int currentSong = 0;
    @Getter
    private final List<String> songs = new ArrayList<>();
    @Getter
    private final List<String> songTitles = new ArrayList<>();
    @Getter @Setter
    private String dir = "C://Users/josep/Desktop/Songs";

    AdvancedPlayer player;

    public App() {
        initialiseSongs();
        initialiseDisplay();
        initialiseComponents();
    }

    public void initialiseSongs() {
        this.songs.clear();
        Path path = Paths.get(dir);
        File folder = new File(path.toString());

        for (File file: folder.listFiles()) {
            if (!file.getName().toLowerCase().endsWith("mp3")) continue;

            this.songs.add(file.getAbsolutePath());
            this.songTitles.add(file.getName());
        }

    }

    public void initialiseDisplay() {
        setSize(600, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Zeply Music Player");
        setVisible(true);
    }

    public void initialiseComponents() {
        setLayout(new BorderLayout());

        JTextField textField = new JTextField(dir);

        JButton button = new JButton("Change Directory");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(textField, BorderLayout.CENTER);
        topPanel.add(button, BorderLayout.EAST);

        JList songList = new SongList(this);
        JScrollPane pane = new JScrollPane(songList);

        add(topPanel, BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);

        button.addActionListener(e -> {
            String value = textField.getText();
            this.setDir(value);
            this.initialiseSongs();
            songList.setListData(this.songs.toArray());
        });
    }

    public void play() {
        if (songs.isEmpty()) return;

        System.out.println(currentSong);
        if (currentSong < 0 || currentSong >= songs.size()) {
            currentSong = 0;
        }

        if (player != null) {
            player.close();
        }

        try {
            player = new AdvancedPlayer(
                    new FileInputStream(songs.get(currentSong))
            );

            Thread thread = new Thread(() -> {
                try {
                    player.play();

                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            });

            thread.start();


            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    thread.interrupt();
                    currentSong++;
                    play();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
