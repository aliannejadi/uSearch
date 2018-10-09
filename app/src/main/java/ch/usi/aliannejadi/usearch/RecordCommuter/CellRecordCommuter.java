package ch.usi.aliannejadi.usearch.recordCommuter;

import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import ch.usi.aliannejadi.usearch.Log;

import java.util.ArrayList;
import java.util.List;

import ch.usi.aliannejadi.usearch.record.CellRecord;

/**
 * Created by jacopofidacaro on 03.07.17.
 */

public class CellRecordCommuter extends CachableRecordCommuter {

    // cached Record
    private static CellRecord cachedCellRecord = null;

    // log tag
    private static String CCACHE = "usearch.Rec.cache.cell";

    // constructor automatically converts the passed object to POJO
    public CellRecordCommuter(List<CellInfo> cellInfoList) {

        super("cell");

        List<String> protocols = new ArrayList<>();
        List<Long> mccs = new ArrayList<>();
        List<Long> mncs = new ArrayList<>();
        List<Long> lacs = new ArrayList<>();
        List<Long> cids = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();

        for (CellInfo cellRecord : cellInfoList) {
            if (cellRecord instanceof CellInfoGsm) {
                CellInfoGsm gsmRecord = (CellInfoGsm) cellRecord;
                CellIdentityGsm gsmInfo = gsmRecord.getCellIdentity();
                protocols.add("gsm");
                mccs.add((long) gsmInfo.getMcc());
                mncs.add((long) gsmInfo.getMnc());
                lacs.add((long) gsmInfo.getLac());
                cids.add((long) gsmInfo.getCid());
                long timeInMillis = System.currentTimeMillis() + ((gsmRecord.getTimeStamp() -
                        System.nanoTime()) / 1000000L);
                timestamps.add(timeInMillis);
            } else if (cellRecord instanceof CellInfoWcdma) {
                CellInfoWcdma wcdmaRecord = (CellInfoWcdma) cellRecord;
                CellIdentityWcdma wcdmaInfo = wcdmaRecord.getCellIdentity();
                protocols.add("wcdma");
                mccs.add((long) wcdmaInfo.getMcc());
                mncs.add((long) wcdmaInfo.getMnc());
                lacs.add((long) wcdmaInfo.getLac());
                cids.add((long) wcdmaInfo.getCid());
                long timeInMillis = System.currentTimeMillis() + ((wcdmaRecord.getTimeStamp() -
                        System.nanoTime()) / 1000000L);
                timestamps.add(timeInMillis);
            } else if (cellRecord instanceof CellInfoLte) {
                CellInfoLte lteRecord = (CellInfoLte) cellRecord;
                CellIdentityLte lteInfo = lteRecord.getCellIdentity();
                protocols.add("lte");
                mccs.add((long) lteInfo.getMcc());
                mncs.add((long) lteInfo.getMnc());
                lacs.add((long) lteInfo.getTac());
                cids.add((long) lteInfo.getCi());
                long timeInMillis = System.currentTimeMillis() + ((lteRecord.getTimeStamp() -
                        System.nanoTime()) / 1000000L);
                timestamps.add(timeInMillis);
            }
        }

        record = new CellRecord(protocols, mccs, mncs, lacs, cids, timestamps);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "CellCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

    @Override
    protected boolean recordIsMeaningful() {

        CellRecord mRecord = (CellRecord) record;

        if (cachedCellRecord == null) {
            Log.i(CCACHE, "cell cache is empty");
            cachedCellRecord = mRecord;
            return true;
        }

        boolean res = false;

        if (cachedCellRecord.protocol.equals(mRecord.protocol)) {
            Log.i(CCACHE, "cell protocol cache hit");
            mRecord.protocol = null;
        } else {
            Log.i(CCACHE, "cell protocol cache miss");
            cachedCellRecord.protocol = mRecord.protocol;
            res = true;
        }

        if (cachedCellRecord.MCC.equals(mRecord.MCC)) {
            Log.i(CCACHE, "cell MCC cache hit");
            mRecord.MCC = null;
        } else {
            Log.i(CCACHE, "cell MCC cache miss");
            cachedCellRecord.MCC = mRecord.MCC;
            res = true;
        }

        if (cachedCellRecord.MNC.equals(mRecord.MNC)) {
            Log.i(CCACHE, "cell MNC cache hit");
            mRecord.MNC = null;
        } else {
            Log.i(CCACHE, "cell MNC cache miss");
            cachedCellRecord.MNC = mRecord.MNC;
            res = true;
        }

        if (cachedCellRecord.LAC.equals(mRecord.LAC)) {
            Log.i(CCACHE, "cell LAC cache hit");
            mRecord.LAC = null;
        } else {
            Log.i(CCACHE, "cell LAC cache miss");
            cachedCellRecord.LAC = mRecord.LAC;
            res = true;
        }

        if (cachedCellRecord.CID.equals(mRecord.CID)) {
            Log.i(CCACHE, "cell CID cache hit");
            mRecord.CID = null;
        } else {
            Log.i(CCACHE, "cell CID cache miss");
            cachedCellRecord.CID = mRecord.CID;
            res = true;
        }

        return res;

    }

}
