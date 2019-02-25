package parking.DAO;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mysql.cj.jdbc.MysqlDataSource;

public enum DAOFactory {
	INSTANCE;

	private MysqlParkingDAO mysqlParkingDAO;
	private JdbcTemplate jdbcTemplate;
	
	public MysqlParkingDAO getMysqlParkingDAO() {
		if (mysqlParkingDAO == null) {
			mysqlParkingDAO = new MysqlParkingDAO(getJdbcTemplate());
		}
		return mysqlParkingDAO;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser("parker_ddt");
			dataSource.setPassword("car");
			dataSource.setUrl("jdbc:mysql://localhost/parking_ddt?serverTimezone=Europe/Bratislava");
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		return jdbcTemplate;
	}	
}
