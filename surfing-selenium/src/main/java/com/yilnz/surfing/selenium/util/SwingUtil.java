package com.yilnz.surfing.selenium.util;

import javax.swing.*;
import java.awt.*;

public class SwingUtil {
	public static void showImage(final String fileName)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.getContentPane().setLayout(new GridLayout(1,1));
				f.getContentPane().add(new JLabel(new ImageIcon(fileName)));
				f.pack();
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		});
	}
}
