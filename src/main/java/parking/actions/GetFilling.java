package parking.actions;

import java.util.List;
import java.util.Map;

import parking.DAO.MysqlParkingDAO;

public class GetFilling {
	
	private List<Integer> PLs;
	private MysqlParkingDAO mysqlParkingDAO;

	public GetFilling(List<Integer> pLs, MysqlParkingDAO mysqlParkingDAO) {
		super();
		PLs = pLs;
		this.mysqlParkingDAO = mysqlParkingDAO;
	}

	public Map<Integer, Double> getPLFilling() {
		return mysqlParkingDAO.getPLFilling(PLs);
	}
}
