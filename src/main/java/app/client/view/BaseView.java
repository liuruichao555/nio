package app.client.view;

import javax.swing.*;
import java.awt.*;

import static java.awt.Toolkit.*;

/**
 * BaseView
 * 
 * @author liuruichao
 * Created on 2016-02-19 16:19
 */
public abstract class BaseView extends JFrame {
    protected final double maxWidth = getDefaultToolkit().getScreenSize().getWidth();
    protected final double maxHeight = getDefaultToolkit().getScreenSize().getHeight();
}
