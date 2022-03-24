package com.luoboduner.moo.tool.ui.listener.func;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.google.common.collect.Lists;
import com.google.googlejavaformat.java.Formatter;
import com.luoboduner.moo.tool.App;
import com.luoboduner.moo.tool.dao.TQuickNoteMapper;
import com.luoboduner.moo.tool.domain.TQuickNote;
import com.luoboduner.moo.tool.ui.component.QuickNoteSyntaxTextViewerManager;
import com.luoboduner.moo.tool.ui.form.MainWindow;
import com.luoboduner.moo.tool.ui.form.func.FindResultForm;
import com.luoboduner.moo.tool.ui.form.func.QuickNoteForm;
import com.luoboduner.moo.tool.ui.frame.FindResultFrame;
import com.luoboduner.moo.tool.util.MybatisUtil;
import com.luoboduner.moo.tool.util.SqliteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 随手记事件监听
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">Zhou Bo</a>
 * @since 2019/8/15.
 */
@Slf4j
public class QuickNoteListener {

    private static TQuickNoteMapper quickNoteMapper = MybatisUtil.getSqlSession().getMapper(TQuickNoteMapper.class);

    public static String selectedName;

    public static void addListeners() {
        QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();
        QuickNoteSyntaxTextViewerManager quickNoteSyntaxTextViewerManager = QuickNoteForm.quickNoteSyntaxTextViewerManager;

        // 保存按钮
        quickNoteForm.getSaveButton().addActionListener(e -> {
            if (StringUtils.isEmpty(selectedName)) {
                selectedName = getDefaultFileName();
            }

            // show input dialog
            String name = JOptionPane.showInputDialog(MainWindow.getInstance().getMainPanel(), "名称", selectedName);
            if (StringUtils.isNotBlank(name)) {
                TQuickNote tQuickNote = quickNoteMapper.selectByName(name);

                if (tQuickNote == null) {
                    tQuickNote = new TQuickNote();
                }
                String now = SqliteUtil.nowDateForSqlite();
                tQuickNote.setName(name);
                tQuickNote.setContent(quickNoteSyntaxTextViewerManager.getCurrentText());
                tQuickNote.setCreateTime(now);
                tQuickNote.setModifiedTime(now);
                if (tQuickNote.getId() == null) {
                    quickNoteMapper.insert(tQuickNote);
                    QuickNoteForm.initNoteListTable();
                } else {
                    quickNoteMapper.updateByPrimaryKey(tQuickNote);
                }
            }
        });

        // 点击左侧表格事件
        quickNoteForm.getNoteListTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = quickNoteForm.getNoteListTable().getSelectedRow();
                viewByRowNum(selectedRow);

                // 显示下方删除按钮
                quickNoteForm.getDeletePanel().setVisible(true);

                super.mousePressed(e);
            }
        });


        // 删除按钮事件
        quickNoteForm.getDeleteButton().addActionListener(e -> {
            deleteFiles(quickNoteForm);
        });

        // 语法高亮下拉框事件
        quickNoteForm.getSyntaxComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String syntaxName = e.getItem().toString();

                if (StringUtils.isNotEmpty(syntaxName)) {
                    if (selectedName != null) {
                        TQuickNote tQuickNote = new TQuickNote();
                        tQuickNote.setName(selectedName);
                        tQuickNote.setSyntax(syntaxName);
                        String now = SqliteUtil.nowDateForSqlite();
                        tQuickNote.setModifiedTime(now);

                        quickNoteMapper.updateByName(tQuickNote);
                    }

                    quickSave(false);

                    quickNoteSyntaxTextViewerManager.removeRTextScrollPane(selectedName);
                    RTextScrollPane syntaxTextViewer = quickNoteSyntaxTextViewerManager.getRTextScrollPane(selectedName);
                    quickNoteForm.getContentSplitPane().setLeftComponent(syntaxTextViewer);
                    syntaxTextViewer.updateUI();
                }
            }
        });

        // 字体名称下拉框事件
        quickNoteForm.getFontNameComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String fontName = e.getItem().toString();
                int fontSize = Integer.parseInt(quickNoteForm.getFontSizeComboBox().getSelectedItem().toString());
                Font font = new Font(fontName, Font.PLAIN, fontSize);
                RSyntaxTextArea view = quickNoteSyntaxTextViewerManager.getCurrentRSyntaxTextArea();

                view.setFont(font);
                if (selectedName != null) {
                    TQuickNote tQuickNote = new TQuickNote();
                    tQuickNote.setName(selectedName);
                    tQuickNote.setFontSize(String.valueOf(fontSize));
                    tQuickNote.setFontName(fontName);
                    String now = SqliteUtil.nowDateForSqlite();
                    tQuickNote.setModifiedTime(now);

                    quickNoteMapper.updateByName(tQuickNote);
                }

                quickSave(false);

                App.config.setQuickNoteFontName(fontName);
                App.config.setQuickNoteFontSize(fontSize);
                App.config.save();

                quickNoteSyntaxTextViewerManager.removeRTextScrollPane(selectedName);
                RTextScrollPane syntaxTextViewer = quickNoteSyntaxTextViewerManager.getRTextScrollPane(selectedName);
                quickNoteForm.getContentSplitPane().setLeftComponent(syntaxTextViewer);
                syntaxTextViewer.updateUI();
            }
        });

        // 字体大小下拉框事件
        quickNoteForm.getFontSizeComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                int fontSize = Integer.parseInt(e.getItem().toString());
                String fontName = quickNoteForm.getFontNameComboBox().getSelectedItem().toString();
                Font font = new Font(fontName, Font.PLAIN, fontSize);

                if (selectedName != null) {
                    TQuickNote tQuickNote = new TQuickNote();
                    tQuickNote.setName(selectedName);
                    tQuickNote.setFontSize(String.valueOf(fontSize));
                    tQuickNote.setFontName(fontName);
                    String now = SqliteUtil.nowDateForSqlite();
                    tQuickNote.setModifiedTime(now);

                    quickNoteMapper.updateByName(tQuickNote);
                }

                quickSave(false);

                App.config.setQuickNoteFontName(fontName);
                App.config.setQuickNoteFontSize(fontSize);
                App.config.save();

                quickNoteSyntaxTextViewerManager.removeRTextScrollPane(selectedName);
                RTextScrollPane syntaxTextViewer = quickNoteSyntaxTextViewerManager.getRTextScrollPane(selectedName);
                quickNoteForm.getContentSplitPane().setLeftComponent(syntaxTextViewer);
                syntaxTextViewer.updateUI();
            }
        });

        // 自动换行按钮事件
        quickNoteForm.getWrapButton().addActionListener(e -> {
            RSyntaxTextArea view = quickNoteSyntaxTextViewerManager.getCurrentRSyntaxTextArea();
            view.setLineWrap(!view.getLineWrap());
        });

        // 添加按钮事件
        quickNoteForm.getAddButton().addActionListener(e -> {
            newNote();
        });


        // 左侧列表按键事件（重命名）
        quickNoteForm.getNoteListTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent evt) {

            }

            @Override
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    int selectedRow = quickNoteForm.getNoteListTable().getSelectedRow();
                    int noteId = Integer.parseInt(String.valueOf(quickNoteForm.getNoteListTable().getValueAt(selectedRow, 0)));
                    String name = String.valueOf(quickNoteForm.getNoteListTable().getValueAt(selectedRow, 1));

                    if (StringUtils.isNotBlank(name)) {
                        TQuickNote tQuickNote = new TQuickNote();
                        tQuickNote.setId(noteId);
                        tQuickNote.setName(name);
                        try {
                            quickNoteMapper.updateByPrimaryKeySelective(tQuickNote);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(App.mainFrame, "重命名失败，可能和已有笔记重名");
                            QuickNoteForm.initNoteListTable();
                            log.error(ExceptionUtils.getStackTrace(e));
                        }
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteFiles(quickNoteForm);
                } else if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
                    int selectedRow = quickNoteForm.getNoteListTable().getSelectedRow();
                    viewByRowNum(selectedRow);
                }
            }
        });

        // 查找按钮
        quickNoteForm.getFindButton().addActionListener(e -> {
            quickNoteForm.getFindReplacePanel().setVisible(true);
            quickNoteForm.getFindTextField().grabFocus();
            quickNoteForm.getFindTextField().selectAll();
        });

        // 快捷替换按钮
        quickNoteForm.getQuickReplaceButton().addActionListener(e -> {
            int totalWidth = quickNoteForm.getContentSplitPane().getWidth();
            int currentDividerLocation = quickNoteForm.getContentSplitPane().getDividerLocation();

            if (totalWidth - currentDividerLocation < 10) {
                quickNoteForm.getQuickReplaceScrollPane().setVisible(true);
                quickNoteForm.getContentSplitPane().setDividerLocation((int) (totalWidth * 0.62));
            } else {
                quickNoteForm.getContentSplitPane().setDividerLocation(totalWidth);
                quickNoteForm.getQuickReplaceScrollPane().setVisible(false);
            }
        });

        // 查找输入框
        quickNoteForm.getFindTextField().addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    find();
                }
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });

        // 列表按钮
        quickNoteForm.getListItemButton().addActionListener(e -> {
            int currentDividerLocation = quickNoteForm.getSplitPane().getDividerLocation();
            if (currentDividerLocation < 5) {
                quickNoteForm.getSplitPane().setDividerLocation((int) (App.mainFrame.getWidth() / 5));
            } else {
                quickNoteForm.getSplitPane().setDividerLocation(0);
            }
        });

        // 导出按钮
        quickNoteForm.getExportButton().addActionListener(e -> {
            int[] selectedRows = quickNoteForm.getNoteListTable().getSelectedRows();

            try {
                if (selectedRows.length > 0) {
                    JFileChooser fileChooser = new JFileChooser(App.config.getQuickNoteExportPath());
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int approve = fileChooser.showOpenDialog(quickNoteForm.getQuickNotePanel());
                    String exportPath;
                    if (approve == JFileChooser.APPROVE_OPTION) {
                        exportPath = fileChooser.getSelectedFile().getAbsolutePath();
                        App.config.setQuickNoteExportPath(exportPath);
                        App.config.save();
                    } else {
                        return;
                    }

                    for (int row : selectedRows) {
                        Integer selectedId = (Integer) quickNoteForm.getNoteListTable().getValueAt(row, 0);
                        TQuickNote tQuickNote = quickNoteMapper.selectByPrimaryKey(selectedId);
                        File exportFile = FileUtil.touch(exportPath + File.separator + tQuickNote.getName() + ".txt");
                        FileUtil.writeUtf8String(tQuickNote.getContent(), exportFile);
                    }
                    JOptionPane.showMessageDialog(quickNoteForm.getQuickNotePanel(), "导出成功！", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(new File(exportPath));
                    } catch (Exception e2) {
                        log.error(ExceptionUtils.getStackTrace(e2));
                    }
                } else {
                    JOptionPane.showMessageDialog(quickNoteForm.getQuickNotePanel(), "请至少选择一个！", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception e1) {
                JOptionPane.showMessageDialog(quickNoteForm.getQuickNotePanel(), "导出失败！\n\n" + e1.getMessage(), "失败",
                        JOptionPane.ERROR_MESSAGE);
                log.error(ExceptionUtils.getStackTrace(e1));
            }

        });

        // 菜单栏导出按钮
        quickNoteForm.getExportButton2().addActionListener(e -> {

            try {
                JFileChooser fileChooser = new JFileChooser(App.config.getQuickNoteExportPath());
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int approve = fileChooser.showOpenDialog(quickNoteForm.getQuickNotePanel());
                String exportPath;
                if (approve == JFileChooser.APPROVE_OPTION) {
                    exportPath = fileChooser.getSelectedFile().getAbsolutePath();
                    App.config.setQuickNoteExportPath(exportPath);
                    App.config.save();
                } else {
                    return;
                }
                TQuickNote tQuickNote = quickNoteMapper.selectByName(selectedName);
                File exportFile = FileUtil.touch(exportPath + File.separator + tQuickNote.getName() + ".txt");
                FileUtil.writeUtf8String(tQuickNote.getContent(), exportFile);
                JOptionPane.showMessageDialog(quickNoteForm.getQuickNotePanel(), "导出成功！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(new File(exportPath));
                } catch (Exception e2) {
                    log.error(ExceptionUtils.getStackTrace(e2));
                }

            } catch (Exception e1) {
                JOptionPane.showMessageDialog(quickNoteForm.getQuickNotePanel(), "导出失败！\n\n" + e1.getMessage(), "失败",
                        JOptionPane.ERROR_MESSAGE);
                log.error(ExceptionUtils.getStackTrace(e1));
            }

        });

        // 格式化按钮
        quickNoteForm.getFormatButton().addActionListener(e -> format());

        // 执行查找
        quickNoteForm.getDoFindButton().addActionListener(e -> find());

        // 执行快捷替换
        quickNoteForm.getStartQuickReplaceButton().addActionListener(e -> quickReplace());

        // 关闭查找面板按钮
        quickNoteForm.getFindReplaceCloseLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                quickNoteForm.getFindReplacePanel().setVisible(false);
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }
        });

        // 关闭快捷查找面板按钮
        quickNoteForm.getQuickReplaceCloseLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                quickNoteForm.getContentSplitPane().setDividerLocation(quickNoteForm.getContentSplitPane().getWidth());
                quickNoteForm.getQuickReplaceScrollPane().setVisible(false);
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }
        });

        // 执行替换按钮
        quickNoteForm.getDoReplaceButton().addActionListener(e -> replace());

        // 替换文本框
        quickNoteForm.getReplaceTextField().addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    replace();
                }
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });

        // 查找-正则匹配CheckBox
        quickNoteForm.getFindUseRegexCheckBox().addActionListener(e -> {
            boolean selected = quickNoteForm.getFindUseRegexCheckBox().isSelected();
            if (selected) {
                quickNoteForm.getFindWordsCheckBox().setSelected(false);
                quickNoteForm.getFindWordsCheckBox().setEnabled(false);
            } else {
                quickNoteForm.getFindWordsCheckBox().setEnabled(true);
            }
        });
    }

    /**
     * view By Row Num
     *
     * @param rowNum
     */
    private static void viewByRowNum(int rowNum) {

        QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();
        QuickNoteSyntaxTextViewerManager quickNoteSyntaxTextViewerManager = QuickNoteForm.quickNoteSyntaxTextViewerManager;

        String name = quickNoteForm.getNoteListTable().getValueAt(rowNum, 1).toString();
        selectedName = name;

        RTextScrollPane syntaxTextViewer = quickNoteSyntaxTextViewerManager.getRTextScrollPane(name);

        quickNoteForm.getContentSplitPane().setLeftComponent(syntaxTextViewer);

        TQuickNote tQuickNote = quickNoteMapper.selectByName(name);
        quickNoteForm.getSyntaxComboBox().setSelectedItem(tQuickNote.getSyntax());
        quickNoteForm.getFontNameComboBox().setSelectedItem(tQuickNote.getFontName());
        quickNoteForm.getFontSizeComboBox().setSelectedItem(String.valueOf(tQuickNote.getFontSize()));

        syntaxTextViewer.updateUI();
    }

    /**
     * Default File Name
     *
     * @return
     */
    @NotNull
    private static String getDefaultFileName() {
        return "未命名_" + DateFormatUtils.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
    }

    /**
     * 快捷替换
     */
    private static void quickReplace() {
        try {
            QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();
            RSyntaxTextArea view = (RSyntaxTextArea) ((RTextScrollPane) quickNoteForm.getContentSplitPane().getLeftComponent()).getViewport().getView();

            String content = view.getText();

            String[] splits = content.split("\n");

            List<String> target = Lists.newArrayList();
            for (String split : splits) {

                if (quickNoteForm.getTrimBlankCheckBox().isSelected()) {
                    split = split.replace(" ", "");
                }

                if (quickNoteForm.getClearTabTCheckBox().isSelected()) {
                    split = split.replace("\t", "");
                }

                // ------------

                if (quickNoteForm.getScientificToNormalCheckBox().isSelected()) {
                    BigDecimal bigDecimal = NumberUtil.toBigDecimal(split);
                    split = bigDecimal.toString();
                }

                if (quickNoteForm.getToThousandthCheckBox().isSelected()) {
                    split = toThousandth(split);
                }

                // ------------
                if (quickNoteForm.getCommaToEnterCheckBox().isSelected()) {
                    split = split.replace(",", "\n");
                }
                if (quickNoteForm.getTabToEnterCheckBox().isSelected()) {
                    split = split.replace("\t", "\n");
                }

                target.add(split);
            }

            if (quickNoteForm.getClearEnterCheckBox().isSelected()) {
                view.setText(StringUtils.join(target, ""));
            } else if (quickNoteForm.getEnterToCommaCheckBox().isSelected()) {
                view.setText(StringUtils.join(target, ","));
            } else if (quickNoteForm.getEnterToCommaSingleQuotesCheckBox().isSelected()) {
                view.setText(StringUtils.join(target, "','"));
            } else if (quickNoteForm.getEnterToCommaDoubleQuotesCheckBox().isSelected()) {
                view.setText(StringUtils.join(target, "\",\""));
            } else {
                view.setText(StringUtils.join(target, "\n"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(App.mainFrame, "转换失败！\n\n" + e.getMessage(), "失败",
                    JOptionPane.ERROR_MESSAGE);
            log.error(ExceptionUtils.getStackTrace(e));
        }

    }

    /**
     * 删除文件
     *
     * @param quickNoteForm
     */
    private static void deleteFiles(QuickNoteForm quickNoteForm) {
        try {
            int[] selectedRows = quickNoteForm.getNoteListTable().getSelectedRows();

            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(App.mainFrame, "请至少选择一个！", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int isDelete = JOptionPane.showConfirmDialog(App.mainFrame, "确认删除？", "确认", JOptionPane.YES_NO_OPTION);
                if (isDelete == JOptionPane.YES_OPTION) {
                    DefaultTableModel tableModel = (DefaultTableModel) quickNoteForm.getNoteListTable().getModel();

                    for (int i = 0; i < selectedRows.length; i++) {
                        int selectedRow = selectedRows[i];
                        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
                        quickNoteMapper.deleteByPrimaryKey(id);
                    }
                    selectedName = null;
                    QuickNoteForm.initNoteListTable();
                }
            }
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(App.mainFrame, "删除失败！\n\n" + e1.getMessage(), "失败",
                    JOptionPane.ERROR_MESSAGE);
            log.error(ExceptionUtils.getStackTrace(e1));
        }
    }

    /**
     * save for quick key and item change
     *
     * @param refreshModifiedTime
     */
    public static void quickSave(boolean refreshModifiedTime) {
        QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();
        String now = SqliteUtil.nowDateForSqlite();
        if (selectedName != null) {
            TQuickNote tQuickNote = new TQuickNote();
            tQuickNote.setName(selectedName);

            String text = QuickNoteForm.quickNoteSyntaxTextViewerManager.getTextByName(selectedName);
            tQuickNote.setContent(text);
            if (refreshModifiedTime) {
                tQuickNote.setModifiedTime(now);
            }

            quickNoteMapper.updateByName(tQuickNote);
        } else {
            String tempName = getDefaultFileName();
            String name = JOptionPane.showInputDialog(MainWindow.getInstance().getMainPanel(), "名称", tempName);
            if (StringUtils.isNotBlank(name)) {
                TQuickNote tQuickNote = new TQuickNote();
                tQuickNote.setName(name);

                RTextScrollPane rTextScrollPane = QuickNoteForm.quickNoteSyntaxTextViewerManager.getRTextScrollPane(name);
                RSyntaxTextArea view = (RSyntaxTextArea) rTextScrollPane.getViewport().getView();
                tQuickNote.setContent(view.getText());
                tQuickNote.setCreateTime(now);
                tQuickNote.setModifiedTime(now);

                quickNoteMapper.insert(tQuickNote);
                QuickNoteForm.initNoteListTable();
            }
        }
    }

    /**
     * create new Note
     */
    public static void newNote() {
        String name = getDefaultFileName();
        name = JOptionPane.showInputDialog(MainWindow.getInstance().getMainPanel(), "名称", name);
        if (StringUtils.isNotBlank(name)) {
            TQuickNote tQuickNote = quickNoteMapper.selectByName(name);
            if (tQuickNote == null) {
                tQuickNote = new TQuickNote();
            } else {
                JOptionPane.showMessageDialog(App.mainFrame, "存在同名笔记，请重新命名！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String now = SqliteUtil.nowDateForSqlite();
            tQuickNote.setName(name);
            tQuickNote.setCreateTime(now);
            tQuickNote.setModifiedTime(now);
            tQuickNote.setSyntax(SyntaxConstants.SYNTAX_STYLE_NONE);
            tQuickNote.setFontName(App.config.getQuickNoteFontName());
            tQuickNote.setFontSize(String.valueOf(App.config.getFontSize()));
            quickNoteMapper.insert(tQuickNote);
            QuickNoteForm.initNoteListTable();
        }
    }

    private static void find() {
        QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();

        String content = QuickNoteForm.quickNoteSyntaxTextViewerManager.getCurrentText();
        String findKeyWord = quickNoteForm.getFindTextField().getText();
        boolean isMatchCase = quickNoteForm.getFindMatchCaseCheckBox().isSelected();
        boolean isWords = quickNoteForm.getFindWordsCheckBox().isSelected();
        boolean useRegex = quickNoteForm.getFindUseRegexCheckBox().isSelected();

        int count;
        String regex = findKeyWord;

        if (!useRegex) {
            regex = ReUtil.escape(regex);
        }
        if (isWords) {
            regex = "\\b" + regex + "\\b";
        }
        if (!isMatchCase) {
            regex = "(?i)" + regex;
        }

        count = ReUtil.findAll(regex, content, 0).size();
        content = ReUtil.replaceAll(content, regex, "<span>$0</span>");

        FindResultForm.getInstance().getFindResultCount().setText(String.valueOf(count));
        FindResultForm.getInstance().setHtmlText(content);
        FindResultFrame.showResultWindow();
    }

    private static void replace() {
        QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();
        RSyntaxTextArea view = QuickNoteForm.quickNoteSyntaxTextViewerManager.getCurrentRSyntaxTextArea();
        String target = quickNoteForm.getFindTextField().getText();
        String replacement = quickNoteForm.getReplaceTextField().getText();
        String content = view.getText();
        boolean isMatchCase = quickNoteForm.getFindMatchCaseCheckBox().isSelected();
        boolean isWords = quickNoteForm.getFindWordsCheckBox().isSelected();
        boolean useRegex = quickNoteForm.getFindUseRegexCheckBox().isSelected();

        String regex = target;

        if (!useRegex) {
            regex = ReUtil.escape(regex);
        }
        if (isWords) {
            regex = "\\b" + regex + "\\b";
        }
        if (!isMatchCase) {
            regex = "(?i)" + regex;
        }

        content = ReUtil.replaceAll(content, regex, replacement);

        view.setText(content);
        view.setCaretPosition(0);
        QuickNoteForm.quickNoteSyntaxTextViewerManager.getCurrentRTextScrollPane().getVerticalScrollBar().setValue(0);
        QuickNoteForm.quickNoteSyntaxTextViewerManager.getCurrentRTextScrollPane().getHorizontalScrollBar().setValue(0);
    }

    /**
     * 将字符串数字转成千分位显示。
     */
    public static String toThousandth(String value) {
        DecimalFormat decimalFormat;
        if (value.indexOf(".") > 0) {
            int afterPointLength = value.length() - value.indexOf(".") - 1;

            StringBuilder formatBuilder = new StringBuilder("###,##0.");
            for (int i = 0; i < afterPointLength; i++) {
                formatBuilder.append("0");
            }
            decimalFormat = new DecimalFormat(formatBuilder.toString());
        } else {
            decimalFormat = new DecimalFormat("###,##0");
        }
        double number;
        try {
            number = Double.parseDouble(value);
        } catch (Exception e) {
            number = 0.0;
        }
        return decimalFormat.format(number);
    }

    public static void format() {
        try {
            QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();
            String text = QuickNoteForm.quickNoteSyntaxTextViewerManager.getTextByName(selectedName);

            String format;
            String selectedSyntax = (String) quickNoteForm.getSyntaxComboBox().getSelectedItem();
            if (StringUtils.isBlank(text) || StringUtils.isEmpty(selectedSyntax)) {
                return;
            }

            switch (selectedSyntax) {
                case SyntaxConstants.SYNTAX_STYLE_SQL:
                    format = SqlFormatter.format(text);
                    break;
                case SyntaxConstants.SYNTAX_STYLE_JSON:
                case SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS:
                    try {
                        format = JSONUtil.toJsonPrettyStr(text);
                    } catch (Exception e1) {
                        log.error(ExceptionUtils.getStackTrace(e1));
                        format = JSONUtil.formatJsonStr(text);
                    }
                    break;

                case SyntaxConstants.SYNTAX_STYLE_JAVA:
                    format = new Formatter().formatSource(text);
                    break;

                default:
                    JOptionPane.showMessageDialog(App.mainFrame, "尚不支持对该语言格式化！\n", "不支持该语言",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
            }

            QuickNoteForm.quickNoteSyntaxTextViewerManager.getCurrentRSyntaxTextArea().setText(format);
            QuickNoteForm.quickNoteSyntaxTextViewerManager.getCurrentRSyntaxTextArea().setCaretPosition(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(App.mainFrame, "格式化失败！\n\n" + e.getMessage(), "失败",
                    JOptionPane.ERROR_MESSAGE);
            log.error(ExceptionUtils.getStackTrace(e));
        }

    }
}
