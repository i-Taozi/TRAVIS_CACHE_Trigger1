package com.luoboduner.moo.tool.ui.component;

import com.luoboduner.moo.tool.App;
import com.luoboduner.moo.tool.ui.Style;
import com.luoboduner.moo.tool.ui.form.func.QuickNoteForm;
import com.luoboduner.moo.tool.ui.listener.func.QuickNoteListener;
import com.luoboduner.moo.tool.util.TextAreaUtil;
import com.luoboduner.moo.tool.util.UIUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class QuickNoteSyntaxTextViewer extends RSyntaxTextArea {
    public QuickNoteSyntaxTextViewer() {

        try {
            Theme theme;
            if (UIUtil.isDarkLaf()) {
                theme = Theme.load(JsonSyntaxTextViewer.class.getResourceAsStream(
                        "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
            } else {
                theme = Theme.load(JsonSyntaxTextViewer.class.getResourceAsStream(
                        "/org/fife/ui/rsyntaxtextarea/themes/idea.xml"));
            }
            theme.apply(this);
        } catch (IOException ioe) { // Never happens
            ioe.printStackTrace();
        }

        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        setCodeFoldingEnabled(true);
//        setCurrentLineHighlightColor(new Color(52, 52, 52));
//        setUseSelectedTextColor(true);
//        setSelectedTextColor(new Color(50, 50, 50));

        // 初始化背景色
        Style.blackTextArea(this);
        // 初始化边距
        setMargin(new Insets(10, 10, 10, 10));

        // 初始化字体
        String fontName = App.config.getQuickNoteFontName();
        int fontSize = App.config.getQuickNoteFontSize();
        if (fontSize == 0) {
            fontSize = getFont().getSize() + 2;
        }
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        setFont(font);

        setHyperlinksEnabled(true);
        addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(e.getURL().toString()));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setDoubleBuffered(true);

        // 文本域键盘事件
        addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyPressed(KeyEvent evt) {
                QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();
                if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_S) {
                    QuickNoteListener.quickSave(true);
                } else if (evt.isControlDown() && evt.isShiftDown() && evt.getKeyCode() == KeyEvent.VK_F) {
                    QuickNoteListener.format();
                } else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_F) {
                    RSyntaxTextArea view = (RSyntaxTextArea) ((RTextScrollPane) quickNoteForm.getContentSplitPane().getLeftComponent()).getViewport().getView();
                    quickNoteForm.getFindReplacePanel().setVisible(true);
                    quickNoteForm.getFindTextField().setText(view.getSelectedText());
                    quickNoteForm.getFindTextField().grabFocus();
                    quickNoteForm.getFindTextField().selectAll();
                } else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_R) {
                    RSyntaxTextArea view = (RSyntaxTextArea) ((RTextScrollPane) quickNoteForm.getContentSplitPane().getLeftComponent()).getViewport().getView();
                    quickNoteForm.getFindReplacePanel().setVisible(true);
                    quickNoteForm.getFindTextField().setText(view.getSelectedText());
                    quickNoteForm.getReplaceTextField().grabFocus();
                    quickNoteForm.getReplaceTextField().selectAll();
                } else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_N) {
                    QuickNoteListener.newNote();
                } else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_D) {
                    RSyntaxTextArea view = (RSyntaxTextArea) ((RTextScrollPane) quickNoteForm.getContentSplitPane().getLeftComponent()).getViewport().getView();
                    TextAreaUtil.deleteSelectedLine(view);
                }
                QuickNoteListener.quickSave(true);
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });

        // 文本域鼠标点击事件，隐藏删除按钮
        addMouseListener(new MouseListener() {
            QuickNoteForm quickNoteForm = QuickNoteForm.getInstance();

            @Override
            public void mouseClicked(MouseEvent e) {
                quickNoteForm.getDeletePanel().setVisible(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}
