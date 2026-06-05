package me.joseph;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.Getter;
import lombok.Setter;
import me.joseph.component.SongList;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    Player player;

    public App() {
        initialiseSongs();
        initialiseDisplay();
        initialiseComponents();
    }

    public void initialiseSongs() {
        String dir = "";
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
        add(new JScrollPane(new SongList(this)));
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
            player = new Player(
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

            while (!player.isComplete()) {
            }

            thread.interrupt();
            System.out.println("b");

            currentSong++;
            play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
