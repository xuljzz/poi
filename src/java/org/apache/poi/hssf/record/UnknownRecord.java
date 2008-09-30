/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/**
 * Title:        Unknown Record (for debugging)<P>
 * Description:  Unknown record just tells you the sid so you can figure out
 *               what records you are missing.  Also helps us read/modify sheets we
 *               don't know all the records to.  (HSSF leaves these alone!) <P>
 * Company:      SuperLink Software, Inc.<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @author Glen Stampoultzis (glens at apache.org)
 */
public final class UnknownRecord extends Record {

	/*
	 * Some Record IDs used by POI as 'milestones' in the record stream
	 */
	public static final int PLS_004D             = 0x004D;
	public static final int SHEETPR_0081         = 0x0081;
	public static final int STANDARDWIDTH_0099   = 0x0099;
	public static final int SCL_00A0             = 0x00A0;
	public static final int BITMAP_00E9          = 0x00E9;
	public static final int PHONETICPR_00EF      = 0x00EF;
	public static final int LABELRANGES_015F     = 0x015F;
	public static final int QUICKTIP_0800        = 0x0800;
	public static final int SHEETEXT_0862        = 0x0862; // OOO calls this SHEETLAYOUT
	public static final int SHEETPROTECTION_0867 = 0x0867;
	public static final int RANGEPROTECTION_0868 = 0x0868;
	
	private int _sid;
	private byte[] _rawData;

	/**
	 * @param id	id of the record -not validated, just stored for serialization
	 * @param data  the data
	 */
	public UnknownRecord(int id, byte[] data) {
	  _sid = id & 0xFFFF;
	  _rawData = data;
	}


	/**
	 * construct an unknown record.  No fields are interpreted and the record will
	 * be serialized in its original form more or less
	 * @param in the RecordInputstream to read the record from
	 */
	public UnknownRecord(RecordInputStream in) {
		_sid = in.getSid();
		_rawData = in.readRemainder();
		if (false && getBiffName(_sid) == null) {
			// unknown sids in the range 0x0004-0x0013 are probably 'sub-records' of ObjectRecord
			// those sids are in a different number space.
			// TODO - put unknown OBJ sub-records in a different class
			System.out.println("Unknown record 0x" + Integer.toHexString(_sid).toUpperCase());
		}
	}

	/**
	 * spit the record out AS IS. no interpretation or identification
	 */
	public final int serialize(int offset, byte[] data) {
		LittleEndian.putUShort(data, 0 + offset, _sid);
		int dataSize = _rawData.length;
		LittleEndian.putUShort(data, 2 + offset, dataSize);
		System.arraycopy(_rawData, 0, data, 4 + offset, dataSize);
		return 4 + dataSize;
	}

	public final int getRecordSize() {
		return 4 + _rawData.length;
	}

	/**
	 * print a sort of string representation ([UNKNOWN RECORD] id = x [/UNKNOWN RECORD])
	 */
	public final String toString() {
		String biffName = getBiffName(_sid);
		if (biffName == null) {
			biffName = "UNKNOWNRECORD";
		}
		StringBuffer sb = new StringBuffer();

		sb.append("[").append(biffName).append("] (0x");
		sb.append(Integer.toHexString(_sid).toUpperCase() + ")\n");
		if (_rawData.length > 0) {
			sb.append("  rawData=").append(HexDump.toHex(_rawData)).append("\n");
		}
		sb.append("[/").append(biffName).append("]\n");
		return sb.toString();
	}

	public final short getSid() {
		return (short) _sid;
	}

