package parking.actions;

import parking.DAO.MysqlParkingDAO;

public class ParkCar {
	
	private String ecv; // car identificator
	private int parkingLotId;
	private MysqlParkingDAO mysqlParkingDAO;
	
	public ParkCar(String ecv, int parkingLotId, MysqlParkingDAO mysqlParkingDAO) {
		this.ecv = ecv;
		this.parkingLotId = parkingLotId;
		this.mysqlParkingDAO = mysqlParkingDAO;
	}
	
	public int park() {
		return mysqlParkingDAO.parkCar(ecv, parkingLotId);
	}

	public String getEcv() {
		return ecv;
	}

	public int getParkingLotId() {
		return parkingLotId;
	}
}
