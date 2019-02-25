package parking.DAO;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class MysqlParkingDAO implements ParkingDAO {

	private String query;
	private JdbcTemplate jdbcTemplate;
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	public MysqlParkingDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * zaparkuje a @return id listka (ak nebolo miesto, vrati -1)
	 */
	public int parkCar(String ecv, int parkingLotId) {		
		// kontrola, ci je miesto na parkovisku
		query = "SELECT numberOfCarsIn FROM parkinglot WHERE id = " + parkingLotId;
		int numberOfCarsIn = jdbcTemplate.queryForObject(query, Integer.class);		
		query = "SELECT capacity FROM parkinglot WHERE id = " + parkingLotId;
		int capacity = jdbcTemplate.queryForObject(query, Integer.class);
		if (numberOfCarsIn >= capacity) {
			System.out.println("Parkovisko je plné.");
			return -1;
		}
		
		// kontrola, ci auto prave nie je zaparkovane
		Date now = new Date();
		query = "SELECT departureTime FROM ticket WHERE ecv = '" + ecv + "'";
		List<Date> departures = jdbcTemplate.queryForList(query, Date.class); // auto uz mohlo parkovat viac krat
		for (Date time : departures) {
			if (time == null) { // ak este auto neodislo z parkoviska
				//System.out.println("Auto už práve parkuje, nie je možné zaparkovať ho znova.");
				return -1;
			}
		}
		
		// vyvorit novy listok
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		simpleJdbcInsert.withTableName("ticket");
		simpleJdbcInsert.usingGeneratedKeyColumns("id");
		simpleJdbcInsert.usingColumns("ecv","arrivalDate","arrivalTime","parkinglot_id");
		Map<String,Object> toInsert = new HashMap<String, Object>();
		toInsert.put("ecv",ecv);
		toInsert.put("arrivalDate", DATE_FORMAT.format(now));
		toInsert.put("arrivalTime", TIME_FORMAT.format(now));
		toInsert.put("parkinglot_id",parkingLotId);
		int ticketId = simpleJdbcInsert.executeAndReturnKey(toInsert).intValue();

		/*Ticket ticket = new Ticket(ecv);
		ticket.setId(ticketId);
		ticket.setParkingLotId(parkingLotId);
		ticket.setDateOfArrival(new Date());
		ticket.setTimeOfArrival(new Date());*/
		//System.out.println("Auto bolo zaparkované na parkovisku č. " + parkingLotId + ", id listka je " + ticketId);
		
		// zvysit obsadenost parkoviska
		query = "UPDATE parkinglot SET numberOfCarsIn = ? WHERE id = ?";
		jdbcTemplate.update(query, numberOfCarsIn + 1, parkingLotId);
		
		return ticketId;
	}

	/**
	 * ak auto bolo na parkovisku, odparkuje a @return true (inak false)
	 */
	public boolean unparkCar(int ticketId, int parkingLotId) {
		// nastavit datum a cas odchodu v listku
		Date now = new Date();
		query = "UPDATE ticket SET departureDate = ? WHERE id = ? AND " +
				"departureDate IS NULL AND parkinglot_id = ?";
		int updated = jdbcTemplate.update(query, DATE_FORMAT.format(now), ticketId, parkingLotId);
		query = "UPDATE ticket SET departureTime = ? WHERE id = ? AND " +
				"departureTime IS NULL AND parkinglot_id = ?";
		updated += jdbcTemplate.update(query, TIME_FORMAT.format(now), ticketId, parkingLotId);
		
		// ak sme take auto nasli, znizit obsadenost parkoviska
		if (updated > 0) {
			query = "SELECT numberOfCarsIn FROM parkinglot WHERE id = " + parkingLotId;
			int numberOfCarsIn = jdbcTemplate.queryForObject(query, Integer.class);
			query = "UPDATE parkinglot SET numberOfCarsIn = ? WHERE id = ?";
			jdbcTemplate.update(query, numberOfCarsIn - 1, parkingLotId);
			System.out.println("Auto s lístkom " + ticketId + " bolo odparkované z parkoviska " + parkingLotId + ".");
			return true;
		}
		//System.out.println("Auto nebolo možné odparkovať.");
		return false;
	}

	/**
	 * @return percentualne zaplnenie 1 parkoviska
	 */
	public double getFilling(int parkingLotId) {
		query = "SELECT numberOfCarsIn FROM parkinglot WHERE id = " + parkingLotId;
		double numberOfCarsIn = 0;
		try {
			numberOfCarsIn = jdbcTemplate.queryForObject(query, Long.class);
		} catch (EmptyResultDataAccessException e) { // to znamena, ze take parkovisko neexistuje
			return -1;
		}
		query = "SELECT capacity FROM parkinglot WHERE id = " + parkingLotId;
		double capacity = jdbcTemplate.queryForObject(query, Long.class);
		double percent = (numberOfCarsIn / capacity) * 100;
		//System.out.println("Parkovisko " + parkingLotId + " je zaplnené na " + percent);
		return percent;
	}
	
	/**
	 * @return percentualne zaplnenia zadanych parkovisk
	 */
	public Map<Integer, Double> getPLFilling(List<Integer> PLs) {
		Map<Integer, Double> fillings = new HashMap<>();
		double percent = 0;
		for (Integer id : PLs) {
			percent = getFilling(id);
			fillings.put(id, percent);
		}
		return fillings;
	}

	/**
	 * @return pocet aut, ktore parkovali v zadany den na zadanom parkovisku
	 */
	public int getNumberOfCarsOnGivenDay(Date date, int parkingLotId) {
		String formatedDate = DATE_FORMAT.format(date);
		query = "SELECT COUNT(*) FROM ticket WHERE parkinglot_id = " + parkingLotId + 
				" AND arrivalDate <= '" + formatedDate + 
				"' AND (departureDate IS NULL OR departureDate >= '" + formatedDate + "')";
		int num = jdbcTemplate.queryForObject(query, Integer.class);
		//System.out.println("V deň " + date + " bolo na parkovisku " + parkingLotId + ": " + num + " áut.");
		return num;
	}
	

	/**
	 * @return id vytvoreneho parkoviska
	 */
	public int createParkingLot(int capacity) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		simpleJdbcInsert.withTableName("parkinglot");
		simpleJdbcInsert.usingGeneratedKeyColumns("id");
		simpleJdbcInsert.usingColumns("capacity","numberOfCarsIn");
		Map<String,Object> toInsert = new HashMap<String, Object>();
		toInsert.put("capacity", capacity);
		toInsert.put("numberOfCarsIn", 0);
		return simpleJdbcInsert.executeAndReturnKey(toInsert).intValue();
	}
}
