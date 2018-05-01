import javafx.event.Event;
import javafx.event.EventType;

public class FileChangedEvent extends Event{

	public FileChangedEvent(EventType<? extends Event> eventType) {
		super(eventType);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
