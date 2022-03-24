package com.godcheese.nimrod.common.exportbyexcel;

import com.godcheese.tile.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author godcheese [godcheese@outlook.com]
 * @date 2019-01-09
 */
public class SimpleDateFormatExportByExcelHandler extends ExportByExcelHandler {

    @Override
    public Object exportHandler(Object object) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT_PATTERN);
        if (object != null) {
            return simpleDateFormat.format(object);
        } else {
            return "";
        }
    }

    @Override
    public Object importHandler(Object object) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT_PATTERN);
        try {
            return simpleDateFormat.parse((String) object);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
