package parking.akka;

import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import parking.actions.GetFilling;
import parking.actions.GetNumberOfCarsIn;
import parking.actions.ParkCar;
import parking.actions.UnparkCar;

public class WorkerActor extends AbstractActor {
	
	private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

	@Override
	public Receive createReceive() {
		return receiveBuilder()
                .match(ParkCar.class, message -> {
                	ParkCar parkCar = (ParkCar) message;
                	int ticket = (parkCar).park();
                	if (ticket == -1) {
                		getSender().tell("Auto " + parkCar.getEcv() + " nebolo možné zaparkovať na parkovisko " 
                		+ parkCar.getParkingLotId(), getSelf());
                	} else {
                		getSender().tell("Auto " + parkCar.getEcv() + " bolo úspešne zaparkované na parkovisko " 
                		+ parkCar.getParkingLotId() + ". Id vytvoreného lístka je " + ticket, getSelf());
                	}
                })
                .match(UnparkCar.class, message -> {
                	UnparkCar unparkCar = (UnparkCar) message;
                	boolean done = (unparkCar).unpark();
                	if (done) {
                		getSender().tell("Auto s lístkom " + unparkCar.getTicketId() + 
                				" bolo úspešne odparkované z parkoviska " + unparkCar.getParkingLotId(), getSelf());
                	} else {
                		getSender().tell("Auto s lístkom " + unparkCar.getTicketId() +
                				" nebolo možné odparkovať.", getSelf());
                	}
                })
                .match(GetFilling.class, message -> {
                	Map<Integer, Double> fillings = ((GetFilling) message).getPLFilling();
                    String sysout = "Zaplnenosť parkovísk je: \n";
                    for (Integer id: fillings.keySet()) {
                    	sysout += id.toString() + ": " + fillings.get(id).toString() + " % \n";
					}
                    getSender().tell(sysout, getSelf());
                })
                .match(GetNumberOfCarsIn.class, message -> {
                	GetNumberOfCarsIn gnoci = (GetNumberOfCarsIn) message;
                	int num = (gnoci).getNumberOfCarsIn();
                    getSender().tell("V deň " + gnoci.getDate() + " bolo na parkovisku " + gnoci.getParkingLotId() + ": " + num + " áut.", getSelf());
                })
                .build();
	}
	
	public static Props props() {
        return Props.create(WorkerActor.class);
    }
}
