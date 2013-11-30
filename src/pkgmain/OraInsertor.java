package pkgmain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class OraInsertor {
	public Connection conn;
	//private String srcId;

	OraInsertor() throws Exception {
		Class.forName("oracle.jdbc.OracleDriver");
		conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//10.201.1.51:1521/arch", "db", "ciuyrhvv");
		// @machineName:port/SID, userid, password
		conn.setAutoCommit(false);
	}
	
	String GetSrcID() throws SQLException {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("select seq_src.nextval from dual");
			return rset.getString("nextval");
		} catch (Exception e) {
			conn.close();
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	void ReportInsertor(String srcId) throws SQLException {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("insert into db.src("+
			"id, station_id, source_type_id, rec_begin_date, rec_end_date, "+
			"report, path, filename, filesize, crc32, system_date, success"+
			")values(?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );
			pst.setString(1,"1" );			
		} catch (Exception e) {
			conn.close();
			throw e;
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
	}

	void DataInsertor(String s[][]) throws SQLException {
		PreparedStatement pst = null;
		try {
			pst = conn
					.prepareStatement("insert into subrev("
							+ "sic, tc, cdc, itg, otg, opn, tpn, service_date, duration,"
							+ "report_date_id, src_id, station_id"
							+ ")values(?,?,?,?,?,?,?,?,?,?,?,?)");

			int rowcount = s.length;
			int i;
			for (i = 0; i <= --rowcount; i++) {
				pst.setString(1, s[i][0]);
				pst.setString(2, s[i][1]);
				pst.setString(3, s[i][2]);
				pst.setString(4, s[i][3]);
				pst.setString(5, s[i][4]);
				pst.setString(6, s[i][5]);
				pst.setString(7, s[i][6]);
				pst.setString(8, s[i][7]);
				pst.setString(9, s[i][8]);
				pst.setString(10, s[i][9]);
				pst.setString(11, s[i][10]);
				pst.setString(12,s[i][11]);
				pst.addBatch();
			}
			pst.executeBatch();
		} catch (Exception e) {
			conn.close();
			throw e;
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
	}
	
	public void InsertSources(Source src) throws Exception, IOException, SQLException{
		src.src_id= GetSrcID();
		src.station_id = "1";
		
		
    	Parser p = new Parser(src.src_id ,src.station_id );
    	p.DoParse("d:/incoming/Ama_Keeper/tmp/",conn); 
    	//ReportInsertor(src);
    	conn.commit();
	}
	
}
