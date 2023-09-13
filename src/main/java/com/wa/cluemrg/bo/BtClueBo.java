package com.wa.cluemrg.bo;

import com.wa.cluemrg.entity.BtClue;
import lombok.Data;

@Data
public class BtClueBo extends BtClue {

    String subOffice;
    String dateFormat;
    String dateFormatToday;
    String imei;
}
