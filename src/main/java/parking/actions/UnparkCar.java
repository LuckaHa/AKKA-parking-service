package parking.actions;

import parking.DAO.MysqlParkingDAO;

public class UnparkCar {
	
	private int ticketId;
	private int parkingLotId;
	private MysqlParkingDAO mysqlParkingDAO;
	
	public UnparkCar(int ticketId, int parkingLotId, MysqlParkingDAO mysqlParkingDAO) {
		super();
		this.ticketId = ticketId;
		this.parkingLotId = parkingLotId;
		this.mysqlParkingDAO = mysqlParkingDAO;
	}
	
	public boolean unpark() {
		return mysqlParkingDAO.unparkCar(ticketId, parkingLotId);
	}

	public int getTicketId() {
		return ticketId;
	}

	public int getParkingLotId() {
		return parkingLotId;
	}
}