package org.herac.tuxguitar.midiinput;

import java.util.ArrayList;
import java.util.Iterator;

import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.models.TGBeat;

class MiStaffEvent {
  static final byte BAR = 1;
  static final byte NONE = 0;
  static final byte OFF_BEAT = 4;
  static final byte ON_BEAT = 2;
  static final byte TIE_BEAT = 8;

  private TGBeat f_Beat; // TuxGuitar beat (optional)
  private long f_BeginTime; // begin time [ticks]
  private ArrayList f_Notes = new ArrayList(); // note on list
  private byte f_Type = NONE; // event type

  public MiStaffEvent(long inBeginTime) {
    f_BeginTime = inBeginTime;
  }

  void addNoteOn(MiNote inNote) {
    MiStaffNote sn = new MiStaffNote(inNote);

    sn.setTied(false);
    f_Type |= ON_BEAT;
    f_Notes.add(sn);
  }

  void addTiedNote(MiStaffNote inSN) {
    inSN.setTied(true);
    f_Type |= TIE_BEAT;
    f_Notes.add(inSN);
  }

  TGBeat getBeat() {
    return (f_Beat);
  }

  long getBeginTime() {
    return (f_BeginTime);
  }

  ArrayList<MiStaffNote> getNotes() {
    return (f_Notes);
  }

  boolean isBar() {
    return ((f_Type & BAR) == BAR);
  }

  boolean isOffBeat() {
    return ((f_Type & OFF_BEAT) == OFF_BEAT);
  }

  boolean isOnBeat() {
    return ((f_Type & ON_BEAT) == ON_BEAT);
  }

  boolean isTieBeat() {
    return ((f_Type & TIE_BEAT) == TIE_BEAT);
  }

  void markAsBar() {
    f_Type |= BAR;
  }

  void merge(MiStaffEvent inEvent) {
    try {
      if (f_Beat != null)
        throw new Exception("f_Beat non  nullo!");

      if (f_BeginTime != inEvent.f_BeginTime)
        throw new Exception("f_BeginTime  diverso, old: " + f_BeginTime
            + " new: " + inEvent.f_BeginTime);
      /*
       * if(f_EndTime != inEvent.f_EndTime) throw new
       * Exception("f_EndTime  diverso, old: " + f_EndTime + " new: " +
       * inEvent.f_EndTime);
       */
      f_Type |= inEvent.f_Type;
      f_Notes.addAll(inEvent.f_Notes);
    } catch (Throwable throwable) {
      MessageDialog.errorMessage(throwable);
    }
  }

  private long normalize(long inTime, long inResolution) {
    long time = (inTime / inResolution) * inResolution;

    if ((inTime % inResolution) > inResolution / 2)
      time += inResolution;

    return (time);
  }

  void normalizeBeat(int inNoteType) {
    long resolution = MiStaffNote.noteToTicks(inNoteType);

    f_BeginTime = normalize(f_BeginTime, resolution);
  }

  void normalizeDurations() {
    for (Iterator it = f_Notes.iterator(); it.hasNext();) {
      MiStaffNote sn = (MiStaffNote) it.next();
      sn.normalizeDuration();
    }
  }

  void setBeat(TGBeat inBeat) {
    f_Beat = inBeat;
  }

  public String toString() {
    String out = Long.toString(f_BeginTime);

    if (isBar())
      out += " BAR";

    out += System.getProperty("line.separator");

    if (!f_Notes.isEmpty()) {
      for (Iterator it = f_Notes.iterator(); it.hasNext();) {
        MiStaffNote sn = (MiStaffNote) it.next();

        out += (sn.isTied() ? "   T: " : "   N: ");
        out += sn;
        out += " ->" + (f_BeginTime + sn.getOverallDuration());
        out += System.getProperty("line.separator");
      }
    }

    return (out);
  }
}
