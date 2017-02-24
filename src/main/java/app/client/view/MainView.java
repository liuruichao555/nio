package app.client.view;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * 主界面
 * 
 * @author liuruichao
 * Created on 2016-02-19 16:14
 */
public class MainView extends BaseView {
    private JPanel topPanel, centerPanel, bottomPanel;
    private String title = "聊天工具";
    private final int viewWidth = 300;  // 窗体初始化宽度
    private final int viewHeight = (int) (maxHeight / 1.5); // 窗体初始化高度
    private volatile int viewCurWidth = viewWidth;   // 窗体当前宽度
    private volatile int viewCurHeight = viewHeight; // 窗体当前高度
    private int rightMargin = 100; // 窗体距离屏幕右边距
    private Image avatar; // 用户头像

    public MainView() {
        setTitle(title);
        // 初始化布局
        initLayout();
        // 设置窗体大小
        setBounds((int) maxWidth - viewWidth - rightMargin, 0, viewWidth, viewHeight);
        // 窗体关闭之后程序退出
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * 初始化布局
     */
    private void initLayout() {
        // 设置布局
        //setLayout(new BorderLayout());

        // 初始化组件
        topPanel = new JPanel();
        topPanel.setBackground(Color.magenta);

        centerPanel = new JPanel();
        centerPanel.setBackground(Color.blue);

        bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.red);

        setPanelWidthAndHeight();

        add(topPanel);
        add(centerPanel);
        add(bottomPanel);

        // 窗体变化需要重新适配
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (e.getComponent().getHeight() != viewCurHeight || e.getComponent().getWidth() != viewCurWidth) {
                    viewCurHeight = e.getComponent().getHeight();
                    viewCurWidth = e.getComponent().getWidth();
                    setPanelWidthAndHeight();
                }
            }
        });
    }

    /**
     * 设置面板长度和宽度
     */
    private void setPanelWidthAndHeight() {
        double itemHeight = viewCurHeight / 10;
        topPanel.setSize(viewCurWidth, (int) (itemHeight * 1));
        topPanel.updateUI();
        centerPanel.setSize(viewCurWidth, (int) (itemHeight * 8));
        centerPanel.updateUI();
        bottomPanel.setSize(viewCurWidth, (int) (itemHeight * 1));
        bottomPanel.updateUI();
    }

    /**
     * 初始化个人资料
     */
    private void initTopPanel() {

    }

    public static void main(String[] args) {
        new MainView();
    }
}
