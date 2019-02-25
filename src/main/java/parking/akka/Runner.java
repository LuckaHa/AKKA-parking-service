package parking.akka;

import parking.DAO.DAOFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import parking.DAO.MysqlParkingDAO;
import parking.actions.GetFilling;
import parking.actions.GetNumberOfCarsIn;
import parking.actions.ParkCar;
import parking.actions.UnparkCar;

public class Runner {
    public static void main(String[] args) throws Exception {
		ActorSystem system = ActorSystem.create();
		ActorRef master = system.actorOf(Props.create(MasterActor.class));
		MysqlParkingDAO mysqlParkingDAO = DAOFactory.INSTANCE.getMysqlParkingDAO();
		
		// pripravit id parkovisk
		List<Integer> idPLs = new ArrayList<Integer>();
		for (int i = 8; i < 11; i++) {
			idPLs.add(i);
		}
		idPLs.add(37);
		idPLs.add(48);
		
		// sprava prisla zvonku aktoroveho systemu, preto je sender neznamy
		master.tell(new ParkCar("KE129", 10, mysqlParkingDAO), ActorRef.noSender());
		master.tell(new UnparkCar(102, 9, mysqlParkingDAO), ActorRef.noSender());
		master.tell(new GetFilling(idPLs, mysqlParkingDAO), ActorRef.noSender());
		master.tell(new GetNumberOfCarsIn(new Date(2019,1,29), 9, mysqlParkingDAO), ActorRef.noSender());
		
		// posleme spravu o dokonceni posielania
		master.tell(new Eof(), ActorRef.noSender());
	}
}