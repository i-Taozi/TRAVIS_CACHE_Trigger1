package com.godcheese.nimrod.common.exportbyexcel;

import com.godcheese.nimrod.common.others.SpringContextUtil;
import com.godcheese.nimrod.system.entity.DictionaryEntity;
import com.godcheese.nimrod.system.service.DictionaryService;
import com.godcheese.nimrod.system.service.impl.DictionaryServiceImpl;

import java.util.List;

/**
 * @author godcheese [godcheese@outlook.com]
 * @date 2019-01-09
 */
public class IsOrNotExportByExcelHandler extends ExportByExcelHandler {

    private List<DictionaryEntity> dictionaryEntityList;

    public IsOrNotExportByExcelHandler() {
        DictionaryService dictionaryService = (DictionaryService) SpringContextUtil.getBean(DictionaryServiceImpl.class);
        dictionaryEntityList = dictionaryService.get("IS_OR_NOT");
    }

    @Override
    public Object exportHandler(Object object) {
        for (DictionaryEntity dictionaryEntity : dictionaryEntityList) {
            if (dictionaryEntity.getValue().equals(String.valueOf(object))) {
                return dictionaryEntity.getValueName();
            }
        }
        return object;
    }

    @Override
    public Object importHandler(Object object) {
        for (DictionaryEntity dictionaryEntity : dictionaryEntityList) {
            if (dictionaryEntity.getValueName().equals(object)) {
                return dictionaryEntity.getValue();
            }
        }
        return object;
    }
}