	/**
	 * These BIFF record types are known but still uninterpreted by POI
	 * 
	 * @return the documented name of this BIFF record type
	 */
	private static String getBiffName(int sid) {
		// Note to POI developers:
		// Make sure you delete the corresponding entry from 
		// this method any time a new Record subclass is created.
		switch (sid) {
			case PLS_004D: return "PLS";
			case 0x0050: return "DCON";
			case 0x007F: return "IMDATA";
			case SHEETPR_0081: return "SHEETPR";
			case 0x0090: return "SORT";
			case 0x0094: return "LHRECORD";
			case STANDARDWIDTH_0099: return "STANDARDWIDTH";
			case 0x009D: return "AUTOFILTERINFO";
			case SCL_00A0: return "SCL";
			case 0x00AE: return "SCENMAN";
			case 0x00D3: return "OBPROJ";
			case 0x00DC: return "PARAMQRY";
			case 0x00DE: return "OLESIZE";
			case BITMAP_00E9: return "BITMAP";
			case PHONETICPR_00EF: return "PHONETICPR";

			case LABELRANGES_015F: return "LABELRANGES";
			case 0x01BA: return "CODENAME";
			case 0x01A9: return "USERBVIEW";
			case 0x01AA: return "USERSVIEWBEGIN";
			case 0x01AB: return "USERSVIEWEND";
			case 0x01AD: return "QSI";

			case 0x01C0: return "EXCEL9FILE";

			case 0x0802: return "QSISXTAG";
			case 0x0803: return "DBQUERYEXT";
			case 0x0805: return "TXTQUERY";

			case QUICKTIP_0800: return "QUICKTIP";
			case 0x0850: return "CHARTFRTINFO";
			case 0x0852: return "STARTBLOCK";
			case 0x0853: return "ENDBLOCK";
			case 0x0856: return "CATLAB";
			case SHEETEXT_0862: return "SHEETEXT";
			case 0x0863: return "BOOKEXT";
			case SHEETPROTECTION_0867: return "SHEETPROTECTION";
			case RANGEPROTECTION_0868: return "RANGEPROTECTION";
			case 0x086B: return "DATALABEXTCONTENTS";
			case 0x086C: return "CELLWATCH";
			case 0x0874: return "DROPDOWNOBJIDS";
			case 0x0876: return "DCONN";
			case 0x087B: return "CFEX";
			case 0x087C: return "XFCRC";
			case 0x087D: return "XFEXT";
			case 0x088B: return "PLV";
			case 0x088C: return "COMPAT12";
			case 0x088D: return "DXF";
			case 0x088E: return "TABLESTYLES";
			case 0x0892: return "STYLEEXT";
			case 0x0896: return "THEME";
			case 0x0897: return "GUIDTYPELIB";
			case 0x089A: return "MTRSETTINGS";
			case 0x089B: return "COMPRESSPICTURES";
			case 0x089C: return "HEADERFOOTER";
			case 0x08A3: return "FORCEFULLCALCULATION";
			case 0x08A4: return "SHAPEPROPSSTREAM";
			case 0x08A5: return "TEXTPROPSSTREAM";
			case 0x08A6: return "RICHTEXTSTREAM";

			case 0x08C8: return "PLV{Mac Excel}";

			case 0x1051: return "SHAPEPROPSSTREAM";

		}
		if (isObservedButUnknown(sid)) {
			return "UNKNOWN-" + Integer.toHexString(sid).toUpperCase();
		}

		return null;
	}

	/**
	 * 
	 * @return <code>true</code> if the unknown record id has been observed in POI unit tests
	 */
	private static boolean isObservedButUnknown(int sid) {
		switch (sid) {
			case 0x0033: 
				// contains 2 bytes of data: 0x0001 or 0x0003
			case 0x0034:
				// Seems to be written by MSAccess
				// contains text "[Microsoft JET Created Table]0021010"
				// appears after last cell value record and before WINDOW2
			case 0x01BD:
			case 0x01C2:
				// Written by Excel 2007 
				// rawData is multiple of 12 bytes long
				// appears after last cell value record and before WINDOW2 or drawing records
			case 0x089D:
			case 0x089E:
			case 0x08A7:

			case 0x1001:
			case 0x1006:
			case 0x1007:
			case 0x1009:
			case 0x100A:
			case 0x100B:
			case 0x100C:
			case 0x1014:
			case 0x1017:
			case 0x1018:
			case 0x1019:
			case 0x101A:
			case 0x101B:
			case 0x101D:
			case 0x101E:
			case 0x101F:
			case 0x1020:
			case 0x1021:
			case 0x1022:
			case 0x1024:
			case 0x1025:
			case 0x1026:
			case 0x1027:
			case 0x1032:
			case 0x1033:
			case 0x1034:
			case 0x1035:
			case 0x103A:
			case 0x1041:
			case 0x1043:
			case 0x1044:
			case 0x1045:
			case 0x1046:
			case 0x104A:
			case 0x104B:
			case 0x104E:
			case 0x104F:
			case 0x1051:
			case 0x105C:
			case 0x105D:
			case 0x105F:
			case 0x1060:
			case 0x1062:
			case 0x1063:
			case 0x1064:
			case 0x1065:
			case 0x1066:
				return true;
		}
		return false;
	}

	protected final void fillFields(RecordInputStream in) {
		throw new RecordFormatException(
				"Unknown record cannot be constructed via offset -- we need a copy of the data");
	}

	public final Object clone() {
		// immutable - ok to return this
		return this;
	}
}
