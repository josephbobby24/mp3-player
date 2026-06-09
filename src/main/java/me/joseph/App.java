package me.joseph;

import javazoom.jl.decoder.JavaLayerException;
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
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class App extends JFrame {

    @Setter @Getter
    private int currentSong = 0;
    @Getter
    private final List<String> songs = new ArrayList<>();
    @Getter
    private final List<String> songTitles = new ArrayList<>();
    @Getter @Setter
    private String dir = "";
    private final Properties config = new Properties();

    private float volume = 0;

    AdvancedPlayer player;
    VolumeAudioDevice device = new VolumeAudioDevice();

    public App() {
        loadConfig();
        initialiseSongs();
        initialiseDisplay();
        initialiseComponents();
    }

    public void initialiseSongs() {
        this.songs.clear();

        if (dir.isEmpty()) return;

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

        JSlider slider = new JSlider(0, 100, 100);
        topPanel.add(slider, BorderLayout.SOUTH);


        JList songList = new SongList(this);
        JScrollPane pane = new JScrollPane(songList);

        add(topPanel, BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);

        button.addActionListener(e -> {
            String value = textField.getText();
            this.setDir(value);
            this.initialiseSongs();
            songList.setListData(this.songs.toArray());
            config.setProperty("dir", value);
            saveConfig();
        });

        slider.addChangeListener(e -> {
            float normalized = slider.getValue() / 100f;

            if (normalized == 0) {
                volume = -80f;
            } else {
                volume = (float) (20 * Math.log10(normalized));
            }

            if (device != null) {
                device.setVolume(volume);
            }

            config.setProperty("volume", "" + volume);
            saveConfig();
        });


    }


    public void play() {
        if (songs.isEmpty()) return;

        if (currentSong < 0 || currentSong >= songs.size()) {
            currentSong = 0;
        }

        if (player != null) {
            player.close();
        }

        try {
            device = new VolumeAudioDevice();
            player = new AdvancedPlayer(
                    new FileInputStream(songs.get(currentSong)),
                    device
            );

            device.setVVolume(this.volume);


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

    public void loadConfig() {
        try {
            File file = new File(System.getProperty("user.dir"), "config.properties");

            if (!file.exists()) return;

            config.load(new FileInputStream(file));

            this.dir = config.getProperty("dir", "");
            this.volume = Float.parseFloat(config.getProperty("volume", "0"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            File file = new File(System.getProperty("user.dir"), "config.properties");
            config.store(new FileOutputStream(file), "App Config");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
