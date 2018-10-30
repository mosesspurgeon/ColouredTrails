package gui.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
<b>Description</b>

This class holds and manages an ordered list of HistoryEntry instances.
For more documentation and examples /docs/historyLog.pdf

<p>

<b>Observers</b>
This class is observed by type.GameStatus

<p>

<b>Notifications</b>
Method addHistoryItem() issues "LOG_CHANGED" message.

<p>

<b>Original Summary</b>
 * A history log which holds all previous actions which have occurred in
 * a game.
 *
 * @author Paul Heymann (ct3@heymann.be)
	@author Sevan G. Ficici (class-level review and comments)
 */
public class HistoryLog extends Observable implements Serializable {

    public HistoryLog() {
    }

    public ArrayList<HistoryEntry> loglist = new ArrayList<HistoryEntry>();

    /**
     * Add a new history entry to the log.
     *
     * @param he The history entry to be added.
     */
    public void addHistoryItem(HistoryEntry he) {
        loglist.add(he);
        setChanged();
        notifyObservers("LOG_CHANGED");
    }

    /**
     * Get a list of all history log entries added to this log.
     *
     * @return A list of all history log entries added to this log.
     */
    public ArrayList<HistoryEntry> getLoglist() {
        return loglist;
    }

    public String toString() {
        String entries = "";
        for (HistoryEntry he : loglist) {
            entries += he.toString();
        }
        return "HistoryLog...\n" + entries +
               "End of HistoryLog...\n\n";
    }
}