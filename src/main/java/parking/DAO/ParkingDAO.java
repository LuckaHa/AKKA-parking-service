package parking.DAO;

import java.util.Date;

public interface ParkingDAO {
	
	int parkCar(String ecv, int parkingLotId);
	boolean unparkCar(int ticketId, int parkingLotId);
	double getFilling(int parkingLotId);
	int getNumberOfCarsOnGivenDay(Date day, int parkingLotId);
	int createParkingLot(int capacity);

}
