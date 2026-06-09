package me.joseph;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.lang.reflect.Field;

public class VolumeAudioDevice extends JavaSoundAudioDevice {

    @Getter @Setter
    private float vVolume;

    @Override
    protected void createSource() throws JavaLayerException {
        super.createSource();

        this.setVolume(vVolume);
    }

    public void setVolume(float db) {
        this.vVolume = db;

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
