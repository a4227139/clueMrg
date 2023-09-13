package com.wa.cluemrg.bo;

import com.wa.cluemrg.entity.TtClue;
import lombok.Data;

@Data
public class TtClueBo extends TtClue {

    String subOffice;
    String dateFormat;
    String dateFormatToday;
    String imei;
}
