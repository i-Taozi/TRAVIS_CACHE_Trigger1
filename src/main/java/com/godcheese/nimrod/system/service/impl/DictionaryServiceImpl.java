package com.godcheese.nimrod.system.service.impl;

import com.godcheese.nimrod.common.easyui.Pagination;
import com.godcheese.nimrod.common.exportbyexcel.ExportByExcelUtil;
import com.godcheese.nimrod.common.others.FailureEntity;
import com.godcheese.nimrod.system.entity.DictionaryEntity;
import com.godcheese.nimrod.system.mapper.DictionaryMapper;
import com.godcheese.nimrod.system.service.DictionaryService;
import com.godcheese.tile.office.ExcelUtil;
import com.godcheese.tile.util.DateUtil;
import com.godcheese.tile.web.exception.BaseResponseException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author godcheese [godcheese@outlook.com]
 * @date 2018-02-22
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryServiceImpl.class);

    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FailureEntity failureEntity;

    @Override
    public void addDictionaryToServletContext() {
        /**
         * 是否有效（0=否，1=是，默认=0）
         */
        final int isOrNotIs = 1;
        final int isOrNotNot = 0;

        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext != null) {
            List<DictionaryEntity> dictionaryEntityList = dictionaryMapper.listAll();
            if (dictionaryEntityList != null) {

                // 添加到内存供 servletContext.getAttribute 获取，如：servletContext.getAttribute('WEB.NAME')、${#servletContext.getAttribute('WEB.NAME')}
                for (DictionaryEntity dictionaryEntity : dictionaryEntityList) {
                    if (isOrNotIs == dictionaryEntity.getEnabled()) {
                        servletContext.setAttribute(dictionaryEntity.getKey().toUpperCase() + "." + dictionaryEntity.getValueSlug().toUpperCase(), dictionaryEntity.getValue());
                    }
                }

                // 添加到内存供字典键直接获取，如：WEB
                Map<String, List<DictionaryEntity>> dictionaryEntityMap = new HashMap<>(6);
                for (DictionaryEntity dictionaryEntity : dictionaryEntityList) {
                    if (isOrNotIs == dictionaryEntity.getEnabled()) {
                        String key = dictionaryEntity.getKey().toUpperCase();
                        if (dictionaryEntityMap.containsKey(key)) {
                            List<DictionaryEntity> dictionaryEntityList1 = dictionaryEntityMap.get(key);
                            if (!dictionaryEntityList1.contains(dictionaryEntity)) {
                                dictionaryEntityList1.add(dictionaryEntity);
                                dictionaryEntityMap.put(key, dictionaryEntityList1);
                            }
                        } else {
                            List<DictionaryEntity> dictionaryEntityList2 = new ArrayList<>(1);
                            dictionaryEntityList2.add(dictionaryEntity);
                            dictionaryEntityMap.put(key, dictionaryEntityList2);
                        }
                    }
                }
                for (Map.Entry entry : dictionaryEntityMap.entrySet()) {
                    servletContext.setAttribute((String) entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * 从数据库中获取
     *
     * @param key       数据字典键
     * @param valueSlug 数据字典值别名
     * @return
     */
    @Override
    public Object getFromDatabase(String key, String valueSlug) {
        DictionaryEntity dictionaryEntity = dictionaryMapper.getOneByKeyAndValueSlug(key.toUpperCase(), valueSlug.toUpperCase());
        if (dictionaryEntity != null) {
            return dictionaryEntity.getValue();
        }
        return null;
    }

    /**
     * 从内存中获取
     *
     * @param key
     * @param valueSlug
     * @return
     */
    private Object getValueByKeyAndValueSlug(String key, String valueSlug) {
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext != null) {
            return servletContext.getAttribute(key + "." + valueSlug);
        } else {
            return null;
        }
    }

    /**
     * 从内存中获取字典
     *
     * @param key       数据字典键
     * @param valueSlug 数据字典值别名
     * @return Object
     */
    @Override
    public Object get(String key, String valueSlug) {
        return getValueByKeyAndValueSlug(key, valueSlug);
    }

    /**
     * 从内存中获取
     *
     * @param key          数据字典键
     * @param valueSlug    数据字典值别名
     * @param defaultValue 数据字典默认值
     * @return
     */
    @Override
    public Object get(String key, String valueSlug, Object defaultValue) {
        Object v = getValueByKeyAndValueSlug(key, valueSlug);
        if (v != null) {
            return v;
        }
        return defaultValue;
    }

    /**
     * 从内存获取
     *
     * @param key
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DictionaryEntity> get(String key) {
        return (List<DictionaryEntity>) Objects.requireNonNull(webApplicationContext.getServletContext()).getAttribute(key.toUpperCase());
    }

    @Override
    public Map<String, Map<String, Object>> keyValueMap() {
        Map<String, Map<String, Object>> mapMap = new HashMap<>(6);
        List<DictionaryEntity> dictionaryEntityList = dictionaryMapper.listAll();
        if (dictionaryEntityList != null) {
            for (DictionaryEntity dictionaryEntity : dictionaryEntityList) {
                if (mapMap.containsKey(dictionaryEntity.getKey())) {
                    Map<String, Object> valueMap = mapMap.get(dictionaryEntity.getKey());
                    if (!valueMap.containsKey(dictionaryEntity.getValueSlug())) {
                        valueMap.put(dictionaryEntity.getValueSlug(), dictionaryEntity.getValue());
                    }
                    mapMap.put(dictionaryEntity.getKey(), valueMap);
                } else {
                    Map<String, Object> valueMap = new HashMap<>(1);
                    valueMap.put(dictionaryEntity.getValueSlug(), dictionaryEntity.getValue());
                    mapMap.put(dictionaryEntity.getKey(), valueMap);
                }
            }
        }
        return mapMap;
    }

    @Override
    public Pagination<DictionaryEntity> pageAllByDictionaryCategoryId(Long dictionaryCategoryId, Integer page, Integer rows) {
        Pagination<DictionaryEntity> pagination = new Pagination<>();
        PageHelper.startPage(page, rows);
        Page<DictionaryEntity> dictionaryEntityPage = dictionaryMapper.pageAllByDictionaryCategoryId(dictionaryCategoryId);
        pagination.setRows(dictionaryEntityPage.getResult());
        pagination.setTotal(dictionaryEntityPage.getTotal());
        return pagination;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DictionaryEntity addOne(DictionaryEntity dictionaryEntity) {
        Date date = new Date();
        dictionaryEntity.setKey(dictionaryEntity.getKey().toUpperCase());
        dictionaryEntity.setValueSlug(dictionaryEntity.getValueSlug().toUpperCase());
        dictionaryEntity.setGmtModified(date);
        dictionaryEntity.setGmtCreated(date);
        dictionaryMapper.insertOne(dictionaryEntity);
        return dictionaryEntity;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DictionaryEntity saveOne(DictionaryEntity dictionaryEntity) {
        DictionaryEntity dictionaryEntity1 = dictionaryMapper.getOne(dictionaryEntity.getId());
        dictionaryEntity1.setKeyName(dictionaryEntity.getKeyName());
        dictionaryEntity1.setKey(dictionaryEntity.getKey());
        dictionaryEntity1.setKey(dictionaryEntity.getKey().toUpperCase());
        dictionaryEntity1.setValueName(dictionaryEntity.getValueName());
        dictionaryEntity1.setValueSlug(dictionaryEntity.getValueSlug().toUpperCase());
        dictionaryEntity1.setValue(dictionaryEntity.getValue());
        dictionaryEntity1.setDictionaryCategoryId(dictionaryEntity.getDictionaryCategoryId());
        dictionaryEntity1.setEnabled(dictionaryEntity.getEnabled());
        dictionaryEntity1.setSort(dictionaryEntity.getSort());
        dictionaryEntity1.setRemark(dictionaryEntity.getRemark());
        dictionaryEntity1.setGmtModified(new Date());
        dictionaryMapper.updateOne(dictionaryEntity1);
        return dictionaryEntity1;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int deleteAll(List<Long> idList) {
        return dictionaryMapper.deleteAll(idList);
    }

    @Override
    public DictionaryEntity getOne(Long id) {
        return dictionaryMapper.getOne(id);
    }

    @Override
    public void exportAllByDictionaryCategoryIdList(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, List<Long> idList) throws BaseResponseException {
        List<DictionaryEntity> dictionaryEntityList = new ArrayList<>();
        for (Long id : idList) {
            dictionaryEntityList.addAll(dictionaryMapper.listAllByDictionaryCategoryId(id));
        }
        String filename = "数据字典_" + DateUtil.getNow("yyyyMMddHHmmss") + ".xls";
        ExportByExcelUtil.exportEntity(httpServletRequest, httpServletResponse, dictionaryEntityList, DictionaryEntity.class, filename);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void importAllByDictionaryCategoryId(MultipartFile multipartFile, Long categoryId) throws BaseResponseException {
        try {
            List<Map<Integer, Cell>> list = uploadAndReadExcel(multipartFile);
            if (list != null && !list.isEmpty()) {
                list.remove(0);
                for (Map<Integer, Cell> map : list) {
                    DictionaryEntity dictionaryEntity = new DictionaryEntity();
                    dictionaryEntity.setDictionaryCategoryId(categoryId);
                    DictionaryEntity dictionaryEntity1 = ExportByExcelUtil.importEntity(dictionaryEntity, map);
                    int effectRows = dictionaryMapper.insertOne(dictionaryEntity1);
                    if (effectRows <= 0) {
                        throw new BaseResponseException(failureEntity.i18n("dictionary.import_fail"));
                    }
                }
            }

        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new BaseResponseException(failureEntity.i18n("dictionary.import_fail"));
        }

    }

    private List<Map<Integer, Cell>> uploadAndReadExcel(MultipartFile multipartFile) throws IOException {
        List<Map<Integer, Cell>> list = new ArrayList<>();
        Workbook workbook = ExcelUtil.getWorkbook(Objects.requireNonNull(multipartFile.getOriginalFilename()), multipartFile.getInputStream());
        if (workbook != null) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex;
            for (rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                int cellIndex;
                Map<Integer, Cell> map = new HashMap<>(1);
                for (cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    map.put(cellIndex, row.getCell(cellIndex));
                }
                list.add(map);
            }
        }
        return list.isEmpty() ? null : list;
    }
}
