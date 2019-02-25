package parking.akka;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import parking.actions.ParkCar;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// stara verzia WorkerActor
public class ParkCarActor extends UntypedActor {
	private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
	
    @Override
	public void onReceive(Object message) throws Exception {
    	// ak dostane spravu od mastera, vykona ulohu
    	if (message instanceof ParkCar) {
    		// posle masterovi, co vypocital 
    		getSender().tell(((ParkCar) message).park(), getSelf());
    		//logger.info((String) message);
		} else {
			unhandled(message);
		}
	}
}