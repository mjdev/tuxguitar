package org.herac.tuxguitar.midiinput;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MiPort implements Receiver {
  private static MiPort s_ControlPort;
  private static MiPort s_NotesPort;

  public static long getNotesPortTimeStamp() {
    if (s_NotesPort != null)
      return (s_NotesPort.f_Device.getMicrosecondPosition());
    else
      return (-1);
  }

  public static void setControlPort(String inDeviceName) throws MiException {
    if (s_ControlPort != null)
      s_ControlPort.closePort();

    MidiDevice device = MiPortProvider.getDevice(inDeviceName);

    if (device != null) {
      s_ControlPort = new MiPort(device);
      s_ControlPort.openPort();
    }
  }

  public static void setNotesPort(String inDeviceName) throws MiException {
    if (s_NotesPort != null)
      s_NotesPort.closePort();

    MidiDevice device = MiPortProvider.getDevice(inDeviceName);

    if (device != null) {
      s_NotesPort = new MiPort(device);
      s_NotesPort.openPort();
    }
  }

  private MidiDevice f_Device;

  private Transmitter f_Transmitter;

  private MiPort(MidiDevice inDevice) {
    this.f_Device = inDevice;
  }

  public void close() {
  }

  /*
   * Notes port management
   */

  protected synchronized void closePort() throws MiException {
    try {
      if (this.f_Transmitter != null) {
        final Transmitter transmitter = this.f_Transmitter;
        TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
          public void run() throws Throwable {
            transmitter.close();
            connectTransmitter(null);
          }
        });
      }

      if (this.f_Device.isOpen()) {
        final MidiDevice device = this.f_Device;
        TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
          public void run() throws Throwable {
            device.close();
          }
        });
      }
    } catch (Throwable t) {
      throw new MiException(TuxGuitar
          .getProperty("midiinput.error.midi.port.close"), t);
    }
  }

  protected void connectTransmitter(Transmitter inTransmitter) {
    this.f_Transmitter = inTransmitter;

    if (this.f_Transmitter != null)
      this.f_Transmitter.setReceiver(this);
  }

  /*
   * Control port management
   */

  public String getName() {
    return this.f_Device.getDeviceInfo().getName();
  }

  /*
   * javax.sound.midi.Receiver implementation
   */

  protected synchronized void openPort() throws MiException {
    try {
      if (!this.f_Device.isOpen()) {
        final MidiDevice device = this.f_Device;
        TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
          public void run() throws Throwable {
            device.open();
          }
        });
      }

      if (this.f_Transmitter == null) {
        final MidiDevice device = this.f_Device;
        TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
          public void run() throws Throwable {
            connectTransmitter(device.getTransmitter());
          }
        });
      }
    } catch (Throwable t) {
      throw new MiException(TuxGuitar
          .getProperty("midiinput.error.midi.port.open"), t);
    }
  }

  public void send(MidiMessage inMessage, long inTimeStamp) {
    if (inMessage instanceof ShortMessage) {
      ShortMessage mm = (ShortMessage) inMessage;

      switch (mm.getCommand()) {
      case ShortMessage.NOTE_ON:
      case ShortMessage.NOTE_OFF:
        MiProvider.instance().noteReceived(mm, inTimeStamp);
        break;

      case ShortMessage.CONTROL_CHANGE:
        // System.err.println("Control change");
        break;
      }
    }
  }
}
