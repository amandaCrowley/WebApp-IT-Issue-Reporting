import java.sql.Connection;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBConnection {
	public static Connection getConnection()
	{
		Connection conn = null;
		try {
			DataSource ds = (DataSource)new InitialContext().lookup("java:/comp/env/SENG2050_2018");
			conn = ds.getConnection();		

			}catch (Exception e) {
				e.printStackTrace();
			}
			return conn;
		}
	}
