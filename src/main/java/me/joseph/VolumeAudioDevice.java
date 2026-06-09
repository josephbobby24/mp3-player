package me.joseph;

import javazoom.jl.player.JavaSoundAudioDevice;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.lang.reflect.Field;

public class VolumeAudioDevice extends JavaSoundAudioDevice {

    public void setVolume(float db) {
        try {
            Field field =
                    JavaSoundAudioDevice.class.getDeclaredField("source");

            field.setAccessible(true);

            SourceDataLine source =
                    (SourceDataLine) field.get(this);

            if (source != null &&
                    source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {

                FloatControl control =
                        (FloatControl) source.getControl(
                                FloatControl.Type.MASTER_GAIN
                        );

                control.setValue(
                        Math.max(
                                control.getMinimum(),
                                Math.min(db, control.getMaximum())
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
