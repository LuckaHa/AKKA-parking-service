package parking.actions;

import java.util.Date;

import parking.DAO.MysqlParkingDAO;

public class GetNumberOfCarsIn {
	
	private Date date;
	private int parkingLotId;
	private MysqlParkingDAO mysqlParkingDAO;
	
	public GetNumberOfCarsIn(Date date, int parkingLotId, MysqlParkingDAO mysqlParkingDAO) {
		super();
		this.date = date;
		this.parkingLotId = parkingLotId;
		this.mysqlParkingDAO = mysqlParkingDAO;
	}
	
	public int getNumberOfCarsIn() {
		return mysqlParkingDAO.getNumberOfCarsOnGivenDay(date, parkingLotId);
	}

	public Date getDate() {
		return date;
	}

	public int getParkingLotId() {
		return parkingLotId;
	}
}
