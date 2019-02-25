package parking.akka;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.Broadcast;
import akka.routing.RoundRobinPool;
import parking.actions.GetFilling;
import parking.actions.GetNumberOfCarsIn;
import parking.actions.ParkCar;
import parking.actions.UnparkCar;

public class MasterActor extends AbstractActor {
	
    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
    private ActorRef workerActor = getContext().actorOf(Props.create(WorkerActor.class)
    		.withRouter(new RoundRobinPool(4)));   
    
    @Override
    public void preStart() throws Exception {
    	super.preStart();
    	// master sleduje workera, ak vidi, ze zomrel (Terminated), konci program
    	getContext().watch(workerActor);
    }
    
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(ParkCar.class, message -> workerActor.tell(message, getSelf())) // posle ulohu	
				.match(UnparkCar.class, message -> workerActor.tell(message, getSelf()))
				.match(GetFilling.class, message -> workerActor.tell(message, getSelf()))
				.match(GetNumberOfCarsIn.class, message -> workerActor.tell(message, getSelf()))
				.match(String.class, message -> System.out.println(message))
				.match(Eof.class, eof -> workerActor.tell(new Broadcast(PoisonPill.getInstance()), getSelf())) // skonci posielanie
				.match(Terminated.class, message -> {
					logger.info("Úlohy dokončené.");
					getContext().system().terminate();
				})
				.build();
	}

	// mechanizmus vytvarania aktora
	public static Props props() {
		return Props.create(MasterActor.class);
	}
}