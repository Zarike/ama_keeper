/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgmain;

import java.io.*;
//import java.lang.*;
//import java.util.ArrayList;
//import java.util.List;
import java.sql.*;

/**
 * 
 * @author Galiakhmetov.Z
 */
public class Parser {
	private final int len902X; // , len9020, len9021, len9025,
	private final int outStep;
	public int aaCnt, abCnt, aa9050Cnt, aa9051Cnt, dddCnt, isdnCnt, ssCnt;
	String dlRecStartDate, dlRecEndDate, fileYear, srcType;
	private OraInsertor o;
	private String src_id, station_id;
	

	Parser(String psrc_id, String pstat_id) throws Exception {
		// this.len9025 = 89;
		// this.len9021 = 80;
		// this.len9020 = 84;
		this.len902X = 12;
		this.aaCnt = 0;
		this.abCnt = 0;
		this.dddCnt = 0;
		this.isdnCnt = 0;
		this.ssCnt = 0;
		this.outStep = 10000;
		this.dlRecStartDate = "";
		this.dlRecEndDate = "";
		this.fileYear = "";
		//this.o = new OraInsertor();
		this.src_id = psrc_id;
		this.station_id =pstat_id ;
	}

	void DoParse(String FileName, Connection conn) throws FileNotFoundException, IOException,SQLException {
		FileInputStream fin = null;
		try {
			byte[] bArr902X = new byte[this.len902X];
			byte[] bArr = new byte[1]; // для побайтового чтения
			byte[] bArr1 = new byte[3]; // для даты
			String[][] strArr = null;
			String strB = "";
			int outCnt = 0;

			fin = new FileInputStream(FileName);
			while ((fin.read(bArr)) != -1) {
				strB = Buf2hex(bArr[0]);
				if (strB.equals("AA") || strB.equals("AB")) {
					if (strB.equals("AA")) {
						aaCnt++;
					} else if (strB.equals("AB")) {
						abCnt++;
						continue; // AB нам не нужен
					}
					fin.read(bArr);
					strB = Buf2hex(bArr[0]);
					if (strB.equals("90")) {
						fin.read(bArr);
						strB = Buf2hex(bArr[0]);
						if (strB.equals("50")) {
							aa9050Cnt++;
							if (this.aaCnt == 0) {
								fin.skip(8);
								fin.read(bArr1);
								dlRecStartDate = "";// GetFileDate(ArrByte);
								fileYear = ""; // GetFileYear(FDLRecStartDate);
							} else {
								continue;
							}
						} else if (strB.equals("51")) {
							fin.skip(8);
							fin.read(bArr1);
							aa9051Cnt++;
							dlRecEndDate = "";
							// Это признак начала разговора
						} else if (strB.equals("20") || strB.equals("20")
								|| strB.equals("20")) {
							fin.skip(-7);
							fin.read(bArr902X);
							if (strArr == null) {
								strArr = new String[outStep][outCnt];
							}
							AppendArray(strArr, bArr902X, outCnt);
							if (outCnt == outStep) {
								o.DataInsertor(strArr);
								strArr = null;
								outCnt = 0;
							} else {
								outCnt++;
							}
							if (strB.equals("20")) {
								dddCnt++;
							} else if (strB.equals("21")) {
								isdnCnt++;
							} else if (strB.equals("25")) {
								ssCnt++;
							}
						}
					}
				}
			}
			// Зальем остаток
			if (outCnt >= 0) {
				o.DataInsertor(strArr);
				strArr = null;
				outCnt = 0;
			}		
		} catch (Exception e){
			throw e;		
		} finally {
			if (fin != null) {
				fin.close();
			}
		}
	}

	void AppendArray(String s[][], byte b[], int i) {
		String sic, tc, cdc, itg, otg, opn, tpn, dtcc, ctime;
		sic = b[6] + b[7] + "";
		opn = GetPhoneNum(b[14], b[15], b[16], b[17], b[18], b[19], b[20],
				b[21], b[22]);
		tpn = GetPhoneNum(b[23], b[24], b[25], b[26], b[27], b[28], b[29],
				b[30], b[31], b[32], b[33], b[34], b[35], b[36], b[37], b[38],
				b[39]);

		if (sic.equals("9020")) {
			dtcc = GetServiceDateOfCall(b[49], b[50], b[51], b[52], b[53],
					b[54]);
			cdc = b[62] + b[63] + "";
			otg = b[64] + b[65] + "";
			itg = b[66] + b[67] + "";
			ctime = GetDuration(b[68], b[69], b[70]);
			tc = b[82] + "";
		} else {
			cdc = b[58] + b[59] + "";
			otg = b[60] + b[61] + "";
			itg = b[62] + b[63] + "";
			dtcc = GetServiceDateOfCall(b[45], b[46], b[47], b[48], b[49],
					b[50]);
			ctime = GetDuration(b[64], b[65], b[66]);
			tc = b[78] + "";
		}

		s[i][0] = sic;
		s[i][1] = tc;
		s[i][2] = cdc;
		s[i][3] = itg;
		s[i][4] = otg;
		s[i][5] = opn;
		s[i][6] = tpn;
		s[i][7] = dtcc;
		s[i][8] = ctime;
		s[i][9] = CalcReportDateID(dtcc);
		s[i][10] = this.src_id;
		s[i][11] = this.station_id;
	}

	String Buf2hex(byte b) {
		String result;
		int bufer = (int) b;
		switch (bufer) {
		case 170:
			result = "AA";
			break;
		case 171:
			result = "AB";
			break;
		default:
			result = Character.toString((char) (48 + bufer / 16))
					+ Character.toString((char) (48 + bufer % 16));
		}
		return result;
		// chr(48 + bufer div 16) + chr(48 + bufer mod 16);
	}

	String GetServiceDateOfCall(byte... b) {
		StringBuilder sb = new StringBuilder();
		sb.append("13"); // YY
		sb.append(b[2]).append(b[3]); // MM
		sb.append(b[4]).append(b[5]); // DD
		sb.append(b[6]).append(b[7]); // HH
		sb.append(b[8]).append(b[9]); // MI
		sb.append(b[11]).append(b[12]); // SS
		return sb.toString();
	}

	String GetPhoneNum(byte... b) {
		StringBuilder sb = new StringBuilder();
		int i;
		int len = b[0] / 2;
		for (i = len; i >= 0; i--) {
			sb.append(b[i]);
		}
		return sb.toString();
	}

	String GetDuration(byte... b) {
		StringBuilder sb = new StringBuilder();
		int i = b[0] * 1000 + b[1] * 100 + b[2] * 10 + b[3] * 1; // РњРёРЅСѓС‚С‹
		for (i = 0; i <= 3; i++) {
			sb.append(Buf2hex(b[i]));
		}
		return Integer.parseInt(sb.substring(0, 4)) * 60
				+ Integer.parseInt(sb.substring(5, 2)) + "";
	}

	String CalcReportDateID(String sdate) {
		return (Integer.parseInt(sdate.substring(0, 2)) - 2013) * 12
				+ Integer.parseInt(sdate.substring(3, 2)) + "";
	}


	
}

