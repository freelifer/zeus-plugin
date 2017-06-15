package freelifer.zeus.plugin.settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author zhukun on 2017/4/21.
 * @version 1.0
 */
public class ImageButton extends JPanel {

    private JButton button;
    private Color preColor;

    public ImageButton(Icon icon) {
//        setSize(icon.getImage().getWidth(null), icon.getImage().getHeight(null));
        button = new JButton();
        button.setIcon(icon);
        //将边框外的上下左右空间设置为0
        button.setMargin(new Insets(0, 0, 0, 0));
        //将标签中显示的文本和图标之间的间隔量设置为0
        button.setIconTextGap(0);
        //不打印边框
        button.setBorderPainted(false);
        //除去边框
        button.setBorder(null);
        //除去按钮默认的名称
        button.setText(null);
        //除去焦点的框
        button.setFocusPainted(false);
        // 除去默认的背景填充
        button.setContentAreaFilled(false);
        button.setToolTipText("New Temp");
        preColor = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(preColor);
            }
        });

        add(button);
    }

    public void setOnClickListener(ActionListener listener) {
        button.addActionListener(listener);
    }
}
